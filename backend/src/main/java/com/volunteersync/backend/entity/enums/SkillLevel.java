package com.volunteersync.backend.entity.enums;

/**
 * Enum representing different levels of skill proficiency.
 * Used to categorize user skills and competencies for better matching
 * with volunteer opportunities and organizational needs.
 */
public enum SkillLevel {
    
    /**
     * Basic understanding - can perform simple tasks with guidance
     */
    BEGINNER(
        "Beginner",
        "Basic understanding",
        "🔰",
        1,
        25,
        "Can perform basic tasks with guidance and supervision",
        0,
        6
    ),
    
    /**
     * Moderate competence - can work independently on routine tasks
     */
    INTERMEDIATE(
        "Intermediate", 
        "Moderate competence",
        "📈",
        2,
        50,
        "Can work independently on routine tasks and solve common problems",
        6,
        24
    ),
    
    /**
     * High competence - can handle complex tasks and teach others
     */
    ADVANCED(
        "Advanced",
        "High competence",
        "🎯",
        3,
        75,
        "Can handle complex tasks, solve problems creatively, and guide others",
        24,
        60
    ),
    
    /**
     * Exceptional mastery - recognized expertise and leadership capability
     */
    EXPERT(
        "Expert",
        "Exceptional mastery",
        "🏆",
        4,
        100,
        "Recognized expertise with ability to innovate, lead, and train others",
        60,
        Integer.MAX_VALUE
    );
    
    // =====================================================
    // ENUM PROPERTIES
    // =====================================================
    
    private final String displayName;
    private final String description;
    private final String icon;
    private final int level;
    private final int proficiencyPercentage;
    private final String detailedDescription;
    private final int minMonthsExperience;
    private final int maxMonthsExperience;
    
    // =====================================================
    // CONSTRUCTOR
    // =====================================================
    
    SkillLevel(String displayName, String description, String icon, int level,
              int proficiencyPercentage, String detailedDescription,
              int minMonthsExperience, int maxMonthsExperience) {
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.level = level;
        this.proficiencyPercentage = proficiencyPercentage;
        this.detailedDescription = detailedDescription;
        this.minMonthsExperience = minMonthsExperience;
        this.maxMonthsExperience = maxMonthsExperience;
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
     * Get short description of skill level
     * @return Brief description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Get icon representation for UI
     * @return Emoji representing this skill level
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
     * Get proficiency as percentage for progress bars
     * @return Proficiency percentage (25, 50, 75, 100)
     */
    public int getProficiencyPercentage() {
        return proficiencyPercentage;
    }
    
    /**
     * Get detailed description explaining this skill level
     * @return Detailed description for help text or tooltips
     */
    public String getDetailedDescription() {
        return detailedDescription;
    }
    
    /**
     * Get minimum months of experience typically associated with this level
     * @return Minimum months of experience
     */
    public int getMinMonthsExperience() {
        return minMonthsExperience;
    }
    
    /**
     * Get maximum months of experience typically associated with this level
     * @return Maximum months of experience
     */
    public int getMaxMonthsExperience() {
        return maxMonthsExperience;
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
     * @return String describing typical experience range for this level
     */
    public String getTypicalExperienceRange() {
        if (minMonthsExperience == 0) {
            return "0-" + (maxMonthsExperience / 12) + " year" + (maxMonthsExperience <= 12 ? "" : "s");
        } else if (maxMonthsExperience == Integer.MAX_VALUE) {
            return (minMonthsExperience / 12) + "+ years";
        } else {
            int minYears = minMonthsExperience / 12;
            int maxYears = maxMonthsExperience / 12;
            return minYears + "-" + maxYears + " years";
        }
    }
    
    /**
     * Check if this is entry-level proficiency
     * @return true if BEGINNER level
     */
    public boolean isEntryLevel() {
        return this == BEGINNER;
    }
    
    /**
     * Check if this is advanced proficiency
     * @return true if ADVANCED or EXPERT level
     */
    public boolean isAdvanced() {
        return this == ADVANCED || this == EXPERT;
    }
    
    /**
     * Check if this is expert level proficiency
     * @return true if EXPERT level
     */
    public boolean isExpert() {
        return this == EXPERT;
    }
    
    /**
     * Check if user can mentor others at this skill level
     * @return true if level is sufficient for mentoring (ADVANCED or EXPERT)
     */
    public boolean canMentor() {
        return this == ADVANCED || this == EXPERT;
    }
    
    /**
     * Check if user needs mentoring at this skill level
     * @return true if level benefits from mentoring (BEGINNER or INTERMEDIATE)
     */
    public boolean needsMentoring() {
        return this == BEGINNER || this == INTERMEDIATE;
    }
    
    /**
     * Check if user can lead projects with this skill level
     * @return true if level is sufficient for leadership (EXPERT)
     */
    public boolean canLead() {
        return this == EXPERT;
    }
    
    /**
     * Compare skill levels
     * @param other Another SkillLevel to compare against
     * @return true if this level is higher than the other
     */
    public boolean isHigherThan(SkillLevel other) {
        return this.level > other.level;
    }
    
    /**
     * Compare skill levels
     * @param other Another SkillLevel to compare against
     * @return true if this level is lower than the other
     */
    public boolean isLowerThan(SkillLevel other) {
        return this.level < other.level;
    }
    
    /**
     * Get next skill level
     * @return Next higher skill level, or null if already at highest
     */
    public SkillLevel getNextLevel() {
        return switch (this) {
            case BEGINNER -> INTERMEDIATE;
            case INTERMEDIATE -> ADVANCED;
            case ADVANCED -> EXPERT;
            case EXPERT -> null;
        };
    }
    
    /**
     * Get previous skill level
     * @return Previous lower skill level, or null if already at lowest
     */
    public SkillLevel getPreviousLevel() {
        return switch (this) {
            case BEGINNER -> null;
            case INTERMEDIATE -> BEGINNER;
            case ADVANCED -> INTERMEDIATE;
            case EXPERT -> ADVANCED;
        };
    }
    
    /**
     * Calculate skill level based on months of experience
     * @param monthsOfExperience Total months of experience with this skill
     * @return Appropriate SkillLevel
     */
    public static SkillLevel fromMonthsOfExperience(int monthsOfExperience) {
        for (SkillLevel level : values()) {
            if (monthsOfExperience >= level.minMonthsExperience && 
                monthsOfExperience <= level.maxMonthsExperience) {
                return level;
            }
        }
        return EXPERT; // Default to expert for very high experience
    }
    
    /**
     * Calculate skill level based on years of experience
     * @param yearsOfExperience Total years of experience with this skill
     * @return Appropriate SkillLevel
     */
    public static SkillLevel fromYearsOfExperience(double yearsOfExperience) {
        return fromMonthsOfExperience((int) (yearsOfExperience * 12));
    }
    
    /**
     * Calculate skill level based on self-assessment percentage
     * @param selfAssessment Self-assessed proficiency (0-100)
     * @return Appropriate SkillLevel
     */
    public static SkillLevel fromSelfAssessment(int selfAssessment) {
        if (selfAssessment < 25) return BEGINNER;
        else if (selfAssessment < 50) return BEGINNER;
        else if (selfAssessment < 75) return INTERMEDIATE;
        else if (selfAssessment < 90) return ADVANCED;
        else return EXPERT;
    }
    
    /**
     * Get default skill level for new skills
     * @return Default skill level (BEGINNER)
     */
    public static SkillLevel getDefaultLevel() {
        return BEGINNER;
    }
    
    /**
     * Parse skill level from string (case-insensitive)
     * @param value String value to parse
     * @return SkillLevel enum or null if invalid
     */
    public static SkillLevel fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            return valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            // Try matching by display name
            for (SkillLevel level : values()) {
                if (level.getDisplayName().equalsIgnoreCase(value.trim())) {
                    return level;
                }
            }
            return null;
        }
    }
    
    /**
     * Get skill levels suitable for teaching/training roles
     * @return Array of skill levels appropriate for teaching
     */
    public static SkillLevel[] getTeachingLevels() {
        return new SkillLevel[]{ADVANCED, EXPERT};
    }
    
    /**
     * Get skill levels suitable for mentoring roles
     * @return Array of skill levels appropriate for mentoring
     */
    public static SkillLevel[] getMentoringLevels() {
        return new SkillLevel[]{ADVANCED, EXPERT};
    }
    
    /**
     * Get skill levels that need mentoring
     * @return Array of skill levels that benefit from mentoring
     */
    public static SkillLevel[] getNeedsMentoringLevels() {
        return new SkillLevel[]{BEGINNER, INTERMEDIATE};
    }
    
    /**
     * Get skill levels suitable for leadership roles
     * @return Array of skill levels appropriate for leadership
     */
    public static SkillLevel[] getLeadershipLevels() {
        return new SkillLevel[]{EXPERT};
    }
    
    /**
     * Get skill levels for independent work
     * @return Array of skill levels capable of working independently
     */
    public static SkillLevel[] getIndependentWorkLevels() {
        return new SkillLevel[]{INTERMEDIATE, ADVANCED, EXPERT};
    }
    
    /**
     * Get color associated with this skill level for UI
     * @return Hex color code for visual representation
     */
    public String getColor() {
        return switch (this) {
            case BEGINNER -> "#28A745";    // Green
            case INTERMEDIATE -> "#17A2B8"; // Blue  
            case ADVANCED -> "#6F42C1";    // Purple
            case EXPERT -> "#FFC107";      // Gold
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
            case ADVANCED -> "primary";
            case EXPERT -> "warning";
        };
    }
    
    /**
     * Get CSS class name for styling
     * @return CSS class name based on skill level
     */
    public String getCssClass() {
        return "skill-level-" + name().toLowerCase();
    }
    
    /**
     * Get progress bar CSS class for this level
     * @return CSS class for progress bar styling
     */
    public String getProgressBarClass() {
        return switch (this) {
            case BEGINNER -> "bg-success";
            case INTERMEDIATE -> "bg-info";
            case ADVANCED -> "bg-primary";
            case EXPERT -> "bg-warning";
        };
    }
    
    /**
     * Get typical responsibilities for this skill level
     * @return Array of typical responsibilities and tasks
     */
    public String[] getTypicalResponsibilities() {
        return switch (this) {
            case BEGINNER -> new String[]{
                "Learn basic concepts and procedures",
                "Follow detailed instructions",
                "Perform simple, routine tasks",
                "Ask questions and seek guidance",
                "Shadow experienced team members"
            };
            case INTERMEDIATE -> new String[]{
                "Work independently on routine tasks",
                "Solve common problems",
                "Assist with moderately complex projects",
                "Support team activities",
                "Apply knowledge to new situations"
            };
            case ADVANCED -> new String[]{
                "Handle complex tasks and projects",
                "Mentor beginners and intermediates",
                "Solve challenging problems creatively",
                "Lead small teams or initiatives",
                "Contribute to planning and strategy"
            };
            case EXPERT -> new String[]{
                "Lead major projects and initiatives",
                "Train and develop others",
                "Innovate and improve processes",
                "Make strategic decisions",
                "Serve as subject matter expert"
            };
        };
    }
    
    /**
     * Get learning opportunities for this skill level
     * @return Array of recommended learning activities
     */
    public String[] getLearningOpportunities() {
        return switch (this) {
            case BEGINNER -> new String[]{
                "Basic training sessions",
                "Introductory workshops",
                "Shadowing experienced volunteers",
                "Online tutorials and courses",
                "Hands-on practice with supervision"
            };
            case INTERMEDIATE -> new String[]{
                "Intermediate workshops",
                "Cross-training in related areas",
                "Small project leadership",
                "Peer collaboration",
                "Skill-specific certifications"
            };
            case ADVANCED -> new String[]{
                "Advanced training programs",
                "Conference presentations",
                "Mentoring others",
                "Leading complex projects",
                "Professional development courses"
            };
            case EXPERT -> new String[]{
                "Industry conferences and seminars",
                "Research and innovation projects",
                "Teaching and curriculum development",
                "Strategic planning involvement",
                "Professional recognition programs"
            };
        };
    }
    
    /**
     * Get assessment criteria for this skill level
     * @return Array of criteria used to evaluate this level
     */
    public String[] getAssessmentCriteria() {
        return switch (this) {
            case BEGINNER -> new String[]{
                "Can identify basic concepts",
                "Follows instructions accurately",
                "Completes simple tasks with guidance",
                "Shows willingness to learn",
                "Demonstrates safety awareness"
            };
            case INTERMEDIATE -> new String[]{
                "Works independently on routine tasks",
                "Applies knowledge to solve problems",
                "Communicates effectively with team",
                "Meets quality and time standards",
                "Seeks help when appropriate"
            };
            case ADVANCED -> new String[]{
                "Handles complex tasks successfully",
                "Demonstrates creative problem-solving",
                "Mentors others effectively",
                "Contributes to process improvement",
                "Shows leadership potential"
            };
            case EXPERT -> new String[]{
                "Recognized expertise in field",
                "Innovates and improves practices",
                "Develops others successfully",
                "Makes strategic contributions",
                "Demonstrates thought leadership"
            };
        };
    }
    
    /**
     * Calculate skill development time to next level
     * @return Estimated months to reach next skill level
     */
    public int getEstimatedDevelopmentTime() {
        return switch (this) {
            case BEGINNER -> 6;      // 6 months to intermediate
            case INTERMEDIATE -> 18; // 18 months to advanced
            case ADVANCED -> 36;     // 36 months to expert
            case EXPERT -> 0;        // Already at highest level
        };
    }
    
    /**
     * Get skill level weight for matching algorithms
     * Higher weight indicates more valuable skill level for matching
     * @return Weight value for matching calculations
     */
    public double getMatchingWeight() {
        return switch (this) {
            case BEGINNER -> 1.0;
            case INTERMEDIATE -> 1.5;
            case ADVANCED -> 2.0;
            case EXPERT -> 3.0;
        };
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}