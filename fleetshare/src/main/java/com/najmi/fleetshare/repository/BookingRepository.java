package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByRenterId(Long renterId);

    List<Booking> findByFleetOwnerId(Long fleetOwnerId);

    List<Booking> findByVehicleId(Long vehicleId);
}
