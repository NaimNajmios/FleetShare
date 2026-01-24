package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.VehiclePriceHistory;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
public class VehiclePriceHistoryRepositoryTest {

    @Autowired
    private VehiclePriceHistoryRepository repository;

    @Test
    public void testFindLatestPricesForVehicles() {
        LocalDateTime now = LocalDateTime.now();

        // Vehicle 1: Has a past price, a current price (latest effective), and a future price
        VehiclePriceHistory v1p1 = createPrice(1L, new BigDecimal("50.00"), now.minusDays(10));
        VehiclePriceHistory v1p2 = createPrice(1L, new BigDecimal("60.00"), now.minusDays(1)); // Should be selected
        VehiclePriceHistory v1p3 = createPrice(1L, new BigDecimal("70.00"), now.plusDays(5));

        // Vehicle 2: Has only one past price
        VehiclePriceHistory v2p1 = createPrice(2L, new BigDecimal("80.00"), now.minusDays(20)); // Should be selected

        // Vehicle 3: Has multiple past prices with same timestamp? Let's keep it simple first.
        VehiclePriceHistory v3p1 = createPrice(3L, new BigDecimal("90.00"), now.minusDays(5));
        VehiclePriceHistory v3p2 = createPrice(3L, new BigDecimal("100.00"), now.minusDays(2)); // Should be selected

        repository.saveAll(Arrays.asList(v1p1, v1p2, v1p3, v2p1, v3p1, v3p2));

        List<Long> vehicleIds = Arrays.asList(1L, 2L, 3L, 4L); // 4L has no prices

        List<VehiclePriceHistory> results = repository.findLatestPricesForVehicles(vehicleIds);

        assertEquals(3, results.size());

        Map<Long, BigDecimal> priceMap = results.stream()
                .collect(Collectors.toMap(VehiclePriceHistory::getVehicleId, VehiclePriceHistory::getRatePerDay));

        // Verify Vehicle 1
        assertTrue(priceMap.containsKey(1L));
        assertEquals(new BigDecimal("60.00"), priceMap.get(1L));

        // Verify Vehicle 2
        assertTrue(priceMap.containsKey(2L));
        assertEquals(new BigDecimal("80.00"), priceMap.get(2L));

        // Verify Vehicle 3
        assertTrue(priceMap.containsKey(3L));
        assertEquals(new BigDecimal("100.00"), priceMap.get(3L));

        // Vehicle 4 should not be in results
        assertTrue(!priceMap.containsKey(4L));
    }

    private VehiclePriceHistory createPrice(Long vehicleId, BigDecimal rate, LocalDateTime effectiveDate) {
        VehiclePriceHistory price = new VehiclePriceHistory();
        price.setVehicleId(vehicleId);
        price.setRatePerDay(rate);
        price.setEffectiveStartDate(effectiveDate);
        return price;
    }
}
