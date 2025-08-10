package com.volunteersync.backend.repository;

import com.volunteersync.backend.entity.Badge;
import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.enums.BadgeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {

       // =====================================================
       // USER BADGE QUERIES
       // =====================================================

       /**
        * Find all badges for a user
        */
       List<Badge> findByUser(User user);

       /**
        * Find user badges ordered by earned date
        */
       List<Badge> findByUserOrderByEarnedAtDesc(User user);

       /**
        * Find featured badges for user profile
        */
       List<Badge> findByUserAndIsFeaturedTrue(User user);

       /**
        * Check if user has specific badge
        */
       Optional<Badge> findByUserAndBadgeType(User user, BadgeType badgeType);

       boolean existsByUserAndBadgeType(User user, BadgeType badgeType);

       // =====================================================
       // BADGE TYPE QUERIES
       // =====================================================

       /**
        * Find all users who have specific badge
        */
       List<Badge> findByBadgeType(BadgeType badgeType);

       /**
        * Find recent earners of specific badge
        */
       List<Badge> findByBadgeTypeOrderByEarnedAtDesc(BadgeType badgeType);

       /**
        * Find badges earned in date range
        */
       @Query("SELECT b FROM Badge b WHERE b.earnedAt BETWEEN :startDate AND :endDate ORDER BY b.earnedAt DESC")
       List<Badge> findBadgesEarnedBetween(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

       // =====================================================
       // LEADERBOARD QUERIES
       // =====================================================

       /**
        * Find users with most badges
        */
       @Query("SELECT b.user, COUNT(b) as badgeCount FROM Badge b GROUP BY b.user ORDER BY badgeCount DESC")
       List<Object[]> findTopBadgeEarners();

       /**
        * Find recent badge earners (last 7 days)
        */
       @Query("SELECT b FROM Badge b WHERE b.earnedAt >= :since ORDER BY b.earnedAt DESC")
       List<Badge> findRecentBadges(@Param("since") LocalDateTime since);

       /**
        * Find users who earned badge today
        */
       @Query("SELECT b FROM Badge b WHERE DATE(b.earnedAt) = DATE(:today)")
       List<Badge> findBadgesEarnedToday(@Param("today") LocalDateTime today);

       // =====================================================
       // STATISTICS QUERIES
       // =====================================================

       /**
        * Count total badges earned
        */
       long count();

       /**
        * Count badges by type
        */
       long countByBadgeType(BadgeType badgeType);

       /**
        * Count badges for user
        */
       long countByUser(User user);

       /**
        * Get badge distribution statistics
        */
       @Query("SELECT b.badgeType, COUNT(b) FROM Badge b GROUP BY b.badgeType ORDER BY COUNT(b) DESC")
       List<Object[]> getBadgeDistributionStats();

       /**
        * Get monthly badge earning statistics
        */
       @Query("SELECT YEAR(b.earnedAt), MONTH(b.earnedAt), COUNT(b) FROM Badge b " +
                     "WHERE b.earnedAt >= :since GROUP BY YEAR(b.earnedAt), MONTH(b.earnedAt) " +
                     "ORDER BY YEAR(b.earnedAt), MONTH(b.earnedAt)")
       List<Object[]> getMonthlyBadgeStats(@Param("since") LocalDateTime since);

       /**
        * Get badge popularity (most earned first)
        */
       @Query("SELECT b.badgeType, COUNT(b) as earnedCount, " +
                     "AVG(b.progressValue) as avgProgress FROM Badge b " +
                     "GROUP BY b.badgeType ORDER BY earnedCount DESC")
       List<Object[]> getBadgePopularityStats();

       /**
        * Find rare badges (earned by less than 10% of users)
        */
       @Query("SELECT b.badgeType, COUNT(b) as earnedCount FROM Badge b " +
                     "GROUP BY b.badgeType HAVING COUNT(b) < :threshold")
       List<Object[]> findRareBadges(@Param("threshold") long threshold);

       // =====================================================
       // PROGRESS TRACKING QUERIES
       // =====================================================

       /**
        * Find badges with progress value greater than threshold
        */
       @Query("SELECT b FROM Badge b WHERE b.progressValue >= :minProgress ORDER BY b.progressValue DESC")
       List<Badge> findBadgesWithMinProgress(@Param("minProgress") Integer minProgress);

       /**
        * Get user's badge progress summary
        */
       @Query("SELECT COUNT(b) as totalBadges, AVG(b.progressValue) as avgProgress, " +
                     "MAX(b.progressValue) as maxProgress FROM Badge b WHERE b.user.id = :userId")
       Object[] getUserBadgeProgressSummary(@Param("userId") Long userId);
}
