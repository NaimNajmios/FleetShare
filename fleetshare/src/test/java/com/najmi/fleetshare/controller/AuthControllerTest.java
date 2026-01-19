package com.najmi.fleetshare.controller;

import com.najmi.fleetshare.dto.RegistrationDTO;
import com.najmi.fleetshare.service.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class AuthControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private RegistrationService registrationService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser
    public void processRegistration_withInvalidData_shouldReturnToForm() throws Exception {
        // Arrange
        // Create an invalid DTO (empty password, etc.)
        // We simulate the form submission parameters directly

        // Act
        mockMvc.perform(post("/register")
                .with(csrf())
                .param("fullName", "Test User")
                .param("email", "invalid-email") // Invalid email
                .param("password", "") // Empty password
                .param("confirmPassword", "")
                .param("userRole", "renter")
                .param("agreeTerms", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register")); // Should return to form

        // Assert
        // Service should NOT be called if validation works
        verify(registrationService, never()).registerUser(any(RegistrationDTO.class));
    }

    @Test
    public void register_withFlashAttribute_shouldPreserveDto() throws Exception {
        RegistrationDTO existingDto = new RegistrationDTO();
        existingDto.setEmail("existing@example.com");

        mockMvc.perform(get("/register")
                .flashAttr("registrationDTO", existingDto))
                .andExpect(status().isOk())
                .andExpect(model().attribute("registrationDTO", existingDto))
                .andExpect(view().name("auth/register"));
    }
}
