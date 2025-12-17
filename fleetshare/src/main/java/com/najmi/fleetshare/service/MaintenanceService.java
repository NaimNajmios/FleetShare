package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.MaintenanceDTO;
import com.najmi.fleetshare.dto.MaintenanceLogDTO;
import com.najmi.fleetshare.entity.User;
import com.najmi.fleetshare.entity.FleetOwner;
import com.najmi.fleetshare.entity.Vehicle;
import com.najmi.fleetshare.entity.VehicleMaintenance;
import com.najmi.fleetshare.entity.VehicleMaintenanceLog;
import com.najmi.fleetshare.repository.FleetOwnerRepository;
import com.najmi.fleetshare.repository.VehicleMaintenanceLogRepository;
import com.najmi.fleetshare.repository.VehicleMaintenanceRepository;
import com.najmi.fleetshare.repository.VehicleRepository;
import com.najmi.fleetshare.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

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
     * Get a single maintenance record by ID
     */
    public MaintenanceDTO getMaintenanceById(Long maintenanceId) {
        return maintenanceRepository.findById(maintenanceId)
                .map(maintenance -> {
                    Vehicle vehicle = vehicleRepository.findById(maintenance.getVehicleId()).orElse(null);
                    String ownerBusinessName = fleetOwnerRepository.findById(maintenance.getFleetOwnerId())
                            .map(FleetOwner::getBusinessName)
                            .orElse("Unknown Owner");

                    if (vehicle != null) {
                        return new MaintenanceDTO(
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
                                maintenance.getCurrentStatus() != null ? maintenance.getCurrentStatus().name()
                                        : "PENDING",
                                ownerBusinessName);
                    }
                    return null;
                })
                .orElse(null);
    }

    /**
     * Get maintenance logs as DTOs with actor names resolved
     */
    public List<MaintenanceLogDTO> getMaintenanceLogsDTO(Long maintenanceId) {
        List<VehicleMaintenanceLog> logs = maintenanceLogRepository
                .findByMaintenanceIdOrderByLogTimestampDesc(maintenanceId);
        List<MaintenanceLogDTO> logDTOs = new ArrayList<>();

        for (VehicleMaintenanceLog log : logs) {
            String actorName = "System";
            if (log.getActorUserId() != null) {
                actorName = userRepository.findById(log.getActorUserId())
                        .map(User::getEmail)
                        .orElse("Unknown User");
            }

            logDTOs.add(new MaintenanceLogDTO(
                    log.getMaintenanceLogId(),
                    log.getStatusValue().name(),
                    log.getLogTimestamp(),
                    actorName,
                    log.getRemarks()));
        }

        return logDTOs;
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

    /**
     * Get maintenance statistics for all records (admin dashboard)
     */
    public com.najmi.fleetshare.dto.MaintenanceStatsDTO getMaintenanceStats() {
        List<VehicleMaintenance> allMaintenance = maintenanceRepository.findAll();
        return calculateStats(allMaintenance);
    }

    /**
     * Get maintenance statistics for a specific fleet owner
     */
    public com.najmi.fleetshare.dto.MaintenanceStatsDTO getMaintenanceStatsByOwnerId(Long ownerId) {
        List<VehicleMaintenance> ownerMaintenance = maintenanceRepository.findByFleetOwnerId(ownerId);
        return calculateStats(ownerMaintenance);
    }

    /**
     * Calculate statistics from maintenance list
     */
    private com.najmi.fleetshare.dto.MaintenanceStatsDTO calculateStats(List<VehicleMaintenance> maintenanceList) {
        com.najmi.fleetshare.dto.MaintenanceStatsDTO stats = new com.najmi.fleetshare.dto.MaintenanceStatsDTO();

        java.math.BigDecimal totalEstimated = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalFinal = java.math.BigDecimal.ZERO;
        java.util.Map<String, Integer> monthlyCount = new java.util.LinkedHashMap<>();
        java.util.Map<String, java.math.BigDecimal> monthlyCost = new java.util.LinkedHashMap<>();

        // Initialize last 12 months for better historical view
        java.time.LocalDate now = java.time.LocalDate.now();
        for (int i = 11; i >= 0; i--) {
            java.time.LocalDate month = now.minusMonths(i);
            String monthKey = month.getMonth().toString().substring(0, 3);
            monthlyCount.put(monthKey, 0);
            monthlyCost.put(monthKey, java.math.BigDecimal.ZERO);
        }

        for (VehicleMaintenance m : maintenanceList) {
            // Count by status
            if (m.getCurrentStatus() != null) {
                switch (m.getCurrentStatus()) {
                    case PENDING -> stats.setPendingCount(stats.getPendingCount() + 1);
                    case IN_PROGRESS -> stats.setInProgressCount(stats.getInProgressCount() + 1);
                    case COMPLETED -> stats.setCompletedCount(stats.getCompletedCount() + 1);
                    case CANCELLED -> stats.setCancelledCount(stats.getCancelledCount() + 1);
                }
            }

            // Sum costs
            if (m.getEstimatedCost() != null) {
                totalEstimated = totalEstimated.add(m.getEstimatedCost());
            }
            if (m.getFinalCost() != null) {
                totalFinal = totalFinal.add(m.getFinalCost());
            }

            // Monthly aggregation - include all data in the chart
            if (m.getScheduledDate() != null) {
                java.time.LocalDate schedDate = m.getScheduledDate();
                String monthKey = schedDate.getMonth().toString().substring(0, 3);
                if (monthlyCount.containsKey(monthKey)) {
                    monthlyCount.put(monthKey, monthlyCount.get(monthKey) + 1);
                    java.math.BigDecimal cost = m.getFinalCost() != null ? m.getFinalCost()
                            : (m.getEstimatedCost() != null ? m.getEstimatedCost() : java.math.BigDecimal.ZERO);
                    monthlyCost.put(monthKey, monthlyCost.get(monthKey).add(cost));
                }
            }
        }

        stats.setTotalCount(maintenanceList.size());
        stats.setTotalEstimatedCost(totalEstimated);
        stats.setTotalFinalCost(totalFinal);
        stats.setMonthlyCountData(monthlyCount);
        stats.setMonthlyCostData(monthlyCost);

        if (!maintenanceList.isEmpty()) {
            stats.setAvgCostPerMaintenance(totalFinal.divide(
                    java.math.BigDecimal.valueOf(maintenanceList.size()), 2, java.math.RoundingMode.HALF_UP));
        }

        return stats;
    }
}
