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
}
