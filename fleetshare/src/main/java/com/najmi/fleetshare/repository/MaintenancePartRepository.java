package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.MaintenancePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenancePartRepository extends JpaRepository<MaintenancePart, Long> {
    List<MaintenancePart> findByMaintenanceId(Long maintenanceId);
    
    List<MaintenancePart> findByMaintenanceIdOrderByCreatedAtDesc(Long maintenanceId);
}
