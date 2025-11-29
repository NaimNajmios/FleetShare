package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.FleetOwnerDTO;
import com.najmi.fleetshare.dto.RenterDTO;
import com.najmi.fleetshare.dto.UserDetailDTO;
import com.najmi.fleetshare.entity.*;
import com.najmi.fleetshare.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserManagementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FleetOwnerRepository fleetOwnerRepository;

    @Autowired
    private RenterRepository renterRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private BookingRepository bookingRepository;

    /**
     * Fetches all fleet owners with their associated user details
     * 
     * @return List of FleetOwnerDTO objects
     */
    public List<FleetOwnerDTO> getAllFleetOwners() {
        List<FleetOwner> fleetOwners = fleetOwnerRepository.findAll();
        List<FleetOwnerDTO> fleetOwnerDTOs = new ArrayList<>();

        for (FleetOwner owner : fleetOwners) {
            User user = userRepository.findById(owner.getUserId()).orElse(null);
            if (user != null) {
                FleetOwnerDTO dto = new FleetOwnerDTO(
                        user.getUserId(),
                        user.getEmail(),
                        owner.getBusinessName(),
                        owner.getContactPhone(),
                        owner.getIsVerified(),
                        user.getIsActive(),
                        user.getCreatedAt());
                fleetOwnerDTOs.add(dto);
            }
        }

        return fleetOwnerDTOs;
    }

    /**
     * Fetches all renters with their associated user details
     * 
     * @return List of RenterDTO objects
     */
    public List<RenterDTO> getAllRenters() {
        List<Renter> renters = renterRepository.findAll();
        List<RenterDTO> renterDTOs = new ArrayList<>();

        for (Renter renter : renters) {
            User user = userRepository.findById(renter.getUserId()).orElse(null);
            if (user != null) {
                RenterDTO dto = new RenterDTO(
                        user.getUserId(),
                        user.getEmail(),
                        renter.getFullName(),
                        renter.getPhoneNumber(),
                        user.getIsActive(),
                        user.getCreatedAt());
                renterDTOs.add(dto);
            }
        }

        return renterDTOs;
    }

    /**
     * Fetches complete user details including address
     * 
     * @param userId   User ID
     * @param userType "owner" or "renter"
     * @return UserDetailDTO with all user information
     */
    public UserDetailDTO getUserDetails(Long userId, String userType) {
        UserDetailDTO dto = new UserDetailDTO();

        // Get user basic info
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setUserRole(user.getUserRole().name());
        dto.setIsActive(user.getIsActive());

        // Get role-specific info
        if ("owner".equalsIgnoreCase(userType)) {
            FleetOwner owner = fleetOwnerRepository.findByUserId(userId).orElse(null);
            if (owner != null) {
                dto.setBusinessName(owner.getBusinessName());
                dto.setPhoneNumber(owner.getContactPhone());
                dto.setIsVerified(owner.getIsVerified());
                dto.setFullName(owner.getBusinessName()); // Use business name as display name
            }
        } else if ("renter".equalsIgnoreCase(userType)) {
            Renter renter = renterRepository.findByUserId(userId).orElse(null);
            if (renter != null) {
                dto.setFullName(renter.getFullName());
                dto.setPhoneNumber(renter.getPhoneNumber());
            }
        }

        // Get address info
        Address address = addressRepository.findLatestAddressByUserId(userId).orElse(null);
        if (address != null) {
            dto.setAddressLine1(address.getAddressLine1());
            dto.setAddressLine2(address.getAddressLine2());
            dto.setCity(address.getCity());
            dto.setState(address.getState());
            dto.setPostalCode(address.getPostalCode());
        }

        return dto;
    }

    /**
     * Fetches all customers (renters) who have booked with a specific fleet owner
     * 
     * @param ownerId Fleet owner ID
     * @return List of RenterDTO objects representing the owner's customers
     */
    public List<RenterDTO> getCustomersByOwnerId(Long ownerId) {
        // 1. Get all bookings for this owner
        List<Booking> ownerBookings = bookingRepository.findByFleetOwnerId(ownerId);

        // 2. Extract distinct renter IDs
        Set<Long> renterIds = ownerBookings.stream()
                .map(Booking::getRenterId)
                .collect(Collectors.toSet());

        // 3. Fetch renter details and convert to DTOs
        List<RenterDTO> customers = new ArrayList<>();
        for (Long renterId : renterIds) {
            Renter renter = renterRepository.findById(renterId).orElse(null);
            if (renter != null) {
                User user = userRepository.findById(renter.getUserId()).orElse(null);
                if (user != null) {
                    RenterDTO dto = new RenterDTO(
                            user.getUserId(),
                            user.getEmail(),
                            renter.getFullName(),
                            renter.getPhoneNumber(),
                            user.getIsActive(),
                            user.getCreatedAt());
                    customers.add(dto);
                }
            }
        }

        return customers;
    }
}
