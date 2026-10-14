# Executable security and limitations

Local synthetic users: admin (ADMIN), advisor-a (ADVISOR/provider A), advisor-b (ADVISOR/provider B), developer (DEVELOPER/provider A), support (SUPPORT/provider A), reporter (REPORTER/provider A). The generated local password is supplied through SIS_DEMO_PASSWORD and hashed with BCrypt in the authentication service. JWT signing uses a generated 256-bit secret and a one-hour lifetime. Values stay in ignored .env. Browser tokens are in-memory; no credentials enter query strings or Vite variables.

Bearer-only APIs are stateless and disable CSRF because browsers do not automatically attach these Authorization headers. The Vite proxy gives one local origin; no permissive cross-origin policy is installed. Actuator health is public; other exposed metrics require authentication. Business writes require ADVISOR or ADMIN. Reports require REPORTER or ADMIN; operational reads require support/developer/admin. Scope checks precede object history, imports, exports, citations, tools and feedback. Conversation ownership is stricter than provider membership: another user in the same provider cannot read it.

PostgreSQL migration and runtime roles are separate. Knowledge runtime cannot update/delete audit rows; business runtime cannot update/delete immutable history. All data is fictitious. JPA's version field rejects stale commands, and idempotency receipts detect reused keys with changed payloads. Concurrent unique conflicts return HTTP 409. Transactions include command, history and outbox.

External OIDC proposal: replace the local issuer with a resource-server issuer-uri and validate audience; map only allowlisted IdP roles/providers server-side. Use Authorization Code + PKCE for the portal or a BFF with appropriate CSRF protection. This is a proposal, not an active corporate connection. No corporate IdP information is available.

Outstanding production controls include per-user rate limits, credential rotation/revocation, comprehensive PII classification, TLS ingress deployment, structured distributed traces, policy retention/purge, shared-secret replacement and model quality review. The demo is not presented as production-ready.
