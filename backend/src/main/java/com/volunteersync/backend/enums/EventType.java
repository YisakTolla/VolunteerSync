package com.volunteersync.backend.enums;

public enum EventType {
    COMMUNITY_CLEANUP("Community Cleanup", "🧹"),
    FOOD_SERVICE("Food Service", "🍽️"),
    TUTORING_EDUCATION("Tutoring & Education", "📚"),
    ANIMAL_CARE("Animal Care", "🐾"),
    ENVIRONMENTAL_CONSERVATION("Environmental Conservation", "🌱"),
    SENIOR_SUPPORT("Senior Support", "👴"),
    YOUTH_MENTORING("Youth Mentoring", "👥"),
    HEALTHCARE_SUPPORT("Healthcare Support", "🏥"),
    ARTS_CULTURE("Arts & Culture", "🎨"),
    TECHNOLOGY_DIGITAL("Technology & Digital", "💻"),
    DISASTER_RELIEF("Disaster Relief", "🚑"),
    COMMUNITY_BUILDING("Community Building", "🏘️"),
    OTHER("Other", "📋");

    private final String displayName;
    private final String icon;

    EventType(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIcon() {
        return icon;
    }
}
