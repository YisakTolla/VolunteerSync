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
        
        // Hash password
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        
        // Create user
        User user = new User(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            hashedPassword,
            UserType.valueOf(request.getUserType())
        );
        
        return userRepository.save(user);
    }
    
    public User createGoogleUser(GoogleTokenInfo tokenInfo, String userType) {
        User user = new User();
        user.setFirstName(tokenInfo.getGivenName());
        user.setLastName(tokenInfo.getFamilyName());
        user.setEmail(tokenInfo.getEmail());
        user.setGoogleId(tokenInfo.getSub());
        user.setProfilePicture(tokenInfo.getPicture());
        user.setUserType(UserType.valueOf(userType));
        // No password for OAuth users
        
        return userRepository.save(user);
    }
    
    public GoogleTokenInfo verifyGoogleToken(String token) {
        try {
            return googleTokenVerifier.verify(token);
        } catch (Exception e) {
            return null;
        }
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    // Additional method for login functionality
    public User authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        
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
        return userRepository.save(user);
    }
    
    // Helper method to find user by ID
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    // Helper method to check if user exists by Google ID
    public User findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId);
    }
}