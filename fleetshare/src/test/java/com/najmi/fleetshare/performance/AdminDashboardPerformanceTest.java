package com.najmi.fleetshare.performance;

import com.najmi.fleetshare.controller.AdminController;
import com.najmi.fleetshare.dto.BookingCountDTO;
import com.najmi.fleetshare.dto.SessionUser;
import com.najmi.fleetshare.service.BookingService;
import com.najmi.fleetshare.service.PaymentService;
import com.najmi.fleetshare.service.UserManagementService;
import com.najmi.fleetshare.service.VehicleManagementService;
import com.najmi.fleetshare.util.SessionHelper;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminDashboardPerformanceTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private UserManagementService userManagementService;

    @Mock
    private VehicleManagementService vehicleManagementService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private AdminController adminController;

    @Test
    public void testDashboardUsesOptimizedMethods() {
        // Setup SessionUser mock
        SessionUser user = new SessionUser();
        user.setUserId(1L);

        try (MockedStatic<SessionHelper> sessionHelper = mockStatic(SessionHelper.class)) {
            sessionHelper.when(() -> SessionHelper.getCurrentUser(session)).thenReturn(user);

            // Mock optimized service returns
            when(bookingService.getPlatformBookingCounts()).thenReturn(new BookingCountDTO(100, 20, 50, 30));
            when(bookingService.getRecentBookings(5)).thenReturn(Collections.emptyList());
            when(paymentService.getTotalPlatformRevenue()).thenReturn(BigDecimal.TEN);

            // Execute
            adminController.dashboard(session, model);

            // Verify optimized methods are called
            verify(bookingService).getPlatformBookingCounts();
            verify(bookingService).getRecentBookings(5);
            verify(paymentService).getTotalPlatformRevenue();

            // Verify expensive methods are NOT called
            verify(bookingService, never()).getAllBookings();
            verify(paymentService, never()).getAllPayments();
        }
    }
}
