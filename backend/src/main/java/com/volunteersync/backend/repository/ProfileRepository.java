package com.volunteersync.backend.repository;

import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.entity.profile.Profile;
import com.volunteersync.backend.entity.enums.ProfileVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Base repository interface for Profile entities.
 * Provides common query methods that work across all profile types
 * (VolunteerProfile and OrganizationProfile).
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    
    // =====================================================
    // BASIC PROFILE QUERIES
    // =====================================================
    
    /**
     * Find profile by associated user
     * @param user The user to find profile for
     * @return Optional containing the profile if found
     */
    Optional<Profile> findByUser(User user);
    
    /**
     * Find profile by user ID
     * @param userId The user ID to search for
     * @return Optional containing the profile if found
     */
    Optional<Profile> findByUserId(Long userId);
    
    /**
     * Check if profile exists for user
     * @param user The user to check
     * @return true if profile exists
     */
    boolean existsByUser(User user);
    
    /**
     * Check if profile exists for user ID
     * @param userId The user ID to check
     * @return true if profile exists
     */
    boolean existsByUserId(Long userId);
    
    // =====================================================
    // VISIBILITY & PRIVACY QUERIES
    // =====================================================
    
    /**
     * Find all public profiles
     * @return List of public profiles
     */
    List<Profile> findByProfileVisibility(ProfileVisibility profileVisibility);
    
    /**
     * Find all searchable profiles
     * @return List of searchable profiles
     */
    List<Profile> findBySearchableTrue();
    
    /**
     * Find public and searchable profiles
     * @return List of profiles that are both public and searchable
     */
    @Query("SELECT p FROM Profile p WHERE p.profileVisibility = 'PUBLIC' AND p.searchable = true")
    List<Profile> findPublicAndSearchableProfiles();
    
    /**
     * Find profiles visible to a specific profile (public + connections)
     * @param viewerProfileId The ID of the profile viewing
     * @return List of visible profiles
     */
    @Query("""
        SELECT DISTINCT p FROM Profile p 
        WHERE p.profileVisibility = 'PUBLIC' 
           OR (p.profileVisibility = 'CONNECTIONS_ONLY' 
               AND EXISTS (
                   SELECT 1 FROM UserConnection uc 
                   WHERE ((uc.requesterProfile.id = :viewerProfileId AND uc.recipientProfile.id = p.id)
                       OR (uc.recipientProfile.id = :viewerProfileId AND uc.requesterProfile.id = p.id))
                   AND uc.connectionStatus = 'ACCEPTED'
               ))
        """)
    List<Profile> findVisibleProfiles(@Param("viewerProfileId") Long viewerProfileId);
    
    // =====================================================
    // LOCATION-BASED QUERIES
    // =====================================================
    
    /**
     * Find profiles by location (city-based search)
     * @param location The location to search for
     * @return List of profiles in that location
     */
    List<Profile> findByLocationContainingIgnoreCase(String location);
    
    /**
     * Find public profiles by location
     * @param location The location to search for
     * @return List of public profiles in that location
     */
    @Query("SELECT p FROM Profile p WHERE p.location ILIKE %:location% AND p.profileVisibility = 'PUBLIC'")
    List<Profile> findPublicProfilesByLocation(@Param("location") String location);
    
    // =====================================================
    // SEARCH & DISCOVERY QUERIES
    // =====================================================
    
    /**
     * Search profiles by bio content
     * @param searchTerm The term to search for in bio
     * @return List of matching profiles
     */
    @Query("SELECT p FROM Profile p WHERE p.searchable = true AND p.bio ILIKE %:searchTerm%")
    List<Profile> searchByBio(@Param("searchTerm") String searchTerm);
    
    /**
     * Search profiles by website domain
     * @param domain The domain to search for
     * @return List of profiles with matching website
     */
    @Query("SELECT p FROM Profile p WHERE p.website ILIKE %:domain%")
    List<Profile> findByWebsiteDomain(@Param("domain") String domain);
    
    /**
     * Find profiles with social media links
     * @return List of profiles that have at least one social media link
     */
    @Query("""
        SELECT p FROM Profile p 
        WHERE p.linkedinUrl IS NOT NULL 
           OR p.twitterUrl IS NOT NULL 
           OR p.facebookUrl IS NOT NULL 
           OR p.instagramUrl IS NOT NULL
        """)
    List<Profile> findProfilesWithSocialMedia();
    
    // =====================================================
    // ACTIVITY & ENGAGEMENT QUERIES
    // =====================================================
    
    /**
     * Find recently updated profiles
     * @param since The date to search from
     * @return List of recently updated profiles
     */
    List<Profile> findByUpdatedAtAfter(LocalDateTime since);
    
    /**
     * Find recently created profiles
     * @param since The date to search from
     * @return List of recently created profiles
     */
    List<Profile> findByCreatedAtAfter(LocalDateTime since);
    
    /**
     * Find profiles that allow messaging
     * @return List of profiles that allow direct messaging
     */
    List<Profile> findByAllowMessagingTrue();
    
    /**
     * Find profiles with activity visible
     * @return List of profiles that show their activity
     */
    List<Profile> findByShowActivityTrue();
    
    // =====================================================
    // CONTACT INFORMATION QUERIES
    // =====================================================
    
    /**
     * Find profiles that show email publicly
     * @return List of profiles with public email
     */
    List<Profile> findByShowEmailTrue();
    
    /**
     * Find profiles that show phone publicly
     * @return List of profiles with public phone
     */
    List<Profile> findByShowPhoneTrue();
    
    /**
     * Find profiles with phone numbers
     * @return List of profiles that have phone numbers
     */
    @Query("SELECT p FROM Profile p WHERE p.phone IS NOT NULL AND p.phone != ''")
    List<Profile> findProfilesWithPhone();
    
    // =====================================================
    // ANALYTICS & STATISTICS QUERIES
    // =====================================================
    
    /**
     * Count profiles by visibility level
     * @param visibility The visibility level to count
     * @return Number of profiles with that visibility
     */
    long countByProfileVisibility(ProfileVisibility visibility);
    
    /**
     * Count searchable profiles
     * @return Number of searchable profiles
     */
    long countBySearchableTrue();
    
    /**
     * Count profiles created in date range
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return Number of profiles created in range
     */
    @Query("SELECT COUNT(p) FROM Profile p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    long countProfilesCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);
    
    /**
     * Count profiles by location
     * @param location The location to count
     * @return Number of profiles in that location
     */
    long countByLocationContainingIgnoreCase(String location);
    
    // =====================================================
    // BULK OPERATIONS
    // =====================================================
    
    /**
     * Find profiles needing data cleanup (missing required fields)
     * @return List of profiles with incomplete data
     */
    @Query("""
        SELECT p FROM Profile p 
        WHERE p.bio IS NULL 
           OR p.bio = '' 
           OR p.location IS NULL 
           OR p.location = ''
        """)
    List<Profile> findProfilesNeedingCleanup();
    
    /**
     * Find inactive profiles (not updated recently)
     * @param cutoffDate Profiles not updated since this date
     * @return List of inactive profiles
     */
    @Query("SELECT p FROM Profile p WHERE p.updatedAt < :cutoffDate")
    List<Profile> findInactiveProfiles(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // =====================================================
    // CUSTOM COMPLEX QUERIES
    // =====================================================
    
    /**
     * Find featured profiles for homepage/discovery
     * Profiles that are public, searchable, have good content, and recent activity
     * @param minBioLength Minimum bio length
     * @param recentActivityCutoff Recent activity cutoff date
     * @return List of featured profiles
     */
    @Query("""
        SELECT p FROM Profile p 
        WHERE p.profileVisibility = 'PUBLIC' 
          AND p.searchable = true 
          AND LENGTH(COALESCE(p.bio, '')) >= :minBioLength
          AND p.updatedAt >= :recentActivityCutoff
          AND p.location IS NOT NULL
        ORDER BY p.updatedAt DESC
        """)
    List<Profile> findFeaturedProfiles(@Param("minBioLength") int minBioLength,
                                     @Param("recentActivityCutoff") LocalDateTime recentActivityCutoff);
    
    /**
     * Get profile statistics summary
     * @return Array containing [total, public, private, connections_only, searchable]
     */
    @Query("""
        SELECT 
            COUNT(*) as total,
            SUM(CASE WHEN p.profileVisibility = 'PUBLIC' THEN 1 ELSE 0 END) as publicCount,
            SUM(CASE WHEN p.profileVisibility = 'PRIVATE' THEN 1 ELSE 0 END) as privateCount,
            SUM(CASE WHEN p.profileVisibility = 'CONNECTIONS_ONLY' THEN 1 ELSE 0 END) as connectionsOnlyCount,
            SUM(CASE WHEN p.searchable = true THEN 1 ELSE 0 END) as searchableCount
        FROM Profile p
        """)
    Object[] getProfileStatistics();
}