package com.volunteersync.backend.repository;

import com.volunteersync.backend.entity.profile.Profile;
import com.volunteersync.backend.entity.profile.ProfileSkill;
import com.volunteersync.backend.entity.enums.SkillLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ProfileSkill entities.
 * Provides skill-specific query methods for skill management, matching,
 * and discovery based on skill names, levels, categories, and verification status.
 */
@Repository
public interface ProfileSkillRepository extends JpaRepository<ProfileSkill, Long> {
    
    // =====================================================
    // BASIC SKILL QUERIES
    // =====================================================
    
    /**
     * Find all skills for a specific profile
     * @param profile The profile to find skills for
     * @return List of skills for that profile
     */
    List<ProfileSkill> findByProfile(Profile profile);
    
    /**
     * Find all skills for a specific profile ID
     * @param profileId The profile ID to find skills for
     * @return List of skills for that profile
     */
    List<ProfileSkill> findByProfileId(Long profileId);
    
    /**
     * Find skill by profile and skill name
     * @param profile The profile
     * @param skillName The skill name
     * @return Optional containing the skill if found
     */
    Optional<ProfileSkill> findByProfileAndSkillNameIgnoreCase(Profile profile, String skillName);
    
    /**
     * Find all public skills for a profile
     * @param profile The profile
     * @return List of public skills
     */
    List<ProfileSkill> findByProfileAndIsPublicTrue(Profile profile);
    
    /**
     * Check if profile has specific skill
     * @param profile The profile
     * @param skillName The skill name
     * @return true if profile has that skill
     */
    boolean existsByProfileAndSkillNameIgnoreCase(Profile profile, String skillName);
    
    // =====================================================
    // SKILL NAME & CATEGORY QUERIES
    // =====================================================
    
    /**
     * Find skills by name across all profiles
     * @param skillName The skill name to search for
     * @return List of skills with that name
     */
    List<ProfileSkill> findBySkillNameIgnoreCase(String skillName);
    
    /**
     * Find skills by category
     * @param category The skill category
     * @return List of skills in that category
     */
    List<ProfileSkill> findByCategory(String category);
    
    /**
     * Find skills by partial name match
     * @param skillName Partial skill name to search for
     * @return List of skills with matching names
     */
    List<ProfileSkill> findBySkillNameContainingIgnoreCase(String skillName);
    
    /**
     * Find skills by category for specific profile
     * @param profile The profile
     * @param category The skill category
     * @return List of skills in that category for the profile
     */
    List<ProfileSkill> findByProfileAndCategory(Profile profile, String category);
    
    /**
     * Get all unique skill names
     * @return List of distinct skill names
     */
    @Query("SELECT DISTINCT ps.skillName FROM ProfileSkill ps WHERE ps.isPublic = true ORDER BY ps.skillName")
    List<String> findDistinctSkillNames();
    
    /**
     * Get all unique skill categories
     * @return List of distinct categories
     */
    @Query("SELECT DISTINCT ps.category FROM ProfileSkill ps WHERE ps.category IS NOT NULL ORDER BY ps.category")
    List<String> findDistinctCategories();
    
    // =====================================================
    // SKILL LEVEL QUERIES
    // =====================================================
    
    /**
     * Find skills by level
     * @param level The skill level
     * @return List of skills at that level
     */
    List<ProfileSkill> findByLevel(SkillLevel level);
    
    /**
     * Find skills by level for specific profile
     * @param profile The profile
     * @param level The skill level
     * @return List of skills at that level for the profile
     */
    List<ProfileSkill> findByProfileAndLevel(Profile profile, SkillLevel level);
    
    /**
     * Find expert-level skills
     * @return List of expert-level skills
     */
    List<ProfileSkill> findByLevelAndIsPublicTrue(SkillLevel level);
    
    /**
     * Find skills at or above minimum level
     * @param skillName The skill name
     * @param minLevel Minimum skill level
     * @return List of skills at or above the level
     */
    @Query("""
        SELECT ps FROM ProfileSkill ps 
        WHERE ps.skillName ILIKE :skillName
        AND ps.level IN :levels
        AND ps.isPublic = true
        """)
    List<ProfileSkill> findBySkillAndMinimumLevel(
        @Param("skillName") String skillName, 
        @Param("levels") List<SkillLevel> levels
    );
    
    /**
     * Find profiles with expert skills in category
     * @param category The skill category
     * @return List of expert skills in that category
     */
    @Query("""
        SELECT ps FROM ProfileSkill ps 
        WHERE ps.category = :category
        AND ps.level IN ('ADVANCED', 'EXPERT')
        AND ps.isPublic = true
        """)
    List<ProfileSkill> findExpertSkillsInCategory(@Param("category") String category);
    
    // =====================================================
    // VERIFICATION & ENDORSEMENT QUERIES
    // =====================================================
    
    /**
     * Find verified skills
     * @return List of verified skills
     */
    List<ProfileSkill> findByVerifiedTrue();
    
    /**
     * Find verified skills for profile
     * @param profile The profile
     * @return List of verified skills for that profile
     */
    List<ProfileSkill> findByProfileAndVerifiedTrue(Profile profile);
    
    /**
     * Find skills with endorsements
     * @param minEndorsements Minimum number of endorsements
     * @return List of well-endorsed skills
     */
    @Query("""
        SELECT ps FROM ProfileSkill ps 
        WHERE ps.endorsementCount >= :minEndorsements
        AND ps.isPublic = true
        """)
    List<ProfileSkill> findSkillsWithEndorsements(@Param("minEndorsements") Integer minEndorsements);
    
    /**
     * Find skills verified by specific entity
     * @param verifiedBy Who verified the skill
     * @return List of skills verified by that entity
     */
    List<ProfileSkill> findByVerifiedBy(String verifiedBy);
    
    /**
     * Find skills needing verification
     * @param cutoffDate Skills added before this date without verification
     * @return List of skills needing verification
     */
    @Query("""
        SELECT ps FROM ProfileSkill ps 
        WHERE ps.verified = false 
        AND ps.createdAt < :cutoffDate
        AND ps.level IN ('ADVANCED', 'EXPERT')
        """)
    List<ProfileSkill> findSkillsNeedingVerification(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // =====================================================
    // CERTIFICATION QUERIES
    // =====================================================
    
    /**
     * Find skills with certifications
     * @return List of certified skills
     */
    @Query("""
        SELECT ps FROM ProfileSkill ps 
        WHERE ps.certificationName IS NOT NULL 
        AND ps.certificationName != ''
        """)
    List<ProfileSkill> findSkillsWithCertifications();
    
    /**
     * Find skills by certification provider
     * @param provider The certification provider
     * @return List of skills certified by that provider
     */
    List<ProfileSkill> findByCertificationProvider(String provider);
    
    /**
     * Find skills with current certifications
     * @param currentDate Current date to check expiry against
     * @return List of skills with non-expired certifications
     */
    @Query("""
        SELECT ps FROM ProfileSkill ps 
        WHERE ps.certificationExpiry IS NULL 
           OR ps.certificationExpiry > :currentDate
        """)
    List<ProfileSkill> findSkillsWithCurrentCertifications(@Param("currentDate") LocalDateTime currentDate);
    
    /**
     * Find skills with expiring certifications
     * @param expiryDate Date to check upcoming expiries
     * @return List of skills with certifications expiring soon
     */
    @Query("""
        SELECT ps FROM ProfileSkill ps 
        WHERE ps.certificationExpiry IS NOT NULL 
        AND ps.certificationExpiry <= :expiryDate
        AND ps.certificationExpiry > CURRENT_TIMESTAMP
        """)
    List<ProfileSkill> findSkillsWithExpiringCertifications(@Param("expiryDate") LocalDateTime expiryDate);
    
    // =====================================================
    // TEACHING & MENTORING QUERIES
    // =====================================================
    
    /**
     * Find skills where user is willing to teach
     * @return List of teachable skills
     */
    List<ProfileSkill> findByWillingToTeachTrue();
    
    /**
     * Find teachable skills by category
     * @param category The skill category
     * @return List of teachable skills in that category
     */
    List<ProfileSkill> findByCategoryAndWillingToTeachTrue(String category);
    
    /**
     * Find skills user wants to improve
     * @return List of skills being developed
     */
    List<ProfileSkill> findByLookingToImproveTrue();
    
    /**
     * Find skills available for volunteering
     * @param skillName The skill name
     * @return List of skills available for volunteer work
     */
    @Query("""
        SELECT ps FROM ProfileSkill ps 
        WHERE ps.skillName ILIKE :skillName
        AND ps.willingToVolunteer = true
        AND ps.isPublic = true
        """)
    List<ProfileSkill> findVolunteerableSkills(@Param("skillName") String skillName);
    
    /**
     * Find potential mentors for a skill
     * @param skillName The skill name
     * @param minLevel Minimum level for mentors
     * @return List of potential mentor skills
     */
    @Query("""
        SELECT ps FROM ProfileSkill ps 
        WHERE ps.skillName ILIKE :skillName
        AND ps.level IN :mentorLevels
        AND ps.willingToTeach = true
        AND ps.isPublic = true
        """)
    List<ProfileSkill> findPotentialMentors(
        @Param("skillName") String skillName,
        @Param("mentorLevels") List<SkillLevel> mentorLevels
    );
    
    // =====================================================
    // EXPERIENCE & USAGE QUERIES
    // =====================================================
    
    /**
     * Find skills by years of experience
     * @param minYears Minimum years of experience
     * @return List of experienced skills
     */
    @Query("""
        SELECT ps FROM ProfileSkill ps 
        WHERE ps.yearsOfExperience >= :minYears
        AND ps.isPublic = true
        """)
    List<ProfileSkill> findSkillsByExperience(@Param("minYears") Integer minYears);
    
    /**
     * Find recently used skills
     * @param recentUsage Recent usage indicators
     * @return List of recently used skills
     */
    @Query("""
        SELECT ps FROM ProfileSkill ps 
        WHERE ps.lastUsedAt IN :recentUsage
        AND ps.isPublic = true
        """)
    List<ProfileSkill> findRecentlyUsedSkills(@Param("recentUsage") List<String> recentUsage);
    
    /**
     * Find skills with achievements
     * @return List of skills with documented achievements
     */
    @Query("""
        SELECT ps FROM ProfileSkill ps 
        WHERE ps.achievements IS NOT NULL 
        AND ps.achievements != ''
        """)
    List<ProfileSkill> findSkillsWithAchievements();
    
    // =====================================================
    // CONTEXT & PREFERENCE QUERIES
    // =====================================================
    
    /**
     * Find skills by usage context
     * @param context The preferred usage context
     * @return List of skills with that context preference
     */
    List<ProfileSkill> findByPreferredUseContext(String context);
    
    /**
     * Find remote-friendly skills
     * @return List of skills available for remote work
     */
    @Query("""
        SELECT ps FROM ProfileSkill ps 
        WHERE ps.preferredUseContext ILIKE '%remote%'
        AND ps.willingToVolunteer = true
        AND ps.isPublic = true
        """)
    List<ProfileSkill> findRemoteFriendlySkills();
    
    // =====================================================
    // MATCHING & DISCOVERY QUERIES
    // =====================================================
    
    /**
     * Find skills matching multiple criteria
     * @param skillNames List of skill names to match
     * @param category Skill category
     * @param minLevel Minimum skill level
     * @param location Location preference
     * @return List of matching skills
     */
    @Query("""
        SELECT ps FROM ProfileSkill ps 
        WHERE (:skillNames IS NULL OR ps.skillName IN :skillNames)
        AND (:category IS NULL OR ps.category = :category)
        AND (:minLevel IS NULL OR ps.level IN :levels)
        AND (:location IS NULL OR ps.profile.location ILIKE %:location% 
             OR ps.preferredUseContext ILIKE '%remote%')
        AND ps.willingToVolunteer = true
        AND ps.isPublic = true
        """)
    List<ProfileSkill> findMatchingSkills(
        @Param("skillNames") List<String> skillNames,
        @Param("category") String category,
        @Param("levels") List<SkillLevel> levels,
        @Param("location") String location
    );
    
    /**
     * Find complementary skills for team building
     * @param profileId Current profile ID to exclude
     * @param categories Skill categories needed
     * @return List of complementary skills from other profiles
     */
    @Query("""
        SELECT ps FROM ProfileSkill ps 
        WHERE ps.profile.id != :profileId
        AND ps.category IN :categories
        AND ps.level IN ('INTERMEDIATE', 'ADVANCED', 'EXPERT')
        AND ps.willingToVolunteer = true
        AND ps.isPublic = true
        """)
    List<ProfileSkill> findComplementarySkills(
        @Param("profileId") Long profileId,
        @Param("categories") List<String> categories
    );
    
    // =====================================================
    // STATISTICS & ANALYTICS QUERIES
    // =====================================================
    
    /**
     * Count skills by level
     * @param level The skill level
     * @return Number of skills at that level
     */
    long countByLevel(SkillLevel level);
    
    /**
     * Count skills by category
     * @param category The skill category
     * @return Number of skills in that category
     */
    long countByCategory(String category);
    
    /**
     * Count verified skills
     * @return Number of verified skills
     */
    long countByVerifiedTrue();
    
    /**
     * Count skills for profile
     * @param profile The profile
     * @return Number of skills for that profile
     */
    long countByProfile(Profile profile);
    
    /**
     * Get skill level distribution
     * @return Skill level statistics
     */
    @Query("""
        SELECT ps.level, COUNT(ps) 
        FROM ProfileSkill ps 
        WHERE ps.isPublic = true
        GROUP BY ps.level
        ORDER BY ps.level
        """)
    List<Object[]> getSkillLevelDistribution();
    
    /**
     * Get top skill categories
     * @param limit Maximum number of categories to return
     * @return Most popular skill categories
     */
    @Query("""
        SELECT ps.category, COUNT(ps) 
        FROM ProfileSkill ps 
        WHERE ps.category IS NOT NULL 
        AND ps.isPublic = true
        GROUP BY ps.category
        ORDER BY COUNT(ps) DESC
        """)
    List<Object[]> getTopSkillCategories(@Param("limit") int limit);
    
    /**
     * Get most endorsed skills
     * @param limit Maximum number of skills to return
     * @return Most endorsed skills
     */
    @Query("""
        SELECT ps.skillName, SUM(ps.endorsementCount) 
        FROM ProfileSkill ps 
        WHERE ps.isPublic = true
        GROUP BY ps.skillName
        ORDER BY SUM(ps.endorsementCount) DESC
        """)
    List<Object[]> getMostEndorsedSkills(@Param("limit") int limit);
    
    /**
     * Get certification statistics
     * @return Certification provider breakdown
     */
    @Query("""
        SELECT ps.certificationProvider, COUNT(ps) 
        FROM ProfileSkill ps 
        WHERE ps.certificationProvider IS NOT NULL
        GROUP BY ps.certificationProvider
        ORDER BY COUNT(ps) DESC
        """)
    List<Object[]> getCertificationStatistics();
    
    /**
     * Get skill gap analysis
     * @param category Skill category to analyze
     * @return Skills with high demand but low supply
     */
    @Query("""
        SELECT ps.skillName, 
               COUNT(ps) as supply,
               SUM(CASE WHEN ps.lookingToImprove = true THEN 1 ELSE 0 END) as demand
        FROM ProfileSkill ps 
        WHERE ps.category = :category
        AND ps.isPublic = true
        GROUP BY ps.skillName
        HAVING SUM(CASE WHEN ps.lookingToImprove = true THEN 1 ELSE 0 END) > COUNT(ps)
        ORDER BY demand DESC, supply ASC
        """)
    List<Object[]> getSkillGapAnalysis(@Param("category") String category);
    
    // =====================================================
    // ADMINISTRATIVE QUERIES
    // =====================================================
    
    /**
     * Find duplicate skills for cleanup
     * @param profile The profile to check for duplicates
     * @return List of potential duplicate skills
     */
    @Query("""
        SELECT ps FROM ProfileSkill ps 
        WHERE ps.profile = :profile
        AND ps.skillName IN (
            SELECT ps2.skillName 
            FROM ProfileSkill ps2 
            WHERE ps2.profile = :profile
            GROUP BY ps2.skillName 
            HAVING COUNT(ps2) > 1
        )
        ORDER BY ps.skillName, ps.createdAt
        """)
    List<ProfileSkill> findDuplicateSkills(@Param("profile") Profile profile);
    
    /**
     * Find skills needing data cleanup
     * @return List of skills with incomplete or invalid data
     */
    @Query("""
        SELECT ps FROM ProfileSkill ps 
        WHERE ps.skillName IS NULL 
           OR ps.skillName = ''
           OR ps.level IS NULL
           OR (ps.verified = true AND ps.verifiedBy IS NULL)
        """)
    List<ProfileSkill> findSkillsNeedingCleanup();
    
    /**
     * Find outdated skills
     * @param cutoffDate Skills not updated since this date
     * @return List of potentially outdated skills
     */
    @Query("""
        SELECT ps FROM ProfileSkill ps 
        WHERE ps.updatedAt < :cutoffDate
        AND ps.lastUsedAt NOT IN ('Currently', 'Recent')
        """)
    List<ProfileSkill> findOutdatedSkills(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Find skills with expired certifications
     * @param currentDate Current date to check against
     * @return List of skills with expired certifications
     */
    @Query("""
        SELECT ps FROM ProfileSkill ps 
        WHERE ps.certificationExpiry IS NOT NULL 
        AND ps.certificationExpiry < :currentDate
        """)
    List<ProfileSkill> findSkillsWithExpiredCertifications(@Param("currentDate") LocalDateTime currentDate);
    
    // =====================================================
    // BULK OPERATIONS
    // =====================================================
    
    /**
     * Find skills for bulk verification
     * @param skillName Skill name to verify
     * @param minLevel Minimum level for verification
     * @return List of skills eligible for bulk verification
     */
    @Query("""
        SELECT ps FROM ProfileSkill ps 
        WHERE ps.skillName ILIKE :skillName
        AND ps.level IN :levels
        AND ps.verified = false
        AND ps.isPublic = true
        """)
    List<ProfileSkill> findSkillsForBulkVerification(
        @Param("skillName") String skillName,
        @Param("levels") List<SkillLevel> levels
    );
    
    /**
     * Find skills for bulk categorization
     * @return List of skills without categories
     */
    @Query("""
        SELECT ps FROM ProfileSkill ps 
        WHERE ps.category IS NULL 
        OR ps.category = ''
        """)
    List<ProfileSkill> findSkillsNeedingCategorization();
    
    /**
     * Find similar skills for merging
     * @param skillName Base skill name to find similar skills for
     * @return List of skills with similar names
     */
    @Query("""
        SELECT ps FROM ProfileSkill ps 
        WHERE ps.skillName ILIKE %:skillName%
        AND ps.skillName != :skillName
        AND ps.isPublic = true
        ORDER BY ps.skillName
        """)
    List<ProfileSkill> findSimilarSkills(@Param("skillName") String skillName);
}