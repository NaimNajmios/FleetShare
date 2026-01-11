package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.BookingPriceSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingPriceSnapshotRepository extends JpaRepository<BookingPriceSnapshot, Long> {

    Optional<BookingPriceSnapshot> findByBookingId(Long bookingId);
}
