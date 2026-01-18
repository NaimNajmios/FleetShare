package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByInvoiceId(Long invoiceId);

    @org.springframework.data.jpa.repository.Query("SELECT p FROM Payment p WHERE p.invoiceId IN :invoiceIds")
    List<Payment> findByInvoiceIdIn(java.util.Collection<Long> invoiceIds);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(p.amount) FROM Payment p, Invoice i " +
            "WHERE p.invoiceId = i.invoiceId " +
            "AND i.fleetOwnerId = :ownerId " +
            "AND p.paymentStatus IN :statuses")
    java.math.BigDecimal calculateTotalRevenueForOwner(@org.springframework.data.repository.query.Param("ownerId") Long ownerId,
                                                       @org.springframework.data.repository.query.Param("statuses") java.util.Collection<Payment.PaymentStatus> statuses);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentStatus IN :statuses")
    java.math.BigDecimal calculateTotalRevenue(@org.springframework.data.repository.query.Param("statuses") java.util.Collection<Payment.PaymentStatus> statuses);
}
