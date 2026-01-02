## 2026-01-02 - Insecure Input Handling in Owner Profile Update

**Context:** `OwnerController.java`, method `updateProfile`.
**Vulnerability:** Input Validation / Mass Assignment.
**Severity:** Medium
**Root Cause:** The endpoint was accepting a raw `Map<String, String>` and manually parsing values without strong type checking or validation. It also bypassed the standard Bean Validation framework.
**Fix Applied:**
1.  Created `OwnerProfileUpdateRequest` DTO with strict validation annotations (`@NotBlank`, `@Size`, `@Pattern`).
2.  Refactored `updateProfile` to accept `@Valid OwnerProfileUpdateRequest` instead of `Map`.
3.  Leveraged existing `GlobalExceptionHandler` to handle `MethodArgumentNotValidException` and return consistent JSON error responses.
**Prevention:** Always use `@Valid` annotated DTOs for `@RequestBody` inputs. Avoid using `Map` or `JsonNode` for request bodies unless absolutely necessary and strictly validated.
**References:** OWASP Top 10: A03:2021 – Injection.
