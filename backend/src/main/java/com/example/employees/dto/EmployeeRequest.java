package com.example.employees.dto;

import jakarta.validation.constraints.*;

public record EmployeeRequest(
    @NotBlank @Size(max = 100) String firstName,
    @NotBlank @Size(max = 100) String lastName,
    @NotNull @Min(18) @Max(100) Integer age,
    @NotBlank @Size(max = 500) String address) {}
