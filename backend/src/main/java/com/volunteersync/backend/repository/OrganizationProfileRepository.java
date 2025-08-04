package com.volunteersync.backend.repository;

import com.volunteersync.backend.entity.OrganizationProfile;
import com.volunteersync.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationProfileRepository extends JpaRepository<OrganizationProfile, Long> {

    // =====================================================
    // CORE PROFILE QUERIES
    // =====================================================

    /**
     * Find organization profile by user
     */
    Optional<OrganizationProfile> findByUser(User user);

    /**
     * Find organization profile by user ID
     */
    Optional<OrganizationProfile> findByUserId(Long userId);

    /**
     * Check if organization profile exists
     */
    boolean existsByUser(User user);

    boolean existsByUserId(Long userId);

    // =====================================================
    // SEARCH AND DISCOVERY
    // =====================================================

    /**
     * Search organizations by name
     */
    List<OrganizationProfile> findByOrganizationNameContainingIgnoreCase(String name);

    /**
     * Search by location components
     */
    List<OrganizationProfile> findByCityContainingIgnoreCase(String city);

    List<OrganizationProfile> findByStateContainingIgnoreCase(String state);

    List<OrganizationProfile> findByZipCode(String zipCode);

    /**
     * Complex location search
     */
    @Query("SELECT op FROM OrganizationProfile op WHERE " +
            "LOWER(op.city) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
            "LOWER(op.state) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
            "LOWER(op.address) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<OrganizationProfile> findByLocationContaining(@Param("location") String location);

    /**
     * Search by description/mission
     */
    @Query("SELECT op FROM OrganizationProfile op WHERE " +
            "LOWER(op.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(op.missionStatement) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<OrganizationProfile> findByKeyword(@Param("keyword") String keyword);

    // =====================================================
    // VERIFICATION AND TRUST
    // =====================================================

    /**
     * Find verified organizations
     */
    List<OrganizationProfile> findByIsVerifiedTrue();

    /**
     * Find unverified organizations
     */
    List<OrganizationProfile> findByIsVerifiedFalse();

    /**
     * Find verified organizations by location
     */
    @Query("SELECT op FROM OrganizationProfile op WHERE op.isVerified = true AND " +
            "(LOWER(op.city) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
            "LOWER(op.state) LIKE LOWER(CONCAT('%', :location, '%')))")
    List<OrganizationProfile> findVerifiedByLocation(@Param("location") String location);

    // =====================================================
    // PERFORMANCE AND ACTIVITY
    // =====================================================

    /**
     * Find most active organizations by events hosted
     */
    @Query("SELECT op FROM OrganizationProfile op ORDER BY op.totalEventsHosted DESC")
    List<OrganizationProfile> findMostActiveOrganizations();

    /**
     * Find organizations by volunteer impact
     */
    @Query("SELECT op FROM OrganizationProfile op ORDER BY op.totalVolunteersServed DESC")
    List<OrganizationProfile> findByVolunteerImpact();

    /**
     * Find organizations with minimum activity
     */
    @Query("SELECT op FROM OrganizationProfile op WHERE op.totalEventsHosted >= :minEvents")
    List<OrganizationProfile> findActiveOrganizations(@Param("minEvents") Integer minEvents);

    /**
     * Find new organizations (no events yet)
     */
    List<OrganizationProfile> findByTotalEventsHosted(Integer eventCount);

    // =====================================================
    // STATISTICS QUERIES
    // =====================================================

    /**
     * Count verified organizations
     */
    long countByIsVerifiedTrue();

    /**
     * Get average events hosted
     */
    @Query("SELECT AVG(op.totalEventsHosted) FROM OrganizationProfile op WHERE op.totalEventsHosted > 0")
    Double getAverageEventsHosted();

    /**
     * Get total impact statistics
     */
    @Query("SELECT SUM(op.totalEventsHosted), SUM(op.totalVolunteersServed) FROM OrganizationProfile op")
    Object[] getTotalImpactStats();

    /**
     * Count organizations by location
     */
    long countByCityContainingIgnoreCase(String city);

    /**
     * Get organization distribution by activity level
     */
    @Query("SELECT " +
            "SUM(CASE WHEN op.totalEventsHosted = 0 THEN 1 ELSE 0 END) as newOrgs, " +
            "SUM(CASE WHEN op.totalEventsHosted BETWEEN 1 AND 5 THEN 1 ELSE 0 END) as beginners, " +
            "SUM(CASE WHEN op.totalEventsHosted BETWEEN 6 AND 20 THEN 1 ELSE 0 END) as active, " +
            "SUM(CASE WHEN op.totalEventsHosted > 20 THEN 1 ELSE 0 END) as veryActive " +
            "FROM OrganizationProfile op")
    Object[] getOrganizationDistributionByActivity();

    /**
     * Find recently joined organizations
     */
    @Query("SELECT op FROM OrganizationProfile op WHERE op.createdAt >= :since ORDER BY op.createdAt DESC")
    List<OrganizationProfile> findRecentlyJoined(@Param("since") LocalDateTime since);
}