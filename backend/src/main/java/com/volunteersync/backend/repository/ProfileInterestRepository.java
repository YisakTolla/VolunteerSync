package com.volunteersync.backend.repository;

import com.volunteersync.backend.entity.profile.Profile;
import com.volunteersync.backend.entity.profile.ProfileInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ProfileInterest entities.
 * Provides interest-specific query methods for matching volunteers with organizations
 * based on shared interests, cause areas, and passion levels.
 */
@Repository
public interface ProfileInterestRepository extends JpaRepository<ProfileInterest, Long> {
    
    // =====================================================
    // BASIC INTEREST QUERIES
    // =====================================================
    
    /**
     * Find all interests for a specific profile
     * @param profile The profile to find interests for
     * @return List of interests for that profile
     */
    List<ProfileInterest> findByProfile(Profile profile);
    
    /**
     * Find all interests for a specific profile ID
     * @param profileId The profile ID to find interests for
     * @return List of interests for that profile
     */
    List<ProfileInterest> findByProfileId(Long profileId);
    
    /**
     * Find interest by profile and interest name
     * @param profile The profile
     * @param interestName The interest name
     * @return Optional containing the interest if found
     */
    Optional<ProfileInterest> findByProfileAndInterestNameIgnoreCase(Profile profile, String interestName);
    
    /**
     * Find all active interests for a profile
     * @param profile The profile
     * @return List of active interests
     */
    List<ProfileInterest> findByProfileAndIsActiveTrue(Profile profile);
    
    /**
     * Find all public interests for a profile
     * @param profile The profile
     * @return List of public interests
     */
    List<ProfileInterest> findByProfileAndIsPublicTrue(Profile profile);
    
    /**
     * Check if profile has specific interest
     * @param profile The profile
     * @param interestName The interest name
     * @return true if profile has that interest
     */
    boolean existsByProfileAndInterestNameIgnoreCase(Profile profile, String interestName);
    
    // =====================================================
    // INTEREST NAME & CATEGORY QUERIES
    // =====================================================
    
    /**
     * Find interests by name across all profiles
     * @param interestName The interest name to search for
     * @return List of interests with that name
     */
    List<ProfileInterest> findByInterestNameIgnoreCase(String interestName);
    
    /**
     * Find interests by category
     * @param category The interest category
     * @return List of interests in that category
     */
    List<ProfileInterest> findByCategory(String category);
    
    /**
     * Find interests by partial name match
     * @param interestName Partial interest name to search for
     * @return List of interests with matching names
     */
    List<ProfileInterest> findByInterestNameContainingIgnoreCase(String interestName);
    
    /**
     * Find interests by category for specific profile
     * @param profile The profile
     * @param category The interest category
     * @return List of interests in that category for the profile
     */
    List<ProfileInterest> findByProfileAndCategory(Profile profile, String category);
    
    /**
     * Get all unique interest names
     * @return List of distinct interest names
     */
    @Query("SELECT DISTINCT pi.interestName FROM ProfileInterest pi WHERE pi.isPublic = true ORDER BY pi.interestName")
    List<String> findDistinctInterestNames();
    
    /**
     * Get all unique interest categories
     * @return List of distinct categories
     */
    @Query("SELECT DISTINCT pi.category FROM ProfileInterest pi WHERE pi.category IS NOT NULL ORDER BY pi.category")
    List<String> findDistinctCategories();
    
    // =====================================================
    // PRIORITY & PASSION LEVEL QUERIES
    // =====================================================
    
    /**
     * Find interests by priority level
     * @param priorityLevel The priority level
     * @return List of interests at that priority
     */
    List<ProfileInterest> findByPriorityLevel(Integer priorityLevel);
    
    /**
     * Find high priority interests for profile
     * @param profile The profile
     * @param minPriority Minimum priority level
     * @return List of high priority interests
     */
    @Query("""
        SELECT pi FROM ProfileInterest pi 
        WHERE pi.profile = :profile
        AND pi.priorityLevel >= :minPriority
        AND pi.isActive = true
        ORDER BY pi.priorityLevel DESC
        """)
    List<ProfileInterest> findHighPriorityInterests(@Param("profile") Profile profile, @Param("minPriority") Integer minPriority);
    
    /**
     * Find interests by passion score
     * @param minPassionScore Minimum passion score
     * @return List of highly passionate interests
     */
    @Query("""
        SELECT pi FROM ProfileInterest pi 
        WHERE pi.passionScore >= :minPassionScore
        AND pi.isPublic = true
        AND pi.isActive = true
        """)
    List<ProfileInterest> findPassionateInterests(@Param("minPassionScore") Integer minPassionScore);
    
    /**
     * Find top priority interests for profile
     * @param profile The profile
     * @param limit Maximum number of interests to return
     * @return List of top priority interests
     */
    @Query("""
        SELECT pi FROM ProfileInterest pi 
        WHERE pi.profile = :profile
        AND pi.isActive = true
        ORDER BY pi.priorityLevel DESC, pi.passionScore DESC
        """)
    List<ProfileInterest> findTopPriorityInterests(@Param("profile") Profile profile, @Param("limit") int limit);
    
    // =====================================================
    // INVOLVEMENT & EXPERIENCE QUERIES
    // =====================================================
    
    /**
     * Find interests with experience
     * @param minYearsInvolved Minimum years of involvement
     * @return List of experienced interests
     */
    @Query("""
        SELECT pi FROM ProfileInterest pi 
        WHERE pi.yearsInvolved >= :minYearsInvolved
        AND pi.isPublic = true
        """)
    List<ProfileInterest> findExperiencedInterests(@Param("minYearsInvolved") Integer minYearsInvolved);
    
    /**
     * Find interests currently being pursued
     * @return List of active interests
     */
    @Query("""
        SELECT pi FROM ProfileInterest pi 
        WHERE pi.currentlyPursuing = true
        AND pi.isPublic = true
        AND pi.isActive = true
        """)
    List<ProfileInterest> findCurrentlyPursuedInterests();
    
    /**
     * Find interests user wants to get involved with
     * @return List of interests seeking involvement
     */
    @Query("""
        SELECT pi FROM ProfileInterest pi 
        WHERE pi.wantsToGetInvolved = true
        AND pi.isPublic = true
        AND pi.isActive = true
        """)
    List<ProfileInterest> findInterestsSeekingInvolvement();
    
    /**
     * Find interests with specific involvement level
     * @param involvementLevel The involvement level
     * @return List of interests at that involvement level
     */
    List<ProfileInterest> findByInvolvementLevel(String involvementLevel);
    
    // =====================================================
    // MATCHING & DISCOVERY QUERIES
    // =====================================================
    
    /**
     * Find profiles with shared interests
     * @param interestNames List of interest names to match
     * @param excludeProfileId Profile ID to exclude from results
     * @return List of profiles with matching interests
     */
    @Query("""
        SELECT DISTINCT pi.profile FROM ProfileInterest pi 
        WHERE pi.interestName IN :interestNames
        AND pi.profile.id != :excludeProfileId
        AND pi.isPublic = true
        AND pi.isActive = true
        AND pi.profile.profileVisibility != 'PRIVATE'
        """)
    List<Profile> findProfilesWithSharedInterests(
        @Param("interestNames") List<String> interestNames,
        @Param("excludeProfileId") Long excludeProfileId
    );
    
    /**
     * Find complementary interests for networking
     * @param categories Interest categories to find
     * @param excludeProfileId Profile ID to exclude
     * @return List of profiles with complementary interests
     */
    @Query("""
        SELECT DISTINCT pi.profile FROM ProfileInterest pi 
        WHERE pi.category IN :categories
        AND pi.profile.id != :excludeProfileId
        AND pi.passionScore >= 7
        AND pi.isPublic = true
        AND pi.isActive = true
        """)
    List<Profile> findProfilesWithComplementaryInterests(
        @Param("categories") List<String> categories,
        @Param("excludeProfileId") Long excludeProfileId
    );
    
    /**
     * Find interest-based volunteer matches
     * @param organizationInterests Organization's focus areas
     * @param location Geographic location
     * @param minPassion Minimum passion score
     * @return List of matching volunteer interests
     */
    @Query("""
        SELECT pi FROM ProfileInterest pi 
        WHERE pi.interestName IN :organizationInterests
        AND pi.wantsToGetInvolved = true
        AND pi.passionScore >= :minPassion
        AND pi.isPublic = true
        AND pi.isActive = true
        AND (:location IS NULL OR pi.profile.location ILIKE %:location%)
        AND pi.profile.profileVisibility = 'PUBLIC'
        """)
    List<ProfileInterest> findVolunteerMatches(
        @Param("organizationInterests") List<String> organizationInterests,
        @Param("location") String location,
        @Param("minPassion") Integer minPassion
    );
    
    /**
     * Find trending interests
     * @param since Interests added since this date
     * @param minCount Minimum number of profiles with the interest
     * @return List of trending interests
     */
    @Query("""
        SELECT pi.interestName, COUNT(pi) as interestCount
        FROM ProfileInterest pi 
        WHERE pi.createdAt >= :since
        AND pi.isPublic = true
        AND pi.isActive = true
        GROUP BY pi.interestName
        HAVING COUNT(pi) >= :minCount
        ORDER BY COUNT(pi) DESC
        """)
    List<Object[]> findTrendingInterests(@Param("since") LocalDateTime since, @Param("minCount") Long minCount);
    
    // =====================================================
    // GEOGRAPHICAL & DEMOGRAPHIC QUERIES
    // =====================================================
    
    /**
     * Find interests by location
     * @param location Geographic location
     * @param interestName Specific interest name
     * @return List of interests in that location
     */
    @Query("""
        SELECT pi FROM ProfileInterest pi 
        WHERE pi.interestName ILIKE :interestName
        AND pi.profile.location ILIKE %:location%
        AND pi.isPublic = true
        AND pi.isActive = true
        """)
    List<ProfileInterest> findInterestsByLocation(
        @Param("interestName") String interestName,
        @Param("location") String location
    );
    
    /**
     * Find local interest communities
     * @param location Geographic area
     * @param category Interest category
     * @param minMembers Minimum community size
     * @return List of local interest communities
     */
    @Query("""
        SELECT pi.interestName, COUNT(pi) as memberCount
        FROM ProfileInterest pi 
        WHERE pi.profile.location ILIKE %:location%
        AND (:category IS NULL OR pi.category = :category)
        AND pi.isPublic = true
        AND pi.isActive = true
        GROUP BY pi.interestName
        HAVING COUNT(pi) >= :minMembers
        ORDER BY COUNT(pi) DESC
        """)
    List<Object[]> findLocalInterestCommunities(
        @Param("location") String location,
        @Param("category") String category,
        @Param("minMembers") Long minMembers
    );
    
    // =====================================================
    // IMPACT & CONTRIBUTION QUERIES
    // =====================================================
    
    /**
     * Find interests with documented impact
     * @return List of interests with contribution records
     */
    @Query("""
        SELECT pi FROM ProfileInterest pi 
        WHERE pi.personalContribution IS NOT NULL 
        AND pi.personalContribution != ''
        AND pi.isPublic = true
        """)
    List<ProfileInterest> findInterestsWithImpact();
    
    /**
     * Find interests with related projects
     * @return List of interests with associated projects
     */
    @Query("""
        SELECT pi FROM ProfileInterest pi 
        WHERE pi.relatedProjects IS NOT NULL 
        AND pi.relatedProjects != ''
        AND pi.isPublic = true
        """)
    List<ProfileInterest> findInterestsWithProjects();
    
    /**
     * Find interests inspiring others
     * @param minInfluenceScore Minimum influence score
     * @return List of influential interests
     */
    @Query("""
        SELECT pi FROM ProfileInterest pi 
        WHERE pi.influenceScore >= :minInfluenceScore
        AND pi.isPublic = true
        AND pi.isActive = true
        """)
    List<ProfileInterest> findInfluentialInterests(@Param("minInfluenceScore") Integer minInfluenceScore);
    
    // =====================================================
    // STATISTICS & ANALYTICS QUERIES
    // =====================================================
    
    /**
     * Count interests by category
     * @param category The interest category
     * @return Number of interests in that category
     */
    long countByCategory(String category);
    
    /**
     * Count active interests
     * @return Number of active interests
     */
    long countByIsActiveTrue();
    
    /**
     * Count public interests
     * @return Number of public interests
     */
    long countByIsPublicTrue();
    
    /**
     * Count interests for profile
     * @param profile The profile
     * @return Number of interests for that profile
     */
    long countByProfile(Profile profile);
    
    /**
     * Get interest category distribution
     * @return Category popularity statistics
     */
    @Query("""
        SELECT pi.category, COUNT(pi) 
        FROM ProfileInterest pi 
        WHERE pi.category IS NOT NULL 
        AND pi.isPublic = true
        AND pi.isActive = true
        GROUP BY pi.category
        ORDER BY COUNT(pi) DESC
        """)
    List<Object[]> getInterestCategoryDistribution();
    
    /**
     * Get most popular interests
     * @param limit Maximum number of interests to return
     * @return Most popular interests
     */
    @Query("""
        SELECT pi.interestName, COUNT(pi) 
        FROM ProfileInterest pi 
        WHERE pi.isPublic = true
        AND pi.isActive = true
        GROUP BY pi.interestName
        ORDER BY COUNT(pi) DESC
        """)
    List<Object[]> getMostPopularInterests(@Param("limit") int limit);
    
    /**
     * Get passion score statistics
     * @return Passion score distribution
     */
    @Query("""
        SELECT 
            AVG(pi.passionScore) as avgPassion,
            MIN(pi.passionScore) as minPassion,
            MAX(pi.passionScore) as maxPassion,
            COUNT(CASE WHEN pi.passionScore >= 8 THEN 1 END) as highPassionCount
        FROM ProfileInterest pi 
        WHERE pi.passionScore IS NOT NULL
        AND pi.isActive = true
        """)
    Object[] getPassionScoreStatistics();
    
    /**
     * Get involvement level statistics
     * @return Involvement level breakdown
     */
    @Query("""
        SELECT pi.involvementLevel, COUNT(pi) 
        FROM ProfileInterest pi 
        WHERE pi.involvementLevel IS NOT NULL
        AND pi.isActive = true
        GROUP BY pi.involvementLevel
        ORDER BY COUNT(pi) DESC
        """)
    List<Object[]> getInvolvementLevelStatistics();
    
    // =====================================================
    // ADMINISTRATIVE QUERIES
    // =====================================================
    
    /**
     * Find duplicate interests for cleanup
     * @param profile The profile to check for duplicates
     * @return List of potential duplicate interests
     */
    @Query("""
        SELECT pi FROM ProfileInterest pi 
        WHERE pi.profile = :profile
        AND pi.interestName IN (
            SELECT pi2.interestName 
            FROM ProfileInterest pi2 
            WHERE pi2.profile = :profile
            GROUP BY pi2.interestName 
            HAVING COUNT(pi2) > 1
        )
        ORDER BY pi.interestName, pi.createdAt
        """)
    List<ProfileInterest> findDuplicateInterests(@Param("profile") Profile profile);
    
    /**
     * Find interests needing categorization
     * @return List of interests without categories
     */
    @Query("""
        SELECT pi FROM ProfileInterest pi 
        WHERE pi.category IS NULL 
        OR pi.category = ''
        AND pi.isActive = true
        """)
    List<ProfileInterest> findInterestsNeedingCategorization();
    
    /**
     * Find inactive interests for cleanup
     * @param cutoffDate Interests not updated since this date
     * @return List of potentially inactive interests
     */
    @Query("""
        SELECT pi FROM ProfileInterest pi 
        WHERE pi.updatedAt < :cutoffDate
        AND pi.currentlyPursuing = false
        AND pi.wantsToGetInvolved = false
        """)
    List<ProfileInterest> findInactiveInterests(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Find interests needing data validation
     * @return List of interests with incomplete or invalid data
     */
    @Query("""
        SELECT pi FROM ProfileInterest pi 
        WHERE pi.interestName IS NULL 
           OR pi.interestName = ''
           OR (pi.passionScore IS NOT NULL AND (pi.passionScore < 1 OR pi.passionScore > 10))
           OR (pi.priorityLevel IS NOT NULL AND (pi.priorityLevel < 1 OR pi.priorityLevel > 10))
        """)
    List<ProfileInterest> findInterestsNeedingValidation();
}