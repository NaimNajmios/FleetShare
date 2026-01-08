## 2026-01-06 - Optimized Booking Details Query

**Context:** `BookingService.getBookingDetails(Long)` endpoint used for viewing single booking details.
**Symptoms:** High latency due to sequential database queries (1+N problem). Profiling indicated 8 separate queries were executed to fetch a single booking's details (Booking, Renter, User, Vehicle, FleetOwner, Status, Invoice, Payment).
**Root Cause:** The application architecture uses decoupled entities (linked by ID rather than JPA `@OneToMany`/`@ManyToOne` relationships). This prevented automatic lazy/eager loading and forced the service layer to manually fetch each related entity sequentially.
**Solution:** Implemented a JPQL Constructor Expression query in `BookingRepository` that uses `LEFT JOIN ... ON` syntax to join 5 core tables (Booking, Renter, User, Vehicle, FleetOwner) in a single database round-trip. Status, Invoice, and Payment are still fetched separately to avoid complex Cartesian products with one-to-many relationships.
**Impact:**
- Database Queries: Reduced from ~8 queries to ~3 queries (62% reduction).
- Latency: Reduced network round-trips significantly.

**Learnings:** When working with decoupled entities (ID-based references), standard JPA `JOIN FETCH` cannot be used. Instead, use explicit JPQL `LEFT JOIN ... ON` clauses combined with a DTO Constructor Expression (`SELECT new DTO(...)`) to achieve efficient data aggregation in a single query.
