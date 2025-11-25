package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.*;
import com.najmi.fleetshare.entity.*;
import com.najmi.fleetshare.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserSessionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlatformAdminRepository adminRepository;

    @Autowired
    private FleetOwnerRepository ownerRepository;

    @Autowired
    private RenterRepository renterRepository;

    public SessionUser loadSessionUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        SessionUser sessionUser = new SessionUser();
        sessionUser.setUserId(user.getUserId());
        sessionUser.setEmail(user.getEmail());
        sessionUser.setRole(user.getUserRole());
        sessionUser.setProfileImageUrl(user.getProfileImageUrl());
        sessionUser.setCreatedAt(user.getCreatedAt());
        sessionUser.setIsActive(user.getIsActive());

        switch (user.getUserRole()) {
            case PLATFORM_ADMIN:
                loadAdminDetails(sessionUser, user.getUserId());
                break;
            case FLEET_OWNER:
                loadOwnerDetails(sessionUser, user.getUserId());
                break;
            case RENTER:
                loadRenterDetails(sessionUser, user.getUserId());
                break;
        }

        return sessionUser;
    }

    private void loadAdminDetails(SessionUser sessionUser, Long userId) {
        PlatformAdmin admin = adminRepository.findByUserId(userId).orElse(null);
        if (admin != null) {
            AdminDetails details = new AdminDetails();
            details.setAdminId(admin.getAdminId());
            details.setFullName(admin.getFullName());
            sessionUser.setAdminDetails(details);
        }
    }

    private void loadOwnerDetails(SessionUser sessionUser, Long userId) {
        FleetOwner owner = ownerRepository.findByUserId(userId).orElse(null);
        if (owner != null) {
            OwnerDetails details = new OwnerDetails();
            details.setFleetOwnerId(owner.getFleetOwnerId());
            details.setBusinessName(owner.getBusinessName());
            details.setContactPhone(owner.getContactPhone());
            details.setIsVerified(owner.getIsVerified());
            sessionUser.setOwnerDetails(details);
        }
    }

    private void loadRenterDetails(SessionUser sessionUser, Long userId) {
        Renter renter = renterRepository.findByUserId(userId).orElse(null);
        if (renter != null) {
            RenterDetails details = new RenterDetails();
            details.setRenterId(renter.getRenterId());
            details.setFullName(renter.getFullName());
            details.setPhoneNumber(renter.getPhoneNumber());
            details.setUpdatedAt(renter.getUpdatedAt());
            sessionUser.setRenterDetails(details);
        }
    }
}
