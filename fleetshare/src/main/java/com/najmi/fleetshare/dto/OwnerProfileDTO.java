package com.najmi.fleetshare.dto;

/**
 * DTO for displaying owner profile to renters
 */
public class OwnerProfileDTO {
    private Long fleetOwnerId;
    private String businessName;
    private String contactPhone;
    private Boolean isVerified;
    private String city;
    private String state;
    private Double latitude;
    private Double longitude;
    private int vehicleCount;

    // Constructors
    public OwnerProfileDTO() {
    }

    public OwnerProfileDTO(Long fleetOwnerId, String businessName, String contactPhone,
            Boolean isVerified, String city, String state, Double latitude, Double longitude, int vehicleCount) {
        this.fleetOwnerId = fleetOwnerId;
        this.businessName = businessName;
        this.contactPhone = contactPhone;
        this.isVerified = isVerified;
        this.city = city;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
        this.vehicleCount = vehicleCount;
    }

    // Getters and Setters
    public Long getFleetOwnerId() {
        return fleetOwnerId;
    }

    public void setFleetOwnerId(Long fleetOwnerId) {
        this.fleetOwnerId = fleetOwnerId;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getVehicleCount() {
        return vehicleCount;
    }

    public void setVehicleCount(int vehicleCount) {
        this.vehicleCount = vehicleCount;
    }
}
