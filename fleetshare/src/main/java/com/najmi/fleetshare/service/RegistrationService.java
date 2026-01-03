package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.RegistrationDTO;
import com.najmi.fleetshare.entity.*;
import com.najmi.fleetshare.exception.RegistrationException;
import com.najmi.fleetshare.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Service to handle user registration with transactional support.
 * Creates User, Address, and role-specific profile (Renter or FleetOwner)
 * atomically.
 */
@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final RenterRepository renterRepository;
    private final FleetOwnerRepository fleetOwnerRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepository userRepository,
            AddressRepository addressRepository,
            RenterRepository renterRepository,
            FleetOwnerRepository fleetOwnerRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.renterRepository = renterRepository;
        this.fleetOwnerRepository = fleetOwnerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user with address and role-specific profile.
     * All database operations are wrapped in a transaction - if any step fails,
     * all changes are rolled back.
     *
     * @param dto Registration form data
     * @throws RegistrationException if validation fails or email already exists
     */
    @Transactional
    public void registerUser(RegistrationDTO dto) throws RegistrationException {
        // 1. Validate email uniqueness
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RegistrationException("Email address is already registered");
        }

        // 2. Validate passwords match
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new RegistrationException("Passwords do not match");
        }

        // 3. Validate terms acceptance
        if (!dto.isAgreeTerms()) {
            throw new RegistrationException("You must agree to the Terms of Service");
        }

        // 4. Determine user role
        UserRole role = parseUserRole(dto.getUserRole());

        // 5. Create and save User entity
        User user = new User();
        user.setEmail(dto.getEmail().toLowerCase().trim());
        user.setHashedPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUserRole(role);
        user.setProfileImageUrl("/uploads/profiles/profile-placeholder.png");
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        // 6. Create and save Address entity
        Address address = new Address();
        address.setAddressUserId(savedUser.getUserId());
        address.setAddressLine1(dto.getAddressLine1());
        address.setAddressLine2(dto.getAddressLine2());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPostalCode(dto.getPostalCode());
        address.setEffectiveStartDate(LocalDate.now());
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());

        addressRepository.save(address);

        // 7. Create role-specific profile
        if (role == UserRole.RENTER) {
            createRenterProfile(savedUser.getUserId(), dto.getFullName(), dto.getPhoneNumber());
        } else if (role == UserRole.FLEET_OWNER) {
            createFleetOwnerProfile(savedUser.getUserId(), dto.getFullName(), dto.getPhoneNumber());
        }
    }

    /**
     * Parse the role string from form to UserRole enum.
     */
    private UserRole parseUserRole(String roleString) throws RegistrationException {
        if (roleString == null || roleString.isBlank()) {
            throw new RegistrationException("Please select a role");
        }

        return switch (roleString.toLowerCase().trim()) {
            case "renter" -> UserRole.RENTER;
            case "owner" -> UserRole.FLEET_OWNER;
            default -> throw new RegistrationException("Invalid role selected");
        };
    }

    /**
     * Create a Renter profile for the new user.
     */
    private void createRenterProfile(Long userId, String fullName, String phoneNumber) {
        Renter renter = new Renter();
        renter.setUserId(userId);
        renter.setFullName(fullName);
        renter.setPhoneNumber(phoneNumber);
        renter.setUpdatedAt(LocalDateTime.now());
        renterRepository.save(renter);
    }

    /**
     * Create a Fleet Owner profile for the new user.
     * Uses full name as initial business name (can be updated later).
     */
    private void createFleetOwnerProfile(Long userId, String fullName, String phoneNumber) {
        FleetOwner fleetOwner = new FleetOwner();
        fleetOwner.setUserId(userId);
        fleetOwner.setBusinessName(fullName); // Default to user's name
        fleetOwner.setContactPhone(phoneNumber);
        fleetOwner.setIsVerified(false); // New owners start unverified
        fleetOwner.setUpdatedAt(LocalDateTime.now());
        fleetOwnerRepository.save(fleetOwner);
    }
}
