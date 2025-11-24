package com.najmi.fleetshare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/renter")
public class RenterController {

    @GetMapping("/vehicles")
    public String browseVehicles(Model model) {
        // TODO: Add vehicle list from service
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
