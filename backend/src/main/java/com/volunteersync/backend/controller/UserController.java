// backend/src/main/java/com/volunteersync/backend/controller/UserController.java
package com.volunteersync.backend.controller;

import com.volunteersync.backend.service.UserService;
import com.volunteersync.backend.dto.UserDTO;
import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.enums.UserType;
import com.volunteersync.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * User Controller - handles user management endpoints
 * Manages user accounts, profile information, and administrative operations
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // ==========================================
    // USER PROFILE OPERATIONS
    // ==========================================

    /**
     * Get current user's information
     * GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            Optional<User> userOpt = userService.findById(userId);
            
            if (userOpt.isPresent()) {
                UserDTO userDTO = userService.convertToDTO(userOpt.get());
                return ResponseEntity.ok(userDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get user by ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userService.findById(id);
            
            if (userOpt.isPresent()) {
                UserDTO userDTO = userService.convertToDTO(userOpt.get());
                return ResponseEntity.ok(userDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Update current user's email
     * PUT /api/users/me/email
     */
    @PutMapping("/me/email")
    public ResponseEntity<?> updateEmail(@RequestBody UpdateEmailRequest request,
                                       Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            
            // Check if new email already exists
            if (userService.emailExists(request.getNewEmail())) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Email already exists"));
            }
            
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setEmail(request.getNewEmail());
                user.setEmailVerified(false); // Reset verification when email changes
                userRepository.save(user);
                
                UserDTO userDTO = userService.convertToDTO(user);
                return ResponseEntity.ok(userDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Update user password
     * PUT /api/users/me/password
     */
    @PutMapping("/me/password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePasswordRequest request,
                                          Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            userService.updatePassword(userId, request.getNewPassword());
            return ResponseEntity.ok(new SuccessResponse("Password updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Verify user email
     * POST /api/users/me/verify-email
     */
    @PostMapping("/me/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody VerifyEmailRequest request,
                                       Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            // TODO: Implement email verification token validation
            userService.verifyEmail(userId);
            return ResponseEntity.ok(new SuccessResponse("Email verified successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Deactivate current user account
     * PUT /api/users/me/deactivate
     */
    @PutMapping("/me/deactivate")
    public ResponseEntity<?> deactivateAccount(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            userService.deactivateUser(userId);
            return ResponseEntity.ok(new SuccessResponse("Account deactivated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // USER SEARCH AND DISCOVERY
    // ==========================================

    /**
     * Search users by email pattern (admin only)
     * GET /api/users/search?email={pattern}
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam(required = false) String email,
                                       @RequestParam(required = false) String userType) {
        try {
            // TODO: Add admin authorization check
            List<User> users;
            
            if (email != null && !email.trim().isEmpty()) {
                users = userRepository.findByEmailContainingIgnoreCase(email.trim());
            } else if (userType != null && !userType.trim().isEmpty()) {
                UserType type = UserType.valueOf(userType.toUpperCase());
                users = userRepository.findByUserType(type);
            } else {
                users = userRepository.findAll();
            }
            
            List<UserDTO> userDTOs = users.stream()
                    .map(userService::convertToDTO)
                    .toList();
            
            return ResponseEntity.ok(userDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get users by type
     * GET /api/users/type/{userType}
     */
    @GetMapping("/type/{userType}")
    public ResponseEntity<?> getUsersByType(@PathVariable String userType) {
        try {
            UserType type = UserType.valueOf(userType.toUpperCase());
            List<User> users = userRepository.findByUserTypeAndIsActiveTrue(type);
            
            List<UserDTO> userDTOs = users.stream()
                    .map(userService::convertToDTO)
                    .toList();
            
            return ResponseEntity.ok(userDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get recently registered users
     * GET /api/users/recent?days={days}
     */
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentUsers(@RequestParam(defaultValue = "7") int days) {
        try {
            LocalDateTime since = LocalDateTime.now().minusDays(days);
            List<User> users = userRepository.findRecentlyRegistered(since);
            
            List<UserDTO> userDTOs = users.stream()
                    .map(userService::convertToDTO)
                    .toList();
            
            return ResponseEntity.ok(userDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // USER STATISTICS
    // ==========================================

    /**
     * Get user statistics
     * GET /api/users/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getUserStatistics() {
        try {
            UserStatsResponse stats = new UserStatsResponse();
            
            // Basic counts
            stats.setTotalUsers(userRepository.count());
            stats.setActiveUsers(userRepository.countByIsActiveTrue());
            stats.setVerifiedUsers(userRepository.countByEmailVerifiedTrue());
            
            // User type counts
            stats.setVolunteers(userRepository.countByUserType(UserType.VOLUNTEER));
            stats.setOrganizations(userRepository.countByUserType(UserType.ORGANIZATION));
            
            // Recent registrations
            LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
            LocalDateTime monthAgo = LocalDateTime.now().minusDays(30);
            
            stats.setNewUsersThisWeek(userRepository.countRegistrationsBetween(weekAgo, LocalDateTime.now()));
            stats.setNewUsersThisMonth(userRepository.countRegistrationsBetween(monthAgo, LocalDateTime.now()));
            
            // Growth data
            LocalDateTime threeMonthsAgo = LocalDateTime.now().minusDays(90);
            stats.setGrowthData(userRepository.getUserGrowthStats(threeMonthsAgo));
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get user activity statistics
     * GET /api/users/stats/activity
     */
    @GetMapping("/stats/activity")
    public ResponseEntity<?> getUserActivityStats() {
        try {
            UserActivityStatsResponse stats = new UserActivityStatsResponse();
            
            // Email verification status
            long totalUsers = userRepository.count();
            long verifiedUsers = userRepository.countByEmailVerifiedTrue();
            stats.setEmailVerificationRate(totalUsers > 0 ? (double) verifiedUsers / totalUsers * 100 : 0);
            
            // Active vs inactive users
            long activeUsers = userRepository.countByIsActiveTrue();
            stats.setActiveUserRate(totalUsers > 0 ? (double) activeUsers / totalUsers * 100 : 0);
            
            // Users needing verification
            List<User> unverifiedUsers = userRepository.findByEmailVerifiedFalseAndIsActiveTrue();
            stats.setUsersNeedingVerification(unverifiedUsers.size());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // ADMIN OPERATIONS
    // ==========================================

    /**
     * Get all users (admin only)
     * GET /api/users/admin/all
     */
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "50") int size) {
        try {
            // TODO: Add admin authorization check
            List<UserDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Activate user account (admin only)
     * PUT /api/users/{id}/activate
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long id) {
        try {
            // TODO: Add admin authorization check
            userService.reactivateUser(id);
            return ResponseEntity.ok(new SuccessResponse("User activated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Deactivate user account (admin only)
     * PUT /api/users/{id}/deactivate
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        try {
            // TODO: Add admin authorization check
            userService.deactivateUser(id);
            return ResponseEntity.ok(new SuccessResponse("User deactivated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Verify user email (admin only)
     * PUT /api/users/{id}/verify
     */
    @PutMapping("/{id}/verify")
    public ResponseEntity<?> verifyUserEmail(@PathVariable Long id) {
        try {
            // TODO: Add admin authorization check
            userService.verifyEmail(id);
            return ResponseEntity.ok(new SuccessResponse("User email verified successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get users needing review (admin only)
     * GET /api/users/admin/review
     */
    @GetMapping("/admin/review")
    public ResponseEntity<?> getUsersNeedingReview() {
        try {
            // TODO: Add admin authorization check
            List<User> unverifiedUsers = userRepository.findByEmailVerifiedFalseAndIsActiveTrue();
            
            List<UserDTO> userDTOs = unverifiedUsers.stream()
                    .map(userService::convertToDTO)
                    .toList();
            
            return ResponseEntity.ok(userDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    /**
     * Extract user ID from authentication context
     */
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        Object principal = authentication.getPrincipal();
        
        // Handle UserPrincipal if implemented
        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getId();
        }
        
        // Handle UserDetails
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            try {
                return Long.parseLong(userDetails.getUsername());
            } catch (NumberFormatException e) {
                throw new RuntimeException("Unable to determine user ID from authentication");
            }
        }
        
        // Fallback - extract from name if it's the user ID
        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid user authentication");
        }
    }

    // ==========================================
    // REQUEST CLASSES
    // ==========================================

    /**
     * Update email request
     */
    public static class UpdateEmailRequest {
        private String newEmail;

        public String getNewEmail() {
            return newEmail;
        }

        public void setNewEmail(String newEmail) {
            this.newEmail = newEmail;
        }
    }

    /**
     * Update password request
     */
    public static class UpdatePasswordRequest {
        private String currentPassword;
        private String newPassword;
        private String confirmPassword;

        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }

    /**
     * Verify email request
     */
    public static class VerifyEmailRequest {
        private String verificationToken;

        public String getVerificationToken() {
            return verificationToken;
        }

        public void setVerificationToken(String verificationToken) {
            this.verificationToken = verificationToken;
        }
    }

    // ==========================================
    // RESPONSE CLASSES
    // ==========================================

    /**
     * Generic error response
     */
    public static class ErrorResponse {
        private String error;
        private long timestamp;

        public ErrorResponse(String error) {
            this.error = error;
            this.timestamp = System.currentTimeMillis();
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    /**
     * Success response
     */
    public static class SuccessResponse {
        private String message;
        private long timestamp;

        public SuccessResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    /**
     * User statistics response
     */
    public static class UserStatsResponse {
        private long totalUsers;
        private long activeUsers;
        private long verifiedUsers;
        private long volunteers;
        private long organizations;
        private long newUsersThisWeek;
        private long newUsersThisMonth;
        private List<Object[]> growthData;

        // Getters and Setters
        public long getTotalUsers() {
            return totalUsers;
        }

        public void setTotalUsers(long totalUsers) {
            this.totalUsers = totalUsers;
        }

        public long getActiveUsers() {
            return activeUsers;
        }

        public void setActiveUsers(long activeUsers) {
            this.activeUsers = activeUsers;
        }

        public long getVerifiedUsers() {
            return verifiedUsers;
        }

        public void setVerifiedUsers(long verifiedUsers) {
            this.verifiedUsers = verifiedUsers;
        }

        public long getVolunteers() {
            return volunteers;
        }

        public void setVolunteers(long volunteers) {
            this.volunteers = volunteers;
        }

        public long getOrganizations() {
            return organizations;
        }

        public void setOrganizations(long organizations) {
            this.organizations = organizations;
        }

        public long getNewUsersThisWeek() {
            return newUsersThisWeek;
        }

        public void setNewUsersThisWeek(long newUsersThisWeek) {
            this.newUsersThisWeek = newUsersThisWeek;
        }

        public long getNewUsersThisMonth() {
            return newUsersThisMonth;
        }

        public void setNewUsersThisMonth(long newUsersThisMonth) {
            this.newUsersThisMonth = newUsersThisMonth;
        }

        public List<Object[]> getGrowthData() {
            return growthData;
        }

        public void setGrowthData(List<Object[]> growthData) {
            this.growthData = growthData;
        }
    }

    /**
     * User activity statistics response
     */
    public static class UserActivityStatsResponse {
        private double emailVerificationRate;
        private double activeUserRate;
        private long usersNeedingVerification;

        public double getEmailVerificationRate() {
            return emailVerificationRate;
        }

        public void setEmailVerificationRate(double emailVerificationRate) {
            this.emailVerificationRate = emailVerificationRate;
        }

        public double getActiveUserRate() {
            return activeUserRate;
        }

        public void setActiveUserRate(double activeUserRate) {
            this.activeUserRate = activeUserRate;
        }

        public long getUsersNeedingVerification() {
            return usersNeedingVerification;
        }

        public void setUsersNeedingVerification(long usersNeedingVerification) {
            this.usersNeedingVerification = usersNeedingVerification;
        }
    }

    /**
     * Placeholder for UserPrincipal - should be implemented based on your security setup
     */
    public interface UserPrincipal {
        Long getId();
        String getUsername();
        String getUserType();
    }
}