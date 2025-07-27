package com.volunteersync.backend.dto.request;

import com.volunteersync.backend.entity.enums.SkillLevel;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for adding skills to volunteer profiles.
 * Contains all necessary information to create a new ProfileSkill entity
 * including skill details, experience level, and preferences.
 * 
 * This request DTO includes comprehensive validation for skill creation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddSkillRequest {

    // =====================================================
    // REQUIRED SKILL INFORMATION
    // =====================================================

    @NotBlank(message = "Skill name is required")
    @Size(max = 100, message = "Skill name cannot exceed 100 characters")
    private String skillName;

    @NotNull(message = "Skill level is required")
    private SkillLevel skillLevel; // BEGINNER, INTERMEDIATE, ADVANCED, EXPERT

    // =====================================================
    // OPTIONAL SKILL DETAILS
    // =====================================================

    @Size(max = 50, message = "Skill category cannot exceed 50 characters")
    private String skillCategory; // e.g., "TECHNICAL", "COMMUNICATION", "LEADERSHIP", "MANUAL"

    @Size(max = 500, message = "Skill description cannot exceed 500 characters")
    private String description;

    // =====================================================
    // EXPERIENCE INFORMATION
    // =====================================================

    @Min(value = 0, message = "Years of experience cannot be negative")
    @Max(value = 100, message = "Years of experience cannot exceed 100")
    private Integer yearsOfExperience;

    @Min(value = 0, message = "Months of experience cannot be negative")
    @Max(value = 11, message = "Months of experience cannot exceed 11")
    private Integer monthsOfExperience;

    @Size(max = 100, message = "Industry context cannot exceed 100 characters")
    private String industryContext; // Industry where skill was developed

    @Size(max = 200, message = "Learning source cannot exceed 200 characters")
    private String learningSource; // How/where the skill was learned

    // =====================================================
    // SKILL PREFERENCES & SETTINGS
    // =====================================================

    private Boolean isPrimary; // Whether this is a primary skill for the volunteer

    private Boolean isVisible; // Whether this skill should be visible on public profile

    private Boolean seekingOpportunities; // Whether volunteer wants opportunities to use this skill

    private Boolean willingToMentor; // Whether volunteer is willing to mentor others in this skill

    // =====================================================
    // RELATED INFORMATION
    // =====================================================

    private String[] relatedSkills; // Array of related skill names

    private String[] certifications; // Related certifications

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public AddSkillRequest() {
        // Default constructor for JSON deserialization
    }

    public AddSkillRequest(String skillName, SkillLevel skillLevel) {
        this.skillName = skillName;
        this.skillLevel = skillLevel;
    }

    public AddSkillRequest(String skillName, SkillLevel skillLevel, String skillCategory,
                          Integer yearsOfExperience, String description) {
        this.skillName = skillName;
        this.skillLevel = skillLevel;
        this.skillCategory = skillCategory;
        this.yearsOfExperience = yearsOfExperience;
        this.description = description;
    }

    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public SkillLevel getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(SkillLevel skillLevel) {
        this.skillLevel = skillLevel;
    }

    public String getSkillCategory() {
        return skillCategory;
    }

    public void setSkillCategory(String skillCategory) {
        this.skillCategory = skillCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getIndustryContext() {
        return industryContext;
    }

    public void setIndustryContext(String industryContext) {
        this.industryContext = industryContext;
    }

    public String getLearningSource() {
        return learningSource;
    }

    public void setLearningSource(String learningSource) {
        this.learningSource = learningSource;
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

    public String[] getCertifications() {
        return certifications;
    }

    public void setCertifications(String[] certifications) {
        this.certifications = certifications;
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
     * Validates that the skill name is not just whitespace.
     */
    public boolean isValidSkillName() {
        return skillName != null && !skillName.trim().isEmpty();
    }

    /**
     * Normalizes the skill name by trimming and capitalizing properly.
     */
    public void normalizeSkillName() {
        if (skillName != null) {
            skillName = skillName.trim();
            // Capitalize first letter of each word
            String[] words = skillName.toLowerCase().split("\\s+");
            StringBuilder normalized = new StringBuilder();
            for (String word : words) {
                if (word.length() > 0) {
                    if (normalized.length() > 0) normalized.append(" ");
                    normalized.append(Character.toUpperCase(word.charAt(0)));
                    if (word.length() > 1) {
                        normalized.append(word.substring(1));
                    }
                }
            }
            skillName = normalized.toString();
        }
    }

    /**
     * Sets default values for optional fields if not provided.
     */
    public void setDefaults() {
        if (isVisible == null) {
            isVisible = true; // Default to visible
        }
        if (isPrimary == null) {
            isPrimary = false; // Default to not primary
        }
        if (seekingOpportunities == null) {
            seekingOpportunities = true; // Default to seeking opportunities
        }
        if (willingToMentor == null) {
            willingToMentor = isAdvancedSkill(); // Default based on skill level
        }
        if (skillCategory == null || skillCategory.trim().isEmpty()) {
            skillCategory = "GENERAL"; // Default category
        }
    }

    /**
     * Validates that experience values are consistent with skill level.
     */
    public boolean isExperienceConsistentWithLevel() {
        if (skillLevel == null) return true; // Can't validate without skill level
        
        int totalMonths = getTotalExperienceInMonths();
        
        return switch (skillLevel) {
            case BEGINNER -> totalMonths <= 12; // Up to 1 year for beginner
            case INTERMEDIATE -> totalMonths >= 6 && totalMonths <= 36; // 6 months to 3 years
            case ADVANCED -> totalMonths >= 24; // At least 2 years for advanced
            case EXPERT -> totalMonths >= 60; // At least 5 years for expert
        };
    }

    /**
     * Gets suggested skill level based on experience.
     */
    public SkillLevel getSuggestedSkillLevel() {
        int totalMonths = getTotalExperienceInMonths();
        
        if (totalMonths >= 60) return SkillLevel.EXPERT;
        if (totalMonths >= 24) return SkillLevel.ADVANCED;
        if (totalMonths >= 6) return SkillLevel.INTERMEDIATE;
        return SkillLevel.BEGINNER;
    }

    /**
     * Checks if the skill has certifications.
     */
    public boolean hasCertifications() {
        return certifications != null && certifications.length > 0;
    }

    /**
     * Checks if the skill has related skills.
     */
    public boolean hasRelatedSkills() {
        return relatedSkills != null && relatedSkills.length > 0;
    }

    /**
     * Gets the number of related skills.
     */
    public int getRelatedSkillCount() {
        return relatedSkills != null ? relatedSkills.length : 0;
    }

    /**
     * Gets the number of certifications.
     */
    public int getCertificationCount() {
        return certifications != null ? certifications.length : 0;
    }

    /**
     * Validates that all array fields contain non-empty values.
     */
    public boolean areArrayFieldsValid() {
        // Check related skills
        if (relatedSkills != null) {
            for (String skill : relatedSkills) {
                if (skill == null || skill.trim().isEmpty()) {
                    return false;
                }
            }
        }
        
        // Check certifications
        if (certifications != null) {
            for (String cert : certifications) {
                if (cert == null || cert.trim().isEmpty()) {
                    return false;
                }
            }
        }
        
        return true;
    }

    /**
     * Cleans up array fields by removing null/empty values.
     */
    public void cleanArrayFields() {
        if (relatedSkills != null) {
            relatedSkills = java.util.Arrays.stream(relatedSkills)
                    .filter(skill -> skill != null && !skill.trim().isEmpty())
                    .map(String::trim)
                    .toArray(String[]::new);
        }
        
        if (certifications != null) {
            certifications = java.util.Arrays.stream(certifications)
                    .filter(cert -> cert != null && !cert.trim().isEmpty())
                    .map(String::trim)
                    .toArray(String[]::new);
        }
    }

    @Override
    public String toString() {
        return "AddSkillRequest{" +
                "skillName='" + skillName + '\'' +
                ", skillLevel=" + skillLevel +
                ", skillCategory='" + skillCategory + '\'' +
                ", yearsOfExperience=" + yearsOfExperience +
                ", monthsOfExperience=" + monthsOfExperience +
                ", isPrimary=" + isPrimary +
                ", isVisible=" + isVisible +
                ", seekingOpportunities=" + seekingOpportunities +
                ", willingToMentor=" + willingToMentor +
                ", relatedSkillCount=" + getRelatedSkillCount() +
                ", certificationCount=" + getCertificationCount() +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AddSkillRequest that = (AddSkillRequest) obj;
        return skillName != null && skillName.equals(that.skillName) &&
               skillLevel == that.skillLevel;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(skillName, skillLevel);
    }
}