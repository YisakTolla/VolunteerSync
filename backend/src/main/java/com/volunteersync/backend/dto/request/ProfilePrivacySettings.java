package com.volunteersync.backend.dto.request;

import com.volunteersync.backend.entity.enums.ProfileVisibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Data Transfer Object for profile privacy settings updates.
 * Contains comprehensive privacy and visibility controls for user profiles
 * including what information is visible to different audiences.
 * 
 * This request DTO allows users to fine-tune their privacy preferences
 * across all aspects of their profile visibility.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfilePrivacySettings {

    // =====================================================
    // CORE VISIBILITY SETTINGS
    // =====================================================

    @NotNull(message = "Profile visibility is required")
    private ProfileVisibility profileVisibility; // PUBLIC, PRIVATE, CONNECTIONS_ONLY

    private Boolean profileSearchable; // Whether profile appears in search results

    private Boolean allowProfileViewing; // Whether others can view the profile

    private Boolean showInDirectory; // Whether to appear in volunteer/organization directory

    // =====================================================
    // CONTACT INFORMATION VISIBILITY
    // =====================================================

    private Boolean showEmail; // Show email address on profile

    private Boolean showPhone; // Show phone number on profile

    private Boolean showLocation; // Show location information

    private Boolean showSocialLinks; // Show social media links

    private Boolean showWebsite; // Show website URL

    // =====================================================
    // PROFILE CONTENT VISIBILITY
    // =====================================================

    private Boolean showBio; // Show bio/description

    private Boolean showSkills; // Show skills list

    private Boolean showInterests; // Show interests list

    private Boolean showExperience; // Show experience level and history

    private Boolean showBadges; // Show earned badges and achievements

    private Boolean showVolunteerHours; // Show total volunteer hours

    private Boolean showActivity; // Show recent activity and participation

    private Boolean showReviews; // Show reviews and ratings

    // =====================================================
    // ORGANIZATION-SPECIFIC VISIBILITY
    // =====================================================

    private Boolean showVerificationStatus; // Show organization verification status

    private Boolean showImpactMetrics; // Show impact statistics

    private Boolean showFinancialInfo; // Show budget and financial information

    private Boolean showVolunteerCount; // Show number of volunteers

    private Boolean showPartnerOrganizations; // Show partner organizations

    // =====================================================
    // COMMUNICATION & MESSAGING
    // =====================================================

    private Boolean allowDirectMessaging; // Allow direct messages from other users

    private Boolean allowOrganizationContact; // Allow organizations to contact (for volunteers)

    private Boolean allowVolunteerContact; // Allow volunteers to contact (for organizations)

    private Boolean allowEventInvitations; // Allow event invitations

    private Boolean allowConnectionRequests; // Allow connection/follow requests

    // =====================================================
    // SEARCH & DISCOVERY SETTINGS
    // =====================================================

    private Boolean showInRecommendations; // Appear in recommendation lists

    private Boolean allowSkillMatching; // Allow matching based on skills

    private Boolean allowInterestMatching; // Allow matching based on interests

    private Boolean allowLocationBasedMatching; // Allow location-based recommendations

    private Boolean showInSimilarProfiles; // Appear in "similar profiles" suggestions

    // =====================================================
    // NOTIFICATION & COMMUNICATION PREFERENCES
    // =====================================================

    private Boolean receiveMatchNotifications; // Receive notifications about potential matches

    private Boolean receiveEventNotifications; // Receive event-related notifications

    private Boolean receiveNewsletters; // Receive platform newsletters

    private Boolean receivePromotionalEmails; // Receive promotional content

    private Boolean receiveSmsNotifications; // Receive SMS notifications

    // =====================================================
    // DATA SHARING & ANALYTICS
    // =====================================================

    private Boolean allowDataAnalytics; // Allow profile data for platform analytics

    private Boolean allowThirdPartySharing; // Allow sharing with partner organizations

    private Boolean allowResearchParticipation; // Allow participation in research studies

    private Boolean allowMarketingCommunications; // Allow marketing communications

    // =====================================================
    // AUDIENCE-SPECIFIC SETTINGS
    // =====================================================

    private Boolean visibleToPublic; // Visible to non-registered users

    private Boolean visibleToRegisteredUsers; // Visible to registered platform users

    private Boolean visibleToConnections; // Visible to connections/followers only

    private Boolean visibleToOrganizations; // Visible to verified organizations

    private Boolean visibleToVolunteers; // Visible to other volunteers

    // =====================================================
    // BLOCKING & RESTRICTIONS
    // =====================================================

    private List<String> blockedUserEmails; // List of blocked user emails

    private List<String> blockedOrganizations; // List of blocked organization names

    private Boolean blockUnverifiedOrganizations; // Block contact from unverified organizations

    private Boolean blockNewUserAccounts; // Block contact from newly created accounts

    // =====================================================
    // PROFILE COMPLETION & VERIFICATION
    // =====================================================

    private Boolean requireVerificationToContact; // Require verification to contact this profile

    private Boolean showVerificationBadge; // Display verification badge if verified

    private Boolean highlightVerifiedStatus; // Prominently display verification status

    // =====================================================
    // CUSTOM PRIVACY RULES
    // =====================================================

    private String customVisibilityRules; // JSON string for custom visibility rules

    private Integer profileViewLimit; // Maximum profile views per day from same user

    private Boolean logProfileViews; // Whether to log who views the profile

    private Boolean notifyOnProfileView; // Notify when profile is viewed

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public ProfilePrivacySettings() {
        // Default constructor for JSON deserialization
    }

    public ProfilePrivacySettings(ProfileVisibility profileVisibility) {
        this.profileVisibility = profileVisibility;
        setDefaultsBasedOnVisibility();
    }

    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================

    public ProfileVisibility getProfileVisibility() {
        return profileVisibility;
    }

    public void setProfileVisibility(ProfileVisibility profileVisibility) {
        this.profileVisibility = profileVisibility;
    }

    public Boolean getProfileSearchable() {
        return profileSearchable;
    }

    public void setProfileSearchable(Boolean profileSearchable) {
        this.profileSearchable = profileSearchable;
    }

    public Boolean getAllowProfileViewing() {
        return allowProfileViewing;
    }

    public void setAllowProfileViewing(Boolean allowProfileViewing) {
        this.allowProfileViewing = allowProfileViewing;
    }

    public Boolean getShowInDirectory() {
        return showInDirectory;
    }

    public void setShowInDirectory(Boolean showInDirectory) {
        this.showInDirectory = showInDirectory;
    }

    public Boolean getShowEmail() {
        return showEmail;
    }

    public void setShowEmail(Boolean showEmail) {
        this.showEmail = showEmail;
    }

    public Boolean getShowPhone() {
        return showPhone;
    }

    public void setShowPhone(Boolean showPhone) {
        this.showPhone = showPhone;
    }

    public Boolean getShowLocation() {
        return showLocation;
    }

    public void setShowLocation(Boolean showLocation) {
        this.showLocation = showLocation;
    }

    public Boolean getShowSocialLinks() {
        return showSocialLinks;
    }

    public void setShowSocialLinks(Boolean showSocialLinks) {
        this.showSocialLinks = showSocialLinks;
    }

    public Boolean getShowWebsite() {
        return showWebsite;
    }

    public void setShowWebsite(Boolean showWebsite) {
        this.showWebsite = showWebsite;
    }

    public Boolean getShowBio() {
        return showBio;
    }

    public void setShowBio(Boolean showBio) {
        this.showBio = showBio;
    }

    public Boolean getShowSkills() {
        return showSkills;
    }

    public void setShowSkills(Boolean showSkills) {
        this.showSkills = showSkills;
    }

    public Boolean getShowInterests() {
        return showInterests;
    }

    public void setShowInterests(Boolean showInterests) {
        this.showInterests = showInterests;
    }

    public Boolean getShowExperience() {
        return showExperience;
    }

    public void setShowExperience(Boolean showExperience) {
        this.showExperience = showExperience;
    }

    public Boolean getShowBadges() {
        return showBadges;
    }

    public void setShowBadges(Boolean showBadges) {
        this.showBadges = showBadges;
    }

    public Boolean getShowVolunteerHours() {
        return showVolunteerHours;
    }

    public void setShowVolunteerHours(Boolean showVolunteerHours) {
        this.showVolunteerHours = showVolunteerHours;
    }

    public Boolean getShowActivity() {
        return showActivity;
    }

    public void setShowActivity(Boolean showActivity) {
        this.showActivity = showActivity;
    }

    public Boolean getShowReviews() {
        return showReviews;
    }

    public void setShowReviews(Boolean showReviews) {
        this.showReviews = showReviews;
    }

    public Boolean getShowVerificationStatus() {
        return showVerificationStatus;
    }

    public void setShowVerificationStatus(Boolean showVerificationStatus) {
        this.showVerificationStatus = showVerificationStatus;
    }

    public Boolean getShowImpactMetrics() {
        return showImpactMetrics;
    }

    public void setShowImpactMetrics(Boolean showImpactMetrics) {
        this.showImpactMetrics = showImpactMetrics;
    }

    public Boolean getShowFinancialInfo() {
        return showFinancialInfo;
    }

    public void setShowFinancialInfo(Boolean showFinancialInfo) {
        this.showFinancialInfo = showFinancialInfo;
    }

    public Boolean getShowVolunteerCount() {
        return showVolunteerCount;
    }

    public void setShowVolunteerCount(Boolean showVolunteerCount) {
        this.showVolunteerCount = showVolunteerCount;
    }

    public Boolean getShowPartnerOrganizations() {
        return showPartnerOrganizations;
    }

    public void setShowPartnerOrganizations(Boolean showPartnerOrganizations) {
        this.showPartnerOrganizations = showPartnerOrganizations;
    }

    public Boolean getAllowDirectMessaging() {
        return allowDirectMessaging;
    }

    public void setAllowDirectMessaging(Boolean allowDirectMessaging) {
        this.allowDirectMessaging = allowDirectMessaging;
    }

    public Boolean getAllowOrganizationContact() {
        return allowOrganizationContact;
    }

    public void setAllowOrganizationContact(Boolean allowOrganizationContact) {
        this.allowOrganizationContact = allowOrganizationContact;
    }

    public Boolean getAllowVolunteerContact() {
        return allowVolunteerContact;
    }

    public void setAllowVolunteerContact(Boolean allowVolunteerContact) {
        this.allowVolunteerContact = allowVolunteerContact;
    }

    public Boolean getAllowEventInvitations() {
        return allowEventInvitations;
    }

    public void setAllowEventInvitations(Boolean allowEventInvitations) {
        this.allowEventInvitations = allowEventInvitations;
    }

    public Boolean getAllowConnectionRequests() {
        return allowConnectionRequests;
    }

    public void setAllowConnectionRequests(Boolean allowConnectionRequests) {
        this.allowConnectionRequests = allowConnectionRequests;
    }

    public Boolean getShowInRecommendations() {
        return showInRecommendations;
    }

    public void setShowInRecommendations(Boolean showInRecommendations) {
        this.showInRecommendations = showInRecommendations;
    }

    public Boolean getAllowSkillMatching() {
        return allowSkillMatching;
    }

    public void setAllowSkillMatching(Boolean allowSkillMatching) {
        this.allowSkillMatching = allowSkillMatching;
    }

    public Boolean getAllowInterestMatching() {
        return allowInterestMatching;
    }

    public void setAllowInterestMatching(Boolean allowInterestMatching) {
        this.allowInterestMatching = allowInterestMatching;
    }

    public Boolean getAllowLocationBasedMatching() {
        return allowLocationBasedMatching;
    }

    public void setAllowLocationBasedMatching(Boolean allowLocationBasedMatching) {
        this.allowLocationBasedMatching = allowLocationBasedMatching;
    }

    public Boolean getShowInSimilarProfiles() {
        return showInSimilarProfiles;
    }

    public void setShowInSimilarProfiles(Boolean showInSimilarProfiles) {
        this.showInSimilarProfiles = showInSimilarProfiles;
    }

    public Boolean getReceiveMatchNotifications() {
        return receiveMatchNotifications;
    }

    public void setReceiveMatchNotifications(Boolean receiveMatchNotifications) {
        this.receiveMatchNotifications = receiveMatchNotifications;
    }

    public Boolean getReceiveEventNotifications() {
        return receiveEventNotifications;
    }

    public void setReceiveEventNotifications(Boolean receiveEventNotifications) {
        this.receiveEventNotifications = receiveEventNotifications;
    }

    public Boolean getReceiveNewsletters() {
        return receiveNewsletters;
    }

    public void setReceiveNewsletters(Boolean receiveNewsletters) {
        this.receiveNewsletters = receiveNewsletters;
    }

    public Boolean getReceivePromotionalEmails() {
        return receivePromotionalEmails;
    }

    public void setReceivePromotionalEmails(Boolean receivePromotionalEmails) {
        this.receivePromotionalEmails = receivePromotionalEmails;
    }

    public Boolean getReceiveSmsNotifications() {
        return receiveSmsNotifications;
    }

    public void setReceiveSmsNotifications(Boolean receiveSmsNotifications) {
        this.receiveSmsNotifications = receiveSmsNotifications;
    }

    public Boolean getAllowDataAnalytics() {
        return allowDataAnalytics;
    }

    public void setAllowDataAnalytics(Boolean allowDataAnalytics) {
        this.allowDataAnalytics = allowDataAnalytics;
    }

    public Boolean getAllowThirdPartySharing() {
        return allowThirdPartySharing;
    }

    public void setAllowThirdPartySharing(Boolean allowThirdPartySharing) {
        this.allowThirdPartySharing = allowThirdPartySharing;
    }

    public Boolean getAllowResearchParticipation() {
        return allowResearchParticipation;
    }

    public void setAllowResearchParticipation(Boolean allowResearchParticipation) {
        this.allowResearchParticipation = allowResearchParticipation;
    }

    public Boolean getAllowMarketingCommunications() {
        return allowMarketingCommunications;
    }

    public void setAllowMarketingCommunications(Boolean allowMarketingCommunications) {
        this.allowMarketingCommunications = allowMarketingCommunications;
    }

    public Boolean getVisibleToPublic() {
        return visibleToPublic;
    }

    public void setVisibleToPublic(Boolean visibleToPublic) {
        this.visibleToPublic = visibleToPublic;
    }

    public Boolean getVisibleToRegisteredUsers() {
        return visibleToRegisteredUsers;
    }

    public void setVisibleToRegisteredUsers(Boolean visibleToRegisteredUsers) {
        this.visibleToRegisteredUsers = visibleToRegisteredUsers;
    }

    public Boolean getVisibleToConnections() {
        return visibleToConnections;
    }

    public void setVisibleToConnections(Boolean visibleToConnections) {
        this.visibleToConnections = visibleToConnections;
    }

    public Boolean getVisibleToOrganizations() {
        return visibleToOrganizations;
    }

    public void setVisibleToOrganizations(Boolean visibleToOrganizations) {
        this.visibleToOrganizations = visibleToOrganizations;
    }

    public Boolean getVisibleToVolunteers() {
        return visibleToVolunteers;
    }

    public void setVisibleToVolunteers(Boolean visibleToVolunteers) {
        this.visibleToVolunteers = visibleToVolunteers;
    }

    public List<String> getBlockedUserEmails() {
        return blockedUserEmails;
    }

    public void setBlockedUserEmails(List<String> blockedUserEmails) {
        this.blockedUserEmails = blockedUserEmails;
    }

    public List<String> getBlockedOrganizations() {
        return blockedOrganizations;
    }

    public void setBlockedOrganizations(List<String> blockedOrganizations) {
        this.blockedOrganizations = blockedOrganizations;
    }

    public Boolean getBlockUnverifiedOrganizations() {
        return blockUnverifiedOrganizations;
    }

    public void setBlockUnverifiedOrganizations(Boolean blockUnverifiedOrganizations) {
        this.blockUnverifiedOrganizations = blockUnverifiedOrganizations;
    }

    public Boolean getBlockNewUserAccounts() {
        return blockNewUserAccounts;
    }

    public void setBlockNewUserAccounts(Boolean blockNewUserAccounts) {
        this.blockNewUserAccounts = blockNewUserAccounts;
    }

    public Boolean getRequireVerificationToContact() {
        return requireVerificationToContact;
    }

    public void setRequireVerificationToContact(Boolean requireVerificationToContact) {
        this.requireVerificationToContact = requireVerificationToContact;
    }

    public Boolean getShowVerificationBadge() {
        return showVerificationBadge;
    }

    public void setShowVerificationBadge(Boolean showVerificationBadge) {
        this.showVerificationBadge = showVerificationBadge;
    }

    public Boolean getHighlightVerifiedStatus() {
        return highlightVerifiedStatus;
    }

    public void setHighlightVerifiedStatus(Boolean highlightVerifiedStatus) {
        this.highlightVerifiedStatus = highlightVerifiedStatus;
    }

    public String getCustomVisibilityRules() {
        return customVisibilityRules;
    }

    public void setCustomVisibilityRules(String customVisibilityRules) {
        this.customVisibilityRules = customVisibilityRules;
    }

    public Integer getProfileViewLimit() {
        return profileViewLimit;
    }

    public void setProfileViewLimit(Integer profileViewLimit) {
        this.profileViewLimit = profileViewLimit;
    }

    public Boolean getLogProfileViews() {
        return logProfileViews;
    }

    public void setLogProfileViews(Boolean logProfileViews) {
        this.logProfileViews = logProfileViews;
    }

    public Boolean getNotifyOnProfileView() {
        return notifyOnProfileView;
    }

    public void setNotifyOnProfileView(Boolean notifyOnProfileView) {
        this.notifyOnProfileView = notifyOnProfileView;
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    /**
     * Sets default privacy settings based on the profile visibility level.
     */
    public void setDefaultsBasedOnVisibility() {
        if (profileVisibility == null) return;

        switch (profileVisibility) {
            case PUBLIC -> setPublicDefaults();
            case PRIVATE -> setPrivateDefaults();
            case CONNECTIONS_ONLY -> setConnectionsOnlyDefaults();
        }
    }

    /**
     * Sets defaults for public profiles (most permissive).
     */
    private void setPublicDefaults() {
        profileSearchable = true;
        allowProfileViewing = true;
        showInDirectory = true;
        showBio = true;
        showSkills = true;
        showInterests = true;
        showExperience = true;
        showBadges = true;
        allowDirectMessaging = true;
        allowConnectionRequests = true;
        showInRecommendations = true;
        allowSkillMatching = true;
        allowInterestMatching = true;
        visibleToPublic = true;
        visibleToRegisteredUsers = true;
        
        // Still keep some things private by default
        showEmail = false;
        showPhone = false;
        showFinancialInfo = false;
    }

    /**
     * Sets defaults for private profiles (most restrictive).
     */
    private void setPrivateDefaults() {
        profileSearchable = false;
        allowProfileViewing = false;
        showInDirectory = false;
        showEmail = false;
        showPhone = false;
        showLocation = false;
        showBio = false;
        showSkills = false;
        showInterests = false;
        showExperience = false;
        showBadges = false;
        showActivity = false;
        allowDirectMessaging = false;
        allowConnectionRequests = false;
        showInRecommendations = false;
        allowSkillMatching = false;
        allowInterestMatching = false;
        visibleToPublic = false;
        visibleToRegisteredUsers = false;
        visibleToConnections = true; // Only visible to connections
    }

    /**
     * Sets defaults for connections-only profiles (moderate restrictions).
     */
    private void setConnectionsOnlyDefaults() {
        profileSearchable = true;
        allowProfileViewing = true;
        showInDirectory = false;
        showBio = true;
        showSkills = true;
        showInterests = true;
        showExperience = true;
        showBadges = true;
        allowDirectMessaging = true;
        allowConnectionRequests = true;
        showInRecommendations = true;
        allowSkillMatching = true;
        allowInterestMatching = true;
        visibleToPublic = false;
        visibleToRegisteredUsers = true;
        visibleToConnections = true;
        
        // Keep contact info private
        showEmail = false;
        showPhone = false;
    }

    /**
     * Checks if the profile is discoverable through search and recommendations.
     */
    public boolean isDiscoverable() {
        return (profileSearchable != null && profileSearchable) ||
               (showInRecommendations != null && showInRecommendations) ||
               (showInDirectory != null && showInDirectory);
    }

    /**
     * Checks if the profile allows any form of communication.
     */
    public boolean allowsCommunication() {
        return (allowDirectMessaging != null && allowDirectMessaging) ||
               (allowOrganizationContact != null && allowOrganizationContact) ||
               (allowVolunteerContact != null && allowVolunteerContact) ||
               (allowEventInvitations != null && allowEventInvitations);
    }

    /**
     * Checks if the profile participates in matching algorithms.
     */
    public boolean participatesInMatching() {
        return (allowSkillMatching != null && allowSkillMatching) ||
               (allowInterestMatching != null && allowInterestMatching) ||
               (allowLocationBasedMatching != null && allowLocationBasedMatching);
    }

    /**
     * Gets a privacy level score (0-100, where 0 is most private, 100 is most public).
     */
    public int getPrivacyScore() {
        int publicSettings = 0;
        int totalSettings = 0;

        // Count visibility settings
        if (profileSearchable != null) { totalSettings++; if (profileSearchable) publicSettings++; }
        if (showInDirectory != null) { totalSettings++; if (showInDirectory) publicSettings++; }
        if (showBio != null) { totalSettings++; if (showBio) publicSettings++; }
        if (showSkills != null) { totalSettings++; if (showSkills) publicSettings++; }
        if (showInterests != null) { totalSettings++; if (showInterests) publicSettings++; }
        if (showExperience != null) { totalSettings++; if (showExperience) publicSettings++; }
        if (showBadges != null) { totalSettings++; if (showBadges) publicSettings++; }
        if (allowDirectMessaging != null) { totalSettings++; if (allowDirectMessaging) publicSettings++; }
        if (allowConnectionRequests != null) { totalSettings++; if (allowConnectionRequests) publicSettings++; }
        if (showInRecommendations != null) { totalSettings++; if (showInRecommendations) publicSettings++; }

        return totalSettings > 0 ? (publicSettings * 100) / totalSettings : 0;
    }

    /**
     * Validates that blocked lists contain valid email addresses.
     */
    public boolean areBlockedListsValid() {
        return isEmailListValid(blockedUserEmails);
    }

    /**
     * Helper method to validate email list.
     */
    private boolean isEmailListValid(List<String> emails) {
        if (emails == null) return true;
        
        return emails.stream()
                .allMatch(email -> email != null && 
                         email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"));
    }

    /**
     * Gets a summary of the current privacy configuration.
     */
    public String getPrivacySummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Visibility: ").append(profileVisibility);
        summary.append(", Discoverable: ").append(isDiscoverable());
        summary.append(", Communications: ").append(allowsCommunication());
        summary.append(", Matching: ").append(participatesInMatching());
        summary.append(", Privacy Score: ").append(getPrivacyScore()).append("%");
        
        return summary.toString();
    }

    @Override
    public String toString() {
        return "ProfilePrivacySettings{" +
                "profileVisibility=" + profileVisibility +
                ", profileSearchable=" + profileSearchable +
                ", showInDirectory=" + showInDirectory +
                ", allowDirectMessaging=" + allowDirectMessaging +
                ", showBio=" + showBio +
                ", showSkills=" + showSkills +
                ", showInterests=" + showInterests +
                ", privacyScore=" + getPrivacyScore() + "%" +
                '}';
    }
}