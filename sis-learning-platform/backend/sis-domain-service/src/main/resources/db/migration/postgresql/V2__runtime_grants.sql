GRANT USAGE ON SCHEMA business TO sis_business;
GRANT SELECT,INSERT,UPDATE ON ALL TABLES IN SCHEMA business TO sis_business;
REVOKE UPDATE ON business.histories FROM sis_business;
REVOKE ALL ON business.flyway_schema_history FROM sis_business;
