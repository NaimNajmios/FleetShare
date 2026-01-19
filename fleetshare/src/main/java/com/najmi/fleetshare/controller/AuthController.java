package com.najmi.fleetshare.controller;

import com.najmi.fleetshare.dto.RegistrationDTO;
import com.najmi.fleetshare.exception.RegistrationException;
import com.najmi.fleetshare.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final RegistrationService registrationService;

    public AuthController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registrationDTO", new RegistrationDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute RegistrationDTO registrationDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        try {
            registrationService.registerUser(registrationDTO);
            redirectAttributes.addFlashAttribute("success", "Account created successfully! Please login.");
            return "redirect:/login";
        } catch (RegistrationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            // Preserve form data on error
            redirectAttributes.addFlashAttribute("registrationDTO", registrationDTO);
            return "redirect:/register";
        }
    }
}
