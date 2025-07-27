package com.volunteersync.backend.repository;

import com.volunteersync.backend.entity.profile.Profile;
import com.volunteersync.backend.entity.profile.ProfileBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ProfileBadge entities.
 * Provides badge-specific query methods for gamification, achievements,
 * recognition, and engagement tracking within the VolunteerSync platform.
 */
@Repository
public interface ProfileBadgeRepository extends JpaRepository<ProfileBadge, Long> {
    
    // =====================================================
    // BASIC BADGE QUERIES
    // =====================================================
    
    /**
     * Find all badges for a specific profile
     * @param profile The profile to find badges for
     * @return List of badges for that profile
     */
    List<ProfileBadge> findByProfile(Profile profile);
    
    /**
     * Find all badges for a specific profile ID
     * @param profileId The profile ID to find badges for
     * @return List of badges for that profile
     */
    List<ProfileBadge> findByProfileId(Long profileId);
    
    /**
     * Find badge by profile and badge name
     * @param profile The profile
     * @param badgeName The badge name
     * @return Optional containing the badge if found
     */
    Optional<ProfileBadge> findByProfileAndBadgeNameIgnoreCase(Profile profile, String badgeName);
    
    /**
     * Find all active badges for a profile
     * @param profile The profile
     * @return List of active badges
     */
    List<ProfileBadge> findByProfileAndIsActiveTrue(Profile profile);
    
    /**
     * Find all featured badges for a profile
     * @param profile The profile
     * @return List of featured badges
     */
    List<ProfileBadge> findByProfileAndIsFeaturedTrue(Profile profile);
    
    /**
     * Check if profile has specific badge
     * @param profile The profile
     * @param badgeName The badge name
     * @return true if profile has that badge
     */
    boolean existsByProfileAndBadgeNameIgnoreCase(Profile profile, String badgeName);
    
    // =====================================================
    // BADGE TYPE & CATEGORY QUERIES
    // =====================================================
    
    /**
     * Find badges by type
     * @param badgeType The badge type
     * @return List of badges of that type
     */
    List<ProfileBadge> findByBadgeType(String badgeType);
    
    /**
     * Find badges by category
     * @param category The badge category
     * @return List of badges in that category
     */
    List<ProfileBadge> findByCategory(String category);
    
    /**
     * Find badges by type for specific profile
     * @param profile The profile
     * @param badgeType The badge type
     * @return List of badges of that type for the profile
     */
    List<ProfileBadge> findByProfileAndBadgeType(Profile profile, String badgeType);
    
    /**
     * Find badges by category for specific profile
     * @param profile The profile
     * @param category The badge category
     * @return List of badges in that category for the profile
     */
    List<ProfileBadge> findByProfileAndCategory(Profile profile, String category);
    
    /**
     * Get all unique badge types
     * @return List of distinct badge types
     */
    @Query("SELECT DISTINCT pb.badgeType FROM ProfileBadge pb WHERE pb.isActive = true ORDER BY pb.badgeType")
    List<String> findDistinctBadgeTypes();
    
    /**
     * Get all unique badge categories
     * @return List of distinct categories
     */
    @Query("SELECT DISTINCT pb.category FROM ProfileBadge pb WHERE pb.category IS NOT NULL ORDER BY pb.category")
    List<String> findDistinctCategories();
    
    // =====================================================
    // RARITY & VALUE QUERIES
    // =====================================================
    
    /**
     * Find badges by rarity level
     * @param rarityLevel The rarity level
     * @return List of badges at that rarity
     */
    List<ProfileBadge> findByRarityLevel(ProfileBadge.BadgeRarity rarityLevel);
    
    /**
     * Find rare and legendary badges
     * @return List of high-rarity badges
     */
    @Query("""
        SELECT pb FROM ProfileBadge pb 
        WHERE pb.rarityLevel IN ('RARE', 'EPIC', 'LEGENDARY')
        AND pb.isActive = true
        """)
    List<ProfileBadge> findHighRarityBadges();
    
    /**
     * Find badges by minimum point value
     * @param minPoints Minimum point value
     * @return List of high-value badges
     */
    @Query("""
        SELECT pb FROM ProfileBadge pb 
        WHERE pb.pointValue >= :minPoints
        AND pb.isActive = true
        """)
    List<ProfileBadge> findHighValueBadges(@Param("minPoints") Integer minPoints);
    
    /**
     * Find badges by difficulty level
     * @param minDifficulty Minimum difficulty level
     * @return List of challenging badges
     */
    @Query("""
        SELECT pb FROM ProfileBadge pb 
        WHERE pb.difficultyLevel >= :minDifficulty
        AND pb.isActive = true
        """)
    List<ProfileBadge> findChallengingBadges(@Param("minDifficulty") Integer minDifficulty);
    
    /**
     * Find exclusive badges (low completion rate)
     * @param maxCompletionRate Maximum completion rate percentage
     * @return List of exclusive badges
     */
    @Query("""
        SELECT pb FROM ProfileBadge pb 
        WHERE pb.completionRate <= :maxCompletionRate
        AND pb.completionRate IS NOT NULL
        AND pb.isActive = true
        """)
    List<ProfileBadge> findExclusiveBadges(@Param("maxCompletionRate") Double maxCompletionRate);
    
    // =====================================================
    // MILESTONE & PROGRESSION QUERIES
    // =====================================================
    
    /**
     * Find progressive badges
     * @return List of badges that can be earned multiple times
     */
    List<ProfileBadge> findByIsProgressiveTrue();
    
    /**
     * Find badges by progression level
     * @param progressionLevel The progression level
     * @return List of badges at that progression level
     */
    List<ProfileBadge> findByProgressionLevel(Integer progressionLevel);
    
    /**
     * Find milestone badges by type
     * @param milestoneUnit The milestone unit (hours, events, etc.)
     * @return List of milestone badges
     */
    List<ProfileBadge> findByMilestoneUnit(String milestoneUnit);
    
    /**
     * Find badges by milestone value range
     * @param minValue Minimum milestone value
     * @param maxValue Maximum milestone value
     * @param unit Milestone unit
     * @return List of badges in milestone range
     */
    @Query("""
        SELECT pb FROM ProfileBadge pb 
        WHERE pb.milestoneValue BETWEEN :minValue AND :maxValue
        AND pb.milestoneUnit = :unit
        AND pb.isActive = true
        """)
    List<ProfileBadge> findByMilestoneRange(
        @Param("minValue") Integer minValue,
        @Param("maxValue") Integer maxValue,
        @Param("unit") String unit
    );
    
    // =====================================================
    // TIME-BASED QUERIES
    // =====================================================
    
    /**
     * Find recently earned badges
     * @param since Badges earned since this date
     * @return List of recently earned badges
     */
    List<ProfileBadge> findByEarnedAtAfter(LocalDateTime since);
    
    /**
     * Find badges earned in date range
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return List of badges earned in range
     */
    List<ProfileBadge> findByEarnedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find badges earned today
     * @param todayStart Start of today
     * @param todayEnd End of today
     * @return List of badges earned today
     */
    @Query("""
        SELECT pb FROM ProfileBadge pb 
        WHERE pb.earnedAt BETWEEN :todayStart AND :todayEnd
        AND pb.isActive = true
        """)
    List<ProfileBadge> findBadgesEarnedToday(
        @Param("todayStart") LocalDateTime todayStart,
        @Param("todayEnd") LocalDateTime todayEnd
    );
    
    /**
     * Find limited-time badges
     * @return List of badges with expiration dates
     */
    @Query("""
        SELECT pb FROM ProfileBadge pb 
        WHERE pb.expiresAt IS NOT NULL
        AND pb.isActive = true
        """)
    List<ProfileBadge> findLimitedTimeBadges();
    
    /**
     * Find expiring badges
     * @param expirationDate Date to check upcoming expirations
     * @return List of badges expiring soon
     */
    @Query("""
        SELECT pb FROM ProfileBadge pb 
        WHERE pb.expiresAt IS NOT NULL 
        AND pb.expiresAt <= :expirationDate
        AND pb.expiresAt > CURRENT_TIMESTAMP
        """)
    List<ProfileBadge> findExpiringBadges(@Param("expirationDate") LocalDateTime expirationDate);
    
    // =====================================================
    // AWARDING & RECOGNITION QUERIES
    // =====================================================
    
    /**
     * Find badges awarded by system
     * @return List of system-awarded badges
     */
    List<ProfileBadge> findByAwardedBy(String awardedBy);
    
    /**
     * Find badges awarded by administrators
     * @return List of admin-awarded badges
     */
    @Query("""
        SELECT pb FROM ProfileBadge pb 
        WHERE pb.awardedBy LIKE 'Administrator%'
        OR pb.awardedBy = 'Admin'
        """)
    List<ProfileBadge> findAdminAwardedBadges();
    
    /**
     * Find badges with achievement details
     * @return List of badges with documented achievements
     */
    @Query("""
        SELECT pb FROM ProfileBadge pb 
        WHERE pb.achievementDetails IS NOT NULL 
        AND pb.achievementDetails != ''
        """)
    List<ProfileBadge> findBadgesWithAchievementDetails();
    
    /**
     * Find organization-specific badges
     * @param organizationName The organization name
     * @return List of badges from that organization
     */
    List<ProfileBadge> findByOrganizationName(String organizationName);
    
    // =====================================================
    // SOCIAL & ENGAGEMENT QUERIES
    // =====================================================
    
    /**
     * Find most viewed badges
     * @param minViews Minimum view count
     * @return List of popular badges
     */
    @Query("""
        SELECT pb FROM ProfileBadge pb 
        WHERE pb.viewCount >= :minViews
        AND pb.isActive = true
        ORDER BY pb.viewCount DESC
        """)
    List<ProfileBadge> findMostViewedBadges(@Param("minViews") Integer minViews);
    
    /**
     * Find most liked badges
     * @param minLikes Minimum like count
     * @return List of well-liked badges
     */
    @Query("""
        SELECT pb FROM ProfileBadge pb 
        WHERE pb.likeCount >= :minLikes
        AND pb.isActive = true
        ORDER BY pb.likeCount DESC
        """)
    List<ProfileBadge> findMostLikedBadges(@Param("minLikes") Integer minLikes);
    
    /**
     * Find badges with high engagement
     * @param minEngagementRate Minimum engagement rate percentage
     * @return List of highly engaging badges
     */
    @Query("""
        SELECT pb FROM ProfileBadge pb 
        WHERE pb.viewCount > 0
        AND ((pb.likeCount + pb.shareCount) * 100.0 / pb.viewCount) >= :minEngagementRate
        AND pb.isActive = true
        """)
    List<ProfileBadge> findHighEngagementBadges(@Param("minEngagementRate") Double minEngagementRate);
    
    /**
     * Find recently shared badges
     * @param since Badges shared since this date
     * @return List of recently shared badges
     */
    List<ProfileBadge> findBySharedAtAfter(LocalDateTime since);
    
    // =====================================================
    // LIMITED EDITION & SPECIAL BADGES
    // =====================================================
    
    /**
     * Find limited edition badges
     * @return List of limited edition badges
     */
    List<ProfileBadge> findByIsLimitedTrue();
    
    /**
     * Find badges by edition number
     * @param editionNumber The edition number
     * @return List of badges with that edition number
     */
    List<ProfileBadge> findByEditionNumber(Integer editionNumber);
    
    /**
     * Find special event badges
     * @param eventName The event name
     * @return List of badges from that event
     */
    @Query("""
        SELECT pb FROM ProfileBadge pb 
        WHERE pb.relatedActivity ILIKE %:eventName%
        OR pb.achievementDetails ILIKE %:eventName%
        """)
    List<ProfileBadge> findSpecialEventBadges(@Param("eventName") String eventName);
    
    /**
     * Find animated badges
     * @return List of badges with animations
     */
    List<ProfileBadge> findByIsAnimatedTrue();
    
    // =====================================================
    // RENEWAL & EXPIRATION QUERIES
    // =====================================================
    
    /**
     * Find renewable badges
     * @return List of badges that can be renewed
     */
    @Query("""
        SELECT pb FROM ProfileBadge pb 
        WHERE pb.renewalCriteria IS NOT NULL 
        AND pb.renewalCriteria != ''
        """)
    List<ProfileBadge> findRenewableBadges();
    
    /**
     * Find expired badges
     * @param currentDate Current date to check against
     * @return List of expired badges
     */
    @Query("""
        SELECT pb FROM ProfileBadge pb 
        WHERE pb.expiresAt IS NOT NULL 
        AND pb.expiresAt < :currentDate
        """)
    List<ProfileBadge> findExpiredBadges(@Param("currentDate") LocalDateTime currentDate);
    
    /**
     * Find badges eligible for renewal
     * @param currentDate Current date
     * @return List of badges that can be renewed
     */
    @Query("""
        SELECT pb FROM ProfileBadge pb 
        WHERE pb.expiresAt IS NOT NULL 
        AND pb.expiresAt < :currentDate
        AND pb.renewalCriteria IS NOT NULL
        AND pb.renewalCriteria != ''
        """)
    List<ProfileBadge> findBadgesEligibleForRenewal(@Param("currentDate") LocalDateTime currentDate);
    
    // =====================================================
    // STATISTICS & ANALYTICS QUERIES
    // =====================================================
    
    /**
     * Count badges by type
     * @param badgeType The badge type
     * @return Number of badges of that type
     */
    long countByBadgeType(String badgeType);
    
    /**
     * Count badges by category
     * @param category The badge category
     * @return Number of badges in that category
     */
    long countByCategory(String category);
    
    /**
     * Count badges by rarity
     * @param rarityLevel The rarity level
     * @return Number of badges at that rarity
     */
    long countByRarityLevel(ProfileBadge.BadgeRarity rarityLevel);
    
    /**
     * Count badges for profile
     * @param profile The profile
     * @return Number of badges for that profile
     */
    long countByProfile(Profile profile);
    
    /**
     * Get badge type distribution
     * @return Badge type statistics
     */
    @Query("""
        SELECT pb.badgeType, COUNT(pb) 
        FROM ProfileBadge pb 
        WHERE pb.isActive = true
        GROUP BY pb.badgeType
        ORDER BY COUNT(pb) DESC
        """)
    List<Object[]> getBadgeTypeDistribution();
    
    /**
     * Get rarity distribution
     * @return Rarity level statistics
     */
    @Query("""
        SELECT pb.rarityLevel, COUNT(pb) 
        FROM ProfileBadge pb 
        WHERE pb.isActive = true
        GROUP BY pb.rarityLevel
        ORDER BY pb.rarityLevel
        """)
    List<Object[]> getRarityDistribution();
    
    /**
     * Get most earned badges
     * @param limit Maximum number of badges to return
     * @return Most popular badges
     */
    @Query("""
        SELECT pb.badgeName, COUNT(pb) 
        FROM ProfileBadge pb 
        WHERE pb.isActive = true
        GROUP BY pb.badgeName
        ORDER BY COUNT(pb) DESC
        """)
    List<Object[]> getMostEarnedBadges(@Param("limit") int limit);
    
    /**
     * Get engagement statistics
     * @return Badge engagement metrics
     */
    @Query("""
        SELECT 
            SUM(pb.viewCount) as totalViews,
            SUM(pb.likeCount) as totalLikes,
            SUM(pb.shareCount) as totalShares,
            AVG(pb.viewCount) as avgViews,
            COUNT(CASE WHEN pb.likeCount > 0 THEN 1 END) as likedBadges
        FROM ProfileBadge pb 
        WHERE pb.isActive = true
        """)
    Object[] getEngagementStatistics();
    
    // =====================================================
    // LEADERBOARD & RANKING QUERIES
    // =====================================================
    
    /**
     * Get top badge collectors
     * @param limit Maximum number of profiles to return
     * @return Profiles with most badges
     */
    @Query("""
        SELECT pb.profile, COUNT(pb) as badgeCount
        FROM ProfileBadge pb 
        WHERE pb.isActive = true
        AND pb.profile.profileVisibility = 'PUBLIC'
        GROUP BY pb.profile
        ORDER BY COUNT(pb) DESC
        """)
    List<Object[]> getTopBadgeCollectors(@Param("limit") int limit);
    
    /**
     * Get highest point earners
     * @param limit Maximum number of profiles to return
     * @return Profiles with highest point totals
     */
    @Query("""
        SELECT pb.profile, SUM(pb.pointValue) as totalPoints
        FROM ProfileBadge pb 
        WHERE pb.isActive = true
        AND pb.profile.profileVisibility = 'PUBLIC'
        GROUP BY pb.profile
        ORDER BY SUM(pb.pointValue) DESC
        """)
    List<Object[]> getHighestPointEarners(@Param("limit") int limit);
    
    /**
     * Get rarest badge holders
     * @param rarityLevel The rarity level to check
     * @return Profiles with rare badges
     */
    @Query("""
        SELECT pb.profile, COUNT(pb) as rareBadgeCount
        FROM ProfileBadge pb 
        WHERE pb.rarityLevel = :rarityLevel
        AND pb.isActive = true
        AND pb.profile.profileVisibility = 'PUBLIC'
        GROUP BY pb.profile
        ORDER BY COUNT(pb) DESC
        """)
    List<Object[]> getRareBadgeHolders(@Param("rarityLevel") ProfileBadge.BadgeRarity rarityLevel);
    
    // =====================================================
    // RECOMMENDATION & SUGGESTION QUERIES
    // =====================================================
    
    /**
     * Find badges similar profiles have earned
     * @param profileId Current profile ID
     * @param badgeTypes Badge types to consider
     * @return List of badges earned by similar profiles
     */
    @Query("""
        SELECT DISTINCT pb.badgeName, COUNT(pb) as earnedCount
        FROM ProfileBadge pb
        WHERE pb.profile.id IN (
            SELECT DISTINCT pb2.profile.id
            FROM ProfileBadge pb2
            WHERE pb2.profile.id != :profileId
            AND pb2.badgeType IN :badgeTypes
            AND pb2.isActive = true
        )
        AND pb.badgeName NOT IN (
            SELECT pb3.badgeName
            FROM ProfileBadge pb3
            WHERE pb3.profile.id = :profileId
            AND pb3.isActive = true
        )
        GROUP BY pb.badgeName
        ORDER BY COUNT(pb) DESC
        """)
    List<Object[]> findSuggestedBadges(
        @Param("profileId") Long profileId,
        @Param("badgeTypes") List<String> badgeTypes
    );
    
    /**
     * Find next progression badges
     * @param profile The profile
     * @return List of next level badges in progression chains
     */
    @Query("""
        SELECT pb FROM ProfileBadge pb
        WHERE pb.isProgressive = true
        AND pb.progressionLevel = (
            SELECT MAX(pb2.progressionLevel) + 1
            FROM ProfileBadge pb2
            WHERE pb2.profile = :profile
            AND pb2.badgeName = pb.badgeName
            AND pb2.isActive = true
        )
        """)
    List<ProfileBadge> findNextProgressionBadges(@Param("profile") Profile profile);
    
    // =====================================================
    // ADMINISTRATIVE QUERIES
    // =====================================================
    
    /**
     * Find badges needing moderation
     * @return List of badges requiring review
     */
    @Query("""
        SELECT pb FROM ProfileBadge pb 
        WHERE pb.badgeName IS NULL 
           OR pb.badgeName = ''
           OR pb.badgeType IS NULL
           OR pb.pointValue < 0
           OR pb.difficultyLevel < 1
           OR pb.difficultyLevel > 10
        """)
    List<ProfileBadge> findBadgesNeedingModeration();
    
    /**
     * Find duplicate badges for cleanup
     * @param profile The profile to check for duplicates
     * @return List of potential duplicate badges
     */
    @Query("""
        SELECT pb FROM ProfileBadge pb 
        WHERE pb.profile = :profile
        AND pb.badgeName IN (
            SELECT pb2.badgeName 
            FROM ProfileBadge pb2 
            WHERE pb2.profile = :profile
            AND pb2.isProgressive = false
            GROUP BY pb2.badgeName 
            HAVING COUNT(pb2) > 1
        )
        ORDER BY pb.badgeName, pb.earnedAt
        """)
    List<ProfileBadge> findDuplicateBadges(@Param("profile") Profile profile);
    
    /**
     * Find inactive badges for cleanup
     * @return List of inactive badges
     */
    List<ProfileBadge> findByIsActiveFalse();
}