// backend/src/main/java/com/volunteersync/backend/service/VolunteerProfileService.java
package com.volunteersync.backend.service;

import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.entity.Application;
import com.volunteersync.backend.entity.VolunteerProfile;
import com.volunteersync.backend.enums.UserType;
import com.volunteersync.backend.repository.VolunteerProfileRepository;
import com.volunteersync.backend.repository.UserRepository;
import com.volunteersync.backend.repository.ApplicationRepository;
import com.volunteersync.backend.dto.VolunteerProfileDTO;
import com.volunteersync.backend.service.BadgeService;
import com.volunteersync.backend.dto.BadgeDTO;
import com.volunteersync.backend.dto.VolunteerProfileDTO.ActivityEntry;
import com.volunteersync.backend.dto.VolunteerProfileDTO.Connection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Volunteer Profile service - handles volunteer profile management and operations
 * Manages volunteer profiles, search, statistics, and profile updates
 */
@Service
@Transactional
public class VolunteerProfileService {

    @Autowired
    private VolunteerProfileRepository volunteerProfileRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private BadgeService badgeService;

    // ==========================================
    // PROFILE MANAGEMENT METHODS
    // ==========================================

    /**
     * Create new volunteer profile
     */
    public VolunteerProfileDTO createProfile(CreateVolunteerProfileRequest request, Long userId) {
        System.out.println("Creating volunteer profile for user ID: " + userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getUserType() != UserType.VOLUNTEER) {
            throw new RuntimeException("Only volunteer users can create volunteer profiles");
        }
        
        // Check if profile already exists
        if (volunteerProfileRepository.existsByUser(user)) {
            throw new RuntimeException("Volunteer profile already exists for this user");
        }
        
        VolunteerProfile profile = new VolunteerProfile();
        profile.setUser(user);
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setBio(request.getBio());
        profile.setLocation(request.getLocation());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setProfileImageUrl(request.getProfileImageUrl());
        profile.setIsAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true);
        
        VolunteerProfile savedProfile = volunteerProfileRepository.save(profile);
        
        System.out.println("Volunteer profile created successfully with ID: " + savedProfile.getId());
        return convertToDTO(savedProfile);
    }

    /**
     * Get volunteer profile by user ID
     */
    public VolunteerProfileDTO getProfileByUserId(Long userId) {
        System.out.println("Fetching volunteer profile for user ID: " + userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        VolunteerProfile profile = volunteerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));
        
        return convertToDTO(profile);
    }

    /**
     * Get volunteer profile by profile ID
     */
    public VolunteerProfileDTO getProfileById(Long profileId) {
        System.out.println("Fetching volunteer profile with ID: " + profileId);
        
        VolunteerProfile profile = volunteerProfileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));
        
        return convertToDTO(profile);
    }

    /**
     * Update volunteer profile
     */
    public VolunteerProfileDTO updateProfile(Long userId, UpdateVolunteerProfileRequest request) {
        System.out.println("Updating volunteer profile for user ID: " + userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        VolunteerProfile profile = volunteerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));
        
        // Update fields if provided
        if (request.getFirstName() != null) {
            profile.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            profile.setLastName(request.getLastName());
        }
        if (request.getBio() != null) {
            profile.setBio(request.getBio());
        }
        if (request.getLocation() != null) {
            profile.setLocation(request.getLocation());
        }
        if (request.getPhoneNumber() != null) {
            profile.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getProfileImageUrl() != null) {
            profile.setProfileImageUrl(request.getProfileImageUrl());
        }
        if (request.getIsAvailable() != null) {
            profile.setIsAvailable(request.getIsAvailable());
        }
        
        VolunteerProfile savedProfile = volunteerProfileRepository.save(profile);
        
        System.out.println("Volunteer profile updated successfully");
        return convertToDTO(savedProfile);
    }

    /**
     * Get volunteer profile with all frontend data (badges, activities, connections)
     */
    public VolunteerProfileDTO getCompleteVolunteerProfile(Long userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
            Optional<VolunteerProfile> profileOpt = volunteerProfileRepository.findByUser(user);
            if (profileOpt.isEmpty()) {
                throw new RuntimeException("Volunteer profile not found");
            }
            
            VolunteerProfile profile = profileOpt.get();
            VolunteerProfileDTO dto = convertToDTO(profile);
            
            // Add badges data
            List<BadgeDTO> badges = badgeService.getUserBadges(userId);
            dto.setBadges(badges);
            
            // Add recent activity
            List<ActivityEntry> recentActivity = getVolunteerHistory(userId);
            dto.setRecentActivity(recentActivity);
            
            // Add connections (mock data for now)
            List<Connection> connections = getVolunteerConnections(userId);
            dto.setConnections(connections);
            
            return dto;
            
        } catch (Exception e) {
            System.err.println("Error getting complete volunteer profile: " + e.getMessage());
            throw new RuntimeException("Failed to get volunteer profile: " + e.getMessage());
        }
    }

    /**
     * Get volunteer activity history
     */
    public List<ActivityEntry> getVolunteerHistory(Long userId) {
        try {
            List<ActivityEntry> activities = new ArrayList<>();
            
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
            Optional<VolunteerProfile> profileOpt = volunteerProfileRepository.findByUser(user);
            if (profileOpt.isEmpty()) {
                return activities; // Return empty list if no profile
            }
            
            VolunteerProfile profile = profileOpt.get();
            
            // Get recent applications
            List<Application> recentApplications = applicationRepository
                .findByVolunteerOrderByAppliedAtDesc(profile)
                .stream()
                .limit(5)
                .collect(Collectors.toList());
                
            for (Application app : recentApplications) {
                activities.add(new ActivityEntry(
                    "application",
                    "Applied to " + app.getEvent().getTitle(),
                    "Application status: " + app.getStatus().toString(),
                    app.getAppliedAt(),
                    app.getStatus().toString(),
                    "📝"
                ));
            }
            
            // Get recent badges
            List<BadgeDTO> recentBadges = badgeService.getUserBadges(userId)
                .stream()
                .filter(badge -> badge.getEarnedAt() != null)
                .sorted((a, b) -> b.getEarnedAt().compareTo(a.getEarnedAt()))
                .limit(3)
                .collect(Collectors.toList());
                
            for (BadgeDTO badge : recentBadges) {
                activities.add(new ActivityEntry(
                    "badge",
                    "Earned " + badge.getBadgeName(),
                    badge.getBadgeDescription(),
                    badge.getEarnedAt(),
                    "EARNED",
                    badge.getBadgeIcon()
                ));
            }
            
            // Sort all activities by timestamp
            activities.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
            
            return activities.stream().limit(10).collect(Collectors.toList());
            
        } catch (Exception e) {
            System.err.println("Error getting volunteer history: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get volunteer connections (mock data for now)
     */
    public List<Connection> getVolunteerConnections(Long userId) {
        // For now, return mock data - you can implement real connections later
        List<Connection> connections = new ArrayList<>();
        
        connections.add(new Connection(
            "Sarah Chen",
            "Event Organizer", 
            "Local Food Bank",
            "/api/images/profiles/sarah.jpg",
            LocalDateTime.now().minusDays(10)
        ));
        
        connections.add(new Connection(
            "Mike Rodriguez",
            "Volunteer Coordinator",
            "Community Center",
            "/api/images/profiles/mike.jpg", 
            LocalDateTime.now().minusDays(15)
        ));
        
        return connections;
    }

    /**
     * Update volunteer skills
     */
    public VolunteerProfileDTO updateVolunteerSkills(Long userId, List<String> skills) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
            VolunteerProfile profile = volunteerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));
            
            profile.setSkillsList(skills);
            profile.setUpdatedAt(LocalDateTime.now());
            
            VolunteerProfile savedProfile = volunteerProfileRepository.save(profile);
            return convertToDTO(savedProfile);
            
        } catch (Exception e) {
            System.err.println("Error updating volunteer skills: " + e.getMessage());
            throw new RuntimeException("Failed to update skills: " + e.getMessage());
        }
    }

    /**
     * Update volunteer interests
     */
    public VolunteerProfileDTO updateVolunteerInterests(Long userId, List<String> interests) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
            VolunteerProfile profile = volunteerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));
            
            profile.setInterestsList(interests);
            profile.setUpdatedAt(LocalDateTime.now());
            
            VolunteerProfile savedProfile = volunteerProfileRepository.save(profile);
            return convertToDTO(savedProfile);
            
        } catch (Exception e) {
            System.err.println("Error updating volunteer interests: " + e.getMessage());
            throw new RuntimeException("Failed to update interests: " + e.getMessage());
        }
    }

    /**
     * Update volunteer hours (called by ApplicationService)
     */
    public void updateVolunteerHours(Long userId, Integer additionalHours) {
        System.out.println("Updating volunteer hours for user ID: " + userId + " with " + additionalHours + " hours");
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        VolunteerProfile profile = volunteerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));
        
        int currentHours = profile.getTotalVolunteerHours() != null ? profile.getTotalVolunteerHours() : 0;
        profile.setTotalVolunteerHours(currentHours + additionalHours);
        
        volunteerProfileRepository.save(profile);
        System.out.println("Volunteer hours updated successfully");
    }

    /**
     * Increment events participated count
     */
    public void incrementEventsParticipated(Long userId) {
        System.out.println("Incrementing events participated for user ID: " + userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        VolunteerProfile profile = volunteerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));
        
        int currentEvents = profile.getEventsParticipated() != null ? profile.getEventsParticipated() : 0;
        profile.setEventsParticipated(currentEvents + 1);
        
        volunteerProfileRepository.save(profile);
        System.out.println("Events participated count updated successfully");
    }

    // ==========================================
    // SEARCH AND DISCOVERY METHODS
    // ==========================================

    /**
     * Search volunteers by name
     */
    public List<VolunteerProfileDTO> searchVolunteersByName(String searchTerm) {
        System.out.println("Searching volunteers by name: " + searchTerm);
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }
        
        List<VolunteerProfile> profiles = volunteerProfileRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searchTerm, searchTerm);
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search volunteers by location
     */
    public List<VolunteerProfileDTO> searchVolunteersByLocation(String location) {
        System.out.println("Searching volunteers by location: " + location);
        
        if (location == null || location.trim().isEmpty()) {
            return List.of();
        }
        
        List<VolunteerProfile> profiles = volunteerProfileRepository.findByLocationContainingIgnoreCase(location);
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search volunteers by bio keywords
     */
    public List<VolunteerProfileDTO> searchVolunteersByBio(String keyword) {
        System.out.println("Searching volunteers by bio keyword: " + keyword);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        
        List<VolunteerProfile> profiles = volunteerProfileRepository.findByBioContaining(keyword);
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Advanced volunteer search
     */
    public List<VolunteerProfileDTO> advancedSearch(VolunteerSearchRequest request) {
        System.out.println("Performing advanced volunteer search");
        
        // Start with all available volunteers
        List<VolunteerProfile> profiles = volunteerProfileRepository.findByIsAvailableTrue();
        
        // Apply filters
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            profiles = profiles.stream()
                    .filter(profile -> 
                            (profile.getFirstName() != null && 
                             profile.getFirstName().toLowerCase().contains(request.getName().toLowerCase())) ||
                            (profile.getLastName() != null && 
                             profile.getLastName().toLowerCase().contains(request.getName().toLowerCase())))
                    .collect(Collectors.toList());
        }
        
        if (request.getLocation() != null && !request.getLocation().trim().isEmpty()) {
            profiles = profiles.stream()
                    .filter(profile -> profile.getLocation() != null && 
                            profile.getLocation().toLowerCase().contains(request.getLocation().toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (request.getMinHours() != null) {
            profiles = profiles.stream()
                    .filter(profile -> profile.getTotalVolunteerHours() != null && 
                            profile.getTotalVolunteerHours() >= request.getMinHours())
                    .collect(Collectors.toList());
        }
        
        if (request.getMinEvents() != null) {
            profiles = profiles.stream()
                    .filter(profile -> profile.getEventsParticipated() != null && 
                            profile.getEventsParticipated() >= request.getMinEvents())
                    .collect(Collectors.toList());
        }
        
        // Apply sorting
        if (request.getSortBy() != null) {
            switch (request.getSortBy()) {
                case "hours":
                    profiles.sort((a, b) -> Integer.compare(
                            b.getTotalVolunteerHours() != null ? b.getTotalVolunteerHours() : 0,
                            a.getTotalVolunteerHours() != null ? a.getTotalVolunteerHours() : 0));
                    break;
                case "events":
                    profiles.sort((a, b) -> Integer.compare(
                            b.getEventsParticipated() != null ? b.getEventsParticipated() : 0,
                            a.getEventsParticipated() != null ? a.getEventsParticipated() : 0));
                    break;
                case "name":
                    profiles.sort((a, b) -> {
                        String nameA = (a.getFirstName() != null ? a.getFirstName() : "") + " " + 
                                      (a.getLastName() != null ? a.getLastName() : "");
                        String nameB = (b.getFirstName() != null ? b.getFirstName() : "") + " " + 
                                      (b.getLastName() != null ? b.getLastName() : "");
                        return nameA.compareToIgnoreCase(nameB);
                    });
                    break;
                case "recent":
                default:
                    profiles.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
                    break;
            }
        }
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get available volunteers
     */
    public List<VolunteerProfileDTO> getAvailableVolunteers() {
        System.out.println("Fetching available volunteers");
        
        List<VolunteerProfile> profiles = volunteerProfileRepository.findByIsAvailableTrue();
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get available volunteers in specific location
     */
    public List<VolunteerProfileDTO> getAvailableVolunteersInLocation(String location) {
        System.out.println("Fetching available volunteers in location: " + location);
        
        List<VolunteerProfile> profiles = volunteerProfileRepository
                .findByIsAvailableTrueAndLocationContainingIgnoreCase(location);
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // RANKINGS AND LEADERBOARDS
    // ==========================================

    /**
     * Get top volunteers by hours
     */
    public List<VolunteerProfileDTO> getTopVolunteersByHours(int limit) {
        System.out.println("Fetching top " + limit + " volunteers by hours");
        
        List<VolunteerProfile> profiles = volunteerProfileRepository.findTopVolunteersByHours();
        
        return profiles.stream()
                .limit(limit)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get most active volunteers by events
     */
    public List<VolunteerProfileDTO> getMostActiveVolunteers(int limit) {
        System.out.println("Fetching top " + limit + " most active volunteers");
        
        List<VolunteerProfile> profiles = volunteerProfileRepository.findMostActiveVolunteers();
        
        return profiles.stream()
                .limit(limit)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get experienced volunteers (minimum hours/events)
     */
    public List<VolunteerProfileDTO> getExperiencedVolunteers(Integer minHours, Integer minEvents) {
        System.out.println("Fetching experienced volunteers with min hours: " + minHours + ", min events: " + minEvents);
        
        List<VolunteerProfile> profiles;
        
        if (minHours != null) {
            profiles = volunteerProfileRepository.findVolunteersWithMinimumHours(minHours);
        } else if (minEvents != null) {
            profiles = volunteerProfileRepository.findExperiencedVolunteers(minEvents);
        } else {
            // Default: volunteers with at least 10 hours or 3 events
            profiles = volunteerProfileRepository.findVolunteersWithMinimumHours(10);
        }
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get new volunteers (0 events participated)
     */
    public List<VolunteerProfileDTO> getNewVolunteers() {
        System.out.println("Fetching new volunteers");
        
        List<VolunteerProfile> profiles = volunteerProfileRepository.findByEventsParticipated(0);
        
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // STATISTICS AND ANALYTICS
    // ==========================================

    /**
     * Get platform volunteer statistics
     */
    public VolunteerStatsResponse getVolunteerStatistics() {
        System.out.println("Fetching volunteer platform statistics");
        
        VolunteerStatsResponse stats = new VolunteerStatsResponse();
        
        // Basic counts
        stats.setTotalVolunteers(volunteerProfileRepository.count());
        stats.setActiveVolunteers(volunteerProfileRepository.countByIsAvailableTrue());
        
        // Hours statistics
        Long totalHours = volunteerProfileRepository.getTotalVolunteerHours();
        Double averageHours = volunteerProfileRepository.getAverageVolunteerHours();
        stats.setTotalVolunteerHours(totalHours != null ? totalHours : 0L);
        stats.setAverageVolunteerHours(averageHours != null ? averageHours : 0.0);
        
        // Volunteer distribution by experience
        Object[] distribution = volunteerProfileRepository.getVolunteerDistributionByHours();
        if (distribution != null && distribution.length >= 4) {
            VolunteerDistribution dist = new VolunteerDistribution();
            dist.setNewVolunteers(((Number) distribution[0]).longValue());
            dist.setBeginners(((Number) distribution[1]).longValue());
            dist.setIntermediate(((Number) distribution[2]).longValue());
            dist.setExperienced(((Number) distribution[3]).longValue());
            stats.setDistribution(dist);
        }
        
        return stats;
    }

    /**
     * Get volunteer profile completion rate
     */
    public ProfileCompletionStats getProfileCompletionStats() {
        System.out.println("Calculating profile completion statistics");
        
        List<VolunteerProfile> allProfiles = volunteerProfileRepository.findAll();
        
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
     * Get individual volunteer statistics
     */
    public IndividualVolunteerStats getIndividualVolunteerStats(Long userId) {
        System.out.println("Fetching individual statistics for user ID: " + userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        VolunteerProfile profile = volunteerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Volunteer profile not found"));
        
        IndividualVolunteerStats stats = new IndividualVolunteerStats();
        stats.setTotalHours(profile.getTotalVolunteerHours() != null ? profile.getTotalVolunteerHours() : 0);
        stats.setEventsParticipated(profile.getEventsParticipated() != null ? profile.getEventsParticipated() : 0);
        stats.setProfileCompleteness(calculateProfileCompleteness(profile));
        stats.setMemberSince(profile.getCreatedAt());
        stats.setIsAvailable(profile.getIsAvailable());
        
        // Calculate ranking
        List<VolunteerProfile> allVolunteers = volunteerProfileRepository.findTopVolunteersByHours();
        int ranking = -1;
        for (int i = 0; i < allVolunteers.size(); i++) {
            if (allVolunteers.get(i).getId().equals(profile.getId())) {
                ranking = i + 1;
                break;
            }
        }
        stats.setHoursRanking(ranking);
        
        return stats;
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    private boolean isProfileComplete(VolunteerProfile profile) {
        return profile.getFirstName() != null && !profile.getFirstName().trim().isEmpty() &&
               profile.getLastName() != null && !profile.getLastName().trim().isEmpty() &&
               profile.getBio() != null && !profile.getBio().trim().isEmpty() &&
               profile.getLocation() != null && !profile.getLocation().trim().isEmpty();
    }

    private int calculateProfileCompleteness(VolunteerProfile profile) {
        int completeness = 0;
        int totalFields = 9; // firstName, lastName, bio, location, phone, profileImage, availability, skills, interests
        
        if (profile.getFirstName() != null && !profile.getFirstName().trim().isEmpty()) completeness++;
        if (profile.getLastName() != null && !profile.getLastName().trim().isEmpty()) completeness++;
        if (profile.getBio() != null && !profile.getBio().trim().isEmpty()) completeness++;
        if (profile.getLocation() != null && !profile.getLocation().trim().isEmpty()) completeness++;
        if (profile.getPhoneNumber() != null && !profile.getPhoneNumber().trim().isEmpty()) completeness++;
        if (profile.getProfileImageUrl() != null && !profile.getProfileImageUrl().trim().isEmpty()) completeness++;
        if (profile.getIsAvailable() != null) completeness++;
        if (profile.getSkills() != null && !profile.getSkills().trim().isEmpty()) completeness++;
        if (profile.getInterests() != null && !profile.getInterests().trim().isEmpty()) completeness++;
        
        return (completeness * 100) / totalFields;
    }

    /**
     * Enhanced convertToDTO method with all frontend data
     */
    private VolunteerProfileDTO convertToDTO(VolunteerProfile profile) {
        VolunteerProfileDTO dto = new VolunteerProfileDTO();
        
        // Existing fields
        dto.setId(profile.getId());
        dto.setUserId(profile.getUser().getId());
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setBio(profile.getBio());
        dto.setLocation(profile.getLocation());
        dto.setPhoneNumber(profile.getPhoneNumber());
        dto.setProfileImageUrl(profile.getProfileImageUrl());
        dto.setTotalVolunteerHours(profile.getTotalVolunteerHours());
        dto.setEventsParticipated(profile.getEventsParticipated());
        dto.setIsAvailable(profile.getIsAvailable());
        dto.setCreatedAt(profile.getCreatedAt());
        dto.setUpdatedAt(profile.getUpdatedAt());
        
        // New fields
        dto.setSkills(profile.getSkillsList());
        dto.setInterests(profile.getInterestsList());
        dto.setAvailabilityPreference(profile.getAvailabilityPreference());
        
        return dto;
    }

    // ==========================================
    // REQUEST/RESPONSE CLASSES
    // ==========================================

    public static class CreateVolunteerProfileRequest {
        private String firstName;
        private String lastName;
        private String bio;
        private String location;
        private String phoneNumber;
        private String profileImageUrl;
        private Boolean isAvailable;
        
        // Getters and setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getProfileImageUrl() { return profileImageUrl; }
        public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
        public Boolean getIsAvailable() { return isAvailable; }
        public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
    }

    public static class UpdateVolunteerProfileRequest {
        private String firstName;
        private String lastName;
        private String bio;
        private String location;
        private String phoneNumber;
        private String profileImageUrl;
        private Boolean isAvailable;
        
        // Getters and setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getProfileImageUrl() { return profileImageUrl; }
        public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
        public Boolean getIsAvailable() { return isAvailable; }
        public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
    }

    public static class VolunteerSearchRequest {
        private String name;
        private String location;
        private Integer minHours;
        private Integer minEvents;
        private String sortBy; // "hours", "events", "name", "recent"
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public Integer getMinHours() { return minHours; }
        public void setMinHours(Integer minHours) { this.minHours = minHours; }
        public Integer getMinEvents() { return minEvents; }
        public void setMinEvents(Integer minEvents) { this.minEvents = minEvents; }
        public String getSortBy() { return sortBy; }
        public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    }

    public static class VolunteerStatsResponse {
        private Long totalVolunteers;
        private Long activeVolunteers;
        private Long totalVolunteerHours;
        private Double averageVolunteerHours;
        private VolunteerDistribution distribution;
        
        // Getters and setters
        public Long getTotalVolunteers() { return totalVolunteers; }
        public void setTotalVolunteers(Long totalVolunteers) { this.totalVolunteers = totalVolunteers; }
        public Long getActiveVolunteers() { return activeVolunteers; }
        public void setActiveVolunteers(Long activeVolunteers) { this.activeVolunteers = activeVolunteers; }
        public Long getTotalVolunteerHours() { return totalVolunteerHours; }
        public void setTotalVolunteerHours(Long totalVolunteerHours) { this.totalVolunteerHours = totalVolunteerHours; }
        public Double getAverageVolunteerHours() { return averageVolunteerHours; }
        public void setAverageVolunteerHours(Double averageVolunteerHours) { this.averageVolunteerHours = averageVolunteerHours; }
        public VolunteerDistribution getDistribution() { return distribution; }
        public void setDistribution(VolunteerDistribution distribution) { this.distribution = distribution; }
    }

    public static class VolunteerDistribution {
        private Long newVolunteers;
        private Long beginners;
        private Long intermediate;
        private Long experienced;
        
        // Getters and setters
        public Long getNewVolunteers() { return newVolunteers; }
        public void setNewVolunteers(Long newVolunteers) { this.newVolunteers = newVolunteers; }
        public Long getBeginners() { return beginners; }
        public void setBeginners(Long beginners) { this.beginners = beginners; }
        public Long getIntermediate() { return intermediate; }
        public void setIntermediate(Long intermediate) { this.intermediate = intermediate; }
        public Long getExperienced() { return experienced; }
        public void setExperienced(Long experienced) { this.experienced = experienced; }
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

    public static class IndividualVolunteerStats {
        private Integer totalHours;
        private Integer eventsParticipated;
        private Integer profileCompleteness;
        private LocalDateTime memberSince;
        private Boolean isAvailable;
        private Integer hoursRanking;
        
        // Getters and setters
        public Integer getTotalHours() { return totalHours; }
        public void setTotalHours(Integer totalHours) { this.totalHours = totalHours; }
        public Integer getEventsParticipated() { return eventsParticipated; }
        public void setEventsParticipated(Integer eventsParticipated) { this.eventsParticipated = eventsParticipated; }
        public Integer getProfileCompleteness() { return profileCompleteness; }
        public void setProfileCompleteness(Integer profileCompleteness) { this.profileCompleteness = profileCompleteness; }
        public LocalDateTime getMemberSince() { return memberSince; }
        public void setMemberSince(LocalDateTime memberSince) { this.memberSince = memberSince; }
        public Boolean getIsAvailable() { return isAvailable; }
        public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
        public Integer getHoursRanking() { return hoursRanking; }
        public void setHoursRanking(Integer hoursRanking) { this.hoursRanking = hoursRanking; }
    }
}