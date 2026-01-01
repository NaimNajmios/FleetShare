package com.najmi.fleetshare.service;

import com.najmi.fleetshare.dto.AddVehicleRequest;
import com.najmi.fleetshare.entity.Vehicle;
import com.najmi.fleetshare.entity.VehiclePriceHistory;
import com.najmi.fleetshare.repository.VehiclePriceHistoryRepository;
import com.najmi.fleetshare.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test") // Use a test profile if available, otherwise it falls back to default
public class VehiclePerformanceTest {

    @Autowired
    private VehicleManagementService vehicleService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehiclePriceHistoryRepository priceHistoryRepository;

    @Test
    public void testGetAllVehiclesPerformance() {
        // Setup: Create 10 vehicles
        List<Long> vehicleIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Vehicle vehicle = new Vehicle();
            vehicle.setFleetOwnerId(1L); // Mock owner
            vehicle.setBrand("Brand" + i);
            vehicle.setModel("Model" + i);
            vehicle.setRegistrationNo("REG" + i);
            vehicle.setManufacturingYear(2020);
            vehicle.setCategory("Sedan");
            vehicle.setFuelType("Petrol");
            vehicle.setTransmissionType("Automatic");
            vehicle.setMileage(1000);
            vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
            vehicle.setCreatedAt(LocalDateTime.now());
            vehicle.setUpdatedAt(LocalDateTime.now());
            vehicle = vehicleRepository.save(vehicle);
            vehicleIds.add(vehicle.getVehicleId());

            // Add 1000 price history records for each vehicle
            List<VehiclePriceHistory> history = new ArrayList<>();
            for (int j = 0; j < 1000; j++) {
                VehiclePriceHistory price = new VehiclePriceHistory();
                price.setVehicleId(vehicle.getVehicleId());
                price.setRatePerDay(BigDecimal.valueOf(100 + j));
                price.setEffectiveStartDate(LocalDateTime.now().minusDays(j));
                history.add(price);
            }
            priceHistoryRepository.saveAll(history);
        }

        // Measure
        long startTime = System.currentTimeMillis();
        var dtos = vehicleService.getAllVehicles();
        long endTime = System.currentTimeMillis();

        System.out.println("Performance Test - getAllVehicles took: " + (endTime - startTime) + "ms");
        System.out.println("Total DTOs: " + dtos.size());

        // Assert that we got results
        assertNotNull(dtos);
    }
}
