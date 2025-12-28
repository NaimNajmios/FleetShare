package com.najmi.fleetshare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RenterProfileUpdateDTO {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s\\-'.]*$", message = "Full name contains invalid characters")
    private String fullName;

    @Size(max = 20, message = "Phone number must be less than 20 characters")
    @Pattern(regexp = "^\\+?[0-9\\s\\-()]*$", message = "Phone number contains invalid characters")
    private String phoneNumber;

    // Getters and Setters

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
}
