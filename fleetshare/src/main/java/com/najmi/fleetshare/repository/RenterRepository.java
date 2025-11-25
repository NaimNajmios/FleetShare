package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.Renter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RenterRepository extends JpaRepository<Renter, Long> {
    Optional<Renter> findByUserId(Long userId);
}
