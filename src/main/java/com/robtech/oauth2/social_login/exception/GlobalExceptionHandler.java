package com.robtech.oauth2.social_login.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Globaler Exception-Handler für die gesamte Anwendung
 * Fängt alle Fehler ab und zeigt benutzerfreundliche Seiten
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Behandelt OAuth2-Authentifizierungsfehler
     * Wird ausgelöst, wenn der Login bei Google/GitHub fehlschlägt
     */
    @ExceptionHandler(OAuth2AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleOAuth2Error(OAuth2AuthenticationException e, Model model) {
        logger.warn("OAuth2 Login fehlgeschlagen: {}", e.getMessage());

        model.addAttribute("error", "Login mit OAuth2 fehlgeschlagen");
        model.addAttribute("errorDetail", getFriendlyErrorMessage(e.getMessage()));
        model.addAttribute("timestamp", java.time.LocalDateTime.now());

        return "error"; // error.html Template
    }

    /**
     * Behandelt allgemeine Authentifizierungsfehler
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleAuthenticationError(AuthenticationException e, Model model) {
        logger.warn("Authentifizierungsfehler: {}", e.getMessage());

        model.addAttribute("error", "Authentifizierung fehlgeschlagen");
        model.addAttribute("errorDetail", "Bitte versuche es erneut");
        model.addAttribute("timestamp", java.time.LocalDateTime.now());

        return "error";
    }

    /**
     * Behandelt alle unerwarteten Fehler (500)
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericError(Exception e, HttpServletRequest request, Model model) {
        logger.error("Unerwarteter Fehler: {}", e.getMessage(), e);

        model.addAttribute("error", "Ein unerwarteter Fehler ist aufgetreten");
        model.addAttribute("errorDetail", "Bitte versuche es später erneut");
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        model.addAttribute("path", request.getRequestURI());

        return "error";
    }

    /**
     * Wandelt technische Fehlermeldungen in benutzerfreundliche Texte um
     */
    private String getFriendlyErrorMessage(String technicalMessage) {
        if (technicalMessage == null) return "Unbekannter Fehler";

        if (technicalMessage.contains("access_denied")) {
            return "Zugriff verweigert. Du hast die Berechtigung nicht erteilt.";
        }
        if (technicalMessage.contains("invalid_scope")) {
            return "Ungültiger Berechtigungsbereich. Bitte kontaktiere den Administrator.";
        }
        if (technicalMessage.contains("invalid_grant")) {
            return "Ungültige Anmeldeinformationen. Bitte versuche es erneut.";
        }
        if (technicalMessage.contains("temporarily_unavailable")) {
            return "Der Dienst ist vorübergehend nicht verfügbar. Bitte versuche es später erneut.";
        }
        if (technicalMessage.contains("not found")) {
            return "Die angeforderte Ressource wurde nicht gefunden.";
        }
        if (technicalMessage.contains("timeout")) {
            return "Die Verbindung zum Authentifizierungsdienst wurde unterbrochen. Bitte versuche es erneut.";
        }

        // Technische Meldung kürzen für Benutzer
        return "Ein Fehler ist aufgetreten: " + technicalMessage.substring(0, Math.min(technicalMessage.length(), 100));
    }
}