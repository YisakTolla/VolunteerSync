// backend/src/main/java/com/volunteersync/backend/controller/OrganizationProfileController.java
package com.volunteersync.backend.controller;

import com.volunteersync.backend.service.OrganizationProfileService;
import com.volunteersync.backend.dto.OrganizationProfileDTO;
import com.volunteersync.backend.service.OrganizationProfileService.CreateOrganizationProfileRequest;
import com.volunteersync.backend.service.OrganizationProfileService.UpdateOrganizationProfileRequest;
import com.volunteersync.backend.service.OrganizationProfileService.OrganizationSearchRequest;
import com.volunteersync.backend.service.OrganizationProfileService.OrganizationFilterRequest;
import com.volunteersync.backend.service.OrganizationProfileService.OrganizationStatsResponse;
import com.volunteersync.backend.service.OrganizationProfileService.ProfileCompletionStats;
import com.volunteersync.backend.service.OrganizationProfileService.IndividualOrganizationStats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Organization Profile Controller - handles organization profile endpoints
 * Manages organization profiles, search, verification, and statistics
 */
@RestController
@RequestMapping("/api/organization-profiles")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrganizationProfileController {

    @Autowired
    private OrganizationProfileService organizationProfileService;

    // ==========================================
    // PROFILE MANAGEMENT
    // ==========================================

    /**
     * Create organization profile
     * POST /api/organization-profiles
     */
    @PostMapping
    public ResponseEntity<?> createProfile(@Valid @RequestBody CreateOrganizationProfileRequest request,
                                         Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            OrganizationProfileDTO profile = organizationProfileService.createProfile(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get current organization's profile
     * GET /api/organization-profiles/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            OrganizationProfileDTO profile = organizationProfileService.getProfileByUserId(userId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get organization profile by ID
     * GET /api/organization-profiles/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProfileById(@PathVariable Long id) {
        try {
            OrganizationProfileDTO profile = organizationProfileService.getProfileById(id);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Update organization profile
     * PUT /api/organization-profiles/me
     */
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(@Valid @RequestBody UpdateOrganizationProfileRequest request,
                                           Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            OrganizationProfileDTO profile = organizationProfileService.updateProfile(userId, request);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // SEARCH AND DISCOVERY
    // ==========================================

    /**
     * Search organizations by name
     * GET /api/organization-profiles/search/name?q={searchTerm}
     */
    @GetMapping("/search/name")
    public ResponseEntity<?> searchByName(@RequestParam String q) {
        try {
            List<OrganizationProfileDTO> profiles = organizationProfileService.searchOrganizationsByName(q);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Search organizations by category
     * GET /api/organization-profiles/search/category?q={category}
     */
    @GetMapping("/search/category")
    public ResponseEntity<?> searchByCategory(@RequestParam String q) {
        try {
            List<OrganizationProfileDTO> profiles = organizationProfileService.searchOrganizationsByCategory(q);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Search organizations by location
     * GET /api/organization-profiles/search/location?q={location}
     */
    @GetMapping("/search/location")
    public ResponseEntity<?> searchByLocation(@RequestParam String q) {
        try {
            List<OrganizationProfileDTO> profiles = organizationProfileService.searchOrganizationsByLocation(q);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Search organizations by keyword
     * GET /api/organization-profiles/search/keyword?q={keyword}
     */
    @GetMapping("/search/keyword")
    public ResponseEntity<?> searchByKeyword(@RequestParam String q) {
        try {
            List<OrganizationProfileDTO> profiles = organizationProfileService.searchOrganizationsByKeyword(q);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Advanced organization search
     * POST /api/organization-profiles/search
     */
    @PostMapping("/search")
    public ResponseEntity<?> advancedSearch(@RequestBody OrganizationSearchRequest request,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<OrganizationProfileDTO> profiles = organizationProfileService.advancedSearch(request, pageable);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Filter organizations with comprehensive criteria
     * POST /api/organization-profiles/filter
     */
    @PostMapping("/filter")
    public ResponseEntity<?> filterOrganizations(@RequestBody OrganizationFilterRequest request,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<OrganizationProfileDTO> profiles = organizationProfileService.filterOrganizations(request, pageable);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // CATEGORY AND TYPE FILTERING
    // ==========================================

    /**
     * Get organizations by category
     * GET /api/organization-profiles/category/{category}
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<?> getOrganizationsByCategory(@PathVariable String category) {
        try {
            List<OrganizationProfileDTO> profiles = organizationProfileService.getOrganizationsByCategory(category);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get organizations by type
     * GET /api/organization-profiles/type/{type}
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getOrganizationsByType(@PathVariable String type) {
        try {
            List<OrganizationProfileDTO> profiles = organizationProfileService.getOrganizationsByType(type);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get non-profit organizations
     * GET /api/organization-profiles/non-profit
     */
    @GetMapping("/non-profit")
    public ResponseEntity<?> getNonProfitOrganizations() {
        try {
            List<OrganizationProfileDTO> profiles = organizationProfileService.getNonProfitOrganizations();
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get organizations by size
     * GET /api/organization-profiles/size/{size}
     */
    @GetMapping("/size/{size}")
    public ResponseEntity<?> getOrganizationsBySize(@PathVariable String size) {
        try {
            List<OrganizationProfileDTO> profiles = organizationProfileService.getOrganizationsBySize(size);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // VERIFICATION AND TRUST
    // ==========================================

    /**
     * Get verified organizations
     * GET /api/organization-profiles/verified
     */
    @GetMapping("/verified")
    public ResponseEntity<?> getVerifiedOrganizations() {
        try {
            List<OrganizationProfileDTO> profiles = organizationProfileService.getVerifiedOrganizations();
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get highly verified organizations
     * GET /api/organization-profiles/highly-verified
     */
    @GetMapping("/highly-verified")
    public ResponseEntity<?> getHighlyVerifiedOrganizations() {
        try {
            List<OrganizationProfileDTO> profiles = organizationProfileService.getHighlyVerifiedOrganizations();
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Update verification status (Admin only)
     * PUT /api/organization-profiles/{id}/verification
     */
    @PutMapping("/{id}/verification")
    public ResponseEntity<?> updateVerificationStatus(@PathVariable Long id,
                                                     @RequestBody VerificationRequest request,
                                                     Authentication authentication) {
        try {
            String adminUserId = authentication.getName(); // Assuming admin authentication
            OrganizationProfileDTO profile = organizationProfileService.updateVerificationStatus(
                    id, request.getVerificationLevel(), request.getIsVerified(), adminUserId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // LOCATION-BASED OPERATIONS
    // ==========================================

    /**
     * Get organizations by country
     * GET /api/organization-profiles/country/{country}
     */
    @GetMapping("/country/{country}")
    public ResponseEntity<?> getOrganizationsByCountry(@PathVariable String country) {
        try {
            List<OrganizationProfileDTO> profiles = organizationProfileService.getOrganizationsByCountry(country);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get international organizations
     * GET /api/organization-profiles/international
     */
    @GetMapping("/international")
    public ResponseEntity<?> getInternationalOrganizations() {
        try {
            List<OrganizationProfileDTO> profiles = organizationProfileService.getInternationalOrganizations();
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get organizations by language support
     * GET /api/organization-profiles/language/{language}
     */
    @GetMapping("/language/{language}")
    public ResponseEntity<?> getOrganizationsByLanguage(@PathVariable String language) {
        try {
            List<OrganizationProfileDTO> profiles = organizationProfileService.getOrganizationsByLanguage(language);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // ACTIVITY AND PERFORMANCE
    // ==========================================

    /**
     * Get most active organizations
     * GET /api/organization-profiles/most-active?limit={limit}
     */
    @GetMapping("/most-active")
    public ResponseEntity<?> getMostActiveOrganizations(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<OrganizationProfileDTO> profiles = organizationProfileService.getMostActiveOrganizations(limit);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get organizations by volunteer impact
     * GET /api/organization-profiles/top-impact?limit={limit}
     */
    @GetMapping("/top-impact")
    public ResponseEntity<?> getOrganizationsByVolunteerImpact(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<OrganizationProfileDTO> profiles = organizationProfileService.getOrganizationsByVolunteerImpact(limit);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get organizations by activity level
     * GET /api/organization-profiles/activity/{level}
     */
    @GetMapping("/activity/{level}")
    public ResponseEntity<?> getOrganizationsByActivityLevel(@PathVariable String level) {
        try {
            List<OrganizationProfileDTO> profiles = organizationProfileService.getOrganizationsByActivityLevel(level);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get recently updated organizations
     * GET /api/organization-profiles/recently-updated?days={days}
     */
    @GetMapping("/recently-updated")
    public ResponseEntity<?> getRecentlyUpdatedOrganizations(@RequestParam(defaultValue = "7") int days) {
        try {
            List<OrganizationProfileDTO> profiles = organizationProfileService.getRecentlyUpdatedOrganizations(days);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get recently joined organizations
     * GET /api/organization-profiles/recently-joined?days={days}
     */
    @GetMapping("/recently-joined")
    public ResponseEntity<?> getRecentlyJoinedOrganizations(@RequestParam(defaultValue = "30") int days) {
        try {
            List<OrganizationProfileDTO> profiles = organizationProfileService.getRecentlyJoinedOrganizations(days);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // STATISTICS AND ANALYTICS
    // ==========================================

    /**
     * Get comprehensive organization statistics
     * GET /api/organization-profiles/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getOrganizationStatistics() {
        try {
            OrganizationStatsResponse stats = organizationProfileService.getOrganizationStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get profile completion statistics
     * GET /api/organization-profiles/stats/completion
     */
    @GetMapping("/stats/completion")
    public ResponseEntity<?> getProfileCompletionStats() {
        try {
            ProfileCompletionStats stats = organizationProfileService.getProfileCompletionStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get individual organization statistics
     * GET /api/organization-profiles/stats/me
     */
    @GetMapping("/stats/me")
    public ResponseEntity<?> getMyStats(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            IndividualOrganizationStats stats = organizationProfileService.getIndividualOrganizationStats(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // ADMINISTRATIVE OPERATIONS
    // ==========================================

    /**
     * Get organizations needing review (Admin only)
     * GET /api/organization-profiles/admin/needing-review
     */
    @GetMapping("/admin/needing-review")
    public ResponseEntity<?> getOrganizationsNeedingReview(Authentication authentication) {
        try {
            // Add admin role check here
            List<OrganizationProfileDTO> profiles = organizationProfileService.getOrganizationsNeedingReview();
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get organizations needing verification (Admin only)
     * GET /api/organization-profiles/admin/needing-verification
     */
    @GetMapping("/admin/needing-verification")
    public ResponseEntity<?> getOrganizationsNeedingVerification(Authentication authentication) {
        try {
            // Add admin role check here
            List<OrganizationProfileDTO> profiles = organizationProfileService.getOrganizationsNeedingVerification();
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Find potential duplicate organizations (Admin only)
     * GET /api/organization-profiles/admin/duplicates
     */
    @GetMapping("/admin/duplicates")
    public ResponseEntity<?> findPotentialDuplicates(Authentication authentication) {
        try {
            // Add admin role check here
            List<List<OrganizationProfileDTO>> duplicateGroups = organizationProfileService.findPotentialDuplicates();
            return ResponseEntity.ok(duplicateGroups);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        // Extract user ID from authentication principal
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getId();
        }
        
        // Fallback - extract from name if it's the user ID
        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid user authentication");
        }
    }

    // ==========================================
    // REQUEST/RESPONSE CLASSES
    // ==========================================

    public static class VerificationRequest {
        private String verificationLevel;
        private Boolean isVerified;
        
        public String getVerificationLevel() { return verificationLevel; }
        public void setVerificationLevel(String verificationLevel) { this.verificationLevel = verificationLevel; }
        public Boolean getIsVerified() { return isVerified; }
        public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    }

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

    // Placeholder for UserPrincipal - should be implemented based on your security setup
    public interface UserPrincipal {
        Long getId();
        String getUsername();
        String getUserType();
    }
}