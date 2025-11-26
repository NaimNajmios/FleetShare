package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.VehicleDTO;
import com.najmi.fleetshare.entity.FleetOwner;
import com.najmi.fleetshare.entity.Vehicle;
import com.najmi.fleetshare.entity.VehiclePriceHistory;
import com.najmi.fleetshare.repository.FleetOwnerRepository;
import com.najmi.fleetshare.repository.VehiclePriceHistoryRepository;
import com.najmi.fleetshare.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
            BigDecimal ratePerDay = priceHistoryRepository.findLatestPriceByVehicleId(vehicle.getVehicleId())
                    .map(VehiclePriceHistory::getRatePerDay)
                    .orElse(BigDecimal.ZERO);

            // Get fleet owner business name
            String ownerBusinessName = fleetOwnerRepository.findById(vehicle.getFleetOwnerId())
                    .map(FleetOwner::getBusinessName)
                    .orElse("Unknown Owner");

            VehicleDTO dto = new VehicleDTO(
                    vehicle.getVehicleId(),
                    vehicle.getRegistrationNo(),
                    vehicle.getModel(),
                    vehicle.getBrand(),
                    vehicle.getManufacturingYear(),
                    vehicle.getCategory(),
                    ratePerDay,
                    vehicle.getVehicleImageUrl(),
                    ownerBusinessName);
            vehicleDTOs.add(dto);
        }

        return vehicleDTOs;
    }
}
