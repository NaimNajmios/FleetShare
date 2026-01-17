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
