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

    long countByStatus(Vehicle.VehicleStatus status);
    long countByFleetOwnerIdAndStatusAndIsDeletedFalse(Long fleetOwnerId, Vehicle.VehicleStatus status);

    List<Vehicle> findByFleetOwnerIdAndIsDeletedFalse(Long fleetOwnerId);
    org.springframework.data.domain.Page<Vehicle> findByFleetOwnerIdAndIsDeletedFalse(Long fleetOwnerId, org.springframework.data.domain.Pageable pageable);

    List<Vehicle> findByIsDeletedFalse();
    org.springframework.data.domain.Page<Vehicle> findByIsDeletedFalse(org.springframework.data.domain.Pageable pageable);

    List<Vehicle> findByStatusAndIsDeletedFalse(Vehicle.VehicleStatus status);
    org.springframework.data.domain.Page<Vehicle> findByStatusAndIsDeletedFalse(Vehicle.VehicleStatus status, org.springframework.data.domain.Pageable pageable);

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

    @Query("SELECT v FROM Vehicle v WHERE v.isDeleted = false " +
           "AND (:status IS NULL OR v.status = :status) " +
           "AND (:year IS NULL OR v.manufacturingYear = :year) " +
           "AND (:category IS NULL OR v.category = :category) " +
           "AND (:term IS NULL OR LOWER(v.brand) LIKE LOWER(CONCAT('%', :term, '%')) " +
           "OR LOWER(v.model) LIKE LOWER(CONCAT('%', :term, '%')) " +
           "OR LOWER(v.registrationNo) LIKE LOWER(CONCAT('%', :term, '%')))")
    org.springframework.data.domain.Page<Vehicle> findVehiclesWithFilters(
            @Param("term") String term,
            @Param("year") Integer year,
            @Param("category") String category,
            @Param("status") Vehicle.VehicleStatus status,
            org.springframework.data.domain.Pageable pageable);

    @Query("SELECT v FROM Vehicle v WHERE v.isDeleted = false AND v.fleetOwnerId = :ownerId " +
           "AND (:status IS NULL OR v.status = :status) " +
           "AND (:year IS NULL OR v.manufacturingYear = :year) " +
           "AND (:category IS NULL OR v.category = :category) " +
           "AND (:term IS NULL OR LOWER(v.brand) LIKE LOWER(CONCAT('%', :term, '%')) " +
           "OR LOWER(v.model) LIKE LOWER(CONCAT('%', :term, '%')) " +
           "OR LOWER(v.registrationNo) LIKE LOWER(CONCAT('%', :term, '%')))")
    org.springframework.data.domain.Page<Vehicle> findOwnerVehiclesWithFilters(
            @Param("ownerId") Long ownerId,
            @Param("term") String term,
            @Param("year") Integer year,
            @Param("category") String category,
            @Param("status") Vehicle.VehicleStatus status,
            org.springframework.data.domain.Pageable pageable);
}
