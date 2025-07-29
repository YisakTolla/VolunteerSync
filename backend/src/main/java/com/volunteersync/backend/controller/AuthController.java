package com.volunteersync.backend.controller;

import com.volunteersync.backend.dto.*;
import com.volunteersync.backend.dto.response.ApiResponse;
import com.volunteersync.backend.entity.user.User;
import com.volunteersync.backend.entity.user.UserType;
import com.volunteersync.backend.service.UserService;
import com.volunteersync.backend.util.JwtTokenUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.Optional;

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

            // Basic validation - the @Valid annotation and custom validator handle detailed
            // validation
            if (request.getEmail() == null || request.getPassword() == null || request.getUserType() == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Email, password, and user type are required"));
            }

            // Validate password confirmation
            if (!request.isPasswordMatching()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Passwords do not match"));
            }

            // Validate user type specific fields
            UserType userType;
            try {
                userType = UserType.valueOf(request.getUserType().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid user type. Must be VOLUNTEER or ORGANIZATION"));
            }

            // Type-specific validation
            if (userType == UserType.VOLUNTEER) {
                if (request.getFirstName() == null || request.getFirstName().trim().isEmpty() ||
                        request.getLastName() == null || request.getLastName().trim().isEmpty()) {
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.error("First name and last name are required for volunteers"));
                }

                // Ensure organization name is not provided for volunteers
                if (request.getOrganizationName() != null && !request.getOrganizationName().trim().isEmpty()) {
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.error("Organization name should not be provided for volunteers"));
                }

            } else if (userType == UserType.ORGANIZATION) {
                if (request.getOrganizationName() == null || request.getOrganizationName().trim().isEmpty()) {
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.error("Organization name is required for organizations"));
                }

                // Ensure individual names are not provided for organizations
                if ((request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) ||
                        (request.getLastName() != null && !request.getLastName().trim().isEmpty())) {
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.error("Individual names should not be provided for organizations"));
                }
            }

            // Check if user already exists
            if (userService.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Email already registered"));
            }

            // Create user
            User user = userService.createUser(request);

            // Generate JWT token
            String token = jwtTokenUtil.generateToken(user.getEmail());

            return ResponseEntity.ok(ApiResponse.success("User registered successfully", 
                    new JwtResponse(token, user)));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Registration failed: " + e.getMessage()));
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
                        .body(ApiResponse.error("Invalid user type. Must be VOLUNTEER or ORGANIZATION"));
            }

            // Verify Google token
            GoogleTokenInfo tokenInfo = userService.verifyGoogleToken(request.getGoogleToken());

            if (tokenInfo == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid Google token"));
            }

            // Check if user exists - FIXED: Use correct method name
            Optional<User> userOptional = userService.findByEmail(tokenInfo.getEmail());
            User user = null;

            if (userOptional.isEmpty()) {
                // Create new user based on type
                user = userService.createGoogleUser(tokenInfo, userType);
            } else {
                user = userOptional.get();
                // For existing users, verify they're trying to login with the correct type
                if (!user.getUserType().equals(userType)) {
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.error(
                                    "Account exists with different user type. Please login as " +
                                            user.getUserType().name().toLowerCase()));
                }
            }

            // Generate JWT token
            String token = jwtTokenUtil.generateToken(user.getEmail());

            return ResponseEntity.ok(ApiResponse.success("Google authentication successful",
                    new JwtResponse(token, user)));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Google authentication failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request) {
        try {
            // Authenticate user
            User user = userService.authenticateUser(request.getEmail(), request.getPassword());

            if (user == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid email or password"));
            }

            // Generate JWT token
            String token = jwtTokenUtil.generateToken(user.getEmail());

            return ResponseEntity.ok(ApiResponse.success("Login successful", 
                    new JwtResponse(token, user)));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtTokenUtil.extractTokenFromHeader(authHeader);

            if (token == null || !jwtTokenUtil.validateToken(token)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid token"));
            }

            // FIXED: Use correct method name
            String username = jwtTokenUtil.getUsernameFromToken(token);
            String newToken = jwtTokenUtil.generateToken(username);

            return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully",
                    Map.of("token", newToken)));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Token refresh failed: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            // Add extensive debugging
            System.out.println("=== /ME ENDPOINT DEBUG ===");
            System.out.println("Received Authorization header: " + authHeader);

            String token = jwtTokenUtil.extractTokenFromHeader(authHeader);
            System.out.println("Extracted token: "
                    + (token != null ? "Token exists (length: " + token.length() + ")" : "Token is null"));

            if (token == null) {
                System.out.println("ERROR: Token extraction failed");
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("No token provided"));
            }

            boolean isValid = jwtTokenUtil.validateToken(token);
            System.out.println("Token validation result: " + isValid);

            if (!isValid) {
                System.out.println("ERROR: Token validation failed");
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid token"));
            }

            // FIXED: Use correct method name
            String email = jwtTokenUtil.getUsernameFromToken(token);
            System.out.println("Email extracted from token: " + email);

            if (email == null) {
                System.out.println("ERROR: Email extraction from token failed");
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Cannot extract email from token"));
            }

            // FIXED: Use correct method name
            Optional<User> userOptional = userService.findByEmail(email);
            System.out.println("User found: " + (userOptional.isPresent() ? "Yes" : "No"));

            if (userOptional.isEmpty()) {
                System.out.println("ERROR: User not found for email: " + email);
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("User not found"));
            }

            User user = userOptional.get();
            System.out.println("User details:");
            System.out.println("  ID: " + user.getId());
            System.out.println("  Email: " + user.getEmail());
            System.out.println("  User Type: " + user.getUserType());
            System.out.println("  Organization Name: " + user.getOrganizationName());
            System.out.println("  First Name: " + user.getFirstName());
            System.out.println("  Last Name: " + user.getLastName());
            System.out.println("========================");

            return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));

        } catch (Exception e) {
            System.out.println("ERROR in /me endpoint: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get user: " + e.getMessage()));
        }
    }

    // Additional endpoint to check user type for existing accounts
    @PostMapping("/check-user-type")
    public ResponseEntity<?> checkUserType(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Email is required"));
            }

            // FIXED: Use correct method name
            Optional<User> userOptional = userService.findByEmail(email);

            if (userOptional.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success("Email available",
                        Map.of("exists", false)));
            } else {
                User user = userOptional.get();
                return ResponseEntity.ok(ApiResponse.success("User found",
                        Map.of("exists", true, "userType", user.getUserType().name())));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to check user type: " + e.getMessage()));
        }
    }

    @PutMapping("/complete-profile")
    public ResponseEntity<?> completeProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> profileData) {
        try {
            String token = jwtTokenUtil.extractTokenFromHeader(authHeader);
            
            if (token == null || !jwtTokenUtil.validateToken(token)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid token"));
            }

            // FIXED: Use correct method name
            String email = jwtTokenUtil.getUsernameFromToken(token);
            
            // FIXED: Use correct method name
            Optional<User> userOptional = userService.findByEmail(email);

            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("User not found"));
            }

            User user = userOptional.get();
            // Update user profile with additional information
            user = userService.updateUserProfile(user, profileData);

            return ResponseEntity.ok(ApiResponse.success("Profile completed successfully", user));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to complete profile: " + e.getMessage()));
        }
    }
}