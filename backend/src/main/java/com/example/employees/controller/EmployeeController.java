package com.example.employees.controller;

import com.example.employees.dto.*;
import com.example.employees.service.EmployeeService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
  private final EmployeeService service;

  public EmployeeController(EmployeeService s) {
    service = s;
  }

  @GetMapping
  public List<EmployeeResponse> all() {
    return service.all();
  }

  @GetMapping("/{id}")
  public EmployeeResponse get(@PathVariable Long id) {
    return service.get(id);
  }

  @PostMapping
  public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest r) {
    var e = service.create(r);
    return ResponseEntity.created(URI.create("/api/employees/" + e.id())).body(e);
  }

  @PutMapping("/{id}")
  public EmployeeResponse update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest r) {
    return service.update(id, r);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
