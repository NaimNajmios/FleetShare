package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.PaymentStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentStatusLogRepository extends JpaRepository<PaymentStatusLog, Long> {
    List<PaymentStatusLog> findByPaymentIdOrderByStatusTimestampDesc(Long paymentId);
}
