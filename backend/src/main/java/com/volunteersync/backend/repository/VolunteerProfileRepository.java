package com.volunteersync.backend.repository;

import com.volunteersync.backend.entity.profile.VolunteerProfile;
import com.volunteersync.backend.entity.enums.ExperienceLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for VolunteerProfile entities.
 * Provides volunteer-specific query methods for matching, filtering,
 * and discovering volunteer profiles based on availability, experience, and preferences.
 */
@Repository
public interface VolunteerProfileRepository extends JpaRepository<VolunteerProfile, Long> {
    
    // =====================================================
    // EXPERIENCE LEVEL QUERIES
    // =====================================================
    
    /**
     * Find volunteers by experience level
     * @param experienceLevel The experience level to search for
     * @return List of volunteers with that experience level
     */
    List<VolunteerProfile> findByExperienceLevel(ExperienceLevel experienceLevel);
    
    /**
     * Find volunteers with experience level at or above specified level
     * @param minExperienceLevel Minimum experience level
     * @return List of experienced volunteers
     */
    @Query("""
        SELECT vp FROM VolunteerProfile vp 
        WHERE vp.experienceLevel IN :experienceLevels
        AND vp.profileVisibility = 'PUBLIC'
        AND vp.searchable = true
        """)
    List<VolunteerProfile> findByMinimumExperienceLevel(@Param("experienceLevels") List<ExperienceLevel> experienceLevels);
    
    /**
     * Find beginner volunteers who might need mentoring
     * @return List of beginner volunteers
     */
    @Query("""
        SELECT vp FROM VolunteerProfile vp 
        WHERE vp.experienceLevel = 'BEGINNER'
        AND vp.profileVisibility != 'PRIVATE'
        """)
    List<VolunteerProfile> findBeginnerVolunteers();
    
    /**
     * Find expert volunteers who can mentor others
     * @return List of expert volunteers
     */
    @Query("""
        SELECT vp FROM VolunteerProfile vp 
        WHERE vp.experienceLevel IN ('EXPERIENCED', 'EXPERT')
        AND vp.profileVisibility = 'PUBLIC'
        """)
    List<VolunteerProfile> findExpertVolunteers();
    
    // =====================================================
    // AVAILABILITY QUERIES
    // =====================================================
    
    /**
     * Find volunteers available on weekdays
     * @return List of volunteers available weekdays
     */
    List<VolunteerProfile> findByAvailableWeekdaysTrue();
    
    /**
     * Find volunteers available on weekends
     * @return List of volunteers available weekends
     */
    List<VolunteerProfile> findByAvailableWeekendsTrue();
    
    /**
     * Find volunteers available in the evening
     * @return List of volunteers available evenings
     */
    List<VolunteerProfile> findByAvailableEveningTrue();
    
    /**
     * Find volunteers available in the morning
     * @return List of volunteers available mornings
     */
    List<VolunteerProfile> findByAvailableMorningTrue();
    
    /**
     * Find volunteers available in the afternoon
     * @return List of volunteers available afternoons
     */
    List<VolunteerProfile> findByAvailableAfternoonTrue();
    
    /**
     * Find volunteers with flexible availability (multiple time slots)
     * @return List of highly available volunteers
     */
    @Query("""
        SELECT vp FROM VolunteerProfile vp 
        WHERE (vp.availableWeekdays = true OR vp.availableWeekends = true)
        AND (vp.availableMorning = true OR vp.availableAfternoon = true OR vp.availableEvening = true)
        AND vp.profileVisibility = 'PUBLIC'
        """)
    List<VolunteerProfile> findFlexibleVolunteers();
    
    /**
     * Find volunteers available during specific time combinations
     * @param weekdays Available weekdays
     * @param weekends Available weekends
     * @param morning Available mornings
     * @param afternoon Available afternoons
     * @param evening Available evenings
     * @return List of volunteers matching availability criteria
     */
    @Query("""
        SELECT vp FROM VolunteerProfile vp 
        WHERE (:weekdays = false OR vp.availableWeekdays = true)
        AND (:weekends = false OR vp.availableWeekends = true)
        AND (:morning = false OR vp.availableMorning = true)
        AND (:afternoon = false OR vp.availableAfternoon = true)
        AND (:evening = false OR vp.availableEvening = true)
        AND vp.profileVisibility != 'PRIVATE'
        """)
    List<VolunteerProfile> findByAvailabilityCriteria(
        @Param("weekdays") boolean weekdays,
        @Param("weekends") boolean weekends,
        @Param("morning") boolean morning,
        @Param("afternoon") boolean afternoon,
        @Param("evening") boolean evening
    );
    
    // =====================================================
    // TRAVEL & TRANSPORTATION QUERIES
    // =====================================================
    
    /**
     * Find volunteers willing to travel
     * @return List of volunteers who can travel
     */
    List<VolunteerProfile> findByWillingToTravelTrue();
    
    /**
     * Find volunteers within travel distance
     * @param maxDistance Maximum travel distance in miles
     * @return List of volunteers within travel range
     */
    @Query("""
        SELECT vp FROM VolunteerProfile vp 
        WHERE vp.willingToTravel = true 
        AND (vp.maxTravelDistance IS NULL OR vp.maxTravelDistance >= :maxDistance)
        AND vp.profileVisibility = 'PUBLIC'
        """)
    List<VolunteerProfile> findVolunteersWithinTravelDistance(@Param("maxDistance") Integer maxDistance);
    
    /**
     * Find volunteers with own transportation
     * @return List of volunteers with transportation
     */
    List<VolunteerProfile> findByHasOwnTransportationTrue();
    
    /**
     * Find volunteers who need transportation
     * @return List of volunteers needing rides
     */
    List<VolunteerProfile> findByHasOwnTransportationFalse();
    
    // =====================================================
    // DEMOGRAPHIC QUERIES
    // =====================================================
    
    /**
     * Find volunteers by age range
     * @param minAge Minimum age
     * @param maxAge Maximum age
     * @return List of volunteers in age range
     */
    @Query("""
        SELECT vp FROM VolunteerProfile vp 
        WHERE vp.dateOfBirth IS NOT NULL
        AND FUNCTION('DATEDIFF', YEAR, vp.dateOfBirth, CURRENT_DATE) BETWEEN :minAge AND :maxAge
        AND vp.profileVisibility = 'PUBLIC'
        """)
    List<VolunteerProfile> findByAgeRange(@Param("minAge") int minAge, @Param("maxAge") int maxAge);
    
    /**
     * Find volunteers by occupation
     * @param occupation The occupation to search for
     * @return List of volunteers with that occupation
     */
    List<VolunteerProfile> findByOccupationContainingIgnoreCase(String occupation);
    
    /**
     * Find volunteers by company
     * @param company The company to search for
     * @return List of volunteers from that company
     */
    List<VolunteerProfile> findByCompanyContainingIgnoreCase(String company);
    
    /**
     * Find volunteers by education
     * @param education The education to search for
     * @return List of volunteers with that educational background
     */
    List<VolunteerProfile> findByEducationContainingIgnoreCase(String education);
    
    // =====================================================
    // BACKGROUND & CLEARANCE QUERIES
    // =====================================================
    
    /**
     * Find volunteers with completed background checks
     * @return List of volunteers with background clearance
     */
    List<VolunteerProfile> findByBackgroundCheckCompletedTrue();
    
    /**
     * Find volunteers needing background checks
     * @return List of volunteers without background clearance
     */
    List<VolunteerProfile> findByBackgroundCheckCompletedFalse();
    
    /**
     * Find volunteers with current background checks
     * @param cutoffDate Background checks completed after this date
     * @return List of volunteers with recent background checks
     */
    @Query("""
        SELECT vp FROM VolunteerProfile vp 
        WHERE vp.backgroundCheckCompleted = true 
        AND vp.backgroundCheckDate >= :cutoffDate
        """)
    List<VolunteerProfile> findWithCurrentBackgroundCheck(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // =====================================================
    // EMERGENCY & SPECIAL SKILLS QUERIES
    // =====================================================
    
    /**
     * Find volunteers available for emergency response
     * @return List of emergency response volunteers
     */
    List<VolunteerProfile> findByEmergencyResponseTrue();
    
    /**
     * Find volunteers with healthcare background
     * @return List of healthcare volunteers
     */
    @Query("""
        SELECT vp FROM VolunteerProfile vp 
        WHERE vp.hasHealthcareBackground = true 
        AND vp.profileVisibility = 'PUBLIC'
        """)
    List<VolunteerProfile> findHealthcareVolunteers();
    
    /**
     * Find volunteers with language skills
     * @param language The language to search for
     * @return List of multilingual volunteers
     */
    @Query("""
        SELECT vp FROM VolunteerProfile vp 
        WHERE vp.languagesSpoken ILIKE %:language%
        AND vp.profileVisibility != 'PRIVATE'
        """)
    List<VolunteerProfile> findByLanguageSkills(@Param("language") String language);
    
    // =====================================================
    // ENGAGEMENT & ACTIVITY QUERIES
    // =====================================================
    
    /**
     * Find most active volunteers (by profile updates)
     * @param since Activity since this date
     * @return List of recently active volunteers
     */
    @Query("""
        SELECT vp FROM VolunteerProfile vp 
        WHERE vp.updatedAt >= :since
        AND vp.profileVisibility = 'PUBLIC'
        ORDER BY vp.updatedAt DESC
        """)
    List<VolunteerProfile> findActiveVolunteers(@Param("since") LocalDateTime since);
    
    /**
     * Find new volunteers (recently joined)
     * @param since Joined since this date
     * @return List of new volunteers
     */
    List<VolunteerProfile> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime since);
    
    /**
     * Find volunteers who completed orientation
     * @return List of oriented volunteers
     */
    List<VolunteerProfile> findByOrientationCompletedTrue();
    
    /**
     * Find volunteers needing orientation
     * @return List of volunteers needing orientation
     */
    List<VolunteerProfile> findByOrientationCompletedFalse();
    
    // =====================================================
    // MATCHING & RECOMMENDATION QUERIES
    // =====================================================
    
    /**
     * Find volunteers for organization matching
     * @param location Organization location
     * @param requiresBackgroundCheck Whether background check is required
     * @param requiresTravel Whether travel is required
     * @param experienceLevel Minimum experience level required
     * @return List of suitable volunteers
     */
    @Query("""
        SELECT vp FROM VolunteerProfile vp 
        WHERE vp.profileVisibility = 'PUBLIC'
        AND vp.searchable = true
        AND (:location IS NULL OR vp.location ILIKE %:location% OR vp.willingToTravel = true)
        AND (:requiresBackgroundCheck = false OR vp.backgroundCheckCompleted = true)
        AND (:requiresTravel = false OR vp.willingToTravel = true)
        AND (:experienceLevel IS NULL OR vp.experienceLevel = :experienceLevel 
             OR (vp.experienceLevel = 'EXPERIENCED' AND :experienceLevel != 'EXPERT')
             OR (vp.experienceLevel = 'EXPERT'))
        ORDER BY vp.updatedAt DESC
        """)
    List<VolunteerProfile> findMatchingVolunteers(
        @Param("location") String location,
        @Param("requiresBackgroundCheck") boolean requiresBackgroundCheck,
        @Param("requiresTravel") boolean requiresTravel,
        @Param("experienceLevel") ExperienceLevel experienceLevel
    );
    
    /**
     * Find similar volunteers for recommendations
     * @param profileId The volunteer profile to find similar profiles for
     * @param limit Maximum number of results
     * @return List of similar volunteers
     */
    @Query("""
        SELECT vp FROM VolunteerProfile vp 
        WHERE vp.id != :profileId
        AND vp.profileVisibility = 'PUBLIC'
        AND vp.experienceLevel = (SELECT vp2.experienceLevel FROM VolunteerProfile vp2 WHERE vp2.id = :profileId)
        AND (vp.occupation = (SELECT vp3.occupation FROM VolunteerProfile vp3 WHERE vp3.id = :profileId)
             OR vp.education = (SELECT vp4.education FROM VolunteerProfile vp4 WHERE vp4.id = :profileId))
        ORDER BY vp.updatedAt DESC
        LIMIT :limit
        """)
    List<VolunteerProfile> findSimilarVolunteers(@Param("profileId") Long profileId, @Param("limit") int limit);
    
    // =====================================================
    // STATISTICS & ANALYTICS QUERIES
    // =====================================================
    
    /**
     * Count volunteers by experience level
     * @param experienceLevel The experience level to count
     * @return Number of volunteers at that level
     */
    long countByExperienceLevel(ExperienceLevel experienceLevel);
    
    /**
     * Count volunteers available for weekends
     * @return Number of weekend-available volunteers
     */
    long countByAvailableWeekendsTrue();
    
    /**
     * Count volunteers willing to travel
     * @return Number of travel-willing volunteers
     */
    long countByWillingToTravelTrue();
    
    /**
     * Get volunteer statistics by experience level
     * @return Experience level distribution
     */
    @Query("""
        SELECT vp.experienceLevel, COUNT(vp) 
        FROM VolunteerProfile vp 
        WHERE vp.profileVisibility = 'PUBLIC'
        GROUP BY vp.experienceLevel
        """)
    List<Object[]> getExperienceLevelStatistics();
    
    /**
     * Get availability statistics
     * @return Availability breakdown
     */
    @Query("""
        SELECT 
            SUM(CASE WHEN vp.availableWeekdays = true THEN 1 ELSE 0 END) as weekdaysCount,
            SUM(CASE WHEN vp.availableWeekends = true THEN 1 ELSE 0 END) as weekendsCount,
            SUM(CASE WHEN vp.availableMorning = true THEN 1 ELSE 0 END) as morningCount,
            SUM(CASE WHEN vp.availableAfternoon = true THEN 1 ELSE 0 END) as afternoonCount,
            SUM(CASE WHEN vp.availableEvening = true THEN 1 ELSE 0 END) as eveningCount
        FROM VolunteerProfile vp 
        WHERE vp.profileVisibility = 'PUBLIC'
        """)
    Object[] getAvailabilityStatistics();
}