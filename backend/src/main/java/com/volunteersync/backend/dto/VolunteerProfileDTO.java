package com.volunteersync.backend.dto;

import java.time.LocalDateTime;

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
    private String skills;
    private List<String> skillsList;
    private String interests;  
    private List<String> interestsList;
    private String availabilityPreference;

    // Constructors
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

    // Getters and Setters
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
}