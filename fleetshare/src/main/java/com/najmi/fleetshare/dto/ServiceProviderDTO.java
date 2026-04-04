package com.najmi.fleetshare.dto;

import java.time.LocalDate;

public class ServiceProviderDTO {
    private Long providerId;
    private Long fleetOwnerId;
    private String providerName;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private String specialty;
    private java.math.BigDecimal rating;
    private Integer totalJobs;
    private String notes;
    private Boolean isActive;

    public ServiceProviderDTO() {
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public Long getFleetOwnerId() {
        return fleetOwnerId;
    }

    public void setFleetOwnerId(Long fleetOwnerId) {
        this.fleetOwnerId = fleetOwnerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public java.math.BigDecimal getRating() {
        return rating;
    }

    public void setRating(java.math.BigDecimal rating) {
        this.rating = rating;
    }

    public Integer getTotalJobs() {
        return totalJobs;
    }

    public void setTotalJobs(Integer totalJobs) {
        this.totalJobs = totalJobs;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
