# Velocity Performance Log

This log documents performance patterns, issues, and optimizations for the Fleetshare application.

## 2025-05-18 - [N+1 Query Issue in Booking Status Logs]

**Context:** `BookingService.getBookingStatusLogsDTO(Long bookingId)`
**Symptoms:** Iteration over booking logs triggered a database query for each log to fetch the actor (user).
**Root Cause:** The `User` entity was being fetched inside a loop: `userRepository.findById(log.getActorUserId())`.
**Solution:** Refactored to collect all user IDs first, fetch them in a single `findAllById` query, and map them in memory.
**Impact:**
- Latency (Mock Test): 139ms → 9ms for 1000 logs.
- Queries: 1001 queries (1 for logs + 1000 for users) → 2 queries (1 for logs + 1 for users).

**Learnings:** Always avoid fetching entities inside a loop. Use `findAllById` with `IN` clause for batch fetching.
