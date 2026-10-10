package com.example.employees.exception;

public class EmployeeNotFound extends RuntimeException {
  public EmployeeNotFound(Long id) {
    super("Employee " + id + " was not found");
  }
}
