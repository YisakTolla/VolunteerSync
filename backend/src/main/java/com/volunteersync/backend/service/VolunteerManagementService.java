// backend/src/main/java/com/volunteersync/backend/service/VolunteerManagementService.java
package com.volunteersync.backend.service;

import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.entity.VolunteerProfile;
import com.volunteersync.backend.entity.Event;
import com.volunteersync.backend.entity.Application;
import com.volunteersync.backend.entity.OrganizationProfile;
import com.volunteersync.backend.enums.UserType;
import com.volunteersync.backend.enums.ApplicationStatus;
import com.volunteersync.backend.enums.EventStatus;
import com.volunteersync.backend.enums.SkillLevel;
import com.volunteersync.backend.repository.UserRepository;
import com.volunteersync.backend.repository.VolunteerProfileRepository;
import com.volunteersync.backend.repository.EventRepository;
import com.volunteersync.backend.repository.ApplicationRepository;
import com.volunteersync.backend.repository.OrganizationProfileRepository;
import com.volunteersync.backend.dto.VolunteerProfileDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Volunteer Management Service - handles volunteer management operations for organizations
 * Provides functionality for organizations to manage their volunteers across events
 */
@Service
@Transactional
public class VolunteerManagementService {

    @Autowired
    private VolunteerProfileRepository volunteerProfileRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private OrganizationProfileRepository organizationProfileRepository;

    // ==========================================
    // VOLUNTEER MANAGEMENT METHODS
    // ==========================================

    /**
     * Get all volunteers for an organization
     */
    public VolunteerManagementResponse getOrganizationVolunteers(Long organizationId, Pageable pageable) {
        System.out.println("Getting volunteers for organization ID: " + organizationId);
        
        // Verify organization exists
        User organization = userRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        
        if (organization.getUserType() != UserType.ORGANIZATION) {
            throw new RuntimeException("User is not an organization");
        }

        // Get all events created by this organization
        OrganizationProfile orgProfile = organizationProfileRepository.findByUserId(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization profile not found"));
        List<Event> organizationEvents = eventRepository.findByOrganization(orgProfile);
        
        // Get all volunteers who have applied to these events
        List<Application> applications = applicationRepository.findByEventIn(organizationEvents);
        
        // Group volunteers by application status
        Map<ApplicationStatus, List<VolunteerInfo>> volunteersByStatus = new HashMap<>();
        
        for (ApplicationStatus status : ApplicationStatus.values()) {
            List<Application> statusApplications = applications.stream()
                    .filter(app -> app.getStatus() == status)
                    .collect(Collectors.toList());
            
            List<VolunteerInfo> volunteers = statusApplications.stream()
                    .map(this::convertToVolunteerInfo)
                    .collect(Collectors.toList());
            
            volunteersByStatus.put(status, volunteers);
        }

        // Create response
        VolunteerManagementResponse response = new VolunteerManagementResponse();
        response.setVolunteersByStatus(volunteersByStatus);
        response.setTotalVolunteers(applications.size());
        response.setActiveVolunteers(volunteersByStatus.getOrDefault(ApplicationStatus.ACCEPTED, new ArrayList<>()).size());
        response.setPendingApplications(volunteersByStatus.getOrDefault(ApplicationStatus.PENDING, new ArrayList<>()).size());
        
        return response;
    }

    /**
     * Get detailed volunteer information
     */
    public VolunteerDetailResponse getVolunteerDetails(Long volunteerId, Long organizationId) {
        System.out.println("Getting volunteer details for ID: " + volunteerId);
        
        // Verify volunteer exists
        User volunteer = userRepository.findById(volunteerId)
                .orElseThrow(() -> new RuntimeException("Volunteer not found"));
        
        if (volunteer.getUserType() != UserType.VOLUNTEER) {
            throw new RuntimeException("User is not a volunteer");
        }

        // Get volunteer profile
        VolunteerProfile profile = volunteerProfileRepository.findByUser(volunteer)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));

        // Get applications to organization's events
        OrganizationProfile orgProfile = organizationProfileRepository.findByUserId(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization profile not found"));
        List<Event> organizationEvents = eventRepository.findByOrganization(orgProfile);
        List<Application> volunteerApplications = applicationRepository
                .findByVolunteerAndEventIn(profile, organizationEvents);

        // Convert to response
        VolunteerDetailResponse response = new VolunteerDetailResponse();
        response.setVolunteerId(volunteerId);
        response.setFirstName(profile.getFirstName());
        response.setLastName(profile.getLastName());
        response.setEmail(volunteer.getEmail());
        response.setBio(profile.getBio());
        response.setSkills(profile.getSkillsList());
        response.setInterests(profile.getInterestsList());
        response.setLocation(profile.getLocation());
        response.setAvailability(profile.getAvailabilityPreference());
        response.setApplications(volunteerApplications.stream()
                .map(this::convertToApplicationInfo)
                .collect(Collectors.toList()));
        response.setJoinedDate(volunteer.getCreatedAt());
        response.setLastActive(volunteer.getUpdatedAt());
        
        return response;
    }

    /**
     * Update volunteer status for an event
     */
    public ApplicationUpdateResponse updateVolunteerStatus(UpdateVolunteerStatusRequest request, Long organizationId) {
        System.out.println("Updating volunteer status - Application ID: " + request.getApplicationId() + 
                          ", Status: " + request.getNewStatus());
        
        // Get application
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        // Verify organization owns the event
        Event event = application.getEvent();
        if (!event.getOrganization().getUser().getId().equals(organizationId)) {
            throw new RuntimeException("Not authorized to update this application");
        }

        // Update status
        ApplicationStatus oldStatus = application.getStatus();
        application.setStatus(request.getNewStatus());
        
        if (request.getOrganizationNotes() != null) {
            application.setOrganizationNotes(request.getOrganizationNotes());
        }
        
        applicationRepository.save(application);

        // Create response
        ApplicationUpdateResponse response = new ApplicationUpdateResponse();
        response.setApplicationId(application.getId());
        response.setOldStatus(oldStatus);
        response.setNewStatus(request.getNewStatus());
        response.setUpdatedAt(LocalDateTime.now()); // Use current time since Application doesn't track updatedAt
        response.setMessage("Volunteer status updated successfully");
        
        return response;
    }

    /**
     * Bulk update volunteer statuses
     */
    public BulkUpdateResponse bulkUpdateVolunteerStatuses(BulkUpdateRequest request, Long organizationId) {
        System.out.println("Bulk updating " + request.getApplicationIds().size() + " applications");
        
        List<Application> applications = applicationRepository.findAllById(request.getApplicationIds());
        
        // Verify all applications belong to organization events
        for (Application application : applications) {
            if (!application.getEvent().getOrganization().getUser().getId().equals(organizationId)) {
                throw new RuntimeException("Not authorized to update application ID: " + application.getId());
            }
        }

        // Update all applications
        int updatedCount = 0;
        for (Application application : applications) {
            application.setStatus(request.getNewStatus());
            if (request.getOrganizationNotes() != null) {
                application.setOrganizationNotes(request.getOrganizationNotes());
            }
            applicationRepository.save(application);
            updatedCount++;
        }

        BulkUpdateResponse response = new BulkUpdateResponse();
        response.setUpdatedCount(updatedCount);
        response.setTotalRequested(request.getApplicationIds().size());
        response.setNewStatus(request.getNewStatus());
        response.setMessage(updatedCount + " applications updated successfully");
        
        return response;
    }

    /**
     * Search volunteers with advanced filters
     */
    public VolunteerSearchResponse searchVolunteers(VolunteerSearchRequest request, Long organizationId, Pageable pageable) {
        System.out.println("Searching volunteers with filters for organization: " + organizationId);
        
        // Get organization events
        OrganizationProfile orgProfile = organizationProfileRepository.findByUserId(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization profile not found"));
        List<Event> organizationEvents = eventRepository.findByOrganization(orgProfile);
        
        // Get all applications to organization events
        List<Application> allApplications = applicationRepository.findByEventIn(organizationEvents);
        
        // Filter applications based on search criteria
        List<Application> filteredApplications = allApplications.stream()
                .filter(app -> matchesSearchCriteria(app, request))
                .collect(Collectors.toList());

        // Convert to volunteer search results
        List<VolunteerSearchResult> results = filteredApplications.stream()
                .map(this::convertToSearchResult)
                .distinct() // Remove duplicates if volunteer applied to multiple events
                .collect(Collectors.toList());

        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), results.size());
        List<VolunteerSearchResult> paginatedResults = results.subList(start, end);

        VolunteerSearchResponse response = new VolunteerSearchResponse();
        response.setVolunteers(paginatedResults);
        response.setTotalElements(results.size());
        response.setTotalPages((int) Math.ceil((double) results.size() / pageable.getPageSize()));
        response.setCurrentPage(pageable.getPageNumber());
        
        return response;
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    private VolunteerInfo convertToVolunteerInfo(Application application) {
        VolunteerProfile profile = application.getVolunteer();
        User user = profile.getUser();
        
        VolunteerInfo info = new VolunteerInfo();
        info.setVolunteerId(user.getId());
        info.setFirstName(profile.getFirstName());
        info.setLastName(profile.getLastName());
        info.setEmail(user.getEmail());
        info.setApplicationId(application.getId());
        info.setEventTitle(application.getEvent().getTitle());
        info.setApplicationStatus(application.getStatus());
        info.setAppliedAt(application.getAppliedAt());
        info.setSkills(profile.getSkillsList());
        info.setLocation(profile.getLocation());
        
        return info;
    }

    private ApplicationInfo convertToApplicationInfo(Application application) {
        ApplicationInfo info = new ApplicationInfo();
        info.setApplicationId(application.getId());
        info.setEventId(application.getEvent().getId());
        info.setEventTitle(application.getEvent().getTitle());
        info.setStatus(application.getStatus());
        info.setAppliedAt(application.getAppliedAt());
        info.setUpdatedAt(LocalDateTime.now()); // Use current time since Application doesn't track updatedAt
        info.setVolunteerMessage(application.getMessage());
        info.setOrganizationNotes(application.getOrganizationNotes());
        
        return info;
    }

    private VolunteerSearchResult convertToSearchResult(Application application) {
        VolunteerProfile profile = application.getVolunteer();
        User user = profile.getUser();
        
        VolunteerSearchResult result = new VolunteerSearchResult();
        result.setVolunteerId(user.getId());
        result.setFirstName(profile.getFirstName());
        result.setLastName(profile.getLastName());
        result.setEmail(user.getEmail());
        result.setJoinedDate(user.getCreatedAt());
        result.setBio(profile.getBio());
        result.setSkills(profile.getSkillsList());
        result.setInterests(profile.getInterestsList());
        result.setLocation(profile.getLocation());
        result.setExperienceLevel(profile.getTotalVolunteerHours() != null && profile.getTotalVolunteerHours() > 0 ? 
            "Experienced" : "Beginner");
        
        return result;
    }

    private boolean matchesSearchCriteria(Application application, VolunteerSearchRequest request) {
        VolunteerProfile profile = application.getVolunteer();
        User user = profile.getUser();
        
        // Text search
        if (request.getSearchTerm() != null && !request.getSearchTerm().trim().isEmpty()) {
            String searchTerm = request.getSearchTerm().toLowerCase();
            boolean matchesText = false;
            
            if (profile.getFirstName() != null) {
                matchesText = profile.getFirstName().toLowerCase().contains(searchTerm);
            }
            if (profile.getLastName() != null) {
                matchesText = matchesText || profile.getLastName().toLowerCase().contains(searchTerm);
            }
            if (user.getEmail() != null) {
                matchesText = matchesText || user.getEmail().toLowerCase().contains(searchTerm);
            }
            if (profile.getBio() != null) {
                matchesText = matchesText || profile.getBio().toLowerCase().contains(searchTerm);
            }
            
            if (!matchesText) return false;
        }
        
        // Status filter
        if (request.getStatus() != null && application.getStatus() != request.getStatus()) {
            return false;
        }
        
        // Skills filter
        if (request.getSkills() != null && !request.getSkills().isEmpty()) {
            List<String> volunteerSkills = profile.getSkillsList();
            boolean hasMatchingSkill = request.getSkills().stream()
                    .anyMatch(skill -> volunteerSkills.contains(skill));
            if (!hasMatchingSkill) return false;
        }
        
        // Location filter
        if (request.getLocation() != null && !request.getLocation().trim().isEmpty()) {
            if (profile.getLocation() == null || 
                !profile.getLocation().toLowerCase().contains(request.getLocation().toLowerCase())) {
                return false;
            }
        }
        
        return true;
    }

    // ==========================================
    // REQUEST/RESPONSE CLASSES
    // ==========================================

    public static class VolunteerManagementResponse {
        private Map<ApplicationStatus, List<VolunteerInfo>> volunteersByStatus;
        private int totalVolunteers;
        private int activeVolunteers;
        private int pendingApplications;

        // Getters and setters
        public Map<ApplicationStatus, List<VolunteerInfo>> getVolunteersByStatus() { return volunteersByStatus; }
        public void setVolunteersByStatus(Map<ApplicationStatus, List<VolunteerInfo>> volunteersByStatus) { this.volunteersByStatus = volunteersByStatus; }
        public int getTotalVolunteers() { return totalVolunteers; }
        public void setTotalVolunteers(int totalVolunteers) { this.totalVolunteers = totalVolunteers; }
        public int getActiveVolunteers() { return activeVolunteers; }
        public void setActiveVolunteers(int activeVolunteers) { this.activeVolunteers = activeVolunteers; }
        public int getPendingApplications() { return pendingApplications; }
        public void setPendingApplications(int pendingApplications) { this.pendingApplications = pendingApplications; }
    }

    public static class VolunteerInfo {
        private Long volunteerId;
        private String firstName;
        private String lastName;
        private String email;
        private Long applicationId;
        private String eventTitle;
        private ApplicationStatus applicationStatus;
        private LocalDateTime appliedAt;
        private List<String> skills;
        private String location;

        // Getters and setters
        public Long getVolunteerId() { return volunteerId; }
        public void setVolunteerId(Long volunteerId) { this.volunteerId = volunteerId; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public Long getApplicationId() { return applicationId; }
        public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
        public String getEventTitle() { return eventTitle; }
        public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
        public ApplicationStatus getApplicationStatus() { return applicationStatus; }
        public void setApplicationStatus(ApplicationStatus applicationStatus) { this.applicationStatus = applicationStatus; }
        public LocalDateTime getAppliedAt() { return appliedAt; }
        public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
        public List<String> getSkills() { return skills; }
        public void setSkills(List<String> skills) { this.skills = skills; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
    }

    public static class VolunteerDetailResponse {
        private Long volunteerId;
        private String firstName;
        private String lastName;
        private String email;
        private String bio;
        private List<String> skills;
        private List<String> interests;
        private String location;
        private String availability;
        private List<ApplicationInfo> applications;
        private LocalDateTime joinedDate;
        private LocalDateTime lastActive;

        // Getters and setters
        public Long getVolunteerId() { return volunteerId; }
        public void setVolunteerId(Long volunteerId) { this.volunteerId = volunteerId; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        public List<String> getSkills() { return skills; }
        public void setSkills(List<String> skills) { this.skills = skills; }
        public List<String> getInterests() { return interests; }
        public void setInterests(List<String> interests) { this.interests = interests; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getAvailability() { return availability; }
        public void setAvailability(String availability) { this.availability = availability; }
        public List<ApplicationInfo> getApplications() { return applications; }
        public void setApplications(List<ApplicationInfo> applications) { this.applications = applications; }
        public LocalDateTime getJoinedDate() { return joinedDate; }
        public void setJoinedDate(LocalDateTime joinedDate) { this.joinedDate = joinedDate; }
        public LocalDateTime getLastActive() { return lastActive; }
        public void setLastActive(LocalDateTime lastActive) { this.lastActive = lastActive; }
    }

    public static class ApplicationInfo {
        private Long applicationId;
        private Long eventId;
        private String eventTitle;
        private ApplicationStatus status;
        private LocalDateTime appliedAt;
        private LocalDateTime updatedAt;
        private String volunteerMessage;
        private String organizationNotes;

        // Getters and setters
        public Long getApplicationId() { return applicationId; }
        public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
        public Long getEventId() { return eventId; }
        public void setEventId(Long eventId) { this.eventId = eventId; }
        public String getEventTitle() { return eventTitle; }
        public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
        public ApplicationStatus getStatus() { return status; }
        public void setStatus(ApplicationStatus status) { this.status = status; }
        public LocalDateTime getAppliedAt() { return appliedAt; }
        public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
        public String getVolunteerMessage() { return volunteerMessage; }
        public void setVolunteerMessage(String volunteerMessage) { this.volunteerMessage = volunteerMessage; }
        public String getOrganizationNotes() { return organizationNotes; }
        public void setOrganizationNotes(String organizationNotes) { this.organizationNotes = organizationNotes; }
    }

    public static class UpdateVolunteerStatusRequest {
        private Long applicationId;
        private ApplicationStatus newStatus;
        private String organizationNotes;

        // Getters and setters
        public Long getApplicationId() { return applicationId; }
        public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
        public ApplicationStatus getNewStatus() { return newStatus; }
        public void setNewStatus(ApplicationStatus newStatus) { this.newStatus = newStatus; }
        public String getOrganizationNotes() { return organizationNotes; }
        public void setOrganizationNotes(String organizationNotes) { this.organizationNotes = organizationNotes; }
    }

    public static class ApplicationUpdateResponse {
        private Long applicationId;
        private ApplicationStatus oldStatus;
        private ApplicationStatus newStatus;
        private LocalDateTime updatedAt;
        private String message;

        // Getters and setters
        public Long getApplicationId() { return applicationId; }
        public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
        public ApplicationStatus getOldStatus() { return oldStatus; }
        public void setOldStatus(ApplicationStatus oldStatus) { this.oldStatus = oldStatus; }
        public ApplicationStatus getNewStatus() { return newStatus; }
        public void setNewStatus(ApplicationStatus newStatus) { this.newStatus = newStatus; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class BulkUpdateRequest {
        private List<Long> applicationIds;
        private ApplicationStatus newStatus;
        private String organizationNotes;

        // Getters and setters
        public List<Long> getApplicationIds() { return applicationIds; }
        public void setApplicationIds(List<Long> applicationIds) { this.applicationIds = applicationIds; }
        public ApplicationStatus getNewStatus() { return newStatus; }
        public void setNewStatus(ApplicationStatus newStatus) { this.newStatus = newStatus; }
        public String getOrganizationNotes() { return organizationNotes; }
        public void setOrganizationNotes(String organizationNotes) { this.organizationNotes = organizationNotes; }
    }

    public static class BulkUpdateResponse {
        private int updatedCount;
        private int totalRequested;
        private ApplicationStatus newStatus;
        private String message;

        // Getters and setters
        public int getUpdatedCount() { return updatedCount; }
        public void setUpdatedCount(int updatedCount) { this.updatedCount = updatedCount; }
        public int getTotalRequested() { return totalRequested; }
        public void setTotalRequested(int totalRequested) { this.totalRequested = totalRequested; }
        public ApplicationStatus getNewStatus() { return newStatus; }
        public void setNewStatus(ApplicationStatus newStatus) { this.newStatus = newStatus; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class VolunteerSearchRequest {
        private String searchTerm;
        private ApplicationStatus status;
        private List<String> skills;
        private String location;

        // Getters and setters
        public String getSearchTerm() { return searchTerm; }
        public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }
        public ApplicationStatus getStatus() { return status; }
        public void setStatus(ApplicationStatus status) { this.status = status; }
        public List<String> getSkills() { return skills; }
        public void setSkills(List<String> skills) { this.skills = skills; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
    }

    public static class VolunteerSearchResponse {
        private List<VolunteerSearchResult> volunteers;
        private int totalElements;
        private int totalPages;
        private int currentPage;

        // Getters and setters
        public List<VolunteerSearchResult> getVolunteers() { return volunteers; }
        public void setVolunteers(List<VolunteerSearchResult> volunteers) { this.volunteers = volunteers; }
        public int getTotalElements() { return totalElements; }
        public void setTotalElements(int totalElements) { this.totalElements = totalElements; }
        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
        public int getCurrentPage() { return currentPage; }
        public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }
    }

    public static class VolunteerSearchResult {
        private Long volunteerId;
        private String firstName;
        private String lastName;
        private String email;
        private String bio;
        private List<String> skills;
        private List<String> interests;
        private String location;
        private String experienceLevel;
        private LocalDateTime joinedDate;

        // Getters and setters
        public Long getVolunteerId() { return volunteerId; }
        public void setVolunteerId(Long volunteerId) { this.volunteerId = volunteerId; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        public List<String> getSkills() { return skills; }
        public void setSkills(List<String> skills) { this.skills = skills; }
        public List<String> getInterests() { return interests; }
        public void setInterests(List<String> interests) { this.interests = interests; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getExperienceLevel() { return experienceLevel; }
        public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
        public LocalDateTime getJoinedDate() { return joinedDate; }
        public void setJoinedDate(LocalDateTime joinedDate) { this.joinedDate = joinedDate; }
    }
}