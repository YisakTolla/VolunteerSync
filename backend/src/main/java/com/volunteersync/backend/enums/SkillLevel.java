package com.volunteersync.backend.enums;

public enum SkillLevel {
    NO_EXPERIENCE_REQUIRED("No Experience Required", 0),
    BEGINNER_FRIENDLY("Beginner Friendly", 1),
    SOME_EXPERIENCE_PREFERRED("Some Experience Preferred", 2),
    EXPERIENCED_VOLUNTEERS("Experienced Volunteers", 3),
    SPECIALIZED_SKILLS_REQUIRED("Specialized Skills Required", 4),
    TRAINING_PROVIDED("Training Provided", 0);

    private final String displayName;
    private final int level;

    SkillLevel(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }
}
