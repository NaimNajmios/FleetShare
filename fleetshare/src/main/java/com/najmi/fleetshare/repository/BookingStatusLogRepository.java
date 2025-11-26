package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.BookingStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingStatusLogRepository extends JpaRepository<BookingStatusLog, Long> {

    @Query("SELECT bsl FROM BookingStatusLog bsl WHERE bsl.bookingId = :bookingId ORDER BY bsl.statusTimestamp DESC LIMIT 1")
    Optional<BookingStatusLog> findLatestStatusByBookingId(Long bookingId);
}
