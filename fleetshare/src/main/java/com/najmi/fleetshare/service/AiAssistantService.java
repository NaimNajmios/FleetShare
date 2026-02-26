package com.najmi.fleetshare.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.najmi.fleetshare.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for AI Data Assistant.
 * Gathers fleet data context from existing services, sends it with the user's
 * question to an LLM API (Groq/Cerebras/OpenRouter), and parses the structured
 * JSON response.
 */
@Service
public class AiAssistantService {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private VehicleManagementService vehicleManagementService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private MaintenanceService maintenanceService;
    @Autowired
    private UserManagementService userManagementService;

    @Value("${ai.assistant.provider:groq}")
    private String defaultProvider;

    @Value("${ai.assistant.groq.api-key:}")
    private String groqApiKey;
    @Value("${ai.assistant.groq.model:llama-3.3-70b-versatile}")
    private String groqModel;
    @Value("${ai.assistant.groq.url:https://api.groq.com/openai/v1/chat/completions}")
    private String groqUrl;

    @Value("${ai.assistant.cerebras.api-key:}")
    private String cerebrasApiKey;
    @Value("${ai.assistant.cerebras.model:llama-3.3-70b}")
    private String cerebrasModel;
    @Value("${ai.assistant.cerebras.url:https://api.cerebras.ai/v1/chat/completions}")
    private String cerebrasUrl;

    @Value("${ai.assistant.openrouter.api-key:}")
    private String openrouterApiKey;
    @Value("${ai.assistant.openrouter.model:meta-llama/llama-3.3-70b-instruct:free}")
    private String openrouterModel;
    @Value("${ai.assistant.openrouter.url:https://openrouter.ai/api/v1/chat/completions}")
    private String openrouterUrl;

    @Value("${ai.assistant.max-data-rows:50}")
    private int maxDataRows;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    /**
     * Process a natural-language query against the fleet data.
     * 
     * @param query    User's question
     * @param ownerId  Fleet owner ID (null for admin)
     * @param isAdmin  Whether the requester is a platform admin
     * @param provider Optional provider override ("groq", "cerebras", "openrouter")
     * @return Structured AI response
     */
    public AiQueryResponse processQuery(String query, Long ownerId, boolean isAdmin, String provider) {
        if (query == null || query.trim().isEmpty()) {
            return AiQueryResponse.error("Please enter a question.");
        }

        // Resolve provider
        String activeProvider = (provider != null && !provider.isEmpty()) ? provider : defaultProvider;
        String apiKey = getApiKey(activeProvider);
        if (apiKey == null || apiKey.isEmpty()) {
            return AiQueryResponse.error(
                    "AI service is not configured. Please set the API key for " + activeProvider
                            + " in application.properties.");
        }

        try {
            // 1. Build data context
            String dataContext = buildDataContext(ownerId, isAdmin);

            // 2. Build system prompt
            String systemPrompt = buildSystemPrompt(dataContext, isAdmin);

            // 3. Call LLM API
            String rawResponse = callLlmApi(systemPrompt, query, activeProvider);

            // 4. Parse and return
            return parseAiResponse(rawResponse);

        } catch (Exception e) {
            String message = e.getMessage();
            if (message != null && message.contains("429")) {
                return AiQueryResponse.error(
                        "Rate limit exceeded. Please wait a moment and try again.");
            }
            if (message != null && message.contains("401")) {
                return AiQueryResponse.error(
                        "Invalid API key. Please check your " + activeProvider + " API key configuration.");
            }
            return AiQueryResponse.error("AI service error: " + (message != null ? message : "Unknown error"));
        }
    }

    // ==================== DATA CONTEXT BUILDER ====================

    /**
     * Builds a compact text summary of fleet data for the LLM prompt context.
     * For owners: only their fleet data.
     * For admins: platform-wide data.
     */
    private String buildDataContext(Long ownerId, boolean isAdmin) {
        StringBuilder ctx = new StringBuilder();
        ctx.append("=== FLEET DATA SNAPSHOT (as of ").append(LocalDate.now().format(DATE_FMT)).append(") ===\n\n");

        // --- Vehicles ---
        List<VehicleDTO> vehicles;
        if (isAdmin) {
            vehicles = vehicleManagementService.getAllVehicles();
        } else {
            vehicles = vehicleManagementService.getVehiclesByOwnerId(ownerId);
        }
        ctx.append("VEHICLES (").append(vehicles.size()).append(" total):\n");
        Map<String, Long> statusCounts = vehicles.stream()
                .collect(Collectors.groupingBy(v -> v.getStatus() != null ? v.getStatus() : "UNKNOWN",
                        Collectors.counting()));
        statusCounts.forEach((s, c) -> ctx.append("  ").append(s).append(": ").append(c).append("\n"));
        ctx.append("  Details:\n");
        for (VehicleDTO v : vehicles.stream().limit(maxDataRows).collect(Collectors.toList())) {
            ctx.append("  - ").append(v.getBrand()).append(" ").append(v.getModel())
                    .append(" (").append(v.getRegistrationNo()).append(")")
                    .append(" | Status: ").append(v.getStatus())
                    .append(" | Rate: RM").append(v.getRatePerDay() != null
                            ? v.getRatePerDay().setScale(2, RoundingMode.HALF_UP)
                            : "N/A")
                    .append("/day")
                    .append(" | Category: ").append(v.getCategory() != null ? v.getCategory() : "N/A")
                    .append(" | Fuel: ").append(v.getFuelType() != null ? v.getFuelType() : "N/A");
            if (isAdmin && v.getOwnerBusinessName() != null) {
                ctx.append(" | Owner: ").append(v.getOwnerBusinessName());
            }
            ctx.append("\n");
        }

        // --- Bookings ---
        List<BookingDTO> bookings;
        if (isAdmin) {
            bookings = bookingService.getAllBookings();
        } else {
            bookings = bookingService.getBookingsByOwnerId(ownerId);
        }
        ctx.append("\nBOOKINGS (").append(bookings.size()).append(" total):\n");
        Map<String, Long> bookingStatusCounts = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getStatus() != null ? b.getStatus() : "UNKNOWN",
                        Collectors.counting()));
        bookingStatusCounts.forEach((s, c) -> ctx.append("  ").append(s).append(": ").append(c).append("\n"));

        BigDecimal totalRevenue = bookings.stream()
                .filter(b -> b.getTotalCost() != null)
                .map(BookingDTO::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ctx.append("  Total Revenue: RM").append(totalRevenue.setScale(2, RoundingMode.HALF_UP)).append("\n");

        ctx.append("  Recent bookings:\n");
        bookings.stream()
                .sorted(Comparator.comparing(BookingDTO::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(maxDataRows)
                .forEach(b -> {
                    ctx.append("  - #").append(b.getBookingId())
                            .append(" | ").append(b.getVehicleBrand()).append(" ").append(b.getVehicleModel())
                            .append(" (").append(b.getVehicleRegistrationNo()).append(")")
                            .append(" | Renter: ").append(b.getRenterName())
                            .append(" | ").append(formatDate(b.getStartDate()))
                            .append(" to ").append(formatDate(b.getEndDate()))
                            .append(" | Status: ").append(b.getStatus())
                            .append(" | Amount: RM").append(b.getTotalCost() != null
                                    ? b.getTotalCost().setScale(2, RoundingMode.HALF_UP)
                                    : "N/A");
                    if (isAdmin && b.getOwnerBusinessName() != null) {
                        ctx.append(" | Owner: ").append(b.getOwnerBusinessName());
                    }
                    ctx.append("\n");
                });

        // --- Payments ---
        List<PaymentDTO> payments;
        if (isAdmin) {
            payments = paymentService.getAllPayments();
        } else {
            payments = paymentService.getPaymentsByOwnerId(ownerId);
        }
        ctx.append("\nPAYMENTS (").append(payments.size()).append(" total):\n");
        Map<String, Long> paymentStatusCounts = payments.stream()
                .collect(Collectors.groupingBy(p -> p.getPaymentStatus() != null ? p.getPaymentStatus() : "UNKNOWN",
                        Collectors.counting()));
        paymentStatusCounts.forEach((s, c) -> ctx.append("  ").append(s).append(": ").append(c).append("\n"));

        BigDecimal totalPayments = payments.stream()
                .filter(p -> p.getAmount() != null && "VERIFIED".equals(p.getPaymentStatus()))
                .map(PaymentDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ctx.append("  Total Verified Payments: RM").append(totalPayments.setScale(2, RoundingMode.HALF_UP))
                .append("\n");

        BigDecimal pendingPayments = payments.stream()
                .filter(p -> p.getAmount() != null && "PENDING".equals(p.getPaymentStatus()))
                .map(PaymentDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ctx.append("  Total Pending Payments: RM").append(pendingPayments.setScale(2, RoundingMode.HALF_UP))
                .append("\n");

        ctx.append("  Recent payments:\n");
        payments.stream()
                .sorted(Comparator.comparing(PaymentDTO::getPaymentDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(maxDataRows)
                .forEach(p -> {
                    ctx.append("  - #").append(p.getPaymentId())
                            .append(" | Invoice: ").append(p.getInvoiceNumber() != null ? p.getInvoiceNumber() : "N/A")
                            .append(" | RM").append(p.getAmount() != null
                                    ? p.getAmount().setScale(2, RoundingMode.HALF_UP)
                                    : "N/A")
                            .append(" | Method: ").append(p.getPaymentMethod())
                            .append(" | Status: ").append(p.getPaymentStatus())
                            .append(" | Renter: ").append(p.getRenterName() != null ? p.getRenterName() : "N/A")
                            .append("\n");
                });

        // --- Maintenance ---
        List<MaintenanceDTO> maintenance;
        if (isAdmin) {
            maintenance = maintenanceService.getAllMaintenance();
        } else {
            maintenance = maintenanceService.getMaintenanceByOwnerId(ownerId);
        }
        ctx.append("\nMAINTENANCE (").append(maintenance.size()).append(" total):\n");
        Map<String, Long> maintStatusCounts = maintenance.stream()
                .collect(Collectors.groupingBy(m -> m.getStatus() != null ? m.getStatus() : "UNKNOWN",
                        Collectors.counting()));
        maintStatusCounts.forEach((s, c) -> ctx.append("  ").append(s).append(": ").append(c).append("\n"));

        BigDecimal totalMaintCost = maintenance.stream()
                .filter(m -> m.getFinalCost() != null)
                .map(MaintenanceDTO::getFinalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ctx.append("  Total Maintenance Cost: RM").append(totalMaintCost.setScale(2, RoundingMode.HALF_UP))
                .append("\n");

        ctx.append("  Records:\n");
        maintenance.stream()
                .sorted(Comparator.comparing(MaintenanceDTO::getScheduledDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(maxDataRows)
                .forEach(m -> {
                    ctx.append("  - ").append(m.getVehicleBrand()).append(" ").append(m.getVehicleModel())
                            .append(" (").append(m.getVehicleRegistrationNo()).append(")")
                            .append(" | ").append(m.getDescription() != null
                                    ? m.getDescription().substring(0, Math.min(50, m.getDescription().length()))
                                    : "N/A")
                            .append(" | Date: ").append(m.getScheduledDate() != null
                                    ? m.getScheduledDate().format(DATE_FMT)
                                    : "N/A")
                            .append(" | Status: ").append(m.getStatus())
                            .append(" | Cost: RM").append(m.getFinalCost() != null
                                    ? m.getFinalCost().setScale(2, RoundingMode.HALF_UP)
                                    : (m.getEstimatedCost() != null
                                            ? m.getEstimatedCost().setScale(2, RoundingMode.HALF_UP) + " (est.)"
                                            : "N/A"))
                            .append("\n");
                });

        // --- Fleet Owners (admin only) ---
        if (isAdmin) {
            List<FleetOwnerDTO> owners = userManagementService.getAllFleetOwners();
            ctx.append("\nFLEET OWNERS (").append(owners.size()).append(" total):\n");
            for (FleetOwnerDTO owner : owners) {
                ctx.append("  - ").append(owner.getBusinessName())
                        .append(" | Verified: ").append(owner.getIsVerified())
                        .append(" | Phone: ").append(owner.getContactPhone() != null ? owner.getContactPhone() : "N/A")
                        .append("\n");
            }

            List<RenterDTO> renters = userManagementService.getAllRenters();
            ctx.append("\nRENTERS (").append(renters.size()).append(" total):\n");
            for (RenterDTO renter : renters) {
                ctx.append("  - ").append(renter.getFullName())
                        .append(" | Email: ").append(renter.getEmail() != null ? renter.getEmail() : "N/A")
                        .append("\n");
            }
        }

        return ctx.toString();
    }

    // ==================== SYSTEM PROMPT ====================

    private String buildSystemPrompt(String dataContext, boolean isAdmin) {
        String roleDescription = isAdmin
                ? "a platform administrator with access to ALL fleet data across all fleet owners"
                : "a fleet owner with access to your own fleet data only";

        return "You are FleetShare AI Assistant, a helpful data analyst for a vehicle rental management platform. "
                + "You are speaking to " + roleDescription + ".\n\n"
                + "IMPORTANT RULES:\n"
                + "1. ONLY answer questions based on the data provided below. Never make up or fabricate data.\n"
                + "2. If the data does not contain enough information, say so clearly in the explanation.\n"
                + "3. All currency is in Malaysian Ringgit (RM).\n"
                + "4. Today's date is " + LocalDate.now().format(DATE_FMT) + ".\n"
                + "5. You MUST respond with ONLY valid JSON (no markdown, no code fences, no extra text).\n\n"
                + "RESPONSE FORMAT (strict JSON):\n"
                + "{\n"
                + "  \"explanation\": \"A brief natural-language summary of the results\",\n"
                + "  \"columns\": [\"Column1\", \"Column2\", ...],\n"
                + "  \"data\": [\n"
                + "    {\"Column1\": \"value\", \"Column2\": \"value\"},\n"
                + "    ...\n"
                + "  ],\n"
                + "  \"summary\": {\"key\": \"value\", ...}\n"
                + "}\n\n"
                + "GUIDELINES:\n"
                + "- Keep column names short and clear\n"
                + "- Format monetary values with RM prefix (e.g., \"RM 1,500.00\")\n"
                + "- For simple questions (e.g., \"total revenue\"), use a single row with the answer\n"
                + "- For listing questions (e.g., \"show all vehicles\"), include relevant columns\n"
                + "- The summary should contain key aggregate metrics\n"
                + "- Limit data to at most 50 rows\n\n"
                + "DATA:\n" + dataContext;
    }

    // ==================== LLM API CLIENT ====================

    private String callLlmApi(String systemPrompt, String userQuery, String provider) throws Exception {
        String apiKey = getApiKey(provider);
        String model = getModel(provider);
        String url = getUrl(provider);

        // Build request body (OpenAI-compatible format)
        String requestBody = "{"
                + "\"model\": " + jsonEscape(model) + ","
                + "\"messages\": ["
                + "  {\"role\": \"system\", \"content\": " + jsonEscape(systemPrompt) + "},"
                + "  {\"role\": \"user\", \"content\": " + jsonEscape(userQuery) + "}"
                + "],"
                + "\"temperature\": 0.1,"
                + "\"max_tokens\": 4096,"
                + "\"response_format\": {\"type\": \"json_object\"}"
                + "}";

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody));

        // OpenRouter requires extra headers
        if ("openrouter".equals(provider)) {
            requestBuilder.header("HTTP-Referer", "https://fleetshare.com");
            requestBuilder.header("X-Title", "FleetShare AI Assistant");
        }

        HttpResponse<String> response = httpClient.send(requestBuilder.build(),
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("LLM API returned status " + response.statusCode() + ": " + response.body());
        }

        // Extract content from the OpenAI-compatible response
        String body = response.body();
        return extractContentFromResponse(body);
    }

    // ==================== RESPONSE PARSER ====================

    /**
     * Parses the AI's JSON response into an AiQueryResponse.
     * Uses simple string parsing to avoid adding a JSON library dependency.
     */
    @SuppressWarnings("unchecked")
    private AiQueryResponse parseAiResponse(String rawJson) {
        try {
            // Clean up: remove potential markdown code fences
            String json = rawJson.trim();
            if (json.startsWith("```")) {
                json = json.substring(json.indexOf("\n") + 1);
                if (json.endsWith("```")) {
                    json = json.substring(0, json.lastIndexOf("```"));
                }
                json = json.trim();
            }

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> parsed = mapper.readValue(json, Map.class);

            String explanation = (String) parsed.getOrDefault("explanation", "Query processed.");
            List<String> columns = (List<String>) parsed.getOrDefault("columns", Collections.emptyList());
            List<Map<String, Object>> data = (List<Map<String, Object>>) parsed.getOrDefault("data",
                    Collections.emptyList());
            Map<String, Object> summary = (Map<String, Object>) parsed.getOrDefault("summary",
                    Collections.emptyMap());

            return AiQueryResponse.success(explanation, columns, data, summary);

        } catch (Exception e) {
            // If parsing fails, try to return the raw text as explanation
            return AiQueryResponse.success(rawJson, Collections.emptyList(),
                    Collections.emptyList(), Collections.emptyMap());
        }
    }

    // ==================== HELPERS ====================

    private String getApiKey(String provider) {
        return switch (provider.toLowerCase()) {
            case "cerebras" -> cerebrasApiKey;
            case "openrouter" -> openrouterApiKey;
            default -> groqApiKey;
        };
    }

    private String getModel(String provider) {
        return switch (provider.toLowerCase()) {
            case "cerebras" -> cerebrasModel;
            case "openrouter" -> openrouterModel;
            default -> groqModel;
        };
    }

    private String getUrl(String provider) {
        return switch (provider.toLowerCase()) {
            case "cerebras" -> cerebrasUrl;
            case "openrouter" -> openrouterUrl;
            default -> groqUrl;
        };
    }

    /**
     * Extracts the assistant's message content from an OpenAI-compatible API
     * response.
     */
    private String extractContentFromResponse(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Object> response = mapper.readValue(responseBody, Map.class);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                if (message != null) {
                    return (String) message.get("content");
                }
            }
            throw new RuntimeException("Unexpected API response format");
        } catch (ClassCastException | JsonProcessingException e) {
            throw new RuntimeException("Failed to parse API response: " + e.getMessage());
        }
    }

    /**
     * JSON-escape a string value (wraps in quotes and escapes special chars).
     */
    private String jsonEscape(String value) {
        if (value == null)
            return "null";
        return "\"" + value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                + "\"";
    }

    private String formatDate(java.time.LocalDateTime dateTime) {
        if (dateTime == null)
            return "N/A";
        return dateTime.toLocalDate().format(DATE_FMT);
    }
}
