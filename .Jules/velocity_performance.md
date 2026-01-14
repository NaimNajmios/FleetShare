## 2026-01-06 - Optimized Booking Details Query

**Context:** `BookingService.getBookingDetails(Long)` endpoint used for viewing single booking details.
**Symptoms:** High latency due to sequential database queries (1+N problem). Profiling indicated 8 separate queries were executed to fetch a single booking's details (Booking, Renter, User, Vehicle, FleetOwner, Status, Invoice, Payment).
**Root Cause:** The application architecture uses decoupled entities (linked by ID rather than JPA `@OneToMany`/`@ManyToOne` relationships). This prevented automatic lazy/eager loading and forced the service layer to manually fetch each related entity sequentially.
**Solution:** Implemented a JPQL Constructor Expression query in `BookingRepository` that uses `LEFT JOIN ... ON` syntax to join 5 core tables (Booking, Renter, User, Vehicle, FleetOwner) in a single database round-trip. Status, Invoice, and Payment are still fetched separately to avoid complex Cartesian products with one-to-many relationships.
**Impact:**
- Database Queries: Reduced from ~8 queries to ~3 queries (62% reduction).
- Latency: Reduced network round-trips significantly.

**Learnings:** When working with decoupled entities (ID-based references), standard JPA `JOIN FETCH` cannot be used. Instead, use explicit JPQL `LEFT JOIN ... ON` clauses combined with a DTO Constructor Expression (`SELECT new DTO(...)`) to achieve efficient data aggregation in a single query.

## 2026-01-13 - Optimized Vehicle Browsing Query

**Context:** `RenterController.browseVehicles` endpoint (`/renter/vehicles`) used for listing available vehicles.
**Symptoms:** Potential high latency and memory usage identified by static analysis. The code was fetching *all* vehicles (including rented, maintenance, and deleted ones) and then filtering for "AVAILABLE" status in memory using Java Streams.
**Root Cause:** "Fetch-all-and-filter" anti-pattern. `VehicleManagementService.getAllVehicles()` retrieved the entire dataset, which was then filtered in the controller.
**Solution:**
1. Added `findByStatusAndIsDeletedFalse` to `VehicleRepository` to filter at the database level.
2. Added `getAvailableVehicles` to `VehicleManagementService` to utilize the new repository method.
3. Updated `RenterController` to call the optimized service method.
**Impact:**
- Database Load: Reduced significantly. Only "AVAILABLE" vehicles are fetched.
- Memory Usage: Reduced as non-relevant vehicles are never loaded into memory.
- Network I/O: Reduced payload size from DB to App.

**Learnings:** Always prefer database-level filtering (WHERE clauses) over application-level filtering (Java Streams) for potentially large datasets like Vehicles or Bookings.
