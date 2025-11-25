package com.najmi.fleetshare.dto;

import java.io.Serializable;

public class AdminDetails implements Serializable {
    private Long adminId;
    private String fullName;

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
