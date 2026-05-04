package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.BookingDTO;
import com.najmi.fleetshare.entity.FleetOwner;
import com.najmi.fleetshare.entity.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

@Service
public class ToyyibPayService {

    private static final Logger logger = LoggerFactory.getLogger(ToyyibPayService.class);

    @Value("${toyyibpay.api.base-url:https://dev.toyyibpay.com}")
    private String apiBaseUrl;

    @Value("${toyyibpay.bill.expiry-days:3}")
    private int billExpiryDays;

    @Value("${toyyibpay.bill.payment-channel:2}")
    private String billPaymentChannel;

    @Value("${app.base-url:http://localhost:8080}")
    private String appBaseUrl;

    // Platform credentials for Central Payout
    @Value("${toyyibpay.platform.secret-key:}")
    private String platformSecretKey;

    @Value("${toyyibpay.platform.category-code:}")
    private String platformCategoryCode;

    @Value("${toyyibpay.platform.username:}")
    private String platformUsername;

    private final RestTemplate restTemplate;

    public ToyyibPayService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Checks if the platform has valid credentials configured for central payout.
     *
     * @return true if platform credentials are configured
     */
    public boolean isPlatformConfigured() {
        return platformSecretKey != null && !platformSecretKey.isEmpty()
                && platformCategoryCode != null && !platformCategoryCode.isEmpty();
    }

    /**
     * Returns the platform's secret key for callback hash validation.
     *
     * @return The platform secret key
     */
    public String getPlatformSecretKey() {
        return platformSecretKey;
    }

    /**
     * Creates a bill on ToyyibPay using the Central Payout model (platform credentials + split payment).
     * Falls back to BYOK (owner credentials) if platform is not configured or owner lacks a username.
     *
     * @param owner              The fleet owner
     * @param payment            The payment entity
     * @param booking            The booking details
     * @param renterEmail        Renter's email address
     * @param renterPhone        Renter's phone number
     * @param renterName         Renter's full name
     * @param platformCommission Platform commission amount (null for BYOK fallback)
     * @param ownerPayout        Owner payout amount (null for BYOK fallback)
     * @return The bill code returned by ToyyibPay
     */
    public String createBill(FleetOwner owner, Payment payment, BookingDTO booking,
                             String renterEmail, String renterPhone, String renterName,
                             BigDecimal platformCommission, BigDecimal ownerPayout) {

        // Determine which mode to use: Central Payout or BYOK fallback
        boolean useCentralPayout = isPlatformConfigured()
                && owner.getToyyibpayUsername() != null
                && !owner.getToyyibpayUsername().isEmpty()
                && ownerPayout != null
                && ownerPayout.compareTo(BigDecimal.ZERO) > 0;

        boolean useByokFallback = !useCentralPayout
                && owner.getToyyibpaySecretKey() != null
                && !owner.getToyyibpaySecretKey().isEmpty()
                && owner.getToyyibpayCategoryCode() != null
                && !owner.getToyyibpayCategoryCode().isEmpty();

        if (!useCentralPayout && !useByokFallback) {
            throw new IllegalStateException(
                    "Payment cannot be processed: neither platform credentials nor owner BYOK credentials are configured.");
        }

        String secretKey;
        String categoryCode;

        if (useCentralPayout) {
            secretKey = platformSecretKey;
            categoryCode = platformCategoryCode;
            logger.info("Using CENTRAL PAYOUT mode for owner: {} (username: {})",
                    owner.getBusinessName(), owner.getToyyibpayUsername());
        } else {
            secretKey = owner.getToyyibpaySecretKey();
            categoryCode = owner.getToyyibpayCategoryCode();
            logger.info("Using BYOK FALLBACK mode for owner: {}", owner.getBusinessName());
        }

        // Convert amount to cents (ToyyibPay expects amount in cents)
        BigDecimal amountInCents = payment.getAmount().multiply(BigDecimal.valueOf(100));
        int amountCents = amountInCents.intValue();

        String billName = truncate("Booking #" + booking.getBookingId(), 30);
        String vehicleDesc = (booking.getVehicleBrand() != null ? booking.getVehicleBrand() : "") +
                             " " +
                             (booking.getVehicleModel() != null ? booking.getVehicleModel() : "");
        String billDescription = truncate("Vehicle rental - " + vehicleDesc.trim(), 100);
        String externalRef = "FS-" + booking.getBookingId() + "-" + System.currentTimeMillis();

        // Build form data
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("userSecretKey", secretKey);
        formData.add("categoryCode", categoryCode);
        formData.add("billName", billName);
        formData.add("billDescription", billDescription);
        formData.add("billPriceSetting", "1"); // Fixed price
        formData.add("billPayorInfo", "1"); // Require payer info
        formData.add("billAmount", String.valueOf(amountCents));
        formData.add("billReturnUrl", appBaseUrl + "/api/payment/toyyibpay/return");
        formData.add("billCallbackUrl", appBaseUrl + "/api/payment/toyyibpay/callback");
        formData.add("billExternalReferenceNo", externalRef);
        formData.add("billTo", renterName != null ? truncate(renterName, 100) : "");
        formData.add("billEmail", renterEmail != null ? renterEmail : "");
        formData.add("billPhone", renterPhone != null ? renterPhone : "");

        // Split Payment configuration
        if (useCentralPayout) {
            formData.add("billSplitPayment", "1");
            int ownerPayoutCents = ownerPayout.multiply(BigDecimal.valueOf(100)).intValue();
            String splitArgs = String.format(
                "[{\"id\":\"%s\",\"amount\":\"%d\"}]",
                owner.getToyyibpayUsername(), ownerPayoutCents);
            formData.add("billSplitPaymentArgs", splitArgs);
            // Restrict to FPX only when split payment is enabled (split only works with FPX)
            formData.add("billPaymentChannel", "0");
            logger.info("Split payment enabled: owner {} gets {} cents, platform gets {} cents",
                    owner.getToyyibpayUsername(), ownerPayoutCents,
                    platformCommission != null ? platformCommission.multiply(BigDecimal.valueOf(100)).intValue() : 0);
        } else {
            formData.add("billSplitPayment", "0");
            formData.add("billSplitPaymentArgs", "");
            formData.add("billPaymentChannel", billPaymentChannel);
        }

        formData.add("billChargeToCustomer", "1");
        formData.add("billExpiryDays", String.valueOf(billExpiryDays));

        logger.info("Creating ToyyibPay bill for booking #{} with amount {} cents (mode: {})",
                     booking.getBookingId(), amountCents, useCentralPayout ? "CENTRAL_PAYOUT" : "BYOK");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

            // ToyyibPay returns Content-Type: text/html even for JSON responses,
            // so we must read as String and parse manually
            ResponseEntity<String> response = restTemplate.postForEntity(
                    apiBaseUrl + "/index.php/api/createBill",
                    request,
                    String.class
            );

            logger.info("ToyyibPay createBill raw response: {}", response.getBody());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String body = response.getBody().trim();

                // Parse JSON array response
                ObjectMapper mapper = new ObjectMapper();
                List<Map<String, Object>> resultList = mapper.readValue(body,
                        new TypeReference<List<Map<String, Object>>>() {});

                if (!resultList.isEmpty()) {
                    String billCode = (String) resultList.get(0).get("BillCode");

                    if (billCode != null && !billCode.isEmpty()) {
                        logger.info("ToyyibPay bill created successfully: {}", billCode);
                        return billCode;
                    } else {
                        logger.error("ToyyibPay returned empty bill code. Response: {}", body);
                        throw new RuntimeException("ToyyibPay returned empty bill code. Response: " + body);
                    }
                } else {
                    logger.error("ToyyibPay returned empty response list. Body: {}", body);
                    throw new RuntimeException("ToyyibPay returned empty response: " + body);
                }
            } else {
                logger.error("ToyyibPay createBill failed. Status: {}, Body: {}", 
                             response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to create ToyyibPay bill: " + response.getBody());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating ToyyibPay bill: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create ToyyibPay bill: " + e.getMessage(), e);
        }
    }

    /**
     * Validates the callback hash from ToyyibPay to prevent tampering.
     *
     * @param userSecretKey Secret key used to create the bill (platform or owner)
     * @param status        Payment status from callback
     * @param orderId       External reference number
     * @param refno         Payment reference number
     * @param receivedHash  The hash received from ToyyibPay
     * @return true if hash is valid
     */
    public boolean validateCallbackHash(String userSecretKey, String status,
                                         String orderId, String refno, String receivedHash) {
        try {
            String rawString = userSecretKey + status + orderId + refno + "ok";
            String expectedHash = md5Hash(rawString);
            
            boolean isValid = expectedHash.equalsIgnoreCase(receivedHash);
            if (!isValid) {
                logger.warn("ToyyibPay callback hash validation failed. Expected: {}, Received: {}", 
                            expectedHash, receivedHash);
            }
            return isValid;
        } catch (Exception e) {
            logger.error("Error validating callback hash: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Gets the ToyyibPay payment page URL for a bill code.
     *
     * @param billCode The bill code
     * @return Full URL to the payment page
     */
    public String getPaymentUrl(String billCode) {
        return apiBaseUrl + "/" + billCode;
    }

    /**
     * Generates MD5 hash of a string.
     */
    private String md5Hash(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Truncates a string to the specified maximum length.
     */
    private String truncate(String value, int maxLength) {
        if (value == null) return "";
        // ToyyibPay only allows alphanumeric, space, and underscore for billName/billDescription
        String cleaned = value.replaceAll("[^a-zA-Z0-9 _#\\-]", "");
        return cleaned.length() > maxLength ? cleaned.substring(0, maxLength) : cleaned;
    }
}
