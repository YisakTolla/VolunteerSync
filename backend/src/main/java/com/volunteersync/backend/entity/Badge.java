package com.volunteersync.backend.entity;

import com.volunteersync.backend.enums.BadgeType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "badges")
public class Badge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "badge_type", nullable = false)
    private BadgeType badgeType;
    
    @Column(name = "earned_at")
    private LocalDateTime earnedAt = LocalDateTime.now();
    
    @Column(name = "progress_value")
    private Integer progressValue; // Current progress towards badge
    
    @Column(name = "is_featured")
    private Boolean isFeatured = false; // Display on profile
    
    @Column(name = "notes")
    private String notes; // Additional context about earning
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Constructors
    public Badge() {}
    
    public Badge(User user, BadgeType badgeType) {
        this.user = user;
        this.badgeType = badgeType;
    }

    public Badge(User user, BadgeType badgeType, Integer progressValue) {
        this.user = user;
        this.badgeType = badgeType;
        this.progressValue = progressValue;
    }

    // Getters and Setters
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

    public BadgeType getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(BadgeType badgeType) {
        this.badgeType = badgeType;
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
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    // Helper methods
    public String getBadgeName() {
        return badgeType != null ? badgeType.getDisplayName() : "Unknown Badge";
    }

    public String getBadgeDescription() {
        return badgeType != null ? badgeType.getDescription() : "No description available";
    }

    public boolean isCompleted() {
        if (progressValue == null || badgeType == null) {
            return false;
        }
        return progressValue >= badgeType.getRequiredCount();
    }

    public double getProgressPercentage() {
        if (progressValue == null || badgeType == null || badgeType.getRequiredCount() == 0) {
            return 0.0;
        }
        return Math.min(100.0, (progressValue * 100.0) / badgeType.getRequiredCount());
    }

    public void updateProgress(int newProgress) {
        this.progressValue = newProgress;
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}