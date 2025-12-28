# Velocity Performance Log 🚀

## 2024-05-21 - N+1 Query Problem in Vehicle Listing

**Context:** `VehicleManagementService.getAllVehicles()` and `getVehiclesByOwnerId()`
**Symptoms:**
- The method iterates through every vehicle and performs 3 additional database queries per vehicle (Price, Owner, Address).
- This results in `1 + 3N` queries (N = number of vehicles).
- Identified via code analysis of `VehicleManagementService.java` and verified with profiling test.
- Baseline (N=50): 151 queries, ~257ms (test environment).

**Root Cause:**
- Eager fetching of related data (Price, Owner, Address) inside a loop (`for (Vehicle vehicle : vehicles)`) instead of bulk fetching or using JOINs.
- Entities are decoupled (using IDs) preventing automatic JPA batch fetching, requiring manual bulk fetching or DTO projection.

**Solution:**
- Implement Bulk Fetching in the Service layer.
- Fetch all Vehicles first.
- Collect IDs and fetch all related Owners, Prices (latest), and Addresses (latest) in 3 batched queries using `IN` clause.
- Reassemble data in memory using Maps.

**Impact:**
- Query count reduction: `1 + 3N` -> `4` constant queries (independent of N).
- Latency (N=50): ~186ms (test environment). 30% reduction in simple test, likely much higher in real distributed env due to reduced network round trips.

**Learnings:**
- Decoupled entities (ID references) require careful handling to avoid N+1 problems since standard JPA `@EntityGraph` or `JOIN FETCH` cannot be easily used without relationship mapping.
- Bulk fetching with `IN` clauses and in-memory mapping is a robust alternative that avoids architectural changes.
