// backend/src/main/java/com/volunteersync/backend/controller/EventController.java
package com.volunteersync.backend.controller;

import com.volunteersync.backend.service.EventService;
import com.volunteersync.backend.dto.EventDTO;
import com.volunteersync.backend.service.EventService.CreateEventRequest;
import com.volunteersync.backend.service.EventService.UpdateEventRequest;
import com.volunteersync.backend.service.EventService.EventSearchRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Event Controller - handles event-related HTTP endpoints
 * Manages event CRUD operations, search, and volunteer registration
 */
@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EventController {

    @Autowired
    private EventService eventService;

    // ==========================================
    // EVENT CRUD OPERATIONS
    // ==========================================

    /**
     * Create new event
     * POST /api/events
     */
    @PostMapping
    public ResponseEntity<?> createEvent(@Valid @RequestBody CreateEventRequest request,
            Authentication authentication) {
        try {
            System.out.println("Received create event request: " + request.getTitle());

            // Debug authentication
            if (authentication == null) {
                System.err.println("⚠ No authentication provided");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Authentication required"));
            }

            System.out.println("🔍 Authentication principal: " + authentication.getPrincipal());
            System.out.println("🔍 Authentication name: " + authentication.getName());

            Long organizerId = getCurrentUserId(authentication);
            System.out.println("✅ Extracted organizer ID: " + organizerId);

            EventDTO event = eventService.createEvent(request, organizerId);

            System.out.println("✅ Event created successfully with ID: " + event.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(event);

        } catch (IllegalArgumentException e) {
            System.err.println("⚠ Invalid argument: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid input: " + e.getMessage()));

        } catch (RuntimeException e) {
            System.err.println("⚠ Runtime error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));

        } catch (Exception e) {
            System.err.println("⚠ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    /**
     * Get events by organization ID
     * GET /api/events/organization/{organizationId}
     */
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<?> getEventsByOrganization(@PathVariable Long organizationId) {
        try {
            System.out.println("Fetching events for organization ID: " + organizationId);

            List<EventDTO> events = eventService.getEventsByOrganizer(organizationId);
            return ResponseEntity.ok(events);

        } catch (Exception e) {
            System.err.println("Error fetching organization events: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get events for current organization (FIXED - this was missing)
     * GET /api/events/organization/me
     */
    @GetMapping("/organization/me")
    public ResponseEntity<?> getMyOrganizationEvents(Authentication authentication) {
        try {
            Long organizerId = getCurrentUserId(authentication);
            System.out.println("Fetching events for current organization ID: " + organizerId);

            List<EventDTO> events = eventService.getEventsByOrganizer(organizerId);
            return ResponseEntity.ok(events);

        } catch (Exception e) {
            System.err.println("Error fetching my organization events: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get events by current organizer (authenticated)
     * GET /api/events/my-events
     */
    @GetMapping("/my-events")
    public ResponseEntity<?> getMyEvents(Authentication authentication) {
        try {
            Long organizerId = getCurrentUserId(authentication);
            List<EventDTO> events = eventService.getEventsByOrganizer(organizerId);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get all active events
     * GET /api/events
     */
    @GetMapping
    public ResponseEntity<?> getAllEvents() {
        try {
            List<EventDTO> events = eventService.getAllEvents();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get event by ID
     * GET /api/events/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        try {
            EventDTO event = eventService.getEventById(id);
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Update event
     * PUT /api/events/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id,
            @Valid @RequestBody UpdateEventRequest request,
            Authentication authentication) {
        try {
            Long organizerId = getCurrentUserId(authentication);
            EventDTO event = eventService.updateEvent(id, request, organizerId);
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Cancel event
     * DELETE /api/events/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelEvent(@PathVariable Long id, Authentication authentication) {
        try {
            Long organizerId = getCurrentUserId(authentication);
            eventService.cancelEvent(id, organizerId);
            return ResponseEntity.ok(new SuccessResponse("Event cancelled successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // EVENT SEARCH AND FILTERING
    // ==========================================

    /**
     * Search events with filters
     * POST /api/events/search
     */
    @PostMapping("/search")
    public ResponseEntity<?> searchEvents(@RequestBody EventSearchRequest request) {
        try {
            List<EventDTO> events = eventService.searchEvents(request);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get events by type
     * GET /api/events/type/{eventType}
     */
    @GetMapping("/type/{eventType}")
    public ResponseEntity<?> getEventsByType(@PathVariable String eventType) {
        try {
            List<EventDTO> events = eventService.getEventsByType(eventType);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get virtual events
     * GET /api/events/virtual
     */
    @GetMapping("/virtual")
    public ResponseEntity<?> getVirtualEvents() {
        try {
            List<EventDTO> events = eventService.getVirtualEvents();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get events by location
     * GET /api/events/location/{location}
     */
    @GetMapping("/location/{location}")
    public ResponseEntity<?> getEventsByLocation(@PathVariable String location) {
        try {
            List<EventDTO> events = eventService.getEventsByLocation(location);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get events with available spots
     * GET /api/events/available
     */
    @GetMapping("/available")
    public ResponseEntity<?> getEventsWithAvailableSpots() {
        try {
            List<EventDTO> events = eventService.getEventsWithAvailableSpots();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get upcoming events for dashboard
     * GET /api/events/upcoming
     */
    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingEvents(@RequestParam(defaultValue = "10") int limit) {
        try {
            System.out.println("Fetching upcoming events with limit: " + limit);

            List<EventDTO> events = eventService.getAllEvents();

            // Limit the results
            if (events.size() > limit) {
                events = events.subList(0, limit);
            }

            return ResponseEntity.ok(events);

        } catch (Exception e) {
            System.err.println("Error fetching upcoming events: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // VOLUNTEER REGISTRATION
    // ==========================================

    /**
     * Register for event
     * POST /api/events/{id}/register
     */
    @PostMapping("/{id}/register")
    public ResponseEntity<?> registerForEvent(@PathVariable Long id, Authentication authentication) {
        try {
            Long volunteerId = getCurrentUserId(authentication);
            String message = eventService.registerForEvent(id, volunteerId);
            return ResponseEntity.ok(new SuccessResponse(message));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Cancel event registration
     * DELETE /api/events/{id}/register
     */
    @DeleteMapping("/{id}/register")
    public ResponseEntity<?> cancelRegistration(@PathVariable Long id, Authentication authentication) {
        try {
            Long volunteerId = getCurrentUserId(authentication);
            String message = eventService.cancelRegistration(id, volunteerId);
            return ResponseEntity.ok(new SuccessResponse(message));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Real-time event search endpoint
     * GET /api/events/search/realtime
     */
    @GetMapping("/search/realtime")
    public ResponseEntity<?> realtimeEventSearch(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String skillLevel,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(required = false) Boolean forceRefresh) {

        try {
            System.out.println("Real-time event search: " + searchTerm + " (forceRefresh: " + forceRefresh + ")");

            EventSearchRequest request = new EventSearchRequest();
            request.setSearchTerm(searchTerm);
            request.setEventType(eventType);
            request.setLocation(location);
            request.setSkillLevel(skillLevel);

            List<EventDTO> events = eventService.searchEvents(request);

            // Limit results
            if (events.size() > limit) {
                events = events.subList(0, limit);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("data", events);
            response.put("timestamp", LocalDateTime.now());
            response.put("count", events.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Real-time search error: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("data", List.of());
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * Get volunteer's events
     * GET /api/events/volunteer/me
     */
    @GetMapping("/volunteer/me")
    public ResponseEntity<?> getVolunteerEvents(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);

            // For now, return empty list - implement based on your application logic
            List<EventDTO> events = List.of();

            return ResponseEntity.ok(events);

        } catch (Exception e) {
            System.err.println("Error fetching volunteer events: " + e.getMessage());
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

        Object principal = authentication.getPrincipal();

        // If the principal is your User entity (which it is based on the error)
        if (principal instanceof com.volunteersync.backend.entity.User) {
            com.volunteersync.backend.entity.User user = (com.volunteersync.backend.entity.User) principal;
            return user.getId();
        }

        // If using Spring Security UserDetails
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            org.springframework.security.core.userdetails.UserDetails userDetails = (org.springframework.security.core.userdetails.UserDetails) principal;

            // Try to extract user ID from username if it's a number
            try {
                return Long.parseLong(userDetails.getUsername());
            } catch (NumberFormatException e) {
                throw new RuntimeException("Cannot extract user ID from UserDetails username");
            }
        }

        // If principal is a string (user ID)
        if (principal instanceof String) {
            try {
                return Long.parseLong((String) principal);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid user ID format: " + principal);
            }
        }

        // Last resort - try authentication name
        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Cannot extract user ID from authentication. Principal type: " +
                    principal.getClass().getName() + ", value: " + principal.toString());
        }
    }

    /**
     * Debug endpoint to test basic connectivity
     * GET /api/events/test
     */
    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Event controller is working");
        response.put("timestamp", LocalDateTime.now());

        if (authentication != null) {
            response.put("authenticated", true);
            response.put("principal", authentication.getPrincipal().toString());
            response.put("name", authentication.getName());
        } else {
            response.put("authenticated", false);
        }

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // RESPONSE CLASSES
    // ==========================================

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

    // Placeholder for UserPrincipal - should be implemented based on your security setup
    public interface UserPrincipal {
        Long getId();
        String getUsername();
        String getUserType();
    }
}