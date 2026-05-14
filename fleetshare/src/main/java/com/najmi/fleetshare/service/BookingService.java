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
import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

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

    @Autowired
    private EmailService emailService;

    @Autowired
    private PaymentService paymentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
                                list -> list.isEmpty() ? null : list.stream()
                                        .max(java.util.Comparator.comparing(Payment::getPaymentId))
                                        .orElse(null))));

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
            dto.setInvoiceIssueDate(invoice.getIssueDate());
            dto.setInvoiceDueDate(invoice.getDueDate());
            dto.setInvoiceStatus(invoice.getStatus() != null ? invoice.getStatus().name() : null);
            dto.setInvoiceRemarks(invoice.getRemarks());

            // Fetch Renter contact info
            Renter renter = renterRepository.findById(invoice.getRenterId()).orElse(null);
            if (renter != null) {
                dto.setRenterPhoneNumber(renter.getPhoneNumber());
            }

            // Fetch FleetOwner contact info
            FleetOwner fleetOwner = fleetOwnerRepository.findById(invoice.getFleetOwnerId()).orElse(null);
            if (fleetOwner != null) {
                dto.setOwnerContactPhone(fleetOwner.getContactPhone());
                dto.setOwnerIsVerified(fleetOwner.getIsVerified() != null ? fleetOwner.getIsVerified() : false);
            }

            // Fetch Payment
            List<Payment> payments = paymentRepository.findByInvoiceId(invoice.getInvoiceId());
            if (!payments.isEmpty()) {
                // Pick the latest payment (highest ID)
                Payment payment = payments.stream()
                        .max(java.util.Comparator.comparing(Payment::getPaymentId))
                        .orElse(null);
                
                if (payment != null) {
                    dto.setPaymentMethod(payment.getPaymentMethod().name());
                    dto.setPaymentStatus(payment.getPaymentStatus().name());
                    dto.setProofOfPaymentUrl(payment.getVerificationProofUrl());
                    dto.setPaymentDate(payment.getPaymentDate());
                    dto.setPaymentTransactionReference(payment.getTransactionReference());
                }
            }
        }

        // Fetch Booking Price Snapshot
        bookingPriceSnapshotRepository.findByBookingId(bookingId).ifPresent(snapshot -> {
            dto.setRatePerDay(snapshot.getRatePerDay());
            dto.setDaysRented(snapshot.getDaysRented());
            dto.setSnapshotRemarks(snapshot.getRemarks());

            // Phase 2: Parse adjustments from JSON remarks if they look like JSON
            if (snapshot.getRemarks() != null && snapshot.getRemarks().trim().startsWith("[")) {
                try {
                    List<BookingDTO.PriceAdjustment> adjustments = objectMapper.readValue(
                        snapshot.getRemarks(), 
                        new TypeReference<List<BookingDTO.PriceAdjustment>>() {}
                    );
                    dto.setAdjustments(adjustments);
                } catch (Exception e) {
                    // Fallback: keep as raw remarks
                }
            }
        });

        // Compute if the booking's start date has passed
        dto.setStartDatePassed(dto.getStartDate() != null && dto.getStartDate().isBefore(java.time.LocalDateTime.now()));

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

    @org.springframework.transaction.annotation.Transactional
    public void updateBooking(BookingDTO bookingDTO) {
        Booking booking = bookingRepository.findById(bookingDTO.getBookingId()).orElse(null);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found: " + bookingDTO.getBookingId());
        }

        // 1. Update booking dates
        booking.setStartDate(bookingDTO.getStartDate());
        booking.setEndDate(bookingDTO.getEndDate());
        bookingRepository.save(booking);

        // 2. Calculate days from new dates
        long days = java.time.temporal.ChronoUnit.DAYS.between(
                bookingDTO.getStartDate().toLocalDate(),
                bookingDTO.getEndDate().toLocalDate());
        if (days <= 0)
            days = 1; // Minimum 1 day

        java.math.BigDecimal totalCost;
        BookingPriceSnapshot snapshot = bookingPriceSnapshotRepository
                .findByBookingId(booking.getBookingId()).orElse(null);

        if (snapshot != null) {
            java.math.BigDecimal autoCalculated = snapshot.getRatePerDay()
                    .multiply(java.math.BigDecimal.valueOf(days));

            // Phase 2: Calculate adjustments total
            java.math.BigDecimal adjustmentsTotal = java.math.BigDecimal.ZERO;
            if (bookingDTO.getAdjustments() != null && !bookingDTO.getAdjustments().isEmpty()) {
                for (BookingDTO.PriceAdjustment adj : bookingDTO.getAdjustments()) {
                    if (adj.getAmount() != null) {
                        adjustmentsTotal = adjustmentsTotal.add(adj.getAmount());
                    }
                }
                
                // Serialize adjustments to JSON remarks
                try {
                    String jsonRemarks = objectMapper.writeValueAsString(bookingDTO.getAdjustments());
                    snapshot.setRemarks(jsonRemarks);
                } catch (Exception e) {
                    snapshot.setRemarks(bookingDTO.getSnapshotRemarks());
                }
                
                totalCost = autoCalculated.add(adjustmentsTotal);
            } else if (bookingDTO.getTotalCost() != null
                    && bookingDTO.getTotalCost().compareTo(java.math.BigDecimal.ZERO) > 0
                    && bookingDTO.getTotalCost().compareTo(autoCalculated) != 0) {
                // Legacy manual override support
                totalCost = bookingDTO.getTotalCost();
                snapshot.setRemarks(bookingDTO.getSnapshotRemarks());
            } else {
                // Pure auto-calculated
                totalCost = autoCalculated;
                snapshot.setRemarks(null);
            }

            snapshot.setDaysRented((int) days);
            snapshot.setTotalCalculatedCost(totalCost);
            bookingPriceSnapshotRepository.save(snapshot);
        } else {
            // Fallback: use totalCost from form if no snapshot exists
            totalCost = bookingDTO.getTotalCost() != null
                    ? bookingDTO.getTotalCost()
                    : java.math.BigDecimal.ZERO;
        }

        // 3. Update invoice total_amount
        List<Invoice> invoices = invoiceRepository.findByBookingId(booking.getBookingId());
        if (!invoices.isEmpty()) {
            Invoice invoice = invoices.get(0);
            invoice.setTotalAmount(totalCost);
            invoiceRepository.save(invoice);

            // 4. Update payment amount (if exists)
            List<Payment> payments = paymentRepository.findByInvoiceId(invoice.getInvoiceId());
            if (!payments.isEmpty()) {
                Payment payment = payments.get(0);
                payment.setAmount(totalCost);
                paymentRepository.save(payment);
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

        // Block CONFIRMED or ACTIVE if the booking start date has passed
        if ((targetStatus == BookingStatusLog.BookingStatus.CONFIRMED || targetStatus == BookingStatusLog.BookingStatus.ACTIVE)
                && booking.getStartDate() != null && booking.getStartDate().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalStateException(
                    "Cannot transition to " + targetStatus + " because the booking start date (" +
                    booking.getStartDate() + ") has already passed.");
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

        String statusName = targetStatus.name();
        String finalRemarks = remarks != null && !remarks.trim().isEmpty() ? remarks.trim() : getDefaultRemarks(targetStatus);
        
        // Notify Renter
        Renter renter = renterRepository.findById(booking.getRenterId()).orElse(null);
        if (renter != null) {
            User renterUser = userRepository.findById(renter.getUserId()).orElse(null);
            if (renterUser != null && renterUser.getEmail() != null) {
                Map<String, Object> emailModel = new HashMap<>();
                emailModel.put("userName", renter.getFullName());
                emailModel.put("bookingId", booking.getBookingId());
                emailModel.put("newStatus", statusName);
                emailModel.put("remarks", finalRemarks);
                
                emailService.sendHtmlEmail(renterUser.getEmail(), "Booking Status Updated to " + statusName, "email/booking-status-update", emailModel);
            }
        }
        
        // Notify Owner
        FleetOwner owner = fleetOwnerRepository.findById(booking.getFleetOwnerId()).orElse(null);
        if (owner != null) {
            User ownerUser = userRepository.findById(owner.getUserId()).orElse(null);
            if (ownerUser != null && ownerUser.getEmail() != null) {
                Map<String, Object> emailModel = new HashMap<>();
                emailModel.put("userName", owner.getBusinessName());
                emailModel.put("bookingId", booking.getBookingId());
                emailModel.put("newStatus", statusName);
                emailModel.put("remarks", finalRemarks);
                
                emailService.sendHtmlEmail(ownerUser.getEmail(), "Booking Status Updated to " + statusName, "email/booking-status-update", emailModel);
            }
        }

        // If cancelled, clean up related resources
        if (targetStatus == BookingStatusLog.BookingStatus.CANCELLED) {
            // Mark vehicle as available
            Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId()).orElse(null);
            if (vehicle != null) {
                vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
                vehicleRepository.save(vehicle);
            }

            // Find invoice and payment records
            List<Invoice> invoices = invoiceRepository.findByBookingId(booking.getBookingId());
            if (!invoices.isEmpty()) {
                Invoice invoice = invoices.get(0);
                // Void the invoice if it was paid
                if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
                    invoice.setStatus(Invoice.InvoiceStatus.VOID);
                    invoiceRepository.save(invoice);
                }
                // Refund payment if verified
                List<Payment> payments = paymentRepository.findByInvoiceId(invoice.getInvoiceId());
                if (!payments.isEmpty()) {
                    Payment payment = payments.stream()
                            .max(java.util.Comparator.comparing(Payment::getPaymentId))
                            .orElse(null);
                    if (payment != null && payment.getPaymentStatus() == Payment.PaymentStatus.VERIFIED) {
                        try {
                            paymentService.refundPayment(payment.getPaymentId(), actorUserId, finalRemarks);
                        } catch (Exception ignored) {
                            // Refund is best-effort; don't fail the cancellation
                        }
                    }
                }
            }
        }

        return booking;
    }

    /**
     * Returns a list of valid next statuses for a given current status.
     */
    public List<String> getValidNextStatuses(String currentStatusStr) {
        try {
            BookingStatusLog.BookingStatus current = BookingStatusLog.BookingStatus.valueOf(currentStatusStr.toUpperCase());
            List<String> nextStatuses = new ArrayList<>();
            for (BookingStatusLog.BookingStatus status : BookingStatusLog.BookingStatus.values()) {
                if (isValidTransition(current, status)) {
                    nextStatuses.add(status.name());
                }
            }
            return nextStatuses;
        } catch (Exception e) {
            return Collections.emptyList();
        }
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

        // 7. Send Email Notifications
        Map<String, Object> emailModel = new HashMap<>();
        emailModel.put("bookingId", booking.getBookingId());
        emailModel.put("vehicleDetails", vehicle.getBrand() + " " + vehicle.getModel() + " (" + vehicle.getRegistrationNo() + ")");
        emailModel.put("startDate", startDate.atStartOfDay());
        emailModel.put("endDate", endDate.atTime(23, 59, 59));

        User renterUser = userRepository.findById(renter.getUserId()).orElse(null);
        if (renterUser != null && renterUser.getEmail() != null) {
            emailModel.put("userName", renter.getFullName());
            emailService.sendHtmlEmail(renterUser.getEmail(), "Booking Initialized", "email/booking-created", emailModel);
        }

        User ownerUser = userRepository.findById(owner.getUserId()).orElse(null);
        if (ownerUser != null && ownerUser.getEmail() != null) {
            emailModel.put("userName", owner.getBusinessName());
            emailService.sendHtmlEmail(ownerUser.getEmail(), "New Booking Request", "email/booking-created", emailModel);
        }

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
     * @param limit   Number of bookings to fetch
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
            } else if (BookingStatusLog.BookingStatus.PENDING.equals(status)
                    || BookingStatusLog.BookingStatus.CONFIRMED.equals(status)) {
                pending += count;
            }
        }

        return new com.najmi.fleetshare.dto.BookingCountDTO(total, active, completed, pending);
    }

    /**
     * Returns date ranges that are unavailable for a vehicle due to existing
     * bookings.
     * Excludes the specified booking (so the user can still see their own dates as
     * available)
     * and excludes bookings with CANCELLED or COMPLETED status.
     *
     * @param vehicleId        The vehicle ID
     * @param excludeBookingId The booking ID to exclude (the one being edited), can
     *                         be null
     * @return List of maps with "start" and "end" keys as ISO date-time strings
     */
    public List<Map<String, String>> getUnavailableDateRanges(Long vehicleId, Long excludeBookingId) {
        List<Booking> vehicleBookings = bookingRepository.findByVehicleId(vehicleId);

        // Get all booking IDs to fetch their statuses
        Set<Long> bookingIds = vehicleBookings.stream()
                .map(Booking::getBookingId)
                .collect(Collectors.toSet());

        // Fetch latest status for all bookings in bulk
        Map<Long, String> statusMap = Collections.emptyMap();
        if (!bookingIds.isEmpty()) {
            statusMap = statusLogRepository.findLatestStatusForBookings(bookingIds).stream()
                    .collect(Collectors.toMap(
                            BookingStatusLog::getBookingId,
                            log -> log.getStatusValue().name(),
                            (existing, replacement) -> existing));
        }

        List<Map<String, String>> unavailableRanges = new ArrayList<>();
        Set<String> excludedStatuses = Set.of("CANCELLED", "COMPLETED");

        for (Booking booking : vehicleBookings) {
            // Skip the booking being edited
            if (excludeBookingId != null && booking.getBookingId().equals(excludeBookingId)) {
                continue;
            }

            String status = statusMap.getOrDefault(booking.getBookingId(), "PENDING");

            // Skip cancelled/completed bookings
            if (excludedStatuses.contains(status)) {
                continue;
            }

            Map<String, String> range = Map.of(
                    "from", booking.getStartDate().toLocalDate().toString(),
                    "to", booking.getEndDate().toLocalDate().toString());
            unavailableRanges.add(range);
        }

        return unavailableRanges;
    }
}
