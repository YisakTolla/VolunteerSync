package com.volunteersync.backend.service;

import com.volunteersync.backend.dto.profile.OrganizationProfileDTO;
import com.volunteersync.backend.dto.request.UpdateOrganizationProfileRequest;
import com.volunteersync.backend.dto.response.ImpactMetricsResponse;
import com.volunteersync.backend.dto.response.NotificationSettingsResponse;
import com.volunteersync.backend.dto.response.RecruitingSettingsResponse;
import com.volunteersync.backend.dto.response.VerificationStatusResponse;
import com.volunteersync.backend.entity.profile.OrganizationProfile;
import com.volunteersync.backend.entity.user.User;
import com.volunteersync.backend.repository.OrganizationProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing organization-specific profile functionality.
 * Handles organization verification, volunteer management, impact tracking,
 * and organization-specific features.
 */
@Service
@Transactional
public class OrganizationProfileService {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationProfileService.class);

    @Autowired
    private OrganizationProfileRepository organizationProfileRepository;

    // =====================================================
    // MISSING METHODS FOR CONTROLLER COMPATIBILITY
    // =====================================================

    /**
     * Find organization profile by user ID
     */
    public OrganizationProfile findByUserId(Long userId) {
        return organizationProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Organization profile not found for user: " + userId));
    }

    /**
     * Updates organization profile using DTO
     */
    public OrganizationProfile updateOrganizationProfile(Long userId, UpdateOrganizationProfileRequest request) {
        logger.info("Updating organization profile for user ID: {}", userId);
        
        OrganizationProfile profile = findByUserId(userId);
        
        // Update fields from request
        if (request.getOrganizationType() != null) {
            profile.setOrganizationType(request.getOrganizationType());
        }
        if (request.getMissionStatement() != null) {
            profile.setMissionStatement(request.getMissionStatement());
        }
        if (request.getDescription() != null) {
            profile.setDescription(request.getDescription());
        }
        if (request.getAddress() != null) {
            profile.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            profile.setCity(request.getCity());
        }
        if (request.getState() != null) {
            profile.setState(request.getState());
        }
        if (request.getZipCode() != null) {
            profile.setZipCode(request.getZipCode());
        }
        if (request.getPhone() != null) {
            profile.setPhone(request.getPhone());
        }
        if (request.getWebsite() != null) {
            profile.setWebsite(request.getWebsite());
        }
        if (request.getFocusAreas() != null && !request.getFocusAreas().isEmpty()) {
            profile.setFocusAreas(String.join(",", request.getFocusAreas()));
        }
        
        return organizationProfileRepository.save(profile);
    }

    /**
     * Convert organization profile to DTO
     */
    public OrganizationProfileDTO convertToDTO(OrganizationProfile profile) {
        if (profile == null) {
            return null;
        }
        
        OrganizationProfileDTO dto = new OrganizationProfileDTO();
        dto.setId(profile.getId());
        dto.setDisplayName(profile.getDisplayName());
        dto.setBio(profile.getBio());
        dto.setLocation(profile.getLocation());
        dto.setPhone(profile.getPhone());
        dto.setWebsite(profile.getWebsite());
        dto.setOrganizationType(profile.getOrganizationType());
        dto.setMissionStatement(profile.getMissionStatement());
        dto.setDescription(profile.getDescription());
        dto.setAddress(profile.getAddress());
        dto.setCity(profile.getCity());
        dto.setState(profile.getState());
        dto.setZipCode(profile.getZipCode());
        dto.setIsVerified(profile.getIsVerified());
        dto.setCreatedAt(profile.getCreatedAt());
        dto.setUpdatedAt(profile.getUpdatedAt());
        
        return dto;
    }

    /**
     * Submit organization for verification
     */
    public OrganizationProfile submitForVerification(Long userId, MultipartFile businessLicense,
                                                   MultipartFile taxDocument, 
                                                   List<MultipartFile> additionalDocs) {
        logger.info("Submitting verification for user ID: {}", userId);
        
        OrganizationProfile profile = findByUserId(userId);
        
        // TODO: Implement file upload logic
        // For now, just update verification status
        profile.setVerificationStatus("PENDING");
        profile.setVerificationSubmittedAt(LocalDateTime.now());
        
        return organizationProfileRepository.save(profile);
    }

    /**
     * Get verification status
     */
    public VerificationStatusResponse getVerificationStatus(Long userId) {
        OrganizationProfile profile = findByUserId(userId);
        
        VerificationStatusResponse response = new VerificationStatusResponse();
        response.setStatus(profile.getVerificationStatus() != null ? profile.getVerificationStatus() : "NOT_SUBMITTED");
        response.setSubmittedAt(profile.getVerificationSubmittedAt());
        response.setReviewedAt(profile.getVerifiedAt());
        
        return response;
    }

    /**
     * Get organization volunteers (paginated)
     */
    public Page<Object> getOrganizationVolunteers(Long userId, int page, int size, String status) {
        Pageable pageable = PageRequest.of(page, size);
        // TODO: Implement actual volunteer retrieval logic
        return Page.empty(pageable);
    }

    /**
     * Invite volunteer
     */
    public void inviteVolunteer(Long userId, String email, String message) {
        logger.info("Inviting volunteer {} for organization user ID: {}", email, userId);
        // TODO: Implement volunteer invitation logic
    }

    /**
     * Update volunteer status
     */
    public void updateVolunteerStatus(Long userId, Long volunteerId, String status, String notes) {
        logger.info("Updating volunteer {} status to {} for organization user ID: {}", volunteerId, status, userId);
        // TODO: Implement volunteer status update logic
    }

    /**
     * Get organization impact metrics
     */
    public ImpactMetricsResponse getOrganizationImpact(Long userId, String period) {
        logger.info("Getting impact metrics for user ID: {} for period: {}", userId, period);
        
        // TODO: Implement actual impact metrics calculation
        ImpactMetricsResponse response = new ImpactMetricsResponse();
        response.setPeriod(period != null ? period : "ALL_TIME");
        response.setTotalBeneficiaries(0L);
        response.setTotalHours(0L);
        response.setTotalEvents(0L);
        
        return response;
    }

    /**
     * Update impact metrics
     */
    public ImpactMetricsResponse updateImpactMetrics(Long userId, Object metricsRequest) {
        logger.info("Updating impact metrics for user ID: {}", userId);
        
        // TODO: Implement impact metrics update logic
        return getOrganizationImpact(userId, null);
    }

    /**
     * Get organization events
     */
    public Page<Object> getOrganizationEvents(Long userId, int page, int size, String status) {
        Pageable pageable = PageRequest.of(page, size);
        // TODO: Implement actual event retrieval logic
        return Page.empty(pageable);
    }

    /**
     * Get volunteer applications
     */
    public Page<Object> getVolunteerApplications(Long userId, int page, int size, String status, Long eventId) {
        Pageable pageable = PageRequest.of(page, size);
        // TODO: Implement actual application retrieval logic
        return Page.empty(pageable);
    }

    /**
     * Get volunteer matches
     */
    public Page<Object> getVolunteerMatches(Long userId, int page, int size, List<String> skills, String location) {
        Pageable pageable = PageRequest.of(page, size);
        // TODO: Implement actual volunteer matching logic
        return Page.empty(pageable);
    }

    /**
     * Get partnership recommendations
     */
    public Page<Object> getPartnershipRecommendations(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // TODO: Implement actual partnership recommendation logic
        return Page.empty(pageable);
    }

    /**
     * Update notification settings
     */
    public NotificationSettingsResponse updateNotificationSettings(Long userId, Object settingsRequest) {
        logger.info("Updating notification settings for user ID: {}", userId);
        
        // TODO: Implement notification settings update logic
        NotificationSettingsResponse response = new NotificationSettingsResponse();
        response.setEmailNotifications(true);
        response.setPushNotifications(true);
        response.setSmsNotifications(false);
        response.setEventReminders(true);
        response.setApplicationUpdates(true);
        
        return response;
    }

    /**
     * Update recruiting settings
     */
    public RecruitingSettingsResponse updateRecruitingSettings(Long userId, Object settingsRequest) {
        logger.info("Updating recruiting settings for user ID: {}", userId);
        
        // TODO: Implement recruiting settings update logic
        RecruitingSettingsResponse response = new RecruitingSettingsResponse();
        response.setAutoAcceptApplications(false);
        response.setPreferredSkills(List.of());
        response.setRecruitingMessage("Welcome to our organization!");
        response.setMaxVolunteersPerEvent(50);
        
        return response;
    }

    // =====================================================
    // ORGANIZATION PROFILE CREATION & SETUP
    // =====================================================

    /**
     * Creates a new organization profile with default settings.
     * 
     * @param user The user to create a profile for
     * @return The created organization profile
     */
    public OrganizationProfile createOrganizationProfile(User user) {
        logger.info("Creating organization profile for user ID: {}", user.getId());

        OrganizationProfile profile = new OrganizationProfile(user);

        // Set organization-specific defaults
        profile.setIsVerified(false);
        profile.setIsActive(true);
        profile.setAcceptsInternationalVolunteers(true);
        profile.setProvidesVolunteerTraining(false);
        profile.setRequiresBackgroundCheck(false);
        profile.setRequiresOrientationSession(false);
        profile.setPublishesAnnualReport(false);
        profile.setTotalVolunteersServed(0);
        profile.setActiveVolunteersCount(0);
        profile.setCountry("United States");

        profile = organizationProfileRepository.save(profile);
        logger.info("Successfully created organization profile ID: {} for user: {}",
                profile.getId(), user.getId());

        return profile;
    }

    /**
     * Completes organization profile setup after initial creation.
     * Used during the organization profile setup wizard.
     * 
     * @param profileId        The profile ID
     * @param userId           The user ID (for verification)
     * @param organizationType Type of organization
     * @param missionStatement Mission statement
     * @param description      Detailed description
     * @param address          Physical address
     * @param city             City
     * @param state            State
     * @param zipCode          ZIP code
     * @param phone            Contact phone
     * @param website          Website URL
     * @param focusAreas       Areas of focus
     * @return The updated profile
     */
    public OrganizationProfile completeProfileSetup(Long profileId, Long userId,
            String organizationType, String missionStatement,
            String description, String address, String city,
            String state, String zipCode, String phone,
            String website, List<String> focusAreas) {
        logger.info("Completing profile setup for organization profile ID: {}", profileId);

        OrganizationProfile profile = organizationProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Organization profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        // Update basic organization info
        if (organizationType != null)
            profile.setOrganizationType(organizationType.trim());
        if (missionStatement != null)
            profile.setMissionStatement(missionStatement.trim());
        if (description != null)
            profile.setDescription(description.trim());
        if (phone != null)
            profile.setPhone(phone.trim());
        if (website != null)
            profile.setWebsite(website.trim());

        // Update address information
        if (address != null)
            profile.setAddress(address.trim());
        if (city != null)
            profile.setCity(city.trim());
        if (state != null)
            profile.setState(state.trim());
        if (zipCode != null)
            profile.setZipCode(zipCode.trim());

        // Update focus areas
        if (focusAreas != null && !focusAreas.isEmpty()) {
            String focusAreasJson = String.join(",", focusAreas);
            profile.setFocusAreas(focusAreasJson);
        }

        profile = organizationProfileRepository.save(profile);
        logger.info("Successfully completed profile setup for organization ID: {}", profileId);

        return profile;
    }

    // =====================================================
    // ORGANIZATION STATISTICS & ANALYTICS
    // =====================================================

    /**
     * Gets comprehensive organization statistics.
     * 
     * @param profile The organization profile
     * @return ProfileStats with organization-specific metrics
     */
    public ProfileService.ProfileStats getOrganizationStats(OrganizationProfile profile) {
        // Get basic profile stats
        int totalViews = 0; // TODO: Implement profile view tracking
        int totalConnections = profile.getTotalVolunteersServed() != null ? profile.getTotalVolunteersServed() : 0;

        // TODO: Count total activities when VolunteerActivityRepository is available
        int totalActivities = 0; // Placeholder

        return new ProfileService.ProfileStats(
                profile.getId(),
                profile.getDisplayName(),
                profile.getCreatedAt(),
                profile.getUpdatedAt(),
                totalViews,
                totalConnections,
                totalActivities);
    }

    // =====================================================
    // SEARCH & DISCOVERY
    // =====================================================

    /**
     * Finds organizations by type.
     * 
     * @param organizationType Type of organization
     * @param isVerified       Whether organization must be verified
     * @param isActive         Whether organization must be active
     * @return List of matching organization profiles
     */
    @Transactional(readOnly = true)
    public List<OrganizationProfile> findOrganizationsByType(String organizationType,
            Boolean isVerified, Boolean isActive) {
        logger.debug("Searching organizations by type: {}", organizationType);

        if (organizationType != null && !organizationType.trim().isEmpty()) {
            return organizationProfileRepository.findByOrganizationTypeIgnoreCase(organizationType);
        }

        if (isVerified != null && isVerified) {
            return organizationProfileRepository.findByIsVerifiedTrue();
        }

        // Return all organizations if no specific criteria
        return organizationProfileRepository.findAll();
    }

    /**
     * Gets verified organizations for display in featured/trusted sections.
     * 
     * @return List of verified organization profiles
     */
    @Transactional(readOnly = true)
    public List<OrganizationProfile> getVerifiedOrganizations() {
        logger.debug("Getting verified organizations");
        return organizationProfileRepository.findByIsVerifiedTrue();
    }
}