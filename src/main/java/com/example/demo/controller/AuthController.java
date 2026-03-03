package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.LoginRequest;
import com.example.demo.entity.User;
import com.example.demo.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
        origins = {
                "http://localhost:5173",
                "http://localhost:5192"
        },
        allowCredentials = "true"
)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ===============================
    // LOGIN
    // ===============================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest,
                                   HttpServletResponse response) {

        try {

            // 1️⃣ Authenticate user
            User user = authService.authenticate(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            // 2️⃣ Generate JWT token
            String token = authService.generateToken(user);

            // 3️⃣ Create HttpOnly Cookie
            Cookie cookie = new Cookie("authToken", token);
            cookie.setHttpOnly(true);      // Cannot access via JS
            cookie.setSecure(false);       // true only if HTTPS
            cookie.setPath("/");           // Accessible for entire app
            cookie.setMaxAge(60 * 60);     // 1 hour

            // 🚫 DO NOT SET DOMAIN FOR LOCALHOST
            // cookie.setDomain("localhost");  ❌ REMOVE THIS

            response.addCookie(cookie);

            // 4️⃣ Send response body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Login successful");
            responseBody.put("username", user.getUsername());
            responseBody.put("role", user.getRole().name());

            return ResponseEntity.ok(responseBody);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    // ===============================
    // LOGOUT (Optional but Recommended)
    // ===============================
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Retrieve authenticated user from the request
            User user = (User) request.getAttribute("authenticatedUser");

            // Delegate logout operation to the service layer
            authService.logout(user);

            // Clear the authentication token cookie
            Cookie cookie = new Cookie("authToken", null);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);

            // Success response
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Logout successful");
            return ResponseEntity.ok(responseBody);

        } catch (RuntimeException e) {
            // Error response
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Logout failed");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}