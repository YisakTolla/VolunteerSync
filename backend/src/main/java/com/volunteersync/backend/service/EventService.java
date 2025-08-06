// backend/src/main/java/com/volunteersync/backend/service/EventService.java
package com.volunteersync.backend.service;

import com.volunteersync.backend.entity.Event;
import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.entity.Application;
import com.volunteersync.backend.entity.OrganizationProfile;
import com.volunteersync.backend.entity.VolunteerProfile;
import com.volunteersync.backend.enums.UserType;
import com.volunteersync.backend.enums.EventStatus;
import com.volunteersync.backend.enums.EventType;
import com.volunteersync.backend.enums.SkillLevel;
import com.volunteersync.backend.enums.EventDuration;
import com.volunteersync.backend.enums.ApplicationStatus;
import com.volunteersync.backend.repository.EventRepository;
import com.volunteersync.backend.repository.ApplicationRepository;
import com.volunteersync.backend.repository.UserRepository;
import com.volunteersync.backend.repository.OrganizationProfileRepository;
import com.volunteersync.backend.repository.VolunteerProfileRepository;
import com.volunteersync.backend.dto.EventDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Event service - handles event creation and management
 */
@Service
@Transactional
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrganizationProfileRepository organizationProfileRepository;
    
    @Autowired
    private VolunteerProfileRepository volunteerProfileRepository;

    // ==========================================
    // EVENT CRUD OPERATIONS
    // ==========================================

    /**
     * Create new event
     */
    public EventDTO createEvent(CreateEventRequest request, Long organizerId) {
        System.out.println("Creating new event '" + request.getTitle() + "' by organizer ID: " + organizerId);
        
        // Verify organizer exists and is an organization
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));
        
        if (organizer.getUserType() != UserType.ORGANIZATION) {
            throw new RuntimeException("Only organizations can create events");
        }
        
        // Get organization profile
        OrganizationProfile orgProfile = organizationProfileRepository.findByUser(organizer)
                .orElseThrow(() -> new RuntimeException("Organization profile not found"));
        
        // Create event
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setCity(request.getCity());
        event.setState(request.getState());
        event.setAddress(request.getAddress());
        event.setZipCode(request.getZipCode());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setMaxVolunteers(request.getMaxVolunteers());
        event.setCurrentVolunteers(0);
        event.setEstimatedHours(request.getEstimatedHours());
        event.setRequirements(request.getRequirements());
        event.setContactEmail(request.getContactEmail());
        event.setContactPhone(request.getContactPhone());
        
        // Set organization
        event.setOrganization(orgProfile);
        
        // Set enhanced fields
        if (request.getEventType() != null) {
            event.setEventType(EventType.valueOf(request.getEventType()));
        }
        if (request.getSkillLevelRequired() != null) {
            event.setSkillLevelRequired(SkillLevel.valueOf(request.getSkillLevelRequired()));
        }
        if (request.getDurationCategory() != null) {
            event.setDurationCategory(EventDuration.valueOf(request.getDurationCategory()));
        }
        event.setIsVirtual(request.getIsVirtual() != null ? request.getIsVirtual() : false);
        event.setVirtualMeetingLink(request.getVirtualMeetingLink());
        event.setIsRecurring(request.getIsRecurring() != null ? request.getIsRecurring() : false);
        event.setRecurrencePattern(request.getRecurrencePattern());
        event.setHasFlexibleTiming(request.getHasFlexibleTiming() != null ? request.getHasFlexibleTiming() : false);
        
        event.setStatus(EventStatus.ACTIVE);
        
        Event savedEvent = eventRepository.save(event);
        System.out.println("Successfully created event with ID: " + savedEvent.getId());
        
        return convertToDTO(savedEvent);
    }

    /**
     * Get all active events
     */
    public List<EventDTO> getAllEvents() {
        System.out.println("Fetching all active events");
        
        List<Event> events = eventRepository.findUpcomingActiveEvents(LocalDateTime.now());
        
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get event by ID
     */
    public EventDTO getEventById(Long eventId) {
        System.out.println("Fetching event with ID: " + eventId);
        
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));
        
        return convertToDTO(event);
    }

    /**
     * Get events by organizer
     */
    public List<EventDTO> getEventsByOrganizer(Long organizerId) {
        System.out.println("Fetching events for organizer ID: " + organizerId);
        
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));
        
        OrganizationProfile orgProfile = organizationProfileRepository.findByUser(organizer)
                .orElseThrow(() -> new RuntimeException("Organization profile not found"));
        
        List<Event> events = eventRepository.findByOrganizationOrderByStartDateDesc(orgProfile);
        
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search events with filters
     */
    public List<EventDTO> searchEvents(EventSearchRequest request) {
        System.out.println("Searching events with filters");
        
        if (request.getSearchTerm() != null || request.getEventType() != null || 
            request.getLocation() != null || request.getSkillLevel() != null) {
            
            EventType eventType = request.getEventType() != null ? 
                    EventType.valueOf(request.getEventType()) : null;
            SkillLevel skillLevel = request.getSkillLevel() != null ? 
                    SkillLevel.valueOf(request.getSkillLevel()) : null;
            
            // Use repository search method with pagination
            Page<Event> eventPage = eventRepository.searchWithFilters(
                    request.getSearchTerm(),
                    EventStatus.ACTIVE,
                    eventType,
                    request.getLocation(),
                    skillLevel,
                    request.getPageable() != null ? request.getPageable() : 
                            org.springframework.data.domain.PageRequest.of(0, 20)
            );
            
            return eventPage.getContent().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } else {
            return getAllEvents();
        }
    }

    /**
     * Get events by type
     */
    public List<EventDTO> getEventsByType(String eventType) {
        System.out.println("Fetching events of type: " + eventType);
        
        EventType type = EventType.valueOf(eventType);
        List<Event> events = eventRepository.findByEventTypeAndStatus(type, EventStatus.ACTIVE);
        
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get virtual events
     */
    public List<EventDTO> getVirtualEvents() {
        System.out.println("Fetching virtual events");
        
        List<Event> events = eventRepository.findByIsVirtualAndStatus(true, EventStatus.ACTIVE);
        
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get events by location
     */
    public List<EventDTO> getEventsByLocation(String location) {
        System.out.println("Fetching events in location: " + location);
        
        List<Event> events = eventRepository.findActiveEventsByLocation(location);
        
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get events with available spots
     */
    public List<EventDTO> getEventsWithAvailableSpots() {
        System.out.println("Fetching events with available spots");
        
        List<Event> events = eventRepository.findEventsWithAvailableSpots();
        
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update event
     */
    public EventDTO updateEvent(Long eventId, UpdateEventRequest request, Long organizerId) {
        System.out.println("Updating event ID: " + eventId + " by organizer ID: " + organizerId);
        
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));
        
        // Check if user is the organizer
        if (!event.getOrganization().getUser().getId().equals(organizerId)) {
            throw new RuntimeException("You can only update events you organized");
        }
        
        // Update fields
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getLocation() != null) {
            event.setLocation(request.getLocation());
        }
        if (request.getCity() != null) {
            event.setCity(request.getCity());
        }
        if (request.getState() != null) {
            event.setState(request.getState());
        }
        if (request.getStartDate() != null) {
            event.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            event.setEndDate(request.getEndDate());
        }
        if (request.getMaxVolunteers() != null) {
            event.setMaxVolunteers(request.getMaxVolunteers());
        }
        if (request.getEstimatedHours() != null) {
            event.setEstimatedHours(request.getEstimatedHours());
        }
        if (request.getRequirements() != null) {
            event.setRequirements(request.getRequirements());
        }
        if (request.getEventType() != null) {
            event.setEventType(EventType.valueOf(request.getEventType()));
        }
        if (request.getSkillLevelRequired() != null) {
            event.setSkillLevelRequired(SkillLevel.valueOf(request.getSkillLevelRequired()));
        }
        if (request.getIsVirtual() != null) {
            event.setIsVirtual(request.getIsVirtual());
        }
        if (request.getVirtualMeetingLink() != null) {
            event.setVirtualMeetingLink(request.getVirtualMeetingLink());
        }
        
        Event savedEvent = eventRepository.save(event);
        
        System.out.println("Successfully updated event with ID: " + eventId);
        return convertToDTO(savedEvent);
    }

    /**
     * Cancel event
     */
    public void cancelEvent(Long eventId, Long organizerId) {
        System.out.println("Cancelling event ID: " + eventId + " by organizer ID: " + organizerId);
        
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));
        
        // Check if user is the organizer
        if (!event.getOrganization().getUser().getId().equals(organizerId)) {
            throw new RuntimeException("You can only cancel events you organized");
        }
        
        event.setStatus(EventStatus.CANCELLED);
        eventRepository.save(event);
        
        System.out.println("Successfully cancelled event with ID: " + eventId);
    }

    // ==========================================
    // VOLUNTEER REGISTRATION METHODS
    // ==========================================

    /**
     * Register volunteer for event
     */
    public String registerForEvent(Long eventId, Long volunteerId) {
        System.out.println("Registering volunteer ID: " + volunteerId + " for event ID: " + eventId);
        
        // Verify volunteer exists and is a volunteer
        User volunteer = userRepository.findById(volunteerId)
                .orElseThrow(() -> new RuntimeException("Volunteer not found"));
        
        if (volunteer.getUserType() != UserType.VOLUNTEER) {
            throw new RuntimeException("Only volunteers can register for events");
        }
        
        // Verify event exists and is active
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        if (event.getStatus() != EventStatus.ACTIVE) {
            throw new RuntimeException("Cannot register for inactive events");
        }
        
        // Get volunteer profile
        VolunteerProfile volunteerProfile = volunteerProfileRepository.findByUser(volunteer)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));
        
        // Check if already registered using correct method signature
        Optional<Application> existingApplication = applicationRepository.findByVolunteerAndEvent(
                volunteerProfile, event);
        if (existingApplication.isPresent()) {
            throw new RuntimeException("Already registered for this event");
        }
        
        // Check capacity
        if (event.getMaxVolunteers() != null && event.getCurrentVolunteers() >= event.getMaxVolunteers()) {
            throw new RuntimeException("Event is full");
        }
        
        // Create application/registration
        Application application = new Application();
        application.setVolunteer(volunteerProfile);
        application.setEvent(event);
        application.setStatus(ApplicationStatus.ACCEPTED); // Auto-accept for events
        application.setAppliedAt(LocalDateTime.now());
        
        applicationRepository.save(application);
        
        // Update event participant count
        event.setCurrentVolunteers(event.getCurrentVolunteers() + 1);
        eventRepository.save(event);
        
        System.out.println("Successfully registered volunteer for event");
        return "Successfully registered for event!";
    }

    /**
     * Cancel event registration
     */
    public String cancelRegistration(Long eventId, Long volunteerId) {
        System.out.println("Cancelling registration for volunteer ID: " + volunteerId + " from event ID: " + eventId);
        
        // Find volunteer profile and event
        User volunteer = userRepository.findById(volunteerId)
                .orElseThrow(() -> new RuntimeException("Volunteer not found"));
        
        VolunteerProfile volunteerProfile = volunteerProfileRepository.findByUser(volunteer)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        // Find registration
        Application application = applicationRepository.findByVolunteerAndEvent(
                volunteerProfile, event)
                .orElseThrow(() -> new RuntimeException("Registration not found"));
        
        // Delete registration
        applicationRepository.delete(application);
        
        // Update event participant count
        event.setCurrentVolunteers(Math.max(0, event.getCurrentVolunteers() - 1));
        eventRepository.save(event);
        
        System.out.println("Successfully cancelled registration");
        return "Registration cancelled successfully!";
    }

    /**
     * Get volunteer's registered events
     */
    public List<EventDTO> getVolunteerEvents(Long volunteerId) {
        System.out.println("Fetching events for volunteer ID: " + volunteerId);
        
        User volunteer = userRepository.findById(volunteerId)
                .orElseThrow(() -> new RuntimeException("Volunteer not found"));
        
        VolunteerProfile volunteerProfile = volunteerProfileRepository.findByUser(volunteer)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));
        
        List<Application> applications = applicationRepository.findByVolunteer(volunteerProfile);
        
        List<Event> events = applications.stream()
                .map(Application::getEvent)
                .collect(Collectors.toList());
        
        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get event registrations (for organizers)
     */
    public List<Application> getEventRegistrations(Long eventId, Long organizerId) {
        System.out.println("Fetching registrations for event ID: " + eventId + " by organizer: " + organizerId);
        
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        // Check if user is the organizer
        if (!event.getOrganization().getUser().getId().equals(organizerId)) {
            throw new RuntimeException("You can only view registrations for events you organized");
        }
        
        return applicationRepository.findByEvent(event);
    }

    // ==========================================
    // PRIVATE HELPER METHODS
    // ==========================================

    private EventDTO convertToDTO(Event event) {
        EventDTO dto = new EventDTO();
        
        // Basic fields
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setLocation(event.getLocation());
        dto.setAddress(event.getAddress());
        dto.setCity(event.getCity());
        dto.setState(event.getState());
        dto.setZipCode(event.getZipCode());
        dto.setStartDate(event.getStartDate());
        dto.setEndDate(event.getEndDate());
        dto.setMaxVolunteers(event.getMaxVolunteers());
        dto.setCurrentVolunteers(event.getCurrentVolunteers());
        dto.setEstimatedHours(event.getEstimatedHours());
        dto.setStatus(event.getStatus());
        dto.setRequirements(event.getRequirements());
        dto.setContactEmail(event.getContactEmail());
        dto.setContactPhone(event.getContactPhone());
        dto.setImageUrl(event.getImageUrl());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());
        
        // Enhanced fields
        dto.setEventType(event.getEventType());
        dto.setSkillLevelRequired(event.getSkillLevelRequired());
        dto.setDurationCategory(event.getDurationCategory());
        dto.setIsVirtual(event.getIsVirtual());
        dto.setVirtualMeetingLink(event.getVirtualMeetingLink());
        dto.setTimeOfDay(event.getTimeOfDay());
        dto.setIsWeekdaysOnly(event.getIsWeekdaysOnly());
        dto.setIsWeekendsOnly(event.getIsWeekendsOnly());
        dto.setHasFlexibleTiming(event.getHasFlexibleTiming());
        dto.setIsRecurring(event.getIsRecurring());
        dto.setRecurrencePattern(event.getRecurrencePattern());
        
        // Organization info
        if (event.getOrganization() != null) {
            dto.setOrganizationId(event.getOrganization().getId());
            dto.setOrganizationName(event.getOrganization().getOrganizationName());
        }
        
        return dto;
    }

    // ==========================================
    // REQUEST CLASSES
    // ==========================================

    public static class CreateEventRequest {
        private String title;
        private String description;
        private String location;
        private String address;
        private String city;
        private String state;
        private String zipCode;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Integer maxVolunteers;
        private Integer estimatedHours;
        private String requirements;
        private String contactEmail;
        private String contactPhone;
        private String eventType;
        private String skillLevelRequired;
        private String durationCategory;
        private Boolean isVirtual;
        private String virtualMeetingLink;
        private Boolean isRecurring;
        private String recurrencePattern;
        private Boolean hasFlexibleTiming;
        
        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
        public Integer getMaxVolunteers() { return maxVolunteers; }
        public void setMaxVolunteers(Integer maxVolunteers) { this.maxVolunteers = maxVolunteers; }
        public Integer getEstimatedHours() { return estimatedHours; }
        public void setEstimatedHours(Integer estimatedHours) { this.estimatedHours = estimatedHours; }
        public String getRequirements() { return requirements; }
        public void setRequirements(String requirements) { this.requirements = requirements; }
        public String getContactEmail() { return contactEmail; }
        public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
        public String getContactPhone() { return contactPhone; }
        public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
        public String getSkillLevelRequired() { return skillLevelRequired; }
        public void setSkillLevelRequired(String skillLevelRequired) { this.skillLevelRequired = skillLevelRequired; }
        public String getDurationCategory() { return durationCategory; }
        public void setDurationCategory(String durationCategory) { this.durationCategory = durationCategory; }
        public Boolean getIsVirtual() { return isVirtual; }
        public void setIsVirtual(Boolean isVirtual) { this.isVirtual = isVirtual; }
        public String getVirtualMeetingLink() { return virtualMeetingLink; }
        public void setVirtualMeetingLink(String virtualMeetingLink) { this.virtualMeetingLink = virtualMeetingLink; }
        public Boolean getIsRecurring() { return isRecurring; }
        public void setIsRecurring(Boolean isRecurring) { this.isRecurring = isRecurring; }
        public String getRecurrencePattern() { return recurrencePattern; }
        public void setRecurrencePattern(String recurrencePattern) { this.recurrencePattern = recurrencePattern; }
        public Boolean getHasFlexibleTiming() { return hasFlexibleTiming; }
        public void setHasFlexibleTiming(Boolean hasFlexibleTiming) { this.hasFlexibleTiming = hasFlexibleTiming; }
    }

    public static class UpdateEventRequest {
        private String title;
        private String description;
        private String location;
        private String city;
        private String state;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Integer maxVolunteers;
        private Integer estimatedHours;
        private String requirements;
        private String eventType;
        private String skillLevelRequired;
        private Boolean isVirtual;
        private String virtualMeetingLink;
        
        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
        public Integer getMaxVolunteers() { return maxVolunteers; }
        public void setMaxVolunteers(Integer maxVolunteers) { this.maxVolunteers = maxVolunteers; }
        public Integer getEstimatedHours() { return estimatedHours; }
        public void setEstimatedHours(Integer estimatedHours) { this.estimatedHours = estimatedHours; }
        public String getRequirements() { return requirements; }
        public void setRequirements(String requirements) { this.requirements = requirements; }
        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
        public String getSkillLevelRequired() { return skillLevelRequired; }
        public void setSkillLevelRequired(String skillLevelRequired) { this.skillLevelRequired = skillLevelRequired; }
        public Boolean getIsVirtual() { return isVirtual; }
        public void setIsVirtual(Boolean isVirtual) { this.isVirtual = isVirtual; }
        public String getVirtualMeetingLink() { return virtualMeetingLink; }
        public void setVirtualMeetingLink(String virtualMeetingLink) { this.virtualMeetingLink = virtualMeetingLink; }
    }

    public static class EventSearchRequest {
        private String searchTerm;
        private String eventType;
        private String location;
        private String skillLevel;
        private Pageable pageable;
        
        // Getters and setters
        public String getSearchTerm() { return searchTerm; }
        public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }
        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getSkillLevel() { return skillLevel; }
        public void setSkillLevel(String skillLevel) { this.skillLevel = skillLevel; }
        public Pageable getPageable() { return pageable; }
        public void setPageable(Pageable pageable) { this.pageable = pageable; }
    }
}