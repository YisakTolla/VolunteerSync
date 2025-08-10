package com.volunteersync.backend.controller;

import com.volunteersync.backend.service.AuthService;
import com.volunteersync.backend.service.AuthService.AuthResponseDTO;
import com.volunteersync.backend.service.AuthService.RegisterRequest;
import com.volunteersync.backend.service.AuthService.LoginRequest;
import com.volunteersync.backend.service.AuthService.GoogleAuthRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Authentication Controller - handles login, registration, and auth endpoints
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    // ==========================================
    // AUTHENTICATION ENDPOINTS
    // ==========================================

    /**
     * Register new user
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponseDTO response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Login user
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponseDTO response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Refresh JWT token
     * POST /api/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from "Bearer " prefix
            String refreshToken = authHeader.substring(7);
            AuthResponseDTO response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Logout user (invalidate token)
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            authService.logout(token);
            return ResponseEntity.ok(new SuccessResponse("Logged out successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Google OAuth registration/login
     * POST /api/auth/google
     */
    @PostMapping("/google")
    public ResponseEntity<?> googleAuth(@Valid @RequestBody GoogleAuthRequest request) {
        try {
            AuthResponseDTO response = authService.googleAuth(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // REQUEST/RESPONSE CLASSES
    // ==========================================

    public static class ErrorResponse {
        private String error;
        private long timestamp;

        public ErrorResponse(String error) {
            this.error = error;
            this.timestamp = System.currentTimeMillis();
        }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    public static class SuccessResponse {
        private String message;
        private long timestamp;

        public SuccessResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}