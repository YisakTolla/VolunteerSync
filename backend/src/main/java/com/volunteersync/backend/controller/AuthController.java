package com.volunteersync.backend.controller;

import com.volunteersync.backend.dto.*;
import com.volunteersync.backend.service.UserService;
import com.volunteersync.backend.util.JwtTokenUtil;
import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.entity.UserType;
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

            // DEBUG: Log the raw request
            System.out.println("=== RAW REQUEST DEBUG ===");
            System.out.println("User Type: " + request.getUserType());
            System.out.println("Organization Name: " + request.getOrganizationName());
            System.out.println("First Name: " + request.getFirstName());
            System.out.println("Last Name: " + request.getLastName());
            System.out.println("Email: " + request.getEmail());
            System.out.println("========================");
            
            // Basic validation - the @Valid annotation and custom validator handle detailed validation
            if (request.getEmail() == null || request.getPassword() == null || request.getUserType() == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Email, password, and user type are required"));
            }

            // Validate password confirmation
            if (!request.isPasswordMatching()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Passwords do not match"));
            }

            // Validate user type specific fields
            UserType userType;
            try {
                userType = UserType.valueOf(request.getUserType().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Invalid user type. Must be VOLUNTEER or ORGANIZATION"));
            }

            // Type-specific validation
            if (userType == UserType.VOLUNTEER) {
                if (request.getFirstName() == null || request.getFirstName().trim().isEmpty() ||
                    request.getLastName() == null || request.getLastName().trim().isEmpty()) {
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse(false, "First name and last name are required for volunteers"));
                }
                
                // Ensure organization name is not provided for volunteers
                if (request.getOrganizationName() != null && !request.getOrganizationName().trim().isEmpty()) {
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse(false, "Organization name should not be provided for volunteers"));
                }
                
            } else if (userType == UserType.ORGANIZATION) {
                if (request.getOrganizationName() == null || request.getOrganizationName().trim().isEmpty()) {
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse(false, "Organization name is required for organizations"));
                }
                
                // Ensure individual names are not provided for organizations
                if ((request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) ||
                    (request.getLastName() != null && !request.getLastName().trim().isEmpty())) {
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse(false, "Individual names should not be provided for organizations"));
                }
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
            // Validate user type
            UserType userType;
            try {
                userType = UserType.valueOf(request.getUserType().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Invalid user type. Must be VOLUNTEER or ORGANIZATION"));
            }

            // Verify Google token
            GoogleTokenInfo tokenInfo = userService.verifyGoogleToken(request.getGoogleToken());

            if (tokenInfo == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Invalid Google token"));
            }

            // Check if user exists
            User user = userService.findByEmail(tokenInfo.getEmail());
            
            if (user == null) {
                // Create new user based on type
                user = userService.createGoogleUser(tokenInfo, userType);
            } else {
                // For existing users, verify they're trying to login with the correct type
                if (!user.getUserType().equals(userType)) {
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse(false, 
                                "Account exists with different user type. Please login as " + 
                                user.getUserType().name().toLowerCase()));
                }
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

    // Additional endpoint to check user type for existing accounts
    @PostMapping("/check-user-type")
    public ResponseEntity<?> checkUserType(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Email is required"));
            }

            User user = userService.findByEmail(email);
            
            if (user == null) {
                return ResponseEntity.ok(new ApiResponse(true, "Email available", 
                        Map.of("exists", false)));
            } else {
                return ResponseEntity.ok(new ApiResponse(true, "User found", 
                        Map.of("exists", true, "userType", user.getUserType().name())));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to check user type: " + e.getMessage()));
        }
    }
}