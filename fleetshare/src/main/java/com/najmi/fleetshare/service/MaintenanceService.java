package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.MaintenanceDTO;
import com.najmi.fleetshare.entity.FleetOwner;
import com.najmi.fleetshare.entity.Vehicle;
import com.najmi.fleetshare.entity.VehicleMaintenance;
import com.najmi.fleetshare.entity.VehicleMaintenanceLog;
import com.najmi.fleetshare.repository.FleetOwnerRepository;
import com.najmi.fleetshare.repository.VehicleMaintenanceLogRepository;
import com.najmi.fleetshare.repository.VehicleMaintenanceRepository;
import com.najmi.fleetshare.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MaintenanceService {

    @Autowired
    private VehicleMaintenanceRepository maintenanceRepository;

    @Autowired
    private VehicleMaintenanceLogRepository maintenanceLogRepository;

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
                        maintenance.getScheduledDate(),
                        maintenance.getActualStartTime(),
                        maintenance.getActualEndTime(),
                        maintenance.getEstimatedCost(),
                        maintenance.getFinalCost(),
                        maintenance.getCurrentStatus() != null ? maintenance.getCurrentStatus().name() : "PENDING",
                        ownerBusinessName);
                maintenanceDTOs.add(dto);
            }
        }

        return maintenanceDTOs;
    }

    /**
     * Fetches all maintenance records for a specific fleet owner
     * 
     * @param ownerId Fleet owner ID
     * @return List of MaintenanceDTO objects for owner's vehicles
     */
    public List<MaintenanceDTO> getMaintenanceByOwnerId(Long ownerId) {
        List<VehicleMaintenance> maintenanceList = maintenanceRepository.findByFleetOwnerId(ownerId);
        return mapToDTOs(maintenanceList);
    }

    /**
     * Get maintenance status logs for a specific maintenance record
     */
    public List<VehicleMaintenanceLog> getMaintenanceLogs(Long maintenanceId) {
        return maintenanceLogRepository.findByMaintenanceIdOrderByLogTimestampDesc(maintenanceId);
    }

    /**
     * Add a new maintenance record
     */
    public VehicleMaintenance addMaintenance(MaintenanceDTO dto, Long actorUserId) {
        VehicleMaintenance maintenance = new VehicleMaintenance();
        maintenance.setVehicleId(dto.getVehicleId());
        maintenance.setDescription(dto.getDescription());
        maintenance.setScheduledDate(dto.getScheduledDate());
        maintenance.setEstimatedCost(dto.getEstimatedCost());
        maintenance.setCreatedAt(LocalDateTime.now());

        // Set status, default to PENDING if null
        if (dto.getStatus() != null) {
            try {
                maintenance.setCurrentStatus(
                        VehicleMaintenance.MaintenanceStatus.valueOf(dto.getStatus()));
            } catch (IllegalArgumentException e) {
                maintenance.setCurrentStatus(VehicleMaintenance.MaintenanceStatus.PENDING);
            }
        } else {
            maintenance.setCurrentStatus(VehicleMaintenance.MaintenanceStatus.PENDING);
        }

        // Set fleet owner ID from vehicle
        vehicleRepository.findById(dto.getVehicleId()).ifPresent(vehicle -> {
            maintenance.setFleetOwnerId(vehicle.getFleetOwnerId());
        });

        VehicleMaintenance saved = maintenanceRepository.save(maintenance);

        // Log the initial status
        VehicleMaintenanceLog log = new VehicleMaintenanceLog(
                saved.getMaintenanceId(),
                saved.getCurrentStatus(),
                actorUserId,
                "Maintenance scheduled");
        maintenanceLogRepository.save(log);

        return saved;
    }

    /**
     * Update maintenance status and log the change
     */
    public void updateMaintenanceStatus(Long maintenanceId, VehicleMaintenance.MaintenanceStatus newStatus,
            Long actorUserId, String remarks) {
        maintenanceRepository.findById(maintenanceId).ifPresent(maintenance -> {
            maintenance.setCurrentStatus(newStatus);

            // Update actual times based on status
            if (newStatus == VehicleMaintenance.MaintenanceStatus.IN_PROGRESS
                    && maintenance.getActualStartTime() == null) {
                maintenance.setActualStartTime(LocalDateTime.now());
            } else if (newStatus == VehicleMaintenance.MaintenanceStatus.COMPLETED
                    && maintenance.getActualEndTime() == null) {
                maintenance.setActualEndTime(LocalDateTime.now());
            }

            maintenanceRepository.save(maintenance);

            // Log the status change
            VehicleMaintenanceLog log = new VehicleMaintenanceLog(
                    maintenanceId,
                    newStatus,
                    actorUserId,
                    remarks);
            maintenanceLogRepository.save(log);
        });
    }
}
