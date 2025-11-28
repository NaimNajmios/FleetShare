package com.najmi.fleetshare.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @GetMapping("/")
    public String root(Authentication authentication) {
        // If user is not authenticated, redirect to login
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // If authenticated, redirect based on role
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            
            if (role.equals("ROLE_PLATFORM_ADMIN")) {
                return "redirect:/admin/dashboard";
            } else if (role.equals("ROLE_FLEET_OWNER")) {
                return "redirect:/owner/dashboard";
            } else if (role.equals("ROLE_RENTER")) {
                return "redirect:/renter/vehicles";
            }
        }

        // Default fallback to login
        return "redirect:/login";
    }
}
