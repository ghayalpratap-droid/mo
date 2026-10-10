package com.example.employees;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeIntegrationTest {
  @Autowired MockMvc mvc;
  @Autowired ObjectMapper mapper;
  private final String valid =
      "{\"firstName\":\"Rahul\",\"lastName\":\"Sharma\",\"age\":28,\"address\":\"Pune,"
          + " Maharashtra\"}";

  @Test
  void completeCrud() throws Exception {
    var result =
        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content(valid))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andReturn();
    long id = mapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    String path = "/api/employees/" + id;
    mvc.perform(get(path))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("Rahul"));
    mvc.perform(get("/api/employees")).andExpect(jsonPath("$[0].id").value(id));
    mvc.perform(
            put(path).contentType(MediaType.APPLICATION_JSON).content(valid.replace("28", "29")))
        .andExpect(jsonPath("$.age").value(29));
    mvc.perform(delete(path)).andExpect(status().isNoContent());
    mvc.perform(get(path)).andExpect(status().isNotFound());
  }

  @Test
  void rejectsInvalidFields() throws Exception {
    mvc.perform(
            post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\" \" ,\"lastName\":\"\",\"age\":-1,\"address\":\"\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.age").exists())
        .andExpect(jsonPath("$.errors.firstName").exists());
  }

  @Test
  void rejectsMalformedAge() throws Exception {
    mvc.perform(
            post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(valid.replace("28", "\"invalid\"")))
        .andExpect(status().isBadRequest());
  }

  @Test
  void missingUpdateAndDelete() throws Exception {
    mvc.perform(put("/api/employees/999999").contentType(MediaType.APPLICATION_JSON).content(valid))
        .andExpect(status().isNotFound());
    mvc.perform(delete("/api/employees/999999")).andExpect(status().isNotFound());
  }

  @Test
  void healthAndCors() throws Exception {
    mvc.perform(get("/actuator/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("UP"));
    mvc.perform(
            options("/api/employees")
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "POST"))
        .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
  }

  @Test
  void rejectsNumericNamesOnCreateAndUpdate() throws Exception {
    for (String field : java.util.List.of("firstName", "lastName")) {
      for (String name : java.util.List.of("123", "Sharma43", "１２３", "Rahul@", "---")) {
        var body = mapper.readTree(valid);
        ((com.fasterxml.jackson.databind.node.ObjectNode) body).put(field, name);
        String payload = mapper.writeValueAsString(body);
        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors." + field).exists());
        mvc.perform(
                put("/api/employees/999999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors." + field).exists());
      }
    }
  }

  @Test
  void acceptsInternationalNames() throws Exception {
    for (String name : java.util.List.of("राहुल", "José", "O’Connor", "Anne-Marie", "De Silva")) {
      var body = (com.fasterxml.jackson.databind.node.ObjectNode) mapper.readTree(valid);
      body.put("firstName", name);
      body.put("lastName", name);
      var result =
          mvc.perform(
                  post("/api/employees")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(mapper.writeValueAsString(body)))
              .andExpect(status().isCreated())
              .andReturn();
      long id = mapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
      mvc.perform(delete("/api/employees/" + id)).andExpect(status().isNoContent());
    }
  }
}
