package com.volunteersync.backend.entity.profile;

import com.volunteersync.backend.entity.enums.ProfileVisibility;
import com.volunteersync.backend.entity.user.User;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

/**
 * Abstract base class for all profile types.
 * Contains common fields shared by VolunteerProfile and OrganizationProfile.
 * Uses single table inheritance strategy with discriminator column.
 */
@Entity
@Table(name = "profiles")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "profile_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Profile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // One-to-One relationship with User
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    // =====================================================
    // BASIC PROFILE INFORMATION
    // =====================================================
    
    @Column(name = "display_name", nullable = false)
    private String displayName;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @Column
    private String phone;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column
    private String location;
    
    @Column
    private String website;
    
    @Column(name = "website_url")
    private String websiteUrl;
    
    @Column
    private String profileImageUrl;
    
    @Column
    private String coverImageUrl;
    
    // =====================================================
    // SOCIAL MEDIA LINKS
    // =====================================================
    
    @Column
    private String linkedinUrl;
    
    @Column
    private String twitterUrl;
    
    @Column
    private String facebookUrl;
    
    @Column
    private String instagramUrl;
    
    // =====================================================
    // VERIFICATION & STATUS
    // =====================================================
    
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    
    @Column(name = "verification_status")
    private String verificationStatus = "UNVERIFIED";
    
    @Column(name = "verification_submitted_at")
    private LocalDateTime verificationSubmittedAt;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // =====================================================
    // PRIVACY SETTINGS
    // =====================================================
    
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private ProfileVisibility visibility = ProfileVisibility.PUBLIC;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "profile_visibility", nullable = false)
    private ProfileVisibility profileVisibility = ProfileVisibility.PUBLIC;
    
    @Column(name = "show_email")
    private Boolean showEmail = false;
    
    @Column(name = "show_phone")
    private Boolean showPhone = false;
    
    @Column(name = "show_location")
    private Boolean showLocation = true;
    
    @Column(name = "show_social_media")
    private Boolean showSocialMedia = true;
    
    @Column(name = "allow_messages")
    private Boolean allowMessages = true;
    
    @Column(name = "allow_messaging")
    private Boolean allowMessaging = true;
    
    @Column(name = "allow_connections")
    private Boolean allowConnections = true;
    
    @Column(name = "show_activity")
    private Boolean showActivity = true;
    
    @Column(name = "searchable")
    private Boolean searchable = true;
    
    // =====================================================
    // TIMESTAMPS
    // =====================================================
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // =====================================================
    // CONSTRUCTORS
    // =====================================================
    
    protected Profile() {
        // Default constructor for JPA
    }
    
    protected Profile(User user) {
        this.user = user;
        // Set display name based on user type
        if (user.isOrganization()) {
            this.displayName = user.getOrganizationName() != null ? user.getOrganizationName() : "Organization";
        } else {
            this.displayName = user.getFirstName() + " " + user.getLastName();
        }
        setDefaults();
    }
    
    protected Profile(User user, String displayName) {
        this.user = user;
        this.displayName = displayName;
        setDefaults();
    }
    
    private void setDefaults() {
        if (this.isVerified == null) this.isVerified = false;
        if (this.isActive == null) this.isActive = true;
        if (this.isDeleted == null) this.isDeleted = false;
        if (this.visibility == null) this.visibility = ProfileVisibility.PUBLIC;
        if (this.profileVisibility == null) this.profileVisibility = ProfileVisibility.PUBLIC;
        if (this.showEmail == null) this.showEmail = false;
        if (this.showPhone == null) this.showPhone = false;
        if (this.showLocation == null) this.showLocation = true;
        if (this.showSocialMedia == null) this.showSocialMedia = true;
        if (this.allowMessages == null) this.allowMessages = true;
        if (this.allowMessaging == null) this.allowMessaging = true;
        if (this.allowConnections == null) this.allowConnections = true;
        if (this.showActivity == null) this.showActivity = true;
        if (this.searchable == null) this.searchable = true;
        if (this.verificationStatus == null) this.verificationStatus = "UNVERIFIED";
    }
    
    // =====================================================
    // ABSTRACT METHODS (must be implemented by subclasses)
    // =====================================================
    
    /**
     * Get profile type as string
     */
    public abstract String getProfileType();
    
    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public String getWebsiteUrl() {
        return websiteUrl;
    }
    
    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
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
    
    public Boolean getIsVerified() {
        return isVerified;
    }
    
    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }
    
    public String getVerificationStatus() {
        return verificationStatus;
    }
    
    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }
    
    public LocalDateTime getVerificationSubmittedAt() {
        return verificationSubmittedAt;
    }
    
    public void setVerificationSubmittedAt(LocalDateTime verificationSubmittedAt) {
        this.verificationSubmittedAt = verificationSubmittedAt;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
    
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
    
    public ProfileVisibility getVisibility() {
        return visibility;
    }
    
    public void setVisibility(ProfileVisibility visibility) {
        this.visibility = visibility;
    }
    
    public ProfileVisibility getProfileVisibility() {
        return profileVisibility;
    }
    
    public void setProfileVisibility(ProfileVisibility profileVisibility) {
        this.profileVisibility = profileVisibility;
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
    
    public Boolean getShowSocialMedia() {
        return showSocialMedia;
    }
    
    public void setShowSocialMedia(Boolean showSocialMedia) {
        this.showSocialMedia = showSocialMedia;
    }
    
    public Boolean getAllowMessages() {
        return allowMessages;
    }
    
    public void setAllowMessages(Boolean allowMessages) {
        this.allowMessages = allowMessages;
    }
    
    public Boolean getAllowMessaging() {
        return allowMessaging;
    }
    
    public void setAllowMessaging(Boolean allowMessaging) {
        this.allowMessaging = allowMessaging;
    }
    
    public Boolean getAllowConnections() {
        return allowConnections;
    }
    
    public void setAllowConnections(Boolean allowConnections) {
        this.allowConnections = allowConnections;
    }
    
    public Boolean getShowActivity() {
        return showActivity;
    }
    
    public void setShowActivity(Boolean showActivity) {
        this.showActivity = showActivity;
    }
    
    public Boolean getSearchable() {
        return searchable;
    }
    
    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
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
    
    // =====================================================
    // BUSINESS LOGIC METHODS
    // =====================================================
    
    /**
     * Check if profile is publicly viewable
     */
    public boolean isPubliclyViewable() {
        return isActive && !isDeleted && visibility == ProfileVisibility.PUBLIC;
    }
    
    /**
     * Check if profile is viewable by a specific user
     */
    public boolean isViewableBy(User user) {
        if (!isActive || isDeleted) return false;
        
        switch (visibility) {
            case PUBLIC:
                return true;
            case PRIVATE:
                return user != null && user.equals(this.user);
            case CONNECTIONS_ONLY:
                // This would need connection checking logic
                return user != null && user.equals(this.user);
            default:
                return false;
        }
    }
    
    /**
     * Soft delete the profile
     */
    public void softDelete() {
        this.isDeleted = true;
        this.isActive = false;
        this.deletedAt = LocalDateTime.now();
    }
    
    /**
     * Restore the profile from soft delete
     */
    public void restore() {
        this.isDeleted = false;
        this.isActive = true;
        this.deletedAt = null;
    }
    
    // =====================================================
    // OBJECT METHODS
    // =====================================================
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Profile profile = (Profile) o;
        return id != null && id.equals(profile.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", displayName='" + displayName + '\'' +
                ", visibility=" + visibility +
                ", isActive=" + isActive +
                ", isDeleted=" + isDeleted +
                '}';
    }
}