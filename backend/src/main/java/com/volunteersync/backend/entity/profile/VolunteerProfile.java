package com.volunteersync.backend.entity.profile;

import com.volunteersync.backend.entity.enums.ExperienceLevel;
import com.volunteersync.backend.entity.user.User;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

/**
 * Volunteer-specific profile entity.
 * Extends the base Profile class with volunteer-specific fields
 * such as availability, experience, and background check information.
 */
@Entity
@DiscriminatorValue("VOLUNTEER")
public class VolunteerProfile extends Profile {

    // =====================================================
    // PERSONAL INFORMATION
    // =====================================================

    @Column
    private LocalDate dateOfBirth;

    @Column
    private String occupation;

    @Column
    private String company;

    @Column
    private String education;

    @Enumerated(EnumType.STRING)
    private ExperienceLevel experienceLevel;

    // =====================================================
    // AVAILABILITY PREFERENCES
    // =====================================================

    @Column(columnDefinition = "TEXT")
    private String availabilityNotes;

    @Column(nullable = false)
    private Boolean availableWeekdays = true;

    @Column(nullable = false)
    private Boolean availableWeekends = true;

    @Column(nullable = false)
    private Boolean availableEvening = false;

    @Column(nullable = false)
    private Boolean availableMorning = true;

    @Column(nullable = false)
    private Boolean availableAfternoon = true;

    // =====================================================
    // TRAVEL AND TRANSPORTATION
    // =====================================================

    @Column(nullable = false)
    private Boolean willingToTravel = false;

    @Column
    private Integer maxTravelDistance; // in miles

    @Column(nullable = false)
    private Boolean hasReliableTransportation = true;

    @Column(nullable = false)
    private Boolean hasDriversLicense = false;

    // =====================================================
    // VOLUNTEER EXPERIENCE
    // =====================================================

    @Column(nullable = false)
    private Boolean hasVolunteeredBefore = false;

    @Column(columnDefinition = "TEXT")
    private String previousVolunteerExperience;

    @Column
    private Integer totalVolunteerHours = 0;

    @Column
    private LocalDate firstVolunteerDate;

    // =====================================================
    // BACKGROUND CHECK & VERIFICATION
    // =====================================================

    @Column(nullable = false)
    private Boolean backgroundCheckCompleted = false;

    @Column
    private LocalDateTime backgroundCheckDate;

    @Column
    private String backgroundCheckProvider;

    @Column
    private LocalDateTime backgroundCheckExpiration;

    // =====================================================
    // EMERGENCY CONTACT INFORMATION
    // =====================================================

    @Column
    private String emergencyContactName;

    @Column
    private String emergencyContactPhone;

    @Column
    private String emergencyContactEmail;

    @Column
    private String emergencyContactRelation;

    // =====================================================
    // HEALTH & SAFETY
    // =====================================================

    @Column(columnDefinition = "TEXT")
    private String healthConditions;

    @Column(columnDefinition = "TEXT")
    private String medications;

    @Column(columnDefinition = "TEXT")
    private String allergies;

    @Column(nullable = false)
    private Boolean firstAidCertified = false;

    @Column
    private LocalDate firstAidCertificationExpiry;

    // =====================================================
    // PREFERENCES & RESTRICTIONS
    // =====================================================

    @Column(nullable = false)
    private Boolean canWorkWithMinors = true;

    @Column(nullable = false)
    private Boolean canLiftHeavyObjects = true;

    @Column(nullable = false)
    private Boolean canStandForLongPeriods = true;

    @Column(columnDefinition = "TEXT")
    private String physicalLimitations;

    @Column(columnDefinition = "TEXT")
    private String specialAccommodations;

    // =====================================================
    // COMMITMENT & SCHEDULING
    // =====================================================

    @Column
    private Integer preferredMinCommitmentHours; // per week/month

    @Column
    private Integer preferredMaxCommitmentHours;

    @Column
    private String preferredCommitmentFrequency; // "Weekly", "Monthly", "One-time", etc.

    @Column(nullable = false)
    private Boolean isActivelyVolunteering = false;

    @Column
    private LocalDateTime lastVolunteerActivity;

    @Column(name = "languages_spoken", columnDefinition = "TEXT")
    private String languagesSpoken; // Add this field

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public VolunteerProfile() {
        super();
    }

    public VolunteerProfile(User user) {
        super(user);
        setVolunteerDefaults();
    }

    // =====================================================
    // IMPLEMENTATION OF ABSTRACT METHODS
    // =====================================================

    @Override
    public String getProfileType() {
        return "VOLUNTEER";
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    /**
     * Get the volunteer's full name from the associated User entity
     * 
     * @return Full name or null if not available
     */
    public String getFullName() {
        if (getUser() != null) {
            String firstName = getUser().getFirstName();
            String lastName = getUser().getLastName();
            if (firstName != null && lastName != null) {
                return firstName + " " + lastName;
            }
        }
        return null;
    }

    /**
     * Calculate the volunteer's age based on date of birth
     * 
     * @return Age in years, or 0 if date of birth not set
     */
    public int getAge() {
        if (dateOfBirth != null) {
            return Period.between(dateOfBirth, LocalDate.now()).getYears();
        }
        return 0;
    }

    /**
     * Check if volunteer is eligible for roles requiring background checks
     * 
     * @return true if background check is completed and not expired
     */
    public boolean isEligibleForSensitiveRoles() {
        if (!Boolean.TRUE.equals(backgroundCheckCompleted)) {
            return false;
        }

        if (backgroundCheckExpiration != null) {
            return backgroundCheckExpiration.isAfter(LocalDateTime.now());
        }

        return true;
    }

    /**
     * Check if volunteer can work with children
     * 
     * @return true if eligible for roles involving minors
     */
    public boolean canWorkWithChildren() {
        return Boolean.TRUE.equals(canWorkWithMinors) &&
                isEligibleForSensitiveRoles();
    }

    /**
     * Check if first aid certification is current
     * 
     * @return true if certified and not expired
     */
    public boolean hasCurrentFirstAidCertification() {
        if (!Boolean.TRUE.equals(firstAidCertified)) {
            return false;
        }

        if (firstAidCertificationExpiry != null) {
            return firstAidCertificationExpiry.isAfter(LocalDate.now());
        }

        return true;
    }

    /**
     * Get years of volunteer experience
     * 
     * @return Years since first volunteer activity, or 0 if never volunteered
     */
    public int getYearsOfExperience() {
        if (firstVolunteerDate != null) {
            return Period.between(firstVolunteerDate, LocalDate.now()).getYears();
        }
        return 0;
    }

    /**
     * Check if volunteer is available on a specific day type
     * 
     * @param dayType "weekday", "weekend", "morning", "afternoon", "evening"
     * @return true if available for that time period
     */
    public boolean isAvailableFor(String dayType) {
        return switch (dayType.toLowerCase()) {
            case "weekday" -> Boolean.TRUE.equals(availableWeekdays);
            case "weekend" -> Boolean.TRUE.equals(availableWeekends);
            case "morning" -> Boolean.TRUE.equals(availableMorning);
            case "afternoon" -> Boolean.TRUE.equals(availableAfternoon);
            case "evening" -> Boolean.TRUE.equals(availableEvening);
            default -> false;
        };
    }

    /**
     * Set default values for volunteer profile
     */
    private void setVolunteerDefaults() {
        this.experienceLevel = ExperienceLevel.BEGINNER;
        this.availableWeekdays = true;
        this.availableWeekends = true;
        this.availableEvening = false;
        this.availableMorning = true;
        this.availableAfternoon = true;
        this.willingToTravel = false;
        this.hasReliableTransportation = true;
        this.hasDriversLicense = false;
        this.hasVolunteeredBefore = false;
        this.backgroundCheckCompleted = false;
        this.firstAidCertified = false;
        this.canWorkWithMinors = true;
        this.canLiftHeavyObjects = true;
        this.canStandForLongPeriods = true;
        this.isActivelyVolunteering = false;
        this.totalVolunteerHours = 0;
        this.hasHealthcareBackground = false; // ADD THIS LINE
    }

    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(ExperienceLevel experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public String getAvailabilityNotes() {
        return availabilityNotes;
    }

    public void setAvailabilityNotes(String availabilityNotes) {
        this.availabilityNotes = availabilityNotes;
    }

    public Boolean getAvailableWeekdays() {
        return availableWeekdays;
    }

    public void setAvailableWeekdays(Boolean availableWeekdays) {
        this.availableWeekdays = availableWeekdays;
    }

    public Boolean getAvailableWeekends() {
        return availableWeekends;
    }

    public void setAvailableWeekends(Boolean availableWeekends) {
        this.availableWeekends = availableWeekends;
    }

    public Boolean getAvailableEvening() {
        return availableEvening;
    }

    public void setAvailableEvening(Boolean availableEvening) {
        this.availableEvening = availableEvening;
    }

    public Boolean getAvailableMorning() {
        return availableMorning;
    }

    public void setAvailableMorning(Boolean availableMorning) {
        this.availableMorning = availableMorning;
    }

    public Boolean getAvailableAfternoon() {
        return availableAfternoon;
    }

    public void setAvailableAfternoon(Boolean availableAfternoon) {
        this.availableAfternoon = availableAfternoon;
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

    public Boolean getHasReliableTransportation() {
        return hasReliableTransportation;
    }

    public void setHasReliableTransportation(Boolean hasReliableTransportation) {
        this.hasReliableTransportation = hasReliableTransportation;
    }

    public Boolean getHasDriversLicense() {
        return hasDriversLicense;
    }

    public void setHasDriversLicense(Boolean hasDriversLicense) {
        this.hasDriversLicense = hasDriversLicense;
    }

    public Boolean getHasVolunteeredBefore() {
        return hasVolunteeredBefore;
    }

    public void setHasVolunteeredBefore(Boolean hasVolunteeredBefore) {
        this.hasVolunteeredBefore = hasVolunteeredBefore;
    }

    public String getPreviousVolunteerExperience() {
        return previousVolunteerExperience;
    }

    public void setPreviousVolunteerExperience(String previousVolunteerExperience) {
        this.previousVolunteerExperience = previousVolunteerExperience;
    }

    public Integer getTotalVolunteerHours() {
        return totalVolunteerHours;
    }

    public void setTotalVolunteerHours(Integer totalVolunteerHours) {
        this.totalVolunteerHours = totalVolunteerHours;
    }

    public LocalDate getFirstVolunteerDate() {
        return firstVolunteerDate;
    }

    public void setFirstVolunteerDate(LocalDate firstVolunteerDate) {
        this.firstVolunteerDate = firstVolunteerDate;
    }

    public Boolean getBackgroundCheckCompleted() {
        return backgroundCheckCompleted;
    }

    public void setBackgroundCheckCompleted(Boolean backgroundCheckCompleted) {
        this.backgroundCheckCompleted = backgroundCheckCompleted;
    }

    public LocalDateTime getBackgroundCheckDate() {
        return backgroundCheckDate;
    }

    public void setBackgroundCheckDate(LocalDateTime backgroundCheckDate) {
        this.backgroundCheckDate = backgroundCheckDate;
    }

    public String getBackgroundCheckProvider() {
        return backgroundCheckProvider;
    }

    public void setBackgroundCheckProvider(String backgroundCheckProvider) {
        this.backgroundCheckProvider = backgroundCheckProvider;
    }

    public LocalDateTime getBackgroundCheckExpiration() {
        return backgroundCheckExpiration;
    }

    public void setBackgroundCheckExpiration(LocalDateTime backgroundCheckExpiration) {
        this.backgroundCheckExpiration = backgroundCheckExpiration;
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

    public String getEmergencyContactEmail() {
        return emergencyContactEmail;
    }

    public void setEmergencyContactEmail(String emergencyContactEmail) {
        this.emergencyContactEmail = emergencyContactEmail;
    }

    public String getEmergencyContactRelation() {
        return emergencyContactRelation;
    }

    public void setEmergencyContactRelation(String emergencyContactRelation) {
        this.emergencyContactRelation = emergencyContactRelation;
    }

    public String getHealthConditions() {
        return healthConditions;
    }

    public void setHealthConditions(String healthConditions) {
        this.healthConditions = healthConditions;
    }

    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public Boolean getFirstAidCertified() {
        return firstAidCertified;
    }

    public void setFirstAidCertified(Boolean firstAidCertified) {
        this.firstAidCertified = firstAidCertified;
    }

    public LocalDate getFirstAidCertificationExpiry() {
        return firstAidCertificationExpiry;
    }

    public void setFirstAidCertificationExpiry(LocalDate firstAidCertificationExpiry) {
        this.firstAidCertificationExpiry = firstAidCertificationExpiry;
    }

    public Boolean getCanWorkWithMinors() {
        return canWorkWithMinors;
    }

    public void setCanWorkWithMinors(Boolean canWorkWithMinors) {
        this.canWorkWithMinors = canWorkWithMinors;
    }

    public Boolean getCanLiftHeavyObjects() {
        return canLiftHeavyObjects;
    }

    public void setCanLiftHeavyObjects(Boolean canLiftHeavyObjects) {
        this.canLiftHeavyObjects = canLiftHeavyObjects;
    }

    public Boolean getCanStandForLongPeriods() {
        return canStandForLongPeriods;
    }

    public void setCanStandForLongPeriods(Boolean canStandForLongPeriods) {
        this.canStandForLongPeriods = canStandForLongPeriods;
    }

    public String getPhysicalLimitations() {
        return physicalLimitations;
    }

    public void setPhysicalLimitations(String physicalLimitations) {
        this.physicalLimitations = physicalLimitations;
    }

    public String getSpecialAccommodations() {
        return specialAccommodations;
    }

    public void setSpecialAccommodations(String specialAccommodations) {
        this.specialAccommodations = specialAccommodations;
    }

    public Integer getPreferredMinCommitmentHours() {
        return preferredMinCommitmentHours;
    }

    public void setPreferredMinCommitmentHours(Integer preferredMinCommitmentHours) {
        this.preferredMinCommitmentHours = preferredMinCommitmentHours;
    }

    public Integer getPreferredMaxCommitmentHours() {
        return preferredMaxCommitmentHours;
    }

    public void setPreferredMaxCommitmentHours(Integer preferredMaxCommitmentHours) {
        this.preferredMaxCommitmentHours = preferredMaxCommitmentHours;
    }

    public String getPreferredCommitmentFrequency() {
        return preferredCommitmentFrequency;
    }

    public void setPreferredCommitmentFrequency(String preferredCommitmentFrequency) {
        this.preferredCommitmentFrequency = preferredCommitmentFrequency;
    }

    public Boolean getIsActivelyVolunteering() {
        return isActivelyVolunteering;
    }

    public void setIsActivelyVolunteering(Boolean isActivelyVolunteering) {
        this.isActivelyVolunteering = isActivelyVolunteering;
    }

    public void setLastVolunteerActivity(LocalDateTime lastVolunteerActivity) {
        this.lastVolunteerActivity = lastVolunteerActivity;
    }

    public String getLanguagesSpoken() {
        return languagesSpoken;
    }

    public void setLanguagesSpoken(String languagesSpoken) {
        this.languagesSpoken = languagesSpoken;
    }

    // ADD THIS NEW FIELD:
    @Column(name = "has_healthcare_background", nullable = false)
    private Boolean hasHealthcareBackground = false;

    // And add the getter and setter methods at the end of your class:

    public Boolean getHasHealthcareBackground() {
        return hasHealthcareBackground;
    }

    public void setHasHealthcareBackground(Boolean hasHealthcareBackground) {
        this.hasHealthcareBackground = hasHealthcareBackground;
    }
}