package com.example.employees.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "employee_activity")
public class EmployeeActivity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long employeeId;

  @Column(nullable = false, length = 201)
  private String employeeName;

  @Column(nullable = false, length = 10)
  private String action;

  @Column(nullable = false)
  private Instant occurredAt;

  protected EmployeeActivity() {}

  public EmployeeActivity(Employee e, String action) {
    this.employeeId = e.getId();
    this.employeeName = e.getFirstName() + " " + e.getLastName();
    this.action = action;
    this.occurredAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public Long getEmployeeId() {
    return employeeId;
  }

  public String getEmployeeName() {
    return employeeName;
  }

  public String getAction() {
    return action;
  }

  public Instant getOccurredAt() {
    return occurredAt;
  }
}
