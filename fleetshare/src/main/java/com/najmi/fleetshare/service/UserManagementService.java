package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.FleetOwnerDTO;
import com.najmi.fleetshare.dto.RenterDTO;
import com.najmi.fleetshare.dto.UserDetailDTO;
import com.najmi.fleetshare.entity.*;
import com.najmi.fleetshare.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

        // Collect all user IDs to fetch in a single query
        Set<Long> userIds = fleetOwners.stream()
                .map(FleetOwner::getUserId)
                .collect(Collectors.toSet());

        // Batch fetch users
        java.util.Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getUserId, java.util.function.Function.identity()));

        List<FleetOwnerDTO> fleetOwnerDTOs = new ArrayList<>();

        for (FleetOwner owner : fleetOwners) {
            User user = userMap.get(owner.getUserId());
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

        // Collect all user IDs to fetch in a single query
        Set<Long> userIds = renters.stream()
                .map(Renter::getUserId)
                .collect(Collectors.toSet());

        // Batch fetch users
        java.util.Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getUserId, java.util.function.Function.identity()));

        List<RenterDTO> renterDTOs = new ArrayList<>();

        for (Renter renter : renters) {
            User user = userMap.get(renter.getUserId());
            if (user != null) {
                RenterDTO dto = new RenterDTO(
                        renter.getRenterId(),
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

        // 3. Batch fetch renters
        List<Renter> renters = renterRepository.findAllById(renterIds);

        // 4. Collect user IDs for batch fetching
        Set<Long> userIds = renters.stream()
                .map(Renter::getUserId)
                .collect(Collectors.toSet());

        // 5. Batch fetch users
        java.util.Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getUserId, java.util.function.Function.identity()));

        // 6. Map to DTOs
        List<RenterDTO> customers = new ArrayList<>();
        for (Renter renter : renters) {
            User user = userMap.get(renter.getUserId());
            if (user != null) {
                RenterDTO dto = new RenterDTO(
                        renter.getRenterId(),
                        user.getUserId(),
                        user.getEmail(),
                        renter.getFullName(),
                        renter.getPhoneNumber(),
                        user.getIsActive(),
                        user.getCreatedAt());
                customers.add(dto);
            }
        }

        return customers;
    }

    /**
     * Updates user information based on user type
     * 
     * @param userId   User ID
     * @param userType "owner" or "renter"
     * @param dto      UserDetailDTO with updated information
     * @return true if update successful, false otherwise
     */
    public boolean updateUser(Long userId, String userType, UserDetailDTO dto) {
        // Get user entity
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }

        // Update user fields
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            user.setEmail(dto.getEmail().trim());
        }
        if (dto.getIsActive() != null) {
            user.setIsActive(dto.getIsActive());
        }
        userRepository.save(user);

        // Update role-specific fields
        if ("owner".equalsIgnoreCase(userType)) {
            FleetOwner owner = fleetOwnerRepository.findByUserId(userId).orElse(null);
            if (owner != null) {
                if (dto.getBusinessName() != null && !dto.getBusinessName().trim().isEmpty()) {
                    owner.setBusinessName(dto.getBusinessName().trim());
                }
                if (dto.getPhoneNumber() != null) {
                    owner.setContactPhone(dto.getPhoneNumber().trim());
                }
                if (dto.getIsVerified() != null) {
                    owner.setIsVerified(dto.getIsVerified());
                }
                owner.setUpdatedAt(LocalDateTime.now());
                fleetOwnerRepository.save(owner);
            }
        } else if ("renter".equalsIgnoreCase(userType)) {
            Renter renter = renterRepository.findByUserId(userId).orElse(null);
            if (renter != null) {
                if (dto.getFullName() != null && !dto.getFullName().trim().isEmpty()) {
                    renter.setFullName(dto.getFullName().trim());
                }
                if (dto.getPhoneNumber() != null) {
                    renter.setPhoneNumber(dto.getPhoneNumber().trim());
                }
                renter.setUpdatedAt(LocalDateTime.now());
                renterRepository.save(renter);
            }
        }

        // Update or create address if address fields provided
        if (dto.getAddressLine1() != null && !dto.getAddressLine1().trim().isEmpty()) {
            Address address = addressRepository.findLatestAddressByUserId(userId).orElse(null);
            if (address == null) {
                // Create new address
                address = new Address();
                address.setAddressUserId(userId);
                address.setEffectiveStartDate(LocalDate.now());
                address.setCreatedAt(LocalDateTime.now());
            }

            address.setAddressLine1(dto.getAddressLine1().trim());
            address.setAddressLine2(dto.getAddressLine2() != null ? dto.getAddressLine2().trim() : null);
            address.setCity(dto.getCity() != null ? dto.getCity().trim() : address.getCity());
            address.setState(dto.getState() != null ? dto.getState().trim() : address.getState());
            address.setPostalCode(dto.getPostalCode() != null ? dto.getPostalCode().trim() : address.getPostalCode());
            address.setUpdatedAt(LocalDateTime.now());
            addressRepository.save(address);
        }

        return true;
    }
}
