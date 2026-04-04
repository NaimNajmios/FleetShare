package com.najmi.fleetshare.repository;

import com.najmi.fleetshare.entity.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {
    List<ServiceProvider> findByFleetOwnerId(Long fleetOwnerId);
    
    List<ServiceProvider> findByFleetOwnerIdAndIsActive(Long fleetOwnerId, Boolean isActive);
    
    List<ServiceProvider> findByFleetOwnerIdAndSpecialty(Long fleetOwnerId, String specialty);
    
    List<ServiceProvider> findByFleetOwnerIdAndProviderNameContaining(Long fleetOwnerId, String name);
}
