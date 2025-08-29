// backend/src/main/java/com/volunteersync/backend/controller/ApplicationController.java
package com.volunteersync.backend.controller;

import com.volunteersync.backend.service.ApplicationService;
import com.volunteersync.backend.dto.ApplicationDTO;
import com.volunteersync.backend.entity.Application;
import com.volunteersync.backend.enums.ApplicationStatus;
import com.volunteersync.backend.service.ApplicationService.SubmitApplicationRequest;
import com.volunteersync.backend.service.ApplicationService.VolunteerStatsResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;


/**
 * Application Controller - handles volunteer application endpoints
 * Manages application lifecycle, approvals, and statistics
 */
@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    // ==========================================
    // VOLUNTEER APPLICATION OPERATIONS
    // ==========================================

    /**
     * Submit application for an event
     * POST /api/applications
     */
    @PostMapping
    public ResponseEntity<?> submitApplication(@Valid @RequestBody SubmitApplicationRequest request,
            Authentication authentication) {
        try {
            Long volunteerId = getCurrentUserId(authentication);
            ApplicationDTO application = applicationService.submitApplication(request, volunteerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Withdraw application
     * DELETE /api/applications/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> withdrawApplication(@PathVariable Long id, Authentication authentication) {
        try {
            Long volunteerId = getCurrentUserId(authentication);
            String message = applicationService.withdrawApplication(id, volunteerId);
            return ResponseEntity.ok(new SuccessResponse(message));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get volunteer's applications
     * GET /api/applications/my-applications
     */
    @GetMapping("/my-applications")
    public ResponseEntity<?> getMyApplications(Authentication authentication) {
        try {
            Long volunteerId = getCurrentUserId(authentication);
            List<ApplicationDTO> applications = applicationService.getVolunteerApplications(volunteerId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get volunteer's applications by status
     * GET /api/applications/my-applications/{status}
     */
    @GetMapping("/my-applications/{status}")
    public ResponseEntity<?> getMyApplicationsByStatus(@PathVariable String status,
            Authentication authentication) {
        try {
            Long volunteerId = getCurrentUserId(authentication);
            ApplicationStatus appStatus = ApplicationStatus.valueOf(status.toUpperCase());
            List<ApplicationDTO> applications = applicationService.getVolunteerApplicationsByStatus(volunteerId,
                    appStatus);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get volunteer statistics
     * GET /api/applications/my-stats
     */
    @GetMapping("/my-stats")
    public ResponseEntity<?> getMyStats(Authentication authentication) {
        try {
            Long volunteerId = getCurrentUserId(authentication);
            VolunteerStatsResponse stats = applicationService.getVolunteerStats(volunteerId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // ORGANIZATION APPLICATION MANAGEMENT
    // ==========================================

    /**
     * Get organization's applications
     * GET /api/applications/organization
     */
    @GetMapping("/organization")
    public ResponseEntity<?> getOrganizationApplications(Authentication authentication) {
        try {
            Long organizerId = getCurrentUserId(authentication);
            List<ApplicationDTO> applications = applicationService.getOrganizationApplications(organizerId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get organization's pending applications
     * GET /api/applications/organization/pending
     */
    @GetMapping("/organization/pending")
    public ResponseEntity<?> getPendingApplications(Authentication authentication) {
        try {
            Long organizerId = getCurrentUserId(authentication);
            List<ApplicationDTO> applications = applicationService.getPendingApplicationsForOrganization(organizerId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get applications for specific event
     * GET /api/applications/event/{eventId}
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<?> getEventApplications(@PathVariable Long eventId, Authentication authentication) {
        try {
            Long organizerId = getCurrentUserId(authentication);
            List<ApplicationDTO> applications = applicationService.getEventApplications(eventId, organizerId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Approve application
     * PUT /api/applications/{id}/approve
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveApplication(@PathVariable Long id,
            @RequestBody(required = false) ApprovalRequest request,
            Authentication authentication) {
        try {
            Long organizerId = getCurrentUserId(authentication);
            String notes = request != null ? request.getNotes() : null;
            ApplicationDTO application = applicationService.approveApplication(id, organizerId, notes);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Reject application
     * PUT /api/applications/{id}/reject
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectApplication(@PathVariable Long id,
            @RequestBody(required = false) ApprovalRequest request,
            Authentication authentication) {
        try {
            Long organizerId = getCurrentUserId(authentication);
            String notes = request != null ? request.getNotes() : null;
            ApplicationDTO application = applicationService.rejectApplication(id, organizerId, notes);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Mark volunteer as attended
     * PUT /api/applications/{id}/attended
     */
    @PutMapping("/{id}/attended")
    public ResponseEntity<?> markAttended(@PathVariable Long id,
            @Valid @RequestBody AttendanceRequest request,
            Authentication authentication) {
        try {
            Long organizerId = getCurrentUserId(authentication);
            ApplicationDTO application = applicationService.markAttended(id, organizerId, request.getHoursCompleted());
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Mark volunteer as no-show
     * PUT /api/applications/{id}/no-show
     */
    @PutMapping("/{id}/no-show")
    public ResponseEntity<?> markNoShow(@PathVariable Long id, Authentication authentication) {
        try {
            Long organizerId = getCurrentUserId(authentication);
            ApplicationDTO application = applicationService.markNoShow(id, organizerId);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // GENERAL APPLICATION OPERATIONS
    // ==========================================

    /**
     * Get application by ID
     * GET /api/applications/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getApplicationById(@PathVariable Long id, Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            ApplicationDTO application = applicationService.getApplicationById(id, userId);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get volunteer's applications (MISSING ENDPOINT - ADD THIS)
     * GET /api/applications/volunteer/me
     */
    @GetMapping("/volunteer/me")
    public ResponseEntity<?> getVolunteerApplications(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);

            // For now, return empty list - you can implement actual logic later
            List<Map<String, Object>> applications = List.of();

            return ResponseEntity.ok(applications);

        } catch (Exception e) {
            System.err.println("Error fetching volunteer applications: " + e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }

        // Extract user ID from authentication principal
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getId();
        }

        // Fallback - extract from name if it's the user ID
        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid user authentication");
        }
    }

    // ==========================================
    // REQUEST/RESPONSE CLASSES
    // ==========================================

    public static class ApprovalRequest {
        private String notes;

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    public static class AttendanceRequest {
        private Integer hoursCompleted;

        public Integer getHoursCompleted() {
            return hoursCompleted;
        }

        public void setHoursCompleted(Integer hoursCompleted) {
            this.hoursCompleted = hoursCompleted;
        }
    }

    public static class ErrorResponse {
        private String error;
        private long timestamp;

        public ErrorResponse(String error) {
            this.error = error;
            this.timestamp = System.currentTimeMillis();
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    public static class SuccessResponse {
        private String message;
        private long timestamp;

        public SuccessResponse(String message) {
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

    // Placeholder for UserPrincipal - should be implemented based on your security
    // setup
    public interface UserPrincipal {
        Long getId();

        String getUsername();

        String getUserType();
    }
}