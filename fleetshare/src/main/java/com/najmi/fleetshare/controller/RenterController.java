package com.najmi.fleetshare.controller;

import com.najmi.fleetshare.dto.SessionUser;
import com.najmi.fleetshare.dto.VehicleDTO;
import com.najmi.fleetshare.service.VehicleManagementService;
import com.najmi.fleetshare.util.SessionHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/renter")
public class RenterController {

    @Autowired
    private VehicleManagementService vehicleManagementService;

    @GetMapping("/vehicles")
    public String browseVehicles(HttpSession session, Model model) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user != null) {
            model.addAttribute("user", user);
            if (user.getRenterDetails() != null) {
                model.addAttribute("renterName", user.getRenterDetails().getFullName());
            }
        }

        // Fetch all vehicles and filter to show only available ones
        List<VehicleDTO> allVehicles = vehicleManagementService.getAllVehicles();
        List<VehicleDTO> availableVehicles = allVehicles.stream()
                .filter(vehicle -> "AVAILABLE".equals(vehicle.getStatus()))
                .collect(Collectors.toList());

        model.addAttribute("vehicles", availableVehicles);
        return "renter/browse-vehicles";
    }

    @GetMapping("/bookings")
    public String myBookings(Model model) {
        // TODO: Add booking list from service
        return "renter/my-bookings";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        // TODO: Add user profile from service
        return "renter/profile";
    }

    @GetMapping("/home")
    public String home() {
        // Redirect to vehicles page for now
        return "redirect:/renter/vehicles";
    }
}
