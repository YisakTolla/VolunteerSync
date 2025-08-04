package com.volunteersync.backend.dto;

import com.volunteersync.backend.enums.ApplicationStatus;
import java.time.LocalDateTime;

public class ApplicationDTO {
    private Long id;
    private Long volunteerId;
    private String volunteerName;
    private Long eventId;
    private String eventTitle;
    private LocalDateTime eventStartDate;
    private String organizationName;
    private ApplicationStatus status;
    private String statusDisplayName;
    private String message;
    private String organizationNotes;
    private Integer hoursCompleted;
    private LocalDateTime appliedAt;
    private LocalDateTime respondedAt;
    private LocalDateTime completedAt;
    private Boolean canBeWithdrawn;
    private Boolean isPending;
    private Boolean isCompleted;

    // Constructors
    public ApplicationDTO() {
    }

    public ApplicationDTO(Long id, String volunteerName, String eventTitle, ApplicationStatus status,
            LocalDateTime appliedAt, Integer hoursCompleted) {
        this.id = id;
        this.volunteerName = volunteerName;
        this.eventTitle = eventTitle;
        this.status = status;
        this.statusDisplayName = status.getDisplayName();
        this.appliedAt = appliedAt;
        this.hoursCompleted = hoursCompleted;
        this.isPending = status.isPending();
        this.isCompleted = status.isCompleted();
        this.canBeWithdrawn = status == ApplicationStatus.PENDING || status == ApplicationStatus.ACCEPTED;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(Long volunteerId) {
        this.volunteerId = volunteerId;
    }

    public String getVolunteerName() {
        return volunteerName;
    }

    public void setVolunteerName(String volunteerName) {
        this.volunteerName = volunteerName;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public LocalDateTime getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(LocalDateTime eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
        this.statusDisplayName = status != null ? status.getDisplayName() : null;
        this.isPending = status != null && status.isPending();
        this.isCompleted = status != null && status.isCompleted();
        this.canBeWithdrawn = status == ApplicationStatus.PENDING || status == ApplicationStatus.ACCEPTED;
    }

    public String getStatusDisplayName() {
        return statusDisplayName;
    }

    public void setStatusDisplayName(String statusDisplayName) {
        this.statusDisplayName = statusDisplayName;
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

    public Boolean getCanBeWithdrawn() {
        return canBeWithdrawn;
    }

    public void setCanBeWithdrawn(Boolean canBeWithdrawn) {
        this.canBeWithdrawn = canBeWithdrawn;
    }

    public Boolean getIsPending() {
        return isPending;
    }

    public void setIsPending(Boolean isPending) {
        this.isPending = isPending;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
}