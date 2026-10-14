package edu.sis.knowledge;

import edu.sis.shared.Identity;
import jakarta.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class ChatTurns {
  final KnowledgeStore store;
  final ModelGateway models;
  final RestClient domain;
  final Semaphore slots = new Semaphore(4);
  final ExecutorService workers = Executors.newVirtualThreadPerTaskExecutor();

  record Running(String owner, AtomicBoolean cancelled, Future<?> task) {}

  final ConcurrentMap<UUID, Running> running = new ConcurrentHashMap<>();

  public ChatTurns(KnowledgeStore s, ModelGateway m, @Value("${sis.domain-url}") String url) {
    store = s;
    models = m;
    var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(2000);
    factory.setReadTimeout(5000);
    domain = RestClient.builder().baseUrl(url).requestFactory(factory).build();
  }

  public record Answer(
      UUID id,
      String answer,
      List<KnowledgeStore.Passage> citations,
      String mode,
      boolean abstained,
      Object liveFacts,
      String tokenUsage) {}

  UUID begin(Identity who, UUID session) {
    store.own(who, session);
    if (!slots.tryAcquire()) throw KnowledgeStore.fail(429, "AI capacity reached");
    UUID turn = UUID.randomUUID();
    try {
      store.db.update(
          "insert into chat_turns(id,session_id,status) values(?,?,'GENERATING')", turn, session);
      return turn;
    } catch (org.springframework.dao.DataIntegrityViolationException e) {
      slots.release();
      throw KnowledgeStore.fail(409, "A turn is already running for this session");
    }
  }

  Answer generate(Jwt jwt, UUID session, String question) {
    var who = Identity.of(jwt);
    String q = ModelGateway.scrub(question);
    var tools = new BusinessTools(domain, jwt, store);
    var live =
        Pattern.compile(
                "(?i)(?:status|attendance|registration).*?([a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12})")
            .matcher(q);
    if (live.find()) {
      Object fact = tools.applicationSummary(UUID.fromString(live.group(1)));
      return new Answer(
          UUID.randomUUID(),
          "Current facts from the authorized business API (not document retrieval).",
          List.of(),
          models.mode,
          false,
          fact,
          "unavailable");
    }
    var passages = store.search(who, q);
    if (passages.isEmpty())
      return new Answer(
          UUID.randomUUID(),
          "Evidence unavailable in authorized sources.",
          List.of(),
          models.mode,
          true,
          Map.of(),
          "unavailable");
    String evidence =
        passages.stream()
            .map(
                p ->
                    "["
                        + p.id()
                        + "] "
                        + p.source()
                        + " version "
                        + p.version()
                        + " lines "
                        + p.startLine()
                        + "-"
                        + p.endLine()
                        + "\n"
                        + p.content())
            .collect(java.util.stream.Collectors.joining("\n"));
    String answer =
        models.answer(q, evidence, new OwnedChatMemory(store, who).get(session.toString()), tools);
    var matcher = Pattern.compile("\\[([a-fA-F0-9-]{36})\\]").matcher(answer);
    Set<String> allowed = new HashSet<>();
    passages.forEach(p -> allowed.add(p.id().toString()));
    boolean cited = false;
    while (matcher.find()) {
      cited = true;
      if (!allowed.contains(matcher.group(1)))
        throw KnowledgeStore.fail(502, "Unsupported model citation");
    }
    if (!cited && !models.mode.equals("offline-demo"))
      throw KnowledgeStore.fail(502, "The answer did not cite supplied evidence");
    for (var p : passages) store.citation(who, p.id());
    return new Answer(
        UUID.randomUUID(),
        ModelGateway.scrub(answer),
        passages,
        models.mode,
        false,
        Map.of(),
        "unavailable");
  }

  void persist(Identity who, UUID session, UUID turn, String question, Answer answer) {
    store.tx.executeWithoutResult(
        t -> {
          store.own(who, session);
          for (var p : answer.citations()) store.citation(who, p.id());
          if (store.db.update(
                  "update chat_turns set status='COMPLETED' where id=? and status='GENERATING'",
                  turn)
              != 1) throw KnowledgeStore.fail(409, "Turn cancelled");
          store.db.update(
              "insert into chat_messages(id,session_id,role,content_masked,status)"
                  + " values(?,?,?,?,?)",
              UUID.randomUUID(),
              session,
              "USER",
              ModelGateway.scrub(question),
              "COMPLETED");
          store.db.update(
              "insert into chat_messages(id,session_id,role,content_masked,status)"
                  + " values(?,?,?,?,?)",
              answer.id(),
              session,
              "ASSISTANT",
              answer.answer(),
              "COMPLETED");
          for (var p : answer.citations())
            store.db.update("insert into message_sources values(?,?)", answer.id(), p.id());
          store.audit(who, "CHAT_TURN", answer.id());
        });
  }

  public Answer message(Jwt jwt, UUID session, String question) {
    var who = Identity.of(jwt);
    UUID turn = begin(who, session);
    try {
      var answer = generate(jwt, session, question);
      persist(who, session, turn, question, answer);
      return answer;
    } catch (Exception e) {
      store.db.update(
          "update chat_turns set status='FAILED' where id=? and status='GENERATING'", turn);
      if (e instanceof org.springframework.web.server.ResponseStatusException r) throw r;
      throw KnowledgeStore.fail(503, "AI unavailable; ordinary business APIs remain available");
    } finally {
      slots.release();
    }
  }

  public SseEmitter stream(Jwt jwt, UUID session, String question) {
    var who = Identity.of(jwt);
    UUID turn = begin(who, session);
    var emitter = new SseEmitter(45000L);
    var cancelled = new AtomicBoolean(false);
    Runnable cancel =
        () -> {
          cancelled.set(true);
          store.db.update(
              "update chat_turns set status='CANCELLED' where id=? and status='GENERATING'", turn);
        };
    emitter.onTimeout(cancel);
    emitter.onError(e -> cancel.run());
    try {
      emitter.send(
          SseEmitter.event()
              .name("start")
              .data(Map.of("turnId", turn, "delivery", "Evidence-validated buffered streaming")));
    } catch (Exception e) {
      cancel.run();
      slots.release();
      return emitter;
    }
    Future<?> task =
        workers.submit(
            () -> {
              Answer answer = null;
              try {
                answer = generate(jwt, session, question);
                if (cancelled.get()) return;
                // Buffer model output until citation validation; transport still supports partial
                // delivery and cancellation.
                for (int i = 0; i < answer.answer().length(); i += 160) {
                  if (cancelled.get() || Thread.currentThread().isInterrupted()) return;
                  emitter.send(
                      SseEmitter.event()
                          .name("delta")
                          .data(
                              Map.of(
                                  "text",
                                  answer
                                      .answer()
                                      .substring(i, Math.min(i + 160, answer.answer().length())))));
                }
                if (cancelled.get()) return;
                persist(who, session, turn, question, answer);
                emitter.send(SseEmitter.event().name("complete").data(answer));
                emitter.complete();
              } catch (Exception e) {
                if (answer != null)
                  store.db.update(
                      "update chat_messages set status='INTERRUPTED' where id=?", answer.id());
                store.db.update("update chat_turns set status='FAILED' where id=?", turn);
                try {
                  emitter.send(
                      SseEmitter.event()
                          .name("failure")
                          .data(Map.of("message", "AI turn failed; retry safely")));
                } catch (Exception ignored) {
                }
                emitter.complete();
              } finally {
                running.remove(turn);
                slots.release();
              }
            });
    running.put(turn, new Running(who.user(), cancelled, task));
    return emitter;
  }

  public void cancel(Identity who, UUID turn) {
    var rows = store.db.queryForList("select session_id from chat_turns where id=?", turn);
    if (rows.isEmpty()) throw KnowledgeStore.fail(404, "Turn not found");
    store.own(who, (UUID) rows.getFirst().get("session_id"));
    var task = running.get(turn);
    if (task != null) {
      task.cancelled().set(true);
    }
    store.db.update(
        "update chat_turns set status='CANCELLED' where id=? and status='GENERATING'", turn);
  }

  @PreDestroy
  void close() {
    workers.shutdownNow();
  }
}
