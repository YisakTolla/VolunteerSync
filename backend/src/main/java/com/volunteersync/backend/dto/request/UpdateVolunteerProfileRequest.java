package com.volunteersync.backend.dto.request;

import com.volunteersync.backend.entity.enums.ExperienceLevel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Email;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object for volunteer profile update requests.
 * Extends the base UpdateProfileRequest with volunteer-specific fields
 * that can be updated including personal information, preferences, and goals.
 * 
 * This request DTO includes validation for volunteer-specific update operations.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateVolunteerProfileRequest extends UpdateProfileRequest {

    // =====================================================
    // VOLUNTEER PERSONAL INFORMATION
    // =====================================================

    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @Past(message = "Date of birth must be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Size(max = 100, message = "Emergency contact name cannot exceed 100 characters")
    private String emergencyContactName;

    private String emergencyContactPhone;

    // =====================================================
    // VOLUNTEER EXPERIENCE & PREFERENCES
    // =====================================================

    private ExperienceLevel experienceLevel;

    @Min(value = 0, message = "Available hours per week cannot be negative")
    @Max(value = 168, message = "Available hours per week cannot exceed 168 (total hours in a week)")
    private Integer availableHoursPerWeek;

    private List<String> preferredDays; // e.g., ["MONDAY", "WEDNESDAY", "FRIDAY"]

    private List<String> preferredTimeSlots; // e.g., ["MORNING", "EVENING"]

    private Boolean willingToTravel;

    @Min(value = 0, message = "Max travel distance cannot be negative")
    @Max(value = 10000, message = "Max travel distance cannot exceed 10,000 km")
    private Integer maxTravelDistance; // in kilometers

    private String transportation; // e.g., "CAR", "PUBLIC_TRANSPORT", "BICYCLE", "WALKING"

    // =====================================================
    // SKILLS & CAPABILITIES
    // =====================================================

    private List<String> languages; // Languages spoken

    private List<String> certifications; // Professional certifications

    private List<String> newSkills; // New skills to add

    private List<String> newInterests; // New interests to add

    // =====================================================
    // VOLUNTEER PREFERENCES & GOALS
    // =====================================================

    private List<String> causeAreas; // e.g., ["ENVIRONMENT", "EDUCATION", "HEALTH"]

    private List<String> volunteerTypes; // e.g., ["ONE_TIME", "RECURRING", "LONG_TERM"]

    @Size(max = 1000, message = "Motivations cannot exceed 1000 characters")
    private String motivations; // Why they volunteer

    @Size(max = 1000, message = "Goals cannot exceed 1000 characters")
    private String goals; // What they hope to achieve

    private Boolean seekingSkillBuilding;

    private Boolean seekingNetworking;

    private Boolean seekingLeadership;

    // =====================================================
    // AVAILABILITY & SCHEDULING
    // =====================================================

    @Size(max = 500, message = "Availability notes cannot exceed 500 characters")
    private String availabilityNotes;

    private Boolean flexibleSchedule;

    private Boolean availableWeekends;

    private Boolean availableEvenings;

    private String timeZone;

    // =====================================================
    // BACKGROUND & VERIFICATION
    // =====================================================

    private Boolean willingBackgroundCheck;

    private Boolean referencesProvided;

    private List<String> referenceEmails;

    @Size(max = 500, message = "Special requirements cannot exceed 500 characters")
    private String specialRequirements; // Accessibility needs, dietary restrictions, etc.

    // =====================================================
    // NOTIFICATION PREFERENCES
    // =====================================================

    private Boolean receiveEventNotifications;

    private Boolean receiveMatchingNotifications;

    private Boolean receiveNewsletters;

    private String notificationFrequency; // "IMMEDIATE", "DAILY", "WEEKLY", "MONTHLY"

    private List<String> preferredCommunicationMethods; // "EMAIL", "SMS", "PHONE", "APP"

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public UpdateVolunteerProfileRequest() {
        super();
    }

    public UpdateVolunteerProfileRequest(String firstName, String lastName, String bio, 
                                       String location, ExperienceLevel experienceLevel) {
        super(bio, location, null);
        this.firstName = firstName;
        this.lastName = lastName;
        this.experienceLevel = experienceLevel;
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

    public List<String> getNewSkills() {
        return newSkills;
    }

    public void setNewSkills(List<String> newSkills) {
        this.newSkills = newSkills;
    }

    public List<String> getNewInterests() {
        return newInterests;
    }

    public void setNewInterests(List<String> newInterests) {
        this.newInterests = newInterests;
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

    public String getAvailabilityNotes() {
        return availabilityNotes;
    }

    public void setAvailabilityNotes(String availabilityNotes) {
        this.availabilityNotes = availabilityNotes;
    }

    public Boolean getFlexibleSchedule() {
        return flexibleSchedule;
    }

    public void setFlexibleSchedule(Boolean flexibleSchedule) {
        this.flexibleSchedule = flexibleSchedule;
    }

    public Boolean getAvailableWeekends() {
        return availableWeekends;
    }

    public void setAvailableWeekends(Boolean availableWeekends) {
        this.availableWeekends = availableWeekends;
    }

    public Boolean getAvailableEvenings() {
        return availableEvenings;
    }

    public void setAvailableEvenings(Boolean availableEvenings) {
        this.availableEvenings = availableEvenings;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Boolean getWillingBackgroundCheck() {
        return willingBackgroundCheck;
    }

    public void setWillingBackgroundCheck(Boolean willingBackgroundCheck) {
        this.willingBackgroundCheck = willingBackgroundCheck;
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

    public String getSpecialRequirements() {
        return specialRequirements;
    }

    public void setSpecialRequirements(String specialRequirements) {
        this.specialRequirements = specialRequirements;
    }

    public Boolean getReceiveEventNotifications() {
        return receiveEventNotifications;
    }

    public void setReceiveEventNotifications(Boolean receiveEventNotifications) {
        this.receiveEventNotifications = receiveEventNotifications;
    }

    public Boolean getReceiveMatchingNotifications() {
        return receiveMatchingNotifications;
    }

    public void setReceiveMatchingNotifications(Boolean receiveMatchingNotifications) {
        this.receiveMatchingNotifications = receiveMatchingNotifications;
    }

    public Boolean getReceiveNewsletters() {
        return receiveNewsletters;
    }

    public void setReceiveNewsletters(Boolean receiveNewsletters) {
        this.receiveNewsletters = receiveNewsletters;
    }

    public String getNotificationFrequency() {
        return notificationFrequency;
    }

    public void setNotificationFrequency(String notificationFrequency) {
        this.notificationFrequency = notificationFrequency;
    }

    public List<String> getPreferredCommunicationMethods() {
        return preferredCommunicationMethods;
    }

    public void setPreferredCommunicationMethods(List<String> preferredCommunicationMethods) {
        this.preferredCommunicationMethods = preferredCommunicationMethods;
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    /**
     * Checks if volunteer-specific fields have been updated.
     */
    public boolean hasVolunteerSpecificUpdates() {
        return firstName != null || lastName != null || dateOfBirth != null ||
               experienceLevel != null || availableHoursPerWeek != null ||
               preferredDays != null || preferredTimeSlots != null ||
               willingToTravel != null || maxTravelDistance != null ||
               transportation != null || languages != null || certifications != null;
    }

    /**
     * Checks if availability preferences have been updated.
     */
    public boolean hasAvailabilityUpdates() {
        return availableHoursPerWeek != null || preferredDays != null ||
               preferredTimeSlots != null || flexibleSchedule != null ||
               availableWeekends != null || availableEvenings != null ||
               timeZone != null || availabilityNotes != null;
    }

    /**
     * Checks if goals and motivations have been updated.
     */
    public boolean hasGoalsUpdates() {
        return motivations != null || goals != null || seekingSkillBuilding != null ||
               seekingNetworking != null || seekingLeadership != null ||
               causeAreas != null || volunteerTypes != null;
    }

    /**
     * Checks if notification preferences have been updated.
     */
    public boolean hasNotificationUpdates() {
        return receiveEventNotifications != null || receiveMatchingNotifications != null ||
               receiveNewsletters != null || notificationFrequency != null ||
               preferredCommunicationMethods != null;
    }

    /**
     * Gets the volunteer's full name if both first and last names are provided.
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

    /**
     * Validates that reference emails are properly formatted.
     */
    public boolean areReferenceEmailsValid() {
        if (referenceEmails == null || referenceEmails.isEmpty()) {
            return true; // Optional field
        }
        
        return referenceEmails.stream()
                .allMatch(email -> email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"));
    }

    /**
     * Checks if the volunteer is available for high-commitment activities.
     */
    public boolean isAvailableForHighCommitment() {
        return availableHoursPerWeek != null && availableHoursPerWeek >= 10 &&
               (flexibleSchedule == null || flexibleSchedule) &&
               (availableWeekends == null || availableWeekends);
    }

    /**
     * Gets a summary of availability preferences.
     */
    public String getAvailabilitySummary() {
        StringBuilder summary = new StringBuilder();
        
        if (availableHoursPerWeek != null) {
            summary.append(availableHoursPerWeek).append(" hours/week");
        }
        
        if (preferredDays != null && !preferredDays.isEmpty()) {
            if (summary.length() > 0) summary.append(", ");
            summary.append("Days: ").append(String.join(", ", preferredDays));
        }
        
        if (preferredTimeSlots != null && !preferredTimeSlots.isEmpty()) {
            if (summary.length() > 0) summary.append(", ");
            summary.append("Times: ").append(String.join(", ", preferredTimeSlots));
        }
        
        return summary.toString();
    }

    @Override
    public boolean hasAnyUpdates() {
        return super.hasAnyUpdates() || hasVolunteerSpecificUpdates() ||
               hasAvailabilityUpdates() || hasGoalsUpdates() || hasNotificationUpdates();
    }

    @Override
    public String toString() {
        return "UpdateVolunteerProfileRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", experienceLevel=" + experienceLevel +
                ", availableHoursPerWeek=" + availableHoursPerWeek +
                ", willingToTravel=" + willingToTravel +
                ", maxTravelDistance=" + maxTravelDistance +
                ", causeAreas=" + (causeAreas != null ? causeAreas.size() + " areas" : "null") +
                ", seekingSkillBuilding=" + seekingSkillBuilding +
                ", seekingNetworking=" + seekingNetworking +
                ", seekingLeadership=" + seekingLeadership +
                ", baseUpdates=" + super.hasAnyUpdates() +
                '}';
    }
}