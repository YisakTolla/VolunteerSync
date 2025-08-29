// Fixed VolunteerProfileController.java - Resolved duplicate endpoints
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
import java.util.Map;
import java.util.HashMap;
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
     * Create volunteer profile (LEGACY - kept for backward compatibility)
     * POST /api/volunteer-profiles
     */
    @PostMapping
    public ResponseEntity<?> createProfile(@Valid @RequestBody CreateVolunteerProfileRequest request,
            Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            VolunteerProfileDTO profile = volunteerProfileService.createOrUpdateProfile(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Create or update volunteer profile (UNIFIED UPSERT ENDPOINT)
     * PUT /api/volunteer-profiles/me
     */
    @PutMapping("/me")
    public ResponseEntity<?> createOrUpdateMyProfile(@Valid @RequestBody CreateVolunteerProfileRequest request,
            Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            VolunteerProfileDTO profile = volunteerProfileService.createOrUpdateProfile(request, userId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * DEPRECATED: Legacy update endpoint (kept for compatibility)
     * PUT /api/volunteer-profiles/me/legacy
     */
    @PutMapping("/me/legacy")
    public ResponseEntity<?> updateMyProfileLegacy(@Valid @RequestBody UpdateVolunteerProfileRequest request,
            Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);

            // Convert UpdateVolunteerProfileRequest to CreateVolunteerProfileRequest
            CreateVolunteerProfileRequest createRequest = new CreateVolunteerProfileRequest();
            createRequest.setFirstName(request.getFirstName());
            createRequest.setLastName(request.getLastName());
            createRequest.setBio(request.getBio());
            createRequest.setLocation(request.getLocation());
            createRequest.setPhoneNumber(request.getPhoneNumber());
            createRequest.setProfileImageUrl(request.getProfileImageUrl());
            createRequest.setIsAvailable(request.getIsAvailable());

            VolunteerProfileDTO profile = volunteerProfileService.createOrUpdateProfile(createRequest, userId);
            return ResponseEntity.ok(profile);
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

    // Add these methods to your existing VolunteerProfileController.java file:

    /**
     * Get volunteer profile statistics (MISSING ENDPOINT - ADD THIS)
     * GET /api/volunteer-profiles/me/stats
     */
    @GetMapping("/me/stats")
    public ResponseEntity<?> getVolunteerStats(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);

            // Create mock stats - replace with actual service call
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalHours", 0);
            stats.put("eventsAttended", 0);
            stats.put("organizationsFollowed", 0);
            stats.put("badgesEarned", 0);
            stats.put("hoursThisMonth", 0);
            stats.put("upcomingEvents", 0);
            stats.put("completedApplications", 0);
            stats.put("profileCompletion", 75);

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            System.err.println("Error fetching volunteer stats: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * Follow an organization (MISSING ENDPOINT - ADD THIS)
     * POST /api/volunteer-profiles/me/follow/{organizationId}
     */
    @PostMapping("/me/follow/{organizationId}")
    public ResponseEntity<?> followOrganization(
            @PathVariable Long organizationId,
            Authentication authentication) {

        try {
            Long userId = getCurrentUserId(authentication);

            // Mock implementation - replace with actual service
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Successfully followed organization");
            response.put("organizationId", organizationId);
            response.put("following", true);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Unfollow an organization (MISSING ENDPOINT - ADD THIS)
     * DELETE /api/volunteer-profiles/me/follow/{organizationId}
     */
    @DeleteMapping("/me/follow/{organizationId}")
    public ResponseEntity<?> unfollowOrganization(
            @PathVariable Long organizationId,
            Authentication authentication) {

        try {
            Long userId = getCurrentUserId(authentication);

            // Mock implementation - replace with actual service
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Successfully unfollowed organization");
            response.put("organizationId", organizationId);
            response.put("following", false);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

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

    // ==========================================
    // RESPONSE CLASSES FOR FOLLOW FUNCTIONALITY
    // Add these classes to your existing VolunteerProfileController.java
    // ==========================================

    public static class FollowResponse {
        private boolean isFollowing;
        private String message;
        private Integer totalFollowedOrganizations;
        private long timestamp;

        public FollowResponse() {
            this.timestamp = System.currentTimeMillis();
        }

        public FollowResponse(boolean isFollowing, String message, Integer totalFollowedOrganizations) {
            this.isFollowing = isFollowing;
            this.message = message;
            this.totalFollowedOrganizations = totalFollowedOrganizations;
            this.timestamp = System.currentTimeMillis();
        }

        public boolean isFollowing() {
            return isFollowing;
        }

        public void setFollowing(boolean following) {
            isFollowing = following;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Integer getTotalFollowedOrganizations() {
            return totalFollowedOrganizations;
        }

        public void setTotalFollowedOrganizations(Integer totalFollowedOrganizations) {
            this.totalFollowedOrganizations = totalFollowedOrganizations;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    public static class FollowStatusCheckResponse {
        private Long organizationId;
        private boolean isFollowing;
        private long timestamp;

        public FollowStatusCheckResponse() {
            this.timestamp = System.currentTimeMillis();
        }

        public FollowStatusCheckResponse(Long organizationId, boolean isFollowing) {
            this.organizationId = organizationId;
            this.isFollowing = isFollowing;
            this.timestamp = System.currentTimeMillis();
        }

        public Long getOrganizationId() {
            return organizationId;
        }

        public void setOrganizationId(Long organizationId) {
            this.organizationId = organizationId;
        }

        public boolean isFollowing() {
            return isFollowing;
        }

        public void setFollowing(boolean following) {
            isFollowing = following;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    public static class OrganizationFollowersResponse {
        private Long organizationId;
        private Long followerCount;
        private List<VolunteerProfileDTO> followers;
        private long timestamp;

        public OrganizationFollowersResponse() {
            this.timestamp = System.currentTimeMillis();
        }

        public OrganizationFollowersResponse(Long organizationId, Long followerCount,
                List<VolunteerProfileDTO> followers) {
            this.organizationId = organizationId;
            this.followerCount = followerCount;
            this.followers = followers;
            this.timestamp = System.currentTimeMillis();
        }

        public Long getOrganizationId() {
            return organizationId;
        }

        public void setOrganizationId(Long organizationId) {
            this.organizationId = organizationId;
        }

        public Long getFollowerCount() {
            return followerCount;
        }

        public void setFollowerCount(Long followerCount) {
            this.followerCount = followerCount;
        }

        public List<VolunteerProfileDTO> getFollowers() {
            return followers;
        }

        public void setFollowers(List<VolunteerProfileDTO> followers) {
            this.followers = followers;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    public static class FollowerCountResponse {
        private Long organizationId;
        private Long followerCount;
        private long timestamp;

        public FollowerCountResponse() {
            this.timestamp = System.currentTimeMillis();
        }

        public FollowerCountResponse(Long organizationId, Long followerCount) {
            this.organizationId = organizationId;
            this.followerCount = followerCount;
            this.timestamp = System.currentTimeMillis();
        }

        public Long getOrganizationId() {
            return organizationId;
        }

        public void setOrganizationId(Long organizationId) {
            this.organizationId = organizationId;
        }

        public Long getFollowerCount() {
            return followerCount;
        }

        public void setFollowerCount(Long followerCount) {
            this.followerCount = followerCount;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}