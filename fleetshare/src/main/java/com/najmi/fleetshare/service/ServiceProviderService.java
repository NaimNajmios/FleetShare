package com.najmi.fleetshare.service;

import com.najmi.fleetshare.entity.ServiceProvider;
import com.najmi.fleetshare.repository.ServiceProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServiceProviderService {

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    public List<ServiceProvider> getAllProviders(Long ownerId) {
        return serviceProviderRepository.findByFleetOwnerId(ownerId);
    }

    public List<ServiceProvider> getActiveProviders(Long ownerId) {
        return serviceProviderRepository.findByFleetOwnerIdAndIsActive(ownerId, true);
    }

    public ServiceProvider getProviderById(Long providerId) {
        return serviceProviderRepository.findById(providerId).orElse(null);
    }

    public ServiceProvider createProvider(ServiceProvider provider) {
        provider.setCreatedAt(LocalDateTime.now());
        provider.setUpdatedAt(LocalDateTime.now());
        if (provider.getRating() == null) {
            provider.setRating(BigDecimal.ZERO);
        }
        if (provider.getTotalJobs() == null) {
            provider.setTotalJobs(0);
        }
        return serviceProviderRepository.save(provider);
    }

    public ServiceProvider updateProvider(Long providerId, ServiceProvider updated) {
        return serviceProviderRepository.findById(providerId).map(provider -> {
            provider.setProviderName(updated.getProviderName());
            provider.setContactPerson(updated.getContactPerson());
            provider.setPhone(updated.getPhone());
            provider.setEmail(updated.getEmail());
            provider.setAddress(updated.getAddress());
            provider.setSpecialty(updated.getSpecialty());
            provider.setNotes(updated.getNotes());
            provider.setIsActive(updated.getIsActive());
            provider.setUpdatedAt(LocalDateTime.now());
            return serviceProviderRepository.save(provider);
        }).orElse(null);
    }

    public void deleteProvider(Long providerId) {
        serviceProviderRepository.findById(providerId).ifPresent(provider -> {
            provider.setIsActive(false);
            provider.setUpdatedAt(LocalDateTime.now());
            serviceProviderRepository.save(provider);
        });
    }

    public List<ServiceProvider> searchProviders(Long ownerId, String name) {
        return serviceProviderRepository.findByFleetOwnerIdAndProviderNameContaining(ownerId, name);
    }

    public List<ServiceProvider> getProvidersBySpecialty(Long ownerId, String specialty) {
        return serviceProviderRepository.findByFleetOwnerIdAndSpecialty(ownerId, specialty);
    }

    public void incrementJobCount(Long providerId) {
        serviceProviderRepository.findById(providerId).ifPresent(provider -> {
            provider.setTotalJobs(provider.getTotalJobs() + 1);
            provider.setUpdatedAt(LocalDateTime.now());
            serviceProviderRepository.save(provider);
        });
    }

    public void updateRating(Long providerId, BigDecimal newRating) {
        serviceProviderRepository.findById(providerId).ifPresent(provider -> {
            BigDecimal currentRating = provider.getRating();
            Integer totalJobs = provider.getTotalJobs();
            if (currentRating == null) currentRating = BigDecimal.ZERO;
            if (totalJobs == null) totalJobs = 0;

            BigDecimal avgRating = currentRating.multiply(BigDecimal.valueOf(totalJobs))
                    .add(newRating)
                    .divide(BigDecimal.valueOf(totalJobs + 1), 2, RoundingMode.HALF_UP);

            provider.setRating(avgRating);
            provider.setTotalJobs(totalJobs + 1);
            provider.setUpdatedAt(LocalDateTime.now());
            serviceProviderRepository.save(provider);
        });
    }
}
