package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.BookingDTO;
import com.najmi.fleetshare.entity.Invoice;
import com.najmi.fleetshare.entity.Payment;
import com.najmi.fleetshare.repository.InvoiceRepository;
import com.najmi.fleetshare.repository.PaymentRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class ReceiptService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * Check if receipt can be generated (payment must be VERIFIED)
     */
    public boolean canGenerateReceipt(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .map(p -> p.getPaymentStatus() == Payment.PaymentStatus.VERIFIED)
                .orElse(false);
    }

    /**
     * Get payment by ID
     */
    public Optional<Payment> getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId);
    }

    /**
     * Get payment by invoice ID
     */
    public Optional<Payment> getPaymentByInvoiceId(Long invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId).stream().findFirst();
    }

    /**
     * Generate PDF receipt for a verified payment
     */
    public byte[] generateReceiptPdf(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (payment.getPaymentStatus() != Payment.PaymentStatus.VERIFIED) {
            throw new IllegalStateException("Receipt can only be generated for verified payments");
        }

        Invoice invoice = invoiceRepository.findById(payment.getInvoiceId())
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        BookingDTO booking = bookingService.getBookingDetails(invoice.getBookingId());

        String html = buildReceiptHtml(payment, invoice, booking);
        return generatePdfFromHtml(html);
    }

    /**
     * Build HTML content for receipt using Thymeleaf template
     */
    private String buildReceiptHtml(Payment payment, Invoice invoice, BookingDTO booking) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

        Context context = new Context();

        // Receipt/Payment details
        context.setVariable("invoiceNumber", invoice.getInvoiceNumber());
        context.setVariable("transactionRef", payment.getTransactionReference() != null
                ? payment.getTransactionReference()
                : "N/A");
        context.setVariable("paymentDate", payment.getPaymentDate() != null
                ? payment.getPaymentDate().format(dateTimeFormatter)
                : "N/A");
        context.setVariable("paymentMethod", payment.getPaymentMethod() != null
                ? payment.getPaymentMethod().name().replace("_", " ")
                : "N/A");
        context.setVariable("bookingId", invoice.getBookingId());

        // Booking/Vehicle details
        if (booking != null) {
            context.setVariable("renterName", booking.getRenterName());
            context.setVariable("vehicleInfo", booking.getVehicleBrand() + " " + booking.getVehicleModel()
                    + " (" + booking.getVehicleRegistrationNo() + ")");
            context.setVariable("rentalPeriod", booking.getStartDate().format(dateFormatter)
                    + " - " + booking.getEndDate().format(dateFormatter));
            context.setVariable("ownerName", booking.getOwnerBusinessName());
        } else {
            context.setVariable("renterName", "N/A");
            context.setVariable("vehicleInfo", "N/A");
            context.setVariable("rentalPeriod", "N/A");
            context.setVariable("ownerName", "N/A");
        }

        // Amount
        BigDecimal amount = payment.getAmount() != null ? payment.getAmount() : BigDecimal.ZERO;
        context.setVariable("amount", amount);

        return templateEngine.process("pdf/receipt-template", context);
    }

    /**
     * Convert HTML to PDF
     */
    private byte[] generatePdfFromHtml(String html) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }
}
