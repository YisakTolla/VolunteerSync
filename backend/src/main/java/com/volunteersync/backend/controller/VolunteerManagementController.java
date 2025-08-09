// backend/src/main/java/com/volunteersync/backend/controller/VolunteerManagementController.java
package com.volunteersync.backend.controller;

import com.volunteersync.backend.service.VolunteerManagementService;
import com.volunteersync.backend.service.VolunteerManagementService.*;
import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.enums.UserType;
import com.volunteersync.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Volunteer Management Controller - handles volunteer management endpoints for
 * organizations
 * Provides functionality for organizations to manage their volunteers across
 * events
 */
@RestController
@RequestMapping("/api/volunteer-management")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VolunteerManagementController {

    @Autowired
    private VolunteerManagementService volunteerManagementService;

    @Autowired
    private UserRepository userRepository;

    // ==========================================
    // VOLUNTEER OVERVIEW AND MANAGEMENT
    // ==========================================

    /**
     * Get all volunteers for the organization
     * GET /api/volunteer-management/volunteers
     */
    @GetMapping("/volunteers")
    public ResponseEntity<?> getOrganizationVolunteers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "appliedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Authentication authentication) {
        try {
            Long organizationId = getCurrentUserId(authentication);
            validateOrganizationUser(organizationId);

            // Create pagination with sorting
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);

            VolunteerManagementResponse response = volunteerManagementService
                    .getOrganizationVolunteers(organizationId, pageable);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get detailed information about a specific volunteer
     * GET /api/volunteer-management/volunteers/{volunteerId}
     */
    @GetMapping("/volunteers/{volunteerId}")
    public ResponseEntity<?> getVolunteerDetails(@PathVariable Long volunteerId,
            Authentication authentication) {
        try {
            Long organizationId = getCurrentUserId(authentication);
            validateOrganizationUser(organizationId);

            VolunteerDetailResponse response = volunteerManagementService
                    .getVolunteerDetails(volunteerId, organizationId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // VOLUNTEER STATUS MANAGEMENT
    // ==========================================

    /**
     * Update volunteer status for a specific application
     * PUT /api/volunteer-management/applications/{applicationId}/status
     */
    @PutMapping("/applications/{applicationId}/status")
    public ResponseEntity<?> updateVolunteerStatus(@PathVariable Long applicationId,
            @Valid @RequestBody UpdateVolunteerStatusRequest request,
            Authentication authentication) {
        try {
            Long organizationId = getCurrentUserId(authentication);
            validateOrganizationUser(organizationId);

            // Set the application ID from the path parameter
            request.setApplicationId(applicationId);

            ApplicationUpdateResponse response = volunteerManagementService
                    .updateVolunteerStatus(request, organizationId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Bulk update volunteer statuses
     * PUT /api/volunteer-management/applications/bulk-update
     */
    @PutMapping("/applications/bulk-update")
    public ResponseEntity<?> bulkUpdateVolunteerStatuses(@Valid @RequestBody BulkUpdateRequest request,
            Authentication authentication) {
        try {
            Long organizationId = getCurrentUserId(authentication);
            validateOrganizationUser(organizationId);

            BulkUpdateResponse response = volunteerManagementService
                    .bulkUpdateVolunteerStatuses(request, organizationId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // VOLUNTEER SEARCH AND FILTERING
    // ==========================================

    /**
     * Search volunteers with advanced filters
     * POST /api/volunteer-management/volunteers/search
     */
    @PostMapping("/volunteers/search")
    public ResponseEntity<?> searchVolunteers(@Valid @RequestBody VolunteerSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Authentication authentication) {
        try {
            Long organizationId = getCurrentUserId(authentication);
            validateOrganizationUser(organizationId);

            // Create pagination with sorting
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);

            VolunteerSearchResponse response = volunteerManagementService
                    .searchVolunteers(request, organizationId, pageable);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get volunteers with specific status
     * GET /api/volunteer-management/volunteers/status/{status}
     */
    @GetMapping("/volunteers/status/{status}")
    public ResponseEntity<?> getVolunteersByStatus(@PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        try {
            Long organizationId = getCurrentUserId(authentication);
            validateOrganizationUser(organizationId);

            // Create search request with status filter
            VolunteerSearchRequest searchRequest = new VolunteerSearchRequest();
            try {
                searchRequest
                        .setStatus(com.volunteersync.backend.enums.ApplicationStatus.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Invalid status: " + status));
            }

            // Create pagination
            Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());

            VolunteerSearchResponse response = volunteerManagementService
                    .searchVolunteers(searchRequest, organizationId, pageable);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // VOLUNTEER STATISTICS AND ANALYTICS
    // ==========================================

    /**
     * Get volunteer management statistics for the organization
     * GET /api/volunteer-management/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getVolunteerManagementStats(Authentication authentication) {
        try {
            Long organizationId = getCurrentUserId(authentication);
            validateOrganizationUser(organizationId);

            // Get basic volunteer overview
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE); // Get all for stats
            VolunteerManagementResponse response = volunteerManagementService
                    .getOrganizationVolunteers(organizationId, pageable);

            // Create a simplified stats response
            VolunteerStatsResponse stats = new VolunteerStatsResponse();
            stats.setTotalVolunteers(response.getTotalVolunteers());
            stats.setActiveVolunteers(response.getActiveVolunteers());
            stats.setPendingApplications(response.getPendingApplications());

            // Calculate additional stats from the volunteers by status
            int totalAccepted = response.getVolunteersByStatus()
                    .getOrDefault(com.volunteersync.backend.enums.ApplicationStatus.ACCEPTED, List.of()).size();
            int totalAttended = response.getVolunteersByStatus()
                    .getOrDefault(com.volunteersync.backend.enums.ApplicationStatus.ATTENDED, List.of()).size();
            int totalNoShows = response.getVolunteersByStatus()
                    .getOrDefault(com.volunteersync.backend.enums.ApplicationStatus.NO_SHOW, List.of()).size();

            stats.setTotalAccepted(totalAccepted);
            stats.setTotalAttended(totalAttended);
            stats.setTotalNoShows(totalNoShows);

            // Calculate attendance rate
            int totalCompleted = totalAttended + totalNoShows;
            if (totalCompleted > 0) {
                stats.setAttendanceRate((double) totalAttended / totalCompleted * 100);
            } else {
                stats.setAttendanceRate(0.0);
            }

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    /**
     * Get current user ID from authentication
     */
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("Authentication required");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        return user.get().getId();
    }

    /**
     * Validate that the user is an organization
     */
    private void validateOrganizationUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getUserType() != UserType.ORGANIZATION) {
            throw new RuntimeException("Access denied: Only organizations can access volunteer management");
        }
    }

    // ==========================================
    // RESPONSE CLASSES
    // ==========================================

    /**
     * Error response wrapper
     */
    public static class ErrorResponse {
        private String message;
        private long timestamp;

        public ErrorResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    /**
     * Volunteer statistics response
     */
    public static class VolunteerStatsResponse {
        private int totalVolunteers;
        private int activeVolunteers;
        private int pendingApplications;
        private int totalAccepted;
        private int totalAttended;
        private int totalNoShows;
        private double attendanceRate;

        // Getters and setters
        public int getTotalVolunteers() {
            return totalVolunteers;
        }

        public void setTotalVolunteers(int totalVolunteers) {
            this.totalVolunteers = totalVolunteers;
        }

        public int getActiveVolunteers() {
            return activeVolunteers;
        }

        public void setActiveVolunteers(int activeVolunteers) {
            this.activeVolunteers = activeVolunteers;
        }

        public int getPendingApplications() {
            return pendingApplications;
        }

        public void setPendingApplications(int pendingApplications) {
            this.pendingApplications = pendingApplications;
        }

        public int getTotalAccepted() {
            return totalAccepted;
        }

        public void setTotalAccepted(int totalAccepted) {
            this.totalAccepted = totalAccepted;
        }

        public int getTotalAttended() {
            return totalAttended;
        }

        public void setTotalAttended(int totalAttended) {
            this.totalAttended = totalAttended;
        }

        public int getTotalNoShows() {
            return totalNoShows;
        }

        public void setTotalNoShows(int totalNoShows) {
            this.totalNoShows = totalNoShows;
        }

        public double getAttendanceRate() {
            return attendanceRate;
        }

        public void setAttendanceRate(double attendanceRate) {
            this.attendanceRate = attendanceRate;
        }
    }
}