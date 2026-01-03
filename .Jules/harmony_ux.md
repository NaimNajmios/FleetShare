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

## 2024-05-23 - Delightful Booking Date Selection

**Context:** The "Book Vehicle" page (`booking-form.html`) where users select their rental dates.
**User Need:** Users need to clearly see the rental period, duration, and total cost in an intuitive and responsive way.
**Observation:** The original form used standard vertical date inputs, providing no visual indication of a "range". The duration was just text, and the cost update was instant but lifeless, lacking the "delight" of a modern booking experience.
**Solution Implemented:**
- **Visual Grouping:** Moved Pickup and Return dates to a side-by-side grid layout to visually imply a range.
- **Enhanced Inputs:** Added icons (`calendar-alt`, `calendar-check`) within the inputs using Bootstrap input groups for better affordance.
- **Quick Select Chips:** Transformed plain buttons into styled "chips" with icons, making them feel like actionable tools.
- **Duration Visualizer:** Added a "Duration Bar" that fills up based on the length of the rental, providing immediate visual feedback on the scale of the booking.
- **Cost Animation:** Implemented a "Count Up" animation for the total price, turning a dry calculation into a micro-interaction that draws attention to the value.
- **Accessibility:** Ensured the cost container uses `aria-live="polite"` so screen readers announce the price change.

**Impact:**
- **Clarity:** The relationship between start and end dates is visually reinforced.
- **Delight:** The animations (price count-up, duration bar) make the form feel responsive and polished.
- **Efficiency:** Quick select chips allow users to book standard durations with a single click.

**Code Pattern:**
```html
<!-- Duration Visualizer -->
<div id="durationDisplay" class="duration-display hidden">
    <div class="d-flex align-items-center gap-2">
        <i class="fas fa-hourglass-half"></i>
        <span id="durationText">3 Days</span>
    </div>
    <div class="duration-bar">
        <div id="durationProgress" class="duration-progress" style="width: 21%;"></div>
    </div>
</div>

<!-- Animated Cost Display -->
<div id="totalCostContainer" class="cost-card pulse-highlight" aria-live="polite">
    <span id="totalCostDisplay">RM 360.00</span>
</div>
```
