package com.volunteersync.backend.dto.profile;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for ProfileBadge entities.
 * Represents badges and achievements earned by volunteers, including
 * badge details, earning criteria, and display preferences.
 * 
 * This DTO is used for API responses when working with volunteer achievements
 * and includes metadata about how badges were earned and their significance.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileBadgeDTO {

    // =====================================================
    // BASIC BADGE INFORMATION
    // =====================================================

    private Long id;

    private Long profileId;

    @NotBlank(message = "Badge name is required")
    @Size(max = 100, message = "Badge name cannot exceed 100 characters")
    private String badgeName;

    @Size(max = 500, message = "Badge description cannot exceed 500 characters")
    private String badgeDescription;

    private String badgeIcon; // URL or icon identifier for the badge

    private String badgeColor; // Color theme for the badge (hex code or color name)

    @Size(max = 50, message = "Badge category cannot exceed 50 characters")
    private String badgeCategory; // e.g., "ACHIEVEMENT", "MILESTONE", "SKILL", "PARTICIPATION", "LEADERSHIP"

    // =====================================================
    // BADGE CLASSIFICATION & RARITY
    // =====================================================

    private String badgeType; // "SYSTEM", "ORGANIZATION", "EVENT", "CUSTOM"

    private String rarity; // "COMMON", "UNCOMMON", "RARE", "EPIC", "LEGENDARY"

    private String difficulty; // "EASY", "MEDIUM", "HARD", "EXPERT"

    private Integer pointValue; // Point value associated with earning this badge

    private Boolean isStackable; // Whether multiple instances of this badge can be earned

    private Integer stackCount; // Number of times this badge has been earned (if stackable)

    // =====================================================
    // EARNING INFORMATION
    // =====================================================

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime earnedAt;

    private String earnedBy; // How the badge was earned (e.g., "HOURS_COMPLETED", "EVENTS_ATTENDED")

    private String awardedBy; // Who/what awarded the badge (organization name, system, etc.)

    private Long awardedByOrganizationId; // ID of the organization that awarded the badge

    private String earnedFromEventId; // ID of the event where badge was earned (if applicable)

    private String earnedFromEventName; // Name of the event where badge was earned

    // =====================================================
    // CRITERIA & REQUIREMENTS
    // =====================================================

    @Size(max = 500, message = "Earning criteria cannot exceed 500 characters")
    private String earningCriteria; // Description of what was required to earn this badge

    private String requirementsMet; // JSON string of specific requirements that were met

    private Integer requiredValue; // Numeric requirement (e.g., hours, events, etc.)

    private Integer actualValue; // Actual value achieved when earning the badge

    private String verificationMethod; // How the achievement was verified

    private Boolean isVerified; // Whether the badge achievement has been verified

    // =====================================================
    // DISPLAY & VISIBILITY
    // =====================================================

    private Boolean isVisible; // Whether this badge is visible on public profile

    private Boolean isFeatured; // Whether this badge should be prominently displayed

    private Integer displayOrder; // Order in which to display this badge

    private Boolean showEarnedDate; // Whether to show the date when badge was earned

    private Boolean showDetails; // Whether to show detailed information about the badge

    // =====================================================
    // BADGE METADATA
    // =====================================================

    private String badgeVersion; // Version of the badge (in case criteria change over time)

    private Boolean isRetired; // Whether this badge is no longer being awarded

    private Boolean isLimited; // Whether this was a limited-time badge

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiresAt; // If the badge has an expiration date

    private String[] tags; // Tags associated with this badge for categorization

    // =====================================================
    // SOCIAL & SHARING
    // =====================================================

    private Boolean allowSharing; // Whether the volunteer allows sharing this achievement

    private Boolean isSharedOnSocial; // Whether this badge was shared on social media

    private Integer likesCount; // Number of likes/reactions this badge has received

    private Integer sharesCount; // Number of times this badge has been shared

    private String shareMessage; // Custom message when sharing this badge

    // =====================================================
    // PROGRESSION & RELATED BADGES
    // =====================================================

    private String nextBadgeId; // ID of the next badge in a progression series

    private String nextBadgeName; // Name of the next badge to earn

    private String progressToNext; // Progress towards earning the next badge

    private String[] relatedBadges; // IDs of related badges

    private String badgeSeries; // Series or collection this badge belongs to

    private Integer seriesPosition; // Position within a badge series

    private Integer seriesTotal; // Total number of badges in the series

    // =====================================================
    // TIMESTAMPS
    // =====================================================

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastViewedAt; // Last time the volunteer viewed this badge

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public ProfileBadgeDTO() {
        // Default constructor for JSON deserialization
    }

    public ProfileBadgeDTO(Long id, String badgeName, String badgeDescription, String badgeIcon,
                          String badgeCategory, LocalDateTime earnedAt, Boolean isVisible) {
        this.id = id;
        this.badgeName = badgeName;
        this.badgeDescription = badgeDescription;
        this.badgeIcon = badgeIcon;
        this.badgeCategory = badgeCategory;
        this.earnedAt = earnedAt;
        this.isVisible = isVisible;
    }

    public ProfileBadgeDTO(Long id, Long profileId, String badgeName, String badgeDescription,
                          String badgeIcon, String badgeCategory, String rarity, 
                          LocalDateTime earnedAt, String awardedBy, Boolean isVisible) {
        this.id = id;
        this.profileId = profileId;
        this.badgeName = badgeName;
        this.badgeDescription = badgeDescription;
        this.badgeIcon = badgeIcon;
        this.badgeCategory = badgeCategory;
        this.rarity = rarity;
        this.earnedAt = earnedAt;
        this.awardedBy = awardedBy;
        this.isVisible = isVisible;
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

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public String getBadgeDescription() {
        return badgeDescription;
    }

    public void setBadgeDescription(String badgeDescription) {
        this.badgeDescription = badgeDescription;
    }

    public String getBadgeIcon() {
        return badgeIcon;
    }

    public void setBadgeIcon(String badgeIcon) {
        this.badgeIcon = badgeIcon;
    }

    public String getBadgeColor() {
        return badgeColor;
    }

    public void setBadgeColor(String badgeColor) {
        this.badgeColor = badgeColor;
    }

    public String getBadgeCategory() {
        return badgeCategory;
    }

    public void setBadgeCategory(String badgeCategory) {
        this.badgeCategory = badgeCategory;
    }

    public String getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(String badgeType) {
        this.badgeType = badgeType;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getPointValue() {
        return pointValue;
    }

    public void setPointValue(Integer pointValue) {
        this.pointValue = pointValue;
    }

    public Boolean getIsStackable() {
        return isStackable;
    }

    public void setIsStackable(Boolean isStackable) {
        this.isStackable = isStackable;
    }

    public Integer getStackCount() {
        return stackCount;
    }

    public void setStackCount(Integer stackCount) {
        this.stackCount = stackCount;
    }

    public LocalDateTime getEarnedAt() {
        return earnedAt;
    }

    public void setEarnedAt(LocalDateTime earnedAt) {
        this.earnedAt = earnedAt;
    }

    public String getEarnedBy() {
        return earnedBy;
    }

    public void setEarnedBy(String earnedBy) {
        this.earnedBy = earnedBy;
    }

    public String getAwardedBy() {
        return awardedBy;
    }

    public void setAwardedBy(String awardedBy) {
        this.awardedBy = awardedBy;
    }

    public Long getAwardedByOrganizationId() {
        return awardedByOrganizationId;
    }

    public void setAwardedByOrganizationId(Long awardedByOrganizationId) {
        this.awardedByOrganizationId = awardedByOrganizationId;
    }

    public String getEarnedFromEventId() {
        return earnedFromEventId;
    }

    public void setEarnedFromEventId(String earnedFromEventId) {
        this.earnedFromEventId = earnedFromEventId;
    }

    public String getEarnedFromEventName() {
        return earnedFromEventName;
    }

    public void setEarnedFromEventName(String earnedFromEventName) {
        this.earnedFromEventName = earnedFromEventName;
    }

    public String getEarningCriteria() {
        return earningCriteria;
    }

    public void setEarningCriteria(String earningCriteria) {
        this.earningCriteria = earningCriteria;
    }

    public String getRequirementsMet() {
        return requirementsMet;
    }

    public void setRequirementsMet(String requirementsMet) {
        this.requirementsMet = requirementsMet;
    }

    public Integer getRequiredValue() {
        return requiredValue;
    }

    public void setRequiredValue(Integer requiredValue) {
        this.requiredValue = requiredValue;
    }

    public Integer getActualValue() {
        return actualValue;
    }

    public void setActualValue(Integer actualValue) {
        this.actualValue = actualValue;
    }

    public String getVerificationMethod() {
        return verificationMethod;
    }

    public void setVerificationMethod(String verificationMethod) {
        this.verificationMethod = verificationMethod;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getShowEarnedDate() {
        return showEarnedDate;
    }

    public void setShowEarnedDate(Boolean showEarnedDate) {
        this.showEarnedDate = showEarnedDate;
    }

    public Boolean getShowDetails() {
        return showDetails;
    }

    public void setShowDetails(Boolean showDetails) {
        this.showDetails = showDetails;
    }

    public String getBadgeVersion() {
        return badgeVersion;
    }

    public void setBadgeVersion(String badgeVersion) {
        this.badgeVersion = badgeVersion;
    }

    public Boolean getIsRetired() {
        return isRetired;
    }

    public void setIsRetired(Boolean isRetired) {
        this.isRetired = isRetired;
    }

    public Boolean getIsLimited() {
        return isLimited;
    }

    public void setIsLimited(Boolean isLimited) {
        this.isLimited = isLimited;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public Boolean getAllowSharing() {
        return allowSharing;
    }

    public void setAllowSharing(Boolean allowSharing) {
        this.allowSharing = allowSharing;
    }

    public Boolean getIsSharedOnSocial() {
        return isSharedOnSocial;
    }

    public void setIsSharedOnSocial(Boolean isSharedOnSocial) {
        this.isSharedOnSocial = isSharedOnSocial;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public Integer getSharesCount() {
        return sharesCount;
    }

    public void setSharesCount(Integer sharesCount) {
        this.sharesCount = sharesCount;
    }

    public String getShareMessage() {
        return shareMessage;
    }

    public void setShareMessage(String shareMessage) {
        this.shareMessage = shareMessage;
    }

    public String getNextBadgeId() {
        return nextBadgeId;
    }

    public void setNextBadgeId(String nextBadgeId) {
        this.nextBadgeId = nextBadgeId;
    }

    public String getNextBadgeName() {
        return nextBadgeName;
    }

    public void setNextBadgeName(String nextBadgeName) {
        this.nextBadgeName = nextBadgeName;
    }

    public String getProgressToNext() {
        return progressToNext;
    }

    public void setProgressToNext(String progressToNext) {
        this.progressToNext = progressToNext;
    }

    public String[] getRelatedBadges() {
        return relatedBadges;
    }

    public void setRelatedBadges(String[] relatedBadges) {
        this.relatedBadges = relatedBadges;
    }

    public String getBadgeSeries() {
        return badgeSeries;
    }

    public void setBadgeSeries(String badgeSeries) {
        this.badgeSeries = badgeSeries;
    }

    public Integer getSeriesPosition() {
        return seriesPosition;
    }

    public void setSeriesPosition(Integer seriesPosition) {
        this.seriesPosition = seriesPosition;
    }

    public Integer getSeriesTotal() {
        return seriesTotal;
    }

    public void setSeriesTotal(Integer seriesTotal) {
        this.seriesTotal = seriesTotal;
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

    public LocalDateTime getLastViewedAt() {
        return lastViewedAt;
    }

    public void setLastViewedAt(LocalDateTime lastViewedAt) {
        this.lastViewedAt = lastViewedAt;
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    /**
     * Checks if this badge is currently valid (not expired).
     */
    public boolean isCurrentlyValid() {
        if (expiresAt == null) return true;
        return LocalDateTime.now().isBefore(expiresAt);
    }

    /**
     * Checks if this badge is rare or higher (rare, epic, legendary).
     */
    public boolean isRareBadge() {
        if (rarity == null) return false;
        return "RARE".equals(rarity) || "EPIC".equals(rarity) || "LEGENDARY".equals(rarity);
    }

    /**
     * Checks if this badge was earned recently (within last 30 days).
     */
    public boolean isRecentlyEarned() {
        if (earnedAt == null) return false;
        return earnedAt.isAfter(LocalDateTime.now().minusDays(30));
    }

    /**
     * Gets the rarity level as a numeric value for sorting.
     */
    public Integer getRarityLevel() {
        if (rarity == null) return 0;
        
        return switch (rarity) {
            case "COMMON" -> 1;
            case "UNCOMMON" -> 2;
            case "RARE" -> 3;
            case "EPIC" -> 4;
            case "LEGENDARY" -> 5;
            default -> 0;
        };
    }

    /**
     * Gets the display name with stack count if applicable.
     */
    public String getDisplayName() {
        if (isStackable != null && isStackable && stackCount != null && stackCount > 1) {
            return badgeName + " x" + stackCount;
        }
        return badgeName;
    }

    /**
     * Checks if this badge should be prominently displayed.
     */
    public boolean shouldFeature() {
        return isFeatured != null && isFeatured && isVisible != null && isVisible;
    }

    /**
     * Gets the progress percentage within a badge series.
     */
    public Double getSeriesProgress() {
        if (seriesPosition == null || seriesTotal == null || seriesTotal == 0) {
            return null;
        }
        return (seriesPosition.doubleValue() / seriesTotal.doubleValue()) * 100.0;
    }

    /**
     * Checks if this badge is part of a series.
     */
    public boolean isPartOfSeries() {
        return badgeSeries != null && !badgeSeries.trim().isEmpty();
    }

    /**
     * Gets the badge's social engagement score.
     */
    public Integer getSocialEngagement() {
        int engagement = 0;
        if (likesCount != null) engagement += likesCount;
        if (sharesCount != null) engagement += sharesCount * 2; // Shares worth more than likes
        return engagement;
    }

    /**
     * Checks if this badge has expired.
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Gets a formatted description of when the badge was earned.
     */
    public String getFormattedEarnedDate() {
        if (earnedAt == null) return "Date unknown";
        
        // This is a simplified version - in a real app you'd use proper date formatting
        return earnedAt.toLocalDate().toString();
    }

    /**
     * Gets the achievement percentage (actual vs required value).
     */
    public Double getAchievementPercentage() {
        if (requiredValue == null || requiredValue == 0 || actualValue == null) {
            return null;
        }
        return Math.min((actualValue.doubleValue() / requiredValue.doubleValue()) * 100.0, 100.0);
    }

    @Override
    public String toString() {
        return "ProfileBadgeDTO{" +
                "id=" + id +
                ", badgeName='" + badgeName + '\'' +
                ", badgeCategory='" + badgeCategory + '\'' +
                ", rarity='" + rarity + '\'' +
                ", earnedAt=" + earnedAt +
                ", awardedBy='" + awardedBy + '\'' +
                ", isVisible=" + isVisible +
                ", isFeatured=" + isFeatured +
                ", stackCount=" + stackCount +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ProfileBadgeDTO that = (ProfileBadgeDTO) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}