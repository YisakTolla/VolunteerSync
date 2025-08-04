package com.volunteersync.backend.dto;

import com.volunteersync.backend.enums.BadgeType;
import java.time.LocalDateTime;

public class BadgeDTO {
    private Long id;
    private Long userId;
    private BadgeType badgeType;
    private String badgeName;
    private String badgeDescription;
    private String badgeIcon;
    private String badgeCategory;
    private String difficultyLevel;
    private LocalDateTime earnedAt;
    private Integer progressValue;
    private Integer requiredCount;
    private Double progressPercentage;
    private Integer remainingProgress;
    private Boolean isFeatured;
    private Boolean isCompleted;
    private String notes;
    private String timeSinceEarned;

    // Constructors
    public BadgeDTO() {
    }

    public BadgeDTO(Long id, BadgeType badgeType, LocalDateTime earnedAt,
            Integer progressValue, Boolean isFeatured) {
        this.id = id;
        this.badgeType = badgeType;
        this.earnedAt = earnedAt;
        this.progressValue = progressValue;
        this.isFeatured = isFeatured;

        if (badgeType != null) {
            this.badgeName = badgeType.getDisplayName();
            this.badgeDescription = badgeType.getDescription();
            this.badgeIcon = badgeType.getIcon();
            this.badgeCategory = badgeType.getCategory();
            this.difficultyLevel = badgeType.getDifficultyLevel();
            this.requiredCount = badgeType.getRequiredCount();
            this.isCompleted = progressValue != null && progressValue >= requiredCount;
            this.progressPercentage = calculateProgressPercentage();
            this.remainingProgress = calculateRemainingProgress();
        }
    }

    // Getters and Setters
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

    public BadgeType getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(BadgeType badgeType) {
        this.badgeType = badgeType;
        if (badgeType != null) {
            this.badgeName = badgeType.getDisplayName();
            this.badgeDescription = badgeType.getDescription();
            this.badgeIcon = badgeType.getIcon();
            this.badgeCategory = badgeType.getCategory();
            this.difficultyLevel = badgeType.getDifficultyLevel();
            this.requiredCount = badgeType.getRequiredCount();
            this.progressPercentage = calculateProgressPercentage();
            this.remainingProgress = calculateRemainingProgress();
            this.isCompleted = progressValue != null && progressValue >= requiredCount;
        }
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

    public String getBadgeCategory() {
        return badgeCategory;
    }

    public void setBadgeCategory(String badgeCategory) {
        this.badgeCategory = badgeCategory;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public LocalDateTime getEarnedAt() {
        return earnedAt;
    }

    public void setEarnedAt(LocalDateTime earnedAt) {
        this.earnedAt = earnedAt;
    }

    public Integer getProgressValue() {
        return progressValue;
    }

    public void setProgressValue(Integer progressValue) {
        this.progressValue = progressValue;
        this.progressPercentage = calculateProgressPercentage();
        this.remainingProgress = calculateRemainingProgress();
        this.isCompleted = requiredCount != null && progressValue != null && progressValue >= requiredCount;
    }

    public Integer getRequiredCount() {
        return requiredCount;
    }

    public void setRequiredCount(Integer requiredCount) {
        this.requiredCount = requiredCount;
    }

    public Double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    private Double calculateProgressPercentage() {
        if (progressValue == null || requiredCount == null || requiredCount == 0)
            return 0.0;
        return Math.min(100.0, (progressValue * 100.0) / requiredCount);
    }

    public Integer getRemainingProgress() {
        return remainingProgress;
    }

    public void setRemainingProgress(Integer remainingProgress) {
        this.remainingProgress = remainingProgress;
    }

    private Integer calculateRemainingProgress() {
        if (progressValue == null || requiredCount == null)
            return requiredCount != null ? requiredCount : 0;
        return Math.max(0, requiredCount - progressValue);
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTimeSinceEarned() {
        return timeSinceEarned;
    }

    public void setTimeSinceEarned(String timeSinceEarned) {
        this.timeSinceEarned = timeSinceEarned;
    }
}