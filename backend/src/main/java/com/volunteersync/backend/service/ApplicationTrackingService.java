// backend/src/main/java/com/volunteersync/backend/service/ApplicationTrackingService.java
package com.volunteersync.backend.service;

import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.entity.Application;
import com.volunteersync.backend.entity.Event;
import com.volunteersync.backend.entity.VolunteerProfile;
import com.volunteersync.backend.enums.UserType;
import com.volunteersync.backend.enums.ApplicationStatus;
import com.volunteersync.backend.enums.EventStatus;
import com.volunteersync.backend.repository.UserRepository;
import com.volunteersync.backend.repository.ApplicationRepository;
import com.volunteersync.backend.repository.EventRepository;
import com.volunteersync.backend.repository.VolunteerProfileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Application Tracking Service - handles volunteer application tracking and status management
 * Provides comprehensive tracking functionality for volunteers to monitor their applications
 */
@Service
@Transactional
public class ApplicationTrackingService {

    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VolunteerProfileRepository volunteerProfileRepository;

    // ==========================================
    // APPLICATION TRACKING METHODS
    // ==========================================

    /**
     * Get all applications for a volunteer with detailed tracking information
     */
    public VolunteerApplicationsResponse getVolunteerApplications(Long volunteerId, ApplicationTrackingFilter filter, Pageable pageable) {
        System.out.println("Getting applications for volunteer ID: " + volunteerId + " with filter: " + filter);
        
        // Get volunteer profile
        VolunteerProfile volunteer = volunteerProfileRepository.findByUserId(volunteerId)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));

        // Get all applications for the volunteer
        List<Application> allApplications = applicationRepository.findByVolunteer(volunteer);
        
        // Apply filters
        List<Application> filteredApplications = applyFilters(allApplications, filter);
        
        // Sort applications (most recent first)
        filteredApplications.sort((a, b) -> b.getAppliedAt().compareTo(a.getAppliedAt()));
        
        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredApplications.size());
        List<Application> paginatedApplications = filteredApplications.subList(start, end);
        
        // Convert to tracking DTOs
        List<ApplicationTrackingDTO> trackingDTOs = paginatedApplications.stream()
                .map(this::convertToTrackingDTO)
                .collect(Collectors.toList());

        // Build response with summary statistics
        VolunteerApplicationsResponse response = new VolunteerApplicationsResponse();
        response.setApplications(trackingDTOs);
        response.setTotalElements(filteredApplications.size());
        response.setTotalPages((int) Math.ceil((double) filteredApplications.size() / pageable.getPageSize()));
        response.setCurrentPage(pageable.getPageNumber());
        response.setSummary(generateApplicationSummary(allApplications));
        
        return response;
    }

    /**
     * Get detailed tracking information for a specific application
     */
    public ApplicationTrackingDetailResponse getApplicationTrackingDetails(Long applicationId, Long volunteerId) {
        System.out.println("Getting tracking details for application ID: " + applicationId);
        
        // Get application and verify ownership
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        if (!application.getVolunteer().getUser().getId().equals(volunteerId)) {
            throw new RuntimeException("Not authorized to view this application");
        }

        // Create detailed response
        ApplicationTrackingDetailResponse response = new ApplicationTrackingDetailResponse();
        response.setApplicationId(application.getId());
        response.setCurrentStatus(application.getStatus());
        response.setEvent(convertToEventSummary(application.getEvent()));
        response.setTimeline(generateApplicationTimeline(application));
        response.setVolunteerMessage(application.getMessage());
        response.setOrganizationNotes(application.getOrganizationNotes());
        response.setSubmittedAt(application.getAppliedAt());
        response.setLastUpdated(application.getRespondedAt() != null ? application.getRespondedAt() : application.getAppliedAt());
        response.setEstimatedResponse(calculateEstimatedResponseTime(application));
        response.setNextSteps(generateNextSteps(application));
        
        return response;
    }

    /**
     * Get application status statistics for a volunteer
     */
    public ApplicationStatusSummary getApplicationStatusSummary(Long volunteerId) {
        System.out.println("Getting application status summary for volunteer ID: " + volunteerId);
        
        VolunteerProfile volunteer = volunteerProfileRepository.findByUserId(volunteerId)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));
        
        List<Application> applications = applicationRepository.findByVolunteer(volunteer);
        
        ApplicationStatusSummary summary = new ApplicationStatusSummary();
        summary.setTotalApplications(applications.size());
        
        // Count by status
        Map<ApplicationStatus, Integer> statusCounts = new HashMap<>();
        for (ApplicationStatus status : ApplicationStatus.values()) {
            int count = (int) applications.stream().filter(a -> a.getStatus() == status).count();
            statusCounts.put(status, count);
        }
        summary.setStatusBreakdown(statusCounts);
        
        // Calculate success rate
        int acceptedCount = statusCounts.getOrDefault(ApplicationStatus.ACCEPTED, 0);
        int rejectedCount = statusCounts.getOrDefault(ApplicationStatus.REJECTED, 0);
        int totalDecided = acceptedCount + rejectedCount;
        
        if (totalDecided > 0) {
            double successRate = (double) acceptedCount / totalDecided * 100;
            summary.setSuccessRate(successRate);
        } else {
            summary.setSuccessRate(0.0);
        }
        
        // Recent activity
        List<Application> recentApplications = applications.stream()
                .filter(app -> {
                    LocalDateTime lastUpdate = app.getRespondedAt() != null ? app.getRespondedAt() : app.getAppliedAt();
                    return lastUpdate.isAfter(LocalDateTime.now().minusDays(7));
                })
                .collect(Collectors.toList());
        summary.setRecentActivity(recentApplications.size());
        
        // Pending response count
        summary.setPendingResponses(statusCounts.getOrDefault(ApplicationStatus.PENDING, 0));
        
        return summary;
    }

    /**
     * Get upcoming events and deadlines for a volunteer
     */
    public UpcomingEventsResponse getUpcomingEvents(Long volunteerId) {
        System.out.println("Getting upcoming events for volunteer ID: " + volunteerId);
        
        VolunteerProfile volunteer = volunteerProfileRepository.findByUserId(volunteerId)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));
        
        List<Application> acceptedApplications = applicationRepository
                .findByVolunteerAndStatus(volunteer, ApplicationStatus.ACCEPTED);
        
        List<UpcomingEventInfo> upcomingEvents = acceptedApplications.stream()
                .map(Application::getEvent)
                .filter(event -> event.getStartDate().isAfter(LocalDateTime.now()))
                .filter(event -> event.getStatus() == EventStatus.ACTIVE)
                .sorted((a, b) -> a.getStartDate().compareTo(b.getStartDate()))
                .map(this::convertToUpcomingEventInfo)
                .collect(Collectors.toList());

        UpcomingEventsResponse response = new UpcomingEventsResponse();
        response.setUpcomingEvents(upcomingEvents);
        response.setTotalUpcoming(upcomingEvents.size());
        
        // Find next event
        if (!upcomingEvents.isEmpty()) {
            response.setNextEvent(upcomingEvents.get(0));
        }
        
        return response;
    }

    /**
     * Withdraw an application
     */
    public ApplicationWithdrawResponse withdrawApplication(Long applicationId, Long volunteerId, WithdrawApplicationRequest request) {
        System.out.println("Withdrawing application ID: " + applicationId + " for volunteer: " + volunteerId);
        
        // Get application and verify ownership
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        if (!application.getVolunteer().getUser().getId().equals(volunteerId)) {
            throw new RuntimeException("Not authorized to withdraw this application");
        }
        
        // Check if withdrawal is allowed
        if (application.getStatus() == ApplicationStatus.WITHDRAWN) {
            throw new RuntimeException("Application is already withdrawn");
        }
        
        if (application.getStatus() == ApplicationStatus.ACCEPTED && 
            application.getEvent().getStartDate().isBefore(LocalDateTime.now().plusDays(1))) {
            throw new RuntimeException("Cannot withdraw from an event starting within 24 hours");
        }

        // Update application status
        ApplicationStatus oldStatus = application.getStatus();
        application.setStatus(ApplicationStatus.WITHDRAWN);
        application.setRespondedAt(LocalDateTime.now());
        
        if (request.getWithdrawalReason() != null) {
            String currentMessage = application.getMessage() != null ? application.getMessage() : "";
            application.setMessage(currentMessage + 
                "\n\nWithdrawal Reason: " + request.getWithdrawalReason());
        }
        
        applicationRepository.save(application);

        ApplicationWithdrawResponse response = new ApplicationWithdrawResponse();
        response.setApplicationId(applicationId);
        response.setOldStatus(oldStatus);
        response.setNewStatus(ApplicationStatus.WITHDRAWN);
        response.setWithdrawnAt(application.getRespondedAt());
        response.setMessage("Application withdrawn successfully");
        
        return response;
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    private List<Application> applyFilters(List<Application> applications, ApplicationTrackingFilter filter) {
        if (filter == null) {
            return applications;
        }

        return applications.stream()
                .filter(app -> {
                    // Status filter
                    if (filter.getStatus() != null && app.getStatus() != filter.getStatus()) {
                        return false;
                    }
                    
                    // Date range filter
                    if (filter.getStartDate() != null && app.getAppliedAt().isBefore(filter.getStartDate())) {
                        return false;
                    }
                    
                    if (filter.getEndDate() != null && app.getAppliedAt().isAfter(filter.getEndDate())) {
                        return false;
                    }
                    
                    // Event title search
                    if (filter.getEventTitleSearch() != null && !filter.getEventTitleSearch().trim().isEmpty()) {
                        String searchTerm = filter.getEventTitleSearch().toLowerCase();
                        if (!app.getEvent().getTitle().toLowerCase().contains(searchTerm)) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());
    }

    private ApplicationTrackingDTO convertToTrackingDTO(Application application) {
        ApplicationTrackingDTO dto = new ApplicationTrackingDTO();
        dto.setApplicationId(application.getId());
        dto.setStatus(application.getStatus());
        dto.setSubmittedAt(application.getAppliedAt());
        dto.setLastUpdated(application.getRespondedAt() != null ? application.getRespondedAt() : application.getAppliedAt());
        dto.setEvent(convertToEventSummary(application.getEvent()));
        dto.setVolunteerMessage(application.getMessage());
        dto.setOrganizationNotes(application.getOrganizationNotes());
        dto.setCanWithdraw(canWithdrawApplication(application));
        dto.setDaysUntilEvent(calculateDaysUntilEvent(application.getEvent()));
        dto.setStatusColor(getStatusColor(application.getStatus()));
        dto.setStatusDescription(getStatusDescription(application.getStatus()));
        
        return dto;
    }

    private EventSummary convertToEventSummary(Event event) {
        EventSummary summary = new EventSummary();
        summary.setEventId(event.getId());
        summary.setTitle(event.getTitle());
        summary.setDescription(event.getDescription());
        summary.setEventDate(event.getStartDate());
        summary.setLocation(event.getLocation());
        summary.setEventType(event.getEventType());
        summary.setStatus(event.getStatus());
        summary.setOrganizationName(event.getOrganization().getOrganizationName());
        
        return summary;
    }

    private ApplicationSummary generateApplicationSummary(List<Application> applications) {
        ApplicationSummary summary = new ApplicationSummary();
        summary.setTotalApplications(applications.size());
        
        // Count by status
        Map<ApplicationStatus, Integer> statusCounts = new HashMap<>();
        for (ApplicationStatus status : ApplicationStatus.values()) {
            int count = (int) applications.stream().filter(a -> a.getStatus() == status).count();
            statusCounts.put(status, count);
        }
        summary.setStatusCounts(statusCounts);
        
        // Recent applications (last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        int recentCount = (int) applications.stream()
                .filter(app -> app.getAppliedAt().isAfter(thirtyDaysAgo))
                .count();
        summary.setRecentApplications(recentCount);
        
        return summary;
    }

    private List<ApplicationTimelineEvent> generateApplicationTimeline(Application application) {
        List<ApplicationTimelineEvent> timeline = new java.util.ArrayList<>();
        
        // Application submitted
        timeline.add(new ApplicationTimelineEvent(
            "Application Submitted",
            "Your application was successfully submitted",
            application.getAppliedAt(),
            "SUBMITTED"
        ));
        
        // Status changes (simplified - in a real system you'd track these)
        if (application.getStatus() != ApplicationStatus.PENDING) {
            LocalDateTime statusChangeTime = application.getRespondedAt() != null ? 
                application.getRespondedAt() : application.getAppliedAt();
            timeline.add(new ApplicationTimelineEvent(
                "Status Updated",
                "Application status changed to " + application.getStatus().getDisplayName(),
                statusChangeTime,
                application.getStatus().toString()
            ));
        }
        
        // Future events based on status
        if (application.getStatus() == ApplicationStatus.ACCEPTED) {
            timeline.add(new ApplicationTimelineEvent(
                "Event Participation",
                "Participate in " + application.getEvent().getTitle(),
                application.getEvent().getStartDate(),
                "UPCOMING"
            ));
        }
        
        return timeline;
    }

    private String calculateEstimatedResponseTime(Application application) {
        if (application.getStatus() != ApplicationStatus.PENDING) {
            return "Response received";
        }
        
        // Simple estimation based on days since application
        long daysSinceApplication = java.time.temporal.ChronoUnit.DAYS.between(
            application.getAppliedAt(), LocalDateTime.now());
        
        if (daysSinceApplication < 3) {
            return "Response expected within 1-2 days";
        } else if (daysSinceApplication < 7) {
            return "Response expected soon";
        } else {
            return "Response may be delayed - consider following up";
        }
    }

    private List<String> generateNextSteps(Application application) {
        List<String> nextSteps = new java.util.ArrayList<>();
        
        switch (application.getStatus()) {
            case PENDING:
                nextSteps.add("Wait for organization to review your application");
                nextSteps.add("Prepare for potential follow-up questions");
                break;
            case ACCEPTED:
                nextSteps.add("Mark your calendar for " + application.getEvent().getStartDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
                nextSteps.add("Review event details and requirements");
                nextSteps.add("Contact organization if you have questions");
                break;
            case REJECTED:
                nextSteps.add("Consider applying to similar events");
                nextSteps.add("Review feedback if provided");
                break;
            case WITHDRAWN:
                nextSteps.add("Look for other volunteer opportunities");
                break;
        }
        
        return nextSteps;
    }

    private UpcomingEventInfo convertToUpcomingEventInfo(Event event) {
        UpcomingEventInfo info = new UpcomingEventInfo();
        info.setEventId(event.getId());
        info.setTitle(event.getTitle());
        info.setEventDate(event.getStartDate());
        info.setLocation(event.getLocation());
        info.setOrganizationName(event.getOrganization().getOrganizationName());
        info.setDaysUntilEvent(calculateDaysUntilEvent(event));
        info.setEventType(event.getEventType());
        
        return info;
    }

    private boolean canWithdrawApplication(Application application) {
        if (application.getStatus() == ApplicationStatus.WITHDRAWN) {
            return false;
        }
        
        // Can't withdraw if event is within 24 hours and accepted
        if (application.getStatus() == ApplicationStatus.ACCEPTED) {
            return application.getEvent().getStartDate().isAfter(LocalDateTime.now().plusDays(1));
        }
        
        // Can withdraw pending or rejected applications
        return application.getStatus() == ApplicationStatus.PENDING || 
               application.getStatus() == ApplicationStatus.REJECTED;
    }

    private long calculateDaysUntilEvent(Event event) {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), event.getStartDate());
    }

    private String getStatusColor(ApplicationStatus status) {
        switch (status) {
            case PENDING: return "#f59e0b"; // yellow
            case ACCEPTED: return "#10b981"; // green
            case REJECTED: return "#ef4444"; // red
            case WITHDRAWN: return "#6b7280"; // gray
            default: return "#6b7280";
        }
    }

    private String getStatusDescription(ApplicationStatus status) {
        switch (status) {
            case PENDING: return "Your application is being reviewed";
            case ACCEPTED: return "Congratulations! Your application was approved";
            case REJECTED: return "Your application was not accepted this time";
            case WITHDRAWN: return "You withdrew your application";
            default: return "Unknown status";
        }
    }

    // ==========================================
    // REQUEST/RESPONSE CLASSES
    // ==========================================

    public static class ApplicationTrackingFilter {
        private ApplicationStatus status;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String eventTitleSearch;

        // Getters and setters
        public ApplicationStatus getStatus() { return status; }
        public void setStatus(ApplicationStatus status) { this.status = status; }
        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
        public String getEventTitleSearch() { return eventTitleSearch; }
        public void setEventTitleSearch(String eventTitleSearch) { this.eventTitleSearch = eventTitleSearch; }
    }

    public static class VolunteerApplicationsResponse {
        private List<ApplicationTrackingDTO> applications;
        private int totalElements;
        private int totalPages;
        private int currentPage;
        private ApplicationSummary summary;

        // Getters and setters
        public List<ApplicationTrackingDTO> getApplications() { return applications; }
        public void setApplications(List<ApplicationTrackingDTO> applications) { this.applications = applications; }
        public int getTotalElements() { return totalElements; }
        public void setTotalElements(int totalElements) { this.totalElements = totalElements; }
        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
        public int getCurrentPage() { return currentPage; }
        public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }
        public ApplicationSummary getSummary() { return summary; }
        public void setSummary(ApplicationSummary summary) { this.summary = summary; }
    }

    public static class ApplicationTrackingDTO {
        private Long applicationId;
        private ApplicationStatus status;
        private LocalDateTime submittedAt;
        private LocalDateTime lastUpdated;
        private EventSummary event;
        private String volunteerMessage;
        private String organizationNotes;
        private boolean canWithdraw;
        private long daysUntilEvent;
        private String statusColor;
        private String statusDescription;

        // Getters and setters
        public Long getApplicationId() { return applicationId; }
        public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
        public ApplicationStatus getStatus() { return status; }
        public void setStatus(ApplicationStatus status) { this.status = status; }
        public LocalDateTime getSubmittedAt() { return submittedAt; }
        public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
        public LocalDateTime getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
        public EventSummary getEvent() { return event; }
        public void setEvent(EventSummary event) { this.event = event; }
        public String getVolunteerMessage() { return volunteerMessage; }
        public void setVolunteerMessage(String volunteerMessage) { this.volunteerMessage = volunteerMessage; }
        public String getOrganizationNotes() { return organizationNotes; }
        public void setOrganizationNotes(String organizationNotes) { this.organizationNotes = organizationNotes; }
        public boolean isCanWithdraw() { return canWithdraw; }
        public void setCanWithdraw(boolean canWithdraw) { this.canWithdraw = canWithdraw; }
        public long getDaysUntilEvent() { return daysUntilEvent; }
        public void setDaysUntilEvent(long daysUntilEvent) { this.daysUntilEvent = daysUntilEvent; }
        public String getStatusColor() { return statusColor; }
        public void setStatusColor(String statusColor) { this.statusColor = statusColor; }
        public String getStatusDescription() { return statusDescription; }
        public void setStatusDescription(String statusDescription) { this.statusDescription = statusDescription; }
    }

    public static class ApplicationTrackingDetailResponse {
        private Long applicationId;
        private ApplicationStatus currentStatus;
        private EventSummary event;
        private List<ApplicationTimelineEvent> timeline;
        private String volunteerMessage;
        private String organizationNotes;
        private LocalDateTime submittedAt;
        private LocalDateTime lastUpdated;
        private String estimatedResponse;
        private List<String> nextSteps;

        // Getters and setters
        public Long getApplicationId() { return applicationId; }
        public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
        public ApplicationStatus getCurrentStatus() { return currentStatus; }
        public void setCurrentStatus(ApplicationStatus currentStatus) { this.currentStatus = currentStatus; }
        public EventSummary getEvent() { return event; }
        public void setEvent(EventSummary event) { this.event = event; }
        public List<ApplicationTimelineEvent> getTimeline() { return timeline; }
        public void setTimeline(List<ApplicationTimelineEvent> timeline) { this.timeline = timeline; }
        public String getVolunteerMessage() { return volunteerMessage; }
        public void setVolunteerMessage(String volunteerMessage) { this.volunteerMessage = volunteerMessage; }
        public String getOrganizationNotes() { return organizationNotes; }
        public void setOrganizationNotes(String organizationNotes) { this.organizationNotes = organizationNotes; }
        public LocalDateTime getSubmittedAt() { return submittedAt; }
        public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
        public LocalDateTime getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
        public String getEstimatedResponse() { return estimatedResponse; }
        public void setEstimatedResponse(String estimatedResponse) { this.estimatedResponse = estimatedResponse; }
        public List<String> getNextSteps() { return nextSteps; }
        public void setNextSteps(List<String> nextSteps) { this.nextSteps = nextSteps; }
    }

    public static class EventSummary {
        private Long eventId;
        private String title;
        private String description;
        private LocalDateTime eventDate;
        private String location;
        private Object eventType;
        private Object status;
        private String organizationName;

        // Getters and setters
        public Long getEventId() { return eventId; }
        public void setEventId(Long eventId) { this.eventId = eventId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public LocalDateTime getEventDate() { return eventDate; }
        public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public Object getEventType() { return eventType; }
        public void setEventType(Object eventType) { this.eventType = eventType; }
        public Object getStatus() { return status; }
        public void setStatus(Object status) { this.status = status; }
        public String getOrganizationName() { return organizationName; }
        public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
    }

    public static class ApplicationSummary {
        private int totalApplications;
        private Map<ApplicationStatus, Integer> statusCounts;
        private int recentApplications;

        // Getters and setters
        public int getTotalApplications() { return totalApplications; }
        public void setTotalApplications(int totalApplications) { this.totalApplications = totalApplications; }
        public Map<ApplicationStatus, Integer> getStatusCounts() { return statusCounts; }
        public void setStatusCounts(Map<ApplicationStatus, Integer> statusCounts) { this.statusCounts = statusCounts; }
        public int getRecentApplications() { return recentApplications; }
        public void setRecentApplications(int recentApplications) { this.recentApplications = recentApplications; }
    }

    public static class ApplicationTimelineEvent {
        private String title;
        private String description;
        private LocalDateTime timestamp;
        private String type;

        public ApplicationTimelineEvent(String title, String description, LocalDateTime timestamp, String type) {
            this.title = title;
            this.description = description;
            this.timestamp = timestamp;
            this.type = type;
        }

        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    public static class ApplicationStatusSummary {
        private int totalApplications;
        private Map<ApplicationStatus, Integer> statusBreakdown;
        private double successRate;
        private int recentActivity;
        private int pendingResponses;

        // Getters and setters
        public int getTotalApplications() { return totalApplications; }
        public void setTotalApplications(int totalApplications) { this.totalApplications = totalApplications; }
        public Map<ApplicationStatus, Integer> getStatusBreakdown() { return statusBreakdown; }
        public void setStatusBreakdown(Map<ApplicationStatus, Integer> statusBreakdown) { this.statusBreakdown = statusBreakdown; }
        public double getSuccessRate() { return successRate; }
        public void setSuccessRate(double successRate) { this.successRate = successRate; }
        public int getRecentActivity() { return recentActivity; }
        public void setRecentActivity(int recentActivity) { this.recentActivity = recentActivity; }
        public int getPendingResponses() { return pendingResponses; }
        public void setPendingResponses(int pendingResponses) { this.pendingResponses = pendingResponses; }
    }

    public static class UpcomingEventsResponse {
        private List<UpcomingEventInfo> upcomingEvents;
        private int totalUpcoming;
        private UpcomingEventInfo nextEvent;

        // Getters and setters
        public List<UpcomingEventInfo> getUpcomingEvents() { return upcomingEvents; }
        public void setUpcomingEvents(List<UpcomingEventInfo> upcomingEvents) { this.upcomingEvents = upcomingEvents; }
        public int getTotalUpcoming() { return totalUpcoming; }
        public void setTotalUpcoming(int totalUpcoming) { this.totalUpcoming = totalUpcoming; }
        public UpcomingEventInfo getNextEvent() { return nextEvent; }
        public void setNextEvent(UpcomingEventInfo nextEvent) { this.nextEvent = nextEvent; }
    }

    public static class UpcomingEventInfo {
        private Long eventId;
        private String title;
        private LocalDateTime eventDate;
        private String location;
        private String organizationName;
        private long daysUntilEvent;
        private Object eventType;

        // Getters and setters
        public Long getEventId() { return eventId; }
        public void setEventId(Long eventId) { this.eventId = eventId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public LocalDateTime getEventDate() { return eventDate; }
        public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getOrganizationName() { return organizationName; }
        public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
        public long getDaysUntilEvent() { return daysUntilEvent; }
        public void setDaysUntilEvent(long daysUntilEvent) { this.daysUntilEvent = daysUntilEvent; }
        public Object getEventType() { return eventType; }
        public void setEventType(Object eventType) { this.eventType = eventType; }
    }

    public static class WithdrawApplicationRequest {
        private String withdrawalReason;

        // Getters and setters
        public String getWithdrawalReason() { return withdrawalReason; }
        public void setWithdrawalReason(String withdrawalReason) { this.withdrawalReason = withdrawalReason; }
    }

    public static class ApplicationWithdrawResponse {
        private Long applicationId;
        private ApplicationStatus oldStatus;
        private ApplicationStatus newStatus;
        private LocalDateTime withdrawnAt;
        private String message;

        // Getters and setters
        public Long getApplicationId() { return applicationId; }
        public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
        public ApplicationStatus getOldStatus() { return oldStatus; }
        public void setOldStatus(ApplicationStatus oldStatus) { this.oldStatus = oldStatus; }
        public ApplicationStatus getNewStatus() { return newStatus; }
        public void setNewStatus(ApplicationStatus newStatus) { this.newStatus = newStatus; }
        public LocalDateTime getWithdrawnAt() { return withdrawnAt; }
        public void setWithdrawnAt(LocalDateTime withdrawnAt) { this.withdrawnAt = withdrawnAt; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}