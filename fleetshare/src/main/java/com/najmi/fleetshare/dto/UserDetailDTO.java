package com.najmi.fleetshare.dto;

public class UserDetailDTO {
    // User basic info
    private Long userId;
    private String email;
    private String userRole;
    private Boolean isActive;

    // Role-specific info (Fleet Owner or Renter)
    private String fullName; // For both
    private String phoneNumber;

    // Fleet Owner specific
    private String businessName;
    private Boolean isVerified;

    // Profile image
    private String profileImageUrl;

    // Address info
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private Double latitude;
    private Double longitude;
    private java.time.LocalDate addressEffectiveStartDate;

    // Additional User Info
    private java.time.LocalDateTime createdAt;

    // Fleet Owner Bank & Integration Info
    private String bankName;
    private String bankAccountNumber;
    private String bankAccountHolder;
    private String paymentQrUrl;
    private String toyyibpaySecretKey;
    private String toyyibpayCategoryCode;
    private String toyyibpayUsername;

    // Constructors
    public UserDetailDTO() {
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

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
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

    public java.time.LocalDate getAddressEffectiveStartDate() {
        return addressEffectiveStartDate;
    }

    public void setAddressEffectiveStartDate(java.time.LocalDate addressEffectiveStartDate) {
        this.addressEffectiveStartDate = addressEffectiveStartDate;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getBankAccountHolder() {
        return bankAccountHolder;
    }

    public void setBankAccountHolder(String bankAccountHolder) {
        this.bankAccountHolder = bankAccountHolder;
    }

    public String getPaymentQrUrl() {
        return paymentQrUrl;
    }

    public void setPaymentQrUrl(String paymentQrUrl) {
        this.paymentQrUrl = paymentQrUrl;
    }

    public String getToyyibpaySecretKey() {
        return toyyibpaySecretKey;
    }

    public void setToyyibpaySecretKey(String toyyibpaySecretKey) {
        this.toyyibpaySecretKey = toyyibpaySecretKey;
    }

    public String getToyyibpayCategoryCode() {
        return toyyibpayCategoryCode;
    }

    public void setToyyibpayCategoryCode(String toyyibpayCategoryCode) {
        this.toyyibpayCategoryCode = toyyibpayCategoryCode;
    }

    public String getToyyibpayUsername() {
        return toyyibpayUsername;
    }

    public void setToyyibpayUsername(String toyyibpayUsername) {
        this.toyyibpayUsername = toyyibpayUsername;
    }
}
