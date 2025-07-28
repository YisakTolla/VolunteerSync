package com.volunteersync.backend.entity.profile;

import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.entity.enums.ProfileVisibility;
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
    // COMMON PROFILE FIELDS
    // =====================================================
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @Column
    private String phone;
    
    @Column
    private String location;
    
    @Column
    private String website;
    
    @Column
    private String profileImageUrl;
    
    @Column
    private String coverImageUrl;
    
    // Social Media Links
    @Column
    private String linkedinUrl;
    
    @Column
    private String twitterUrl;
    
    @Column
    private String facebookUrl;
    
    @Column
    private String instagramUrl;
    
    // =====================================================
    // MISSING FIELDS NEEDED FOR COMPILATION
    // =====================================================
    
    @Column(name = "display_name", nullable = false)
    private String displayName;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "website_url")
    private String websiteUrl;
    
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // Alias for profileVisibility to match other code references
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility")
    private ProfileVisibility visibility;
    
    // =====================================================
    // PRIVACY & VISIBILITY SETTINGS
    // =====================================================
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProfileVisibility profileVisibility = ProfileVisibility.PUBLIC;
    
    @Column(nullable = false)
    private Boolean showEmail = false;
    
    @Column(nullable = false)
    private Boolean showPhone = false;
    
    @Column(nullable = false)
    private Boolean showLocation = true;
    
    @Column(nullable = false)
    private Boolean allowMessaging = true;
    
    @Column(nullable = false)
    private Boolean showActivity = true;
    
    @Column(nullable = false)
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
        this.visibility = ProfileVisibility.PUBLIC; // Initialize visibility
    }
    
    protected Profile(User user) {
        this.user = user;
        setDefaultPrivacySettings();
    }
    
    // =====================================================
    // ABSTRACT METHODS (must be implemented by subclasses)
    // =====================================================
    
    /**
     * Get the profile type display string
     * @return Profile type with icon (e.g., "🙋‍♀️ Volunteer")
     */
    public abstract String getProfileTypeDisplay();
    
    // =====================================================
    // COMMON HELPER METHODS
    // =====================================================
    
    /**
     * Check if this is a volunteer profile
     * @return true if VolunteerProfile instance
     */
    public boolean isVolunteerProfile() {
        return this instanceof VolunteerProfile;
    }
    
    /**
     * Check if this is an organization profile
     * @return true if OrganizationProfile instance
     */
    public boolean isOrganizationProfile() {
        return this instanceof OrganizationProfile;
    }
    
    /**
     * Check if profile is publicly visible
     * @return true if profile visibility is PUBLIC
     */
    public boolean isPubliclyVisible() {
        return ProfileVisibility.PUBLIC.equals(profileVisibility);
    }
    
    /**
     * Check if profile is completely private
     * @return true if profile visibility is PRIVATE
     */
    public boolean isPrivate() {
        return ProfileVisibility.PRIVATE.equals(profileVisibility);
    }
    
    /**
     * Set default privacy settings for new profiles
     */
    private void setDefaultPrivacySettings() {
        this.profileVisibility = ProfileVisibility.PUBLIC;
        this.visibility = ProfileVisibility.PUBLIC;
        this.showEmail = false;
        this.showPhone = false;
        this.showLocation = true;
        this.allowMessaging = true;
        this.showActivity = true;
        this.searchable = true;
    }
    
    /**
     * Get formatted website URL with protocol
     * @return Website URL with http/https protocol
     */
    public String getFormattedWebsite() {
        String websiteToFormat = website != null ? website : websiteUrl;
        if (websiteToFormat == null || websiteToFormat.trim().isEmpty()) {
            return null;
        }
        
        String trimmedWebsite = websiteToFormat.trim();
        if (!trimmedWebsite.startsWith("http://") && !trimmedWebsite.startsWith("https://")) {
            return "https://" + trimmedWebsite;
        }
        
        return trimmedWebsite;
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
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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
    
    public ProfileVisibility getProfileVisibility() {
        return profileVisibility;
    }
    
    public void setProfileVisibility(ProfileVisibility profileVisibility) {
        this.profileVisibility = profileVisibility;
        this.visibility = profileVisibility; // Keep them in sync
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
    // MISSING GETTERS/SETTERS FOR COMPILATION
    // =====================================================
    
    // Override getDisplayName to return the displayName field if abstract method isn't implemented
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getPhoneNumber() {
        return phoneNumber != null ? phoneNumber : phone;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.phone = phoneNumber; // Keep them in sync
    }
    
    public String getWebsiteUrl() {
        return websiteUrl != null ? websiteUrl : website;
    }
    
    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
        this.website = websiteUrl; // Keep them in sync
    }
    
    public Boolean getIsVerified() {
        return isVerified;
    }
    
    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
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
        return visibility != null ? visibility : profileVisibility;
    }
    
    public void setVisibility(ProfileVisibility visibility) {
        this.visibility = visibility;
        this.profileVisibility = visibility; // Keep them in sync
    }
    
    // =====================================================
    // EQUALS, HASHCODE, AND TOSTRING
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
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", profileType='" + getClass().getSimpleName() + '\'' +
                ", displayName='" + getDisplayName() + '\'' +
                ", location='" + location + '\'' +
                ", profileVisibility=" + profileVisibility +
                ", createdAt=" + createdAt +
                '}';
    }
}