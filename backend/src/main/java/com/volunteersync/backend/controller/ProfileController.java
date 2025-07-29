package com.volunteersync.backend.controller;

import com.volunteersync.backend.dto.response.ApiResponse;
import com.volunteersync.backend.dto.profile.*;
import com.volunteersync.backend.dto.request.*;
import com.volunteersync.backend.dto.response.*;
import com.volunteersync.backend.service.ProfileService;
import com.volunteersync.backend.service.VolunteerProfileService;
import com.volunteersync.backend.service.OrganizationProfileService;
import com.volunteersync.backend.entity.profile.Profile;
import com.volunteersync.backend.entity.user.User;
import com.volunteersync.backend.entity.user.UserType;
import com.volunteersync.backend.entity.enums.ProfileVisibility;
import com.volunteersync.backend.util.JwtTokenUtil;
import com.volunteersync.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Main profile controller providing common endpoints for all profile types.
 * Handles profile creation, retrieval, updates, privacy settings, and search functionality.
 * 
 * This controller acts as the central hub for profile management, providing
 * unified endpoints that work across both volunteer and organization profiles.
 */
@RestController
@RequestMapping("/api/profiles")
@CrossOrigin(origins = "*") // Configure properly for production
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private VolunteerProfileService volunteerProfileService;

    @Autowired
    private OrganizationProfileService organizationProfileService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    // =====================================================
    // PROFILE CREATION & INITIALIZATION
    // =====================================================

    /**
     * Creates a new profile for the authenticated user.
     * Automatically determines profile type based on user type.
     * 
     * POST /api/profiles
     */
    @PostMapping
    public ResponseEntity<?> createProfile(HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            // Check if profile already exists
            Optional<Profile> existingProfile = profileService.findByUserId(user.getId());
            if (existingProfile.isPresent()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Profile already exists for this user"));
            }

            // Create profile based on user type
            Profile profile = profileService.createProfileForUser(user.getId());
            
            return ResponseEntity.ok(ApiResponse.success("Profile created successfully", 
                    convertToDTO(profile)));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to create profile: " + e.getMessage()));
        }
    }

    // =====================================================
    // PROFILE RETRIEVAL
    // =====================================================

    /**
     * Gets the current user's profile.
     * 
     * GET /api/profiles/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserProfile(HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            Optional<Profile> profile = profileService.findByUserId(user.getId());
            if (profile.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Profile not found"));
            }

            return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", 
                    convertToDTO(profile.get())));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve profile: " + e.getMessage()));
        }
    }

    /**
     * Gets a specific profile by ID (public view).
     * Respects privacy settings - only returns public information.
     * 
     * GET /api/profiles/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProfileById(@PathVariable Long id, HttpServletRequest request) {
        try {
            Optional<Profile> profile = profileService.findById(id);
            if (profile.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Profile not found"));
            }

            Profile prof = profile.get();
            
            // Check privacy settings
            User currentUser = getCurrentUser(request);
            if (!profileService.canViewProfile(prof, currentUser)) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("Not authorized to view this profile"));
            }

            return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", 
                    convertToPublicDTO(prof, currentUser)));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve profile: " + e.getMessage()));
        }
    }

    // =====================================================
    // PROFILE UPDATES
    // =====================================================

    /**
     * Updates the current user's profile.
     * Delegates to appropriate specialized controller based on profile type.
     * 
     * PUT /api/profiles/me
     */
    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUserProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            HttpServletRequest httpRequest) {
        try {
            User user = getCurrentUser(httpRequest);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            Profile updatedProfile = profileService.updateProfile(user.getId(), request);
            
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", 
                    convertToDTO(updatedProfile)));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to update profile: " + e.getMessage()));
        }
    }

    /**
     * Updates profile privacy settings.
     * 
     * PUT /api/profiles/me/privacy
     */
    @PutMapping("/me/privacy")
    public ResponseEntity<?> updatePrivacySettings(
            @Valid @RequestBody ProfilePrivacySettings settings,
            HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            Profile updatedProfile = profileService.updatePrivacySettings(user.getId(), settings);
            
            return ResponseEntity.ok(ApiResponse.success("Privacy settings updated successfully", 
                    convertToDTO(updatedProfile)));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to update privacy settings: " + e.getMessage()));
        }
    }

    // =====================================================
    // PROFILE SEARCH & DISCOVERY
    // =====================================================

    /**
     * Searches profiles based on criteria.
     * Returns paginated results with appropriate privacy filtering.
     * 
     * GET /api/profiles/search?query=&type=&location=&page=&size=
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchProfiles(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) List<String> skills,
            @RequestParam(required = false) List<String> interests,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);
            
            // Use the actual method signature from ProfileService
            List<Profile> searchResults = profileService.searchProfiles(
                    query, location, type, skills, interests, page, size, currentUser);
            
            // Create a simple response wrapper
            Map<String, Object> response = new HashMap<>();
            response.put("profiles", searchResults);
            response.put("totalElements", searchResults.size());
            response.put("totalPages", 1);
            response.put("currentPage", page);
            
            return ResponseEntity.ok(ApiResponse.success("Search completed successfully", 
                    response));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Search failed: " + e.getMessage()));
        }
    }

    /**
     * Gets profile statistics for the current user.
     * Includes metrics like profile views, connections, activity counts.
     * 
     * GET /api/profiles/me/stats
     */
    @GetMapping("/me/stats")
    public ResponseEntity<?> getProfileStats(HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            // Get the user's profile first
            Optional<Profile> profileOpt = profileService.findByUserId(user.getId());
            if (profileOpt.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Profile not found"));
            }

            // Use the actual method that exists in ProfileService
            ProfileService.ProfileStats basicStats = profileService.getProfileStats(profileOpt.get().getId());
            
            // Convert to ProfileStatsResponse
            ProfileStatsResponse stats = new ProfileStatsResponse(
                    basicStats.getProfileId(), 
                    user.getUserType().toString()
            );
            
            // Populate the response with basic stats
            stats.setLastUpdated(basicStats.getLastUpdated());
            stats.setProfileViews(basicStats.getTotalViews());
            stats.setConnectionsCount(basicStats.getTotalConnections());
            
            return ResponseEntity.ok(ApiResponse.success("Stats retrieved successfully", stats));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve stats: " + e.getMessage()));
        }
    }

    // =====================================================
    // PROFILE DELETION
    // =====================================================

    /**
     * Soft deletes the current user's profile.
     * Profile data is archived but not permanently removed.
     * 
     * DELETE /api/profiles/me
     */
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteProfile(HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            profileService.softDeleteProfile(user.getId());
            
            return ResponseEntity.ok(ApiResponse.success("Profile deleted successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to delete profile: " + e.getMessage()));
        }
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    /**
     * Gets the current authenticated user from the JWT token.
     */
    private User getCurrentUser(HttpServletRequest request) {
        try {
            // FIXED: Use correct method name from JwtTokenUtil
            String token = jwtTokenUtil.extractTokenFromRequest(request);
            if (token != null && jwtTokenUtil.validateToken(token)) {
                // FIXED: Use correct method name from JwtTokenUtil
                String email = jwtTokenUtil.getUsernameFromToken(token);
                return userRepository.findByEmail(email).orElse(null);
            }
        } catch (Exception e) {
            // Token is invalid or expired
        }
        return null;
    }

    /**
     * Converts a Profile entity to appropriate DTO based on profile type.
     */
    private Object convertToDTO(Profile profile) {
        if (profile.getUser().getUserType() == UserType.VOLUNTEER) {
            return profileService.convertToVolunteerDTO(profile);
        } else if (profile.getUser().getUserType() == UserType.ORGANIZATION) {
            return profileService.convertToOrganizationDTO(profile);
        }
        return profileService.convertToBaseDTO(profile);
    }

    /**
     * Converts a Profile entity to public DTO (respects privacy settings).
     */
    private Object convertToPublicDTO(Profile profile, User viewer) {
        if (profile.getUser().getUserType() == UserType.VOLUNTEER) {
            return profileService.convertToPublicVolunteerDTO(profile, viewer);
        } else if (profile.getUser().getUserType() == UserType.ORGANIZATION) {
            return profileService.convertToPublicOrganizationDTO(profile, viewer);
        }
        return profileService.convertToPublicBaseDTO(profile, viewer);
    }
}