package com.najmi.fleetshare.util;

import com.najmi.fleetshare.dto.SessionUser;
import com.najmi.fleetshare.entity.UserRole;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class SessionHelper {

    public static SessionUser getCurrentUser(HttpSession session) {
        return (SessionUser) session.getAttribute("sessionUser");
    }

    public static Long getCurrentUserId(HttpSession session) {
        SessionUser user = getCurrentUser(session);
        return user != null ? user.getUserId() : null;
    }

    public static boolean isAdmin(HttpSession session) {
        SessionUser user = getCurrentUser(session);
        return user != null && user.getRole() == UserRole.PLATFORM_ADMIN;
    }

    public static boolean isOwner(HttpSession session) {
        SessionUser user = getCurrentUser(session);
        return user != null && user.getRole() == UserRole.FLEET_OWNER;
    }

    public static boolean isRenter(HttpSession session) {
        SessionUser user = getCurrentUser(session);
        return user != null && user.getRole() == UserRole.RENTER;
    }
}
