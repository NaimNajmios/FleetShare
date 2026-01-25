package com.najmi.fleetshare.performance;

import com.najmi.fleetshare.entity.VehiclePriceHistory;
import com.najmi.fleetshare.repository.VehiclePriceHistoryRepository;
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
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@Transactional
public class VehiclePricePerformanceTest {

    @Autowired
    private VehiclePriceHistoryRepository priceHistoryRepository;

    private List<Long> vehicleIds;

    @BeforeEach
    public void setup() {
        vehicleIds = new ArrayList<>();
        int vehicleCount = 100;
        int pricesPerVehicle = 50; // Total 5000 records
        LocalDateTime baseDate = LocalDateTime.now().minusDays(100);

        List<VehiclePriceHistory> histories = new ArrayList<>();

        for (long i = 1; i <= vehicleCount; i++) {
            vehicleIds.add(i);
            for (int j = 0; j < pricesPerVehicle; j++) {
                VehiclePriceHistory vph = new VehiclePriceHistory();
                vph.setVehicleId(i);
                vph.setRatePerDay(new BigDecimal("100.00").add(new BigDecimal(j)));
                // Ensure unique timestamps per vehicle to have deterministic latest
                vph.setEffectiveStartDate(baseDate.plusDays(j));
                histories.add(vph);
            }
        }
        priceHistoryRepository.saveAll(histories);
        priceHistoryRepository.flush();
    }

    @Test
    public void testFindLatestPricesPerformance() {
        // Warm up
        priceHistoryRepository.findLatestPricesForVehicles(vehicleIds);

        long startTime = System.nanoTime();
        List<VehiclePriceHistory> results = priceHistoryRepository.findLatestPricesForVehicles(vehicleIds);
        long endTime = System.nanoTime();

        long durationMs = (endTime - startTime) / 1_000_000;
        System.out.println("Execution Time: " + durationMs + " ms");

        assertEquals(100, results.size(), "Should return latest price for each vehicle");

        // Verify correctness for one vehicle
        VehiclePriceHistory firstResult = results.stream()
            .filter(r -> r.getVehicleId().equals(1L))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Vehicle 1 not found"));

        // The last added price (index 49) should be the latest
        assertEquals(0, new BigDecimal("149.00").compareTo(firstResult.getRatePerDay()),
            "Latest rate should be 149.00");
    }
}
