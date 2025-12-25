## 2025-12-25 - Login UX Enhancements

**Context:** Authentication Login Form (`login.html`, `auth.css`)
**Challenge:** The login form lacked immediate feedback upon submission (loading state) and forced users to type passwords blindly (no visibility toggle), leading to friction and potential double-submissions.
**Solution:**
1.  Implemented a progressive loading state using vanilla JS and CSS. We used a new class `.btn-loading-inline` to avoid breaking legacy pages that might rely on the old `.btn-loading` implementation.
2.  Added a password visibility toggle with full accessibility support (ARIA labels, keyboard navigation).
**Impact:** Improved perceived performance and reduced error rates for user login.
**Code Example:**
```html
<!-- Password Toggle Pattern -->
<div class="input-wrapper">
    <input type="password" class="form-input form-input-with-icon" ...>
    <i class="mdi mdi-eye-off-outline input-icon-right" id="togglePassword" role="button" aria-label="Show password" tabindex="0"></i>
</div>

<!-- Loading Button Pattern -->
<button type="submit" class="btn btn-primary" id="loginButton">
    <span class="btn-text">Login</span>
    <span class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true"></span>
</button>
```
