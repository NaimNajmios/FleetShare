package com.najmi.fleetshare.controller;

import com.najmi.fleetshare.dto.BookingDTO;
import com.najmi.fleetshare.dto.MaintenanceDTO;
import com.najmi.fleetshare.dto.PaymentDTO;
import com.najmi.fleetshare.dto.RenterDTO;
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
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user != null) {
            model.addAttribute("user", user);
            if (user.getOwnerDetails() != null) {
                model.addAttribute("businessName", user.getOwnerDetails().getBusinessName());
            }
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
        }
        return "owner/maintenance";
    }

    @GetMapping("/maintenance/vehicle/{vehicleId}")
    public String vehicleMaintenance(@PathVariable Long vehicleId, Model model) {
        // Get vehicle details
        VehicleDTO vehicle = vehicleManagementService.getVehicleDetails(vehicleId);
        model.addAttribute("vehicle", vehicle);

        // Get maintenance records
        List<MaintenanceDTO> maintenanceRecords = maintenanceService.getMaintenanceByVehicleId(vehicleId);

        // Sort by date descending
        maintenanceRecords.sort(Comparator.comparing(MaintenanceDTO::getMaintenanceDate).reversed());

        model.addAttribute("maintenanceRecords", maintenanceRecords);

        // Calculate KPI metrics
        int totalRecords = maintenanceRecords.size();
        BigDecimal totalCost = maintenanceRecords.stream()
                .map(MaintenanceDTO::getCost)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        LocalDate lastMaintenanceDate = maintenanceRecords.isEmpty() ? null
                : maintenanceRecords.get(0).getMaintenanceDate();

        model.addAttribute("totalRecords", totalRecords);
        model.addAttribute("totalCost", totalCost);
        model.addAttribute("lastMaintenanceDate", lastMaintenanceDate);
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
        }
        return "owner/bookings";
    }

    @GetMapping("/bookings/view/{bookingId}")
    public String viewBooking(@PathVariable Long bookingId, Model model) {
        BookingDTO booking = bookingService.getBookingDetails(bookingId);
        model.addAttribute("booking", booking);
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
        }
        return "owner/payments";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        return "owner/reports";
    }

    @GetMapping("/ai-reports")
    public String aiReports(Model model) {
        return "owner/ai-reports";
    }
}
