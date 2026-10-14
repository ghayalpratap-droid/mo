# Typed read-only tools

All are new demo facades. Documentary legacy API mappings are in Ne.md:526–565 and 2658–2709. Provider is never a model argument. Each call forwards authenticated identity, the domain reauthorizes the object, and knowledge records outcome. Timeout: connect 2s/read 5s. Turn budget: at most five calls / 15s. No replay, write, stored procedure or funding tool exists.

| Tool | Input | Output / backing API | Permission / bound |
|---|---|---|---|
| applicationSummary | UUID | status, registration, attendance, source/asOf; GET applications/{id}/summary | Object provider; one record |
| academicYears | none | GET academic-years | Authenticated; seeded reference list |
| courseOptions | none | GET courses | Provider scope; seeded reference list |
| referenceData | none | GET reference-data | Authenticated; fixed categories |
| confirmationHistory | UUID | GET applications/{id}/history | Object provider; history projection |
| cocHistory | UUID | GET applications/{id}/cocs | Object provider; synthetic decisions |
| tuitionPeriods | UUID | GET applications/{id}/tuition-periods | Object provider; synthetic finance projection |
| featureToggles | none | GET operations/toggles | Support/developer/admin; fixed allowlist |
| syncExceptions | none | GET operations/exceptions | Support/developer/admin; maximum 50 masked rows |

Application search as a masked model tool, course detail tool, separate CoC dry-run callback and full reference-domain catalogue remain incomplete. Normal portal APIs provide application search and side-effect-free CoC preview. Offline mode does not run an autonomous LLM tool loop; explicit current-status routing demonstrates real API reads.
