package com.volunteersync.backend.dto.request;

import com.volunteersync.backend.entity.enums.ProfileVisibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

/**
 * Base Data Transfer Object for profile update requests.
 * Contains common fields that can be updated across all profile types.
 * 
 * This request DTO serves as the foundation for more specialized profile update
 * requests and includes validation for common profile fields.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateProfileRequest {

    // =====================================================
    // BASIC PROFILE INFORMATION
    // =====================================================

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;

    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;

    private String profileImageUrl;

    private String coverImageUrl;

    // =====================================================
    // PRIVACY & VISIBILITY SETTINGS
    // =====================================================

    private ProfileVisibility visibility;

    private Boolean showEmail;

    private Boolean showPhone;

    private Boolean showLocation;

    private Boolean allowMessaging;

    private Boolean showActivity;

    // =====================================================
    // CONTACT INFORMATION
    // =====================================================

    @Pattern(regexp = "^(https?://).*", message = "Website must be a valid URL starting with http:// or https://")
    private String website;

    @Pattern(regexp = "^[+]?[1-9]\\d{1,14}$", message = "Phone number must be a valid international format")
    private String phoneNumber;

    @Pattern(regexp = "^(https?://)?(www\\.)?linkedin\\.com/.*", message = "LinkedIn URL must be a valid LinkedIn profile URL")
    private String linkedinUrl;

    @Pattern(regexp = "^(https?://)?(www\\.)?twitter\\.com/.*", message = "Twitter URL must be a valid Twitter profile URL")
    private String twitterUrl;

    @Pattern(regexp = "^(https?://)?(www\\.)?facebook\\.com/.*", message = "Facebook URL must be a valid Facebook profile URL")
    private String facebookUrl;

    @Pattern(regexp = "^(https?://)?(www\\.)?instagram\\.com/.*", message = "Instagram URL must be a valid Instagram profile URL")
    private String instagramUrl;

    // =====================================================
    // PROFILE STATUS
    // =====================================================

    private Boolean isActive;

    private Boolean isCompleted;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public UpdateProfileRequest() {
        // Default constructor for JSON deserialization
    }

    public UpdateProfileRequest(String bio, String location, ProfileVisibility visibility) {
        this.bio = bio;
        this.location = location;
        this.visibility = visibility;
    }

    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public ProfileVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(ProfileVisibility visibility) {
        this.visibility = visibility;
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

    public Boolean getAllowMessaging() {
        return allowMessaging;
    }

    public void setAllowMessaging(Boolean allowMessaging) {
        this.allowMessaging = allowMessaging;
    }

    public Boolean getShowActivity() {
        return showActivity;
    }

    public void setShowActivity(Boolean showActivity) {
        this.showActivity = showActivity;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getTwitterUrl() {
        return twitterUrl;
    }

    public void setTwitterUrl(String twitterUrl) {
        this.twitterUrl = twitterUrl;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    /**
     * Checks if any field in the request has been set (not null).
     */
    public boolean hasAnyUpdates() {
        return bio != null || location != null || profileImageUrl != null || 
               coverImageUrl != null || visibility != null || showEmail != null ||
               showPhone != null || showLocation != null || allowMessaging != null ||
               showActivity != null || website != null || phoneNumber != null ||
               linkedinUrl != null || twitterUrl != null || facebookUrl != null ||
               instagramUrl != null || isActive != null || isCompleted != null;
    }

    /**
     * Checks if privacy-related fields have been updated.
     */
    public boolean hasPrivacyUpdates() {
        return visibility != null || showEmail != null || showPhone != null ||
               showLocation != null || allowMessaging != null || showActivity != null;
    }

    /**
     * Checks if contact information has been updated.
     */
    public boolean hasContactUpdates() {
        return website != null || phoneNumber != null || linkedinUrl != null ||
               twitterUrl != null || facebookUrl != null || instagramUrl != null;
    }

    /**
     * Checks if basic profile information has been updated.
     */
    public boolean hasBasicInfoUpdates() {
        return bio != null || location != null || profileImageUrl != null || coverImageUrl != null;
    }

    /**
     * Validates that URLs are properly formatted if provided.
     */
    public boolean areUrlsValid() {
        return isValidUrl(website) && isValidUrl(linkedinUrl) && 
               isValidUrl(twitterUrl) && isValidUrl(facebookUrl) && isValidUrl(instagramUrl);
    }

    /**
     * Helper method to validate URL format.
     */
    private boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return true; // null/empty URLs are valid (optional fields)
        }
        return url.matches("^(https?://).*");
    }

    /**
     * Sanitizes and normalizes URL inputs.
     */
    public void normalizeUrls() {
        website = normalizeUrl(website);
        linkedinUrl = normalizeUrl(linkedinUrl);
        twitterUrl = normalizeUrl(twitterUrl);
        facebookUrl = normalizeUrl(facebookUrl);
        instagramUrl = normalizeUrl(instagramUrl);
    }

    /**
     * Helper method to normalize URL format.
     */
    private String normalizeUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return url;
        }
        
        url = url.trim();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        
        return url;
    }

    /**
     * Gets a count of how many fields are being updated.
     */
    public int getUpdateCount() {
        int count = 0;
        if (bio != null) count++;
        if (location != null) count++;
        if (profileImageUrl != null) count++;
        if (coverImageUrl != null) count++;
        if (visibility != null) count++;
        if (showEmail != null) count++;
        if (showPhone != null) count++;
        if (showLocation != null) count++;
        if (allowMessaging != null) count++;
        if (showActivity != null) count++;
        if (website != null) count++;
        if (phoneNumber != null) count++;
        if (linkedinUrl != null) count++;
        if (twitterUrl != null) count++;
        if (facebookUrl != null) count++;
        if (instagramUrl != null) count++;
        if (isActive != null) count++;
        if (isCompleted != null) count++;
        
        return count;
    }

    @Override
    public String toString() {
        return "UpdateProfileRequest{" +
                "bio='" + (bio != null ? "[SET]" : "null") + '\'' +
                ", location='" + location + '\'' +
                ", visibility=" + visibility +
                ", showEmail=" + showEmail +
                ", showPhone=" + showPhone +
                ", showLocation=" + showLocation +
                ", allowMessaging=" + allowMessaging +
                ", website='" + website + '\'' +
                ", phoneNumber='" + (phoneNumber != null ? "[SET]" : "null") + '\'' +
                ", isActive=" + isActive +
                ", updateCount=" + getUpdateCount() +
                '}';
    }
}