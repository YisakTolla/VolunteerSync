// backend/src/main/java/com/volunteersync/backend/service/AuthService.java
package com.volunteersync.backend.service;

import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.entity.VolunteerProfile;
import com.volunteersync.backend.entity.OrganizationProfile;
import com.volunteersync.backend.enums.UserType;
import com.volunteersync.backend.repository.UserRepository;
import com.volunteersync.backend.repository.VolunteerProfileRepository;
import com.volunteersync.backend.repository.OrganizationProfileRepository;
import com.volunteersync.backend.dto.UserDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

/**
 * Simple authentication service - handles login and registration
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
        user.setIsActive(true);
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);

        // Create profile based on user type
        if (savedUser.getUserType() == UserType.VOLUNTEER) {
            createVolunteerProfile(savedUser, request);
        } else if (savedUser.getUserType() == UserType.ORGANIZATION) {
            createOrganizationProfile(savedUser, request);
        }

        // Generate JWT token
        String token = jwtService.generateToken(savedUser);

        // Create response
        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        response.setType("Bearer");
        response.setId(savedUser.getId());
        response.setEmail(savedUser.getEmail());
        response.setUserType(savedUser.getUserType().toString());

        if (savedUser.getUserType() == UserType.VOLUNTEER) {
            response.setFirstName(request.getFirstName());
            response.setLastName(request.getLastName());
        } else {
            response.setOrganizationName(request.getOrganizationName());
        }

        log.info("Successfully registered user with ID: {}", savedUser.getId());
        return response;
    }

    /**
     * Login user
     */
    public AuthResponseDTO login(LoginRequest request) {
        log.info("Authenticating user with email: {}", request.getEmail());

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

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Update last login (you'll need to add this field to User entity if needed)
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Generate JWT token
        String token = jwtService.generateToken(user);

        // Create response
        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        response.setType("Bearer");
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setUserType(user.getUserType().toString());

        // Add profile-specific data
        if (user.getUserType() == UserType.VOLUNTEER) {
            Optional<VolunteerProfile> profile = volunteerProfileRepository.findByUser(user);
            if (profile.isPresent()) {
                response.setFirstName(profile.get().getFirstName());
                response.setLastName(profile.get().getLastName());
            }
        } else if (user.getUserType() == UserType.ORGANIZATION) {
            Optional<OrganizationProfile> profile = organizationProfileRepository.findByUser(user);
            if (profile.isPresent()) {
                response.setOrganizationName(profile.get().getOrganizationName());
            }
        }

        log.info("Successfully authenticated user: {}", user.getEmail());
        return response;
    }

    /**
     * Get current user information
     */
    public UserDTO getCurrentUser(Long userId) {
        log.info("Fetching current user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDTO response = new UserDTO();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setUserType(user.getUserType());
        response.setIsActive(user.getIsActive());
        response.setEmailVerified(user.getEmailVerified());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return response;
    }

    // ==========================================
    // PRIVATE HELPER METHODS
    // ==========================================

    private void createVolunteerProfile(User user, RegisterRequest request) {
        VolunteerProfile profile = new VolunteerProfile();
        profile.setUser(user);
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        // Other fields will be set to defaults by the entity

        volunteerProfileRepository.save(profile);
        log.info("Created volunteer profile for user: {}", user.getEmail());
    }

    private void createOrganizationProfile(User user, RegisterRequest request) {
        OrganizationProfile profile = new OrganizationProfile();
        profile.setUser(user);
        profile.setOrganizationName(request.getOrganizationName());
        // Other fields will be set to defaults by the entity

        organizationProfileRepository.save(profile);
        log.info("Created organization profile for user: {}", user.getEmail());
    }

    // ==========================================
    // REQUEST/RESPONSE CLASSES
    // ==========================================

    public static class LoginRequest {
        private String email;
        private String password;

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class RegisterRequest {
        private String email;
        private String password;
        private String confirmPassword;
        private String userType;
        private String firstName;
        private String lastName;
        private String organizationName;

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getOrganizationName() {
            return organizationName;
        }

        public void setOrganizationName(String organizationName) {
            this.organizationName = organizationName;
        }
    }

    public static class AuthResponseDTO {
        private String token;
        private String type;
        private Long id;
        private String email;
        private String userType;
        private String firstName;
        private String lastName;
        private String organizationName;

        // Getters and setters
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getOrganizationName() {
            return organizationName;
        }

        public void setOrganizationName(String organizationName) {
            this.organizationName = organizationName;
        }
    }
}