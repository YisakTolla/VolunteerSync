package com.volunteersync.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.util.List;

/**
 * Data Transfer Object for updating volunteer availability preferences.
 * Contains information about when a volunteer is available to participate
 * in volunteer opportunities, including days, times, and scheduling preferences.
 * 
 * This request DTO is used when volunteers want to update their
 * availability and scheduling preferences for volunteer opportunities.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateAvailabilityRequest {

    // =====================================================
    // BASIC AVAILABILITY (matches service method parameters)
    // =====================================================

    private Boolean availableWeekdays; // Available Monday through Friday

    private Boolean availableWeekends; // Available Saturday and Sunday

    private Boolean availableMorning; // Available in morning hours (6 AM - 12 PM)

    private Boolean availableAfternoon; // Available in afternoon hours (12 PM - 6 PM)

    private Boolean availableEvening; // Available in evening hours (6 PM - 10 PM)

    @Size(max = 500, message = "Availability notes cannot exceed 500 characters")
    private String availabilityNotes; // Additional notes about availability

    // =====================================================
    // DETAILED DAY PREFERENCES
    // =====================================================

    private Boolean availableMonday;

    private Boolean availableTuesday;

    private Boolean availableWednesday;

    private Boolean availableThursday;

    private Boolean availableFriday;

    private Boolean availableSaturday;

    private Boolean availableSunday;

    // =====================================================
    // TIME SLOT PREFERENCES
    // =====================================================

    private Boolean availableEarlyMorning; // 6 AM - 9 AM

    private Boolean availableLateMorning; // 9 AM - 12 PM

    private Boolean availableEarlyAfternoon; // 12 PM - 3 PM

    private Boolean availableLateAfternoon; // 3 PM - 6 PM

    private Boolean availableEarlyEvening; // 6 PM - 8 PM

    private Boolean availableLateEvening; // 8 PM - 10 PM

    private Boolean availableOvernight; // 10 PM - 6 AM (for emergency response, etc.)

    // =====================================================
    // COMMITMENT PREFERENCES
    // =====================================================

    @Min(value = 0, message = "Hours per week cannot be negative")
    @Max(value = 168, message = "Hours per week cannot exceed 168")
    private Integer availableHoursPerWeek; // Total hours available per week

    @Min(value = 0, message = "Hours per day cannot be negative")
    @Max(value = 24, message = "Hours per day cannot exceed 24")
    private Integer maxHoursPerDay; // Maximum hours willing to volunteer per day

    @Min(value = 0, message = "Days per week cannot be negative")
    @Max(value = 7, message = "Days per week cannot exceed 7")
    private Integer maxDaysPerWeek; // Maximum days willing to volunteer per week

    // =====================================================
    // SCHEDULING PREFERENCES
    // =====================================================

    private Boolean preferRegularSchedule; // Prefers consistent, regular schedule

    private Boolean availableShortNotice; // Available for last-minute opportunities

    @Min(value = 0, message = "Minimum notice hours cannot be negative")
    @Max(value = 8760, message = "Minimum notice hours cannot exceed 1 year")
    private Integer minimumNoticeHours; // Minimum hours of notice needed

    private Boolean flexibleScheduling; // Open to flexible scheduling

    private Boolean canCommitLongTerm; // Willing to commit to long-term opportunities

    private Boolean preferOneTimeEvents; // Prefers one-time volunteer events

    // =====================================================
    // SPECIAL AVAILABILITY
    // =====================================================

    private Boolean availableHolidays; // Available during holidays

    private Boolean availableSchoolHours; // Available during school hours (for students/teachers)

    private Boolean availableBusinessHours; // Available during business hours (9 AM - 5 PM weekdays)

    private Boolean availableEmergencies; // Available for emergency response

    private List<String> unavailableDates; // Specific dates when unavailable (ISO format: "2024-12-25")

    private List<String> preferredSeasons; // Preferred seasons for volunteering ("SPRING", "SUMMER", etc.)

    // =====================================================
    // LOCATION & REMOTE OPTIONS
    // =====================================================

    private Boolean availableRemote; // Available for remote/virtual volunteering

    private Boolean availableOnSite; // Available for on-site volunteering

    private Boolean availableHybrid; // Available for hybrid opportunities

    // =====================================================
    // ACCESSIBILITY & SPECIAL NEEDS
    // =====================================================

    @Size(max = 500, message = "Scheduling restrictions cannot exceed 500 characters")
    private String schedulingRestrictions; // Any specific scheduling restrictions

    @Size(max = 500, message = "Accessibility needs cannot exceed 500 characters")
    private String accessibilityNeeds; // Any accessibility requirements for scheduling

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Default constructor for JSON deserialization.
     */
    public UpdateAvailabilityRequest() {
        // Default constructor
    }

    /**
     * Constructor with basic availability information.
     * 
     * @param availableWeekdays Available on weekdays
     * @param availableWeekends Available on weekends
     * @param availableMorning Available in morning
     * @param availableAfternoon Available in afternoon
     * @param availableEvening Available in evening
     */
    public UpdateAvailabilityRequest(Boolean availableWeekdays, Boolean availableWeekends,
                                   Boolean availableMorning, Boolean availableAfternoon,
                                   Boolean availableEvening) {
        this.availableWeekdays = availableWeekdays;
        this.availableWeekends = availableWeekends;
        this.availableMorning = availableMorning;
        this.availableAfternoon = availableAfternoon;
        this.availableEvening = availableEvening;
    }

    /**
     * Constructor with basic availability and notes.
     * 
     * @param availableWeekdays Available on weekdays
     * @param availableWeekends Available on weekends
     * @param availableMorning Available in morning
     * @param availableAfternoon Available in afternoon
     * @param availableEvening Available in evening
     * @param availabilityNotes Additional availability notes
     */
    public UpdateAvailabilityRequest(Boolean availableWeekdays, Boolean availableWeekends,
                                   Boolean availableMorning, Boolean availableAfternoon,
                                   Boolean availableEvening, String availabilityNotes) {
        this.availableWeekdays = availableWeekdays;
        this.availableWeekends = availableWeekends;
        this.availableMorning = availableMorning;
        this.availableAfternoon = availableAfternoon;
        this.availableEvening = availableEvening;
        this.availabilityNotes = availabilityNotes;
    }

    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================

    public Boolean getAvailableWeekdays() {
        return availableWeekdays;
    }

    public void setAvailableWeekdays(Boolean availableWeekdays) {
        this.availableWeekdays = availableWeekdays;
    }

    public Boolean getAvailableWeekends() {
        return availableWeekends;
    }

    public void setAvailableWeekends(Boolean availableWeekends) {
        this.availableWeekends = availableWeekends;
    }

    public Boolean getAvailableMorning() {
        return availableMorning;
    }

    public void setAvailableMorning(Boolean availableMorning) {
        this.availableMorning = availableMorning;
    }

    public Boolean getAvailableAfternoon() {
        return availableAfternoon;
    }

    public void setAvailableAfternoon(Boolean availableAfternoon) {
        this.availableAfternoon = availableAfternoon;
    }

    public Boolean getAvailableEvening() {
        return availableEvening;
    }

    public void setAvailableEvening(Boolean availableEvening) {
        this.availableEvening = availableEvening;
    }

    public String getAvailabilityNotes() {
        return availabilityNotes;
    }

    public void setAvailabilityNotes(String availabilityNotes) {
        this.availabilityNotes = availabilityNotes;
    }

    public Boolean getAvailableMonday() {
        return availableMonday;
    }

    public void setAvailableMonday(Boolean availableMonday) {
        this.availableMonday = availableMonday;
    }

    public Boolean getAvailableTuesday() {
        return availableTuesday;
    }

    public void setAvailableTuesday(Boolean availableTuesday) {
        this.availableTuesday = availableTuesday;
    }

    public Boolean getAvailableWednesday() {
        return availableWednesday;
    }

    public void setAvailableWednesday(Boolean availableWednesday) {
        this.availableWednesday = availableWednesday;
    }

    public Boolean getAvailableThursday() {
        return availableThursday;
    }

    public void setAvailableThursday(Boolean availableThursday) {
        this.availableThursday = availableThursday;
    }

    public Boolean getAvailableFriday() {
        return availableFriday;
    }

    public void setAvailableFriday(Boolean availableFriday) {
        this.availableFriday = availableFriday;
    }

    public Boolean getAvailableSaturday() {
        return availableSaturday;
    }

    public void setAvailableSaturday(Boolean availableSaturday) {
        this.availableSaturday = availableSaturday;
    }

    public Boolean getAvailableSunday() {
        return availableSunday;
    }

    public void setAvailableSunday(Boolean availableSunday) {
        this.availableSunday = availableSunday;
    }

    public Boolean getAvailableEarlyMorning() {
        return availableEarlyMorning;
    }

    public void setAvailableEarlyMorning(Boolean availableEarlyMorning) {
        this.availableEarlyMorning = availableEarlyMorning;
    }

    public Boolean getAvailableLateMorning() {
        return availableLateMorning;
    }

    public void setAvailableLateMorning(Boolean availableLateMorning) {
        this.availableLateMorning = availableLateMorning;
    }

    public Boolean getAvailableEarlyAfternoon() {
        return availableEarlyAfternoon;
    }

    public void setAvailableEarlyAfternoon(Boolean availableEarlyAfternoon) {
        this.availableEarlyAfternoon = availableEarlyAfternoon;
    }

    public Boolean getAvailableLateAfternoon() {
        return availableLateAfternoon;
    }

    public void setAvailableLateAfternoon(Boolean availableLateAfternoon) {
        this.availableLateAfternoon = availableLateAfternoon;
    }

    public Boolean getAvailableEarlyEvening() {
        return availableEarlyEvening;
    }

    public void setAvailableEarlyEvening(Boolean availableEarlyEvening) {
        this.availableEarlyEvening = availableEarlyEvening;
    }

    public Boolean getAvailableLateEvening() {
        return availableLateEvening;
    }

    public void setAvailableLateEvening(Boolean availableLateEvening) {
        this.availableLateEvening = availableLateEvening;
    }

    public Boolean getAvailableOvernight() {
        return availableOvernight;
    }

    public void setAvailableOvernight(Boolean availableOvernight) {
        this.availableOvernight = availableOvernight;
    }

    public Integer getAvailableHoursPerWeek() {
        return availableHoursPerWeek;
    }

    public void setAvailableHoursPerWeek(Integer availableHoursPerWeek) {
        this.availableHoursPerWeek = availableHoursPerWeek;
    }

    public Integer getMaxHoursPerDay() {
        return maxHoursPerDay;
    }

    public void setMaxHoursPerDay(Integer maxHoursPerDay) {
        this.maxHoursPerDay = maxHoursPerDay;
    }

    public Integer getMaxDaysPerWeek() {
        return maxDaysPerWeek;
    }

    public void setMaxDaysPerWeek(Integer maxDaysPerWeek) {
        this.maxDaysPerWeek = maxDaysPerWeek;
    }

    public Boolean getPreferRegularSchedule() {
        return preferRegularSchedule;
    }

    public void setPreferRegularSchedule(Boolean preferRegularSchedule) {
        this.preferRegularSchedule = preferRegularSchedule;
    }

    public Boolean getAvailableShortNotice() {
        return availableShortNotice;
    }

    public void setAvailableShortNotice(Boolean availableShortNotice) {
        this.availableShortNotice = availableShortNotice;
    }

    public Integer getMinimumNoticeHours() {
        return minimumNoticeHours;
    }

    public void setMinimumNoticeHours(Integer minimumNoticeHours) {
        this.minimumNoticeHours = minimumNoticeHours;
    }

    public Boolean getFlexibleScheduling() {
        return flexibleScheduling;
    }

    public void setFlexibleScheduling(Boolean flexibleScheduling) {
        this.flexibleScheduling = flexibleScheduling;
    }

    public Boolean getCanCommitLongTerm() {
        return canCommitLongTerm;
    }

    public void setCanCommitLongTerm(Boolean canCommitLongTerm) {
        this.canCommitLongTerm = canCommitLongTerm;
    }

    public Boolean getPreferOneTimeEvents() {
        return preferOneTimeEvents;
    }

    public void setPreferOneTimeEvents(Boolean preferOneTimeEvents) {
        this.preferOneTimeEvents = preferOneTimeEvents;
    }

    public Boolean getAvailableHolidays() {
        return availableHolidays;
    }

    public void setAvailableHolidays(Boolean availableHolidays) {
        this.availableHolidays = availableHolidays;
    }

    public Boolean getAvailableSchoolHours() {
        return availableSchoolHours;
    }

    public void setAvailableSchoolHours(Boolean availableSchoolHours) {
        this.availableSchoolHours = availableSchoolHours;
    }

    public Boolean getAvailableBusinessHours() {
        return availableBusinessHours;
    }

    public void setAvailableBusinessHours(Boolean availableBusinessHours) {
        this.availableBusinessHours = availableBusinessHours;
    }

    public Boolean getAvailableEmergencies() {
        return availableEmergencies;
    }

    public void setAvailableEmergencies(Boolean availableEmergencies) {
        this.availableEmergencies = availableEmergencies;
    }

    public List<String> getUnavailableDates() {
        return unavailableDates;
    }

    public void setUnavailableDates(List<String> unavailableDates) {
        this.unavailableDates = unavailableDates;
    }

    public List<String> getPreferredSeasons() {
        return preferredSeasons;
    }

    public void setPreferredSeasons(List<String> preferredSeasons) {
        this.preferredSeasons = preferredSeasons;
    }

    public Boolean getAvailableRemote() {
        return availableRemote;
    }

    public void setAvailableRemote(Boolean availableRemote) {
        this.availableRemote = availableRemote;
    }

    public Boolean getAvailableOnSite() {
        return availableOnSite;
    }

    public void setAvailableOnSite(Boolean availableOnSite) {
        this.availableOnSite = availableOnSite;
    }

    public Boolean getAvailableHybrid() {
        return availableHybrid;
    }

    public void setAvailableHybrid(Boolean availableHybrid) {
        this.availableHybrid = availableHybrid;
    }

    public String getSchedulingRestrictions() {
        return schedulingRestrictions;
    }

    public void setSchedulingRestrictions(String schedulingRestrictions) {
        this.schedulingRestrictions = schedulingRestrictions;
    }

    public String getAccessibilityNeeds() {
        return accessibilityNeeds;
    }

    public void setAccessibilityNeeds(String accessibilityNeeds) {
        this.accessibilityNeeds = accessibilityNeeds;
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    /**
     * Checks if the volunteer is available during standard business hours.
     */
    public boolean isAvailableDuringBusinessHours() {
        return (availableWeekdays != null && availableWeekdays) &&
               ((availableMorning != null && availableMorning) || 
                (availableAfternoon != null && availableAfternoon));
    }

    /**
     * Checks if the volunteer has high availability (available most times).
     */
    public boolean hasHighAvailability() {
        int availableTimeSlots = 0;
        if (availableMorning != null && availableMorning) availableTimeSlots++;
        if (availableAfternoon != null && availableAfternoon) availableTimeSlots++;
        if (availableEvening != null && availableEvening) availableTimeSlots++;
        
        boolean weekdayAvailable = availableWeekdays != null && availableWeekdays;
        boolean weekendAvailable = availableWeekends != null && availableWeekends;
        
        return availableTimeSlots >= 2 && (weekdayAvailable || weekendAvailable);
    }

    /**
     * Checks if the volunteer is suitable for emergency response.
     */
    public boolean isSuitableForEmergencyResponse() {
        return (availableEmergencies != null && availableEmergencies) &&
               (availableShortNotice != null && availableShortNotice) &&
               (minimumNoticeHours == null || minimumNoticeHours <= 2);
    }

    /**
     * Gets the total possible availability hours per week.
     */
    public Integer getMaxPossibleHoursPerWeek() {
        if (availableHoursPerWeek != null) {
            return availableHoursPerWeek;
        }
        
        // Calculate based on day/time availability if specific hours not provided
        int timeSlots = 0;
        if (availableMorning != null && availableMorning) timeSlots++;
        if (availableAfternoon != null && availableAfternoon) timeSlots++;
        if (availableEvening != null && availableEvening) timeSlots++;
        
        int availableDays = 0;
        if (availableWeekdays != null && availableWeekdays) availableDays += 5;
        if (availableWeekends != null && availableWeekends) availableDays += 2;
        
        // Estimate 4 hours per time slot
        return timeSlots * availableDays * 4;
    }

    /**
     * Checks if the volunteer prefers flexible arrangements.
     */
    public boolean prefersFlexibility() {
        return (flexibleScheduling != null && flexibleScheduling) ||
               (availableShortNotice != null && availableShortNotice) ||
               (preferOneTimeEvents != null && preferOneTimeEvents);
    }

    @Override
    public String toString() {
        return "UpdateAvailabilityRequest{" +
                "availableWeekdays=" + availableWeekdays +
                ", availableWeekends=" + availableWeekends +
                ", availableMorning=" + availableMorning +
                ", availableAfternoon=" + availableAfternoon +
                ", availableEvening=" + availableEvening +
                ", availableHoursPerWeek=" + availableHoursPerWeek +
                ", flexibleScheduling=" + flexibleScheduling +
                ", availableShortNotice=" + availableShortNotice +
                ", availableRemote=" + availableRemote +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        UpdateAvailabilityRequest that = (UpdateAvailabilityRequest) obj;
        
        return java.util.Objects.equals(availableWeekdays, that.availableWeekdays) &&
               java.util.Objects.equals(availableWeekends, that.availableWeekends) &&
               java.util.Objects.equals(availableMorning, that.availableMorning) &&
               java.util.Objects.equals(availableAfternoon, that.availableAfternoon) &&
               java.util.Objects.equals(availableEvening, that.availableEvening) &&
               java.util.Objects.equals(availabilityNotes, that.availabilityNotes);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(availableWeekdays, availableWeekends, availableMorning, 
                                    availableAfternoon, availableEvening, availabilityNotes);
    }
}