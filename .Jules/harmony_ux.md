## 2026-01-06 - Client-Side Sorting & Animation

**Context:** Renter Browse Vehicles Page (`browse-vehicles.html`)
**User Need:** Users want to sort vehicles by price or newness to find the best deal or latest model quickly, but the previous interface only supported filtering (hide/show), not reordering.
**Observation:** The vehicle list was static (server order). While filters were instant (JS), finding the "cheapest" required scrolling and manually comparing.
**Solution Implemented:**
1.  Added a "Sort" dropdown with options: Recommended, Price (Low-High/High-Low), Newest.
2.  Implemented JS logic to reorder DOM elements based on `data-*` attributes (`data-price`, `data-year`).
3.  Added a `data-original-index` on load to allow resetting to server order.
4.  Added a CSS animation (`fadeIn`) that triggers when items are re-appended/shown to smooth the transition.

**Impact:** Immediate, responsive sorting without server round-trips for the current page set. The animation provides delightful feedback that the list has updated.

**Code Pattern:**
```javascript
// Sorting Logic
cards.sort((a, b) => {
    if (currentSort === 'price-asc') {
        return parseFloat(a.dataset.price) - parseFloat(b.dataset.price);
    }
    // ...
});

// Re-append to DOM
cards.forEach(card => {
    container.appendChild(card);
    card.classList.add('animate-fade-in'); // Trigger animation
});
```

```css
.animate-fade-in {
    animation: fadeIn 0.4s ease-out forwards;
}
@keyframes fadeIn {
    from { opacity: 0; transform: translateY(10px); }
    to { opacity: 1; transform: translateY(0); }
}
```
