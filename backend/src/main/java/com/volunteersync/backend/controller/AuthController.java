package com.volunteersync.backend.controller;

import com.volunteersync.backend.dto.*;
import com.volunteersync.backend.service.UserService;
import com.volunteersync.backend.util.JwtTokenUtil;
import com.volunteersync.backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Configure properly for production
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        try {
            // Validate request
            if (request.getEmail() == null || request.getPassword() == null ||
                    request.getFirstName() == null || request.getLastName() == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "All fields are required"));
            }

            // Check if user already exists
            if (userService.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Email already registered"));
            }

            // Create user
            User user = userService.createUser(request);

            // Generate JWT token
            String token = jwtTokenUtil.generateToken(user.getEmail());

            return ResponseEntity.ok(new JwtResponse(token, user));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleAuth(@Valid @RequestBody GoogleAuthRequest request) {
        try {
            // Verify Google token
            GoogleTokenInfo tokenInfo = userService.verifyGoogleToken(request.getGoogleToken());

            if (tokenInfo == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Invalid Google token"));
            }

            // Check if user exists or create new one
            User user = userService.findByEmail(tokenInfo.getEmail());
            if (user == null) {
                user = userService.createGoogleUser(tokenInfo, request.getUserType());
            }

            // Generate JWT token
            String token = jwtTokenUtil.generateToken(user.getEmail());

            return ResponseEntity.ok(new JwtResponse(token, user));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Google authentication failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request) {
        try {
            // Authenticate user
            User user = userService.authenticateUser(request.getEmail(), request.getPassword());
            
            if (user == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Invalid email or password"));
            }

            // Generate JWT token
            String token = jwtTokenUtil.generateToken(user.getEmail());

            return ResponseEntity.ok(new JwtResponse(token, user));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtTokenUtil.extractTokenFromHeader(authHeader);
            
            if (token == null || !jwtTokenUtil.validateToken(token)) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Invalid token"));
            }

            String username = jwtTokenUtil.getUsernameFromToken(token);
            String newToken = jwtTokenUtil.generateToken(username);

            return ResponseEntity.ok(new ApiResponse(true, "Token refreshed", 
                    Map.of("token", newToken)));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Token refresh failed: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtTokenUtil.extractTokenFromHeader(authHeader);
            
            if (token == null || !jwtTokenUtil.validateToken(token)) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Invalid token"));
            }

            String email = jwtTokenUtil.getUsernameFromToken(token);
            User user = userService.findByEmail(email);

            if (user == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "User not found"));
            }

            return ResponseEntity.ok(new ApiResponse(true, "User found", user));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to get user: " + e.getMessage()));
        }
    }
}