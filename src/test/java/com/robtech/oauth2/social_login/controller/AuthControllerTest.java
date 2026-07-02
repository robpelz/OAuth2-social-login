package com.robtech.oauth2.social_login.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * <p><b>Bezeichnung:</b> Authentifizierungs- und Sicherheits-Integrationstest</p>
 * <p><b>Beschreibung:</b> Überprüft die Zugriffskontrolle der Endpunkte sowie das Verhalten der OAuth2-Sitzungen.</p>
 * <p><b>Test-Umfeld:</b> Geladen via SpringBootTest im isolierten MockMvc-Kontext.</p>
 *
 * @author RobTech
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Entkopplung von externen Providern (Google/GitHub) durch isoliertes Test-Profil
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Target: GET /api/v1/demo
     * Szenario: Erfolgreicher Zugriff auf geschützten Ressourcen-Endpunkt via OAuth2-Login.
     * Logik: Simuliert ein gültiges OAuth2AuthenticationToken mit Benutzerattributen.
     */
    @Test
    @DisplayName("GET /api/v1/demo - Erfolgreich mit OAuth2 Authentifizierung")
    public void testHelloEndpointWithOAuth2() throws Exception {
        // [Arrange & Act] Anfrageaufbau mit injizierten OAuth2-Attributen
        mockMvc.perform(get("/api/v1/demo")
                        .with(oauth2Login()))
                // [Assert] Validierung des HTTP-Status und der JSON-Antwortstruktur
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello from secure endpoint!"))
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.status").value("Authenticated ✅"));
    }

    /**
     * Target: GET /api/v1/demo/user
     * Szenario: Abfrage von Benutzerprofil-Details nach erfolgreichem Social-Login.
     * Logik: Prüft die korrekte Extraktion und Konvertierung der Principal-Attribute im Controller.
     */
    @Test
    @DisplayName("GET /api/v1/demo/user - Gibt korrekte User-Details nach OAuth2-Login zurück")
    public void testUserInfoEndpointWithOAuth2() throws Exception {
        // [Act] Abruf des Benutzerinfo-Endpunkts mit simulierter Session
        mockMvc.perform(get("/api/v1/demo/user")
                        .with(oauth2Login()))
                // [Assert] Abgleich der extrahierten Datenfelder
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.name").exists());
    }

    /**
     * Target: GET /api/v1/demo
     * Szenario: Zugriff verweigert für anonyme/unauthentifizierte Anfragen.
     * Logik: Stellt sicher, dass die Spring-Security-Filterkette unautorisierte Anfragen blockiert.
     * Hinweis: Spring Security leitet für Web-Anwendungen standardmäßig zur Login-Seite um (302 Found).
     */
    @Test
    @DisplayName("GET /api/v1/demo - Schlägt fehl (302 Redirect) ohne Authentifizierung")
    public void testSecureEndpointWithoutAuth() throws Exception {
        // [Act & Assert] Anonymer Zugriff führt zur Weiterleitung auf die Login-Seite
        mockMvc.perform(get("/api/v1/demo"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    /**
     * Target: GET /login
     * Szenario: Rendern der Standard-Login-Auswahlseite.
     * Logik: Überprüfung des MVC-View-Resolvers für die Benutzeroberfläche.
     */
    @Test
    @DisplayName("GET /login - Zeigt die Login-Seite an")
    public void testLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login")); // Validiert, ob das korrekte Template (z.B. Thymeleaf) geladen wird
    }

    /**
     * Target: GET /login?error=true
     * Szenario: Fehlerbehandlung bei abgebrochenem oder fehlgeschlagenem OAuth2-Handshake.
     * Logik: Prüft, ob das UI-Model die Fehlermeldung für den Endbenutzer bereithält.
     */
    @Test
    @DisplayName("GET /login?error=true - Zeigt Fehlermeldung bei Login-Abbruch")
    public void testLoginError() throws Exception {
        mockMvc.perform(get("/login?error=true"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("errorMessage")); // Pflichtfeld zur UI-Fehleranzeige
    }

    /**
     * Target: GET /login?logout=true
     * Szenario: Erfolgreiche Abmeldung und Zerstörung der Security-Session.
     * Logik: Verifiziert die Weiterleitung zur Login-Seite inklusive Erfolgsindikator im Model.
     */
    @Test
    @DisplayName("GET /login?logout=true - Zeigt Logout-Bestätigung an")
    public void testLogoutSuccess() throws Exception {
        mockMvc.perform(get("/login?logout=true"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("logoutMessage")); // Pflichtfeld zur UI-Erfolgsanzeige
    }
}