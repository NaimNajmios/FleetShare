package com.najmi.fleetshare.controller;

import com.najmi.fleetshare.dto.SessionUser;
import com.najmi.fleetshare.util.SessionHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/renter")
public class RenterController {

    @GetMapping("/vehicles")
    public String browseVehicles(HttpSession session, Model model) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user != null) {
            model.addAttribute("user", user);
            if (user.getRenterDetails() != null) {
                model.addAttribute("renterName", user.getRenterDetails().getFullName());
            }
        }
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
