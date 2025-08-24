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
    DISASTER_RELIEF("Disaster Relief", "🚑"),
    ARTS_CULTURE("Arts & Culture", "🎨"),
    SPORTS_RECREATION("Sports & Recreation", "⚽"),
    FUNDRAISING("Fundraising", "💰"),
    ADMINISTRATIVE_SUPPORT("Administrative Support", "📁"),
    CONSTRUCTION_BUILDING("Construction & Building", "🔨"),
    TECHNOLOGY_SUPPORT("Technology Support", "💻"),
    EVENT_PLANNING("Event Planning", "📅"),
    ADVOCACY_AWARENESS("Advocacy & Awareness", "📢"),
    RESEARCH_DATA("Research & Data", "📊"),
    TRANSPORTATION("Transportation", "🚗"),
    GARDENING("Gardening", "🌻"),
    CRISIS_SUPPORT("Crisis Support", "🆘"),
    FESTIVAL_FAIR("Festival & Fair", "🎪"),
    WORKSHOP_TRAINING("Workshop & Training", "🎓"),
    BLOOD_DRIVE("Blood Drive", "🩸"),
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