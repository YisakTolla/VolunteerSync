package com.volunteersync.backend.service;

import com.volunteersync.backend.entity.profile.VolunteerProfile;
import com.volunteersync.backend.entity.user.User;
import com.volunteersync.backend.entity.profile.ProfileSkill;
import com.volunteersync.backend.entity.profile.ProfileInterest;
import com.volunteersync.backend.entity.profile.ProfileBadge;
import com.volunteersync.backend.entity.enums.ExperienceLevel;
import com.volunteersync.backend.entity.enums.SkillLevel;
import com.volunteersync.backend.repository.VolunteerProfileRepository;
import com.volunteersync.backend.repository.ProfileSkillRepository;
import com.volunteersync.backend.repository.ProfileInterestRepository;
import com.volunteersync.backend.repository.ProfileBadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing volunteer-specific profile functionality.
 * Handles volunteer skills, interests, experience, badges, and activities.
 * 
 * This service extends the base profile functionality with volunteer-specific
 * features like skill management, experience tracking, and badge systems.
 */
@Service
@Transactional
public class VolunteerProfileService {

    private static final Logger logger = LoggerFactory.getLogger(VolunteerProfileService.class);

    @Autowired
    private VolunteerProfileRepository volunteerProfileRepository;

    @Autowired
    private ProfileSkillRepository profileSkillRepository;

    @Autowired
    private ProfileInterestRepository profileInterestRepository;

    @Autowired
    private ProfileBadgeRepository profileBadgeRepository;

    // =====================================================
    // VOLUNTEER PROFILE CREATION & SETUP
    // =====================================================

    /**
     * Creates a new volunteer profile with default settings.
     * 
     * @param user The user to create a profile for
     * @return The created volunteer profile
     */
    public VolunteerProfile createVolunteerProfile(User user) {
        logger.info("Creating volunteer profile for user ID: {}", user.getId());

        VolunteerProfile profile = new VolunteerProfile(user);
        
        // Set volunteer-specific defaults (already handled by setVolunteerDefaults() in entity)
        // Additional defaults specific to our service
        profile.setTotalVolunteerHours(0);

        profile = volunteerProfileRepository.save(profile);
        logger.info("Successfully created volunteer profile ID: {} for user: {}", 
                   profile.getId(), user.getId());
        
        return profile;
    }

    /**
     * Completes volunteer profile setup after initial creation.
     * Used during the profile setup wizard.
     * 
     * @param profileId The profile ID
     * @param userId The user ID (for verification)
     * @param bio Profile bio
     * @param location Location
     * @param experienceLevel Experience level
     * @param availabilityNotes Availability notes
     * @param skills List of skills
     * @param interests List of interests
     * @return The updated profile
     */
    public VolunteerProfile completeProfileSetup(Long profileId, Long userId, String bio,
                                                String location, ExperienceLevel experienceLevel,
                                                String availabilityNotes, List<String> skills,
                                                List<String> interests) {
        logger.info("Completing profile setup for volunteer profile ID: {}", profileId);

        VolunteerProfile profile = volunteerProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Volunteer profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        // Update basic info
        if (bio != null) profile.setBio(bio.trim());
        if (location != null) profile.setLocation(location.trim());
        if (experienceLevel != null) profile.setExperienceLevel(experienceLevel);
        if (availabilityNotes != null) profile.setAvailabilityNotes(availabilityNotes.trim());

        // Add skills
        if (skills != null && !skills.isEmpty()) {
            addSkillsToProfile(profile, skills, SkillLevel.BEGINNER);
        }

        // Add interests
        if (interests != null && !interests.isEmpty()) {
            addInterestsToProfile(profile, interests);
        }

        profile = volunteerProfileRepository.save(profile);
        logger.info("Successfully completed profile setup for volunteer ID: {}", profileId);
        
        return profile;
    }

    // =====================================================
    // VOLUNTEER-SPECIFIC PROFILE UPDATES
    // =====================================================

    /**
     * Updates availability preferences for a volunteer.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param availableWeekdays Available on weekdays
     * @param availableWeekends Available on weekends
     * @param availableMorning Available in morning
     * @param availableAfternoon Available in afternoon
     * @param availableEvening Available in evening
     * @param availabilityNotes Additional availability notes
     * @return The updated profile
     */
    public VolunteerProfile updateAvailabilityPreferences(Long profileId, Long userId,
                                                         Boolean availableWeekdays, Boolean availableWeekends,
                                                         Boolean availableMorning, Boolean availableAfternoon,
                                                         Boolean availableEvening, String availabilityNotes) {
        logger.info("Updating availability preferences for volunteer profile ID: {}", profileId);

        VolunteerProfile profile = volunteerProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Volunteer profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        // Update availability fields
        if (availableWeekdays != null) profile.setAvailableWeekdays(availableWeekdays);
        if (availableWeekends != null) profile.setAvailableWeekends(availableWeekends);
        if (availableMorning != null) profile.setAvailableMorning(availableMorning);
        if (availableAfternoon != null) profile.setAvailableAfternoon(availableAfternoon);
        if (availableEvening != null) profile.setAvailableEvening(availableEvening);
        if (availabilityNotes != null) profile.setAvailabilityNotes(availabilityNotes.trim());

        profile = volunteerProfileRepository.save(profile);
        logger.info("Successfully updated availability preferences for profile ID: {}", profileId);
        
        return profile;
    }

    /**
     * Updates travel and transportation information.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param willingToTravel Whether willing to travel
     * @param maxTravelDistance Maximum travel distance in miles
     * @param hasReliableTransportation Has reliable transportation
     * @param hasDriversLicense Has driver's license
     * @return The updated profile
     */
    public VolunteerProfile updateTravelPreferences(Long profileId, Long userId,
                                                   Boolean willingToTravel, Integer maxTravelDistance,
                                                   Boolean hasReliableTransportation, Boolean hasDriversLicense) {
        logger.info("Updating travel preferences for volunteer profile ID: {}", profileId);

        VolunteerProfile profile = volunteerProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Volunteer profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        // Update travel fields
        if (willingToTravel != null) profile.setWillingToTravel(willingToTravel);
        if (maxTravelDistance != null) profile.setMaxTravelDistance(maxTravelDistance);
        if (hasReliableTransportation != null) profile.setHasReliableTransportation(hasReliableTransportation);
        if (hasDriversLicense != null) profile.setHasDriversLicense(hasDriversLicense);

        profile = volunteerProfileRepository.save(profile);
        logger.info("Successfully updated travel preferences for profile ID: {}", profileId);
        
        return profile;
    }

    /**
     * Updates personal information for a volunteer.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param dateOfBirth Date of birth
     * @param occupation Occupation
     * @param company Company
     * @param education Education background
     * @return The updated profile
     */
    public VolunteerProfile updatePersonalInfo(Long profileId, Long userId,
                                             LocalDate dateOfBirth, String occupation,
                                             String company, String education) {
        logger.info("Updating personal info for volunteer profile ID: {}", profileId);

        VolunteerProfile profile = volunteerProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Volunteer profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        // Update personal info fields
        if (dateOfBirth != null) profile.setDateOfBirth(dateOfBirth);
        if (occupation != null) profile.setOccupation(occupation.trim());
        if (company != null) profile.setCompany(company.trim());
        if (education != null) profile.setEducation(education.trim());

        profile = volunteerProfileRepository.save(profile);
        logger.info("Successfully updated personal info for profile ID: {}", profileId);
        
        return profile;
    }

    /**
     * Updates background check information.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param backgroundCheckCompleted Whether background check is completed
     * @param backgroundCheckDate Date of background check
     * @param backgroundCheckProvider Provider of background check
     * @param backgroundCheckExpiration Expiration date
     * @return The updated profile
     */
    public VolunteerProfile updateBackgroundCheckInfo(Long profileId, Long userId,
                                                     Boolean backgroundCheckCompleted,
                                                     LocalDateTime backgroundCheckDate,
                                                     String backgroundCheckProvider,
                                                     LocalDateTime backgroundCheckExpiration) {
        logger.info("Updating background check info for volunteer profile ID: {}", profileId);

        VolunteerProfile profile = volunteerProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Volunteer profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        // Update background check fields
        if (backgroundCheckCompleted != null) profile.setBackgroundCheckCompleted(backgroundCheckCompleted);
        if (backgroundCheckDate != null) profile.setBackgroundCheckDate(backgroundCheckDate);
        if (backgroundCheckProvider != null) profile.setBackgroundCheckProvider(backgroundCheckProvider.trim());
        if (backgroundCheckExpiration != null) profile.setBackgroundCheckExpiration(backgroundCheckExpiration);

        profile = volunteerProfileRepository.save(profile);
        logger.info("Successfully updated background check info for profile ID: {}", profileId);
        
        return profile;
    }

    /**
     * Updates health and safety information.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param healthConditions Health conditions
     * @param medications Medications
     * @param allergies Allergies
     * @param firstAidCertified Whether first aid certified
     * @param firstAidCertificationExpiry First aid certification expiry
     * @return The updated profile
     */
    public VolunteerProfile updateHealthSafetyInfo(Long profileId, Long userId,
                                                  String healthConditions, String medications,
                                                  String allergies, Boolean firstAidCertified,
                                                  LocalDate firstAidCertificationExpiry) {
        logger.info("Updating health and safety info for volunteer profile ID: {}", profileId);

        VolunteerProfile profile = volunteerProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Volunteer profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        // Update health and safety fields
        if (healthConditions != null) profile.setHealthConditions(healthConditions.trim());
        if (medications != null) profile.setMedications(medications.trim());
        if (allergies != null) profile.setAllergies(allergies.trim());
        if (firstAidCertified != null) profile.setFirstAidCertified(firstAidCertified);
        if (firstAidCertificationExpiry != null) profile.setFirstAidCertificationExpiry(firstAidCertificationExpiry);

        profile = volunteerProfileRepository.save(profile);
        logger.info("Successfully updated health and safety info for profile ID: {}", profileId);
        
        return profile;
    }

    /**
     * Updates physical capabilities and restrictions.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param canWorkWithMinors Can work with minors
     * @param canLiftHeavyObjects Can lift heavy objects
     * @param canStandForLongPeriods Can stand for long periods
     * @param physicalLimitations Physical limitations
     * @param specialAccommodations Special accommodations needed
     * @return The updated profile
     */
    public VolunteerProfile updatePhysicalCapabilities(Long profileId, Long userId,
                                                      Boolean canWorkWithMinors, Boolean canLiftHeavyObjects,
                                                      Boolean canStandForLongPeriods, String physicalLimitations,
                                                      String specialAccommodations) {
        logger.info("Updating physical capabilities for volunteer profile ID: {}", profileId);

        VolunteerProfile profile = volunteerProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Volunteer profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        // Update physical capability fields
        if (canWorkWithMinors != null) profile.setCanWorkWithMinors(canWorkWithMinors);
        if (canLiftHeavyObjects != null) profile.setCanLiftHeavyObjects(canLiftHeavyObjects);
        if (canStandForLongPeriods != null) profile.setCanStandForLongPeriods(canStandForLongPeriods);
        if (physicalLimitations != null) profile.setPhysicalLimitations(physicalLimitations.trim());
        if (specialAccommodations != null) profile.setSpecialAccommodations(specialAccommodations.trim());

        profile = volunteerProfileRepository.save(profile);
        logger.info("Successfully updated physical capabilities for profile ID: {}", profileId);
        
        return profile;
    }

    /**
     * Updates commitment preferences.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param preferredMinCommitmentHours Preferred minimum commitment hours
     * @param preferredMaxCommitmentHours Preferred maximum commitment hours
     * @param preferredCommitmentFrequency Preferred commitment frequency
     * @return The updated profile
     */
    public VolunteerProfile updateCommitmentPreferences(Long profileId, Long userId,
                                                       Integer preferredMinCommitmentHours,
                                                       Integer preferredMaxCommitmentHours,
                                                       String preferredCommitmentFrequency) {
        logger.info("Updating commitment preferences for volunteer profile ID: {}", profileId);

        VolunteerProfile profile = volunteerProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Volunteer profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        // Update commitment preference fields
        if (preferredMinCommitmentHours != null) profile.setPreferredMinCommitmentHours(preferredMinCommitmentHours);
        if (preferredMaxCommitmentHours != null) profile.setPreferredMaxCommitmentHours(preferredMaxCommitmentHours);
        if (preferredCommitmentFrequency != null) profile.setPreferredCommitmentFrequency(preferredCommitmentFrequency.trim());

        profile = volunteerProfileRepository.save(profile);
        logger.info("Successfully updated commitment preferences for profile ID: {}", profileId);
        
        return profile;
    }

    /**
     * Updates volunteer-specific information.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param experienceLevel Updated experience level
     * @param availabilityNotes Updated availability notes
     * @param isActivelyVolunteering Whether currently actively volunteering
     * @return The updated profile
     */
    public VolunteerProfile updateVolunteerInfo(Long profileId, Long userId, 
                                              ExperienceLevel experienceLevel, String availabilityNotes,
                                              Boolean isActivelyVolunteering) {
        logger.info("Updating volunteer info for profile ID: {}", profileId);

        VolunteerProfile profile = volunteerProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Volunteer profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        // Update volunteer-specific fields
        if (experienceLevel != null) profile.setExperienceLevel(experienceLevel);
        if (availabilityNotes != null) profile.setAvailabilityNotes(availabilityNotes.trim());
        if (isActivelyVolunteering != null) profile.setIsActivelyVolunteering(isActivelyVolunteering);

        profile = volunteerProfileRepository.save(profile);
        logger.info("Successfully updated volunteer info for profile ID: {}", profileId);
        
        return profile;
    }

    /**
     * Updates emergency contact information.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param emergencyContactName Emergency contact name
     * @param emergencyContactPhone Emergency contact phone
     * @param emergencyContactEmail Emergency contact email
     * @param emergencyContactRelation Relationship to volunteer
     * @return The updated profile
     */
    public VolunteerProfile updateEmergencyContact(Long profileId, Long userId,
                                                 String emergencyContactName, String emergencyContactPhone,
                                                 String emergencyContactEmail, String emergencyContactRelation) {
        logger.info("Updating emergency contact for volunteer profile ID: {}", profileId);

        VolunteerProfile profile = volunteerProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Volunteer profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        profile.setEmergencyContactName(emergencyContactName);
        profile.setEmergencyContactPhone(emergencyContactPhone);
        profile.setEmergencyContactEmail(emergencyContactEmail);
        profile.setEmergencyContactRelation(emergencyContactRelation);

        profile = volunteerProfileRepository.save(profile);
        logger.info("Successfully updated emergency contact for profile ID: {}", profileId);
        
        return profile;
    }

    // =====================================================
    // SKILLS MANAGEMENT
    // =====================================================

    /**
     * Adds skills to a volunteer profile.
     * 
     * @param profile The volunteer profile
     * @param skillNames List of skill names to add
     * @param defaultLevel Default skill level for new skills
     */
    private void addSkillsToProfile(VolunteerProfile profile, List<String> skillNames, SkillLevel defaultLevel) {
        for (String skillName : skillNames) {
            if (skillName != null && !skillName.trim().isEmpty()) {
                ProfileSkill skill = new ProfileSkill();
                skill.setProfile(profile);
                skill.setSkillName(skillName.trim());
                skill.setLevel(defaultLevel); // Using 'level' instead of 'skillLevel'
                skill.setVerified(false);
                skill.setEndorsementCount(0);
                
                profileSkillRepository.save(skill);
            }
        }
    }

    /**
     * Adds a single skill to a volunteer profile.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param skillName The skill name
     * @param skillLevel The skill level
     * @return The created skill
     */
    public ProfileSkill addSkill(Long profileId, Long userId, String skillName, SkillLevel skillLevel) {
        logger.info("Adding skill '{}' to volunteer profile ID: {}", skillName, profileId);

        VolunteerProfile profile = volunteerProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Volunteer profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        // Check if skill already exists
        Optional<ProfileSkill> existingSkill = profileSkillRepository
                .findByProfileAndSkillNameIgnoreCase(profile, skillName.trim());
        
        if (existingSkill.isPresent()) {
            throw new IllegalArgumentException("Skill already exists for this profile");
        }

        ProfileSkill skill = new ProfileSkill();
        skill.setProfile(profile);
        skill.setSkillName(skillName.trim());
        skill.setLevel(skillLevel != null ? skillLevel : SkillLevel.BEGINNER); // Using 'level'
        skill.setVerified(false); // Using 'verified'
        skill.setEndorsementCount(0);

        skill = profileSkillRepository.save(skill);
        logger.info("Successfully added skill '{}' to profile ID: {}", skillName, profileId);
        
        return skill;
    }

    /**
     * Updates a skill level for a volunteer.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param skillId The skill ID to update
     * @param newLevel The new skill level
     * @return The updated skill
     */
    public ProfileSkill updateSkillLevel(Long profileId, Long userId, Long skillId, SkillLevel newLevel) {
        logger.info("Updating skill level for skill ID: {} in profile ID: {}", skillId, profileId);

        VolunteerProfile profile = volunteerProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Volunteer profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        ProfileSkill skill = profileSkillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));

        // Verify skill belongs to this profile
        if (!skill.getProfile().getId().equals(profileId)) {
            throw new IllegalArgumentException("Skill does not belong to this profile");
        }

        skill.setLevel(newLevel); // Using 'level'
        skill = profileSkillRepository.save(skill);
        
        logger.info("Successfully updated skill level for skill ID: {}", skillId);
        return skill;
    }

    /**
     * Removes a skill from a volunteer profile.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param skillId The skill ID to remove
     * @return true if successfully removed
     */
    public boolean removeSkill(Long profileId, Long userId, Long skillId) {
        logger.info("Removing skill ID: {} from profile ID: {}", skillId, profileId);

        VolunteerProfile profile = volunteerProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Volunteer profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        ProfileSkill skill = profileSkillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));

        // Verify skill belongs to this profile
        if (!skill.getProfile().getId().equals(profileId)) {
            throw new IllegalArgumentException("Skill does not belong to this profile");
        }

        profileSkillRepository.delete(skill);
        logger.info("Successfully removed skill ID: {} from profile ID: {}", skillId, profileId);
        return true;
    }

    /**
     * Gets all skills for a volunteer profile.
     * 
     * @param profileId The profile ID
     * @return List of skills
     */
    @Transactional(readOnly = true)
    public List<ProfileSkill> getProfileSkills(Long profileId) {
        return profileSkillRepository.findByProfileId(profileId);
    }

    // =====================================================
    // INTERESTS MANAGEMENT
    // =====================================================

    /**
     * Adds interests to a volunteer profile.
     * 
     * @param profile The volunteer profile
     * @param interestNames List of interest names to add
     */
    private void addInterestsToProfile(VolunteerProfile profile, List<String> interestNames) {
        for (String interestName : interestNames) {
            if (interestName != null && !interestName.trim().isEmpty()) {
                ProfileInterest interest = new ProfileInterest();
                interest.setProfile(profile);
                interest.setInterestName(interestName.trim());
                interest.setCategory("General"); // Default category
                
                profileInterestRepository.save(interest);
            }
        }
    }

    /**
     * Adds a single interest to a volunteer profile.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param interestName The interest name
     * @param category The interest category
     * @return The created interest
     */
    public ProfileInterest addInterest(Long profileId, Long userId, String interestName, String category) {
        logger.info("Adding interest '{}' to volunteer profile ID: {}", interestName, profileId);

        VolunteerProfile profile = volunteerProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Volunteer profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        // Check if interest already exists
        Optional<ProfileInterest> existingInterest = profileInterestRepository
                .findByProfileAndInterestNameIgnoreCase(profile, interestName.trim());
        
        if (existingInterest.isPresent()) {
            throw new IllegalArgumentException("Interest already exists for this profile");
        }

        ProfileInterest interest = new ProfileInterest();
        interest.setProfile(profile);
        interest.setInterestName(interestName.trim());
        interest.setCategory(category != null ? category.trim() : "General");

        interest = profileInterestRepository.save(interest);
        logger.info("Successfully added interest '{}' to profile ID: {}", interestName, profileId);
        
        return interest;
    }

    /**
     * Removes an interest from a volunteer profile.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param interestId The interest ID to remove
     * @return true if successfully removed
     */
    public boolean removeInterest(Long profileId, Long userId, Long interestId) {
        logger.info("Removing interest ID: {} from profile ID: {}", interestId, profileId);

        VolunteerProfile profile = volunteerProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Volunteer profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        ProfileInterest interest = profileInterestRepository.findById(interestId)
                .orElseThrow(() -> new IllegalArgumentException("Interest not found"));

        // Verify interest belongs to this profile
        if (!interest.getProfile().getId().equals(profileId)) {
            throw new IllegalArgumentException("Interest does not belong to this profile");
        }

        profileInterestRepository.delete(interest);
        logger.info("Successfully removed interest ID: {} from profile ID: {}", interestId, profileId);
        return true;
    }

    /**
     * Gets all interests for a volunteer profile.
     * 
     * @param profileId The profile ID
     * @return List of interests
     */
    @Transactional(readOnly = true)
    public List<ProfileInterest> getProfileInterests(Long profileId) {
        return profileInterestRepository.findByProfileId(profileId);
    }

    // =====================================================
    // BADGES & ACHIEVEMENTS
    // =====================================================

    /**
     * Awards a badge to a volunteer profile with full badge details.
     * 
     * @param profileId The profile ID
     * @param badgeName The badge name
     * @param badgeType The badge type (Hours, Skills, Impact, etc.)
     * @param badgeDescription Badge description
     * @param badgeIcon Badge icon emoji
     * @param awardedBy Who awarded the badge
     * @param rarity Badge rarity level
     * @param pointValue Points awarded for this badge
     * @param milestoneValue The milestone value achieved
     * @param milestoneUnit The milestone unit (hours, events, etc.)
     * @return The created badge
     */
    public ProfileBadge awardDetailedBadge(Long profileId, String badgeName, String badgeType,
                                         String badgeDescription, String badgeIcon, String awardedBy,
                                         ProfileBadge.BadgeRarity rarity, Integer pointValue,
                                         Integer milestoneValue, String milestoneUnit) {
        logger.info("Awarding detailed badge '{}' to volunteer profile ID: {}", badgeName, profileId);

        VolunteerProfile profile = volunteerProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Volunteer profile not found"));

        // Check if badge already exists for this profile
        Optional<ProfileBadge> existingBadge = profileBadgeRepository
                .findByProfileAndBadgeNameIgnoreCase(profile, badgeName.trim());
        
        if (existingBadge.isPresent()) {
            logger.warn("Badge '{}' already exists for profile ID: {}", badgeName, profileId);
            return existingBadge.get();
        }

        // Use the entity constructor with rarity
        ProfileBadge badge = new ProfileBadge(profile, badgeName.trim(), badgeType, 
                                            rarity != null ? rarity : ProfileBadge.BadgeRarity.COMMON);
        
        // Set additional fields
        badge.setDescription(badgeDescription);
        badge.setIconEmoji(badgeIcon);
        badge.setAwardedBy(awardedBy);
        badge.setCategory("Volunteer");
        
        if (pointValue != null) badge.setPointValue(pointValue);
        if (milestoneValue != null) badge.setMilestoneValue(milestoneValue);
        if (milestoneUnit != null) badge.setMilestoneUnit(milestoneUnit);
        
        badge.setMilestoneDate(LocalDateTime.now());
        badge.setVerificationMethod("Automatic");
        badge.setVerifiedAt(LocalDateTime.now());

        badge = profileBadgeRepository.save(badge);
        logger.info("Successfully awarded detailed badge '{}' to profile ID: {}", badgeName, profileId);
        
        return badge;
    }

    /**
     * Awards a badge to a volunteer profile.
     * 
     * @param profileId The profile ID
     * @param badgeName The badge name
     * @param badgeDescription Badge description
     * @param badgeIcon Badge icon URL or identifier
     * @param awardedBy Who awarded the badge (system or organization)
     * @return The created badge
     */
    public ProfileBadge awardBadge(Long profileId, String badgeName, String badgeDescription,
                                 String badgeIcon, String awardedBy) {
        logger.info("Awarding badge '{}' to volunteer profile ID: {}", badgeName, profileId);

        VolunteerProfile profile = volunteerProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Volunteer profile not found"));

        // Check if badge already exists for this profile
        Optional<ProfileBadge> existingBadge = profileBadgeRepository
                .findByProfileAndBadgeNameIgnoreCase(profile, badgeName.trim());
        
        if (existingBadge.isPresent()) {
            logger.warn("Badge '{}' already exists for profile ID: {}", badgeName, profileId);
            return existingBadge.get();
        }

        ProfileBadge badge = new ProfileBadge();
        badge.setProfile(profile);
        badge.setBadgeName(badgeName.trim());
        badge.setDescription(badgeDescription);
        badge.setIconEmoji(badgeIcon); // Using iconEmoji instead of badgeIcon
        badge.setAwardedBy(awardedBy);
        badge.setIsPublic(true);
        badge.setIsActive(true);
        badge.setBadgeType("Achievement"); // Default badge type

        badge = profileBadgeRepository.save(badge);
        logger.info("Successfully awarded badge '{}' to profile ID: {}", badgeName, profileId);
        
        return badge;
    }

    /**
     * Gets all badges for a volunteer profile.
     * 
     * @param profileId The profile ID
     * @param includeHidden Whether to include hidden badges (simplified implementation)
     * @return List of badges
     */
    @Transactional(readOnly = true)
    public List<ProfileBadge> getProfileBadges(Long profileId, boolean includeHidden) {
        // For now, return all badges for the profile
        // TODO: Implement visibility filtering when needed
        return profileBadgeRepository.findByProfileId(profileId);
    }

    /**
     * Updates badge visibility.
     * 
     * @param profileId The profile ID
     * @param userId The user ID making the update
     * @param badgeId The badge ID
     * @param isVisible Whether badge should be visible
     * @return The updated badge
     */
    public ProfileBadge updateBadgeVisibility(Long profileId, Long userId, Long badgeId, boolean isVisible) {
        logger.info("Updating badge visibility for badge ID: {} in profile ID: {}", badgeId, profileId);

        VolunteerProfile profile = volunteerProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Volunteer profile not found"));

        // Verify ownership
        if (!profile.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this profile");
        }

        ProfileBadge badge = profileBadgeRepository.findById(badgeId)
                .orElseThrow(() -> new IllegalArgumentException("Badge not found"));

        // Verify badge belongs to this profile
        if (!badge.getProfile().getId().equals(profileId)) {
            throw new IllegalArgumentException("Badge does not belong to this profile");
        }

        badge.setIsPublic(isVisible); // Using isPublic instead of isVisible
        badge = profileBadgeRepository.save(badge);
        
        logger.info("Successfully updated badge visibility for badge ID: {}", badgeId);
        return badge;
    }

    // =====================================================
    // VOLUNTEER STATISTICS & ANALYTICS
    // =====================================================

    /**
     * Updates volunteer statistics after activity completion.
     * 
     * @param profileId The profile ID
     * @param hoursContributed Hours to add
     * @param newActivity Whether a new activity was completed
     */
    public void updateVolunteerStats(Long profileId, Integer hoursContributed, Boolean newActivity) {
        logger.info("Updating volunteer stats for profile ID: {}", profileId);

        VolunteerProfile profile = volunteerProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Volunteer profile not found"));

        if (hoursContributed != null && hoursContributed > 0) {
            int currentHours = profile.getTotalVolunteerHours() != null ? profile.getTotalVolunteerHours() : 0;
            profile.setTotalVolunteerHours(currentHours + hoursContributed);
        }

        if (newActivity != null && newActivity) {
            profile.setLastVolunteerActivity(LocalDateTime.now());
            profile.setIsActivelyVolunteering(true);
            
            // Set first volunteer date if not already set
            if (profile.getFirstVolunteerDate() == null) {
                profile.setFirstVolunteerDate(LocalDate.now());
                profile.setHasVolunteeredBefore(true);
            }
        }

        volunteerProfileRepository.save(profile);
        
        // Check for milestone badges
        checkAndAwardMilestoneBadges(profile);
        
        logger.info("Successfully updated volunteer stats for profile ID: {}", profileId);
    }

    /**
     * Checks and awards milestone badges based on volunteer activity.
     * 
     * @param profile The volunteer profile
     */
    private void checkAndAwardMilestoneBadges(VolunteerProfile profile) {
        int totalHours = profile.getTotalVolunteerHours() != null ? profile.getTotalVolunteerHours() : 0;
        
        // Hours-based badges with proper rarity and details
        if (totalHours >= 100 && !hasBadge(profile.getId(), "Century Volunteer")) {
            awardDetailedBadge(profile.getId(), "Century Volunteer", "Hours", 
                      "Completed 100+ volunteer hours", "🏆", "system",
                      ProfileBadge.BadgeRarity.UNCOMMON, 50, 100, "hours");
        }
        
        if (totalHours >= 500 && !hasBadge(profile.getId(), "Super Volunteer")) {
            awardDetailedBadge(profile.getId(), "Super Volunteer", "Hours", 
                      "Completed 500+ volunteer hours", "⭐", "system",
                      ProfileBadge.BadgeRarity.RARE, 150, 500, "hours");
        }
        
        if (totalHours >= 1000 && !hasBadge(profile.getId(), "Hero Volunteer")) {
            awardDetailedBadge(profile.getId(), "Hero Volunteer", "Hours", 
                      "Completed 1000+ volunteer hours", "🦸", "system",
                      ProfileBadge.BadgeRarity.EPIC, 300, 1000, "hours");
        }
        
        // Experience-based badges
        int yearsOfExperience = profile.getYearsOfExperience();
        if (yearsOfExperience >= 1 && !hasBadge(profile.getId(), "Loyal Volunteer")) {
            awardDetailedBadge(profile.getId(), "Loyal Volunteer", "Milestone", 
                      "Active volunteer for 1+ years", "💝", "system",
                      ProfileBadge.BadgeRarity.UNCOMMON, 25, yearsOfExperience, "years");
        }
        
        // Special qualification badges
        if (profile.getBackgroundCheckCompleted() && profile.isEligibleForSensitiveRoles() 
            && !hasBadge(profile.getId(), "Verified Volunteer")) {
            awardDetailedBadge(profile.getId(), "Verified Volunteer", "Verification", 
                      "Completed background check verification", "✅", "system",
                      ProfileBadge.BadgeRarity.RARE, 75, null, null);
        }
        
        if (profile.hasCurrentFirstAidCertification() && !hasBadge(profile.getId(), "First Aid Ready")) {
            awardDetailedBadge(profile.getId(), "First Aid Ready", "Skills", 
                      "Current first aid certification", "🏥", "system",
                      ProfileBadge.BadgeRarity.UNCOMMON, 40, null, null);
        }
        
        // Availability badges
        if (profile.getAvailableWeekends() && profile.getAvailableEvening() 
            && !hasBadge(profile.getId(), "Flexible Helper")) {
            awardDetailedBadge(profile.getId(), "Flexible Helper", "Availability", 
                      "Available weekends and evenings", "🕐", "system",
                      ProfileBadge.BadgeRarity.COMMON, 20, null, null);
        }
        
        // Travel badge
        if (profile.getWillingToTravel() && !hasBadge(profile.getId(), "Mobile Volunteer")) {
            awardDetailedBadge(profile.getId(), "Mobile Volunteer", "Travel", 
                      "Willing to travel for volunteer opportunities", "🚗", "system",
                      ProfileBadge.BadgeRarity.COMMON, 15, null, null);
        }
        
        // Special capability badges
        if (profile.canWorkWithChildren() && !hasBadge(profile.getId(), "Youth Mentor")) {
            awardDetailedBadge(profile.getId(), "Youth Mentor", "Special", 
                      "Qualified to work with minors", "👨‍🏫", "system",
                      ProfileBadge.BadgeRarity.RARE, 60, null, null);
        }
    }

    /**
     * Checks if a profile already has a specific badge.
     * 
     * @param profileId The profile ID
     * @param badgeName The badge name
     * @return true if badge exists
     */
    private boolean hasBadge(Long profileId, String badgeName) {
        VolunteerProfile profile = volunteerProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));
        return profileBadgeRepository.existsByProfileAndBadgeNameIgnoreCase(profile, badgeName);
    }

    /**
     * Gets comprehensive volunteer statistics.
     * 
     * @param profile The volunteer profile
     * @return ProfileStats with volunteer-specific metrics
     */
    public ProfileService.ProfileStats getVolunteerStats(VolunteerProfile profile) {
        // Get basic profile stats
        int totalViews = 0; // TODO: Implement profile view tracking
        int totalConnections = 0; // TODO: Implement when connections feature is added
        int totalActivities = 0; // TODO: Count volunteer activities
        
        return new ProfileService.ProfileStats(
            profile.getId(),
            profile.getDisplayName(),
            profile.getCreatedAt(),
            profile.getUpdatedAt(),
            totalViews,
            totalConnections,
            totalActivities
        );
    }

    // =====================================================
    // SEARCH & DISCOVERY
    // =====================================================

    /**
     * Finds volunteers by experience level and availability.
     * 
     * @param experienceLevel Experience level
     * @param isAvailable Whether volunteer must be available
     * @return List of matching volunteer profiles
     */
    @Transactional(readOnly = true)
    public List<VolunteerProfile> findVolunteersByExperience(ExperienceLevel experienceLevel,
                                                           Boolean isAvailable) {
        logger.debug("Searching for volunteers with experience: {}", experienceLevel);
        
        if (experienceLevel != null) {
            return volunteerProfileRepository.findByExperienceLevel(experienceLevel);
        }
        
        // Return all volunteers if no criteria specified
        return volunteerProfileRepository.findAll();
    }

    /**
     * Finds volunteers by availability criteria.
     * 
     * @param weekdays Available weekdays
     * @param weekends Available weekends
     * @param morning Available mornings
     * @param afternoon Available afternoons
     * @param evening Available evenings
     * @return List of matching volunteer profiles
     */
    @Transactional(readOnly = true)
    public List<VolunteerProfile> findVolunteersByAvailability(Boolean weekdays, Boolean weekends,
                                                             Boolean morning, Boolean afternoon, Boolean evening) {
        logger.debug("Searching for volunteers by availability criteria");
        
        return volunteerProfileRepository.findByAvailabilityCriteria(
            weekdays != null ? weekdays : false,
            weekends != null ? weekends : false,
            morning != null ? morning : false,
            afternoon != null ? afternoon : false,
            evening != null ? evening : false
        );
    }

    /**
     * Finds volunteers suitable for organization matching.
     * 
     * @param location Organization location
     * @param requiresBackgroundCheck Whether background check is required
     * @param requiresTravel Whether travel is required
     * @param experienceLevel Minimum experience level required
     * @return List of suitable volunteers
     */
    @Transactional(readOnly = true)
    public List<VolunteerProfile> findMatchingVolunteers(String location, Boolean requiresBackgroundCheck,
                                                        Boolean requiresTravel, ExperienceLevel experienceLevel) {
        logger.debug("Finding matching volunteers for location: {}, experienceLevel: {}", location, experienceLevel);
        
        return volunteerProfileRepository.findMatchingVolunteers(
            location,
            requiresBackgroundCheck != null ? requiresBackgroundCheck : false,
            requiresTravel != null ? requiresTravel : false,
            experienceLevel
        );
    }

    /**
     * Finds volunteers by age range.
     * 
     * @param minAge Minimum age
     * @param maxAge Maximum age
     * @return List of volunteers in age range
     */
    @Transactional(readOnly = true)
    public List<VolunteerProfile> findVolunteersByAge(Integer minAge, Integer maxAge) {
        logger.debug("Searching for volunteers aged {} to {}", minAge, maxAge);
        
        if (minAge != null && maxAge != null) {
            return volunteerProfileRepository.findByAgeRange(minAge, maxAge);
        }
        
        return volunteerProfileRepository.findAll();
    }

    /**
     * Finds volunteers by language skills.
     * 
     * @param language The language to search for
     * @return List of multilingual volunteers
     */
    @Transactional(readOnly = true)
    public List<VolunteerProfile> findVolunteersByLanguage(String language) {
        logger.debug("Searching for volunteers with language: {}", language);
        
        if (language != null && !language.trim().isEmpty()) {
            return volunteerProfileRepository.findByLanguageSkills(language.trim());
        }
        
        return List.of();
    }

    /**
     * Finds volunteers by occupation.
     * 
     * @param occupation The occupation to search for
     * @return List of volunteers with that occupation
     */
    @Transactional(readOnly = true)
    public List<VolunteerProfile> findVolunteersByOccupation(String occupation) {
        logger.debug("Searching for volunteers with occupation: {}", occupation);
        
        if (occupation != null && !occupation.trim().isEmpty()) {
            return volunteerProfileRepository.findByOccupationContainingIgnoreCase(occupation.trim());
        }
        
        return List.of();
    }

    /**
     * Finds volunteers willing to travel.
     * 
     * @return List of volunteers who can travel
     */
    @Transactional(readOnly = true)
    public List<VolunteerProfile> findVolunteersWillingToTravel() {
        logger.debug("Finding volunteers willing to travel");
        return volunteerProfileRepository.findByWillingToTravelTrue();
    }

    /**
     * Finds volunteers with healthcare background.
     * 
     * @return List of healthcare volunteers
     */
    @Transactional(readOnly = true)
    public List<VolunteerProfile> findHealthcareVolunteers() {
        logger.debug("Finding volunteers with healthcare background");
        return volunteerProfileRepository.findHealthcareVolunteers();
    }

    /**
     * Finds volunteers with completed background checks.
     * 
     * @return List of volunteers with background clearance
     */
    @Transactional(readOnly = true)
    public List<VolunteerProfile> findVolunteersWithBackgroundCheck() {
        logger.debug("Finding volunteers with completed background checks");
        return volunteerProfileRepository.findByBackgroundCheckCompletedTrue();
    }

    /**
     * Finds new volunteers (recently joined).
     * 
     * @param since Joined since this date
     * @return List of new volunteers
     */
    @Transactional(readOnly = true)
    public List<VolunteerProfile> findNewVolunteers(LocalDateTime since) {
        logger.debug("Finding new volunteers since: {}", since);
        return volunteerProfileRepository.findByCreatedAtAfterOrderByCreatedAtDesc(since);
    }

    /**
     * Finds recently active volunteers.
     * 
     * @param since Active since this date
     * @return List of recently active volunteers
     */
    @Transactional(readOnly = true)
    public List<VolunteerProfile> findActiveVolunteers(LocalDateTime since) {
        logger.debug("Finding active volunteers since: {}", since);
        return volunteerProfileRepository.findActiveVolunteers(since);
    }

    /**
     * Finds expert volunteers who can mentor others.
     * 
     * @return List of expert volunteers
     */
    @Transactional(readOnly = true)
    public List<VolunteerProfile> findExpertVolunteers() {
        logger.debug("Finding expert volunteers for mentoring");
        return volunteerProfileRepository.findExpertVolunteers();
    }

    /**
     * Finds beginner volunteers who might need mentoring.
     * 
     * @return List of beginner volunteers
     */
    @Transactional(readOnly = true)
    public List<VolunteerProfile> findBeginnerVolunteers() {
        logger.debug("Finding beginner volunteers who might need mentoring");
        return volunteerProfileRepository.findBeginnerVolunteers();
    }

    /**
     * Finds volunteers with flexible availability.
     * 
     * @return List of highly available volunteers
     */
    @Transactional(readOnly = true)
    public List<VolunteerProfile> findFlexibleVolunteers() {
        logger.debug("Finding volunteers with flexible availability");
        return volunteerProfileRepository.findFlexibleVolunteers();
    }

    /**
     * Finds similar volunteers for recommendations.
     * 
     * @param profileId The volunteer profile to find similar profiles for
     * @param limit Maximum number of results
     * @return List of similar volunteers
     */
    @Transactional(readOnly = true)
    public List<VolunteerProfile> findSimilarVolunteers(Long profileId, Integer limit) {
        logger.debug("Finding similar volunteers to profile ID: {}", profileId);
        
        int maxResults = limit != null ? limit : 10;
        return volunteerProfileRepository.findSimilarVolunteers(profileId, maxResults);
    }

    // =====================================================
    // VOLUNTEER STATISTICS & ANALYTICS
    // =====================================================

    /**
     * Gets volunteer count by experience level.
     * 
     * @param experienceLevel The experience level to count
     * @return Number of volunteers at that level
     */
    @Transactional(readOnly = true)
    public long getVolunteerCountByExperience(ExperienceLevel experienceLevel) {
        return volunteerProfileRepository.countByExperienceLevel(experienceLevel);
    }

    /**
     * Gets count of volunteers available for weekends.
     * 
     * @return Number of weekend-available volunteers
     */
    @Transactional(readOnly = true)
    public long getWeekendAvailableCount() {
        return volunteerProfileRepository.countByAvailableWeekendsTrue();
    }

    /**
     * Gets count of volunteers willing to travel.
     * 
     * @return Number of travel-willing volunteers
     */
    @Transactional(readOnly = true)
    public long getTravelWillingCount() {
        return volunteerProfileRepository.countByWillingToTravelTrue();
    }

    /**
     * Gets experience level distribution statistics.
     * 
     * @return Map of experience levels to counts
     */
    @Transactional(readOnly = true)
    public java.util.Map<ExperienceLevel, Long> getExperienceLevelStatistics() {
        List<Object[]> results = volunteerProfileRepository.getExperienceLevelStatistics();
        return results.stream()
                .collect(java.util.stream.Collectors.toMap(
                    result -> (ExperienceLevel) result[0],
                    result -> (Long) result[1]
                ));
    }

    /**
     * Gets availability statistics.
     * 
     * @return Availability breakdown object
     */
    @Transactional(readOnly = true)
    public AvailabilityStats getAvailabilityStatistics() {
        Object[] result = volunteerProfileRepository.getAvailabilityStatistics();
        
        if (result != null && result.length >= 5) {
            return new AvailabilityStats(
                ((Number) result[0]).longValue(), // weekdaysCount
                ((Number) result[1]).longValue(), // weekendsCount
                ((Number) result[2]).longValue(), // morningCount
                ((Number) result[3]).longValue(), // afternoonCount
                ((Number) result[4]).longValue()  // eveningCount
            );
        }
        
        return new AvailabilityStats(0L, 0L, 0L, 0L, 0L);
    }

    /**
     * Inner class for availability statistics.
     */
    public static class AvailabilityStats {
        private final long weekdaysCount;
        private final long weekendsCount;
        private final long morningCount;
        private final long afternoonCount;
        private final long eveningCount;

        public AvailabilityStats(long weekdaysCount, long weekendsCount, long morningCount,
                               long afternoonCount, long eveningCount) {
            this.weekdaysCount = weekdaysCount;
            this.weekendsCount = weekendsCount;
            this.morningCount = morningCount;
            this.afternoonCount = afternoonCount;
            this.eveningCount = eveningCount;
        }

        // Getters
        public long getWeekdaysCount() { return weekdaysCount; }
        public long getWeekendsCount() { return weekendsCount; }
        public long getMorningCount() { return morningCount; }
        public long getAfternoonCount() { return afternoonCount; }
        public long getEveningCount() { return eveningCount; }
    }
}