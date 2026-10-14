# Source assessment

The supplied Ne.md is documentary evidence, not inspected legacy source. Its SHA-256 is `2890534b44b33f151a4afed19f97e5b2263e1e08d6aff7298d34b9b8889c3e00`.

| Evidence | Classification | Source |
|---|---|---|
| Java 8, Boot 2.3.12, JSF, Oracle | DOCUMENTED_CURRENT | Ne.md:9–21, 1321–1380 |
| RabbitMQ inbound and Oracle AQ/JMS outbound | DOCUMENTED_CURRENT | Ne.md:15, 1621–1657 |
| React, Spring AI, OpenAI, pgvector | DOCUMENTED_PROPOSAL | Ne.md:592–735, 1773–2145 |
| Kafka | DOCUMENTED_PROPOSAL; explicitly absent in current-state analysis | Ne.md:1623–1632, 2710 onward |
| Entity counts | Documentary counts, not complete field specifications | Ne.md:3194–3239 |
| Schema ownership | Inferred in the supplied index | Ne.md:4762–4774 |
| Synthetic providers, students, eligibility and finance rules | DEMO_ASSUMPTION | This project's implementation |
| Production PL/SQL, Mule contracts, shared-library internals | UNKNOWN | Ne.md:3177–3187 |

Part 1 is empty (3220–3226). Part 2 supplies notes but no complete definitions (3227–3240). The introduction is repeated at 4803. TuitionFeeForecast is an inactive JPA mapping (3233), though a legacy projection of the same name is described at 3960. AwardAuthority and AcademicYear have alternative JLBHEI/JAVHEP mappings; these must not be collapsed into a single verified class definition. Assertions within the source that all entities were read refer to the document author's work, not to evidence available here.

The complete supplied text is retained at the project root for line references. It is not automatically sent to an external model. Source files and transactional data are separate trust boundaries.
