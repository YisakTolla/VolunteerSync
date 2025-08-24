package com.volunteersync.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "volunteer_profiles")
public class VolunteerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(length = 1000)
    private String bio;

    private String location;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "total_volunteer_hours")
    private Integer totalVolunteerHours = 0;

    @Column(name = "events_participated")
    private Integer eventsParticipated = 0;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "skills")
    private String skills; // "JavaScript,React,Leadership,Communication"

    @Column(name = "interests")
    private String interests; // "Environment,Education,Healthcare,Technology"

    @Column(name = "availability_preference")
    private String availabilityPreference; // "weekends", "weekdays", "flexible"

    // NEW FIELD: Store followed organization IDs as comma-separated string
    @Column(name = "followed_organizations")
    private String followedOrganizations; // "1,5,10,25"

    // =====================================================
    // FOLLOWED ORGANIZATIONS METHODS
    // =====================================================

    public String getFollowedOrganizations() {
        return followedOrganizations;
    }

    public void setFollowedOrganizations(String followedOrganizations) {
        this.followedOrganizations = followedOrganizations;
    }

    public List<Long> getFollowedOrganizationsList() {
        if (followedOrganizations == null || followedOrganizations.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(followedOrganizations.split(","))
                .stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    public void setFollowedOrganizationsList(List<Long> organizationIds) {
        if (organizationIds == null || organizationIds.isEmpty()) {
            this.followedOrganizations = null;
        } else {
            this.followedOrganizations = organizationIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }
    }

    public boolean isFollowingOrganization(Long organizationId) {
        if (organizationId == null)
            return false;
        List<Long> followedIds = getFollowedOrganizationsList();
        return followedIds.contains(organizationId);
    }

    public void followOrganization(Long organizationId) {
        if (organizationId == null)
            return;

        List<Long> followedIds = getFollowedOrganizationsList();
        if (!followedIds.contains(organizationId)) {
            followedIds.add(organizationId);
            setFollowedOrganizationsList(followedIds);
        }
    }

    public void unfollowOrganization(Long organizationId) {
        if (organizationId == null)
            return;

        List<Long> followedIds = getFollowedOrganizationsList();
        followedIds.remove(organizationId);
        setFollowedOrganizationsList(followedIds);
    }

    public int getFollowedOrganizationsCount() {
        return getFollowedOrganizationsList().size();
    }

    // =====================================================
    // EXISTING SKILLS AND INTERESTS METHODS
    // =====================================================

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public List<String> getSkillsList() {
        if (skills == null || skills.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(skills.split(","))
                .stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    public void setSkillsList(List<String> skillsList) {
        if (skillsList == null || skillsList.isEmpty()) {
            this.skills = null;
        } else {
            this.skills = String.join(",", skillsList);
        }
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public List<String> getInterestsList() {
        if (interests == null || interests.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(interests.split(","))
                .stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    public void setInterestsList(List<String> interestsList) {
        if (interestsList == null || interestsList.isEmpty()) {
            this.interests = null;
        } else {
            this.interests = String.join(",", interestsList);
        }
    }

    public String getAvailabilityPreference() {
        return availabilityPreference;
    }

    public void setAvailabilityPreference(String availabilityPreference) {
        this.availabilityPreference = availabilityPreference;
    }

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public VolunteerProfile() {
    }

    public VolunteerProfile(User user, String firstName, String lastName) {
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // =====================================================
    // BASIC GETTERS AND SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
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

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}