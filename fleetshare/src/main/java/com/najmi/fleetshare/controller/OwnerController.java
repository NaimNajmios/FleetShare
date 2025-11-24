package com.najmi.fleetshare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/owner")
public class OwnerController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
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
