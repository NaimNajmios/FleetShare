package com.najmi.fleetshare.service;

import java.util.Map;

/**
 * Service interface for handling email operations.
 */
public interface EmailService {

    /**
     * Sends an HTML formatted email using a Thymeleaf template.
     * 
     * @param to            The recipient's email address
     * @param subject       The email subject
     * @param templateName  The name of the Thymeleaf template (e.g.,
     *                      "email/welcome-email")
     * @param templateModel A map of variables to be processed by the template
     */
    void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> templateModel);

    /**
     * Sends a simple text email.
     * 
     * @param to      The recipient's email address
     * @param subject The email subject
     * @param text    The plain text content
     */
    void sendSimpleMessage(String to, String subject, String text);
}
