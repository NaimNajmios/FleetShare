## 2024-05-23 - Vehicle Price History Fetching Optimization

**Context:** `VehicleManagementService.mapVehiclesToDTOs`
**Symptoms:** Potential performance bottleneck and memory overhead when fetching vehicle lists. The service was fetching *all* price history records for every vehicle in the list and filtering them in memory.
**Root Cause:** Inefficient data fetching strategy (N+1-like issue for history data). The application was loading the entire history of price changes just to find the current effective price.
**Solution:**
1. Implemented `findLatestPricesForVehicles` in `VehiclePriceHistoryRepository` using a correlated subquery to fetch only the single latest effective price record per vehicle.
2. Added a composite index `idx_vehicle_price_history_vehicle_date` on `(vehicle_id, effective_start_date)` in `VehiclePriceHistory` entity to optimize the subquery execution.

**Impact:**
- **Scalability:** Reduces the number of rows fetched from `Total History Records` to `Total Vehicles`. If a vehicle has 100 price changes, we now fetch 1 row instead of 100.
- **Latency:** Initial benchmarks showed high latency for the subquery without an index (~37s for 10k records in H2). After adding the index, latency dropped to ~650ms.
- **Memory:** Significant reduction in heap usage as we no longer materialize thousands of history objects only to discard them.

**Learnings:**
- Correlated subqueries can be very slow without proper indexing.
- For "latest record per group" problems, a subquery with an index is often more scalable than fetching all data, even if simple "fetch all" approaches seem fast on small datasets in-memory.

## 2026-01-02 - UserManagementService Optimization

**Context:** `UserManagementService.getCustomersByOwnerId(Long ownerId)` endpoint used to fetch all bookings for a fleet owner to identify their unique customers.
**Symptoms:** High memory usage and potential performance degradation when a fleet owner has a large number of bookings (e.g., thousands of bookings) but only a few unique customers. The application was fetching all full Booking entities into memory.
**Root Cause:** The method was performing an inefficient "fetch all and filter in memory" operation:
1. `bookingRepository.findByFleetOwnerId(ownerId)` -> Loaded ALL booking entities.
2. `bookings.stream().map(Booking::getRenterId).collect(Collectors.toSet())` -> Extracted IDs in memory.

**Solution:** Implemented a new JPQL query in `BookingRepository` to fetch only the distinct Renter IDs directly from the database.
```java
@Query("SELECT DISTINCT b.renterId FROM Booking b WHERE b.fleetOwnerId = :fleetOwnerId")
List<Long> findDistinctRenterIdsByFleetOwnerId(@Param("fleetOwnerId") Long fleetOwnerId);
```
Updated `UserManagementService` to use this efficient query.

**Impact:**
- **Database Query:** Replaced `SELECT * FROM bookings WHERE fleet_owner_id = ?` with `SELECT DISTINCT renter_id FROM bookings WHERE fleet_owner_id = ?`.
- **Memory Usage:** Drastically reduced. Instead of creating thousands of Booking entity objects, we now only load a small list of Long IDs.
- **Scalability:** The operation complexity now depends on the number of *unique customers* rather than the *total number of bookings*.

**Learnings:** Always prefer database-level aggregation and distinct filtering over application-level filtering for potentially large datasets.
