package com.najmi.fleetshare.service;

import com.najmi.fleetshare.entity.MaintenancePart;
import com.najmi.fleetshare.repository.MaintenancePartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaintenancePartService {

    @Autowired
    private MaintenancePartRepository partRepository;

    public List<MaintenancePart> getPartsByMaintenanceId(Long maintenanceId) {
        return partRepository.findByMaintenanceIdOrderByCreatedAtDesc(maintenanceId);
    }

    public MaintenancePart getPartById(Long partId) {
        return partRepository.findById(partId).orElse(null);
    }

    public MaintenancePart addPart(MaintenancePart part) {
        if (part.getQuantity() != null && part.getUnitPrice() != null) {
            part.setTotalPrice(part.getUnitPrice().multiply(BigDecimal.valueOf(part.getQuantity())));
        }
        part.setCreatedAt(LocalDateTime.now());
        return partRepository.save(part);
    }

    public MaintenancePart updatePart(Long partId, MaintenancePart updated) {
        return partRepository.findById(partId).map(part -> {
            part.setPartName(updated.getPartName());
            part.setPartNumber(updated.getPartNumber());
            part.setQuantity(updated.getQuantity());
            part.setUnitPrice(updated.getUnitPrice());
            if (updated.getQuantity() != null && updated.getUnitPrice() != null) {
                part.setTotalPrice(updated.getUnitPrice().multiply(BigDecimal.valueOf(updated.getQuantity())));
            }
            part.setSupplier(updated.getSupplier());
            part.setNotes(updated.getNotes());
            return partRepository.save(part);
        }).orElse(null);
    }

    public void deletePart(Long partId) {
        partRepository.deleteById(partId);
    }

    public BigDecimal calculateTotalPartsCost(Long maintenanceId) {
        List<MaintenancePart> parts = partRepository.findByMaintenanceId(maintenanceId);
        BigDecimal total = BigDecimal.ZERO;
        for (MaintenancePart part : parts) {
            if (part.getTotalPrice() != null) {
                total = total.add(part.getTotalPrice());
            }
        }
        return total;
    }
}
