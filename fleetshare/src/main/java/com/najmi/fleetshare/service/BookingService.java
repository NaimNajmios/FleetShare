package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.BookingDTO;
import com.najmi.fleetshare.entity.*;
import com.najmi.fleetshare.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        List<BookingDTO> bookingDTOs = new ArrayList<>();

        for (Booking booking : bookings) {
            // Get renter information
            Renter renter = renterRepository.findById(booking.getRenterId()).orElse(null);
            User renterUser = renter != null ? userRepository.findById(renter.getUserId()).orElse(null) : null;

            // Get vehicle information
            Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId()).orElse(null);

            // Get fleet owner information
            FleetOwner owner = fleetOwnerRepository.findById(booking.getFleetOwnerId()).orElse(null);

            // Get latest status
            String status = statusLogRepository.findLatestStatusByBookingId(booking.getBookingId())
                    .map(log -> log.getStatusValue().name())
                    .orElse("PENDING");

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
                        null,
                        null,
                        null,
                        null,
                        null,
                        booking.getCreatedAt());
                bookingDTOs.add(dto);
            }
        }

        return bookingDTOs;
    }

    public BookingDTO getBookingDetails(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null)
            return null;

        Renter renter = renterRepository.findById(booking.getRenterId()).orElse(null);
        User renterUser = renter != null ? userRepository.findById(renter.getUserId()).orElse(null) : null;
        Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId()).orElse(null);
        FleetOwner owner = fleetOwnerRepository.findById(booking.getFleetOwnerId()).orElse(null);

        String status = statusLogRepository.findLatestStatusByBookingId(booking.getBookingId())
                .map(log -> log.getStatusValue().name())
                .orElse("PENDING");

        List<Invoice> invoices = invoiceRepository.findByBookingId(bookingId);
        Invoice invoice = invoices.isEmpty() ? null : invoices.get(0);

        Payment payment = null;
        if (invoice != null) {
            List<Payment> payments = paymentRepository.findByInvoiceId(invoice.getInvoiceId());
            if (!payments.isEmpty()) {
                payment = payments.get(0);
            }
        }

        return new BookingDTO(
                booking.getBookingId(),
                renter != null ? renter.getRenterId() : null,
                renter != null ? renter.getFullName() : "Unknown",
                renterUser != null ? renterUser.getEmail() : "N/A",
                vehicle != null ? vehicle.getVehicleId() : null,
                vehicle != null ? vehicle.getModel() : "Unknown",
                vehicle != null ? vehicle.getBrand() : "Unknown",
                vehicle != null ? vehicle.getRegistrationNo() : "Unknown",
                vehicle != null ? vehicle.getVehicleImageUrl() : null,
                owner != null ? owner.getBusinessName() : "Unknown Owner",
                booking.getStartDate(),
                booking.getEndDate(),
                status,
                invoice != null ? invoice.getTotalAmount() : null,
                payment != null ? payment.getPaymentMethod().name() : null,
                payment != null ? payment.getPaymentStatus().name() : null,
                invoice != null ? invoice.getInvoiceNumber() : null,
                payment != null ? payment.getVerificationProofUrl() : null,
                booking.getCreatedAt());
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
}
