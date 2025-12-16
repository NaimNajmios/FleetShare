package com.najmi.fleetshare.controller;

import com.najmi.fleetshare.dto.BookingDTO;
import com.najmi.fleetshare.dto.MaintenanceDTO;
import com.najmi.fleetshare.dto.SessionUser;
import com.najmi.fleetshare.dto.UserDetailDTO;
import com.najmi.fleetshare.dto.VehicleDTO;
import com.najmi.fleetshare.service.BookingService;
import com.najmi.fleetshare.service.MaintenanceService;
import com.najmi.fleetshare.service.PaymentService;
import com.najmi.fleetshare.service.UserManagementService;
import com.najmi.fleetshare.service.VehicleManagementService;
import com.najmi.fleetshare.util.SessionHelper;
import jakarta.servlet.http.HttpSession;
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

        // Get owner name
        String ownerName = "Unknown Owner";
        if (!maintenanceRecords.isEmpty()) {
            ownerName = maintenanceRecords.get(0).getOwnerBusinessName();
        }
        model.addAttribute("ownerName", ownerName);

        return "admin/vehicle-maintenance-details";
    }

    @GetMapping("/bookings")
    public String bookings(Model model) {
        model.addAttribute("bookings", bookingService.getAllBookings());
        return "admin/bookings";
    }

    @GetMapping("/bookings/view/{bookingId}")
    public String viewBooking(@PathVariable Long bookingId, Model model) {
        BookingDTO booking = bookingService.getBookingDetails(bookingId);
        model.addAttribute("booking", booking);
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
}