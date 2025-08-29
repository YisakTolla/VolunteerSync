package com.volunteersync.backend.controller;

import com.volunteersync.backend.entity.VolunteerProfile;
import com.volunteersync.backend.entity.OrganizationProfile;
import com.volunteersync.backend.repository.VolunteerProfileRepository;
import com.volunteersync.backend.repository.OrganizationProfileRepository;
import com.volunteersync.backend.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * Simple Follow Controller - uses existing VolunteerProfile follow functionality
 */
@RestController
@RequestMapping("/api/volunteer-profiles")
@CrossOrigin(origins = "http://localhost:5173")
public class VolunteerFollowController {

    @Autowired
    private VolunteerProfileRepository volunteerProfileRepository;

    @Autowired
    private OrganizationProfileRepository organizationProfileRepository;

    @Autowired
    private JwtService jwtService;

    /**
     * Toggle follow status for an organization
     * PUT /api/volunteer-profiles/me/follow/{organizationId}
     */
    @PutMapping("/me/follow/{organizationId}")
    public ResponseEntity<?> toggleFollowOrganization(
            @PathVariable Long organizationId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Authentication required"));
            }

            VolunteerProfile volunteer = volunteerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));

            boolean wasFollowing = volunteer.isFollowingOrganization(organizationId);
            
            if (wasFollowing) {
                volunteer.unfollowOrganization(organizationId);
            } else {
                volunteer.followOrganization(organizationId);
            }
            
            volunteerProfileRepository.save(volunteer);
            
            boolean isNowFollowing = !wasFollowing;
            String message = isNowFollowing ? "Successfully followed organization" : "Successfully unfollowed organization";

            return ResponseEntity.ok(Map.of(
                "success", true,
                "isFollowing", isNowFollowing,
                "message", message,
                "organizationId", organizationId
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Server error: " + e.getMessage()));
        }
    }

    /**
     * Check follow status for an organization
     * GET /api/volunteer-profiles/me/follow/{organizationId}/status
     */
    @GetMapping("/me/follow/{organizationId}/status")
    public ResponseEntity<?> checkFollowStatus(
            @PathVariable Long organizationId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Authentication required"));
            }

            VolunteerProfile volunteer = volunteerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));

            boolean isFollowing = volunteer.isFollowingOrganization(organizationId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "isFollowing", isFollowing,
                "organizationId", organizationId
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Server error: " + e.getMessage()));
        }
    }

    /**
     * Get follower count for an organization (public endpoint)
     * GET /api/volunteer-profiles/organization/{organizationId}/follower-count
     */
    @GetMapping("/organization/{organizationId}/follower-count")
    public ResponseEntity<?> getOrganizationFollowerCount(@PathVariable Long organizationId) {
        try {
            Long followerCount = volunteerProfileRepository.countVolunteersFollowingOrganization(organizationId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "followerCount", followerCount,
                "organizationId", organizationId
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Server error: " + e.getMessage()));
        }
    }

    /**
     * Get all organizations followed by current volunteer (just IDs)
     * GET /api/volunteer-profiles/me/followed-organizations
     */
    @GetMapping("/me/followed-organizations")
    public ResponseEntity<?> getFollowedOrganizations(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Authentication required"));
            }

            VolunteerProfile volunteer = volunteerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));

            List<Long> followedOrgIds = volunteer.getFollowedOrganizationsList();

            return ResponseEntity.ok(followedOrgIds);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Server error: " + e.getMessage()));
        }
    }

    /**
     * Unfollow an organization (DELETE endpoint)
     * DELETE /api/volunteer-profiles/me/follow/{organizationId}
     */
    @DeleteMapping("/me/unfollow/{organizationId}")
    public ResponseEntity<?> unfollowOrganization(
            @PathVariable Long organizationId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Authentication required"));
            }

            VolunteerProfile volunteer = volunteerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));

            // Check if currently following the organization
            boolean wasFollowing = volunteer.isFollowingOrganization(organizationId);
            
            if (!wasFollowing) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "success", false, 
                        "message", "You are not currently following this organization",
                        "organizationId", organizationId
                    ));
            }

            // Unfollow the organization
            volunteer.unfollowOrganization(organizationId);
            volunteerProfileRepository.save(volunteer);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Successfully unfollowed organization",
                "organizationId", organizationId,
                "isFollowing", false,
                "remainingFollowedCount", volunteer.getFollowedOrganizationsCount()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Server error: " + e.getMessage()));
        }
    }

    /**
     * NEW: Get full details of organizations followed by current volunteer
     * GET /api/volunteer-profiles/me/followed-organizations-details
     */
    @GetMapping("/me/followed-organizations-details")
    public ResponseEntity<?> getFollowedOrganizationsDetails(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Authentication required"));
            }

            VolunteerProfile volunteer = volunteerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));

            List<Long> followedOrgIds = volunteer.getFollowedOrganizationsList();
            
            if (followedOrgIds.isEmpty()) {
                return ResponseEntity.ok(new ArrayList<>());
            }

            // Fetch organization details for each ID
            List<Map<String, Object>> organizationDetails = new ArrayList<>();
            
            for (Long orgId : followedOrgIds) {
                try {
                    OrganizationProfile org = organizationProfileRepository.findByUserId(orgId)
                        .orElse(null);
                    
                    if (org != null) {
                        Map<String, Object> orgData = new HashMap<>();
                        orgData.put("id", orgId);
                        orgData.put("organizationName", org.getOrganizationName() != null ? org.getOrganizationName() : "Unnamed Organization");
                        orgData.put("profileImageUrl", org.getProfileImageUrl() != null ? org.getProfileImageUrl() : "");
                        orgData.put("city", org.getCity() != null ? org.getCity() : "");
                        orgData.put("state", org.getState() != null ? org.getState() : "");
                        orgData.put("location", org.getLocationString() != null ? org.getLocationString() : "");
                        orgData.put("address", org.getAddress() != null ? org.getAddress() : "");
                        orgData.put("categories", org.getCategories() != null ? org.getCategories() : "");
                        orgData.put("primaryCategory", org.getPrimaryCategory() != null ? org.getPrimaryCategory() : "");
                        orgData.put("organizationType", org.getOrganizationType() != null ? org.getOrganizationType() : "");
                        orgData.put("bio", org.getDescription() != null ? org.getDescription() : "");
                        organizationDetails.add(orgData);
                    } else {
                        // Organization not found, create placeholder
                        Map<String, Object> orgData = new HashMap<>();
                        orgData.put("id", orgId);
                        orgData.put("organizationName", "Unnamed Organization");
                        orgData.put("profileImageUrl", "");
                        orgData.put("city", "");
                        orgData.put("state", "");
                        orgData.put("location", "Location not specified");
                        orgData.put("address", "");
                        orgData.put("categories", "Non-Profit Organization");
                        orgData.put("primaryCategory", "Non-Profit Organization");
                        orgData.put("organizationType", "Non-Profit Organization");
                        orgData.put("bio", "");
                        organizationDetails.add(orgData);
                    }
                } catch (Exception e) {
                    System.err.println("Error fetching organization details for ID " + orgId + ": " + e.getMessage());
                    // Continue with next organization
                }
            }

            return ResponseEntity.ok(organizationDetails);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Server error: " + e.getMessage()));
        }
    }

    /**
     * Extract user ID from JWT token
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                // Use the correct JwtService method names from your project
                String email = jwtService.getEmailFromToken(token);
                
                if (email != null && jwtService.validateToken(token)) {
                    return jwtService.getUserIdFromToken(token);
                }
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error extracting user ID from token: " + e.getMessage());
            return null;
        }
    }
}