package com.najmi.fleetshare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/")
    public String index() {
        // Best Practice: Redirect to dashboard so the layout logic
        // isn't duplicated. Otherwise, this returns "dashboard.html"
        // without the "layouts/base" wrapper.
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {
        return "user-management";
    }

    @GetMapping("/maintenance")
    public String maintenance(Model model) {
        return "maintenance-management";
    }

    @GetMapping("/bookings")
    public String bookings(Model model) {
        return "booking-management";
    }

    @GetMapping("/payment")
    public String payment(Model model) {
        return "payment";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        return "report";
    }
}