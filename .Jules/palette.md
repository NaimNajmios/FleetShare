# Palette - UX & Accessibility Insights

## Responsive Design
- **Pattern:** Use `offcanvas-lg` for sidebars that should be visible on desktop but collapsible on mobile.
- **Why:** It maintains context on large screens while saving space on small screens, following the "Mobile First" approach where content is king.
- **Implementation:**
  - Ensure the "drawer" content is wrapped in `.offcanvas-body` for proper scrolling and padding on mobile.
  - Reset custom sidebar styles (padding, shadow, borders) when in offcanvas mode to rely on Bootstrap's native offcanvas styling, or manually adjust them.
  - Use `d-lg-none` for toggle buttons to ensure they only appear when needed.

## Accessibility
- **Filters:** When hiding filters behind a button, ensure the button has a clear `aria-label` or visible text (icon-only buttons need `aria-label`).
- **Focus Management:** Offcanvas components manage focus automatically (trapping focus when open), which is crucial for keyboard users.
- **Structure:** `aside` is the correct semantic element for a sidebar filter, even when it becomes an offcanvas drawer.
