package edu.sis.shared;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class Errors {
  @ExceptionHandler(ResponseStatusException.class)
  ResponseEntity<?> status(ResponseStatusException e, HttpServletRequest r) {
    return ResponseEntity.status(e.getStatusCode()).body(body(e.getReason(), Map.of(), r));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<?> validation(MethodArgumentNotValidException e, HttpServletRequest r) {
    var fields = new LinkedHashMap<String, String>();
    e.getBindingResult()
        .getFieldErrors()
        .forEach(f -> fields.put(f.getField(), f.getDefaultMessage()));
    return ResponseEntity.badRequest().body(body("Validation failed", fields, r));
  }

  @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
  ResponseEntity<?> duplicate(
      org.springframework.dao.DataIntegrityViolationException e, HttpServletRequest r) {
    return ResponseEntity.status(409)
        .body(body("Duplicate request or constraint conflict", Map.of(), r));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  ResponseEntity<?> invalid(IllegalArgumentException e, HttpServletRequest r) {
    return ResponseEntity.badRequest().body(body("Invalid request", Map.of(), r));
  }

  @ExceptionHandler({
    org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,
    org.springframework.http.converter.HttpMessageNotReadableException.class,
    org.springframework.web.bind.MissingRequestHeaderException.class
  })
  ResponseEntity<?> malformed(Exception e, HttpServletRequest r) {
    return ResponseEntity.badRequest().body(body("Malformed request", Map.of(), r));
  }

  @ExceptionHandler(org.springframework.dao.DataAccessException.class)
  ResponseEntity<?> unavailable(Exception e, HttpServletRequest r) {
    return ResponseEntity.status(503).body(body("Database operation unavailable", Map.of(), r));
  }

  static void security(HttpServletRequest req, HttpServletResponse res, int status, String message)
      throws IOException {
    res.setStatus(status);
    res.setContentType("application/json");
    new com.fasterxml.jackson.databind.ObjectMapper()
        .writeValue(res.getOutputStream(), body(message, Map.of(), req));
  }

  static Map<String, Object> body(String message, Object fields, HttpServletRequest r) {
    return Map.of(
        "message",
        message == null ? "Request failed" : message,
        "fieldErrors",
        fields,
        "correlationId",
        Objects.toString(r.getAttribute("correlationId"), "unavailable"));
  }
}

class CorrelationFilter extends OncePerRequestFilter {
  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String supplied = req.getHeader("X-Correlation-ID");
    String id =
        supplied != null && supplied.matches("[a-fA-F0-9-]{36}")
            ? supplied
            : UUID.randomUUID().toString();
    req.setAttribute("correlationId", id);
    res.setHeader("X-Correlation-ID", id);
    chain.doFilter(req, res);
  }
}
