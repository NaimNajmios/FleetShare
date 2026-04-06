package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.FleetOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FleetOwnerRepository extends JpaRepository<FleetOwner, Long> {
    Optional<FleetOwner> findByUserId(Long userId);

    @Query("SELECT f FROM FleetOwner f WHERE " +
           "LOWER(f.businessName) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(f.contactPhone) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<FleetOwner> searchAll(@Param("term") String term);
}
