package edu.sis.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.*;
import java.math.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@SpringBootTest(
    properties = {
      "sis.jwt-secret=synthetic-test-signing-key-32-characters-long",
      "sis.demo-password=synthetic-test-password",
      "spring.datasource.url=jdbc:h2:mem:tests;DB_CLOSE_DELAY=-1"
    })
@AutoConfigureMockMvc
class BusinessTest {
  @Autowired MockMvc mvc;
  @Autowired ObjectMapper json;

  RequestPostProcessor user(String provider, String role) {
    return jwt()
        .jwt(
            j ->
                j.subject("test-" + provider)
                    .claim("provider", provider)
                    .claim("roles", List.of(role)));
  }

  String intake(String ref) {
    return "{\"provider\":\"A\",\"sourceRef\":\""
        + ref
        + "\",\"studentName\":\"Synthetic"
        + " Learner\",\"source\":\"LLE\",\"courseId\":\"A-CS\",\"academicYear\":2026,\"fee\":9250.01}";
  }

  JsonNode create() throws Exception {
    return json.readTree(
        mvc.perform(
                post("/api/v1/applications")
                    .with(user("A", "ADVISOR"))
                    .header("Idempotency-Key", UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(intake(UUID.randomUUID().toString())))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString());
  }

  @Test
  void exactMoney() {
    var split = BusinessService.split(new BigDecimal("9250.01"));
    assertEquals(
        new BigDecimal("9250.01"), split.stream().reduce(BigDecimal.ZERO, BigDecimal::add));
    assertEquals(new BigDecimal("4625.01"), split.get(2));
  }

  @Test
  void authRequired() throws Exception {
    mvc.perform(get("/api/v1/applications")).andExpect(status().isUnauthorized());
  }

  @Test
  void providerIsolation() throws Exception {
    var a = create();
    mvc.perform(get("/api/v1/applications/" + a.get("id").asText()).with(user("B", "ADVISOR")))
        .andExpect(status().isNotFound());
    mvc.perform(
            get("/api/v1/applications/" + a.get("id").asText() + "/history")
                .with(user("B", "ADVISOR")))
        .andExpect(status().isNotFound());
  }

  @Test
  void readOnlyRoleCannotCreate() throws Exception {
    mvc.perform(
            post("/api/v1/applications")
                .with(user("A", "REPORTER"))
                .header("Idempotency-Key", "read-only")
                .contentType(MediaType.APPLICATION_JSON)
                .content(intake("reporter")))
        .andExpect(status().isForbidden());
  }

  @Test
  void duplicateKeyAndConflict() throws Exception {
    String body = intake(UUID.randomUUID().toString()), key = UUID.randomUUID().toString();
    String a =
        mvc.perform(
                post("/api/v1/applications")
                    .with(user("A", "ADVISOR"))
                    .header("Idempotency-Key", key)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    String b =
        mvc.perform(
                post("/api/v1/applications")
                    .with(user("A", "ADVISOR"))
                    .header("Idempotency-Key", key)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    assertEquals(json.readTree(a).get("id"), json.readTree(b).get("id"));
    mvc.perform(
            post("/api/v1/applications")
                .with(user("A", "ADVISOR"))
                .header("Idempotency-Key", key)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.replace("9250.01", "50.00")))
        .andExpect(status().isConflict());
  }

  @Test
  void registrationBeforeAttendanceAndVersionConflict() throws Exception {
    String id = create().get("id").asText();
    var body = "{\"version\":0,\"status\":\"CONFIRMED\"}";
    mvc.perform(
            post("/api/v1/applications/" + id + "/attendance")
                .with(user("A", "ADVISOR"))
                .header("Idempotency-Key", "atn-" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isUnprocessableEntity());
    mvc.perform(
            post("/api/v1/applications/" + id + "/registration")
                .with(user("A", "ADVISOR"))
                .header("Idempotency-Key", "reg-" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.version").value(1));
    mvc.perform(
            post("/api/v1/applications/" + id + "/attendance")
                .with(user("A", "ADVISOR"))
                .header("Idempotency-Key", "stale-" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isConflict());
  }

  @Test
  void dryRunHasNoSideEffectsAndRejectionIsRecorded() throws Exception {
    String id = create().get("id").asText();
    String body = "{\"version\":0,\"type\":\"RESUMPTION\",\"effectiveDate\":\"2026-01-01\"}";
    String before =
        mvc.perform(get("/api/v1/applications/" + id + "/history").with(user("A", "ADVISOR")))
            .andReturn()
            .getResponse()
            .getContentAsString();
    mvc.perform(
            post("/api/v1/applications/" + id + "/cocs/preview")
                .with(user("A", "ADVISOR"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.valid").value(false));
    String after =
        mvc.perform(get("/api/v1/applications/" + id + "/history").with(user("A", "ADVISOR")))
            .andReturn()
            .getResponse()
            .getContentAsString();
    assertEquals(before, after);
    mvc.perform(
            post("/api/v1/applications/" + id + "/cocs")
                .with(user("A", "ADVISOR"))
                .header("Idempotency-Key", "coc-" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk());
  }
}
