package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.VehiclePriceHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class VehiclePriceHistoryRepositoryPerformanceTest {

    @Autowired
    private VehiclePriceHistoryRepository repository;

    @Test
    public void benchmarkFindLatestPricesForVehicles() {
        // Data setup
        int vehicleCount = 100;
        int historyPerVehicle = 50;
        List<VehiclePriceHistory> allHistory = new ArrayList<>();
        List<Long> vehicleIds = LongStream.rangeClosed(1, vehicleCount).boxed().collect(Collectors.toList());

        LocalDateTime now = LocalDateTime.now();

        for (Long vehicleId : vehicleIds) {
            for (int i = 0; i < historyPerVehicle; i++) {
                VehiclePriceHistory vph = new VehiclePriceHistory();
                vph.setVehicleId(vehicleId);
                vph.setRatePerDay(BigDecimal.valueOf(100 + i));
                // Dates going back from now
                vph.setEffectiveStartDate(now.minusDays(i));
                allHistory.add(vph);
            }
        }
        repository.saveAllAndFlush(allHistory);

        // Warmup
        repository.findLatestPricesForVehicles(vehicleIds.subList(0, 10));

        // Benchmark
        long startTime = System.nanoTime();
        List<VehiclePriceHistory> results = repository.findLatestPricesForVehicles(vehicleIds);
        long endTime = System.nanoTime();

        double durationMs = (endTime - startTime) / 1_000_000.0;
        System.out.println("Query execution time (ms): " + durationMs);
        System.out.println("Result size: " + results.size());

        assertThat(results).hasSize(vehicleCount);
    }
}
