package com.najmi.fleetshare.controller;

import com.najmi.fleetshare.dto.SessionUser;
import com.najmi.fleetshare.service.UserManagementService;
import com.najmi.fleetshare.service.VehicleManagementService;
import com.najmi.fleetshare.util.SessionHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private VehicleManagementService vehicleManagementService;

    @GetMapping("/")
    public String index() {
        // Best Practice: Redirect to dashboard so the layout logic
        // isn't duplicated. Otherwise, this returns "dashboard.html"
        // without the "layouts/admin-layout" wrapper.
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
        return "admin/maintenance";
    }

    @GetMapping("/bookings")
    public String bookings(Model model) {
        return "admin/bookings";
    }

    @GetMapping("/payment")
    public String payment(Model model) {
        return "admin/payments";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        return "admin/reports";
    }

    @GetMapping("/vehicles")
    public String vehicles(Model model) {
        model.addAttribute("vehicles", vehicleManagementService.getAllVehicles());
        return "admin/vehicles";
    }
}