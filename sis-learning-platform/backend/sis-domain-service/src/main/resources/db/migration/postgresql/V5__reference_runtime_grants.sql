GRANT SELECT,INSERT,UPDATE ON ALL TABLES IN SCHEMA business TO sis_business;
REVOKE UPDATE,DELETE ON business.histories,business.tuition_period_histories FROM sis_business;
REVOKE ALL ON business.flyway_schema_history FROM sis_business;
