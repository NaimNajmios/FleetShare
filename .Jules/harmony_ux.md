## 2025-12-28 - Responsive Filter Offcanvas

**Context:** The "Browse Vehicles" page (`browse-vehicles.html`) on mobile devices.
**User Need:** Users on mobile need to filter vehicles without the filter interface pushing the main content down or being difficult to access.
**Observation:** The previous filter sidebar was displayed as a static block at the top of the content on mobile, consuming significant vertical space and pushing the vehicle results below the fold.
**Solution Implemented:**
- Converted the `filter-sidebar` into a responsive `offcanvas-lg` component.
- On desktop (≥992px), it remains a sticky sidebar.
- On mobile (<992px), it becomes a hidden-by-default drawer.
- Added a toggle button next to the search bar for mobile users to access filters.
- Implemented proper accessibility attributes (`aria-expanded`, `aria-controls`, `aria-label`).

**Impact:**
- Improved mobile layout by prioritizing vehicle content.
- Standardized interaction pattern using familiar mobile drawer for filters.
- Better screen real estate usage on small screens.

**Code Pattern:**
```html
<!-- Mobile Toggle Button -->
<button class="btn btn-primary d-lg-none" type="button" data-bs-toggle="offcanvas" data-bs-target="#filterSidebar">
    <i class="fas fa-filter"></i>
</button>

<!-- Responsive Sidebar -->
<aside class="filter-sidebar offcanvas-lg offcanvas-start" id="filterSidebar">
    <div class="offcanvas-header d-lg-none">
        <h5 class="offcanvas-title">Filters</h5>
        <button type="button" class="btn-close" data-bs-dismiss="offcanvas"></button>
    </div>
    <div class="offcanvas-body">
        <!-- Filter Content -->
    </div>
</aside>
```
