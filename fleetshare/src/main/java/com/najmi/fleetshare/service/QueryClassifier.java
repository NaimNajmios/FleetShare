package com.najmi.fleetshare.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class QueryClassifier {

    public enum QueryIntent {
        AGGREGATION,
        FILTERING,
        COMPARISON,
        TREND_ANALYSIS,
        RANKING,
        LISTING,
        STATUS
    }

    public static class ClassificationResult {
        private final QueryIntent intent;
        private final LocalDate fromDate;
        private final LocalDate toDate;
        private final Map<String, Object> filters;
        private final String simplifiedQuery;
        private final double confidence;

        public ClassificationResult(QueryIntent intent, LocalDate fromDate, LocalDate toDate,
                Map<String, Object> filters, String simplifiedQuery, double confidence) {
            this.intent = intent;
            this.fromDate = fromDate;
            this.toDate = toDate;
            this.filters = filters;
            this.simplifiedQuery = simplifiedQuery;
            this.confidence = confidence;
        }

        public QueryIntent getIntent() {
            return intent;
        }

        public LocalDate getFromDate() {
            return fromDate;
        }

        public LocalDate getToDate() {
            return toDate;
        }

        public Map<String, Object> getFilters() {
            return filters;
        }

        public String getSimplifiedQuery() {
            return simplifiedQuery;
        }

        public double getConfidence() {
            return confidence;
        }
    }

    private static final Pattern AGGREGATION_PATTERN = Pattern.compile(
            "\\b(total|sum|count|average|how many|how much|total amount|total revenue|total cost)\\b",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern FILTERING_PATTERN = Pattern.compile(
            "\\b(show|list|get|find|where|with|having)\\b.*\\b(available|rented|cancelled|completed|pending|active|overdue|red|white|black|sedan|suv|van)\\b",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern COMPARISON_PATTERN = Pattern.compile(
            "\\b(vs|versus|compare|difference|than|increase|decrease|change|growth)\\b",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern TREND_PATTERN = Pattern.compile(
            "\\b(trend|over time|growth|monthly|weekly|yearly|season|progression| fluctuate)\\b",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern RANKING_PATTERN = Pattern.compile(
            "\\b(top|bottom|most|least|highest|lowest|best|worst|leading)\\b",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern STATUS_PATTERN = Pattern.compile(
            "\\b(active|pending|completed|cancelled|confirmed|rejected|overdue|expired|in-progress|paid|unpaid|verified)\\b",
            Pattern.CASE_INSENSITIVE);

    private static final Set<String> TIME_KEYWORDS = new HashSet<>(Arrays.asList(
            "today", "yesterday", "tomorrow", "this week", "last week", "next week",
            "this month", "last month", "next month", "this year", "last year",
            "quarter", "q1", "q2", "q3", "q4", "ytd", "mtd"));

    private static final Set<String> VEHICLE_STATUSES = new HashSet<>(Arrays.asList(
            "available", "rented", "maintenance", "reserved", "unavailable"));

    private static final Set<String> BOOKING_STATUSES = new HashSet<>(Arrays.asList(
            "pending", "confirmed", "completed", "cancelled", "rejected", "overdue", "in-progress"));

    private static final Set<String> PAYMENT_STATUSES = new HashSet<>(Arrays.asList(
            "pending", "verified", "failed", "cancelled", "refunded"));

    public ClassificationResult classify(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ClassificationResult(QueryIntent.LISTING, null, null, new HashMap<>(), query, 1.0);
        }

        String normalized = query.toLowerCase().trim();
        LocalDate fromDate = null;
        LocalDate toDate = null;
        Map<String, Object> filters = new HashMap<>();
        double confidence = 0.5;

        Pair<DateRangeResult, LocalDate[]> dateResult = parseDateRange(normalized);
        if (dateResult.getKey() != null) {
            fromDate = dateResult.getValue()[0];
            toDate = dateResult.getValue()[1];
            normalized = dateResult.getKey().getRemainingQuery();
            confidence = 0.8;
        }

        extractFilters(normalized, filters);

        QueryIntent intent = detectIntent(query, normalized);
        if (intent != null) {
            confidence = Math.min(confidence + 0.2, 1.0);
        }

        return new ClassificationResult(intent, fromDate, toDate, filters, normalized, confidence);
    }

    private QueryIntent detectIntent(String original, String normalized) {
        if (COMPARISON_PATTERN.matcher(normalized).find()) {
            return QueryIntent.COMPARISON;
        }
        if (TREND_PATTERN.matcher(normalized).find()) {
            return QueryIntent.TREND_ANALYSIS;
        }
        if (RANKING_PATTERN.matcher(normalized).find()) {
            return QueryIntent.RANKING;
        }
        if (AGGREGATION_PATTERN.matcher(normalized).find()) {
            return QueryIntent.AGGREGATION;
        }
        if (STATUS_PATTERN.matcher(normalized).find()) {
            return QueryIntent.STATUS;
        }
        if (FILTERING_PATTERN.matcher(normalized).find()) {
            return QueryIntent.FILTERING;
        }
        return QueryIntent.LISTING;
    }

    private static class DateRangeResult {
        private final String remainingQuery;

        public DateRangeResult(String remainingQuery) {
            this.remainingQuery = remainingQuery;
        }

        public String getRemainingQuery() {
            return remainingQuery;
        }
    }

    private static class Pair<K, V> {
        private final K key;
        private final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }

    private Pair<DateRangeResult, LocalDate[]> parseDateRange(String query) {
        LocalDate today = LocalDate.now();
        LocalDate from = null;
        LocalDate to = null;
        String remaining = query;

        if (query.contains("today")) {
            from = today;
            to = today;
            remaining = remaining.replaceAll("\\btoday\\b", "").trim();
        } else if (query.contains("yesterday")) {
            from = today.minusDays(1);
            to = today.minusDays(1);
            remaining = remaining.replaceAll("\\byesterday\\b", "").trim();
        } else if (query.contains("this week")) {
            from = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            to = today;
            remaining = remaining.replaceAll("\\bthis week\\b", "").trim();
        } else if (query.contains("last week")) {
            from = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            to = from.plusDays(6);
            remaining = remaining.replaceAll("\\blast week\\b", "").trim();
        } else if (query.contains("this month")) {
            from = today.with(TemporalAdjusters.firstDayOfMonth());
            to = today;
            remaining = remaining.replaceAll("\\bthis month\\b", "").trim();
        } else if (query.contains("last month")) {
            from = today.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
            to = today.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
            remaining = remaining.replaceAll("\\blast month\\b", "").trim();
        } else if (query.contains("this year")) {
            from = today.with(TemporalAdjusters.firstDayOfYear());
            to = today;
            remaining = remaining.replaceAll("\\bthis year\\b", "").trim();
        } else if (query.contains("last year")) {
            from = today.minusYears(1).with(TemporalAdjusters.firstDayOfYear());
            to = today.minusYears(1).with(TemporalAdjusters.lastDayOfYear());
            remaining = remaining.replaceAll("\\blast year\\b", "").trim();
        } else if (query.matches(".*\\bq[1-4]\\b.*")) {
            int year = extractYear(query);
            if (query.contains("q1")) {
                from = LocalDate.of(year, 1, 1);
                to = LocalDate.of(year, 3, 31);
            } else if (query.contains("q2")) {
                from = LocalDate.of(year, 4, 1);
                to = LocalDate.of(year, 6, 30);
            } else if (query.contains("q3")) {
                from = LocalDate.of(year, 7, 1);
                to = LocalDate.of(year, 9, 30);
            } else if (query.contains("q4")) {
                from = LocalDate.of(year, 10, 1);
                to = LocalDate.of(year, 12, 31);
            }
            remaining = remaining.replaceAll("\\bq[1-4]\\s*\\d{4}?\\b", "").trim();
            if (remaining.isEmpty()) {
                remaining = query.replaceAll("\\d{4}", "").replaceAll("\\bq[1-4]", "").trim();
            }
        } else {
            return new Pair<>(null, new LocalDate[]{null, null});
        }

        return new Pair<>(new DateRangeResult(remaining), new LocalDate[]{from, to});
    }

    private int extractYear(String query) {
        Pattern yearPattern = Pattern.compile("20\\d{2}");
        Matcher matcher = yearPattern.matcher(query);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return LocalDate.now().getYear();
    }

    private void extractFilters(String query, Map<String, Object> filters) {
        for (String status : VEHICLE_STATUSES) {
            if (query.contains(status)) {
                filters.put("vehicleStatus", status.toUpperCase());
            }
        }
        for (String status : BOOKING_STATUSES) {
            if (query.contains(status)) {
                filters.put("bookingStatus", status.toUpperCase());
            }
        }
        for (String status : PAYMENT_STATUSES) {
            if (query.contains(status)) {
                filters.put("paymentStatus", status.toUpperCase());
            }
        }

        String[] colors = {"red", "white", "black", "silver", "blue", "green", "yellow", "gold"};
        for (String color : colors) {
            if (query.contains(color)) {
                filters.put("color", color);
            }
        }

        String[] categories = {"sedan", "suv", "van", "mpv", "compact", "luxury", "sports"};
        for (String category : categories) {
            if (query.contains(category)) {
                filters.put("category", category.toUpperCase());
            }
        }
    }
}