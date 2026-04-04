package com.najmi.fleetshare.service;

import com.najmi.fleetshare.entity.MaintenanceSchedule;
import com.najmi.fleetshare.entity.Vehicle;
import com.najmi.fleetshare.repository.MaintenanceScheduleRepository;
import com.najmi.fleetshare.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class MaintenanceScheduleService {

    private static final Logger log = Logger.getLogger(MaintenanceScheduleService.class.getName());

    @Autowired
    private MaintenanceScheduleRepository scheduleRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    /**
     * Validates schedule creation inputs
     * @return Map with keys: valid (boolean), errors (List), warnings (List)
     */
    public Map<String, Object> validateScheduleCreation(MaintenanceSchedule schedule, Long ownerId) {
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // 1. Vehicle exists and belongs to owner
        if (schedule.getVehicleId() == null) {
            errors.add("Vehicle is required");
        } else {
            Vehicle vehicle = vehicleRepository.findById(schedule.getVehicleId()).orElse(null);
            if (vehicle == null) {
                errors.add("Vehicle not found");
            } else if (!vehicle.getFleetOwnerId().equals(ownerId)) {
                errors.add("You do not own this vehicle");
            } else {
                // 5. Check duplicate (same vehicle + maintenance type + is_active)
                List<MaintenanceSchedule> existing = scheduleRepository.findByVehicleId(schedule.getVehicleId());
                boolean hasDuplicate = existing.stream()
                    .filter(s -> s.getIsActive() != null && s.getIsActive())
                    .filter(s -> s.getMaintenanceType() != null && s.getMaintenanceType().equals(schedule.getMaintenanceType()))
                    .anyMatch(s -> true);
                if (hasDuplicate) {
                    warnings.add("An active schedule already exists for this maintenance type on this vehicle");
                }

                // 6. Mileage-based: next due mileage > current mileage
                if (schedule.getFrequencyType() == MaintenanceSchedule.FrequencyType.MILEAGE_BASED) {
                    Integer nextMileage = schedule.getNextDueMileage();
                    Integer currentMileage = vehicle.getMileage();
                    if (nextMileage != null && currentMileage != null && nextMileage <= currentMileage) {
                        warnings.add("Next due mileage should be greater than current vehicle mileage");
                    }
                }
            }
        }

        // 2. Maintenance type required
        if (schedule.getMaintenanceType() == null || schedule.getMaintenanceType().trim().isEmpty()) {
            errors.add("Maintenance type is required");
        }

        // 3. Frequency type and value valid
        if (schedule.getFrequencyType() == null) {
            errors.add("Frequency type is required");
        } else if (schedule.getFrequencyType() != MaintenanceSchedule.FrequencyType.MILEAGE_BASED) {
            if (schedule.getFrequencyValue() == null || schedule.getFrequencyValue() <= 0) {
                errors.add("Frequency value must be positive");
            }
        }

        // 4. Past next due date - warning only (for date-based)
        if (schedule.getFrequencyType() != MaintenanceSchedule.FrequencyType.MILEAGE_BASED) {
            if (schedule.getNextDueDate() != null && schedule.getNextDueDate().isBefore(LocalDate.now())) {
                warnings.add("Next due date is in the past");
            }
        }

        result.put("valid", errors.isEmpty());
        result.put("errors", errors);
        result.put("warnings", warnings);
        return result;
    }

    public List<MaintenanceSchedule> getAllSchedules(Long ownerId) {
        try {
            return scheduleRepository.findByFleetOwnerId(ownerId);
        } catch (Exception e) {
            log.warning("maintenance_schedules table unavailable: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<MaintenanceSchedule> getActiveSchedules(Long ownerId) {
        try {
            return scheduleRepository.findByFleetOwnerIdAndIsActive(ownerId, true);
        } catch (Exception e) {
            log.warning("maintenance_schedules table unavailable: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<MaintenanceSchedule> getSchedulesByVehicle(Long vehicleId) {
        try {
            return scheduleRepository.findByVehicleId(vehicleId);
        } catch (Exception e) {
            log.warning("maintenance_schedules table unavailable: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public MaintenanceSchedule getScheduleById(Long scheduleId) {
        try {
            return scheduleRepository.findById(scheduleId).orElse(null);
        } catch (Exception e) {
            log.warning("maintenance_schedules table unavailable: " + e.getMessage());
            return null;
        }
    }

    public MaintenanceSchedule createSchedule(MaintenanceSchedule schedule) {
        schedule.setCreatedAt(LocalDateTime.now());
        schedule.setUpdatedAt(LocalDateTime.now());
        calculateNextDueDate(schedule);
        return scheduleRepository.save(schedule);
    }

    public MaintenanceSchedule updateSchedule(Long scheduleId, MaintenanceSchedule updated) {
        try {
            return scheduleRepository.findById(scheduleId).map(schedule -> {
                schedule.setMaintenanceType(updated.getMaintenanceType());
                schedule.setDescription(updated.getDescription());
                schedule.setFrequencyType(updated.getFrequencyType());
                schedule.setFrequencyValue(updated.getFrequencyValue());
                schedule.setNextDueDate(updated.getNextDueDate());
                schedule.setNextDueMileage(updated.getNextDueMileage());
                schedule.setEstimatedCost(updated.getEstimatedCost());
                schedule.setIsActive(updated.getIsActive());
                schedule.setNotes(updated.getNotes());
                schedule.setUpdatedAt(LocalDateTime.now());
                return scheduleRepository.save(schedule);
            }).orElse(null);
        } catch (Exception e) {
            log.warning("maintenance_schedules table unavailable: " + e.getMessage());
            return null;
        }
    }

    public void deleteSchedule(Long scheduleId) {
        try {
            scheduleRepository.findById(scheduleId).ifPresent(schedule -> {
                schedule.setIsActive(false);
                schedule.setUpdatedAt(LocalDateTime.now());
                scheduleRepository.save(schedule);
            });
        } catch (Exception e) {
            log.warning("maintenance_schedules table unavailable: " + e.getMessage());
        }
    }

    public List<MaintenanceSchedule> getDueSchedules(Long ownerId, LocalDate date) {
        try {
            return scheduleRepository.findDueSchedules(ownerId, date);
        } catch (Exception e) {
            log.warning("maintenance_schedules table unavailable: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<MaintenanceSchedule> getDueByMileage(Long ownerId, Integer mileage) {
        try {
            return scheduleRepository.findDueByMileage(ownerId, mileage);
        } catch (Exception e) {
            log.warning("maintenance_schedules table unavailable: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void markSchedulePerformed(Long scheduleId, LocalDate performedDate, Integer performedMileage) {
        try {
            scheduleRepository.findById(scheduleId).ifPresent(schedule -> {
                schedule.setLastPerformedDate(performedDate);
                schedule.setLastPerformedMileage(performedMileage);
                calculateNextDueDate(schedule);
                schedule.setUpdatedAt(LocalDateTime.now());
                scheduleRepository.save(schedule);
            });
        } catch (Exception e) {
            log.warning("maintenance_schedules table unavailable: " + e.getMessage());
        }
    }

    private void calculateNextDueDate(MaintenanceSchedule schedule) {
        if (schedule.getFrequencyType() == null || schedule.getFrequencyValue() == null) {
            return;
        }

        LocalDate nextDate = schedule.getNextDueDate();
        Integer nextMileage = schedule.getNextDueMileage();

        switch (schedule.getFrequencyType()) {
            case DAILY -> nextDate = nextDate != null ? nextDate.plusDays(schedule.getFrequencyValue()) : LocalDate.now().plusDays(schedule.getFrequencyValue());
            case WEEKLY -> nextDate = nextDate != null ? nextDate.plusWeeks(schedule.getFrequencyValue()) : LocalDate.now().plusWeeks(schedule.getFrequencyValue());
            case MONTHLY -> nextDate = nextDate != null ? nextDate.plusMonths(schedule.getFrequencyValue()) : LocalDate.now().plusMonths(schedule.getFrequencyValue());
            case YEARLY -> nextDate = nextDate != null ? nextDate.plusYears(schedule.getFrequencyValue()) : LocalDate.now().plusYears(schedule.getFrequencyValue());
            case MILEAGE_BASED -> {
            }
        }

        schedule.setNextDueDate(nextDate);
        schedule.setNextDueMileage(nextMileage);
    }

    public List<MaintenanceSchedule> checkSchedulesDue(Long ownerId) {
        LocalDate today = LocalDate.now();
        List<MaintenanceSchedule> dateDue = scheduleRepository.findDueSchedules(ownerId, today);
        return dateDue;
    }

    public List<MaintenanceSchedule> checkSchedulesDueByMileage(Long ownerId) {
        List<Vehicle> vehicles = vehicleRepository.findByFleetOwnerId(ownerId);
        java.util.List<MaintenanceSchedule> allDue = new java.util.ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getMileage() != null) {
                List<MaintenanceSchedule> due = scheduleRepository.findDueByMileage(ownerId, vehicle.getMileage());
                allDue.addAll(due);
            }
        }
        return allDue;
    }
}
