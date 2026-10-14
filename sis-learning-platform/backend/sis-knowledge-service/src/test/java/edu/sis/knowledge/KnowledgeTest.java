package edu.sis.knowledge;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class KnowledgeTest {
  @Test
  void longLinesRetainAllContent() {
    String text = "x".repeat(5500);
    var parts = KnowledgeStore.chunks(text);
    assertEquals(5500, parts.stream().mapToInt(c -> c.content().stripTrailing().length()).sum());
    assertTrue(parts.size() >= 2);
    assertTrue(parts.stream().allMatch(c -> TokenChunks.count(c.content()) <= 600));
  }

  @Test
  void retainsEvidenceLineNumbers() {
    var parts = KnowledgeStore.chunks("# First\nline one\n# Second\nline two");
    assertEquals(2, parts.size());
    assertEquals(3, parts.get(1).start());
    assertEquals("Second", parts.get(1).heading());
  }

  @Test
  void masksSecretsAndIdentifiers() {
    String result = ModelGateway.scrub("api_key=abc123 email demo@example.com id 1234567890");
    assertFalse(result.contains("abc123"));
    assertFalse(result.contains("demo@example.com"));
    assertFalse(result.contains("1234567890"));
  }

  @Test
  void rejectsExternalEntities() {
    assertThrows(
        org.springframework.web.server.ResponseStatusException.class,
        () ->
            SourceValidation.validate(
                "bad.xml", "<!DOCTYPE x [<!ENTITY xxe SYSTEM 'file:///etc/passwd'>]><x>&xxe;</x>"));
  }

  @Test
  void rejectsUnsafeYaml() {
    assertThrows(
        org.springframework.web.server.ResponseStatusException.class,
        () -> SourceValidation.validate("bad.yaml", "!!java.lang.Runtime {}"));
  }

  @Test
  void validatesJavaSqlAndOpenApi() {
    SourceValidation.validate("Demo.java", "class Demo { int value; }");
    SourceValidation.validate("demo.sql", "CREATE TABLE demo(id integer PRIMARY KEY);");
    SourceValidation.validate("openapi.json", "{\"openapi\":\"3.1.0\"}");
  }
  @Test void preservesPublicUuidToolReferences(){String id="12345678-abcd-1234-abcd-123456789012";assertEquals(id,ModelGateway.scrub(id));}
}
