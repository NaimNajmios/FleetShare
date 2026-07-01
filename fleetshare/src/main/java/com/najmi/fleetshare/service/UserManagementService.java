package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.FleetOwnerDTO;
import com.najmi.fleetshare.dto.RenterDTO;
import com.najmi.fleetshare.dto.UserDetailDTO;
import com.najmi.fleetshare.entity.*;
import com.najmi.fleetshare.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private com.najmi.fleetshare.repository.PaymentRepository paymentRepository;

    /**
     * Fetches all fleet owners with their associated user details
     * 
     * @return List of FleetOwnerDTO objects
     */
    public List<FleetOwnerDTO> getAllFleetOwners(String search, String status) {
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
                // Apply search filter
                if (search != null && !search.trim().isEmpty()) {
                    String searchLower = search.trim().toLowerCase();
                    boolean matchesName = owner.getBusinessName() != null && owner.getBusinessName().toLowerCase().contains(searchLower);
                    boolean matchesEmail = user.getEmail() != null && user.getEmail().toLowerCase().contains(searchLower);
                    boolean matchesPhone = owner.getContactPhone() != null && owner.getContactPhone().toLowerCase().contains(searchLower);
                    if (!matchesName && !matchesEmail && !matchesPhone) {
                        continue;
                    }
                }
                
                // Apply status filter
                if (status != null && !status.trim().isEmpty() && !status.equals("all")) {
                    if (status.equals("active") && !Boolean.TRUE.equals(user.getIsActive())) continue;
                    if (status.equals("inactive") && Boolean.TRUE.equals(user.getIsActive())) continue;
                }

                FleetOwnerDTO dto = new FleetOwnerDTO(
                        user.getUserId(),
                        user.getEmail(),
                        owner.getBusinessName(),
                        owner.getContactPhone(),
                        owner.getIsVerified(),
                        user.getIsActive(),
                        user.getCreatedAt(),
                        user.getProfileImageUrl());
                fleetOwnerDTOs.add(dto);
            }
        }

        return fleetOwnerDTOs;
    }

    public org.springframework.data.domain.Page<FleetOwnerDTO> getAllFleetOwnersPaginated(String search, String status, org.springframework.data.domain.Pageable pageable) {
        List<FleetOwnerDTO> allOwners = getAllFleetOwners(search, status);
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allOwners.size());
        
        List<FleetOwnerDTO> pageContent;
        if (start > allOwners.size()) {
            pageContent = new ArrayList<>();
        } else {
            pageContent = allOwners.subList(start, end);
        }
        
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, allOwners.size());
    }

    /**
     * Fetches all renters with their associated user details
     * 
     * @return List of RenterDTO objects
     */
    public List<RenterDTO> getAllRenters(String search, String status) {
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
                // Apply search filter
                if (search != null && !search.trim().isEmpty()) {
                    String searchLower = search.trim().toLowerCase();
                    boolean matchesName = renter.getFullName() != null && renter.getFullName().toLowerCase().contains(searchLower);
                    boolean matchesEmail = user.getEmail() != null && user.getEmail().toLowerCase().contains(searchLower);
                    boolean matchesPhone = renter.getPhoneNumber() != null && renter.getPhoneNumber().toLowerCase().contains(searchLower);
                    if (!matchesName && !matchesEmail && !matchesPhone) {
                        continue;
                    }
                }
                
                // Apply status filter
                if (status != null && !status.trim().isEmpty() && !status.equals("all")) {
                    if (status.equals("active") && !Boolean.TRUE.equals(user.getIsActive())) continue;
                    if (status.equals("inactive") && Boolean.TRUE.equals(user.getIsActive())) continue;
                }

                RenterDTO dto = new RenterDTO(
                        renter.getRenterId(),
                        user.getUserId(),
                        user.getEmail(),
                        renter.getFullName(),
                        renter.getPhoneNumber(),
                        user.getIsActive(),
                        user.getCreatedAt(),
                        user.getProfileImageUrl());
                renterDTOs.add(dto);
            }
        }

        return renterDTOs;
    }

    public org.springframework.data.domain.Page<RenterDTO> getAllRentersPaginated(String search, String status, org.springframework.data.domain.Pageable pageable) {
        List<RenterDTO> allRenters = getAllRenters(search, status);
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allRenters.size());
        
        List<RenterDTO> pageContent;
        if (start > allRenters.size()) {
            pageContent = new ArrayList<>();
        } else {
            pageContent = allRenters.subList(start, end);
        }
        
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, allRenters.size());
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
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setCreatedAt(user.getCreatedAt());

        // Get role-specific info
        if ("owner".equalsIgnoreCase(userType)) {
            FleetOwner owner = fleetOwnerRepository.findByUserId(userId).orElse(null);
            if (owner != null) {
                dto.setBusinessName(owner.getBusinessName());
                dto.setPhoneNumber(owner.getContactPhone());
                dto.setIsVerified(owner.getIsVerified());
                dto.setFullName(owner.getBusinessName()); // Use business name as display name
                
                // Set bank & integration details
                dto.setBankName(owner.getBankName());
                dto.setBankAccountNumber(owner.getBankAccountNumber());
                dto.setBankAccountHolder(owner.getBankAccountHolder());
                dto.setPaymentQrUrl(owner.getPaymentQrUrl());
                dto.setToyyibpaySecretKey(owner.getToyyibpaySecretKey());
                dto.setToyyibpayCategoryCode(owner.getToyyibpayCategoryCode());
                dto.setToyyibpayUsername(owner.getToyyibpayUsername());
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
            dto.setLatitude(address.getLatitude());
            dto.setLongitude(address.getLongitude());
            dto.setAddressEffectiveStartDate(address.getEffectiveStartDate());
        }

        return dto;
    }

    /**
     * Fetches all customers (renters) who have booked with a specific fleet owner
     * 
     * @param ownerId Fleet owner ID
     * @param search Optional search string
     * @return List of RenterDTO objects representing the owner's customers
     */
    public List<RenterDTO> getCustomersByOwnerId(Long ownerId, String search, String status) {
        // 1. Get distinct renter IDs directly from DB (Optimized)
        List<Long> renterIdsList = bookingRepository.findDistinctRenterIdsByFleetOwnerId(ownerId);
        Set<Long> renterIds = new java.util.HashSet<>(renterIdsList);
        renterIds.remove(null);

        if (renterIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. Batch fetch renters
        List<Renter> renters = renterRepository.findAllById(renterIds);

        // 3. Collect user IDs for batch fetching
        Set<Long> userIds = renters.stream()
                .map(Renter::getUserId)
                .collect(Collectors.toSet());

        // 4. Batch fetch users
        java.util.Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getUserId, java.util.function.Function.identity()));

        // 5. Batch fetch aggregate booking data per renter
        List<Booking> allOwnerBookings = bookingRepository.findByFleetOwnerId(ownerId);
        Map<Long, List<Booking>> bookingsByRenter = allOwnerBookings.stream()
                .collect(Collectors.groupingBy(Booking::getRenterId));

        // 6. Batch fetch invoices for all relevant renters to compute total spent
        Map<Long, Invoice> invoiceByBooking = new java.util.HashMap<>();
        for (Booking b : allOwnerBookings) {
            List<Invoice> invs = invoiceRepository.findByBookingId(b.getBookingId());
            if (!invs.isEmpty()) {
                invoiceByBooking.put(b.getBookingId(), invs.get(0));
            }
        }
        Set<Long> invoiceIds = invoiceByBooking.values().stream()
                .map(Invoice::getInvoiceId)
                .collect(Collectors.toSet());
        Map<Long, Payment> paymentByInvoice = new java.util.HashMap<>();
        if (!invoiceIds.isEmpty()) {
            List<Payment> payments = paymentRepository.findByInvoiceIdIn(invoiceIds);
            for (Payment p : payments) {
                paymentByInvoice.putIfAbsent(p.getInvoiceId(), p);
            }
        }

        // 7. Map to DTOs with aggregates
        List<RenterDTO> customers = new ArrayList<>();
        for (Renter renter : renters) {
            User user = userMap.get(renter.getUserId());
            if (user != null) {
                // Apply search filter
                if (search != null && !search.trim().isEmpty()) {
                    String searchLower = search.trim().toLowerCase();
                    boolean matchesName = renter.getFullName() != null && renter.getFullName().toLowerCase().contains(searchLower);
                    boolean matchesEmail = user.getEmail() != null && user.getEmail().toLowerCase().contains(searchLower);
                    boolean matchesPhone = renter.getPhoneNumber() != null && renter.getPhoneNumber().toLowerCase().contains(searchLower);
                    if (!matchesName && !matchesEmail && !matchesPhone) {
                        continue;
                    }
                }
                if (status != null && !status.trim().isEmpty() && !status.equals("all")) {
                    if (status.equals("active") && !Boolean.TRUE.equals(user.getIsActive())) continue;
                    if (status.equals("inactive") && Boolean.TRUE.equals(user.getIsActive())) continue;
                }

                RenterDTO dto = new RenterDTO(
                        renter.getRenterId(),
                        user.getUserId(),
                        user.getEmail(),
                        renter.getFullName(),
                        renter.getPhoneNumber(),
                        user.getIsActive(),
                        user.getCreatedAt(),
                        user.getProfileImageUrl());
                List<Booking> renterBookings = bookingsByRenter.getOrDefault(renter.getRenterId(), Collections.emptyList());
                dto.setTotalBookings((long) renterBookings.size());

                // Last booking date
                renterBookings.stream()
                        .map(Booking::getCreatedAt)
                        .filter(Objects::nonNull)
                        .max(Comparator.naturalOrder())
                        .ifPresent(dto::setLastBookingDate);

                // Total spent (sum of verified/paid invoice amounts)
                BigDecimal totalSpent = renterBookings.stream()
                        .map(b -> invoiceByBooking.get(b.getBookingId()))
                        .filter(Objects::nonNull)
                        .filter(inv -> {
                            Payment pmt = paymentByInvoice.get(inv.getInvoiceId());
                            return pmt != null && pmt.getPaymentStatus() == Payment.PaymentStatus.VERIFIED;
                        })
                        .map(Invoice::getTotalAmount)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                dto.setTotalSpent(totalSpent);

                customers.add(dto);
            }
        }

        return customers;
    }

    public org.springframework.data.domain.Page<RenterDTO> getCustomersByOwnerIdPaginated(Long ownerId, String search, String status, org.springframework.data.domain.Pageable pageable) {
        List<RenterDTO> allCustomers = getCustomersByOwnerId(ownerId, search, status);
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allCustomers.size());
        
        List<RenterDTO> pageContent;
        if (start > allCustomers.size()) {
            pageContent = new ArrayList<>();
        } else {
            pageContent = allCustomers.subList(start, end);
        }
        
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, allCustomers.size());
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
                if (dto.getBankName() != null) {
                    owner.setBankName(dto.getBankName().trim());
                }
                if (dto.getBankAccountNumber() != null) {
                    owner.setBankAccountNumber(dto.getBankAccountNumber().trim());
                }
                if (dto.getBankAccountHolder() != null) {
                    owner.setBankAccountHolder(dto.getBankAccountHolder().trim());
                }
                if (dto.getPaymentQrUrl() != null) {
                    owner.setPaymentQrUrl(dto.getPaymentQrUrl().trim());
                }
                if (dto.getToyyibpaySecretKey() != null) {
                    owner.setToyyibpaySecretKey(dto.getToyyibpaySecretKey().trim());
                }
                if (dto.getToyyibpayCategoryCode() != null) {
                    owner.setToyyibpayCategoryCode(dto.getToyyibpayCategoryCode().trim());
                }
                if (dto.getToyyibpayUsername() != null) {
                    owner.setToyyibpayUsername(dto.getToyyibpayUsername().trim());
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

        // Create new address record if address fields changed (maintains history with
        // effective dates)
        if (dto.getAddressLine1() != null && !dto.getAddressLine1().trim().isEmpty()) {
            Address existingAddress = addressRepository.findLatestAddressByUserId(userId).orElse(null);

            // Check if address actually changed (to avoid duplicate records)
            boolean addressChanged = existingAddress == null ||
                    !dto.getAddressLine1().trim().equals(existingAddress.getAddressLine1()) ||
                    !String.valueOf(dto.getAddressLine2()).equals(String.valueOf(existingAddress.getAddressLine2())) ||
                    !String.valueOf(dto.getCity()).equals(String.valueOf(existingAddress.getCity())) ||
                    !String.valueOf(dto.getState()).equals(String.valueOf(existingAddress.getState())) ||
                    !String.valueOf(dto.getPostalCode()).equals(String.valueOf(existingAddress.getPostalCode()));

            if (addressChanged) {
                // Always create a new address record with new effective date
                Address newAddress = new Address();
                newAddress.setAddressUserId(userId);
                newAddress.setAddressLine1(dto.getAddressLine1().trim());
                newAddress.setAddressLine2(dto.getAddressLine2() != null ? dto.getAddressLine2().trim() : null);
                newAddress.setCity(dto.getCity() != null ? dto.getCity().trim()
                        : (existingAddress != null ? existingAddress.getCity() : ""));
                newAddress.setState(dto.getState() != null ? dto.getState().trim()
                        : (existingAddress != null ? existingAddress.getState() : ""));
                newAddress.setPostalCode(dto.getPostalCode() != null ? dto.getPostalCode().trim()
                        : (existingAddress != null ? existingAddress.getPostalCode() : ""));
                newAddress.setEffectiveStartDate(LocalDate.now());
                newAddress.setCreatedAt(LocalDateTime.now());
                newAddress.setUpdatedAt(LocalDateTime.now());
                // Update coordinates if available
                if (dto.getLatitude() != null) {
                    newAddress.setLatitude(dto.getLatitude());
                } else if (existingAddress != null) {
                    newAddress.setLatitude(existingAddress.getLatitude());
                }

                if (dto.getLongitude() != null) {
                    newAddress.setLongitude(dto.getLongitude());
                } else if (existingAddress != null) {
                    newAddress.setLongitude(existingAddress.getLongitude());
                }
                
                addressRepository.save(newAddress);
            }
        }

        return true;
    }
}
