## 2026-01-08 - BookingService Optimization
**Context:** Fetching all bookings or bookings by renter involves multiple bulk fetches (Renters, Vehicles, Owners, Users, StatusLogs, Invoices, Payments).
**Symptoms:** While N+1 is avoided, the overhead of 7-8 separate queries + in-memory mapping is significant for list views. Also, fetching *all* status logs for bookings just to find the latest is inefficient.
**Root Cause:** Manual stitching of DTOs instead of using JPQL projections or optimized fetching.
**Planned Solution:** Implement a JPQL query to fetch the latest status timestamp/value directly for a list of bookings, reducing the data transfer overhead.
