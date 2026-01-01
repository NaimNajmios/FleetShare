## 2026-01-01 - Input Validation Pattern

**Context:** `OwnerController` endpoints for creating and updating vehicles (`/owner/vehicles/add`, `/owner/vehicles/update/{id}`).
**Vulnerability:** Insecure Design / Lack of Input Validation. The controller was accepting `AddVehicleRequest` DTOs without any validation annotations, relying solely on business logic which could be bypassed or fail on unexpected input types (e.g. negative numbers, massive strings).
**Severity:** Medium
**Root Cause:** Missing Bean Validation annotations on DTOs and missing `@Valid` checks in Controller methods.
**Fix Applied:**
1. Annotated `AddVehicleRequest` with strict constraints (`@NotBlank`, `@Size`, `@Min`, `@Max`, `@PositiveOrZero`, `@DecimalMin`).
2. Updated `OwnerController` to use `@Valid` on request bodies and check `BindingResult`.
3. Implemented defensive error handling that redirects back to the form with a validation summary if errors occur.
**Prevention:** Always use `@Valid` and Bean Validation annotations on all input DTOs.
