package com.najmi.fleetshare.controller;

import com.najmi.fleetshare.dto.BookingDTO;
import com.najmi.fleetshare.dto.BookingLogDTO;
import com.najmi.fleetshare.dto.MaintenanceDTO;
import com.najmi.fleetshare.dto.MaintenanceLogDTO;
import com.najmi.fleetshare.dto.SessionUser;
import com.najmi.fleetshare.dto.UserDetailDTO;
import com.najmi.fleetshare.dto.VehicleDTO;
import com.najmi.fleetshare.service.BookingService;
import com.najmi.fleetshare.service.MaintenanceService;
import com.najmi.fleetshare.service.PaymentService;
import com.najmi.fleetshare.service.UserManagementService;
import com.najmi.fleetshare.service.VehicleManagementService;
import com.najmi.fleetshare.util.SessionHelper;
import com.najmi.fleetshare.repository.AddressRepository;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private VehicleManagementService vehicleManagementService;

    @Autowired
    private MaintenanceService maintenanceService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AddressRepository addressRepository;

    @GetMapping("/")
    public String index() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user != null) {
            model.addAttribute("user", user);
            if (user.getAdminDetails() != null) {
                model.addAttribute("adminName", user.getAdminDetails().getFullName());
            }
        }

        try {
            // Platform-wide user statistics
            var allOwners = userManagementService.getAllFleetOwners();
            var allRenters = userManagementService.getAllRenters();
            int ownerCount = allOwners != null ? allOwners.size() : 0;
            int renterCount = allRenters != null ? allRenters.size() : 0;
            int totalUsers = ownerCount + renterCount;
            model.addAttribute("totalUsers", totalUsers);
            model.addAttribute("renterCount", renterCount);
            model.addAttribute("ownerCount", ownerCount);

            // Platform-wide vehicle statistics
            List<VehicleDTO> allVehicles = vehicleManagementService.getAllVehicles();
            int totalVehicles = allVehicles != null ? allVehicles.size() : 0;
            long availableVehicles = allVehicles != null
                    ? allVehicles.stream().filter(v -> "AVAILABLE".equals(v.getStatus())).count()
                    : 0;
            long rentedVehicles = allVehicles != null
                    ? allVehicles.stream().filter(v -> "RENTED".equals(v.getStatus())).count()
                    : 0;
            long maintenanceVehicles = allVehicles != null
                    ? allVehicles.stream().filter(v -> "MAINTENANCE".equals(v.getStatus())).count()
                    : 0;
            model.addAttribute("totalVehicles", totalVehicles);
            model.addAttribute("availableVehicles", availableVehicles);
            model.addAttribute("rentedVehicles", rentedVehicles);
            model.addAttribute("maintenanceVehicles", maintenanceVehicles);

            // Platform-wide booking statistics
            List<BookingDTO> allBookings = bookingService.getAllBookings();
            int totalBookings = allBookings != null ? allBookings.size() : 0;
            long activeRentals = allBookings != null ? allBookings.stream()
                    .filter(b -> "ACTIVE".equals(b.getStatus()) || "IN_PROGRESS".equals(b.getStatus()))
                    .count() : 0;
            long pendingBookings = allBookings != null ? allBookings.stream()
                    .filter(b -> "PENDING".equals(b.getStatus()) || "CONFIRMED".equals(b.getStatus()))
                    .count() : 0;
            model.addAttribute("totalBookings", totalBookings);
            model.addAttribute("activeRentals", activeRentals);
            model.addAttribute("pendingBookings", pendingBookings);

            // Recent bookings (last 5)
            List<BookingDTO> recentBookings = allBookings != null ? allBookings.stream()
                    .sorted((b1, b2) -> {
                        if (b1.getCreatedAt() == null)
                            return 1;
                        if (b2.getCreatedAt() == null)
                            return -1;
                        return b2.getCreatedAt().compareTo(b1.getCreatedAt());
                    })
                    .limit(5)
                    .collect(java.util.stream.Collectors.toList()) : new java.util.ArrayList<>();
            model.addAttribute("recentBookings", recentBookings);

            // Platform-wide revenue
            var allPayments = paymentService.getAllPayments();
            BigDecimal totalRevenue = allPayments != null ? allPayments.stream()
                    .filter(p -> "COMPLETED".equals(p.getPaymentStatus()) || "PAID".equals(p.getPaymentStatus()))
                    .map(p -> p.getAmount())
                    .filter(a -> a != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;
            model.addAttribute("totalRevenue", totalRevenue);

        } catch (Exception e) {
            e.printStackTrace();
            // Set defaults on error
            model.addAttribute("totalUsers", 0);
            model.addAttribute("renterCount", 0);
            model.addAttribute("ownerCount", 0);
            model.addAttribute("totalVehicles", 0);
            model.addAttribute("availableVehicles", 0);
            model.addAttribute("rentedVehicles", 0);
            model.addAttribute("maintenanceVehicles", 0);
            model.addAttribute("totalBookings", 0);
            model.addAttribute("activeRentals", 0);
            model.addAttribute("pendingBookings", 0);
            model.addAttribute("recentBookings", new java.util.ArrayList<>());
            model.addAttribute("totalRevenue", BigDecimal.ZERO);
        }

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("fleetOwners", userManagementService.getAllFleetOwners());
        model.addAttribute("renters", userManagementService.getAllRenters());
        return "admin/users";
    }

    @GetMapping("/maintenance")
    public String maintenance(Model model) {
        model.addAttribute("maintenanceRecords", maintenanceService.getAllMaintenance());
        model.addAttribute("stats", maintenanceService.getMaintenanceStats());
        return "admin/maintenance";
    }

    @GetMapping("/maintenance/vehicle/{vehicleId}")
    public String vehicleMaintenanceDetails(@PathVariable Long vehicleId, Model model) {
        // Get vehicle details
        VehicleDTO vehicle = vehicleManagementService.getVehicleDetails(vehicleId);
        model.addAttribute("vehicle", vehicle);

        // Get maintenance records
        List<MaintenanceDTO> maintenanceRecords = maintenanceService.getMaintenanceByVehicleId(vehicleId);

        // Sort by date descending
        maintenanceRecords.sort(
                Comparator.comparing(MaintenanceDTO::getScheduledDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        .reversed());

        model.addAttribute("maintenanceRecords", maintenanceRecords);

        // Calculate KPI metrics
        int totalRecords = maintenanceRecords.size();
        BigDecimal totalCost = maintenanceRecords.stream()
                .map(MaintenanceDTO::getEstimatedCost)
                .filter(cost -> cost != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        LocalDate lastMaintenanceDate = maintenanceRecords.stream()
                .map(MaintenanceDTO::getScheduledDate)
                .findFirst()
                .orElse(null);

        model.addAttribute("totalRecords", totalRecords);
        model.addAttribute("totalCost", totalCost);
        model.addAttribute("lastMaintenanceDate", lastMaintenanceDate);

        // Count by status
        long pendingCount = maintenanceRecords.stream()
                .filter(m -> "PENDING".equals(m.getStatus()))
                .count();
        long completedCount = maintenanceRecords.stream()
                .filter(m -> "COMPLETED".equals(m.getStatus()))
                .count();
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("completedCount", completedCount);

        // Get owner name
        String ownerName = "Unknown Owner";
        if (!maintenanceRecords.isEmpty()) {
            ownerName = maintenanceRecords.get(0).getOwnerBusinessName();
        }
        model.addAttribute("ownerName", ownerName);

        return "admin/vehicle-maintenance-details";
    }

    @GetMapping("/maintenance/view/{maintenanceId}")
    public String viewMaintenance(@PathVariable Long maintenanceId, Model model) {
        // Get maintenance details
        MaintenanceDTO maintenance = maintenanceService.getMaintenanceById(maintenanceId);
        if (maintenance == null) {
            return "redirect:/admin/maintenance";
        }
        model.addAttribute("maintenance", maintenance);

        // Get vehicle details
        VehicleDTO vehicle = vehicleManagementService.getVehicleDetails(maintenance.getVehicleId());
        model.addAttribute("vehicle", vehicle);

        // Get status logs
        List<MaintenanceLogDTO> statusLogs = maintenanceService.getMaintenanceLogsDTO(maintenanceId);
        model.addAttribute("statusLogs", statusLogs);

        return "admin/view-maintenance";
    }

    @GetMapping("/bookings")
    public String bookings(Model model) {
        List<BookingDTO> allBookings = bookingService.getAllBookings();
        model.addAttribute("bookings", allBookings);

        // Calculate booking statistics
        long pendingCount = allBookings.stream().filter(b -> "PENDING".equals(b.getStatus())).count();
        long confirmedCount = allBookings.stream().filter(b -> "CONFIRMED".equals(b.getStatus())).count();
        long activeCount = allBookings.stream().filter(b -> "ACTIVE".equals(b.getStatus())).count();
        long completedCount = allBookings.stream().filter(b -> "COMPLETED".equals(b.getStatus())).count();
        long cancelledCount = allBookings.stream().filter(b -> "CANCELLED".equals(b.getStatus())).count();
        long disputedCount = allBookings.stream().filter(b -> "DISPUTED".equals(b.getStatus())).count();

        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("confirmedCount", confirmedCount);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("cancelledCount", cancelledCount);
        model.addAttribute("disputedCount", disputedCount);

        // Calculate total revenue from completed bookings
        BigDecimal totalRevenue = allBookings.stream()
                .filter(b -> "COMPLETED".equals(b.getStatus()) && b.getTotalCost() != null)
                .map(BookingDTO::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("totalRevenue", totalRevenue);

        // Calculate monthly booking counts (last 6 months)
        java.util.Map<String, Long> monthlyBookings = new java.util.LinkedHashMap<>();
        java.util.Map<String, BigDecimal> monthlyRevenue = new java.util.LinkedHashMap<>();

        for (int i = 5; i >= 0; i--) {
            LocalDate monthDate = LocalDate.now().minusMonths(i);
            String monthKey = monthDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM yyyy"));
            int year = monthDate.getYear();
            int month = monthDate.getMonthValue();

            long count = allBookings.stream()
                    .filter(b -> b.getCreatedAt() != null
                            && b.getCreatedAt().getYear() == year
                            && b.getCreatedAt().getMonthValue() == month)
                    .count();
            monthlyBookings.put(monthKey, count);

            BigDecimal revenue = allBookings.stream()
                    .filter(b -> b.getCreatedAt() != null
                            && b.getCreatedAt().getYear() == year
                            && b.getCreatedAt().getMonthValue() == month
                            && b.getTotalCost() != null)
                    .map(BookingDTO::getTotalCost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            monthlyRevenue.put(monthKey, revenue);
        }

        model.addAttribute("monthlyLabels", monthlyBookings.keySet());
        model.addAttribute("monthlyBookingCounts", monthlyBookings.values());
        model.addAttribute("monthlyRevenue", monthlyRevenue.values());

        return "admin/bookings";
    }

    @GetMapping("/bookings/view/{bookingId}")
    public String viewBooking(@PathVariable Long bookingId, Model model) {
        BookingDTO booking = bookingService.getBookingDetails(bookingId);
        model.addAttribute("booking", booking);

        // Get status logs for timeline
        List<BookingLogDTO> statusLogs = bookingService.getBookingStatusLogsDTO(bookingId);
        model.addAttribute("statusLogs", statusLogs);

        return "admin/booking-details";
    }

    @GetMapping("/payment")
    public String payment(Model model) {
        model.addAttribute("payments", paymentService.getAllPayments());
        return "admin/payments";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        return "admin/reports";
    }

    @GetMapping("/ai-reports")
    public String aiReports(Model model) {
        return "admin/ai-reports";
    }

    @GetMapping("/vehicles")
    public String vehicles(Model model) {
        model.addAttribute("vehicles", vehicleManagementService.getAllVehicles());
        return "admin/vehicles";
    }

    @GetMapping("/users/view/{userId}")
    public String viewUser(@PathVariable Long userId, @RequestParam String type, Model model) {
        UserDetailDTO userDetail = userManagementService.getUserDetails(userId, type);
        model.addAttribute("userDetail", userDetail);
        model.addAttribute("userType", type);
        return "admin/view-user";
    }

    @GetMapping("/vehicles/view/{vehicleId}")
    public String viewVehicle(@PathVariable Long vehicleId, Model model) {
        VehicleDTO vehicle = vehicleManagementService.getVehicleDetails(vehicleId);
        model.addAttribute("vehicle", vehicle);
        return "admin/view-vehicle";
    }

    @GetMapping("/vehicles/add")
    public String addVehicle(Model model) {
        model.addAttribute("vehicle", new VehicleDTO());
        return "admin/add-vehicle";
    }

    @GetMapping("/bookings/edit/{bookingId}")
    public String editBooking(@PathVariable Long bookingId, Model model) {
        BookingDTO booking = bookingService.getBookingDetails(bookingId);
        model.addAttribute("booking", booking);
        return "admin/edit-booking";
    }

    @PostMapping("/bookings/update")
    public String updateBooking(BookingDTO bookingDTO) {
        bookingService.updateBooking(bookingDTO);
        return "redirect:/admin/bookings/view/" + bookingDTO.getBookingId();
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user != null) {
            model.addAttribute("user", user);
            if (user.getAdminDetails() != null) {
                model.addAttribute("adminDetails", user.getAdminDetails());
            }

            // Fetch address for the user
            Optional<com.najmi.fleetshare.entity.Address> addressOpt = addressRepository
                    .findLatestAddressByUserId(user.getUserId());
            addressOpt.ifPresent(address -> model.addAttribute("address", address));
        }
        return "admin/profile";
    }
}
