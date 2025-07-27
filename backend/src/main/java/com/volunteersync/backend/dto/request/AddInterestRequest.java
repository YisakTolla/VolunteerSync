package com.volunteersync.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for adding interests to volunteer profiles.
 * Contains all necessary information to create a new ProfileInterest entity
 * including interest details, priority levels, and engagement preferences.
 * 
 * This request DTO includes comprehensive validation for interest creation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddInterestRequest {

    // =====================================================
    // REQUIRED INTEREST INFORMATION
    // =====================================================

    @NotBlank(message = "Interest name is required")
    @Size(max = 100, message = "Interest name cannot exceed 100 characters")
    private String interestName;

    // =====================================================
    // OPTIONAL INTEREST DETAILS
    // =====================================================

    @Size(max = 50, message = "Interest category cannot exceed 50 characters")
    private String interestCategory; // e.g., "CAUSE_AREA", "ACTIVITY_TYPE", "SKILL_DEVELOPMENT"

    @Size(max = 50, message = "Cause area cannot exceed 50 characters")
    private String causeArea; // e.g., "ENVIRONMENT", "EDUCATION", "HEALTH", "POVERTY", "ANIMALS"

    @Size(max = 500, message = "Interest description cannot exceed 500 characters")
    private String description;

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
    // EXPERIENCE & BACKGROUND
    // =====================================================

    @Min(value = 0, message = "Years of involvement cannot be negative")
    @Max(value = 100, message = "Years of involvement cannot exceed 100")
    private Integer yearsOfInvolvement; // How long they've been involved in this interest area

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

    @Min(value = 0, message = "Max distance cannot be negative")
    @Max(value = 10000, message = "Max distance cannot exceed 10,000 km")
    private Integer maxDistanceKm; // Maximum distance willing to travel for this interest

    private String[] preferredDays; // Preferred days for activities related to this interest

    private String[] preferredTimes; // Preferred time slots for this interest

    // =====================================================
    // PERSONAL CONNECTION & MOTIVATION
    // =====================================================

    @Size(max = 1000, message = "Motivations cannot exceed 1000 characters")
    private String motivations; // Why they're interested in this area

    @Size(max = 500, message = "Personal connection cannot exceed 500 characters")
    private String personalConnection; // Personal connection to this cause/interest

    private String[] organizationTypes; // Types of organizations they prefer for this interest

    // =====================================================
    // RELATED INFORMATION
    // =====================================================

    private String[] relatedSkills; // Skills that complement this interest

    private String[] relatedInterests; // Other interests that are related

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public AddInterestRequest() {
        // Default constructor for JSON deserialization
    }

    public AddInterestRequest(String interestName) {
        this.interestName = interestName;
    }

    public AddInterestRequest(String interestName, String interestCategory, 
                             String causeArea, Integer priorityLevel) {
        this.interestName = interestName;
        this.interestCategory = interestCategory;
        this.causeArea = causeArea;
        this.priorityLevel = priorityLevel;
    }

    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
     * Validates that the interest name is not just whitespace.
     */
    public boolean isValidInterestName() {
        return interestName != null && !interestName.trim().isEmpty();
    }

    /**
     * Normalizes the interest name by trimming and capitalizing properly.
     */
    public void normalizeInterestName() {
        if (interestName != null) {
            interestName = interestName.trim();
            // Capitalize first letter of each word
            String[] words = interestName.toLowerCase().split("\\s+");
            StringBuilder normalized = new StringBuilder();
            for (String word : words) {
                if (word.length() > 0) {
                    if (normalized.length() > 0) normalized.append(" ");
                    normalized.append(Character.toUpperCase(word.charAt(0)));
                    if (word.length() > 1) {
                        normalized.append(word.substring(1));
                    }
                }
            }
            interestName = normalized.toString();
        }
    }

    /**
     * Sets default values for optional fields if not provided.
     */
    public void setDefaults() {
        if (isVisible == null) {
            isVisible = true; // Default to visible
        }
        if (isPrimary == null) {
            isPrimary = false; // Default to not primary
        }
        if (isActive == null) {
            isActive = true; // Default to active
        }
        if (receiveNotifications == null) {
            receiveNotifications = true; // Default to receiving notifications
        }
        if (priorityLevel == null) {
            priorityLevel = 3; // Default to medium priority
        }
        if (hasExperience == null) {
            hasExperience = yearsOfInvolvement != null && yearsOfInvolvement > 0;
        }
        if (seekingLearning == null) {
            seekingLearning = !hasExperience; // Default to seeking learning if no experience
        }
        if (willingToLead == null) {
            willingToLead = hasExperience && yearsOfInvolvement != null && yearsOfInvolvement >= 2;
        }
        if (interestCategory == null || interestCategory.trim().isEmpty()) {
            interestCategory = "GENERAL"; // Default category
        }
        if (involvementLevel == null || involvementLevel.trim().isEmpty()) {
            involvementLevel = hasExperience ? "MODERATE" : "CASUAL"; // Default based on experience
        }
        if (timeCommitment == null || timeCommitment.trim().isEmpty()) {
            timeCommitment = "OCCASIONAL"; // Default time commitment
        }
        if (preferredRole == null || preferredRole.trim().isEmpty()) {
            preferredRole = willingToLead ? "LEADER" : "PARTICIPANT"; // Default based on leadership willingness
        }
        if (notificationFrequency == null || notificationFrequency.trim().isEmpty()) {
            notificationFrequency = "WEEKLY"; // Default notification frequency
        }
    }

    /**
     * Validates that all array fields contain non-empty values.
     */
    public boolean areArrayFieldsValid() {
        return isArrayValid(preferredDays) && isArrayValid(preferredTimes) &&
               isArrayValid(organizationTypes) && isArrayValid(relatedSkills) &&
               isArrayValid(relatedInterests);
    }

    /**
     * Helper method to validate array field.
     */
    private boolean isArrayValid(String[] array) {
        if (array == null) return true; // null arrays are valid
        
        for (String item : array) {
            if (item == null || item.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Cleans up array fields by removing null/empty values.
     */
    public void cleanArrayFields() {
        preferredDays = cleanArray(preferredDays);
        preferredTimes = cleanArray(preferredTimes);
        organizationTypes = cleanArray(organizationTypes);
        relatedSkills = cleanArray(relatedSkills);
        relatedInterests = cleanArray(relatedInterests);
    }

    /**
     * Helper method to clean array field.
     */
    private String[] cleanArray(String[] array) {
        if (array == null) return null;
        
        return java.util.Arrays.stream(array)
                .filter(item -> item != null && !item.trim().isEmpty())
                .map(String::trim)
                .toArray(String[]::new);
    }

    /**
     * Checks if the volunteer is seeking active involvement in this interest.
     */
    public boolean isSeekingActiveInvolvement() {
        return isActive != null && isActive && receiveNotifications != null && receiveNotifications;
    }

    /**
     * Gets the number of preferred days.
     */
    public int getPreferredDaysCount() {
        return preferredDays != null ? preferredDays.length : 0;
    }

    /**
     * Gets the number of related skills.
     */
    public int getRelatedSkillsCount() {
        return relatedSkills != null ? relatedSkills.length : 0;
    }

    /**
     * Checks if this interest has personal motivations or connections.
     */
    public boolean hasPersonalContext() {
        return (motivations != null && !motivations.trim().isEmpty()) ||
               (personalConnection != null && !personalConnection.trim().isEmpty());
    }

    /**
     * Gets a summary of the interest configuration.
     */
    public String getInterestSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(interestName);
        
        if (causeArea != null && !causeArea.trim().isEmpty()) {
            summary.append(" (").append(causeArea).append(")");
        }
        
        if (priorityLevel != null) {
            summary.append(" - Priority: ").append(getPriorityDescription());
        }
        
        if (yearsOfInvolvement != null && yearsOfInvolvement > 0) {
            summary.append(" - ").append(yearsOfInvolvement).append(" years experience");
        }
        
        return summary.toString();
    }

    @Override
    public String toString() {
        return "AddInterestRequest{" +
                "interestName='" + interestName + '\'' +
                ", interestCategory='" + interestCategory + '\'' +
                ", causeArea='" + causeArea + '\'' +
                ", priorityLevel=" + priorityLevel +
                ", isPrimary=" + isPrimary +
                ", isActive=" + isActive +
                ", hasExperience=" + hasExperience +
                ", yearsOfInvolvement=" + yearsOfInvolvement +
                ", willingToLead=" + willingToLead +
                ", seekingLearning=" + seekingLearning +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AddInterestRequest that = (AddInterestRequest) obj;
        return interestName != null && interestName.equals(that.interestName) &&
               java.util.Objects.equals(causeArea, that.causeArea);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(interestName, causeArea);
    }
}