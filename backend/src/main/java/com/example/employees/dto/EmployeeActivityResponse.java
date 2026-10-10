package com.example.employees.dto;

import com.example.employees.entity.EmployeeActivity;
import java.time.Instant;

public record EmployeeActivityResponse(
    Long id, Long employeeId, String employeeName, String action, Instant occurredAt) {
  public static EmployeeActivityResponse from(EmployeeActivity a) {
    return new EmployeeActivityResponse(
        a.getId(), a.getEmployeeId(), a.getEmployeeName(), a.getAction(), a.getOccurredAt());
  }
}
