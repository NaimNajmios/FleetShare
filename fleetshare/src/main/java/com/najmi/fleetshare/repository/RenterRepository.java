package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.Renter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RenterRepository extends JpaRepository<Renter, Long> {
    Optional<Renter> findByUserId(Long userId);

    @Query("SELECT r FROM Renter r WHERE " +
           "LOWER(r.fullName) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(r.phoneNumber) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Renter> searchAll(@Param("term") String term);
}
