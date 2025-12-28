# Velocity Performance Log

## 2025-12-27 - BookingService N+1 Optimization

**Context:** `BookingService` methods `getAllBookings`, `getBookingsByRenterId`, and `getBookingsByOwnerId`.
**Symptoms:** Code analysis revealed classic N+1 query patterns. For each booking fetched, separate queries were executed to retrieve Renter, User, Vehicle, FleetOwner, BookingStatusLog, Invoice, and Payment entities.
**Root Cause:** Iterating over a collection of `Booking` entities and calling repository `findById` methods within the loop.
**Solution:** Refactored `BookingService` to use a "bulk fetching" strategy.
1. Implemented `mapBookingsToDTOs` helper method.
2. Collected all related entity IDs from the booking list.
3. Added `findBy...In` methods to `InvoiceRepository`, `PaymentRepository`, and `BookingStatusLogRepository`.
4. Fetched all related entities in single batch queries using `IN` clauses.
5. Assembled DTOs in memory using Maps.

**Impact:**
- **Database Queries:** Drastically reduced.
    - Before: 1 query for bookings + (N bookings * 7 related queries). For 100 bookings, this would be ~701 queries.
    - After: 1 query for bookings + ~6 bulk queries (Renter, Vehicle, Owner, Status, Invoice, Payment). Total ~7 queries regardless of booking count (until `IN` clause limits are reached).
- **Latency:** Expected significant reduction in response time for listing bookings, especially as the dataset grows.
- **Consistency:** Unified the DTO mapping logic, ensuring consistent data (e.g., invoices/payments) is returned across all booking list endpoints.

**Learnings:**
- JPA's `findAllById` is efficient for bulk fetching.
- For non-ID lookups (like `findByBookingId`), custom `@Query` methods with `IN` clauses are necessary.
- Grouping by ID using Java Streams (`Collectors.toMap` or `Collectors.groupingBy`) allows for efficient in-memory join/assembly of DTOs.
