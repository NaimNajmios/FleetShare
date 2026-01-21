## 2024-05-24 - URL Synchronization for Filters

**Context:** Renter Browse Vehicles (`browse-vehicles.html`)
**User Need:** Users want to share search results or return to their filtered view after viewing vehicle details, without losing their context.
**Observation:** Previously, filters were ephemeral. Navigating to a vehicle detail page and hitting "Back" would reset all filters, causing frustration and loss of context.
**Solution Implemented:** Implemented bi-directional synchronization between the filter form and the URL query parameters using `history.replaceState` and `URLSearchParams`.
**Impact:**
- **Context Preservation:** Back/Forward navigation now perfectly restores the search state.
- **Shareability:** Users can copy-paste the URL to share specific search results (e.g., "?category=suv&minPrice=100").
- **Cohesion:** Matches expected behavior for e-commerce/listing sites.

**Code Pattern:**
```javascript
// Sync form -> URL
function updateURL() {
    const params = new URLSearchParams();
    // ... collect inputs ...
    history.replaceState(null, '', '?' + params.toString());
}

// Sync URL -> form
function loadFiltersFromURL() {
    const params = new URLSearchParams(window.location.search);
    // ... populate inputs ...
    applyFilters(false); // Apply visual filter without re-triggering URL update
}

// Listeners
window.addEventListener('popstate', loadFiltersFromURL);
document.addEventListener('DOMContentLoaded', loadFiltersFromURL);
```
