package com.volunteersync.backend.dto.profile;

import com.volunteersync.backend.entity.enums.ExperienceLevel;
import com.volunteersync.backend.entity.enums.ProfileVisibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for VolunteerProfile entities.
 * Extends the base ProfileDTO with volunteer-specific fields including
 * skills, interests, experience, volunteer hours, and achievements.
 * 
 * This DTO is used for API responses and includes both basic profile
 * information and volunteer-specific data like skills and badges.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VolunteerProfileDTO extends ProfileDTO {

    // =====================================================
    // VOLUNTEER-SPECIFIC BASIC INFORMATION
    // =====================================================

    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String emergencyContactName;

    private String emergencyContactPhone;

    // =====================================================
    // VOLUNTEER EXPERIENCE & PREFERENCES
    // =====================================================

    private ExperienceLevel experienceLevel;

    @Min(value = 0, message = "Total volunteer hours cannot be negative")
    private Integer totalVolunteerHours;

    @Min(value = 0, message = "Available hours per week cannot be negative")
    private Integer availableHoursPerWeek;

    private List<String> preferredDays; // e.g., ["MONDAY", "WEDNESDAY", "FRIDAY"]

    private List<String> preferredTimeSlots; // e.g., ["MORNING", "EVENING"]

    private Boolean willingToTravel;

    @Min(value = 0, message = "Max travel distance cannot be negative")
    private Integer maxTravelDistance; // in kilometers

    private String transportation; // e.g., "CAR", "PUBLIC_TRANSPORT", "BICYCLE"

    // =====================================================
    // VOLUNTEER SKILLS & INTERESTS
    // =====================================================

    private List<ProfileSkillDTO> skills;

    private List<ProfileInterestDTO> interests;

    private List<String> languages; // Languages spoken

    private List<String> certifications; // Professional certifications

    // =====================================================
    // VOLUNTEER ACTIVITY & ACHIEVEMENTS
    // =====================================================

    private List<ProfileBadgeDTO> badges;

    @Min(value = 0, message = "Events completed cannot be negative")
    private Integer eventsCompleted;

    @Min(value = 0, message = "Organizations worked with cannot be negative")
    private Integer organizationsWorkedWith;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastVolunteerActivity;

    private Double averageRating; // Average rating from organizations

    @Min(value = 0, message = "Total reviews cannot be negative")
    private Integer totalReviews;

    // =====================================================
    // VOLUNTEER PREFERENCES & GOALS
    // =====================================================

    private List<String> causeAreas; // e.g., ["ENVIRONMENT", "EDUCATION", "HEALTH"]

    private List<String> volunteerTypes; // e.g., ["ONE_TIME", "RECURRING", "LONG_TERM"]

    private String motivations; // Why they volunteer

    private String goals; // What they hope to achieve

    private Boolean seekingSkillBuilding;

    private Boolean seekingNetworking;

    private Boolean seekingLeadership;

    // =====================================================
    // BACKGROUND CHECK & VERIFICATION
    // =====================================================

    private Boolean backgroundCheckCompleted;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate backgroundCheckDate;

    private String backgroundCheckStatus; // "PENDING", "APPROVED", "REJECTED"

    private Boolean referencesProvided;

    private List<String> referenceEmails;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public VolunteerProfileDTO() {
        super();
    }

    public VolunteerProfileDTO(Long id, Long userId, String userType, String bio, String location,
            ProfileVisibility visibility, Boolean isActive, Boolean isVerified,
            Boolean isCompleted, LocalDateTime createdAt, LocalDateTime updatedAt,
            String firstName, String lastName, ExperienceLevel experienceLevel,
            Integer totalVolunteerHours) {
        super(id, userId, userType, bio, location, visibility, isActive, isVerified,
                isCompleted, createdAt, updatedAt);
        this.firstName = firstName;
        this.lastName = lastName;
        this.experienceLevel = experienceLevel;
        this.totalVolunteerHours = totalVolunteerHours;
    }

    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================

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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(ExperienceLevel experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public Integer getTotalVolunteerHours() {
        return totalVolunteerHours;
    }

    public void setTotalVolunteerHours(Integer totalVolunteerHours) {
        this.totalVolunteerHours = totalVolunteerHours;
    }

    public Integer getAvailableHoursPerWeek() {
        return availableHoursPerWeek;
    }

    public void setAvailableHoursPerWeek(Integer availableHoursPerWeek) {
        this.availableHoursPerWeek = availableHoursPerWeek;
    }

    public List<String> getPreferredDays() {
        return preferredDays;
    }

    public void setPreferredDays(List<String> preferredDays) {
        this.preferredDays = preferredDays;
    }

    public List<String> getPreferredTimeSlots() {
        return preferredTimeSlots;
    }

    public void setPreferredTimeSlots(List<String> preferredTimeSlots) {
        this.preferredTimeSlots = preferredTimeSlots;
    }

    public Boolean getWillingToTravel() {
        return willingToTravel;
    }

    public void setWillingToTravel(Boolean willingToTravel) {
        this.willingToTravel = willingToTravel;
    }

    public Integer getMaxTravelDistance() {
        return maxTravelDistance;
    }

    public void setMaxTravelDistance(Integer maxTravelDistance) {
        this.maxTravelDistance = maxTravelDistance;
    }

    public String getTransportation() {
        return transportation;
    }

    public void setTransportation(String transportation) {
        this.transportation = transportation;
    }

    public List<ProfileSkillDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<ProfileSkillDTO> skills) {
        this.skills = skills;
    }

    public List<ProfileInterestDTO> getInterests() {
        return interests;
    }

    public void setInterests(List<ProfileInterestDTO> interests) {
        this.interests = interests;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<String> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<String> certifications) {
        this.certifications = certifications;
    }

    public List<ProfileBadgeDTO> getBadges() {
        return badges;
    }

    public void setBadges(List<ProfileBadgeDTO> badges) {
        this.badges = badges;
    }

    public Integer getEventsCompleted() {
        return eventsCompleted;
    }

    public void setEventsCompleted(Integer eventsCompleted) {
        this.eventsCompleted = eventsCompleted;
    }

    public Integer getOrganizationsWorkedWith() {
        return organizationsWorkedWith;
    }

    public void setOrganizationsWorkedWith(Integer organizationsWorkedWith) {
        this.organizationsWorkedWith = organizationsWorkedWith;
    }

    public LocalDateTime getLastVolunteerActivity() {
        return lastVolunteerActivity;
    }

    public void setLastVolunteerActivity(LocalDateTime lastVolunteerActivity) {
        this.lastVolunteerActivity = lastVolunteerActivity;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }

    public List<String> getCauseAreas() {
        return causeAreas;
    }

    public void setCauseAreas(List<String> causeAreas) {
        this.causeAreas = causeAreas;
    }

    public List<String> getVolunteerTypes() {
        return volunteerTypes;
    }

    public void setVolunteerTypes(List<String> volunteerTypes) {
        this.volunteerTypes = volunteerTypes;
    }

    public String getMotivations() {
        return motivations;
    }

    public void setMotivations(String motivations) {
        this.motivations = motivations;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public Boolean getSeekingSkillBuilding() {
        return seekingSkillBuilding;
    }

    public void setSeekingSkillBuilding(Boolean seekingSkillBuilding) {
        this.seekingSkillBuilding = seekingSkillBuilding;
    }

    public Boolean getSeekingNetworking() {
        return seekingNetworking;
    }

    public void setSeekingNetworking(Boolean seekingNetworking) {
        this.seekingNetworking = seekingNetworking;
    }

    public Boolean getSeekingLeadership() {
        return seekingLeadership;
    }

    public void setSeekingLeadership(Boolean seekingLeadership) {
        this.seekingLeadership = seekingLeadership;
    }

    public Boolean getBackgroundCheckCompleted() {
        return backgroundCheckCompleted;
    }

    public void setBackgroundCheckCompleted(Boolean backgroundCheckCompleted) {
        this.backgroundCheckCompleted = backgroundCheckCompleted;
    }

    public LocalDate getBackgroundCheckDate() {
        return backgroundCheckDate;
    }

    public void setBackgroundCheckDate(LocalDate backgroundCheckDate) {
        this.backgroundCheckDate = backgroundCheckDate;
    }

    public String getBackgroundCheckStatus() {
        return backgroundCheckStatus;
    }

    public void setBackgroundCheckStatus(String backgroundCheckStatus) {
        this.backgroundCheckStatus = backgroundCheckStatus;
    }

    public Boolean getReferencesProvided() {
        return referencesProvided;
    }

    public void setReferencesProvided(Boolean referencesProvided) {
        this.referencesProvided = referencesProvided;
    }

    public List<String> getReferenceEmails() {
        return referenceEmails;
    }

    public void setReferenceEmails(List<String> referenceEmails) {
        this.referenceEmails = referenceEmails;
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    /**
     * Gets the volunteer's full name.
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }
        return null;
    }

    // Add these methods to the VolunteerProfileDTO class
    // Place them in the UTILITY METHODS section after the existing getFullName()
    // method

    /**
     * Sets the volunteer's full name by parsing it into first and last name.
     * This method handles various name formats and edge cases.
     * 
     * @param fullName The full name to parse (e.g., "John Doe", "John", "John
     *                 Michael Doe")
     */
    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            this.firstName = null;
            this.lastName = null;
            return;
        }

        String trimmedName = fullName.trim();

        // Split the name by spaces
        String[] nameParts = trimmedName.split("\\s+");

        if (nameParts.length == 1) {
            // Only one name part - treat as first name
            this.firstName = nameParts[0];
            this.lastName = null;
        } else if (nameParts.length == 2) {
            // Two name parts - first and last name
            this.firstName = nameParts[0];
            this.lastName = nameParts[1];
        } else if (nameParts.length > 2) {
            // Multiple name parts - first name is first part, last name is everything else
            this.firstName = nameParts[0];
            this.lastName = String.join(" ", java.util.Arrays.copyOfRange(nameParts, 1, nameParts.length));
        }
    }

    /**
     * Checks if the volunteer has completed their background check.
     */
    public boolean isBackgroundCheckValid() {
        return backgroundCheckCompleted != null && backgroundCheckCompleted &&
                "APPROVED".equals(backgroundCheckStatus);
    }

    /**
     * Gets the volunteer's skill count.
     */
    public int getSkillCount() {
        return skills != null ? skills.size() : 0;
    }

    /**
     * Gets the volunteer's badge count.
     */
    public int getBadgeCount() {
        return badges != null ? badges.size() : 0;
    }

    @Override
    public String toString() {
        return "VolunteerProfileDTO{" +
                "id=" + getId() +
                ", userId=" + getUserId() +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", experienceLevel=" + experienceLevel +
                ", totalVolunteerHours=" + totalVolunteerHours +
                ", eventsCompleted=" + eventsCompleted +
                ", skillCount=" + getSkillCount() +
                ", badgeCount=" + getBadgeCount() +
                '}';
    }
}