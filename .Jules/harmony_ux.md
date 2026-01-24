## 2024-05-22 - [Deep Linking for Filter State]

**Context:** Renter Vehicle Browser (`browse-vehicles.html`)
**User Need:** Users need to share search results or return to their filtered view after navigating to a vehicle detail page and clicking "Back".
**Observation:** Filters and sort order were reset on page reload or navigation, causing frustration and loss of context.
**Solution Implemented:** Implemented client-side URL synchronization using `history.replaceState` and `URLSearchParams`.
- `updateURL()`: Serializes current filter state (categories, transmission, price, search, sort) to URL query parameters.
- `restoreStateFromURL()`: Parses query parameters on load/popstate and updates UI elements accordingly.
- `isRestoringState`: Flag to prevent redundant URL updates during initialization.
**Impact:** Users can now bookmark specific filter combinations and use browser navigation without losing their search context.
**Measurement:** Verified via Playwright automation covering reload persistence and back-button simulation.

**Code Pattern:**
```javascript
let isRestoringState = false;

function updateURL() {
    if (isRestoringState) return;
    const params = new URLSearchParams();
    // ... append params ...
    history.replaceState(null, '', window.location.pathname + '?' + params.toString());
}

function restoreStateFromURL() {
    isRestoringState = true;
    // ... restore inputs from params ...
    applyFilters();
    isRestoringState = false;
}
```
