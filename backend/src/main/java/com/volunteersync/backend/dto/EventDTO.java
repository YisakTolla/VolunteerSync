package com.volunteersync.backend.dto;

import com.volunteersync.backend.enums.EventStatus;
import com.volunteersync.backend.enums.EventType;
import com.volunteersync.backend.enums.SkillLevel;
import com.volunteersync.backend.enums.EventDuration;
import java.time.LocalDateTime;

public class EventDTO {
    
    // =====================================================
    // EXISTING FIELDS
    // =====================================================
    
    private Long id;
    private Long organizationId;
    private String organizationName;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String fullAddress;
    private Integer maxVolunteers;
    private Integer currentVolunteers;
    private Integer spotsRemaining;
    private Integer estimatedHours;
    private EventStatus status;
    private String requirements;
    private String contactEmail;
    private String contactPhone;
    private String imageUrl;
    private Boolean isFull;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // =====================================================
    // NEW FIELDS FOR ENHANCED FILTERING
    // =====================================================
    
    private EventType eventType;
    private SkillLevel skillLevelRequired;
    private EventDuration durationCategory;
    private Boolean isVirtual;
    private String virtualMeetingLink;
    private String timeOfDay;
    private Boolean isWeekdaysOnly;
    private Boolean isWeekendsOnly;
    private Boolean hasFlexibleTiming;
    private Boolean isRecurring;
    private String recurrencePattern;
    
    // =====================================================
    // COMPUTED FIELDS FOR FRONTEND
    // =====================================================
    
    private Long durationInHours;
    private Boolean isUpcoming;
    private Boolean isPast;
    private Boolean isOngoing;
    private Boolean isOnWeekend;
    private Boolean isMorningEvent;
    private Boolean isAfternoonEvent;
    private Boolean isEveningEvent;
    private String eventDisplayType;
    private String skillDisplayLevel;
    private String durationDisplayText;
    private String locationDisplayText;
    private String timeDisplayText;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================
    
    public EventDTO() {
    }

    public EventDTO(Long id, String title, LocalDateTime startDate, String location,
            Integer maxVolunteers, Integer currentVolunteers, EventStatus status) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.location = location;
        this.maxVolunteers = maxVolunteers;
        this.currentVolunteers = currentVolunteers;
        this.status = status;
        updateComputedFields();
    }
    
    /**
     * Enhanced constructor with filtering fields
     */
    public EventDTO(Long id, String title, String description, LocalDateTime startDate,
            LocalDateTime endDate, String location, String city, String state,
            EventType eventType, SkillLevel skillLevelRequired, EventDuration durationCategory,
            Boolean isVirtual, String timeOfDay, Integer maxVolunteers, Integer currentVolunteers,
            EventStatus status, String organizationName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.city = city;
        this.state = state;
        this.eventType = eventType;
        this.skillLevelRequired = skillLevelRequired;
        this.durationCategory = durationCategory;
        this.isVirtual = isVirtual;
        this.timeOfDay = timeOfDay;
        this.maxVolunteers = maxVolunteers;
        this.currentVolunteers = currentVolunteers;
        this.status = status;
        this.organizationName = organizationName;
        updateComputedFields();
    }

    // =====================================================
    // COMPUTED FIELD UPDATES
    // =====================================================
    
    private void updateComputedFields() {
        updateFullAddress();
        updateAvailabilityFields();
        updateTimeFields();
        updateDisplayFields();
    }
    
    private void updateFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (address != null && !address.trim().isEmpty())
            sb.append(address);
        if (city != null && !city.trim().isEmpty())
            sb.append(sb.length() > 0 ? ", " : "").append(city);
        if (state != null && !state.trim().isEmpty())
            sb.append(sb.length() > 0 ? ", " : "").append(state);
        if (zipCode != null && !zipCode.trim().isEmpty())
            sb.append(sb.length() > 0 ? " " : "").append(zipCode);
        this.fullAddress = sb.toString();
    }
    
    private void updateAvailabilityFields() {
        this.isFull = calculateIsFull();
        this.spotsRemaining = calculateSpotsRemaining();
        
        if (startDate != null && endDate != null) {
            this.durationInHours = java.time.Duration.between(startDate, endDate).toHours();
        }
    }
    
    private void updateTimeFields() {
        LocalDateTime now = LocalDateTime.now();
        
        if (startDate != null) {
            this.isUpcoming = startDate.isAfter(now);
            this.isOnWeekend = startDate.getDayOfWeek().getValue() >= 6;
            
            // Determine time of day characteristics
            this.isMorningEvent = "MORNING".equals(timeOfDay);
            this.isAfternoonEvent = "AFTERNOON".equals(timeOfDay);
            this.isEveningEvent = "EVENING".equals(timeOfDay);
        }
        
        if (endDate != null) {
            this.isPast = endDate.isBefore(now);
            this.isOngoing = startDate != null && startDate.isBefore(now) && endDate.isAfter(now);
        } else if (startDate != null) {
            this.isPast = startDate.isBefore(now);
            this.isOngoing = false;
        }
    }
    
    private void updateDisplayFields() {
        // Event type display
        this.eventDisplayType = eventType != null ? eventType.toString().replace("_", " ") : "General Event";
        
        // Skill level display
        this.skillDisplayLevel = skillLevelRequired != null ? 
            skillLevelRequired.toString().replace("_", " ") : "No Experience Required";
        
        // Duration display
        if (durationCategory != null) {
            this.durationDisplayText = durationCategory.toString().replace("_", " ");
        } else if (durationInHours != null) {
            if (durationInHours <= 2) {
                this.durationDisplayText = "Short (" + durationInHours + "h)";
            } else if (durationInHours <= 8) {
                this.durationDisplayText = "Half/Full Day (" + durationInHours + "h)";
            } else {
                this.durationDisplayText = "Multi-Day (" + durationInHours + "h)";
            }
        } else {
            this.durationDisplayText = "Duration TBD";
        }
        
        // Location display
        if (isVirtual != null && isVirtual) {
            this.locationDisplayText = "Virtual/Remote";
        } else if (city != null && state != null) {
            this.locationDisplayText = city + ", " + state;
        } else if (location != null) {
            this.locationDisplayText = location;
        } else {
            this.locationDisplayText = "Location TBD";
        }
        
        // Time display
        if (timeOfDay != null) {
            this.timeDisplayText = timeOfDay.toLowerCase();
            if (hasFlexibleTiming != null && hasFlexibleTiming) {
                this.timeDisplayText += " (flexible)";
            }
        } else {
            this.timeDisplayText = "Time TBD";
        }
    }

    // =====================================================
    // EXISTING GETTERS AND SETTERS
    // =====================================================
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
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
        updateComputedFields();
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
        updateComputedFields();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
        updateComputedFields();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        updateComputedFields();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
        updateComputedFields();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        updateComputedFields();
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
        updateComputedFields();
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public Integer getMaxVolunteers() {
        return maxVolunteers;
    }

    public void setMaxVolunteers(Integer maxVolunteers) {
        this.maxVolunteers = maxVolunteers;
        updateComputedFields();
    }

    public Integer getCurrentVolunteers() {
        return currentVolunteers;
    }

    public void setCurrentVolunteers(Integer currentVolunteers) {
        this.currentVolunteers = currentVolunteers;
        updateComputedFields();
    }

    public Integer getSpotsRemaining() {
        return spotsRemaining;
    }

    public void setSpotsRemaining(Integer spotsRemaining) {
        this.spotsRemaining = spotsRemaining;
    }

    private Integer calculateSpotsRemaining() {
        if (maxVolunteers == null)
            return Integer.MAX_VALUE;
        return Math.max(0, maxVolunteers - (currentVolunteers != null ? currentVolunteers : 0));
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

    public Boolean getIsFull() {
        return isFull;
    }

    public void setIsFull(Boolean isFull) {
        this.isFull = isFull;
    }

    private Boolean calculateIsFull() {
        return maxVolunteers != null && currentVolunteers != null && currentVolunteers >= maxVolunteers;
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
    // NEW GETTERS AND SETTERS
    // =====================================================
    
    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
        updateDisplayFields();
    }

    public SkillLevel getSkillLevelRequired() {
        return skillLevelRequired;
    }

    public void setSkillLevelRequired(SkillLevel skillLevelRequired) {
        this.skillLevelRequired = skillLevelRequired;
        updateDisplayFields();
    }

    public EventDuration getDurationCategory() {
        return durationCategory;
    }

    public void setDurationCategory(EventDuration durationCategory) {
        this.durationCategory = durationCategory;
        updateDisplayFields();
    }

    public Boolean getIsVirtual() {
        return isVirtual;
    }

    public void setIsVirtual(Boolean isVirtual) {
        this.isVirtual = isVirtual;
        updateDisplayFields();
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
        updateTimeFields();
        updateDisplayFields();
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
        updateDisplayFields();
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
    // COMPUTED FIELD GETTERS
    // =====================================================
    
    public Long getDurationInHours() {
        return durationInHours;
    }

    public Boolean getIsUpcoming() {
        return isUpcoming;
    }

    public Boolean getIsPast() {
        return isPast;
    }

    public Boolean getIsOngoing() {
        return isOngoing;
    }

    public Boolean getIsOnWeekend() {
        return isOnWeekend;
    }

    public Boolean getIsMorningEvent() {
        return isMorningEvent;
    }

    public Boolean getIsAfternoonEvent() {
        return isAfternoonEvent;
    }

    public Boolean getIsEveningEvent() {
        return isEveningEvent;
    }

    public String getEventDisplayType() {
        return eventDisplayType;
    }

    public String getSkillDisplayLevel() {
        return skillDisplayLevel;
    }

    public String getDurationDisplayText() {
        return durationDisplayText;
    }

    public String getLocationDisplayText() {
        return locationDisplayText;
    }

    public String getTimeDisplayText() {
        return timeDisplayText;
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================
    
    /**
     * Check if event has specific event type
     */
    public boolean hasEventType(EventType type) {
        return eventType != null && eventType.equals(type);
    }
    
    /**
     * Check if event requires specific skill level or lower
     */
    public boolean isAccessibleForSkillLevel(SkillLevel userSkillLevel) {
        if (skillLevelRequired == null) return true;
        if (userSkillLevel == null) return skillLevelRequired.equals(SkillLevel.NO_EXPERIENCE_REQUIRED);
        
        // Compare skill levels (assuming they have ordinal values)
        return userSkillLevel.ordinal() >= skillLevelRequired.ordinal();
    }
    
    /**
     * Check if event matches duration category
     */
    public boolean hasDurationCategory(EventDuration duration) {
        return durationCategory != null && durationCategory.equals(duration);
    }
    
    /**
     * Get primary display location
     */
    public String getPrimaryLocation() {
        if (isVirtual != null && isVirtual) {
            return "Virtual";
        } else if (city != null) {
            return city;
        } else if (location != null) {
            return location;
        }
        return "TBD";
    }
    
    /**
     * Get event urgency level based on start date
     */
    public String getUrgencyLevel() {
        if (startDate == null || !isUpcoming) return "none";
        
        LocalDateTime now = LocalDateTime.now();
        long hoursUntil = java.time.Duration.between(now, startDate).toHours();
        
        if (hoursUntil <= 24) return "urgent";
        if (hoursUntil <= 72) return "soon";
        if (hoursUntil <= 168) return "this-week";
        return "normal";
    }
    
    /**
     * Get capacity status
     */
    public String getCapacityStatus() {
        if (maxVolunteers == null) return "unlimited";
        if (isFull != null && isFull) return "full";
        
        int spotsLeft = spotsRemaining != null ? spotsRemaining : 0;
        if (spotsLeft <= 2) return "almost-full";
        if (spotsLeft <= 5) return "filling-up";
        return "available";
    }
    
    /**
     * Check if event matches time preferences
     */
    public boolean matchesTimePreferences(String timePreference) {
        if (timePreference == null) return true;
        
        switch (timePreference.toLowerCase()) {
            case "morning":
                return isMorningEvent != null && isMorningEvent;
            case "afternoon":
                return isAfternoonEvent != null && isAfternoonEvent;
            case "evening":
                return isEveningEvent != null && isEveningEvent;
            case "weekdays":
                return isWeekdaysOnly != null && isWeekdaysOnly;
            case "weekends":
                return isWeekendsOnly != null && isWeekendsOnly;
            case "flexible":
                return hasFlexibleTiming != null && hasFlexibleTiming;
            default:
                return true;
        }
    }

    @Override
    public String toString() {
        return "EventDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", eventType=" + eventType +
                ", startDate=" + startDate +
                ", location='" + location + '\'' +
                ", isVirtual=" + isVirtual +
                ", skillLevelRequired=" + skillLevelRequired +
                ", status=" + status +
                ", maxVolunteers=" + maxVolunteers +
                ", currentVolunteers=" + currentVolunteers +
                ", isUpcoming=" + isUpcoming +
                '}';
    }
}