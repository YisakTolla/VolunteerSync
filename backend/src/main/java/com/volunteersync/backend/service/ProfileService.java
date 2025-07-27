package com.volunteersync.backend.service;

import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.entity.UserType;
import com.volunteersync.backend.entity.profile.Profile;
import com.volunteersync.backend.entity.profile.VolunteerProfile;
import com.volunteersync.backend.entity.profile.OrganizationProfile;
import com.volunteersync.backend.entity.enums.ProfileVisibility;
import com.volunteersync.backend.repository.ProfileRepository;
import com.volunteersync.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Main profile service providing common functionality for all profile types.
 * Handles profile creation, retrieval, updates, and privacy settings.
 * 
 * This service acts as the central hub for profile management, delegating
 * specialized operations to VolunteerProfileService and OrganizationProfileService.
 */
@Service
@Transactional
public class ProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VolunteerProfileService volunteerProfileService;

    @Autowired
    private OrganizationProfileService organizationProfileService;

    // =====================================================
    // PROFILE CREATION & INITIALIZATION
    // =====================================================

    /**
     * Creates a new profile for a user based on their user type.
     * This method delegates to the appropriate specialized service.
     * 
     * @param userId The user ID to create a profile for
     * @return The newly created profile
     * @throws IllegalArgumentException if user not found or already has profile
     */
    public Profile createProfileForUser(Long userId) {
        logger.info("Creating profile for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Check if profile already exists
        if (profileRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("Profile already exists for user ID: " + userId);
        }

        Profile profile;
        if (user.getUserType() == UserType.VOLUNTEER) {
            profile = volunteerProfileService.createVolunteerProfile(user);
        } else if (user.getUserType() == UserType.ORGANIZATION) {
            profile = organizationProfileService.createOrganizationProfile(user);
        } else {
            throw new IllegalArgumentException("Unsupported user type: " + user.getUserType());
        }

        logger.info("Successfully created {} profile for user ID: {}", 
                   user.getUserType(), userId);
        return profile;
    }

    /**
     * Creates a default profile with basic privacy settings.
     * Used internally by specialized services.
     * 
     * @param user The user to create a profile for
     * @param profileClass The profile class type (VolunteerProfile or OrganizationProfile)
     * @return The created profile with default settings
     */
    protected <T extends Profile> T createDefaultProfile(User user, Class<T> profileClass) {
        try {
            T profile = profileClass.getDeclaredConstructor(User.class).newInstance(user);
            
            // Set default privacy settings
            profile.setProfileVisibility(ProfileVisibility.PUBLIC);
            profile.setShowEmail(false);
            profile.setShowPhone(false);
            profile.setShowLocation(true);
            profile.setAllowMessaging(true);
            profile.setShowActivity(true);
            profile.setSearchable(true);

            return profileRepository.save(profile);
        } catch (Exception e) {
            logger.error("Error creating default profile for user {}: {}", user.getId(), e.getMessage());
            throw new RuntimeException("Failed to create profile", e);
        }
    }

    // =====================================================
    // PROFILE RETRIEVAL
    // =====================================================

    /**
     * Retrieves a profile by ID with optional visibility check.
     * 
     * @param profileId The profile ID
     * @param requestingUserId The ID of the user requesting the profile (null for public access)
     * @return The profile if found and accessible
     */
    @Transactional(readOnly = true)
    public Optional<Profile> getProfileById(Long profileId, Long requestingUserId) {
        logger.debug("Retrieving profile ID: {} for user: {}", profileId, requestingUserId);

        Optional<Profile> profileOpt = profileRepository.findById(profileId);
        if (profileOpt.isEmpty()) {
            return Optional.empty();
        }

        Profile profile = profileOpt.get();
        
        // Check if profile is accessible based on privacy settings
        if (isProfileAccessible(profile, requestingUserId)) {
            return Optional.of(profile);
        }

        logger.debug("Profile ID: {} is not accessible to user: {}", profileId, requestingUserId);
        return Optional.empty();
    }

    /**
     * Retrieves a profile by user ID.
     * 
     * @param userId The user ID
     * @return The profile if found
     */
    @Transactional(readOnly = true)
    public Optional<Profile> getProfileByUserId(Long userId) {
        logger.debug("Retrieving profile for user ID: {}", userId);
        return profileRepository.findByUserId(userId);
    }

    /**
     * Retrieves all public profiles.
     * 
     * @return List of public profiles
     */
    @Transactional(readOnly = true)
    public List<Profile> getPublicProfiles() {
        logger.debug("Retrieving public profiles");
        return profileRepository.findByProfileVisibility(ProfileVisibility.PUBLIC);
    }

    /**
     * Searches profiles by visibility and searchable status.
     * 
     * @param searchTerm The search term (currently not used - basic implementation)
     * @param requestingUserId The ID of the user performing the search
     * @return List of searchable profiles
     */
    @Transactional(readOnly = true)
    public List<Profile> searchProfiles(String searchTerm, Long requestingUserId) {
        logger.debug("Searching profiles with term: '{}' for user: {}", searchTerm, requestingUserId);
        
        // For now, return all public and searchable profiles
        // TODO: Implement full-text search when needed
        return profileRepository.findPublicAndSearchableProfiles();
    }

    // =====================================================
    // PROFILE UPDATES
    // =====================================================

    /**
     * Updates basic profile information that's common to all profile types.
     * 
     * @param profileId The profile ID to update
     * @param userId The user ID making the update (must be profile owner)
     * @param bio Updated bio
     * @param phone Updated phone number
     * @param location Updated location
     * @param website Updated website URL
     * @return The updated profile
     */
    public Profile updateBasicProfile(Long profileId, Long userId, String bio, 
                                    String phone, String location, String website) {
        logger.info("Updating basic profile info for profile ID: {} by user: {}", profileId, userId);

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found with ID: " + profileId));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        // Update basic fields
        if (bio != null) profile.setBio(bio.trim());
        if (phone != null) profile.setPhone(phone.trim());
        if (location != null) profile.setLocation(location.trim());
        if (website != null) profile.setWebsite(website.trim());

        profile = profileRepository.save(profile);
        logger.info("Successfully updated basic profile info for profile ID: {}", profileId);
        return profile;
    }

    /**
     * Updates social media links for a profile.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param linkedinUrl LinkedIn URL
     * @param twitterUrl Twitter URL
     * @param facebookUrl Facebook URL
     * @param instagramUrl Instagram URL
     * @return The updated profile
     */
    public Profile updateSocialMediaLinks(Long profileId, Long userId, String linkedinUrl,
                                        String twitterUrl, String facebookUrl, String instagramUrl) {
        logger.info("Updating social media links for profile ID: {} by user: {}", profileId, userId);

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found with ID: " + profileId));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        // Update social media links
        profile.setLinkedinUrl(cleanUrl(linkedinUrl));
        profile.setTwitterUrl(cleanUrl(twitterUrl));
        profile.setFacebookUrl(cleanUrl(facebookUrl));
        profile.setInstagramUrl(cleanUrl(instagramUrl));

        profile = profileRepository.save(profile);
        logger.info("Successfully updated social media links for profile ID: {}", profileId);
        return profile;
    }

    /**
     * Updates privacy settings for a profile.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param visibility Profile visibility setting
     * @param showEmail Whether to show email
     * @param showPhone Whether to show phone
     * @param showLocation Whether to show location
     * @param allowMessaging Whether to allow messaging
     * @param showActivity Whether to show activity feed
     * @param searchable Whether profile should appear in searches
     * @return The updated profile
     */
    public Profile updatePrivacySettings(Long profileId, Long userId, ProfileVisibility visibility,
                                       Boolean showEmail, Boolean showPhone, Boolean showLocation,
                                       Boolean allowMessaging, Boolean showActivity, Boolean searchable) {
        logger.info("Updating privacy settings for profile ID: {} by user: {}", profileId, userId);

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found with ID: " + profileId));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        // Update privacy settings
        if (visibility != null) profile.setProfileVisibility(visibility);
        if (showEmail != null) profile.setShowEmail(showEmail);
        if (showPhone != null) profile.setShowPhone(showPhone);
        if (showLocation != null) profile.setShowLocation(showLocation);
        if (allowMessaging != null) profile.setAllowMessaging(allowMessaging);
        if (showActivity != null) profile.setShowActivity(showActivity);
        if (searchable != null) profile.setSearchable(searchable);

        profile = profileRepository.save(profile);
        logger.info("Successfully updated privacy settings for profile ID: {}", profileId);
        return profile;
    }

    // =====================================================
    // PROFILE IMAGES
    // =====================================================

    /**
     * Updates profile image URL.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param imageUrl The new profile image URL
     * @return The updated profile
     */
    public Profile updateProfileImage(Long profileId, Long userId, String imageUrl) {
        logger.info("Updating profile image for profile ID: {} by user: {}", profileId, userId);

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found with ID: " + profileId));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        profile.setProfileImageUrl(cleanUrl(imageUrl));
        profile = profileRepository.save(profile);
        
        logger.info("Successfully updated profile image for profile ID: {}", profileId);
        return profile;
    }

    /**
     * Updates cover image URL.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param imageUrl The new cover image URL
     * @return The updated profile
     */
    public Profile updateCoverImage(Long profileId, Long userId, String imageUrl) {
        logger.info("Updating cover image for profile ID: {} by user: {}", profileId, userId);

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found with ID: " + profileId));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        profile.setCoverImageUrl(cleanUrl(imageUrl));
        profile = profileRepository.save(profile);
        
        logger.info("Successfully updated cover image for profile ID: {}", profileId);
        return profile;
    }

    // =====================================================
    // PROFILE DELETION & DEACTIVATION
    // =====================================================

    /**
     * Soft deletes a profile by marking it as inactive.
     * 
     * @param profileId The profile ID to delete
     * @param userId The user ID requesting deletion
     * @return true if successfully deleted
     */
    public boolean deactivateProfile(Long profileId, Long userId) {
        logger.info("Deactivating profile ID: {} by user: {}", profileId, userId);

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found with ID: " + profileId));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to deactivate this profile");
        }

        profile.setProfileVisibility(ProfileVisibility.PRIVATE);
        profile.setSearchable(false);
        // Note: We're not actually deleting, just hiding the profile
        
        profileRepository.save(profile);
        logger.info("Successfully deactivated profile ID: {}", profileId);
        return true;
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    /**
     * Checks if a profile is accessible to a requesting user based on privacy settings.
     * 
     * @param profile The profile to check
     * @param requestingUserId The user ID requesting access (null for public access)
     * @return true if profile is accessible
     */
    private boolean isProfileAccessible(Profile profile, Long requestingUserId) {
        // Profile owner can always access their own profile
        if (requestingUserId != null && profile.getUser().getId().equals(requestingUserId)) {
            return true;
        }

        // Check visibility settings
        switch (profile.getProfileVisibility()) {
            case PUBLIC:
                return true;
            case PRIVATE:
                return false;
            case CONNECTIONS_ONLY:
                // TODO: Implement connections check when connections feature is added
                return requestingUserId != null;
            default:
                return false;
        }
    }

    /**
     * Cleans and validates URL inputs.
     * 
     * @param url The URL to clean
     * @return Cleaned URL or null if invalid
     */
    private String cleanUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }
        
        String cleanedUrl = url.trim();
        
        // Add https:// if no protocol specified
        if (!cleanedUrl.startsWith("http://") && !cleanedUrl.startsWith("https://")) {
            cleanedUrl = "https://" + cleanedUrl;
        }
        
        // Basic URL validation (you might want to use a more robust validator)
        if (cleanedUrl.length() > 500) {
            throw new IllegalArgumentException("URL too long");
        }
        
        return cleanedUrl;
    }

    /**
     * Gets profile statistics for analytics.
     * 
     * @param profileId The profile ID
     * @return Profile statistics
     */
    @Transactional(readOnly = true)
    public ProfileStats getProfileStats(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found with ID: " + profileId));

        // Delegate to specialized services for detailed stats
        if (profile instanceof VolunteerProfile) {
            return volunteerProfileService.getVolunteerStats((VolunteerProfile) profile);
        } else if (profile instanceof OrganizationProfile) {
            return organizationProfileService.getOrganizationStats((OrganizationProfile) profile);
        }

        // Basic stats for unknown profile types
        return new ProfileStats(
            profile.getId(),
            profile.getDisplayName(),
            profile.getCreatedAt(),
            profile.getUpdatedAt(),
            0, 0, 0  // views, connections, activities
        );
    }

    /**
     * Inner class for profile statistics.
     */
    public static class ProfileStats {
        private final Long profileId;
        private final String displayName;
        private final LocalDateTime createdAt;
        private final LocalDateTime lastUpdated;
        private final int totalViews;
        private final int totalConnections;
        private final int totalActivities;

        public ProfileStats(Long profileId, String displayName, LocalDateTime createdAt, 
                          LocalDateTime lastUpdated, int totalViews, int totalConnections, 
                          int totalActivities) {
            this.profileId = profileId;
            this.displayName = displayName;
            this.createdAt = createdAt;
            this.lastUpdated = lastUpdated;
            this.totalViews = totalViews;
            this.totalConnections = totalConnections;
            this.totalActivities = totalActivities;
        }

        // Getters
        public Long getProfileId() { return profileId; }
        public String getDisplayName() { return displayName; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getLastUpdated() { return lastUpdated; }
        public int getTotalViews() { return totalViews; }
        public int getTotalConnections() { return totalConnections; }
        public int getTotalActivities() { return totalActivities; }
    }
}