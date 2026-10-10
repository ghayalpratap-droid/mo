package com.example.employees.service;

import com.example.employees.dto.*;
import com.example.employees.entity.Employee;
import com.example.employees.exception.EmployeeNotFound;
import com.example.employees.repository.EmployeeRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmployeeService {
  private final EmployeeRepository repository;

  public EmployeeService(EmployeeRepository repository) {
    this.repository = repository;
  }

  @Transactional(readOnly = true)
  public List<EmployeeResponse> all() {
    return repository.findAll(Sort.by("id")).stream().map(EmployeeResponse::from).toList();
  }

  private Employee find(Long id) {
    return repository.findById(id).orElseThrow(() -> new EmployeeNotFound(id));
  }

  @Transactional(readOnly = true)
  public EmployeeResponse get(Long id) {
    return EmployeeResponse.from(find(id));
  }

  public EmployeeResponse create(EmployeeRequest r) {
    return save(new Employee(), r);
  }

  public EmployeeResponse update(Long id, EmployeeRequest r) {
    return save(find(id), r);
  }

  private EmployeeResponse save(Employee e, EmployeeRequest r) {
    e.setFirstName(r.firstName().trim());
    e.setLastName(r.lastName().trim());
    e.setAge(r.age());
    e.setAddress(r.address().trim());
    return EmployeeResponse.from(repository.save(e));
  }

  public void delete(Long id) {
    repository.delete(find(id));
  }
}
