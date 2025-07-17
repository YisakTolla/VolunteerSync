package com.volunteersync.backend.service;

import com.volunteersync.backend.dto.RegisterRequest;
import com.volunteersync.backend.dto.GoogleTokenInfo;
import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.entity.UserType;
import com.volunteersync.backend.repository.UserRepository;
import com.volunteersync.backend.util.GoogleTokenVerifier;
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
            
            // Create volunteer user
            user = new User(
                request.getFirstName().trim(),
                request.getLastName().trim(),
                request.getEmail().trim().toLowerCase(),
                hashedPassword,
                userType
            );
            
        } else if (userType == UserType.ORGANIZATION) {
            // Validate organization fields
            if (request.getOrganizationName() == null || request.getOrganizationName().trim().isEmpty()) {
                throw new RuntimeException("Organization name is required for organizations");
            }
            
            // Create organization user
            user = new User(
                request.getOrganizationName().trim(),
                request.getEmail().trim().toLowerCase(),
                hashedPassword,
                userType
            );
            
        } else {
            throw new RuntimeException("Unsupported user type: " + userType);
        }
        
        return userRepository.save(user);
    }
    
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
    
    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return userRepository.findByEmail(email.trim().toLowerCase());
    }
    
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByEmail(email.trim().toLowerCase());
    }
    
    // Additional method for login functionality
    public User authenticateUser(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null) {
            return null;
        }
        
        User user = userRepository.findByEmail(email.trim().toLowerCase());
        
        if (user != null && user.getPassword() != null) {
            // Check if password matches (for non-OAuth users)
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        
        return null; // Authentication failed
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
}