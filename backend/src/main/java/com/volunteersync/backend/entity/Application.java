package com.volunteersync.backend.entity;

import com.volunteersync.backend.enums.ApplicationStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
public class Application {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "volunteer_id", nullable = false)
    private VolunteerProfile volunteer;
    
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.PENDING;
    
    @Column(name = "message", length = 1000)
    private String message; // Optional message from volunteer
    
    @Column(name = "organization_notes", length = 1000)
    private String organizationNotes; // Notes from organization
    
    @Column(name = "hours_completed")
    private Integer hoursCompleted;
    
    @Column(name = "applied_at")
    private LocalDateTime appliedAt = LocalDateTime.now();
    
    @Column(name = "responded_at")
    private LocalDateTime respondedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Constructors
    public Application() {}
    
    public Application(VolunteerProfile volunteer, Event event) {
        this.volunteer = volunteer;
        this.event = event;
    }

    public Application(VolunteerProfile volunteer, Event event, String message) {
        this.volunteer = volunteer;
        this.event = event;
        this.message = message;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VolunteerProfile getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(VolunteerProfile volunteer) {
        this.volunteer = volunteer;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOrganizationNotes() {
        return organizationNotes;
    }

    public void setOrganizationNotes(String organizationNotes) {
        this.organizationNotes = organizationNotes;
    }

    public Integer getHoursCompleted() {
        return hoursCompleted;
    }

    public void setHoursCompleted(Integer hoursCompleted) {
        this.hoursCompleted = hoursCompleted;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    // Helper methods for common operations
    public void approve(String notes) {
        this.status = ApplicationStatus.ACCEPTED;
        this.organizationNotes = notes;
        this.respondedAt = LocalDateTime.now();
    }

    public void reject(String notes) {
        this.status = ApplicationStatus.REJECTED;
        this.organizationNotes = notes;
        this.respondedAt = LocalDateTime.now();
    }

    public void markAttended(int hoursCompleted) {
        this.status = ApplicationStatus.ATTENDED;
        this.hoursCompleted = hoursCompleted;
        this.completedAt = LocalDateTime.now();
    }

    public void markNoShow() {
        this.status = ApplicationStatus.NO_SHOW;
        this.hoursCompleted = 0;
        this.completedAt = LocalDateTime.now();
    }

    public void withdraw() {
        this.status = ApplicationStatus.WITHDRAWN;
        this.respondedAt = LocalDateTime.now();
    }

    // Utility methods
    public boolean isPending() {
        return this.status == ApplicationStatus.PENDING;
    }

    public boolean isApproved() {
        return this.status == ApplicationStatus.ACCEPTED;
    }

    public boolean isCompleted() {
        return this.status == ApplicationStatus.ATTENDED || this.status == ApplicationStatus.NO_SHOW;
    }

    public boolean canBeWithdrawn() {
        return this.status == ApplicationStatus.PENDING || this.status == ApplicationStatus.ACCEPTED;
    }

    public String getStatusDisplayName() {
        return this.status.getDisplayName();
    }

    @PreUpdate
    public void preUpdate() {
        // This method is called automatically before updating the entity
        // Can be used for additional validation or automatic field updates
    }
}