package com.najmi.fleetshare.performance;

import com.najmi.fleetshare.entity.Vehicle;
import com.najmi.fleetshare.entity.VehiclePriceHistory;
import com.najmi.fleetshare.repository.VehiclePriceHistoryRepository;
import com.najmi.fleetshare.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@Transactional
public class VehiclePricePerformanceTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehiclePriceHistoryRepository priceHistoryRepository;

    private List<Long> vehicleIds;

    @BeforeEach
    public void setup() {
        // Seed data
        vehicleIds = new ArrayList<>();
        int vehicleCount = 1000;
        int historyPerVehicle = 5;

        for (int i = 0; i < vehicleCount; i++) {
            Vehicle vehicle = new Vehicle();
            vehicle.setFleetOwnerId(1L);
            vehicle.setBrand("Brand" + i);
            vehicle.setModel("Model" + i);
            vehicle.setRegistrationNo("REG" + i);
            vehicle.setCreatedAt(LocalDateTime.now());
            vehicle.setUpdatedAt(LocalDateTime.now());
            vehicle = vehicleRepository.save(vehicle);
            vehicleIds.add(vehicle.getVehicleId());

            for (int j = 0; j < historyPerVehicle; j++) {
                VehiclePriceHistory history = new VehiclePriceHistory();
                history.setVehicleId(vehicle.getVehicleId());
                history.setRatePerDay(BigDecimal.valueOf(100 + j));
                // Set dates: 5 days ago, 4 days ago, ..., today.
                // Latest one is the one with highest effective date <= now.
                history.setEffectiveStartDate(LocalDateTime.now().minusDays(historyPerVehicle - j));
                priceHistoryRepository.save(history);
            }
        }
    }

    @Test
    public void testFindLatestPricesPerformance() {
        // Warm up (optional, but good for JIT)
        priceHistoryRepository.findLatestPricesForVehicles(vehicleIds.subList(0, 10));

        long startTime = System.currentTimeMillis();

        List<VehiclePriceHistory> results = priceHistoryRepository.findLatestPricesForVehicles(vehicleIds);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("PERFORMANCE_METRIC: Execution time: " + duration + " ms");

        assertEquals(vehicleIds.size(), results.size(), "Should return one price per vehicle");

        // Verify correctness: all should be the latest (highest rate in my seed logic)
        for (VehiclePriceHistory history : results) {
             // In seed, the last one added has the highest rate (100 + 4 = 104) and latest date (today)
             assertEquals(0, history.getRatePerDay().compareTo(BigDecimal.valueOf(104)),
                 "Should match latest rate for vehicle " + history.getVehicleId());
        }
    }
}
