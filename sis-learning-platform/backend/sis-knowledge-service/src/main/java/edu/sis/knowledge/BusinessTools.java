package edu.sis.knowledge;

import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestClient;

/**
 * A new demo facade, not a claim that these legacy endpoints exist. No model-controlled host or
 * scope.
 */
public final class BusinessTools {
  private final RestClient client;
  private final Jwt identity;
  private final KnowledgeStore store;
  private final AtomicInteger calls = new AtomicInteger();
  private final Instant deadline = Instant.now().plusSeconds(15);

  BusinessTools(RestClient client, Jwt identity, KnowledgeStore store) {
    this.client = client;
    this.identity = identity;
    this.store = store;
  }

  private Object read(String name, String path) {
    if (calls.incrementAndGet() > 5 || Instant.now().isAfter(deadline))
      throw KnowledgeStore.fail(429, "Tool budget exceeded");
    if (identity.getExpiresAt() == null || identity.getExpiresAt().isBefore(Instant.now()))
      throw KnowledgeStore.fail(401, "Identity expired");
    String outcome = "ERROR";
    try {
      Object value =
          client
              .get()
              .uri(path)
              .headers(h -> h.setBearerAuth(identity.getTokenValue()))
              .retrieve()
              .body(Object.class);
      outcome = "SUCCESS";
      return value;
    } catch (org.springframework.web.client.HttpClientErrorException e) {
      outcome = "DENIED";
      throw KnowledgeStore.fail(
          e.getStatusCode().value(), "Tool access denied or record unavailable");
    } finally {
      store.db.update(
          "insert into tool_invocations(id,actor,tool_name,outcome) values(?,?,?,?)",
          UUID.randomUUID(),
          identity.getSubject(),
          name,
          outcome);
    }
  }

  @Tool(
      description =
          "Read the current masked application status. UUID identifies the application. Provider"
              + " scope comes from authenticated identity, never from tool input.")
  public Object applicationSummary(UUID applicationId) {
    return read("applicationSummary", "/api/v1/applications/" + applicationId + "/summary");
  }

  @Tool(description = "Read authorized academic years for this educational demo.")
  public Object academicYears() {
    return read("academicYears", "/api/v1/academic-years");
  }

  @Tool(description = "Read course options visible to the authenticated provider.")
  public Object courseOptions() {
    return read("courseOptions", "/api/v1/courses");
  }

  @Tool(description = "Read allowed synthetic source categories and currency.")
  public Object referenceData() {
    return read("referenceData", "/api/v1/reference-data");
  }

  @Tool(
      description =
          "Read registration and attendance event history; no student identifiers or names"
              + " returned.")
  public Object confirmationHistory(UUID applicationId) {
    return read("confirmationHistory", "/api/v1/applications/" + applicationId + "/history");
  }

  @Tool(description = "Read authorized CoC decisions and validation reasons.")
  public Object cocHistory(UUID applicationId) {
    return read("cocHistory", "/api/v1/applications/" + applicationId + "/cocs");
  }

  @Tool(description = "Read scoped synthetic tuition periods and exact financial details.")
  public Object tuitionPeriods(UUID applicationId) {
    return read("tuitionPeriods", "/api/v1/applications/" + applicationId + "/tuition-periods");
  }

  @Tool(
      description =
          "Read allowlisted synthetic feature toggle values; requires support permission.")
  public Object featureToggles() {
    return read("featureToggles", "/api/v1/operations/toggles");
  }

  @Tool(description = "Read bounded masked sync exception summaries; requires support permission.")
  public Object syncExceptions() {
    return read("syncExceptions", "/api/v1/operations/exceptions");
  }
}
