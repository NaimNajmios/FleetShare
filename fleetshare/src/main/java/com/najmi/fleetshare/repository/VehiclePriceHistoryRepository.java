package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.VehiclePriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiclePriceHistoryRepository extends JpaRepository<VehiclePriceHistory, Long> {

    @Query("SELECT vph FROM VehiclePriceHistory vph WHERE vph.vehicleId = :vehicleId ORDER BY vph.effectiveStartDate DESC LIMIT 1")
    Optional<VehiclePriceHistory> findLatestPriceByVehicleId(Long vehicleId);

    @Query("SELECT vph FROM VehiclePriceHistory vph WHERE vph.vehicleId IN :vehicleIds")
    List<VehiclePriceHistory> findByVehicleIdIn(java.util.Collection<Long> vehicleIds);
}
