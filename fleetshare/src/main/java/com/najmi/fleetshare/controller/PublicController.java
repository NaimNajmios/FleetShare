package com.najmi.fleetshare.controller;

import com.najmi.fleetshare.dto.VehicleDTO;
import com.najmi.fleetshare.service.VehicleManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private VehicleManagementService vehicleManagementService;

    @GetMapping("/home")
    public String landingPage(Model model) {
        // Fetch a limited set of featured vehicles (6 latest available) for the landing page
        List<VehicleDTO> featuredVehicles = vehicleManagementService.getAvailableVehicles()
                .stream()
                .limit(6)
                .collect(Collectors.toList());
        model.addAttribute("featuredVehicles", featuredVehicles);
        
        // Add basic platform statistics
        // For simplicity we will just provide some static nice numbers here 
        // since we don't have a dedicated stat service yet. 
        // Ideally these would come from a service method.
        model.addAttribute("totalVehicles", vehicleManagementService.getAvailableVehicles().size());
        
        return "public/landing";
    }

    @GetMapping("/vehicles")
    public String browseVehicles(
            @RequestParam(value = "pickupDate", required = false) String pickupDateStr,
            @RequestParam(value = "returnDate", required = false) String returnDateStr,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "12") int size,
            Model model) {
        
        size = Math.min(Math.max(size, 1), 48);
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        
        org.springframework.data.domain.Page<VehicleDTO> vehiclePage;
        
        if (pickupDateStr != null && returnDateStr != null && !pickupDateStr.trim().isEmpty() && !returnDateStr.trim().isEmpty()) {
            LocalDate pickupDate = LocalDate.parse(pickupDateStr);
            LocalDate returnDate = LocalDate.parse(returnDateStr);
            vehiclePage = vehicleManagementService.getAvailableVehiclesPaginated(pickupDate, returnDate, pageable);
            model.addAttribute("pickupDate", pickupDateStr);
            model.addAttribute("returnDate", returnDateStr);
        } else {
            vehiclePage = vehicleManagementService.getAvailableVehiclesPaginated(pageable);
        }

        model.addAttribute("vehicles", vehiclePage.getContent());
        model.addAttribute("currentPage", vehiclePage.getNumber());
        model.addAttribute("totalPages", vehiclePage.getTotalPages());
        model.addAttribute("totalItems", vehiclePage.getTotalElements());
        model.addAttribute("defaultSize", size);
        
        java.util.Map<String, String> pageParams = new java.util.HashMap<>();
        if (pickupDateStr != null && !pickupDateStr.trim().isEmpty()) {
            pageParams.put("pickupDate", pickupDateStr);
        }
        if (returnDateStr != null && !returnDateStr.trim().isEmpty()) {
            pageParams.put("returnDate", returnDateStr);
        }
        model.addAttribute("pageParams", pageParams);

        return "public/browse-vehicles";
    }
}
