package com.volunteersync.backend.controller;

import com.volunteersync.backend.dto.*;
import com.volunteersync.backend.dto.profile.*;
import com.volunteersync.backend.dto.request.*;
import com.volunteersync.backend.service.OrganizationProfileService;
import com.volunteersync.backend.service.ProfileService;
import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.entity.UserType;
import com.volunteersync.backend.entity.profile.OrganizationProfile;
import com.volunteersync.backend.entity.profile.VolunteerProfile;
import com.volunteersync.backend.entity.enums.ProfileVisibility;
import com.volunteersync.backend.util.JwtTokenUtil;
import com.volunteersync.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * Controller for organization-specific profile functionality.
 * Handles organization verification, volunteer management, impact tracking,
 * and organization-specific features.
 * 
 * This controller extends the base profile functionality with organization-specific
 * features like verification status, volunteer tracking, and impact metrics.
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
                        .body(new ApiResponse(false, "Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "This endpoint is only for organization accounts"));
            }

            OrganizationProfile updatedProfile = organizationProfileService.completeProfileSetup(
                    user.getId(), request);
            
            return ResponseEntity.ok(new ApiResponse(true, "Organization profile setup completed", 
                    convertToOrganizationDTO(updatedProfile)));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to complete setup: " + e.getMessage()));
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
                        .body(new ApiResponse(false, "Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "This endpoint is only for organization accounts"));
            }

            OrganizationProfile updatedProfile = organizationProfileService.updateOrganizationProfile(
                    user.getId(), request);
            
            return ResponseEntity.ok(new ApiResponse(true, "Organization profile updated successfully", 
                    convertToOrganizationDTO(updatedProfile)));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to update profile: " + e.getMessage()));
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
                        .body(new ApiResponse(false, "Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "This endpoint is only for organization accounts"));
            }

            OrganizationProfile updatedProfile = organizationProfileService.submitForVerification(
                    user.getId(), taxDocument, registrationDocument, additionalDocuments);
            
            return ResponseEntity.ok(new ApiResponse(true, "Verification documents submitted successfully", 
                    convertToOrganizationDTO(updatedProfile)));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to submit verification: " + e.getMessage()));
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
                        .body(new ApiResponse(false, "Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "This endpoint is only for organization accounts"));
            }

            Object verificationStatus = organizationProfileService.getVerificationStatus(user.getId());
            
            return ResponseEntity.ok(new ApiResponse(true, "Verification status retrieved successfully", 
                    verificationStatus));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to retrieve verification status: " + e.getMessage()));
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
                        .body(new ApiResponse(false, "Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "This endpoint is only for organization accounts"));
            }

            Object volunteers = organizationProfileService.getOrganizationVolunteers(
                    user.getId(), page, size, status);
            
            return ResponseEntity.ok(new ApiResponse(true, "Volunteers retrieved successfully", volunteers));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to retrieve volunteers: " + e.getMessage()));
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
                        .body(new ApiResponse(false, "Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "This endpoint is only for organization accounts"));
            }

            Object invitation = organizationProfileService.inviteVolunteer(
                    user.getId(), volunteerEmail, message);
            
            return ResponseEntity.ok(new ApiResponse(true, "Volunteer invitation sent successfully", invitation));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to send invitation: " + e.getMessage()));
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
                        .body(new ApiResponse(false, "Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "This endpoint is only for organization accounts"));
            }

            Object updatedStatus = organizationProfileService.updateVolunteerStatus(
                    user.getId(), volunteerId, status, notes);
            
            return ResponseEntity.ok(new ApiResponse(true, "Volunteer status updated successfully", updatedStatus));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to update volunteer status: " + e.getMessage()));
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
                        .body(new ApiResponse(false, "Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "This endpoint is only for organization accounts"));
            }

            Object impactMetrics = organizationProfileService.getOrganizationImpact(user.getId(), timeframe);
            
            return ResponseEntity.ok(new ApiResponse(true, "Impact metrics retrieved successfully", impactMetrics));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to retrieve impact metrics: " + e.getMessage()));
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
                        .body(new ApiResponse(false, "Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "This endpoint is only for organization accounts"));
            }

            Object updatedMetrics = organizationProfileService.updateImpactMetrics(user.getId(), impactData);
            
            return ResponseEntity.ok(new ApiResponse(true, "Impact metrics updated successfully", updatedMetrics));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to update impact metrics: " + e.getMessage()));
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
                        .body(new ApiResponse(false, "Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "This endpoint is only for organization accounts"));
            }

            Object events = organizationProfileService.getOrganizationEvents(
                    user.getId(), page, size, status);
            
            return ResponseEntity.ok(new ApiResponse(true, "Events retrieved successfully", events));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to retrieve events: " + e.getMessage()));
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
                        .body(new ApiResponse(false, "Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "This endpoint is only for organization accounts"));
            }

            Object applications = organizationProfileService.getVolunteerApplications(
                    user.getId(), page, size, status, eventId);
            
            return ResponseEntity.ok(new ApiResponse(true, "Applications retrieved successfully", applications));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to retrieve applications: " + e.getMessage()));
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
                        .body(new ApiResponse(false, "Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "This endpoint is only for organization accounts"));
            }

            Object matches = organizationProfileService.getVolunteerMatches(
                    user.getId(), page, size, skills, location);
            
            return ResponseEntity.ok(new ApiResponse(true, "Volunteer matches retrieved successfully", matches));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to retrieve matches: " + e.getMessage()));
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
                        .body(new ApiResponse(false, "Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "This endpoint is only for organization accounts"));
            }

            Object partnerships = organizationProfileService.getPartnershipRecommendations(
                    user.getId(), page, size);
            
            return ResponseEntity.ok(new ApiResponse(true, "Partnership recommendations retrieved successfully", 
                    partnerships));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to retrieve partnership recommendations: " + e.getMessage()));
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
                        .body(new ApiResponse(false, "Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "This endpoint is only for organization accounts"));
            }

            Object updatedSettings = organizationProfileService.updateNotificationSettings(
                    user.getId(), notificationSettings);
            
            return ResponseEntity.ok(new ApiResponse(true, "Notification settings updated successfully", 
                    updatedSettings));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to update notification settings: " + e.getMessage()));
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
                        .body(new ApiResponse(false, "Authentication required"));
            }

            if (user.getUserType() != UserType.ORGANIZATION) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "This endpoint is only for organization accounts"));
            }

            Object updatedSettings = organizationProfileService.updateRecruitingSettings(
                    user.getId(), recruitingSettings);
            
            return ResponseEntity.ok(new ApiResponse(true, "Recruiting settings updated successfully", 
                    updatedSettings));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to update recruiting settings: " + e.getMessage()));
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
            if (token != null) {
                String email = jwtTokenUtil.extractEmail(token);
                return userRepository.findByEmail(email);
            }
        } catch (Exception e) {
            // Token is invalid or expired
        }
        return null;
    }

    /**
     * Converts an OrganizationProfile entity to DTO.
     */
    private OrganizationProfileDTO convertToOrganizationDTO(OrganizationProfile profile) {
        // This would use a mapper service or manual conversion
        // For now, returning a placeholder - implement based on your DTO structure
        return organizationProfileService.convertToDTO(profile);
    }
}