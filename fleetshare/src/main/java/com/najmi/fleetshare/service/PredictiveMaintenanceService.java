package com.najmi.fleetshare.service;

import com.najmi.fleetshare.entity.Vehicle;
import com.najmi.fleetshare.entity.VehicleMaintenance;
import com.najmi.fleetshare.entity.MaintenanceSchedule;
import com.najmi.fleetshare.repository.VehicleMaintenanceRepository;
import com.najmi.fleetshare.repository.VehicleRepository;
import com.najmi.fleetshare.repository.MaintenanceScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PredictiveMaintenanceService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleMaintenanceRepository maintenanceRepository;

    @Autowired
    private MaintenanceScheduleRepository scheduleRepository;

    public List<Map<String, Object>> getVehicleRiskScores(Long ownerId) {
        List<Vehicle> vehicles = vehicleRepository.findByFleetOwnerId(ownerId);
        List<Map<String, Object>> riskScores = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {
            Map<String, Object> riskData = new HashMap<>();
            riskData.put("vehicleId", vehicle.getVehicleId());
            riskData.put("registrationNo", vehicle.getRegistrationNo());
            riskData.put("model", vehicle.getModel());
            riskData.put("brand", vehicle.getBrand());
            riskData.put("mileage", vehicle.getMileage());
            riskData.put("manufacturingYear", vehicle.getManufacturingYear());

            int riskScore = calculateRiskScore(vehicle);
            String riskLevel = getRiskLevel(riskScore);

            riskData.put("riskScore", riskScore);
            riskData.put("riskLevel", riskLevel);
            riskData.put("recommendations", getRecommendations(vehicle, riskScore));

            riskScores.add(riskData);
        }

        return riskScores.stream()
            .sorted((a, b) -> ((Integer) b.get("riskScore")).compareTo((Integer) a.get("riskScore")))
            .collect(Collectors.toList());
    }

    private int calculateRiskScore(Vehicle vehicle) {
        int score = 0;

        if (vehicle.getMileage() != null) {
            if (vehicle.getMileage() > 100000) score += 40;
            else if (vehicle.getMileage() > 50000) score += 25;
            else if (vehicle.getMileage() > 30000) score += 15;
            else if (vehicle.getMileage() > 15000) score += 5;
        }

        if (vehicle.getManufacturingYear() != null) {
            int age = LocalDate.now().getYear() - vehicle.getManufacturingYear();
            if (age > 10) score += 30;
            else if (age > 7) score += 20;
            else if (age > 5) score += 10;
            else if (age > 3) score += 5;
        }

        List<VehicleMaintenance> recentMaintenance = maintenanceRepository.findByVehicleId(vehicle.getVehicleId());
        long daysSinceLastMaintenance = recentMaintenance.stream()
            .filter(m -> m.getActualEndTime() != null)
            .filter(m -> m.getCurrentStatus() == VehicleMaintenance.MaintenanceStatus.COMPLETED)
            .max(Comparator.comparing(VehicleMaintenance::getActualEndTime))
            .map(m -> ChronoUnit.DAYS.between(m.getActualEndTime().toLocalDate(), LocalDate.now()))
            .orElse(180L);

        if (daysSinceLastMaintenance > 180) score += 25;
        else if (daysSinceLastMaintenance > 90) score += 15;
        else if (daysSinceLastMaintenance > 60) score += 5;

        long pendingCount = recentMaintenance.stream()
            .filter(m -> m.getCurrentStatus() == VehicleMaintenance.MaintenanceStatus.PENDING)
            .count();
        score += (int) pendingCount * 10;

        return Math.min(score, 100);
    }

    private String getRiskLevel(int score) {
        if (score >= 75) return "CRITICAL";
        if (score >= 50) return "HIGH";
        if (score >= 25) return "MODERATE";
        return "LOW";
    }

    private List<String> getRecommendations(Vehicle vehicle, int riskScore) {
        List<String> recommendations = new ArrayList<>();

        if (riskScore >= 75) {
            recommendations.add("Immediate inspection required");
            recommendations.add("Consider comprehensive service");
        }

        if (vehicle.getMileage() != null && vehicle.getMileage() > 50000) {
            recommendations.add("Check brake pads and tires");
        }

        if (vehicle.getMileage() != null && vehicle.getMileage() > 75000) {
            recommendations.add("Inspect transmission and engine components");
        }

        List<MaintenanceSchedule> schedules = scheduleRepository.findByVehicleId(vehicle.getVehicleId());
        if (schedules.isEmpty()) {
            recommendations.add("Set up recurring maintenance schedule");
        }

        if (vehicle.getManufacturingYear() != null) {
            int age = LocalDate.now().getYear() - vehicle.getManufacturingYear();
            if (age > 7) {
                recommendations.add("Check suspension and exhaust system");
            }
        }

        if (recommendations.isEmpty()) {
            recommendations.add("Vehicle is in good condition");
        }

        return recommendations;
    }

    public Map<String, Object> getPredictedMaintenance(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElse(null);
        if (vehicle == null) return null;

        Map<String, Object> prediction = new HashMap<>();
        
        List<VehicleMaintenance> history = maintenanceRepository.findByVehicleId(vehicleId);
        BigDecimal avgCost = BigDecimal.ZERO;
        int completedCount = 0;
        
        for (VehicleMaintenance m : history) {
            if (m.getCurrentStatus() == VehicleMaintenance.MaintenanceStatus.COMPLETED && m.getFinalCost() != null) {
                avgCost = avgCost.add(m.getFinalCost());
                completedCount++;
            }
        }
        
        if (completedCount > 0) {
            avgCost = avgCost.divide(BigDecimal.valueOf(completedCount), 2, RoundingMode.HALF_UP);
        }
        
        prediction.put("averageMaintenanceCost", avgCost);
        prediction.put("maintenanceCount", history.size());
        
        if (vehicle.getMileage() != null) {
            int nextServiceMileage = ((vehicle.getMileage() / 10000) + 1) * 10000;
            prediction.put("nextServiceMileage", nextServiceMileage);
            prediction.put("milesUntilService", nextServiceMileage - vehicle.getMileage());
        }
        
        return prediction;
    }

    public Map<String, Object> getFleetHealthSummary(Long ownerId) {
        List<Map<String, Object>> riskScores = getVehicleRiskScores(ownerId);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalVehicles", riskScores.size());
        summary.put("criticalCount", riskScores.stream().filter(r -> "CRITICAL".equals(r.get("riskLevel"))).count());
        summary.put("highCount", riskScores.stream().filter(r -> "HIGH".equals(r.get("riskLevel"))).count());
        summary.put("moderateCount", riskScores.stream().filter(r -> "MODERATE".equals(r.get("riskLevel"))).count());
        summary.put("lowCount", riskScores.stream().filter(r -> "LOW".equals(r.get("riskLevel"))).count());
        
        double avgRiskScore = riskScores.stream()
            .mapToInt(r -> (Integer) r.get("riskScore"))
            .average()
            .orElse(0.0);
        summary.put("averageRiskScore", Math.round(avgRiskScore * 100.0) / 100.0);
        
        return summary;
    }
}
