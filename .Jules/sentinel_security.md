## 2026-01-03 - [Missing Input Validation in Registration]

**Context:** `AuthController.java`, `RegistrationDTO`
**Vulnerability:** Missing Input Validation (Improper Input Validation)
**Severity:** High
**Root Cause:** The `RegistrationDTO` had validation annotations, but the `AuthController`'s `processRegistration` method was missing the `@Valid` annotation on the `ModelAttribute`, causing validation logic to be skipped entirely.
**Fix Applied:** Added `@Valid` annotation to the `RegistrationDTO` parameter and implemented `BindingResult` checking in `AuthController`.
**Prevention:** Ensure all controller methods accepting user input (DTOs) use `@Valid` or `@Validated` and handle `BindingResult` errors.
**References:** CWE-20: Improper Input Validation

## 2026-01-09 - Path Traversal Vulnerability in File Deletion

**Context:** `FileStorageService.java`, method `deleteFile(String fileUrl)`
**Vulnerability:** Path Traversal (CWE-22)
**Severity:** High
**Root Cause:** The application was concatenating the upload directory with a user-supplied relative path without normalizing or validating that the resulting path was still within the intended directory.
**Fix Applied:** Normalized the path using `toAbsolutePath().normalize()` and verified that the resolved path starts with the normalized upload directory.
**Prevention:** Always normalize paths and verify they are contained within the intended directory before performing file operations. Use `Path.startsWith()` checks.
**References:** https://cwe.mitre.org/data/definitions/22.html
