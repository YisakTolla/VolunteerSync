package com.volunteersync.backend.enums;

public enum EventDuration {
    SHORT("1-2 Hours", 1, 2),
    MEDIUM("3-4 Hours", 3, 4),
    FULL_DAY("5-8 Hours (Full Day)", 5, 8),
    MULTI_DAY("Multi-Day Event", 24, 168),
    WEEKLY_COMMITMENT("Weekly Commitment", 168, 672),
    MONTHLY_COMMITMENT("Monthly Commitment", 672, 2920),
    ONGOING_LONG_TERM("Ongoing/Long-term", 2920, Integer.MAX_VALUE);

    private final String displayName;
    private final int minHours;
    private final int maxHours;

    EventDuration(String displayName, int minHours, int maxHours) {
        this.displayName = displayName;
        this.minHours = minHours;
        this.maxHours = maxHours;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMinHours() {
        return minHours;
    }

    public int getMaxHours() {
        return maxHours;
    }
}