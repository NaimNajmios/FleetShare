package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.PaymentDTO;
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
}
