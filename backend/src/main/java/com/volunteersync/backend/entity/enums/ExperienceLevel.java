package com.volunteersync.backend.entity.enums;

/**
 * Enum representing different levels of volunteer experience.
 * Used to categorize volunteers based on their volunteering background
 * and help organizations find volunteers with appropriate experience levels.
 */
public enum ExperienceLevel {
    
    /**
     * New to volunteering - little to no previous volunteer experience
     */
    BEGINNER(
        "Beginner",
        "New to volunteering",
        "🌱",
        1,
        0,
        12,
        "Perfect for first-time volunteers ready to make a difference"
    ),
    
    /**
     * Some volunteer experience - has volunteered before but not extensively
     */
    INTERMEDIATE(
        "Intermediate",
        "Some volunteer experience",
        "🌿",
        2,
        6,
        36,
        "Has volunteered before and comfortable with basic volunteer activities"
    ),
    
    /**
     * Significant volunteer experience - regularly volunteers and has broad experience
     */
    EXPERIENCED(
        "Experienced",
        "Extensive volunteer background",
        "🌳",
        3,
        24,
        120,
        "Seasoned volunteer with diverse experience across multiple organizations"
    ),
    
    /**
     * Expert level - extensive experience, leadership roles, specialized skills
     */
    EXPERT(
        "Expert",
        "Professional-level expertise",
        "⭐",
        4,
        60,
        Integer.MAX_VALUE,
        "Highly experienced volunteer leader with specialized expertise"
    );
    
    // =====================================================
    // ENUM PROPERTIES
    // =====================================================
    
    private final String displayName;
    private final String description;
    private final String icon;
    private final int level;
    private final int minMonthsExperience;
    private final int maxMonthsExperience;
    private final String detailedDescription;
    
    // =====================================================
    // CONSTRUCTOR
    // =====================================================
    
    ExperienceLevel(String displayName, String description, String icon, int level,
                   int minMonthsExperience, int maxMonthsExperience, String detailedDescription) {
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.level = level;
        this.minMonthsExperience = minMonthsExperience;
        this.maxMonthsExperience = maxMonthsExperience;
        this.detailedDescription = detailedDescription;
    }
    
    // =====================================================
    // GETTER METHODS
    // =====================================================
    
    /**
     * Get user-friendly display name
     * @return Display name for UI
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Get short description of experience level
     * @return Brief description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Get icon representation for UI
     * @return Emoji representing this experience level
     */
    public String getIcon() {
        return icon;
    }
    
    /**
     * Get numeric level for comparisons and sorting
     * @return Level as integer (1-4)
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * Get minimum months of experience for this level
     * @return Minimum months of volunteer experience
     */
    public int getMinMonthsExperience() {
        return minMonthsExperience;
    }
    
    /**
     * Get maximum months of experience for this level
     * @return Maximum months of volunteer experience
     */
    public int getMaxMonthsExperience() {
        return maxMonthsExperience;
    }
    
    /**
     * Get detailed description explaining this experience level
     * @return Detailed description for help text or tooltips
     */
    public String getDetailedDescription() {
        return detailedDescription;
    }
    
    // =====================================================
    // UTILITY METHODS
    // =====================================================
    
    /**
     * Get formatted display with icon and name
     * @return Formatted string for UI display
     */
    public String getFormattedDisplay() {
        return icon + " " + displayName;
    }
    
    /**
     * Get experience range as human-readable string
     * @return String describing the experience range
     */
    public String getExperienceRange() {
        if (minMonthsExperience == 0 && maxMonthsExperience <= 12) {
            return "0-1 year";
        } else if (maxMonthsExperience == Integer.MAX_VALUE) {
            return minMonthsExperience / 12 + "+ years";
        } else {
            int minYears = minMonthsExperience / 12;
            int maxYears = maxMonthsExperience / 12;
            if (minYears == maxYears) {
                return minYears + " year" + (minYears != 1 ? "s" : "");
            }
            return minYears + "-" + maxYears + " years";
        }
    }
    
    /**
     * Check if this is entry-level experience
     * @return true if BEGINNER level
     */
    public boolean isEntryLevel() {
        return this == BEGINNER;
    }
    
    /**
     * Check if this is advanced experience
     * @return true if EXPERIENCED or EXPERT level
     */
    public boolean isAdvanced() {
        return this == EXPERIENCED || this == EXPERT;
    }
    
    /**
     * Check if this is expert level
     * @return true if EXPERT level
     */
    public boolean isExpert() {
        return this == EXPERT;
    }
    
    /**
     * Compare experience levels
     * @param other Another ExperienceLevel to compare against
     * @return true if this level is higher than the other
     */
    public boolean isHigherThan(ExperienceLevel other) {
        return this.level > other.level;
    }
    
    /**
     * Compare experience levels
     * @param other Another ExperienceLevel to compare against
     * @return true if this level is lower than the other
     */
    public boolean isLowerThan(ExperienceLevel other) {
        return this.level < other.level;
    }
    
    /**
     * Get next experience level
     * @return Next higher experience level, or null if already at highest
     */
    public ExperienceLevel getNextLevel() {
        return switch (this) {
            case BEGINNER -> INTERMEDIATE;
            case INTERMEDIATE -> EXPERIENCED;
            case EXPERIENCED -> EXPERT;
            case EXPERT -> null;
        };
    }
    
    /**
     * Get previous experience level
     * @return Previous lower experience level, or null if already at lowest
     */
    public ExperienceLevel getPreviousLevel() {
        return switch (this) {
            case BEGINNER -> null;
            case INTERMEDIATE -> BEGINNER;
            case EXPERIENCED -> INTERMEDIATE;
            case EXPERT -> EXPERIENCED;
        };
    }
    
    /**
     * Calculate experience level based on months of volunteer experience
     * @param monthsOfExperience Total months of volunteer experience
     * @return Appropriate ExperienceLevel
     */
    public static ExperienceLevel fromMonthsOfExperience(int monthsOfExperience) {
        for (ExperienceLevel level : values()) {
            if (monthsOfExperience >= level.minMonthsExperience && 
                monthsOfExperience <= level.maxMonthsExperience) {
                return level;
            }
        }
        return EXPERT; // Default to expert for very high experience
    }
    
    /**
     * Calculate experience level based on years of volunteer experience
     * @param yearsOfExperience Total years of volunteer experience
     * @return Appropriate ExperienceLevel
     */
    public static ExperienceLevel fromYearsOfExperience(double yearsOfExperience) {
        return fromMonthsOfExperience((int) (yearsOfExperience * 12));
    }
    
    /**
     * Get default experience level for new volunteers
     * @return Default experience level (BEGINNER)
     */
    public static ExperienceLevel getDefaultLevel() {
        return BEGINNER;
    }
    
    /**
     * Parse experience level from string (case-insensitive)
     * @param value String value to parse
     * @return ExperienceLevel enum or null if invalid
     */
    public static ExperienceLevel fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            return valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            // Try matching by display name
            for (ExperienceLevel level : values()) {
                if (level.getDisplayName().equalsIgnoreCase(value.trim())) {
                    return level;
                }
            }
            return null;
        }
    }
    
    /**
     * Get experience levels suitable for leadership roles
     * @return Array of experience levels appropriate for leadership
     */
    public static ExperienceLevel[] getLeadershipLevels() {
        return new ExperienceLevel[]{EXPERIENCED, EXPERT};
    }
    
    /**
     * Get experience levels suitable for mentoring roles
     * @return Array of experience levels appropriate for mentoring
     */
    public static ExperienceLevel[] getMentoringLevels() {
        return new ExperienceLevel[]{INTERMEDIATE, EXPERIENCED, EXPERT};
    }
    
    /**
     * Get experience levels that need mentoring
     * @return Array of experience levels that benefit from mentoring
     */
    public static ExperienceLevel[] getNeedsMentoringLevels() {
        return new ExperienceLevel[]{BEGINNER, INTERMEDIATE};
    }
    
    /**
     * Get color associated with this experience level for UI
     * @return Hex color code for visual representation
     */
    public String getColor() {
        return switch (this) {
            case BEGINNER -> "#28A745";    // Green
            case INTERMEDIATE -> "#17A2B8"; // Blue
            case EXPERIENCED -> "#6F42C1";  // Purple
            case EXPERT -> "#FFC107";       // Gold
        };
    }
    
    /**
     * Get bootstrap color variant for UI elements
     * @return Bootstrap color class
     */
    public String getBootstrapColor() {
        return switch (this) {
            case BEGINNER -> "success";
            case INTERMEDIATE -> "info";
            case EXPERIENCED -> "primary";
            case EXPERT -> "warning";
        };
    }
    
    /**
     * Get CSS class name for styling
     * @return CSS class name based on experience level
     */
    public String getCssClass() {
        return "experience-" + name().toLowerCase();
    }
    
    /**
     * Get recommended opportunity types for this experience level
     * @return Array of opportunity types suitable for this level
     */
    public String[] getRecommendedOpportunityTypes() {
        return switch (this) {
            case BEGINNER -> new String[]{
                "Orientation sessions", "Group activities", "Simple tasks", 
                "Supervised work", "Training programs"
            };
            case INTERMEDIATE -> new String[]{
                "Independent tasks", "Team projects", "Event assistance", 
                "Regular commitments", "Skill-based volunteering"
            };
            case EXPERIENCED -> new String[]{
                "Project leadership", "Training others", "Complex projects", 
                "Multi-day events", "Specialized roles"
            };
            case EXPERT -> new String[]{
                "Program management", "Strategic planning", "Board positions", 
                "Consulting roles", "Mentoring coordinators"
            };
        };
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}