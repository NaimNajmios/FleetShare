## 2024-05-23 - Renter Dashboard Performance Optimization

**Context:** `RenterController.home` endpoint (Renter Dashboard)
**Symptoms:** Inefficient data fetching. The controller was fetching *all* bookings for a renter (including all related entities like Vehicle, Owner, Payments, etc.) just to calculate simple status counts and display the top 3 recent bookings.
**Root Cause:**
1. Fetching full entity graph for all historical bookings.
2. In-memory filtering and counting using Java Streams.
3. In-memory sorting and limiting for "Recent Bookings".

**Solution:**
1. Implemented `BookingCountDTO` to hold summary stats.
2. Added `countBookingsByStatusForRenter` native query in `BookingStatusLogRepository` to calculate counts at the database level using a single aggregate query.
3. Added `getRecentBookingsByRenterId` in `BookingService` using Spring Data's `Pageable` to fetch only the top 3 most recent bookings.
4. Refactored `RenterController` to use these optimized methods.

**Impact:**
- **Latency:** Significantly reduced for users with many bookings. O(N) database fetch + O(N) memory processing -> O(1) database aggregate + O(1) fetch.
- **Memory:** Reduced memory pressure by avoiding loading thousands of booking entities and their associations into the Heap.
- **Database:** Reduced I/O by fetching only necessary data (counts and top 3 rows).

**Learnings:**
- Always prefer database-level aggregation (COUNT, GROUP BY) over fetching all rows and counting in memory.
- Use `Pageable` for "Top N" queries to limit result set size at the source.
