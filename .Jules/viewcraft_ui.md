## 2026-01-16 - Price Range Input Enhancement

**Context:** Renter Vehicle Browse Page (`browse-vehicles.html`)
**Challenge:** The price range inputs were naked `input[type=number]` fields without currency indicators or clear association, leading to poor affordance.
**Solution:** Wrapped inputs in Bootstrap `.input-group` with "RM" prefix. Refactored custom styling into a `.price-filter-group` class to handle border radius correctly within the group, avoiding inline styles.
**Impact:** Improved clarity and visual polish of the filter sidebar.
**Code Example:**
```html
<div class="input-group flex-nowrap price-filter-group" style="flex: 1;">
    <span class="input-group-text bg-light border-end-0 text-muted px-2">RM</span>
    <input type="number" class="form-control price-input border-start-0 ps-1" aria-label="Minimum Price">
</div>
```
