package com.volunteersync.backend.service;

import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.entity.profile.OrganizationProfile;
import com.volunteersync.backend.entity.profile.OrganizationMembership;
import com.volunteersync.backend.entity.profile.VolunteerProfile;
import com.volunteersync.backend.entity.profile.VolunteerActivity;
import com.volunteersync.backend.repository.OrganizationProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for managing organization-specific profile functionality.
 * Handles organization verification, volunteer management, impact tracking,
 * and organization-specific features.
 * 
 * This service extends the base profile functionality with organization-specific
 * features like verification status, volunteer tracking, and impact metrics.
 */
@Service
@Transactional
public class OrganizationProfileService {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationProfileService.class);

    @Autowired
    private OrganizationProfileRepository organizationProfileRepository;

    // Note: These repositories might not exist yet - removing for now
    // @Autowired
    // private OrganizationMembershipRepository membershipRepository;

    // @Autowired
    // private VolunteerActivityRepository volunteerActivityRepository;

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
     * @param profileId The profile ID
     * @param userId The user ID (for verification)
     * @param organizationType Type of organization
     * @param missionStatement Mission statement
     * @param description Detailed description
     * @param address Physical address
     * @param city City
     * @param state State
     * @param zipCode ZIP code
     * @param phone Contact phone
     * @param website Website URL
     * @param focusAreas Areas of focus
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
        if (organizationType != null) profile.setOrganizationType(organizationType.trim());
        if (missionStatement != null) profile.setMissionStatement(missionStatement.trim());
        if (description != null) profile.setDescription(description.trim());
        if (phone != null) profile.setPhone(phone.trim());
        if (website != null) profile.setWebsite(website.trim());

        // Update address information
        if (address != null) profile.setAddress(address.trim());
        if (city != null) profile.setCity(city.trim());
        if (state != null) profile.setState(state.trim());
        if (zipCode != null) profile.setZipCode(zipCode.trim());

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
    // ORGANIZATION-SPECIFIC PROFILE UPDATES
    // =====================================================

    /**
     * Updates organization legal and administrative information.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param ein Employer Identification Number
     * @param taxExemptStatus Tax exempt status
     * @param registrationNumber State registration number
     * @param registrationState Registration state
     * @param incorporationDate Incorporation date
     * @param foundedDate Founded date
     * @return The updated profile
     */
    public OrganizationProfile updateLegalInfo(Long profileId, Long userId, String ein,
                                             String taxExemptStatus, String registrationNumber,
                                             String registrationState, LocalDate incorporationDate,
                                             LocalDate foundedDate) {
        logger.info("Updating legal info for organization profile ID: {}", profileId);

        OrganizationProfile profile = organizationProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Organization profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        // Update legal information
        if (ein != null) profile.setEin(ein.trim());
        if (taxExemptStatus != null) profile.setTaxExemptStatus(taxExemptStatus.trim());
        if (registrationNumber != null) profile.setRegistrationNumber(registrationNumber.trim());
        if (registrationState != null) profile.setRegistrationState(registrationState.trim());
        if (incorporationDate != null) profile.setIncorporationDate(incorporationDate);
        if (foundedDate != null) profile.setFoundedDate(foundedDate);

        profile = organizationProfileRepository.save(profile);
        logger.info("Successfully updated legal info for organization ID: {}", profileId);
        
        return profile;
    }

    /**
     * Updates organization contact information.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param primaryContactName Primary contact name
     * @param primaryContactTitle Primary contact title
     * @param primaryContactEmail Primary contact email
     * @param primaryContactPhone Primary contact phone
     * @param secondaryContactName Secondary contact name
     * @param secondaryContactTitle Secondary contact title
     * @param secondaryContactEmail Secondary contact email
     * @param secondaryContactPhone Secondary contact phone
     * @return The updated profile
     */
    public OrganizationProfile updateContactInfo(Long profileId, Long userId,
                                               String primaryContactName, String primaryContactTitle,
                                               String primaryContactEmail, String primaryContactPhone,
                                               String secondaryContactName, String secondaryContactTitle,
                                               String secondaryContactEmail, String secondaryContactPhone) {
        logger.info("Updating contact info for organization profile ID: {}", profileId);

        OrganizationProfile profile = organizationProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Organization profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        // Update primary contact
        profile.setPrimaryContactName(primaryContactName);
        profile.setPrimaryContactTitle(primaryContactTitle);
        profile.setPrimaryContactEmail(primaryContactEmail);
        profile.setPrimaryContactPhone(primaryContactPhone);

        // Update secondary contact
        profile.setSecondaryContactName(secondaryContactName);
        profile.setSecondaryContactTitle(secondaryContactTitle);
        profile.setSecondaryContactEmail(secondaryContactEmail);
        profile.setSecondaryContactPhone(secondaryContactPhone);

        profile = organizationProfileRepository.save(profile);
        logger.info("Successfully updated contact info for organization ID: {}", profileId);
        
        return profile;
    }

    /**
     * Updates organization operational details.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param operatingHours Operating hours
     * @param seasonalOperations Seasonal operations info
     * @param servingAreas Geographic areas served
     * @param targetDemographic Target demographic
     * @param minimumAge Minimum volunteer age
     * @param minimumCommitmentHours Minimum commitment hours
     * @param commitmentFrequency Commitment frequency
     * @return The updated profile
     */
    public OrganizationProfile updateOperationalInfo(Long profileId, Long userId,
                                                    String operatingHours, String seasonalOperations,
                                                    String servingAreas, String targetDemographic,
                                                    Integer minimumAge, Integer minimumCommitmentHours,
                                                    String commitmentFrequency) {
        logger.info("Updating operational info for organization profile ID: {}", profileId);

        OrganizationProfile profile = organizationProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Organization profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        // Update operational details
        profile.setOperatingHours(operatingHours);
        profile.setSeasonalOperations(seasonalOperations);
        profile.setServingAreas(servingAreas);
        profile.setTargetDemographic(targetDemographic);
        profile.setMinimumAge(minimumAge);
        profile.setMinimumCommitmentHours(minimumCommitmentHours);
        profile.setCommitmentFrequency(commitmentFrequency);

        profile = organizationProfileRepository.save(profile);
        logger.info("Successfully updated operational info for organization ID: {}", profileId);
        
        return profile;
    }

    /**
     * Updates volunteer requirements and policies.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param acceptsInternationalVolunteers Whether accepts international volunteers
     * @param providesVolunteerTraining Whether provides training
     * @param requiresBackgroundCheck Whether requires background check
     * @param requiresOrientationSession Whether requires orientation
     * @param volunteerBenefits Volunteer benefits description
     * @param equipmentProvided Equipment provided description
     * @param safetyPolicies Safety policies description
     * @return The updated profile
     */
    public OrganizationProfile updateVolunteerRequirements(Long profileId, Long userId,
                                                          Boolean acceptsInternationalVolunteers,
                                                          Boolean providesVolunteerTraining,
                                                          Boolean requiresBackgroundCheck,
                                                          Boolean requiresOrientationSession,
                                                          String volunteerBenefits,
                                                          String equipmentProvided,
                                                          String safetyPolicies) {
        logger.info("Updating volunteer requirements for organization profile ID: {}", profileId);

        OrganizationProfile profile = organizationProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Organization profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        // Update volunteer requirements
        if (acceptsInternationalVolunteers != null) 
            profile.setAcceptsInternationalVolunteers(acceptsInternationalVolunteers);
        if (providesVolunteerTraining != null) 
            profile.setProvidesVolunteerTraining(providesVolunteerTraining);
        if (requiresBackgroundCheck != null) 
            profile.setRequiresBackgroundCheck(requiresBackgroundCheck);
        if (requiresOrientationSession != null) 
            profile.setRequiresOrientationSession(requiresOrientationSession);
        
        profile.setVolunteerBenefits(volunteerBenefits);
        profile.setEquipmentProvided(equipmentProvided);
        profile.setSafetyPolicies(safetyPolicies);

        profile = organizationProfileRepository.save(profile);
        logger.info("Successfully updated volunteer requirements for organization ID: {}", profileId);
        
        return profile;
    }

    /**
     * Updates organization financial and transparency information.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param annualRevenue Annual revenue range
     * @param fundingSources Primary funding sources
     * @param financialReportsUrl Financial reports URL
     * @param publishesAnnualReport Whether publishes annual report
     * @param latestAnnualReportUrl Latest annual report URL
     * @return The updated profile
     */
    public OrganizationProfile updateFinancialInfo(Long profileId, Long userId,
                                                 String annualRevenue, String fundingSources,
                                                 String financialReportsUrl, Boolean publishesAnnualReport,
                                                 String latestAnnualReportUrl) {
        logger.info("Updating financial info for organization profile ID: {}", profileId);

        OrganizationProfile profile = organizationProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Organization profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        // Update financial information
        profile.setAnnualRevenue(annualRevenue);
        profile.setFundingSources(fundingSources);
        profile.setFinancialReportsUrl(financialReportsUrl);
        if (publishesAnnualReport != null) profile.setPublishesAnnualReport(publishesAnnualReport);
        profile.setLatestAnnualReportUrl(latestAnnualReportUrl);

        profile = organizationProfileRepository.save(profile);
        logger.info("Successfully updated financial info for organization ID: {}", profileId);
        
        return profile;
    }

    /**
     * Updates organization impact metrics and achievements.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param peopleServedAnnually Number of people served annually
     * @param impactMetrics Impact metrics description
     * @param successStories Success stories
     * @param awardsAndRecognition Awards and recognition
     * @return The updated profile
     */
    public OrganizationProfile updateImpactInfo(Long profileId, Long userId,
                                              Integer peopleServedAnnually, String impactMetrics,
                                              String successStories, String awardsAndRecognition) {
        logger.info("Updating impact info for organization profile ID: {}", profileId);

        OrganizationProfile profile = organizationProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Organization profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        // Update impact information
        profile.setPeopleServedAnnually(peopleServedAnnually);
        profile.setImpactMetrics(impactMetrics);
        profile.setSuccessStories(successStories);
        profile.setAwardsAndRecognition(awardsAndRecognition);

        profile = organizationProfileRepository.save(profile);
        logger.info("Successfully updated impact info for organization ID: {}", profileId);
        
        return profile;
    }

    // =====================================================
    // ORGANIZATION VERIFICATION
    // =====================================================

    /**
     * Initiates the verification process for an organization.
     * This would typically involve document submission and review.
     * 
     * @param profileId The profile ID
     * @param userId The user ID requesting verification
     * @return The updated profile with verification status
     */
    public OrganizationProfile requestVerification(Long profileId, Long userId) {
        logger.info("Verification requested for organization profile ID: {}", profileId);

        OrganizationProfile profile = organizationProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Organization profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to request verification for this profile");
        }

        // Check if already verified
        if (profile.getIsVerified()) {
            throw new IllegalStateException("Organization is already verified");
        }

        // Validate required information for verification
        validateVerificationRequirements(profile);

        // TODO: Implement verification workflow
        // This would typically involve:
        // 1. Document submission
        // 2. Admin review queue
        // 3. Verification status tracking
        
        logger.info("Verification request submitted for organization ID: {}", profileId);
        return profile;
    }

    /**
     * Validates that an organization has the required information for verification.
     * 
     * @param profile The organization profile
     * @throws IllegalStateException if required information is missing
     */
    private void validateVerificationRequirements(OrganizationProfile profile) {
        StringBuilder missingFields = new StringBuilder();

        if (profile.getOrganizationType() == null || profile.getOrganizationType().trim().isEmpty()) {
            missingFields.append("Organization Type, ");
        }
        if (profile.getMissionStatement() == null || profile.getMissionStatement().trim().isEmpty()) {
            missingFields.append("Mission Statement, ");
        }
        if (profile.getAddress() == null || profile.getAddress().trim().isEmpty()) {
            missingFields.append("Address, ");
        }
        if (profile.getCity() == null || profile.getCity().trim().isEmpty()) {
            missingFields.append("City, ");
        }
        if (profile.getState() == null || profile.getState().trim().isEmpty()) {
            missingFields.append("State, ");
        }
        if (profile.getPrimaryContactName() == null || profile.getPrimaryContactName().trim().isEmpty()) {
            missingFields.append("Primary Contact Name, ");
        }
        if (profile.getPrimaryContactEmail() == null || profile.getPrimaryContactEmail().trim().isEmpty()) {
            missingFields.append("Primary Contact Email, ");
        }

        if (missingFields.length() > 0) {
            String missing = missingFields.toString();
            missing = missing.substring(0, missing.length() - 2); // Remove trailing comma and space
            throw new IllegalStateException("Missing required fields for verification: " + missing);
        }
    }

    /**
     * Approves verification for an organization (admin only).
     * 
     * @param profileId The profile ID
     * @param adminUserId The admin user ID performing the verification
     * @param verificationNotes Notes about the verification
     * @return The verified profile
     */
    public OrganizationProfile approveVerification(Long profileId, Long adminUserId, String verificationNotes) {
        logger.info("Approving verification for organization profile ID: {} by admin: {}", 
                   profileId, adminUserId);

        OrganizationProfile profile = organizationProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Organization profile not found"));

        // TODO: Verify admin permissions
        // if (!isAdmin(adminUserId)) {
        //     throw new SecurityException("User not authorized to approve verifications");
        // }

        profile.setIsVerified(true);
        profile.setVerifiedAt(LocalDateTime.now());
        profile.setVerifiedBy("admin_" + adminUserId);

        profile = organizationProfileRepository.save(profile);
        
        // TODO: Send notification to organization about verification approval
        // notificationService.sendVerificationApprovalNotification(profile);
        
        logger.info("Successfully approved verification for organization ID: {}", profileId);
        return profile;
    }

    /**
     * Deactivates an organization profile.
     * 
     * @param profileId The profile ID
     * @param userId The user ID requesting deactivation
     * @param reason Reason for deactivation
     * @return The deactivated profile
     */
    public OrganizationProfile deactivateOrganization(Long profileId, Long userId, String reason) {
        logger.info("Deactivating organization profile ID: {} by user: {}", profileId, userId);

        OrganizationProfile profile = organizationProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Organization profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to deactivate this profile");
        }

        profile.setIsActive(false);
        profile.setDeactivatedAt(LocalDateTime.now());
        profile.setDeactivationReason(reason);

        profile = organizationProfileRepository.save(profile);
        logger.info("Successfully deactivated organization ID: {}", profileId);
        
        return profile;
    }

    // =====================================================
    // VOLUNTEER MANAGEMENT (Basic Implementation)
    // =====================================================

    /**
     * Placeholder for getting organization volunteers.
     * TODO: Implement when OrganizationMembershipRepository is available
     * 
     * @param organizationId The organization profile ID
     * @param includeInactive Whether to include inactive memberships
     * @return Empty list for now
     */
    @Transactional(readOnly = true)
    public List<OrganizationMembership> getOrganizationVolunteers(Long organizationId, 
                                                                Boolean includeInactive) {
        logger.debug("Getting volunteers for organization ID: {}", organizationId);
        
        // TODO: Implement when OrganizationMembershipRepository is available
        // if (includeInactive != null && includeInactive) {
        //     return membershipRepository.findByOrganizationIdOrderByJoinedDateDesc(organizationId);
        // } else {
        //     return membershipRepository.findActiveByOrganizationIdOrderByJoinedDateDesc(organizationId);
        // }
        
        return List.of(); // Return empty list for now
    }

    /**
     * Placeholder for getting volunteer activity statistics.
     * TODO: Implement when VolunteerActivityRepository is available
     * 
     * @param organizationId The organization profile ID
     * @return Basic stats with zeros
     */
    @Transactional(readOnly = true)
    public VolunteerActivityStats getVolunteerActivityStats(Long organizationId) {
        logger.debug("Getting volunteer activity stats for organization ID: {}", organizationId);
        
        // TODO: Implement when VolunteerActivityRepository is available
        // Return basic stats for now
        return new VolunteerActivityStats(organizationId, 0, 0, 0, 0);
    }

    /**
     * Updates organization volunteer statistics.
     * Called when volunteer activities are logged.
     * 
     * @param organizationId The organization profile ID
     * @param newVolunteerHours Hours to add
     * @param newVolunteer Whether a new volunteer joined
     */
    public void updateOrganizationStats(Long organizationId, Integer newVolunteerHours, Boolean newVolunteer) {
        logger.info("Updating organization stats for organization ID: {}", organizationId);

        OrganizationProfile profile = organizationProfileRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization profile not found"));

        if (newVolunteer != null && newVolunteer) {
            int currentTotal = profile.getTotalVolunteersServed() != null ? profile.getTotalVolunteersServed() : 0;
            profile.setTotalVolunteersServed(currentTotal + 1);
        }

        // TODO: Update active volunteers count when membership repository is available
        // Long activeCount = membershipRepository.countActiveByOrganizationId(organizationId);
        // profile.setActiveVolunteersCount(activeCount.intValue());

        profile.setLastActivityDate(LocalDateTime.now());
        organizationProfileRepository.save(profile);
        
        logger.info("Successfully updated organization stats for organization ID: {}", organizationId);
    }

    // =====================================================
    // SEARCH & DISCOVERY (Basic Implementation)
    // =====================================================

    /**
     * Finds organizations by type.
     * 
     * @param organizationType Type of organization
     * @param isVerified Whether organization must be verified
     * @param isActive Whether organization must be active
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
        // Long activitiesCount = volunteerActivityRepository.countByOrganizationId(profile.getId());
        // int totalActivities = activitiesCount != null ? activitiesCount.intValue() : 0;
        int totalActivities = 0; // Placeholder
        
        return new ProfileService.ProfileStats(
            profile.getId(),
            profile.getDisplayName(),
            profile.getCreatedAt(),
            profile.getUpdatedAt(),
            totalViews,
            totalConnections,
            totalActivities
        );
    }

    /**
     * Inner class for volunteer activity statistics.
     */
    public static class VolunteerActivityStats {
        private final Long organizationId;
        private final int totalActivities;
        private final int totalVolunteerHours;
        private final int activeVolunteers;
        private final int recentActivities;

        public VolunteerActivityStats(Long organizationId, int totalActivities, 
                                    int totalVolunteerHours, int activeVolunteers, 
                                    int recentActivities) {
            this.organizationId = organizationId;
            this.totalActivities = totalActivities;
            this.totalVolunteerHours = totalVolunteerHours;
            this.activeVolunteers = activeVolunteers;
            this.recentActivities = recentActivities;
        }

        // Getters
        public Long getOrganizationId() { return organizationId; }
        public int getTotalActivities() { return totalActivities; }
        public int getTotalVolunteerHours() { return totalVolunteerHours; }
        public int getActiveVolunteers() { return activeVolunteers; }
        public int getRecentActivities() { return recentActivities; }
    }

    // =====================================================
    // MAILING ADDRESS MANAGEMENT
    // =====================================================

    /**
     * Updates organization mailing address if different from physical address.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param mailingAddress Mailing address
     * @param mailingCity Mailing city
     * @param mailingState Mailing state
     * @param mailingZipCode Mailing ZIP code
     * @return The updated profile
     */
    public OrganizationProfile updateMailingAddress(Long profileId, Long userId,
                                                  String mailingAddress, String mailingCity,
                                                  String mailingState, String mailingZipCode) {
        logger.info("Updating mailing address for organization profile ID: {}", profileId);

        OrganizationProfile profile = organizationProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Organization profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        // Update mailing address
        profile.setMailingAddress(mailingAddress);
        profile.setMailingCity(mailingCity);
        profile.setMailingState(mailingState);
        profile.setMailingZipCode(mailingZipCode);

        profile = organizationProfileRepository.save(profile);
        logger.info("Successfully updated mailing address for organization ID: {}", profileId);
        
        return profile;
    }

    /**
     * Gets organization size category based on volunteer numbers.
     * 
     * @param profile The organization profile
     * @return Size category string
     */
    public String getOrganizationSizeCategory(OrganizationProfile profile) {
        int activeVolunteers = profile.getActiveVolunteersCount() != null ? profile.getActiveVolunteersCount() : 0;
        
        if (activeVolunteers == 0) return "Starting Out";
        if (activeVolunteers <= 10) return "Small";
        if (activeVolunteers <= 50) return "Medium";
        if (activeVolunteers <= 200) return "Large";
        return "Enterprise";
    }

    /**
     * Checks if organization meets minimum requirements for certain features.
     * 
     * @param profile The organization profile
     * @return true if organization meets minimum requirements
     */
    public boolean meetsMinimumRequirements(OrganizationProfile profile) {
        return profile.getMissionStatement() != null && !profile.getMissionStatement().trim().isEmpty() &&
               profile.getDescription() != null && !profile.getDescription().trim().isEmpty() &&
               profile.getAddress() != null && !profile.getAddress().trim().isEmpty() &&
               profile.getPrimaryContactName() != null && !profile.getPrimaryContactName().trim().isEmpty();
    }
}