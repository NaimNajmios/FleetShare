## 2026-01-11 - Enforced Strong Password Policy

**Context:** The registration process (`RegistrationDTO`) used a weak password validation policy (`@Size(min=8)`).
**Vulnerability:** Weak Password Policy
**Severity:** High
**Root Cause:** Lack of complexity enforcement in the validation logic.
**Fix Applied:**
- Created a custom annotation `@StrongPassword` and `StrongPasswordValidator`.
- Replaced `@Size` with `@StrongPassword` in `RegistrationDTO`.
- Added strict rules: min 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special char, no whitespace.
- Also added `@AssertTrue` to `agreeTerms` in `RegistrationDTO` to ensure terms acceptance.
**Prevention:** Always use custom validators or strong regex patterns for password fields.
**References:** OWASP Authentication Cheatsheet
