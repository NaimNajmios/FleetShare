## 2026-01-05 - Weak Password Policy Remediation

**Context:** User Registration (`RegistrationDTO`)
**Vulnerability:** Weak Password Policy
**Severity:** High
**Root Cause:** The application was using simple length-based password validation (`@Size(min=8)`) without enforcing complexity requirements (uppercase, lowercase, digits, special characters).
**Fix Applied:**
- Created `@StrongPassword` annotation and `StrongPasswordValidator` utilizing regex to enforce complexity.
- Replaced `@Size` with `@StrongPassword` in `RegistrationDTO`.
- Enforced 8+ characters, 1 uppercase, 1 lowercase, 1 digit, 1 special character, and no whitespace.
**Prevention:** Use `@StrongPassword` annotation for all password fields in DTOs (e.g., password reset, profile update).
**References:**
- NIST SP 800-63B (Password strength guidelines)
- OWASP Authentication Cheat Sheet
