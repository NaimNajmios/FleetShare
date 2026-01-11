package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.BookingDTO;
import com.najmi.fleetshare.entity.Invoice;
import com.najmi.fleetshare.repository.InvoiceRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private BookingService bookingService;

    /**
     * Get invoice by booking ID
     */
    public Optional<Invoice> getInvoiceByBookingId(Long bookingId) {
        return invoiceRepository.findFirstByBookingId(bookingId);
    }

    /**
     * Get invoice by ID
     */
    public Optional<Invoice> getInvoiceById(Long invoiceId) {
        return invoiceRepository.findById(invoiceId);
    }

    /**
     * Check if invoice can be downloaded (must be ISSUED, PAID, or OVERDUE)
     */
    public boolean canDownloadInvoice(Invoice invoice) {
        return invoice != null && (invoice.getStatus() == Invoice.InvoiceStatus.ISSUED ||
                invoice.getStatus() == Invoice.InvoiceStatus.PAID ||
                invoice.getStatus() == Invoice.InvoiceStatus.OVERDUE);
    }

    /**
     * Generate PDF invoice
     */
    public byte[] generateInvoicePdf(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        if (!canDownloadInvoice(invoice)) {
            throw new IllegalStateException("Invoice cannot be downloaded in current status");
        }

        BookingDTO booking = bookingService.getBookingDetails(invoice.getBookingId());

        String html = buildInvoiceHtml(invoice, booking);
        return generatePdfFromHtml(html);
    }

    /**
     * Build HTML content for invoice
     */
    private String buildInvoiceHtml(Invoice invoice, BookingDTO booking) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        String vehicleInfo = booking != null
                ? booking.getVehicleBrand() + " " + booking.getVehicleModel() + " ("
                        + booking.getVehicleRegistrationNo() + ")"
                : "N/A";

        String renterName = booking != null ? booking.getRenterName() : "N/A";
        String ownerName = booking != null ? booking.getOwnerBusinessName() : "N/A";

        String rentalPeriod = booking != null
                ? booking.getStartDate().format(dateFormatter) + " - " + booking.getEndDate().format(dateFormatter)
                : "N/A";

        BigDecimal amount = invoice.getTotalAmount() != null ? invoice.getTotalAmount() : BigDecimal.ZERO;

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; padding: 40px; color: #333; }
                        .header { text-align: center; margin-bottom: 30px; }
                        .header h1 { color: #4B49AC; margin: 0; }
                        .header p { color: #666; margin: 5px 0; }
                        .invoice-info { display: flex; justify-content: space-between; margin-bottom: 30px; }
                        .info-block { width: 45%; }
                        .info-block h3 { color: #4B49AC; border-bottom: 2px solid #4B49AC; padding-bottom: 5px; }
                        table { width: 100%; border-collapse: collapse; margin: 20px 0; }
                        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
                        th { background-color: #4B49AC; color: white; }
                        .total-row { font-weight: bold; font-size: 1.2em; }
                        .total-row td { border-top: 2px solid #333; }
                        .status { padding: 5px 15px; border-radius: 20px; font-weight: bold; display: inline-block; }
                        .status-issued { background: #fff3cd; color: #856404; }
                        .status-paid { background: #d4edda; color: #155724; }
                        .footer { text-align: center; margin-top: 40px; color: #666; font-size: 0.9em; }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h1>INVOICE</h1>
                        <p>FleetShare Vehicle Rental Platform</p>
                    </div>

                    <div class="invoice-info">
                        <div class="info-block">
                            <h3>Invoice Details</h3>
                            <p><strong>Invoice No:</strong> %s</p>
                            <p><strong>Issue Date:</strong> %s</p>
                            <p><strong>Due Date:</strong> %s</p>
                            <p><strong>Status:</strong> <span class="status status-%s">%s</span></p>
                        </div>
                        <div class="info-block">
                            <h3>Bill To</h3>
                            <p><strong>%s</strong></p>
                            <p>Booking #%s</p>
                        </div>
                    </div>

                    <table>
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
                                <td colspan="3">Total</td>
                                <td style="text-align: right;">RM %.2f</td>
                            </tr>
                        </tbody>
                    </table>

                    <div class="footer">
                        <p>Fleet Owner: %s</p>
                        <p>Thank you for using FleetShare!</p>
                    </div>
                </body>
                </html>
                """.formatted(
                invoice.getInvoiceNumber(),
                invoice.getIssueDate() != null ? invoice.getIssueDate().format(dateFormatter) : "N/A",
                invoice.getDueDate() != null ? invoice.getDueDate().format(dateFormatter) : "N/A",
                invoice.getStatus().name().toLowerCase(),
                invoice.getStatus().name(),
                renterName,
                invoice.getBookingId(),
                vehicleInfo,
                rentalPeriod,
                amount,
                amount,
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
