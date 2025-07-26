package com.volunteersync.backend.service;

import com.volunteersync.backend.dto.RegisterRequest;
import com.volunteersync.backend.dto.GoogleTokenInfo;
import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.entity.UserType;
import com.volunteersync.backend.repository.UserRepository;
import com.volunteersync.backend.util.GoogleTokenVerifier;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GoogleTokenVerifier googleTokenVerifier;

    public User createGoogleUser(GoogleTokenInfo tokenInfo, UserType userType) {
        if (tokenInfo == null) {
            throw new RuntimeException("Google token info cannot be null");
        }

        if (userType == null) {
            throw new RuntimeException("User type cannot be null");
        }

        User user = new User();
        user.setEmail(tokenInfo.getEmail().trim().toLowerCase());
        user.setGoogleId(tokenInfo.getSub());
        user.setProfilePicture(tokenInfo.getPicture());
        user.setUserType(userType);
        // No password for OAuth users

        if (userType == UserType.VOLUNTEER) {
            // For volunteers, use Google profile names
            String firstName = tokenInfo.getGivenName();
            String lastName = tokenInfo.getFamilyName();

            if (firstName == null || firstName.trim().isEmpty()) {
                firstName = tokenInfo.getName(); // Fallback to full name
            }
            if (lastName == null || lastName.trim().isEmpty()) {
                lastName = ""; // Set empty if not available
            }

            user.setFirstName(firstName.trim());
            user.setLastName(lastName.trim());

        } else if (userType == UserType.ORGANIZATION) {
            // For organizations, we need to handle this differently
            // Option 1: Use the full name as organization name
            String orgName = tokenInfo.getName();
            if (orgName == null || orgName.trim().isEmpty()) {
                // Fallback to email prefix if name not available
                orgName = tokenInfo.getEmail().split("@")[0];
            }

            user.setOrganizationName(orgName.trim());

            // Note: You might want to prompt for actual organization name
            // in a separate step after Google auth for better UX
        }

        return userRepository.save(user);
    }

    // Overloaded method for backward compatibility
    public User createGoogleUser(GoogleTokenInfo tokenInfo, String userTypeString) {
        UserType userType;
        try {
            userType = UserType.valueOf(userTypeString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid user type: " + userTypeString);
        }

        return createGoogleUser(tokenInfo, userType);
    }

    public GoogleTokenInfo verifyGoogleToken(String token) {
        try {
            return googleTokenVerifier.verify(token);
        } catch (Exception e) {
            return null;
        }
    }

    // Helper method to update user profile
    public User updateUser(User user) {
        if (user == null) {
            throw new RuntimeException("User cannot be null");
        }

        // Validate user has required fields for their type
        if (!user.hasValidFields()) {
            throw new RuntimeException("User does not have valid fields for type: " + user.getUserType());
        }

        return userRepository.save(user);
    }

    // Helper method to find user by ID
    public User findById(Long id) {
        if (id == null) {
            return null;
        }
        return userRepository.findById(id).orElse(null);
    }

    // Helper method to check if user exists by Google ID
    public User findByGoogleId(String googleId) {
        if (googleId == null || googleId.trim().isEmpty()) {
            return null;
        }
        return userRepository.findByGoogleId(googleId);
    }

    // Helper method to update organization name (for organizations only)
    public User updateOrganizationName(Long userId, String newOrganizationName) {
        User user = findById(userId);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (!user.isOrganization()) {
            throw new RuntimeException("User is not an organization");
        }

        if (newOrganizationName == null || newOrganizationName.trim().isEmpty()) {
            throw new RuntimeException("Organization name cannot be empty");
        }

        user.setOrganizationName(newOrganizationName.trim());
        return userRepository.save(user);
    }

    // Helper method to update volunteer name (for volunteers only)
    public User updateVolunteerName(Long userId, String firstName, String lastName) {
        User user = findById(userId);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (!user.isVolunteer()) {
            throw new RuntimeException("User is not a volunteer");
        }

        if (firstName == null || firstName.trim().isEmpty() ||
                lastName == null || lastName.trim().isEmpty()) {
            throw new RuntimeException("First name and last name cannot be empty");
        }

        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        return userRepository.save(user);
    }

    // Helper method to get user display name
    public String getUserDisplayName(Long userId) {
        User user = findById(userId);
        return user != null ? user.getDisplayName() : null;
    }

    // Helper method to check if user can be updated
    public boolean canUpdateUser(User user) {
        return user != null && user.hasValidFields();
    }

    // Add this method to your UserService.java class to ensure consistent email
    // handling

    public User createUser(RegisterRequest request) {
        // Validate passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Parse user type
        UserType userType;
        try {
            userType = UserType.valueOf(request.getUserType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid user type: " + request.getUserType());
        }

        // NORMALIZE EMAIL TO LOWERCASE - This is crucial!
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        // Check if user already exists with normalized email
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new RuntimeException("Email already registered: " + normalizedEmail);
        }

        // Hash password
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create user based on type
        User user;

        if (userType == UserType.VOLUNTEER) {
            // Validate volunteer fields
            if (request.getFirstName() == null || request.getFirstName().trim().isEmpty() ||
                    request.getLastName() == null || request.getLastName().trim().isEmpty()) {
                throw new RuntimeException("First name and last name are required for volunteers");
            }

            user = new User(
                    request.getFirstName().trim(),
                    request.getLastName().trim(),
                    normalizedEmail, // Use normalized email
                    hashedPassword,
                    userType);
        } else if (userType == UserType.ORGANIZATION) {
            // Validate organization fields
            if (request.getOrganizationName() == null || request.getOrganizationName().trim().isEmpty()) {
                throw new RuntimeException("Organization name is required for organizations");
            }

            user = new User(
                    request.getOrganizationName().trim(),
                    normalizedEmail, // Use normalized email
                    hashedPassword,
                    userType);
        } else {
            throw new RuntimeException("Unsupported user type: " + userType);
        }

        // Save user to database
        User savedUser = userRepository.save(user);

        // Add debug logging
        System.out.println("=== USER CREATION DEBUG ===");
        System.out.println("Original email: " + request.getEmail());
        System.out.println("Normalized email: " + normalizedEmail);
        System.out.println("Saved user ID: " + savedUser.getId());
        System.out.println("Saved user email: " + savedUser.getEmail());
        System.out.println("User type: " + savedUser.getUserType());
        System.out.println("===========================");

        return savedUser;
    }

    // Also update the authenticateUser method to add debug logging
    public User authenticateUser(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null) {
            System.out.println("=== LOGIN DEBUG ===");
            System.out.println("Invalid input - email or password is null/empty");
            return null;
        }

        String normalizedEmail = email.trim().toLowerCase();

        System.out.println("=== LOGIN DEBUG ===");
        System.out.println("Original email: " + email);
        System.out.println("Normalized email: " + normalizedEmail);

        User user = userRepository.findByEmail(normalizedEmail);

        if (user == null) {
            System.out.println("No user found with email: " + normalizedEmail);
            return null;
        }

        System.out.println("Found user: ID=" + user.getId() + ", Email=" + user.getEmail());

        if (user.getPassword() == null) {
            System.out.println("User has no password (OAuth user?)");
            return null;
        }

        // Check if password matches (for non-OAuth users)
        boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
        System.out.println("Password matches: " + passwordMatches);

        if (passwordMatches) {
            System.out.println("Authentication successful for user: " + user.getEmail());
            return user;
        } else {
            System.out.println("Authentication failed - incorrect password");
            return null;
        }
    }

    // Update findByEmail method to ensure consistency
    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        String normalizedEmail = email.trim().toLowerCase();
        return userRepository.findByEmail(normalizedEmail);
    }

    // Update existsByEmail method to ensure consistency
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String normalizedEmail = email.trim().toLowerCase();
        return userRepository.existsByEmail(normalizedEmail);
    }
    
    /**
     * Update user profile with additional information from profile completion
     */
    public User updateUserProfile(User user, Map<String, Object> profileData) {
        if (user == null) {
            throw new RuntimeException("User cannot be null");
        }
        
        if (profileData == null || profileData.isEmpty()) {
            throw new RuntimeException("Profile data cannot be null or empty");
        }
        
        System.out.println("=== PROFILE UPDATE DEBUG ===");
        System.out.println("Updating profile for user ID: " + user.getId());
        System.out.println("Profile data received: " + profileData);
        
        try {
            // Update bio if provided
            if (profileData.containsKey("bio") && profileData.get("bio") != null) {
                String bio = profileData.get("bio").toString().trim();
                user.setBio(bio);
                System.out.println("Updated bio: " + bio);
            }
            
            // Update location if provided
            if (profileData.containsKey("location") && profileData.get("location") != null) {
                String location = profileData.get("location").toString().trim();
                user.setLocation(location);
                System.out.println("Updated location: " + location);
            }
            
            // Update phone if provided
            if (profileData.containsKey("phone") && profileData.get("phone") != null) {
                String phone = profileData.get("phone").toString().trim();
                user.setPhone(phone);
                System.out.println("Updated phone: " + phone);
            }
            
            // Update profile picture if provided
            if (profileData.containsKey("profilePicture") && profileData.get("profilePicture") != null) {
                String profilePicture = profileData.get("profilePicture").toString().trim();
                user.setProfilePicture(profilePicture);
                System.out.println("Updated profile picture: " + profilePicture);
            }
            
            // Update interests if provided (assuming it's stored as a comma-separated string)
            if (profileData.containsKey("interests") && profileData.get("interests") != null) {
                String interests = profileData.get("interests").toString();
                user.setInterests(interests);
                System.out.println("Updated interests: " + interests);
            }
            
            // Update skills if provided (assuming it's stored as a comma-separated string)
            if (profileData.containsKey("skills") && profileData.get("skills") != null) {
                String skills = profileData.get("skills").toString();
                user.setSkills(skills);
                System.out.println("Updated skills: " + skills);
            }
            
            // Update availability if provided
            if (profileData.containsKey("availability") && profileData.get("availability") != null) {
                String availability = profileData.get("availability").toString();
                user.setAvailability(availability);
                System.out.println("Updated availability: " + availability);
            }
            
            // Save the updated user
            User updatedUser = userRepository.save(user);
            
            System.out.println("Profile update successful for user ID: " + updatedUser.getId());
            System.out.println("============================");
            
            return updatedUser;
            
        } catch (Exception e) {
            System.out.println("ERROR updating profile: " + e.getMessage());
            throw new RuntimeException("Failed to update user profile: " + e.getMessage());
        }
    }
}