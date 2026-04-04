package com.najmi.fleetshare.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper class for summary values with optional trend data
 */
public class SummaryValue extends HashMap<String, Object> {

    public SummaryValue() {
        super();
    }

    public SummaryValue(String displayValue) {
        super();
        put("value", displayValue);
    }

    public SummaryValue(String displayValue, double trend) {
        super();
        put("value", displayValue);
        put("trend", trend);
    }

    public String getValue() {
        return (String) get("value");
    }

    public Double getTrend() {
        return (Double) get("trend");
    }

    public static Map<String, Object> toMap(String displayValue) {
        Map<String, Object> map = new HashMap<>();
        map.put("value", displayValue);
        return map;
    }

    public static Map<String, Object> toMap(String displayValue, double trend) {
        Map<String, Object> map = new HashMap<>();
        map.put("value", displayValue);
        map.put("trend", trend);
        return map;
    }
}
