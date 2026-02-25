package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.ReportResponse;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating SVG chart markup for PDF reports.
 * OpenHTMLToPDF does not execute JavaScript, so charts must be
 * rendered as inline SVG on the server side.
 */
@Service
public class ReportChartService {

    // Chart color palette (matches frontend chartConfigs)
    private static final String[] BAR_COLORS = {
            "#667eea", "#764ba2", "#11998e", "#38ef7d",
            "#fa709a", "#fee140", "#4facfe", "#f093fb"
    };

    private static final String[] DOUGHNUT_COLORS = {
            "#667eea", "#764ba2", "#11998e", "#38ef7d",
            "#fa709a", "#fee140", "#4facfe", "#f093fb"
    };

    /**
     * Generate SVG chart markup for a given report type.
     * Returns null if the report type does not support charts.
     */
    public String generateChartSvg(String reportType, ReportResponse<Map<String, Object>> report) {
        if (report.getData() == null || report.getData().isEmpty()) {
            return null;
        }

        return switch (reportType) {
            case "monthly-revenue" -> generateMonthlyRevenueChart(report);
            case "utilization-rate" -> generateHorizontalBarChart(report, "Vehicle", "Utilization",
                    "Fleet Utilization (%)", "#11998e", true);
            case "vehicle-performance" -> generateVerticalBarChart(report, "Vehicle", "Revenue",
                    "Revenue per Vehicle (RM)", "#667eea");
            case "revenue-analysis" -> generateDoughnutChart(report, "Payment Method", "Revenue");
            case "cost-analysis" -> generateVerticalBarChart(report, "Vehicle", "Total Cost",
                    "Maintenance Cost per Vehicle (RM)", "#4facfe");
            case "top-customers" -> generateHorizontalBarChart(report, "Customer", "Revenue",
                    "Top Customers by Revenue (RM)", "#f093fb", false);
            default -> null;
        };
    }

    // ─── Monthly Revenue: vertical bar chart ───

    private String generateMonthlyRevenueChart(ReportResponse<Map<String, Object>> report) {
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        for (Map<String, Object> row : report.getData()) {
            labels.add(truncateLabel(String.valueOf(row.getOrDefault("Month", "-")), 10));
            values.add(parseNumeric(String.valueOf(row.getOrDefault("Revenue", "0"))));
        }

        return buildVerticalBarSvg(labels, values, "Monthly Revenue (RM)", "#667eea");
    }

    // ─── Vertical Bar Chart ───

    private String generateVerticalBarChart(ReportResponse<Map<String, Object>> report,
            String labelCol, String valueCol, String title, String color) {
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        for (Map<String, Object> row : report.getData()) {
            labels.add(truncateLabel(String.valueOf(row.getOrDefault(labelCol, "-")), 12));
            values.add(parseNumeric(String.valueOf(row.getOrDefault(valueCol, "0"))));
        }

        return buildVerticalBarSvg(labels, values, title, color);
    }

    private String buildVerticalBarSvg(List<String> labels, List<Double> values, String title, String color) {
        int svgWidth = 700;
        int svgHeight = 280;
        int marginLeft = 70;
        int marginRight = 20;
        int marginTop = 35;
        int marginBottom = 60;
        int chartWidth = svgWidth - marginLeft - marginRight;
        int chartHeight = svgHeight - marginTop - marginBottom;

        int count = Math.min(labels.size(), 15); // Cap at 15 bars
        double maxVal = values.stream().limit(count).mapToDouble(Double::doubleValue).max().orElse(1);
        if (maxVal == 0)
            maxVal = 1;

        // Round max to nice number
        maxVal = niceMax(maxVal);

        double barWidth = (double) chartWidth / count * 0.6;
        double gap = (double) chartWidth / count * 0.4;

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"%d\" height=\"%d\" viewBox=\"0 0 %d %d\" style=\"font-family:Arial,sans-serif;\">",
                svgWidth, svgHeight, svgWidth, svgHeight));

        // Title
        sb.append(String.format(
                "<text x=\"%d\" y=\"20\" font-size=\"13\" font-weight=\"bold\" fill=\"#333\" text-anchor=\"middle\">%s</text>",
                svgWidth / 2, escapeXml(title)));

        // Grid lines and Y-axis labels
        int gridLines = 5;
        for (int i = 0; i <= gridLines; i++) {
            double yVal = maxVal * i / gridLines;
            int y = marginTop + chartHeight - (int) (chartHeight * i / gridLines);
            sb.append(String.format(
                    "<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"#e0e0e0\" stroke-width=\"1\"/>",
                    marginLeft, y, svgWidth - marginRight, y));
            sb.append(String.format(
                    "<text x=\"%d\" y=\"%d\" font-size=\"9\" fill=\"#666\" text-anchor=\"end\">%s</text>",
                    marginLeft - 5, y + 3, formatNumber(yVal)));
        }

        // Bars
        for (int i = 0; i < count; i++) {
            double val = values.get(i);
            int barH = (int) (chartHeight * val / maxVal);
            double x = marginLeft + i * (barWidth + gap) + gap / 2;
            int y = marginTop + chartHeight - barH;
            String barColor = BAR_COLORS[i % BAR_COLORS.length];
            if (color != null)
                barColor = color;

            sb.append(String.format(
                    "<rect x=\"%.1f\" y=\"%d\" width=\"%.1f\" height=\"%d\" fill=\"%s\" rx=\"2\" opacity=\"0.85\"/>",
                    x, y, barWidth, barH, barColor));

            // Value on top
            sb.append(String.format(
                    "<text x=\"%.1f\" y=\"%d\" font-size=\"8\" fill=\"#333\" text-anchor=\"middle\">%s</text>",
                    x + barWidth / 2, y - 3, formatNumber(val)));

            // X-axis label
            sb.append(String.format(
                    "<text x=\"%.1f\" y=\"%d\" font-size=\"8\" fill=\"#666\" text-anchor=\"middle\" transform=\"rotate(-30,%.1f,%d)\">%s</text>",
                    x + barWidth / 2, marginTop + chartHeight + 15,
                    x + barWidth / 2, marginTop + chartHeight + 15,
                    escapeXml(labels.get(i))));
        }

        // Axes
        sb.append(String.format(
                "<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"#333\" stroke-width=\"1.5\"/>",
                marginLeft, marginTop, marginLeft, marginTop + chartHeight));
        sb.append(String.format(
                "<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"#333\" stroke-width=\"1.5\"/>",
                marginLeft, marginTop + chartHeight, svgWidth - marginRight, marginTop + chartHeight));

        sb.append("</svg>");
        return sb.toString();
    }

    // ─── Horizontal Bar Chart ───

    private String generateHorizontalBarChart(ReportResponse<Map<String, Object>> report,
            String labelCol, String valueCol, String title, String color, boolean isPercent) {
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        for (Map<String, Object> row : report.getData()) {
            labels.add(truncateLabel(String.valueOf(row.getOrDefault(labelCol, "-")), 18));
            values.add(parseNumeric(String.valueOf(row.getOrDefault(valueCol, "0"))));
        }

        int count = Math.min(labels.size(), 12);
        int svgWidth = 700;
        int barHeight = 22;
        int barGap = 8;
        int marginLeft = 140;
        int marginRight = 60;
        int marginTop = 35;
        int chartWidth = svgWidth - marginLeft - marginRight;
        int svgHeight = marginTop + count * (barHeight + barGap) + 20;

        double maxVal = values.stream().limit(count).mapToDouble(Double::doubleValue).max().orElse(1);
        if (maxVal == 0)
            maxVal = 1;
        if (isPercent)
            maxVal = Math.max(maxVal, 100);
        else
            maxVal = niceMax(maxVal);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"%d\" height=\"%d\" viewBox=\"0 0 %d %d\" style=\"font-family:Arial,sans-serif;\">",
                svgWidth, svgHeight, svgWidth, svgHeight));

        // Title
        sb.append(String.format(
                "<text x=\"%d\" y=\"20\" font-size=\"13\" font-weight=\"bold\" fill=\"#333\" text-anchor=\"middle\">%s</text>",
                svgWidth / 2, escapeXml(title)));

        // Bars
        for (int i = 0; i < count; i++) {
            double val = values.get(i);
            int barW = (int) (chartWidth * val / maxVal);
            int y = marginTop + i * (barHeight + barGap);
            String barColor = BAR_COLORS[i % BAR_COLORS.length];
            if (color != null)
                barColor = color;

            // Label
            sb.append(String.format(
                    "<text x=\"%d\" y=\"%d\" font-size=\"9\" fill=\"#333\" text-anchor=\"end\">%s</text>",
                    marginLeft - 5, y + barHeight / 2 + 3, escapeXml(labels.get(i))));

            // Bar
            sb.append(String.format(
                    "<rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" fill=\"%s\" rx=\"3\" opacity=\"0.8\"/>",
                    marginLeft, y, barW, barHeight, barColor));

            // Value
            String valText = isPercent ? String.format("%.1f%%", val) : formatNumber(val);
            sb.append(String.format(
                    "<text x=\"%d\" y=\"%d\" font-size=\"9\" fill=\"#333\" text-anchor=\"start\">%s</text>",
                    marginLeft + barW + 5, y + barHeight / 2 + 3, valText));
        }

        sb.append("</svg>");
        return sb.toString();
    }

    // ─── Doughnut Chart ───

    private String generateDoughnutChart(ReportResponse<Map<String, Object>> report,
            String labelCol, String valueCol) {
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        for (Map<String, Object> row : report.getData()) {
            labels.add(String.valueOf(row.getOrDefault(labelCol, "-")));
            values.add(parseNumeric(String.valueOf(row.getOrDefault(valueCol, "0"))));
        }

        int count = Math.min(labels.size(), 8);
        double total = values.stream().limit(count).mapToDouble(Double::doubleValue).sum();
        if (total == 0)
            return null;

        int svgWidth = 700;
        int svgHeight = 250;
        int cx = 200;
        int cy = 130;
        int outerR = 90;
        int innerR = 50;

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"%d\" height=\"%d\" viewBox=\"0 0 %d %d\" style=\"font-family:Arial,sans-serif;\">",
                svgWidth, svgHeight, svgWidth, svgHeight));

        // Title
        sb.append(String.format(
                "<text x=\"%d\" y=\"20\" font-size=\"13\" font-weight=\"bold\" fill=\"#333\" text-anchor=\"middle\">%s</text>",
                svgWidth / 2, "Revenue by Payment Method"));

        // Arcs
        double startAngle = -Math.PI / 2;
        for (int i = 0; i < count; i++) {
            double val = values.get(i);
            double angle = (val / total) * 2 * Math.PI;
            double endAngle = startAngle + angle;
            int largeArc = angle > Math.PI ? 1 : 0;

            double x1Outer = cx + outerR * Math.cos(startAngle);
            double y1Outer = cy + outerR * Math.sin(startAngle);
            double x2Outer = cx + outerR * Math.cos(endAngle);
            double y2Outer = cy + outerR * Math.sin(endAngle);
            double x1Inner = cx + innerR * Math.cos(endAngle);
            double y1Inner = cy + innerR * Math.sin(endAngle);
            double x2Inner = cx + innerR * Math.cos(startAngle);
            double y2Inner = cy + innerR * Math.sin(startAngle);

            String path = String.format(
                    "M %.2f %.2f A %d %d 0 %d 1 %.2f %.2f L %.2f %.2f A %d %d 0 %d 0 %.2f %.2f Z",
                    x1Outer, y1Outer, outerR, outerR, largeArc, x2Outer, y2Outer,
                    x1Inner, y1Inner, innerR, innerR, largeArc, x2Inner, y2Inner);

            sb.append(String.format(
                    "<path d=\"%s\" fill=\"%s\" stroke=\"white\" stroke-width=\"2\"/>",
                    path, DOUGHNUT_COLORS[i % DOUGHNUT_COLORS.length]));

            startAngle = endAngle;
        }

        // Center text
        sb.append(String.format(
                "<text x=\"%d\" y=\"%d\" font-size=\"11\" font-weight=\"bold\" fill=\"#333\" text-anchor=\"middle\">RM %s</text>",
                cx, cy + 4, formatNumber(total)));

        // Legend
        int legendX = 360;
        int legendY = 45;
        for (int i = 0; i < count; i++) {
            double pct = (values.get(i) / total) * 100;
            sb.append(String.format(
                    "<rect x=\"%d\" y=\"%d\" width=\"12\" height=\"12\" rx=\"2\" fill=\"%s\"/>",
                    legendX, legendY + i * 22, DOUGHNUT_COLORS[i % DOUGHNUT_COLORS.length]));
            sb.append(String.format(
                    "<text x=\"%d\" y=\"%d\" font-size=\"10\" fill=\"#333\">%s (%.1f%%)</text>",
                    legendX + 18, legendY + i * 22 + 10, escapeXml(labels.get(i)), pct));
        }

        sb.append("</svg>");
        return sb.toString();
    }

    // ─── Utility Methods ───

    /**
     * Parse numeric value from formatted strings like "RM 150.00" or "85.5%"
     */
    private double parseNumeric(String value) {
        if (value == null)
            return 0;
        String cleaned = value.replaceAll("[^\\d.-]", "");
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String truncateLabel(String label, int maxLen) {
        if (label == null)
            return "-";
        return label.length() > maxLen ? label.substring(0, maxLen - 2) + ".." : label;
    }

    private String formatNumber(double val) {
        if (val == (long) val) {
            return String.format("%,d", (long) val);
        }
        return String.format("%,.1f", val);
    }

    /**
     * Round up to a "nice" max value for axis scaling
     */
    private double niceMax(double val) {
        if (val <= 0)
            return 1;
        double magnitude = Math.pow(10, Math.floor(Math.log10(val)));
        double normalized = val / magnitude;
        if (normalized <= 1)
            return magnitude;
        if (normalized <= 2)
            return 2 * magnitude;
        if (normalized <= 5)
            return 5 * magnitude;
        return 10 * magnitude;
    }

    private String escapeXml(String text) {
        if (text == null)
            return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
