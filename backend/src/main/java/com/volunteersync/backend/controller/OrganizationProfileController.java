package com.volunteersync.backend.controller;

import com.volunteersync.backend.dto.request.UpdateOrganizationProfileRequest;
import com.volunteersync.backend.dto.response.ApiResponse;
import com.volunteersync.backend.service.OrganizationProfileService;
import com.volunteersync.backend.service.ProfileService;
import com.volunteersync.backend.entity.profile.OrganizationProfile;
import com.volunteersync.backend.entity.user.User;
import com.volunteersync.backend.entity.user.UserType;
import com.volunteersync.backend.util.JwtTokenUtil;
import com.volunteersync.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Controller for organization-specific profile functionality.
 * Handles organization verification, volunteer management, impact tracking,
 * and organization-specific features.
 */
@RestController
@RequestMapping("/api/profiles/organization")
@CrossOrigin(origins = "*") // Configure properly for production
public class OrganizationProfileController {

    @Autowired
    private OrganizationProfileService organizationProfileService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    // =====================================================
    // ORGANIZATION PROFILE SETUP & COMPLETION
    // =====================================================

    /**
     * Completes organization profile setup after initial creation.
     * Used during the profile setup wizard.
     * 
     * POST /api/profiles/organization/complete-setup
     */
    @PostMapping("/complete-setup")
    public ResponseEntity<?> completeOrganizationSetup(
            @Valid @RequestBody UpdateOrganizationProfileRequest request,
            HttpServletRequest httpRequest) {
        try {
            User user = getCurrentUser(httpRequest);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for organization accounts"));
            }

            // Extract individual fields from the DTO and call the service method
            OrganizationProfile updatedProfile = organizationProfileService.completeProfileSetup(
                user.getId(), // Use the user ID as profile ID for now
                user.getId(),
                request.getOrganizationType(),
                request.getMissionStatement(),
                request.getDescription(),
                request.getAddress(),
                request.getCity(),
                request.getState(),
                request.getZipCode(),
                request.getPhone(),
                request.getWebsite(),
                request.getFocusAreas()
            );
            
            return ResponseEntity.ok(ApiResponse.success("Organization profile setup completed", 
                    organizationProfileService.convertToDTO(updatedProfile)));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to complete setup: " + e.getMessage()));
        }
    }

    /**
     * Updates organization-specific profile information.
     * 
     * PUT /api/profiles/organization/me
     */
    @PutMapping("/me")
    public ResponseEntity<?> updateOrganizationProfile(
            @Valid @RequestBody UpdateOrganizationProfileRequest request,
            HttpServletRequest httpRequest) {
        try {
            User user = getCurrentUser(httpRequest);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for organization accounts"));
            }

            OrganizationProfile updatedProfile = organizationProfileService.updateOrganizationProfile(
                    user.getId(), request);

            return ResponseEntity.ok(ApiResponse.success("Organization profile updated successfully",
                    organizationProfileService.convertToDTO(updatedProfile)));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to update profile: " + e.getMessage()));
        }
    }

    // =====================================================
    // ORGANIZATION VERIFICATION
    // =====================================================

    /**
     * Submits organization for verification.
     * Uploads required documents and submits verification request.
     * 
     * POST /api/profiles/organization/verification/submit
     */
    @PostMapping("/verification/submit")
    public ResponseEntity<?> submitForVerification(
            @RequestParam("taxDocument") MultipartFile taxDocument,
            @RequestParam("registrationDocument") MultipartFile registrationDocument,
            @RequestParam(value = "additionalDocuments", required = false) List<MultipartFile> additionalDocuments,
            HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for organization accounts"));
            }

            OrganizationProfile updatedProfile = organizationProfileService.submitForVerification(
                    user.getId(), taxDocument, registrationDocument, additionalDocuments);

            return ResponseEntity.ok(ApiResponse.success("Verification documents submitted successfully",
                    organizationProfileService.convertToDTO(updatedProfile)));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to submit verification: " + e.getMessage()));
        }
    }

    /**
     * Gets the current verification status and details.
     * 
     * GET /api/profiles/organization/verification/status
     */
    @GetMapping("/verification/status")
    public ResponseEntity<?> getVerificationStatus(HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for organization accounts"));
            }

            Object verificationStatus = organizationProfileService.getVerificationStatus(user.getId());

            return ResponseEntity.ok(ApiResponse.success("Verification status retrieved successfully",
                    verificationStatus));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve verification status: " + e.getMessage()));
        }
    }

    // =====================================================
    // VOLUNTEER MANAGEMENT
    // =====================================================

    /**
     * Gets all volunteers associated with the organization.
     * 
     * GET /api/profiles/organization/volunteers
     */
    @GetMapping("/volunteers")
    public ResponseEntity<?> getOrganizationVolunteers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for organization accounts"));
            }

            Object volunteers = organizationProfileService.getOrganizationVolunteers(
                    user.getId(), page, size, status);

            return ResponseEntity.ok(ApiResponse.success("Volunteers retrieved successfully", volunteers));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve volunteers: " + e.getMessage()));
        }
    }

    /**
     * Invites a volunteer to join the organization.
     * 
     * POST /api/profiles/organization/volunteers/invite
     */
    @PostMapping("/volunteers/invite")
    public ResponseEntity<?> inviteVolunteer(
            @RequestParam String volunteerEmail,
            @RequestParam(required = false) String message,
            HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for organization accounts"));
            }

            organizationProfileService.inviteVolunteer(user.getId(), volunteerEmail, message);

            return ResponseEntity.ok(ApiResponse.success("Volunteer invitation sent successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to send invitation: " + e.getMessage()));
        }
    }

    /**
     * Approves or rejects a volunteer application.
     * 
     * PUT /api/profiles/organization/volunteers/{volunteerId}/status
     */
    @PutMapping("/volunteers/{volunteerId}/status")
    public ResponseEntity<?> updateVolunteerStatus(
            @PathVariable Long volunteerId,
            @RequestParam String status,
            @RequestParam(required = false) String notes,
            HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for organization accounts"));
            }

            organizationProfileService.updateVolunteerStatus(user.getId(), volunteerId, status, notes);

            return ResponseEntity.ok(ApiResponse.success("Volunteer status updated successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to update volunteer status: " + e.getMessage()));
        }
    }

    // =====================================================
    // IMPACT TRACKING & ANALYTICS
    // =====================================================

    /**
     * Gets organization impact metrics and statistics.
     * 
     * GET /api/profiles/organization/impact
     */
    @GetMapping("/impact")
    public ResponseEntity<?> getOrganizationImpact(
            @RequestParam(required = false) String timeframe,
            HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for organization accounts"));
            }

            Object impactMetrics = organizationProfileService.getOrganizationImpact(user.getId(), timeframe);

            return ResponseEntity.ok(ApiResponse.success("Impact metrics retrieved successfully", impactMetrics));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve impact metrics: " + e.getMessage()));
        }
    }

    /**
     * Updates organization impact data manually.
     * 
     * PUT /api/profiles/organization/impact
     */
    @PutMapping("/impact")
    public ResponseEntity<?> updateImpactMetrics(
            @Valid @RequestBody Object impactData,
            HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for organization accounts"));
            }

            Object updatedMetrics = organizationProfileService.updateImpactMetrics(user.getId(), impactData);

            return ResponseEntity.ok(ApiResponse.success("Impact metrics updated successfully", updatedMetrics));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to update impact metrics: " + e.getMessage()));
        }
    }

    // =====================================================
    // EVENT & OPPORTUNITY MANAGEMENT
    // =====================================================

    /**
     * Gets all events created by the organization.
     * 
     * GET /api/profiles/organization/events
     */
    @GetMapping("/events")
    public ResponseEntity<?> getOrganizationEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for organization accounts"));
            }

            Object events = organizationProfileService.getOrganizationEvents(
                    user.getId(), page, size, status);

            return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully", events));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve events: " + e.getMessage()));
        }
    }

    /**
     * Gets volunteer applications for organization events.
     * 
     * GET /api/profiles/organization/applications
     */
    @GetMapping("/applications")
    public ResponseEntity<?> getVolunteerApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long eventId,
            HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for organization accounts"));
            }

            Object applications = organizationProfileService.getVolunteerApplications(
                    user.getId(), page, size, status, eventId);

            return ResponseEntity.ok(ApiResponse.success("Applications retrieved successfully", applications));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve applications: " + e.getMessage()));
        }
    }

    // =====================================================
    // ORGANIZATION DISCOVERY & NETWORKING
    // =====================================================

    /**
     * Finds potential volunteer matches for the organization.
     * 
     * GET /api/profiles/organization/matches
     */
    @GetMapping("/matches")
    public ResponseEntity<?> getVolunteerMatches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<String> skills,
            @RequestParam(required = false) String location,
            HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for organization accounts"));
            }

            Object matches = organizationProfileService.getVolunteerMatches(
                    user.getId(), page, size, skills, location);

            return ResponseEntity.ok(ApiResponse.success("Volunteer matches retrieved successfully", matches));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve matches: " + e.getMessage()));
        }
    }

    /**
     * Gets recommendations for similar organizations to partner with.
     * 
     * GET /api/profiles/organization/partnerships
     */
    @GetMapping("/partnerships")
    public ResponseEntity<?> getPartnershipRecommendations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for organization accounts"));
            }

            Object partnerships = organizationProfileService.getPartnershipRecommendations(
                    user.getId(), page, size);

            return ResponseEntity.ok(ApiResponse.success("Partnership recommendations retrieved successfully",
                    partnerships));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve partnership recommendations: " + e.getMessage()));
        }
    }

    // =====================================================
    // ORGANIZATION SETTINGS & PREFERENCES
    // =====================================================

    /**
     * Updates organization notification preferences.
     * 
     * PUT /api/profiles/organization/notifications
     */
    @PutMapping("/notifications")
    public ResponseEntity<?> updateNotificationSettings(
            @Valid @RequestBody Object notificationSettings,
            HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for organization accounts"));
            }

            Object updatedSettings = organizationProfileService.updateNotificationSettings(
                    user.getId(), notificationSettings);

            return ResponseEntity.ok(ApiResponse.success("Notification settings updated successfully",
                    updatedSettings));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to update notification settings: " + e.getMessage()));
        }
    }

    /**
     * Updates organization recruiting preferences.
     * 
     * PUT /api/profiles/organization/recruiting
     */
    @PutMapping("/recruiting")
    public ResponseEntity<?> updateRecruitingSettings(
            @Valid @RequestBody Object recruitingSettings,
            HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for organization accounts"));
            }

            Object updatedSettings = organizationProfileService.updateRecruitingSettings(
                    user.getId(), recruitingSettings);

            return ResponseEntity.ok(ApiResponse.success("Recruiting settings updated successfully",
                    updatedSettings));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to update recruiting settings: " + e.getMessage()));
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
            String token = jwtTokenUtil.extractTokenFromRequest(request);
            if (token != null && jwtTokenUtil.validateToken(token)) {
                String email = jwtTokenUtil.getUsernameFromToken(token);
                return userRepository.findByEmail(email).orElse(null);
            }
        } catch (Exception e) {
            // Token is invalid or expired
        }
        return null;
    }
}