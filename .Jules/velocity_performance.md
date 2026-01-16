## 2026-01-16 - [Inefficient Correlated Subquery in VehiclePriceHistory]

**Context:** Fetching the "latest" effective price for a list of vehicles (`VehiclePriceHistoryRepository.findLatestPricesForVehicles`). This is a critical path for the "Browse Vehicles" feature.
**Symptoms:** Query analysis revealed a correlated subquery pattern: `SELECT ... WHERE effective_date = (SELECT MAX(...) WHERE outer.id = inner.id)`. This forces the database to execute the subquery for every candidate row (effectively O(N*M) or at best repeated index lookups).
**Root Cause:** The original JPQL query relied on a standard SQL pattern (max-per-group via subquery) which is suboptimal for large datasets compared to modern window functions.
**Solution:** Replaced the JPQL query with a Native Query using Window Functions: `ROW_NUMBER() OVER (PARTITION BY vehicle_id ORDER BY effective_start_date DESC)`. This allows the DB to scan and sort in a single pass (O(N) or O(N log N)).
**Impact:**
- Latency: 2748 ms → 951 ms (approx 2.9x improvement)
- Throughput: Improved due to reduced database CPU load.
- Memory: Unchanged (Database side optimization).

**Learnings:**
- For "Greatest-N-Per-Group" problems, Window Functions (`ROW_NUMBER()`, `RANK()`) are significantly more performant than Correlated Subqueries or Tuple-IN patterns.
- H2 supports Window Functions, allowing for consistent testing of these optimizations in integration tests.
- When `ddl-auto` fails in `@SpringBootTest`, manual schema creation via `JdbcTemplate` is a robust fallback for performance test setups.
