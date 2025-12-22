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

    public Payment getPaymentByBookingId(Long bookingId) {
        List<Invoice> invoices = invoiceRepository.findByBookingId(bookingId);
        if (invoices.isEmpty()) {
            return null;
        }
        // Assuming one active invoice per booking for now, or take the latest
        Invoice invoice = invoices.get(0);
        List<Payment> payments = paymentRepository.findByInvoiceId(invoice.getInvoiceId());
        if (payments.isEmpty()) {
            return null;
        }
        // Assuming one payment record per invoice for now
        return payments.get(0);
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
     * Fetches all payments for a specific fleet owner
     * 
     * @param ownerId Fleet owner ID
     * @return List of PaymentDTO objects for owner's invoices
     */
    public List<PaymentDTO> getPaymentsByOwnerId(Long ownerId) {
        // Get all invoices for this owner
        List<Invoice> ownerInvoices = invoiceRepository.findByFleetOwnerId(ownerId);
        List<PaymentDTO> paymentDTOs = new ArrayList<>();

        for (Invoice invoice : ownerInvoices) {
            // Get payments for this invoice
            List<Payment> payments = paymentRepository.findByInvoiceId(invoice.getInvoiceId());

            for (Payment payment : payments) {
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
}
