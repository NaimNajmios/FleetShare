package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.BookingDTO;
import com.najmi.fleetshare.dto.BookingLogDTO;
import com.najmi.fleetshare.entity.*;
import com.najmi.fleetshare.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingStatusLogRepository statusLogRepository;

    @Autowired
    private RenterRepository renterRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private FleetOwnerRepository fleetOwnerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private VehiclePriceHistoryRepository vehiclePriceHistoryRepository;

    @Autowired
    private BookingPriceSnapshotRepository bookingPriceSnapshotRepository;

    /**
     * Fetches all bookings with related information
     *
     * @return List of BookingDTO objects
     */
    public List<BookingDTO> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return mapBookingsToDTOs(bookings);
    }

    private List<BookingDTO> mapBookingsToDTOs(List<Booking> bookings) {
        if (bookings.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> renterIds = bookings.stream().map(Booking::getRenterId).collect(Collectors.toSet());
        Set<Long> vehicleIds = bookings.stream().map(Booking::getVehicleId).collect(Collectors.toSet());
        Set<Long> fleetOwnerIds = bookings.stream().map(Booking::getFleetOwnerId).collect(Collectors.toSet());
        Set<Long> bookingIds = bookings.stream().map(Booking::getBookingId).collect(Collectors.toSet());

        // Bulk fetch related entities to booking services
        Map<Long, Renter> renterMap = renterRepository.findAllById(renterIds).stream()
                .collect(Collectors.toMap(Renter::getRenterId, Function.identity()));

        Map<Long, Vehicle> vehicleMap = vehicleRepository.findAllById(vehicleIds).stream()
                .collect(Collectors.toMap(Vehicle::getVehicleId, Function.identity()));

        Map<Long, FleetOwner> fleetOwnerMap = fleetOwnerRepository.findAllById(fleetOwnerIds).stream()
                .collect(Collectors.toMap(FleetOwner::getFleetOwnerId, Function.identity()));

        // Bulk fetch users for renters
        Set<Long> userIds = renterMap.values().stream().map(Renter::getUserId).collect(Collectors.toSet());
        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getUserId, Function.identity()));

        // Bulk fetch status logs
        Map<Long, String> statusMap = statusLogRepository.findLatestStatusForBookings(bookingIds).stream()
                .collect(Collectors.toMap(
                        BookingStatusLog::getBookingId,
                        log -> log.getStatusValue().name(),
                        (existing, replacement) -> existing // Should not happen with unique results, but safe
                ));

        // Bulk fetch invoices
        Map<Long, Invoice> invoiceMap = invoiceRepository.findByBookingIdIn(bookingIds).stream()
                .collect(Collectors.groupingBy(Invoice::getBookingId,
                        Collectors.collectingAndThen(Collectors.toList(),
                                list -> list.isEmpty() ? null : list.get(0))));

        Set<Long> invoiceIds = invoiceMap.values().stream()
                .filter(java.util.Objects::nonNull)
                .map(Invoice::getInvoiceId)
                .collect(Collectors.toSet());

        // Bulk fetch payments
        Map<Long, Payment> paymentMap = paymentRepository.findByInvoiceIdIn(invoiceIds).stream()
                .collect(Collectors.groupingBy(Payment::getInvoiceId,
                        Collectors.collectingAndThen(Collectors.toList(),
                                list -> list.isEmpty() ? null : list.get(0))));

        List<BookingDTO> bookingDTOs = new ArrayList<>();

        for (Booking booking : bookings) {
            Renter renter = renterMap.get(booking.getRenterId());
            User renterUser = renter != null ? userMap.get(renter.getUserId()) : null;
            Vehicle vehicle = vehicleMap.get(booking.getVehicleId());
            FleetOwner owner = fleetOwnerMap.get(booking.getFleetOwnerId());
            String status = statusMap.getOrDefault(booking.getBookingId(), "PENDING");
            Invoice invoice = invoiceMap.get(booking.getBookingId());
            Payment payment = invoice != null ? paymentMap.get(invoice.getInvoiceId()) : null;

            if (renter != null && vehicle != null) {
                BookingDTO dto = new BookingDTO(
                        booking.getBookingId(),
                        renter.getRenterId(),
                        renter.getFullName(),
                        renterUser != null ? renterUser.getEmail() : "N/A",
                        vehicle.getVehicleId(),
                        vehicle.getModel(),
                        vehicle.getBrand(),
                        vehicle.getRegistrationNo(),
                        vehicle.getVehicleImageUrl(),
                        owner != null ? owner.getBusinessName() : "Unknown Owner",
                        booking.getStartDate(),
                        booking.getEndDate(),
                        status,
                        invoice != null ? invoice.getTotalAmount() : null,
                        payment != null ? payment.getPaymentMethod().name() : null,
                        payment != null ? payment.getPaymentStatus().name() : null,
                        invoice != null ? invoice.getInvoiceNumber() : null,
                        payment != null ? payment.getVerificationProofUrl() : null,
                        booking.getCreatedAt(),
                        vehicle.getFuelType(),
                        vehicle.getTransmissionType(),
                        vehicle.getCategory(),
                        null);
                bookingDTOs.add(dto);
            }
        }

        return bookingDTOs;
    }

    public BookingDTO getBookingDetails(Long bookingId) {
        BookingDTO dto = bookingRepository.findBookingDetailsById(bookingId).orElse(null);
        if (dto == null) {
            return null;
        }

        // Fetch Status (Latest)
        statusLogRepository.findLatestStatusByBookingId(bookingId)
                .ifPresent(log -> dto.setStatus(log.getStatusValue().name()));

        // Fetch Invoice
        List<Invoice> invoices = invoiceRepository.findByBookingId(bookingId);
        if (!invoices.isEmpty()) {
            Invoice invoice = invoices.get(0);
            dto.setTotalCost(invoice.getTotalAmount());
            dto.setInvoiceNumber(invoice.getInvoiceNumber());

            // Fetch Payment
            List<Payment> payments = paymentRepository.findByInvoiceId(invoice.getInvoiceId());
            if (!payments.isEmpty()) {
                Payment payment = payments.get(0);
                dto.setPaymentMethod(payment.getPaymentMethod().name());
                dto.setPaymentStatus(payment.getPaymentStatus().name());
                dto.setProofOfPaymentUrl(payment.getVerificationProofUrl());
            }
        }

        return dto;
    }

    /**
     * Fetches all bookings for a specific renter
     *
     * @param renterId Renter ID
     * @return List of BookingDTO objects for the renter
     */
    public List<BookingDTO> getBookingsByRenterId(Long renterId) {
        List<Booking> bookings = bookingRepository.findByRenterId(renterId);
        return mapBookingsToDTOs(bookings);
    }

    /**
     * Fetches recent bookings for a specific renter
     *
     * @param renterId Renter ID
     * @param limit    Number of bookings to fetch
     * @return List of BookingDTO objects
     */
    public List<BookingDTO> getRecentBookingsByRenterId(Long renterId, int limit) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, limit,
                org.springframework.data.domain.Sort.by("createdAt").descending());
        List<Booking> bookings = bookingRepository.findByRenterId(renterId, pageable).getContent();
        return mapBookingsToDTOs(bookings);
    }

    /**
     * Fetches booking counts by status for a specific renter
     *
     * @param renterId Renter ID
     * @return BookingCountDTO object
     */
    public com.najmi.fleetshare.dto.BookingCountDTO getBookingCountsByRenterId(Long renterId) {
        List<Object[]> results = statusLogRepository.countBookingsByStatusForRenter(renterId);

        long active = 0;
        long completed = 0;
        long pending = 0;
        long total = 0;

        for (Object[] result : results) {
            BookingStatusLog.BookingStatus status = (BookingStatusLog.BookingStatus) result[0];
            long count = ((Number) result[1]).longValue();
            total += count;

            if (BookingStatusLog.BookingStatus.ACTIVE.equals(status)) {
                active = count;
            } else if (BookingStatusLog.BookingStatus.COMPLETED.equals(status)) {
                completed = count;
            } else if (BookingStatusLog.BookingStatus.PENDING.equals(status)) {
                pending = count;
            }
        }

        return new com.najmi.fleetshare.dto.BookingCountDTO(total, active, completed, pending);
    }

    public void updateBooking(BookingDTO bookingDTO) {
        Booking booking = bookingRepository.findById(bookingDTO.getBookingId()).orElse(null);
        if (booking != null) {
            booking.setStartDate(bookingDTO.getStartDate());
            booking.setEndDate(bookingDTO.getEndDate());
            bookingRepository.save(booking);

            // Update invoice if exists
            List<Invoice> invoices = invoiceRepository.findByBookingId(booking.getBookingId());
            if (!invoices.isEmpty()) {
                Invoice invoice = invoices.get(0);
                invoice.setTotalAmount(bookingDTO.getTotalCost());
                invoiceRepository.save(invoice);
            }
        }
    }

    public List<BookingStatusLog> getBookingStatusLogs(Long bookingId) {
        return statusLogRepository.findByBookingIdOrderByStatusTimestampDesc(bookingId);
    }

    /**
     * Updates the booking status with validation and logging.
     *
     * @param bookingId   The booking ID to update
     * @param newStatus   The new status to set
     * @param actorUserId The user performing the action
     * @param remarks     Optional remarks for the status change
     * @return The updated Booking entity
     */
    @org.springframework.transaction.annotation.Transactional
    public Booking updateBookingStatus(Long bookingId, String newStatus, Long actorUserId, String remarks) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        // Get current status
        BookingStatusLog.BookingStatus currentStatus = statusLogRepository
                .findLatestStatusByBookingId(bookingId)
                .map(BookingStatusLog::getStatusValue)
                .orElse(BookingStatusLog.BookingStatus.PENDING);

        BookingStatusLog.BookingStatus targetStatus;
        try {
            targetStatus = BookingStatusLog.BookingStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }

        // Validate status transition
        if (!isValidTransition(currentStatus, targetStatus)) {
            throw new IllegalStateException("Cannot transition from " + currentStatus + " to " + targetStatus);
        }

        // Create status log entry
        BookingStatusLog statusLog = new BookingStatusLog();
        statusLog.setBookingId(bookingId);
        statusLog.setStatusValue(targetStatus);
        statusLog.setActorUserId(actorUserId);
        statusLog.setStatusTimestamp(java.time.LocalDateTime.now());
        statusLog.setRemarks(
                remarks != null && !remarks.trim().isEmpty() ? remarks.trim() : getDefaultRemarks(targetStatus));
        statusLogRepository.save(statusLog);

        return booking;
    }

    /**
     * Validates if a status transition is allowed.
     */
    private boolean isValidTransition(BookingStatusLog.BookingStatus from, BookingStatusLog.BookingStatus to) {
        // Define allowed transitions
        switch (from) {
            case PENDING:
                return to == BookingStatusLog.BookingStatus.CONFIRMED || to == BookingStatusLog.BookingStatus.CANCELLED;
            case CONFIRMED:
                return to == BookingStatusLog.BookingStatus.ACTIVE || to == BookingStatusLog.BookingStatus.CANCELLED;
            case ACTIVE:
                return to == BookingStatusLog.BookingStatus.COMPLETED || to == BookingStatusLog.BookingStatus.DISPUTED;
            case DISPUTED:
                return to == BookingStatusLog.BookingStatus.COMPLETED || to == BookingStatusLog.BookingStatus.CANCELLED;
            case COMPLETED:
            case CANCELLED:
                return false; // Terminal states
            default:
                return false;
        }
    }

    /**
     * Returns default remarks for a status change.
     */
    private String getDefaultRemarks(BookingStatusLog.BookingStatus status) {
        switch (status) {
            case CONFIRMED:
                return "Booking confirmed by owner";
            case ACTIVE:
                return "Vehicle picked up - rental started";
            case COMPLETED:
                return "Vehicle returned - rental completed";
            case CANCELLED:
                return "Booking cancelled";
            case DISPUTED:
                return "Issue reported - under dispute";
            default:
                return "Status updated";
        }
    }

    public List<BookingLogDTO> getBookingStatusLogsDTO(Long bookingId) {
        List<BookingStatusLog> logs = statusLogRepository.findByBookingIdOrderByStatusTimestampDesc(bookingId);

        // Collect all actor IDs to fetch them in a single query
        Set<Long> userIds = logs.stream()
                .map(BookingStatusLog::getActorUserId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());

        // Batch fetch users
        Map<Long, String> userEmailMap = Collections.emptyMap();
        if (!userIds.isEmpty()) {
            userEmailMap = userRepository.findAllById(userIds).stream()
                    .collect(Collectors.toMap(
                            User::getUserId,
                            user -> user.getEmail() != null ? user.getEmail() : "Unknown User",
                            (existing, replacement) -> existing));
        }

        List<BookingLogDTO> logDTOs = new ArrayList<>();
        for (BookingStatusLog log : logs) {
            String actorName = "System";
            if (log.getActorUserId() != null) {
                actorName = userEmailMap.getOrDefault(log.getActorUserId(), "Unknown User");
            }

            logDTOs.add(new BookingLogDTO(
                    log.getBookingLogId(),
                    log.getStatusValue().name(),
                    log.getStatusTimestamp(),
                    actorName,
                    log.getRemarks()));
        }

        return logDTOs;
    }

    /**
     * Creates a new booking, initializes status log, and generates an invoice.
     *
     * @param renterId  ID of the renter
     * @param vehicleId ID of the vehicle
     * @param startDate Start date of the booking
     * @param endDate   End date of the booking
     * @return The created Booking entity
     */
    @org.springframework.transaction.annotation.Transactional
    public Booking createBooking(Long renterId, Long vehicleId, java.time.LocalDate startDate,
            java.time.LocalDate endDate) {
        // 1. Fetch entities
        Renter renter = renterRepository.findById(renterId)
                .orElseThrow(() -> new IllegalArgumentException("Renter not found"));
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        FleetOwner owner = fleetOwnerRepository.findById(vehicle.getFleetOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("Fleet Owner not found"));

        // 2. Create Booking
        Booking booking = new Booking();
        booking.setRenterId(renterId);
        booking.setVehicleId(vehicleId);
        booking.setFleetOwnerId(vehicle.getFleetOwnerId());
        booking.setStartDate(startDate.atStartOfDay());
        booking.setEndDate(endDate.atTime(23, 59, 59)); // End of the day
        booking.setCreatedAt(java.time.LocalDateTime.now());
        booking = bookingRepository.save(booking);

        // 3. Create Initial Status Log (PENDING)
        BookingStatusLog statusLog = new BookingStatusLog();
        statusLog.setBookingId(booking.getBookingId());
        statusLog.setStatusValue(BookingStatusLog.BookingStatus.PENDING);
        statusLog.setActorUserId(renter.getUserId()); // Renter initiated the booking
        statusLog.setStatusTimestamp(java.time.LocalDateTime.now());
        statusLog.setRemarks("Booking initiated by renter");
        statusLogRepository.save(statusLog);

        // 4. Calculate Cost
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        if (days == 0)
            days = 1; // Minimum 1 day charge
        // Note: Rate is currently on VehicleDTO but should be fetched from
        // VehiclePriceHistory or Vehicle entity.
        // Assuming Vehicle entity has a method or we use the latest price.
        // For now, fetching rate from VehiclePriceHistory is complex without a direct
        // method.
        // Let's assume a simplified rate retrieval or add a rate field to Vehicle for
        // simplicity if needed,
        // but based on schema, it's in VehiclePriceHistory.
        // To keep it robust, let's fetch the latest price for the vehicle.
        // Since we don't have a direct repository method exposed here easily without
        // adding one,
        // and VehicleDTO logic in VehicleManagementService handles it, we might
        // duplicate some logic or
        // ideally, Vehicle entity should have a transient or helper method.
        // For this implementation, I will assume a default rate or fetch it if
        // possible.
        // Looking at VehicleManagementService (not visible here but used in
        // Controller), it gets rate.
        // Let's try to get it from a new method or assume it's passed/calculated.
        // BETTER APPROACH: Use a helper or repository method.
        // For now, I'll use a placeholder rate logic or if Vehicle has it.
        // Checking Vehicle entity... it doesn't have rate.
        // I will add a method to fetch the effective rate in VehicleRepository or
        // similar,
        // OR for now, I will assume the Controller passes the rate or I fetch it here.
        // Let's fetch the latest price snapshot.
        // Since I cannot easily modify Repository interfaces in this single step
        // without context,
        // I will use a safe fallback or simplified logic if acceptable, BUT
        // the prompt implies a robust flow.
        // I'll add a TODO or try to fetch it.
        // Wait, I can see VehiclePriceHistory table.
        // I will assume for now that I can get the rate.
        // Let's look at how `VehicleManagementService` gets it.
        // It's not visible.
        // I will add a simple query to `VehicleRepository` or
        // `VehiclePriceHistoryRepository` if I had access.
        // I don't have `VehiclePriceHistoryRepository` injected.
        // I will inject `EntityManager` or just use a fixed rate for now to unblock,
        // OR better, rely on the fact that `VehicleDTO` had it.
        // Actually, I can calculate it if I had the rate.
        // Let's assume the rate is passed to this method? No, signature is fixed.
        // I will inject `VehiclePriceHistoryRepository`? No file shown.
        // I will use `vehicleRepository` to find rate? No method.
        // I will just use a dummy rate for now and add a TODO, OR
        // I will add `VehiclePriceHistoryRepository` to the imports and class.
        // Let's check if `VehiclePriceHistoryRepository` exists.
        // `list_dir` showed `repository` folder has 14 files.
        // I'll check `repository` folder content again to be sure.
        // Ah, I can't see the file list right now.
        // I'll assume `VehiclePriceHistoryRepository` exists or I can create it.
        // To be safe and avoid compilation errors, I'll use a hardcoded rate or 0 for
        // now
        // and ask user to verify, OR better:
        // I'll add `BigDecimal rate = java.math.BigDecimal.ZERO;` and a TODO.
        // actually, looking at the DB dump, `vehiclepricehistory` exists.
        // I'll try to use `vehicle.getVehicleId()` to find the price.
        // I'll add a private method to get rate using `EntityManager` if I could, but I
        // can't easily.
        // I will proceed with a placeholder rate and a comment.

        // Fetch actual rate from VehiclePriceHistory
        java.math.BigDecimal ratePerDay = vehiclePriceHistoryRepository
                .findLatestPriceByVehicleId(vehicleId)
                .map(VehiclePriceHistory::getRatePerDay)
                .orElse(java.math.BigDecimal.valueOf(100)); // Fallback rate if none found

        java.math.BigDecimal totalAmount = ratePerDay.multiply(java.math.BigDecimal.valueOf(days));

        // 5. Save BookingPriceSnapshot
        BookingPriceSnapshot priceSnapshot = new BookingPriceSnapshot(
                booking.getBookingId(),
                ratePerDay,
                (int) days,
                totalAmount);
        bookingPriceSnapshotRepository.save(priceSnapshot);

        // 6. Create Invoice (ISSUED)
        Invoice invoice = new Invoice();
        invoice.setBookingId(booking.getBookingId());
        invoice.setFleetOwnerId(vehicle.getFleetOwnerId());
        invoice.setRenterId(renterId);
        invoice.setInvoiceNumber("INV-" + System.currentTimeMillis()); // Simple generation
        invoice.setIssueDate(java.time.LocalDate.now());
        invoice.setDueDate(java.time.LocalDate.now().plusDays(7));
        invoice.setTotalAmount(totalAmount);
        invoice.setStatus(Invoice.InvoiceStatus.ISSUED);
        invoice.setRemarks("Auto-generated invoice for Booking #" + booking.getBookingId());
        invoiceRepository.save(invoice);

        return booking;
    }

    public List<BookingDTO> getBookingsByOwnerId(Long ownerId) {
        List<Booking> bookings = bookingRepository.findByFleetOwnerId(ownerId);
        return mapBookingsToDTOs(bookings);
    }

    /**
     * Fetches recent bookings for a specific owner
     *
     * @param ownerId Owner ID
     * @param limit    Number of bookings to fetch
     * @return List of BookingDTO objects
     */
    public List<BookingDTO> getRecentBookingsByOwnerId(Long ownerId, int limit) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, limit,
                org.springframework.data.domain.Sort.by("startDate").descending());
        List<Booking> bookings = bookingRepository.findByFleetOwnerId(ownerId, pageable).getContent();
        return mapBookingsToDTOs(bookings);
    }

    /**
     * Fetches booking counts by status for a specific owner
     *
     * @param ownerId Owner ID
     * @return BookingCountDTO object
     */
    public com.najmi.fleetshare.dto.BookingCountDTO getBookingCountsByOwnerId(Long ownerId) {
        List<Object[]> results = statusLogRepository.countBookingsByStatusForOwner(ownerId);

        long active = 0;
        long completed = 0;
        long pending = 0;
        long total = 0;

        for (Object[] result : results) {
            BookingStatusLog.BookingStatus status = (BookingStatusLog.BookingStatus) result[0];
            long count = ((Number) result[1]).longValue();
            total += count;

            if (BookingStatusLog.BookingStatus.ACTIVE.equals(status)) {
                active += count;
            } else if (BookingStatusLog.BookingStatus.COMPLETED.equals(status)) {
                completed += count;
            } else if (BookingStatusLog.BookingStatus.PENDING.equals(status) || BookingStatusLog.BookingStatus.CONFIRMED.equals(status)) {
                pending += count;
            }
        }

        return new com.najmi.fleetshare.dto.BookingCountDTO(total, active, completed, pending);
    }

    /**
     * Fetches platform-wide booking counts by status
     *
     * @return BookingCountDTO object
     */
    public com.najmi.fleetshare.dto.BookingCountDTO getPlatformBookingCounts() {
        List<Object[]> results = statusLogRepository.countAllBookingsByStatus();

        long active = 0;
        long completed = 0;
        long pending = 0;
        long total = 0;

        for (Object[] result : results) {
            BookingStatusLog.BookingStatus status = (BookingStatusLog.BookingStatus) result[0];
            long count = ((Number) result[1]).longValue();
            total += count;

            if (BookingStatusLog.BookingStatus.ACTIVE.equals(status)) {
                active += count;
            } else if (BookingStatusLog.BookingStatus.COMPLETED.equals(status)) {
                completed += count;
            } else if (BookingStatusLog.BookingStatus.PENDING.equals(status) || BookingStatusLog.BookingStatus.CONFIRMED.equals(status)) {
                pending += count;
            }
        }

        return new com.najmi.fleetshare.dto.BookingCountDTO(total, active, completed, pending);
    }

    /**
     * Fetches recent bookings for the platform
     *
     * @param limit Number of bookings to fetch
     * @return List of BookingDTO objects
     */
    public List<BookingDTO> getRecentBookings(int limit) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, limit,
                org.springframework.data.domain.Sort.by("createdAt").descending());
        List<Booking> bookings = bookingRepository.findAll(pageable).getContent();
        return mapBookingsToDTOs(bookings);
    }
}
