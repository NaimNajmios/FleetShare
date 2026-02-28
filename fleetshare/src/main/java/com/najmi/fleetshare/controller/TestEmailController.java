package com.najmi.fleetshare.controller;

import com.najmi.fleetshare.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Temporary controller to test email functionality.
 * TODO: Remove this once actual use cases are implemented.
 */
@RestController
@RequestMapping("/api/test/email")
public class TestEmailController {

    private final EmailService emailService;

    @Autowired
    public TestEmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/html")
    public ResponseEntity<String> sendTestHtmlEmail(@RequestParam String to) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("subject", "Test HTML Email from FleetShare");
        templateModel.put("name", "Test User");
        templateModel.put("message",
                "If you are reading this, the FleetShare Email Service is working correctly with Thymeleaf templates!");

        emailService.sendHtmlEmail(
                to,
                "Test HTML Email from FleetShare",
                "email/test-email",
                templateModel);

        return ResponseEntity.ok("Async HTML email requested to be sent to " + to + ". Check your console/logs.");
    }

    @GetMapping("/simple")
    public ResponseEntity<String> sendTestSimpleEmail(@RequestParam String to) {
        emailService.sendSimpleMessage(
                to,
                "Test Simple Email from FleetShare",
                "If you are reading this, the simple non-HTML email is working correctly.");
        return ResponseEntity.ok("Async simple email requested to be sent to " + to + ". Check your console/logs.");
    }
}
