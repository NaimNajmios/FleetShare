## 2026-01-06 - Strong Password Validation Enforced

**Context:** User Registration (`RegistrationDTO`)
**Vulnerability:** Weak Password Policy
**Severity:** High
**Root Cause:** The application previously only enforced a minimum length of 8 characters for passwords, allowing weak passwords like "password" or "12345678".
**Fix Applied:** Implemented a custom `@StrongPassword` annotation and `StrongPasswordValidator` that enforces:
- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one digit
- At least one special character
- No whitespace
**Prevention:** Use standard or custom JSR-380 validators for all security-sensitive input fields.
**References:** CWE-521: Weak Password Requirements
