package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.VehicleMaintenanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleMaintenanceLogRepository extends JpaRepository<VehicleMaintenanceLog, Long> {

    List<VehicleMaintenanceLog> findByMaintenanceIdOrderByLogTimestampDesc(Long maintenanceId);
}
