package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.AddVehicleRequest;
import com.najmi.fleetshare.dto.VehicleDTO;
import com.najmi.fleetshare.entity.Address;
import com.najmi.fleetshare.entity.FleetOwner;
import com.najmi.fleetshare.entity.Vehicle;
import com.najmi.fleetshare.entity.VehiclePriceHistory;
import com.najmi.fleetshare.repository.AddressRepository;
import com.najmi.fleetshare.repository.FleetOwnerRepository;
import com.najmi.fleetshare.repository.VehiclePriceHistoryRepository;
import com.najmi.fleetshare.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VehicleManagementService {

        @Autowired
        private VehicleRepository vehicleRepository;

        @Autowired
        private VehiclePriceHistoryRepository priceHistoryRepository;

        @Autowired
        private FleetOwnerRepository fleetOwnerRepository;

        @Autowired
        private AddressRepository addressRepository;

        /**
         * Fetches all vehicles with their pricing and owner information
         * 
         * @return List of VehicleDTO objects
         */
        public List<VehicleDTO> getAllVehicles() {
                List<Vehicle> vehicles = vehicleRepository.findAll();
                List<VehicleDTO> vehicleDTOs = new ArrayList<>();

                for (Vehicle vehicle : vehicles) {
                        // Get latest price for this vehicle
                        BigDecimal ratePerDay = priceHistoryRepository
                                        .findLatestPriceByVehicleId(vehicle.getVehicleId())
                                        .map(VehiclePriceHistory::getRatePerDay)
                                        .orElse(BigDecimal.ZERO);

                        // Get fleet owner information
                        FleetOwner owner = fleetOwnerRepository.findById(vehicle.getFleetOwnerId()).orElse(null);
                        String ownerBusinessName = owner != null ? owner.getBusinessName() : "Unknown Owner";
                        String ownerContactPhone = owner != null ? owner.getContactPhone() : "N/A";
                        Boolean ownerIsVerified = owner != null ? owner.getIsVerified() : false;

                        // Get address information
                        String city = "Unknown City";
                        String state = "Unknown State";
                        Double ownerLatitude = null;
                        Double ownerLongitude = null;
                        if (owner != null) {
                                Address address = addressRepository.findLatestAddressByUserId(owner.getUserId())
                                                .orElse(null);
                                if (address != null) {
                                        city = address.getCity();
                                        state = address.getState();
                                        ownerLatitude = address.getLatitude();
                                        ownerLongitude = address.getLongitude();
                                }
                        }

                        VehicleDTO dto = new VehicleDTO(
                                        vehicle.getVehicleId(),
                                        vehicle.getRegistrationNo(),
                                        vehicle.getModel(),
                                        vehicle.getBrand(),
                                        vehicle.getManufacturingYear(),
                                        vehicle.getCategory(),
                                        ratePerDay,
                                        vehicle.getVehicleImageUrl(),
                                        ownerBusinessName,
                                        vehicle.getFuelType(),
                                        vehicle.getTransmissionType(),
                                        vehicle.getMileage(),
                                        vehicle.getStatus() != null ? vehicle.getStatus().name() : "AVAILABLE",
                                        ownerContactPhone,
                                        ownerIsVerified,
                                        city,
                                        state,
                                        ownerLatitude,
                                        ownerLongitude);
                        dto.setFleetOwnerId(vehicle.getFleetOwnerId());
                        vehicleDTOs.add(dto);
                }

                return vehicleDTOs;
        }

        /**
         * Fetches detailed information for a specific vehicle
         * 
         * @param vehicleId Vehicle ID
         * @return VehicleDTO with full details
         */
        public VehicleDTO getVehicleDetails(Long vehicleId) {
                Vehicle vehicle = vehicleRepository.findById(vehicleId).orElse(null);
                if (vehicle == null) {
                        return null;
                }

                // Get latest price
                BigDecimal ratePerDay = priceHistoryRepository.findLatestPriceByVehicleId(vehicleId)
                                .map(VehiclePriceHistory::getRatePerDay)
                                .orElse(BigDecimal.ZERO);

                // Get owner information
                FleetOwner owner = fleetOwnerRepository.findById(vehicle.getFleetOwnerId()).orElse(null);
                String ownerBusinessName = owner != null ? owner.getBusinessName() : "Unknown Owner";
                String ownerContactPhone = owner != null ? owner.getContactPhone() : "N/A";
                Boolean ownerIsVerified = owner != null ? owner.getIsVerified() : false;

                // Get address information
                String city = "Unknown City";
                String state = "Unknown State";
                Double ownerLatitude = null;
                Double ownerLongitude = null;
                if (owner != null) {
                        Address address = addressRepository.findLatestAddressByUserId(owner.getUserId()).orElse(null);
                        if (address != null) {
                                city = address.getCity();
                                state = address.getState();
                                ownerLatitude = address.getLatitude();
                                ownerLongitude = address.getLongitude();
                        }
                }

                VehicleDTO result = new VehicleDTO(
                                vehicle.getVehicleId(),
                                vehicle.getRegistrationNo(),
                                vehicle.getModel(),
                                vehicle.getBrand(),
                                vehicle.getManufacturingYear(),
                                vehicle.getCategory(),
                                ratePerDay,
                                vehicle.getVehicleImageUrl(),
                                ownerBusinessName,
                                vehicle.getFuelType(),
                                vehicle.getTransmissionType(),
                                vehicle.getMileage(),
                                vehicle.getStatus() != null ? vehicle.getStatus().name() : "AVAILABLE",
                                ownerContactPhone,
                                ownerIsVerified,
                                city,
                                state,
                                ownerLatitude,
                                ownerLongitude);
                result.setFleetOwnerId(vehicle.getFleetOwnerId());
                return result;
        }

        /**
         * Fetches all vehicles owned by a specific fleet owner
         * 
         * @param ownerId Fleet owner ID
         * @return List of VehicleDTO objects for owner's vehicles
         */
        public List<VehicleDTO> getVehiclesByOwnerId(Long ownerId) {
                List<Vehicle> vehicles = vehicleRepository.findByFleetOwnerId(ownerId);
                List<VehicleDTO> vehicleDTOs = new ArrayList<>();

                for (Vehicle vehicle : vehicles) {
                        // Get latest price for this vehicle
                        BigDecimal ratePerDay = priceHistoryRepository
                                        .findLatestPriceByVehicleId(vehicle.getVehicleId())
                                        .map(VehiclePriceHistory::getRatePerDay)
                                        .orElse(BigDecimal.ZERO);

                        // Get fleet owner information
                        FleetOwner owner = fleetOwnerRepository.findById(vehicle.getFleetOwnerId()).orElse(null);
                        String ownerBusinessName = owner != null ? owner.getBusinessName() : "Unknown Owner";
                        String ownerContactPhone = owner != null ? owner.getContactPhone() : "N/A";
                        Boolean ownerIsVerified = owner != null ? owner.getIsVerified() : false;

                        // Get address information
                        String city = "Unknown City";
                        String state = "Unknown State";
                        Double ownerLatitude = null;
                        Double ownerLongitude = null;
                        if (owner != null) {
                                Address address = addressRepository.findLatestAddressByUserId(owner.getUserId())
                                                .orElse(null);
                                if (address != null) {
                                        city = address.getCity();
                                        state = address.getState();
                                        ownerLatitude = address.getLatitude();
                                        ownerLongitude = address.getLongitude();
                                }
                        }

                        VehicleDTO dto = new VehicleDTO(
                                        vehicle.getVehicleId(),
                                        vehicle.getRegistrationNo(),
                                        vehicle.getModel(),
                                        vehicle.getBrand(),
                                        vehicle.getManufacturingYear(),
                                        vehicle.getCategory(),
                                        ratePerDay,
                                        vehicle.getVehicleImageUrl(),
                                        ownerBusinessName,
                                        vehicle.getFuelType(),
                                        vehicle.getTransmissionType(),
                                        vehicle.getMileage(),
                                        vehicle.getStatus() != null ? vehicle.getStatus().name() : "AVAILABLE",
                                        ownerContactPhone,
                                        ownerIsVerified,
                                        city,
                                        state,
                                        ownerLatitude,
                                        ownerLongitude);
                        dto.setFleetOwnerId(vehicle.getFleetOwnerId());
                        vehicleDTOs.add(dto);
                }

                return vehicleDTOs;
        }

        /**
         * Creates a new vehicle and its initial price history entry
         * 
         * @param fleetOwnerId Fleet owner ID
         * @param request      AddVehicleRequest with vehicle details
         * @return Created Vehicle entity
         */
        @Transactional
        public Vehicle createVehicle(Long fleetOwnerId, AddVehicleRequest request) {
                // Create vehicle entity
                Vehicle vehicle = new Vehicle();
                vehicle.setFleetOwnerId(fleetOwnerId);
                vehicle.setBrand(request.getBrand());
                vehicle.setModel(request.getModel());
                vehicle.setManufacturingYear(request.getManufacturingYear());
                vehicle.setRegistrationNo(request.getRegistrationNo());
                vehicle.setCategory(request.getCategory());
                vehicle.setFuelType(request.getFuelType());
                vehicle.setTransmissionType(request.getTransmissionType());
                vehicle.setMileage(request.getMileage());
                vehicle.setVehicleImageUrl(request.getVehicleImageUrl());

                // Set status
                if (request.getStatus() != null && !request.getStatus().isEmpty()) {
                        vehicle.setStatus(Vehicle.VehicleStatus.valueOf(request.getStatus()));
                } else {
                        vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
                }

                LocalDateTime now = LocalDateTime.now();
                vehicle.setCreatedAt(now);
                vehicle.setUpdatedAt(now);

                // Save vehicle first to get ID
                Vehicle savedVehicle = vehicleRepository.save(vehicle);

                // Create price history entry if rate per day is provided
                if (request.getRatePerDay() != null && request.getRatePerDay().compareTo(BigDecimal.ZERO) > 0) {
                        VehiclePriceHistory priceHistory = new VehiclePriceHistory();
                        priceHistory.setVehicleId(savedVehicle.getVehicleId());
                        priceHistory.setRatePerDay(request.getRatePerDay());
                        priceHistory.setEffectiveStartDate(now);
                        priceHistoryRepository.save(priceHistory);
                }

                return savedVehicle;
        }

        /**
         * Updates an existing vehicle and optionally updates rate per day
         * 
         * @param vehicleId    Vehicle ID to update
         * @param fleetOwnerId Fleet owner ID (for authorization)
         * @param request      AddVehicleRequest with updated details
         * @return Updated Vehicle entity
         */
        @Transactional
        public Vehicle updateVehicle(Long vehicleId, Long fleetOwnerId, AddVehicleRequest request) {
                Vehicle vehicle = vehicleRepository.findById(vehicleId).orElse(null);
                if (vehicle == null) {
                        throw new RuntimeException("Vehicle not found");
                }

                // Verify ownership
                if (!vehicle.getFleetOwnerId().equals(fleetOwnerId)) {
                        throw new RuntimeException("Unauthorized to update this vehicle");
                }

                // Update vehicle fields
                vehicle.setBrand(request.getBrand());
                vehicle.setModel(request.getModel());
                vehicle.setManufacturingYear(request.getManufacturingYear());
                vehicle.setRegistrationNo(request.getRegistrationNo());
                vehicle.setCategory(request.getCategory());
                vehicle.setFuelType(request.getFuelType());
                vehicle.setTransmissionType(request.getTransmissionType());
                vehicle.setMileage(request.getMileage());

                if (request.getVehicleImageUrl() != null && !request.getVehicleImageUrl().isEmpty()) {
                        vehicle.setVehicleImageUrl(request.getVehicleImageUrl());
                }

                // Update status
                if (request.getStatus() != null && !request.getStatus().isEmpty()) {
                        vehicle.setStatus(Vehicle.VehicleStatus.valueOf(request.getStatus()));
                }

                LocalDateTime now = LocalDateTime.now();
                vehicle.setUpdatedAt(now);

                Vehicle savedVehicle = vehicleRepository.save(vehicle);

                // Update price history if rate changed
                if (request.getRatePerDay() != null && request.getRatePerDay().compareTo(BigDecimal.ZERO) > 0) {
                        // Check if rate has changed
                        BigDecimal currentRate = priceHistoryRepository.findLatestPriceByVehicleId(vehicleId)
                                        .map(VehiclePriceHistory::getRatePerDay)
                                        .orElse(BigDecimal.ZERO);

                        if (request.getRatePerDay().compareTo(currentRate) != 0) {
                                VehiclePriceHistory priceHistory = new VehiclePriceHistory();
                                priceHistory.setVehicleId(vehicleId);
                                priceHistory.setRatePerDay(request.getRatePerDay());
                                priceHistory.setEffectiveStartDate(now);
                                priceHistoryRepository.save(priceHistory);
                        }
                }

                return savedVehicle;
        }

        /**
         * Updates the vehicle image URL
         * 
         * @param vehicleId    Vehicle ID
         * @param fleetOwnerId Fleet owner ID for authorization
         * @param imageUrl     New image URL
         * @return Updated Vehicle
         */
        @Transactional
        public Vehicle updateVehicleImage(Long vehicleId, Long fleetOwnerId, String imageUrl) {
                Vehicle vehicle = vehicleRepository.findById(vehicleId).orElse(null);
                if (vehicle == null) {
                        throw new RuntimeException("Vehicle not found");
                }

                if (!vehicle.getFleetOwnerId().equals(fleetOwnerId)) {
                        throw new RuntimeException("Unauthorized to update this vehicle");
                }

                vehicle.setVehicleImageUrl(imageUrl);
                vehicle.setUpdatedAt(LocalDateTime.now());
                return vehicleRepository.save(vehicle);
        }
}
