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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
public class VehiclePriceHistoryRepositoryTest {

    @Autowired
    private VehiclePriceHistoryRepository repository;

    @Test
    public void findLatestPricesForVehicles_ShouldReturnLatestPricePerVehicle() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime tomorrow = now.plusDays(1);
        LocalDateTime lastWeek = now.minusDays(7);

        // Vehicle 1: Old price, Current Price (Target), Future Price
        VehiclePriceHistory v1_old = createPrice(1L, new BigDecimal("100.00"), lastWeek);
        VehiclePriceHistory v1_current = createPrice(1L, new BigDecimal("120.00"), yesterday);
        VehiclePriceHistory v1_future = createPrice(1L, new BigDecimal("150.00"), tomorrow);

        // Vehicle 2: Just one price (Target)
        VehiclePriceHistory v2_current = createPrice(2L, new BigDecimal("200.00"), lastWeek);

        // Vehicle 3: Two prices today, should pick latest by time (Target)
        VehiclePriceHistory v3_first = createPrice(3L, new BigDecimal("300.00"), yesterday);
        VehiclePriceHistory v3_second = createPrice(3L, new BigDecimal("350.00"), yesterday.plusHours(1));

        repository.saveAll(Arrays.asList(v1_old, v1_current, v1_future, v2_current, v3_first, v3_second));

        List<Long> vehicleIds = Arrays.asList(1L, 2L, 3L);
        List<VehiclePriceHistory> results = repository.findLatestPricesForVehicles(vehicleIds);

        assertThat(results).hasSize(3);

        // Check Vehicle 1
        VehiclePriceHistory resultV1 = results.stream().filter(p -> p.getVehicleId().equals(1L)).findFirst().orElse(null);
        assertThat(resultV1).isNotNull();
        assertThat(resultV1.getRatePerDay()).isEqualByComparingTo("120.00");

        // Check Vehicle 2
        VehiclePriceHistory resultV2 = results.stream().filter(p -> p.getVehicleId().equals(2L)).findFirst().orElse(null);
        assertThat(resultV2).isNotNull();
        assertThat(resultV2.getRatePerDay()).isEqualByComparingTo("200.00");

        // Check Vehicle 3
        VehiclePriceHistory resultV3 = results.stream().filter(p -> p.getVehicleId().equals(3L)).findFirst().orElse(null);
        assertThat(resultV3).isNotNull();
        assertThat(resultV3.getRatePerDay()).isEqualByComparingTo("350.00");
    }

    private VehiclePriceHistory createPrice(Long vehicleId, BigDecimal rate, LocalDateTime effectiveDate) {
        VehiclePriceHistory vph = new VehiclePriceHistory();
        vph.setVehicleId(vehicleId);
        vph.setRatePerDay(rate);
        vph.setEffectiveStartDate(effectiveDate);
        return vph;
    }
}
