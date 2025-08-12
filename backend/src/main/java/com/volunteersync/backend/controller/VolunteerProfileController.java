// backend/src/main/java/com/volunteersync/backend/controller/VolunteerProfileController.java
package com.volunteersync.backend.controller;

import com.volunteersync.backend.service.VolunteerProfileService;
import com.volunteersync.backend.service.BadgeService;
import com.volunteersync.backend.repository.UserRepository;
import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.dto.BadgeDTO;
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
public class VolunteerProfileController extends BaseController {

    @Autowired
    private VolunteerProfileService volunteerProfileService;

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private UserRepository userRepository;

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
     * Get current user's volunteer profile (UPDATED to include all frontend data)
     * GET /api/volunteer-profiles/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            // Use the complete profile method instead of basic profile
            VolunteerProfileDTO profile = volunteerProfileService.getCompleteVolunteerProfile(userId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            System.err.println("Error getting volunteer profile: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get complete volunteer profile with badges, activities, and connections
     * GET /api/volunteer-profiles/me/complete
     */
    @GetMapping("/me/complete")
    public ResponseEntity<?> getCompleteProfile(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            VolunteerProfileDTO completeProfile = volunteerProfileService.getCompleteVolunteerProfile(userId);
            return ResponseEntity.ok(completeProfile);
        } catch (Exception e) {
            System.err.println("Error getting complete volunteer profile: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get user badges (integrates with existing BadgeService)
     * GET /api/volunteer-profiles/me/badges
     */
    @GetMapping("/me/badges")
    public ResponseEntity<?> getMyBadges(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            List<BadgeDTO> badges = badgeService.getUserBadges(userId);
            return ResponseEntity.ok(badges);
        } catch (Exception e) {
            System.err.println("Error getting user badges: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get user activity history
     * GET /api/volunteer-profiles/me/activity
     */
    @GetMapping("/me/activity")
    public ResponseEntity<?> getMyActivity(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            List<VolunteerProfileDTO.ActivityEntry> activity = volunteerProfileService.getVolunteerHistory(userId);
            return ResponseEntity.ok(activity);
        } catch (Exception e) {
            System.err.println("Error getting user activity: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Update volunteer skills
     * PUT /api/volunteer-profiles/me/skills
     */
    @PutMapping("/me/skills")
    public ResponseEntity<?> updateSkills(@RequestBody UpdateSkillsRequest request,
            Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            VolunteerProfileDTO updatedProfile = volunteerProfileService.updateVolunteerSkills(userId,
                    request.getSkills());
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            System.err.println("Error updating volunteer skills: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Update volunteer interests
     * PUT /api/volunteer-profiles/me/interests
     */
    @PutMapping("/me/interests")
    public ResponseEntity<?> updateInterests(@RequestBody UpdateInterestsRequest request,
            Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            VolunteerProfileDTO updatedProfile = volunteerProfileService.updateVolunteerInterests(userId,
                    request.getInterests());
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            System.err.println("Error updating volunteer interests: " + e.getMessage());
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

    // ==========================================
    // SEARCH AND DISCOVERY
    // ==========================================

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

    // ==========================================
    // HELPER METHODS
    // ==========================================

    /**
     * Extract user ID from authentication context
     */
    // private Long getCurrentUserId(Authentication authentication) {
    //     if (authentication == null || authentication.getPrincipal() == null) {
    //         throw new RuntimeException("User not authenticated");
    //     }

    //     Object principal = authentication.getPrincipal();

    //     // Handle UserDetails
    //     if (principal instanceof UserDetails) {
    //         UserDetails userDetails = (UserDetails) principal;
    //         String email = userDetails.getUsername();

    //         // Find user by email
    //         User user = userRepository.findByEmail(email)
    //                 .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

    //         return user.getId();
    //     }

    //     // Fallback - extract from name if it's the user ID
    //     try {
    //         return Long.parseLong(authentication.getName());
    //     } catch (NumberFormatException e) {
    //         throw new RuntimeException("Invalid user authentication");
    //     }
    // }

    // ==========================================
    // REQUEST CLASSES
    // ==========================================

    public static class UpdateSkillsRequest {
        private List<String> skills;

        public UpdateSkillsRequest() {
        }

        public UpdateSkillsRequest(List<String> skills) {
            this.skills = skills;
        }

        public List<String> getSkills() {
            return skills;
        }

        public void setSkills(List<String> skills) {
            this.skills = skills;
        }
    }

    public static class UpdateInterestsRequest {
        private List<String> interests;

        public UpdateInterestsRequest() {
        }

        public UpdateInterestsRequest(List<String> interests) {
            this.interests = interests;
        }

        public List<String> getInterests() {
            return interests;
        }

        public void setInterests(List<String> interests) {
            this.interests = interests;
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
}