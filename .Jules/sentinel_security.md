## 2025-12-25 - CSRF Protection Re-enabled

**Context:** `fleetshare/src/main/java/com/najmi/fleetshare/config/SecurityConfig.java`
**Vulnerability:** Cross-Site Request Forgery (CSRF)
**Severity:** High
**Root Cause:** CSRF protection was explicitly disabled (`.csrf(csrf -> csrf.disable())`) in `SecurityConfig.java`. This allowed attackers to potentially trick authenticated users into performing unintended actions.
**Fix Applied:**
1.  Removed the line disabling CSRF in `SecurityConfig.java`.
2.  Added CSRF meta tags (`_csrf` and `_csrf_header`) to all layout templates (`renter-layout.html`, `admin-layout.html`, `owner-layout.html`).
3.  Updated all AJAX `POST` requests in `profile.html` (renter, admin, owner) and `view-vehicle.html` (admin, owner) to include the CSRF token in the request headers.
**Prevention:** Always ensure CSRF is enabled for web applications using session-based authentication. Use Spring Security's default CSRF protection and ensure client-side code handles the token correctly.
**References:** OWASP Top 10 - Broken Access Control
