package com.najmi.fleetshare.dto;

import java.time.LocalDateTime;

public class FleetOwnerDTO {
    private Long userId;
    private String email;
    private String businessName;
    private String contactPhone;
    private Boolean isVerified;
    private Boolean isActive;
    private LocalDateTime createdAt;

    // Constructors
    public FleetOwnerDTO() {
    }

    public FleetOwnerDTO(Long userId, String email, String businessName, String contactPhone,
            Boolean isVerified, Boolean isActive, LocalDateTime createdAt) {
        this.userId = userId;
        this.email = email;
        this.businessName = businessName;
        this.contactPhone = contactPhone;
        this.isVerified = isVerified;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
