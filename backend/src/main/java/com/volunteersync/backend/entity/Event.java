package com.volunteersync.backend.entity;

import com.volunteersync.backend.enums.EventStatus;
import com.volunteersync.backend.enums.EventType;
import com.volunteersync.backend.enums.SkillLevel;
import com.volunteersync.backend.enums.EventDuration;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.DayOfWeek;

@Entity
@Table(name = "events")
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private OrganizationProfile organization;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 2000)
    private String description;
    
    // =====================================================
    // NEW FIELDS FOR ENHANCED FILTERING
    // =====================================================
    
    /**
     * Event Type - NEW FIELD for Event Type filtering
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private EventType eventType;
    
    /**
     * Skill Level Required - NEW FIELD for Skill Level filtering
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "skill_level_required")
    private SkillLevel skillLevelRequired = SkillLevel.NO_EXPERIENCE_REQUIRED;
    
    /**
     * Event Duration Category - NEW FIELD for Duration filtering
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "duration_category")
    private EventDuration durationCategory;
    
    /**
     * Is Virtual Event - NEW FIELD for Virtual/Remote filtering
     */
    @Column(name = "is_virtual")
    private Boolean isVirtual = false;
    
    /**
     * Virtual Meeting Link - NEW FIELD for virtual events
     */
    @Column(name = "virtual_meeting_link")
    private String virtualMeetingLink;
    
    /**
     * Time of Day Category - NEW FIELD for Time filtering
     */
    @Column(name = "time_of_day")
    private String timeOfDay; // "MORNING", "AFTERNOON", "EVENING"
    
    /**
     * Is Weekdays Only - NEW FIELD for weekday filtering
     */
    @Column(name = "is_weekdays_only")
    private Boolean isWeekdaysOnly = false;
    
    /**
     * Is Weekends Only - NEW FIELD for weekend filtering
     */
    @Column(name = "is_weekends_only")
    private Boolean isWeekendsOnly = false;
    
    /**
     * Has Flexible Timing - NEW FIELD for flexible timing
     */
    @Column(name = "has_flexible_timing")
    private Boolean hasFlexibleTiming = false;
    
    /**
     * Is Recurring Event - NEW FIELD for ongoing commitments
     */
    @Column(name = "is_recurring")
    private Boolean isRecurring = false;
    
    /**
     * Recurrence Pattern - NEW FIELD (WEEKLY, MONTHLY, etc.)
     */
    @Column(name = "recurrence_pattern")
    private String recurrencePattern; // "WEEKLY", "MONTHLY", "DAILY"
    
    // =====================================================
    // EXISTING FIELDS (KEEP THESE)
    // =====================================================
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    private String location;
    private String address;
    private String city;
    private String state;
    
    @Column(name = "zip_code")
    private String zipCode;
    
    @Column(name = "max_volunteers")
    private Integer maxVolunteers;
    
    @Column(name = "current_volunteers")
    private Integer currentVolunteers = 0;
    
    @Column(name = "estimated_hours")
    private Integer estimatedHours;
    
    @Enumerated(EnumType.STRING)
    private EventStatus status = EventStatus.DRAFT;
    
    @Column(name = "requirements", length = 1000)
    private String requirements;
    
    @Column(name = "contact_email")
    private String contactEmail;
    
    @Column(name = "contact_phone")
    private String contactPhone;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // =====================================================
    // CONSTRUCTORS
    // =====================================================
    
    public Event() {}
    
    public Event(OrganizationProfile organization, String title, LocalDateTime startDate) {
        this.organization = organization;
        this.title = title;
        this.startDate = startDate;
        this.updateTimeOfDay();
        this.updateWeekdayWeekendFlags();
    }

    // =====================================================
    // NEW HELPER METHODS
    // =====================================================
    
    /**
     * Automatically update time of day based on start date
     */
    private void updateTimeOfDay() {
        if (startDate != null) {
            int hour = startDate.getHour();
            if (hour >= 6 && hour < 12) {
                this.timeOfDay = "MORNING";
            } else if (hour >= 12 && hour < 18) {
                this.timeOfDay = "AFTERNOON";
            } else {
                this.timeOfDay = "EVENING";
            }
        }
    }
    
    /**
     * Automatically update weekday/weekend flags
     */
    private void updateWeekdayWeekendFlags() {
        if (startDate != null) {
            DayOfWeek dayOfWeek = startDate.getDayOfWeek();
            boolean isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
            
            this.isWeekendsOnly = isWeekend;
            this.isWeekdaysOnly = !isWeekend;
        }
    }
    
    /**
     * Check if event is on a weekend
     */
    public boolean isOnWeekend() {
        if (startDate == null) return false;
        DayOfWeek dayOfWeek = startDate.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }
    
    /**
     * Check if event is morning (6AM-12PM)
     */
    public boolean isMorningEvent() {
        return "MORNING".equals(timeOfDay);
    }
    
    /**
     * Check if event is afternoon (12PM-6PM)
     */
    public boolean isAfternoonEvent() {
        return "AFTERNOON".equals(timeOfDay);
    }
    
    /**
     * Check if event is evening (6PM-10PM)
     */
    public boolean isEveningEvent() {
        return "EVENING".equals(timeOfDay);
    }

    // =====================================================
    // GETTERS AND SETTERS FOR NEW FIELDS
    // =====================================================
    
    public EventType getEventType() { 
        return eventType; 
    }
    
    public void setEventType(EventType eventType) { 
        this.eventType = eventType; 
    }

    public SkillLevel getSkillLevelRequired() { 
        return skillLevelRequired; 
    }
    
    public void setSkillLevelRequired(SkillLevel skillLevelRequired) { 
        this.skillLevelRequired = skillLevelRequired; 
    }

    public EventDuration getDurationCategory() { 
        return durationCategory; 
    }
    
    public void setDurationCategory(EventDuration durationCategory) { 
        this.durationCategory = durationCategory; 
    }

    public Boolean getIsVirtual() { 
        return isVirtual; 
    }
    
    public void setIsVirtual(Boolean isVirtual) { 
        this.isVirtual = isVirtual; 
    }

    public String getVirtualMeetingLink() { 
        return virtualMeetingLink; 
    }
    
    public void setVirtualMeetingLink(String virtualMeetingLink) { 
        this.virtualMeetingLink = virtualMeetingLink; 
    }

    public String getTimeOfDay() { 
        return timeOfDay; 
    }
    
    public void setTimeOfDay(String timeOfDay) { 
        this.timeOfDay = timeOfDay; 
    }

    public Boolean getIsWeekdaysOnly() { 
        return isWeekdaysOnly; 
    }
    
    public void setIsWeekdaysOnly(Boolean isWeekdaysOnly) { 
        this.isWeekdaysOnly = isWeekdaysOnly; 
    }

    public Boolean getIsWeekendsOnly() { 
        return isWeekendsOnly; 
    }
    
    public void setIsWeekendsOnly(Boolean isWeekendsOnly) { 
        this.isWeekendsOnly = isWeekendsOnly; 
    }

    public Boolean getHasFlexibleTiming() { 
        return hasFlexibleTiming; 
    }
    
    public void setHasFlexibleTiming(Boolean hasFlexibleTiming) { 
        this.hasFlexibleTiming = hasFlexibleTiming; 
    }

    public Boolean getIsRecurring() { 
        return isRecurring; 
    }
    
    public void setIsRecurring(Boolean isRecurring) { 
        this.isRecurring = isRecurring; 
    }

    public String getRecurrencePattern() { 
        return recurrencePattern; 
    }
    
    public void setRecurrencePattern(String recurrencePattern) { 
        this.recurrencePattern = recurrencePattern; 
    }

    // =====================================================
    // GETTERS AND SETTERS FOR EXISTING FIELDS
    // =====================================================
    
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }

    public OrganizationProfile getOrganization() { 
        return organization; 
    }
    
    public void setOrganization(OrganizationProfile organization) { 
        this.organization = organization; 
    }

    public String getTitle() { 
        return title; 
    }
    
    public void setTitle(String title) { 
        this.title = title; 
    }

    public String getDescription() { 
        return description; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }

    public LocalDateTime getStartDate() { 
        return startDate; 
    }
    
    public void setStartDate(LocalDateTime startDate) { 
        this.startDate = startDate;
        this.updateTimeOfDay();
        this.updateWeekdayWeekendFlags();
    }

    public LocalDateTime getEndDate() { 
        return endDate; 
    }
    
    public void setEndDate(LocalDateTime endDate) { 
        this.endDate = endDate; 
    }

    public String getLocation() { 
        return location; 
    }
    
    public void setLocation(String location) { 
        this.location = location; 
    }

    public String getAddress() { 
        return address; 
    }
    
    public void setAddress(String address) { 
        this.address = address; 
    }

    public String getCity() { 
        return city; 
    }
    
    public void setCity(String city) { 
        this.city = city; 
    }

    public String getState() { 
        return state; 
    }
    
    public void setState(String state) { 
        this.state = state; 
    }

    public String getZipCode() { 
        return zipCode; 
    }
    
    public void setZipCode(String zipCode) { 
        this.zipCode = zipCode; 
    }

    public Integer getMaxVolunteers() { 
        return maxVolunteers; 
    }
    
    public void setMaxVolunteers(Integer maxVolunteers) { 
        this.maxVolunteers = maxVolunteers; 
    }

    public Integer getCurrentVolunteers() { 
        return currentVolunteers; 
    }
    
    public void setCurrentVolunteers(Integer currentVolunteers) { 
        this.currentVolunteers = currentVolunteers; 
    }

    public Integer getEstimatedHours() { 
        return estimatedHours; 
    }
    
    public void setEstimatedHours(Integer estimatedHours) { 
        this.estimatedHours = estimatedHours; 
    }

    public EventStatus getStatus() { 
        return status; 
    }
    
    public void setStatus(EventStatus status) { 
        this.status = status; 
    }

    public String getRequirements() { 
        return requirements; 
    }
    
    public void setRequirements(String requirements) { 
        this.requirements = requirements; 
    }

    public String getContactEmail() { 
        return contactEmail; 
    }
    
    public void setContactEmail(String contactEmail) { 
        this.contactEmail = contactEmail; 
    }

    public String getContactPhone() { 
        return contactPhone; 
    }
    
    public void setContactPhone(String contactPhone) { 
        this.contactPhone = contactPhone; 
    }

    public String getImageUrl() { 
        return imageUrl; 
    }
    
    public void setImageUrl(String imageUrl) { 
        this.imageUrl = imageUrl; 
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

    // =====================================================
    // JPA LIFECYCLE METHODS
    // =====================================================
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.updateTimeOfDay();
        this.updateWeekdayWeekendFlags();
    }
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.updateTimeOfDay();
        this.updateWeekdayWeekendFlags();
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    /**
     * Calculate event duration in hours
     */
    public Long getDurationInHours() {
        if (startDate != null && endDate != null) {
            return java.time.Duration.between(startDate, endDate).toHours();
        }
        return null;
    }

    /**
     * Check if event is full (max volunteers reached)
     */
    public boolean isFull() {
        return maxVolunteers != null && currentVolunteers != null && 
               currentVolunteers >= maxVolunteers;
    }

    /**
     * Get remaining volunteer spots
     */
    public Integer getRemainingSpots() {
        if (maxVolunteers != null && currentVolunteers != null) {
            return Math.max(0, maxVolunteers - currentVolunteers);
        }
        return null;
    }

    /**
     * Check if event is upcoming
     */
    public boolean isUpcoming() {
        return startDate != null && startDate.isAfter(LocalDateTime.now());
    }

    /**
     * Check if event is past
     */
    public boolean isPast() {
        return endDate != null ? endDate.isBefore(LocalDateTime.now()) : 
               startDate != null && startDate.isBefore(LocalDateTime.now());
    }

    /**
     * Check if event is currently happening
     */
    public boolean isOngoing() {
        LocalDateTime now = LocalDateTime.now();
        return startDate != null && startDate.isBefore(now) &&
               (endDate == null || endDate.isAfter(now));
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", startDate=" + startDate +
                ", location='" + location + '\'' +
                ", status=" + status +
                ", eventType=" + eventType +
                ", isVirtual=" + isVirtual +
                ", currentVolunteers=" + currentVolunteers +
                ", maxVolunteers=" + maxVolunteers +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id != null && id.equals(event.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}