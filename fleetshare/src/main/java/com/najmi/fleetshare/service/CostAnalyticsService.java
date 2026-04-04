package com.najmi.fleetshare.service;

import com.najmi.fleetshare.entity.VehicleMaintenance;
import com.najmi.fleetshare.repository.VehicleMaintenanceRepository;
import com.najmi.fleetshare.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CostAnalyticsService {

    @Autowired
    private VehicleMaintenanceRepository maintenanceRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public Map<String, Object> getCostSummary(Long ownerId) {
        List<VehicleMaintenance> allMaintenance = maintenanceRepository.findByFleetOwnerId(ownerId);
        
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal estimatedTotal = BigDecimal.ZERO;
        int completedCount = 0;
        int pendingCount = 0;
        
        for (VehicleMaintenance m : allMaintenance) {
            if (m.getFinalCost() != null) {
                totalCost = totalCost.add(m.getFinalCost());
                completedCount++;
            }
            if (m.getEstimatedCost() != null) {
                estimatedTotal = estimatedTotal.add(m.getEstimatedCost());
            }
            if (m.getCurrentStatus() == VehicleMaintenance.MaintenanceStatus.PENDING) {
                pendingCount++;
            }
        }
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalActualCost", totalCost);
        summary.put("totalEstimatedCost", estimatedTotal);
        summary.put("completedCount", completedCount);
        summary.put("pendingCount", pendingCount);
        summary.put("averageCost", completedCount > 0 
            ? totalCost.divide(BigDecimal.valueOf(completedCount), 2, RoundingMode.HALF_UP) 
            : BigDecimal.ZERO);
        summary.put("costVariance", estimatedTotal.subtract(totalCost));
        
        return summary;
    }

    public Map<String, BigDecimal> getCostByVehicle(Long ownerId) {
        List<VehicleMaintenance> allMaintenance = maintenanceRepository.findByFleetOwnerId(ownerId);
        Map<Long, BigDecimal> vehicleCosts = new HashMap<>();
        
        for (VehicleMaintenance m : allMaintenance) {
            if (m.getFinalCost() != null) {
                vehicleCosts.merge(m.getVehicleId(), m.getFinalCost(), BigDecimal::add);
            }
        }
        
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (Map.Entry<Long, BigDecimal> entry : vehicleCosts.entrySet()) {
            vehicleRepository.findById(entry.getKey()).ifPresent(vehicle -> {
                result.put(vehicle.getRegistrationNo() + " (" + vehicle.getModel() + ")", entry.getValue());
            });
        }
        
        return result;
    }

    public Map<String, BigDecimal> getCostByType(Long ownerId) {
        List<VehicleMaintenance> allMaintenance = maintenanceRepository.findByFleetOwnerId(ownerId);
        Map<String, BigDecimal> typeCosts = new HashMap<>();
        
        for (VehicleMaintenance m : allMaintenance) {
            String type = m.getMaintenanceType() != null ? m.getMaintenanceType() : "General";
            if (m.getFinalCost() != null) {
                typeCosts.merge(type, m.getFinalCost(), BigDecimal::add);
            } else if (m.getEstimatedCost() != null) {
                typeCosts.merge(type, m.getEstimatedCost(), BigDecimal::add);
            }
        }
        
        return typeCosts;
    }

    public Map<String, Map<String, Object>> getMonthlyTrends(Long ownerId, int months) {
        List<VehicleMaintenance> allMaintenance = maintenanceRepository.findByFleetOwnerId(ownerId);
        LocalDate now = LocalDate.now();
        
        Map<String, Map<String, Object>> monthlyData = new LinkedHashMap<>();
        
        for (int i = months - 1; i >= 0; i--) {
            LocalDate monthStart = now.minusMonths(i).withDayOfMonth(1);
            String monthKey = monthStart.getMonth().toString().substring(0, 3) + " " + monthStart.getYear();
            
            Map<String, Object> data = new HashMap<>();
            data.put("count", 0);
            data.put("cost", BigDecimal.ZERO);
            monthlyData.put(monthKey, data);
        }
        
        for (VehicleMaintenance m : allMaintenance) {
            if (m.getScheduledDate() != null) {
                LocalDate schedDate = m.getScheduledDate();
                if (schedDate.isAfter(now.minusMonths(months))) {
                    String monthKey = schedDate.getMonth().toString().substring(0, 3) + " " + schedDate.getYear();
                    if (monthlyData.containsKey(monthKey)) {
                        Map<String, Object> data = monthlyData.get(monthKey);
                        data.put("count", ((Integer) data.get("count")) + 1);
                        BigDecimal cost = m.getFinalCost() != null ? m.getFinalCost() : BigDecimal.ZERO;
                        data.put("cost", ((BigDecimal) data.get("cost")).add(cost));
                    }
                }
            }
        }
        
        return monthlyData;
    }

    public List<Map<String, Object>> getTopVehiclesByCost(Long ownerId, int limit) {
        Map<String, BigDecimal> vehicleCosts = getCostByVehicle(ownerId);
        
        return vehicleCosts.entrySet().stream()
            .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
            .limit(limit)
            .map(entry -> {
                Map<String, Object> item = new HashMap<>();
                item.put("vehicle", entry.getKey());
                item.put("totalCost", entry.getValue());
                return item;
            })
            .collect(Collectors.toList());
    }

    public Map<String, Object> getCostBreakdown(Long ownerId) {
        Map<String, Object> breakdown = new HashMap<>();
        
        breakdown.put("byVehicle", getCostByVehicle(ownerId));
        breakdown.put("byType", getCostByType(ownerId));
        breakdown.put("monthlyTrends", getMonthlyTrends(ownerId, 12));
        breakdown.put("summary", getCostSummary(ownerId));
        
        return breakdown;
    }
}
