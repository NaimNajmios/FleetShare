## 2026-01-07 - Strong Password Validation

**Context:** User Registration Form (`RegistrationDTO`)
**Vulnerability:** Weak Password Policy
**Severity:** Medium
**Root Cause:** The application was only checking for a minimum password length of 8 characters, allowing weak passwords like "password" or "12345678".
**Fix Applied:** Implemented a custom `@StrongPassword` annotation and `StrongPasswordValidator` that enforces:
- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one digit
- At least one special character
- No whitespace
**Prevention:** Use the `@StrongPassword` annotation on all password fields in DTOs.
**References:** CWE-521: Weak Password Requirements
