package com.najmi.fleetshare;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPasswordEncoder {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String plainPassword = "password";
        String hashedFromDB = "$2a$12$SSWDxpmB./hwm9gpSL4/bZ.2sUurW4t9qTLSfvJLHz25..."; // From screenshot

        // Test if the password matches
        System.out.println("Testing password: " + plainPassword);
        System.out.println("Hash from DB: " + hashedFromDB);

        // Generate a new hash for comparison
        String newHash = encoder.encode(plainPassword);
        System.out.println("Newly generated hash: " + newHash);

        // Test matching
        boolean matches = encoder.matches(plainPassword, hashedFromDB);
        System.out.println("Password matches DB hash: " + matches);

        // The hash from the original SQL dump
        String originalHash = "$2a$12$23a1K6vRlzZJWOejUT5K2OVp3gEz1C6X8E3eaFVYgXHsZWjP636eO";
        boolean matchesOriginal = encoder.matches(plainPassword, originalHash);
        System.out.println("Password matches original SQL dump hash: " + matchesOriginal);
    }
}
