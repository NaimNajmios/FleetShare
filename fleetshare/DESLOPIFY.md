# Design Refactor Guide — Deslopify Client-Side UI

## Context

The client-side pages are fully functional but look AI-generated / generic compared to the admin dashboard (which was built from a real template). The goal is to **refactor the visual design only** — preserving all HTML structure, Thymeleaf fragments, Bootstrap classes for layout/breakpoints, and every JS behavior.

**Stack:** Spring Boot + Thymeleaf (layout dialect) · Bootstrap 5 · Vanilla JS

---

## The Golden Rule

> Touch **only** what makes it look bad. Break **nothing** that makes it work.

---

## What Makes It Look "AI Slop" — Diagnose First

Before touching a single line, audit the page for these red flags:

- Generic sans-serif fonts (`Inter`, `Roboto`, `Arial`, `system-ui`) with no personality
- Flat blue/purple gradient hero sections with centered white text
- Every card identical height, same shadow, same radius (`border-radius: 8px` everywhere)
- Evenly spaced, symmetrical layouts — nothing breaks the grid
- `text-muted` gray on white for everything secondary
- Stock icon sets used as-is with no sizing intentionality
- Buttons that look exactly like Bootstrap defaults with just a color change
- Section backgrounds alternating white / light-gray with no depth or texture
- No typographic hierarchy beyond bold vs normal weight

---

## Rules for Safe Refactoring

### 1. Preserve Thymeleaf Fragments and Layout Structure
- Do **not** rename, move, or remove any `th:fragment`, `th:replace`, `th:insert`, or `layout:fragment` attributes
- Do **not** restructure the DOM hierarchy of elements that have Thymeleaf bindings (`th:each`, `th:if`, `th:text`, `th:href`, etc.)
- Keep `th:object` and `th:field` wrappers on forms intact

### 2. Preserve Bootstrap Layout Classes
Keep these classes untouched unless you're **adding** to them:
- Grid: `container`, `row`, `col-*`, `col-md-*`, `col-lg-*`
- Display utilities: `d-none`, `d-flex`, `d-md-block`, etc.
- Order utilities: `order-*`
- Offset utilities: `offset-*`

You **can** add custom CSS classes alongside Bootstrap classes. Never strip Bootstrap layout classes to replace with raw CSS — breakpoints must stay intact.

### 3. Preserve JS Hooks
- Do **not** remove or rename `id` attributes — JS may target them
- Do **not** remove or rename `data-*` attributes — used by Bootstrap JS (modals, tooltips, dropdowns, collapse) and custom scripts
- Keep `name` and `id` on all form inputs (form submission + label associations)
- Do **not** change `type` attributes on buttons/inputs
- Check for `document.querySelector`, `getElementById`, `addEventListener` usage in any linked `.js` file before renaming classes used as JS selectors

---

## Refactor Strategy — What to Actually Change

### Typography
- Replace the body/heading font with something with character. Add via Google Fonts in `<head>` or the existing font import location.
- Use a **display font** for headings (`h1`–`h3`, hero text, section titles)
- Use a **distinct but readable** font for body text
- Establish a real type scale — vary `font-size`, `letter-spacing`, `line-height`, `font-weight` intentionally
- Avoid: Inter, Roboto, Open Sans, Lato, Nunito, Poppins (overused)
- Consider: Fraunces, Syne, DM Serif Display, Playfair Display, Bricolage Grotesque, Instrument Serif + Instrument Sans, Plus Jakarta Sans paired with something editorial

### Color
- Define a palette using CSS custom properties in a `<style>` block or existing stylesheet:
  ```css
  :root {
    --color-primary: ...;
    --color-accent: ...;
    --color-surface: ...;
    --color-text: ...;
    --color-text-muted: ...;
  }
  ```
- Derive the palette from the **admin dashboard** colors for cohesion
- Use Bootstrap's `--bs-*` variable overrides where possible to keep consistency without fighting the framework
- Avoid: purple-on-white gradients, teal-cyan combos, generic blue CTAs

### Component Polish (Bootstrap-Safe)
Override Bootstrap components visually via custom CSS — do not change the Bootstrap class names themselves.

**Cards:**
```css
.card {
  border: 1px solid var(--color-border);
  border-radius: 2px; /* sharper or more distinct than default */
  box-shadow: none; /* or a more intentional shadow */
}
```

**Buttons:**
```css
.btn-primary {
  background: var(--color-primary);
  border: none;
  border-radius: 2px;
  letter-spacing: 0.05em;
  font-weight: 600;
  text-transform: uppercase;
  font-size: 0.8rem;
}
```

**Navbars / Headers:**
- Add `border-bottom` instead of `box-shadow` for crispness
- Use the accent color for active states rather than Bootstrap's default blue

### Spacing & Rhythm
- Use Bootstrap's spacing utilities (`py-5`, `mt-4`, etc.) — add/change these freely
- Add section padding that creates breathing room
- Break the monotony: not every section needs the same vertical padding

### Visual Texture / Depth
Add depth **without changing structure**:
```css
/* Subtle background texture on a section */
.hero-section {
  background-color: var(--color-surface);
  background-image: radial-gradient(circle at 20% 50%, var(--color-primary-light) 0%, transparent 60%);
}
```
- Thin `border-top` or `border-left` accents on cards
- `::before` / `::after` decorative pseudo-elements that don't affect layout
- Asymmetric section dividers using `clip-path` (be careful with overflow)

---

## Workflow Per Page / Template

1. **Read the template** — identify all `th:*` attributes, `data-*` attributes, and element `id`s. Note them before touching anything.
2. **Audit linked JS files** — grep for class selectors being used as JS hooks (`.myClass`, `#myId`).
3. **Add a scoped `<style>` block** at the bottom of the template (above `</body>` or in the appropriate Thymeleaf block) for page-specific overrides. Keep global changes in the shared stylesheet.
4. **Change one component at a time** — card, then navbar, then buttons, then typography.
5. **Test JS interactions after each component** — modals, dropdowns, form validation, AJAX calls should all still work.
6. **Compare against admin dashboard** — use browser split-screen. Font, color, and spacing language should feel related.

---

## Anti-Patterns to Avoid During Refactor

| Don't | Do Instead |
|---|---|
| Remove `data-bs-toggle` / `data-bs-target` | Leave them; style the triggering element with CSS |
| Replace `<form th:action th:object>` structure | Keep form wrapper; restyle inputs and layout inside |
| Remove Bootstrap grid classes to use flexbox | Add custom flex behavior with extra wrapper divs or CSS |
| Use `!important` everywhere | Increase CSS specificity properly |
| Apply one giant global font override | Target `body`, headings, and special elements separately |
| Change `<button type="submit">` to `<a>` | Keep the element type; restyle with CSS |

---

## Quick Checklist Before Committing a Refactor

- [ ] All Thymeleaf expressions still render (no broken `th:` attributes)
- [ ] Forms still submit correctly
- [ ] Bootstrap modals, tooltips, dropdowns still open/close
- [ ] Custom JS (event listeners, DOM queries) still finds its targets
- [ ] Page is responsive — check mobile breakpoints
- [ ] Font loads (check Network tab — no 404 on font files)
- [ ] Color contrast is accessible (use browser DevTools accessibility checker)
- [ ] Visual style feels consistent with admin dashboard
