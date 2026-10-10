package com.example.employees;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.employees.dto.EmployeeRequest;
import com.example.employees.entity.Employee;
import com.example.employees.exception.EmployeeNotFound;
import com.example.employees.repository.EmployeeRepository;
import com.example.employees.service.EmployeeService;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class EmployeeServiceTest {
  @Test
  void trimsNamesAndAddress() {
    var repo = mock(EmployeeRepository.class);
    when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
    var service = new EmployeeService(repo);
    var result = service.create(new EmployeeRequest(" Rahul ", " Sharma ", 28, " Pune "));
    assertEquals("Rahul", result.firstName());
    assertEquals("Pune", result.address());
  }

  @Test
  void missingEmployeeFails() {
    var repo = mock(EmployeeRepository.class);
    when(repo.findById(1L)).thenReturn(Optional.empty());
    assertThrows(EmployeeNotFound.class, () -> new EmployeeService(repo).get(1L));
    verify(repo, never()).save(any(Employee.class));
  }
}
