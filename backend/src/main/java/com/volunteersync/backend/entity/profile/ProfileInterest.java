package com.volunteersync.backend.entity.profile;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

/**
 * Entity representing an interest or cause area associated with a user profile.
 * Interests help match volunteers with organizations and opportunities that align with their passions,
 * values, and preferred areas of contribution.
 */
@Entity
@Table(name = "profile_interests",
       indexes = {
           @Index(name = "idx_profile_interest_name", columnList = "profile_id, interest_name"),
           @Index(name = "idx_interest_category", columnList = "category"),
           @Index(name = "idx_interest_priority", columnList = "priority_level"),
           @Index(name = "idx_interest_active", columnList = "is_active"),
           @Index(name = "idx_interest_public", columnList = "is_public")
       },
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"profile_id", "interest_name"})
       })
public class ProfileInterest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Many-to-One relationship with Profile
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
    
    // =====================================================
    // CORE INTEREST INFORMATION
    // =====================================================
    
    @Column(name = "interest_name", nullable = false, length = 100)
    private String interestName;
    
    @Column(length = 50)
    private String category; // "Environment", "Education", "Health", "Social Justice", "Arts", etc.
    
    @Column(columnDefinition = "TEXT")
    private String description; // Why this interest is important to the user
    
    @Enumerated(EnumType.STRING)
    @Column(name = "priority_level", nullable = false)
    private InterestPriority priorityLevel = InterestPriority.MEDIUM;
    
    // =====================================================
    // EXPERIENCE & BACKGROUND
    // =====================================================
    
    @Column
    private Integer yearsOfInterest; // How long they've been interested in this cause
    
    @Column(nullable = false)
    private Boolean hasExperience = false; // Has prior experience in this area
    
    @Column(columnDefinition = "TEXT")
    private String experienceDescription; // Description of their experience
    
    @Column(nullable = false)
    private Boolean activelyInvolved = false; // Currently volunteering/working in this area
    
    @Column(columnDefinition = "TEXT")
    private String currentInvolvement; // Current organizations or activities
    
    @Column(nullable = false)
    private Integer totalHoursContributed = 0; // Total hours spent on this interest area
    
    // =====================================================
    // PERSONAL CONNECTION & MOTIVATION
    // =====================================================
    
    @Column(columnDefinition = "TEXT")
    private String personalStory; // Personal connection or story related to this interest
    
    @Column(nullable = false)
    private Boolean shareStory = false; // Willing to share personal story publicly
    
    @Column
    private String inspirationSource; // What inspired this interest
    
    @Column(columnDefinition = "TEXT")
    private String specificGoals; // What they hope to achieve in this interest area
    
    @Column(columnDefinition = "TEXT")
    private String impactAchieved; // Impact or outcomes they've achieved
    
    // =====================================================
    // ENGAGEMENT PREFERENCES
    // =====================================================
    
    @Column(nullable = false)
    private Boolean willingToLearn = true; // Open to learning more about this area
    
    @Column(nullable = false)
    private Boolean willingToLead = false; // Interested in leadership roles
    
    @Column(nullable = false)
    private Boolean willingToTravel = false; // Will travel for opportunities in this area
    
    @Column
    private Integer maxTravelDistance; // Maximum distance willing to travel (in miles)
    
    @Column(nullable = false)
    private Boolean willingToTeach = false; // Can educate others about this cause
    
    @Column(nullable = false)
    private Boolean willingToAdvocate = false; // Interested in advocacy/awareness activities
    
    // =====================================================
    // COMMITMENT & AVAILABILITY
    // =====================================================
    
    @Column
    private String preferredFrequency; // "Weekly", "Monthly", "Quarterly", "As needed", "One-time"
    
    @Column
    private Integer preferredHoursPerMonth; // How many hours per month they'd like to contribute
    
    @Column
    private String preferredTimeOfDay; // "Morning", "Afternoon", "Evening", "Weekend", "Flexible"
    
    @Column(columnDefinition = "TEXT")
    private String availabilityNotes; // Specific availability for this interest
    
    @Column(nullable = false)
    private Boolean longTermCommitment = false; // Interested in long-term projects
    
    @Column(nullable = false)
    private Boolean shortTermProjects = true; // Interested in short-term projects
    
    @Column(nullable = false)
    private Boolean emergencyResponse = false; // Available for urgent/emergency needs
    
    // =====================================================
    // INTERACTION STYLE & PREFERENCES
    // =====================================================
    
    @Column
    private String preferredRoleType; // "Hands-on", "Administrative", "Planning", "Fundraising", etc.
    
    @Column
    private String preferredTeamSize; // "Individual", "Small group", "Large team", "Any"
    
    @Column
    private String preferredEnvironment; // "Indoor", "Outdoor", "Remote", "Community center", etc.
    
    @Column(nullable = false)
    private Boolean comfortableWithPhysicalWork = true;
    
    @Column(nullable = false)
    private Boolean comfortableWithPublicSpeaking = false;
    
    @Column(nullable = false)
    private Boolean comfortableWithFundraising = false;
    
    // =====================================================
    // MATCHING & DISCOVERY
    // =====================================================
    
    @Column(nullable = false)
    private Boolean receiveNotifications = true; // Get notifications for relevant opportunities
    
    @Column(nullable = false)
    private Boolean isPublic = true; // Whether this interest is visible to organizations
    
    @Column(nullable = false)
    private Boolean isActive = true; // Whether this interest is current
    
    @Column(columnDefinition = "TEXT")
    private String keywords; // Additional keywords for better matching (comma-separated)
    
    @Column(columnDefinition = "TEXT")
    private String exclusions; // Things they want to avoid in this interest area
    
    // =====================================================
    // ACTIVITY TRACKING
    // =====================================================
    
    @Column
    private LocalDateTime lastActiveDate; // Last time they engaged with this interest
    
    @Column(nullable = false)
    private Integer opportunitiesApplied = 0; // Number of opportunities applied for in this area
    
    @Column(nullable = false)
    private Integer opportunitiesCompleted = 0; // Number of completed volunteer activities
    
    @Column(nullable = false)
    private Integer organizationsWorkedWith = 0; // Number of different organizations
    
    @Column
    private Double averageRating; // Average rating received for work in this area
    
    @Column(nullable = false)
    private Integer profileViews = 0; // How many times this interest attracted views
    
    // =====================================================
    // METADATA
    // =====================================================
    
    @Column
    private String sourceType = "Self-reported"; // How this interest was added
    
    @Column
    private LocalDateTime lastEngagementDate; // Last time they actually volunteered in this area
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // =====================================================
    // ENUMS
    // =====================================================
    
    public enum InterestPriority {
        LOW("Low Priority", 1, "Occasional interest"),
        MEDIUM("Medium Priority", 2, "Regular interest"),
        HIGH("High Priority", 3, "Strong passion"),
        PASSION("Top Passion", 4, "Life mission");
        
        private final String displayName;
        private final int level;
        private final String description;
        
        InterestPriority(String displayName, int level, String description) {
            this.displayName = displayName;
            this.level = level;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public int getLevel() { return level; }
        public String getDescription() { return description; }
    }
    
    // =====================================================
    // CONSTRUCTORS
    // =====================================================
    
    public ProfileInterest() {
        // Default constructor for JPA
    }
    
    public ProfileInterest(Profile profile, String interestName) {
        this.profile = profile;
        this.interestName = interestName;
        setDefaults();
    }
    
    public ProfileInterest(Profile profile, String interestName, String category) {
        this.profile = profile;
        this.interestName = interestName;
        this.category = category;
        setDefaults();
    }
    
    public ProfileInterest(Profile profile, String interestName, String category, InterestPriority priorityLevel) {
        this.profile = profile;
        this.interestName = interestName;
        this.category = category;
        this.priorityLevel = priorityLevel;
        setDefaults();
    }
    
    // =====================================================
    // HELPER METHODS
    // =====================================================
    
    /**
     * Set default values for new interest entries
     */
    private void setDefaults() {
        this.priorityLevel = InterestPriority.MEDIUM;
        this.hasExperience = false;
        this.activelyInvolved = false;
        this.totalHoursContributed = 0;
        this.shareStory = false;
        this.willingToLearn = true;
        this.willingToLead = false;
        this.willingToTravel = false;
        this.willingToTeach = false;
        this.willingToAdvocate = false;
        this.longTermCommitment = false;
        this.shortTermProjects = true;
        this.emergencyResponse = false;
        this.comfortableWithPhysicalWork = true;
        this.comfortableWithPublicSpeaking = false;
        this.comfortableWithFundraising = false;
        this.receiveNotifications = true;
        this.isPublic = true;
        this.isActive = true;
        this.opportunitiesApplied = 0;
        this.opportunitiesCompleted = 0;
        this.organizationsWorkedWith = 0;
        this.profileViews = 0;
        this.sourceType = "Self-reported";
    }
    
    /**
     * Get display text for priority level
     * @return User-friendly priority description
     */
    public String getPriorityDisplay() {
        if (priorityLevel != null) {
            return priorityLevel.getDisplayName();
        }
        return "Not specified";
    }
    
    /**
     * Check if this is a high-priority interest
     * @return true if priority is HIGH or PASSION
     */
    public boolean isHighPriority() {
        return priorityLevel == InterestPriority.HIGH || priorityLevel == InterestPriority.PASSION;
    }
    
    /**
     * Check if this is a top passion
     * @return true if priority is PASSION
     */
    public boolean isTopPassion() {
        return priorityLevel == InterestPriority.PASSION;
    }
    
    /**
     * Check if user has significant experience in this area
     * @return true if has experience and has been interested for 2+ years or 100+ hours
     */
    public boolean hasSignificantExperience() {
        boolean experienceTime = Boolean.TRUE.equals(hasExperience) && 
                                yearsOfInterest != null && yearsOfInterest >= 2;
        boolean experienceHours = totalHoursContributed != null && totalHoursContributed >= 100;
        
        return experienceTime || experienceHours;
    }
    
    /**
     * Get commitment level as string
     * @return Description of commitment preferences
     */
    public String getCommitmentLevel() {
        if (Boolean.TRUE.equals(longTermCommitment) && Boolean.TRUE.equals(shortTermProjects)) {
            return "Both short-term and long-term";
        } else if (Boolean.TRUE.equals(longTermCommitment)) {
            return "Long-term commitment preferred";
        } else if (Boolean.TRUE.equals(shortTermProjects)) {
            return "Short-term projects preferred";
        }
        return "Flexible";
    }
    
    /**
     * Calculate interest engagement score for matching purposes
     * @return Engagement score (0-100)
     */
    public int calculateEngagementScore() {
        int score = 0;
        
        // Priority level weight (highest impact)
        if (priorityLevel != null) {
            score += priorityLevel.getLevel() * 20; // 20, 40, 60, 80
        }
        
        // Experience bonus
        if (Boolean.TRUE.equals(hasExperience)) {
            score += 10;
        }
        
        // Active involvement bonus
        if (Boolean.TRUE.equals(activelyInvolved)) {
            score += 10;
        }
        
        // Hours contributed
        if (totalHoursContributed != null && totalHoursContributed > 0) {
            score += Math.min(totalHoursContributed / 10, 5); // Up to 5 points
        }
        
        // Leadership willingness
        if (Boolean.TRUE.equals(willingToLead)) {
            score += 3;
        }
        
        // Long-term commitment
        if (Boolean.TRUE.equals(longTermCommitment)) {
            score += 2;
        }
        
        return Math.min(score, 100); // Cap at 100
    }
    
    /**
     * Check if interest matches search criteria
     * @param searchTerm Search term to match against
     * @return true if interest matches the search
     */
    public boolean matchesSearch(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return false;
        }
        
        String searchLower = searchTerm.toLowerCase();
        
        // Check interest name
        if (interestName != null && interestName.toLowerCase().contains(searchLower)) {
            return true;
        }
        
        // Check category
        if (category != null && category.toLowerCase().contains(searchLower)) {
            return true;
        }
        
        // Check keywords
        if (keywords != null && keywords.toLowerCase().contains(searchLower)) {
            return true;
        }
        
        // Check description
        if (description != null && description.toLowerCase().contains(searchLower)) {
            return true;
        }
        
        // Check specific goals
        if (specificGoals != null && specificGoals.toLowerCase().contains(searchLower)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Update activity tracking when user engages with opportunities
     */
    public void recordOpportunityApplication() {
        if (this.opportunitiesApplied == null) {
            this.opportunitiesApplied = 0;
        }
        this.opportunitiesApplied++;
        this.lastActiveDate = LocalDateTime.now();
    }
    
    /**
     * Record completion of a volunteer opportunity
     * @param hoursContributed Hours spent on this opportunity
     * @param rating Rating received (1-5)
     */
    public void recordOpportunityCompletion(int hoursContributed, Double rating) {
        if (this.opportunitiesCompleted == null) {
            this.opportunitiesCompleted = 0;
        }
        this.opportunitiesCompleted++;
        
        // Add hours
        if (this.totalHoursContributed == null) {
            this.totalHoursContributed = 0;
        }
        this.totalHoursContributed += hoursContributed;
        
        // Update average rating
        if (rating != null) {
            if (this.averageRating == null) {
                this.averageRating = rating;
            } else {
                // Calculate running average
                this.averageRating = ((this.averageRating * (this.opportunitiesCompleted - 1)) + rating) 
                                   / this.opportunitiesCompleted;
            }
        }
        
        this.lastEngagementDate = LocalDateTime.now();
        this.lastActiveDate = LocalDateTime.now();
    }
    
    /**
     * Record working with a new organization
     */
    public void recordNewOrganization() {
        if (this.organizationsWorkedWith == null) {
            this.organizationsWorkedWith = 0;
        }
        this.organizationsWorkedWith++;
    }
    
    /**
     * Increment profile views counter
     */
    public void incrementViews() {
        if (this.profileViews == null) {
            this.profileViews = 0;
        }
        this.profileViews++;
    }
    
    /**
     * Check if this interest is suitable for emergency response
     * @return true if user is available for emergency/urgent needs
     */
    public boolean isAvailableForEmergencies() {
        return Boolean.TRUE.equals(emergencyResponse) && Boolean.TRUE.equals(isActive);
    }
    
    /**
     * Get a summary of capabilities for this interest
     * @return String describing what the user can contribute
     */
    public String getCapabilitiesSummary() {
        StringBuilder capabilities = new StringBuilder();
        
        if (Boolean.TRUE.equals(willingToLead)) {
            capabilities.append("Leadership, ");
        }
        if (Boolean.TRUE.equals(willingToTeach)) {
            capabilities.append("Teaching/Training, ");
        }
        if (Boolean.TRUE.equals(willingToAdvocate)) {
            capabilities.append("Advocacy, ");
        }
        if (Boolean.TRUE.equals(comfortableWithPublicSpeaking)) {
            capabilities.append("Public Speaking, ");
        }
        if (Boolean.TRUE.equals(comfortableWithFundraising)) {
            capabilities.append("Fundraising, ");
        }
        if (Boolean.TRUE.equals(comfortableWithPhysicalWork)) {
            capabilities.append("Physical Work, ");
        }
        
        // Remove trailing comma and space
        if (capabilities.length() > 0) {
            capabilities.setLength(capabilities.length() - 2);
        } else {
            capabilities.append("General Support");
        }
        
        return capabilities.toString();
    }
    
    // =====================================================
    // GETTERS AND SETTERS (showing key ones, full implementation would include all)
    // =====================================================
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) { this.profile = profile; }
    
    public String getInterestName() { return interestName; }
    public void setInterestName(String interestName) { this.interestName = interestName; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public InterestPriority getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(InterestPriority priorityLevel) { this.priorityLevel = priorityLevel; }
    
    public Integer getYearsOfInterest() { return yearsOfInterest; }
    public void setYearsOfInterest(Integer yearsOfInterest) { this.yearsOfInterest = yearsOfInterest; }
    
    public Boolean getHasExperience() { return hasExperience; }
    public void setHasExperience(Boolean hasExperience) { this.hasExperience = hasExperience; }
    
    public String getExperienceDescription() { return experienceDescription; }
    public void setExperienceDescription(String experienceDescription) { this.experienceDescription = experienceDescription; }
    
    public Boolean getActivelyInvolved() { return activelyInvolved; }
    public void setActivelyInvolved(Boolean activelyInvolved) { this.activelyInvolved = activelyInvolved; }
    
    public String getCurrentInvolvement() { return currentInvolvement; }
    public void setCurrentInvolvement(String currentInvolvement) { this.currentInvolvement = currentInvolvement; }
    
    public Integer getTotalHoursContributed() { return totalHoursContributed; }
    public void setTotalHoursContributed(Integer totalHoursContributed) { this.totalHoursContributed = totalHoursContributed; }
    
    public String getPersonalStory() { return personalStory; }
    public void setPersonalStory(String personalStory) { this.personalStory = personalStory; }
    
    public Boolean getShareStory() { return shareStory; }
    public void setShareStory(Boolean shareStory) { this.shareStory = shareStory; }
    
    public String getInspirationSource() { return inspirationSource; }
    public void setInspirationSource(String inspirationSource) { this.inspirationSource = inspirationSource; }
    
    public String getSpecificGoals() { return specificGoals; }
    public void setSpecificGoals(String specificGoals) { this.specificGoals = specificGoals; }
    
    public String getImpactAchieved() { return impactAchieved; }
    public void setImpactAchieved(String impactAchieved) { this.impactAchieved = impactAchieved; }
    
    public Boolean getWillingToLearn() { return willingToLearn; }
    public void setWillingToLearn(Boolean willingToLearn) { this.willingToLearn = willingToLearn; }
    
    public Boolean getWillingToLead() { return willingToLead; }
    public void setWillingToLead(Boolean willingToLead) { this.willingToLead = willingToLead; }
    
    public Boolean getWillingToTravel() { return willingToTravel; }
    public void setWillingToTravel(Boolean willingToTravel) { this.willingToTravel = willingToTravel; }
    
    public Integer getMaxTravelDistance() { return maxTravelDistance; }
    public void setMaxTravelDistance(Integer maxTravelDistance) { this.maxTravelDistance = maxTravelDistance; }
    
    public Boolean getWillingToTeach() { return willingToTeach; }
    public void setWillingToTeach(Boolean willingToTeach) { this.willingToTeach = willingToTeach; }
    
    public Boolean getWillingToAdvocate() { return willingToAdvocate; }
    public void setWillingToAdvocate(Boolean willingToAdvocate) { this.willingToAdvocate = willingToAdvocate; }
    
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Integer getOpportunitiesApplied() { return opportunitiesApplied; }
    public void setOpportunitiesApplied(Integer opportunitiesApplied) { this.opportunitiesApplied = opportunitiesApplied; }
    
    public Integer getOpportunitiesCompleted() { return opportunitiesCompleted; }
    public void setOpportunitiesCompleted(Integer opportunitiesCompleted) { this.opportunitiesCompleted = opportunitiesCompleted; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // =====================================================
    // EQUALS, HASHCODE, AND TOSTRING
    // =====================================================
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ProfileInterest that = (ProfileInterest) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "ProfileInterest{" +
                "id=" + id +
                ", interestName='" + interestName + '\'' +
                ", category='" + category + '\'' +
                ", priorityLevel=" + priorityLevel +
                ", hasExperience=" + hasExperience +
                ", totalHoursContributed=" + totalHoursContributed +
                ", createdAt=" + createdAt +
                '}';
    }
}