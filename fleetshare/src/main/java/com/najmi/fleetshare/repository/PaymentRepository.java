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

    @org.springframework.data.jpa.repository.Query("SELECT p FROM Payment p " +
            "LEFT JOIN Invoice i ON p.invoiceId = i.invoiceId " +
            "LEFT JOIN Renter r ON i.renterId = r.renterId " +
            "LEFT JOIN FleetOwner f ON i.fleetOwnerId = f.fleetOwnerId " +
            "WHERE (i.fleetOwnerId = :ownerId) " +
            "AND (:status IS NULL OR p.paymentStatus = :status) " +
            "AND (:method IS NULL OR p.paymentMethod = :method) " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR p.paymentDate >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR p.paymentDate <= :endDate) " +
            "AND (:search IS NULL OR " +
            "LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.transactionReference) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.gatewayRefNo) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(r.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(f.businessName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Payment> findFilteredPaymentsForOwner(
            @org.springframework.data.repository.query.Param("ownerId") Long ownerId,
            @org.springframework.data.repository.query.Param("search") String search,
            @org.springframework.data.repository.query.Param("status") Payment.PaymentStatus status,
            @org.springframework.data.repository.query.Param("method") Payment.PaymentMethod method,
            @org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate,
            @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate,
            Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT p FROM Payment p " +
            "LEFT JOIN Invoice i ON p.invoiceId = i.invoiceId " +
            "LEFT JOIN Renter r ON i.renterId = r.renterId " +
            "LEFT JOIN FleetOwner f ON i.fleetOwnerId = f.fleetOwnerId " +
            "WHERE (:status IS NULL OR p.paymentStatus = :status) " +
            "AND (:method IS NULL OR p.paymentMethod = :method) " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR p.paymentDate >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR p.paymentDate <= :endDate) " +
            "AND (:search IS NULL OR " +
            "LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.transactionReference) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.gatewayRefNo) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(r.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(f.businessName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Payment> findFilteredPaymentsForAdmin(
            @org.springframework.data.repository.query.Param("search") String search,
            @org.springframework.data.repository.query.Param("status") Payment.PaymentStatus status,
            @org.springframework.data.repository.query.Param("method") Payment.PaymentMethod method,
            @org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate,
            @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate,
            Pageable pageable);
}
