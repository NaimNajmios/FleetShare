package com.najmi.fleetshare.service;

import com.najmi.fleetshare.service.QueryClassifier.QueryIntent;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PromptTemplateService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private static final Map<QueryIntent, String> INTENT_PROMPTS = new EnumMap<>(QueryIntent.class);

    static {
        INTENT_PROMPTS.put(QueryIntent.AGGREGATION,
                "You are answering an AGGREGATION query. Provide a single-row result with summary statistics. " +
                "Use 'Total', 'Count', or 'Average' as column names. " +
                "Include relevant summary metrics in the summary object.");

        INTENT_PROMPTS.put(QueryIntent.FILTERING,
                "You are answering a FILTERING query. Only include records that match the specified criteria. " +
                "Show all relevant columns for the filtered records.");

        INTENT_PROMPTS.put(QueryIntent.COMPARISON,
                "You are answering a COMPARISON query. Present data in a way that highlights differences. " +
                "Use columns like 'Period 1', 'Period 2', 'Difference', 'Change %' for clarity. " +
                "Add comparison metrics to the summary object.");

        INTENT_PROMPTS.put(QueryIntent.TREND_ANALYSIS,
                "You are answering a TREND ANALYSIS query. Order data chronologically. " +
                "Include period-over-period change in the summary. " +
                "Format: ['Period', 'Value', 'Change %'] or similar time-series columns.");

        INTENT_PROMPTS.put(QueryIntent.RANKING,
                "You are answering a RANKING query. Sort by the ranking metric (descending). " +
                "Include Rank as the first column. " +
                "Format: ['Rank', 'Item', 'Metric', ...]. Limit to top 10.");

        INTENT_PROMPTS.put(QueryIntent.LISTING,
                "You are answering a LISTING query. Show all relevant columns for the records. " +
                "Order by most recent first unless specified otherwise.");

        INTENT_PROMPTS.put(QueryIntent.STATUS,
                "You are answering a STATUS query. Group or filter by the specified status. " +
                "Include status as a column. Show count per status in summary.");
    }

    private static final Map<QueryIntent, Map<String, String>> RESPONSE_EXAMPLES = new EnumMap<>(QueryIntent.class);

    static {
        RESPONSE_EXAMPLES.put(QueryIntent.AGGREGATION, Map.of(
                "example", """
                        {
                          "explanation": "Total revenue for the period is RM 45,230.00 from 32 bookings.",
                          "columns": ["Metric", "Value"],
                          "data": [{"Metric": "Total Revenue", "Value": "RM 45,230.00"}],
                          "summary": {"total_bookings": 32, "average_per_booking": "RM 1,413.44"}
                        }"""
        ));

        RESPONSE_EXAMPLES.put(QueryIntent.FILTERING, Map.of(
                "example", """
                        {
                          "explanation": "Found 5 red vehicles available for rent.",
                          "columns": ["Vehicle", "Model", "Registration", "Rate/Day"],
                          "data": [
                            {"Vehicle": "Honda City", "Model": "2022", "Registration": "ABC 1234", "Rate/Day": "RM 120.00"},
                            {"Vehicle": "Toyota Vios", "Model": "2023", "Registration": "DEF 5678", "Rate/Day": "RM 130.00"}
                          ],
                          "summary": {"total_found": 5, "available": 5}
                        }"""
        ));

        RESPONSE_EXAMPLES.put(QueryIntent.RANKING, Map.of(
                "example", """
                        {
                          "explanation": "Top 5 vehicles by revenue this month.",
                          "columns": ["Rank", "Vehicle", "Revenue", "Bookings"],
                          "data": [
                            {"Rank": 1, "Vehicle": "Toyota Camry", "Revenue": "RM 8,500", "Bookings": 12},
                            {"Rank": 2, "Vehicle": "Honda City", "Revenue": "RM 7,200", "Bookings": 15},
                            {"Rank": 3, "Vehicle": "Perodua Myvi", "Revenue": "RM 5,100", "Bookings": 18}
                          ],
                          "summary": {"top_vehicle": "Toyota Camry", "total_revenue": "RM 20,800"}
                        }"""
        ));

        RESPONSE_EXAMPLES.put(QueryIntent.COMPARISON, Map.of(
                "example", """
                        {
                          "explanation": "Revenue increased by 23% compared to last month.",
                          "columns": ["Period", "Revenue", "Bookings"],
                          "data": [
                            {"Period": "This Month", "Revenue": "RM 45,230", "Bookings": 32},
                            {"Period": "Last Month", "Revenue": "RM 36,780", "Bookings": 28}
                          ],
                          "summary": {"increase": "RM 8,450", "change_percent": 23}
                        }"""
        ));

        RESPONSE_EXAMPLES.put(QueryIntent.TREND_ANALYSIS, Map.of(
                "example", """
                        {
                          "explanation": "Monthly revenue trend for the last 6 months.",
                          "columns": ["Month", "Revenue", "Change %"],
                          "data": [
                            {"Month": "Oct 2025", "Revenue": "RM 45,230", "Change %": 23},
                            {"Month": "Sep 2025", "Revenue": "RM 36,780", "Change %": -5},
                            {"Month": "Aug 2025", "Revenue": "RM 38,720", "Change %": 12}
                          ],
                          "summary": {"trend": "increasing", "avg_monthly": "RM 40,243"}
                        }"""
        ));
    }

    public String getSystemPrompt(String dataContext, boolean isAdmin, QueryIntent intent, LocalDate fromDate, LocalDate toDate) {
        String roleDescription = isAdmin
                ? "a platform administrator with access to ALL fleet data across all fleet owners"
                : "a fleet owner with access to your own fleet data only";

        String dateRangeInfo = "";
        if (fromDate != null && toDate != null) {
            dateRangeInfo = "Query applies to period: " + fromDate.format(DATE_FMT) + " to " + toDate.format(DATE_FMT) + ".\n";
        }

        String intentGuidance = INTENT_PROMPTS.getOrDefault(intent, INTENT_PROMPTS.get(QueryIntent.LISTING));

        String example = "";
        if (RESPONSE_EXAMPLES.containsKey(intent)) {
            example = "EXAMPLE OUTPUT:\n" + RESPONSE_EXAMPLES.get(intent).get("example") + "\n";
        }

        return "You are FleetShare AI Assistant, a helpful data analyst for a vehicle rental management platform. "
                + "You are speaking to " + roleDescription + ".\n\n"
                + dateRangeInfo
                + "IMPORTANT RULES:\n"
                + "1. ONLY answer questions based on the data provided below. Never make up or fabricate data.\n"
                + "2. If the data does not contain enough information, say so clearly in the explanation.\n"
                + "3. All currency is in Malaysian Ringgit (RM).\n"
                + "4. Today's date is " + LocalDate.now().format(DATE_FMT) + ".\n"
                + "5. You MUST respond with ONLY valid JSON (no markdown, no code fences, no extra text).\n\n"
                + "INTENT-SPECIFIC GUIDANCE:\n"
                + intentGuidance + "\n\n"
                + example
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
                + "- For ranking queries, include a Rank column starting from 1\n"
                + "- Include relevant metrics in the summary object\n"
                + "- Limit data to at most 50 rows\n\n"
                + "DATA:\n" + dataContext;
    }

    public String getUserPrompt(String originalQuery, QueryIntent intent, Map<String, Object> filters) {
        String query = originalQuery;

        String filterInfo = "";
        if (filters != null && !filters.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            filters.forEach((k, v) -> sb.append(", ").append(k).append("=").append(v));
            filterInfo = sb.toString().substring(2);
            query = query + " [Filters: " + filterInfo + "]";
        }

        return query;
    }
}