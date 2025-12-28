package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.VehiclePriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehiclePriceHistoryRepository extends JpaRepository<VehiclePriceHistory, Long> {

    @Query("SELECT vph FROM VehiclePriceHistory vph WHERE vph.vehicleId = :vehicleId AND vph.effectiveStartDate <= CURRENT_TIMESTAMP ORDER BY vph.effectiveStartDate DESC LIMIT 1")
    Optional<VehiclePriceHistory> findLatestPriceByVehicleId(Long vehicleId);

    /**
     * Find the effective rate for a vehicle on a specific date.
     * Returns the rate with the latest effectiveStartDate that is <= the target
     * date.
     */
    @Query("SELECT vph FROM VehiclePriceHistory vph WHERE vph.vehicleId = :vehicleId AND vph.effectiveStartDate <= :date ORDER BY vph.effectiveStartDate DESC LIMIT 1")
    Optional<VehiclePriceHistory> findEffectiveRateOnDate(@Param("vehicleId") Long vehicleId,
            @Param("date") LocalDateTime date);

    /**
     * Find all scheduled rates (including future) for a vehicle.
     */
    List<VehiclePriceHistory> findByVehicleIdOrderByEffectiveStartDateDesc(Long vehicleId);

    @Query("SELECT vph FROM VehiclePriceHistory vph WHERE vph.vehicleId IN :vehicleIds")
    List<VehiclePriceHistory> findByVehicleIdIn(java.util.Collection<Long> vehicleIds);
}
