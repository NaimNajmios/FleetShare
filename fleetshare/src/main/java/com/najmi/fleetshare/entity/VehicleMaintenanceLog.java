package com.najmi.fleetshare.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehiclemaintenancelog")
public class VehicleMaintenanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maintenance_log_id")
    private Long maintenanceLogId;

    @Column(name = "maintenance_id", nullable = false)
    private Long maintenanceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_value", nullable = false)
    private VehicleMaintenance.MaintenanceStatus statusValue;

    @Column(name = "actor_user_id")
    private Long actorUserId;

    @Column(name = "log_timestamp", nullable = false)
    private LocalDateTime logTimestamp;

    @Column(name = "remarks", length = 500)
    private String remarks;

    // Constructors
    public VehicleMaintenanceLog() {
    }

    public VehicleMaintenanceLog(Long maintenanceId, VehicleMaintenance.MaintenanceStatus statusValue,
            Long actorUserId, String remarks) {
        this.maintenanceId = maintenanceId;
        this.statusValue = statusValue;
        this.actorUserId = actorUserId;
        this.logTimestamp = LocalDateTime.now();
        this.remarks = remarks;
    }

    // Getters and Setters
    public Long getMaintenanceLogId() {
        return maintenanceLogId;
    }

    public void setMaintenanceLogId(Long maintenanceLogId) {
        this.maintenanceLogId = maintenanceLogId;
    }

    public Long getMaintenanceId() {
        return maintenanceId;
    }

    public void setMaintenanceId(Long maintenanceId) {
        this.maintenanceId = maintenanceId;
    }

    public VehicleMaintenance.MaintenanceStatus getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(VehicleMaintenance.MaintenanceStatus statusValue) {
        this.statusValue = statusValue;
    }

    public Long getActorUserId() {
        return actorUserId;
    }

    public void setActorUserId(Long actorUserId) {
        this.actorUserId = actorUserId;
    }

    public LocalDateTime getLogTimestamp() {
        return logTimestamp;
    }

    public void setLogTimestamp(LocalDateTime logTimestamp) {
        this.logTimestamp = logTimestamp;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
