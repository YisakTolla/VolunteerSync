package com.volunteersync.backend.enums;

public enum ApplicationStatus {
    PENDING("Pending Review"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    WITHDRAWN("Withdrawn"),
    ATTENDED("Attended"),
    NO_SHOW("No Show");

    private final String displayName;

    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isApproved() {
        return this == ACCEPTED;
    }

    public boolean isCompleted() {
        return this == ATTENDED || this == NO_SHOW;
    }
}