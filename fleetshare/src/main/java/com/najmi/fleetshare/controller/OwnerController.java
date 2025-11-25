package com.najmi.fleetshare.controller;

import com.najmi.fleetshare.dto.SessionUser;
import com.najmi.fleetshare.util.SessionHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/owner")
public class OwnerController {

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
    public String customers(Model model) {
        return "owner/customers";
    }

    @GetMapping("/vehicles")
    public String vehicles(Model model) {
        return "owner/vehicles";
    }

    @GetMapping("/maintenance")
    public String maintenance(Model model) {
        return "owner/maintenance";
    }

    @GetMapping("/bookings")
    public String bookings(Model model) {
        return "owner/bookings";
    }

    @GetMapping("/payments")
    public String payments(Model model) {
        return "owner/payments";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        return "owner/reports";
    }
}
