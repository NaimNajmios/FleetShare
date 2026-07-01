package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.VehicleMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface VehicleMaintenanceRepository extends JpaRepository<VehicleMaintenance, Long> {
    List<VehicleMaintenance> findByVehicleId(Long vehicleId);

    List<VehicleMaintenance> findByFleetOwnerId(Long fleetOwnerId);

    List<VehicleMaintenance> findByIsDeletedFalse();

    List<VehicleMaintenance> findByVehicleIdAndIsDeletedFalse(Long vehicleId);

    List<VehicleMaintenance> findByFleetOwnerIdAndIsDeletedFalse(Long fleetOwnerId);

    Page<VehicleMaintenance> findByIsDeletedFalse(Pageable pageable);

    Page<VehicleMaintenance> findByFleetOwnerIdAndIsDeletedFalse(Long fleetOwnerId, Pageable pageable);

    List<VehicleMaintenance> findByVehicleIdAndCurrentStatus(Long vehicleId, VehicleMaintenance.MaintenanceStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT m FROM VehicleMaintenance m " +
            "LEFT JOIN Vehicle v ON m.vehicleId = v.vehicleId " +
            "LEFT JOIN FleetOwner f ON m.fleetOwnerId = f.fleetOwnerId " +
            "WHERE m.isDeleted = false " +
            "AND (:ownerId IS NULL OR m.fleetOwnerId = :ownerId) " +
            "AND (:status IS NULL OR m.currentStatus = :status) " +
            "AND (:startDate IS NULL OR m.scheduledDate >= :startDate OR CAST(m.actualStartTime AS date) >= :startDate) " +
            "AND (:endDate IS NULL OR m.scheduledDate <= :endDate OR CAST(m.actualStartTime AS date) <= :endDate) " +
            "AND (:search IS NULL OR " +
            "LOWER(v.brand) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(v.model) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(v.registrationNo) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(f.businessName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(m.maintenanceType) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(m.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<VehicleMaintenance> findFilteredMaintenance(
            @org.springframework.data.repository.query.Param("ownerId") Long ownerId,
            @org.springframework.data.repository.query.Param("search") String search,
            @org.springframework.data.repository.query.Param("status") VehicleMaintenance.MaintenanceStatus status,
            @org.springframework.data.repository.query.Param("startDate") java.time.LocalDate startDate,
            @org.springframework.data.repository.query.Param("endDate") java.time.LocalDate endDate,
            Pageable pageable);
}
