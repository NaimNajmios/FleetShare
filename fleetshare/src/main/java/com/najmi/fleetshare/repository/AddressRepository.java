package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("SELECT a FROM Address a WHERE a.addressUserId = :userId ORDER BY a.effectiveStartDate DESC LIMIT 1")
    Optional<Address> findLatestAddressByUserId(Long userId);

    @Query("SELECT a FROM Address a WHERE a.addressUserId IN :userIds")
    List<Address> findByAddressUserIdIn(java.util.Collection<Long> userIds);
}
