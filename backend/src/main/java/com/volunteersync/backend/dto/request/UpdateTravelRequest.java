package com.volunteersync.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * Data Transfer Object for updating volunteer travel preferences.
 * Contains information about a volunteer's willingness to travel,
 * transportation capabilities, and travel-related restrictions.
 * 
 * This request DTO is used when volunteers want to update their
 * travel and transportation preferences for volunteer opportunities.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateTravelRequest {

    // =====================================================
    // TRAVEL WILLINGNESS & DISTANCE
    // =====================================================

    private Boolean willingToTravel; // Whether volunteer is willing to travel for opportunities

    @Min(value = 0, message = "Max travel distance cannot be negative")
    @Max(value = 10000, message = "Max travel distance cannot exceed 10,000 km")
    private Integer maxTravelDistance; // Maximum travel distance in kilometers

    // =====================================================
    // TRANSPORTATION & LOGISTICS
    // =====================================================

    private Boolean hasReliableTransportation; // Whether volunteer has reliable transportation

    private Boolean hasDriversLicense; // Whether volunteer has a valid driver's license

    private Boolean ownsCar; // Whether volunteer owns a car

    private Boolean canUsePublicTransport; // Whether volunteer can use public transportation

    private Boolean willingToCarpool; // Whether volunteer is willing to carpool with others

    // =====================================================
    // TRAVEL PREFERENCES & RESTRICTIONS
    // =====================================================

    private Boolean canTravelWeekdays; // Available to travel on weekdays

    private Boolean canTravelWeekends; // Available to travel on weekends

    private Boolean canTravelOvernight; // Willing to travel for overnight opportunities

    @Min(value = 0, message = "Max overnight stays cannot be negative")
    @Max(value = 365, message = "Max overnight stays cannot exceed 365 days")
    private Integer maxOvernightStays; // Maximum nights willing to stay away

    // =====================================================
    // SPECIAL CONSIDERATIONS
    // =====================================================

    private Boolean hasPassport; // Whether volunteer has a valid passport for international travel

    private Boolean willingToTravelInternationally; // Willing to travel internationally

    private String travelRestrictions; // Any specific travel restrictions or limitations

    private String accommodationPreferences; // Preferred accommodation types when traveling

    private String accessibilityNeeds; // Any accessibility requirements for travel

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Default constructor for JSON deserialization.
     */
    public UpdateTravelRequest() {
        // Default constructor
    }

    /**
     * Constructor with basic travel information.
     * 
     * @param willingToTravel Whether volunteer is willing to travel
     * @param maxTravelDistance Maximum travel distance in kilometers
     */
    public UpdateTravelRequest(Boolean willingToTravel, Integer maxTravelDistance) {
        this.willingToTravel = willingToTravel;
        this.maxTravelDistance = maxTravelDistance;
    }

    /**
     * Constructor with travel and transportation information.
     * 
     * @param willingToTravel Whether volunteer is willing to travel
     * @param maxTravelDistance Maximum travel distance in kilometers
     * @param hasReliableTransportation Whether volunteer has reliable transportation
     * @param hasDriversLicense Whether volunteer has a driver's license
     */
    public UpdateTravelRequest(Boolean willingToTravel, Integer maxTravelDistance,
                              Boolean hasReliableTransportation, Boolean hasDriversLicense) {
        this.willingToTravel = willingToTravel;
        this.maxTravelDistance = maxTravelDistance;
        this.hasReliableTransportation = hasReliableTransportation;
        this.hasDriversLicense = hasDriversLicense;
    }

    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================

    public Boolean getWillingToTravel() {
        return willingToTravel;
    }

    public void setWillingToTravel(Boolean willingToTravel) {
        this.willingToTravel = willingToTravel;
    }

    public Integer getMaxTravelDistance() {
        return maxTravelDistance;
    }

    public void setMaxTravelDistance(Integer maxTravelDistance) {
        this.maxTravelDistance = maxTravelDistance;
    }

    public Boolean getHasReliableTransportation() {
        return hasReliableTransportation;
    }

    public void setHasReliableTransportation(Boolean hasReliableTransportation) {
        this.hasReliableTransportation = hasReliableTransportation;
    }

    public Boolean getHasDriversLicense() {
        return hasDriversLicense;
    }

    public void setHasDriversLicense(Boolean hasDriversLicense) {
        this.hasDriversLicense = hasDriversLicense;
    }

    public Boolean getOwnsCar() {
        return ownsCar;
    }

    public void setOwnsCar(Boolean ownsCar) {
        this.ownsCar = ownsCar;
    }

    public Boolean getCanUsePublicTransport() {
        return canUsePublicTransport;
    }

    public void setCanUsePublicTransport(Boolean canUsePublicTransport) {
        this.canUsePublicTransport = canUsePublicTransport;
    }

    public Boolean getWillingToCarpool() {
        return willingToCarpool;
    }

    public void setWillingToCarpool(Boolean willingToCarpool) {
        this.willingToCarpool = willingToCarpool;
    }

    public Boolean getCanTravelWeekdays() {
        return canTravelWeekdays;
    }

    public void setCanTravelWeekdays(Boolean canTravelWeekdays) {
        this.canTravelWeekdays = canTravelWeekdays;
    }

    public Boolean getCanTravelWeekends() {
        return canTravelWeekends;
    }

    public void setCanTravelWeekends(Boolean canTravelWeekends) {
        this.canTravelWeekends = canTravelWeekends;
    }

    public Boolean getCanTravelOvernight() {
        return canTravelOvernight;
    }

    public void setCanTravelOvernight(Boolean canTravelOvernight) {
        this.canTravelOvernight = canTravelOvernight;
    }

    public Integer getMaxOvernightStays() {
        return maxOvernightStays;
    }

    public void setMaxOvernightStays(Integer maxOvernightStays) {
        this.maxOvernightStays = maxOvernightStays;
    }

    public Boolean getHasPassport() {
        return hasPassport;
    }

    public void setHasPassport(Boolean hasPassport) {
        this.hasPassport = hasPassport;
    }

    public Boolean getWillingToTravelInternationally() {
        return willingToTravelInternationally;
    }

    public void setWillingToTravelInternationally(Boolean willingToTravelInternationally) {
        this.willingToTravelInternationally = willingToTravelInternationally;
    }

    public String getTravelRestrictions() {
        return travelRestrictions;
    }

    public void setTravelRestrictions(String travelRestrictions) {
        this.travelRestrictions = travelRestrictions;
    }

    public String getAccommodationPreferences() {
        return accommodationPreferences;
    }

    public void setAccommodationPreferences(String accommodationPreferences) {
        this.accommodationPreferences = accommodationPreferences;
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
     * Checks if the volunteer has any form of transportation.
     */
    public boolean hasAnyTransportation() {
        return (hasReliableTransportation != null && hasReliableTransportation) ||
               (ownsCar != null && ownsCar) ||
               (canUsePublicTransport != null && canUsePublicTransport);
    }

    /**
     * Checks if the volunteer is suitable for local opportunities only.
     */
    public boolean isLocalOnly() {
        return (willingToTravel == null || !willingToTravel) ||
               (maxTravelDistance != null && maxTravelDistance <= 25); // 25km or less
    }

    /**
     * Checks if the volunteer is suitable for long-distance travel.
     */
    public boolean canTravelLongDistance() {
        return (willingToTravel != null && willingToTravel) &&
               (maxTravelDistance == null || maxTravelDistance > 100); // More than 100km
    }

    /**
     * Checks if the volunteer can handle extended assignments.
     */
    public boolean canHandleExtendedAssignments() {
        return (canTravelOvernight != null && canTravelOvernight) &&
               (maxOvernightStays == null || maxOvernightStays > 7); // More than a week
    }

    @Override
    public String toString() {
        return "UpdateTravelRequest{" +
                "willingToTravel=" + willingToTravel +
                ", maxTravelDistance=" + maxTravelDistance +
                ", hasReliableTransportation=" + hasReliableTransportation +
                ", hasDriversLicense=" + hasDriversLicense +
                ", ownsCar=" + ownsCar +
                ", canUsePublicTransport=" + canUsePublicTransport +
                ", canTravelOvernight=" + canTravelOvernight +
                ", willingToTravelInternationally=" + willingToTravelInternationally +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        UpdateTravelRequest that = (UpdateTravelRequest) obj;
        
        // Compare key fields for equality
        return java.util.Objects.equals(willingToTravel, that.willingToTravel) &&
               java.util.Objects.equals(maxTravelDistance, that.maxTravelDistance) &&
               java.util.Objects.equals(hasReliableTransportation, that.hasReliableTransportation) &&
               java.util.Objects.equals(hasDriversLicense, that.hasDriversLicense);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(willingToTravel, maxTravelDistance, hasReliableTransportation, hasDriversLicense);
    }
}