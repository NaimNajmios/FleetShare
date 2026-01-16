package com.najmi.fleetshare.controller;

import com.najmi.fleetshare.dto.PasswordChangeRequest;
import com.najmi.fleetshare.dto.SessionUser;
import com.najmi.fleetshare.entity.User;
import com.najmi.fleetshare.entity.UserRole;
import com.najmi.fleetshare.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class RenterControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private UserRepository userRepository;

    // We need to mock other dependencies to avoid context load failure if any
    // But SpringBootTest should handle most.
    // If RenterController has required dependencies, we might need to mock them if they fail initialization.
    // For now, let's assume H2 handles the repositories.
    // Services might need mocking if they do external calls.

    private MockMvc mockMvc;
    private MockHttpSession session;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        session = new MockHttpSession();
        SessionUser sessionUser = new SessionUser();
        sessionUser.setUserId(1L);
        sessionUser.setEmail("test@example.com");
        sessionUser.setRole(UserRole.RENTER);
        session.setAttribute("sessionUser", sessionUser);
    }

    @Test
    @WithMockUser(username = "renter", roles = {"RENTER"})
    public void updatePassword_success() throws Exception {
        // Arrange
        User user = new User();
        user.setUserId(1L);
        user.setEmail("test@example.com");
        String encodedOldPassword = passwordEncoder.encode("oldPassword123");
        user.setHashedPassword(encodedOldPassword);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        String jsonRequest = "{\"oldPassword\":\"oldPassword123\",\"newPassword\":\"newPassword456\",\"confirmPassword\":\"newPassword456\"}";

        // Act & Assert
        mockMvc.perform(post("/renter/profile/password")
                .session(session)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Password changed successfully"));
    }

    @Test
    @WithMockUser(username = "renter", roles = {"RENTER"})
    public void updatePassword_wrongOldPassword() throws Exception {
        // Arrange
        User user = new User();
        user.setUserId(1L);
        user.setEmail("test@example.com");
        String encodedOldPassword = passwordEncoder.encode("oldPassword123");
        user.setHashedPassword(encodedOldPassword);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        String jsonRequest = "{\"oldPassword\":\"wrongPassword\",\"newPassword\":\"newPassword456\",\"confirmPassword\":\"newPassword456\"}";

        // Act & Assert
        mockMvc.perform(post("/renter/profile/password")
                .session(session)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Incorrect old password"));
    }

    @Test
    @WithMockUser(username = "renter", roles = {"RENTER"})
    public void updatePassword_mismatch() throws Exception {
        // Arrange
        String jsonRequest = "{\"oldPassword\":\"oldPassword123\",\"newPassword\":\"newPassword456\",\"confirmPassword\":\"mismatch\"}";

        // Act & Assert
        mockMvc.perform(post("/renter/profile/password")
                .session(session)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("New passwords do not match"));
    }
}
