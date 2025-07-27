import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.entity.enums.ProfileVisibility;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

/**
 * Entity representing connections between users in the VolunteerSync platform.
 * This tracks the "My Connections" section for volunteers and networking relationships
 * between volunteers, organizations, and other stakeholders.
 */
@Entity
@Table(name = "user_connections",
       indexes = {
           @Index(name = "idx_requester_connection", columnList = "requester_profile_id, connection_status"),
           @Index(name = "idx_recipient_connection", columnList = "recipient_profile_id, connection_status"),
           @Index(name = "idx_connection_type", columnList = "connection_type"),
           @Index(name = "idx_connection_status", columnList = "connection_status"),
           @Index(name = "idx_created_date", columnList = "created_at")
       },
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"requester_profile_id", "recipient_profile_id"})
       })
public class UserConnection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // =====================================================
    // RELATIONSHIPS
    // =====================================================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_profile_id", nullable = false)
    private Profile requesterProfile; // Profile who initiated the connection
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_profile_id", nullable = false)
    private Profile recipientProfile; // Profile who received the connection request
    
    // =====================================================
    // CONNECTION DETAILS
    // =====================================================
    
    @Enumerated(EnumType.STRING)
    @Column(name = "connection_status", nullable = false)
    private ConnectionStatus connectionStatus = ConnectionStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "connection_type", nullable = false)
    private ConnectionType connectionType = ConnectionType.PROFESSIONAL;
    
    @Column(columnDefinition = "TEXT")
    private String connectionMessage; // Message sent with connection request
    
    @Column(columnDefinition = "TEXT")
    private String rejectionReason; // Reason if connection was rejected
    
    // =====================================================
    // RELATIONSHIP CONTEXT
    // =====================================================
    
    @Column(length = 100)
    private String relationshipContext; // How they know each other
    
    @Column(length = 100)
    private String metAt; // Where/how they met (event, organization, etc.)
    
    @Column
    private LocalDateTime metDate; // When they first met
    
    @Column(columnDefinition = "TEXT")
    private String sharedInterests; // JSON array of shared interests/causes
    
    @Column(columnDefinition = "TEXT")
    private String sharedOrganizations; // JSON array of organizations they both work with
    
    // =====================================================
    // INTERACTION & ENGAGEMENT
    // =====================================================
    
    @Column(nullable = false)
    private Integer interactionCount = 0; // Number of times they've interacted
    
    @Column
    private LocalDateTime lastInteractionDate; // Last time they communicated
    
    @Column(nullable = false)
    private Integer collaborativeProjects = 0; // Projects worked on together
    
    @Column(nullable = false)
    private Integer eventsAttendedTogether = 0; // Events they both attended
    
    @Column(nullable = false)
    private Double connectionStrength = 0.0; // Calculated connection strength (0-1)
    
    // =====================================================
    // COMMUNICATION PREFERENCES
    // =====================================================
    
    @Column(nullable = false)
    private Boolean allowDirectMessages = true;
    
    @Column(nullable = false)
    private Boolean allowEventInvitations = true;
    
    @Column(nullable = false)
    private Boolean allowProjectInvitations = true;
    
    @Column(nullable = false)
    private Boolean isVisible = true; // Show this connection on profile
    
    @Column(nullable = false)
    private Boolean notifyOnActivity = true; // Notify about connection's activities
    
    // =====================================================
    // PROFESSIONAL DETAILS
    // =====================================================
    
    @Column(length = 100)
    private String requesterRoleAtTime; // Requester's role when connection was made
    
    @Column(length = 100)
    private String recipientRoleAtTime; // Recipient's role when connection was made
    
    @Column(columnDefinition = "TEXT")
    private String professionalContext; // Professional relationship context
    
    @Column(nullable = false)
    private Boolean isMentorshipRelation = false; // One mentors the other
    
    @Column
    private Long mentorProfileId; // If mentorship, profile ID of the mentor
    
    // =====================================================
    // ENDORSEMENTS & RECOMMENDATIONS
    // =====================================================
    
    @Column(nullable = false)
    private Boolean hasEndorsedRequester = false; // Recipient endorsed requester
    
    @Column(nullable = false)
    private Boolean hasEndorsedRecipient = false; // Requester endorsed recipient
    
    @Column(columnDefinition = "TEXT")
    private String requesterEndorsement; // Endorsement of requester by recipient
    
    @Column(columnDefinition = "TEXT")
    private String recipientEndorsement; // Endorsement of recipient by requester
    
    @Column
    private LocalDateTime lastEndorsementDate;
    
    // =====================================================
    // SOCIAL FEATURES
    // =====================================================
    
    @Column(nullable = false)
    private Boolean isFavorite = false; // Mark as favorite connection
    
    @Column(nullable = false)
    private Boolean isBlocked = false; // Block communication
    
    @Column(length = 50)
    private String connectionLabel; // Custom label (e.g., "Close Friend", "Mentor")
    
    @Column(columnDefinition = "TEXT")
    private String privateNotes; // Private notes about this connection
    
    // =====================================================
    // TIMESTAMPS
    // =====================================================
    
    @Column
    private LocalDateTime requestedAt; // When connection was requested
    
    @Column
    private LocalDateTime acceptedAt; // When connection was accepted
    
    @Column
    private LocalDateTime rejectedAt; // When connection was rejected
    
    @Column
    private LocalDateTime blockedAt; // When connection was blocked
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // =====================================================
    // ENUMS
    // =====================================================
    
    public enum ConnectionStatus {
        PENDING("Pending", "⏳", "Connection request pending"),
        ACCEPTED("Connected", "✅", "Connection established"),
        REJECTED("Rejected", "❌", "Connection request rejected"),
        BLOCKED("Blocked", "🚫", "User blocked"),
        ARCHIVED("Archived", "📦", "Connection archived");
        
        private final String displayName;
        private final String icon;
        private final String description;
        
        ConnectionStatus(String displayName, String icon, String description) {
            this.displayName = displayName;
            this.icon = icon;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getIcon() { return icon; }
        public String getDescription() { return description; }
    }
    
    public enum ConnectionType {
        PROFESSIONAL("Professional", "💼", "Professional networking connection"),
        VOLUNTEER("Volunteer", "👥", "Met through volunteer work"),
        MENTOR("Mentor", "🎓", "Mentorship relationship"),
        FRIEND("Friend", "🤝", "Personal friendship"),
        COLLEAGUE("Colleague", "👔", "Work colleagues"),
        ALUMNI("Alumni", "🎓", "Alumni connection"),
        COMMUNITY("Community", "🏘️", "Community members"),
        COLLABORATOR("Collaborator", "⚡", "Project collaborator");
        
        private final String displayName;
        private final String icon;
        private final String description;
        
        ConnectionType(String displayName, String icon, String description) {
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
    
    public UserConnection() {
        // Default constructor for JPA
    }
    
    public UserConnection(Profile requesterProfile, Profile recipientProfile) {
        this.requesterProfile = requesterProfile;
        this.recipientProfile = recipientProfile;
        this.requestedAt = LocalDateTime.now();
        setDefaults();
    }
    
    public UserConnection(Profile requesterProfile, Profile recipientProfile, ConnectionType connectionType, String message) {
        this.requesterProfile = requesterProfile;
        this.recipientProfile = recipientProfile;
        this.connectionType = connectionType;
        this.connectionMessage = message;
        this.requestedAt = LocalDateTime.now();
        setDefaults();
    }
    
    private void setDefaults() {
        if (this.connectionStatus == null) {
            this.connectionStatus = ConnectionStatus.PENDING;
        }
        if (this.connectionType == null) {
            this.connectionType = ConnectionType.PROFESSIONAL;
        }
        if (this.interactionCount == null) {
            this.interactionCount = 0;
        }
        if (this.collaborativeProjects == null) {
            this.collaborativeProjects = 0;
        }
        if (this.eventsAttendedTogether == null) {
            this.eventsAttendedTogether = 0;
        }
        if (this.connectionStrength == null) {
            this.connectionStrength = 0.0;
        }
        if (this.allowDirectMessages == null) {
            this.allowDirectMessages = true;
        }
        if (this.allowEventInvitations == null) {
            this.allowEventInvitations = true;
        }
        if (this.allowProjectInvitations == null) {
            this.allowProjectInvitations = true;
        }
        if (this.isVisible == null) {
            this.isVisible = true;
        }
        if (this.notifyOnActivity == null) {
            this.notifyOnActivity = true;
        }
        if (this.isMentorshipRelation == null) {
            this.isMentorshipRelation = false;
        }
        if (this.hasEndorsedRequester == null) {
            this.hasEndorsedRequester = false;
        }
        if (this.hasEndorsedRecipient == null) {
            this.hasEndorsedRecipient = false;
        }
        if (this.isFavorite == null) {
            this.isFavorite = false;
        }
        if (this.isBlocked == null) {
            this.isBlocked = false;
        }
    }
    
    // =====================================================
    // GETTERS
    // =====================================================
    
    public Long getId() {
        return id;
    }
    
    public Profile getRequesterProfile() {
        return requesterProfile;
    }
    
    public Profile getRecipientProfile() {
        return recipientProfile;
    }
    
    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }
    
    public ConnectionType getConnectionType() {
        return connectionType;
    }
    
    public String getConnectionMessage() {
        return connectionMessage;
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public String getRelationshipContext() {
        return relationshipContext;
    }
    
    public String getMetAt() {
        return metAt;
    }
    
    public LocalDateTime getMetDate() {
        return metDate;
    }
    
    public String getSharedInterests() {
        return sharedInterests;
    }
    
    public String getSharedOrganizations() {
        return sharedOrganizations;
    }
    
    public Integer getInteractionCount() {
        return interactionCount;
    }
    
    public LocalDateTime getLastInteractionDate() {
        return lastInteractionDate;
    }
    
    public Integer getCollaborativeProjects() {
        return collaborativeProjects;
    }
    
    public Integer getEventsAttendedTogether() {
        return eventsAttendedTogether;
    }
    
    public Double getConnectionStrength() {
        return connectionStrength;
    }
    
    public Boolean getAllowDirectMessages() {
        return allowDirectMessages;
    }
    
    public Boolean getAllowEventInvitations() {
        return allowEventInvitations;
    }
    
    public Boolean getAllowProjectInvitations() {
        return allowProjectInvitations;
    }
    
    public Boolean getIsVisible() {
        return isVisible;
    }
    
    public Boolean getNotifyOnActivity() {
        return notifyOnActivity;
    }
    
    public String getRequesterRoleAtTime() {
        return requesterRoleAtTime;
    }
    
    public String getRecipientRoleAtTime() {
        return recipientRoleAtTime;
    }
    
    public String getProfessionalContext() {
        return professionalContext;
    }
    
    public Boolean getIsMentorshipRelation() {
        return isMentorshipRelation;
    }
    
    public Long getMentorProfileId() {
        return mentorProfileId;
    }
    
    public Boolean getHasEndorsedRequester() {
        return hasEndorsedRequester;
    }
    
    public Boolean getHasEndorsedRecipient() {
        return hasEndorsedRecipient;
    }
    
    public String getRequesterEndorsement() {
        return requesterEndorsement;
    }
    
    public String getRecipientEndorsement() {
        return recipientEndorsement;
    }
    
    public LocalDateTime getLastEndorsementDate() {
        return lastEndorsementDate;
    }
    
    public Boolean getIsFavorite() {
        return isFavorite;
    }
    
    public Boolean getIsBlocked() {
        return isBlocked;
    }
    
    public String getConnectionLabel() {
        return connectionLabel;
    }
    
    public String getPrivateNotes() {
        return privateNotes;
    }
    
    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }
    
    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }
    
    public LocalDateTime getRejectedAt() {
        return rejectedAt;
    }
    
    public LocalDateTime getBlockedAt() {
        return blockedAt;
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
    
    public void setRequesterProfile(Profile requesterProfile) {
        this.requesterProfile = requesterProfile;
    }
    
    public void setRecipientProfile(Profile recipientProfile) {
        this.recipientProfile = recipientProfile;
    }
    
    public void setConnectionStatus(ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }
    
    public void setConnectionType(ConnectionType connectionType) {
        this.connectionType = connectionType;
    }
    
    public void setConnectionMessage(String connectionMessage) {
        this.connectionMessage = connectionMessage;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
    
    public void setRelationshipContext(String relationshipContext) {
        this.relationshipContext = relationshipContext;
    }
    
    public void setMetAt(String metAt) {
        this.metAt = metAt;
    }
    
    public void setMetDate(LocalDateTime metDate) {
        this.metDate = metDate;
    }
    
    public void setSharedInterests(String sharedInterests) {
        this.sharedInterests = sharedInterests;
    }
    
    public void setSharedOrganizations(String sharedOrganizations) {
        this.sharedOrganizations = sharedOrganizations;
    }
    
    public void setInteractionCount(Integer interactionCount) {
        this.interactionCount = interactionCount;
    }
    
    public void setLastInteractionDate(LocalDateTime lastInteractionDate) {
        this.lastInteractionDate = lastInteractionDate;
    }
    
    public void setCollaborativeProjects(Integer collaborativeProjects) {
        this.collaborativeProjects = collaborativeProjects;
    }
    
    public void setEventsAttendedTogether(Integer eventsAttendedTogether) {
        this.eventsAttendedTogether = eventsAttendedTogether;
    }
    
    public void setConnectionStrength(Double connectionStrength) {
        this.connectionStrength = connectionStrength;
    }
    
    public void setAllowDirectMessages(Boolean allowDirectMessages) {
        this.allowDirectMessages = allowDirectMessages;
    }
    
    public void setAllowEventInvitations(Boolean allowEventInvitations) {
        this.allowEventInvitations = allowEventInvitations;
    }
    
    public void setAllowProjectInvitations(Boolean allowProjectInvitations) {
        this.allowProjectInvitations = allowProjectInvitations;
    }
    
    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }
    
    public void setNotifyOnActivity(Boolean notifyOnActivity) {
        this.notifyOnActivity = notifyOnActivity;
    }
    
    public void setRequesterRoleAtTime(String requesterRoleAtTime) {
        this.requesterRoleAtTime = requesterRoleAtTime;
    }
    
    public void setRecipientRoleAtTime(String recipientRoleAtTime) {
        this.recipientRoleAtTime = recipientRoleAtTime;
    }
    
    public void setProfessionalContext(String professionalContext) {
        this.professionalContext = professionalContext;
    }
    
    public void setIsMentorshipRelation(Boolean isMentorshipRelation) {
        this.isMentorshipRelation = isMentorshipRelation;
    }
    
    public void setMentorProfileId(Long mentorProfileId) {
        this.mentorProfileId = mentorProfileId;
    }
    
    public void setHasEndorsedRequester(Boolean hasEndorsedRequester) {
        this.hasEndorsedRequester = hasEndorsedRequester;
    }
    
    public void setHasEndorsedRecipient(Boolean hasEndorsedRecipient) {
        this.hasEndorsedRecipient = hasEndorsedRecipient;
    }
    
    public void setRequesterEndorsement(String requesterEndorsement) {
        this.requesterEndorsement = requesterEndorsement;
    }
    
    public void setRecipientEndorsement(String recipientEndorsement) {
        this.recipientEndorsement = recipientEndorsement;
    }
    
    public void setLastEndorsementDate(LocalDateTime lastEndorsementDate) {
        this.lastEndorsementDate = lastEndorsementDate;
    }
    
    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }
    
    public void setIsBlocked(Boolean isBlocked) {
        this.isBlocked = isBlocked;
    }
    
    public void setConnectionLabel(String connectionLabel) {
        this.connectionLabel = connectionLabel;
    }
    
    public void setPrivateNotes(String privateNotes) {
        this.privateNotes = privateNotes;
    }
    
    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }
    
    public void setAcceptedAt(LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }
    
    public void setRejectedAt(LocalDateTime rejectedAt) {
        this.rejectedAt = rejectedAt;
    }
    
    public void setBlockedAt(LocalDateTime blockedAt) {
        this.blockedAt = blockedAt;
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
     * Get the other profile in this connection relationship
     */
    public Profile getOtherProfile(Profile currentProfile) {
        if (currentProfile.equals(requesterProfile)) {
            return recipientProfile;
        } else if (currentProfile.equals(recipientProfile)) {
            return requesterProfile;
        }
        throw new IllegalArgumentException("Current profile is not part of this connection");
    }
    
    /**
     * Check if the connection is currently active and usable
     */
    public boolean isActiveConnection() {
        return connectionStatus == ConnectionStatus.ACCEPTED && !isBlocked;
    }
    
    /**
     * Check if connection is pending approval
     */
    public boolean isPendingConnection() {
        return connectionStatus == ConnectionStatus.PENDING;
    }
    
    /**
     * Check if current profile can message the other profile
     */
    public boolean canSendMessage(Profile currentProfile) {
        return isActiveConnection() && allowDirectMessages;
    }
    
    /**
     * Check if current profile can invite the other profile to events
     */
    public boolean canInviteToEvents(Profile currentProfile) {
        return isActiveConnection() && allowEventInvitations;
    }
    
    /**
     * Check if current profile can invite the other profile to projects
     */
    public boolean canInviteToProjects(Profile currentProfile) {
        return isActiveConnection() && allowProjectInvitations;
    }
    
    /**
     * Accept the connection request
     */
    public void acceptConnection() {
        if (connectionStatus != ConnectionStatus.PENDING) {
            throw new IllegalStateException("Cannot accept connection that is not pending");
        }
        this.connectionStatus = ConnectionStatus.ACCEPTED;
        this.acceptedAt = LocalDateTime.now();
        
        // Initialize connection strength
        calculateConnectionStrength();
    }
    
    /**
     * Reject the connection request
     */
    public void rejectConnection(String reason) {
        if (connectionStatus != ConnectionStatus.PENDING) {
            throw new IllegalStateException("Cannot reject connection that is not pending");
        }
        this.connectionStatus = ConnectionStatus.REJECTED;
        this.rejectedAt = LocalDateTime.now();
        this.rejectionReason = reason;
    }
    
    /**
     * Block the connection
     */
    public void blockConnection() {
        this.isBlocked = true;
        this.connectionStatus = ConnectionStatus.BLOCKED;
        this.blockedAt = LocalDateTime.now();
    }
    
    /**
     * Unblock the connection
     */
    public void unblockConnection() {
        this.isBlocked = false;
        if (connectionStatus == ConnectionStatus.BLOCKED) {
            this.connectionStatus = ConnectionStatus.ACCEPTED;
        }
        this.blockedAt = null;
    }
    
    /**
     * Record an interaction between the connected users
     */
    public void recordInteraction() {
        this.interactionCount++;
        this.lastInteractionDate = LocalDateTime.now();
        calculateConnectionStrength();
    }
    
    /**
     * Record that users attended an event together
     */
    public void recordEventAttendance() {
        this.eventsAttendedTogether++;
        calculateConnectionStrength();
    }
    
    /**
     * Record collaborative project work
     */
    public void recordCollaboration() {
        this.collaborativeProjects++;
        calculateConnectionStrength();
    }
    
    /**
     * Calculate connection strength based on various factors
     */
    public void calculateConnectionStrength() {
        double strength = 0.0;
        
        // Base strength for being connected
        strength += 0.1;
        
        // Interaction frequency (up to 0.3)
        strength += Math.min(0.3, interactionCount * 0.01);
        
        // Events attended together (up to 0.2)
        strength += Math.min(0.2, eventsAttendedTogether * 0.02);
        
        // Collaborative projects (up to 0.2)
        strength += Math.min(0.2, collaborativeProjects * 0.05);
        
        // Endorsements (up to 0.1)
        if (hasEndorsedRequester || hasEndorsedRecipient) {
            strength += 0.05;
        }
        if (hasEndorsedRequester && hasEndorsedRecipient) {
            strength += 0.05; // Both endorsed each other
        }
        
        // Recency of interaction (up to 0.1)
        if (lastInteractionDate != null) {
            long daysSinceLastInteraction = java.time.Duration.between(lastInteractionDate, LocalDateTime.now()).toDays();
            if (daysSinceLastInteraction < 7) {
                strength += 0.1;
            } else if (daysSinceLastInteraction < 30) {
                strength += 0.05;
            }
        }
        
        this.connectionStrength = Math.min(1.0, strength);
    }
    
    /**
     * Get connection strength level
     */
    public String getConnectionStrengthLevel() {
        if (connectionStrength >= 0.8) return "Very Strong";
        if (connectionStrength >= 0.6) return "Strong";
        if (connectionStrength >= 0.4) return "Moderate";
        if (connectionStrength >= 0.2) return "Weak";
        return "Very Weak";
    }
    
    /**
     * Add endorsement from one profile to another
     */
    public void addEndorsement(Profile endorser, String endorsementText) {
        if (endorser.equals(requesterProfile)) {
            this.recipientEndorsement = endorsementText;
            this.hasEndorsedRecipient = true;
        } else if (endorser.equals(recipientProfile)) {
            this.requesterEndorsement = endorsementText;
            this.hasEndorsedRequester = true;
        } else {
            throw new IllegalArgumentException("Endorser must be part of this connection");
        }
        
        this.lastEndorsementDate = LocalDateTime.now();
        calculateConnectionStrength();
    }
    
    /**
     * Set connection as favorite
     */
    public void markAsFavorite() {
        this.isFavorite = true;
    }
    
    /**
     * Remove from favorites
     */
    public void removeFromFavorites() {
        this.isFavorite = false;
    }
    
    /**
     * Update connection label
     */
    public void setCustomLabel(String label) {
        this.connectionLabel = label;
    }
    
    /**
     * Get status display text with icon
     */
    public String getStatusDisplayText() {
        return connectionStatus.getIcon() + " " + connectionStatus.getDisplayName();
    }
    
    /**
     * Get type display text with icon
     */
    public String getTypeDisplayText() {
        return connectionType.getIcon() + " " + connectionType.getDisplayName();
    }
    
    /**
     * Get connection summary for profile display
     */
    public String getConnectionSummary(Profile currentProfile) {
        Profile otherProfile = getOtherProfile(currentProfile);
        StringBuilder summary = new StringBuilder();
        
        summary.append(otherProfile.getDisplayName());
        
        if (connectionType != null) {
            summary.append(" • ").append(connectionType.getDisplayName());
        }
        
        if (relationshipContext != null && !relationshipContext.trim().isEmpty()) {
            summary.append(" • ").append(relationshipContext);
        }
        
        return summary.toString();
    }
    
    // =====================================================
    // UTILITY METHODS
    // =====================================================
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        UserConnection that = (UserConnection) o;
        
        if (id != null && that.id != null) {
            return id.equals(that.id);
        }
        
        // If no IDs, compare by requester and recipient profiles
        return requesterProfile != null && requesterProfile.equals(that.requesterProfile) &&
               recipientProfile != null && recipientProfile.equals(that.recipientProfile);
    }
    
    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        
        int result = requesterProfile != null ? requesterProfile.hashCode() : 0;
        result = 31 * result + (recipientProfile != null ? recipientProfile.hashCode() : 0);
        return result;
    }
    
    @Override
    public String toString() {
        return "UserConnection{" +
                "id=" + id +
                ", requesterProfile=" + (requesterProfile != null ? requesterProfile.getId() : null) +
                ", recipientProfile=" + (recipientProfile != null ? recipientProfile.getId() : null) +
                ", connectionStatus=" + connectionStatus +
                ", connectionType=" + connectionType +
                ", connectionStrength=" + connectionStrength +
                ", isActive=" + isActiveConnection() +
                ", createdAt=" + createdAt +
                '}';
    }
}