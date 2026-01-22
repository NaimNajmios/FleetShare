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

## 2026-01-17 - Optimized Owner Dashboard Load

**Context:** `OwnerController.dashboard` endpoint used for displaying fleet owner statistics and recent bookings.
**Symptoms:** High memory usage and potential latency. The controller fetched *all* bookings and *all* payments for the owner, then filtered and aggregated them in-memory to calculate counts and revenue. It also fetched all bookings to display only the 5 most recent ones.
**Root Cause:** "Fetch-all-and-compute" anti-pattern. `BookingService.getBookingsByOwnerId` and `PaymentService.getPaymentsByOwnerId` were loading entire datasets into memory.
**Solution:**
1. Implemented JPQL aggregation queries (`COUNT`, `SUM`) in repositories to fetch statistics directly.
2. Implemented `Pageable` in `BookingRepository` to fetch only the top 5 recent bookings.
3. Updated `OwnerController` to use these optimized service methods.
**Impact:**
- Database Queries: Replaced heavy SELECT * queries with lightweight COUNT/SUM queries.
- Memory Usage: Reduced significantly by not loading thousands of Booking/Payment objects into heap.
- Network I/O: drastically reduced payload size from DB.

**Learnings:** Use database aggregation for statistics. Avoid fetching entire collections just to count them or show a subset. When entities are decoupled (no direct relationships), use JPQL `IN` subqueries or Cross Joins with careful WHERE clauses to perform aggregations.

## 2026-01-22 - Optimized Latest Vehicle Price Retrieval

**Context:** `VehicleManagementService.mapVehiclesToDTOs` used in vehicle browsing and listing.
**Symptoms:** Performance bottleneck identified in `VehiclePriceHistoryRepository.findLatestPricesForVehicles`. The query used a Correlated Subquery pattern (`WHERE date = (SELECT MAX(...) WHERE id = outer.id)`) which executes effectively in O(N^2) complexity on some database engines or prevents optimal index usage, leading to slow performance as the price history table grows.
**Root Cause:** The use of a Correlated Subquery to find the "latest" record per group (Vehicle) causes the database to re-evaluate the subquery for every candidate row.
**Solution:**
1. Implemented a Native SQL Query using Window Functions (`ROW_NUMBER() OVER (PARTITION BY ... ORDER BY ...)`).
2. Added `findLatestPricesForVehiclesOptimized` to `VehiclePriceHistoryRepository`.
3. Updated `VehicleManagementService` to use the optimized method.
**Impact:**
- Complexity: Reduced from potentially O(N^2) to O(N log N) (sorting for window function).
- Scalability: Handles large datasets of price history much more efficiently.
- Correctness: Preserved the logic of selecting the latest effective price <= current time.

**Learnings:** For "Greatest-N-Per-Group" problems (e.g., latest price per item, latest status per order), Window Functions (like `ROW_NUMBER()`) are generally far superior to Correlated Subqueries or `IN` tuple matching, especially in modern databases like MySQL 8.0+ and PostgreSQL.
