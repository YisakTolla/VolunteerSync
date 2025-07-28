package com.volunteersync.backend.repository;

import com.volunteersync.backend.entity.profile.OrganizationProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for OrganizationProfile entities.
 * Provides organization-specific query methods for discovering, filtering,
 * and managing organization profiles based on type, verification status,
 * and operational characteristics.
 */
@Repository
public interface OrganizationProfileRepository extends JpaRepository<OrganizationProfile, Long> {
    
    // =====================================================
    // CORE USER RELATIONSHIP QUERIES
    // =====================================================
    
    /**
     * Find organization profile by user ID
     */
    @Query("SELECT op FROM OrganizationProfile op WHERE op.user.id = :userId")
    Optional<OrganizationProfile> findByUserId(@Param("userId") Long userId);
    
    // =====================================================
    // ORGANIZATION TYPE & CLASSIFICATION QUERIES
    // =====================================================
    
    /**
     * Find organizations by type (case insensitive)
     */
    List<OrganizationProfile> findByOrganizationTypeIgnoreCase(String organizationType);
    
    /**
     * Find all non-profit organizations
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE op.organizationType ILIKE '%non-profit%' 
           OR op.organizationType ILIKE '%nonprofit%'
           OR op.organizationType ILIKE '%501%'
        AND op.profileVisibility = 'PUBLIC'
        """)
    List<OrganizationProfile> findNonProfitOrganizations();
    
    /**
     * Find organizations by tax exempt status
     */
    List<OrganizationProfile> findByTaxExemptStatus(String taxExemptStatus);
    
    /**
     * Find organizations by size category
     */
    List<OrganizationProfile> findByOrganizationSize(String organizationSize);
    
    // =====================================================
    // VERIFICATION & TRUST QUERIES
    // =====================================================
    
    /**
     * Find all verified organizations
     */
    List<OrganizationProfile> findByIsVerifiedTrue();
    
    /**
     * Find unverified organizations
     */
    List<OrganizationProfile> findByIsVerifiedFalse();
    
    /**
     * Find organizations verified by specific admin
     */
    List<OrganizationProfile> findByVerifiedBy(String verifiedBy);
    
    /**
     * Find recently verified organizations
     */
    List<OrganizationProfile> findByVerifiedAtAfter(LocalDateTime since);
    
    /**
     * Find organizations needing verification review
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE op.isVerified = false 
        AND op.createdAt < :cutoffDate
        AND op.isActive = true
        """)
    List<OrganizationProfile> findOrganizationsNeedingVerification(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // =====================================================
    // OPERATIONAL STATUS QUERIES
    // =====================================================
    
    /**
     * Find all active organizations
     */
    List<OrganizationProfile> findByIsActiveTrue();
    
    /**
     * Find verified and active organizations
     */
    List<OrganizationProfile> findByIsVerifiedTrueAndIsActiveTrue();
    
    /**
     * Find inactive/deactivated organizations
     */
    List<OrganizationProfile> findByIsActiveFalse();
    
    /**
     * Find organizations deactivated for specific reason
     */
    List<OrganizationProfile> findByDeactivationReason(String reason);
    
    /**
     * Find organizations deactivated recently
     */
    List<OrganizationProfile> findByDeactivatedAtAfter(LocalDateTime since);
    
    // =====================================================
    // LOCATION & GEOGRAPHIC QUERIES
    // =====================================================
    
    /**
     * Find organizations by city
     */
    List<OrganizationProfile> findByCityIgnoreCase(String city);
    
    /**
     * Find organizations by state
     */
    List<OrganizationProfile> findByStateIgnoreCase(String state);
    
    /**
     * Find organizations by city and state
     */
    List<OrganizationProfile> findByCityAndStateIgnoreCase(String city, String state);
    
    /**
     * Find organizations by zip code
     */
    List<OrganizationProfile> findByZipCode(String zipCode);
    
    /**
     * Find organizations by address area
     */
    List<OrganizationProfile> findByAddressContainingIgnoreCase(String address);
    
    /**
     * Find organizations serving specific areas
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE op.servingAreas ILIKE %:area%
        AND op.profileVisibility = 'PUBLIC'
        AND op.isActive = true
        """)
    List<OrganizationProfile> findByServingArea(@Param("area") String area);
    
    // =====================================================
    // FOCUS AREAS & MISSION QUERIES
    // =====================================================
    
    /**
     * Find organizations with focus areas containing specific area
     */
    @Query("SELECT op FROM OrganizationProfile op WHERE " +
           "LOWER(op.focusAreas) LIKE LOWER(CONCAT('%', :focusArea, '%'))")
    List<OrganizationProfile> findByFocusAreasContaining(@Param("focusArea") String focusArea);
    
    /**
     * Find organizations by focus area (alternative method)
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE op.focusAreas ILIKE %:focusArea%
        AND op.profileVisibility = 'PUBLIC'
        """)
    List<OrganizationProfile> findByFocusArea(@Param("focusArea") String focusArea);
    
    /**
     * Find organizations by mission keywords
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE op.missionStatement ILIKE %:keyword%
        AND op.profileVisibility = 'PUBLIC'
        """)
    List<OrganizationProfile> findByMissionKeyword(@Param("keyword") String keyword);
    
    /**
     * Find organizations serving specific demographics
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE op.targetDemographic ILIKE %:demographic%
        AND op.profileVisibility = 'PUBLIC'
        """)
    List<OrganizationProfile> findByTargetDemographic(@Param("demographic") String demographic);
    
    // =====================================================
    // SEARCH & DISCOVERY QUERIES
    // =====================================================
    
    /**
     * Search organizations by name or description
     */
    @Query("SELECT op FROM OrganizationProfile op WHERE " +
           "LOWER(op.displayName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(op.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(op.missionStatement) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<OrganizationProfile> searchByNameOrDescription(@Param("searchTerm") String searchTerm);
    
    // =====================================================
    // VOLUNTEER REQUIREMENTS QUERIES
    // =====================================================
    
    /**
     * Find organizations accepting international volunteers
     */
    List<OrganizationProfile> findByAcceptsInternationalVolunteersTrue();
    
    /**
     * Find organizations that provide volunteer training
     */
    List<OrganizationProfile> findByProvidesVolunteerTrainingTrue();
    
    /**
     * Find organizations requiring background checks
     */
    List<OrganizationProfile> findByRequiresBackgroundCheckTrue();
    
    /**
     * Find organizations requiring orientation
     */
    List<OrganizationProfile> findByRequiresOrientationSessionTrue();
    
    /**
     * Find organizations suitable for volunteer age
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE (op.minimumAge IS NULL OR op.minimumAge <= :age)
        AND op.profileVisibility = 'PUBLIC'
        AND op.isActive = true
        """)
    List<OrganizationProfile> findSuitableForAge(@Param("age") Integer age);
    
    /**
     * Find organizations by commitment requirements
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE (op.minimumCommitmentHours IS NULL OR op.minimumCommitmentHours <= :maxHours)
        AND op.profileVisibility = 'PUBLIC'
        AND op.isActive = true
        """)
    List<OrganizationProfile> findByCommitmentLevel(@Param("maxHours") Integer maxHours);
    
    // =====================================================
    // FOUNDING & HISTORICAL QUERIES
    // =====================================================
    
    /**
     * Find organizations founded after specific date
     */
    List<OrganizationProfile> findByFoundedDateAfter(LocalDate date);
    
    /**
     * Find organizations founded before specific date (established organizations)
     */
    List<OrganizationProfile> findByFoundedDateBefore(LocalDate date);
    
    /**
     * Find organizations by founding year
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE FUNCTION('YEAR', op.foundedDate) = :year
        AND op.profileVisibility = 'PUBLIC'
        """)
    List<OrganizationProfile> findByFoundingYear(@Param("year") int year);
    
    /**
     * Find organizations incorporated after specific date
     */
    List<OrganizationProfile> findByIncorporationDateAfter(LocalDate date);
    
    // =====================================================
    // CONTACT & COMMUNICATION QUERIES
    // =====================================================
    
    /**
     * Find organizations by contact email domain
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE op.primaryContactEmail ILIKE %:domain%
           OR op.secondaryContactEmail ILIKE %:domain%
        """)
    List<OrganizationProfile> findByContactEmailDomain(@Param("domain") String domain);
    
    /**
     * Find organizations with complete contact information
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE op.primaryContactName IS NOT NULL 
        AND op.primaryContactEmail IS NOT NULL 
        AND op.primaryContactPhone IS NOT NULL
        AND op.profileVisibility = 'PUBLIC'
        """)
    List<OrganizationProfile> findOrganizationsWithCompleteContact();
    
    /**
     * Find organizations missing contact information
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE op.primaryContactName IS NULL 
           OR op.primaryContactEmail IS NULL 
           OR op.primaryContactPhone IS NULL
        """)
    List<OrganizationProfile> findOrganizationsWithIncompleteContact();
    
    // =====================================================
    // ACTIVITY & ENGAGEMENT QUERIES
    // =====================================================
    
    /**
     * Find organizations with recent activity
     */
    List<OrganizationProfile> findByLastActivityDateAfter(LocalDateTime since);
    
    /**
     * Count active volunteers for organization
     */
    @Query("SELECT op.activeVolunteersCount FROM OrganizationProfile op WHERE op.id = :organizationId")
    Integer countActiveVolunteersByOrganizationId(@Param("organizationId") Long organizationId);
    
    /**
     * Find organizations with active volunteer base
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE op.activeVolunteersCount >= :minVolunteers
        AND op.profileVisibility = 'PUBLIC'
        AND op.isActive = true
        """)
    List<OrganizationProfile> findWithActiveVolunteers(@Param("minVolunteers") Integer minVolunteers);
    
    /**
     * Find organizations serving many people
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE op.peopleServedAnnually >= :minPeopleServed
        AND op.profileVisibility = 'PUBLIC'
        """)
    List<OrganizationProfile> findHighImpactOrganizations(@Param("minPeopleServed") Integer minPeopleServed);
    
    // =====================================================
    // TRANSPARENCY & REPORTING QUERIES
    // =====================================================
    
    /**
     * Find organizations that publish annual reports
     */
    List<OrganizationProfile> findByPublishesAnnualReportTrue();
    
    /**
     * Find organizations with financial reports available
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE op.financialReportsUrl IS NOT NULL 
        AND op.financialReportsUrl != ''
        """)
    List<OrganizationProfile> findOrganizationsWithFinancialReports();
    
    /**
     * Find organizations with latest annual reports
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE op.latestAnnualReportUrl IS NOT NULL 
        AND op.latestAnnualReportUrl != ''
        AND op.publishesAnnualReport = true
        """)
    List<OrganizationProfile> findOrganizationsWithCurrentReports();
    
    // =====================================================
    // MATCHING & RECOMMENDATION QUERIES
    // =====================================================
    
    /**
     * Find organizations suitable for volunteer matching
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE op.profileVisibility = 'PUBLIC'
        AND op.isActive = true
        AND op.isVerified = true
        AND (op.minimumAge IS NULL OR op.minimumAge <= :volunteerAge)
        AND (op.requiresBackgroundCheck = false OR :hasBackgroundCheck = true)
        AND (op.minimumCommitmentHours IS NULL OR op.minimumCommitmentHours <= :maxCommitmentHours)
        AND (:location IS NULL OR op.city ILIKE %:location% OR op.servingAreas ILIKE %:location%)
        ORDER BY op.lastActivityDate DESC NULLS LAST
        """)
    List<OrganizationProfile> findSuitableForVolunteer(
        @Param("volunteerAge") Integer volunteerAge,
        @Param("hasBackgroundCheck") boolean hasBackgroundCheck,
        @Param("maxCommitmentHours") Integer maxCommitmentHours,
        @Param("location") String location
    );
    
    /**
     * Find featured organizations for homepage
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE op.profileVisibility = 'PUBLIC'
        AND op.isActive = true
        AND op.isVerified = true
        AND op.activeVolunteersCount >= :minVolunteers
        AND op.lastActivityDate >= :cutoffDate
        AND op.missionStatement IS NOT NULL
        AND LENGTH(op.missionStatement) > 50
        ORDER BY op.lastActivityDate DESC, op.activeVolunteersCount DESC
        """)
    List<OrganizationProfile> findFeaturedOrganizations(
        @Param("minVolunteers") Integer minVolunteers,
        @Param("cutoffDate") LocalDateTime cutoffDate
    );
    
    // =====================================================
    // STATISTICS & ANALYTICS QUERIES
    // =====================================================
    
    /**
     * Count organizations by type
     */
    long countByOrganizationTypeIgnoreCase(String organizationType);
    
    /**
     * Count verified organizations
     */
    long countByIsVerifiedTrue();
    
    /**
     * Count active organizations
     */
    long countByIsActiveTrue();
    
    /**
     * Get organization statistics by type
     */
    @Query("""
        SELECT op.organizationType, COUNT(op) 
        FROM OrganizationProfile op 
        WHERE op.profileVisibility = 'PUBLIC'
        AND op.isActive = true
        GROUP BY op.organizationType
        ORDER BY COUNT(op) DESC
        """)
    List<Object[]> getOrganizationTypeStatistics();
    
    /**
     * Get verification statistics
     */
    @Query("""
        SELECT 
            COUNT(*) as total,
            SUM(CASE WHEN op.isVerified = true THEN 1 ELSE 0 END) as verified,
            SUM(CASE WHEN op.isActive = true THEN 1 ELSE 0 END) as active,
            SUM(CASE WHEN op.requiresBackgroundCheck = true THEN 1 ELSE 0 END) as requiresBackground
        FROM OrganizationProfile op
        """)
    Object[] getVerificationStatistics();
    
    /**
     * Get geographic distribution
     */
    @Query("""
        SELECT op.state, COUNT(op) 
        FROM OrganizationProfile op 
        WHERE op.profileVisibility = 'PUBLIC'
        AND op.isActive = true
        AND op.state IS NOT NULL
        GROUP BY op.state
        ORDER BY COUNT(op) DESC
        """)
    List<Object[]> getGeographicDistribution();
    
    // =====================================================
    // ADMINISTRATIVE QUERIES
    // =====================================================
    
    /**
     * Find organizations needing data review
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE op.missionStatement IS NULL 
           OR op.missionStatement = ''
           OR op.primaryContactEmail IS NULL
           OR op.foundedDate IS NULL
           OR (op.isVerified = false AND op.createdAt < :cutoffDate)
        """)
    List<OrganizationProfile> findOrganizationsNeedingReview(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Find organizations by EIN
     */
    Optional<OrganizationProfile> findByEin(String ein);
    
    /**
     * Find organizations by registration number
     */
    Optional<OrganizationProfile> findByRegistrationNumberAndRegistrationState(
        String registrationNumber, String registrationState);
}