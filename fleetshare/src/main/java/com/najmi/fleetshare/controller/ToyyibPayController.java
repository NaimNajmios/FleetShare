package com.najmi.fleetshare.controller;

import com.najmi.fleetshare.entity.FleetOwner;
import com.najmi.fleetshare.entity.Invoice;
import com.najmi.fleetshare.entity.Payment;
import com.najmi.fleetshare.entity.PaymentStatusLog;
import com.najmi.fleetshare.repository.FleetOwnerRepository;
import com.najmi.fleetshare.repository.InvoiceRepository;
import com.najmi.fleetshare.repository.PaymentRepository;
import com.najmi.fleetshare.repository.PaymentStatusLogRepository;
import com.najmi.fleetshare.service.ToyyibPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/api/payment/toyyibpay")
public class ToyyibPayController {

    private static final Logger logger = LoggerFactory.getLogger(ToyyibPayController.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private FleetOwnerRepository fleetOwnerRepository;

    @Autowired
    private PaymentStatusLogRepository paymentStatusLogRepository;

    @Autowired
    private ToyyibPayService toyyibPayService;

    /**
     * Callback endpoint - receives payment status from ToyyibPay servers (server-to-server POST).
     * This endpoint must be public and CSRF-exempt.
     *
     * ToyyibPay sends the following POST parameters:
     * - refno: Payment reference number
     * - status: 1=success, 2=pending, 3=fail
     * - reason: Reason for status
     * - billcode: The bill code
     * - order_id: External reference number
     * - amount: Payment amount
     * - transaction_time: Transaction datetime
     * - hash: MD5 verification hash
     */
    @PostMapping("/callback")
    @ResponseBody
    public String handleCallback(
            @RequestParam("refno") String refno,
            @RequestParam("status") String status,
            @RequestParam("reason") String reason,
            @RequestParam("billcode") String billcode,
            @RequestParam("order_id") String orderId,
            @RequestParam("amount") String amount,
            @RequestParam(value = "transaction_time", required = false) String transactionTime,
            @RequestParam(value = "hash", required = false) String hash) {

        logger.info("ToyyibPay callback received - billcode: {}, status: {}, refno: {}, order_id: {}, amount: {}",
                billcode, status, refno, orderId, amount);

        try {
            // 1. Find payment by bill code
            Optional<Payment> paymentOpt = paymentRepository.findByToyyibpayBillCode(billcode);
            if (paymentOpt.isEmpty()) {
                logger.error("Payment not found for billcode: {}", billcode);
                return "FAIL";
            }

            Payment payment = paymentOpt.get();

            // 2. Find the associated invoice to get the fleet owner
            Invoice invoice = invoiceRepository.findById(payment.getInvoiceId()).orElse(null);
            if (invoice == null) {
                logger.error("Invoice not found for payment: {}", payment.getPaymentId());
                return "FAIL";
            }

            FleetOwner owner = fleetOwnerRepository.findById(invoice.getFleetOwnerId()).orElse(null);
            if (owner == null) {
                logger.error("Fleet owner not found for invoice: {}", invoice.getInvoiceId());
                return "FAIL";
            }

            // 3. Validate hash if provided
            if (hash != null && !hash.isEmpty()) {
                boolean isValid = toyyibPayService.validateCallbackHash(
                        owner.getToyyibpaySecretKey(), status, orderId, refno, hash);
                if (!isValid) {
                    logger.error("Hash validation failed for billcode: {}", billcode);
                    return "FAIL";
                }
            }

            // 4. Prevent duplicate processing
            if (payment.getPaymentStatus() == Payment.PaymentStatus.VERIFIED) {
                logger.info("Payment already verified for billcode: {}", billcode);
                return "OK";
            }

            // 5. Update payment based on status
            payment.setGatewayRefNo(refno);

            if ("1".equals(status)) {
                // Success
                payment.setPaymentStatus(Payment.PaymentStatus.VERIFIED);
                payment.setTransactionReference("TP-" + refno);
                paymentRepository.save(payment);

                // Update invoice to PAID
                invoice.setStatus(Invoice.InvoiceStatus.PAID);
                invoiceRepository.save(invoice);

                // Log status
                logStatusChange(payment.getPaymentId(), Payment.PaymentStatus.VERIFIED,
                        null, "Payment verified via ToyyibPay. Ref: " + refno + ". Reason: " + reason);

                logger.info("Payment VERIFIED for billcode: {}, refno: {}", billcode, refno);

            } else if ("3".equals(status)) {
                // Failed
                payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
                paymentRepository.save(payment);

                logStatusChange(payment.getPaymentId(), Payment.PaymentStatus.FAILED,
                        null, "Payment failed via ToyyibPay. Ref: " + refno + ". Reason: " + reason);

                logger.info("Payment FAILED for billcode: {}, reason: {}", billcode, reason);

            } else {
                // Pending (status = 2) or other
                logger.info("Payment pending/other for billcode: {}, status: {}", billcode, status);

                logStatusChange(payment.getPaymentId(), Payment.PaymentStatus.PENDING,
                        null, "Payment pending via ToyyibPay. Status: " + status + ". Reason: " + reason);
            }

            return "OK";

        } catch (Exception e) {
            logger.error("Error processing ToyyibPay callback: {}", e.getMessage(), e);
            return "FAIL";
        }
    }

    /**
     * Return URL endpoint - handles renter redirect after payment on ToyyibPay.
     * 
     * ToyyibPay sends the following GET parameters:
     * - status_id: 1=success, 2=pending, 3=fail
     * - billcode: The bill code
     * - order_id: External reference number
     */
    @GetMapping("/return")
    public String handleReturn(
            @RequestParam(value = "status_id", required = false) String statusId,
            @RequestParam(value = "billcode", required = false) String billcode,
            @RequestParam(value = "order_id", required = false) String orderId,
            RedirectAttributes redirectAttributes) {

        logger.info("ToyyibPay return - status_id: {}, billcode: {}, order_id: {}", statusId, billcode, orderId);

        // Find payment and booking from bill code
        Long bookingId = null;
        if (billcode != null) {
            Optional<Payment> paymentOpt = paymentRepository.findByToyyibpayBillCode(billcode);
            if (paymentOpt.isPresent()) {
                Payment payment = paymentOpt.get();
                Invoice invoice = invoiceRepository.findById(payment.getInvoiceId()).orElse(null);
                if (invoice != null) {
                    bookingId = invoice.getBookingId();

                    // Fallback: Update payment status here in case callback couldn't reach the server
                    // (e.g., when running on localhost without ngrok)
                    if ("1".equals(statusId) && payment.getPaymentStatus() != Payment.PaymentStatus.VERIFIED) {
                        // Mark payment as VERIFIED
                        payment.setPaymentStatus(Payment.PaymentStatus.VERIFIED);
                        payment.setTransactionReference("TP-RETURN-" + (orderId != null ? orderId : billcode));
                        paymentRepository.save(payment);

                        // Mark invoice as PAID
                        invoice.setStatus(Invoice.InvoiceStatus.PAID);
                        invoiceRepository.save(invoice);

                        logStatusChange(payment.getPaymentId(), Payment.PaymentStatus.VERIFIED,
                                null, "Payment verified via ToyyibPay return. Order: " + orderId);

                        logger.info("Payment VERIFIED via return URL for billcode: {}", billcode);

                    } else if ("3".equals(statusId) && payment.getPaymentStatus() == Payment.PaymentStatus.PENDING) {
                        payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
                        paymentRepository.save(payment);

                        logStatusChange(payment.getPaymentId(), Payment.PaymentStatus.FAILED,
                                null, "Payment failed via ToyyibPay return. Order: " + orderId);

                        logger.info("Payment FAILED via return URL for billcode: {}", billcode);
                    }
                }
            }
        }

        // Set flash message based on status
        if ("1".equals(statusId)) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Payment completed successfully! Your booking has been confirmed.");
        } else if ("3".equals(statusId)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Payment was unsuccessful. Please try again or choose a different payment method.");
        } else {
            redirectAttributes.addFlashAttribute("warningMessage",
                    "Payment is being processed. You will be notified once confirmed.");
        }

        // Redirect to booking details or bookings list
        if (bookingId != null) {
            return "redirect:/renter/bookings/" + bookingId;
        }
        return "redirect:/renter/bookings";
    }

    /**
     * Helper to log payment status changes.
     */
    private void logStatusChange(Long paymentId, Payment.PaymentStatus status,
                                  Long actorUserId, String remarks) {
        PaymentStatusLog log = new PaymentStatusLog();
        log.setPaymentId(paymentId);
        log.setStatusValue(status);
        log.setStatusTimestamp(LocalDateTime.now());
        log.setActorUserId(actorUserId);
        log.setRemarks(remarks);
        paymentStatusLogRepository.save(log);
    }
}
