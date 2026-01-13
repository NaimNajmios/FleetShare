package com.najmi.fleetshare.performance;

import com.najmi.fleetshare.entity.Vehicle;
import com.najmi.fleetshare.repository.AddressRepository;
import com.najmi.fleetshare.repository.FleetOwnerRepository;
import com.najmi.fleetshare.repository.VehiclePriceHistoryRepository;
import com.najmi.fleetshare.repository.VehicleRepository;
import com.najmi.fleetshare.service.VehicleManagementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VehicleFilteringPerformanceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehiclePriceHistoryRepository priceHistoryRepository;

    @Mock
    private FleetOwnerRepository fleetOwnerRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private VehicleManagementService vehicleManagementService;

    @Test
    public void testGetAllVehiclesInefficient() {
        // Baseline: checks that getAllVehicles calls the broad findByIsDeletedFalse
        when(vehicleRepository.findByIsDeletedFalse()).thenReturn(Collections.emptyList());
        vehicleManagementService.getAllVehicles();
        verify(vehicleRepository).findByIsDeletedFalse();
    }

    @Test
    public void testGetAvailableVehiclesOptimized() {
       // Goal: Verify that getAvailableVehicles calls the specific status query
       when(vehicleRepository.findByStatusAndIsDeletedFalse(Vehicle.VehicleStatus.AVAILABLE))
           .thenReturn(Collections.emptyList());
       vehicleManagementService.getAvailableVehicles();
       verify(vehicleRepository).findByStatusAndIsDeletedFalse(Vehicle.VehicleStatus.AVAILABLE);
       verify(vehicleRepository, never()).findByIsDeletedFalse();
    }
}
