package com.example.employees.dto;

import com.example.employees.entity.Employee;

public record EmployeeResponse(
    Long id, String firstName, String lastName, Integer age, String address) {
  public static EmployeeResponse from(Employee e) {
    return new EmployeeResponse(
        e.getId(), e.getFirstName(), e.getLastName(), e.getAge(), e.getAddress());
  }
}
