package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByFleetOwnerId(Long fleetOwnerId);
}
