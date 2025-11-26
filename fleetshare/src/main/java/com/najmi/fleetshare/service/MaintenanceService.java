package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.MaintenanceDTO;
import com.najmi.fleetshare.entity.FleetOwner;
import com.najmi.fleetshare.entity.Vehicle;
import com.najmi.fleetshare.entity.VehicleMaintenance;
import com.najmi.fleetshare.repository.FleetOwnerRepository;
import com.najmi.fleetshare.repository.VehicleMaintenanceRepository;
import com.najmi.fleetshare.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MaintenanceService {

    @Autowired
    private VehicleMaintenanceRepository maintenanceRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private FleetOwnerRepository fleetOwnerRepository;

    /**
     * Fetches all maintenance records with vehicle and owner information
     * 
     * @return List of MaintenanceDTO objects
     */
    public List<MaintenanceDTO> getAllMaintenance() {
        List<VehicleMaintenance> maintenanceList = maintenanceRepository.findAll();
        return mapToDTOs(maintenanceList);
    }

    public List<MaintenanceDTO> getMaintenanceByVehicleId(Long vehicleId) {
        List<VehicleMaintenance> maintenanceList = maintenanceRepository.findByVehicleId(vehicleId);
        return mapToDTOs(maintenanceList);
    }

    private List<MaintenanceDTO> mapToDTOs(List<VehicleMaintenance> maintenanceList) {
        List<MaintenanceDTO> maintenanceDTOs = new ArrayList<>();

        for (VehicleMaintenance maintenance : maintenanceList) {
            // Get vehicle information
            Vehicle vehicle = vehicleRepository.findById(maintenance.getVehicleId()).orElse(null);

            // Get fleet owner business name
            String ownerBusinessName = fleetOwnerRepository.findById(maintenance.getFleetOwnerId())
                    .map(FleetOwner::getBusinessName)
                    .orElse("Unknown Owner");

            if (vehicle != null) {
                MaintenanceDTO dto = new MaintenanceDTO(
                        maintenance.getMaintenanceId(),
                        vehicle.getVehicleId(),
                        vehicle.getRegistrationNo(),
                        vehicle.getModel(),
                        vehicle.getBrand(),
                        maintenance.getDescription(),
                        maintenance.getMaintenanceDate(),
                        maintenance.getCost(),
                        maintenance.getStatus() != null ? maintenance.getStatus().name() : "PENDING",
                        ownerBusinessName);
                maintenanceDTOs.add(dto);
            }
        }

        return maintenanceDTOs;
    }
}
