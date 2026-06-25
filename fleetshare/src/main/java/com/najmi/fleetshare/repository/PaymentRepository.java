package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByInvoiceId(Long invoiceId);

    Optional<Payment> findByToyyibpayBillCode(String toyyibpayBillCode);

    @org.springframework.data.jpa.repository.Query("SELECT p FROM Payment p WHERE p.invoiceId IN :invoiceIds")
    List<Payment> findByInvoiceIdIn(java.util.Collection<Long> invoiceIds);

    @org.springframework.data.jpa.repository.Query("SELECT p FROM Payment p WHERE p.invoiceId IN :invoiceIds")
    Page<Payment> findByInvoiceIdIn(java.util.Collection<Long> invoiceIds, Pageable pageable);

    Page<Payment> findByInvoiceIdInAndSplitPaymentEnabledTrue(java.util.Collection<Long> invoiceIds, Pageable pageable);

    Page<Payment> findBySplitPaymentEnabledTrue(Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT p FROM Payment p WHERE p.invoiceId IN :invoiceIds AND (p.paymentStatus = 'PENDING' OR p.paymentDate >= :since)")
    List<Payment> findDashboardPaymentsByInvoiceIds(@org.springframework.data.repository.query.Param("invoiceIds") java.util.Collection<Long> invoiceIds, @org.springframework.data.repository.query.Param("since") java.time.LocalDateTime since);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(p.amount) FROM Payment p, Invoice i " +
            "WHERE p.invoiceId = i.invoiceId " +
            "AND i.fleetOwnerId = :ownerId " +
            "AND p.paymentStatus IN :statuses")
    java.math.BigDecimal calculateTotalRevenueForOwner(@org.springframework.data.repository.query.Param("ownerId") Long ownerId,
                                                       @org.springframework.data.repository.query.Param("statuses") java.util.Collection<Payment.PaymentStatus> statuses);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentStatus IN :statuses")
    java.math.BigDecimal calculateTotalPlatformRevenue(@org.springframework.data.repository.query.Param("statuses") java.util.Collection<Payment.PaymentStatus> statuses);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentStatus IN :statuses AND p.paymentDate >= :since")
    java.math.BigDecimal calculatePlatformRevenueSince(@org.springframework.data.repository.query.Param("statuses") java.util.Collection<Payment.PaymentStatus> statuses, @org.springframework.data.repository.query.Param("since") java.time.LocalDateTime since);
}
