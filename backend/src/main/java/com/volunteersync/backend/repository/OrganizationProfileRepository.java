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
    // ORGANIZATION TYPE & CLASSIFICATION QUERIES
    // =====================================================
    
    /**
     * Find organizations by type
     * @param organizationType The organization type to search for
     * @return List of organizations of that type
     */
    List<OrganizationProfile> findByOrganizationTypeIgnoreCase(String organizationType);
    
    /**
     * Find all non-profit organizations
     * @return List of non-profit organizations
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
     * @param taxExemptStatus The tax exempt status (e.g., "501(c)(3)")
     * @return List of organizations with that tax status
     */
    List<OrganizationProfile> findByTaxExemptStatus(String taxExemptStatus);
    
    /**
     * Find organizations by size category
     * @param organizationSize The size category to search for
     * @return List of organizations of that size
     */
    List<OrganizationProfile> findByOrganizationSize(String organizationSize);
    
    // =====================================================
    // VERIFICATION & TRUST QUERIES
    // =====================================================
    
    /**
     * Find verified organizations
     * @return List of verified organizations
     */
    List<OrganizationProfile> findByIsVerifiedTrue();
    
    /**
     * Find unverified organizations
     * @return List of organizations pending verification
     */
    List<OrganizationProfile> findByIsVerifiedFalse();
    
    /**
     * Find organizations verified by specific admin
     * @param verifiedBy The admin who verified the organization
     * @return List of organizations verified by that admin
     */
    List<OrganizationProfile> findByVerifiedBy(String verifiedBy);
    
    /**
     * Find recently verified organizations
     * @param since Verified since this date
     * @return List of recently verified organizations
     */
    List<OrganizationProfile> findByVerifiedAtAfter(LocalDateTime since);
    
    /**
     * Find organizations needing verification review
     * @param cutoffDate Organizations created before this date without verification
     * @return List of organizations needing verification
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
     * Find active organizations
     * @return List of active organizations
     */
    List<OrganizationProfile> findByIsActiveTrue();
    
    /**
     * Find inactive/deactivated organizations
     * @return List of inactive organizations
     */
    List<OrganizationProfile> findByIsActiveFalse();
    
    /**
     * Find organizations deactivated for specific reason
     * @param reason The deactivation reason
     * @return List of organizations deactivated for that reason
     */
    List<OrganizationProfile> findByDeactivationReason(String reason);
    
    /**
     * Find organizations deactivated recently
     * @param since Deactivated since this date
     * @return List of recently deactivated organizations
     */
    List<OrganizationProfile> findByDeactivatedAtAfter(LocalDateTime since);
    
    // =====================================================
    // LOCATION & GEOGRAPHIC QUERIES
    // =====================================================
    
    /**
     * Find organizations by city
     * @param city The city to search for
     * @return List of organizations in that city
     */
    List<OrganizationProfile> findByCityIgnoreCase(String city);
    
    /**
     * Find organizations by state
     * @param state The state to search for
     * @return List of organizations in that state
     */
    List<OrganizationProfile> findByStateIgnoreCase(String state);
    
    /**
     * Find organizations by zip code
     * @param zipCode The zip code to search for
     * @return List of organizations in that zip code
     */
    List<OrganizationProfile> findByZipCode(String zipCode);
    
    /**
     * Find organizations by address area
     * @param address Partial address to search for
     * @return List of organizations with matching address
     */
    List<OrganizationProfile> findByAddressContainingIgnoreCase(String address);
    
    /**
     * Find organizations serving specific areas
     * @param area The area they serve
     * @return List of organizations serving that area
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
     * Find organizations by focus area
     * @param focusArea The focus area to search for
     * @return List of organizations with that focus
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE op.focusAreas ILIKE %:focusArea%
        AND op.profileVisibility = 'PUBLIC'
        """)
    List<OrganizationProfile> findByFocusArea(@Param("focusArea") String focusArea);
    
    /**
     * Find organizations by mission keywords
     * @param keyword Keywords to search in mission statement
     * @return List of organizations with matching mission
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE op.missionStatement ILIKE %:keyword%
        AND op.profileVisibility = 'PUBLIC'
        """)
    List<OrganizationProfile> findByMissionKeyword(@Param("keyword") String keyword);
    
    /**
     * Find organizations serving specific demographics
     * @param demographic The target demographic
     * @return List of organizations serving that group
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE op.targetDemographic ILIKE %:demographic%
        AND op.profileVisibility = 'PUBLIC'
        """)
    List<OrganizationProfile> findByTargetDemographic(@Param("demographic") String demographic);
    
    // =====================================================
    // VOLUNTEER REQUIREMENTS QUERIES
    // =====================================================
    
    /**
     * Find organizations accepting international volunteers
     * @return List of organizations open to international volunteers
     */
    List<OrganizationProfile> findByAcceptsInternationalVolunteersTrue();
    
    /**
     * Find organizations that provide volunteer training
     * @return List of organizations offering training
     */
    List<OrganizationProfile> findByProvidesVolunteerTrainingTrue();
    
    /**
     * Find organizations requiring background checks
     * @return List of organizations with background check requirements
     */
    List<OrganizationProfile> findByRequiresBackgroundCheckTrue();
    
    /**
     * Find organizations requiring orientation
     * @return List of organizations with orientation requirements
     */
    List<OrganizationProfile> findByRequiresOrientationSessionTrue();
    
    /**
     * Find organizations by minimum volunteer age
     * @param age The minimum age requirement
     * @return List of organizations with that age requirement
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
     * @param maxHours Maximum hours volunteer can commit
     * @return List of organizations with suitable commitment levels
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
     * @param date The founding date threshold
     * @return List of organizations founded after that date
     */
    List<OrganizationProfile> findByFoundedDateAfter(LocalDate date);
    
    /**
     * Find organizations founded before specific date (established organizations)
     * @param date The founding date threshold
     * @return List of established organizations
     */
    List<OrganizationProfile> findByFoundedDateBefore(LocalDate date);
    
    /**
     * Find organizations by founding year
     * @param year The founding year
     * @return List of organizations founded in that year
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE FUNCTION('YEAR', op.foundedDate) = :year
        AND op.profileVisibility = 'PUBLIC'
        """)
    List<OrganizationProfile> findByFoundingYear(@Param("year") int year);
    
    /**
     * Find organizations incorporated after specific date
     * @param date The incorporation date threshold
     * @return List of recently incorporated organizations
     */
    List<OrganizationProfile> findByIncorporationDateAfter(LocalDate date);
    
    // =====================================================
    // CONTACT & COMMUNICATION QUERIES
    // =====================================================
    
    /**
     * Find organizations by contact email domain
     * @param domain The email domain to search for
     * @return List of organizations with that email domain
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE op.primaryContactEmail ILIKE %:domain%
           OR op.secondaryContactEmail ILIKE %:domain%
        """)
    List<OrganizationProfile> findByContactEmailDomain(@Param("domain") String domain);
    
    /**
     * Find organizations with complete contact information
     * @return List of organizations with full contact details
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
     * @return List of organizations with incomplete contact details
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
     * @param since Activity since this date
     * @return List of recently active organizations
     */
    List<OrganizationProfile> findByLastActivityDateAfter(LocalDateTime since);
    
    /**
     * Find organizations with active volunteer base
     * @param minVolunteers Minimum number of active volunteers
     * @return List of organizations with active volunteers
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
     * @param minPeopleServed Minimum number of people served annually
     * @return List of high-impact organizations
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
     * @return List of transparent organizations
     */
    List<OrganizationProfile> findByPublishesAnnualReportTrue();
    
    /**
     * Find organizations with financial reports available
     * @return List of financially transparent organizations
     */
    @Query("""
        SELECT op FROM OrganizationProfile op 
        WHERE op.financialReportsUrl IS NOT NULL 
        AND op.financialReportsUrl != ''
        """)
    List<OrganizationProfile> findOrganizationsWithFinancialReports();
    
    /**
     * Find organizations with latest annual reports
     * @return List of organizations with current annual reports
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
     * @param volunteerAge Volunteer's age
     * @param hasBackgroundCheck Whether volunteer has background check
     * @param maxCommitmentHours Volunteer's available hours
     * @param location Volunteer's location
     * @return List of suitable organizations
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
     * @param minVolunteers Minimum volunteer count
     * @param recentActivityDays Days since last activity
     * @return List of featured organizations
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
     * @param organizationType The organization type
     * @return Number of organizations of that type
     */
    long countByOrganizationTypeIgnoreCase(String organizationType);
    
    /**
     * Count verified organizations
     * @return Number of verified organizations
     */
    long countByIsVerifiedTrue();
    
    /**
     * Count active organizations
     * @return Number of active organizations
     */
    long countByIsActiveTrue();
    
    /**
     * Get organization statistics by type
     * @return Organization type distribution
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
     * @return Verification status breakdown
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
     * @return State-wise organization count
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
     * @return List of organizations with incomplete or outdated data
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
     * @param ein The Employer Identification Number
     * @return Optional containing the organization if found
     */
    Optional<OrganizationProfile> findByEin(String ein);
    
    /**
     * Find organizations by registration number
     * @param registrationNumber The state registration number
     * @param registrationState The state of registration
     * @return Optional containing the organization if found
     */
    Optional<OrganizationProfile> findByRegistrationNumberAndRegistrationState(
        String registrationNumber, String registrationState);
}