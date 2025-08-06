// backend/src/main/java/com/volunteersync/backend/service/OrganizationProfileService.java
package com.volunteersync.backend.service;

import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.entity.OrganizationProfile;
import com.volunteersync.backend.enums.UserType;
import com.volunteersync.backend.repository.UserRepository;
import com.volunteersync.backend.repository.OrganizationProfileRepository;
import com.volunteersync.backend.repository.EventRepository;
import com.volunteersync.backend.repository.ApplicationRepository;
import com.volunteersync.backend.dto.OrganizationProfileDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Organization Profile service - handles organization profile management and operations
 * Manages organization profiles, search, verification, statistics, and enhanced filtering
 */
@Service
@Transactional
public class OrganizationProfileService {

    @Autowired
    private OrganizationProfileRepository organizationProfileRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private ApplicationRepository applicationRepository;

    // ==========================================
    // PROFILE MANAGEMENT METHODS
    // ==========================================

    /**
     * Create new organization profile
     */
    public OrganizationProfileDTO createProfile(CreateOrganizationProfileRequest request, Long userId) {
        System.out.println("Creating organization profile for user ID: " + userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getUserType() != UserType.ORGANIZATION) {
            throw new RuntimeException("Only organization users can create organization profiles");
        }
        
        // Check if profile already exists
        if (organizationProfileRepository.existsByUser(user)) {
            throw new RuntimeException("Organization profile already exists for this user");
        }
        
        OrganizationProfile profile = new OrganizationProfile();
        profile.setUser(user);
        profile.setOrganizationName(request.getOrganizationName());
        profile.setDescription(request.getDescription());
        profile.setMissionStatement(request.getMissionStatement());
        profile.setWebsite(request.getWebsite());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setAddress(request.getAddress());
        profile.setCity(request.getCity());
        profile.setState(request.getState());
        profile.setZipCode(request.getZipCode());
        profile.setCountry(request.getCountry() != null ? request.getCountry() : "United States");
        profile.setProfileImageUrl(request.getProfileImageUrl());
        
        // Enhanced fields
        profile.setCategories(request.getCategories());
        profile.setPrimaryCategory(request.getPrimaryCategory());
        profile.setOrganizationType(request.getOrganizationType());
        profile.setOrganizationSize(request.getOrganizationSize());
        profile.setEmployeeCount(request.getEmployeeCount());
        profile.setLanguagesSupported(request.getLanguagesSupported());
        profile.setFoundedYear(request.getFoundedYear());
        profile.setTaxExemptStatus(request.getTaxExemptStatus());
        profile.setVerificationLevel("Unverified");
        profile.setIsVerified(false);
        
        OrganizationProfile savedProfile = organizationProfileRepository.save(profile);
        
        System.out.println("Organization profile created successfully with ID: " + savedProfile.getId());
        return convertToDTO(savedProfile);
    }

    /**
     * Get organization profile by user ID
     */
    public OrganizationProfileDTO getProfileByUserId(Long userId) {
        System.out.println("Fetching organization profile for user ID: " + userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        OrganizationProfile profile = organizationProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Organization profile not found"));
        
        return convertToDTO(profile);
    }

    /**
     * Get organization profile by profile ID
     */
    public OrganizationProfileDTO getProfileById(Long profileId) {
        System.out.println("Fetching organization profile with ID: " + profileId);
        
        OrganizationProfile profile = organizationProfileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Organization profile not found"));
        
        return convertToDTO(profile);
    }

    /**
     * Update organization profile
     */
    public OrganizationProfileDTO updateProfile(Long userId, UpdateOrganizationProfileRequest request) {
        System.out.println("Updating organization profile for user ID: " + userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        OrganizationProfile profile = organizationProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Organization profile not found"));
        
        // Update basic fields if provided
        if (request.getOrganizationName() != null) {
            profile.setOrganizationName(request.getOrganizationName());
        }
        if (request.getDescription() != null) {
            profile.setDescription(request.getDescription());
        }
        if (request.getMissionStatement() != null) {
            profile.setMissionStatement(request.getMissionStatement());
        }
        if (request.getWebsite() != null) {
            profile.setWebsite(request.getWebsite());
        }
        if (request.getPhoneNumber() != null) {
            profile.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAddress() != null) {
            profile.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            profile.setCity(request.getCity());
        }
        if (request.getState() != null) {
            profile.setState(request.getState());
        }
        if (request.getZipCode() != null) {
            profile.setZipCode(request.getZipCode());
        }
        if (request.getCountry() != null) {
            profile.setCountry(request.getCountry());
        }
        if (request.getProfileImageUrl() != null) {
            profile.setProfileImageUrl(request.getProfileImageUrl());
        }
        
        // Update enhanced fields if provided
        if (request.getCategories() != null) {
            profile.setCategories(request.getCategories());
        }
        if (request.getPrimaryCategory() != null) {
            profile.setPrimaryCategory(request.getPrimaryCategory());
        }
        if (request.getOrganizationType() != null) {
            profile.setOrganizationType(request.getOrganizationType());
        }
        if (request.getOrganizationSize() != null) {
            profile.setOrganizationSize(request.getOrganizationSize());
        }
        if (request.getEmployeeCount() != null) {
            profile.setEmployeeCount(request.getEmployeeCount());
        }
        if (request.getLanguagesSupported() != null) {
            profile.setLanguagesSupported(request.getLanguagesSupported());
        }
        if (request.getFoundedYear() != null) {
            profile.setFoundedYear(request.getFoundedYear());
        }
        if (request.getTaxExemptStatus() != null) {
            profile.setTaxExemptStatus(request.getTaxExemptStatus());
        }
        
        OrganizationProfile savedProfile = organizationProfileRepository.save(profile);
        
        System.out.println("Organization profile updated successfully");
        return convertToDTO(savedProfile);
    }

    /**
     * Update organization statistics (called by EventService)
     */
    public void updateOrganizationStats(Long userId, Integer additionalEvents, Integer additionalVolunteers) {
        System.out.println("Updating organization stats for user ID: " + userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        OrganizationProfile profile = organizationProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Organization profile not found"));
        
        if (additionalEvents != null && additionalEvents > 0) {
            int currentEvents = profile.getTotalEventsHosted() != null ? profile.getTotalEventsHosted() : 0;
            profile.setTotalEventsHosted(currentEvents + additionalEvents);
        }
        
        if (additionalVolunteers != null && additionalVolunteers > 0) {
            int currentVolunteers = profile.getTotalVolunteersServed() != null ? profile.getTotalVolunteersServed() : 0;
            profile.setTotalVolunteersServed(currentVolunteers + additionalVolunteers);
        }
        
        organizationProfileRepository.save(profile);
        System.out.println("Organization statistics updated successfully");
    }

    // ==========================================
    // SEARCH AND DISCOVERY METHODS
    // ==========================================

    /**
     * Search organizations by name
     */
    public List<OrganizationProfileDTO> searchOrganizationsByName(String searchTerm) {
        System.out.println("Searching organizations by name: " + searchTerm);
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }
        
        List<OrganizationProfile> profiles = organizationProfileRepository
                .findByOrganizationNameContainingIgnoreCase(searchTerm);
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search organizations by category
     */
    public List<OrganizationProfileDTO> searchOrganizationsByCategory(String category) {
        System.out.println("Searching organizations by category: " + category);
        
        if (category == null || category.trim().isEmpty()) {
            return List.of();
        }
        
        List<OrganizationProfile> profiles = organizationProfileRepository.findByCategoryContaining(category);
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search organizations by location
     */
    public List<OrganizationProfileDTO> searchOrganizationsByLocation(String location) {
        System.out.println("Searching organizations by location: " + location);
        
        if (location == null || location.trim().isEmpty()) {
            return List.of();
        }
        
        List<OrganizationProfile> profiles = organizationProfileRepository.findByLocationContaining(location);
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search organizations by keyword in description/mission
     */
    public List<OrganizationProfileDTO> searchOrganizationsByKeyword(String keyword) {
        System.out.println("Searching organizations by keyword: " + keyword);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        
        List<OrganizationProfile> profiles = organizationProfileRepository.findByKeyword(keyword);
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Advanced organization search with filters
     */
    public Page<OrganizationProfileDTO> advancedSearch(OrganizationSearchRequest request, Pageable pageable) {
        System.out.println("Performing advanced organization search with filters");
        
        Page<OrganizationProfile> profilePage = organizationProfileRepository.searchWithFilters(
                request.getSearchTerm(),
                request.getCategory(),
                request.getCountry(),
                request.getOrganizationSize(),
                request.getIsVerified(),
                pageable
        );
        
        return profilePage.map(this::convertToDTO);
    }

    /**
     * Advanced filtering with comprehensive criteria
     */
    public Page<OrganizationProfileDTO> filterOrganizations(OrganizationFilterRequest request, Pageable pageable) {
        System.out.println("Filtering organizations with comprehensive criteria");
        
        LocalDateTime updatedSince = null;
        if (request.getUpdatedWithinDays() != null) {
            updatedSince = LocalDateTime.now().minusDays(request.getUpdatedWithinDays());
        }
        
        Page<OrganizationProfile> profilePage = organizationProfileRepository.findWithAdvancedFilters(
                request.getCategory(),
                request.getCountry(),
                request.getOrganizationSize(),
                updatedSince,
                request.getVerificationType(),
                request.getOrganizationType(),
                request.getSortBy(),
                pageable
        );
        
        return profilePage.map(this::convertToDTO);
    }

    // ==========================================
    // CATEGORY AND TYPE FILTERING
    // ==========================================

    /**
     * Get organizations by primary category
     */
    public List<OrganizationProfileDTO> getOrganizationsByCategory(String primaryCategory) {
        System.out.println("Fetching organizations by primary category: " + primaryCategory);
        
        List<OrganizationProfile> profiles = organizationProfileRepository
                .findByPrimaryCategoryIgnoreCase(primaryCategory);
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get organizations by type
     */
    public List<OrganizationProfileDTO> getOrganizationsByType(String organizationType) {
        System.out.println("Fetching organizations by type: " + organizationType);
        
        List<OrganizationProfile> profiles = organizationProfileRepository
                .findByOrganizationTypeIgnoreCase(organizationType);
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get non-profit organizations
     */
    public List<OrganizationProfileDTO> getNonProfitOrganizations() {
        System.out.println("Fetching non-profit organizations");
        
        List<OrganizationProfile> profiles = organizationProfileRepository.findNonProfitOrganizations();
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get organizations by size
     */
    public List<OrganizationProfileDTO> getOrganizationsBySize(String organizationSize) {
        System.out.println("Fetching organizations by size: " + organizationSize);
        
        List<OrganizationProfile> profiles;
        
        switch (organizationSize.toLowerCase()) {
            case "small":
                profiles = organizationProfileRepository.findSmallOrganizations();
                break;
            case "medium":
                profiles = organizationProfileRepository.findMediumOrganizations();
                break;
            case "large":
                profiles = organizationProfileRepository.findLargeOrganizations();
                break;
            case "enterprise":
                profiles = organizationProfileRepository.findEnterpriseOrganizations();
                break;
            default:
                profiles = organizationProfileRepository.findByOrganizationSizeIgnoreCase(organizationSize);
                break;
        }
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // VERIFICATION AND TRUST METHODS
    // ==========================================

    /**
     * Get verified organizations
     */
    public List<OrganizationProfileDTO> getVerifiedOrganizations() {
        System.out.println("Fetching verified organizations");
        
        List<OrganizationProfile> profiles = organizationProfileRepository.findByIsVerifiedTrue();
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get highly verified organizations
     */
    public List<OrganizationProfileDTO> getHighlyVerifiedOrganizations() {
        System.out.println("Fetching highly verified organizations");
        
        List<OrganizationProfile> profiles = organizationProfileRepository.findHighlyVerifiedOrganizations();
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update organization verification status
     */
    public OrganizationProfileDTO updateVerificationStatus(Long profileId, String verificationLevel, 
                                                          Boolean isVerified, String adminUserId) {
        System.out.println("Updating verification status for profile ID: " + profileId + " by admin: " + adminUserId);
        
        OrganizationProfile profile = organizationProfileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Organization profile not found"));
        
        profile.setVerificationLevel(verificationLevel);
        profile.setIsVerified(isVerified);
        
        OrganizationProfile savedProfile = organizationProfileRepository.save(profile);
        
        System.out.println("Verification status updated successfully");
        return convertToDTO(savedProfile);
    }

    // ==========================================
    // LOCATION-BASED METHODS
    // ==========================================

    /**
     * Get organizations by country
     */
    public List<OrganizationProfileDTO> getOrganizationsByCountry(String country) {
        System.out.println("Fetching organizations by country: " + country);
        
        List<OrganizationProfile> profiles = organizationProfileRepository.findByCountryIgnoreCase(country);
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get international organizations
     */
    public List<OrganizationProfileDTO> getInternationalOrganizations() {
        System.out.println("Fetching international organizations");
        
        List<OrganizationProfile> profiles = organizationProfileRepository.findInternationalOrganizations();
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get organizations by language support
     */
    public List<OrganizationProfileDTO> getOrganizationsByLanguage(String language) {
        System.out.println("Fetching organizations supporting language: " + language);
        
        List<OrganizationProfile> profiles = organizationProfileRepository.findByLanguageSupport(language);
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // ACTIVITY AND PERFORMANCE METHODS
    // ==========================================

    /**
     * Get most active organizations by events hosted
     */
    public List<OrganizationProfileDTO> getMostActiveOrganizations(int limit) {
        System.out.println("Fetching top " + limit + " most active organizations");
        
        List<OrganizationProfile> profiles = organizationProfileRepository.findMostActiveOrganizations();
        
        return profiles.stream()
                .limit(limit)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get organizations by volunteer impact
     */
    public List<OrganizationProfileDTO> getOrganizationsByVolunteerImpact(int limit) {
        System.out.println("Fetching top " + limit + " organizations by volunteer impact");
        
        List<OrganizationProfile> profiles = organizationProfileRepository.findByVolunteerImpact();
        
        return profiles.stream()
                .limit(limit)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get organizations by activity level
     */
    public List<OrganizationProfileDTO> getOrganizationsByActivityLevel(String activityLevel) {
        System.out.println("Fetching organizations by activity level: " + activityLevel);
        
        List<OrganizationProfile> profiles = organizationProfileRepository.findByActivityLevel(activityLevel);
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get recently updated organizations
     */
    public List<OrganizationProfileDTO> getRecentlyUpdatedOrganizations(int days) {
        System.out.println("Fetching organizations updated in last " + days + " days");
        
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<OrganizationProfile> profiles = organizationProfileRepository.findUpdatedSince(since);
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get recently joined organizations
     */
    public List<OrganizationProfileDTO> getRecentlyJoinedOrganizations(int days) {
        System.out.println("Fetching organizations that joined in last " + days + " days");
        
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<OrganizationProfile> profiles = organizationProfileRepository.findRecentlyJoined(since);
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // STATISTICS AND ANALYTICS
    // ==========================================

    /**
     * Get comprehensive organization statistics
     */
    public OrganizationStatsResponse getOrganizationStatistics() {
        System.out.println("Fetching comprehensive organization statistics");
        
        OrganizationStatsResponse stats = new OrganizationStatsResponse();
        
        // Get comprehensive statistics
        Object[] comprehensiveStats = organizationProfileRepository.getComprehensiveStatistics();
        if (comprehensiveStats != null && comprehensiveStats.length >= 8) {
            stats.setTotalOrganizations(((Number) comprehensiveStats[0]).longValue());
            stats.setVerifiedOrganizations(((Number) comprehensiveStats[1]).longValue());
            stats.setNonProfitOrganizations(((Number) comprehensiveStats[2]).longValue());
            stats.setSmallOrganizations(((Number) comprehensiveStats[3]).longValue());
            stats.setMediumOrganizations(((Number) comprehensiveStats[4]).longValue());
            stats.setLargeOrganizations(((Number) comprehensiveStats[5]).longValue());
            stats.setEnterpriseOrganizations(((Number) comprehensiveStats[6]).longValue());
            stats.setInternationalOrganizations(((Number) comprehensiveStats[7]).longValue());
        }
        
        // Get category distribution
        List<Object[]> categoryStats = organizationProfileRepository.getCategoryStatistics();
        stats.setCategoryDistribution(categoryStats.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> ((Number) result[1]).intValue()
                )));
        
        // Get organization type distribution
        List<Object[]> typeStats = organizationProfileRepository.getOrganizationTypeStatistics();
        stats.setTypeDistribution(typeStats.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> ((Number) result[1]).intValue()
                )));
        
        // Get geographic distribution
        List<Object[]> locationStats = organizationProfileRepository.getLocationStatistics();
        stats.setGeographicDistribution(locationStats.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> ((Number) result[1]).intValue()
                )));
        
        // Get verification statistics
        Object[] verificationStats = organizationProfileRepository.getVerificationStatistics();
        if (verificationStats != null && verificationStats.length >= 5) {
            VerificationStats vstats = new VerificationStats();
            vstats.setTotal(((Number) verificationStats[0]).longValue());
            vstats.setVerified(((Number) verificationStats[1]).longValue());
            vstats.setPremium(((Number) verificationStats[2]).longValue());
            vstats.setStandardVerified(((Number) verificationStats[3]).longValue());
            vstats.setBasic(((Number) verificationStats[4]).longValue());
            vstats.setUnverified(((Number) verificationStats[5]).longValue());
            stats.setVerificationStats(vstats);
        }
        
        return stats;
    }

    /**
     * Get organization profile completion statistics
     */
    public ProfileCompletionStats getProfileCompletionStats() {
        System.out.println("Calculating organization profile completion statistics");
        
        List<OrganizationProfile> allProfiles = organizationProfileRepository.findAll();
        
        long totalProfiles = allProfiles.size();
        long completeProfiles = allProfiles.stream()
                .mapToLong(profile -> isProfileComplete(profile) ? 1L : 0L)
                .sum();
        
        ProfileCompletionStats stats = new ProfileCompletionStats();
        stats.setTotalProfiles(totalProfiles);
        stats.setCompleteProfiles(completeProfiles);
        stats.setCompletionRate(totalProfiles > 0 ? (completeProfiles * 100.0 / totalProfiles) : 0.0);
        
        return stats;
    }

    /**
     * Get individual organization statistics
     */
    public IndividualOrganizationStats getIndividualOrganizationStats(Long userId) {
        System.out.println("Fetching individual statistics for organization user ID: " + userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        OrganizationProfile profile = organizationProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Organization profile not found"));
        
        IndividualOrganizationStats stats = new IndividualOrganizationStats();
        stats.setTotalEventsHosted(profile.getTotalEventsHosted() != null ? profile.getTotalEventsHosted() : 0);
        stats.setTotalVolunteersServed(profile.getTotalVolunteersServed() != null ? profile.getTotalVolunteersServed() : 0);
        stats.setProfileCompleteness(calculateProfileCompleteness(profile));
        stats.setMemberSince(profile.getCreatedAt());
        stats.setIsVerified(profile.getIsVerified());
        stats.setVerificationLevel(profile.getVerificationLevel());
        
        // Calculate activity ranking
        List<OrganizationProfile> allOrganizations = organizationProfileRepository.findMostActiveOrganizations();
        int ranking = -1;
        for (int i = 0; i < allOrganizations.size(); i++) {
            if (allOrganizations.get(i).getId().equals(profile.getId())) {
                ranking = i + 1;
                break;
            }
        }
        stats.setActivityRanking(ranking);
        
        return stats;
    }

    // ==========================================
    // ADMINISTRATIVE METHODS
    // ==========================================

    /**
     * Find organizations needing review
     */
    public List<OrganizationProfileDTO> getOrganizationsNeedingReview() {
        System.out.println("Fetching organizations needing data review");
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30); // Organizations unverified for 30+ days
        List<OrganizationProfile> profiles = organizationProfileRepository.findOrganizationsNeedingReview(cutoffDate);
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Find organizations needing verification review
     */
    public List<OrganizationProfileDTO> getOrganizationsNeedingVerification() {
        System.out.println("Fetching organizations needing verification review");
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7); // Complete profiles unverified for 7+ days
        List<OrganizationProfile> profiles = organizationProfileRepository.findOrganizationsNeedingVerification(cutoffDate);
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Find potential duplicate organizations
     */
    public List<List<OrganizationProfileDTO>> findPotentialDuplicates() {
        System.out.println("Searching for potential duplicate organizations");
        
        // This is a simplified approach - in a real system, you'd use more sophisticated algorithms
        List<OrganizationProfile> allProfiles = organizationProfileRepository.findAll();
        List<List<OrganizationProfileDTO>> duplicateGroups = List.of();
        
        // Group by similar names and locations
        for (OrganizationProfile profile : allProfiles) {
            if (profile.getOrganizationName() != null && profile.getCity() != null && profile.getState() != null) {
                List<OrganizationProfile> potentialDuplicates = organizationProfileRepository
                        .findPotentialDuplicates(profile.getOrganizationName(), profile.getCity(), profile.getState());
                
                if (potentialDuplicates.size() > 1) {
                    List<OrganizationProfileDTO> duplicateGroup = potentialDuplicates.stream()
                            .map(this::convertToDTO)
                            .collect(Collectors.toList());
                    duplicateGroups.add(duplicateGroup);
                }
            }
        }
        
        return duplicateGroups;
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    private boolean isProfileComplete(OrganizationProfile profile) {
        return profile.getOrganizationName() != null && !profile.getOrganizationName().trim().isEmpty() &&
               profile.getDescription() != null && !profile.getDescription().trim().isEmpty() &&
               profile.getMissionStatement() != null && !profile.getMissionStatement().trim().isEmpty() &&
               profile.getPrimaryCategory() != null && !profile.getPrimaryCategory().trim().isEmpty() &&
               profile.getOrganizationType() != null && !profile.getOrganizationType().trim().isEmpty() &&
               profile.getCity() != null && !profile.getCity().trim().isEmpty() &&
               profile.getState() != null && !profile.getState().trim().isEmpty();
    }

    private int calculateProfileCompleteness(OrganizationProfile profile) {
        int completeness = 0;
        int totalFields = 12; // Count of key profile fields
        
        if (profile.getOrganizationName() != null && !profile.getOrganizationName().trim().isEmpty()) completeness++;
        if (profile.getDescription() != null && !profile.getDescription().trim().isEmpty()) completeness++;
        if (profile.getMissionStatement() != null && !profile.getMissionStatement().trim().isEmpty()) completeness++;
        if (profile.getWebsite() != null && !profile.getWebsite().trim().isEmpty()) completeness++;
        if (profile.getPhoneNumber() != null && !profile.getPhoneNumber().trim().isEmpty()) completeness++;
        if (profile.getAddress() != null && !profile.getAddress().trim().isEmpty()) completeness++;
        if (profile.getCity() != null && !profile.getCity().trim().isEmpty()) completeness++;
        if (profile.getState() != null && !profile.getState().trim().isEmpty()) completeness++;
        if (profile.getPrimaryCategory() != null && !profile.getPrimaryCategory().trim().isEmpty()) completeness++;
        if (profile.getOrganizationType() != null && !profile.getOrganizationType().trim().isEmpty()) completeness++;
        if (profile.getOrganizationSize() != null && !profile.getOrganizationSize().trim().isEmpty()) completeness++;
        if (profile.getProfileImageUrl() != null && !profile.getProfileImageUrl().trim().isEmpty()) completeness++;
        
        return (completeness * 100) / totalFields;
    }

    private OrganizationProfileDTO convertToDTO(OrganizationProfile profile) {
        OrganizationProfileDTO dto = new OrganizationProfileDTO();
        
        // Basic fields
        dto.setId(profile.getId());
        dto.setUserId(profile.getUser().getId());
        dto.setOrganizationName(profile.getOrganizationName());
        dto.setDescription(profile.getDescription());
        dto.setMissionStatement(profile.getMissionStatement());
        dto.setWebsite(profile.getWebsite());
        dto.setPhoneNumber(profile.getPhoneNumber());
        dto.setAddress(profile.getAddress());
        dto.setCity(profile.getCity());
        dto.setState(profile.getState());
        dto.setZipCode(profile.getZipCode());
        dto.setProfileImageUrl(profile.getProfileImageUrl());
        dto.setIsVerified(profile.getIsVerified());
        dto.setTotalEventsHosted(profile.getTotalEventsHosted());
        dto.setTotalVolunteersServed(profile.getTotalVolunteersServed());
        dto.setCreatedAt(profile.getCreatedAt());
        dto.setUpdatedAt(profile.getUpdatedAt());
        
        // Enhanced fields
        dto.setCategories(profile.getCategories());
        dto.setPrimaryCategory(profile.getPrimaryCategory());
        dto.setOrganizationType(profile.getOrganizationType());
        dto.setOrganizationSize(profile.getOrganizationSize());
        dto.setEmployeeCount(profile.getEmployeeCount());
        dto.setCountry(profile.getCountry());
        dto.setLanguagesSupported(profile.getLanguagesSupported());
        dto.setFoundedYear(profile.getFoundedYear());
        dto.setTaxExemptStatus(profile.getTaxExemptStatus());
        dto.setVerificationLevel(profile.getVerificationLevel());
        
        return dto;
    }

    // ==========================================
    // REQUEST/RESPONSE CLASSES
    // ==========================================

    public static class CreateOrganizationProfileRequest {
        private String organizationName;
        private String description;
        private String missionStatement;
        private String website;
        private String phoneNumber;
        private String address;
        private String city;
        private String state;
        private String zipCode;
        private String country;
        private String profileImageUrl;
        private String categories;
        private String primaryCategory;
        private String organizationType;
        private String organizationSize;
        private Integer employeeCount;
        private String languagesSupported;
        private Integer foundedYear;
        private String taxExemptStatus;
        
        // Getters and setters
        public String getOrganizationName() { return organizationName; }
        public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getMissionStatement() { return missionStatement; }
        public void setMissionStatement(String missionStatement) { this.missionStatement = missionStatement; }
        public String getWebsite() { return website; }
        public void setWebsite(String website) { this.website = website; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getProfileImageUrl() { return profileImageUrl; }
        public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
        public String getCategories() { return categories; }
        public void setCategories(String categories) { this.categories = categories; }
        public String getPrimaryCategory() { return primaryCategory; }
        public void setPrimaryCategory(String primaryCategory) { this.primaryCategory = primaryCategory; }
        public String getOrganizationType() { return organizationType; }
        public void setOrganizationType(String organizationType) { this.organizationType = organizationType; }
        public String getOrganizationSize() { return organizationSize; }
        public void setOrganizationSize(String organizationSize) { this.organizationSize = organizationSize; }
        public Integer getEmployeeCount() { return employeeCount; }
        public void setEmployeeCount(Integer employeeCount) { this.employeeCount = employeeCount; }
        public String getLanguagesSupported() { return languagesSupported; }
        public void setLanguagesSupported(String languagesSupported) { this.languagesSupported = languagesSupported; }
        public Integer getFoundedYear() { return foundedYear; }
        public void setFoundedYear(Integer foundedYear) { this.foundedYear = foundedYear; }
        public String getTaxExemptStatus() { return taxExemptStatus; }
        public void setTaxExemptStatus(String taxExemptStatus) { this.taxExemptStatus = taxExemptStatus; }
    }

    public static class UpdateOrganizationProfileRequest {
        private String organizationName;
        private String description;
        private String missionStatement;
        private String website;
        private String phoneNumber;
        private String address;
        private String city;
        private String state;
        private String zipCode;
        private String country;
        private String profileImageUrl;
        private String categories;
        private String primaryCategory;
        private String organizationType;
        private String organizationSize;
        private Integer employeeCount;
        private String languagesSupported;
        private Integer foundedYear;
        private String taxExemptStatus;
        
        // Getters and setters (same as CreateOrganizationProfileRequest)
        public String getOrganizationName() { return organizationName; }
        public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getMissionStatement() { return missionStatement; }
        public void setMissionStatement(String missionStatement) { this.missionStatement = missionStatement; }
        public String getWebsite() { return website; }
        public void setWebsite(String website) { this.website = website; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getProfileImageUrl() { return profileImageUrl; }
        public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
        public String getCategories() { return categories; }
        public void setCategories(String categories) { this.categories = categories; }
        public String getPrimaryCategory() { return primaryCategory; }
        public void setPrimaryCategory(String primaryCategory) { this.primaryCategory = primaryCategory; }
        public String getOrganizationType() { return organizationType; }
        public void setOrganizationType(String organizationType) { this.organizationType = organizationType; }
        public String getOrganizationSize() { return organizationSize; }
        public void setOrganizationSize(String organizationSize) { this.organizationSize = organizationSize; }
        public Integer getEmployeeCount() { return employeeCount; }
        public void setEmployeeCount(Integer employeeCount) { this.employeeCount = employeeCount; }
        public String getLanguagesSupported() { return languagesSupported; }
        public void setLanguagesSupported(String languagesSupported) { this.languagesSupported = languagesSupported; }
        public Integer getFoundedYear() { return foundedYear; }
        public void setFoundedYear(Integer foundedYear) { this.foundedYear = foundedYear; }
        public String getTaxExemptStatus() { return taxExemptStatus; }
        public void setTaxExemptStatus(String taxExemptStatus) { this.taxExemptStatus = taxExemptStatus; }
    }

    public static class OrganizationSearchRequest {
        private String searchTerm;
        private String category;
        private String country;
        private String organizationSize;
        private Boolean isVerified;
        
        // Getters and setters
        public String getSearchTerm() { return searchTerm; }
        public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getOrganizationSize() { return organizationSize; }
        public void setOrganizationSize(String organizationSize) { this.organizationSize = organizationSize; }
        public Boolean getIsVerified() { return isVerified; }
        public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    }

    public static class OrganizationFilterRequest {
        private String category;
        private String country;
        private String organizationSize;
        private Integer updatedWithinDays;
        private String verificationType; // "verified", "unverified", "highly_verified"
        private String organizationType;
        private String sortBy; // "name", "events", "volunteers", "updated"
        
        // Getters and setters
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getOrganizationSize() { return organizationSize; }
        public void setOrganizationSize(String organizationSize) { this.organizationSize = organizationSize; }
        public Integer getUpdatedWithinDays() { return updatedWithinDays; }
        public void setUpdatedWithinDays(Integer updatedWithinDays) { this.updatedWithinDays = updatedWithinDays; }
        public String getVerificationType() { return verificationType; }
        public void setVerificationType(String verificationType) { this.verificationType = verificationType; }
        public String getOrganizationType() { return organizationType; }
        public void setOrganizationType(String organizationType) { this.organizationType = organizationType; }
        public String getSortBy() { return sortBy; }
        public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    }

    public static class OrganizationStatsResponse {
        private Long totalOrganizations;
        private Long verifiedOrganizations;
        private Long nonProfitOrganizations;
        private Long smallOrganizations;
        private Long mediumOrganizations;
        private Long largeOrganizations;
        private Long enterpriseOrganizations;
        private Long internationalOrganizations;
        private java.util.Map<String, Integer> categoryDistribution;
        private java.util.Map<String, Integer> typeDistribution;
        private java.util.Map<String, Integer> geographicDistribution;
        private VerificationStats verificationStats;
        
        // Getters and setters
        public Long getTotalOrganizations() { return totalOrganizations; }
        public void setTotalOrganizations(Long totalOrganizations) { this.totalOrganizations = totalOrganizations; }
        public Long getVerifiedOrganizations() { return verifiedOrganizations; }
        public void setVerifiedOrganizations(Long verifiedOrganizations) { this.verifiedOrganizations = verifiedOrganizations; }
        public Long getNonProfitOrganizations() { return nonProfitOrganizations; }
        public void setNonProfitOrganizations(Long nonProfitOrganizations) { this.nonProfitOrganizations = nonProfitOrganizations; }
        public Long getSmallOrganizations() { return smallOrganizations; }
        public void setSmallOrganizations(Long smallOrganizations) { this.smallOrganizations = smallOrganizations; }
        public Long getMediumOrganizations() { return mediumOrganizations; }
        public void setMediumOrganizations(Long mediumOrganizations) { this.mediumOrganizations = mediumOrganizations; }
        public Long getLargeOrganizations() { return largeOrganizations; }
        public void setLargeOrganizations(Long largeOrganizations) { this.largeOrganizations = largeOrganizations; }
        public Long getEnterpriseOrganizations() { return enterpriseOrganizations; }
        public void setEnterpriseOrganizations(Long enterpriseOrganizations) { this.enterpriseOrganizations = enterpriseOrganizations; }
        public Long getInternationalOrganizations() { return internationalOrganizations; }
        public void setInternationalOrganizations(Long internationalOrganizations) { this.internationalOrganizations = internationalOrganizations; }
        public java.util.Map<String, Integer> getCategoryDistribution() { return categoryDistribution; }
        public void setCategoryDistribution(java.util.Map<String, Integer> categoryDistribution) { this.categoryDistribution = categoryDistribution; }
        public java.util.Map<String, Integer> getTypeDistribution() { return typeDistribution; }
        public void setTypeDistribution(java.util.Map<String, Integer> typeDistribution) { this.typeDistribution = typeDistribution; }
        public java.util.Map<String, Integer> getGeographicDistribution() { return geographicDistribution; }
        public void setGeographicDistribution(java.util.Map<String, Integer> geographicDistribution) { this.geographicDistribution = geographicDistribution; }
        public VerificationStats getVerificationStats() { return verificationStats; }
        public void setVerificationStats(VerificationStats verificationStats) { this.verificationStats = verificationStats; }
    }

    public static class VerificationStats {
        private Long total;
        private Long verified;
        private Long premium;
        private Long standardVerified;
        private Long basic;
        private Long unverified;
        
        // Getters and setters
        public Long getTotal() { return total; }
        public void setTotal(Long total) { this.total = total; }
        public Long getVerified() { return verified; }
        public void setVerified(Long verified) { this.verified = verified; }
        public Long getPremium() { return premium; }
        public void setPremium(Long premium) { this.premium = premium; }
        public Long getStandardVerified() { return standardVerified; }
        public void setStandardVerified(Long standardVerified) { this.standardVerified = standardVerified; }
        public Long getBasic() { return basic; }
        public void setBasic(Long basic) { this.basic = basic; }
        public Long getUnverified() { return unverified; }
        public void setUnverified(Long unverified) { this.unverified = unverified; }
    }

    public static class ProfileCompletionStats {
        private Long totalProfiles;
        private Long completeProfiles;
        private Double completionRate;
        
        // Getters and setters
        public Long getTotalProfiles() { return totalProfiles; }
        public void setTotalProfiles(Long totalProfiles) { this.totalProfiles = totalProfiles; }
        public Long getCompleteProfiles() { return completeProfiles; }
        public void setCompleteProfiles(Long completeProfiles) { this.completeProfiles = completeProfiles; }
        public Double getCompletionRate() { return completionRate; }
        public void setCompletionRate(Double completionRate) { this.completionRate = completionRate; }
    }

    public static class IndividualOrganizationStats {
        private Integer totalEventsHosted;
        private Integer totalVolunteersServed;
        private Integer profileCompleteness;
        private LocalDateTime memberSince;
        private Boolean isVerified;
        private String verificationLevel;
        private Integer activityRanking;
        
        // Getters and setters
        public Integer getTotalEventsHosted() { return totalEventsHosted; }
        public void setTotalEventsHosted(Integer totalEventsHosted) { this.totalEventsHosted = totalEventsHosted; }
        public Integer getTotalVolunteersServed() { return totalVolunteersServed; }
        public void setTotalVolunteersServed(Integer totalVolunteersServed) { this.totalVolunteersServed = totalVolunteersServed; }
        public Integer getProfileCompleteness() { return profileCompleteness; }
        public void setProfileCompleteness(Integer profileCompleteness) { this.profileCompleteness = profileCompleteness; }
        public LocalDateTime getMemberSince() { return memberSince; }
        public void setMemberSince(LocalDateTime memberSince) { this.memberSince = memberSince; }
        public Boolean getIsVerified() { return isVerified; }
        public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
        public String getVerificationLevel() { return verificationLevel; }
        public void setVerificationLevel(String verificationLevel) { this.verificationLevel = verificationLevel; }
        public Integer getActivityRanking() { return activityRanking; }
        public void setActivityRanking(Integer activityRanking) { this.activityRanking = activityRanking; }
    }
}