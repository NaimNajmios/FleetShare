## 2026-01-24 - [Correlated Subquery to Window Function Optimization]

**Context:** The `VehiclePriceHistoryRepository.findLatestPricesForVehicles` method is critical for the "Browse Vehicles" page, which fetches the latest effective price for a list of vehicles.
**Symptoms:** The original JPQL query utilized a correlated subquery (`WHERE effectiveStartDate = (SELECT MAX(...) WHERE vehicleId = outer.vehicleId)`). This causes the database to execute the inner query for every candidate row, leading to O(N*M) complexity behavior in worst cases, effectively an N+1 query pattern at the database level.
**Root Cause:** The use of correlated subqueries for "greatest-n-per-group" problems prevents the database from efficiently scanning the index once.
**Solution:** Replaced the JPQL query with a Native SQL Query utilizing the `ROW_NUMBER()` window function.
```sql
SELECT price_id, vehicle_id, rate_per_day, effective_start_date FROM (
    SELECT *, ROW_NUMBER() OVER (PARTITION BY vehicle_id ORDER BY effective_start_date DESC, price_id DESC) as rn
    FROM vehiclepricehistory
    WHERE vehicle_id IN :vehicleIds AND effective_start_date <= CURRENT_TIMESTAMP
) t WHERE rn = 1
```
**Impact:**
- Latency: Reduced query complexity from O(N*logM) (with index) to a single pass O(N) using the window function.
- Throughput: Significantly improves database throughput under high concurrency.
- Memory: Java-side memory usage remains constant, but DB CPU usage decreases.

**Learnings:**
- For "latest record per group" queries, always prefer Window Functions (`ROW_NUMBER()`) over correlated subqueries or `IN` tuple matching.
- Native queries must explicitly list entity columns to ensure safe Hibernate mapping and avoid partial object hydration risks or "extra column" errors (e.g., the `rn` column).
- Always include a tie-breaker in `ORDER BY` (e.g., `price_id DESC`) to ensure deterministic results.
