package com.volunteersync.backend.entity.profile;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.volunteersync.backend.entity.user.User;

import java.time.LocalDateTime;

/**
 * Entity representing a volunteer activity or event.
 * This tracks individual volunteer actions, events, achievements, and interactions
 * to create the "Recent Activity" feed shown in user profiles.
 */
@Entity
@Table(name = "volunteer_activities",
       indexes = {
           @Index(name = "idx_volunteer_activity_date", columnList = "volunteer_profile_id, activity_date"),
           @Index(name = "idx_activity_type", columnList = "activity_type"),
           @Index(name = "idx_activity_status", columnList = "status"),
           @Index(name = "idx_organization_activity", columnList = "organization_profile_id, activity_date"),
           @Index(name = "idx_public_activities", columnList = "is_public, activity_date")
       })
public class VolunteerActivity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // =====================================================
    // RELATIONSHIPS
    // =====================================================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volunteer_profile_id", nullable = false)
    private VolunteerProfile volunteer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_profile_id")
    private OrganizationProfile organization;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy; // Who logged this activity (volunteer, org admin, system)
    
    // =====================================================
    // CORE ACTIVITY INFORMATION
    // =====================================================
    
    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    private ActivityType activityType;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "activity_date", nullable = false)
    private LocalDateTime activityDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityStatus status = ActivityStatus.COMPLETED;
    
    // =====================================================
    // EVENT/VOLUNTEER WORK DETAILS
    // =====================================================
    
    @Column
    private LocalDateTime startTime;
    
    @Column
    private LocalDateTime endTime;
    
    @Column
    private Integer hoursContributed = 0;
    
    @Column
    private Integer volunteersParticipated; // For events with multiple volunteers
    
    @Column
    private String location;
    
    @Column
    private String role; // Role played in this activity
    
    // =====================================================
    // IMPACT & OUTCOMES
    // =====================================================
    
    @Column
    private Integer peopleServed; // Number of people helped
    
    @Column
    private String impactDescription; // Description of impact achieved
    
    @Column
    private Double fundingRaised; // Money raised (for fundraising activities)
    
    @Column(columnDefinition = "TEXT")
    private String outcomeNotes; // What was accomplished
    
    // =====================================================
    // FEEDBACK & RECOGNITION
    // =====================================================
    
    @Column
    private Double volunteerRating; // Rating given to volunteer (1-5)
    
    @Column
    private Double organizationRating; // Rating given to organization (1-5)
    
    @Column(columnDefinition = "TEXT")
    private String volunteerFeedback; // Feedback from organization
    
    @Column(columnDefinition = "TEXT")
    private String organizationFeedback; // Feedback from volunteer
    
    @Column(nullable = false)
    private Boolean featuredActivity = false; // Highlight this activity
    
    // =====================================================
    // SOCIAL & SHARING
    // =====================================================
    
    @Column(nullable = false)
    private Boolean isPublic = true; // Show in public activity feed
    
    @Column(nullable = false)
    private Boolean shareOnSocial = false; // User chose to share on social media
    
    @Column
    private String socialMediaPost; // Generated social media content
    
    @Column
    private LocalDateTime sharedAt;
    
    // =====================================================
    // ENGAGEMENT TRACKING
    // =====================================================
    
    @Column(nullable = false)
    private Integer viewCount = 0; // How many times viewed
    
    @Column(nullable = false)
    private Integer likeCount = 0; // Likes/reactions received
    
    @Column(nullable = false)
    private Integer commentCount = 0; // Comments received
    
    @Column
    private LocalDateTime lastViewedAt;
    
    // =====================================================
    // VERIFICATION & VALIDATION
    // =====================================================
    
    @Column(nullable = false)
    private Boolean verified = false; // Activity has been verified
    
    @Column
    private String verifiedBy; // Who verified (organization admin, system)
    
    @Column
    private LocalDateTime verifiedAt;
    
    @Column
    private String verificationMethod; // "Organization", "Photo", "Check-in", etc.
    
    // =====================================================
    // RELATED ENTITIES
    // =====================================================
    
    @Column
    private String relatedBadgeId; // Badge earned from this activity
    
    @Column
    private String relatedEventId; // Event this activity is part of
    
    @Column
    private String relatedOpportunityId; // Opportunity this fulfills
    
    @Column(columnDefinition = "TEXT")
    private String attachments; // JSON array of photos, documents, etc.
    
    // =====================================================
    // METADATA
    // =====================================================
    
    @Column
    private String sourceType = "Manual"; // "Manual", "Check-in", "Import", "API"
    
    @Column(columnDefinition = "TEXT")
    private String metadata; // JSON for additional activity-specific data
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // =====================================================
    // ENUMS
    // =====================================================
    
    public enum ActivityType {
        // Core volunteer activities
        EVENT("event", "🎪", "Participated in an event"),
        VOLUNTEER_WORK("volunteer", "🤝", "Completed volunteer work"),
        TRAINING("training", "📚", "Attended training session"),
        MEETING("meeting", "👥", "Attended meeting or orientation"),
        
        // Social interactions  
        CONNECTION("connection", "🔗", "Made a new connection"),
        ORGANIZATION_JOIN("organization", "🏢", "Joined an organization"),
        
        // Achievements
        ACHIEVEMENT("achievement", "🏆", "Earned an achievement"),
        BADGE_EARNED("badge", "🥇", "Earned a badge"),
        MILESTONE("milestone", "⭐", "Reached a milestone"),
        
        // Special activities
        FUNDRAISING("fundraising", "💰", "Participated in fundraising"),
        ADVOCACY("advocacy", "📢", "Advocacy or awareness activity"),
        LEADERSHIP("leadership", "👑", "Leadership role or responsibility"),
        MENTORING("mentoring", "🎓", "Mentored other volunteers"),
        
        // System activities
        PROFILE_UPDATE("profile", "👤", "Updated profile information"),
        SKILL_ADDED("skill", "💪", "Added new skill"),
        INTEREST_ADDED("interest", "❤️", "Added new interest");
        
        private final String code;
        private final String icon;
        private final String description;
        
        ActivityType(String code, String icon, String description) {
            this.code = code;
            this.icon = icon;
            this.description = description;
        }
        
        public String getCode() { return code; }
        public String getIcon() { return icon; }
        public String getDescription() { return description; }
    }
    
    public enum ActivityStatus {
        PLANNED("Planned", "📅"),
        IN_PROGRESS("In Progress", "⏳"),
        COMPLETED("Completed", "✅"),
        CANCELLED("Cancelled", "❌"),
        NO_SHOW("No Show", "⭕");
        
        private final String displayName;
        private final String icon;
        
        ActivityStatus(String displayName, String icon) {
            this.displayName = displayName;
            this.icon = icon;
        }
        
        public String getDisplayName() { return displayName; }
        public String getIcon() { return icon; }
    }
    
    // =====================================================
    // CONSTRUCTORS
    // =====================================================
    
    public VolunteerActivity() {
        // Default constructor for JPA
    }
    
    public VolunteerActivity(VolunteerProfile volunteer, ActivityType activityType, String title) {
        this.volunteer = volunteer;
        this.activityType = activityType;
        this.title = title;
        this.activityDate = LocalDateTime.now();
        setDefaults();
    }
    
    public VolunteerActivity(VolunteerProfile volunteer, OrganizationProfile organization, 
                           ActivityType activityType, String title, LocalDateTime activityDate) {
        this.volunteer = volunteer;
        this.organization = organization;
        this.activityType = activityType;
        this.title = title;
        this.activityDate = activityDate;
        setDefaults();
    }
    
    // =====================================================
    // HELPER METHODS
    // =====================================================
    
    /**
     * Set default values for new activities
     */
    private void setDefaults() {
        this.status = ActivityStatus.COMPLETED;
        this.hoursContributed = 0;
        this.isPublic = true;
        this.shareOnSocial = false;
        this.featuredActivity = false;
        this.viewCount = 0;
        this.likeCount = 0;
        this.commentCount = 0;
        this.verified = false;
        this.sourceType = "Manual";
    }
    
    /**
     * Get formatted activity display for UI
     * @return Formatted activity string with icon
     */
    public String getFormattedDisplay() {
        StringBuilder display = new StringBuilder();
        
        if (activityType != null) {
            display.append(activityType.getIcon()).append(" ");
        }
        
        display.append(title);
        
        if (organization != null) {
            display.append(" at ").append(organization.getDisplayName());
        }
        
        return display.toString();
    }
    
    /**
     * Get relative time string (e.g., "2 days ago", "1 week ago")
     * @return Human-readable time difference
     */
    public String getRelativeTime() {
        if (activityDate == null) {
            return "Unknown";
        }
        
        LocalDateTime now = LocalDateTime.now();
        long hours = java.time.Duration.between(activityDate, now).toHours();
        
        if (hours < 1) {
            return "Just now";
        } else if (hours < 24) {
            return hours + " hour" + (hours != 1 ? "s" : "") + " ago";
        } else if (hours < 48) {
            return "1 day ago";
        } else if (hours < 168) { // 7 days
            long days = hours / 24;
            return days + " day" + (days != 1 ? "s" : "") + " ago";
        } else if (hours < 720) { // 30 days
            long weeks = hours / 168;
            return weeks + " week" + (weeks != 1 ? "s" : "") + " ago";
        } else {
            long months = hours / 720;
            return months + " month" + (months != 1 ? "s" : "") + " ago";
        }
    }
    
    /**
     * Calculate activity duration in hours
     * @return Duration in hours, or null if times not set
     */
    public Double getDurationHours() {
        if (startTime != null && endTime != null) {
            return (double) java.time.Duration.between(startTime, endTime).toMinutes() / 60.0;
        }
        return null;
    }
    
    /**
     * Check if activity is recent (within last 30 days)
     * @return true if activity is recent
     */
    public boolean isRecent() {
        if (activityDate == null) {
            return false;
        }
        return activityDate.isAfter(LocalDateTime.now().minusDays(30));
    }
    
    /**
     * Check if activity is highly engaged (lots of views/likes)
     * @return true if activity has high engagement
     */
    public boolean isHighlyEngaged() {
        return (viewCount != null && viewCount > 50) || 
               (likeCount != null && likeCount > 10);
    }
    
    /**
     * Get engagement rate (likes + comments / views)
     * @return Engagement rate as percentage
     */
    public double getEngagementRate() {
        if (viewCount == null || viewCount == 0) {
            return 0.0;
        }
        
        int engagements = (likeCount != null ? likeCount : 0) + 
                         (commentCount != null ? commentCount : 0);
        
        return (double) engagements / viewCount * 100;
    }
    
    /**
     * Record a view of this activity
     */
    public void recordView() {
        if (this.viewCount == null) {
            this.viewCount = 0;
        }
        this.viewCount++;
        this.lastViewedAt = LocalDateTime.now();
    }
    
    /**
     * Record a like for this activity
     */
    public void recordLike() {
        if (this.likeCount == null) {
            this.likeCount = 0;
        }
        this.likeCount++;
    }
    
    /**
     * Record a comment on this activity
     */
    public void recordComment() {
        if (this.commentCount == null) {
            this.commentCount = 0;
        }
        this.commentCount++;
    }
    
    /**
     * Verify this activity
     * @param verifier Who is verifying
     * @param method How it was verified
     */
    public void verify(String verifier, String method) {
        this.verified = true;
        this.verifiedBy = verifier;
        this.verifiedAt = LocalDateTime.now();
        this.verificationMethod = method;
    }
    
    /**
     * Generate social media post content
     * @return Generated social media content
     */
    public String generateSocialMediaPost() {
        StringBuilder post = new StringBuilder();
        
        post.append("Just completed: ").append(title);
        
        if (organization != null) {
            post.append(" with ").append(organization.getDisplayName());
        }
        
        if (hoursContributed != null && hoursContributed > 0) {
            post.append(" (").append(hoursContributed).append(" hours)");
        }
        
        post.append(" #Volunteering #MakingADifference");
        
        return post.toString();
    }
    
    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public VolunteerProfile getVolunteer() { return volunteer; }
    public void setVolunteer(VolunteerProfile volunteer) { this.volunteer = volunteer; }
    
    public OrganizationProfile getOrganization() { return organization; }
    public void setOrganization(OrganizationProfile organization) { this.organization = organization; }
    
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    
    public ActivityType getActivityType() { return activityType; }
    public void setActivityType(ActivityType activityType) { this.activityType = activityType; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getActivityDate() { return activityDate; }
    public void setActivityDate(LocalDateTime activityDate) { this.activityDate = activityDate; }
    
    public ActivityStatus getStatus() { return status; }
    public void setStatus(ActivityStatus status) { this.status = status; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public Integer getHoursContributed() { return hoursContributed; }
    public void setHoursContributed(Integer hoursContributed) { this.hoursContributed = hoursContributed; }
    
    public Integer getVolunteersParticipated() { return volunteersParticipated; }
    public void setVolunteersParticipated(Integer volunteersParticipated) { this.volunteersParticipated = volunteersParticipated; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public Integer getPeopleServed() { return peopleServed; }
    public void setPeopleServed(Integer peopleServed) { this.peopleServed = peopleServed; }
    
    public String getImpactDescription() { return impactDescription; }
    public void setImpactDescription(String impactDescription) { this.impactDescription = impactDescription; }
    
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
    
    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }
    
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
        
        VolunteerActivity that = (VolunteerActivity) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "VolunteerActivity{" +
                "id=" + id +
                ", activityType=" + activityType +
                ", title='" + title + '\'' +
                ", activityDate=" + activityDate +
                ", hoursContributed=" + hoursContributed +
                ", status=" + status +
                '}';
    }
}