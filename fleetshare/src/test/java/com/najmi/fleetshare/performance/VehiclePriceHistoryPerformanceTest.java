package com.najmi.fleetshare.performance;

import com.najmi.fleetshare.entity.VehiclePriceHistory;
import com.najmi.fleetshare.repository.VehiclePriceHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
public class VehiclePriceHistoryPerformanceTest {

    @Autowired
    private VehiclePriceHistoryRepository repository;

    @BeforeEach
    public void setup() {
        // Clear existing data
        repository.deleteAll();

        // Populate data
        List<VehiclePriceHistory> batch = new ArrayList<>();
        int vehicleCount = 1000;
        int historyPerVehicle = 20;

        for (long vId = 1; vId <= vehicleCount; vId++) {
            for (int i = 0; i < historyPerVehicle; i++) {
                VehiclePriceHistory vph = new VehiclePriceHistory();
                vph.setVehicleId(vId);
                // Varying rates
                vph.setRatePerDay(BigDecimal.valueOf(100 + i));
                // Dates spread out over the last 20 days
                vph.setEffectiveStartDate(LocalDateTime.now().minusDays(historyPerVehicle - i));

                batch.add(vph);
            }
        }
        repository.saveAll(batch);
        repository.flush();
    }

    @Test
    public void testFindLatestPricesPerformance() {
        List<Long> vehicleIds = LongStream.rangeClosed(1, 1000).boxed().collect(Collectors.toList());

        long start = System.currentTimeMillis();
        List<VehiclePriceHistory> results = repository.findLatestPricesForVehicles(vehicleIds);
        long duration = System.currentTimeMillis() - start;

        System.out.println("PERFORMANCE_METRIC_START");
        System.out.println("Execution time: " + duration + " ms");
        System.out.println("Result size: " + results.size());
        System.out.println("PERFORMANCE_METRIC_END");

        assertEquals(1000, results.size());

        // Check accuracy for first vehicle
        VehiclePriceHistory first = results.stream().filter(r -> r.getVehicleId() == 1L).findFirst().orElseThrow();
        // The last added entry (index 19) should be the latest, rate = 100 + 19 = 119
        assertEquals(0, new BigDecimal("119.00").compareTo(first.getRatePerDay()), "Latest rate should be 119");
    }
}
