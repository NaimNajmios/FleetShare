package com.najmi.fleetshare.security;

import com.najmi.fleetshare.dto.SessionUser;
import com.najmi.fleetshare.service.UserSessionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserSessionService userSessionService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // Load complete user session data
        SessionUser sessionUser = userSessionService.loadSessionUser(authentication.getName());

        // Store in HTTP session
        HttpSession session = request.getSession();
        session.setAttribute("sessionUser", sessionUser);

        String redirectUrl = "/login?error=true";

        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String role = userDetails.getAuthorities().iterator().next().getAuthority();

            if (role.equals("ROLE_PLATFORM_ADMIN")) {
                redirectUrl = "/admin/dashboard";
            } else if (role.equals("ROLE_FLEET_OWNER")) {
                redirectUrl = "/owner/dashboard";
            } else if (role.equals("ROLE_RENTER")) {
                redirectUrl = "/renter/vehicles";
            }
        }

        response.sendRedirect(redirectUrl);
    }
}
