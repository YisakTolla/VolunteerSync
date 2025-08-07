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
import java.util.List;

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
            Long organizerId = getCurrentUserId(authentication);
            EventDTO event = eventService.createEvent(request, organizerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(event);
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

    // ==========================================
    // ORGANIZER-SPECIFIC OPERATIONS
    // ==========================================

    /**
     * Get events by current organizer
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

    // ==========================================
    // HELPER METHODS
    // ==========================================

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        // Extract user ID from authentication principal
        // This assumes you have a custom UserPrincipal or similar
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
    // RESPONSE CLASSES
    // ==========================================

    public static class ErrorResponse {
        private String error;
        private long timestamp;

        public ErrorResponse(String error) {
            this.error = error;
            this.timestamp = System.currentTimeMillis();
        }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    public static class SuccessResponse {
        private String message;
        private long timestamp;

        public SuccessResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    // Placeholder for UserPrincipal - should be implemented based on your security setup
    public interface UserPrincipal {
        Long getId();
        String getUsername();
        String getUserType();
    }
}