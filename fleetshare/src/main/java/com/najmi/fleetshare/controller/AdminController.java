package com.najmi.fleetshare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/")
    public String index() {
        // Best Practice: Redirect to dashboard so the layout logic
        // isn't duplicated. Otherwise, this returns "dashboard.html"
        // without the "layouts/admin-layout" wrapper.
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {
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
        return "admin/vehicles";
    }
}