// backend/src/main/java/com/volunteersync/backend/service/BadgeService.java
package com.volunteersync.backend.service;

import com.volunteersync.backend.entity.Badge;
import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.entity.VolunteerProfile;
import com.volunteersync.backend.entity.OrganizationProfile;
import com.volunteersync.backend.enums.BadgeType;
import com.volunteersync.backend.enums.UserType;
import com.volunteersync.backend.repository.BadgeRepository;
import com.volunteersync.backend.repository.UserRepository;
import com.volunteersync.backend.repository.VolunteerProfileRepository;
import com.volunteersync.backend.repository.OrganizationProfileRepository;
import com.volunteersync.backend.repository.ApplicationRepository;
import com.volunteersync.backend.repository.EventRepository;
import com.volunteersync.backend.dto.BadgeDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Arrays;

/**
 * Badge service - handles gamification and achievement system
 * Manages badge earning, progress tracking, and user achievements
 */
@Service
@Transactional
public class BadgeService {

    @Autowired
    private BadgeRepository badgeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VolunteerProfileRepository volunteerProfileRepository;
    
    @Autowired
    private OrganizationProfileRepository organizationProfileRepository;
    
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private EventRepository eventRepository;

    // ==========================================
    // BADGE EARNING & PROGRESS METHODS
    // ==========================================

    /**
     * Check and award badges for a user after completing an action
     * This is the main method called when users complete activities
     */
    public List<BadgeDTO> checkAndAwardBadges(Long userId, BadgeTrigger trigger) {
        System.out.println("Checking badges for user ID: " + userId + " with trigger: " + trigger);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Badge> newlyEarnedBadges = switch (trigger) {
            case VOLUNTEER_HOURS_UPDATED -> checkVolunteerHoursBadges(user);
            case EVENT_ATTENDED -> checkEventBadges(user);
            case PROFILE_COMPLETED -> checkProfileBadges(user);
            case FIRST_EVENT_CREATED -> checkOrganizationBadges(user);
            case USER_REGISTERED -> checkRegistrationBadges(user);
        };
        
        System.out.println("Awarded " + newlyEarnedBadges.size() + " new badges");
        return newlyEarnedBadges.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Manually award a badge to a user (for special achievements)
     */
    public BadgeDTO awardBadge(Long userId, BadgeType badgeType, String notes) {
        System.out.println("Manually awarding badge " + badgeType + " to user ID: " + userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if user already has this badge
        if (badgeRepository.existsByUserAndBadgeType(user, badgeType)) {
            throw new RuntimeException("User already has this badge");
        }
        
        Badge badge = new Badge(user, badgeType);
        badge.setProgressValue(badgeType.getRequiredCount());
        badge.setNotes(notes);
        
        Badge savedBadge = badgeRepository.save(badge);
        System.out.println("Badge awarded successfully with ID: " + savedBadge.getId());
        
        return convertToDTO(savedBadge);
    }

    /**
     * Update badge progress for a user
     */
    public List<BadgeDTO> updateBadgeProgress(Long userId, BadgeType badgeType, Integer newProgress) {
        System.out.println("Updating progress for badge " + badgeType + " to " + newProgress + " for user: " + userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<BadgeDTO> result = List.of();
        Optional<Badge> existingBadge = badgeRepository.findByUserAndBadgeType(user, badgeType);
        
        if (existingBadge.isPresent()) {
            Badge badge = existingBadge.get();
            badge.updateProgress(newProgress);
            badgeRepository.save(badge);
            result = List.of(convertToDTO(badge));
        } else if (newProgress > 0) {
            // Create new badge with progress
            Badge newBadge = new Badge(user, badgeType, newProgress);
            
            // Check if badge is completed
            if (newProgress >= badgeType.getRequiredCount()) {
                newBadge.setEarnedAt(LocalDateTime.now());
            }
            
            Badge savedBadge = badgeRepository.save(newBadge);
            result = List.of(convertToDTO(savedBadge));
        }
        
        return result;
    }

    // ==========================================
    // USER BADGE QUERIES
    // ==========================================

    /**
     * Get all badges for a user
     */
    public List<BadgeDTO> getUserBadges(Long userId) {
        System.out.println("Fetching badges for user ID: " + userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Badge> badges = badgeRepository.findByUserOrderByEarnedAtDesc(user);
        
        return badges.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get featured badges for user profile display
     */
    public List<BadgeDTO> getFeaturedBadges(Long userId) {
        System.out.println("Fetching featured badges for user ID: " + userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Badge> featuredBadges = badgeRepository.findByUserAndIsFeaturedTrue(user);
        
        return featuredBadges.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get available badges for user (badges they can still earn)
     */
    public List<BadgeDTO> getAvailableBadges(Long userId) {
        System.out.println("Fetching available badges for user ID: " + userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Badge> earnedBadges = badgeRepository.findByUser(user);
        List<BadgeType> earnedTypes = earnedBadges.stream()
                .map(Badge::getBadgeType)
                .collect(Collectors.toList());
        
        return Arrays.stream(BadgeType.values())
                .filter(badgeType -> !earnedTypes.contains(badgeType))
                .filter(badgeType -> isBadgeAvailableForUser(badgeType, user))
                .map(badgeType -> {
                    BadgeDTO dto = new BadgeDTO();
                    dto.setBadgeType(badgeType);
                    dto.setProgressValue(0);
                    dto.setUserId(userId);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Set badge as featured/unfeatured
     */
    public BadgeDTO toggleBadgeFeatured(Long badgeId, Long userId) {
        System.out.println("Toggling featured status for badge ID: " + badgeId + " by user: " + userId);
        
        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new RuntimeException("Badge not found"));
        
        // Verify ownership
        if (!badge.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only modify your own badges");
        }
        
        badge.setIsFeatured(!badge.getIsFeatured());
        Badge savedBadge = badgeRepository.save(badge);
        
        System.out.println("Badge featured status updated");
        return convertToDTO(savedBadge);
    }

    // ==========================================
    // STATISTICS & LEADERBOARDS
    // ==========================================

    /**
     * Get badge leaderboard (users with most badges)
     */
    public List<BadgeLeaderboardEntry> getBadgeLeaderboard(int limit) {
        System.out.println("Fetching badge leaderboard with limit: " + limit);
        
        List<Object[]> results = badgeRepository.findTopBadgeEarners();
        
        return results.stream()
                .limit(limit)
                .map(result -> {
                    User user = (User) result[0];
                    Long badgeCount = (Long) result[1];
                    
                    BadgeLeaderboardEntry entry = new BadgeLeaderboardEntry();
                    entry.setUserId(user.getId());
                    entry.setUserEmail(user.getEmail()); // You might want to get display name from profile
                    entry.setBadgeCount(badgeCount.intValue());
                    
                    return entry;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get recent badge activity (last 7 days)
     */
    public List<BadgeDTO> getRecentBadgeActivity(int days) {
        System.out.println("Fetching recent badge activity for last " + days + " days");
        
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Badge> recentBadges = badgeRepository.findRecentBadges(since);
        
        return recentBadges.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get badge statistics for the platform
     */
    public BadgeStatsResponse getBadgeStatistics() {
        System.out.println("Fetching platform badge statistics");
        
        BadgeStatsResponse stats = new BadgeStatsResponse();
        
        // Total badges earned
        stats.setTotalBadgesEarned(badgeRepository.count());
        
        // Badge distribution
        List<Object[]> distribution = badgeRepository.getBadgeDistributionStats();
        stats.setBadgeDistribution(distribution.stream()
                .collect(Collectors.toMap(
                        result -> ((BadgeType) result[0]).getDisplayName(),
                        result -> ((Long) result[1]).intValue()
                )));
        
        // Rare badges
        long userCount = userRepository.count();
        long threshold = Math.max(1, userCount / 10); // Less than 10% of users
        List<Object[]> rareBadges = badgeRepository.findRareBadges(threshold);
        stats.setRareBadges(rareBadges.stream()
                .collect(Collectors.toMap(
                        result -> ((BadgeType) result[0]).getDisplayName(),
                        result -> ((Long) result[1]).intValue()
                )));
        
        return stats;
    }

    // ==========================================
    // PRIVATE BADGE CHECKING METHODS
    // ==========================================

    private List<Badge> checkVolunteerHoursBadges(User user) {
        if (user.getUserType() != UserType.VOLUNTEER) {
            return List.of();
        }
        
        VolunteerProfile profile = volunteerProfileRepository.findByUser(user).orElse(null);
        if (profile == null) {
            return List.of();
        }
        
        int totalHours = profile.getTotalVolunteerHours();
        List<Badge> newBadges = List.of();
        
        // Check hour-based badges
        BadgeType[] hourBadges = {
            BadgeType.FIRST_VOLUNTEER,
            BadgeType.HELPING_HAND,
            BadgeType.DEDICATED_HELPER,
            BadgeType.COMMUNITY_CHAMPION,
            BadgeType.VOLUNTEER_HERO
        };
        
        for (BadgeType badgeType : hourBadges) {
            if (totalHours >= badgeType.getRequiredCount() && 
                !badgeRepository.existsByUserAndBadgeType(user, badgeType)) {
                
                Badge badge = new Badge(user, badgeType, totalHours);
                badge.setNotes("Earned by completing " + totalHours + " volunteer hours");
                newBadges.add(badgeRepository.save(badge));
            }
        }
        
        return newBadges;
    }

    private List<Badge> checkEventBadges(User user) {
        if (user.getUserType() != UserType.VOLUNTEER) {
            return List.of();
        }
        
        VolunteerProfile profile = volunteerProfileRepository.findByUser(user).orElse(null);
        if (profile == null) {
            return List.of();
        }
        
        int eventsParticipated = profile.getEventsParticipated();
        List<Badge> newBadges = List.of();
        
        // Check event-based badges
        BadgeType[] eventBadges = {
            BadgeType.EVENT_STARTER,
            BadgeType.REGULAR_VOLUNTEER,
            BadgeType.EVENT_ENTHUSIAST
        };
        
        for (BadgeType badgeType : eventBadges) {
            if (eventsParticipated >= badgeType.getRequiredCount() && 
                !badgeRepository.existsByUserAndBadgeType(user, badgeType)) {
                
                Badge badge = new Badge(user, badgeType, eventsParticipated);
                badge.setNotes("Earned by attending " + eventsParticipated + " events");
                newBadges.add(badgeRepository.save(badge));
            }
        }
        
        return newBadges;
    }

    private List<Badge> checkOrganizationBadges(User user) {
        if (user.getUserType() != UserType.ORGANIZATION) {
            return List.of();
        }
        
        OrganizationProfile profile = organizationProfileRepository.findByUser(user).orElse(null);
        if (profile == null) {
            return List.of();
        }
        
        // Count events created by this organization
        long eventsCreated = eventRepository.countByOrganization(profile);
        List<Badge> newBadges = List.of();
        
        // Check organization badges
        BadgeType[] orgBadges = {
            BadgeType.FIRST_EVENT,
            BadgeType.EVENT_ORGANIZER,
            BadgeType.COMMUNITY_BUILDER
        };
        
        for (BadgeType badgeType : orgBadges) {
            if (eventsCreated >= badgeType.getRequiredCount() && 
                !badgeRepository.existsByUserAndBadgeType(user, badgeType)) {
                
                Badge badge = new Badge(user, badgeType, (int) eventsCreated);
                badge.setNotes("Earned by creating " + eventsCreated + " events");
                newBadges.add(badgeRepository.save(badge));
            }
        }
        
        return newBadges;
    }

    private List<Badge> checkProfileBadges(User user) {
        List<Badge> newBadges = List.of();
        
        // Check if profile is complete and award SKILL_SHARER badge
        if (isProfileComplete(user) && 
            !badgeRepository.existsByUserAndBadgeType(user, BadgeType.SKILL_SHARER)) {
            
            Badge badge = new Badge(user, BadgeType.SKILL_SHARER, 1);
            badge.setNotes("Earned by completing profile with skills and bio");
            newBadges.add(badgeRepository.save(badge));
        }
        
        return newBadges;
    }

    private List<Badge> checkRegistrationBadges(User user) {
        List<Badge> newBadges = List.of();
        
        // Check if user registered within the first year (Early Adopter)
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        if (user.getCreatedAt().isAfter(oneYearAgo) && 
            !badgeRepository.existsByUserAndBadgeType(user, BadgeType.EARLY_ADOPTER)) {
            
            Badge badge = new Badge(user, BadgeType.EARLY_ADOPTER, 1);
            badge.setNotes("Earned by joining VolunteerSync in its first year");
            newBadges.add(badgeRepository.save(badge));
        }
        
        return newBadges;
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    private boolean isBadgeAvailableForUser(BadgeType badgeType, User user) {
        // Check if badge is appropriate for user type
        if (user.getUserType() == UserType.VOLUNTEER && !badgeType.isForVolunteers()) {
            return false;
        }
        if (user.getUserType() == UserType.ORGANIZATION && !badgeType.isForOrganizations()) {
            return false;
        }
        
        return true;
    }

    private boolean isProfileComplete(User user) {
        if (user.getUserType() == UserType.VOLUNTEER) {
            VolunteerProfile profile = volunteerProfileRepository.findByUser(user).orElse(null);
            return profile != null && 
                   profile.getBio() != null && !profile.getBio().trim().isEmpty() &&
                   profile.getLocation() != null && !profile.getLocation().trim().isEmpty();
        } else if (user.getUserType() == UserType.ORGANIZATION) {
            OrganizationProfile profile = organizationProfileRepository.findByUser(user).orElse(null);
            return profile != null && 
                   profile.getDescription() != null && !profile.getDescription().trim().isEmpty() &&
                   profile.getMissionStatement() != null && !profile.getMissionStatement().trim().isEmpty();
        }
        return false;
    }

    private BadgeDTO convertToDTO(Badge badge) {
        BadgeDTO dto = new BadgeDTO();
        
        // Basic fields
        dto.setId(badge.getId());
        dto.setUserId(badge.getUser().getId());
        dto.setBadgeType(badge.getBadgeType());
        dto.setEarnedAt(badge.getEarnedAt());
        dto.setProgressValue(badge.getProgressValue());
        dto.setIsFeatured(badge.getIsFeatured());
        dto.setNotes(badge.getNotes());
        
        // Calculate time since earned
        if (badge.getEarnedAt() != null) {
            dto.setTimeSinceEarned(calculateTimeSinceEarned(badge.getEarnedAt()));
        }
        
        return dto;
    }

    private String calculateTimeSinceEarned(LocalDateTime earnedAt) {
        LocalDateTime now = LocalDateTime.now();
        
        long days = ChronoUnit.DAYS.between(earnedAt, now);
        if (days == 0) {
            return "Today";
        } else if (days == 1) {
            return "Yesterday";
        } else if (days < 7) {
            return days + " days ago";
        } else if (days < 30) {
            long weeks = days / 7;
            return weeks == 1 ? "1 week ago" : weeks + " weeks ago";
        } else if (days < 365) {
            long months = days / 30;
            return months == 1 ? "1 month ago" : months + " months ago";
        } else {
            long years = days / 365;
            return years == 1 ? "1 year ago" : years + " years ago";
        }
    }

    // ==========================================
    // INNER CLASSES & ENUMS
    // ==========================================

    public enum BadgeTrigger {
        VOLUNTEER_HOURS_UPDATED,
        EVENT_ATTENDED,
        PROFILE_COMPLETED,
        FIRST_EVENT_CREATED,
        USER_REGISTERED
    }

    public static class BadgeLeaderboardEntry {
        private Long userId;
        private String userEmail;
        private String displayName;
        private Integer badgeCount;
        
        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUserEmail() { return userEmail; }
        public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        public Integer getBadgeCount() { return badgeCount; }
        public void setBadgeCount(Integer badgeCount) { this.badgeCount = badgeCount; }
    }

    public static class BadgeStatsResponse {
        private Long totalBadgesEarned;
        private java.util.Map<String, Integer> badgeDistribution;
        private java.util.Map<String, Integer> rareBadges;
        
        // Getters and setters
        public Long getTotalBadgesEarned() { return totalBadgesEarned; }
        public void setTotalBadgesEarned(Long totalBadgesEarned) { this.totalBadgesEarned = totalBadgesEarned; }
        public java.util.Map<String, Integer> getBadgeDistribution() { return badgeDistribution; }
        public void setBadgeDistribution(java.util.Map<String, Integer> badgeDistribution) { this.badgeDistribution = badgeDistribution; }
        public java.util.Map<String, Integer> getRareBadges() { return rareBadges; }
        public void setRareBadges(java.util.Map<String, Integer> rareBadges) { this.rareBadges = rareBadges; }
    }
}