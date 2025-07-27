package com.volunteersync.backend.dto.profile;

import com.volunteersync.backend.entity.enums.ProfileVisibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Base Data Transfer Object for Profile entities.
 * Contains common fields shared across all profile types (volunteer and organization).
 * 
 * This DTO serves as the foundation for more specialized profile DTOs and includes
 * essential profile information like basic details, privacy settings, and metadata.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileDTO {

    // =====================================================
    // BASIC PROFILE INFORMATION
    // =====================================================

    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    private String userType; // "VOLUNTEER" or "ORGANIZATION"

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;

    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;

    private String profileImageUrl;

    private String coverImageUrl;

    // =====================================================
    // PRIVACY & VISIBILITY SETTINGS
    // =====================================================

    @NotNull(message = "Profile visibility is required")
    private ProfileVisibility visibility;

    private Boolean showEmail;

    private Boolean showPhone;

    private Boolean showLocation;

    private Boolean allowMessaging;

    private Boolean showActivity;

    // =====================================================
    // CONTACT INFORMATION
    // =====================================================

    private String website;

    private String phoneNumber;

    private String linkedinUrl;

    private String twitterUrl;

    private String facebookUrl;

    private String instagramUrl;

    // =====================================================
    // METADATA & TIMESTAMPS
    // =====================================================

    private Boolean isActive;

    private Boolean isVerified;

    private Boolean isCompleted;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastActiveAt;

    // =====================================================
    // PROFILE STATISTICS (READ-ONLY)
    // =====================================================

    private Integer profileViews;

    private Integer connectionsCount;

    private Double profileCompletionPercentage;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public ProfileDTO() {
        // Default constructor for JSON deserialization
    }

    public ProfileDTO(Long id, Long userId, String userType, String bio, String location,
                     ProfileVisibility visibility, Boolean isActive, Boolean isVerified,
                     Boolean isCompleted, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.userType = userType;
        this.bio = bio;
        this.location = location;
        this.visibility = visibility;
        this.isActive = isActive;
        this.isVerified = isVerified;
        this.isCompleted = isCompleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

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

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getLastActiveAt() {
        return lastActiveAt;
    }

    public void setLastActiveAt(LocalDateTime lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    public Integer getProfileViews() {
        return profileViews;
    }

    public void setProfileViews(Integer profileViews) {
        this.profileViews = profileViews;
    }

    public Integer getConnectionsCount() {
        return connectionsCount;
    }

    public void setConnectionsCount(Integer connectionsCount) {
        this.connectionsCount = connectionsCount;
    }

    public Double getProfileCompletionPercentage() {
        return profileCompletionPercentage;
    }

    public void setProfileCompletionPercentage(Double profileCompletionPercentage) {
        this.profileCompletionPercentage = profileCompletionPercentage;
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    @Override
    public String toString() {
        return "ProfileDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", userType='" + userType + '\'' +
                ", bio='" + bio + '\'' +
                ", location='" + location + '\'' +
                ", visibility=" + visibility +
                ", isActive=" + isActive +
                ", isVerified=" + isVerified +
                ", isCompleted=" + isCompleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ProfileDTO that = (ProfileDTO) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}