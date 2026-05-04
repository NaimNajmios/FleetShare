package com.najmi.fleetshare.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PaymentDetailDTO {
    private Long paymentId;
    private String invoiceNumber;
    private String renterName;
    private String renterEmail;
    private Long renterId;
    private String ownerBusinessName;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime paymentDate;
    private String transactionReference;
    private String verificationProofUrl;

    // Invoice details
    private Long invoiceId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private String invoiceStatus;
    private String invoiceRemarks;
    private Long bookingId;
    private LocalDateTime bookingStartDate;

    // Commission/split payment details
    private java.math.BigDecimal platformCommission;
    private java.math.BigDecimal ownerPayout;
    private java.math.BigDecimal commissionRate;
    private Boolean splitPaymentEnabled;

    // Constructors
    public PaymentDetailDTO() {
    }

    // Getters and Setters
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getRenterName() {
        return renterName;
    }

    public void setRenterName(String renterName) {
        this.renterName = renterName;
    }

    public String getRenterEmail() {
        return renterEmail;
    }

    public void setRenterEmail(String renterEmail) {
        this.renterEmail = renterEmail;
    }

    public Long getRenterId() {
        return renterId;
    }

    public void setRenterId(Long renterId) {
        this.renterId = renterId;
    }

    public String getOwnerBusinessName() {
        return ownerBusinessName;
    }

    public void setOwnerBusinessName(String ownerBusinessName) {
        this.ownerBusinessName = ownerBusinessName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
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

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public String getInvoiceRemarks() {
        return invoiceRemarks;
    }

    public void setInvoiceRemarks(String invoiceRemarks) {
        this.invoiceRemarks = invoiceRemarks;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public LocalDateTime getBookingStartDate() {
        return bookingStartDate;
    }

    public void setBookingStartDate(LocalDateTime bookingStartDate) {
        this.bookingStartDate = bookingStartDate;
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
