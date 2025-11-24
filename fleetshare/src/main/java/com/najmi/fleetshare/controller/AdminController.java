package com.najmi.fleetshare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/")
    public String index() {
        // Best Practice: Redirect to dashboard so the layout logic
        // isn't duplicated. Otherwise, this returns "dashboard.html"
        // without the "layouts/base" wrapper.
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "dashboard";
    }
}