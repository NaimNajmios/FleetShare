package com.najmi.fleetshare.controller;

import com.najmi.fleetshare.dto.SessionUser;
import com.najmi.fleetshare.entity.Booking;
import com.najmi.fleetshare.entity.FleetOwner;
import com.najmi.fleetshare.entity.Renter;
import com.najmi.fleetshare.entity.User;
import com.najmi.fleetshare.entity.Vehicle;
import com.najmi.fleetshare.repository.BookingRepository;
import com.najmi.fleetshare.repository.FleetOwnerRepository;
import com.najmi.fleetshare.repository.RenterRepository;
import com.najmi.fleetshare.repository.UserRepository;
import com.najmi.fleetshare.repository.VehicleRepository;
import com.najmi.fleetshare.util.SessionHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SearchController {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RenterRepository renterRepository;

    @Autowired
    private FleetOwnerRepository fleetOwnerRepository;

    private static final int MAX_RESULTS = 5;

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam String q,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        SessionUser currentUser = SessionHelper.getCurrentUser(session);

        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        if (q == null || q.trim().length() < 2) {
            return ResponseEntity.ok(Map.of("results", Map.of(), "totalResults", 0));
        }

        String term = q.trim();

        if (currentUser.getOwnerDetails() != null) {
            Long ownerId = currentUser.getOwnerDetails().getFleetOwnerId();
            return searchForOwner(ownerId, term, response);
        } else if (currentUser.getAdminDetails() != null) {
            return searchForAdmin(term, response);
        } else if (currentUser.getRenterDetails() != null) {
            return searchForAdmin(term, response);
        }

        return ResponseEntity.ok(Map.of("results", Map.of(), "totalResults", 0));
    }

    private ResponseEntity<Map<String, Object>> searchForOwner(Long ownerId, String term, Map<String, Object> response) {
        Map<String, Object> results = new HashMap<>();
        int total = 0;

        List<Map<String, Object>> vehicles = vehicleRepository.searchByOwner(ownerId, term).stream()
                .limit(MAX_RESULTS)
                .map(this::vehicleToMap)
                .collect(Collectors.toList());
        results.put("vehicles", vehicles);
        total += vehicles.size();

        List<Map<String, Object>> bookings = bookingRepository.searchByOwner(ownerId, term).stream()
                .limit(MAX_RESULTS)
                .map(this::bookingToMap)
                .collect(Collectors.toList());
        results.put("bookings", bookings);
        total += bookings.size();

        List<Map<String, Object>> renters = searchRentersForOwner(ownerId, term).stream()
                .limit(MAX_RESULTS)
                .map(this::renterToMap)
                .collect(Collectors.toList());
        results.put("renters", renters);
        total += renters.size();

        response.put("results", results);
        response.put("totalResults", total);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> searchForAdmin(String term, Map<String, Object> response) {
        Map<String, Object> results = new HashMap<>();
        int total = 0;

        List<Map<String, Object>> vehicles = vehicleRepository.searchAll(term).stream()
                .limit(MAX_RESULTS)
                .map(this::vehicleToMap)
                .collect(Collectors.toList());
        results.put("vehicles", vehicles);
        total += vehicles.size();

        List<Map<String, Object>> bookings = bookingRepository.searchAll(term).stream()
                .limit(MAX_RESULTS)
                .map(this::bookingToMap)
                .collect(Collectors.toList());
        results.put("bookings", bookings);
        total += bookings.size();

        List<Map<String, Object>> users = userRepository.searchAll(term).stream()
                .limit(MAX_RESULTS)
                .map(this::userToMap)
                .collect(Collectors.toList());
        results.put("users", users);
        total += users.size();

        List<Map<String, Object>> renters = renterRepository.searchAll(term).stream()
                .limit(MAX_RESULTS)
                .map(this::renterToMap)
                .collect(Collectors.toList());
        results.put("renters", renters);
        total += renters.size();

        List<Map<String, Object>> owners = fleetOwnerRepository.searchAll(term).stream()
                .limit(MAX_RESULTS)
                .map(this::ownerToMap)
                .collect(Collectors.toList());
        results.put("owners", owners);
        total += owners.size();

        response.put("results", results);
        response.put("totalResults", total);
        return ResponseEntity.ok(response);
    }

    private List<Renter> searchRentersForOwner(Long ownerId, String term) {
        List<Booking> bookings = bookingRepository.findByFleetOwnerId(ownerId);
        List<Long> renterIds = bookings.stream()
                .map(Booking::getRenterId)
                .distinct()
                .collect(Collectors.toList());

        return renterRepository.searchAll(term).stream()
                .filter(r -> renterIds.contains(r.getRenterId()))
                .collect(Collectors.toList());
    }

    private Map<String, Object> vehicleToMap(Vehicle v) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", v.getVehicleId());
        map.put("brand", v.getBrand());
        map.put("model", v.getModel());
        map.put("registrationNo", v.getRegistrationNo());
        map.put("status", v.getStatus() != null ? v.getStatus().name() : null);
        return map;
    }

    private Map<String, Object> bookingToMap(Booking b) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", b.getBookingId());
        map.put("bookingId", b.getBookingId());
        map.put("vehicleId", b.getVehicleId());
        map.put("renterId", b.getRenterId());
        return map;
    }

    private Map<String, Object> userToMap(User u) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", u.getUserId());
        map.put("email", u.getEmail());
        map.put("role", u.getUserRole() != null ? u.getUserRole().name() : null);
        map.put("isActive", u.getIsActive());
        return map;
    }

    private Map<String, Object> renterToMap(Renter r) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", r.getRenterId());
        map.put("fullName", r.getFullName());
        map.put("phoneNumber", r.getPhoneNumber());
        map.put("userId", r.getUserId());
        return map;
    }

    private Map<String, Object> ownerToMap(FleetOwner f) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", f.getFleetOwnerId());
        map.put("businessName", f.getBusinessName());
        map.put("contactPhone", f.getContactPhone());
        map.put("isVerified", f.getIsVerified());
        return map;
    }
}
