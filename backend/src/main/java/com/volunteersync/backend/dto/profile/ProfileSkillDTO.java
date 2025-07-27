package com.volunteersync.backend.dto.profile;

import com.volunteersync.backend.entity.enums.SkillLevel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for ProfileSkill entities.
 * Represents skills associated with volunteer profiles, including
 * skill level, experience, endorsements, and metadata.
 * 
 * This DTO is used for API responses when working with volunteer skills
 * and includes validation for skill management operations.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileSkillDTO {

    // =====================================================
    // BASIC SKILL INFORMATION
    // =====================================================

    private Long id;

    private Long profileId;

    @NotBlank(message = "Skill name is required")
    @Size(max = 100, message = "Skill name cannot exceed 100 characters")
    private String skillName;

    @Size(max = 50, message = "Skill category cannot exceed 50 characters")
    private String skillCategory; // e.g., "TECHNICAL", "COMMUNICATION", "LEADERSHIP", "MANUAL"

    @NotNull(message = "Skill level is required")
    private SkillLevel skillLevel; // BEGINNER, INTERMEDIATE, ADVANCED, EXPERT

    // =====================================================
    // EXPERIENCE & PROFICIENCY
    // =====================================================

    @Min(value = 0, message = "Years of experience cannot be negative")
    @Max(value = 100, message = "Years of experience cannot exceed 100")
    private Integer yearsOfExperience;

    @Min(value = 0, message = "Months of experience cannot be negative")
    @Max(value = 11, message = "Months of experience cannot exceed 11")
    private Integer monthsOfExperience;

    @Size(max = 500, message = "Skill description cannot exceed 500 characters")
    private String description;

    private Boolean isPrimary; // Whether this is a primary skill for the volunteer

    private Boolean isVisible; // Whether this skill is visible on public profile

    // =====================================================
    // ENDORSEMENTS & VERIFICATION
    // =====================================================

    private Boolean isEndorsed; // Whether the skill has been endorsed by organizations

    @Min(value = 0, message = "Endorsement count cannot be negative")
    private Integer endorsementCount;

    private String lastEndorsedBy; // Name of last organization that endorsed this skill

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastEndorsedAt;

    private Boolean isVerified; // Whether the skill has been verified through testing/certification

    private String verificationSource; // Source of verification (e.g., certification body)

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime verifiedAt;

    // =====================================================
    // USAGE & APPLICATION
    // =====================================================

    @Min(value = 0, message = "Times used cannot be negative")
    private Integer timesUsedInEvents; // Number of events where this skill was utilized

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastUsedAt; // Last time this skill was used in volunteering

    private Boolean seekingOpportunities; // Whether volunteer wants opportunities to use this skill

    private Boolean willingToMentor; // Whether volunteer is willing to mentor others in this skill

    // =====================================================
    // RELATED SKILLS & CONTEXT
    // =====================================================

    private String[] relatedSkills; // Array of related skill names

    private String industryContext; // Industry context where skill was developed

    private String[] certifications; // Related certifications

    private String learningSource; // How/where the skill was learned

    // =====================================================
    // METADATA
    // =====================================================

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private String addedBy; // "SELF", "ORGANIZATION", "IMPORT" - how the skill was added

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public ProfileSkillDTO() {
        // Default constructor for JSON deserialization
    }

    public ProfileSkillDTO(Long id, String skillName, SkillLevel skillLevel, 
                          Integer yearsOfExperience, Boolean isEndorsed, LocalDateTime createdAt) {
        this.id = id;
        this.skillName = skillName;
        this.skillLevel = skillLevel;
        this.yearsOfExperience = yearsOfExperience;
        this.isEndorsed = isEndorsed;
        this.createdAt = createdAt;
    }

    public ProfileSkillDTO(Long id, Long profileId, String skillName, String skillCategory,
                          SkillLevel skillLevel, Integer yearsOfExperience, Boolean isEndorsed,
                          Integer endorsementCount, Boolean isVerified, LocalDateTime createdAt) {
        this.id = id;
        this.profileId = profileId;
        this.skillName = skillName;
        this.skillCategory = skillCategory;
        this.skillLevel = skillLevel;
        this.yearsOfExperience = yearsOfExperience;
        this.isEndorsed = isEndorsed;
        this.endorsementCount = endorsementCount;
        this.isVerified = isVerified;
        this.createdAt = createdAt;
    }

    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getSkillCategory() {
        return skillCategory;
    }

    public void setSkillCategory(String skillCategory) {
        this.skillCategory = skillCategory;
    }

    public SkillLevel getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(SkillLevel skillLevel) {
        this.skillLevel = skillLevel;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public Integer getMonthsOfExperience() {
        return monthsOfExperience;
    }

    public void setMonthsOfExperience(Integer monthsOfExperience) {
        this.monthsOfExperience = monthsOfExperience;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public Boolean getIsEndorsed() {
        return isEndorsed;
    }

    public void setIsEndorsed(Boolean isEndorsed) {
        this.isEndorsed = isEndorsed;
    }

    public Integer getEndorsementCount() {
        return endorsementCount;
    }

    public void setEndorsementCount(Integer endorsementCount) {
        this.endorsementCount = endorsementCount;
    }

    public String getLastEndorsedBy() {
        return lastEndorsedBy;
    }

    public void setLastEndorsedBy(String lastEndorsedBy) {
        this.lastEndorsedBy = lastEndorsedBy;
    }

    public LocalDateTime getLastEndorsedAt() {
        return lastEndorsedAt;
    }

    public void setLastEndorsedAt(LocalDateTime lastEndorsedAt) {
        this.lastEndorsedAt = lastEndorsedAt;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getVerificationSource() {
        return verificationSource;
    }

    public void setVerificationSource(String verificationSource) {
        this.verificationSource = verificationSource;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public Integer getTimesUsedInEvents() {
        return timesUsedInEvents;
    }

    public void setTimesUsedInEvents(Integer timesUsedInEvents) {
        this.timesUsedInEvents = timesUsedInEvents;
    }

    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(LocalDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public Boolean getSeekingOpportunities() {
        return seekingOpportunities;
    }

    public void setSeekingOpportunities(Boolean seekingOpportunities) {
        this.seekingOpportunities = seekingOpportunities;
    }

    public Boolean getWillingToMentor() {
        return willingToMentor;
    }

    public void setWillingToMentor(Boolean willingToMentor) {
        this.willingToMentor = willingToMentor;
    }

    public String[] getRelatedSkills() {
        return relatedSkills;
    }

    public void setRelatedSkills(String[] relatedSkills) {
        this.relatedSkills = relatedSkills;
    }

    public String getIndustryContext() {
        return industryContext;
    }

    public void setIndustryContext(String industryContext) {
        this.industryContext = industryContext;
    }

    public String[] getCertifications() {
        return certifications;
    }

    public void setCertifications(String[] certifications) {
        this.certifications = certifications;
    }

    public String getLearningSource() {
        return learningSource;
    }

    public void setLearningSource(String learningSource) {
        this.learningSource = learningSource;
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

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    /**
     * Gets total experience in months.
     */
    public Integer getTotalExperienceInMonths() {
        int total = 0;
        if (yearsOfExperience != null) {
            total += yearsOfExperience * 12;
        }
        if (monthsOfExperience != null) {
            total += monthsOfExperience;
        }
        return total;
    }

    /**
     * Gets formatted experience string.
     */
    public String getFormattedExperience() {
        if (yearsOfExperience == null && monthsOfExperience == null) {
            return "No experience specified";
        }
        
        StringBuilder sb = new StringBuilder();
        if (yearsOfExperience != null && yearsOfExperience > 0) {
            sb.append(yearsOfExperience).append(" year");
            if (yearsOfExperience > 1) sb.append("s");
        }
        
        if (monthsOfExperience != null && monthsOfExperience > 0) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(monthsOfExperience).append(" month");
            if (monthsOfExperience > 1) sb.append("s");
        }
        
        return sb.toString();
    }

    /**
     * Checks if this skill is considered advanced or expert level.
     */
    public boolean isAdvancedSkill() {
        return skillLevel == SkillLevel.ADVANCED || skillLevel == SkillLevel.EXPERT;
    }

    /**
     * Checks if this skill has been endorsed recently (within last 6 months).
     */
    public boolean hasRecentEndorsement() {
        if (lastEndorsedAt == null) return false;
        return lastEndorsedAt.isAfter(LocalDateTime.now().minusMonths(6));
    }

    /**
     * Gets the skill proficiency as a percentage (0-100).
     */
    public Integer getSkillProficiencyPercentage() {
        if (skillLevel == null) return 0;
        
        return switch (skillLevel) {
            case BEGINNER -> 25;
            case INTERMEDIATE -> 50;
            case ADVANCED -> 75;
            case EXPERT -> 100;
        };
    }

    /**
     * Checks if this skill is ready for mentoring others.
     */
    public boolean canMentorOthers() {
        return willingToMentor != null && willingToMentor && 
               (skillLevel == SkillLevel.ADVANCED || skillLevel == SkillLevel.EXPERT);
    }

    @Override
    public String toString() {
        return "ProfileSkillDTO{" +
                "id=" + id +
                ", skillName='" + skillName + '\'' +
                ", skillLevel=" + skillLevel +
                ", yearsOfExperience=" + yearsOfExperience +
                ", isEndorsed=" + isEndorsed +
                ", endorsementCount=" + endorsementCount +
                ", isVerified=" + isVerified +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ProfileSkillDTO that = (ProfileSkillDTO) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}