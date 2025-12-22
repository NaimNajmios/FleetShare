package com.najmi.fleetshare.dto;

import java.time.LocalDateTime;

public class PaymentStatusLogDTO {
    private String status;
    private String actorName;
    private LocalDateTime timestamp;

    // Constructors
    public PaymentStatusLogDTO() {
    }

    public PaymentStatusLogDTO(String status, String actorName, LocalDateTime timestamp) {
        this.status = status;
        this.actorName = actorName;
        this.timestamp = timestamp;
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
}
