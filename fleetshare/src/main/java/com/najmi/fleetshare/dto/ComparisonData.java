package com.najmi.fleetshare.dto;

import java.util.List;

/**
 * DTO for comparison report data
 */
public class ComparisonData {

    private String periodALabel;
    private String periodBLabel;
    private String periodAStart;
    private String periodAEnd;
    private String periodBStart;
    private String periodBEnd;
    private List<ComparisonRow> rows;

    public ComparisonData() {
    }

    public String getPeriodALabel() {
        return periodALabel;
    }

    public void setPeriodALabel(String periodALabel) {
        this.periodALabel = periodALabel;
    }

    public String getPeriodBLabel() {
        return periodBLabel;
    }

    public void setPeriodBLabel(String periodBLabel) {
        this.periodBLabel = periodBLabel;
    }

    public String getPeriodAStart() {
        return periodAStart;
    }

    public void setPeriodAStart(String periodAStart) {
        this.periodAStart = periodAStart;
    }

    public String getPeriodAEnd() {
        return periodAEnd;
    }

    public void setPeriodAEnd(String periodAEnd) {
        this.periodAEnd = periodAEnd;
    }

    public String getPeriodBStart() {
        return periodBStart;
    }

    public void setPeriodBStart(String periodBStart) {
        this.periodBStart = periodBStart;
    }

    public String getPeriodBEnd() {
        return periodBEnd;
    }

    public void setPeriodBEnd(String periodBEnd) {
        this.periodBEnd = periodBEnd;
    }

    public List<ComparisonRow> getRows() {
        return rows;
    }

    public void setRows(List<ComparisonRow> rows) {
        this.rows = rows;
    }

    public static class ComparisonRow {
        private String label;
        private String valueA;
        private String valueB;
        private double change;
        private boolean isPercentage;

        public ComparisonRow() {
        }

        public ComparisonRow(String label, String valueA, String valueB, double change, boolean isPercentage) {
            this.label = label;
            this.valueA = valueA;
            this.valueB = valueB;
            this.change = change;
            this.isPercentage = isPercentage;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValueA() {
            return valueA;
        }

        public void setValueA(String valueA) {
            this.valueA = valueA;
        }

        public String getValueB() {
            return valueB;
        }

        public void setValueB(String valueB) {
            this.valueB = valueB;
        }

        public double getChange() {
            return change;
        }

        public void setChange(double change) {
            this.change = change;
        }

        public boolean isPercentage() {
            return isPercentage;
        }

        public void setPercentage(boolean percentage) {
            isPercentage = percentage;
        }
    }
}
