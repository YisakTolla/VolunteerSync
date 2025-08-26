package com.volunteersync.backend.controller;

import com.volunteersync.backend.entity.VolunteerProfile;
import com.volunteersync.backend.repository.VolunteerProfileRepository;
import com.volunteersync.backend.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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
     * Get all organizations followed by current volunteer
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