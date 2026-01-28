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
@Transactional
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
public class VehiclePriceRepositoryTest {

    @Autowired
    private VehiclePriceHistoryRepository repository;

    @BeforeEach
    public void setup() {
        repository.deleteAll();

        // Setup data for 50 vehicles
        List<VehiclePriceHistory> histories = new ArrayList<>();
        for (long vehicleId = 1; vehicleId <= 50; vehicleId++) {
            // For each vehicle, add 5 price changes
            for (int i = 0; i < 5; i++) {
                VehiclePriceHistory vph = new VehiclePriceHistory();
                vph.setVehicleId(vehicleId);
                vph.setRatePerDay(BigDecimal.valueOf(100 + i * 10));
                // Dates: Today minus (5-i) days.
                // i=0: -5 days (Oldest)
                // i=4: -1 day (Newest valid)
                vph.setEffectiveStartDate(LocalDateTime.now().minusDays(5 - i));
                histories.add(vph);
            }

            // Add a future price that should NOT be picked up
            VehiclePriceHistory futureVph = new VehiclePriceHistory();
            futureVph.setVehicleId(vehicleId);
            futureVph.setRatePerDay(BigDecimal.valueOf(999));
            futureVph.setEffectiveStartDate(LocalDateTime.now().plusDays(10));
            histories.add(futureVph);
        }
        repository.saveAll(histories);
    }

    @Test
    public void testFindLatestPricesForVehicles_Correctness() {
        List<Long> vehicleIds = new ArrayList<>();
        for (long i = 1; i <= 50; i++) {
            vehicleIds.add(i);
        }

        // Warm up
        repository.findLatestPricesForVehicles(vehicleIds);

        long start = System.nanoTime();
        List<VehiclePriceHistory> results = repository.findLatestPricesForVehicles(vehicleIds);
        long duration = System.nanoTime() - start;

        System.out.println("Query execution time (ms): " + duration / 1_000_000.0);

        assertEquals(50, results.size(), "Should return one price per vehicle");

        for (VehiclePriceHistory vph : results) {
            // The latest valid price (i=4) was 100 + 4*10 = 140
            assertEquals(0, BigDecimal.valueOf(140).compareTo(vph.getRatePerDay()),
                "Incorrect rate for vehicle " + vph.getVehicleId());
        }
    }
}
