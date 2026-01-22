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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password="
})
public class VehiclePricePerformanceTest {

    @Autowired
    private VehiclePriceHistoryRepository repository;

    private List<Long> vehicleIds;

    @BeforeEach
    public void setup() {
        // Create dummy data for 3 vehicles, with multiple price changes
        VehiclePriceHistory v1_old = createPrice(1L, new BigDecimal("50.00"), LocalDateTime.now().minusDays(10));
        VehiclePriceHistory v1_mid = createPrice(1L, new BigDecimal("60.00"), LocalDateTime.now().minusDays(5));
        VehiclePriceHistory v1_new = createPrice(1L, new BigDecimal("70.00"), LocalDateTime.now().minusDays(1)); // Latest

        VehiclePriceHistory v2_old = createPrice(2L, new BigDecimal("100.00"), LocalDateTime.now().minusDays(20));
        VehiclePriceHistory v2_new = createPrice(2L, new BigDecimal("120.00"), LocalDateTime.now().minusDays(2)); // Latest

        VehiclePriceHistory v3_single = createPrice(3L, new BigDecimal("200.00"), LocalDateTime.now().minusDays(1));

        VehiclePriceHistory v1_future = createPrice(1L, new BigDecimal("999.00"), LocalDateTime.now().plusDays(5)); // Future

        repository.saveAll(Arrays.asList(v1_old, v1_mid, v1_new, v2_old, v2_new, v3_single, v1_future));

        vehicleIds = Arrays.asList(1L, 2L, 3L);
    }

    private VehiclePriceHistory createPrice(Long vehicleId, BigDecimal rate, LocalDateTime effectiveDate) {
        VehiclePriceHistory vph = new VehiclePriceHistory();
        vph.setVehicleId(vehicleId);
        vph.setRatePerDay(rate);
        vph.setEffectiveStartDate(effectiveDate);
        return vph;
    }

    @Test
    public void testFindLatestPricesForVehicles_Correctness() {
        // This tests the existing method
        List<VehiclePriceHistory> results = repository.findLatestPricesForVehicles(vehicleIds);
        verifyResults(results);
    }

    @Test
    public void testFindLatestPricesForVehiclesOptimized_Correctness() {
        // This tests the new optimized method
        List<VehiclePriceHistory> results = repository.findLatestPricesForVehiclesOptimized(vehicleIds);
        verifyResults(results);
    }

    private void verifyResults(List<VehiclePriceHistory> results) {
        assertThat(results).hasSize(3);

        Map<Long, BigDecimal> priceMap = results.stream()
                .collect(Collectors.toMap(VehiclePriceHistory::getVehicleId, VehiclePriceHistory::getRatePerDay));

        // Vehicle 1: Should be 70.00 (v1_new), not 999.00 (future) or 60.00 (old)
        assertThat(priceMap.get(1L)).isEqualByComparingTo("70.00");

        // Vehicle 2: Should be 120.00
        assertThat(priceMap.get(2L)).isEqualByComparingTo("120.00");

        // Vehicle 3: Should be 200.00
        assertThat(priceMap.get(3L)).isEqualByComparingTo("200.00");
    }
}
