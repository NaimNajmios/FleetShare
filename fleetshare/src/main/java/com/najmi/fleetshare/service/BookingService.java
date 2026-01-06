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
        Map<Long, String> statusMap = statusLogRepository.findByBookingIdIn(bookingIds).stream()
                .collect(Collectors.groupingBy(BookingStatusLog::getBookingId,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(BookingStatusLog::getStatusTimestamp)),
                                opt -> opt.map(log -> log.getStatusValue().name()).orElse("PENDING"))));

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
     * Fetches all bookings for a specific fleet owner
     *
     * @param ownerId Fleet owner ID
     * @return List of BookingDTO objects for owner's vehicle bookings
     */
    public List<BookingDTO> getBookingsByOwnerId(Long ownerId) {
        List<Booking> bookings = bookingRepository.findByFleetOwnerId(ownerId);
        return mapBookingsToDTOs(bookings);
    }
}
