package com.volunteersync.backend.enums;

public enum BadgeType {
    // Hours-based badges
    FIRST_VOLUNTEER("First Volunteer", "Complete your first volunteer activity", 1),
    HELPING_HAND("Helping Hand", "Complete 10 volunteer hours", 10),
    DEDICATED_HELPER("Dedicated Helper", "Complete 50 volunteer hours", 50),
    COMMUNITY_CHAMPION("Community Champion", "Complete 100 volunteer hours", 100),
    VOLUNTEER_HERO("Volunteer Hero", "Complete 500 volunteer hours", 500),
    
    // Event-based badges
    EVENT_STARTER("Event Starter", "Attend your first event", 1),
    REGULAR_VOLUNTEER("Regular Volunteer", "Attend 5 events", 5),
    EVENT_ENTHUSIAST("Event Enthusiast", "Attend 25 events", 25),
    
    // Organization badges (for organizations)
    FIRST_EVENT("First Event", "Host your first event", 1),
    EVENT_ORGANIZER("Event Organizer", "Host 10 events", 10),
    COMMUNITY_BUILDER("Community Builder", "Host 50 events", 50),
    
    // Special achievements
    EARLY_ADOPTER("Early Adopter", "Join VolunteerSync in its first year", 1),
    SOCIAL_BUTTERFLY("Social Butterfly", "Connect with 10 organizations", 10),
    SKILL_SHARER("Skill Sharer", "Complete profile with skills", 1);

    private final String displayName;
    private final String description;
    private final int requiredCount;

    BadgeType(String displayName, String description, int requiredCount) {
        this.displayName = displayName;
        this.description = description;
        this.requiredCount = requiredCount;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getRequiredCount() {
        return requiredCount;
    }

    // Helper methods to categorize badges
    public boolean isHoursBased() {
        return this == FIRST_VOLUNTEER || this == HELPING_HAND || 
               this == DEDICATED_HELPER || this == COMMUNITY_CHAMPION || 
               this == VOLUNTEER_HERO;
    }

    public boolean isEventBased() {
        return this == EVENT_STARTER || this == REGULAR_VOLUNTEER || 
               this == EVENT_ENTHUSIAST || this == FIRST_EVENT || 
               this == EVENT_ORGANIZER || this == COMMUNITY_BUILDER;
    }

    public boolean isForVolunteers() {
        return this == FIRST_VOLUNTEER || this == HELPING_HAND || 
               this == DEDICATED_HELPER || this == COMMUNITY_CHAMPION || 
               this == VOLUNTEER_HERO || this == EVENT_STARTER || 
               this == REGULAR_VOLUNTEER || this == EVENT_ENTHUSIAST ||
               this == SOCIAL_BUTTERFLY || this == SKILL_SHARER || 
               this == EARLY_ADOPTER;
    }

    public boolean isForOrganizations() {
        return this == FIRST_EVENT || this == EVENT_ORGANIZER || 
               this == COMMUNITY_BUILDER || this == EARLY_ADOPTER;
    }
}