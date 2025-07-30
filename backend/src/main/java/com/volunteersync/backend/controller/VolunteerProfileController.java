package com.volunteersync.backend.controller;

import com.volunteersync.backend.dto.profile.*;
import com.volunteersync.backend.dto.request.*;
import com.volunteersync.backend.dto.response.ApiResponse;
import com.volunteersync.backend.service.VolunteerProfileService;
import com.volunteersync.backend.service.ProfileService;
import com.volunteersync.backend.entity.user.User;
import com.volunteersync.backend.entity.user.UserType;
import com.volunteersync.backend.entity.profile.VolunteerProfile;
import com.volunteersync.backend.entity.profile.ProfileSkill;
import com.volunteersync.backend.entity.profile.ProfileInterest;
import com.volunteersync.backend.entity.profile.ProfileBadge;
import com.volunteersync.backend.entity.enums.ExperienceLevel;
import com.volunteersync.backend.entity.enums.SkillLevel;
import com.volunteersync.backend.util.JwtTokenUtil;
import com.volunteersync.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for volunteer-specific profile functionality.
 * Handles volunteer skills, interests, experience levels, badges, and volunteer
 * activities.
 * 
 * This controller extends the base profile functionality with
 * volunteer-specific
 * features like skill management, experience tracking, and badge systems.
 */
@RestController
@RequestMapping("/api/profiles/volunteer")
@CrossOrigin(origins = "*") // Configure properly for production
public class VolunteerProfileController {

    @Autowired
    private VolunteerProfileService volunteerProfileService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    // =====================================================
    // VOLUNTEER PROFILE SETUP & COMPLETION
    // =====================================================

    /**
     * Completes volunteer profile setup after initial creation.
     * Used during the profile setup wizard.
     * 
     * POST /api/profiles/volunteer/complete-setup
     */
    @PostMapping("/complete-setup")
    public ResponseEntity<?> completeVolunteerSetup(
            @Valid @RequestBody UpdateVolunteerProfileRequest request,
            HttpServletRequest httpRequest) {
        try {
            User user = getCurrentUser(httpRequest);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.VOLUNTEER) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for volunteer accounts"));
            }

            // Get the volunteer profile ID - assuming it exists
            VolunteerProfile profile = getVolunteerProfileByUserId(user.getId());
            if (profile == null) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Volunteer profile not found"));
            }

            // Use the actual method signature from the service
            VolunteerProfile updatedProfile = volunteerProfileService.completeProfileSetup(
                    profile.getId(),
                    user.getId(),
                    request.getBio(),
                    request.getLocation(),
                    request.getExperienceLevel(),
                    request.getAvailabilityNotes(),
                    request.getNewSkills(),
                    request.getNewInterests());

            return ResponseEntity.ok(ApiResponse.success("Volunteer profile setup completed",
                    convertToVolunteerDTO(updatedProfile)));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to complete setup: " + e.getMessage()));
        }
    }

    /**
     * Updates volunteer availability preferences.
     * 
     * PUT /api/profiles/volunteer/availability
     */
    @PutMapping("/availability")
    public ResponseEntity<?> updateAvailability(
            @Valid @RequestBody UpdateAvailabilityRequest request,
            HttpServletRequest httpRequest) {
        try {
            User user = getCurrentUser(httpRequest);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.VOLUNTEER) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for volunteer accounts"));
            }

            VolunteerProfile profile = getVolunteerProfileByUserId(user.getId());
            if (profile == null) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Volunteer profile not found"));
            }

            VolunteerProfile updatedProfile = volunteerProfileService.updateAvailabilityPreferences(
                    profile.getId(),
                    user.getId(),
                    request.getAvailableWeekdays(),
                    request.getAvailableWeekends(),
                    request.getAvailableMorning(),
                    request.getAvailableAfternoon(),
                    request.getAvailableEvening(),
                    request.getAvailabilityNotes());

            return ResponseEntity.ok(ApiResponse.success("Availability updated successfully",
                    convertToVolunteerDTO(updatedProfile)));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to update availability: " + e.getMessage()));
        }
    }

    /**
     * Updates volunteer travel preferences.
     * 
     * PUT /api/profiles/volunteer/travel
     */
    @PutMapping("/travel")
    public ResponseEntity<?> updateTravelPreferences(
            @Valid @RequestBody UpdateTravelRequest request,
            HttpServletRequest httpRequest) {
        try {
            User user = getCurrentUser(httpRequest);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.VOLUNTEER) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for volunteer accounts"));
            }

            VolunteerProfile profile = getVolunteerProfileByUserId(user.getId());
            if (profile == null) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Volunteer profile not found"));
            }

            VolunteerProfile updatedProfile = volunteerProfileService.updateTravelPreferences(
                    profile.getId(),
                    user.getId(),
                    request.getWillingToTravel(),
                    request.getMaxTravelDistance(),
                    request.getHasReliableTransportation(),
                    request.getHasDriversLicense());

            return ResponseEntity.ok(ApiResponse.success("Travel preferences updated successfully",
                    convertToVolunteerDTO(updatedProfile)));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to update travel preferences: " + e.getMessage()));
        }
    }

    // =====================================================
    // SKILLS MANAGEMENT
    // =====================================================

    /**
     * Adds a skill to the volunteer's profile.
     * 
     * POST /api/profiles/volunteer/skills
     */
    @PostMapping("/skills")
    public ResponseEntity<?> addSkill(
            @Valid @RequestBody AddSkillRequest request,
            HttpServletRequest httpRequest) {
        try {
            User user = getCurrentUser(httpRequest);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.VOLUNTEER) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for volunteer accounts"));
            }

            VolunteerProfile profile = getVolunteerProfileByUserId(user.getId());
            if (profile == null) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Volunteer profile not found"));
            }

            // Use the actual method signature from the service
            ProfileSkill skill = volunteerProfileService.addSkill(
                    profile.getId(),
                    user.getId(),
                    request.getSkillName(),
                    request.getSkillLevel());

            return ResponseEntity.ok(ApiResponse.success("Skill added successfully",
                    convertToSkillDTO(skill)));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to add skill: " + e.getMessage()));
        }
    }

    /**
     * Updates a volunteer's skill level.
     * 
     * PUT /api/profiles/volunteer/skills/{skillId}
     */
    @PutMapping("/skills/{skillId}")
    public ResponseEntity<?> updateSkill(
            @PathVariable Long skillId,
            @RequestParam SkillLevel skillLevel,
            HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.VOLUNTEER) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for volunteer accounts"));
            }

            VolunteerProfile profile = getVolunteerProfileByUserId(user.getId());
            if (profile == null) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Volunteer profile not found"));
            }

            ProfileSkill updatedSkill = volunteerProfileService.updateSkillLevel(
                    profile.getId(), user.getId(), skillId, skillLevel);

            return ResponseEntity.ok(ApiResponse.success("Skill updated successfully",
                    convertToSkillDTO(updatedSkill)));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to update skill: " + e.getMessage()));
        }
    }

    /**
     * Removes a skill from the volunteer's profile.
     * 
     * DELETE /api/profiles/volunteer/skills/{skillId}
     */
    @DeleteMapping("/skills/{skillId}")
    public ResponseEntity<?> removeSkill(
            @PathVariable Long skillId,
            HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.VOLUNTEER) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for volunteer accounts"));
            }

            VolunteerProfile profile = getVolunteerProfileByUserId(user.getId());
            if (profile == null) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Volunteer profile not found"));
            }

            boolean removed = volunteerProfileService.removeSkill(profile.getId(), user.getId(), skillId);

            if (removed) {
                return ResponseEntity.ok(ApiResponse.success("Skill removed successfully"));
            } else {
                return ResponseEntity.status(400)
                        .body(ApiResponse.error("Failed to remove skill"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to remove skill: " + e.getMessage()));
        }
    }

    /**
     * Gets all skills for the current volunteer.
     * 
     * GET /api/profiles/volunteer/skills
     */
    @GetMapping("/skills")
    public ResponseEntity<?> getVolunteerSkills(HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.VOLUNTEER) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for volunteer accounts"));
            }

            VolunteerProfile profile = getVolunteerProfileByUserId(user.getId());
            if (profile == null) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Volunteer profile not found"));
            }

            List<ProfileSkill> skills = volunteerProfileService.getProfileSkills(profile.getId());
            List<ProfileSkillDTO> skillDTOs = skills.stream()
                    .map(this::convertToSkillDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Skills retrieved successfully", skillDTOs));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve skills: " + e.getMessage()));
        }
    }

    // =====================================================
    // INTERESTS MANAGEMENT
    // =====================================================

    /**
     * Adds an interest to the volunteer's profile.
     * 
     * POST /api/profiles/volunteer/interests
     */
    @PostMapping("/interests")
    public ResponseEntity<?> addInterest(
            @Valid @RequestBody AddInterestRequest request,
            HttpServletRequest httpRequest) {
        try {
            User user = getCurrentUser(httpRequest);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.VOLUNTEER) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for volunteer accounts"));
            }

            VolunteerProfile profile = getVolunteerProfileByUserId(user.getId());
            if (profile == null) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Volunteer profile not found"));
            }

            ProfileInterest interest = volunteerProfileService.addInterest(
                    profile.getId(),
                    user.getId(),
                    request.getInterestName(),
                    request.getInterestCategory());

            return ResponseEntity.ok(ApiResponse.success("Interest added successfully",
                    convertToInterestDTO(interest)));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to add interest: " + e.getMessage()));
        }
    }

    /**
     * Removes an interest from the volunteer's profile.
     * 
     * DELETE /api/profiles/volunteer/interests/{interestId}
     */
    @DeleteMapping("/interests/{interestId}")
    public ResponseEntity<?> removeInterest(
            @PathVariable Long interestId,
            HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.VOLUNTEER) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for volunteer accounts"));
            }

            VolunteerProfile profile = getVolunteerProfileByUserId(user.getId());
            if (profile == null) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Volunteer profile not found"));
            }

            boolean removed = volunteerProfileService.removeInterest(profile.getId(), user.getId(), interestId);

            if (removed) {
                return ResponseEntity.ok(ApiResponse.success("Interest removed successfully"));
            } else {
                return ResponseEntity.status(400)
                        .body(ApiResponse.error("Failed to remove interest"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to remove interest: " + e.getMessage()));
        }
    }

    /**
     * Gets all interests for the current volunteer.
     * 
     * GET /api/profiles/volunteer/interests
     */
    @GetMapping("/interests")
    public ResponseEntity<?> getVolunteerInterests(HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.VOLUNTEER) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for volunteer accounts"));
            }

            VolunteerProfile profile = getVolunteerProfileByUserId(user.getId());
            if (profile == null) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Volunteer profile not found"));
            }

            List<ProfileInterest> interests = volunteerProfileService.getProfileInterests(profile.getId());
            List<ProfileInterestDTO> interestDTOs = interests.stream()
                    .map(this::convertToInterestDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Interests retrieved successfully", interestDTOs));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve interests: " + e.getMessage()));
        }
    }

    // =====================================================
    // EXPERIENCE & ACTIVITY TRACKING
    // =====================================================

    /**
     * Updates the volunteer's overall experience level.
     * 
     * PUT /api/profiles/volunteer/experience
     */
    @PutMapping("/experience")
    public ResponseEntity<?> updateExperienceLevel(
            @RequestParam ExperienceLevel experienceLevel,
            HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.VOLUNTEER) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for volunteer accounts"));
            }

            VolunteerProfile profile = getVolunteerProfileByUserId(user.getId());
            if (profile == null) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Volunteer profile not found"));
            }

            VolunteerProfile updatedProfile = volunteerProfileService.updateVolunteerInfo(
                    profile.getId(), user.getId(), experienceLevel, null, null);

            return ResponseEntity.ok(ApiResponse.success("Experience level updated successfully",
                    convertToVolunteerDTO(updatedProfile)));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to update experience level: " + e.getMessage()));
        }
    }

    /**
     * Updates volunteer stats after activity completion.
     * 
     * PUT /api/profiles/volunteer/stats
     */
    @PutMapping("/stats")
    public ResponseEntity<?> updateVolunteerStats(
            @RequestParam(required = false) Integer hoursContributed,
            @RequestParam(required = false) Boolean newActivity,
            HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.VOLUNTEER) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for volunteer accounts"));
            }

            VolunteerProfile profile = getVolunteerProfileByUserId(user.getId());
            if (profile == null) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Volunteer profile not found"));
            }

            volunteerProfileService.updateVolunteerStats(profile.getId(), hoursContributed, newActivity);

            return ResponseEntity.ok(ApiResponse.success("Volunteer stats updated successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to update stats: " + e.getMessage()));
        }
    }

    // =====================================================
    // BADGES & ACHIEVEMENTS
    // =====================================================

    /**
     * Gets all badges earned by the volunteer.
     * 
     * GET /api/profiles/volunteer/badges
     */
    @GetMapping("/badges")
    public ResponseEntity<?> getVolunteerBadges(
            @RequestParam(defaultValue = "false") boolean includeHidden,
            HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.VOLUNTEER) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for volunteer accounts"));
            }

            VolunteerProfile profile = getVolunteerProfileByUserId(user.getId());
            if (profile == null) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Volunteer profile not found"));
            }

            List<ProfileBadge> badges = volunteerProfileService.getProfileBadges(profile.getId(), includeHidden);
            List<ProfileBadgeDTO> badgeDTOs = badges.stream()
                    .map(this::convertToBadgeDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Badges retrieved successfully", badgeDTOs));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve badges: " + e.getMessage()));
        }
    }

    // =====================================================
    // PROFILE INFORMATION ENDPOINTS
    // =====================================================

    /**
     * Gets the current volunteer's profile.
     * 
     * GET /api/profiles/volunteer/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentVolunteerProfile(HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("Authentication required"));
            }

            if (user.getUserType() != UserType.VOLUNTEER) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("This endpoint is only for volunteer accounts"));
            }

            VolunteerProfile profile = getVolunteerProfileByUserId(user.getId());
            if (profile == null) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Volunteer profile not found"));
            }

            return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully",
                    convertToVolunteerDTO(profile)));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve profile: " + e.getMessage()));
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

    /**
     * Gets volunteer profile by user ID.
     */
    private VolunteerProfile getVolunteerProfileByUserId(Long userId) {
        try {
            return getVolunteerProfileByUserId(userId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Converts a VolunteerProfile entity to DTO.
     */
    private VolunteerProfileDTO convertToVolunteerDTO(VolunteerProfile profile) {
        if (profile == null)
            return null;

        VolunteerProfileDTO dto = new VolunteerProfileDTO();

        // Basic profile information
        dto.setId(profile.getId());
        dto.setFullName(profile.getFullName());
        dto.setBio(profile.getBio());
        dto.setLocation(profile.getLocation());
        dto.setCreatedAt(profile.getCreatedAt());
        dto.setUpdatedAt(profile.getUpdatedAt());

        // Volunteer-specific information
        dto.setExperienceLevel(profile.getExperienceLevel());
        dto.setTotalVolunteerHours(profile.getTotalVolunteerHours());
        dto.setWillingToTravel(profile.getWillingToTravel());
        dto.setMaxTravelDistance(profile.getMaxTravelDistance());

        // Include skills, interests, and badges if needed
        // Note: These could be lazy-loaded separately for performance

        return dto;
    }

    /**
     * Converts a ProfileSkill entity to DTO.
     */
    private ProfileSkillDTO convertToSkillDTO(ProfileSkill skill) {
        if (skill == null)
            return null;

        ProfileSkillDTO dto = new ProfileSkillDTO();
        dto.setId(skill.getId());
        dto.setSkillName(skill.getSkillName());
        dto.setSkillLevel(skill.getLevel());
        dto.setYearsOfExperience(skill.getYearsOfExperience());
        dto.setIsVerified(skill.getVerified());
        dto.setCreatedAt(skill.getCreatedAt());

        return dto;
    }

    /**
     * Converts a ProfileInterest entity to DTO.
     */
    private ProfileInterestDTO convertToInterestDTO(ProfileInterest interest) {
        if (interest == null)
            return null;

        ProfileInterestDTO dto = new ProfileInterestDTO();
        dto.setId(interest.getId());
        dto.setInterestName(interest.getInterestName());
        dto.setInterestCategory(interest.getCategory());
        // Fix: Handle enum to Integer conversion for priority level
        if (interest.getPriorityLevel() != null) {
            dto.setPriorityLevel(interest.getPriorityLevel().getLevel());
        }
        dto.setCreatedAt(interest.getCreatedAt());

        return dto;
    }

    /**
     * Converts a ProfileBadge entity to DTO.
     */
    private ProfileBadgeDTO convertToBadgeDTO(ProfileBadge badge) {
        if (badge == null)
            return null;

        ProfileBadgeDTO dto = new ProfileBadgeDTO();
        dto.setId(badge.getId());
        dto.setBadgeName(badge.getBadgeName());
        dto.setBadgeDescription(badge.getDescription());
        dto.setBadgeIcon(badge.getIconEmoji());
        dto.setBadgeCategory(badge.getCategory());
        dto.setEarnedAt(badge.getEarnedAt());
        dto.setIsVisible(badge.getIsPublic());

        return dto;
    }
}