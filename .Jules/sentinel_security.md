## 2026-01-14 - Weak Password Policy

**Context:** User Registration (`RegistrationDTO`)
**Vulnerability:** Weak Password Policy
**Severity:** Medium
**Root Cause:** The application only enforced a minimum length of 8 characters without requiring complexity (numbers, symbols, mixed case), making passwords susceptible to brute-force attacks.
**Fix Applied:** Implemented `@StrongPassword` custom validation annotation enforcing 12+ chars, uppercase, lowercase, numbers, and symbols.
**Prevention:** Use strong custom validators for security-critical fields instead of basic length checks.
**References:** OWASP ASVS V2.1
