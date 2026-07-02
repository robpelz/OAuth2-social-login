package com.robtech.oauth2.social_login.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "expired", required = false) String expired,
            Model model) {

        logger.info("Login-Seite aufgerufen - error: {}, logout: {}, expired: {}", error, logout, expired);

        // Fehlermeldungen setzen
        if (error != null) {
            model.addAttribute("errorMessage", "Anmeldung fehlgeschlagen. Bitte versuche es erneut.");
            logger.warn("Login-Fehler: {}", error);
        }

        if (logout != null) {
            model.addAttribute("logoutMessage", "Du wurdest erfolgreich abgemeldet.");
            logger.info("User hat sich abgemeldet");
        }

        if (expired != null) {
            model.addAttribute("errorMessage", "Deine Sitzung ist abgelaufen. Bitte melde dich erneut an.");
            logger.info("Session abgelaufen");
        }

        return "login";
    }
}