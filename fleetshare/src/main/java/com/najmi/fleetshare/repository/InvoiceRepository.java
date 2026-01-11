package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByRenterId(Long renterId);

    List<Invoice> findByFleetOwnerId(Long fleetOwnerId);

    List<Invoice> findByBookingId(Long bookingId);

    java.util.Optional<Invoice> findFirstByBookingId(Long bookingId);

    @org.springframework.data.jpa.repository.Query("SELECT i FROM Invoice i WHERE i.bookingId IN :bookingIds")
    List<Invoice> findByBookingIdIn(java.util.Collection<Long> bookingIds);
}
