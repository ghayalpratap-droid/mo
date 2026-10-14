# Complete capability coverage plan

Each row remains in scope. Implementation and tests must be recorded in traceability.csv; a plan is not completed functionality.

| Module | Required implementation | Acceptance evidence |
|---|---|---|
| Identity and access | Typed API, server authorization, persistence, UI, audit, documented source mapping | Unit/API/integration/UI tests; failure and denial paths |
| Provider and reference data | Typed API, server authorization, persistence, UI, audit, documented source mapping | Unit/API/integration/UI tests; failure and denial paths |
| Students | Typed API, server authorization, persistence, UI, audit, documented source mapping | Unit/API/integration/UI tests; failure and denial paths |
| Applications | Typed API, server authorization, persistence, UI, audit, documented source mapping | Unit/API/integration/UI tests; failure and denial paths |
| Course catalogue | Typed API, server authorization, persistence, UI, audit, documented source mapping | Unit/API/integration/UI tests; failure and denial paths |
| Tuition and finance | Typed API, server authorization, persistence, UI, audit, documented source mapping | Unit/API/integration/UI tests; failure and denial paths |
| Registration and attendance | Typed API, server authorization, persistence, UI, audit, documented source mapping | Unit/API/integration/UI tests; failure and denial paths |
| CoC | Typed API, server authorization, persistence, UI, audit, documented source mapping | Unit/API/integration/UI tests; failure and denial paths |
| Configured validation | Typed API, server authorization, persistence, UI, audit, documented source mapping | Unit/API/integration/UI tests; failure and denial paths |
| Payments | Typed API, server authorization, persistence, UI, audit, documented source mapping | Unit/API/integration/UI tests; failure and denial paths |
| CSV imports | Typed API, server authorization, persistence, UI, audit, documented source mapping | Unit/API/integration/UI tests; failure and denial paths |
| Publisher and integrations | Typed API, server authorization, persistence, UI, audit, documented source mapping | Unit/API/integration/UI tests; failure and denial paths |
| Reports and operations | Typed API, server authorization, persistence, UI, audit, documented source mapping | Unit/API/integration/UI tests; failure and denial paths |
| Knowledge and AI | Typed API, server authorization, persistence, UI, audit, documented source mapping | Unit/API/integration/UI tests; failure and denial paths |

Source-specific flows: UG/EDS intake, PG/NI notification, Apply Once, LLE intake/catalogue, attendance-to-BAS, bulk CoC, PG CoC-to-BCF, registration CSV, course closure, disposition, validation/search, negative-balance reporting. External adapters are simulators with explicit contract gaps. Every source candidate has a row in entity-inventory.csv; unsupported mappings stay pending rather than being claimed as six generic entities.
