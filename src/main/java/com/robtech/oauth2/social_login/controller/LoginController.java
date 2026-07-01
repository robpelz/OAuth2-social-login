package com.robtech.oauth2.social_login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        // Fehlermeldung setzen
        if (error != null) {
            model.addAttribute("errorMessage", "Anmeldung fehlgeschlagen. Bitte versuche es erneut.");
        }

        // Logout-Meldung setzen
        if (logout != null) {
            model.addAttribute("logoutMessage", "Du wurdest erfolgreich abgemeldet.");
        }

        return "login";
    }
}