## 2026-01-10 - Vehicle Price History Query Optimization

**Context:** VehicleManagementService.getAllVehicles -> VehiclePriceHistoryRepository.findLatestPricesForVehicles
**Symptoms:** Correlated subquery in IN clause causing potential O(N*M) complexity on large datasets.
**Root Cause:** The query used a correlated subquery pattern: effectiveStartDate = (SELECT MAX(...) WHERE outer.id = inner.id).
**Solution:** Refactored to use a tuple-based GROUP BY subquery: (id, date) IN (SELECT id, MAX(date) GROUP BY id).
**Impact:**
- Latency (H2 Test): ~347ms -> ~355ms (Similar on small in-memory dataset, but expected O(N) vs O(N*M) gain on production MySQL).
- Scalability: Prevents performance degradation as price history grows.

**Learnings:** Tuple syntax with GROUP BY is effective for 'greatest-n-per-group' problems in JPA.
## 2026-01-10 - Vehicle Price History Query Optimization

**Context:** VehicleManagementService.getAllVehicles -> VehiclePriceHistoryRepository.findLatestPricesForVehicles
**Symptoms:** Correlated subquery in IN clause causing potential O(N*M) complexity on large datasets.
**Root Cause:** The query used a correlated subquery pattern: effectiveStartDate = (SELECT MAX(...) WHERE outer.id = inner.id).
**Solution:** Refactored to use a tuple-based GROUP BY subquery: (id, date) IN (SELECT id, MAX(date) GROUP BY id).
**Impact:**
- Latency (H2 Test): ~347ms -> ~355ms (Similar on small in-memory dataset, but expected O(N) vs O(N*M) gain on production MySQL).
- Scalability: Prevents performance degradation as price history grows.

**Learnings:** Tuple syntax with GROUP BY is effective for 'greatest-n-per-group' problems in JPA.
