package com.robtech.oauth2.social_login.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/demo")
public class AuthController {

    @GetMapping
    public ResponseEntity<Map<String, String>> hello(Principal principal) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello from secure endpoint!");
        response.put("user", principal.getName());
        response.put("status", "Authenticated ✅");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", auth.getName());
        userInfo.put("authenticated", auth.isAuthenticated());
        userInfo.put("authorities", auth.getAuthorities());
        return ResponseEntity.ok(userInfo);
    }
}