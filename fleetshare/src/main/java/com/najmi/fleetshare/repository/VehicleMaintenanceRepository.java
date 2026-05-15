package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.VehicleMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleMaintenanceRepository extends JpaRepository<VehicleMaintenance, Long> {
    List<VehicleMaintenance> findByVehicleId(Long vehicleId);

    List<VehicleMaintenance> findByFleetOwnerId(Long fleetOwnerId);

    List<VehicleMaintenance> findByIsDeletedFalse();

    List<VehicleMaintenance> findByVehicleIdAndIsDeletedFalse(Long vehicleId);

    List<VehicleMaintenance> findByFleetOwnerIdAndIsDeletedFalse(Long fleetOwnerId);

    List<VehicleMaintenance> findByVehicleIdAndCurrentStatus(Long vehicleId, VehicleMaintenance.MaintenanceStatus status);
}
