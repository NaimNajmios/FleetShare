# Velocity's Performance Log

## 2025-02-18 - [Optimization Init]

**Context:** Initial codebase exploration.
**Symptoms:** N/A
**Root Cause:** N/A
**Solution:** N/A
**Impact:** N/A
**Learnings:** N/A

## 2026-01-14 - [VehiclePriceHistory Fetch Optimization]

**Context:** `VehiclePriceHistoryRepository.findLatestPricesForVehicles` used a correlated subquery to fetch the effective price for a list of vehicles.
**Symptoms:** Potential N+1 like behavior (or N*M complexity) in database execution plan due to correlated subquery, especially on large datasets. Attempting Tuple IN pattern caused severe regression on H2 (28s execution).
**Root Cause:** Correlated subqueries execute the inner query for each candidate row. H2's optimizer failed to optimize the Tuple IN pattern efficiently.
**Solution:** Replaced with a Native Query using Window Function (`ROW_NUMBER() OVER (PARTITION BY ...)`).
**Impact:**
- Latency (H2 Test Benchmark): ~130ms → ~91ms (30% improvement).
- Scalability: Avoids correlated subquery loop, ensuring O(N log N) complexity instead of potentially O(N*M).
- H2 Compatibility: Fixed the severe regression (28s -> 0.09s).

**Learnings:**
- Window functions are a robust alternative to "Top-N-per-group" problems where Tuple IN optimization fails (e.g., on H2).
- Always benchmark optimizations on the target environment (or representative test env) as theoretical improvements (Tuple IN) might have specific regressions.
