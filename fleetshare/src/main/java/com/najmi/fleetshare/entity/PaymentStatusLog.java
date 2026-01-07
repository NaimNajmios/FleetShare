package com.najmi.fleetshare.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "paymentstatuslog")
public class PaymentStatusLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_log_id")
    private Long paymentLogId;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_value", nullable = false)
    private Payment.PaymentStatus statusValue;

    @Column(name = "actor_user_id")
    private Long actorUserId;

    @Column(name = "status_timestamp", nullable = false)
    private LocalDateTime statusTimestamp;

    @Column(name = "remarks", length = 500)
    private String remarks;

    // Constructors
    public PaymentStatusLog() {
    }

    public PaymentStatusLog(Long paymentId, Payment.PaymentStatus statusValue, Long actorUserId,
            LocalDateTime statusTimestamp, String remarks) {
        this.paymentId = paymentId;
        this.statusValue = statusValue;
        this.actorUserId = actorUserId;
        this.statusTimestamp = statusTimestamp;
        this.remarks = remarks;
    }

    // Getters and Setters
    public Long getPaymentLogId() {
        return paymentLogId;
    }

    public void setPaymentLogId(Long paymentLogId) {
        this.paymentLogId = paymentLogId;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Payment.PaymentStatus getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(Payment.PaymentStatus statusValue) {
        this.statusValue = statusValue;
    }

    public Long getActorUserId() {
        return actorUserId;
    }

    public void setActorUserId(Long actorUserId) {
        this.actorUserId = actorUserId;
    }

    public LocalDateTime getStatusTimestamp() {
        return statusTimestamp;
    }

    public void setStatusTimestamp(LocalDateTime statusTimestamp) {
        this.statusTimestamp = statusTimestamp;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
