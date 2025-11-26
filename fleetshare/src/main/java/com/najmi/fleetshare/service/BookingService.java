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
                        renter.getFullName(),
                        renterUser != null ? renterUser.getEmail() : "N/A",
                        vehicle.getModel(),
                        vehicle.getBrand(),
                        vehicle.getRegistrationNo(),
                        owner != null ? owner.getBusinessName() : "Unknown Owner",
                        booking.getStartDate(),
                        booking.getEndDate(),
                        status,
                        booking.getCreatedAt());
                bookingDTOs.add(dto);
            }
        }

        return bookingDTOs;
    }
}
