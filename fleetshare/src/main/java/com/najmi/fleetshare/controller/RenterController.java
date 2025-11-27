package com.najmi.fleetshare.controller;

import com.najmi.fleetshare.dto.BookingDTO;
import com.najmi.fleetshare.dto.SessionUser;
import com.najmi.fleetshare.dto.VehicleDTO;
import com.najmi.fleetshare.service.BookingService;
import com.najmi.fleetshare.service.VehicleManagementService;
import com.najmi.fleetshare.util.SessionHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/renter")
public class RenterController {

    @Autowired
    private VehicleManagementService vehicleManagementService;

    @Autowired
    private BookingService bookingService;

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
    public String myBookings(HttpSession session, Model model) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user != null && user.getRenterDetails() != null) {
            // Fetch bookings for the current renter
            Long renterId = user.getRenterDetails().getRenterId();
            List<BookingDTO> bookings = bookingService.getBookingsByRenterId(renterId);
            model.addAttribute("bookings", bookings);
        }
        return "renter/my-bookings";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user != null && user.getRenterDetails() != null) {
            model.addAttribute("user", user);
            model.addAttribute("renterDetails", user.getRenterDetails());
        }
        return "renter/profile";
    }

    @GetMapping("/bookings/{id}")
    public String bookingDetails(@PathVariable Long id, HttpSession session, Model model) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user != null && user.getRenterDetails() != null) {
            // Fetch booking details
            BookingDTO booking = bookingService.getBookingDetails(id);

            // Verify booking belongs to logged-in renter
            if (booking != null && booking.getRenterId().equals(user.getRenterDetails().getRenterId())) {
                model.addAttribute("booking", booking);
                return "renter/booking-details";
            }
        }
        // If booking not found or doesn't belong to user, redirect to bookings list
        return "redirect:/renter/bookings";
    }

    @GetMapping("/home")
    public String home() {
        // Redirect to vehicles page for now
        return "redirect:/renter/vehicles";
    }
}
