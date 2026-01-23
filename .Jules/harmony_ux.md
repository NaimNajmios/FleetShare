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

## 2025-01-23 - Client-Side URL Synchronization for Filters

**Context:** Vehicle Browse Page (`browse-vehicles.html`), where users filter results by category, price, and transmission.
**User Need:** Users expect their filtered view to persist when they navigate away (e.g., to view vehicle details) and return via the browser's Back button, or when they share a link.
**Observation:** The previous implementation used client-side DOM manipulation to hide/show cards but did not update the URL. Refreshing or navigating back reset all filters to the default state, causing frustration and loss of context.
**Solution Implemented:** Added `updateURLParams()` and `restoreStateFromURL()` functions.
- `updateURLParams`: Serializes filter inputs to `URLSearchParams` and updates the browser URL using `history.replaceState` (without reloading).
- `restoreStateFromURL`: On page load (and `popstate`), reads the URL parameters and programmatically sets the input values, then triggers the filtering logic.
- Included `isRestoringState` guard to prevent redundant URL updates during restoration.
**Impact:** Filters are now "sticky". Users can bookmark specific searches or navigate back from details pages without losing their filter criteria.
**Measurement:** Verified via Playwright script; URL updates immediately upon interaction, and state is correctly restored on reload.

**Code Pattern:**
```javascript
function updateURLParams() {
    if (isRestoringState) return;
    const params = new URLSearchParams();
    // ... populate params from inputs ...
    const newUrl = `${window.location.pathname}?${params.toString()}`;
    history.replaceState(null, '', newUrl);
}

document.addEventListener('DOMContentLoaded', () => {
    restoreStateFromURL();
    window.addEventListener('popstate', restoreStateFromURL);
});
```
