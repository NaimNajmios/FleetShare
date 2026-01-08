package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.BookingStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingStatusLogRepository extends JpaRepository<BookingStatusLog, Long> {

    @Query("SELECT bsl FROM BookingStatusLog bsl WHERE bsl.bookingId = :bookingId ORDER BY bsl.statusTimestamp DESC LIMIT 1")
    Optional<BookingStatusLog> findLatestStatusByBookingId(Long bookingId);

    List<BookingStatusLog> findByBookingIdOrderByStatusTimestampDesc(Long bookingId);

    @Query("SELECT bsl FROM BookingStatusLog bsl WHERE bsl.bookingId IN :bookingIds")
    List<BookingStatusLog> findByBookingIdIn(java.util.Collection<Long> bookingIds);

    @Query("SELECT bsl.bookingId, bsl.statusValue FROM BookingStatusLog bsl " +
           "WHERE (bsl.bookingId, bsl.statusTimestamp) IN (" +
           "    SELECT bsl2.bookingId, MAX(bsl2.statusTimestamp) " +
           "    FROM BookingStatusLog bsl2 " +
           "    WHERE bsl2.bookingId IN :bookingIds " +
           "    GROUP BY bsl2.bookingId" +
           ")")
    List<Object[]> findLatestStatusForBookings(@org.springframework.data.repository.query.Param("bookingIds") java.util.Collection<Long> bookingIds);

    @Query("SELECT bsl.statusValue, COUNT(bsl) " +
            "FROM BookingStatusLog bsl " +
            "WHERE (bsl.bookingId, bsl.statusTimestamp) IN (" +
            "    SELECT bsl2.bookingId, MAX(bsl2.statusTimestamp) " +
            "    FROM BookingStatusLog bsl2 " +
            "    WHERE bsl2.bookingId IN (SELECT b.bookingId FROM Booking b WHERE b.renterId = :renterId) " +
            "    GROUP BY bsl2.bookingId" +
            ") " +
            "GROUP BY bsl.statusValue")
    List<Object[]> countBookingsByStatusForRenter(@org.springframework.data.repository.query.Param("renterId") Long renterId);
}
