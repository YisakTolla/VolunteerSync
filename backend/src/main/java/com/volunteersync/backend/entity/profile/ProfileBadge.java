package com.volunteersync.backend.entity.profile;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * Entity representing a badge or achievement earned by a user profile.
 * Badges recognize volunteer contributions, milestones, skills, and other accomplishments
 * to gamify the volunteer experience and showcase user achievements.
 */
@Entity
@Table(name = "profile_badges",
       indexes = {
           @Index(name = "idx_profile_badge_type", columnList = "profile_id, badge_type"),
           @Index(name = "idx_badge_category", columnList = "category"),
           @Index(name = "idx_badge_earned_date", columnList = "earned_at"),
           @Index(name = "idx_badge_rarity", columnList = "rarity_level"),
           @Index(name = "idx_badge_active", columnList = "is_active"),
           @Index(name = "idx_badge_featured", columnList = "is_featured")
       })
public class ProfileBadge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Many-to-One relationship with Profile
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
    
    // =====================================================
    // CORE BADGE INFORMATION
    // =====================================================
    
    @Column(name = "badge_name", nullable = false, length = 100)
    private String badgeName;
    
    @Column(name = "badge_type", nullable = false, length = 50)
    private String badgeType; // "Hours", "Skills", "Impact", "Leadership", "Special", etc.
    
    @Column(length = 50)
    private String category; // "Volunteer", "Skill", "Achievement", "Milestone", "Special Event"
    
    @Column(columnDefinition = "TEXT")
    private String description; // What this badge represents
    
    @Column(columnDefinition = "TEXT")
    private String criteria; // How this badge is earned
    
    // =====================================================
    // VISUAL & DISPLAY
    // =====================================================
    
    @Column
    private String iconUrl; // URL to badge icon/image
    
    @Column
    private String iconEmoji; // Emoji representation of the badge
    
    @Column
    private String colorScheme; // "gold", "silver", "bronze", "blue", "green", etc.
    
    @Column(nullable = false)
    private Boolean isAnimated = false; // Whether badge has animation effects
    
    @Column
    private String badgeDesign; // "Classic", "Modern", "Special", "Custom"
    
    // =====================================================
    // RARITY & VALUE
    // =====================================================
    
    @Enumerated(EnumType.STRING)
    @Column(name = "rarity_level", nullable = false)
    private BadgeRarity rarityLevel = BadgeRarity.COMMON;
    
    @Column(nullable = false)
    private Integer pointValue = 0; // Points awarded for earning this badge
    
    @Column(nullable = false)
    private Integer difficultyLevel = 1; // 1-10 scale of how hard to earn
    
    @Column
    private Double completionRate; // Percentage of users who have this badge
    
    // =====================================================
    // EARNING DETAILS
    // =====================================================
    
    @CreationTimestamp
    @Column(name = "earned_at", nullable = false, updatable = false)
    private LocalDateTime earnedAt;
    
    @Column
    private String awardedBy; // "System", "Administrator", "Organization", specific user ID
    
    @Column(columnDefinition = "TEXT")
    private String achievementDetails; // Specific details about how it was earned
    
    @Column
    private String relatedActivity; // What activity/event led to earning this badge
    
    @Column
    private String organizationName; // Organization associated with earning this badge
    
    // =====================================================
    // MILESTONE & PROGRESS
    // =====================================================
    
    @Column
    private Integer milestoneValue; // The milestone reached (e.g., 100 hours, 50 events)
    
    @Column
    private String milestoneUnit; // "hours", "events", "organizations", "skills", etc.
    
    @Column
    private LocalDateTime milestoneDate; // When the milestone was reached
    
    @Column(nullable = false)
    private Boolean isProgressive = false; // Can be earned multiple times (Bronze → Silver → Gold)
    
    @Column
    private Integer progressionLevel; // For progressive badges (1, 2, 3, etc.)
    
    @Column
    private String nextLevelBadge; // Name of next level badge
    
    // =====================================================
    // VERIFICATION & AUTHENTICITY
    // =====================================================
    
    @Column(nullable = false)
    private Boolean isVerified = true; // Whether badge earning has been verified
    
    @Column
    private String verificationMethod; // "Automatic", "Manual Review", "External API"
    
    @Column
    private LocalDateTime verifiedAt;
    
    @Column
    private String verificationNotes; // Additional verification details
    
    @Column
    private String badgeId; // Unique identifier for this badge type
    
    // =====================================================
    // VISIBILITY & SHARING
    // =====================================================
    
    @Column(nullable = false)
    private Boolean isPublic = true; // Whether this badge is visible to others
    
    @Column(nullable = false)
    private Boolean isActive = true; // Whether this badge is currently active
    
    @Column(nullable = false)
    private Boolean isFeatured = false; // Whether to highlight this badge on profile
    
    @Column(nullable = false)
    private Boolean shareOnSocial = false; // User chose to share this badge on social media
    
    @Column
    private LocalDateTime sharedAt; // When badge was shared
    
    // =====================================================
    // ENGAGEMENT & RECOGNITION
    // =====================================================
    
    @Column(nullable = false)
    private Integer viewCount = 0; // How many times this badge has been viewed
    
    @Column(nullable = false)
    private Integer likeCount = 0; // How many "likes" this badge has received
    
    @Column(nullable = false)
    private Integer shareCount = 0; // How many times this badge has been shared
    
    @Column
    private LocalDateTime lastViewedAt; // Last time someone viewed this badge
    
    // =====================================================
    // EXPIRATION & RENEWAL
    // =====================================================
    
    @Column
    private LocalDateTime expiresAt; // When this badge expires (for time-limited badges)
    
    @Column(nullable = false)
    private Boolean canExpire = false; // Whether this badge can expire
    
    @Column
    private String renewalCriteria; // How to renew an expired badge
    
    @Column(nullable = false)
    private Boolean autoRenewal = false; // Whether badge auto-renews if criteria still met
    
    // =====================================================
    // SPECIAL PROPERTIES
    // =====================================================
    
    @Column(nullable = false)
    private Boolean isLimited = false; // Limited edition badge
    
    @Column
    private Integer limitedQuantity; // Total number available (for limited badges)
    
    @Column
    private Integer editionNumber; // Which number badge this is (e.g., #42 of 100)
    
    @Column
    private String specialOccasion; // "Anniversary", "Holiday", "Event", etc.
    
    @Column(columnDefinition = "TEXT")
    private String metadata; // JSON for additional badge-specific data
    
    // =====================================================
    // ENUMS
    // =====================================================
    
    public enum BadgeRarity {
        COMMON("Common", 1, "#8B7355", "Easily earned by most volunteers"),
        UNCOMMON("Uncommon", 2, "#A0A0A0", "Requires some effort and dedication"),
        RARE("Rare", 3, "#FFD700", "Significant achievement requiring commitment"),
        EPIC("Epic", 4, "#9966CC", "Exceptional accomplishment by dedicated volunteers"),
        LEGENDARY("Legendary", 5, "#FF6B35", "Extraordinary achievement by outstanding individuals");
        
        private final String displayName;
        private final int level;
        private final String colorCode;
        private final String description;
        
        BadgeRarity(String displayName, int level, String colorCode, String description) {
            this.displayName = displayName;
            this.level = level;
            this.colorCode = colorCode;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public int getLevel() { return level; }
        public String getColorCode() { return colorCode; }
        public String getDescription() { return description; }
    }
    
    // =====================================================
    // CONSTRUCTORS
    // =====================================================
    
    public ProfileBadge() {
        // Default constructor for JPA
    }
    
    public ProfileBadge(Profile profile, String badgeName, String badgeType) {
        this.profile = profile;
        this.badgeName = badgeName;
        this.badgeType = badgeType;
        setDefaults();
    }
    
    public ProfileBadge(Profile profile, String badgeName, String badgeType, BadgeRarity rarityLevel) {
        this.profile = profile;
        this.badgeName = badgeName;
        this.badgeType = badgeType;
        this.rarityLevel = rarityLevel;
        setDefaults();
    }
    
    // =====================================================
    // HELPER METHODS
    // =====================================================
    
    /**
     * Set default values for new badge entries
     */
    private void setDefaults() {
        this.rarityLevel = BadgeRarity.COMMON;
        this.pointValue = 0;
        this.difficultyLevel = 1;
        this.isAnimated = false;
        this.isProgressive = false;
        this.isVerified = true;
        this.isPublic = true;
        this.isActive = true;
        this.isFeatured = false;
        this.shareOnSocial = false;
        this.viewCount = 0;
        this.likeCount = 0;
        this.shareCount = 0;
        this.canExpire = false;
        this.autoRenewal = false;
        this.isLimited = false;
        this.verificationMethod = "Automatic";
    }
    
    /**
     * Get display text for badge rarity
     * @return User-friendly rarity description
     */
    public String getRarityDisplay() {
        if (rarityLevel != null) {
            return rarityLevel.getDisplayName();
        }
        return "Common";
    }
    
    /**
     * Get rarity color for UI display
     * @return Hex color code for the rarity level
     */
    public String getRarityColor() {
        if (rarityLevel != null) {
            return rarityLevel.getColorCode();
        }
        return "#8B7355"; // Default to common color
    }
    
    /**
     * Check if this is a rare or higher badge
     * @return true if rarity is RARE, EPIC, or LEGENDARY
     */
    public boolean isHighRarity() {
        return rarityLevel != null && rarityLevel.getLevel() >= BadgeRarity.RARE.getLevel();
    }
    
    /**
     * Check if badge is currently expired
     * @return true if badge has expired
     */
    public boolean isExpired() {
        return Boolean.TRUE.equals(canExpire) && 
               expiresAt != null && 
               expiresAt.isBefore(LocalDateTime.now());
    }
    
    /**
     * Check if badge is expiring soon (within 30 days)
     * @return true if badge expires within 30 days
     */
    public boolean isExpiringSoon() {
        return Boolean.TRUE.equals(canExpire) && 
               expiresAt != null && 
               expiresAt.isBefore(LocalDateTime.now().plusDays(30));
    }
    
    /**
     * Calculate badge prestige score based on rarity, difficulty, and uniqueness
     * @return Prestige score (0-100)
     */
    public int calculatePrestigeScore() {
        int score = 0;
        
        // Base score from rarity
        if (rarityLevel != null) {
            score += rarityLevel.getLevel() * 15; // 15, 30, 45, 60, 75
        }
        
        // Difficulty bonus
        if (difficultyLevel != null) {
            score += difficultyLevel * 2; // Up to 20 points
        }
        
        // Limited edition bonus
        if (Boolean.TRUE.equals(isLimited)) {
            score += 10;
        }
        
        // Progressive badge bonus (higher levels are more prestigious)
        if (Boolean.TRUE.equals(isProgressive) && progressionLevel != null) {
            score += progressionLevel * 3; // Bonus for higher progression levels
        }
        
        // Verification bonus
        if (Boolean.TRUE.equals(isVerified)) {
            score += 5;
        }
        
        return Math.min(score, 100); // Cap at 100
    }
    
    /**
     * Get formatted badge display with emoji and name
     * @return Formatted badge string for UI
     */
    public String getFormattedDisplay() {
        StringBuilder display = new StringBuilder();
        
        if (iconEmoji != null && !iconEmoji.trim().isEmpty()) {
            display.append(iconEmoji).append(" ");
        }
        
        display.append(badgeName);
        
        if (Boolean.TRUE.equals(isProgressive) && progressionLevel != null) {
            display.append(" (Level ").append(progressionLevel).append(")");
        }
        
        if (Boolean.TRUE.equals(isLimited) && editionNumber != null) {
            display.append(" #").append(editionNumber);
            if (limitedQuantity != null) {
                display.append("/").append(limitedQuantity);
            }
        }
        
        return display.toString();
    }
    
    /**
     * Record a view of this badge
     */
    public void recordView() {
        if (this.viewCount == null) {
            this.viewCount = 0;
        }
        this.viewCount++;
        this.lastViewedAt = LocalDateTime.now();
    }
    
    /**
     * Record a like for this badge
     */
    public void recordLike() {
        if (this.likeCount == null) {
            this.likeCount = 0;
        }
        this.likeCount++;
    }
    
    /**
     * Record a share of this badge
     */
    public void recordShare() {
        if (this.shareCount == null) {
            this.shareCount = 0;
        }
        this.shareCount++;
        this.sharedAt = LocalDateTime.now();
    }
    
    /**
     * Check if badge can be renewed
     * @return true if badge is expired and can be renewed
     */
    public boolean canBeRenewed() {
        return isExpired() && renewalCriteria != null && !renewalCriteria.trim().isEmpty();
    }
    
    /**
     * Get next progression level badge name
     * @return Name of next level badge or null if at max level
     */
    public String getNextProgressionLevel() {
        if (Boolean.TRUE.equals(isProgressive)) {
            return nextLevelBadge;
        }
        return null;
    }
    
    /**
     * Get engagement rate (likes + shares / views)
     * @return Engagement rate as percentage
     */
    public double getEngagementRate() {
        if (viewCount == null || viewCount == 0) {
            return 0.0;
        }
        
        int engagements = (likeCount != null ? likeCount : 0) + 
                         (shareCount != null ? shareCount : 0);
        
        return (double) engagements / viewCount * 100;
    }
    
    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) { this.profile = profile; }
    
    public String getBadgeName() { return badgeName; }
    public void setBadgeName(String badgeName) { this.badgeName = badgeName; }
    
    public String getBadgeType() { return badgeType; }
    public void setBadgeType(String badgeType) { this.badgeType = badgeType; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCriteria() { return criteria; }
    public void setCriteria(String criteria) { this.criteria = criteria; }
    
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    
    public String getIconEmoji() { return iconEmoji; }
    public void setIconEmoji(String iconEmoji) { this.iconEmoji = iconEmoji; }
    
    public String getColorScheme() { return colorScheme; }
    public void setColorScheme(String colorScheme) { this.colorScheme = colorScheme; }
    
    public Boolean getIsAnimated() { return isAnimated; }
    public void setIsAnimated(Boolean isAnimated) { this.isAnimated = isAnimated; }
    
    public String getBadgeDesign() { return badgeDesign; }
    public void setBadgeDesign(String badgeDesign) { this.badgeDesign = badgeDesign; }
    
    public BadgeRarity getRarityLevel() { return rarityLevel; }
    public void setRarityLevel(BadgeRarity rarityLevel) { this.rarityLevel = rarityLevel; }
    
    public Integer getPointValue() { return pointValue; }
    public void setPointValue(Integer pointValue) { this.pointValue = pointValue; }
    
    public Integer getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(Integer difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    
    public Double getCompletionRate() { return completionRate; }
    public void setCompletionRate(Double completionRate) { this.completionRate = completionRate; }
    
    public LocalDateTime getEarnedAt() { return earnedAt; }
    public void setEarnedAt(LocalDateTime earnedAt) { this.earnedAt = earnedAt; }
    
    public String getAwardedBy() { return awardedBy; }
    public void setAwardedBy(String awardedBy) { this.awardedBy = awardedBy; }
    
    public String getAchievementDetails() { return achievementDetails; }
    public void setAchievementDetails(String achievementDetails) { this.achievementDetails = achievementDetails; }
    
    public String getRelatedActivity() { return relatedActivity; }
    public void setRelatedActivity(String relatedActivity) { this.relatedActivity = relatedActivity; }
    
    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
    
    public Integer getMilestoneValue() { return milestoneValue; }
    public void setMilestoneValue(Integer milestoneValue) { this.milestoneValue = milestoneValue; }
    
    public String getMilestoneUnit() { return milestoneUnit; }
    public void setMilestoneUnit(String milestoneUnit) { this.milestoneUnit = milestoneUnit; }
    
    public LocalDateTime getMilestoneDate() { return milestoneDate; }
    public void setMilestoneDate(LocalDateTime milestoneDate) { this.milestoneDate = milestoneDate; }
    
    public Boolean getIsProgressive() { return isProgressive; }
    public void setIsProgressive(Boolean isProgressive) { this.isProgressive = isProgressive; }
    
    public Integer getProgressionLevel() { return progressionLevel; }
    public void setProgressionLevel(Integer progressionLevel) { this.progressionLevel = progressionLevel; }
    
    public String getNextLevelBadge() { return nextLevelBadge; }
    public void setNextLevelBadge(String nextLevelBadge) { this.nextLevelBadge = nextLevelBadge; }
    
    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    
    public String getVerificationMethod() { return verificationMethod; }
    public void setVerificationMethod(String verificationMethod) { this.verificationMethod = verificationMethod; }
    
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
    
    public String getVerificationNotes() { return verificationNotes; }
    public void setVerificationNotes(String verificationNotes) { this.verificationNotes = verificationNotes; }
    
    public String getBadgeId() { return badgeId; }
    public void setBadgeId(String badgeId) { this.badgeId = badgeId; }
    
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Boolean getIsFeatured() { return isFeatured; }
    public void setIsFeatured(Boolean isFeatured) { this.isFeatured = isFeatured; }
    
    public Boolean getShareOnSocial() { return shareOnSocial; }
    public void setShareOnSocial(Boolean shareOnSocial) { this.shareOnSocial = shareOnSocial; }
    
    public LocalDateTime getSharedAt() { return sharedAt; }
    public void setSharedAt(LocalDateTime sharedAt) { this.sharedAt = sharedAt; }
    
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    
    public Integer getShareCount() { return shareCount; }
    public void setShareCount(Integer shareCount) { this.shareCount = shareCount; }
    
    public LocalDateTime getLastViewedAt() { return lastViewedAt; }
    public void setLastViewedAt(LocalDateTime lastViewedAt) { this.lastViewedAt = lastViewedAt; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public Boolean getCanExpire() { return canExpire; }
    public void setCanExpire(Boolean canExpire) { this.canExpire = canExpire; }
    
    public String getRenewalCriteria() { return renewalCriteria; }
    public void setRenewalCriteria(String renewalCriteria) { this.renewalCriteria = renewalCriteria; }
    
    public Boolean getAutoRenewal() { return autoRenewal; }
    public void setAutoRenewal(Boolean autoRenewal) { this.autoRenewal = autoRenewal; }
    
    public Boolean getIsLimited() { return isLimited; }
    public void setIsLimited(Boolean isLimited) { this.isLimited = isLimited; }
    
    public Integer getLimitedQuantity() { return limitedQuantity; }
    public void setLimitedQuantity(Integer limitedQuantity) { this.limitedQuantity = limitedQuantity; }
    
    public Integer getEditionNumber() { return editionNumber; }
    public void setEditionNumber(Integer editionNumber) { this.editionNumber = editionNumber; }
    
    public String getSpecialOccasion() { return specialOccasion; }
    public void setSpecialOccasion(String specialOccasion) { this.specialOccasion = specialOccasion; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
    // =====================================================
    // EQUALS, HASHCODE, AND TOSTRING
    // =====================================================
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ProfileBadge that = (ProfileBadge) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "ProfileBadge{" +
                "id=" + id +
                ", badgeName='" + badgeName + '\'' +
                ", badgeType='" + badgeType + '\'' +
                ", rarityLevel=" + rarityLevel +
                ", pointValue=" + pointValue +
                ", earnedAt=" + earnedAt +
                ", isVerified=" + isVerified +
                '}';
    }
}