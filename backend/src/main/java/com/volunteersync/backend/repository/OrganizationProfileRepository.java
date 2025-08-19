package com.volunteersync.backend.repository;

import com.volunteersync.backend.entity.OrganizationProfile;
import com.volunteersync.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
       // ENHANCED CATEGORY FILTERING
       // =====================================================

       /**
        * Find organizations by primary category
        */
       List<OrganizationProfile> findByPrimaryCategoryIgnoreCase(String primaryCategory);

       /**
        * Find organizations by primary category with pagination
        */
       Page<OrganizationProfile> findByPrimaryCategoryIgnoreCase(String primaryCategory, Pageable pageable);

       /**
        * Find organizations containing specific category
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE " +
                     "LOWER(op.categories) LIKE LOWER(CONCAT('%', :category, '%'))")
       List<OrganizationProfile> findByCategoryContaining(@Param("category") String category);

       /**
        * Find organizations by multiple categories
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE " +
                     "(:education = '' OR LOWER(op.categories) LIKE LOWER(CONCAT('%', :education, '%'))) AND " +
                     "(:environment = '' OR LOWER(op.categories) LIKE LOWER(CONCAT('%', :environment, '%'))) AND " +
                     "(:healthcare = '' OR LOWER(op.categories) LIKE LOWER(CONCAT('%', :healthcare, '%'))) AND " +
                     "(:animalWelfare = '' OR LOWER(op.categories) LIKE LOWER(CONCAT('%', :animalWelfare, '%'))) AND " +
                     "(:communityService = '' OR LOWER(op.categories) LIKE LOWER(CONCAT('%', :communityService, '%'))) AND "
                     +
                     "(:humanServices = '' OR LOWER(op.categories) LIKE LOWER(CONCAT('%', :humanServices, '%'))) AND " +
                     "(:artsCulture = '' OR LOWER(op.categories) LIKE LOWER(CONCAT('%', :artsCulture, '%'))) AND " +
                     "(:youthDevelopment = '' OR LOWER(op.categories) LIKE LOWER(CONCAT('%', :youthDevelopment, '%')))")
       List<OrganizationProfile> findByMultipleCategories(
                     @Param("education") String education,
                     @Param("environment") String environment,
                     @Param("healthcare") String healthcare,
                     @Param("animalWelfare") String animalWelfare,
                     @Param("communityService") String communityService,
                     @Param("humanServices") String humanServices,
                     @Param("artsCulture") String artsCulture,
                     @Param("youthDevelopment") String youthDevelopment);

       /**
        * Get category statistics
        */
       @Query("SELECT op.primaryCategory, COUNT(op) FROM OrganizationProfile op " +
                     "WHERE op.primaryCategory IS NOT NULL " +
                     "GROUP BY op.primaryCategory ORDER BY COUNT(op) DESC")
       List<Object[]> getCategoryStatistics();

       // =====================================================
       // ENHANCED LOCATION FILTERING
       // =====================================================

       /**
        * Find organizations by country
        */
       List<OrganizationProfile> findByCountryIgnoreCase(String country);

       /**
        * Find organizations by country with pagination
        */
       Page<OrganizationProfile> findByCountryIgnoreCase(String country, Pageable pageable);

       /**
        * Find organizations by multiple countries
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE " +
                     "LOWER(op.country) IN (:countries)")
       List<OrganizationProfile> findByCountryIn(@Param("countries") List<String> countries);

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
                     "LOWER(op.country) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
                     "LOWER(op.address) LIKE LOWER(CONCAT('%', :location, '%'))")
       List<OrganizationProfile> findByLocationContaining(@Param("location") String location);

       /**
        * Search by description/mission
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE " +
                     "LOWER(op.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                     "LOWER(op.missionStatement) LIKE LOWER(CONCAT('%', :keyword, '%'))")
       List<OrganizationProfile> findByKeyword(@Param("keyword") String keyword);

       /**
        * Get location distribution statistics
        */
       @Query("SELECT op.country, COUNT(op) FROM OrganizationProfile op " +
                     "WHERE op.country IS NOT NULL " +
                     "GROUP BY op.country ORDER BY COUNT(op) DESC")
       List<Object[]> getLocationStatistics();

       // =====================================================
       // ENHANCED DATE FILTERING
       // =====================================================

       /**
        * Find organizations updated within specified hours
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE op.updatedAt >= :since")
       List<OrganizationProfile> findUpdatedSince(@Param("since") LocalDateTime since);

       /**
        * Find organizations updated in last 24 hours
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE op.updatedAt >= :yesterday")
       List<OrganizationProfile> findUpdatedInLast24Hours(@Param("yesterday") LocalDateTime yesterday);

       /**
        * Find organizations updated in last 3 days
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE op.updatedAt >= :threeDaysAgo")
       List<OrganizationProfile> findUpdatedInLast3Days(@Param("threeDaysAgo") LocalDateTime threeDaysAgo);

       /**
        * Find organizations updated in last 7 days
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE op.updatedAt >= :weekAgo")
       List<OrganizationProfile> findUpdatedInLast7Days(@Param("weekAgo") LocalDateTime weekAgo);

       /**
        * Find organizations updated in last 14 days
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE op.updatedAt >= :twoWeeksAgo")
       List<OrganizationProfile> findUpdatedInLast14Days(@Param("twoWeeksAgo") LocalDateTime twoWeeksAgo);

       /**
        * Find organizations updated in last 30 days
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE op.updatedAt >= :monthAgo")
       List<OrganizationProfile> findUpdatedInLast30Days(@Param("monthAgo") LocalDateTime monthAgo);

       /**
        * Find recently joined organizations
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE op.createdAt >= :since ORDER BY op.createdAt DESC")
       List<OrganizationProfile> findRecentlyJoined(@Param("since") LocalDateTime since);

       // =====================================================
       // ENHANCED ORGANIZATION SIZE FILTERING
       // =====================================================

       /**
        * Find organizations by size category
        */
       List<OrganizationProfile> findByOrganizationSizeIgnoreCase(String organizationSize);

       /**
        * Find organizations by size category with pagination
        */
       Page<OrganizationProfile> findByOrganizationSizeIgnoreCase(String organizationSize, Pageable pageable);

       /**
        * Find small organizations (1-50 employees)
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE " +
                     "op.organizationSize = 'Small (1-50)' OR " +
                     "(op.employeeCount IS NOT NULL AND op.employeeCount <= 50)")
       List<OrganizationProfile> findSmallOrganizations();

       /**
        * Find medium organizations (51-200 employees)
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE " +
                     "op.organizationSize = 'Medium (51-200)' OR " +
                     "(op.employeeCount IS NOT NULL AND op.employeeCount BETWEEN 51 AND 200)")
       List<OrganizationProfile> findMediumOrganizations();

       /**
        * Find large organizations (201-1000 employees)
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE " +
                     "op.organizationSize = 'Large (201-1000)' OR " +
                     "(op.employeeCount IS NOT NULL AND op.employeeCount BETWEEN 201 AND 1000)")
       List<OrganizationProfile> findLargeOrganizations();

       /**
        * Find enterprise organizations (1000+ employees)
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE " +
                     "op.organizationSize = 'Enterprise (1000+)' OR " +
                     "(op.employeeCount IS NOT NULL AND op.employeeCount > 1000)")
       List<OrganizationProfile> findEnterpriseOrganizations();

       /**
        * Find organizations by employee count range
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE " +
                     "op.employeeCount BETWEEN :minEmployees AND :maxEmployees")
       List<OrganizationProfile> findByEmployeeCountRange(
                     @Param("minEmployees") Integer minEmployees,
                     @Param("maxEmployees") Integer maxEmployees);

       /**
        * Get organization size distribution
        */
       @Query("SELECT op.organizationSize, COUNT(op) FROM OrganizationProfile op " +
                     "WHERE op.organizationSize IS NOT NULL " +
                     "GROUP BY op.organizationSize ORDER BY COUNT(op) DESC")
       List<Object[]> getOrganizationSizeStatistics();

       // =====================================================
       // COMPREHENSIVE FILTERING QUERY
       // =====================================================

       /**
        * Advanced filter query supporting all filter criteria
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE " +
                     "(:category IS NULL OR LOWER(op.categories) LIKE LOWER(CONCAT('%', :category, '%'))) AND " +
                     "(:country IS NULL OR LOWER(op.country) = LOWER(:country)) AND " +
                     "(:organizationSize IS NULL OR op.organizationSize = :organizationSize) AND " +
                     "(:updatedSince IS NULL OR op.updatedAt >= :updatedSince) AND " +
                     "(:verificationType IS NULL OR " +
                     "  (:verificationType = 'verified' AND op.isVerified = true) OR " +
                     "  (:verificationType = 'unverified' AND op.isVerified = false) OR " +
                     "  (:verificationType = 'highly_verified' AND op.isVerified = true AND op.verificationLevel IN ('Verified', 'Premium'))"
                     +
                     ") AND " +
                     "(:organizationType IS NULL OR LOWER(op.organizationType) = LOWER(:organizationType)) " +
                     "ORDER BY " +
                     "CASE WHEN :sortBy = 'name' THEN op.organizationName END ASC, " +
                     "CASE WHEN :sortBy = 'events' THEN op.totalEventsHosted END DESC, " +
                     "CASE WHEN :sortBy = 'volunteers' THEN op.totalVolunteersServed END DESC, " +
                     "CASE WHEN :sortBy = 'updated' THEN op.updatedAt END DESC, " +
                     "op.createdAt DESC")
       Page<OrganizationProfile> findWithAdvancedFilters(
                     @Param("category") String category,
                     @Param("country") String country,
                     @Param("organizationSize") String organizationSize,
                     @Param("updatedSince") LocalDateTime updatedSince,
                     @Param("verificationType") String verificationType,
                     @Param("organizationType") String organizationType,
                     @Param("sortBy") String sortBy,
                     Pageable pageable);

       // =====================================================
       // ORGANIZATION TYPE FILTERING
       // =====================================================

       /**
        * Find organizations by type
        */
       List<OrganizationProfile> findByOrganizationTypeIgnoreCase(String organizationType);

       /**
        * Find all non-profit organizations
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE " +
                     "LOWER(op.organizationType) LIKE '%non-profit%' OR " +
                     "LOWER(op.organizationType) LIKE '%nonprofit%' OR " +
                     "op.taxExemptStatus LIKE '501(c)%'")
       List<OrganizationProfile> findNonProfitOrganizations();

       /**
        * Find organizations by tax exempt status
        */
       List<OrganizationProfile> findByTaxExemptStatus(String taxExemptStatus);

       /**
        * Get organization type statistics
        */
       @Query("SELECT op.organizationType, COUNT(op) FROM OrganizationProfile op " +
                     "WHERE op.organizationType IS NOT NULL " +
                     "GROUP BY op.organizationType ORDER BY COUNT(op) DESC")
       List<Object[]> getOrganizationTypeStatistics();

       // =====================================================
       // VERIFICATION AND TRUST QUERIES
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
        * Find organizations by verification level
        */
       List<OrganizationProfile> findByVerificationLevelIgnoreCase(String verificationLevel);

       /**
        * Find highly verified organizations
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE " +
                     "op.isVerified = true AND op.verificationLevel IN ('Verified', 'Premium')")
       List<OrganizationProfile> findHighlyVerifiedOrganizations();

       /**
        * Find verified organizations by location
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE op.isVerified = true AND " +
                     "(LOWER(op.city) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
                     "LOWER(op.state) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
                     "LOWER(op.country) LIKE LOWER(CONCAT('%', :location, '%')))")
       List<OrganizationProfile> findVerifiedByLocation(@Param("location") String location);

       // =====================================================
       // PERFORMANCE AND ACTIVITY QUERIES
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

       /**
        * Find organizations by activity level
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE " +
                     "(:activityLevel = 'new' AND op.totalEventsHosted = 0) OR " +
                     "(:activityLevel = 'beginner' AND op.totalEventsHosted BETWEEN 1 AND 5) OR " +
                     "(:activityLevel = 'active' AND op.totalEventsHosted BETWEEN 6 AND 20) OR " +
                     "(:activityLevel = 'very_active' AND op.totalEventsHosted > 20)")
       List<OrganizationProfile> findByActivityLevel(@Param("activityLevel") String activityLevel);

       // =====================================================
       // INTERNATIONAL ORGANIZATIONS
       // =====================================================

       /**
        * Find international organizations (multiple language support)
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE " +
                     "op.languagesSupported IS NOT NULL AND " +
                     "op.languagesSupported LIKE '%,%' AND " +
                     "op.languagesSupported != 'English'")
       List<OrganizationProfile> findInternationalOrganizations();

       /**
        * Find organizations by language support
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE " +
                     "LOWER(op.languagesSupported) LIKE LOWER(CONCAT('%', :language, '%'))")
       List<OrganizationProfile> findByLanguageSupport(@Param("language") String language);

       // =====================================================
       // STATISTICS QUERIES
       // =====================================================

       /**
        * Count verified organizations
        */
       long countByIsVerifiedTrue();

       /**
        * Count organizations by category
        */
       long countByPrimaryCategoryIgnoreCase(String primaryCategory);

       /**
        * Count organizations by country
        */
       long countByCountryIgnoreCase(String country);

       /**
        * Count organizations by size
        */
       long countByOrganizationSizeIgnoreCase(String organizationSize);

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
        * Get comprehensive organization statistics
        */
       @Query("SELECT " +
                     "COUNT(*) as total, " +
                     "SUM(CASE WHEN op.isVerified = true THEN 1 ELSE 0 END) as verified, " +
                     "SUM(CASE WHEN op.organizationType LIKE '%non-profit%' OR op.organizationType LIKE '%nonprofit%' THEN 1 ELSE 0 END) as nonProfit, "
                     +
                     "SUM(CASE WHEN op.organizationSize = 'Small (1-50)' THEN 1 ELSE 0 END) as small, " +
                     "SUM(CASE WHEN op.organizationSize = 'Medium (51-200)' THEN 1 ELSE 0 END) as medium, " +
                     "SUM(CASE WHEN op.organizationSize = 'Large (201-1000)' THEN 1 ELSE 0 END) as large, " +
                     "SUM(CASE WHEN op.organizationSize = 'Enterprise (1000+)' THEN 1 ELSE 0 END) as enterprise, " +
                     "SUM(CASE WHEN op.languagesSupported LIKE '%,%' THEN 1 ELSE 0 END) as international " +
                     "FROM OrganizationProfile op")
       Object[] getComprehensiveStatistics();

       /**
        * Get verification statistics
        */
       @Query("SELECT " +
                     "COUNT(*) as total, " +
                     "SUM(CASE WHEN op.isVerified = true THEN 1 ELSE 0 END) as verified, " +
                     "SUM(CASE WHEN op.verificationLevel = 'Premium' THEN 1 ELSE 0 END) as premium, " +
                     "SUM(CASE WHEN op.verificationLevel = 'Verified' THEN 1 ELSE 0 END) as standardVerified, " +
                     "SUM(CASE WHEN op.verificationLevel = 'Basic' THEN 1 ELSE 0 END) as basic, " +
                     "SUM(CASE WHEN op.verificationLevel = 'Unverified' OR op.verificationLevel IS NULL THEN 1 ELSE 0 END) as unverified "
                     +
                     "FROM OrganizationProfile op")
       Object[] getVerificationStatistics();

       /**
        * Get geographic distribution
        */
       @Query("SELECT op.state, op.country, COUNT(op) FROM OrganizationProfile op " +
                     "WHERE op.state IS NOT NULL AND op.country IS NOT NULL " +
                     "GROUP BY op.state, op.country ORDER BY COUNT(op) DESC")
       List<Object[]> getGeographicDistribution();

       // =====================================================
       // SEARCH AND DISCOVERY WITH FILTERS
       // =====================================================

       /**
        * Search organizations with filters and text search
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE " +
                     "(:searchTerm IS NULL OR " +
                     "  LOWER(op.organizationName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "  LOWER(op.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "  LOWER(op.missionStatement) LIKE LOWER(CONCAT('%', :searchTerm, '%'))" +
                     ") AND " +
                     "(:category IS NULL OR LOWER(op.categories) LIKE LOWER(CONCAT('%', :category, '%'))) AND " +
                     "(:country IS NULL OR LOWER(op.country) = LOWER(:country)) AND " +
                     "(:organizationSize IS NULL OR op.organizationSize = :organizationSize) AND " +
                     "(:isVerified IS NULL OR op.isVerified = :isVerified)")
       Page<OrganizationProfile> searchWithFilters(
                     @Param("searchTerm") String searchTerm,
                     @Param("category") String category,
                     @Param("country") String country,
                     @Param("organizationSize") String organizationSize,
                     @Param("isVerified") Boolean isVerified,
                     Pageable pageable);

       // =====================================================
       // ADMINISTRATIVE QUERIES
       // =====================================================

       /**
        * Find organizations needing data review
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE " +
                     "op.missionStatement IS NULL OR op.missionStatement = '' OR " +
                     "op.primaryCategory IS NULL OR " +
                     "op.organizationType IS NULL OR " +
                     "(op.isVerified = false AND op.createdAt < :cutoffDate)")
       List<OrganizationProfile> findOrganizationsNeedingReview(@Param("cutoffDate") LocalDateTime cutoffDate);

       /**
        * Find organizations by EIN (if applicable)
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE " +
                     "op.taxExemptStatus IS NOT NULL AND " +
                     "op.taxExemptStatus LIKE CONCAT('%', :ein, '%')")
       List<OrganizationProfile> findByEinContaining(@Param("ein") String ein);

       /**
        * Find duplicate organizations by name and location
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE " +
                     "LOWER(op.organizationName) = LOWER(:name) AND " +
                     "LOWER(op.city) = LOWER(:city) AND " +
                     "LOWER(op.state) = LOWER(:state)")
       List<OrganizationProfile> findPotentialDuplicates(
                     @Param("name") String name,
                     @Param("city") String city,
                     @Param("state") String state);

       /**
        * Find organizations that need verification review
        */
       @Query("SELECT op FROM OrganizationProfile op WHERE " +
                     "op.isVerified = false AND " +
                     "op.createdAt < :cutoffDate AND " +
                     "op.organizationType IS NOT NULL AND " +
                     "op.missionStatement IS NOT NULL AND " +
                     "op.missionStatement != ''")
       List<OrganizationProfile> findOrganizationsNeedingVerification(@Param("cutoffDate") LocalDateTime cutoffDate);

       /**
        * Find organizations created after a specific date, ordered by creation date
        * (newest first)
        * 
        * @param createdAfter The cutoff date
        * @return List of organizations created after the date
        */
       List<OrganizationProfile> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime createdAfter);

       /**
        * Find organizations updated after a specific date, ordered by update date
        * (newest first)
        * 
        * @param updatedAfter The cutoff date
        * @return List of organizations updated after the date
        */
       List<OrganizationProfile> findByUpdatedAtAfterOrderByUpdatedAtDesc(LocalDateTime updatedAfter);

       /**
        * Find organization by exact name (case-insensitive)
        * 
        * @param organizationName The organization name to search for
        * @return Optional containing the organization if found
        */
       Optional<OrganizationProfile> findByOrganizationNameIgnoreCase(String organizationName);

       /**
        * Get the 50 most recently updated organizations
        * 
        * @return List of the most recently updated organizations
        */
       List<OrganizationProfile> findTop50ByOrderByUpdatedAtDesc();

       /**
        * Get the most recently created organizations with limit
        * 
        * @return List of the most recently created organizations
        */
       List<OrganizationProfile> findTop50ByOrderByCreatedAtDesc();

       /**
        * Find organizations created between two dates
        * 
        * @param startDate Start of date range
        * @param endDate   End of date range
        * @return List of organizations created in the date range
        */
       List<OrganizationProfile> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate,
                     LocalDateTime endDate);

       /**
        * Find organizations created today
        * 
        * @param startOfDay Beginning of today
        * @param endOfDay   End of today
        * @return List of organizations created today
        */
       @Query("SELECT o FROM OrganizationProfile o WHERE o.createdAt >= :startOfDay AND o.createdAt <= :endOfDay ORDER BY o.createdAt DESC")
       List<OrganizationProfile> findOrganizationsCreatedToday(@Param("startOfDay") LocalDateTime startOfDay,
                     @Param("endOfDay") LocalDateTime endOfDay);

       /**
        * Count organizations created after a specific date
        * 
        * @param createdAfter The cutoff date
        * @return Number of organizations created after the date
        */
       Long countByCreatedAtAfter(LocalDateTime createdAfter);
}