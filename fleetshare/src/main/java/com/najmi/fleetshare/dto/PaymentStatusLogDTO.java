package com.najmi.fleetshare.dto;

import java.time.LocalDateTime;

public class PaymentStatusLogDTO {
    private String status;
    private String actorName;
    private LocalDateTime timestamp;
    private String remarks;

    // Constructors
    public PaymentStatusLogDTO() {
    }

    public PaymentStatusLogDTO(String status, String actorName, LocalDateTime timestamp, String remarks) {
        this.status = status;
        this.actorName = actorName;
        this.timestamp = timestamp;
        this.remarks = remarks;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
