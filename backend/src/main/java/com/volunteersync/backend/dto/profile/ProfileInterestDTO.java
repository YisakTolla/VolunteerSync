package com.volunteersync.backend.dto.profile;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for ProfileInterest entities.
 * Represents interests associated with volunteer profiles, including
 * interest categories, priority levels, and engagement preferences.
 * 
 * This DTO is used for API responses when working with volunteer interests
 * and includes validation for interest management operations.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileInterestDTO {

    // =====================================================
    // BASIC INTEREST INFORMATION
    // =====================================================

    private Long id;

    private Long profileId;

    @NotBlank(message = "Interest name is required")
    @Size(max = 100, message = "Interest name cannot exceed 100 characters")
    private String interestName;

    @Size(max = 50, message = "Interest category cannot exceed 50 characters")
    private String interestCategory; // e.g., "CAUSE_AREA", "ACTIVITY_TYPE", "SKILL_DEVELOPMENT"

    @Size(max = 50, message = "Cause area cannot exceed 50 characters")
    private String causeArea; // e.g., "ENVIRONMENT", "EDUCATION", "HEALTH", "POVERTY", "ANIMALS"

    // =====================================================
    // PRIORITY & PREFERENCES
    // =====================================================

    @Min(value = 1, message = "Priority level must be at least 1")
    @Max(value = 5, message = "Priority level cannot exceed 5")
    private Integer priorityLevel; // 1 = Highest priority, 5 = Lowest priority

    private Boolean isPrimary; // Whether this is a primary interest for the volunteer

    private Boolean isActive; // Whether volunteer is currently interested in opportunities

    private Boolean isVisible; // Whether this interest is visible on public profile

    // =====================================================
    // EXPERIENCE & ENGAGEMENT
    // =====================================================

    @Min(value = 0, message = "Years of involvement cannot be negative")
    private Integer yearsOfInvolvement; // How long they've been involved in this interest area

    @Min(value = 0, message = "Events participated cannot be negative")
    private Integer eventsParticipated; // Number of events related to this interest

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastEngagedAt; // Last time they volunteered in this interest area

    private Boolean hasExperience; // Whether they have prior experience in this area

    @Size(max = 500, message = "Experience description cannot exceed 500 characters")
    private String experienceDescription; // Description of their experience

    // =====================================================
    // INVOLVEMENT PREFERENCES
    // =====================================================

    private String involvementLevel; // "CASUAL", "MODERATE", "DEEP", "LEADERSHIP"

    private String timeCommitment; // "ONE_TIME", "OCCASIONAL", "REGULAR", "INTENSIVE"

    private String preferredRole; // "PARTICIPANT", "ORGANIZER", "LEADER", "SUPPORTER"

    private Boolean willingToLead; // Whether they're willing to lead projects in this area

    private Boolean seekingLearning; // Whether they want to learn more about this interest

    // =====================================================
    // MATCHING & NOTIFICATIONS
    // =====================================================

    private Boolean receiveNotifications; // Whether to receive notifications for related opportunities

    private String notificationFrequency; // "IMMEDIATE", "DAILY", "WEEKLY", "MONTHLY"

    private Integer maxDistanceKm; // Maximum distance willing to travel for this interest

    private String[] preferredDays; // Preferred days for activities related to this interest

    private String[] preferredTimes; // Preferred time slots for this interest

    // =====================================================
    // RELATED INFORMATION
    // =====================================================

    private String[] relatedSkills; // Skills that complement this interest

    private String[] relatedInterests; // Other interests that are related

    private String motivations; // Why they're interested in this area

    private String personalConnection; // Personal connection to this cause/interest

    private String[] organizationTypes; // Types of organizations they prefer for this interest

    // =====================================================
    // METADATA
    // =====================================================

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private String addedBy; // "SELF", "ORGANIZATION", "IMPORT" - how the interest was added

    private String source; // Where this interest came from (e.g., "PROFILE_SETUP", "EVENT_PARTICIPATION")

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public ProfileInterestDTO() {
        // Default constructor for JSON deserialization
    }

    public ProfileInterestDTO(Long id, String interestName, String interestCategory, 
                             Integer priorityLevel, LocalDateTime createdAt) {
        this.id = id;
        this.interestName = interestName;
        this.interestCategory = interestCategory;
        this.priorityLevel = priorityLevel;
        this.createdAt = createdAt;
    }

    public ProfileInterestDTO(Long id, Long profileId, String interestName, String interestCategory,
                             String causeArea, Integer priorityLevel, Boolean isPrimary, 
                             Boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.profileId = profileId;
        this.interestName = interestName;
        this.interestCategory = interestCategory;
        this.causeArea = causeArea;
        this.priorityLevel = priorityLevel;
        this.isPrimary = isPrimary;
        this.isActive = isActive;
        this.createdAt = createdAt;
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

    public String getInterestName() {
        return interestName;
    }

    public void setInterestName(String interestName) {
        this.interestName = interestName;
    }

    public String getInterestCategory() {
        return interestCategory;
    }

    public void setInterestCategory(String interestCategory) {
        this.interestCategory = interestCategory;
    }

    public String getCauseArea() {
        return causeArea;
    }

    public void setCauseArea(String causeArea) {
        this.causeArea = causeArea;
    }

    public Integer getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(Integer priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public Integer getYearsOfInvolvement() {
        return yearsOfInvolvement;
    }

    public void setYearsOfInvolvement(Integer yearsOfInvolvement) {
        this.yearsOfInvolvement = yearsOfInvolvement;
    }

    public Integer getEventsParticipated() {
        return eventsParticipated;
    }

    public void setEventsParticipated(Integer eventsParticipated) {
        this.eventsParticipated = eventsParticipated;
    }

    public LocalDateTime getLastEngagedAt() {
        return lastEngagedAt;
    }

    public void setLastEngagedAt(LocalDateTime lastEngagedAt) {
        this.lastEngagedAt = lastEngagedAt;
    }

    public Boolean getHasExperience() {
        return hasExperience;
    }

    public void setHasExperience(Boolean hasExperience) {
        this.hasExperience = hasExperience;
    }

    public String getExperienceDescription() {
        return experienceDescription;
    }

    public void setExperienceDescription(String experienceDescription) {
        this.experienceDescription = experienceDescription;
    }

    public String getInvolvementLevel() {
        return involvementLevel;
    }

    public void setInvolvementLevel(String involvementLevel) {
        this.involvementLevel = involvementLevel;
    }

    public String getTimeCommitment() {
        return timeCommitment;
    }

    public void setTimeCommitment(String timeCommitment) {
        this.timeCommitment = timeCommitment;
    }

    public String getPreferredRole() {
        return preferredRole;
    }

    public void setPreferredRole(String preferredRole) {
        this.preferredRole = preferredRole;
    }

    public Boolean getWillingToLead() {
        return willingToLead;
    }

    public void setWillingToLead(Boolean willingToLead) {
        this.willingToLead = willingToLead;
    }

    public Boolean getSeekingLearning() {
        return seekingLearning;
    }

    public void setSeekingLearning(Boolean seekingLearning) {
        this.seekingLearning = seekingLearning;
    }

    public Boolean getReceiveNotifications() {
        return receiveNotifications;
    }

    public void setReceiveNotifications(Boolean receiveNotifications) {
        this.receiveNotifications = receiveNotifications;
    }

    public String getNotificationFrequency() {
        return notificationFrequency;
    }

    public void setNotificationFrequency(String notificationFrequency) {
        this.notificationFrequency = notificationFrequency;
    }

    public Integer getMaxDistanceKm() {
        return maxDistanceKm;
    }

    public void setMaxDistanceKm(Integer maxDistanceKm) {
        this.maxDistanceKm = maxDistanceKm;
    }

    public String[] getPreferredDays() {
        return preferredDays;
    }

    public void setPreferredDays(String[] preferredDays) {
        this.preferredDays = preferredDays;
    }

    public String[] getPreferredTimes() {
        return preferredTimes;
    }

    public void setPreferredTimes(String[] preferredTimes) {
        this.preferredTimes = preferredTimes;
    }

    public String[] getRelatedSkills() {
        return relatedSkills;
    }

    public void setRelatedSkills(String[] relatedSkills) {
        this.relatedSkills = relatedSkills;
    }

    public String[] getRelatedInterests() {
        return relatedInterests;
    }

    public void setRelatedInterests(String[] relatedInterests) {
        this.relatedInterests = relatedInterests;
    }

    public String getMotivations() {
        return motivations;
    }

    public void setMotivations(String motivations) {
        this.motivations = motivations;
    }

    public String getPersonalConnection() {
        return personalConnection;
    }

    public void setPersonalConnection(String personalConnection) {
        this.personalConnection = personalConnection;
    }

    public String[] getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(String[] organizationTypes) {
        this.organizationTypes = organizationTypes;
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

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    /**
     * Gets the priority level as a descriptive string.
     */
    public String getPriorityDescription() {
        if (priorityLevel == null) return "Not specified";
        
        return switch (priorityLevel) {
            case 1 -> "Highest Priority";
            case 2 -> "High Priority";
            case 3 -> "Medium Priority";
            case 4 -> "Low Priority";
            case 5 -> "Lowest Priority";
            default -> "Unknown Priority";
        };
    }

    /**
     * Checks if this is a high-priority interest (level 1 or 2).
     */
    public boolean isHighPriority() {
        return priorityLevel != null && priorityLevel <= 2;
    }

    /**
     * Checks if the volunteer is actively seeking opportunities in this interest.
     */
    public boolean isActivelySeeking() {
        return isActive != null && isActive && receiveNotifications != null && receiveNotifications;
    }

    /**
     * Checks if the volunteer has recent engagement in this interest area.
     */
    public boolean hasRecentEngagement() {
        if (lastEngagedAt == null) return false;
        return lastEngagedAt.isAfter(LocalDateTime.now().minusMonths(6));
    }

    /**
     * Checks if the volunteer is suitable for leadership roles in this interest.
     */
    public boolean isSuitableForLeadership() {
        return willingToLead != null && willingToLead && 
               hasExperience != null && hasExperience &&
               (yearsOfInvolvement != null && yearsOfInvolvement >= 1);
    }

    /**
     * Gets the engagement level as a score from 0-100.
     */
    public Integer getEngagementScore() {
        int score = 0;
        
        if (isPrimary != null && isPrimary) score += 20;
        if (isHighPriority()) score += 15;
        if (hasExperience != null && hasExperience) score += 15;
        if (yearsOfInvolvement != null && yearsOfInvolvement > 0) score += Math.min(yearsOfInvolvement * 5, 25);
        if (eventsParticipated != null && eventsParticipated > 0) score += Math.min(eventsParticipated * 2, 15);
        if (hasRecentEngagement()) score += 10;
        
        return Math.min(score, 100);
    }

    /**
     * Gets a formatted involvement description.
     */
    public String getFormattedInvolvement() {
        StringBuilder sb = new StringBuilder();
        
        if (yearsOfInvolvement != null && yearsOfInvolvement > 0) {
            sb.append(yearsOfInvolvement).append(" year");
            if (yearsOfInvolvement > 1) sb.append("s");
            sb.append(" of experience");
        }
        
        if (eventsParticipated != null && eventsParticipated > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(eventsParticipated).append(" event");
            if (eventsParticipated > 1) sb.append("s");
            sb.append(" participated");
        }
        
        return sb.length() > 0 ? sb.toString() : "New to this area";
    }

    @Override
    public String toString() {
        return "ProfileInterestDTO{" +
                "id=" + id +
                ", interestName='" + interestName + '\'' +
                ", interestCategory='" + interestCategory + '\'' +
                ", causeArea='" + causeArea + '\'' +
                ", priorityLevel=" + priorityLevel +
                ", isPrimary=" + isPrimary +
                ", isActive=" + isActive +
                ", yearsOfInvolvement=" + yearsOfInvolvement +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ProfileInterestDTO that = (ProfileInterestDTO) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}