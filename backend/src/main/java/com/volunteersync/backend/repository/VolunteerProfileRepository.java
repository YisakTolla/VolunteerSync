package com.volunteersync.backend.repository;

import com.volunteersync.backend.entity.VolunteerProfile;
import com.volunteersync.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerProfileRepository extends JpaRepository<VolunteerProfile, Long> {

    // =====================================================
    // CORE PROFILE QUERIES
    // =====================================================

    /**
     * Find volunteer profile by user
     */
    Optional<VolunteerProfile> findByUser(User user);

    /**
     * Find volunteer profile by user ID
     */
    Optional<VolunteerProfile> findByUserId(Long userId);

    /**
     * Check if volunteer profile exists for user
     */
    boolean existsByUser(User user);

    boolean existsByUserId(Long userId);

    // =====================================================
    // SEARCH AND DISCOVERY
    // =====================================================

    /**
     * Search volunteers by name
     */
    List<VolunteerProfile> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName);

    /**
     * Search by location
     */
    List<VolunteerProfile> findByLocationContainingIgnoreCase(String location);

    /**
     * Find available volunteers
     */
    List<VolunteerProfile> findByIsAvailableTrue();

    /**
     * Find available volunteers in specific location
     */
    List<VolunteerProfile> findByIsAvailableTrueAndLocationContainingIgnoreCase(String location);

    /**
     * Search volunteers by bio keywords
     */
    @Query("SELECT vp FROM VolunteerProfile vp WHERE LOWER(vp.bio) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<VolunteerProfile> findByBioContaining(@Param("keyword") String keyword);

    // =====================================================
    // VOLUNTEER RANKINGS AND STATISTICS
    // =====================================================

    /**
     * Find top volunteers by hours
     */
    @Query("SELECT vp FROM VolunteerProfile vp ORDER BY vp.totalVolunteerHours DESC")
    List<VolunteerProfile> findTopVolunteersByHours();

    /**
     * Find most active volunteers by events participated
     */
    @Query("SELECT vp FROM VolunteerProfile vp ORDER BY vp.eventsParticipated DESC")
    List<VolunteerProfile> findMostActiveVolunteers();

    /**
     * Find volunteers with minimum hours
     */
    @Query("SELECT vp FROM VolunteerProfile vp WHERE vp.totalVolunteerHours >= :minHours ORDER BY vp.totalVolunteerHours DESC")
    List<VolunteerProfile> findVolunteersWithMinimumHours(@Param("minHours") Integer minHours);

    /**
     * Find new volunteers (with 0 events)
     */
    List<VolunteerProfile> findByEventsParticipated(Integer eventsCount);

    /**
     * Find experienced volunteers
     */
    @Query("SELECT vp FROM VolunteerProfile vp WHERE vp.eventsParticipated >= :minEvents")
    List<VolunteerProfile> findExperiencedVolunteers(@Param("minEvents") Integer minEvents);

    // =====================================================
    // STATISTICS QUERIES
    // =====================================================

    /**
     * Get average volunteer hours
     */
    @Query("SELECT AVG(vp.totalVolunteerHours) FROM VolunteerProfile vp WHERE vp.totalVolunteerHours > 0")
    Double getAverageVolunteerHours();

    /**
     * Get total volunteer hours across platform
     */
    @Query("SELECT SUM(vp.totalVolunteerHours) FROM VolunteerProfile vp")
    Long getTotalVolunteerHours();

    /**
     * Count active volunteers
     */
    long countByIsAvailableTrue();

    /**
     * Count volunteers by location
     */
    long countByLocationContainingIgnoreCase(String location);

    /**
     * Get volunteer distribution by hours
     */
    @Query("SELECT " +
            "SUM(CASE WHEN vp.totalVolunteerHours = 0 THEN 1 ELSE 0 END) as newVolunteers, " +
            "SUM(CASE WHEN vp.totalVolunteerHours BETWEEN 1 AND 10 THEN 1 ELSE 0 END) as beginners, " +
            "SUM(CASE WHEN vp.totalVolunteerHours BETWEEN 11 AND 50 THEN 1 ELSE 0 END) as intermediate, " +
            "SUM(CASE WHEN vp.totalVolunteerHours > 50 THEN 1 ELSE 0 END) as experienced " +
            "FROM VolunteerProfile vp")
    Object[] getVolunteerDistributionByHours();
}