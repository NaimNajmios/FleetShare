## 2024-05-23 - Centralized Toast Notification System

**Context:** The application had a `toast.html` fragment, but it was incorrectly included inside the `layout:fragment="content"` block in `renter-layout.html`, causing it to be overwritten by page content. This forced individual pages like `profile.html` to implement their own ad-hoc toast logic, leading to inconsistency and code duplication.

**User Need:** Users need consistent, non-intrusive feedback for actions (e.g., "Profile updated", "Booking cancelled") across the application without jarring alerts or inconsistent styling.

**Observation:**
- `renter-layout.html` swallowed the toast fragment.
- `profile.html` duplicated toast CSS and JS.
- `booking-details.html` relied on `alert()` as a fallback.

**Solution Implemented:**
1. Moved the toast fragment inclusion in `renter-layout.html` outside the content block.
2. Removed duplicate toast logic and CSS from `profile.html`.
3. Replaced `alert()` with `window.showToast()` in `booking-details.html`.

**Impact:**
- **Consistency:** All renter pages now use the same beautiful toast notifications.
- **Maintainability:** Removed ~50 lines of duplicate code/CSS.
- **Delight:** Users get smooth, animated feedback instead of browser alerts.

**Code Pattern:**
```html
<!-- Correct Layout Implementation -->
<main layout:fragment="content">
    <!-- Page Content -->
</main>
<!-- Toast outside content block -->
<div th:replace="~{fragments/toast :: toast}"></div>
```

## 2024-05-23 - [Simplified Input Interactions]

**Context:** Owner Vehicle Onboarding (add-vehicle.html)
**User Need:** Fleet owners need to quickly add vehicles with price and photos.
**Observation:** The previous design hid the "Rate Per Day" input inside a modal, requiring 3 extra clicks. The image upload had no visual feedback.
**Solution Implemented:** Replaced the modal with a direct Bootstrap input group. Added a drag-and-drop zone with immediate image preview.
**Impact:** Reduced clicks for rate entry from 4 to 1. Provided immediate confirmation of image selection.
**Code Pattern:**
```html
<!-- Input Group Pattern -->
<div class="input-group">
    <span class="input-group-text">RM</span>
    <input type="number" class="form-control" name="ratePerDay">
</div>

<!-- Image Preview Pattern -->
<div class="image-dropzone" role="button" tabindex="0">
    <img id="imagePreview" src="#" style="display:none;">
    <div id="dropzoneContent">...</div>
</div>
```

## 2024-05-23 - [Search Filter Persistence & Deep Linking]

**Context:** The "Browse Vehicles" page (`/renter/vehicles`) allows users to filter vehicles by category, transmission, price, and search keywords.
**User Need:** Users want to be able to share their search results with others or navigate back to their filtered list after viewing a vehicle's details.
**Observation:** Currently, all filtering is client-side and ephemeral. Navigating away and back resets all filters, causing frustration and lost context.
**Solution Implemented:** Implemented Deep Linking using `history.replaceState` and `URLSearchParams`.
- When filters change, the URL query parameters are updated.
- On page load, filters are populated from the URL query parameters.
- `popstate` event listener handles browser Back/Forward navigation.
**Impact:**
- Users can bookmark specific searches.
- Browser "Back" button now correctly restores the previous filter state.
- Friction reduced when browsing multiple vehicles.

**Code Pattern:**
```javascript
// Sync filters to URL
function updateURLParams() {
    const params = new URLSearchParams();
    // ... collect filter values ...
    const newUrl = window.location.pathname + (params.toString() ? '?' + params.toString() : '');
    window.history.replaceState(null, '', newUrl);
}

// Restore filters from URL
function loadFiltersFromURL() {
    const params = new URLSearchParams(window.location.search);
    // ... apply values to inputs ...
    if (shouldApply) applyFilters();
}
```
