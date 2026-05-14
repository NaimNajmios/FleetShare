package com.najmi.fleetshare.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(name = "transaction_reference", length = 255)
    private String transactionReference;

    @Column(name = "verification_proof_url", length = 1024)
    private String verificationProofUrl;

    @Column(name = "verified_by_user_id")
    private Long verifiedByUserId;

    @Column(name = "toyyibpay_bill_code", length = 50)
    private String toyyibpayBillCode;

    @Column(name = "gateway_ref_no", length = 100)
    private String gatewayRefNo;

    @Column(name = "platform_commission", precision = 10, scale = 2)
    private java.math.BigDecimal platformCommission;

    @Column(name = "owner_payout", precision = 10, scale = 2)
    private java.math.BigDecimal ownerPayout;

    @Column(name = "commission_rate", precision = 5, scale = 4)
    private java.math.BigDecimal commissionRate;

    @Column(name = "split_payment_enabled")
    private Boolean splitPaymentEnabled = false;

    // Enums
    public enum PaymentMethod {
        FPX, BANK_TRANSFER, QR_PAYMENT, CASH
    }

    public enum PaymentStatus {
        PENDING, VERIFIED, FAILED
    }

    // Constructors
    public Payment() {
    }

    // Getters and Setters
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getVerificationProofUrl() {
        return verificationProofUrl;
    }

    public void setVerificationProofUrl(String verificationProofUrl) {
        this.verificationProofUrl = verificationProofUrl;
    }

    public Long getVerifiedByUserId() {
        return verifiedByUserId;
    }

    public void setVerifiedByUserId(Long verifiedByUserId) {
        this.verifiedByUserId = verifiedByUserId;
    }

    public String getToyyibpayBillCode() {
        return toyyibpayBillCode;
    }

    public void setToyyibpayBillCode(String toyyibpayBillCode) {
        this.toyyibpayBillCode = toyyibpayBillCode;
    }

    public String getGatewayRefNo() {
        return gatewayRefNo;
    }

    public void setGatewayRefNo(String gatewayRefNo) {
        this.gatewayRefNo = gatewayRefNo;
    }

    public java.math.BigDecimal getPlatformCommission() {
        return platformCommission;
    }

    public void setPlatformCommission(java.math.BigDecimal platformCommission) {
        this.platformCommission = platformCommission;
    }

    public java.math.BigDecimal getOwnerPayout() {
        return ownerPayout;
    }

    public void setOwnerPayout(java.math.BigDecimal ownerPayout) {
        this.ownerPayout = ownerPayout;
    }

    public java.math.BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(java.math.BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public Boolean getSplitPaymentEnabled() {
        return splitPaymentEnabled;
    }

    public void setSplitPaymentEnabled(Boolean splitPaymentEnabled) {
        this.splitPaymentEnabled = splitPaymentEnabled;
    }
}
