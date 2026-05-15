package com.najmi.fleetshare.dto;

import java.time.LocalDate;

public class AgendaItemDTO {
    private String dateLabel;
    private LocalDate date;
    private String type;
    private String title;
    private String description;
    private String status;
    private String urgency;
    private String actionUrl;

    public AgendaItemDTO() {}

    public AgendaItemDTO(String dateLabel, LocalDate date, String type, String title,
                         String description, String status, String urgency, String actionUrl) {
        this.dateLabel = dateLabel;
        this.date = date;
        this.type = type;
        this.title = title;
        this.description = description;
        this.status = status;
        this.urgency = urgency;
        this.actionUrl = actionUrl;
    }

    public String getDateLabel() { return dateLabel; }
    public void setDateLabel(String dateLabel) { this.dateLabel = dateLabel; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public String getActionUrl() { return actionUrl; }
    public void setActionUrl(String actionUrl) { this.actionUrl = actionUrl; }
}
