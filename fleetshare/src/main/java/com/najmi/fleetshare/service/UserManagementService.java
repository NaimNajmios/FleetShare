package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.FleetOwnerDTO;
import com.najmi.fleetshare.dto.RenterDTO;
import com.najmi.fleetshare.entity.FleetOwner;
import com.najmi.fleetshare.entity.Renter;
import com.najmi.fleetshare.entity.User;
import com.najmi.fleetshare.repository.FleetOwnerRepository;
import com.najmi.fleetshare.repository.RenterRepository;
import com.najmi.fleetshare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserManagementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FleetOwnerRepository fleetOwnerRepository;

    @Autowired
    private RenterRepository renterRepository;

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
}
