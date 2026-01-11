## 2026-01-10 - Information Leakage & Credential Exposure Hardening

**Context:** Application Configuration and Entity Definition
**Vulnerability:**
1.  **Information Leakage:** `server.error.include-message=always` in `application.properties` was exposing internal exception messages in API responses.
2.  **Credential Exposure:** `User` entity's `hashedPassword` field lacked `@JsonIgnore`, risking exposure in JSON serialization.
**Severity:** Medium
**Root Cause:**
1.  Insecure default configuration for development convenience left enabled.
2.  Missing Jackson annotation on sensitive entity field.
**Fix Applied:**
1.  Updated `application.properties` to set `server.error.include-message`, `include-binding-errors`, and `include-stacktrace` to `never`.
2.  Added `@JsonIgnore` to `hashedPassword` in `User.java`.
3.  Removed `TestPasswordEncoder.java` (hardcoded credentials).
**Prevention:**
1.  Use `never` for error attributes in production configuration.
2.  Always annotate sensitive fields (passwords, tokens) with `@JsonIgnore` in Entities and DTOs.
**References:** CWE-209 (Information Exposure Through an Error Message), CWE-200 (Exposure of Sensitive Information to an Unauthorized Actor)
