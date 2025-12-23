package com.najmi.fleetshare.controller;

import com.najmi.fleetshare.dto.BookingDTO;
import com.najmi.fleetshare.dto.BookingLogDTO;
import com.najmi.fleetshare.dto.MaintenanceDTO;
import com.najmi.fleetshare.dto.MaintenanceLogDTO;
import com.najmi.fleetshare.dto.PaymentDTO;
import com.najmi.fleetshare.dto.RenterDTO;
import com.najmi.fleetshare.dto.SessionUser;
import com.najmi.fleetshare.dto.UserDetailDTO;
import com.najmi.fleetshare.dto.VehicleDTO;
import com.najmi.fleetshare.entity.FleetOwner;
import com.najmi.fleetshare.repository.FleetOwnerRepository;
import com.najmi.fleetshare.service.BookingService;
import com.najmi.fleetshare.service.MaintenanceService;
import com.najmi.fleetshare.service.PaymentService;
import com.najmi.fleetshare.service.UserManagementService;
import com.najmi.fleetshare.service.VehicleManagementService;
import com.najmi.fleetshare.util.SessionHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/owner")
public class OwnerController {

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
    private com.najmi.fleetshare.repository.AddressRepository addressRepository;

    @Autowired
    private FleetOwnerRepository fleetOwnerRepository;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user != null && user.getOwnerDetails() != null) {
            try {
                Long ownerId = user.getOwnerDetails().getFleetOwnerId();
                model.addAttribute("user", user);
                model.addAttribute("businessName", user.getOwnerDetails().getBusinessName());

                // Fetch vehicles for this owner
                List<VehicleDTO> vehicles = vehicleManagementService.getVehiclesByOwnerId(ownerId);
                if (vehicles == null)
                    vehicles = new java.util.ArrayList<>();
                model.addAttribute("totalVehicles", vehicles.size());

                // Count vehicles by status
                long availableCount = vehicles.stream().filter(v -> v != null && "AVAILABLE".equals(v.getStatus()))
                        .count();
                long rentedCount = vehicles.stream().filter(v -> v != null && "RENTED".equals(v.getStatus())).count();
                long maintenanceCount = vehicles.stream().filter(v -> v != null && "MAINTENANCE".equals(v.getStatus()))
                        .count();
                model.addAttribute("availableVehicles", availableCount);
                model.addAttribute("rentedVehicles", rentedCount);
                model.addAttribute("maintenanceVehicles", maintenanceCount);

                // Fetch bookings for this owner
                List<BookingDTO> bookings = bookingService.getBookingsByOwnerId(ownerId);
                if (bookings == null)
                    bookings = new java.util.ArrayList<>();
                model.addAttribute("totalBookings", bookings.size());

                // Count active rentals (IN_PROGRESS status)
                long activeRentals = bookings.stream()
                        .filter(b -> b != null
                                && ("IN_PROGRESS".equals(b.getStatus()) || "ACTIVE".equals(b.getStatus())))
                        .count();
                model.addAttribute("activeRentals", activeRentals);

                // Count pending bookings
                long pendingBookings = bookings.stream()
                        .filter(b -> b != null
                                && ("PENDING".equals(b.getStatus()) || "CONFIRMED".equals(b.getStatus())))
                        .count();
                model.addAttribute("pendingBookings", pendingBookings);

                // Get recent bookings (last 5)
                List<BookingDTO> recentBookings = bookings.stream()
                        .filter(Objects::nonNull)
                        .sorted((b1, b2) -> {
                            if (b1.getStartDate() == null)
                                return 1;
                            if (b2.getStartDate() == null)
                                return -1;
                            return b2.getStartDate().compareTo(b1.getStartDate());
                        })
                        .limit(5)
                        .collect(java.util.stream.Collectors.toList());
                model.addAttribute("recentBookings", recentBookings);

                // Fetch payments and calculate revenue
                List<PaymentDTO> payments = paymentService.getPaymentsByOwnerId(ownerId);
                if (payments == null)
                    payments = new java.util.ArrayList<>();
                BigDecimal totalRevenue = payments.stream()
                        .filter(p -> p != null
                                && ("COMPLETED".equals(p.getPaymentStatus()) || "PAID".equals(p.getPaymentStatus())))
                        .map(PaymentDTO::getAmount)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                model.addAttribute("totalRevenue", totalRevenue);

                // Pass vehicles list for status overview
                model.addAttribute("vehicles", vehicles);
            } catch (Exception e) {
                // Log error and set defaults
                e.printStackTrace();
                model.addAttribute("totalVehicles", 0);
                model.addAttribute("availableVehicles", 0);
                model.addAttribute("rentedVehicles", 0);
                model.addAttribute("maintenanceVehicles", 0);
                model.addAttribute("totalBookings", 0);
                model.addAttribute("activeRentals", 0);
                model.addAttribute("pendingBookings", 0);
                model.addAttribute("recentBookings", new java.util.ArrayList<>());
                model.addAttribute("totalRevenue", BigDecimal.ZERO);
                model.addAttribute("vehicles", new java.util.ArrayList<>());
            }
        } else {
            // Set default values when user is null
            model.addAttribute("totalVehicles", 0);
            model.addAttribute("availableVehicles", 0);
            model.addAttribute("rentedVehicles", 0);
            model.addAttribute("maintenanceVehicles", 0);
            model.addAttribute("totalBookings", 0);
            model.addAttribute("activeRentals", 0);
            model.addAttribute("pendingBookings", 0);
            model.addAttribute("recentBookings", new java.util.ArrayList<>());
            model.addAttribute("totalRevenue", BigDecimal.ZERO);
            model.addAttribute("vehicles", new java.util.ArrayList<>());
        }
        return "owner/dashboard";
    }

    @GetMapping("/customers")
    public String customers(HttpSession session, Model model) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user != null && user.getOwnerDetails() != null) {
            Long ownerId = user.getOwnerDetails().getFleetOwnerId();
            List<RenterDTO> customers = userManagementService.getCustomersByOwnerId(ownerId);
            model.addAttribute("customers", customers);
        }
        return "owner/customers";
    }

    @GetMapping("/customers/view/{customerId}")
    public String viewCustomer(@PathVariable Long customerId, Model model) {
        UserDetailDTO customerDetail = userManagementService.getUserDetails(customerId, "renter");
        model.addAttribute("customerDetail", customerDetail);
        return "owner/view-customer";
    }

    @GetMapping("/vehicles")
    public String vehicles(HttpSession session, Model model) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user != null && user.getOwnerDetails() != null) {
            Long ownerId = user.getOwnerDetails().getFleetOwnerId();
            List<VehicleDTO> vehicles = vehicleManagementService.getVehiclesByOwnerId(ownerId);
            model.addAttribute("vehicles", vehicles);
        }
        return "owner/vehicles";
    }

    @GetMapping("/vehicles/view/{vehicleId}")
    public String viewVehicle(@PathVariable Long vehicleId, Model model) {
        VehicleDTO vehicle = vehicleManagementService.getVehicleDetails(vehicleId);
        model.addAttribute("vehicle", vehicle);
        return "owner/view-vehicle";
    }

    @GetMapping("/vehicles/add")
    public String addVehicle(Model model) {
        model.addAttribute("vehicle", new VehicleDTO());
        return "owner/add-vehicle";
    }

    @GetMapping("/maintenance")
    public String maintenance(HttpSession session, Model model) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user != null && user.getOwnerDetails() != null) {
            Long ownerId = user.getOwnerDetails().getFleetOwnerId();
            List<MaintenanceDTO> maintenanceRecords = maintenanceService.getMaintenanceByOwnerId(ownerId);
            model.addAttribute("maintenanceRecords", maintenanceRecords);
            model.addAttribute("stats", maintenanceService.getMaintenanceStatsByOwnerId(ownerId));

            // Add vehicles for the dropdown
            List<VehicleDTO> vehicles = vehicleManagementService.getVehiclesByOwnerId(ownerId);
            model.addAttribute("vehicles", vehicles);

            // Add empty DTO for the form
            model.addAttribute("maintenanceDTO", new MaintenanceDTO());
        }
        return "owner/maintenance";
    }

    @org.springframework.web.bind.annotation.PostMapping("/maintenance/add")
    public String addMaintenance(@org.springframework.web.bind.annotation.ModelAttribute MaintenanceDTO maintenanceDTO,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            maintenanceService.addMaintenance(maintenanceDTO, null);
            redirectAttributes.addFlashAttribute("successMessage", "Maintenance record added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding maintenance record: " + e.getMessage());
        }
        return "redirect:/owner/maintenance";
    }

    @GetMapping("/maintenance/view/{maintenanceId}")
    public String viewMaintenance(@PathVariable Long maintenanceId, Model model) {
        // Get maintenance details
        MaintenanceDTO maintenance = maintenanceService.getMaintenanceById(maintenanceId);
        if (maintenance == null) {
            return "redirect:/owner/maintenance";
        }
        model.addAttribute("maintenance", maintenance);

        // Get vehicle details
        VehicleDTO vehicle = vehicleManagementService.getVehicleDetails(maintenance.getVehicleId());
        model.addAttribute("vehicle", vehicle);

        // Get status logs
        List<MaintenanceLogDTO> statusLogs = maintenanceService.getMaintenanceLogsDTO(maintenanceId);
        model.addAttribute("statusLogs", statusLogs);

        return "owner/view-maintenance";
    }

    @GetMapping("/maintenance/vehicle/{vehicleId}")
    public String vehicleMaintenance(@PathVariable Long vehicleId, Model model) {
        // Get vehicle details
        VehicleDTO vehicle = vehicleManagementService.getVehicleDetails(vehicleId);
        model.addAttribute("vehicle", vehicle);

        // Get maintenance records
        List<MaintenanceDTO> maintenanceRecords = maintenanceService.getMaintenanceByVehicleId(vehicleId);

        // Sort by date descending
        maintenanceRecords.sort(Comparator.comparing(MaintenanceDTO::getScheduledDate).reversed());

        model.addAttribute("maintenanceRecords", maintenanceRecords);

        // Calculate KPI metrics
        int totalRecords = maintenanceRecords.size();
        BigDecimal totalCost = maintenanceRecords.stream()
                .map(MaintenanceDTO::getEstimatedCost)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        LocalDate lastMaintenanceDate = maintenanceRecords.isEmpty() ? null
                : maintenanceRecords.get(0).getScheduledDate();

        // Count by status
        long pendingCount = maintenanceRecords.stream()
                .filter(m -> "PENDING".equals(m.getStatus()))
                .count();
        long completedCount = maintenanceRecords.stream()
                .filter(m -> "COMPLETED".equals(m.getStatus()))
                .count();

        model.addAttribute("totalRecords", totalRecords);
        model.addAttribute("totalCost", totalCost);
        model.addAttribute("lastMaintenanceDate", lastMaintenanceDate);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("ownerName", vehicle.getOwnerBusinessName());

        return "owner/vehicle-maintenance-details";
    }

    @GetMapping("/bookings")
    public String bookings(HttpSession session, Model model) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user != null && user.getOwnerDetails() != null) {
            Long ownerId = user.getOwnerDetails().getFleetOwnerId();
            List<BookingDTO> bookings = bookingService.getBookingsByOwnerId(ownerId);
            model.addAttribute("bookings", bookings);

            // Calculate booking statistics
            long pendingCount = bookings.stream().filter(b -> "PENDING".equals(b.getStatus())).count();
            long confirmedCount = bookings.stream().filter(b -> "CONFIRMED".equals(b.getStatus())).count();
            long activeCount = bookings.stream().filter(b -> "ACTIVE".equals(b.getStatus())).count();
            long completedCount = bookings.stream().filter(b -> "COMPLETED".equals(b.getStatus())).count();
            long cancelledCount = bookings.stream().filter(b -> "CANCELLED".equals(b.getStatus())).count();
            long disputedCount = bookings.stream().filter(b -> "DISPUTED".equals(b.getStatus())).count();

            model.addAttribute("pendingCount", pendingCount);
            model.addAttribute("confirmedCount", confirmedCount);
            model.addAttribute("activeCount", activeCount);
            model.addAttribute("completedCount", completedCount);
            model.addAttribute("cancelledCount", cancelledCount);
            model.addAttribute("disputedCount", disputedCount);

            // Calculate total revenue from completed bookings
            BigDecimal totalRevenue = bookings.stream()
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

                long count = bookings.stream()
                        .filter(b -> b.getCreatedAt() != null
                                && b.getCreatedAt().getYear() == year
                                && b.getCreatedAt().getMonthValue() == month)
                        .count();
                monthlyBookings.put(monthKey, count);

                BigDecimal revenue = bookings.stream()
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
        }
        return "owner/bookings";
    }

    @GetMapping("/bookings/create")
    public String createBooking(HttpSession session, Model model) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user != null && user.getOwnerDetails() != null) {
            Long ownerId = user.getOwnerDetails().getFleetOwnerId();

            // Fetch all renters for the dropdown
            List<RenterDTO> renters = userManagementService.getAllRenters();
            model.addAttribute("renters", renters);

            // Fetch owner's vehicles for the dropdown
            List<VehicleDTO> vehicles = vehicleManagementService.getVehiclesByOwnerId(ownerId);
            model.addAttribute("vehicles", vehicles);

            model.addAttribute("booking", new BookingDTO());
        }
        return "owner/create-booking";
    }

    @GetMapping("/bookings/view/{bookingId}")
    public String viewBooking(@PathVariable Long bookingId, Model model) {
        BookingDTO booking = bookingService.getBookingDetails(bookingId);
        model.addAttribute("booking", booking);

        // Get status logs for timeline
        List<BookingLogDTO> statusLogs = bookingService.getBookingStatusLogsDTO(bookingId);
        model.addAttribute("statusLogs", statusLogs);

        return "owner/view-booking";
    }

    @GetMapping("/bookings/edit/{bookingId}")
    public String editBooking(@PathVariable Long bookingId, Model model) {
        BookingDTO booking = bookingService.getBookingDetails(bookingId);
        model.addAttribute("booking", booking);
        return "owner/edit-booking";
    }

    @GetMapping("/payments")
    public String payments(HttpSession session, Model model) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user != null && user.getOwnerDetails() != null) {
            Long ownerId = user.getOwnerDetails().getFleetOwnerId();
            List<PaymentDTO> payments = paymentService.getPaymentsByOwnerId(ownerId);
            model.addAttribute("payments", payments);

            // Calculate payment statistics by status
            long pendingCount = payments.stream().filter(p -> "PENDING".equals(p.getPaymentStatus())).count();
            long verifiedCount = payments.stream().filter(p -> "VERIFIED".equals(p.getPaymentStatus())
                    || "COMPLETED".equals(p.getPaymentStatus()) || "PAID".equals(p.getPaymentStatus())).count();
            long failedCount = payments.stream()
                    .filter(p -> "FAILED".equals(p.getPaymentStatus()) || "REJECTED".equals(p.getPaymentStatus()))
                    .count();

            model.addAttribute("pendingCount", pendingCount);
            model.addAttribute("verifiedCount", verifiedCount);
            model.addAttribute("failedCount", failedCount);

            // Calculate statistics by payment method
            long creditCardCount = payments.stream().filter(p -> "CREDIT_CARD".equals(p.getPaymentMethod())).count();
            long bankTransferCount = payments.stream().filter(p -> "BANK_TRANSFER".equals(p.getPaymentMethod()))
                    .count();
            long qrPaymentCount = payments.stream().filter(p -> "QR_PAYMENT".equals(p.getPaymentMethod())).count();
            long cashCount = payments.stream().filter(p -> "CASH".equals(p.getPaymentMethod())).count();

            model.addAttribute("creditCardCount", creditCardCount);
            model.addAttribute("bankTransferCount", bankTransferCount);
            model.addAttribute("qrPaymentCount", qrPaymentCount);
            model.addAttribute("cashCount", cashCount);

            // Calculate total amounts
            BigDecimal totalAmount = payments.stream()
                    .filter(p -> p.getAmount() != null)
                    .map(p -> p.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal verifiedAmount = payments.stream()
                    .filter(p -> ("VERIFIED".equals(p.getPaymentStatus()) || "COMPLETED".equals(p.getPaymentStatus())
                            || "PAID".equals(p.getPaymentStatus())) && p.getAmount() != null)
                    .map(p -> p.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal pendingAmount = payments.stream()
                    .filter(p -> "PENDING".equals(p.getPaymentStatus()) && p.getAmount() != null)
                    .map(p -> p.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            model.addAttribute("totalAmount", totalAmount);
            model.addAttribute("verifiedAmount", verifiedAmount);
            model.addAttribute("pendingAmount", pendingAmount);

            // Calculate monthly payment data (last 6 months)
            java.util.Map<String, Long> monthlyPaymentCounts = new java.util.LinkedHashMap<>();
            java.util.Map<String, BigDecimal> monthlyPaymentAmounts = new java.util.LinkedHashMap<>();

            for (int i = 5; i >= 0; i--) {
                LocalDate monthDate = LocalDate.now().minusMonths(i);
                String monthKey = monthDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM yyyy"));
                int year = monthDate.getYear();
                int month = monthDate.getMonthValue();

                long count = payments.stream()
                        .filter(p -> p.getPaymentDate() != null
                                && p.getPaymentDate().getYear() == year
                                && p.getPaymentDate().getMonthValue() == month)
                        .count();
                monthlyPaymentCounts.put(monthKey, count);

                BigDecimal amount = payments.stream()
                        .filter(p -> p.getPaymentDate() != null
                                && p.getPaymentDate().getYear() == year
                                && p.getPaymentDate().getMonthValue() == month
                                && p.getAmount() != null)
                        .map(p -> p.getAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                monthlyPaymentAmounts.put(monthKey, amount);
            }

            model.addAttribute("monthlyLabels", monthlyPaymentCounts.keySet());
            model.addAttribute("monthlyPaymentCounts", monthlyPaymentCounts.values());
            model.addAttribute("monthlyPaymentAmounts", monthlyPaymentAmounts.values());
        }
        return "owner/payments";
    }

    @GetMapping("/payments/view/{paymentId}")
    public String viewPayment(@PathVariable Long paymentId, HttpSession session, Model model) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user == null || user.getOwnerDetails() == null) {
            return "redirect:/login";
        }

        com.najmi.fleetshare.dto.PaymentDetailDTO payment = paymentService.getPaymentDetailById(paymentId);
        if (payment == null) {
            return "redirect:/owner/payments";
        }

        model.addAttribute("payment", payment);

        // Get status logs for timeline
        java.util.List<com.najmi.fleetshare.dto.PaymentStatusLogDTO> statusLogs = paymentService
                .getPaymentStatusLogsDTO(paymentId);
        model.addAttribute("statusLogs", statusLogs);

        return "owner/view-payment";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        return "owner/reports";
    }

    @GetMapping("/ai-reports")
    public String aiReports(Model model) {
        return "owner/ai-reports";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user != null && user.getOwnerDetails() != null) {
            model.addAttribute("user", user);
            model.addAttribute("ownerDetails", user.getOwnerDetails());

            // Fetch address for the user
            Optional<com.najmi.fleetshare.entity.Address> addressOpt = addressRepository
                    .findLatestAddressByUserId(user.getUserId());
            addressOpt.ifPresent(address -> model.addAttribute("address", address));
        }
        return "owner/profile";
    }

    @PostMapping("/profile")
    @ResponseBody
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> profileData, HttpSession session) {
        SessionUser sessionUser = SessionHelper.getCurrentUser(session);
        if (sessionUser == null || sessionUser.getOwnerDetails() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        String businessName = profileData.get("businessName");
        String contactPhone = profileData.get("contactPhone");

        // Validation
        if (businessName == null || businessName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Business name is required"));
        }

        // Find and update owner entity
        FleetOwner owner = fleetOwnerRepository.findByUserId(sessionUser.getUserId()).orElse(null);
        if (owner == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Owner not found"));
        }

        owner.setBusinessName(businessName.trim());
        owner.setContactPhone(contactPhone != null ? contactPhone.trim() : null);
        owner.setUpdatedAt(LocalDateTime.now());
        fleetOwnerRepository.save(owner);

        // Update session
        sessionUser.getOwnerDetails().setBusinessName(businessName.trim());
        sessionUser.getOwnerDetails().setContactPhone(contactPhone != null ? contactPhone.trim() : null);

        return ResponseEntity.ok(Map.of("success", true, "message", "Profile updated successfully"));
    }
}
