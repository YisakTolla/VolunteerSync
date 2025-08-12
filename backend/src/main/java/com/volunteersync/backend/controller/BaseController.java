package com.volunteersync.backend.controller;

import com.volunteersync.backend.entity.User;
import org.springframework.security.core.Authentication;

/**
 * Base controller with authentication helper methods
 */
public abstract class BaseController {

    /**
     * 🔧 FIXED: Extract user ID from authentication that works with our JWT filter
     * @param authentication Spring Security authentication object
     * @return User ID
     * @throws RuntimeException if user is not authenticated
     */
    protected Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        Object principal = authentication.getPrincipal();
        
        // 🔧 FIXED: Check if principal is our User entity (set by JwtAuthenticationFilter)
        if (principal instanceof User) {
            User user = (User) principal;
            return user.getId();
        }
        
        // Fallback for other authentication types
        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid user authentication - unable to extract user ID");
        }
    }

    /**
     * Get current user entity from authentication
     * @param authentication Spring Security authentication object
     * @return User entity
     * @throws RuntimeException if user is not authenticated
     */
    protected User getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof User) {
            return (User) principal;
        }
        
        throw new RuntimeException("Invalid user authentication - user not found in context");
    }

    /**
     * Check if current user has specific role
     * @param authentication Spring Security authentication object
     * @param role Role to check (without ROLE_ prefix)
     * @return true if user has the role
     */
    protected boolean hasRole(Authentication authentication, String role) {
        if (authentication == null) {
            return false;
        }
        
        String roleWithPrefix = "ROLE_" + role;
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(roleWithPrefix));
    }

    /**
     * Check if current user is admin
     * @param authentication Spring Security authentication object
     * @return true if user is admin
     */
    protected boolean isAdmin(Authentication authentication) {
        return hasRole(authentication, "ADMIN");
    }

    /**
     * Check if current user is volunteer
     * @param authentication Spring Security authentication object
     * @return true if user is volunteer
     */
    protected boolean isVolunteer(Authentication authentication) {
        return hasRole(authentication, "VOLUNTEER");
    }

    /**
     * Check if current user is organization
     * @param authentication Spring Security authentication object
     * @return true if user is organization
     */
    protected boolean isOrganization(Authentication authentication) {
        return hasRole(authentication, "ORGANIZATION");
    }

    /**
     * Common error response class
     */
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

    /**
     * Common success response class
     */
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