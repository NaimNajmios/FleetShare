package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.PaymentDTO;
import com.najmi.fleetshare.dto.PaymentDetailDTO;
import com.najmi.fleetshare.dto.PaymentStatusLogDTO;
import com.najmi.fleetshare.entity.*;
import com.najmi.fleetshare.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private RenterRepository renterRepository;

    @Autowired
    private FleetOwnerRepository fleetOwnerRepository;

    @Autowired
    private PaymentStatusLogRepository paymentStatusLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Fetches all payments with related information
     * 
     * @return List of PaymentDTO objects
     */
    public List<PaymentDTO> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        List<PaymentDTO> paymentDTOs = new ArrayList<>();

        for (Payment payment : payments) {
            // Get invoice information
            Invoice invoice = invoiceRepository.findById(payment.getInvoiceId()).orElse(null);

            if (invoice != null) {
                // Get renter information
                Renter renter = renterRepository.findById(invoice.getRenterId()).orElse(null);

                // Get fleet owner information
                FleetOwner owner = fleetOwnerRepository.findById(invoice.getFleetOwnerId()).orElse(null);

                PaymentDTO dto = new PaymentDTO(
                        payment.getPaymentId(),
                        invoice.getInvoiceNumber(),
                        renter != null ? renter.getFullName() : "Unknown",
                        owner != null ? owner.getBusinessName() : "Unknown",
                        payment.getAmount(),
                        payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : "N/A",
                        payment.getPaymentStatus() != null ? payment.getPaymentStatus().name() : "PENDING",
                        payment.getPaymentDate(),
                        payment.getTransactionReference());
                paymentDTOs.add(dto);
            }
        }

        return paymentDTOs;
    }

    public List<PaymentStatusLog> getPaymentStatusLogs(Long paymentId) {
        return paymentStatusLogRepository.findByPaymentIdOrderByStatusTimestampDesc(paymentId);
    }

    /**
     * Fetches payment status logs as DTOs with actor names resolved
     * 
     * @param paymentId Payment ID
     * @return List of PaymentStatusLogDTO objects
     */
    public List<PaymentStatusLogDTO> getPaymentStatusLogsDTO(Long paymentId) {
        List<PaymentStatusLog> logs = paymentStatusLogRepository.findByPaymentIdOrderByStatusTimestampDesc(paymentId);
        List<PaymentStatusLogDTO> dtos = new ArrayList<>();

        for (PaymentStatusLog log : logs) {
            String actorName = "System";
            if (log.getActorUserId() != null) {
                User actor = userRepository.findById(log.getActorUserId()).orElse(null);
                if (actor != null) {
                    actorName = actor.getEmail();
                }
            }
            PaymentStatusLogDTO dto = new PaymentStatusLogDTO(
                    log.getStatusValue() != null ? log.getStatusValue().name() : "UNKNOWN",
                    actorName,
                    log.getStatusTimestamp());
            dtos.add(dto);
        }

        return dtos;
    }

    /**
     * Fetches detailed payment information by payment ID
     * 
     * @param paymentId Payment ID
     * @return PaymentDetailDTO with all related information
     */
    public PaymentDetailDTO getPaymentDetailById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null) {
            return null;
        }

        Invoice invoice = invoiceRepository.findById(payment.getInvoiceId()).orElse(null);
        if (invoice == null) {
            return null;
        }

        Renter renter = renterRepository.findById(invoice.getRenterId()).orElse(null);
        FleetOwner owner = fleetOwnerRepository.findById(invoice.getFleetOwnerId()).orElse(null);

        // Get renter email from User entity
        String renterEmail = "Unknown";
        if (renter != null && renter.getUserId() != null) {
            User renterUser = userRepository.findById(renter.getUserId()).orElse(null);
            if (renterUser != null) {
                renterEmail = renterUser.getEmail();
            }
        }

        PaymentDetailDTO dto = new PaymentDetailDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setRenterName(renter != null ? renter.getFullName() : "Unknown");
        dto.setRenterEmail(renterEmail);
        dto.setRenterId(renter != null ? renter.getRenterId() : null);
        dto.setOwnerBusinessName(owner != null ? owner.getBusinessName() : "Unknown");
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : "N/A");
        dto.setPaymentStatus(payment.getPaymentStatus() != null ? payment.getPaymentStatus().name() : "PENDING");
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setTransactionReference(payment.getTransactionReference());
        dto.setVerificationProofUrl(payment.getVerificationProofUrl());

        // Invoice details
        dto.setInvoiceId(invoice.getInvoiceId());
        dto.setIssueDate(invoice.getIssueDate());
        dto.setDueDate(invoice.getDueDate());
        dto.setInvoiceStatus(invoice.getStatus() != null ? invoice.getStatus().name() : "UNKNOWN");
        dto.setInvoiceRemarks(invoice.getRemarks());
        dto.setBookingId(invoice.getBookingId());

        return dto;
    }

    /**
     * Processes a cash payment for a booking.
     * Creates a payment record with PENDING status and CASH method.
     *
     * @param bookingId The booking ID
     * @return The created Payment entity
     */
    @org.springframework.transaction.annotation.Transactional
    public Payment processCashPayment(Long bookingId) {
        // 1. Find Invoice
        List<Invoice> invoices = invoiceRepository.findByBookingId(bookingId);
        if (invoices.isEmpty()) {
            throw new IllegalArgumentException("No invoice found for booking ID: " + bookingId);
        }
        // Assuming the latest invoice is the valid one
        Invoice invoice = invoices.get(0);

        // 2. Check if payment already exists
        List<Payment> existingPayments = paymentRepository.findByInvoiceId(invoice.getInvoiceId());
        if (!existingPayments.isEmpty()) {
            // Check if any is successful or pending
            for (Payment p : existingPayments) {
                if (p.getPaymentStatus() == Payment.PaymentStatus.VERIFIED ||
                        p.getPaymentStatus() == Payment.PaymentStatus.PENDING) {
                    // For now, allow re-submission if pending? Or maybe just return existing.
                    // Let's assume we return the existing one if it's pending cash.
                    if (p.getPaymentMethod() == Payment.PaymentMethod.CASH
                            && p.getPaymentStatus() == Payment.PaymentStatus.PENDING) {
                        return p;
                    }
                    // If it's another method or completed, we might want to throw error or handle
                    // gracefully.
                    // For simplicity, let's proceed to create a new one only if no valid payment
                    // exists.
                    // But strictly, we should probably throw exception if already paid.
                    if (p.getPaymentStatus() == Payment.PaymentStatus.VERIFIED) {
                        throw new IllegalStateException("Payment already completed for this booking.");
                    }
                }
            }
        }

        // 3. Create Payment
        Payment payment = new Payment();
        payment.setInvoiceId(invoice.getInvoiceId());
        payment.setAmount(invoice.getTotalAmount());
        payment.setPaymentMethod(Payment.PaymentMethod.CASH);
        payment.setPaymentStatus(Payment.PaymentStatus.PENDING);
        payment.setPaymentDate(java.time.LocalDateTime.now());
        payment.setTransactionReference("CASH-" + System.currentTimeMillis()); // Generate a reference

        payment = paymentRepository.save(payment);

        // 4. Log Status
        PaymentStatusLog log = new PaymentStatusLog();
        log.setPaymentId(payment.getPaymentId());
        log.setStatusValue(Payment.PaymentStatus.PENDING);
        log.setStatusTimestamp(java.time.LocalDateTime.now());
        log.setActorUserId(invoice.getRenterId()); // Renter ID as actor (approximate, ideally User ID)
        // Note: ActorUserId should be User ID, but here we have Renter ID.
        // We might need to fetch User from Renter if strict.
        // Given existing code uses Renter ID in some places or User ID, let's try to be
        // consistent.
        // In createBooking, we used renter.getUserId().
        // Here we only have invoice.getRenterId().
        // Let's fetch Renter to get UserId.
        Renter renter = renterRepository.findById(invoice.getRenterId()).orElse(null);
        if (renter != null) {
            log.setActorUserId(renter.getUserId());
        }
        log.setRemarks("Cash payment option selected by renter");
        paymentStatusLogRepository.save(log);

        return payment;
    }

    /**
     * Processes a bank transfer payment for a booking with receipt upload.
     *
     * @param bookingId The booking ID
     * @param receipt   The uploaded receipt file
     * @return The created Payment entity
     * @throws java.io.IOException if file storage fails
     */
    @org.springframework.transaction.annotation.Transactional
    public Payment processBankTransferPayment(Long bookingId, MultipartFile receipt) throws java.io.IOException {
        // 1. Find Invoice
        List<Invoice> invoices = invoiceRepository.findByBookingId(bookingId);
        if (invoices.isEmpty()) {
            throw new IllegalArgumentException("No invoice found for booking ID: " + bookingId);
        }
        Invoice invoice = invoices.get(0);

        // 2. Check if payment already exists
        List<Payment> existingPayments = paymentRepository.findByInvoiceId(invoice.getInvoiceId());
        for (Payment p : existingPayments) {
            if (p.getPaymentStatus() == Payment.PaymentStatus.VERIFIED) {
                throw new IllegalStateException("Payment already completed for this booking.");
            }
            // If pending bank transfer exists, update it with new receipt
            if (p.getPaymentMethod() == Payment.PaymentMethod.BANK_TRANSFER &&
                    p.getPaymentStatus() == Payment.PaymentStatus.PENDING) {
                String proofUrl = fileStorageService.storePaymentProof(receipt, bookingId);
                p.setVerificationProofUrl(proofUrl);
                p.setPaymentDate(java.time.LocalDateTime.now());
                p = paymentRepository.save(p);

                // Log receipt re-upload
                PaymentStatusLog log = new PaymentStatusLog();
                log.setPaymentId(p.getPaymentId());
                log.setStatusValue(Payment.PaymentStatus.PENDING);
                log.setStatusTimestamp(java.time.LocalDateTime.now());
                Renter renter = renterRepository.findById(invoice.getRenterId()).orElse(null);
                if (renter != null) {
                    log.setActorUserId(renter.getUserId());
                }
                log.setRemarks("Bank transfer receipt re-uploaded");
                paymentStatusLogRepository.save(log);

                return p;
            }
        }

        // 3. Store receipt file
        String proofUrl = fileStorageService.storePaymentProof(receipt, bookingId);

        // 4. Create Payment
        Payment payment = new Payment();
        payment.setInvoiceId(invoice.getInvoiceId());
        payment.setAmount(invoice.getTotalAmount());
        payment.setPaymentMethod(Payment.PaymentMethod.BANK_TRANSFER);
        payment.setPaymentStatus(Payment.PaymentStatus.PENDING);
        payment.setPaymentDate(java.time.LocalDateTime.now());
        payment.setTransactionReference("TRANSFER-" + System.currentTimeMillis());
        payment.setVerificationProofUrl(proofUrl);
        payment = paymentRepository.save(payment);

        // 5. Log Status
        PaymentStatusLog log = new PaymentStatusLog();
        log.setPaymentId(payment.getPaymentId());
        log.setStatusValue(Payment.PaymentStatus.PENDING);
        log.setStatusTimestamp(java.time.LocalDateTime.now());
        Renter renter = renterRepository.findById(invoice.getRenterId()).orElse(null);
        if (renter != null) {
            log.setActorUserId(renter.getUserId());
        }
        log.setRemarks("Bank transfer receipt submitted");
        paymentStatusLogRepository.save(log);

        return payment;
    }

    /**
     * Verifies a payment by updating its status to VERIFIED.
     *
     * @param paymentId        The payment ID to verify
     * @param verifiedByUserId The user ID of the person verifying
     * @return The updated Payment entity
     */
    @org.springframework.transaction.annotation.Transactional
    public Payment verifyPayment(Long paymentId, Long verifiedByUserId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        if (payment.getPaymentStatus() == Payment.PaymentStatus.VERIFIED) {
            throw new IllegalStateException("Payment is already verified.");
        }

        // 1. Update payment status
        payment.setPaymentStatus(Payment.PaymentStatus.VERIFIED);
        payment.setVerifiedByUserId(verifiedByUserId);
        payment = paymentRepository.save(payment);

        // 2. Log status change
        PaymentStatusLog log = new PaymentStatusLog();
        log.setPaymentId(payment.getPaymentId());
        log.setStatusValue(Payment.PaymentStatus.VERIFIED);
        log.setStatusTimestamp(java.time.LocalDateTime.now());
        log.setActorUserId(verifiedByUserId);
        log.setRemarks("Payment verified by owner");
        paymentStatusLogRepository.save(log);

        // 3. Update invoice status to PAID
        Invoice invoice = invoiceRepository.findById(payment.getInvoiceId()).orElse(null);
        if (invoice != null) {
            invoice.setStatus(Invoice.InvoiceStatus.PAID);
            invoiceRepository.save(invoice);
        }

        return payment;
    }

    /**
     * Gets the current payment for a booking.
     *
     * @param bookingId The booking ID
     * @return The Payment entity or null if no payment exists
     */
    public Payment getPaymentByBookingId(Long bookingId) {
        List<Invoice> invoices = invoiceRepository.findByBookingId(bookingId);
        if (invoices.isEmpty()) {
            return null;
        }
        Invoice invoice = invoices.get(0);
        List<Payment> payments = paymentRepository.findByInvoiceId(invoice.getInvoiceId());
        if (payments.isEmpty()) {
            return null;
        }
        return payments.get(0);
    }

    /**
     * Changes the payment method for a booking.
     * Only allowed when payment is PENDING.
     *
     * @param bookingId The booking ID
     * @param newMethod The new payment method
     * @return The new Payment entity
     */
    @org.springframework.transaction.annotation.Transactional
    public Payment changePaymentMethod(Long bookingId, Payment.PaymentMethod newMethod) {
        // 1. Get current payment
        Payment existingPayment = getPaymentByBookingId(bookingId);

        if (existingPayment == null) {
            throw new IllegalArgumentException("No payment found for booking ID: " + bookingId);
        }

        if (existingPayment.getPaymentStatus() == Payment.PaymentStatus.VERIFIED) {
            throw new IllegalStateException("Cannot change payment method for a verified payment.");
        }

        // 2. Get invoice
        List<Invoice> invoices = invoiceRepository.findByBookingId(bookingId);
        Invoice invoice = invoices.get(0);

        // 3. Get user ID for logging
        Renter renter = renterRepository.findById(invoice.getRenterId()).orElse(null);
        Long userId = renter != null ? renter.getUserId() : null;

        // 4. Log the change on old payment
        PaymentStatusLog changeLog = new PaymentStatusLog();
        changeLog.setPaymentId(existingPayment.getPaymentId());
        changeLog.setStatusValue(Payment.PaymentStatus.FAILED); // Mark old as failed/cancelled
        changeLog.setStatusTimestamp(java.time.LocalDateTime.now());
        changeLog.setActorUserId(userId);
        changeLog.setRemarks("Payment method changed from " + existingPayment.getPaymentMethod().name() +
                " to " + newMethod.name());
        paymentStatusLogRepository.save(changeLog);

        // 5. Update old payment status to FAILED
        existingPayment.setPaymentStatus(Payment.PaymentStatus.FAILED);
        paymentRepository.save(existingPayment);

        // 6. Create new payment with new method
        Payment newPayment = new Payment();
        newPayment.setInvoiceId(invoice.getInvoiceId());
        newPayment.setAmount(invoice.getTotalAmount());
        newPayment.setPaymentMethod(newMethod);
        newPayment.setPaymentStatus(Payment.PaymentStatus.PENDING);
        newPayment.setPaymentDate(java.time.LocalDateTime.now());
        newPayment.setTransactionReference(newMethod.name() + "-" + System.currentTimeMillis());
        newPayment = paymentRepository.save(newPayment);

        // 7. Log new payment creation
        PaymentStatusLog newLog = new PaymentStatusLog();
        newLog.setPaymentId(newPayment.getPaymentId());
        newLog.setStatusValue(Payment.PaymentStatus.PENDING);
        newLog.setStatusTimestamp(java.time.LocalDateTime.now());
        newLog.setActorUserId(userId);
        newLog.setRemarks(getMethodRemarks(newMethod));
        paymentStatusLogRepository.save(newLog);

        return newPayment;
    }

    /**
     * Helper to get descriptive remarks for a payment method.
     */
    private String getMethodRemarks(Payment.PaymentMethod method) {
        switch (method) {
            case CASH:
                return "Cash payment option selected by renter";
            case CREDIT_CARD:
                return "Credit card payment initiated";
            case BANK_TRANSFER:
                return "Bank transfer payment selected";
            case QR_PAYMENT:
                return "QR payment selected";
            default:
                return "Payment method selected";
        }
    }

    public List<PaymentDTO> getPaymentsByOwnerId(Long ownerId) {
        // Get all invoices for this owner
        List<Invoice> ownerInvoices = invoiceRepository.findByFleetOwnerId(ownerId);
        if (ownerInvoices.isEmpty()) {
            return new ArrayList<>();
        }

        // Collect Invoice IDs
        Set<Long> invoiceIds = ownerInvoices.stream()
                .map(Invoice::getInvoiceId)
                .collect(Collectors.toSet());

        // Bulk Fetch Payments
        List<Payment> allPayments = paymentRepository.findByInvoiceIdIn(invoiceIds);

        // Collect Renter IDs and FleetOwner IDs
        Set<Long> renterIds = ownerInvoices.stream()
                .map(Invoice::getRenterId)
                .collect(Collectors.toSet());

        Set<Long> fleetOwnerIds = ownerInvoices.stream()
                .map(Invoice::getFleetOwnerId)
                .collect(Collectors.toSet());

        // Bulk Fetch Renters
        Map<Long, Renter> renterMap = renterRepository.findAllById(renterIds).stream()
                .collect(Collectors.toMap(Renter::getRenterId, Function.identity()));

        // Bulk Fetch Fleet Owners
        Map<Long, FleetOwner> ownerMap = fleetOwnerRepository.findAllById(fleetOwnerIds).stream()
                .collect(Collectors.toMap(FleetOwner::getFleetOwnerId, Function.identity()));

        // Map Invoices to ID for quick lookup (if needed, but we iterate payments)
        Map<Long, Invoice> invoiceMap = ownerInvoices.stream()
                .collect(Collectors.toMap(Invoice::getInvoiceId, Function.identity()));

        List<PaymentDTO> paymentDTOs = new ArrayList<>();

        for (Payment payment : allPayments) {
            Invoice invoice = invoiceMap.get(payment.getInvoiceId());
            if (invoice != null) {
                Renter renter = renterMap.get(invoice.getRenterId());
                FleetOwner owner = ownerMap.get(invoice.getFleetOwnerId());

                PaymentDTO dto = new PaymentDTO(
                        payment.getPaymentId(),
                        invoice.getInvoiceNumber(),
                        renter != null ? renter.getFullName() : "Unknown",
                        owner != null ? owner.getBusinessName() : "Unknown",
                        payment.getAmount(),
                        payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : "N/A",
                        payment.getPaymentStatus() != null ? payment.getPaymentStatus().name() : "PENDING",
                        payment.getPaymentDate(),
                        payment.getTransactionReference());
                paymentDTOs.add(dto);
            }
        }

        return paymentDTOs;
    }

    /**
     * Calculates total revenue for a specific owner
     *
     * @param ownerId Owner ID
     * @return Total revenue
     */
    public java.math.BigDecimal getTotalRevenueByOwnerId(Long ownerId) {
        java.util.List<Payment.PaymentStatus> statuses = java.util.Arrays.asList(
                Payment.PaymentStatus.VERIFIED
        );
        java.math.BigDecimal revenue = paymentRepository.calculateTotalRevenueForOwner(ownerId, statuses);
        return revenue != null ? revenue : java.math.BigDecimal.ZERO;
    }
}
