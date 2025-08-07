// backend/src/main/java/com/volunteersync/backend/controller/BadgeController.java
package com.volunteersync.backend.controller;

import com.volunteersync.backend.service.BadgeService;
import com.volunteersync.backend.dto.BadgeDTO;
import com.volunteersync.backend.enums.BadgeType;
import com.volunteersync.backend.service.BadgeService.BadgeTrigger;
import com.volunteersync.backend.service.BadgeService.BadgeLeaderboardEntry;
import com.volunteersync.backend.service.BadgeService.BadgeStatsResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Badge Controller - handles gamification and achievement endpoints
 * Manages badge earning, progress tracking, and leaderboards
 */
@RestController
@RequestMapping("/api/badges")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BadgeController {

    @Autowired
    private BadgeService badgeService;

    // ==========================================
    // USER BADGE OPERATIONS
    // ==========================================

    /**
     * Get current user's badges
     * GET /api/badges/my-badges
     */
    @GetMapping("/my-badges")
    public ResponseEntity<?> getMyBadges(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            List<BadgeDTO> badges = badgeService.getUserBadges(userId);
            return ResponseEntity.ok(badges);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get user's featured badges
     * GET /api/badges/my-featured
     */
    @GetMapping("/my-featured")
    public ResponseEntity<?> getMyFeaturedBadges(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            List<BadgeDTO> badges = badgeService.getFeaturedBadges(userId);
            return ResponseEntity.ok(badges);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get available badges for user
     * GET /api/badges/available
     */
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableBadges(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            List<BadgeDTO> badges = badgeService.getAvailableBadges(userId);
            return ResponseEntity.ok(badges);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get badges for specific user
     * GET /api/badges/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserBadges(@PathVariable Long userId) {
        try {
            List<BadgeDTO> badges = badgeService.getUserBadges(userId);
            return ResponseEntity.ok(badges);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Toggle badge featured status
     * PUT /api/badges/{badgeId}/featured
     */
    @PutMapping("/{badgeId}/featured")
    public ResponseEntity<?> toggleBadgeFeatured(@PathVariable Long badgeId, Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            BadgeDTO badge = badgeService.toggleBadgeFeatured(badgeId, userId);
            return ResponseEntity.ok(badge);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // BADGE EARNING AND PROGRESS
    // ==========================================

    /**
     * Check and award badges (typically called by other services)
     * POST /api/badges/check/{trigger}
     */
    @PostMapping("/check/{trigger}")
    public ResponseEntity<?> checkAndAwardBadges(@PathVariable String trigger, 
                                                Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            BadgeTrigger badgeTrigger = BadgeTrigger.valueOf(trigger.toUpperCase());
            List<BadgeDTO> newBadges = badgeService.checkAndAwardBadges(userId, badgeTrigger);
            return ResponseEntity.ok(new BadgeCheckResponse(newBadges, newBadges.size() > 0));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get user's badge progress
     * GET /api/badges/progress
     */
    @GetMapping("/progress")
    public ResponseEntity<?> getBadgeProgress(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            List<BadgeDTO> progress = badgeService.getBadgeProgress(userId);
            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get progress for specific badge type
     * GET /api/badges/progress/{badgeType}
     */
    @GetMapping("/progress/{badgeType}")
    public ResponseEntity<?> getSpecificBadgeProgress(@PathVariable String badgeType, 
                                                     Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            BadgeType type = BadgeType.valueOf(badgeType.toUpperCase());
            BadgeDTO progress = badgeService.getSpecificBadgeProgress(userId, type);
            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // BADGE INFORMATION
    // ==========================================

    /**
     * Get all available badge types
     * GET /api/badges/types
     */
    @GetMapping("/types")
    public ResponseEntity<?> getAllBadgeTypes() {
        try {
            List<BadgeDTO> badgeTypes = badgeService.getAllBadgeTypes();
            return ResponseEntity.ok(badgeTypes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get badge by type
     * GET /api/badges/type/{badgeType}
     */
    @GetMapping("/type/{badgeType}")
    public ResponseEntity<?> getBadgeByType(@PathVariable String badgeType) {
        try {
            BadgeType type = BadgeType.valueOf(badgeType.toUpperCase());
            BadgeDTO badge = badgeService.getBadgeByType(type);
            return ResponseEntity.ok(badge);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get badges by category
     * GET /api/badges/category/{category}
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<?> getBadgesByCategory(@PathVariable String category) {
        try {
            List<BadgeDTO> badges = badgeService.getBadgesByCategory(category);
            return ResponseEntity.ok(badges);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // LEADERBOARDS
    // ==========================================

    /**
     * Get badge leaderboard
     * GET /api/badges/leaderboard
     */
    @GetMapping("/leaderboard")
    public ResponseEntity<?> getBadgeLeaderboard(@RequestParam(defaultValue = "10") int limit,
                                               @RequestParam(defaultValue = "0") int offset) {
        try {
            List<BadgeLeaderboardEntry> leaderboard = badgeService.getBadgeLeaderboard(limit, offset);
            return ResponseEntity.ok(leaderboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get recent badge achievements
     * GET /api/badges/recent
     */
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentBadges(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<BadgeDTO> recentBadges = badgeService.getRecentBadges(limit);
            return ResponseEntity.ok(recentBadges);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get today's badge earners
     * GET /api/badges/today
     */
    @GetMapping("/today")
    public ResponseEntity<?> getTodaysBadges() {
        try {
            List<BadgeDTO> todaysBadges = badgeService.getTodaysBadges();
            return ResponseEntity.ok(todaysBadges);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // STATISTICS
    // ==========================================

    /**
     * Get badge statistics
     * GET /api/badges/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getBadgeStats(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            BadgeStatsResponse stats = badgeService.getBadgeStats(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get global badge statistics
     * GET /api/badges/stats/global
     */
    @GetMapping("/stats/global")
    public ResponseEntity<?> getGlobalBadgeStats() {
        try {
            BadgeStatsResponse stats = badgeService.getGlobalBadgeStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get badge distribution stats
     * GET /api/badges/stats/distribution
     */
    @GetMapping("/stats/distribution")
    public ResponseEntity<?> getBadgeDistribution() {
        try {
            List<BadgeStatsResponse.BadgeDistribution> distribution = badgeService.getBadgeDistribution();
            return ResponseEntity.ok(distribution);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // ADMIN ENDPOINTS
    // ==========================================

    /**
     * Award badge to user (admin only)
     * POST /api/badges/award
     */
    @PostMapping("/award")
    public ResponseEntity<?> awardBadge(@Valid @RequestBody AwardBadgeRequest request,
                                       Authentication authentication) {
        try {
            // Verify admin permissions here
            BadgeDTO badge = badgeService.awardBadge(request.getUserId(), request.getBadgeType(), 
                                                   request.getProgressValue(), request.getNotes());
            return ResponseEntity.ok(badge);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Remove badge from user (admin only)
     * DELETE /api/badges/{badgeId}
     */
    @DeleteMapping("/{badgeId}")
    public ResponseEntity<?> removeBadge(@PathVariable Long badgeId,
                                        Authentication authentication) {
        try {
            // Verify admin permissions here
            badgeService.removeBadge(badgeId);
            return ResponseEntity.ok(new SuccessResponse("Badge removed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    /**
     * Extract user ID from authentication context
     */
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            // Assuming username is the user ID or email
            try {
                return Long.parseLong(userDetails.getUsername());
            } catch (NumberFormatException e) {
                // If username is email, you'll need to lookup user by email
                throw new IllegalArgumentException("Unable to determine user ID from authentication");
            }
        }
        
        throw new IllegalArgumentException("Invalid authentication principal type");
    }

    // ==========================================
    // RESPONSE CLASSES
    // ==========================================

    /**
     * Generic error response
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
     * Badge check response
     */
    public static class BadgeCheckResponse {
        private List<BadgeDTO> newBadges;
        private boolean hasNewBadges;
        private int totalNewBadges;

        public BadgeCheckResponse(List<BadgeDTO> newBadges, boolean hasNewBadges) {
            this.newBadges = newBadges;
            this.hasNewBadges = hasNewBadges;
            this.totalNewBadges = newBadges != null ? newBadges.size() : 0;
        }

        public List<BadgeDTO> getNewBadges() {
            return newBadges;
        }

        public void setNewBadges(List<BadgeDTO> newBadges) {
            this.newBadges = newBadges;
        }

        public boolean isHasNewBadges() {
            return hasNewBadges;
        }

        public void setHasNewBadges(boolean hasNewBadges) {
            this.hasNewBadges = hasNewBadges;
        }

        public int getTotalNewBadges() {
            return totalNewBadges;
        }

        public void setTotalNewBadges(int totalNewBadges) {
            this.totalNewBadges = totalNewBadges;
        }
    }

    /**
     * Success response
     */
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

    /**
     * Award badge request
     */
    public static class AwardBadgeRequest {
        private Long userId;
        private BadgeType badgeType;
        private Integer progressValue;
        private String notes;

        // Constructors
        public AwardBadgeRequest() {
        }

        public AwardBadgeRequest(Long userId, BadgeType badgeType, Integer progressValue, String notes) {
            this.userId = userId;
            this.badgeType = badgeType;
            this.progressValue = progressValue;
            this.notes = notes;
        }

        // Getters and Setters
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public BadgeType getBadgeType() {
            return badgeType;
        }

        public void setBadgeType(BadgeType badgeType) {
            this.badgeType = badgeType;
        }

        public Integer getProgressValue() {
            return progressValue;
        }

        public void setProgressValue(Integer progressValue) {
            this.progressValue = progressValue;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }
}