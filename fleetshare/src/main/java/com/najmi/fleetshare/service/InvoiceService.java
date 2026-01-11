package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.BookingDTO;
import com.najmi.fleetshare.entity.Invoice;
import com.najmi.fleetshare.repository.InvoiceRepository;
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
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TemplateEngine templateEngine;

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
     * Build HTML content for invoice using Thymeleaf template
     */
    private String buildInvoiceHtml(Invoice invoice, BookingDTO booking) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        Context context = new Context();

        // Invoice details
        context.setVariable("invoiceNumber", invoice.getInvoiceNumber());
        context.setVariable("issueDate", invoice.getIssueDate() != null
                ? invoice.getIssueDate().format(dateFormatter)
                : "N/A");
        context.setVariable("dueDate", invoice.getDueDate() != null
                ? invoice.getDueDate().format(dateFormatter)
                : "N/A");
        context.setVariable("status", invoice.getStatus().name());
        context.setVariable("statusClass", invoice.getStatus().name().toLowerCase());
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
        BigDecimal amount = invoice.getTotalAmount() != null ? invoice.getTotalAmount() : BigDecimal.ZERO;
        context.setVariable("amount", amount);

        return templateEngine.process("pdf/invoice-template", context);
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
