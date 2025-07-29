package com.volunteersync.backend.controller;

import com.volunteersync.backend.dto.*;
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
import java.util.List;
import java.util.Optional;

/**
 * Controller for volunteer-specific profile functionality.
 * Handles volunteer skills, interests, experience levels, badges, and volunteer activities.
 * 
 * This controller extends the base profile functionality with volunteer-specific
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

            VolunteerProfile updatedProfile = volunteerProfileService.completeProfileSetup(
                    user.getId(), request);
            
            return ResponseEntity.ok(ApiResponse.success("Volunteer profile setup completed", 
                    convertToVolunteerDTO(updatedProfile)));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to complete setup: " + e.getMessage()));
        }
    }

    /**
     * Updates volunteer-specific profile information.
     * 
     * PUT /api/profiles/volunteer/me
     */
    @PutMapping("/me")
    public ResponseEntity<?> updateVolunteerProfile(
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

            VolunteerProfile updatedProfile = volunteerProfileService.updateVolunteerProfile(
                    user.getId(), request);
            
            return ResponseEntity.ok(ApiResponse.success("Volunteer profile updated successfully", 
                    convertToVolunteerDTO(updatedProfile)));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to update profile: " + e.getMessage()));
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

            ProfileSkill skill = volunteerProfileService.addSkill(user.getId(), request);
            
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

            ProfileSkill updatedSkill = volunteerProfileService.updateSkillLevel(
                    user.getId(), skillId, skillLevel);
            
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

            volunteerProfileService.removeSkill(user.getId(), skillId);
            
            return ResponseEntity.ok(ApiResponse.success("Skill removed successfully"));

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

            List<ProfileSkill> skills = volunteerProfileService.getVolunteerSkills(user.getId());
            List<ProfileSkillDTO> skillDTOs = skills.stream()
                    .map(this::convertToSkillDTO)
                    .toList();
            
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

            ProfileInterest interest = volunteerProfileService.addInterest(user.getId(), request);
            
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

            volunteerProfileService.removeInterest(user.getId(), interestId);
            
            return ResponseEntity.ok(ApiResponse.success("Interest removed successfully"));

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

            List<ProfileInterest> interests = volunteerProfileService.getVolunteerInterests(user.getId());
            List<ProfileInterestDTO> interestDTOs = interests.stream()
                    .map(this::convertToInterestDTO)
                    .toList();
            
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

            VolunteerProfile updatedProfile = volunteerProfileService.updateExperienceLevel(
                    user.getId(), experienceLevel);
            
            return ResponseEntity.ok(ApiResponse.success("Experience level updated successfully", 
                    convertToVolunteerDTO(updatedProfile)));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to update experience level: " + e.getMessage()));
        }
    }

    /**
     * Gets volunteer activity statistics and history.
     * 
     * GET /api/profiles/volunteer/activity
     */
    @GetMapping("/activity")
    public ResponseEntity<?> getVolunteerActivity(HttpServletRequest request) {
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

            // This would return activity stats, volunteer hours, completed events, etc.
            Object activityStats = volunteerProfileService.getVolunteerActivityStats(user.getId());
            
            return ResponseEntity.ok(ApiResponse.success("Activity retrieved successfully", activityStats));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve activity: " + e.getMessage()));
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
    public ResponseEntity<?> getVolunteerBadges(HttpServletRequest request) {
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

            List<ProfileBadge> badges = volunteerProfileService.getVolunteerBadges(user.getId());
            List<ProfileBadgeDTO> badgeDTOs = badges.stream()
                    .map(this::convertToBadgeDTO)
                    .toList();
            
            return ResponseEntity.ok(ApiResponse.success("Badges retrieved successfully", badgeDTOs));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve badges: " + e.getMessage()));
        }
    }

    // =====================================================
    // VOLUNTEER DISCOVERY & MATCHING
    // =====================================================

    /**
     * Finds volunteer opportunities matching the volunteer's profile.
     * 
     * GET /api/profiles/volunteer/recommendations
     */
    @GetMapping("/recommendations")
    public ResponseEntity<?> getVolunteerRecommendations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
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

            // This would use the volunteer's skills, interests, and location to find matching opportunities
            Object recommendations = volunteerProfileService.getVolunteerRecommendations(
                    user.getId(), page, size);
            
            return ResponseEntity.ok(ApiResponse.success("Recommendations retrieved successfully", 
                    recommendations));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve recommendations: " + e.getMessage()));
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
     * Converts a VolunteerProfile entity to DTO.
     */
    private VolunteerProfileDTO convertToVolunteerDTO(VolunteerProfile profile) {
        // This would use a mapper service or manual conversion
        // For now, returning a placeholder - implement based on your DTO structure
        return volunteerProfileService.convertToDTO(profile);
    }

    /**
     * Converts a ProfileSkill entity to DTO.
     */
    private ProfileSkillDTO convertToSkillDTO(ProfileSkill skill) {
        // This would use a mapper service or manual conversion
        return new ProfileSkillDTO(
                skill.getId(),
                skill.getSkillName(),
                skill.getSkillLevel(),
                skill.getYearsOfExperience(),
                skill.isEndorsed(),
                skill.getCreatedAt()
        );
    }

    /**
     * Converts a ProfileInterest entity to DTO.
     */
    private ProfileInterestDTO convertToInterestDTO(ProfileInterest interest) {
        // This would use a mapper service or manual conversion
        return new ProfileInterestDTO(
                interest.getId(),
                interest.getInterestName(),
                interest.getInterestCategory(),
                interest.getPriorityLevel(),
                interest.getCreatedAt()
        );
    }

    /**
     * Converts a ProfileBadge entity to DTO.
     */
    private ProfileBadgeDTO convertToBadgeDTO(ProfileBadge badge) {
        // This would use a mapper service or manual conversion
        return new ProfileBadgeDTO(
                badge.getId(),
                badge.getBadgeName(),
                badge.getBadgeDescription(),
                badge.getBadgeIcon(),
                badge.getBadgeCategory(),
                badge.getEarnedAt(),
                badge.isVisible()
        );
    }
}