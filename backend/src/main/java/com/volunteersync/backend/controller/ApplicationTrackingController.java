// backend/src/main/java/com/volunteersync/backend/controller/ApplicationTrackingController.java
package com.volunteersync.backend.controller;

import com.volunteersync.backend.service.ApplicationTrackingService;
import com.volunteersync.backend.service.ApplicationTrackingService.*;
import com.volunteersync.backend.enums.ApplicationStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * REST Controller for Application Tracking functionality
 * Provides endpoints for volunteers to track their applications and manage their volunteer status
 */
@RestController
@RequestMapping("/api/applications/tracking")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApplicationTrackingController {

    @Autowired
    private ApplicationTrackingService applicationTrackingService;

    // ==========================================
    // VOLUNTEER APPLICATION TRACKING ENDPOINTS
    // ==========================================

    /**
     * Get all applications for a volunteer with filtering and pagination
     * GET /api/applications/tracking/volunteer/{volunteerId}
     */
    @GetMapping("/volunteer/{volunteerId}")
    public ResponseEntity<VolunteerApplicationsResponse> getVolunteerApplications(
            @PathVariable Long volunteerId,
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String eventTitleSearch,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            // Create filter object
            ApplicationTrackingFilter filter = new ApplicationTrackingFilter();
            filter.setStatus(status);
            filter.setStartDate(startDate);
            filter.setEndDate(endDate);
            filter.setEventTitleSearch(eventTitleSearch);
            
            // Create pageable
            Pageable pageable = PageRequest.of(page, size);
            
            // Get applications
            VolunteerApplicationsResponse response = applicationTrackingService
                    .getVolunteerApplications(volunteerId, filter, pageable);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get detailed tracking information for a specific application
     * GET /api/applications/tracking/{applicationId}/details
     */
    @GetMapping("/{applicationId}/details")
    public ResponseEntity<ApplicationTrackingDetailResponse> getApplicationTrackingDetails(
            @PathVariable Long applicationId,
            @RequestParam Long volunteerId) {
        
        try {
            ApplicationTrackingDetailResponse response = applicationTrackingService
                    .getApplicationTrackingDetails(applicationId, volunteerId);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get application status summary and statistics for a volunteer
     * GET /api/applications/tracking/volunteer/{volunteerId}/summary
     */
    @GetMapping("/volunteer/{volunteerId}/summary")
    public ResponseEntity<ApplicationStatusSummary> getApplicationStatusSummary(
            @PathVariable Long volunteerId) {
        
        try {
            ApplicationStatusSummary summary = applicationTrackingService
                    .getApplicationStatusSummary(volunteerId);
            
            return ResponseEntity.ok(summary);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get upcoming events for a volunteer
     * GET /api/applications/tracking/volunteer/{volunteerId}/upcoming
     */
    @GetMapping("/volunteer/{volunteerId}/upcoming")
    public ResponseEntity<UpcomingEventsResponse> getUpcomingEvents(
            @PathVariable Long volunteerId) {
        
        try {
            UpcomingEventsResponse response = applicationTrackingService
                    .getUpcomingEvents(volunteerId);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Withdraw an application
     * POST /api/applications/tracking/{applicationId}/withdraw
     */
    @PostMapping("/{applicationId}/withdraw")
    public ResponseEntity<ApplicationWithdrawResponse> withdrawApplication(
            @PathVariable Long applicationId,
            @RequestParam Long volunteerId,
            @RequestBody WithdrawApplicationRequest request) {
        
        try {
            ApplicationWithdrawResponse response = applicationTrackingService
                    .withdrawApplication(applicationId, volunteerId, request);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==========================================
    // UTILITY ENDPOINTS
    // ==========================================

    /**
     * Get application status options for filtering
     * GET /api/applications/tracking/status-options
     */
    @GetMapping("/status-options")
    public ResponseEntity<ApplicationStatus[]> getApplicationStatusOptions() {
        return ResponseEntity.ok(ApplicationStatus.values());
    }

    /**
     * Check if a volunteer can withdraw a specific application
     * GET /api/applications/tracking/{applicationId}/can-withdraw
     */
    @GetMapping("/{applicationId}/can-withdraw")
    public ResponseEntity<Boolean> canWithdrawApplication(
            @PathVariable Long applicationId,
            @RequestParam Long volunteerId) {
        
        try {
            // Get application details to check withdrawal eligibility
            ApplicationTrackingDetailResponse details = applicationTrackingService
                    .getApplicationTrackingDetails(applicationId, volunteerId);
            
            // Simple check based on status - this could be enhanced
            boolean canWithdraw = details.getCurrentStatus() == ApplicationStatus.PENDING ||
                                details.getCurrentStatus() == ApplicationStatus.REJECTED ||
                                (details.getCurrentStatus() == ApplicationStatus.ACCEPTED &&
                                 details.getEvent().getEventDate().isAfter(LocalDateTime.now().plusDays(1)));
            
            return ResponseEntity.ok(canWithdraw);
            
        } catch (RuntimeException e) {
            return ResponseEntity.ok(false);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get quick application counts for dashboard
     * GET /api/applications/tracking/volunteer/{volunteerId}/quick-stats
     */
    @GetMapping("/volunteer/{volunteerId}/quick-stats")
    public ResponseEntity<QuickStatsResponse> getQuickStats(@PathVariable Long volunteerId) {
        
        try {
            ApplicationStatusSummary summary = applicationTrackingService
                    .getApplicationStatusSummary(volunteerId);
            
            UpcomingEventsResponse upcoming = applicationTrackingService
                    .getUpcomingEvents(volunteerId);
            
            QuickStatsResponse quickStats = new QuickStatsResponse();
            quickStats.setTotalApplications(summary.getTotalApplications());
            quickStats.setPendingApplications(summary.getPendingResponses());
            quickStats.setAcceptedApplications(summary.getStatusBreakdown()
                    .getOrDefault(ApplicationStatus.ACCEPTED, 0));
            quickStats.setUpcomingEvents(upcoming.getTotalUpcoming());
            quickStats.setRecentActivity(summary.getRecentActivity());
            quickStats.setSuccessRate(summary.getSuccessRate());
            
            return ResponseEntity.ok(quickStats);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==========================================
    // RESPONSE CLASSES
    // ==========================================

    /**
     * Quick statistics response for dashboard display
     */
    public static class QuickStatsResponse {
        private int totalApplications;
        private int pendingApplications;
        private int acceptedApplications;
        private int upcomingEvents;
        private int recentActivity;
        private double successRate;

        // Getters and setters
        public int getTotalApplications() { return totalApplications; }
        public void setTotalApplications(int totalApplications) { this.totalApplications = totalApplications; }
        public int getPendingApplications() { return pendingApplications; }
        public void setPendingApplications(int pendingApplications) { this.pendingApplications = pendingApplications; }
        public int getAcceptedApplications() { return acceptedApplications; }
        public void setAcceptedApplications(int acceptedApplications) { this.acceptedApplications = acceptedApplications; }
        public int getUpcomingEvents() { return upcomingEvents; }
        public void setUpcomingEvents(int upcomingEvents) { this.upcomingEvents = upcomingEvents; }
        public int getRecentActivity() { return recentActivity; }
        public void setRecentActivity(int recentActivity) { this.recentActivity = recentActivity; }
        public double getSuccessRate() { return successRate; }
        public void setSuccessRate(double successRate) { this.successRate = successRate; }
    }

    // ==========================================
    // EXCEPTION HANDLING
    // ==========================================

    /**
     * Handle validation errors
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(IllegalArgumentException e) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage("Invalid request parameters");
        error.setDetails(e.getMessage());
        error.setTimestamp(LocalDateTime.now());
        
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle authorization errors
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationErrors(SecurityException e) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage("Access denied");
        error.setDetails(e.getMessage());
        error.setTimestamp(LocalDateTime.now());
        
        return ResponseEntity.status(403).body(error);
    }

    /**
     * Handle not found errors
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundErrors(RuntimeException e) {
        if (e.getMessage().contains("not found")) {
            ErrorResponse error = new ErrorResponse();
            error.setMessage("Resource not found");
            error.setDetails(e.getMessage());
            error.setTimestamp(LocalDateTime.now());
            
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.internalServerError().build();
    }

    /**
     * Error response structure
     */
    public static class ErrorResponse {
        private String message;
        private String details;
        private LocalDateTime timestamp;

        // Getters and setters
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getDetails() { return details; }
        public void setDetails(String details) { this.details = details; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
}