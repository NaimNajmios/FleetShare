package com.najmi.fleetshare.dto;

import com.najmi.fleetshare.entity.UserRole;
import java.io.Serializable;
import java.time.LocalDateTime;

public class SessionUser implements Serializable {
    private Long userId;
    private String email;
    private UserRole role;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private Boolean isActive;

    // Role-specific data
    private AdminDetails adminDetails;
    private OwnerDetails ownerDetails;
    private RenterDetails renterDetails;

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

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public AdminDetails getAdminDetails() {
        return adminDetails;
    }

    public void setAdminDetails(AdminDetails adminDetails) {
        this.adminDetails = adminDetails;
    }

    public OwnerDetails getOwnerDetails() {
        return ownerDetails;
    }

    public void setOwnerDetails(OwnerDetails ownerDetails) {
        this.ownerDetails = ownerDetails;
    }

    public RenterDetails getRenterDetails() {
        return renterDetails;
    }

    public void setRenterDetails(RenterDetails renterDetails) {
        this.renterDetails = renterDetails;
    }
}
