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

## 2025-05-23 - Client-Side URL Synchronization (Deep Linking)

**Context:** Renter Vehicle Browse Page ()
**Challenge:** Users filtering vehicles (by category, price, etc.) lost their context when refreshing the page or sharing the link, as filters were purely client-side state.
**Solution:** Implemented  in the  function to serialize filter state to URL query parameters. Added a  function to parse these parameters on  and , restoring the UI state (checkboxes, inputs).
**Impact:** Significantly improved usability by allowing link sharing and preserving state on refresh.
**Code Example:**
```javascript
// URL Synchronization
if (!isRestoringState) {
    const params = new URLSearchParams();
    if (checkedCategories.length > 0) params.set('category', checkedCategories.join(','));
    // ...
    const newUrl = window.location.pathname + (params.toString() ? '?' + params.toString() : '');
    window.history.replaceState(null, '', newUrl);
}
```

## 2025-05-23 - Client-Side URL Synchronization (Deep Linking)

**Context:** Renter Vehicle Browse Page (`browse-vehicles.html`)
**Challenge:** Users filtering vehicles (by category, price, etc.) lost their context when refreshing the page or sharing the link, as filters were purely client-side state.
**Solution:** Implemented `history.replaceState` in the `applyFilters` function to serialize filter state to URL query parameters. Added a `loadFiltersFromUrl` function to parse these parameters on `DOMContentLoaded` and `popstate`, restoring the UI state (checkboxes, inputs).
**Impact:** Significantly improved usability by allowing link sharing and preserving state on refresh.
**Code Example:**
```javascript
// URL Synchronization
if (!isRestoringState) {
    const params = new URLSearchParams();
    if (checkedCategories.length > 0) params.set('category', checkedCategories.join(','));
    // ...
    const newUrl = window.location.pathname + (params.toString() ? '?' + params.toString() : '');
    window.history.replaceState(null, '', newUrl);
}
```
