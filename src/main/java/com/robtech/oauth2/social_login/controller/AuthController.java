package com.robtech.oauth2.social_login.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/demo")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @GetMapping
    public ResponseEntity<Map<String, Object>> hello(Principal principal) {
        if (principal == null) {
            logger.warn("Unauhorisierter Zugriff auf /api/v1/demo");
            return ResponseEntity.status(401).build();
        }

        logger.info("User {} hat auf sicheren Endpunkt zugegriffen", principal.getName());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello from secure endpoint!");
        response.put("user", principal.getName());
        response.put("status", "Authenticated ✅");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Unauhorisierter Zugriff auf /api/v1/demo/user");
            return ResponseEntity.status(401).build();
        }

        logger.debug("User-Info für {} abgerufen", authentication.getName());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", authentication.getName());
        userInfo.put("authenticated", authentication.isAuthenticated());
        userInfo.put("authorities", authentication.getAuthorities());
        userInfo.put("principal", authentication.getPrincipal());

        return ResponseEntity.ok(userInfo);
    }
}