## 2026-01-03 - Quick View Modal for Vehicle Browsing

**Context:** Renter Browsing Flow (`browse-vehicles.html`)
**User Need:** Users need to quickly compare vehicle details (specs, price, location) without navigating away from the search results and losing their filter/scroll context.
**Observation:** The previous flow required clicking "View Details" -> Page Load -> Back -> Restore Context. This was high friction for browsing.
**Solution Implemented:** Implemented a lightweight "Quick View" modal.
-   Added `data-*` attributes to vehicle cards to store details (Fuel, Year, Seats, Owner, Location) without extra DB calls.
-   Added a floating "Eye" icon button on vehicle cards.
-   Used Bootstrap 5 Modal API to show details instantly via JavaScript.
**Impact:** Eliminates page loads for initial screening. Keeps user in the "flow" of browsing.
**Code Pattern:**
```html
<!-- Trigger -->
<button class="quick-view-btn" onclick="openQuickView(this)" data-id="1" data-fuel="Petrol" ...>
    <i class="fas fa-eye"></i>
</button>

<!-- JS Populator -->
<script>
function openQuickView(btn) {
    const card = btn.closest('.vehicle-card');
    // ... extract data ...
    document.getElementById('qvTitle').textContent = data.title;
    new bootstrap.Modal(document.getElementById('quickViewModal')).show();
}
</script>
```
