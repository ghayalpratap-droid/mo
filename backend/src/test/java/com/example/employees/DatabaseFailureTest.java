package com.example.employees;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.employees.controller.EmployeeController;
import com.example.employees.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
class DatabaseFailureTest {
  @Autowired MockMvc mvc;
  @MockitoBean EmployeeService service;

  @Test
  void returnsSafeUnavailableResponse() throws Exception {
    when(service.all())
        .thenThrow(new DataAccessResourceFailureException("internal database detail"));
    mvc.perform(get("/api/employees"))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.message").value("Database is unavailable. Please try again later."));
  }
}
