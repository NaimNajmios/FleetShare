package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.dto.BookingDTO;
import com.najmi.fleetshare.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByRenterId(Long renterId);

    org.springframework.data.domain.Page<Booking> findByRenterId(Long renterId, org.springframework.data.domain.Pageable pageable);

    List<Booking> findByFleetOwnerId(Long fleetOwnerId);

    org.springframework.data.domain.Page<Booking> findByFleetOwnerId(Long fleetOwnerId, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT DISTINCT b.renterId FROM Booking b WHERE b.fleetOwnerId = :fleetOwnerId")
    List<Long> findDistinctRenterIdsByFleetOwnerId(@Param("fleetOwnerId") Long fleetOwnerId);

    @Query("SELECT new com.najmi.fleetshare.dto.BookingDTO(" +
            "b.bookingId, r.renterId, r.fullName, COALESCE(u.email, 'N/A'), v.vehicleId, v.model, v.brand, " +
            "v.registrationNo, v.vehicleImageUrl, COALESCE(f.businessName, 'Unknown Owner'), b.startDate, b.endDate, " +
            "'PENDING', " +
            "null, " +
            "null, " +
            "null, " +
            "null, " +
            "null, " +
            "b.createdAt, v.fuelType, v.transmissionType, v.category, null) " +
            "FROM Booking b " +
            "LEFT JOIN Renter r ON b.renterId = r.renterId " +
            "LEFT JOIN User u ON r.userId = u.userId " +
            "LEFT JOIN Vehicle v ON b.vehicleId = v.vehicleId " +
            "LEFT JOIN FleetOwner f ON b.fleetOwnerId = f.fleetOwnerId " +
            "WHERE b.bookingId = :bookingId")
    java.util.Optional<BookingDTO> findBookingDetailsById(@Param("bookingId") Long bookingId);

    List<Booking> findByVehicleId(Long vehicleId);
}
