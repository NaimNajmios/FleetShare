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

## 2026-01-06 - Booking Cost Transparency

**Context:** Booking Form (`booking-form.html`, `booking-enhancements.css`)
**Challenge:** Users were presented with only a "Total Cost" without understanding the breakdown (daily rate * duration, fees, discounts). This lack of transparency could lead to trust issues or cart abandonment. The initial state was also "RM 0.00" which felt broken.
**Solution:**
1.  Implemented a dynamic JS-driven cost breakdown section.
2.  Added an "Empty State" for the cost card that guides the user to select dates.
3.  Introduced a "Weekly Discount" logic (client-side simulation for delight) that visually highlights savings with a pill badge.
4.  Used vanilla JS to calculate and animate the values instantly as dates change.
**Impact:** Increased transparency and trust; clearer value proposition with the discount visual.
**Code Example:**
```html
<!-- Dynamic Discount Row -->
<div id="discountRow" class="breakdown-row text-success d-none">
    <span>
        <i class="fas fa-tag me-1"></i>Weekly Discount (10%)
        <span class="discount-pill">SAVED</span>
    </span>
    <span id="discountAmount">- RM 0.00</span>
</div>
```
