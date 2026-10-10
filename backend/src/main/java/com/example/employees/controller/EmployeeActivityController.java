package com.example.employees.controller;

import com.example.employees.dto.EmployeeActivityResponse;
import com.example.employees.repository.EmployeeActivityRepository;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
public class EmployeeActivityController {
  private final EmployeeActivityRepository repository;

  public EmployeeActivityController(EmployeeActivityRepository repository) {
    this.repository = repository;
  }

  @GetMapping
  public List<EmployeeActivityResponse> all() {
    return repository.findAll(PageRequest.of(0, 200, Sort.by(Sort.Direction.DESC, "id"))).stream()
        .map(EmployeeActivityResponse::from)
        .toList();
  }
}
