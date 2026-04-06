package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByFleetOwnerId(Long fleetOwnerId);

    List<Vehicle> findByFleetOwnerIdAndIsDeletedFalse(Long fleetOwnerId);

    List<Vehicle> findByIsDeletedFalse();

    List<Vehicle> findByStatusAndIsDeletedFalse(Vehicle.VehicleStatus status);

    @Query("SELECT v FROM Vehicle v WHERE v.isDeleted = false AND (" +
           "LOWER(v.brand) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(v.model) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(v.registrationNo) LIKE LOWER(CONCAT('%', :term, '%')))")
    List<Vehicle> searchAll(@Param("term") String term);

    @Query("SELECT v FROM Vehicle v WHERE v.isDeleted = false AND v.fleetOwnerId = :ownerId AND (" +
           "LOWER(v.brand) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(v.model) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(v.registrationNo) LIKE LOWER(CONCAT('%', :term, '%')))")
    List<Vehicle> searchByOwner(@Param("ownerId") Long ownerId, @Param("term") String term);
}
