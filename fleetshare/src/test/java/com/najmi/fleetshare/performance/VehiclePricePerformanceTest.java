package com.najmi.fleetshare.performance;

import com.najmi.fleetshare.entity.Vehicle;
import com.najmi.fleetshare.entity.VehiclePriceHistory;
import com.najmi.fleetshare.repository.VehiclePriceHistoryRepository;
import com.najmi.fleetshare.repository.VehicleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
// Use a transactional test so data is rolled back, but we need to commit for performance measurement accuracy?
// Actually, for read performance, we need data committed or at least flushed.
// But since we are inserting in the same transaction, standard transactional test might be slow due to flush checks,
// or fast because it's in memory.
// Ideally for performance testing we want a separate setup, but @Transactional is fine for a quick check.
@Transactional
public class VehiclePricePerformanceTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehiclePriceHistoryRepository vehiclePriceHistoryRepository;

    @Test
    public void testFindLatestPricesPerformance() {
        int vehicleCount = 1000;
        int historyPerVehicle = 5;

        System.out.println("Seeding database with " + vehicleCount + " vehicles and " + (vehicleCount * historyPerVehicle) + " price records...");

        List<Vehicle> vehicles = new ArrayList<>();
        List<VehiclePriceHistory> histories = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < vehicleCount; i++) {
            Vehicle v = new Vehicle();
            v.setFleetOwnerId(1L); // Dummy owner
            v.setBrand("Brand" + i);
            v.setModel("Model" + i);
            v.setRegistrationNo("REG" + i);
            v.setCreatedAt(now);
            v.setUpdatedAt(now);
            vehicles.add(v);
        }
        vehicleRepository.saveAll(vehicles);
        vehicleRepository.flush(); // Ensure IDs are generated

        for (Vehicle v : vehicles) {
            for (int j = 0; j < historyPerVehicle; j++) {
                VehiclePriceHistory vph = new VehiclePriceHistory();
                vph.setVehicleId(v.getVehicleId());
                vph.setRatePerDay(BigDecimal.valueOf(100 + j));
                // Dates: now, now-1day, now-2days...
                // Latest is 'now' (j=0)
                vph.setEffectiveStartDate(now.minusDays(j));
                histories.add(vph);
            }
        }
        vehiclePriceHistoryRepository.saveAll(histories);
        vehiclePriceHistoryRepository.flush();

        List<Long> vehicleIds = vehicles.stream().map(Vehicle::getVehicleId).collect(Collectors.toList());

        System.out.println("Starting performance test...");

        // Warmup (Old)
        vehiclePriceHistoryRepository.findLatestPricesForVehicles(vehicleIds);
        // Warmup (New)
        vehiclePriceHistoryRepository.findLatestPricesForVehiclesNative(vehicleIds);

        // Test Old
        long startTimeOld = System.nanoTime();
        List<VehiclePriceHistory> resultsOld = vehiclePriceHistoryRepository.findLatestPricesForVehicles(vehicleIds);
        long endTimeOld = System.nanoTime();
        double durationMsOld = (endTimeOld - startTimeOld) / 1_000_000.0;
        System.out.println("Old Execution time: " + durationMsOld + " ms");

        // Test New
        long startTimeNew = System.nanoTime();
        List<VehiclePriceHistory> resultsNew = vehiclePriceHistoryRepository.findLatestPricesForVehiclesNative(vehicleIds);
        long endTimeNew = System.nanoTime();
        double durationMsNew = (endTimeNew - startTimeNew) / 1_000_000.0;
        System.out.println("New Execution time: " + durationMsNew + " ms");

        System.out.println("Results count: " + resultsNew.size());

        // Verification
        Assertions.assertEquals(vehicleCount, resultsNew.size(), "Should return one price per vehicle");

        // Verify correctness of data (latest price should be rate 100)
        // Check a few random entries
        for (VehiclePriceHistory vph : resultsNew) {
            // Depending on scale, might need compareTo
            Assertions.assertEquals(0, vph.getRatePerDay().compareTo(BigDecimal.valueOf(100)), "Latest rate should be 100");
        }

        System.out.println("Verification passed: Data correctness confirmed.");
    }
}
