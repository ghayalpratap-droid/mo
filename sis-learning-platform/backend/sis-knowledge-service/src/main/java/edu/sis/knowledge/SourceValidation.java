package edu.sis.knowledge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.StaticJavaParser;
import java.io.*;
import java.util.*;
import javax.xml.stream.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

public final class SourceValidation {
  public static void validate(String name, String content) {
    String ext = name.substring(name.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    try {
      switch (ext) {
        case "md" -> {}
        case "java" -> {
          var parsed = StaticJavaParser.parse(content);
          if (parsed.getTypes().isEmpty())
            throw new IllegalArgumentException("No Java declaration");
        }
        case "sql" -> CCJSqlParserUtil.parseStatements(content);
        case "properties" -> new Properties().load(new StringReader(content));
        case "yaml", "yml" -> {
          var limits = new LoaderOptions();
          limits.setMaxAliasesForCollections(20);
          limits.setCodePointLimit(500000);
          limits.setNestingDepthLimit(40);
          new Yaml(new SafeConstructor(limits)).load(content);
        }
        case "xml" -> {
          if (content.toUpperCase(Locale.ROOT).contains("<!DOCTYPE"))
            throw new IllegalArgumentException("DOCTYPE is prohibited");
          var factory = XMLInputFactory.newFactory();
          factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
          factory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
          factory.setXMLResolver(
              (a, b, c, d) -> {
                throw new XMLStreamException("External entities prohibited");
              });
          var reader = factory.createXMLStreamReader(new StringReader(content));
          while (reader.hasNext()) reader.next();
          reader.close();
        }
        case "json" -> {
          var node = new ObjectMapper().readTree(content);
          if (!node.isObject()) throw new IllegalArgumentException("Expected OpenAPI JSON object");
          if (!node.has("openapi") && !node.has("swagger"))
            throw new IllegalArgumentException("Expected OpenAPI or Swagger version");
        }
        default -> throw new IllegalArgumentException("Unsupported source format");
      }
    } catch (Exception e) {
      throw KnowledgeStore.fail(
          400, "Source parsing failed; check supported syntax. No previous version changed.");
    }
  }
}
