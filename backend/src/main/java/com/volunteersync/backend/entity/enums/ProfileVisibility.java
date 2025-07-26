package com.volunteersync.backend.entity.enums;

/**
 * Enum representing different visibility levels for user profiles.
 * Controls who can view profile information and under what circumstances.
 */
public enum ProfileVisibility {
    
    /**
     * Profile is visible to everyone, including anonymous users
     */
    PUBLIC(
        "Public", 
        "Anyone can view your profile",
        "🌐",
        true,
        true,
        true
    ),
    
    /**
     * Profile is only visible to connected users (friends, connections)
     */
    CONNECTIONS_ONLY(
        "Connections Only", 
        "Only your connections can view your profile",
        "👥",
        false,
        true,
        false
    ),
    
    /**
     * Profile is completely private - only visible to the user themselves
     */
    PRIVATE(
        "Private", 
        "Only you can view your profile",
        "🔒",
        false,
        false,
        false
    );
    
    // =====================================================
    // ENUM PROPERTIES
    // =====================================================
    
    private final String displayName;
    private final String description;
    private final String icon;
    private final boolean visibleToPublic;
    private final boolean visibleToConnections;
    private final boolean searchable;
    
    // =====================================================
    // CONSTRUCTOR
    // =====================================================
    
    ProfileVisibility(String displayName, String description, String icon, 
                     boolean visibleToPublic, boolean visibleToConnections, boolean searchable) {
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.visibleToPublic = visibleToPublic;
        this.visibleToConnections = visibleToConnections;
        this.searchable = searchable;
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
     * Get detailed description of visibility level
     * @return Description explaining what this visibility means
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Get icon representation for UI
     * @return Emoji or icon representing this visibility level
     */
    public String getIcon() {
        return icon;
    }
    
    /**
     * Check if profile is visible to public/anonymous users
     * @return true if public can view this profile
     */
    public boolean isVisibleToPublic() {
        return visibleToPublic;
    }
    
    /**
     * Check if profile is visible to connected users
     * @return true if connections can view this profile
     */
    public boolean isVisibleToConnections() {
        return visibleToConnections;
    }
    
    /**
     * Check if profile appears in search results
     * @return true if profile can be found in searches
     */
    public boolean isSearchable() {
        return searchable;
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
     * Check if this is the most restrictive visibility level
     * @return true if this is PRIVATE
     */
    public boolean isPrivate() {
        return this == PRIVATE;
    }
    
    /**
     * Check if this is the most open visibility level
     * @return true if this is PUBLIC
     */
    public boolean isPublic() {
        return this == PUBLIC;
    }
    
    /**
     * Check if visibility requires some form of connection/relationship
     * @return true if visibility is restricted to connections
     */
    public boolean requiresConnection() {
        return this == CONNECTIONS_ONLY;
    }
    
    /**
     * Get privacy level as numeric value (higher = more private)
     * @return Privacy level (1-3)
     */
    public int getPrivacyLevel() {
        return switch (this) {
            case PUBLIC -> 1;
            case CONNECTIONS_ONLY -> 2;
            case PRIVATE -> 3;
        };
    }
    
    /**
     * Compare privacy levels
     * @param other Another ProfileVisibility to compare against
     * @return true if this visibility is more private than the other
     */
    public boolean isMorePrivateThan(ProfileVisibility other) {
        return this.getPrivacyLevel() > other.getPrivacyLevel();
    }
    
    /**
     * Compare privacy levels
     * @param other Another ProfileVisibility to compare against
     * @return true if this visibility is more public than the other
     */
    public boolean isMorePublicThan(ProfileVisibility other) {
        return this.getPrivacyLevel() < other.getPrivacyLevel();
    }
    
    /**
     * Get recommended visibility for new users
     * @return Default visibility setting for new profiles
     */
    public static ProfileVisibility getDefaultVisibility() {
        return PUBLIC;
    }
    
    /**
     * Get all visibility options as array for UI dropdowns
     * @return Array of all ProfileVisibility values
     */
    public static ProfileVisibility[] getAllOptions() {
        return values();
    }
    
    /**
     * Parse visibility from string (case-insensitive)
     * @param value String value to parse
     * @return ProfileVisibility enum or null if invalid
     */
    public static ProfileVisibility fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            return valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            // Try matching by display name
            for (ProfileVisibility visibility : values()) {
                if (visibility.getDisplayName().equalsIgnoreCase(value.trim())) {
                    return visibility;
                }
            }
            return null;
        }
    }
    
    /**
     * Get visibility options suitable for organizations
     * Organizations typically want to be discoverable
     * @return Array of recommended visibility options for organizations
     */
    public static ProfileVisibility[] getOrganizationOptions() {
        return new ProfileVisibility[]{PUBLIC, CONNECTIONS_ONLY};
    }
    
    /**
     * Get visibility options suitable for volunteers
     * Volunteers may want more privacy control
     * @return Array of all visibility options for volunteers
     */
    public static ProfileVisibility[] getVolunteerOptions() {
        return values();
    }
    
    /**
     * Check if a user with this visibility can be contacted by organizations
     * @return true if organizations can reach out to this user
     */
    public boolean allowsOrganizationContact() {
        return this == PUBLIC || this == CONNECTIONS_ONLY;
    }
    
    /**
     * Check if a user with this visibility appears in volunteer matching
     * @return true if user can be matched with opportunities
     */
    public boolean allowsMatching() {
        return this == PUBLIC;
    }
    
    /**
     * Get CSS class name for styling
     * @return CSS class name based on visibility level
     */
    public String getCssClass() {
        return switch (this) {
            case PUBLIC -> "visibility-public";
            case CONNECTIONS_ONLY -> "visibility-connections";
            case PRIVATE -> "visibility-private";
        };
    }
    
    /**
     * Get bootstrap color variant for UI elements
     * @return Bootstrap color class
     */
    public String getBootstrapColor() {
        return switch (this) {
            case PUBLIC -> "success";
            case CONNECTIONS_ONLY -> "warning";
            case PRIVATE -> "secondary";
        };
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}