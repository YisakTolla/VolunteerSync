package com.volunteersync.backend.dto;

import java.time.LocalDateTime;
import java.util.*;

public class VolunteerProfileDTO {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String bio;
    private String location;
    private String phoneNumber;
    private String profileImageUrl;
    private Integer totalVolunteerHours;
    private Integer eventsParticipated;
    private Boolean isAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // NEW FIELDS FOR FRONTEND COMPATIBILITY
    private List<String> skills;
    private List<String> interests;
    private String availabilityPreference;

    // FRONTEND INTEGRATION FIELDS
    private List<BadgeDTO> badges; // Frontend expects user badges
    private List<ActivityEntry> recentActivity; // Frontend expects recent activities
    private List<Connection> connections; // Frontend expects user connections

    private List<Long> followedOrganizations;
    private Integer followedOrganizationsCount;

    // =====================================================
    // SUPPORTING CLASSES
    // =====================================================

    public static class ActivityEntry {
        private String type; // "event", "application", "badge"
        private String title;
        private String description;
        private LocalDateTime timestamp;
        private String status;
        private String icon;

        // Constructors
        public ActivityEntry() {
        }

        public ActivityEntry(String type, String title, String description,
                LocalDateTime timestamp, String status, String icon) {
            this.type = type;
            this.title = title;
            this.description = description;
            this.timestamp = timestamp;
            this.status = status;
            this.icon = icon;
        }

        // Getters and Setters
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

    public static class Connection {
        private String name;
        private String role;
        private String organization;
        private String profileImageUrl;
        private LocalDateTime connectedAt;

        // Constructors
        public Connection() {
        }

        public Connection(String name, String role, String organization,
                String profileImageUrl, LocalDateTime connectedAt) {
            this.name = name;
            this.role = role;
            this.organization = organization;
            this.profileImageUrl = profileImageUrl;
            this.connectedAt = connectedAt;
        }

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getOrganization() {
            return organization;
        }

        public void setOrganization(String organization) {
            this.organization = organization;
        }

        public String getProfileImageUrl() {
            return profileImageUrl;
        }

        public void setProfileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
        }

        public LocalDateTime getConnectedAt() {
            return connectedAt;
        }

        public void setConnectedAt(LocalDateTime connectedAt) {
            this.connectedAt = connectedAt;
        }
    }

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public VolunteerProfileDTO() {
    }

    public VolunteerProfileDTO(Long id, String firstName, String lastName, String location,
            Integer totalVolunteerHours, Integer eventsParticipated) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + " " + lastName;
        this.location = location;
        this.totalVolunteerHours = totalVolunteerHours;
        this.eventsParticipated = eventsParticipated;
    }

    // =====================================================
    // GETTERS AND SETTERS - EXISTING FIELDS
    // =====================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateFullName();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateFullName();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    private void updateFullName() {
        this.fullName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Integer getTotalVolunteerHours() {
        return totalVolunteerHours;
    }

    public void setTotalVolunteerHours(Integer totalVolunteerHours) {
        this.totalVolunteerHours = totalVolunteerHours;
    }

    public Integer getEventsParticipated() {
        return eventsParticipated;
    }

    public void setEventsParticipated(Integer eventsParticipated) {
        this.eventsParticipated = eventsParticipated;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // =====================================================
    // GETTERS AND SETTERS - NEW FIELDS
    // =====================================================

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public String getAvailabilityPreference() {
        return availabilityPreference;
    }

    public void setAvailabilityPreference(String availabilityPreference) {
        this.availabilityPreference = availabilityPreference;
    }

    public List<BadgeDTO> getBadges() {
        return badges;
    }

    public void setBadges(List<BadgeDTO> badges) {
        this.badges = badges;
    }

    public List<ActivityEntry> getRecentActivity() {
        return recentActivity;
    }

    public void setRecentActivity(List<ActivityEntry> recentActivity) {
        this.recentActivity = recentActivity;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }

    public List<Long> getFollowedOrganizations() {
        return followedOrganizations;
    }

    public void setFollowedOrganizations(List<Long> followedOrganizations) {
        this.followedOrganizations = followedOrganizations;
        this.followedOrganizationsCount = followedOrganizations != null ? followedOrganizations.size() : 0;
    }

    public Integer getFollowedOrganizationsCount() {
        return followedOrganizationsCount;
    }

    public void setFollowedOrganizationsCount(Integer followedOrganizationsCount) {
        this.followedOrganizationsCount = followedOrganizationsCount;
    }

    public boolean isFollowingOrganization(Long organizationId) {
        return followedOrganizations != null && followedOrganizations.contains(organizationId);
    }
}