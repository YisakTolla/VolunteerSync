package com.volunteersync.backend.service;

import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.entity.VolunteerProfile;
import com.volunteersync.backend.entity.OrganizationProfile;
import com.volunteersync.backend.enums.UserType;
import com.volunteersync.backend.repository.UserRepository;
import com.volunteersync.backend.repository.VolunteerProfileRepository;
import com.volunteersync.backend.repository.OrganizationProfileRepository;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

/**
 * Authentication service - handles login, registration, and token management
 */
@Slf4j
@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VolunteerProfileRepository volunteerProfileRepository;

    @Autowired
    private OrganizationProfileRepository organizationProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Value("${google.oauth.client-id}")
    private String googleClientId;

    /**
     * Register a new user
     */
    public AuthResponseDTO register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with email " + request.getEmail() + " already exists");
        }

        // Validate passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserType(UserType.valueOf(request.getUserType()));
        user.setEmailVerified(false);
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Save user first
        user = userRepository.save(user);

        // Create profile based on user type
        if (user.getUserType() == UserType.VOLUNTEER) {
            createVolunteerProfile(user, request);
        } else if (user.getUserType() == UserType.ORGANIZATION) {
            createOrganizationProfile(user, request);
        }

        // Generate JWT token
        String token = jwtService.generateToken(user);

        // Return response
        return AuthResponseDTO.builder()
                .token(token)
                .id(user.getId())
                .email(user.getEmail())
                .userType(user.getUserType().toString())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .organizationName(request.getOrganizationName())
                .profileComplete(false) // New users need to complete profile
                .build();
    }

    /**
     * Login user
     */
    public AuthResponseDTO login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        // Find user by email
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail().toLowerCase().trim());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = userOpt.get();

        // Check if user is active
        if (!user.getIsActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtService.generateToken(user);

        // Get additional user info based on type
        String firstName = null;
        String lastName = null;
        String organizationName = null;

        if (user.getUserType() == UserType.VOLUNTEER) {
            Optional<VolunteerProfile> profile = volunteerProfileRepository.findByUserId(user.getId());
            if (profile.isPresent()) {
                firstName = profile.get().getFirstName();
                lastName = profile.get().getLastName();
            }
        } else if (user.getUserType() == UserType.ORGANIZATION) {
            Optional<OrganizationProfile> profile = organizationProfileRepository.findByUserId(user.getId());
            if (profile.isPresent()) {
                organizationName = profile.get().getOrganizationName();
            }
        }

        // Return response
        return AuthResponseDTO.builder()
                .token(token)
                .id(user.getId())
                .email(user.getEmail())
                .userType(user.getUserType().toString())
                .firstName(firstName)
                .lastName(lastName)
                .organizationName(organizationName)
                .profileComplete(true) // Existing users have completed profiles
                .build();
    }

    /**
     * Refresh JWT token
     */
    public AuthResponseDTO refreshToken(String refreshToken) {
        try {
            // Validate refresh token
            String email = jwtService.getEmailFromToken(refreshToken);
            if (jwtService.isTokenExpired(refreshToken)) {
                throw new RuntimeException("Refresh token expired");
            }

            // Find user
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                throw new RuntimeException("User not found");
            }

            User user = userOpt.get();

            // Generate new token
            String newToken = jwtService.generateToken(user);

            return AuthResponseDTO.builder()
                    .token(newToken)
                    .id(user.getId())
                    .email(user.getEmail())
                    .userType(user.getUserType().toString())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token");
        }
    }

    /**
     * Logout user (blacklist token)
     */
    public void logout(String token) {
        jwtService.blacklistToken(token);
    }

    /**
     * Google OAuth authentication implementation
     * Verifies Google ID token and creates/logs in users
     */
    public AuthResponseDTO googleAuth(GoogleAuthRequest request) {
        try {
            log.info("Processing Google OAuth for user type: {}", request.getUserType());

            // Verify Google ID token and extract user info
            GoogleIdToken.Payload payload = verifyGoogleToken(request.getGoogleToken());

            if (payload == null) {
                throw new RuntimeException("Invalid Google token");
            }

            // Extract user information from Google payload
            String email = payload.getEmail();
            String firstName = (String) payload.get("given_name");
            String lastName = (String) payload.get("family_name");
            String googleId = payload.getSubject();
            Boolean emailVerified = payload.getEmailVerified();

            log.info("Google OAuth verified for email: {}", email);

            // Check if user already exists
            Optional<User> existingUserOpt = userRepository.findByEmail(email.toLowerCase().trim());

            if (existingUserOpt.isPresent()) {
                // User exists - perform login
                User existingUser = existingUserOpt.get();

                // Verify user type matches request
                if (!existingUser.getUserType().toString().equals(request.getUserType())) {
                    throw new RuntimeException("Account exists with different user type. Please use " +
                            existingUser.getUserType().toString().toLowerCase() + " login.");
                }

                // Check if user is active
                if (!existingUser.getIsActive()) {
                    throw new RuntimeException("Account is deactivated");
                }

                // Generate JWT token
                String token = jwtService.generateToken(existingUser);

                // Get additional user info
                String userFirstName = null;
                String userLastName = null;
                String organizationName = null;

                if (existingUser.getUserType() == UserType.VOLUNTEER) {
                    Optional<VolunteerProfile> profile = volunteerProfileRepository.findByUserId(existingUser.getId());
                    if (profile.isPresent()) {
                        userFirstName = profile.get().getFirstName();
                        userLastName = profile.get().getLastName();
                    }
                } else if (existingUser.getUserType() == UserType.ORGANIZATION) {
                    Optional<OrganizationProfile> profile = organizationProfileRepository
                            .findByUserId(existingUser.getId());
                    if (profile.isPresent()) {
                        organizationName = profile.get().getOrganizationName();
                    }
                }

                return AuthResponseDTO.builder()
                        .token(token)
                        .id(existingUser.getId())
                        .email(existingUser.getEmail())
                        .userType(existingUser.getUserType().toString())
                        .firstName(userFirstName)
                        .lastName(userLastName)
                        .organizationName(organizationName)
                        .profileComplete(true)
                        .build();

            } else {
                // User doesn't exist - create new account
                return createGoogleUser(email, firstName, lastName, googleId, emailVerified, request.getUserType());
            }

        } catch (Exception e) {
            log.error("Google OAuth authentication failed: {}", e.getMessage(), e);
            throw new RuntimeException("Google authentication failed: " + e.getMessage());
        }
    }

    /**
     * Verify Google ID token
     * @param idTokenString Google ID token
     * @return Google token payload
     */
    private GoogleIdToken.Payload verifyGoogleToken(String idTokenString) {
        try {
            // Create Google ID token verifier
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            // Verify token
            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                return idToken.getPayload();
            } else {
                log.error("Invalid Google ID token");
                return null;
            }

        } catch (GeneralSecurityException | IOException e) {
            log.error("Error verifying Google token: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Create new user from Google OAuth
     * @param email User email
     * @param firstName User first name
     * @param lastName User last name
     * @param googleId Google user ID
     * @param emailVerified Email verification status
     * @param userType User type (VOLUNTEER/ORGANIZATION)
     * @return Auth response
     */
    private AuthResponseDTO createGoogleUser(String email, String firstName, String lastName,
            String googleId, Boolean emailVerified, String userType) {

        log.info("Creating new Google user with email: {}", email);

        // Create new user
        User user = new User();
        user.setEmail(email.toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(generateRandomPassword())); // Random password for OAuth users
        user.setUserType(UserType.valueOf(userType));
        user.setEmailVerified(emailVerified != null ? emailVerified : false);
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Save user first
        user = userRepository.save(user);

        // Create profile based on user type
        String organizationName = null;

        if (user.getUserType() == UserType.VOLUNTEER) {
            createGoogleVolunteerProfile(user, firstName, lastName);
        } else if (user.getUserType() == UserType.ORGANIZATION) {
            // For organizations, use the first name as organization name if last name is empty
            organizationName = !isEmpty(lastName) ? firstName + " " + lastName : firstName;
            createGoogleOrganizationProfile(user, organizationName);
        }

        // Generate JWT token
        String token = jwtService.generateToken(user);

        return AuthResponseDTO.builder()
                .token(token)
                .id(user.getId())
                .email(user.getEmail())
                .userType(user.getUserType().toString())
                .firstName(firstName)
                .lastName(lastName)
                .organizationName(organizationName)
                .profileComplete(false) // New users need to complete their profile
                .build();
    }

    /**
     * Create volunteer profile for Google OAuth user
     */
    private void createGoogleVolunteerProfile(User user, String firstName, String lastName) {
        VolunteerProfile profile = new VolunteerProfile();
        profile.setUser(user);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setIsAvailable(true);
        profile.setEventsParticipated(0);
        profile.setTotalVolunteerHours(0);
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());

        volunteerProfileRepository.save(profile);
    }

    /**
     * Create organization profile for Google OAuth user
     */
    private void createGoogleOrganizationProfile(User user, String organizationName) {
        OrganizationProfile profile = new OrganizationProfile();
        profile.setUser(user);
        profile.setOrganizationName(organizationName);
        profile.setIsVerified(false);
        profile.setTotalEventsHosted(0);
        profile.setnumberOfVolunteers(0);
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());

        organizationProfileRepository.save(profile);
    }

    /**
     * Generate random password for OAuth users
     * @return Random password
     */
    private String generateRandomPassword() {
        return java.util.UUID.randomUUID().toString();
    }

    /**
     * Check if string is empty or null
     */
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    private void createVolunteerProfile(User user, RegisterRequest request) {
        VolunteerProfile profile = new VolunteerProfile();
        profile.setUser(user);
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setIsAvailable(true);
        profile.setEventsParticipated(0);
        profile.setTotalVolunteerHours(0);
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());

        volunteerProfileRepository.save(profile);
    }

    private void createOrganizationProfile(User user, RegisterRequest request) {
        OrganizationProfile profile = new OrganizationProfile();
        profile.setUser(user);
        profile.setOrganizationName(request.getOrganizationName());
        profile.setIsVerified(false);
        profile.setTotalEventsHosted(0);
        profile.setnumberOfVolunteers(0);
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());

        organizationProfileRepository.save(profile);
    }

    // ==========================================
    // REQUEST/RESPONSE CLASSES
    // ==========================================

    public static class RegisterRequest {
        private String email;
        private String password;
        private String confirmPassword;
        private String userType;
        private String firstName;
        private String lastName;
        private String organizationName;

        // Getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getConfirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getOrganizationName() { return organizationName; }
        public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
    }

    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class GoogleAuthRequest {
        private String googleToken;
        private String userType;

        public String getGoogleToken() { return googleToken; }
        public void setGoogleToken(String googleToken) { this.googleToken = googleToken; }
        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
    }

    public static class AuthResponseDTO {
        private String token;
        private Long id;
        private String email;
        private String userType;
        private String firstName;
        private String lastName;
        private String organizationName;
        private boolean profileComplete;

        // Builder pattern
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private AuthResponseDTO response = new AuthResponseDTO();

            public Builder token(String token) { response.token = token; return this; }
            public Builder id(Long id) { response.id = id; return this; }
            public Builder email(String email) { response.email = email; return this; }
            public Builder userType(String userType) { response.userType = userType; return this; }
            public Builder firstName(String firstName) { response.firstName = firstName; return this; }
            public Builder lastName(String lastName) { response.lastName = lastName; return this; }
            public Builder organizationName(String organizationName) { response.organizationName = organizationName; return this; }
            public Builder profileComplete(boolean profileComplete) { response.profileComplete = profileComplete; return this; }

            public AuthResponseDTO build() { return response; }
        }

        // Getters and setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getOrganizationName() { return organizationName; }
        public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
        public boolean isProfileComplete() { return profileComplete; }
        public void setProfileComplete(boolean profileComplete) { this.profileComplete = profileComplete; }
    }
}