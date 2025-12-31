## 2025-12-30 - [Input Validation Improvement]

**Context:** `RenterController.updateProfile`
**Vulnerability:** Weak input validation (Manual map parsing)
**Severity:** Medium
**Root Cause:** The controller was manually extracting and validating fields from a `Map<String, String>` payload, which is error-prone and bypasses standard Spring Validation mechanisms.
**Fix Applied:**
1. Introduced `RenterProfileUpdateRequest` DTO with JSR-380 Bean Validation annotations (`@NotBlank`, `@Pattern`, `@Size`).
2. Updated `RenterController.updateProfile` to use `@Valid @RequestBody RenterProfileUpdateRequest`.
3. Added `handleValidationExceptions` to `GlobalExceptionHandler` to return structured JSON errors for validation failures.
4. Added `spring-boot-starter-validation` dependency to `pom.xml`.
**Prevention:** Always use `@Valid` annotated DTOs for request bodies in Controllers. Avoid using `Map<String, Object>` for structured data.
**References:** CWE-20: Improper Input Validation
