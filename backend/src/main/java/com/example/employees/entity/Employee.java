package com.example.employees.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class Employee {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String firstName;

  @Column(nullable = false, length = 100)
  private String lastName;

  @Column(nullable = false)
  private Integer age;

  @Column(nullable = false, length = 500)
  private String address;

  public Long getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String v) {
    firstName = v;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String v) {
    lastName = v;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer v) {
    age = v;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String v) {
    address = v;
  }
}
