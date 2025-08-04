package com.volunteersync.backend.enums;

public enum EventStatus {
    DRAFT("Draft"),
    ACTIVE("Active"),
    FULL("Full"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed");

    private final String displayName;

    EventStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isOpen() {
        return this == ACTIVE;
    }

    public boolean isFinished() {
        return this == COMPLETED || this == CANCELLED;
    }
}