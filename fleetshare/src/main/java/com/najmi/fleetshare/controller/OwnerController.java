package com.najmi.fleetshare.controller;

import com.najmi.fleetshare.dto.RenterDTO;
import com.najmi.fleetshare.dto.SessionUser;
import com.najmi.fleetshare.dto.UserDetailDTO;
import com.najmi.fleetshare.service.UserManagementService;
import com.najmi.fleetshare.util.SessionHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/owner")
public class OwnerController {

    @Autowired
    private UserManagementService userManagementService;

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
