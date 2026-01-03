## 2026-01-03 - [Missing Input Validation in Registration]

**Context:** `AuthController.java`, `RegistrationDTO`
**Vulnerability:** Missing Input Validation (Improper Input Validation)
**Severity:** High
**Root Cause:** The `RegistrationDTO` had validation annotations, but the `AuthController`'s `processRegistration` method was missing the `@Valid` annotation on the `ModelAttribute`, causing validation logic to be skipped entirely.
**Fix Applied:** Added `@Valid` annotation to the `RegistrationDTO` parameter and implemented `BindingResult` checking in `AuthController`.
**Prevention:** Ensure all controller methods accepting user input (DTOs) use `@Valid` or `@Validated` and handle `BindingResult` errors.
**References:** CWE-20: Improper Input Validation
