package edu.sis.knowledge;

import edu.sis.shared.Identity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/v1/ai")
public class KnowledgeController {
  final ChatTurns turns;
  final KnowledgeStore store;
  final ModelGateway models;
  final JdbcTemplate db;
  final RestClient domain;
  final Semaphore slots = new Semaphore(4);

  public KnowledgeController(
      ChatTurns t,
      KnowledgeStore s,
      ModelGateway m,
      JdbcTemplate d,
      @Value("${sis.domain-url}") String url) {
    turns = t;
    store = s;
    models = m;
    db = d;
    var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(2000);
    factory.setReadTimeout(5000);
    domain = RestClient.builder().baseUrl(url).requestFactory(factory).build();
  }

  public record Source(
      @NotBlank String name,
      @NotBlank String text,
      @NotBlank String provider,
      @NotBlank String audience) {}

  public record Question(@NotBlank @Size(max = 2000) String question) {}

  public record Feedback(@NotNull UUID messageId, @Min(-1) @Max(1) int rating) {}

  @GetMapping("/status")
  public Object status() {
    return Map.of(
        "mode",
        models.mode,
        "embeddingModel",
        models.model,
        "dimensions",
        1536,
        "simulation",
        models.mode.equals("offline-demo"));
  }

  @PostMapping("/sources")
  public Object ingest(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody Source r) {
    return store.ingest(Identity.of(jwt), r.name(), r.text(), r.provider(), r.audience());
  }

  @GetMapping("/sources")
  public Object sources(@AuthenticationPrincipal Jwt jwt) {
    var who = Identity.of(jwt);
    who.administrator();
    return db.queryForList(
        "select id,name,provider,audience,active_version,deleted from source_files order by name");
  }

  @DeleteMapping("/sources/{id}")
  public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
    store.delete(Identity.of(jwt), id);
  }

  @GetMapping("/ingestion-jobs")
  public Object jobs(@AuthenticationPrincipal Jwt jwt) {
    Identity.of(jwt).administrator();
    return db.queryForList("select * from ingestion_jobs order by created_at desc limit 50");
  }

  @PostMapping("/search")
  public Object search(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody Question r) {
    return store.search(Identity.of(jwt), r.question());
  }

  @GetMapping("/citations/{id}")
  public Object citation(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
    return store.citation(Identity.of(jwt), id);
  }

  @PostMapping("/sessions")
  public Object session(@AuthenticationPrincipal Jwt jwt) {
    var who = Identity.of(jwt);
    UUID id = UUID.randomUUID();
    db.update(
        "insert into chat_sessions(id,owner,provider) values(?,?,?)",
        id,
        who.user(),
        who.provider());
    return Map.of("id", id);
  }

  @GetMapping("/sessions")
  public Object sessions(@AuthenticationPrincipal Jwt jwt) {
    return db.queryForList(
        "select id,created_at from chat_sessions where owner=? and provider=? order by created_at"
            + " desc limit 50",
        jwt.getSubject(),
        jwt.getClaimAsString("provider"));
  }

  @GetMapping("/sessions/{id}/messages")
  public Object history(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
    store.own(Identity.of(jwt), id);
    var who = Identity.of(jwt);
    var rows =
        db.queryForList(
            "select id,role,content_masked,status,created_at from chat_messages where session_id=?"
                + " order by message_order limit 100",
            id);
    for (var row : rows)
      if (!store.visibleMessage(who, (UUID) row.get("id")))
        row.put("content_masked", "Evidence withdrawn or access changed.");
    return rows;
  }

  @DeleteMapping("/sessions/{id}")
  public void remove(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
    store.own(Identity.of(jwt), id);
    db.update("delete from chat_sessions where id=?", id);
  }

  @PostMapping("/sessions/{id}/messages")
  public Object message(
      @AuthenticationPrincipal Jwt jwt, @PathVariable UUID id, @Valid @RequestBody Question r) {
    return turns.message(jwt, id, r.question());
  }

  @PostMapping(value = "/sessions/{id}/messages/stream", produces = "text/event-stream")
  public org.springframework.web.servlet.mvc.method.annotation.SseEmitter stream(
      @AuthenticationPrincipal Jwt jwt, @PathVariable UUID id, @Valid @RequestBody Question r) {
    return turns.stream(jwt, id, r.question());
  }

  @PostMapping("/turns/{id}/cancel")
  public void cancel(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
    turns.cancel(Identity.of(jwt), id);
  }

  @GetMapping("/tools/applications/{id}/summary")
  public Object tool(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
    var who = Identity.of(jwt);
    String outcome = "ERROR";
    try {
      Object result =
          domain
              .get()
              .uri("/api/v1/applications/{id}/summary", id)
              .headers(h -> h.setBearerAuth(jwt.getTokenValue()))
              .retrieve()
              .body(Object.class);
      outcome = "SUCCESS";
      return result;
    } catch (org.springframework.web.client.HttpClientErrorException e) {
      outcome = "DENIED";
      throw KnowledgeStore.fail(e.getStatusCode().value(), "Application unavailable");
    } finally {
      db.update(
          "insert into tool_invocations(id,actor,tool_name,outcome) values(?,?,?,?)",
          UUID.randomUUID(),
          who.user(),
          "applicationSummary",
          outcome);
    }
  }

  @PostMapping("/feedback")
  public void feedback(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody Feedback r) {
    if (r.rating() == 0) throw KnowledgeStore.fail(400, "Rating must be -1 or 1");
    var rows =
        db.queryForList(
            "select session_id from chat_messages where id=? and role='ASSISTANT'", r.messageId());
    if (rows.isEmpty()) throw KnowledgeStore.fail(404, "Message not found");
    store.own(Identity.of(jwt), (UUID) rows.getFirst().get("session_id"));
    db.update(
        "insert into feedback(id,message_id,owner,rating) values(?,?,?,?) on"
            + " conflict(message_id,owner) do update set rating=excluded.rating",
        UUID.randomUUID(),
        r.messageId(),
        jwt.getSubject(),
        r.rating());
  }
}
