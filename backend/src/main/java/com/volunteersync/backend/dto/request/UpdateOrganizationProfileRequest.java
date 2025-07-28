package com.volunteersync.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public class UpdateOrganizationProfileRequest {
    
    private Long organizationId;
    
    @NotBlank(message = "Organization name is required")
    @Size(max = 255, message = "Organization name must not exceed 255 characters")
    private String name;
    
    @NotBlank(message = "Organization type is required")
    private String organizationType;
    
    @NotBlank(message = "Mission statement is required")
    @Size(max = 1000, message = "Mission statement must not exceed 1000 characters")
    private String missionStatement;
    
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
    
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String phone;
    private String website;
    
    @Email(message = "Primary contact email must be valid")
    private String primaryContactEmail;
    
    private String primaryContactName;
    private String primaryContactTitle;
    private String primaryContactPhone;
    
    private List<String> focusAreas;
    private String targetDemographic;
    private Integer minimumAge;
    private Boolean acceptsInternationalVolunteers;
    private Boolean providesVolunteerTraining;
    private Boolean requiresBackgroundCheck;
    private Boolean requiresOrientationSession;

    // Constructors
    public UpdateOrganizationProfileRequest() {}

    // Getters and Setters
    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }

    public String getMissionStatement() {
        return missionStatement;
    }

    public void setMissionStatement(String missionStatement) {
        this.missionStatement = missionStatement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPrimaryContactEmail() {
        return primaryContactEmail;
    }

    public void setPrimaryContactEmail(String primaryContactEmail) {
        this.primaryContactEmail = primaryContactEmail;
    }

    public String getPrimaryContactName() {
        return primaryContactName;
    }

    public void setPrimaryContactName(String primaryContactName) {
        this.primaryContactName = primaryContactName;
    }

    public String getPrimaryContactTitle() {
        return primaryContactTitle;
    }

    public void setPrimaryContactTitle(String primaryContactTitle) {
        this.primaryContactTitle = primaryContactTitle;
    }

    public String getPrimaryContactPhone() {
        return primaryContactPhone;
    }

    public void setPrimaryContactPhone(String primaryContactPhone) {
        this.primaryContactPhone = primaryContactPhone;
    }

    public List<String> getFocusAreas() {
        return focusAreas;
    }

    public void setFocusAreas(List<String> focusAreas) {
        this.focusAreas = focusAreas;
    }

    public String getTargetDemographic() {
        return targetDemographic;
    }

    public void setTargetDemographic(String targetDemographic) {
        this.targetDemographic = targetDemographic;
    }

    public Integer getMinimumAge() {
        return minimumAge;
    }

    public void setMinimumAge(Integer minimumAge) {
        this.minimumAge = minimumAge;
    }

    public Boolean getAcceptsInternationalVolunteers() {
        return acceptsInternationalVolunteers;
    }

    public void setAcceptsInternationalVolunteers(Boolean acceptsInternationalVolunteers) {
        this.acceptsInternationalVolunteers = acceptsInternationalVolunteers;
    }

    public Boolean getProvidesVolunteerTraining() {
        return providesVolunteerTraining;
    }

    public void setProvidesVolunteerTraining(Boolean providesVolunteerTraining) {
        this.providesVolunteerTraining = providesVolunteerTraining;
    }

    public Boolean getRequiresBackgroundCheck() {
        return requiresBackgroundCheck;
    }

    public void setRequiresBackgroundCheck(Boolean requiresBackgroundCheck) {
        this.requiresBackgroundCheck = requiresBackgroundCheck;
    }

    public Boolean getRequiresOrientationSession() {
        return requiresOrientationSession;
    }

    public void setRequiresOrientationSession(Boolean requiresOrientationSession) {
        this.requiresOrientationSession = requiresOrientationSession;
    }

    @Override
    public String toString() {
        return "UpdateOrganizationProfileRequest{" +
                "organizationId=" + organizationId +
                ", name='" + name + '\'' +
                ", organizationType='" + organizationType + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}