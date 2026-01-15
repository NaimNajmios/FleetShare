package com.najmi.fleetshare.controller;

import com.najmi.fleetshare.dto.BookingDTO;
import com.najmi.fleetshare.dto.SessionUser;
import com.najmi.fleetshare.dto.VehicleDTO;
import com.najmi.fleetshare.entity.Address;
import com.najmi.fleetshare.entity.Renter;
import com.najmi.fleetshare.repository.AddressRepository;
import com.najmi.fleetshare.repository.RenterRepository;
import com.najmi.fleetshare.service.BookingService;
import com.najmi.fleetshare.service.VehicleManagementService;
import com.najmi.fleetshare.util.SessionHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.najmi.fleetshare.entity.BookingStatusLog;
import com.najmi.fleetshare.entity.FleetOwner;
import com.najmi.fleetshare.dto.OwnerProfileDTO;
import com.najmi.fleetshare.repository.FleetOwnerRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.najmi.fleetshare.dto.PasswordChangeRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
@RequestMapping("/renter")
public class RenterController {

    @Autowired
    private VehicleManagementService vehicleManagementService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private com.najmi.fleetshare.service.PaymentService paymentService;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private FleetOwnerRepository fleetOwnerRepository;

    @Autowired
    private RenterRepository renterRepository;

    @Autowired
    private com.najmi.fleetshare.repository.UserRepository userRepository;

    @Autowired
    private com.najmi.fleetshare.service.FileStorageService fileStorageService;

    @Autowired
    private com.najmi.fleetshare.service.InvoiceService invoiceService;

    @Autowired
    private com.najmi.fleetshare.service.ReceiptService receiptService;

    @GetMapping("/vehicles")
    public String browseVehicles(HttpSession session, Model model) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user != null) {
            model.addAttribute("user", user);
            if (user.getRenterDetails() != null) {
                model.addAttribute("renterName", user.getRenterDetails().getFullName());
            }
        }

        // Fetch available vehicles directly from DB
        // TODO: Add pagination
        List<VehicleDTO> availableVehicles = vehicleManagementService.getAvailableVehicles();

        model.addAttribute("vehicles", availableVehicles);
        return "renter/browse-vehicles";
    }

    @GetMapping("/vehicles/{id}")
    public String vehicleDetails(@PathVariable Long id, HttpSession session, Model model) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user != null) {
            model.addAttribute("user", user);
            if (user.getRenterDetails() != null) {
                model.addAttribute("renterName", user.getRenterDetails().getFullName());
            }
        }

        VehicleDTO vehicle = vehicleManagementService.getVehicleDetails(id);
        if (vehicle == null) {
            return "redirect:/renter/vehicles";
        }

        model.addAttribute("vehicle", vehicle);
        return "renter/vehicle-details";
    }

    @GetMapping("/vehicles/{id}/book")
    public String bookingForm(@PathVariable Long id, HttpSession session, Model model) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user != null) {
            model.addAttribute("user", user);
            if (user.getRenterDetails() != null) {
                model.addAttribute("renterName", user.getRenterDetails().getFullName());
            }
        }

        VehicleDTO vehicle = vehicleManagementService.getVehicleDetails(id);
        if (vehicle == null) {
            return "redirect:/renter/vehicles";
        }

        model.addAttribute("vehicle", vehicle);
        return "renter/booking-form";
    }

    @org.springframework.web.bind.annotation.PostMapping("/vehicles/{id}/book/review")
    public String reviewBooking(@PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestParam("pickupDate") String pickupDateStr,
            @org.springframework.web.bind.annotation.RequestParam("returnDate") String returnDateStr,
            HttpSession session, Model model) {

        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user == null || user.getRenterDetails() == null) {
            return "redirect:/login";
        }

        VehicleDTO vehicle = vehicleManagementService.getVehicleDetails(id);
        if (vehicle == null) {
            return "redirect:/renter/vehicles";
        }

        try {
            java.time.LocalDate pickupDate = java.time.LocalDate.parse(pickupDateStr);
            java.time.LocalDate returnDate = java.time.LocalDate.parse(returnDateStr);

            long days = java.time.temporal.ChronoUnit.DAYS.between(pickupDate, returnDate);
            if (days == 0)
                days = 1; // Minimum 1 day

            java.math.BigDecimal totalCost = vehicle.getRatePerDay().multiply(java.math.BigDecimal.valueOf(days));

            model.addAttribute("user", user);
            model.addAttribute("renterName", user.getRenterDetails().getFullName());
            model.addAttribute("renterId", user.getRenterDetails().getRenterId());
            model.addAttribute("renterPhone", user.getRenterDetails().getPhoneNumber()); // Assuming phone number is
                                                                                         // available in RenterDetails

            model.addAttribute("vehicle", vehicle);
            model.addAttribute("pickupDate", pickupDate);
            model.addAttribute("returnDate", returnDate);
            model.addAttribute("duration", days);
            model.addAttribute("totalCost", totalCost);
            model.addAttribute("confirmationDate", java.time.LocalDateTime.now());

            return "renter/booking-confirmation";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/renter/vehicles/" + id + "/book";
        }
    }

    @PostMapping("/vehicles/{id}/book/confirm")
    public String confirmBooking(@PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestParam("pickupDate") String pickupDateStr,
            @org.springframework.web.bind.annotation.RequestParam("returnDate") String returnDateStr,
            HttpSession session,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user == null || user.getRenterDetails() == null) {
            return "redirect:/login";
        }

        try {
            java.time.LocalDate pickupDate = java.time.LocalDate.parse(pickupDateStr);
            java.time.LocalDate returnDate = java.time.LocalDate.parse(returnDateStr);

            com.najmi.fleetshare.entity.Booking booking = bookingService.createBooking(
                    user.getRenterDetails().getRenterId(),
                    id,
                    pickupDate,
                    returnDate);

            redirectAttributes.addFlashAttribute("successMessage", "Booking confirmed successfully!");
            return "redirect:/renter/bookings/" + booking.getBookingId() + "/payment";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to confirm booking: " + e.getMessage());
            return "redirect:/renter/vehicles/" + id + "/book";
        }
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

            // Fetch address information
            Address address = addressRepository.findLatestAddressByUserId(user.getUserId()).orElse(null);
            model.addAttribute("address", address);
        }
        return "renter/profile";
    }

    @PostMapping("/profile")
    @ResponseBody
    public ResponseEntity<?> updateProfile(
            @jakarta.validation.Valid @RequestBody com.najmi.fleetshare.dto.RenterProfileUpdateRequest request,
            HttpSession session) {
        SessionUser sessionUser = SessionHelper.getCurrentUser(session);
        if (sessionUser == null || sessionUser.getRenterDetails() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        String fullName = request.getFullName();
        String phoneNumber = request.getPhoneNumber();

        // Find and update renter entity
        Renter renter = renterRepository.findByUserId(sessionUser.getUserId()).orElse(null);
        if (renter == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Renter not found"));
        }

        renter.setFullName(fullName.trim());
        renter.setPhoneNumber(phoneNumber != null ? phoneNumber.trim() : null);
        renter.setUpdatedAt(LocalDateTime.now());
        renterRepository.save(renter);

        // Update Address
        String addressLine1 = request.getAddressLine1();
        if (addressLine1 != null && !addressLine1.trim().isEmpty()) {
            Address address = addressRepository
                    .findLatestAddressByUserId(sessionUser.getUserId())
                    .orElse(new Address());

            if (address.getAddressId() == null) {
                address.setAddressUserId(sessionUser.getUserId());
                address.setCreatedAt(LocalDateTime.now());
                address.setEffectiveStartDate(java.time.LocalDate.now());
            }

            address.setAddressLine1(addressLine1.trim());
            address.setAddressLine2(request.getAddressLine2() != null ? request.getAddressLine2().trim() : null);
            address.setCity(request.getCity() != null ? request.getCity().trim() : null);
            address.setState(request.getState() != null ? request.getState().trim() : null);
            address.setPostalCode(request.getPostalCode() != null ? request.getPostalCode().trim() : null);

            String latitudeStr = request.getLatitude();
            if (latitudeStr != null && !latitudeStr.isEmpty()) {
                try {
                    address.setLatitude(Double.parseDouble(latitudeStr));
                } catch (NumberFormatException e) {
                    // Ignore invalid lat
                }
            }

            String longitudeStr = request.getLongitude();
            if (longitudeStr != null && !longitudeStr.isEmpty()) {
                try {
                    address.setLongitude(Double.parseDouble(longitudeStr));
                } catch (NumberFormatException e) {
                    // Ignore invalid lng
                }
            }

            address.setUpdatedAt(LocalDateTime.now());
            addressRepository.save(address);
        }

        // Update session
        sessionUser.getRenterDetails().setFullName(fullName.trim());
        sessionUser.getRenterDetails().setPhoneNumber(phoneNumber != null ? phoneNumber.trim() : null);

        return ResponseEntity.ok(Map.of("success", true, "message", "Profile updated successfully"));
    }

    @PostMapping("/profile/password")
    @ResponseBody
    public ResponseEntity<?> updatePassword(
            @jakarta.validation.Valid @RequestBody PasswordChangeRequest request,
            HttpSession session) {
        SessionUser sessionUser = SessionHelper.getCurrentUser(session);
        if (sessionUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "New passwords do not match"));
        }

        com.najmi.fleetshare.entity.User user = userRepository.findById(sessionUser.getUserId()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getHashedPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Incorrect old password"));
        }

        user.setHashedPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("success", true, "message", "Password changed successfully"));
    }

    @PostMapping("/profile/image")
    @ResponseBody
    public ResponseEntity<?> uploadProfileImage(
            @org.springframework.web.bind.annotation.RequestParam("image") org.springframework.web.multipart.MultipartFile file,
            HttpSession session) {
        SessionUser sessionUser = SessionHelper.getCurrentUser(session);
        if (sessionUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        try {
            // Store the profile image
            String imageUrl = fileStorageService.storeProfileImage(file, sessionUser.getUserId());

            // Update user profile image URL
            com.najmi.fleetshare.entity.User user = userRepository.findById(sessionUser.getUserId()).orElse(null);

            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }

            user.setProfileImageUrl(imageUrl);
            userRepository
                    .save(user);

            // Update session
            sessionUser.setProfileImageUrl(imageUrl);

            return ResponseEntity.ok(Map.of("success", true, "imageUrl", imageUrl));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload: " + e.getMessage()));
        }
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

                // Fetch booking status logs
                List<BookingStatusLog> statusLogs = bookingService.getBookingStatusLogs(id);
                model.addAttribute("statusLogs", statusLogs);

                // Fetch payment details and logs
                com.najmi.fleetshare.entity.Payment payment = paymentService.getPaymentByBookingId(id);
                if (payment != null) {
                    model.addAttribute("payment", payment);
                    List<com.najmi.fleetshare.entity.PaymentStatusLog> paymentLogs = paymentService
                            .getPaymentStatusLogs(payment.getPaymentId());
                    model.addAttribute("paymentLogs", paymentLogs);
                }

                return "renter/booking-details";
            }
        }
        // If booking not found or doesn't belong to user, redirect to bookings list
        return "redirect:/renter/bookings";
    }

    @GetMapping("/bookings/{id}/payment")
    public String managePayment(@PathVariable Long id, HttpSession session, Model model) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user != null && user.getRenterDetails() != null) {
            // Fetch booking details
            BookingDTO booking = bookingService.getBookingDetails(id);

            // Verify booking belongs to logged-in renter
            if (booking != null && booking.getRenterId().equals(user.getRenterDetails().getRenterId())) {
                model.addAttribute("booking", booking);

                // Fetch current payment if exists
                com.najmi.fleetshare.entity.Payment currentPayment = paymentService.getPaymentByBookingId(id);
                model.addAttribute("currentPayment", currentPayment);

                return "renter/manage-payment";
            }
        }
        return "redirect:/renter/bookings";
    }

    @PostMapping("/bookings/{id}/payment/cash")
    public String confirmCashPayment(@PathVariable Long id, HttpSession session,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user == null || user.getRenterDetails() == null) {
            return "redirect:/login";
        }

        try {
            paymentService.processCashPayment(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Cash payment option confirmed. Please pay at counter.");
            return "redirect:/renter/bookings/" + id;
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to process cash payment: " + e.getMessage());
            return "redirect:/renter/bookings/" + id + "/payment";
        }
    }

    @PostMapping("/bookings/{id}/payment/change")
    public String changePaymentMethod(@PathVariable Long id,
            @RequestParam("method") String method,
            HttpSession session,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user == null || user.getRenterDetails() == null) {
            return "redirect:/login";
        }

        try {
            com.najmi.fleetshare.entity.Payment.PaymentMethod newMethod;
            switch (method.toUpperCase()) {
                case "CASH":
                    newMethod = com.najmi.fleetshare.entity.Payment.PaymentMethod.CASH;
                    break;
                case "CREDIT_CARD":
                    newMethod = com.najmi.fleetshare.entity.Payment.PaymentMethod.CREDIT_CARD;
                    break;
                case "BANK_TRANSFER":
                    newMethod = com.najmi.fleetshare.entity.Payment.PaymentMethod.BANK_TRANSFER;
                    break;
                case "QR_PAYMENT":
                    newMethod = com.najmi.fleetshare.entity.Payment.PaymentMethod.QR_PAYMENT;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid payment method: " + method);
            }

            paymentService.changePaymentMethod(id, newMethod);
            redirectAttributes.addFlashAttribute("successMessage", "Payment method changed successfully.");
            return "redirect:/renter/bookings/" + id;
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to change payment method: " + e.getMessage());
            return "redirect:/renter/bookings/" + id + "/payment";
        }
    }

    @PostMapping("/bookings/{id}/payment/transfer")
    public String processTransferPayment(@PathVariable Long id,
            @RequestParam("receipt") org.springframework.web.multipart.MultipartFile receipt,
            HttpSession session,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user == null || user.getRenterDetails() == null) {
            return "redirect:/login";
        }

        try {
            paymentService.processBankTransferPayment(id, receipt);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Receipt submitted successfully. Your payment will be verified within 24 hours.");
            return "redirect:/renter/bookings/" + id;
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload receipt: " + e.getMessage());
            return "redirect:/renter/bookings/" + id + "/payment";
        }
    }

    @GetMapping("/owners/{id}")
    public String ownerProfile(@PathVariable Long id, HttpSession session, Model model) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user != null) {
            model.addAttribute("user", user);
            if (user.getRenterDetails() != null) {
                model.addAttribute("renterName", user.getRenterDetails().getFullName());
            }
        }

        // Fetch owner details
        FleetOwner owner = fleetOwnerRepository.findById(id).orElse(null);
        if (owner == null) {
            return "redirect:/renter/vehicles";
        }

        // Get address information
        String city = "Unknown City";
        String state = "Unknown State";
        Double latitude = null;
        Double longitude = null;
        Address address = addressRepository.findLatestAddressByUserId(owner.getUserId()).orElse(null);
        if (address != null) {
            city = address.getCity();
            state = address.getState();
            latitude = address.getLatitude();
            longitude = address.getLongitude();
        }

        // Get owner's vehicles (only available ones)
        List<VehicleDTO> ownerVehicles = vehicleManagementService.getVehiclesByOwnerId(id);
        List<VehicleDTO> availableVehicles = ownerVehicles.stream()
                .filter(v -> "AVAILABLE".equals(v.getStatus()))
                .collect(Collectors.toList());

        // Create owner profile DTO
        OwnerProfileDTO ownerProfile = new OwnerProfileDTO(
                owner.getFleetOwnerId(),
                owner.getBusinessName(),
                owner.getContactPhone(),
                owner.getIsVerified(),
                city,
                state,
                latitude,
                longitude,
                ownerVehicles.size());

        model.addAttribute("owner", ownerProfile);
        model.addAttribute("vehicles", availableVehicles);
        return "renter/owner-profile";
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user == null || user.getRenterDetails() == null) {
            return "redirect:/login";
        }

        // Add user info
        model.addAttribute("user", user);
        model.addAttribute("userName", user.getRenterDetails().getFullName());

        // Greeting based on time of day
        int hour = java.time.LocalTime.now().getHour();
        String greeting;
        if (hour < 12) {
            greeting = "Good Morning";
        } else if (hour < 18) {
            greeting = "Good Afternoon";
        } else {
            greeting = "Good Evening";
        }
        model.addAttribute("greeting", greeting);

        // Fetch bookings for stats
        Long renterId = user.getRenterDetails().getRenterId();

        // Optimized: Get counts directly from DB
        com.najmi.fleetshare.dto.BookingCountDTO counts = bookingService.getBookingCountsByRenterId(renterId);

        model.addAttribute("totalBookings", counts.getTotal());
        model.addAttribute("activeBookings", counts.getActive());
        model.addAttribute("completedBookings", counts.getCompleted());
        model.addAttribute("pendingBookings", counts.getPending());

        // Optimized: Get only recent bookings (latest 3)
        List<BookingDTO> recentBookings = bookingService.getRecentBookingsByRenterId(renterId, 3);
        model.addAttribute("recentBookings", recentBookings);

        return "renter/home";
    }

    /**
     * Download invoice PDF for a booking
     */
    @GetMapping("/bookings/{id}/invoice")
    public org.springframework.http.ResponseEntity<byte[]> downloadInvoice(
            @PathVariable Long id, HttpSession session) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user == null || user.getRenterDetails() == null) {
            return org.springframework.http.ResponseEntity.status(401).build();
        }

        try {
            // Get invoice for this booking
            com.najmi.fleetshare.entity.Invoice invoice = invoiceService.getInvoiceByBookingId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

            // Verify ownership
            if (!invoice.getRenterId().equals(user.getRenterDetails().getRenterId())) {
                return org.springframework.http.ResponseEntity.status(403).build();
            }

            byte[] pdf = invoiceService.generateInvoicePdf(invoice.getInvoiceId());

            return org.springframework.http.ResponseEntity.ok()
                    .header("Content-Disposition",
                            "attachment; filename=invoice-" + invoice.getInvoiceNumber() + ".pdf")
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().build();
        }
    }

    /**
     * Download receipt PDF for a booking (only if payment is verified)
     */
    @GetMapping("/bookings/{id}/receipt")
    public org.springframework.http.ResponseEntity<byte[]> downloadReceipt(
            @PathVariable Long id, HttpSession session) {
        SessionUser user = SessionHelper.getCurrentUser(session);
        if (user == null || user.getRenterDetails() == null) {
            return org.springframework.http.ResponseEntity.status(401).build();
        }

        try {
            // Get invoice for this booking
            com.najmi.fleetshare.entity.Invoice invoice = invoiceService.getInvoiceByBookingId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

            // Verify ownership
            if (!invoice.getRenterId().equals(user.getRenterDetails().getRenterId())) {
                return org.springframework.http.ResponseEntity.status(403).build();
            }

            // Get payment for this invoice
            com.najmi.fleetshare.entity.Payment payment = receiptService.getPaymentByInvoiceId(invoice.getInvoiceId())
                    .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

            // Check if payment is verified
            if (!receiptService.canGenerateReceipt(payment.getPaymentId())) {
                return org.springframework.http.ResponseEntity.status(400).build();
            }

            byte[] pdf = receiptService.generateReceiptPdf(payment.getPaymentId());

            return org.springframework.http.ResponseEntity.ok()
                    .header("Content-Disposition",
                            "attachment; filename=receipt-" + invoice.getInvoiceNumber() + ".pdf")
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().build();
        }
    }
}
