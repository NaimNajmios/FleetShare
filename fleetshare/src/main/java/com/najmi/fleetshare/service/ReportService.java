package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.*;
import com.najmi.fleetshare.entity.Vehicle;
import com.najmi.fleetshare.repository.*;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating reports in PDF and CSV formats
 */
@Service
public class ReportService {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private VehicleManagementService vehicleManagementService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private MaintenanceService maintenanceService;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private TemplateEngine templateEngine;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    /**
     * Generate report data based on request parameters
     */
    public ReportResponse<Map<String, Object>> generateReportData(ReportRequest request) {
        return switch (request.getCategory()) {
            case "booking" -> generateBookingReport(request);
            case "vehicle" -> generateVehicleReport(request);
            case "payment" -> generatePaymentReport(request);
            case "maintenance" -> generateMaintenanceReport(request);
            case "user" -> generateUserReport(request);
            default -> throw new IllegalArgumentException("Unknown report category: " + request.getCategory());
        };
    }

    /**
     * Generate PDF report
     */
    public byte[] generatePdfReport(ReportRequest request) {
        ReportResponse<Map<String, Object>> reportData = generateReportData(request);
        String html = buildReportHtml(reportData, request);
        return generatePdfFromHtml(html);
    }

    /**
     * Generate CSV report
     */
    public byte[] generateCsvReport(ReportRequest request) {
        ReportResponse<Map<String, Object>> reportData = generateReportData(request);
        return buildCsv(reportData);
    }

    // ==================== BOOKING REPORTS ====================

    private ReportResponse<Map<String, Object>> generateBookingReport(ReportRequest request) {
        List<BookingDTO> bookings = getFilteredBookings(request);

        return switch (request.getReportType()) {
            case "monthly-revenue" -> generateMonthlyRevenueReport(bookings, request);
            case "utilization-rate" -> generateUtilizationReport(request);
            case "booking-summary" -> generateBookingSummaryReport(bookings, request);
            default -> generateBookingSummaryReport(bookings, request);
        };
    }

    private ReportResponse<Map<String, Object>> generateMonthlyRevenueReport(List<BookingDTO> bookings,
            ReportRequest request) {
        // Group by month and calculate revenue
        Map<String, BigDecimal> revenueByMonth = new LinkedHashMap<>();
        Map<String, Long> countByMonth = new LinkedHashMap<>();

        for (BookingDTO booking : bookings) {
            if (booking.getCreatedAt() != null) {
                String month = booking.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM yyyy"));
                BigDecimal amount = booking.getTotalCost() != null ? booking.getTotalCost() : BigDecimal.ZERO;
                revenueByMonth.merge(month, amount, BigDecimal::add);
                countByMonth.merge(month, 1L, Long::sum);
            }
        }

        List<Map<String, Object>> data = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        long totalCount = 0;

        for (Map.Entry<String, BigDecimal> entry : revenueByMonth.entrySet()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("Month", entry.getKey());
            row.put("Bookings", countByMonth.get(entry.getKey()));
            row.put("Revenue", "RM " + entry.getValue().setScale(2, RoundingMode.HALF_UP));
            data.add(row);
            totalRevenue = totalRevenue.add(entry.getValue());
            totalCount += countByMonth.get(entry.getKey());
        }

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("Total Bookings", totalCount);
        summary.put("Total Revenue", "RM " + totalRevenue.setScale(2, RoundingMode.HALF_UP));

        return buildResponse("Monthly Revenue Report", request,
                Arrays.asList("Month", "Bookings", "Revenue"), data, summary);
    }

    private ReportResponse<Map<String, Object>> generateUtilizationReport(ReportRequest request) {
        List<VehicleDTO> vehicles;
        if (request.isAdmin()) {
            vehicles = vehicleManagementService.getAllVehicles();
            if (request.getOwnerId() != null) {
                vehicles = vehicles.stream()
                        .filter(v -> request.getOwnerId().equals(v.getFleetOwnerId()))
                        .collect(Collectors.toList());
            }
        } else {
            vehicles = vehicleManagementService.getVehiclesByOwnerId(request.getRequesterId());
        }

        List<BookingDTO> allBookings = request.isAdmin()
                ? bookingService.getAllBookings()
                : bookingService.getBookingsByOwnerId(request.getRequesterId());

        LocalDate start = request.getEffectiveStartDate();
        LocalDate end = request.getEffectiveEndDate();
        long totalDays = ChronoUnit.DAYS.between(start, end) + 1;

        List<Map<String, Object>> data = new ArrayList<>();
        long totalRentedDays = 0;

        for (VehicleDTO vehicle : vehicles) {
            long rentedDays = allBookings.stream()
                    .filter(b -> vehicle.getVehicleId().equals(b.getVehicleId()))
                    .filter(b -> "COMPLETED".equals(b.getStatus()) || "ACTIVE".equals(b.getStatus()))
                    .filter(b -> b.getStartDate() != null && b.getEndDate() != null)
                    .mapToLong(b -> {
                        LocalDate bStart = b.getStartDate().toLocalDate().isBefore(start) ? start
                                : b.getStartDate().toLocalDate();
                        LocalDate bEnd = b.getEndDate().toLocalDate().isAfter(end) ? end : b.getEndDate().toLocalDate();
                        return Math.max(0, ChronoUnit.DAYS.between(bStart, bEnd) + 1);
                    })
                    .sum();

            double utilization = totalDays > 0 ? (rentedDays * 100.0 / totalDays) : 0;

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("Vehicle", vehicle.getBrand() + " " + vehicle.getModel());
            row.put("Registration", vehicle.getRegistrationNo());
            row.put("Days Rented", rentedDays);
            row.put("Utilization", String.format("%.1f%%", utilization));
            data.add(row);

            totalRentedDays += rentedDays;
        }

        double avgUtilization = vehicles.size() > 0 && totalDays > 0
                ? (totalRentedDays * 100.0 / (vehicles.size() * totalDays))
                : 0;

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("Total Vehicles", vehicles.size());
        summary.put("Period Days", totalDays);
        summary.put("Avg Utilization", String.format("%.1f%%", avgUtilization));

        return buildResponse("Fleet Utilization Report", request,
                Arrays.asList("Vehicle", "Registration", "Days Rented", "Utilization"), data, summary);
    }

    private ReportResponse<Map<String, Object>> generateBookingSummaryReport(List<BookingDTO> bookings,
            ReportRequest request) {
        List<Map<String, Object>> data = bookings.stream()
                .sorted(Comparator.comparing(BookingDTO::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(b -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("ID", b.getBookingId());
                    row.put("Vehicle", b.getVehicleBrand() + " " + b.getVehicleModel());
                    row.put("Renter", b.getRenterName());
                    row.put("Period", formatDateTimeRange(b.getStartDate(), b.getEndDate()));
                    row.put("Status", b.getStatus());
                    row.put("Amount",
                            b.getTotalCost() != null ? "RM " + b.getTotalCost().setScale(2, RoundingMode.HALF_UP)
                                    : "-");
                    return row;
                })
                .collect(Collectors.toList());

        BigDecimal totalRevenue = bookings.stream()
                .filter(b -> b.getTotalCost() != null)
                .map(BookingDTO::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("Total Bookings", bookings.size());
        summary.put("Total Revenue", "RM " + totalRevenue.setScale(2, RoundingMode.HALF_UP));

        return buildResponse("Booking Summary Report", request,
                Arrays.asList("ID", "Vehicle", "Renter", "Period", "Status", "Amount"), data, summary);
    }

    // ==================== VEHICLE REPORTS ====================

    private ReportResponse<Map<String, Object>> generateVehicleReport(ReportRequest request) {
        return switch (request.getReportType()) {
            case "vehicle-performance" -> generateVehiclePerformanceReport(request);
            case "fleet-status" -> generateFleetStatusReport(request);
            case "maintenance-due" -> generateMaintenanceDueReport(request);
            default -> generateFleetStatusReport(request);
        };
    }

    private ReportResponse<Map<String, Object>> generateVehiclePerformanceReport(ReportRequest request) {
        List<VehicleDTO> vehicles = getFilteredVehicles(request);
        List<BookingDTO> allBookings = getFilteredBookings(request);

        List<Map<String, Object>> data = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (VehicleDTO vehicle : vehicles) {
            List<BookingDTO> vehicleBookings = allBookings.stream()
                    .filter(b -> vehicle.getVehicleId().equals(b.getVehicleId()))
                    .collect(Collectors.toList());

            BigDecimal revenue = vehicleBookings.stream()
                    .filter(b -> b.getTotalCost() != null)
                    .map(BookingDTO::getTotalCost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("Vehicle", vehicle.getBrand() + " " + vehicle.getModel());
            row.put("Registration", vehicle.getRegistrationNo());
            row.put("Bookings", vehicleBookings.size());
            row.put("Revenue", "RM " + revenue.setScale(2, RoundingMode.HALF_UP));
            row.put("Status", vehicle.getStatus());
            data.add(row);

            totalRevenue = totalRevenue.add(revenue);
        }

        // Sort by revenue descending
        data.sort((a, b) -> {
            String revA = ((String) a.get("Revenue")).replace("RM ", "").replace(",", "");
            String revB = ((String) b.get("Revenue")).replace("RM ", "").replace(",", "");
            return new BigDecimal(revB).compareTo(new BigDecimal(revA));
        });

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("Total Vehicles", vehicles.size());
        summary.put("Total Revenue", "RM " + totalRevenue.setScale(2, RoundingMode.HALF_UP));

        return buildResponse("Vehicle Performance Report", request,
                Arrays.asList("Vehicle", "Registration", "Bookings", "Revenue", "Status"), data, summary);
    }

    private ReportResponse<Map<String, Object>> generateFleetStatusReport(ReportRequest request) {
        List<VehicleDTO> vehicles = getFilteredVehicles(request);

        List<Map<String, Object>> data = vehicles.stream()
                .map(v -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("Vehicle", v.getBrand() + " " + v.getModel());
                    row.put("Registration", v.getRegistrationNo());
                    row.put("Year", v.getManufacturingYear());
                    row.put("Status", v.getStatus());
                    row.put("Daily Rate",
                            v.getRatePerDay() != null ? "RM " + v.getRatePerDay().setScale(2, RoundingMode.HALF_UP)
                                    : "-");
                    return row;
                })
                .collect(Collectors.toList());

        Map<String, Long> statusCounts = vehicles.stream()
                .collect(Collectors.groupingBy(VehicleDTO::getStatus, Collectors.counting()));

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("Total Vehicles", vehicles.size());
        statusCounts.forEach((k, v) -> summary.put(k, v));

        return buildResponse("Fleet Status Report", request,
                Arrays.asList("Vehicle", "Registration", "Year", "Status", "Daily Rate"), data, summary);
    }

    private ReportResponse<Map<String, Object>> generateMaintenanceDueReport(ReportRequest request) {
        List<MaintenanceDTO> maintenance;
        if (request.isAdmin()) {
            maintenance = maintenanceService.getAllMaintenance();
        } else {
            maintenance = maintenanceService.getMaintenanceByOwnerId(request.getRequesterId());
        }

        // Filter for scheduled/pending maintenance
        List<MaintenanceDTO> due = maintenance.stream()
                .filter(m -> "SCHEDULED".equals(m.getStatus()) || "PENDING".equals(m.getStatus()))
                .sorted(Comparator.comparing(MaintenanceDTO::getScheduledDate,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        List<Map<String, Object>> data = due.stream()
                .map(m -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("Vehicle", m.getVehicleBrand() + " " + m.getVehicleModel());
                    row.put("Registration", m.getVehicleRegistrationNo());
                    row.put("Type",
                            m.getDescription() != null
                                    ? m.getDescription().substring(0, Math.min(30, m.getDescription().length()))
                                    : "-");
                    row.put("Scheduled Date",
                            m.getScheduledDate() != null ? m.getScheduledDate().format(DATE_FORMATTER) : "-");
                    row.put("Est. Cost",
                            m.getEstimatedCost() != null
                                    ? "RM " + m.getEstimatedCost().setScale(2, RoundingMode.HALF_UP)
                                    : "-");
                    return row;
                })
                .collect(Collectors.toList());

        BigDecimal totalCost = due.stream()
                .filter(m -> m.getEstimatedCost() != null)
                .map(MaintenanceDTO::getEstimatedCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("Pending Services", due.size());
        summary.put("Estimated Total", "RM " + totalCost.setScale(2, RoundingMode.HALF_UP));

        return buildResponse("Maintenance Due Report", request,
                Arrays.asList("Vehicle", "Registration", "Type", "Scheduled Date", "Est. Cost"), data, summary);
    }

    // ==================== PAYMENT REPORTS ====================

    private ReportResponse<Map<String, Object>> generatePaymentReport(ReportRequest request) {
        return switch (request.getReportType()) {
            case "payment-summary" -> generatePaymentSummaryReport(request);
            case "outstanding-payments" -> generateOutstandingPaymentsReport(request);
            case "revenue-analysis" -> generateRevenueAnalysisReport(request);
            default -> generatePaymentSummaryReport(request);
        };
    }

    private ReportResponse<Map<String, Object>> generatePaymentSummaryReport(ReportRequest request) {
        List<PaymentDTO> payments = getFilteredPayments(request);

        List<Map<String, Object>> data = payments.stream()
                .sorted(Comparator.comparing(PaymentDTO::getPaymentDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(p -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("Payment ID", p.getPaymentId());
                    row.put("Date", p.getPaymentDate() != null ? p.getPaymentDate().format(DATE_FORMATTER) : "-");
                    row.put("Amount",
                            p.getAmount() != null ? "RM " + p.getAmount().setScale(2, RoundingMode.HALF_UP) : "-");
                    row.put("Method", p.getPaymentMethod());
                    row.put("Status", p.getPaymentStatus());
                    return row;
                })
                .collect(Collectors.toList());

        BigDecimal totalAmount = payments.stream()
                .filter(p -> p.getAmount() != null)
                .map(PaymentDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("Total Payments", payments.size());
        summary.put("Total Amount", "RM " + totalAmount.setScale(2, RoundingMode.HALF_UP));

        return buildResponse("Payment Summary Report", request,
                Arrays.asList("Payment ID", "Date", "Amount", "Method", "Status"), data, summary);
    }

    private ReportResponse<Map<String, Object>> generateOutstandingPaymentsReport(ReportRequest request) {
        List<PaymentDTO> payments = getFilteredPayments(request);

        List<PaymentDTO> pending = payments.stream()
                .filter(p -> "PENDING".equals(p.getPaymentStatus()))
                .sorted(Comparator.comparing(PaymentDTO::getPaymentDate,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        List<Map<String, Object>> data = pending.stream()
                .map(p -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("Payment ID", p.getPaymentId());
                    row.put("Invoice", p.getInvoiceNumber() != null ? p.getInvoiceNumber() : "-");
                    row.put("Amount Due",
                            p.getAmount() != null ? "RM " + p.getAmount().setScale(2, RoundingMode.HALF_UP) : "-");
                    row.put("Created", p.getPaymentDate() != null ? p.getPaymentDate().format(DATE_FORMATTER) : "-");
                    return row;
                })
                .collect(Collectors.toList());

        BigDecimal totalPending = pending.stream()
                .filter(p -> p.getAmount() != null)
                .map(PaymentDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("Pending Payments", pending.size());
        summary.put("Total Outstanding", "RM " + totalPending.setScale(2, RoundingMode.HALF_UP));

        return buildResponse("Outstanding Payments Report", request,
                Arrays.asList("Payment ID", "Invoice", "Amount Due", "Created"), data, summary);
    }

    private ReportResponse<Map<String, Object>> generateRevenueAnalysisReport(ReportRequest request) {
        List<PaymentDTO> payments = getFilteredPayments(request).stream()
                .filter(p -> "VERIFIED".equals(p.getPaymentStatus()) || "COMPLETED".equals(p.getPaymentStatus())
                        || "PAID".equals(p.getPaymentStatus()))
                .collect(Collectors.toList());

        // Group by payment method
        Map<String, BigDecimal> revenueByMethod = new LinkedHashMap<>();
        Map<String, Long> countByMethod = new LinkedHashMap<>();

        for (PaymentDTO payment : payments) {
            String method = payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "Other";
            BigDecimal amount = payment.getAmount() != null ? payment.getAmount() : BigDecimal.ZERO;
            revenueByMethod.merge(method, amount, BigDecimal::add);
            countByMethod.merge(method, 1L, Long::sum);
        }

        List<Map<String, Object>> data = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<String, BigDecimal> entry : revenueByMethod.entrySet()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("Payment Method", entry.getKey());
            row.put("Transactions", countByMethod.get(entry.getKey()));
            row.put("Revenue", "RM " + entry.getValue().setScale(2, RoundingMode.HALF_UP));
            data.add(row);
            total = total.add(entry.getValue());
        }

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("Total Transactions", payments.size());
        summary.put("Total Revenue", "RM " + total.setScale(2, RoundingMode.HALF_UP));

        return buildResponse("Revenue Analysis Report", request,
                Arrays.asList("Payment Method", "Transactions", "Revenue"), data, summary);
    }

    // ==================== MAINTENANCE REPORTS ====================

    private ReportResponse<Map<String, Object>> generateMaintenanceReport(ReportRequest request) {
        return switch (request.getReportType()) {
            case "maintenance-history" -> generateMaintenanceHistoryReport(request);
            case "cost-analysis" -> generateMaintenanceCostReport(request);
            case "upcoming-maintenance" -> generateMaintenanceDueReport(request);
            default -> generateMaintenanceHistoryReport(request);
        };
    }

    private ReportResponse<Map<String, Object>> generateMaintenanceHistoryReport(ReportRequest request) {
        List<MaintenanceDTO> maintenance = getFilteredMaintenance(request);

        List<Map<String, Object>> data = maintenance.stream()
                .sorted(Comparator.comparing(MaintenanceDTO::getScheduledDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(m -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("Date", m.getScheduledDate() != null ? m.getScheduledDate().format(DATE_FORMATTER) : "-");
                    row.put("Vehicle", m.getVehicleBrand() + " " + m.getVehicleModel());
                    row.put("Type",
                            m.getDescription() != null
                                    ? m.getDescription().substring(0, Math.min(30, m.getDescription().length()))
                                    : "-");
                    row.put("Status", m.getStatus());
                    row.put("Cost",
                            m.getEstimatedCost() != null
                                    ? "RM " + m.getEstimatedCost().setScale(2, RoundingMode.HALF_UP)
                                    : "-");
                    return row;
                })
                .collect(Collectors.toList());

        BigDecimal totalCost = maintenance.stream()
                .filter(m -> m.getEstimatedCost() != null)
                .map(MaintenanceDTO::getEstimatedCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("Total Records", maintenance.size());
        summary.put("Total Cost", "RM " + totalCost.setScale(2, RoundingMode.HALF_UP));

        return buildResponse("Maintenance History Report", request,
                Arrays.asList("Date", "Vehicle", "Type", "Status", "Cost"), data, summary);
    }

    private ReportResponse<Map<String, Object>> generateMaintenanceCostReport(ReportRequest request) {
        List<MaintenanceDTO> maintenance = getFilteredMaintenance(request);
        List<VehicleDTO> vehicles = getFilteredVehicles(request);

        // Group costs by vehicle
        Map<Long, BigDecimal> costByVehicle = new HashMap<>();
        Map<Long, Long> countByVehicle = new HashMap<>();

        for (MaintenanceDTO m : maintenance) {
            BigDecimal cost = m.getEstimatedCost() != null ? m.getEstimatedCost() : BigDecimal.ZERO;
            costByVehicle.merge(m.getVehicleId(), cost, BigDecimal::add);
            countByVehicle.merge(m.getVehicleId(), 1L, Long::sum);
        }

        List<Map<String, Object>> data = new ArrayList<>();
        BigDecimal totalCost = BigDecimal.ZERO;

        for (VehicleDTO vehicle : vehicles) {
            BigDecimal cost = costByVehicle.getOrDefault(vehicle.getVehicleId(), BigDecimal.ZERO);
            long count = countByVehicle.getOrDefault(vehicle.getVehicleId(), 0L);
            BigDecimal avg = count > 0 ? cost.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("Vehicle", vehicle.getBrand() + " " + vehicle.getModel());
            row.put("Registration", vehicle.getRegistrationNo());
            row.put("Services", count);
            row.put("Total Cost", "RM " + cost.setScale(2, RoundingMode.HALF_UP));
            row.put("Avg per Service", "RM " + avg.setScale(2, RoundingMode.HALF_UP));
            data.add(row);

            totalCost = totalCost.add(cost);
        }

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("Total Vehicles", vehicles.size());
        summary.put("Total Maintenance Cost", "RM " + totalCost.setScale(2, RoundingMode.HALF_UP));

        return buildResponse("Maintenance Cost Analysis", request,
                Arrays.asList("Vehicle", "Registration", "Services", "Total Cost", "Avg per Service"), data, summary);
    }

    // ==================== USER REPORTS ====================

    private ReportResponse<Map<String, Object>> generateUserReport(ReportRequest request) {
        return switch (request.getReportType()) {
            case "user-activity" -> generateUserActivityReport(request);
            case "top-customers" -> generateTopCustomersReport(request);
            case "user-demographics" -> generateUserDemographicsReport(request);
            default -> generateUserActivityReport(request);
        };
    }

    private ReportResponse<Map<String, Object>> generateUserActivityReport(ReportRequest request) {
        List<BookingDTO> bookings = getFilteredBookings(request);

        // Group by renter
        Map<String, List<BookingDTO>> bookingsByRenter = bookings.stream()
                .filter(b -> b.getRenterName() != null)
                .collect(Collectors.groupingBy(BookingDTO::getRenterName));

        List<Map<String, Object>> data = new ArrayList<>();

        for (Map.Entry<String, List<BookingDTO>> entry : bookingsByRenter.entrySet()) {
            BigDecimal totalSpent = entry.getValue().stream()
                    .filter(b -> b.getTotalCost() != null)
                    .map(BookingDTO::getTotalCost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("Customer", entry.getKey());
            row.put("Bookings", entry.getValue().size());
            row.put("Total Spent", "RM " + totalSpent.setScale(2, RoundingMode.HALF_UP));
            data.add(row);
        }

        // Sort by bookings descending
        data.sort((a, b) -> Integer.compare((Integer) b.get("Bookings"), (Integer) a.get("Bookings")));

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("Total Customers", bookingsByRenter.size());
        summary.put("Total Bookings", bookings.size());

        return buildResponse("User Activity Report", request,
                Arrays.asList("Customer", "Bookings", "Total Spent"), data, summary);
    }

    private ReportResponse<Map<String, Object>> generateTopCustomersReport(ReportRequest request) {
        List<BookingDTO> bookings = getFilteredBookings(request);

        // Group by renter and calculate total
        Map<String, BigDecimal> revenueByRenter = new LinkedHashMap<>();
        Map<String, Long> countByRenter = new LinkedHashMap<>();

        for (BookingDTO booking : bookings) {
            if (booking.getRenterName() != null) {
                BigDecimal amount = booking.getTotalCost() != null ? booking.getTotalCost() : BigDecimal.ZERO;
                revenueByRenter.merge(booking.getRenterName(), amount, BigDecimal::add);
                countByRenter.merge(booking.getRenterName(), 1L, Long::sum);
            }
        }

        // Sort by revenue and take top 10
        List<Map.Entry<String, BigDecimal>> sorted = revenueByRenter.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());

        List<Map<String, Object>> data = new ArrayList<>();
        int rank = 1;
        for (Map.Entry<String, BigDecimal> entry : sorted) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("Rank", rank++);
            row.put("Customer", entry.getKey());
            row.put("Bookings", countByRenter.get(entry.getKey()));
            row.put("Revenue", "RM " + entry.getValue().setScale(2, RoundingMode.HALF_UP));
            data.add(row);
        }

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("Showing Top", Math.min(10, sorted.size()));

        return buildResponse("Top Customers Report", request,
                Arrays.asList("Rank", "Customer", "Bookings", "Revenue"), data, summary);
    }

    private ReportResponse<Map<String, Object>> generateUserDemographicsReport(ReportRequest request) {
        List<RenterDTO> renters = userManagementService.getAllRenters();

        // Simple demographics - just count renters (could be expanded with address
        // data)
        List<Map<String, Object>> data = new ArrayList<>();

        Map<String, Object> row = new LinkedHashMap<>();
        row.put("Category", "Total Renters");
        row.put("Count", renters.size());
        row.put("Percentage", "100%");
        data.add(row);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("Total Renters", renters.size());

        return buildResponse("User Demographics Report", request,
                Arrays.asList("Category", "Count", "Percentage"), data, summary);
    }

    // ==================== HELPER METHODS ====================

    private List<BookingDTO> getFilteredBookings(ReportRequest request) {
        List<BookingDTO> bookings;
        if (request.isAdmin()) {
            bookings = bookingService.getAllBookings();
            // Note: BookingDTO doesn't have ownerId, skip owner filtering for admin
            // Owner-specific bookings would require joining with vehicle data
        } else {
            bookings = bookingService.getBookingsByOwnerId(request.getRequesterId());
        }

        LocalDate start = request.getEffectiveStartDate();
        LocalDate end = request.getEffectiveEndDate();

        // Filter by date
        bookings = bookings.stream()
                .filter(b -> b.getCreatedAt() != null)
                .filter(b -> {
                    LocalDate created = b.getCreatedAt().toLocalDate();
                    return !created.isBefore(start) && !created.isAfter(end);
                })
                .collect(Collectors.toList());

        // Filter by status
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            bookings = bookings.stream()
                    .filter(b -> request.getStatus().equalsIgnoreCase(b.getStatus()))
                    .collect(Collectors.toList());
        }

        // Filter by vehicle
        if (request.getVehicleId() != null) {
            bookings = bookings.stream()
                    .filter(b -> request.getVehicleId().equals(b.getVehicleId()))
                    .collect(Collectors.toList());
        }

        return bookings;
    }

    private List<VehicleDTO> getFilteredVehicles(ReportRequest request) {
        if (request.isAdmin()) {
            List<VehicleDTO> vehicles = vehicleManagementService.getAllVehicles();
            if (request.getOwnerId() != null) {
                vehicles = vehicles.stream()
                        .filter(v -> request.getOwnerId().equals(v.getFleetOwnerId()))
                        .collect(Collectors.toList());
            }
            return vehicles;
        } else {
            return vehicleManagementService.getVehiclesByOwnerId(request.getRequesterId());
        }
    }

    private List<PaymentDTO> getFilteredPayments(ReportRequest request) {
        List<PaymentDTO> payments;
        if (request.isAdmin()) {
            payments = paymentService.getAllPayments();
        } else {
            payments = paymentService.getPaymentsByOwnerId(request.getRequesterId());
        }

        LocalDate start = request.getEffectiveStartDate();
        LocalDate end = request.getEffectiveEndDate();

        return payments.stream()
                .filter(p -> p.getPaymentDate() != null)
                .filter(p -> {
                    LocalDate paymentDate = p.getPaymentDate().toLocalDate();
                    return !paymentDate.isBefore(start) && !paymentDate.isAfter(end);
                })
                .collect(Collectors.toList());
    }

    private List<MaintenanceDTO> getFilteredMaintenance(ReportRequest request) {
        List<MaintenanceDTO> maintenance;
        if (request.isAdmin()) {
            maintenance = maintenanceService.getAllMaintenance();
        } else {
            maintenance = maintenanceService.getMaintenanceByOwnerId(request.getRequesterId());
        }

        LocalDate start = request.getEffectiveStartDate();
        LocalDate end = request.getEffectiveEndDate();

        return maintenance.stream()
                .filter(m -> m.getScheduledDate() != null)
                .filter(m -> !m.getScheduledDate().isBefore(start) && !m.getScheduledDate().isAfter(end))
                .collect(Collectors.toList());
    }

    private ReportResponse<Map<String, Object>> buildResponse(String title, ReportRequest request,
            List<String> columns, List<Map<String, Object>> data, Map<String, Object> summary) {

        String period = request.getEffectiveStartDate().format(DATE_FORMATTER)
                + " - " + request.getEffectiveEndDate().format(DATE_FORMATTER);

        return new ReportResponse<Map<String, Object>>()
                .withTitle(title)
                .withGeneratedAt(LocalDateTime.now().format(DATETIME_FORMATTER))
                .withPeriod(period)
                .withColumns(columns)
                .withData(data)
                .withSummary(summary);
    }

    private String formatDateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null)
            return "-";
        return start.format(DATE_FORMATTER) + " - " + end.format(DATE_FORMATTER);
    }

    private String formatDateTimeRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null)
            return "-";
        return start.toLocalDate().format(DATE_FORMATTER) + " - " + end.toLocalDate().format(DATE_FORMATTER);
    }

    // ==================== PDF/CSV GENERATION ====================

    private String buildReportHtml(ReportResponse<Map<String, Object>> report, ReportRequest request) {
        Context context = new Context();
        context.setVariable("report", report);
        context.setVariable("request", request);
        return templateEngine.process("pdf/report-template", context);
    }

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

    private byte[] buildCsv(ReportResponse<Map<String, Object>> report) {
        StringBuilder csv = new StringBuilder();

        // Header
        csv.append(String.join(",", report.getColumns())).append("\n");

        // Data rows
        for (Map<String, Object> row : report.getData()) {
            List<String> values = report.getColumns().stream()
                    .map(col -> {
                        Object val = row.get(col);
                        String str = val != null ? val.toString() : "";
                        // Escape commas and quotes
                        if (str.contains(",") || str.contains("\"")) {
                            str = "\"" + str.replace("\"", "\"\"") + "\"";
                        }
                        return str;
                    })
                    .collect(Collectors.toList());
            csv.append(String.join(",", values)).append("\n");
        }

        return csv.toString().getBytes();
    }
}
