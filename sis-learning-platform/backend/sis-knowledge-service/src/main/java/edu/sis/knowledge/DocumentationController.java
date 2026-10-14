package edu.sis.knowledge;

import edu.sis.shared.Identity;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.apache.commons.csv.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
public class DocumentationController {
  final List<Map<String, String>> inventory;

  public DocumentationController() throws IOException {
    try (var reader =
            new InputStreamReader(
                new ClassPathResource("catalogue/entity-inventory.csv").getInputStream(),
                StandardCharsets.UTF_8);
        var csv =
            CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).get().parse(reader)) {
      inventory = csv.getRecords().stream().map(CSVRecord::toMap).toList();
    }
  }

  void authorized(Jwt jwt) {
    if (!Identity.of(jwt).staff())
      throw KnowledgeStore.fail(403, "Developer or support role required");
  }

  @GetMapping("/catalogue")
  public Object catalogue(
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "50") int size,
      @RequestParam(defaultValue = "") String q) {
    authorized(jwt);
    if (page < 0 || page > 10000 || size < 1 || size > 100 || q.length() > 100)
      throw KnowledgeStore.fail(400, "Invalid pagination");
    return inventory.stream()
        .filter(
            r -> r.get("source_name").toLowerCase(Locale.ROOT).contains(q.toLowerCase(Locale.ROOT)))
        .skip((long) page * size)
        .limit(size)
        .map(
            r ->
                Map.of(
                    "source",
                    r.get("source_name"),
                    "classification",
                    r.get("classification"),
                    "disposition",
                    r.get("mapping_type"),
                    "implementation",
                    r.get("implementation_name"),
                    "status",
                    r.get("completeness"),
                    "lines",
                    r.get("source_lines")))
        .toList();
  }

  @GetMapping("/architecture")
  public Object architecture(@AuthenticationPrincipal Jwt jwt) throws IOException {
    authorized(jwt);
    return Map.of(
        "title",
        "Synthetic architecture and decisions",
        "markdown",
        new ClassPathResource("catalogue/architecture.md")
            .getContentAsString(StandardCharsets.UTF_8));
  }
}
