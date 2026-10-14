# Legacy to demo mapping

This inventory has 909 names, including uncertain extraction tokens. It is an auditable candidate superset, not a certified entity count. `extraction-candidates.csv` retains the first pass. `source-sections.json` and `data-dictionary.md` retain the evidence. Rows marked DOCUMENTARY_ONLY are not implemented; SYNTHETIC_MAPPING does not imply matching legacy fields. Alternative class mappings remain separate rows. Schema owners are inferred unless the source gives an explicit schema.

| Legacy family | Demo disposition |
|---|---|
| Customer, course, HEI | Synthetic students/courses/providers; no actual identifiers or bank details |
| UG/PG/Apply Once/LLE | One synthetic application aggregate with source category |
| Registration/attendance | Aggregate status plus immutable event history; not legacy confirmation tables |
| CoC subtypes | Explicit type discriminator and deterministic rules; unsupported types remain rejected |
| Tuition/finance | Separate synthetic period, detail and snapshot tables |
| Payments | Exact-decimal instalments; funding-run and bank processing not implemented |
| Bulk imports | Synthetic jobs/results; legacy header/footer/AQ contracts unavailable |
| Catalogue | Synthetic courses/modules/campuses/qualifications; detailed XXLC variants remain documentary |
| Rule metadata | Explanatory rule catalogue; no arbitrary database-driven bean execution |
| Integrations | Named deterministic local receivers; no production contact |
| AI proposed DDL | All 17 named tables migrated; dormant metadata tables are not complete features |

Alternative class definitions and aliases are explicitly retained in mapping-alternatives.csv, with context, source lines and complete supplied section text. AwardAuthority and AcademicYear alternatives remain distinguishable.
