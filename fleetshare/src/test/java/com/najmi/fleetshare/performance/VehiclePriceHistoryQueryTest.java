package com.najmi.fleetshare.performance;

import com.najmi.fleetshare.entity.Vehicle;
import com.najmi.fleetshare.entity.VehiclePriceHistory;
import com.najmi.fleetshare.repository.VehiclePriceHistoryRepository;
import com.najmi.fleetshare.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
public class VehiclePriceHistoryQueryTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehiclePriceHistoryRepository priceHistoryRepository;

    @Test
    public void testFindLatestPricesForVehiclesPerformance() {
        // Setup data
        int vehicleCount = 50;
        int pricesPerVehicle = 100;
        List<Long> vehicleIds = new ArrayList<>();

        for (int i = 0; i < vehicleCount; i++) {
            Vehicle v = new Vehicle();
            v.setFleetOwnerId(1L);
            v.setModel("Model " + i);
            v.setBrand("Brand " + i);
            v.setManufacturingYear(2020);
            v.setRegistrationNo("REG-" + i);
            v.setCreatedAt(LocalDateTime.now());
            v.setUpdatedAt(LocalDateTime.now());
            v = vehicleRepository.save(v);
            vehicleIds.add(v.getVehicleId());

            for (int j = 0; j < pricesPerVehicle; j++) {
                VehiclePriceHistory vph = new VehiclePriceHistory();
                vph.setVehicleId(v.getVehicleId());
                vph.setRatePerDay(BigDecimal.valueOf(100 + j));
                // Dates from 100 days ago to today
                vph.setEffectiveStartDate(LocalDateTime.now().minusDays(pricesPerVehicle - j));
                priceHistoryRepository.save(vph);
            }
        }

        priceHistoryRepository.flush();

        // Warm up (optional, but good for JIT)
        priceHistoryRepository.findLatestPricesForVehicles(vehicleIds);

        // Measure
        long start = System.nanoTime();
        List<VehiclePriceHistory> results = priceHistoryRepository.findLatestPricesForVehicles(vehicleIds);
        long end = System.nanoTime();
        long durationMs = (end - start) / 1_000_000;

        System.out.println("--------------------------------------------------");
        System.out.println("Execution Time: " + durationMs + " ms");
        System.out.println("Records returned: " + results.size());
        System.out.println("--------------------------------------------------");

        assertEquals(vehicleCount, results.size());

        // Verify we got the latest price (which should be the highest rate in our setup)
        // The last added price (j=99) has the latest date (today) and rate 199.
        for (VehiclePriceHistory vph : results) {
            assertEquals(0, vph.getRatePerDay().compareTo(BigDecimal.valueOf(199)),
                "Expected latest rate 199 but got " + vph.getRatePerDay() + " for vehicle " + vph.getVehicleId());
        }
    }
}
