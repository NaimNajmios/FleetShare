package com.najmi.fleetshare.dto;

import java.time.LocalDateTime;

public class BookingLogDTO {
    private Long logId;
    private String status;
    private LocalDateTime timestamp;
    private String actorName;
    private String remarks;

    public BookingLogDTO() {
    }

    public BookingLogDTO(Long logId, String status, LocalDateTime timestamp, String actorName, String remarks) {
        this.logId = logId;
        this.status = status;
        this.timestamp = timestamp;
        this.actorName = actorName;
        this.remarks = remarks;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
