package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.BookingDTO;
import com.najmi.fleetshare.entity.Invoice;
import com.najmi.fleetshare.entity.Payment;
import com.najmi.fleetshare.repository.InvoiceRepository;
import com.najmi.fleetshare.repository.PaymentRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * Build HTML content for receipt
     */
    private String buildReceiptHtml(Payment payment, Invoice invoice, BookingDTO booking) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

        String vehicleInfo = booking != null
                ? booking.getVehicleBrand() + " " + booking.getVehicleModel() + " ("
                        + booking.getVehicleRegistrationNo() + ")"
                : "N/A";

        String renterName = booking != null ? booking.getRenterName() : "N/A";
        String ownerName = booking != null ? booking.getOwnerBusinessName() : "N/A";

        String rentalPeriod = booking != null
                ? booking.getStartDate().format(dateFormatter) + " - " + booking.getEndDate().format(dateFormatter)
                : "N/A";

        BigDecimal amount = payment.getAmount() != null ? payment.getAmount() : BigDecimal.ZERO;
        String paymentMethod = payment.getPaymentMethod() != null ? payment.getPaymentMethod().name().replace("_", " ")
                : "N/A";
        String transactionRef = payment.getTransactionReference() != null ? payment.getTransactionReference() : "N/A";
        String paymentDate = payment.getPaymentDate() != null ? payment.getPaymentDate().format(dateTimeFormatter)
                : "N/A";

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; padding: 40px; color: #333; font-size: 12px; }
                        .header { text-align: center; margin-bottom: 30px; background-color: #28a745; padding: 30px; color: white; }
                        .header h1 { margin: 0; font-size: 24px; }
                        .header p { margin: 5px 0; }
                        .success-icon { font-size: 36px; margin-bottom: 10px; }
                        .info-table { width: 100%%; margin-bottom: 20px; }
                        .info-table td { vertical-align: top; padding: 10px; }
                        .info-block h3 { color: #28a745; border-bottom: 2px solid #28a745; padding-bottom: 5px; margin-top: 0; }
                        .items-table { width: 100%%; border-collapse: collapse; margin: 20px 0; }
                        .items-table th, .items-table td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
                        .items-table th { background-color: #28a745; color: white; }
                        .total-row { font-weight: bold; font-size: 14px; background-color: #d4edda; }
                        .payment-details { background: #f8f9fa; padding: 15px; margin: 20px 0; }
                        .payment-details h3 { color: #28a745; margin-top: 0; }
                        .footer { text-align: center; margin-top: 40px; color: #666; font-size: 11px; }
                        .verified-badge { background: #28a745; color: white; padding: 3px 10px; font-weight: bold; }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <div class="success-icon">&#10003;</div>
                        <h1>PAYMENT RECEIPT</h1>
                        <p>FleetShare Vehicle Rental Platform</p>
                    </div>

                    <table class="info-table">
                        <tr>
                            <td style="width: 50%%;">
                                <div class="info-block">
                                    <h3>Receipt Details</h3>
                                    <p><strong>Invoice No:</strong> %s</p>
                                    <p><strong>Transaction Ref:</strong> %s</p>
                                    <p><strong>Payment Date:</strong> %s</p>
                                    <p><strong>Status:</strong> <span class="verified-badge">VERIFIED</span></p>
                                </div>
                            </td>
                            <td style="width: 50%%;">
                                <div class="info-block">
                                    <h3>Paid By</h3>
                                    <p><strong>%s</strong></p>
                                    <p>Booking #%s</p>
                                </div>
                            </td>
                        </tr>
                    </table>

                    <table class="items-table">
                        <thead>
                            <tr>
                                <th>Description</th>
                                <th>Vehicle</th>
                                <th>Rental Period</th>
                                <th style="text-align: right;">Amount</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>Vehicle Rental</td>
                                <td>%s</td>
                                <td>%s</td>
                                <td style="text-align: right;">RM %.2f</td>
                            </tr>
                            <tr class="total-row">
                                <td colspan="3">Amount Paid</td>
                                <td style="text-align: right;">RM %.2f</td>
                            </tr>
                        </tbody>
                    </table>

                    <div class="payment-details">
                        <h3>Payment Information</h3>
                        <p><strong>Payment Method:</strong> %s</p>
                        <p><strong>Transaction Reference:</strong> %s</p>
                    </div>

                    <div class="footer">
                        <p>Received by: %s</p>
                        <p>This is a computer-generated receipt and does not require a signature.</p>
                        <p>Thank you for using FleetShare!</p>
                    </div>
                </body>
                </html>
                """
                .formatted(
                        invoice.getInvoiceNumber(),
                        transactionRef,
                        paymentDate,
                        renterName,
                        invoice.getBookingId(),
                        vehicleInfo,
                        rentalPeriod,
                        amount,
                        amount,
                        paymentMethod,
                        transactionRef,
                        ownerName);
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
