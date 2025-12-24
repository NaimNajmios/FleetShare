## 2024-05-23 - Initial Setup
**Learning:** Understanding the project structure and identifying potential improvements.
**Action:** Explored `src/main/resources/templates` and identified `renter/my-bookings.html` as a candidate for improvement.

## 2025-12-23 - Accessibility Improvements
**Learning:** Icon-only buttons in the admin dashboard navigation lacked `aria-label` attributes, making them inaccessible to screen readers.
**Action:** Added `aria-label` to sidebar toggle, notification dropdown, search input, and mobile menu toggle in `fragments/nav.html`. Improved `alt` text for user profile image.
## 2025-12-24 - Admin Table Accessibility
**Learning:** Data tables in the admin dashboard lacked semantic structure and accessible labels. Screen reader users would struggle to understand column relationships or the purpose of identical 'View' buttons.
**Action:** Added `scope="col"` to headers, a visually hidden `<caption>`, and dynamic `aria-label` attributes to search/filter inputs and row action buttons.
