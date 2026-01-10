## 2025-05-18 - Card-Based Payment Method Selection

**Context:** Renter Payment Management (`manage-payment.html`)
**User Need:** Users need to select a payment method (Card, Transfer, Cash) to proceed with or verify their booking.
**Observation:** The previous dropdown menu (`<select>`) was less intuitive for mobile users and hid the available options behind a click. It lacked visual weight for a primary decision step.
**Solution Implemented:** Replaced the standard dropdown with a grid of interactive "cards" (`.payment-option-card`). Each card displays an icon and label, making all options immediately visible and providing a larger touch target.
**Impact:**
- Improved discoverability of payment options.
- Enhanced touch usability on mobile devices.
- Visual feedback (border, background, shadow) provides clear confirmation of selection.
- Maintained progressive disclosure of the detailed payment instructions below the selection.

**Code Pattern:**
```html
<div class="payment-options-grid" role="radiogroup" aria-label="Payment Method">
    <button type="button" class="payment-option-card active" data-value="card" role="radio" aria-checked="true">
        <i class="fab fa-cc-visa text-primary mb-2"></i>
        <span>Card / FPX</span>
    </button>
    <!-- ... other options ... -->
</div>
```
