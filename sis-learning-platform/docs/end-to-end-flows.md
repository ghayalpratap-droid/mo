# Documentary end-to-end mappings

These are DOCUMENTED_CURRENT statements from Ne.md:207–427. Referenced Java/PL SQL is unavailable. The demo uses source categories and named simulators; it does not reproduce production procedures.

| Flow | Documented service | Data/rules | Integration | Demo limit |
|---|---|---|---|---|
| UG EDS intake | sis-application-service | TuitionPeriodRepository | sis-publisher and AQ | Synthetic aggregate or named receiver; production contract unavailable |
| PG NI notification | sis-notification-service | Configured AppRulesEngine | LAP AQ and publisher | Synthetic aggregate or named receiver; production contract unavailable |
| Apply Once | sis-applyonce-service | PGNewAppRulesEngine | CEM and DSFS | Synthetic aggregate or named receiver; production contract unavailable |
| LLE application catalogue | lle-application-service and lle-course-service | XXLE and XXLC repositories | Mule and CMS | Synthetic aggregate or named receiver; production contract unavailable |
| Attendance to bursary | sis-attendance-service | Staged attendance JDBC | BAS | Synthetic aggregate or named receiver; production contract unavailable |
| Bulk CoC | sis-coc-service | CSV processor and validation | RabbitMQ and AQ | Synthetic aggregate or named receiver; production contract unavailable |
| PG CoC to BCF | pg-coc-service | KIFPK032 cursor and callback | BCF | Synthetic aggregate or named receiver; production contract unavailable |
| Registration CSV | JMBSRI | SpringSisCsvService | Oracle AQ | Synthetic aggregate or named receiver; production contract unavailable |
| Course closure | sis-application-service | kifpk053 | CMS RabbitMQ | Synthetic aggregate or named receiver; production contract unavailable |
| Disposition | sis-application-service | KIFPK052 and KIFPK059 | GDPR RabbitMQ | Synthetic aggregate or named receiver; production contract unavailable |
| Validation search | sis-validation-service and sis-data-service | Rules metadata and SQL | Legacy portal | Synthetic aggregate or named receiver; production contract unavailable |
| Negative balance | sis-reports-service | Report DAO | CLASS ORDS | Synthetic aggregate or named receiver; production contract unavailable |

For implemented commands, React → typed request DTO → bearer validation → role/provider/version checks → controller → deterministic service → JPA/JDBC transaction → application/history/outbox → response DTO → Query invalidation. Duplicate keys return the prior result or conflict on changed payload. Stale versions return 409. Role denial returns 403, hidden object returns 404, invalid input returns 400/422 or explicit rejected CoC. Database failures must not produce fake success. Outbox delivery happens after commit and is retried independently. CSV crash recovery and production dead-letter replay remain incomplete.
