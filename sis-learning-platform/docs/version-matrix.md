# Version matrix

Initial official-source verification: 2026-10-14. Compiled and tested checkpoint: 2026-10-15 (Asia/Calcutta).

| Component | Pin | Evidence |
|---|---|---|
| Java | 21 | Installed OpenJDK 21.0.12.1 |
| Spring Boot | 3.5.16 | Maven Central; same supported minor as Spring AI build |
| Spring AI | 1.1.8 BOM | Official tagged getting-started.adoc supports Boot 3.4.x/3.5.x; tagged pom uses Boot 3.5.15 |
| Gradle | 8.14.3 | Official distribution with SHA-256 verification |
| React | 19.1.1 | Exact package and lockfile |
| TypeScript | 5.9.3 | Exact package and lockfile |
| Vite | 7.3.7 | Exact package and lockfile |
| Node | 24.19.0 | Installed runtime; .node-version |
| PostgreSQL / pgvector | 17 / 0.8.1 | pgvector/pgvector:0.8.1-pg17, resolve image digest during setup |

Official references: https://raw.githubusercontent.com/spring-projects/spring-ai/v1.1.8/spring-ai-docs/src/main/antora/modules/ROOT/pages/getting-started.adoc and https://raw.githubusercontent.com/spring-projects/spring-ai/v1.1.8/pom.xml . docs.spring.io returned proxy 403; tagged official GitHub sources were fetched successfully. Compilation remains the compatibility gate. No live OpenAI verification is implied.

Additional pinned dependencies: Springdoc 2.8.17 (generated OpenAPI), Commons CSV 1.14.1, JavaParser 3.27.1, JSqlParser 5.3, SnakeYAML 2.5, JTokkit 1.1.0; resolved in Gradle locks and compiled.
