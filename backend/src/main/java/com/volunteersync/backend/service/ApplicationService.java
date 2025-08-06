// backend/src/main/java/com/volunteersync/backend/service/ApplicationService.java
package com.volunteersync.backend.service;

import com.volunteersync.backend.entity.Application;
import com.volunteersync.backend.entity.Event;
import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.entity.VolunteerProfile;
import com.volunteersync.backend.entity.OrganizationProfile;
import com.volunteersync.backend.enums.ApplicationStatus;
import com.volunteersync.backend.enums.UserType;
import com.volunteersync.backend.enums.EventStatus;
import com.volunteersync.backend.repository.ApplicationRepository;
import com.volunteersync.backend.repository.EventRepository;
import com.volunteersync.backend.repository.UserRepository;
import com.volunteersync.backend.repository.VolunteerProfileRepository;
import com.volunteersync.backend.repository.OrganizationProfileRepository;
import com.volunteersync.backend.dto.ApplicationDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service - handles volunteer application management
 * Manages the lifecycle of volunteer applications to events
 */
@Service
@Transactional
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VolunteerProfileRepository volunteerProfileRepository;
    
    @Autowired
    private OrganizationProfileRepository organizationProfileRepository;

    // ==========================================
    // VOLUNTEER APPLICATION METHODS
    // ==========================================

    /**
     * Submit application for an event
     */
    public ApplicationDTO submitApplication(SubmitApplicationRequest request, Long volunteerId) {
        System.out.println("Volunteer ID: " + volunteerId + " applying for event ID: " + request.getEventId());
        
        // Verify volunteer exists and is a volunteer
        User volunteer = userRepository.findById(volunteerId)
                .orElseThrow(() -> new RuntimeException("Volunteer not found"));
        
        if (volunteer.getUserType() != UserType.VOLUNTEER) {
            throw new RuntimeException("Only volunteers can submit applications");
        }
        
        // Get volunteer profile
        VolunteerProfile volunteerProfile = volunteerProfileRepository.findByUser(volunteer)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));
        
        // Verify event exists and is active
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        if (event.getStatus() != EventStatus.ACTIVE) {
            throw new RuntimeException("Cannot apply to inactive events");
        }
        
        // Check if already applied
        if (applicationRepository.existsByVolunteerAndEvent(volunteerProfile, event)) {
            throw new RuntimeException("Already applied to this event");
        }
        
        // Check if event is full
        if (event.isFull()) {
            throw new RuntimeException("Event is full");
        }
        
        // Create application
        Application application = new Application(volunteerProfile, event, request.getMessage());
        Application savedApplication = applicationRepository.save(application);
        
        System.out.println("Application submitted successfully with ID: " + savedApplication.getId());
        return convertToDTO(savedApplication);
    }

    /**
     * Withdraw application
     */
    public String withdrawApplication(Long applicationId, Long volunteerId) {
        System.out.println("Withdrawing application ID: " + applicationId + " by volunteer ID: " + volunteerId);
        
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        // Verify ownership
        if (!application.getVolunteer().getUser().getId().equals(volunteerId)) {
            throw new RuntimeException("You can only withdraw your own applications");
        }
        
        // Check if can be withdrawn
        if (!application.canBeWithdrawn()) {
            throw new RuntimeException("Application cannot be withdrawn at this time");
        }
        
        // Withdraw application
        application.withdraw();
        applicationRepository.save(application);
        
        System.out.println("Application withdrawn successfully");
        return "Application withdrawn successfully";
    }

    /**
     * Get volunteer's applications
     */
    public List<ApplicationDTO> getVolunteerApplications(Long volunteerId) {
        System.out.println("Fetching applications for volunteer ID: " + volunteerId);
        
        User volunteer = userRepository.findById(volunteerId)
                .orElseThrow(() -> new RuntimeException("Volunteer not found"));
        
        VolunteerProfile volunteerProfile = volunteerProfileRepository.findByUser(volunteer)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));
        
        List<Application> applications = applicationRepository.findByVolunteerOrderByAppliedAtDesc(volunteerProfile);
        
        return applications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get volunteer's applications by status
     */
    public List<ApplicationDTO> getVolunteerApplicationsByStatus(Long volunteerId, ApplicationStatus status) {
        System.out.println("Fetching " + status + " applications for volunteer ID: " + volunteerId);
        
        User volunteer = userRepository.findById(volunteerId)
                .orElseThrow(() -> new RuntimeException("Volunteer not found"));
        
        VolunteerProfile volunteerProfile = volunteerProfileRepository.findByUser(volunteer)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));
        
        List<Application> applications = applicationRepository.findByVolunteerAndStatus(volunteerProfile, status);
        
        return applications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // ORGANIZATION APPLICATION MANAGEMENT
    // ==========================================

    /**
     * Get applications for organization's events
     */
    public List<ApplicationDTO> getOrganizationApplications(Long organizerId) {
        System.out.println("Fetching applications for organization ID: " + organizerId);
        
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));
        
        if (organizer.getUserType() != UserType.ORGANIZATION) {
            throw new RuntimeException("Only organizations can view organization applications");
        }
        
        OrganizationProfile orgProfile = organizationProfileRepository.findByUser(organizer)
                .orElseThrow(() -> new RuntimeException("Organization profile not found"));
        
        List<Application> applications = applicationRepository.findApplicationsByOrganization(orgProfile.getId());
        
        return applications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get pending applications for organization
     */
    public List<ApplicationDTO> getPendingApplicationsForOrganization(Long organizerId) {
        System.out.println("Fetching pending applications for organization ID: " + organizerId);
        
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));
        
        OrganizationProfile orgProfile = organizationProfileRepository.findByUser(organizer)
                .orElseThrow(() -> new RuntimeException("Organization profile not found"));
        
        List<Application> applications = applicationRepository.findPendingApplicationsByOrganization(orgProfile.getId());
        
        return applications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get applications for a specific event
     */
    public List<ApplicationDTO> getEventApplications(Long eventId, Long organizerId) {
        System.out.println("Fetching applications for event ID: " + eventId + " by organizer: " + organizerId);
        
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        // Verify organizer owns the event
        if (!event.getOrganization().getUser().getId().equals(organizerId)) {
            throw new RuntimeException("You can only view applications for your own events");
        }
        
        List<Application> applications = applicationRepository.findByEventOrderByAppliedAtAsc(event);
        
        return applications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Approve application
     */
    public ApplicationDTO approveApplication(Long applicationId, Long organizerId, String notes) {
        System.out.println("Approving application ID: " + applicationId + " by organizer: " + organizerId);
        
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        // Verify organizer owns the event
        if (!application.getEvent().getOrganization().getUser().getId().equals(organizerId)) {
            throw new RuntimeException("You can only manage applications for your own events");
        }
        
        // Check if application can be approved
        if (!application.isPending()) {
            throw new RuntimeException("Only pending applications can be approved");
        }
        
        // Check event capacity
        Event event = application.getEvent();
        if (event.isFull()) {
            throw new RuntimeException("Event is already at full capacity");
        }
        
        // Approve application
        application.approve(notes);
        
        // Update event volunteer count
        event.setCurrentVolunteers(event.getCurrentVolunteers() + 1);
        eventRepository.save(event);
        
        Application savedApplication = applicationRepository.save(application);
        
        System.out.println("Application approved successfully");
        return convertToDTO(savedApplication);
    }

    /**
     * Reject application
     */
    public ApplicationDTO rejectApplication(Long applicationId, Long organizerId, String notes) {
        System.out.println("Rejecting application ID: " + applicationId + " by organizer: " + organizerId);
        
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        // Verify organizer owns the event
        if (!application.getEvent().getOrganization().getUser().getId().equals(organizerId)) {
            throw new RuntimeException("You can only manage applications for your own events");
        }
        
        // Check if application can be rejected
        if (!application.isPending()) {
            throw new RuntimeException("Only pending applications can be rejected");
        }
        
        // Reject application
        application.reject(notes);
        Application savedApplication = applicationRepository.save(application);
        
        System.out.println("Application rejected successfully");
        return convertToDTO(savedApplication);
    }

    /**
     * Mark volunteer as attended
     */
    public ApplicationDTO markAttended(Long applicationId, Long organizerId, Integer hoursCompleted) {
        System.out.println("Marking application ID: " + applicationId + " as attended with " + hoursCompleted + " hours");
        
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        // Verify organizer owns the event
        if (!application.getEvent().getOrganization().getUser().getId().equals(organizerId)) {
            throw new RuntimeException("You can only manage applications for your own events");
        }
        
        // Check if application is approved
        if (!application.isApproved()) {
            throw new RuntimeException("Only approved applications can be marked as attended");
        }
        
        // Mark as attended
        application.markAttended(hoursCompleted);
        
        // Update volunteer profile hours
        VolunteerProfile volunteerProfile = application.getVolunteer();
        volunteerProfile.setTotalVolunteerHours(
                volunteerProfile.getTotalVolunteerHours() + hoursCompleted);
        volunteerProfile.setEventsParticipated(
                volunteerProfile.getEventsParticipated() + 1);
        volunteerProfileRepository.save(volunteerProfile);
        
        Application savedApplication = applicationRepository.save(application);
        
        System.out.println("Application marked as attended successfully");
        return convertToDTO(savedApplication);
    }

    /**
     * Mark volunteer as no-show
     */
    public ApplicationDTO markNoShow(Long applicationId, Long organizerId) {
        System.out.println("Marking application ID: " + applicationId + " as no-show");
        
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        // Verify organizer owns the event
        if (!application.getEvent().getOrganization().getUser().getId().equals(organizerId)) {
            throw new RuntimeException("You can only manage applications for your own events");
        }
        
        // Check if application is approved
        if (!application.isApproved()) {
            throw new RuntimeException("Only approved applications can be marked as no-show");
        }
        
        // Mark as no-show
        application.markNoShow();
        
        // Update event volunteer count (remove from current count)
        Event event = application.getEvent();
        event.setCurrentVolunteers(Math.max(0, event.getCurrentVolunteers() - 1));
        eventRepository.save(event);
        
        Application savedApplication = applicationRepository.save(application);
        
        System.out.println("Application marked as no-show successfully");
        return convertToDTO(savedApplication);
    }

    // ==========================================
    // STATISTICS AND ANALYTICS
    // ==========================================

    /**
     * Get volunteer statistics
     */
    public VolunteerStatsResponse getVolunteerStats(Long volunteerId) {
        System.out.println("Fetching statistics for volunteer ID: " + volunteerId);
        
        User volunteer = userRepository.findById(volunteerId)
                .orElseThrow(() -> new RuntimeException("Volunteer not found"));
        
        VolunteerProfile volunteerProfile = volunteerProfileRepository.findByUser(volunteer)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));
        
        Integer totalHours = applicationRepository.getTotalHoursForVolunteer(volunteerProfile.getId());
        Long completedEvents = applicationRepository.countCompletedApplicationsForVolunteer(volunteerProfile.getId());
        
        Object[] attendanceStats = applicationRepository.getVolunteerAttendanceStats(volunteerProfile.getId());
        
        VolunteerStatsResponse stats = new VolunteerStatsResponse();
        stats.setTotalHours(totalHours != null ? totalHours : 0);
        stats.setCompletedEvents(completedEvents);
        
        if (attendanceStats != null && attendanceStats.length >= 3) {
            Long attended = (Long) attendanceStats[0];
            Long noShow = (Long) attendanceStats[1];
            Long total = (Long) attendanceStats[2];
            
            stats.setAttendedCount(attended);
            stats.setNoShowCount(noShow);
            stats.setAttendanceRate(total > 0 ? (attended.doubleValue() / total.doubleValue()) * 100 : 0.0);
        }
        
        return stats;
    }

    /**
     * Get application by ID (with permission check)
     */
    public ApplicationDTO getApplicationById(Long applicationId, Long userId) {
        System.out.println("Fetching application ID: " + applicationId + " for user: " + userId);
        
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check permissions
        boolean hasPermission = false;
        
        if (user.getUserType() == UserType.VOLUNTEER) {
            VolunteerProfile volunteerProfile = volunteerProfileRepository.findByUser(user)
                    .orElse(null);
            hasPermission = volunteerProfile != null && 
                    application.getVolunteer().getId().equals(volunteerProfile.getId());
        } else if (user.getUserType() == UserType.ORGANIZATION) {
            OrganizationProfile orgProfile = organizationProfileRepository.findByUser(user)
                    .orElse(null);
            hasPermission = orgProfile != null && 
                    application.getEvent().getOrganization().getId().equals(orgProfile.getId());
        }
        
        if (!hasPermission) {
            throw new RuntimeException("You do not have permission to view this application");
        }
        
        return convertToDTO(application);
    }

    // ==========================================
    // PRIVATE HELPER METHODS
    // ==========================================

    private ApplicationDTO convertToDTO(Application application) {
        ApplicationDTO dto = new ApplicationDTO();
        
        // Basic fields
        dto.setId(application.getId());
        dto.setStatus(application.getStatus());
        dto.setMessage(application.getMessage());
        dto.setOrganizationNotes(application.getOrganizationNotes());
        dto.setHoursCompleted(application.getHoursCompleted());
        dto.setAppliedAt(application.getAppliedAt());
        dto.setRespondedAt(application.getRespondedAt());
        dto.setCompletedAt(application.getCompletedAt());
        
        // Computed fields
        dto.setCanBeWithdrawn(application.canBeWithdrawn());
        dto.setIsPending(application.isPending());
        dto.setIsCompleted(application.isCompleted());
        
        // Volunteer information
        if (application.getVolunteer() != null) {
            dto.setVolunteerId(application.getVolunteer().getUser().getId());
            dto.setVolunteerName(application.getVolunteer().getFullName());
        }
        
        // Event information
        if (application.getEvent() != null) {
            dto.setEventId(application.getEvent().getId());
            dto.setEventTitle(application.getEvent().getTitle());
            dto.setEventStartDate(application.getEvent().getStartDate());
            
            if (application.getEvent().getOrganization() != null) {
                dto.setOrganizationName(application.getEvent().getOrganization().getOrganizationName());
            }
        }
        
        return dto;
    }

    // ==========================================
    // REQUEST/RESPONSE CLASSES
    // ==========================================

    public static class SubmitApplicationRequest {
        private Long eventId;
        private String message;
        
        // Getters and setters
        public Long getEventId() { return eventId; }
        public void setEventId(Long eventId) { this.eventId = eventId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class VolunteerStatsResponse {
        private Integer totalHours;
        private Long completedEvents;
        private Long attendedCount;
        private Long noShowCount;
        private Double attendanceRate;
        
        // Getters and setters
        public Integer getTotalHours() { return totalHours; }
        public void setTotalHours(Integer totalHours) { this.totalHours = totalHours; }
        public Long getCompletedEvents() { return completedEvents; }
        public void setCompletedEvents(Long completedEvents) { this.completedEvents = completedEvents; }
        public Long getAttendedCount() { return attendedCount; }
        public void setAttendedCount(Long attendedCount) { this.attendedCount = attendedCount; }
        public Long getNoShowCount() { return noShowCount; }
        public void setNoShowCount(Long noShowCount) { this.noShowCount = noShowCount; }
        public Double getAttendanceRate() { return attendanceRate; }
        public void setAttendanceRate(Double attendanceRate) { this.attendanceRate = attendanceRate; }
    }
}