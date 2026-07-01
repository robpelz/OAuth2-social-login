package com.robtech.oauth2.social_login.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF-Schutz deaktivieren - für OAuth2 mit Token-basierter Authentifizierung nicht erforderlich
        http.csrf(csrf -> csrf.disable());

        // Autorisierung für HTTP-Anfragen konfigurieren
        http.authorizeHttpRequests(auth -> auth
                // Öffentliche Ressourcen freigeben
                .requestMatchers(
                        "/login",
                        "/css/**",
                        "/js/**",
                        "/webjars/**",
                        "/favicon.ico"
                ).permitAll()
                // Alle anderen Anfragen erfordern Authentifizierung
                .anyRequest().authenticated()
        );

        // OAuth2 Login mit eigener Login-Seite konfigurieren
        http.oauth2Login(oauth2 -> oauth2
                // Eigene Login-Seite verwenden
                .loginPage("/login")
                // Nach erfolgreichem Login zur Startseite
                .defaultSuccessUrl("/api/v1/demo", true)
                // Bei Fehler zurück zur Login-Seite mit Fehlerparameter
                .failureUrl("/login?error=true")
        );

        // Logout konfigurieren
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        );

        return http.build();
    }
}