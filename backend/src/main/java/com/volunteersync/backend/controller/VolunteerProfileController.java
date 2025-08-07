// backend/src/main/java/com/volunteersync/backend/controller/VolunteerProfileController.java
package com.volunteersync.backend.controller;

import com.volunteersync.backend.service.VolunteerProfileService;
import com.volunteersync.backend.dto.VolunteerProfileDTO;
import com.volunteersync.backend.service.VolunteerProfileService.CreateVolunteerProfileRequest;
import com.volunteersync.backend.service.VolunteerProfileService.UpdateVolunteerProfileRequest;
import com.volunteersync.backend.service.VolunteerProfileService.VolunteerSearchRequest;
import com.volunteersync.backend.service.VolunteerProfileService.VolunteerStatsResponse;
import com.volunteersync.backend.service.VolunteerProfileService.ProfileCompletionStats;
import com.volunteersync.backend.service.VolunteerProfileService.IndividualVolunteerStats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Volunteer Profile Controller - handles volunteer profile endpoints
 * Manages volunteer profiles, search, and statistics
 */
@RestController
@RequestMapping("/api/volunteer-profiles")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VolunteerProfileController {

    @Autowired
    private VolunteerProfileService volunteerProfileService;

    // ==========================================
    // PROFILE MANAGEMENT
    // ==========================================

    /**
     * Create volunteer profile
     * POST /api/volunteer-profiles
     */
    @PostMapping
    public ResponseEntity<?> createProfile(@Valid @RequestBody CreateVolunteerProfileRequest request,
                                         Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            VolunteerProfileDTO profile = volunteerProfileService.createProfile(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get current user's volunteer profile
     * GET /api/volunteer-profiles/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            VolunteerProfileDTO profile = volunteerProfileService.getProfileByUserId(userId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get volunteer profile by ID
     * GET /api/volunteer-profiles/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProfileById(@PathVariable Long id) {
        try {
            VolunteerProfileDTO profile = volunteerProfileService.getProfileById(id);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Update volunteer profile
     * PUT /api/volunteer-profiles/me
     */
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(@Valid @RequestBody UpdateVolunteerProfileRequest request,
                                           Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            VolunteerProfileDTO profile = volunteerProfileService.updateProfile(userId, request);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Delete volunteer profile
     * DELETE /api/volunteer-profiles/me
     */
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteMyProfile(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            volunteerProfileService.deleteProfile(userId);
            return ResponseEntity.ok(new SuccessResponse("Profile deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // SEARCH AND DISCOVERY
    // ==========================================

    /**
     * Get all volunteer profiles (with pagination)
     * GET /api/volunteer-profiles?page={page}&size={size}
     */
    @GetMapping
    public ResponseEntity<?> getAllProfiles(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        try {
            List<VolunteerProfileDTO> profiles = volunteerProfileService.getAllProfiles(page, size);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Search volunteers by name
     * GET /api/volunteer-profiles/search/name?q={searchTerm}
     */
    @GetMapping("/search/name")
    public ResponseEntity<?> searchByName(@RequestParam String q) {
        try {
            List<VolunteerProfileDTO> profiles = volunteerProfileService.searchVolunteersByName(q);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Search volunteers by location
     * GET /api/volunteer-profiles/search/location?q={location}
     */
    @GetMapping("/search/location")
    public ResponseEntity<?> searchByLocation(@RequestParam String q) {
        try {
            List<VolunteerProfileDTO> profiles = volunteerProfileService.searchVolunteersByLocation(q);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Search volunteers by bio keywords
     * GET /api/volunteer-profiles/search/bio?q={keyword}
     */
    @GetMapping("/search/bio")
    public ResponseEntity<?> searchByBio(@RequestParam String q) {
        try {
            List<VolunteerProfileDTO> profiles = volunteerProfileService.searchVolunteersByBio(q);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Advanced volunteer search
     * POST /api/volunteer-profiles/search
     */
    @PostMapping("/search")
    public ResponseEntity<?> advancedSearch(@RequestBody VolunteerSearchRequest request) {
        try {
            List<VolunteerProfileDTO> profiles = volunteerProfileService.advancedSearch(request);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get available volunteers
     * GET /api/volunteer-profiles/available
     */
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableVolunteers() {
        try {
            List<VolunteerProfileDTO> profiles = volunteerProfileService.getAvailableVolunteers();
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get available volunteers in specific location
     * GET /api/volunteer-profiles/available/location/{location}
     */
    @GetMapping("/available/location/{location}")
    public ResponseEntity<?> getAvailableVolunteersInLocation(@PathVariable String location) {
        try {
            List<VolunteerProfileDTO> profiles = volunteerProfileService.getAvailableVolunteersInLocation(location);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // RANKINGS AND LEADERBOARDS
    // ==========================================

    /**
     * Get top volunteers by hours
     * GET /api/volunteer-profiles/top/hours?limit={limit}
     */
    @GetMapping("/top/hours")
    public ResponseEntity<?> getTopVolunteersByHours(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<VolunteerProfileDTO> profiles = volunteerProfileService.getTopVolunteersByHours(limit);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get most active volunteers by events
     * GET /api/volunteer-profiles/top/active?limit={limit}
     */
    @GetMapping("/top/active")
    public ResponseEntity<?> getMostActiveVolunteers(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<VolunteerProfileDTO> profiles = volunteerProfileService.getMostActiveVolunteers(limit);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get experienced volunteers
     * GET /api/volunteer-profiles/experienced?minHours={hours}&minEvents={events}
     */
    @GetMapping("/experienced")
    public ResponseEntity<?> getExperiencedVolunteers(@RequestParam(required = false) Integer minHours,
                                                    @RequestParam(required = false) Integer minEvents) {
        try {
            List<VolunteerProfileDTO> profiles = volunteerProfileService.getExperiencedVolunteers(minHours, minEvents);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get new volunteers
     * GET /api/volunteer-profiles/new
     */
    @GetMapping("/new")
    public ResponseEntity<?> getNewVolunteers() {
        try {
            List<VolunteerProfileDTO> profiles = volunteerProfileService.getNewVolunteers();
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // STATISTICS AND ANALYTICS
    // ==========================================

    /**
     * Get platform volunteer statistics
     * GET /api/volunteer-profiles/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getVolunteerStatistics() {
        try {
            VolunteerStatsResponse stats = volunteerProfileService.getVolunteerStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get profile completion statistics
     * GET /api/volunteer-profiles/stats/completion
     */
    @GetMapping("/stats/completion")
    public ResponseEntity<?> getProfileCompletionStats() {
        try {
            ProfileCompletionStats stats = volunteerProfileService.getProfileCompletionStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get individual volunteer statistics
     * GET /api/volunteer-profiles/stats/me
     */
    @GetMapping("/stats/me")
    public ResponseEntity<?> getMyStats(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            IndividualVolunteerStats stats = volunteerProfileService.getIndividualVolunteerStats(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get volunteer stats by location
     * GET /api/volunteer-profiles/stats/location/{location}
     */
    @GetMapping("/stats/location/{location}")
    public ResponseEntity<?> getStatsByLocation(@PathVariable String location) {
        try {
            VolunteerStatsResponse stats = volunteerProfileService.getStatsByLocation(location);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // PROFILE FEATURES
    // ==========================================

    /**
     * Update volunteer availability
     * PUT /api/volunteer-profiles/availability
     */
    @PutMapping("/availability")
    public ResponseEntity<?> updateAvailability(@RequestParam Boolean isAvailable, 
                                               Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            VolunteerProfileDTO profile = volunteerProfileService.updateAvailability(userId, isAvailable);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Update profile image
     * PUT /api/volunteer-profiles/image
     */
    @PutMapping("/image")
    public ResponseEntity<?> updateProfileImage(@RequestParam String imageUrl,
                                              Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            VolunteerProfileDTO profile = volunteerProfileService.updateProfileImage(userId, imageUrl);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Add volunteer hours
     * POST /api/volunteer-profiles/hours
     */
    @PostMapping("/hours")
    public ResponseEntity<?> addVolunteerHours(@RequestParam Integer hours,
                                             @RequestParam(required = false) String description,
                                             Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            VolunteerProfileDTO profile = volunteerProfileService.addVolunteerHours(userId, hours, description);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get volunteer history
     * GET /api/volunteer-profiles/history/me
     */
    @GetMapping("/history/me")
    public ResponseEntity<?> getMyVolunteerHistory(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            List<VolunteerProfileService.VolunteerHistoryEntry> history = 
                volunteerProfileService.getVolunteerHistory(userId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get recommended events for volunteer
     * GET /api/volunteer-profiles/recommendations/events
     */
    @GetMapping("/recommendations/events")
    public ResponseEntity<?> getRecommendedEvents(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            List<VolunteerProfileService.RecommendedEvent> events = 
                volunteerProfileService.getRecommendedEvents(userId);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // ADMIN ENDPOINTS
    // ==========================================

    /**
     * Get all profiles for admin (with detailed info)
     * GET /api/volunteer-profiles/admin/all
     */
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllProfilesForAdmin(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "50") int size) {
        try {
            // TODO: Add admin authorization check
            List<VolunteerProfileDTO> profiles = volunteerProfileService.getAllProfilesForAdmin(page, size);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get profile analytics for admin
     * GET /api/volunteer-profiles/admin/analytics
     */
    @GetMapping("/admin/analytics")
    public ResponseEntity<?> getProfileAnalytics() {
        try {
            // TODO: Add admin authorization check
            VolunteerStatsResponse analytics = volunteerProfileService.getProfileAnalytics();
            return ResponseEntity.ok(analytics);
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
     * Placeholder for UserPrincipal - should be implemented based on your security setup
     * This interface represents the authenticated user principal
     */
    public interface UserPrincipal {
        Long getId();
        String getUsername();
        String getUserType();
    }
}