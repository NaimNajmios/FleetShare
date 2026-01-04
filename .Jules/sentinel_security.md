## 2026-01-04 - Weak Password Policy Vulnerability

**Context:** `RegistrationDTO` used standard `@Size` validation for passwords.
**Vulnerability:** Weak Password Policy
**Severity:** Medium
**Root Cause:** The application only enforced a minimum length of 8 characters for passwords, allowing weak passwords like "password" or "12345678".
**Fix Applied:** Replaced `@Size(min = 8)` with a custom `@StrongPassword` annotation in `RegistrationDTO`. This annotation uses a `StrongPasswordValidator` that enforces:
  - Minimum 8 characters
  - At least one uppercase letter
  - At least one lowercase letter
  - At least one digit
  - At least one special character
**Prevention:** Always use strong password complexity requirements for user registration and password updates. Use custom validators or established libraries (like Passay) rather than simple length checks.
**References:** OWASP Top 10 - Broken Authentication
