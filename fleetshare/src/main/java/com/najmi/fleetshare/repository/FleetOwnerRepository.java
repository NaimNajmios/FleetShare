package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.FleetOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FleetOwnerRepository extends JpaRepository<FleetOwner, Long> {
    Optional<FleetOwner> findByUserId(Long userId);
}
