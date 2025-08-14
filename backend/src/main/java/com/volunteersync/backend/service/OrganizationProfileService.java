// backend/src/main/java/com/volunteersync/backend/service/OrganizationProfileService.java
package com.volunteersync.backend.service;

import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.entity.OrganizationProfile;
import com.volunteersync.backend.entity.Event;
import com.volunteersync.backend.entity.Application;
import com.volunteersync.backend.enums.UserType;
import com.volunteersync.backend.repository.UserRepository;
import com.volunteersync.backend.repository.OrganizationProfileRepository;
import com.volunteersync.backend.repository.EventRepository;
import com.volunteersync.backend.repository.ApplicationRepository;
import com.volunteersync.backend.dto.OrganizationProfileDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Organization Profile service - handles organization profile management and
 * operations
 * Manages organization profiles, search, verification, statistics, and enhanced
 * filtering
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

    public OrganizationProfileDTO createOrUpdateProfile(CreateOrganizationProfileRequest request, Long userId) {
        System.out.println("Creating or updating organization profile for user ID: " + userId);
        System.out.println("Request categories: '" + request.getCategories() + "'");
        System.out.println("Request services: '" + request.getServices() + "'");
        System.out.println("Request bio: '" + request.getBio() + "'");
        System.out.println("Request location: '" + request.getLocation() + "'");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getUserType() != UserType.ORGANIZATION) {
            throw new RuntimeException("User must be of type ORGANIZATION to create organization profile");
        }

        // Check if profile already exists
        Optional<OrganizationProfile> existingProfile = organizationProfileRepository.findByUserId(userId);

        OrganizationProfile profile;
        boolean isUpdate = false;

        if (existingProfile.isPresent()) {
            // Update existing profile
            profile = existingProfile.get();
            isUpdate = true;
            System.out.println("Updating existing organization profile with ID: " + profile.getId());

            // Update all fields - use request values or keep existing if null
            if (isValidString(request.getOrganizationName())) {
                profile.setOrganizationName(request.getOrganizationName());
            }

            // ✅ CRITICAL: Handle bio -> description mapping
            if (isValidString(request.getBio())) {
                profile.setDescription(request.getBio());
            } else if (isValidString(request.getDescription())) {
                profile.setDescription(request.getDescription());
            }

            if (isValidString(request.getMissionStatement())) {
                profile.setMissionStatement(request.getMissionStatement());
            }
            if (isValidString(request.getWebsite())) {
                profile.setWebsite(request.getWebsite());
            }
            if (isValidString(request.getPhoneNumber())) {
                profile.setPhoneNumber(request.getPhoneNumber());
            }

            // ✅ CRITICAL: Handle location -> address mapping
            if (isValidString(request.getLocation())) {
                profile.setAddress(request.getLocation());
            } else if (isValidString(request.getAddress())) {
                profile.setAddress(request.getAddress());
            }

            if (isValidString(request.getCity())) {
                profile.setCity(request.getCity());
            }
            if (isValidString(request.getState())) {
                profile.setState(request.getState());
            }
            if (isValidString(request.getZipCode())) {
                profile.setZipCode(request.getZipCode());
            }
            if (isValidString(request.getCountry())) {
                profile.setCountry(request.getCountry());
            }
            if (isValidString(request.getProfileImageUrl())) {
                profile.setProfileImageUrl(request.getProfileImageUrl());
            }

            // ✅ CRITICAL: Handle categories and services
            if (isValidString(request.getCategories())) {
                System.out.println("Setting categories: '" + request.getCategories() + "'");
                profile.setCategories(request.getCategories());
            }
            if (isValidString(request.getServices())) {
                System.out.println("Setting services: '" + request.getServices() + "'");
                profile.setServices(request.getServices());
            }

            if (isValidString(request.getPrimaryCategory())) {
                profile.setPrimaryCategory(request.getPrimaryCategory());
            }
            if (isValidString(request.getOrganizationType())) {
                profile.setOrganizationType(request.getOrganizationType());
            }
            if (isValidString(request.getOrganizationSize())) {
                profile.setOrganizationSize(request.getOrganizationSize());
            }
            if (request.getEmployeeCount() != null) {
                profile.setEmployeeCount(request.getEmployeeCount());
            }
            if (isValidString(request.getLanguagesSupported())) {
                profile.setLanguagesSupported(request.getLanguagesSupported());
            }
            if (request.getFoundedYear() != null) {
                profile.setFoundedYear(request.getFoundedYear());
            }
            if (isValidString(request.getTaxExemptStatus())) {
                profile.setTaxExemptStatus(request.getTaxExemptStatus());
            }

            profile.setUpdatedAt(LocalDateTime.now());

        } else {
            // Create new profile
            System.out.println("Creating new organization profile");
            profile = new OrganizationProfile();
            profile.setUser(user);
            profile.setOrganizationName(
                    isValidString(request.getOrganizationName()) ? request.getOrganizationName() : "");

            // ✅ CRITICAL: Map bio to description for new profiles
            String description = isValidString(request.getBio()) ? request.getBio()
                    : (isValidString(request.getDescription()) ? request.getDescription() : "");
            profile.setDescription(description);

            profile.setMissionStatement(
                    isValidString(request.getMissionStatement()) ? request.getMissionStatement() : "");
            profile.setWebsite(isValidString(request.getWebsite()) ? request.getWebsite() : "");
            profile.setPhoneNumber(isValidString(request.getPhoneNumber()) ? request.getPhoneNumber() : "");

            // ✅ CRITICAL: Map location to address for new profiles
            String address = isValidString(request.getLocation()) ? request.getLocation()
                    : (isValidString(request.getAddress()) ? request.getAddress() : "");
            profile.setAddress(address);

            profile.setCity(isValidString(request.getCity()) ? request.getCity() : "");
            profile.setState(isValidString(request.getState()) ? request.getState() : "");
            profile.setZipCode(isValidString(request.getZipCode()) ? request.getZipCode() : "");
            profile.setCountry(isValidString(request.getCountry()) ? request.getCountry() : "United States");
            profile.setProfileImageUrl(request.getProfileImageUrl());

            // ✅ CRITICAL: Set categories and services for new profiles
            System.out.println("Setting new profile categories: '" + request.getCategories() + "'");
            System.out.println("Setting new profile services: '" + request.getServices() + "'");
            profile.setCategories(isValidString(request.getCategories()) ? request.getCategories() : "");
            profile.setServices(isValidString(request.getServices()) ? request.getServices() : "");

            profile.setPrimaryCategory(isValidString(request.getPrimaryCategory()) ? request.getPrimaryCategory() : "");
            profile.setOrganizationType(
                    isValidString(request.getOrganizationType()) ? request.getOrganizationType() : "");
            profile.setOrganizationSize(
                    isValidString(request.getOrganizationSize()) ? request.getOrganizationSize() : "");
            profile.setEmployeeCount(request.getEmployeeCount());
            profile.setLanguagesSupported(
                    isValidString(request.getLanguagesSupported()) ? request.getLanguagesSupported() : "");
            profile.setFoundedYear(request.getFoundedYear());
            profile.setTaxExemptStatus(isValidString(request.getTaxExemptStatus()) ? request.getTaxExemptStatus() : "");
            profile.setVerificationLevel("Unverified");
            profile.setCreatedAt(LocalDateTime.now());
            profile.setUpdatedAt(LocalDateTime.now());
        }

        OrganizationProfile savedProfile = organizationProfileRepository.save(profile);

        System.out.println("Saved organization profile categories: '" + savedProfile.getCategories() + "'");
        System.out.println("Saved organization profile services: '" + savedProfile.getServices() + "'");
        System.out.println("Saved organization profile description: '" + savedProfile.getDescription() + "'");
        System.out.println("Saved organization profile address: '" + savedProfile.getAddress() + "'");

        if (isUpdate) {
            System.out.println("Organization profile updated successfully with ID: " + savedProfile.getId());
        } else {
            System.out.println("Organization profile created successfully with ID: " + savedProfile.getId());
        }

        return convertToDTO(savedProfile);
    }

    // Helper method to check if a string is valid
    private boolean isValidString(String value) {
        return value != null && !value.trim().isEmpty();
    }

    // Keep the original createProfile method for backward compatibility
    public OrganizationProfileDTO createProfile(CreateOrganizationProfileRequest request, Long userId) {
        System.out.println("Legacy createProfile called - redirecting to createOrUpdateProfile");
        return createOrUpdateProfile(request, userId);
    }

    /**
     * Get organization profile by user ID
     */
    public OrganizationProfileDTO getProfileByUserId(Long userId) {
        OrganizationProfile profile = organizationProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Organization profile not found"));
        return convertToDTO(profile);
    }

    /**
     * Get organization profile by ID
     */
    public OrganizationProfileDTO getProfileById(Long profileId) {
        OrganizationProfile profile = organizationProfileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Organization profile not found"));
        return convertToDTO(profile);
    }

    /**
     * Update organization profile
     */
    public OrganizationProfileDTO updateProfile(Long userId, UpdateOrganizationProfileRequest request) {
        OrganizationProfile profile = organizationProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Organization profile not found"));

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
        return convertToDTO(savedProfile);
    }

    // ==========================================
    // SEARCH AND DISCOVERY METHODS
    // ==========================================

    /**
     * Search organizations by name
     */
    public List<OrganizationProfileDTO> searchOrganizationsByName(String searchTerm) {
        List<OrganizationProfile> profiles = organizationProfileRepository
                .findByOrganizationNameContainingIgnoreCase(searchTerm);
        return profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Search organizations by category
     */
    public List<OrganizationProfileDTO> searchOrganizationsByCategory(String category) {
        List<OrganizationProfile> profiles = organizationProfileRepository
                .findByCategoryContaining(category);
        return profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Search organizations by location
     */
    public List<OrganizationProfileDTO> searchOrganizationsByLocation(String location) {
        List<OrganizationProfile> profiles = organizationProfileRepository
                .findByLocationContaining(location);
        return profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Search organizations by keyword
     */
    public List<OrganizationProfileDTO> searchOrganizationsByKeyword(String keyword) {
        List<OrganizationProfile> profiles = organizationProfileRepository
                .findByKeyword(keyword);
        return profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Advanced search with filters
     */
    public Page<OrganizationProfileDTO> advancedSearch(OrganizationSearchRequest request, Pageable pageable) {
        Page<OrganizationProfile> profiles = organizationProfileRepository.searchWithFilters(
                request.getSearchTerm(),
                request.getCategory(),
                request.getCountry(),
                request.getOrganizationSize(),
                request.getIsVerified(),
                pageable);

        List<OrganizationProfileDTO> dtos = profiles.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, profiles.getTotalElements());
    }

    /**
     * Filter organizations with comprehensive criteria
     */
    public Page<OrganizationProfileDTO> filterOrganizations(OrganizationFilterRequest request, Pageable pageable) {
        LocalDateTime updatedSince = null;
        if (request.getUpdatedWithinDays() != null) {
            updatedSince = LocalDateTime.now().minusDays(request.getUpdatedWithinDays());
        }

        Page<OrganizationProfile> profiles = organizationProfileRepository.findWithAdvancedFilters(
                request.getCategory(),
                request.getCountry(),
                request.getOrganizationSize(),
                updatedSince,
                request.getVerificationType(),
                request.getOrganizationType(),
                request.getSortBy(),
                pageable);

        List<OrganizationProfileDTO> dtos = profiles.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, profiles.getTotalElements());
    }

    // ==========================================
    // CATEGORY AND TYPE FILTERING METHODS
    // ==========================================

    /**
     * Get organizations by category
     */
    public List<OrganizationProfileDTO> getOrganizationsByCategory(String category) {
        List<OrganizationProfile> profiles = organizationProfileRepository
                .findByPrimaryCategoryIgnoreCase(category);
        return profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Get organizations by type
     */
    public List<OrganizationProfileDTO> getOrganizationsByType(String type) {
        List<OrganizationProfile> profiles = organizationProfileRepository
                .findByOrganizationTypeIgnoreCase(type);
        return profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Get non-profit organizations
     */
    public List<OrganizationProfileDTO> getNonProfitOrganizations() {
        List<OrganizationProfile> profiles = organizationProfileRepository.findNonProfitOrganizations();
        return profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Get organizations by size
     */
    public List<OrganizationProfileDTO> getOrganizationsBySize(String size) {
        List<OrganizationProfile> profiles = organizationProfileRepository
                .findByOrganizationSizeIgnoreCase(size);
        return profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // ==========================================
    // VERIFICATION AND TRUST METHODS
    // ==========================================

    /**
     * Get verified organizations
     */
    public List<OrganizationProfileDTO> getVerifiedOrganizations() {
        List<OrganizationProfile> profiles = organizationProfileRepository.findByIsVerifiedTrue();
        return profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Get highly verified organizations
     */
    public List<OrganizationProfileDTO> getHighlyVerifiedOrganizations() {
        List<OrganizationProfile> profiles = organizationProfileRepository.findHighlyVerifiedOrganizations();
        return profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Update verification status (Admin only)
     */
    public OrganizationProfileDTO updateVerificationStatus(Long profileId, String verificationLevel,
            Boolean isVerified, String adminUserId) {
        OrganizationProfile profile = organizationProfileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Organization profile not found"));

        profile.setIsVerified(isVerified);
        profile.setVerificationLevel(verificationLevel);

        OrganizationProfile savedProfile = organizationProfileRepository.save(profile);
        return convertToDTO(savedProfile);
    }

    // ==========================================
    // LOCATION-BASED METHODS
    // ==========================================

    /**
     * Get organizations by country
     */
    public List<OrganizationProfileDTO> getOrganizationsByCountry(String country) {
        List<OrganizationProfile> profiles = organizationProfileRepository
                .findByCountryIgnoreCase(country);
        return profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Get international organizations
     */
    public List<OrganizationProfileDTO> getInternationalOrganizations() {
        List<OrganizationProfile> profiles = organizationProfileRepository.findInternationalOrganizations();
        return profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Get organizations by language support
     */
    public List<OrganizationProfileDTO> getOrganizationsByLanguage(String language) {
        List<OrganizationProfile> profiles = organizationProfileRepository
                .findByLanguageSupport(language);
        return profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // ==========================================
    // ACTIVITY AND PERFORMANCE METHODS
    // ==========================================

    /**
     * Get most active organizations
     */
    public List<OrganizationProfileDTO> getMostActiveOrganizations(int limit) {
        List<OrganizationProfile> profiles = organizationProfileRepository.findMostActiveOrganizations()
                .stream().limit(limit).collect(Collectors.toList());
        return profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Get organizations by volunteer impact
     */
    public List<OrganizationProfileDTO> getOrganizationsByVolunteerImpact(int limit) {
        List<OrganizationProfile> profiles = organizationProfileRepository.findByVolunteerImpact()
                .stream().limit(limit).collect(Collectors.toList());
        return profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Get organizations by activity level
     */
    public List<OrganizationProfileDTO> getOrganizationsByActivityLevel(String level) {
        List<OrganizationProfile> profiles = organizationProfileRepository
                .findByActivityLevel(level);
        return profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Get recently updated organizations
     */
    public List<OrganizationProfileDTO> getRecentlyUpdatedOrganizations(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<OrganizationProfile> profiles = organizationProfileRepository.findUpdatedSince(since);
        return profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Get recently joined organizations
     */
    public List<OrganizationProfileDTO> getRecentlyJoinedOrganizations(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<OrganizationProfile> profiles = organizationProfileRepository.findRecentlyJoined(since);
        return profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // ==========================================
    // STATISTICS AND ANALYTICS METHODS
    // ==========================================

    /**
     * Get comprehensive organization statistics
     */
    public OrganizationStatsResponse getOrganizationStatistics() {
        Object[] comprehensiveStats = organizationProfileRepository.getComprehensiveStatistics();
        Object[] verificationStats = organizationProfileRepository.getVerificationStatistics();

        OrganizationStatsResponse response = new OrganizationStatsResponse();

        if (comprehensiveStats.length > 0) {
            Object[] stats = comprehensiveStats;
            response.setTotalOrganizations(getLongValue(stats[0]));
            response.setVerifiedOrganizations(getLongValue(stats[1]));
            response.setNonProfitOrganizations(getLongValue(stats[2]));
            response.setSmallOrganizations(getLongValue(stats[3]));
            response.setMediumOrganizations(getLongValue(stats[4]));
            response.setLargeOrganizations(getLongValue(stats[5]));
            response.setEnterpriseOrganizations(getLongValue(stats[6]));
            response.setInternationalOrganizations(getLongValue(stats[7]));
        }

        // Get category distribution
        List<Object[]> categoryStats = organizationProfileRepository.getCategoryStatistics();
        Map<String, Integer> categoryDistribution = new HashMap<>();
        for (Object[] stat : categoryStats) {
            categoryDistribution.put((String) stat[0], ((Long) stat[1]).intValue());
        }
        response.setCategoryDistribution(categoryDistribution);

        // Get type distribution
        List<Object[]> typeStats = organizationProfileRepository.getOrganizationTypeStatistics();
        Map<String, Integer> typeDistribution = new HashMap<>();
        for (Object[] stat : typeStats) {
            typeDistribution.put((String) stat[0], ((Long) stat[1]).intValue());
        }
        response.setTypeDistribution(typeDistribution);

        // Get geographic distribution
        List<Object[]> geoStats = organizationProfileRepository.getGeographicDistribution();
        Map<String, Integer> geographicDistribution = new HashMap<>();
        for (Object[] stat : geoStats) {
            String location = stat[0] + ", " + stat[1];
            geographicDistribution.put(location, ((Long) stat[2]).intValue());
        }
        response.setGeographicDistribution(geographicDistribution);

        // Set verification stats
        VerificationStats verificationStatsObj = new VerificationStats();
        if (verificationStats.length > 0) {
            Object[] vStats = verificationStats;
            verificationStatsObj.setTotal(getLongValue(vStats[0]));
            verificationStatsObj.setVerified(getLongValue(vStats[1]));
            verificationStatsObj.setPremium(getLongValue(vStats[2]));
            verificationStatsObj.setStandardVerified(getLongValue(vStats[3]));
            verificationStatsObj.setBasic(getLongValue(vStats[4]));
            verificationStatsObj.setUnverified(getLongValue(vStats[5]));
        }
        response.setVerificationStats(verificationStatsObj);

        return response;
    }

    /**
     * Get profile completion statistics
     */
    public ProfileCompletionStats getProfileCompletionStats() {
        long totalProfiles = organizationProfileRepository.count();

        ProfileCompletionStats stats = new ProfileCompletionStats();
        stats.setTotalProfiles(totalProfiles);
        stats.setCompleteProfiles(0L); // Calculate based on your completion criteria
        stats.setIncompleteProfiles(totalProfiles);
        stats.setCompletionRate(0.0); // Calculate based on complete/total

        return stats;
    }

    /**
     * Get individual organization statistics
     */
    public IndividualOrganizationStats getIndividualOrganizationStats(Long userId) {
        OrganizationProfile profile = organizationProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Organization profile not found"));

        IndividualOrganizationStats stats = new IndividualOrganizationStats();
        stats.setOrganizationId(profile.getId());
        stats.setOrganizationName(profile.getOrganizationName());
        stats.setTotalEventsHosted(profile.getTotalEventsHosted());
        stats.setTotalVolunteersServed(profile.getTotalVolunteersServed());
        stats.setProfileViews(0L); // Implement profile view tracking
        stats.setApplicationsReceived(0L); // Calculate from applications
        stats.setVerificationLevel(profile.getVerificationLevel());
        stats.setIsVerified(profile.getIsVerified());

        return stats;
    }

    // ==========================================
    // ADMINISTRATIVE METHODS
    // ==========================================

    /**
     * Get organizations needing review (Admin only)
     */
    public List<OrganizationProfileDTO> getOrganizationsNeedingReview() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        List<OrganizationProfile> profiles = organizationProfileRepository
                .findOrganizationsNeedingReview(cutoffDate);
        return profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Get organizations needing verification (Admin only)
     */
    public List<OrganizationProfileDTO> getOrganizationsNeedingVerification() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);
        List<OrganizationProfile> profiles = organizationProfileRepository
                .findOrganizationsNeedingVerification(cutoffDate);
        return profiles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Find potential duplicate organizations (Admin only)
     */
    public List<List<OrganizationProfileDTO>> findPotentialDuplicates() {
        // This is a simplified implementation
        // In a real scenario, you'd implement more sophisticated duplicate detection
        List<List<OrganizationProfileDTO>> duplicateGroups = new ArrayList<>();

        // For now, return empty list - implement based on your duplicate detection
        // logic
        return duplicateGroups;
    }

    // ==========================================
    // UTILITY AND CONVERSION METHODS
    // ==========================================

    /**
     * Convert OrganizationProfile entity to DTO
     */
    private OrganizationProfileDTO convertToDTO(OrganizationProfile profile) {
        OrganizationProfileDTO dto = new OrganizationProfileDTO();

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

        // New enhanced fields
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

        // Set additional DTO fields
        dto.setCoverImageUrl(profile.getCoverImageUrl());
        dto.setServices(profile.getServicesList());
        dto.setCauses(profile.getCausesList());
        dto.setFundingGoal(profile.getFundingGoal());
        dto.setFundingRaised(profile.getFundingRaised());
        dto.setEin(profile.getEin());
        dto.setFounded(profile.getFounded());

        // Set mock data for fields not in entity
        dto.setAchievements(getMockAchievements());
        dto.setPartnerships(getMockPartnerships());
        dto.setVolunteers(getMockVolunteers());
        dto.setRecentActivity(getMockRecentActivity());

        return dto;
    }

    /**
     * Helper method to safely convert Object to Long
     */
    private Long getLongValue(Object value) {
        if (value == null)
            return 0L;
        if (value instanceof Long)
            return (Long) value;
        if (value instanceof Integer)
            return ((Integer) value).longValue();
        if (value instanceof Number)
            return ((Number) value).longValue();
        return 0L;
    }

    /**
     * Mock achievements for DTO
     */
    private List<OrganizationProfileDTO.Achievement> getMockAchievements() {
        List<OrganizationProfileDTO.Achievement> achievements = new ArrayList<>();
        achievements.add(new OrganizationProfileDTO.Achievement(1L, "Verified Organization", "✅",
                "Background checked and verified"));
        achievements.add(
                new OrganizationProfileDTO.Achievement(2L, "Top Rated", "⭐", "4.9/5 volunteer satisfaction rating"));
        return achievements;
    }

    /**
     * Mock partnerships for DTO
     */
    private List<OrganizationProfileDTO.Partnership> getMockPartnerships() {
        List<OrganizationProfileDTO.Partnership> partnerships = new ArrayList<>();
        partnerships.add(new OrganizationProfileDTO.Partnership(1L, "City Parks Department", "Government Partner",
                "Jan 2022", "🏛️"));
        partnerships.add(new OrganizationProfileDTO.Partnership(2L, "Green Tech Solutions", "Corporate Sponsor",
                "Mar 2023", "💼"));
        return partnerships;
    }

    /**
     * Mock volunteers for DTO
     */
    private List<OrganizationProfileDTO.VolunteerSummary> getMockVolunteers() {
        List<OrganizationProfileDTO.VolunteerSummary> volunteers = new ArrayList<>();
        volunteers.add(new OrganizationProfileDTO.VolunteerSummary(1L, "Sarah Chen", "Team Leader", 156, "SC"));
        volunteers.add(
                new OrganizationProfileDTO.VolunteerSummary(2L, "Marcus Rodriguez", "Event Coordinator", 142, "MR"));
        return volunteers;
    }

    /**
     * Mock recent activity for DTO
     */
    private List<OrganizationProfileDTO.ActivityEntry> getMockRecentActivity() {
        List<OrganizationProfileDTO.ActivityEntry> activity = new ArrayList<>();
        activity.add(
                new OrganizationProfileDTO.ActivityEntry(1L, "event", "Hosted River Cleanup Event", "3 days ago", 45));
        activity.add(new OrganizationProfileDTO.ActivityEntry(2L, "volunteer", "Welcome new volunteer: Alex Johnson",
                "1 week ago", null));
        return activity;
    }

    // ==========================================
    // REQUEST/RESPONSE CLASSES
    // ==========================================

    public static class CreateOrganizationProfileRequest {
        // Basic organization info
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

        // ✅ CRITICAL: These fields were missing - causing data to be ignored
        private String categories; // "Education,Environment,Community Service"
        private String services; // "Tutoring,Cleanup Events,Food Distribution"
        private String bio; // For bio field from frontend
        private String location; // For location field from frontend

        // Enhanced organization fields
        private String primaryCategory;
        private String organizationType;
        private String organizationSize;
        private Integer employeeCount;
        private String languagesSupported;
        private Integer foundedYear;
        private String taxExemptStatus;

        // Getters and setters
        public String getOrganizationName() {
            return organizationName;
        }

        public void setOrganizationName(String organizationName) {
            this.organizationName = organizationName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getMissionStatement() {
            return missionStatement;
        }

        public void setMissionStatement(String missionStatement) {
            this.missionStatement = missionStatement;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getProfileImageUrl() {
            return profileImageUrl;
        }

        public void setProfileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
        }

        // ✅ NEW: Categories getter/setter
        public String getCategories() {
            return categories;
        }

        public void setCategories(String categories) {
            this.categories = categories;
        }

        // ✅ NEW: Services getter/setter
        public String getServices() {
            return services;
        }

        public void setServices(String services) {
            this.services = services;
        }

        // ✅ NEW: Bio getter/setter (frontend sends bio, backend maps to description)
        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        // ✅ NEW: Location getter/setter (frontend sends location, backend maps to
        // address)
        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getPrimaryCategory() {
            return primaryCategory;
        }

        public void setPrimaryCategory(String primaryCategory) {
            this.primaryCategory = primaryCategory;
        }

        public String getOrganizationType() {
            return organizationType;
        }

        public void setOrganizationType(String organizationType) {
            this.organizationType = organizationType;
        }

        public String getOrganizationSize() {
            return organizationSize;
        }

        public void setOrganizationSize(String organizationSize) {
            this.organizationSize = organizationSize;
        }

        public Integer getEmployeeCount() {
            return employeeCount;
        }

        public void setEmployeeCount(Integer employeeCount) {
            this.employeeCount = employeeCount;
        }

        public String getLanguagesSupported() {
            return languagesSupported;
        }

        public void setLanguagesSupported(String languagesSupported) {
            this.languagesSupported = languagesSupported;
        }

        public Integer getFoundedYear() {
            return foundedYear;
        }

        public void setFoundedYear(Integer foundedYear) {
            this.foundedYear = foundedYear;
        }

        public String getTaxExemptStatus() {
            return taxExemptStatus;
        }

        public void setTaxExemptStatus(String taxExemptStatus) {
            this.taxExemptStatus = taxExemptStatus;
        }
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

        // ✅ CRITICAL: Added missing fields
        private String categories;
        private String services;
        private String bio;
        private String location;

        private String primaryCategory;
        private String organizationType;
        private String organizationSize;
        private Integer employeeCount;
        private String languagesSupported;
        private Integer foundedYear;
        private String taxExemptStatus;

        // All getters and setters (same as CreateOrganizationProfileRequest)
        // [Include all the same getters/setters as above]
        public String getOrganizationName() {
            return organizationName;
        }

        public void setOrganizationName(String organizationName) {
            this.organizationName = organizationName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getMissionStatement() {
            return missionStatement;
        }

        public void setMissionStatement(String missionStatement) {
            this.missionStatement = missionStatement;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getProfileImageUrl() {
            return profileImageUrl;
        }

        public void setProfileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
        }

        public String getCategories() {
            return categories;
        }

        public void setCategories(String categories) {
            this.categories = categories;
        }

        public String getServices() {
            return services;
        }

        public void setServices(String services) {
            this.services = services;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getPrimaryCategory() {
            return primaryCategory;
        }

        public void setPrimaryCategory(String primaryCategory) {
            this.primaryCategory = primaryCategory;
        }

        public String getOrganizationType() {
            return organizationType;
        }

        public void setOrganizationType(String organizationType) {
            this.organizationType = organizationType;
        }

        public String getOrganizationSize() {
            return organizationSize;
        }

        public void setOrganizationSize(String organizationSize) {
            this.organizationSize = organizationSize;
        }

        public Integer getEmployeeCount() {
            return employeeCount;
        }

        public void setEmployeeCount(Integer employeeCount) {
            this.employeeCount = employeeCount;
        }

        public String getLanguagesSupported() {
            return languagesSupported;
        }

        public void setLanguagesSupported(String languagesSupported) {
            this.languagesSupported = languagesSupported;
        }

        public Integer getFoundedYear() {
            return foundedYear;
        }

        public void setFoundedYear(Integer foundedYear) {
            this.foundedYear = foundedYear;
        }

        public String getTaxExemptStatus() {
            return taxExemptStatus;
        }

        public void setTaxExemptStatus(String taxExemptStatus) {
            this.taxExemptStatus = taxExemptStatus;
        }
    }

    public static class OrganizationSearchRequest {
        private String searchTerm;
        private String category;
        private String country;
        private String organizationSize;
        private Boolean isVerified;
        private String organizationType;
        private String sortBy;

        // Getters and setters
        public String getSearchTerm() {
            return searchTerm;
        }

        public void setSearchTerm(String searchTerm) {
            this.searchTerm = searchTerm;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getOrganizationSize() {
            return organizationSize;
        }

        public void setOrganizationSize(String organizationSize) {
            this.organizationSize = organizationSize;
        }

        public Boolean getIsVerified() {
            return isVerified;
        }

        public void setIsVerified(Boolean isVerified) {
            this.isVerified = isVerified;
        }

        public String getOrganizationType() {
            return organizationType;
        }

        public void setOrganizationType(String organizationType) {
            this.organizationType = organizationType;
        }

        public String getSortBy() {
            return sortBy;
        }

        public void setSortBy(String sortBy) {
            this.sortBy = sortBy;
        }
    }

    public static class OrganizationFilterRequest {
        private String category;
        private String country;
        private String organizationSize;
        private Integer updatedWithinDays;
        private String verificationType;
        private String organizationType;
        private String sortBy;

        // Getters and setters
        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getOrganizationSize() {
            return organizationSize;
        }

        public void setOrganizationSize(String organizationSize) {
            this.organizationSize = organizationSize;
        }

        public Integer getUpdatedWithinDays() {
            return updatedWithinDays;
        }

        public void setUpdatedWithinDays(Integer updatedWithinDays) {
            this.updatedWithinDays = updatedWithinDays;
        }

        public String getVerificationType() {
            return verificationType;
        }

        public void setVerificationType(String verificationType) {
            this.verificationType = verificationType;
        }

        public String getOrganizationType() {
            return organizationType;
        }

        public void setOrganizationType(String organizationType) {
            this.organizationType = organizationType;
        }

        public String getSortBy() {
            return sortBy;
        }

        public void setSortBy(String sortBy) {
            this.sortBy = sortBy;
        }
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
        private Map<String, Integer> categoryDistribution;
        private Map<String, Integer> typeDistribution;
        private Map<String, Integer> geographicDistribution;
        private VerificationStats verificationStats;

        // Getters and setters
        public Long getTotalOrganizations() {
            return totalOrganizations;
        }

        public void setTotalOrganizations(Long totalOrganizations) {
            this.totalOrganizations = totalOrganizations;
        }

        public Long getVerifiedOrganizations() {
            return verifiedOrganizations;
        }

        public void setVerifiedOrganizations(Long verifiedOrganizations) {
            this.verifiedOrganizations = verifiedOrganizations;
        }

        public Long getNonProfitOrganizations() {
            return nonProfitOrganizations;
        }

        public void setNonProfitOrganizations(Long nonProfitOrganizations) {
            this.nonProfitOrganizations = nonProfitOrganizations;
        }

        public Long getSmallOrganizations() {
            return smallOrganizations;
        }

        public void setSmallOrganizations(Long smallOrganizations) {
            this.smallOrganizations = smallOrganizations;
        }

        public Long getMediumOrganizations() {
            return mediumOrganizations;
        }

        public void setMediumOrganizations(Long mediumOrganizations) {
            this.mediumOrganizations = mediumOrganizations;
        }

        public Long getLargeOrganizations() {
            return largeOrganizations;
        }

        public void setLargeOrganizations(Long largeOrganizations) {
            this.largeOrganizations = largeOrganizations;
        }

        public Long getEnterpriseOrganizations() {
            return enterpriseOrganizations;
        }

        public void setEnterpriseOrganizations(Long enterpriseOrganizations) {
            this.enterpriseOrganizations = enterpriseOrganizations;
        }

        public Long getInternationalOrganizations() {
            return internationalOrganizations;
        }

        public void setInternationalOrganizations(Long internationalOrganizations) {
            this.internationalOrganizations = internationalOrganizations;
        }

        public Map<String, Integer> getCategoryDistribution() {
            return categoryDistribution;
        }

        public void setCategoryDistribution(Map<String, Integer> categoryDistribution) {
            this.categoryDistribution = categoryDistribution;
        }

        public Map<String, Integer> getTypeDistribution() {
            return typeDistribution;
        }

        public void setTypeDistribution(Map<String, Integer> typeDistribution) {
            this.typeDistribution = typeDistribution;
        }

        public Map<String, Integer> getGeographicDistribution() {
            return geographicDistribution;
        }

        public void setGeographicDistribution(Map<String, Integer> geographicDistribution) {
            this.geographicDistribution = geographicDistribution;
        }

        public VerificationStats getVerificationStats() {
            return verificationStats;
        }

        public void setVerificationStats(VerificationStats verificationStats) {
            this.verificationStats = verificationStats;
        }
    }

    public static class VerificationStats {
        private Long total;
        private Long verified;
        private Long premium;
        private Long standardVerified;
        private Long basic;
        private Long unverified;

        // Getters and setters
        public Long getTotal() {
            return total;
        }

        public void setTotal(Long total) {
            this.total = total;
        }

        public Long getVerified() {
            return verified;
        }

        public void setVerified(Long verified) {
            this.verified = verified;
        }

        public Long getPremium() {
            return premium;
        }

        public void setPremium(Long premium) {
            this.premium = premium;
        }

        public Long getStandardVerified() {
            return standardVerified;
        }

        public void setStandardVerified(Long standardVerified) {
            this.standardVerified = standardVerified;
        }

        public Long getBasic() {
            return basic;
        }

        public void setBasic(Long basic) {
            this.basic = basic;
        }

        public Long getUnverified() {
            return unverified;
        }

        public void setUnverified(Long unverified) {
            this.unverified = unverified;
        }
    }

    public static class ProfileCompletionStats {
        private Long totalProfiles;
        private Long completeProfiles;
        private Long incompleteProfiles;
        private Double completionRate;

        // Getters and setters
        public Long getTotalProfiles() {
            return totalProfiles;
        }

        public void setTotalProfiles(Long totalProfiles) {
            this.totalProfiles = totalProfiles;
        }

        public Long getCompleteProfiles() {
            return completeProfiles;
        }

        public void setCompleteProfiles(Long completeProfiles) {
            this.completeProfiles = completeProfiles;
        }

        public Long getIncompleteProfiles() {
            return incompleteProfiles;
        }

        public void setIncompleteProfiles(Long incompleteProfiles) {
            this.incompleteProfiles = incompleteProfiles;
        }

        public Double getCompletionRate() {
            return completionRate;
        }

        public void setCompletionRate(Double completionRate) {
            this.completionRate = completionRate;
        }
    }

    public static class IndividualOrganizationStats {
        private Long organizationId;
        private String organizationName;
        private Integer totalEventsHosted;
        private Integer totalVolunteersServed;
        private Long profileViews;
        private Long applicationsReceived;
        private String verificationLevel;
        private Boolean isVerified;

        // Getters and setters
        public Long getOrganizationId() {
            return organizationId;
        }

        public void setOrganizationId(Long organizationId) {
            this.organizationId = organizationId;
        }

        public String getOrganizationName() {
            return organizationName;
        }

        public void setOrganizationName(String organizationName) {
            this.organizationName = organizationName;
        }

        public Integer getTotalEventsHosted() {
            return totalEventsHosted;
        }

        public void setTotalEventsHosted(Integer totalEventsHosted) {
            this.totalEventsHosted = totalEventsHosted;
        }

        public Integer getTotalVolunteersServed() {
            return totalVolunteersServed;
        }

        public void setTotalVolunteersServed(Integer totalVolunteersServed) {
            this.totalVolunteersServed = totalVolunteersServed;
        }

        public Long getProfileViews() {
            return profileViews;
        }

        public void setProfileViews(Long profileViews) {
            this.profileViews = profileViews;
        }

        public Long getApplicationsReceived() {
            return applicationsReceived;
        }

        public void setApplicationsReceived(Long applicationsReceived) {
            this.applicationsReceived = applicationsReceived;
        }

        public String getVerificationLevel() {
            return verificationLevel;
        }

        public void setVerificationLevel(String verificationLevel) {
            this.verificationLevel = verificationLevel;
        }

        public Boolean getIsVerified() {
            return isVerified;
        }

        public void setIsVerified(Boolean isVerified) {
            this.isVerified = isVerified;
        }
    }
}