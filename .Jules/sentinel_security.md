## 2026-01-12 - Enforce Strong Passwords

**Context:** User Registration (`RegistrationDTO`)
**Vulnerability:** Weak Password Policy
**Severity:** Medium
**Root Cause:** The application was only checking for a minimum length of 8 characters for passwords, allowing weak passwords like "password" or "12345678".
**Fix Applied:** Implemented a custom `@StrongPassword` annotation and `StrongPasswordValidator` that enforces:
- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one digit
- At least one special character
- No whitespace
**Prevention:** Always use strong password policies for user authentication. Use custom validators or standard libraries (like Passay) to enforce complexity.
**References:** CWE-521: Weak Password Requirements
