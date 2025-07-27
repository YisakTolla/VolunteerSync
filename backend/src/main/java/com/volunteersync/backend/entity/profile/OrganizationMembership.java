package com.volunteersync.backend.entity.profile;

import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.entity.enums.ProfileVisibility;
import com.volunteersync.backend.entity.enums.ExperienceLevel;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.time.Period;

/**
 * Entity representing a volunteer's membership/relationship with an organization.
 * This tracks the "My Organizations" section shown in volunteer profiles,
 * including roles, duration, and involvement history.
 */
@Entity
@Table(name = "organization_memberships",
       indexes = {
           @Index(name = "idx_volunteer_membership", columnList = "volunteer_profile_id, is_active"),
           @Index(name = "idx_organization_membership", columnList = "organization_profile_id, is_active"),
           @Index(name = "idx_membership_status", columnList = "membership_status"),
           @Index(name = "idx_membership_type", columnList = "membership_type"),
           @Index(name = "idx_joined_date", columnList = "joined_at")
       },
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"volunteer_profile_id", "organization_profile_id"})
       })
public class OrganizationMembership {
    
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
    @JoinColumn(name = "organization_profile_id", nullable = false)
    private OrganizationProfile organization;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by_user_id")
    private User invitedBy; // Who invited this volunteer
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_user_id")
    private User approvedBy; // Who approved the membership
    
    // =====================================================
    // MEMBERSHIP DETAILS
    // =====================================================
    
    @Enumerated(EnumType.STRING)
    @Column(name = "membership_type", nullable = false)
    private MembershipType membershipType = MembershipType.VOLUNTEER;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "membership_status", nullable = false)
    private MembershipStatus membershipStatus = MembershipStatus.ACTIVE;
    
    @Column(nullable = false, length = 100)
    private String currentRole = "Volunteer"; // Current role with organization
    
    @Column(columnDefinition = "TEXT")
    private String roleDescription; // Detailed description of current role
    
    // =====================================================
    // TIMELINE & DURATION
    // =====================================================
    
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;
    
    @Column(name = "left_at")
    private LocalDateTime leftAt;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column
    private String inactiveReason; // Why membership ended/paused
    
    @Column
    private LocalDateTime lastActiveDate; // Last time volunteer was active
    
    // =====================================================
    // INVOLVEMENT METRICS
    // =====================================================
    
    @Column(nullable = false)
    private Integer totalHoursContributed = 0;
    
    @Column(nullable = false)
    private Integer activitiesCompleted = 0;
    
    @Column(nullable = false)
    private Integer eventsAttended = 0;
    
    @Column
    private Integer leadershipRolesHeld = 0;
    
    @Column
    private Integer trainingsCompleted = 0;
    
    @Column
    private LocalDateTime firstActivityDate;
    
    @Column
    private LocalDateTime lastActivityDate;
    
    // =====================================================
    // PERFORMANCE & RECOGNITION
    // =====================================================
    
    @Column
    private Double averageRating; // Average performance rating
    
    @Column(nullable = false)
    private Integer ratingsReceived = 0;
    
    @Column(columnDefinition = "TEXT")
    private String recognitionNotes; // Special recognition or achievements
    
    @Column(nullable = false)
    private Integer badgesEarned = 0;
    
    @Column(nullable = false)
    private Boolean isRecommended = false; // Organization recommends this volunteer
    
    @Column(columnDefinition = "TEXT")
    private String recommendationNote;
    
    // =====================================================
    // ROLE HISTORY
    // =====================================================
    
    @Column(columnDefinition = "TEXT")
    private String rolesHistory; // JSON array of previous roles with dates
    
    @Column(columnDefinition = "TEXT")
    private String achievements; // JSON array of achievements within organization
    
    @Column
    private LocalDateTime lastRoleChange;
    
    @Column
    private Integer roleProgressions = 0; // Number of times promoted/role changed
    
    // =====================================================
    // PERMISSIONS & ACCESS
    // =====================================================
    
    @Column(nullable = false)
    private Boolean canCreateEvents = false;
    
    @Column(nullable = false)
    private Boolean canManageVolunteers = false;
    
    @Column(nullable = false)
    private Boolean canViewReports = false;
    
    @Column(nullable = false)
    private Boolean isPubliclyVisible = true; // Show this membership on profile
    
    @Column(nullable = false)
    private Boolean receiveNotifications = true;
    
    // =====================================================
    // ENGAGEMENT & COMMUNICATION
    // =====================================================
    
    @Column
    private String preferredCommunicationMethod; // "Email", "SMS", "App", "Phone"
    
    @Column(columnDefinition = "TEXT")
    private String availabilityNotes; // When volunteer is available for this org
    
    @Column(columnDefinition = "TEXT")
    private String specialSkillsOffered; // Skills volunteer offers to this org
    
    @Column(columnDefinition = "TEXT")
    private String personalNotes; // Internal notes from organization
    
    // =====================================================
    // APPLICATION & ONBOARDING
    // =====================================================
    
    @Column
    private LocalDateTime applicationDate;
    
    @Column
    private LocalDateTime approvalDate;
    
    @Column(columnDefinition = "TEXT")
    private String applicationNotes; // Notes from application process
    
    @Column(nullable = false)
    private Boolean backgroundCheckRequired = false;
    
    @Column(nullable = false)
    private Boolean backgroundCheckCompleted = false;
    
    @Column
    private LocalDateTime backgroundCheckDate;
    
    @Column(nullable = false)
    private Boolean orientationCompleted = false;
    
    @Column
    private LocalDateTime orientationDate;
    
    // =====================================================
    // RENEWAL & COMMITMENT
    // =====================================================
    
    @Column
    private LocalDateTime membershipExpiresAt;
    
    @Column(nullable = false)
    private Boolean autoRenewal = true;
    
    @Column
    private String commitmentLevel; // "Casual", "Regular", "Dedicated", "Leadership"
    
    @Column
    private Integer minimumHoursCommitment; // Hours committed per month
    
    // =====================================================
    // METADATA
    // =====================================================
    
    @Column
    private String sourceType = "Application"; // How they joined
    
    @Column(columnDefinition = "TEXT")
    private String metadata; // JSON for additional org-specific data
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // =====================================================
    // ENUMS
    // =====================================================
    
    public enum MembershipType {
        VOLUNTEER("Volunteer", "👥", "Regular volunteer member"),
        BOARD_MEMBER("Board Member", "👔", "Serves on board of directors"),
        STAFF("Staff", "💼", "Paid staff member"),
        INTERN("Intern", "🎓", "Intern or student volunteer"),
        CONSULTANT("Consultant", "🤝", "Professional consultant"),
        AMBASSADOR("Ambassador", "🌟", "Organization ambassador"),
        MENTOR("Mentor", "🎯", "Mentors other volunteers"),
        COORDINATOR("Coordinator", "📋", "Coordinates volunteer activities"),
        SPECIALIST("Specialist", "⚡", "Subject matter expert");
        
        private final String displayName;
        private final String icon;
        private final String description;
        
        MembershipType(String displayName, String icon, String description) {
            this.displayName = displayName;
            this.icon = icon;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getIcon() { return icon; }
        public String getDescription() { return description; }
    }
    
    public enum MembershipStatus {
        PENDING("Pending", "⏳", "Application pending approval"),
        ACTIVE("Active", "✅", "Active member in good standing"),
        INACTIVE("Inactive", "⏸️", "Temporarily inactive"),
        SUSPENDED("Suspended", "⚠️", "Membership suspended"),
        TERMINATED("Terminated", "❌", "Membership terminated"),
        ALUMNI("Alumni", "🎓", "Former member, left in good standing");
        
        private final String displayName;
        private final String icon;
        private final String description;
        
        MembershipStatus(String displayName, String icon, String description) {
            this.displayName = displayName;
            this.icon = icon;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getIcon() { return icon; }
        public String getDescription() { return description; }
    }
    
    // =====================================================
    // CONSTRUCTORS
    // =====================================================
    
    public OrganizationMembership() {
        // Default constructor for JPA
    }
    
    public OrganizationMembership(VolunteerProfile volunteer, OrganizationProfile organization) {
        this.volunteer = volunteer;
        this.organization = organization;
        this.joinedAt = LocalDateTime.now();
        setDefaults();
    }
    
    public OrganizationMembership(VolunteerProfile volunteer, OrganizationProfile organization, 
                                MembershipType membershipType, String role) {
        this.volunteer = volunteer;
        this.organization = organization;
        this.membershipType = membershipType;
        this.currentRole = role;
        this.joinedAt = LocalDateTime.now();
        setDefaults();
    }
    
    private void setDefaults() {
        if (this.membershipStatus == null) {
            this.membershipStatus = MembershipStatus.PENDING;
        }
        if (this.currentRole == null) {
            this.currentRole = "Volunteer";
        }
        if (this.isActive == null) {
            this.isActive = true;
        }
        if (this.totalHoursContributed == null) {
            this.totalHoursContributed = 0;
        }
        if (this.activitiesCompleted == null) {
            this.activitiesCompleted = 0;
        }
        if (this.eventsAttended == null) {
            this.eventsAttended = 0;
        }
        if (this.ratingsReceived == null) {
            this.ratingsReceived = 0;
        }
        if (this.badgesEarned == null) {
            this.badgesEarned = 0;
        }
        if (this.isRecommended == null) {
            this.isRecommended = false;
        }
        if (this.roleProgressions == null) {
            this.roleProgressions = 0;
        }
        if (this.canCreateEvents == null) {
            this.canCreateEvents = false;
        }
        if (this.canManageVolunteers == null) {
            this.canManageVolunteers = false;
        }
        if (this.canViewReports == null) {
            this.canViewReports = false;
        }
        if (this.isPubliclyVisible == null) {
            this.isPubliclyVisible = true;
        }
        if (this.receiveNotifications == null) {
            this.receiveNotifications = true;
        }
        if (this.backgroundCheckRequired == null) {
            this.backgroundCheckRequired = false;
        }
        if (this.backgroundCheckCompleted == null) {
            this.backgroundCheckCompleted = false;
        }
        if (this.orientationCompleted == null) {
            this.orientationCompleted = false;
        }
        if (this.autoRenewal == null) {
            this.autoRenewal = true;
        }
        if (this.sourceType == null) {
            this.sourceType = "Application";
        }
    }
    
    // =====================================================
    // GETTERS
    // =====================================================
    
    public Long getId() {
        return id;
    }
    
    public VolunteerProfile getVolunteer() {
        return volunteer;
    }
    
    public OrganizationProfile getOrganization() {
        return organization;
    }
    
    public User getInvitedBy() {
        return invitedBy;
    }
    
    public User getApprovedBy() {
        return approvedBy;
    }
    
    public MembershipType getMembershipType() {
        return membershipType;
    }
    
    public MembershipStatus getMembershipStatus() {
        return membershipStatus;
    }
    
    public String getCurrentRole() {
        return currentRole;
    }
    
    public String getRoleDescription() {
        return roleDescription;
    }
    
    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
    
    public LocalDateTime getLeftAt() {
        return leftAt;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public String getInactiveReason() {
        return inactiveReason;
    }
    
    public LocalDateTime getLastActiveDate() {
        return lastActiveDate;
    }
    
    public Integer getTotalHoursContributed() {
        return totalHoursContributed;
    }
    
    public Integer getActivitiesCompleted() {
        return activitiesCompleted;
    }
    
    public Integer getEventsAttended() {
        return eventsAttended;
    }
    
    public Integer getLeadershipRolesHeld() {
        return leadershipRolesHeld;
    }
    
    public Integer getTrainingsCompleted() {
        return trainingsCompleted;
    }
    
    public LocalDateTime getFirstActivityDate() {
        return firstActivityDate;
    }
    
    public LocalDateTime getLastActivityDate() {
        return lastActivityDate;
    }
    
    public Double getAverageRating() {
        return averageRating;
    }
    
    public Integer getRatingsReceived() {
        return ratingsReceived;
    }
    
    public String getRecognitionNotes() {
        return recognitionNotes;
    }
    
    public Integer getBadgesEarned() {
        return badgesEarned;
    }
    
    public Boolean getIsRecommended() {
        return isRecommended;
    }
    
    public String getRecommendationNote() {
        return recommendationNote;
    }
    
    public String getRolesHistory() {
        return rolesHistory;
    }
    
    public String getAchievements() {
        return achievements;
    }
    
    public LocalDateTime getLastRoleChange() {
        return lastRoleChange;
    }
    
    public Integer getRoleProgressions() {
        return roleProgressions;
    }
    
    public Boolean getCanCreateEvents() {
        return canCreateEvents;
    }
    
    public Boolean getCanManageVolunteers() {
        return canManageVolunteers;
    }
    
    public Boolean getCanViewReports() {
        return canViewReports;
    }
    
    public Boolean getIsPubliclyVisible() {
        return isPubliclyVisible;
    }
    
    public Boolean getReceiveNotifications() {
        return receiveNotifications;
    }
    
    public String getPreferredCommunicationMethod() {
        return preferredCommunicationMethod;
    }
    
    public String getAvailabilityNotes() {
        return availabilityNotes;
    }
    
    public String getSpecialSkillsOffered() {
        return specialSkillsOffered;
    }
    
    public String getPersonalNotes() {
        return personalNotes;
    }
    
    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }
    
    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }
    
    public String getApplicationNotes() {
        return applicationNotes;
    }
    
    public Boolean getBackgroundCheckRequired() {
        return backgroundCheckRequired;
    }
    
    public Boolean getBackgroundCheckCompleted() {
        return backgroundCheckCompleted;
    }
    
    public LocalDateTime getBackgroundCheckDate() {
        return backgroundCheckDate;
    }
    
    public Boolean getOrientationCompleted() {
        return orientationCompleted;
    }
    
    public LocalDateTime getOrientationDate() {
        return orientationDate;
    }
    
    public LocalDateTime getMembershipExpiresAt() {
        return membershipExpiresAt;
    }
    
    public Boolean getAutoRenewal() {
        return autoRenewal;
    }
    
    public String getCommitmentLevel() {
        return commitmentLevel;
    }
    
    public Integer getMinimumHoursCommitment() {
        return minimumHoursCommitment;
    }
    
    public String getSourceType() {
        return sourceType;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // =====================================================
    // SETTERS
    // =====================================================
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setVolunteer(VolunteerProfile volunteer) {
        this.volunteer = volunteer;
    }
    
    public void setOrganization(OrganizationProfile organization) {
        this.organization = organization;
    }
    
    public void setInvitedBy(User invitedBy) {
        this.invitedBy = invitedBy;
    }
    
    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }
    
    public void setMembershipType(MembershipType membershipType) {
        this.membershipType = membershipType;
    }
    
    public void setMembershipStatus(MembershipStatus membershipStatus) {
        this.membershipStatus = membershipStatus;
    }
    
    public void setCurrentRole(String currentRole) {
        this.currentRole = currentRole;
    }
    
    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }
    
    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
    
    public void setLeftAt(LocalDateTime leftAt) {
        this.leftAt = leftAt;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public void setInactiveReason(String inactiveReason) {
        this.inactiveReason = inactiveReason;
    }
    
    public void setLastActiveDate(LocalDateTime lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }
    
    public void setTotalHoursContributed(Integer totalHoursContributed) {
        this.totalHoursContributed = totalHoursContributed;
    }
    
    public void setActivitiesCompleted(Integer activitiesCompleted) {
        this.activitiesCompleted = activitiesCompleted;
    }
    
    public void setEventsAttended(Integer eventsAttended) {
        this.eventsAttended = eventsAttended;
    }
    
    public void setLeadershipRolesHeld(Integer leadershipRolesHeld) {
        this.leadershipRolesHeld = leadershipRolesHeld;
    }
    
    public void setTrainingsCompleted(Integer trainingsCompleted) {
        this.trainingsCompleted = trainingsCompleted;
    }
    
    public void setFirstActivityDate(LocalDateTime firstActivityDate) {
        this.firstActivityDate = firstActivityDate;
    }
    
    public void setLastActivityDate(LocalDateTime lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }
    
    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
    
    public void setRatingsReceived(Integer ratingsReceived) {
        this.ratingsReceived = ratingsReceived;
    }
    
    public void setRecognitionNotes(String recognitionNotes) {
        this.recognitionNotes = recognitionNotes;
    }
    
    public void setBadgesEarned(Integer badgesEarned) {
        this.badgesEarned = badgesEarned;
    }
    
    public void setIsRecommended(Boolean isRecommended) {
        this.isRecommended = isRecommended;
    }
    
    public void setRecommendationNote(String recommendationNote) {
        this.recommendationNote = recommendationNote;
    }
    
    public void setRolesHistory(String rolesHistory) {
        this.rolesHistory = rolesHistory;
    }
    
    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }
    
    public void setLastRoleChange(LocalDateTime lastRoleChange) {
        this.lastRoleChange = lastRoleChange;
    }
    
    public void setRoleProgressions(Integer roleProgressions) {
        this.roleProgressions = roleProgressions;
    }
    
    public void setCanCreateEvents(Boolean canCreateEvents) {
        this.canCreateEvents = canCreateEvents;
    }
    
    public void setCanManageVolunteers(Boolean canManageVolunteers) {
        this.canManageVolunteers = canManageVolunteers;
    }
    
    public void setCanViewReports(Boolean canViewReports) {
        this.canViewReports = canViewReports;
    }
    
    public void setIsPubliclyVisible(Boolean isPubliclyVisible) {
        this.isPubliclyVisible = isPubliclyVisible;
    }
    
    public void setReceiveNotifications(Boolean receiveNotifications) {
        this.receiveNotifications = receiveNotifications;
    }
    
    public void setPreferredCommunicationMethod(String preferredCommunicationMethod) {
        this.preferredCommunicationMethod = preferredCommunicationMethod;
    }
    
    public void setAvailabilityNotes(String availabilityNotes) {
        this.availabilityNotes = availabilityNotes;
    }
    
    public void setSpecialSkillsOffered(String specialSkillsOffered) {
        this.specialSkillsOffered = specialSkillsOffered;
    }
    
    public void setPersonalNotes(String personalNotes) {
        this.personalNotes = personalNotes;
    }
    
    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }
    
    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }
    
    public void setApplicationNotes(String applicationNotes) {
        this.applicationNotes = applicationNotes;
    }
    
    public void setBackgroundCheckRequired(Boolean backgroundCheckRequired) {
        this.backgroundCheckRequired = backgroundCheckRequired;
    }
    
    public void setBackgroundCheckCompleted(Boolean backgroundCheckCompleted) {
        this.backgroundCheckCompleted = backgroundCheckCompleted;
    }
    
    public void setBackgroundCheckDate(LocalDateTime backgroundCheckDate) {
        this.backgroundCheckDate = backgroundCheckDate;
    }
    
    public void setOrientationCompleted(Boolean orientationCompleted) {
        this.orientationCompleted = orientationCompleted;
    }
    
    public void setOrientationDate(LocalDateTime orientationDate) {
        this.orientationDate = orientationDate;
    }
    
    public void setMembershipExpiresAt(LocalDateTime membershipExpiresAt) {
        this.membershipExpiresAt = membershipExpiresAt;
    }
    
    public void setAutoRenewal(Boolean autoRenewal) {
        this.autoRenewal = autoRenewal;
    }
    
    public void setCommitmentLevel(String commitmentLevel) {
        this.commitmentLevel = commitmentLevel;
    }
    
    public void setMinimumHoursCommitment(Integer minimumHoursCommitment) {
        this.minimumHoursCommitment = minimumHoursCommitment;
    }
    
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // =====================================================
    // BUSINESS LOGIC METHODS
    // =====================================================
    
    /**
     * Calculate the duration of membership in various units
     */
    public Period getMembershipDuration() {
        LocalDateTime endDate = leftAt != null ? leftAt : LocalDateTime.now();
        return Period.between(joinedAt.toLocalDate(), endDate.toLocalDate());
    }
    
    /**
     * Get formatted duration string (e.g., "2 years, 3 months")
     */
    public String getFormattedMembershipDuration() {
        Period duration = getMembershipDuration();
        StringBuilder result = new StringBuilder();
        
        if (duration.getYears() > 0) {
            result.append(duration.getYears()).append(" year");
            if (duration.getYears() > 1) result.append("s");
        }
        
        if (duration.getMonths() > 0) {
            if (result.length() > 0) result.append(", ");
            result.append(duration.getMonths()).append(" month");
            if (duration.getMonths() > 1) result.append("s");
        }
        
        if (result.length() == 0) {
            result.append("Less than a month");
        }
        
        return result.toString();
    }
    
    /**
     * Check if this membership is currently active and valid
     */
    public boolean isCurrentlyActive() {
        if (!isActive || membershipStatus != MembershipStatus.ACTIVE) {
            return false;
        }
        
        if (membershipExpiresAt != null && membershipExpiresAt.isBefore(LocalDateTime.now())) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Check if membership needs renewal soon (within 30 days)
     */
    public boolean needsRenewalSoon() {
        if (membershipExpiresAt == null || !isCurrentlyActive()) {
            return false;
        }
        
        return membershipExpiresAt.isBefore(LocalDateTime.now().plusDays(30));
    }
    
    /**
     * Calculate engagement score based on activities, hours, and ratings
     */
    public double calculateEngagementScore() {
        double score = 0.0;
        
        // Hours contributed (up to 40% of score)
        score += Math.min(40.0, totalHoursContributed * 0.1);
        
        // Activities completed (up to 30% of score)
        score += Math.min(30.0, activitiesCompleted * 2.0);
        
        // Average rating (up to 20% of score)
        if (averageRating != null && ratingsReceived > 0) {
            score += (averageRating / 5.0) * 20.0;
        }
        
        // Leadership and training (up to 10% of score)
        if (leadershipRolesHeld != null) {
            score += Math.min(5.0, leadershipRolesHeld * 1.0);
        }
        if (trainingsCompleted != null) {
            score += Math.min(5.0, trainingsCompleted * 0.5);
        }
        
        return Math.min(100.0, score);
    }
    
    /**
     * Get the engagement level based on score
     */
    public String getEngagementLevel() {
        double score = calculateEngagementScore();
        
        if (score >= 80) return "Highly Engaged";
        if (score >= 60) return "Active";
        if (score >= 40) return "Moderate";
        if (score >= 20) return "Limited";
        return "Inactive";
    }
    
    /**
     * Check if volunteer has special permissions
     */
    public boolean hasAnyPermissions() {
        return canCreateEvents || canManageVolunteers || canViewReports;
    }
    
    /**
     * Check if onboarding is complete
     */
    public boolean isOnboardingComplete() {
        boolean basicRequirements = orientationCompleted;
        
        if (backgroundCheckRequired) {
            basicRequirements = basicRequirements && backgroundCheckCompleted;
        }
        
        return basicRequirements;
    }
    
    /**
     * Get display text for membership status with icon
     */
    public String getStatusDisplayText() {
        return membershipStatus.getIcon() + " " + membershipStatus.getDisplayName();
    }
    
    /**
     * Get display text for membership type with icon
     */
    public String getTypeDisplayText() {
        return membershipType.getIcon() + " " + membershipType.getDisplayName();
    }
    
    /**
     * Check if this is a leadership position
     */
    public boolean isLeadershipPosition() {
        return membershipType == MembershipType.BOARD_MEMBER ||
               membershipType == MembershipType.COORDINATOR ||
               membershipType == MembershipType.MENTOR ||
               membershipType == MembershipType.AMBASSADOR ||
               hasAnyPermissions();
    }
    
    /**
     * Get commitment level display with fallback
     */
    public String getCommitmentLevelDisplay() {
        if (commitmentLevel != null && !commitmentLevel.trim().isEmpty()) {
            return commitmentLevel;
        }
        
        // Fallback based on hours and activities
        if (totalHoursContributed > 100 || activitiesCompleted > 20) {
            return "Dedicated";
        } else if (totalHoursContributed > 40 || activitiesCompleted > 10) {
            return "Regular";
        } else if (totalHoursContributed > 10 || activitiesCompleted > 3) {
            return "Casual";
        }
        
        return "New Member";
    }
    
    /**
     * Update activity metrics
     */
    public void addActivity(int hours, boolean isLeadershipRole) {
        this.activitiesCompleted++;
        this.totalHoursContributed += hours;
        this.lastActivityDate = LocalDateTime.now();
        
        if (this.firstActivityDate == null) {
            this.firstActivityDate = LocalDateTime.now();
        }
        
        if (isLeadershipRole && this.leadershipRolesHeld != null) {
            this.leadershipRolesHeld++;
        }
        
        // Update last active date
        this.lastActiveDate = LocalDateTime.now();
    }
    
    /**
     * Add a new rating
     */
    public void addRating(double rating) {
        if (rating < 1.0 || rating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 1.0 and 5.0");
        }
        
        if (this.averageRating == null) {
            this.averageRating = rating;
            this.ratingsReceived = 1;
        } else {
            // Calculate new average
            double totalRating = this.averageRating * this.ratingsReceived;
            this.ratingsReceived++;
            this.averageRating = (totalRating + rating) / this.ratingsReceived;
        }
    }
    
    /**
     * Promote/change role
     */
    public void changeRole(String newRole, MembershipType newType, String reason) {
        // Store previous role in history if roles history tracking is implemented
        this.currentRole = newRole;
        this.membershipType = newType;
        this.lastRoleChange = LocalDateTime.now();
        this.roleProgressions++;
        
        // Could implement JSON-based role history here
        // Example: {"date": "2024-01-15", "from": "Volunteer", "to": "Coordinator", "reason": "Promotion"}
    }
    
    /**
     * Deactivate membership
     */
    public void deactivate(String reason) {
        this.isActive = false;
        this.membershipStatus = MembershipStatus.INACTIVE;
        this.leftAt = LocalDateTime.now();
        this.inactiveReason = reason;
    }
    
    /**
     * Reactivate membership
     */
    public void reactivate() {
        this.isActive = true;
        this.membershipStatus = MembershipStatus.ACTIVE;
        this.leftAt = null;
        this.inactiveReason = null;
        this.lastActiveDate = LocalDateTime.now();
    }
    
    /**
     * Complete orientation
     */
    public void completeOrientation() {
        this.orientationCompleted = true;
        this.orientationDate = LocalDateTime.now();
    }
    
    /**
     * Complete background check
     */
    public void completeBackgroundCheck() {
        this.backgroundCheckCompleted = true;
        this.backgroundCheckDate = LocalDateTime.now();
    }
    
    /**
     * Award a badge
     */
    public void awardBadge() {
        this.badgesEarned++;
    }
    
    /**
     * Set recommendation status
     */
    public void setRecommendation(boolean recommended, String note) {
        this.isRecommended = recommended;
        this.recommendationNote = note;
    }
    
    // =====================================================
    // UTILITY METHODS
    // =====================================================
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        OrganizationMembership that = (OrganizationMembership) o;
        
        if (id != null && that.id != null) {
            return id.equals(that.id);
        }
        
        // If no IDs, compare by volunteer and organization
        return volunteer != null && volunteer.equals(that.volunteer) &&
               organization != null && organization.equals(that.organization);
    }
    
    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        
        int result = volunteer != null ? volunteer.hashCode() : 0;
        result = 31 * result + (organization != null ? organization.hashCode() : 0);
        return result;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("OrganizationMembership{");
        sb.append("id=").append(id);
        
        if (volunteer != null) {
            sb.append(", volunteer=").append(volunteer.getId());
        }
        if (organization != null) {
            sb.append(", organization=").append(organization.getId());
        }
        
        sb.append(", membershipType=").append(membershipType);
        sb.append(", membershipStatus=").append(membershipStatus);
        sb.append(", currentRole='").append(currentRole).append('\'');
        sb.append(", joinedAt=").append(joinedAt);
        sb.append(", isActive=").append(isActive);
        sb.append(", totalHoursContributed=").append(totalHoursContributed);
        sb.append(", activitiesCompleted=").append(activitiesCompleted);
        sb.append(", averageRating=").append(averageRating);
        sb.append(", createdAt=").append(createdAt);
        sb.append('}');
        
        return sb.toString();
    }
    
    /**
     * Get the organization name for display purposes
     */
    public String getOrganizationName() {
        return organization != null ? organization.getDisplayName() : "Unknown Organization";
    }
    
    /**
     * Get the volunteer name for display purposes
     */
    public String getVolunteerName() {
        return volunteer != null ? volunteer.getDisplayName() : "Unknown Volunteer";
    }
    
    /**
     * Create a summary string for display purposes
     */
    public String getSummaryText() {
        return String.format("%s at %s since %s (%s hours, %s activities)", 
                currentRole,
                getOrganizationName(),
                joinedAt.toLocalDate().toString(),
                totalHoursContributed,
                activitiesCompleted);
    }
    
    /**
     * Get a short description for profile cards
     */
    public String getProfileCardDescription() {
        return String.format("%s • %s • %s", 
                getTypeDisplayText(),
                getFormattedMembershipDuration(),
                getEngagementLevel());
    }
}