package com.volunteersync.backend.entity.profile;

import com.volunteersync.backend.entity.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Organization-specific profile entity.
 * Extends the base Profile class with organization-specific fields
 * such as legal information, verification status, and operational details.
 */
@Entity
@DiscriminatorValue("ORGANIZATION")
public class OrganizationProfile extends Profile {
    
    // =====================================================
    // ORGANIZATION BASIC INFORMATION
    // =====================================================
    
    @Column
    private String organizationType; // "Non-Profit", "Religious", "Educational", "Government", etc.
    
    @Column
    private LocalDate foundedDate;
    
    @Column
    private String ein; // Employer Identification Number
    
    @Column(columnDefinition = "TEXT")
    private String missionStatement;
    
    @Column
    private String organizationSize; // "1-10", "11-50", "51-200", "201-1000", "1000+"
    
    @Column(columnDefinition = "TEXT")
    private String description; // Detailed organization description
    
    // =====================================================
    // ADDRESS & LOCATION INFORMATION
    // =====================================================
    
    @Column
    private String address;
    
    @Column
    private String city;
    
    @Column
    private String state;
    
    @Column
    private String zipCode;
    
    @Column
    private String country = "United States";
    
    @Column
    private String mailingAddress; // If different from physical address
    
    @Column
    private String mailingCity;
    
    @Column
    private String mailingState;
    
    @Column
    private String mailingZipCode;
    
    // =====================================================
    // LEGAL & ADMINISTRATIVE INFORMATION
    // =====================================================
    
    @Column(nullable = false)
    private Boolean isVerified = false;
    
    @Column
    private LocalDateTime verifiedAt;
    
    @Column
    private String verifiedBy; // Admin user who verified
    
    @Column
    private String taxExemptStatus; // "501(c)(3)", "501(c)(4)", etc.
    
    @Column
    private String registrationNumber; // State registration number
    
    @Column
    private String registrationState;
    
    @Column
    private LocalDate incorporationDate;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column
    private LocalDateTime deactivatedAt;
    
    @Column
    private String deactivationReason;
    
    // =====================================================
    // CONTACT INFORMATION
    // =====================================================
    
    @Column
    private String primaryContactName;
    
    @Column
    private String primaryContactTitle;
    
    @Column
    private String primaryContactEmail;
    
    @Column
    private String primaryContactPhone;
    
    @Column
    private String secondaryContactName;
    
    @Column
    private String secondaryContactTitle;
    
    @Column
    private String secondaryContactEmail;
    
    @Column
    private String secondaryContactPhone;
    
    // =====================================================
    // ORGANIZATION DETAILS & OPERATIONS
    // =====================================================
    
    @Column(columnDefinition = "TEXT")
    private String focusAreas; // JSON array or comma-separated list of focus areas
    
    @Column(columnDefinition = "TEXT")
    private String servingAreas; // Geographic areas served
    
    @Column(columnDefinition = "TEXT")
    private String targetDemographic; // Who the organization serves
    
    @Column(nullable = false)
    private Boolean acceptsInternationalVolunteers = true;
    
    @Column(nullable = false)
    private Boolean providesVolunteerTraining = false;
    
    @Column(nullable = false)
    private Boolean requiresBackgroundCheck = false;
    
    @Column(nullable = false)
    private Boolean requiresOrientationSession = false;
    
    @Column
    private Integer minimumAge; // Minimum age for volunteers
    
    @Column
    private Integer minimumCommitmentHours; // Minimum volunteer commitment
    
    @Column
    private String commitmentFrequency; // "Weekly", "Monthly", "Flexible", etc.
    
    // =====================================================
    // OPERATIONAL DETAILS
    // =====================================================
    
    @Column
    private String operatingHours; // "Mon-Fri 9-5, Sat 10-2", etc.
    
    @Column
    private String seasonalOperations; // If operations are seasonal
    
    @Column(columnDefinition = "TEXT")
    private String volunteerBenefits; // What volunteers receive
    
    @Column(columnDefinition = "TEXT")
    private String equipmentProvided; // Equipment/supplies provided to volunteers
    
    @Column(columnDefinition = "TEXT")
    private String safetyPolicies; // Safety policies and procedures
    
    @Column
    private LocalDateTime lastActivityDate;
    
    @Column
    private Integer totalVolunteersServed = 0;
    
    @Column
    private Integer activeVolunteersCount = 0;
    
    // =====================================================
    // FINANCIAL & TRANSPARENCY
    // =====================================================
    
    @Column
    private String annualRevenue; // Revenue range
    
    @Column
    private String fundingSources; // Primary funding sources
    
    @Column
    private String financialReportsUrl; // Link to financial transparency
    
    @Column
    private Boolean publishesAnnualReport = false;
    
    @Column
    private String latestAnnualReportUrl;
    
    // =====================================================
    // SOCIAL IMPACT & METRICS
    // =====================================================
    
    @Column
    private Integer peopleServedAnnually;
    
    @Column
    private String impactMetrics; // JSON or comma-separated impact statistics
    
    @Column(columnDefinition = "TEXT")
    private String successStories;
    
    @Column
    private String awardsAndRecognition;
    
    // =====================================================
    // CONSTRUCTORS
    // =====================================================
    
    public OrganizationProfile() {
        super();
    }
    
    public OrganizationProfile(User user) {
        super(user);
        setOrganizationDefaults();
    }
    
    // =====================================================
    // IMPLEMENTATION OF ABSTRACT METHODS
    // =====================================================
    
    @Override
    public String getDisplayName() {
        if (getUser() != null && getUser().getOrganizationName() != null) {
            return getUser().getOrganizationName();
        }
        return "Organization";
    }
    
    @Override
    public String getProfileTypeDisplay() {
        return "🏢 Organization";
    }
    
    // =====================================================
    // HELPER METHODS
    // =====================================================
    
    /**
     * Get the full physical address as a formatted string
     * @return Formatted address string
     */
    public String getFullAddress() {
        StringBuilder addressBuilder = new StringBuilder();
        
        if (address != null && !address.trim().isEmpty()) {
            addressBuilder.append(address.trim());
        }
        
        if (city != null && !city.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(city.trim());
        }
        
        if (state != null && !state.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(state.trim());
        }
        
        if (zipCode != null && !zipCode.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(" ");
            addressBuilder.append(zipCode.trim());
        }
        
        if (country != null && !country.trim().isEmpty() && !"United States".equals(country)) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(country.trim());
        }
        
        return addressBuilder.toString();
    }
    
    /**
     * Get the full mailing address as a formatted string
     * @return Formatted mailing address string, or physical address if mailing address not set
     */
    public String getFullMailingAddress() {
        // If no mailing address is set, return physical address
        if (mailingAddress == null || mailingAddress.trim().isEmpty()) {
            return getFullAddress();
        }
        
        StringBuilder addressBuilder = new StringBuilder();
        
        if (mailingAddress != null && !mailingAddress.trim().isEmpty()) {
            addressBuilder.append(mailingAddress.trim());
        }
        
        if (mailingCity != null && !mailingCity.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(mailingCity.trim());
        }
        
        if (mailingState != null && !mailingState.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(mailingState.trim());
        }
        
        if (mailingZipCode != null && !mailingZipCode.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(" ");
            addressBuilder.append(mailingZipCode.trim());
        }
        
        return addressBuilder.toString();
    }
    
    /**
     * Get the year the organization was founded
     * @return Founded year as string, or null if not set
     */
    public String getFormattedFoundedYear() {
        if (foundedDate != null) {
            return String.valueOf(foundedDate.getYear());
        }
        return null;
    }
    
    /**
     * Check if organization is eligible for verification
     * @return true if has required information for verification
     */
    public boolean isEligibleForVerification() {
        return ein != null && !ein.trim().isEmpty() && 
               taxExemptStatus != null && !taxExemptStatus.trim().isEmpty() &&
               primaryContactEmail != null && !primaryContactEmail.trim().isEmpty();
    }
    
    /**
     * Calculate number of years the organization has been in operation
     * @return Years in operation, or 0 if founded date not set
     */
    public int getYearsInOperation() {
        if (foundedDate != null) {
            return LocalDate.now().getYear() - foundedDate.getYear();
        }
        return 0;
    }
    
    /**
     * Check if organization is currently accepting volunteers
     * @return true if active and not deactivated
     */
    public boolean isAcceptingVolunteers() {
        return Boolean.TRUE.equals(isActive) && deactivatedAt == null;
    }
    
    /**
     * Check if organization requires background checks for volunteers
     * @return true if background checks are required
     */
    public boolean requiresBackgroundChecks() {
        return Boolean.TRUE.equals(requiresBackgroundCheck);
    }
    
    /**
     * Check if organization provides training to volunteers
     * @return true if training is provided
     */
    public boolean providesTraining() {
        return Boolean.TRUE.equals(providesVolunteerTraining);
    }
    
    /**
     * Check if organization has been verified by administrators
     * @return true if verified
     */
    public boolean isVerifiedOrganization() {
        return Boolean.TRUE.equals(isVerified) && verifiedAt != null;
    }
    
    /**
     * Get primary contact information as formatted string
     * @return Formatted contact info
     */
    public String getPrimaryContactInfo() {
        StringBuilder contact = new StringBuilder();
        
        if (primaryContactName != null && !primaryContactName.trim().isEmpty()) {
            contact.append(primaryContactName.trim());
            
            if (primaryContactTitle != null && !primaryContactTitle.trim().isEmpty()) {
                contact.append(" (").append(primaryContactTitle.trim()).append(")");
            }
        }
        
        return contact.toString();
    }
    
    /**
     * Set default values for organization profile
     */
    private void setOrganizationDefaults() {
        this.isVerified = false;
        this.country = "United States";
        this.acceptsInternationalVolunteers = true;
        this.providesVolunteerTraining = false;
        this.requiresBackgroundCheck = false;
        this.requiresOrientationSession = false;
        this.isActive = true;
        this.lastActivityDate = LocalDateTime.now();
        this.totalVolunteersServed = 0;
        this.activeVolunteersCount = 0;
        this.publishesAnnualReport = false;
    }
    
    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================
    
    public String getOrganizationType() {
        return organizationType;
    }
    
    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }
    
    public LocalDate getFoundedDate() {
        return foundedDate;
    }
    
    public void setFoundedDate(LocalDate foundedDate) {
        this.foundedDate = foundedDate;
    }
    
    public String getEin() {
        return ein;
    }
    
    public void setEin(String ein) {
        this.ein = ein;
    }
    
    public String getMissionStatement() {
        return missionStatement;
    }
    
    public void setMissionStatement(String missionStatement) {
        this.missionStatement = missionStatement;
    }
    
    public String getOrganizationSize() {
        return organizationSize;
    }
    
    public void setOrganizationSize(String organizationSize) {
        this.organizationSize = organizationSize;
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
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getMailingAddress() {
        return mailingAddress;
    }
    
    public void setMailingAddress(String mailingAddress) {
        this.mailingAddress = mailingAddress;
    }
    
    public String getMailingCity() {
        return mailingCity;
    }
    
    public void setMailingCity(String mailingCity) {
        this.mailingCity = mailingCity;
    }
    
    public String getMailingState() {
        return mailingState;
    }
    
    public void setMailingState(String mailingState) {
        this.mailingState = mailingState;
    }
    
    public String getMailingZipCode() {
        return mailingZipCode;
    }
    
    public void setMailingZipCode(String mailingZipCode) {
        this.mailingZipCode = mailingZipCode;
    }
    
    public Boolean getIsVerified() {
        return isVerified;
    }
    
    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }
    
    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }
    
    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
    
    public String getVerifiedBy() {
        return verifiedBy;
    }
    
    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }
    
    public String getTaxExemptStatus() {
        return taxExemptStatus;
    }
    
    public void setTaxExemptStatus(String taxExemptStatus) {
        this.taxExemptStatus = taxExemptStatus;
    }
    
    public String getRegistrationNumber() {
        return registrationNumber;
    }
    
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
    
    public String getRegistrationState() {
        return registrationState;
    }
    
    public void setRegistrationState(String registrationState) {
        this.registrationState = registrationState;
    }
    
    public LocalDate getIncorporationDate() {
        return incorporationDate;
    }
    
    public void setIncorporationDate(LocalDate incorporationDate) {
        this.incorporationDate = incorporationDate;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getDeactivatedAt() {
        return deactivatedAt;
    }
    
    public void setDeactivatedAt(LocalDateTime deactivatedAt) {
        this.deactivatedAt = deactivatedAt;
    }
    
    public String getDeactivationReason() {
        return deactivationReason;
    }
    
    public void setDeactivationReason(String deactivationReason) {
        this.deactivationReason = deactivationReason;
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
    
    public String getPrimaryContactEmail() {
        return primaryContactEmail;
    }
    
    public void setPrimaryContactEmail(String primaryContactEmail) {
        this.primaryContactEmail = primaryContactEmail;
    }
    
    public String getPrimaryContactPhone() {
        return primaryContactPhone;
    }
    
    public void setPrimaryContactPhone(String primaryContactPhone) {
        this.primaryContactPhone = primaryContactPhone;
    }
    
    public String getSecondaryContactName() {
        return secondaryContactName;
    }
    
    public void setSecondaryContactName(String secondaryContactName) {
        this.secondaryContactName = secondaryContactName;
    }
    
    public String getSecondaryContactTitle() {
        return secondaryContactTitle;
    }
    
    public void setSecondaryContactTitle(String secondaryContactTitle) {
        this.secondaryContactTitle = secondaryContactTitle;
    }
    
    public String getSecondaryContactEmail() {
        return secondaryContactEmail;
    }
    
    public void setSecondaryContactEmail(String secondaryContactEmail) {
        this.secondaryContactEmail = secondaryContactEmail;
    }
    
    public String getSecondaryContactPhone() {
        return secondaryContactPhone;
    }
    
    public void setSecondaryContactPhone(String secondaryContactPhone) {
        this.secondaryContactPhone = secondaryContactPhone;
    }
    
    public String getFocusAreas() {
        return focusAreas;
    }
    
    public void setFocusAreas(String focusAreas) {
        this.focusAreas = focusAreas;
    }
    
    public String getServingAreas() {
        return servingAreas;
    }
    
    public void setServingAreas(String servingAreas) {
        this.servingAreas = servingAreas;
    }
    
    public String getTargetDemographic() {
        return targetDemographic;
    }
    
    public void setTargetDemographic(String targetDemographic) {
        this.targetDemographic = targetDemographic;
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
    
    public Integer getMinimumAge() {
        return minimumAge;
    }
    
    public void setMinimumAge(Integer minimumAge) {
        this.minimumAge = minimumAge;
    }
    
    public Integer getMinimumCommitmentHours() {
        return minimumCommitmentHours;
    }
    
    public void setMinimumCommitmentHours(Integer minimumCommitmentHours) {
        this.minimumCommitmentHours = minimumCommitmentHours;
    }
    
    public String getCommitmentFrequency() {
        return commitmentFrequency;
    }
    
    public void setCommitmentFrequency(String commitmentFrequency) {
        this.commitmentFrequency = commitmentFrequency;
    }
    
    public String getOperatingHours() {
        return operatingHours;
    }
    
    public void setOperatingHours(String operatingHours) {
        this.operatingHours = operatingHours;
    }
    
    public String getSeasonalOperations() {
        return seasonalOperations;
    }
    
    public void setSeasonalOperations(String seasonalOperations) {
        this.seasonalOperations = seasonalOperations;
    }
    
    public String getVolunteerBenefits() {
        return volunteerBenefits;
    }
    
    public void setVolunteerBenefits(String volunteerBenefits) {
        this.volunteerBenefits = volunteerBenefits;
    }
    
    public String getEquipmentProvided() {
        return equipmentProvided;
    }
    
    public void setEquipmentProvided(String equipmentProvided) {
        this.equipmentProvided = equipmentProvided;
    }
    
    public String getSafetyPolicies() {
        return safetyPolicies;
    }
    
    public void setSafetyPolicies(String safetyPolicies) {
        this.safetyPolicies = safetyPolicies;
    }
    
    public LocalDateTime getLastActivityDate() {
        return lastActivityDate;
    }
    
    public void setLastActivityDate(LocalDateTime lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }
    
    public Integer getTotalVolunteersServed() {
        return totalVolunteersServed;
    }
    
    public void setTotalVolunteersServed(Integer totalVolunteersServed) {
        this.totalVolunteersServed = totalVolunteersServed;
    }
    
    public Integer getActiveVolunteersCount() {
        return activeVolunteersCount;
    }
    
    public void setActiveVolunteersCount(Integer activeVolunteersCount) {
        this.activeVolunteersCount = activeVolunteersCount;
    }
    
    public String getAnnualRevenue() {
        return annualRevenue;
    }
    
    public void setAnnualRevenue(String annualRevenue) {
        this.annualRevenue = annualRevenue;
    }
    
    public String getFundingSources() {
        return fundingSources;
    }
    
    public void setFundingSources(String fundingSources) {
        this.fundingSources = fundingSources;
    }
    
    public String getFinancialReportsUrl() {
        return financialReportsUrl;
    }
    
    public void setFinancialReportsUrl(String financialReportsUrl) {
        this.financialReportsUrl = financialReportsUrl;
    }
    
    public Boolean getPublishesAnnualReport() {
        return publishesAnnualReport;
    }
    
    public void setPublishesAnnualReport(Boolean publishesAnnualReport) {
        this.publishesAnnualReport = publishesAnnualReport;
    }
    
    public String getLatestAnnualReportUrl() {
        return latestAnnualReportUrl;
    }
    
    public void setLatestAnnualReportUrl(String latestAnnualReportUrl) {
        this.latestAnnualReportUrl = latestAnnualReportUrl;
    }
    
    public Integer getPeopleServedAnnually() {
        return peopleServedAnnually;
    }
    
    public void setPeopleServedAnnually(Integer peopleServedAnnually) {
        this.peopleServedAnnually = peopleServedAnnually;
    }
    
    public String getImpactMetrics() {
        return impactMetrics;
    }
    
    public void setImpactMetrics(String impactMetrics) {
        this.impactMetrics = impactMetrics;
    }
    
    public String getSuccessStories() {
        return successStories;
    }
    
    public void setSuccessStories(String successStories) {
        this.successStories = successStories;
    }
    
    public String getAwardsAndRecognition() {
        return awardsAndRecognition;
    }
    
    public void setAwardsAndRecognition(String awardsAndRecognition) {
        this.awardsAndRecognition = awardsAndRecognition;
    }
}