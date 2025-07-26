package com.volunteersync.backend.entity.profile;

import com.volunteersync.backend.entity.enums.SkillLevel;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

/**
 * Entity representing a skill associated with a user profile.
 * Skills can be added by volunteers and organizations to showcase their capabilities,
 * experience, and areas of expertise for better matching with opportunities.
 */
@Entity
@Table(name = "profile_skills", 
       indexes = {
           @Index(name = "idx_profile_skill_name", columnList = "profile_id, skill_name"),
           @Index(name = "idx_skill_category", columnList = "category"),
           @Index(name = "idx_skill_level", columnList = "level"),
           @Index(name = "idx_skill_verified", columnList = "verified"),
           @Index(name = "idx_skill_public", columnList = "is_public")
       },
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"profile_id", "skill_name"})
       })
public class ProfileSkill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Many-to-One relationship with Profile
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
    
    // =====================================================
    // CORE SKILL INFORMATION
    // =====================================================
    
    @Column(name = "skill_name", nullable = false, length = 100)
    private String skillName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkillLevel level;
    
    @Column(length = 50)
    private String category; // "Technical", "Communication", "Leadership", "Healthcare", "Creative", etc.
    
    @Column(columnDefinition = "TEXT")
    private String description; // Detailed description of the skill and how it's applied
    
    // =====================================================
    // EXPERIENCE & PROFICIENCY
    // =====================================================
    
    @Column
    private Integer yearsOfExperience;
    
    @Column
    private String lastUsedAt; // "Currently", "2023", "1-2 years ago", etc.
    
    @Column(columnDefinition = "TEXT")
    private String usageContext; // Where/how this skill was acquired or used
    
    @Column(columnDefinition = "TEXT")
    private String achievements; // Specific achievements or projects using this skill
    
    // =====================================================
    // VERIFICATION & ENDORSEMENTS
    // =====================================================
    
    @Column(nullable = false)
    private Boolean verified = false; // If skill has been verified/endorsed
    
    @Column
    private String verifiedBy; // Who verified this skill (organization, supervisor, peer, etc.)
    
    @Column
    private LocalDateTime verifiedAt;
    
    @Column(columnDefinition = "TEXT")
    private String verificationNotes; // Additional verification details
    
    @Column(nullable = false)
    private Integer endorsementCount = 0; // Number of endorsements from other users
    
    // =====================================================
    // CERTIFICATIONS & CREDENTIALS
    // =====================================================
    
    @Column
    private String certificationName; // Name of certification if applicable
    
    @Column
    private String certificationProvider; // Organization that provided certification
    
    @Column
    private LocalDateTime certificationDate;
    
    @Column
    private LocalDateTime certificationExpiry;
    
    @Column
    private String certificationUrl; // Link to verify certification or badge
    
    @Column
    private String certificationId; // Certification ID or number
    
    // =====================================================
    // SKILL PREFERENCES & AVAILABILITY
    // =====================================================
    
    @Column(nullable = false)
    private Boolean willingToTeach = false; // Can mentor others or teach this skill
    
    @Column(nullable = false)
    private Boolean lookingToImprove = false; // Wants to develop this skill further
    
    @Column(nullable = false)
    private Boolean willingToVolunteer = true; // Available to use this skill for volunteering
    
    @Column
    private String preferredUseContext; // "Remote", "In-person", "Emergency response", etc.
    
    @Column(columnDefinition = "TEXT")
    private String availabilityNotes; // When/how this skill can be utilized
    
    // =====================================================
    // VISIBILITY & STATUS
    // =====================================================
    
    @Column(nullable = false)
    private Boolean isPublic = true; // Whether this skill is visible to organizations
    
    @Column(nullable = false)
    private Boolean isActive = true; // Whether this skill is currently relevant/maintained
    
    @Column(nullable = false)
    private Boolean isFeatured = false; // Whether to highlight this skill on profile
    
    // =====================================================
    // MATCHING & DISCOVERY
    // =====================================================
    
    @Column(columnDefinition = "TEXT")
    private String keywords; // Additional keywords for better matching (comma-separated)
    
    @Column
    private String relatedSkills; // Related or complementary skills (comma-separated)
    
    @Column
    private Integer opportunitiesMatched = 0; // Number of opportunities this skill has matched
    
    @Column
    private LocalDateTime lastMatchedAt; // Last time this skill was matched to an opportunity
    
    // =====================================================
    // METADATA & TRACKING
    // =====================================================
    
    @Column
    private String sourceType; // "Self-reported", "Import", "Assessment", "Verified"
    
    @Column
    private Integer profileViews = 0; // How many times this skill was viewed by others
    
    @Column
    private LocalDateTime lastUpdatedBy; // When user last updated this skill
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // =====================================================
    // CONSTRUCTORS
    // =====================================================
    
    public ProfileSkill() {
        // Default constructor for JPA
    }
    
    public ProfileSkill(Profile profile, String skillName, SkillLevel level) {
        this.profile = profile;
        this.skillName = skillName;
        this.level = level;
        setDefaults();
    }
    
    public ProfileSkill(Profile profile, String skillName, SkillLevel level, String category) {
        this.profile = profile;
        this.skillName = skillName;
        this.level = level;
        this.category = category;
        setDefaults();
    }
    
    // =====================================================
    // HELPER METHODS
    // =====================================================
    
    /**
     * Set default values for new skill entries
     */
    private void setDefaults() {
        this.verified = false;
        this.endorsementCount = 0;
        this.willingToTeach = false;
        this.lookingToImprove = false;
        this.willingToVolunteer = true;
        this.isPublic = true;
        this.isActive = true;
        this.isFeatured = false;
        this.opportunitiesMatched = 0;
        this.sourceType = "Self-reported";
        this.profileViews = 0;
    }
    
    /**
     * Check if the skill has an active certification
     * @return true if certified and certification hasn't expired
     */
    public boolean hasActiveCertification() {
        if (certificationName == null || certificationName.trim().isEmpty()) {
            return false;
        }
        
        if (certificationExpiry != null) {
            return certificationExpiry.isAfter(LocalDateTime.now());
        }
        
        return true; // No expiry date means certification doesn't expire
    }
    
    /**
     * Check if the skill verification is current (within last 2 years)
     * @return true if verified within reasonable timeframe
     */
    public boolean hasCurrentVerification() {
        if (!Boolean.TRUE.equals(verified)) {
            return false;
        }
        
        if (verifiedAt != null) {
            return verifiedAt.isAfter(LocalDateTime.now().minusYears(2));
        }
        
        return false;
    }
    
    /**
     * Get display text for skill level
     * @return User-friendly skill level description
     */
    public String getSkillLevelDisplay() {
        if (level != null) {
            return level.getDisplayName();
        }
        return "Not specified";
    }
    
    /**
     * Get skill proficiency as percentage (for UI progress bars)
     * @return Proficiency percentage (0-100)
     */
    public int getProficiencyPercentage() {
        if (level != null) {
            return switch (level) {
                case BEGINNER -> 25;
                case INTERMEDIATE -> 50;
                case ADVANCED -> 75;
                case EXPERT -> 100;
            };
        }
        return 0;
    }
    
    /**
     * Check if certification is expiring soon (within 30 days)
     * @return true if certification expires within 30 days
     */
    public boolean isCertificationExpiringSoon() {
        if (certificationExpiry != null) {
            return certificationExpiry.isBefore(LocalDateTime.now().plusDays(30));
        }
        return false;
    }
    
    /**
     * Get formatted certification info for display
     * @return Formatted string with certification details
     */
    public String getFormattedCertification() {
        if (certificationName == null || certificationName.trim().isEmpty()) {
            return null;
        }
        
        StringBuilder cert = new StringBuilder(certificationName.trim());
        
        if (certificationProvider != null && !certificationProvider.trim().isEmpty()) {
            cert.append(" (").append(certificationProvider.trim()).append(")");
        }
        
        return cert.toString();
    }
    
    /**
     * Calculate skill score based on level, experience, verification, etc.
     * @return Skill score (0-100) for ranking/matching purposes
     */
    public int calculateSkillScore() {
        int score = 0;
        
        // Base score from skill level
        if (level != null) {
            score += level.getLevel() * 20; // 20, 40, 60, 80
        }
        
        // Experience bonus
        if (yearsOfExperience != null) {
            score += Math.min(yearsOfExperience * 2, 10); // Up to 10 points for experience
        }
        
        // Verification bonus
        if (Boolean.TRUE.equals(verified)) {
            score += 5;
        }
        
        // Certification bonus
        if (hasActiveCertification()) {
            score += 5;
        }
        
        // Endorsement bonus
        if (endorsementCount != null && endorsementCount > 0) {
            score += Math.min(endorsementCount, 5); // Up to 5 points for endorsements
        }
        
        return Math.min(score, 100); // Cap at 100
    }
    
    /**
     * Check if skill matches search criteria
     * @param searchTerm Search term to match against
     * @return true if skill matches the search
     */
    public boolean matchesSearch(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return false;
        }
        
        String searchLower = searchTerm.toLowerCase();
        
        // Check skill name
        if (skillName != null && skillName.toLowerCase().contains(searchLower)) {
            return true;
        }
        
        // Check category
        if (category != null && category.toLowerCase().contains(searchLower)) {
            return true;
        }
        
        // Check keywords
        if (keywords != null && keywords.toLowerCase().contains(searchLower)) {
            return true;
        }
        
        // Check description
        if (description != null && description.toLowerCase().contains(searchLower)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Increment profile views counter
     */
    public void incrementViews() {
        if (this.profileViews == null) {
            this.profileViews = 0;
        }
        this.profileViews++;
    }
    
    /**
     * Record that this skill was matched to an opportunity
     */
    public void recordOpportunityMatch() {
        if (this.opportunitiesMatched == null) {
            this.opportunitiesMatched = 0;
        }
        this.opportunitiesMatched++;
        this.lastMatchedAt = LocalDateTime.now();
    }
    
    /**
     * Add an endorsement to this skill
     */
    public void addEndorsement() {
        if (this.endorsementCount == null) {
            this.endorsementCount = 0;
        }
        this.endorsementCount++;
    }
    
    /**
     * Remove an endorsement from this skill
     */
    public void removeEndorsement() {
        if (this.endorsementCount != null && this.endorsementCount > 0) {
            this.endorsementCount--;
        }
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
    
    public Profile getProfile() {
        return profile;
    }
    
    public void setProfile(Profile profile) {
        this.profile = profile;
    }
    
    public String getSkillName() {
        return skillName;
    }
    
    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }
    
    public SkillLevel getLevel() {
        return level;
    }
    
    public void setLevel(SkillLevel level) {
        this.level = level;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
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
    
    public String getLastUsedAt() {
        return lastUsedAt;
    }
    
    public void setLastUsedAt(String lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }
    
    public String getUsageContext() {
        return usageContext;
    }
    
    public void setUsageContext(String usageContext) {
        this.usageContext = usageContext;
    }
    
    public String getAchievements() {
        return achievements;
    }
    
    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }
    
    public Boolean getVerified() {
        return verified;
    }
    
    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
    
    public String getVerifiedBy() {
        return verifiedBy;
    }
    
    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }
    
    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }
    
    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
    
    public String getVerificationNotes() {
        return verificationNotes;
    }
    
    public void setVerificationNotes(String verificationNotes) {
        this.verificationNotes = verificationNotes;
    }
    
    public Integer getEndorsementCount() {
        return endorsementCount;
    }
    
    public void setEndorsementCount(Integer endorsementCount) {
        this.endorsementCount = endorsementCount;
    }
    
    public String getCertificationName() {
        return certificationName;
    }
    
    public void setCertificationName(String certificationName) {
        this.certificationName = certificationName;
    }
    
    public String getCertificationProvider() {
        return certificationProvider;
    }
    
    public void setCertificationProvider(String certificationProvider) {
        this.certificationProvider = certificationProvider;
    }
    
    public LocalDateTime getCertificationDate() {
        return certificationDate;
    }
    
    public void setCertificationDate(LocalDateTime certificationDate) {
        this.certificationDate = certificationDate;
    }
    
    public LocalDateTime getCertificationExpiry() {
        return certificationExpiry;
    }
    
    public void setCertificationExpiry(LocalDateTime certificationExpiry) {
        this.certificationExpiry = certificationExpiry;
    }
    
    public String getCertificationUrl() {
        return certificationUrl;
    }
    
    public void setCertificationUrl(String certificationUrl) {
        this.certificationUrl = certificationUrl;
    }
    
    public String getCertificationId() {
        return certificationId;
    }
    
    public void setCertificationId(String certificationId) {
        this.certificationId = certificationId;
    }
    
    public Boolean getWillingToTeach() {
        return willingToTeach;
    }
    
    public void setWillingToTeach(Boolean willingToTeach) {
        this.willingToTeach = willingToTeach;
    }
    
    public Boolean getLookingToImprove() {
        return lookingToImprove;
    }
    
    public void setLookingToImprove(Boolean lookingToImprove) {
        this.lookingToImprove = lookingToImprove;
    }
    
    public Boolean getWillingToVolunteer() {
        return willingToVolunteer;
    }
    
    public void setWillingToVolunteer(Boolean willingToVolunteer) {
        this.willingToVolunteer = willingToVolunteer;
    }
    
    public String getPreferredUseContext() {
        return preferredUseContext;
    }
    
    public void setPreferredUseContext(String preferredUseContext) {
        this.preferredUseContext = preferredUseContext;
    }
    
    public String getAvailabilityNotes() {
        return availabilityNotes;
    }
    
    public void setAvailabilityNotes(String availabilityNotes) {
        this.availabilityNotes = availabilityNotes;
    }
    
    public Boolean getIsPublic() {
        return isPublic;
    }
    
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Boolean getIsFeatured() {
        return isFeatured;
    }
    
    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }
    
    public String getKeywords() {
        return keywords;
    }
    
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
    
    public String getRelatedSkills() {
        return relatedSkills;
    }
    
    public void setRelatedSkills(String relatedSkills) {
        this.relatedSkills = relatedSkills;
    }
    
    public Integer getOpportunitiesMatched() {
        return opportunitiesMatched;
    }
    
    public void setOpportunitiesMatched(Integer opportunitiesMatched) {
        this.opportunitiesMatched = opportunitiesMatched;
    }
    
    public LocalDateTime getLastMatchedAt() {
        return lastMatchedAt;
    }
    
    public void setLastMatchedAt(LocalDateTime lastMatchedAt) {
        this.lastMatchedAt = lastMatchedAt;
    }
    
    public String getSourceType() {
        return sourceType;
    }
    
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
    
    public Integer getProfileViews() {
        return profileViews;
    }
    
    public void setProfileViews(Integer profileViews) {
        this.profileViews = profileViews;
    }
    
    public LocalDateTime getLastUpdatedBy() {
        return lastUpdatedBy;
    }
    
    public void setLastUpdatedBy(LocalDateTime lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
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
    // EQUALS, HASHCODE, AND TOSTRING
    // =====================================================
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ProfileSkill that = (ProfileSkill) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "ProfileSkill{" +
                "id=" + id +
                ", skillName='" + skillName + '\'' +
                ", level=" + level +
                ", category='" + category + '\'' +
                ", verified=" + verified +
                ", yearsOfExperience=" + yearsOfExperience +
                ", endorsementCount=" + endorsementCount +
                ", createdAt=" + createdAt +
                '}';
    }
}