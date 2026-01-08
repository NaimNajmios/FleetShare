## 2026-01-08 - Weak Password Policy and Missing Terms Enforcement

**Context:** User Registration (`RegistrationDTO`)
**Vulnerability:** Weak Password Policy & Missing Terms Validation
**Severity:** High
**Root Cause:** `RegistrationDTO` only used `@Size(min=8)` for passwords and `agreeTerms` field lacked validation annotation.
**Fix Applied:**
- Implemented custom `@StrongPassword` annotation and validator.
- Enforced complexity: Upper, Lower, Digit, Special Char, No Whitespace.
- Added `@AssertTrue` to `agreeTerms` in `RegistrationDTO`.
- Added unit tests for validation logic.
**Prevention:** Always use strong password validators and verify boolean flags for critical agreements.
**References:** CWE-521: Weak Password Requirements
