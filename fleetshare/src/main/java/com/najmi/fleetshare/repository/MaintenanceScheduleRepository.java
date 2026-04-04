package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.MaintenanceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule, Long> {
    List<MaintenanceSchedule> findByFleetOwnerId(Long fleetOwnerId);
    
    List<MaintenanceSchedule> findByFleetOwnerIdAndIsActive(Long fleetOwnerId, Boolean isActive);
    
    List<MaintenanceSchedule> findByVehicleId(Long vehicleId);
    
    @Query("SELECT s FROM MaintenanceSchedule s WHERE s.fleetOwnerId = :ownerId AND s.nextDueDate <= :date AND s.isActive = true")
    List<MaintenanceSchedule> findDueSchedules(Long ownerId, LocalDate date);
    
    @Query("SELECT s FROM MaintenanceSchedule s WHERE s.fleetOwnerId = :ownerId AND s.nextDueMileage <= :mileage AND s.isActive = true")
    List<MaintenanceSchedule> findDueByMileage(Long ownerId, Integer mileage);
}
