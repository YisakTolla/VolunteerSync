package com.volunteersync.backend.enums;

public enum UserType {
    VOLUNTEER("Volunteer"),
    ORGANIZATION("Organization");

    private final String displayName;

    UserType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}