## 2024-12-31 - [Insecure File Upload]

**Context:** `AdminController` and `OwnerController` implemented duplicate, manual file upload logic for vehicle images.
**Vulnerability:** Arbitrary File Upload / Remote Code Execution (RCE)
**Severity:** High
**Root Cause:** The controllers relied solely on the client-provided `Content-Type` header (MIME sniffing) and the file extension without verifying the actual file content (Magic Bytes). This allowed attackers to upload malicious scripts (e.g., PHP, JSP) disguised as images.
**Fix Applied:**
1.  Enhanced `FileStorageService` to include robust validation:
    *   **Magic Byte Validation:** Reads the first few bytes of the file stream to verify it matches known signatures for JPEG, PNG, GIF, WEBP, and PDF.
    *   **Extension Whitelisting:** Strictly enforces allowed extensions (`.jpg`, `.jpeg`, `.png`, `.gif`, `.webp`, `.pdf`).
    *   **Filename Sanitization:** Generates a unique filename (UUID + Timestamp) to prevent path traversal and overwrites.
2.  Refactored `AdminController` and `OwnerController` to delegate file upload handling to `FileStorageService.storeVehicleImage()`.
**Prevention:** Always validate file content server-side using file signatures (Magic Bytes), never trust client headers, and use a centralized service for file operations.
**References:** CWE-434: Unrestricted Upload of File with Dangerous Type
