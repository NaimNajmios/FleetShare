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
