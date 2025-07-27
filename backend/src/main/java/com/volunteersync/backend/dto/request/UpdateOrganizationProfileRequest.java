package com.volunteersync.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object for organization profile update requests.
 * Extends the base UpdateProfileRequest with organization-specific fields
 * that can be updated including organization details, verification info, and settings.
 * 
 * This request DTO includes validation for organization-specific update operations.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateOrganizationProfileRequest extends UpdateProfileRequest {

    // =====================================================
    // ORGANIZATION BASIC INFORMATION
    // =====================================================

    // =====================================================
    // ORGANIZATION BASIC INFORMATION
    // =====================================================

    @Size(max = 100, message = "Organization name cannot exceed 100 characters")
    private String organizationName;

    private String organizationType; // "NONPROFIT", "CHARITY", "NGO", "GOVERNMENT", "CORPORATE", "RELIGIOUS"

    @Past(message = "Founded date must be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate foundedDate;

    @Size(max = 1000, message = "Mission statement cannot exceed 1000 characters")
    private String missionStatement;

    @Size(max = 1000, message = "Organization description cannot exceed 1000 characters")
    private String description;

    // =====================================================
    // ORGANIZATION SIZE & STRUCTURE
    // =====================================================

    @Min(value = 1, message = "Number of employees must be at least 1")
    @Max(value = 1000000, message = "Number of employees cannot exceed 1,000,000")
    private Integer numberOfEmployees;

    @Min(value = 0, message = "Annual budget cannot be negative")
    private Double annualBudget;

    private String budgetRange; // "UNDER_10K", "10K_50K", "50K_100K", "100K_500K", "500K_1M", "OVER_1M"

    private List<String> serviceAreas; // Geographic areas served

    private List<String> causeAreas; // e.g., ["ENVIRONMENT", "EDUCATION", "HEALTH", "POVERTY"]

    // =====================================================
    // CONTACT & ADMINISTRATIVE INFORMATION
    // =====================================================

    @Size(max = 100, message = "Primary contact name cannot exceed 100 characters")
    private String primaryContactName;

    @Size(max = 100, message = "Primary contact title cannot exceed 100 characters")
    private String primaryContactTitle;

    @Email(message = "Primary contact email must be a valid email address")
    private String primaryContactEmail;

    private String primaryContactPhone;

    @Size(max = 200, message = "Mailing address cannot exceed 200 characters")
    private String mailingAddress;

    @Size(max = 200, message = "Physical address cannot exceed 200 characters")
    private String physicalAddress;

    private String timeZone;

    private List<String> operatingHours; // e.g., ["MON:9-17", "TUE:9-17"]

    // =====================================================
    // LEGAL & VERIFICATION INFORMATION
    // =====================================================

    private Boolean isNonprofit;

    private Boolean isTaxExempt;

    private String taxExemptionNumber;

    @Size(max = 50, message = "Registration number cannot exceed 50 characters")
    private String registrationNumber;

    // =====================================================
    // VOLUNTEER MANAGEMENT PREFERENCES
    // =====================================================

    private Boolean acceptingVolunteers;

    private Boolean requiresBackgroundCheck;

    private Boolean requiresOrientation;

    private Boolean requiresTraining;

    @Min(value = 0, message = "Minimum age cannot be negative")
    @Max(value = 100, message = "Minimum age cannot exceed 100")
    private Integer minimumAge;

    @Min(value = 0, message = "Minimum commitment hours cannot be negative")
    @Max(value = 8760, message = "Minimum commitment hours cannot exceed 8760 (hours in a year)")
    private Integer minimumCommitmentHours;

    private String volunteerApplicationProcess; // "OPEN", "APPROVAL_REQUIRED", "INVITATION_ONLY"

    private List<String> requiredSkills;

    private List<String> preferredSkills;

    @Size(max = 100, message = "Volunteer coordinator name cannot exceed 100 characters")
    private String volunteerCoordinatorName;

    @Email(message = "Volunteer coordinator email must be a valid email address")
    private String volunteerCoordinatorEmail;

    // =====================================================
    // IMPACT & METRICS
    // =====================================================

    @Size(max = 1000, message = "Impact statement cannot exceed 1000 characters")
    private String impactStatement;

    private List<String> impactMetrics; // Custom impact measurements

    @Min(value = 0, message = "People served cannot be negative")
    private Integer peopleServed;

    // =====================================================
    // PARTNERSHIPS & AFFILIATIONS
    // =====================================================

    private List<String> partnerOrganizations;

    private List<String> affiliations;

    private List<String> accreditations;

    private List<String> awards;

    @Size(max = 100, message = "Parent organization cannot exceed 100 characters")
    private String parentOrganization;

    private List<String> subsidiaries;

    // =====================================================
    // ORGANIZATION SETTINGS & PREFERENCES
    // =====================================================

    private Boolean allowPublicApplications;

    private Boolean allowVolunteerReviews;

    private Boolean showImpactMetrics;

    private Boolean showFinancialInfo;

    private Boolean sendNotificationEmails;

    private String recruitmentStrategy; // "PASSIVE", "ACTIVE", "AGGRESSIVE"

    private List<String> communicationPreferences; // "EMAIL", "SMS", "PHONE", "MAIL"

    // =====================================================
    // ORGANIZATION CAPACITY & RESOURCES
    // =====================================================

    @Min(value = 0, message = "Maximum volunteers cannot be negative")
    private Integer maxVolunteersCapacity;

    private Boolean hasTrainingPrograms;

    private Boolean providesMeals;

    private Boolean providesTransportation;

    private Boolean hasInsurance;

    private List<String> facilities; // Available facilities

    private List<String> equipment; // Available equipment

    // =====================================================
    // NOTIFICATION & COMMUNICATION SETTINGS
    // =====================================================

    private Boolean receiveVolunteerApplications;

    private Boolean receiveEventUpdates;

    private Boolean receiveSystemNotifications;

    private String notificationFrequency; // "IMMEDIATE", "DAILY", "WEEKLY", "MONTHLY"

    private Boolean allowDirectMessaging;

    private String responseTimeCommitment; // "WITHIN_24H", "WITHIN_48H", "WITHIN_WEEK"

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public UpdateOrganizationProfileRequest() {
        super();
    }

    public UpdateOrganizationProfileRequest(String organizationName, String organizationType,
                                          String missionStatement, String bio, String location) {
        super(bio, location, null);
        this.organizationName = organizationName;
        this.organizationType = organizationType;
        this.missionStatement = missionStatement;
    }

    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

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

    public Integer getNumberOfEmployees() {
        return numberOfEmployees;
    }

    public void setNumberOfEmployees(Integer numberOfEmployees) {
        this.numberOfEmployees = numberOfEmployees;
    }

    public Double getAnnualBudget() {
        return annualBudget;
    }

    public void setAnnualBudget(Double annualBudget) {
        this.annualBudget = annualBudget;
    }

    public String getBudgetRange() {
        return budgetRange;
    }

    public void setBudgetRange(String budgetRange) {
        this.budgetRange = budgetRange;
    }

    public List<String> getServiceAreas() {
        return serviceAreas;
    }

    public void setServiceAreas(List<String> serviceAreas) {
        this.serviceAreas = serviceAreas;
    }

    public List<String> getCauseAreas() {
        return causeAreas;
    }

    public void setCauseAreas(List<String> causeAreas) {
        this.causeAreas = causeAreas;
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

    public String getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(String mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    public String getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public List<String> getOperatingHours() {
        return operatingHours;
    }

    public void setOperatingHours(List<String> operatingHours) {
        this.operatingHours = operatingHours;
    }

    public Boolean getIsNonprofit() {
        return isNonprofit;
    }

    public void setIsNonprofit(Boolean isNonprofit) {
        this.isNonprofit = isNonprofit;
    }

    public Boolean getIsTaxExempt() {
        return isTaxExempt;
    }

    public void setIsTaxExempt(Boolean isTaxExempt) {
        this.isTaxExempt = isTaxExempt;
    }

    public String getTaxExemptionNumber() {
        return taxExemptionNumber;
    }

    public void setTaxExemptionNumber(String taxExemptionNumber) {
        this.taxExemptionNumber = taxExemptionNumber;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Boolean getAcceptingVolunteers() {
        return acceptingVolunteers;
    }

    public void setAcceptingVolunteers(Boolean acceptingVolunteers) {
        this.acceptingVolunteers = acceptingVolunteers;
    }

    public Boolean getRequiresBackgroundCheck() {
        return requiresBackgroundCheck;
    }

    public void setRequiresBackgroundCheck(Boolean requiresBackgroundCheck) {
        this.requiresBackgroundCheck = requiresBackgroundCheck;
    }

    public Boolean getRequiresOrientation() {
        return requiresOrientation;
    }

    public void setRequiresOrientation(Boolean requiresOrientation) {
        this.requiresOrientation = requiresOrientation;
    }

    public Boolean getRequiresTraining() {
        return requiresTraining;
    }

    public void setRequiresTraining(Boolean requiresTraining) {
        this.requiresTraining = requiresTraining;
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

    public String getVolunteerApplicationProcess() {
        return volunteerApplicationProcess;
    }

    public void setVolunteerApplicationProcess(String volunteerApplicationProcess) {
        this.volunteerApplicationProcess = volunteerApplicationProcess;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public List<String> getPreferredSkills() {
        return preferredSkills;
    }

    public void setPreferredSkills(List<String> preferredSkills) {
        this.preferredSkills = preferredSkills;
    }

    public String getVolunteerCoordinatorName() {
        return volunteerCoordinatorName;
    }

    public void setVolunteerCoordinatorName(String volunteerCoordinatorName) {
        this.volunteerCoordinatorName = volunteerCoordinatorName;
    }

    public String getVolunteerCoordinatorEmail() {
        return volunteerCoordinatorEmail;
    }

    public void setVolunteerCoordinatorEmail(String volunteerCoordinatorEmail) {
        this.volunteerCoordinatorEmail = volunteerCoordinatorEmail;
    }

    public String getImpactStatement() {
        return impactStatement;
    }

    public void setImpactStatement(String impactStatement) {
        this.impactStatement = impactStatement;
    }

    public List<String> getImpactMetrics() {
        return impactMetrics;
    }

    public void setImpactMetrics(List<String> impactMetrics) {
        this.impactMetrics = impactMetrics;
    }

    public Integer getPeopleServed() {
        return peopleServed;
    }

    public void setPeopleServed(Integer peopleServed) {
        this.peopleServed = peopleServed;
    }

    public List<String> getPartnerOrganizations() {
        return partnerOrganizations;
    }

    public void setPartnerOrganizations(List<String> partnerOrganizations) {
        this.partnerOrganizations = partnerOrganizations;
    }

    public List<String> getAffiliations() {
        return affiliations;
    }

    public void setAffiliations(List<String> affiliations) {
        this.affiliations = affiliations;
    }

    public List<String> getAccreditations() {
        return accreditations;
    }

    public void setAccreditations(List<String> accreditations) {
        this.accreditations = accreditations;
    }

    public List<String> getAwards() {
        return awards;
    }

    public void setAwards(List<String> awards) {
        this.awards = awards;
    }

    public String getParentOrganization() {
        return parentOrganization;
    }

    public void setParentOrganization(String parentOrganization) {
        this.parentOrganization = parentOrganization;
    }

    public List<String> getSubsidiaries() {
        return subsidiaries;
    }

    public void setSubsidiaries(List<String> subsidiaries) {
        this.subsidiaries = subsidiaries;
    }

    public Boolean getAllowPublicApplications() {
        return allowPublicApplications;
    }

    public void setAllowPublicApplications(Boolean allowPublicApplications) {
        this.allowPublicApplications = allowPublicApplications;
    }

    public Boolean getAllowVolunteerReviews() {
        return allowVolunteerReviews;
    }

    public void setAllowVolunteerReviews(Boolean allowVolunteerReviews) {
        this.allowVolunteerReviews = allowVolunteerReviews;
    }

    public Boolean getShowImpactMetrics() {
        return showImpactMetrics;
    }

    public void setShowImpactMetrics(Boolean showImpactMetrics) {
        this.showImpactMetrics = showImpactMetrics;
    }

    public Boolean getShowFinancialInfo() {
        return showFinancialInfo;
    }

    public void setShowFinancialInfo(Boolean showFinancialInfo) {
        this.showFinancialInfo = showFinancialInfo;
    }

    public Boolean getSendNotificationEmails() {
        return sendNotificationEmails;
    }

    public void setSendNotificationEmails(Boolean sendNotificationEmails) {
        this.sendNotificationEmails = sendNotificationEmails;
    }

    public String getRecruitmentStrategy() {
        return recruitmentStrategy;
    }

    public void setRecruitmentStrategy(String recruitmentStrategy) {
        this.recruitmentStrategy = recruitmentStrategy;
    }

    public List<String> getCommunicationPreferences() {
        return communicationPreferences;
    }

    public void setCommunicationPreferences(List<String> communicationPreferences) {
        this.communicationPreferences = communicationPreferences;
    }

    public Integer getMaxVolunteersCapacity() {
        return maxVolunteersCapacity;
    }

    public void setMaxVolunteersCapacity(Integer maxVolunteersCapacity) {
        this.maxVolunteersCapacity = maxVolunteersCapacity;
    }

    public Boolean getHasTrainingPrograms() {
        return hasTrainingPrograms;
    }

    public void setHasTrainingPrograms(Boolean hasTrainingPrograms) {
        this.hasTrainingPrograms = hasTrainingPrograms;
    }

    public Boolean getProvidesMeals() {
        return providesMeals;
    }

    public void setProvidesMeals(Boolean providesMeals) {
        this.providesMeals = providesMeals;
    }

    public Boolean getProvidesTransportation() {
        return providesTransportation;
    }

    public void setProvidesTransportation(Boolean providesTransportation) {
        this.providesTransportation = providesTransportation;
    }

    public Boolean getHasInsurance() {
        return hasInsurance;
    }

    public void setHasInsurance(Boolean hasInsurance) {
        this.hasInsurance = hasInsurance;
    }

    public List<String> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<String> facilities) {
        this.facilities = facilities;
    }

    public List<String> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<String> equipment) {
        this.equipment = equipment;
    }

    public Boolean getReceiveVolunteerApplications() {
        return receiveVolunteerApplications;
    }

    public void setReceiveVolunteerApplications(Boolean receiveVolunteerApplications) {
        this.receiveVolunteerApplications = receiveVolunteerApplications;
    }

    public Boolean getReceiveEventUpdates() {
        return receiveEventUpdates;
    }

    public void setReceiveEventUpdates(Boolean receiveEventUpdates) {
        this.receiveEventUpdates = receiveEventUpdates;
    }

    public Boolean getReceiveSystemNotifications() {
        return receiveSystemNotifications;
    }

    public void setReceiveSystemNotifications(Boolean receiveSystemNotifications) {
        this.receiveSystemNotifications = receiveSystemNotifications;
    }

    public String getNotificationFrequency() {
        return notificationFrequency;
    }

    public void setNotificationFrequency(String notificationFrequency) {
        this.notificationFrequency = notificationFrequency;
    }

    public Boolean getAllowDirectMessaging() {
        return allowDirectMessaging;
    }

    public void setAllowDirectMessaging(Boolean allowDirectMessaging) {
        this.allowDirectMessaging = allowDirectMessaging;
    }

    public String getResponseTimeCommitment() {
        return responseTimeCommitment;
    }

    public void setResponseTimeCommitment(String responseTimeCommitment) {
        this.responseTimeCommitment = responseTimeCommitment;
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    /**
     * Checks if organization-specific fields have been updated.
     */
    public boolean hasOrganizationSpecificUpdates() {
        return organizationName != null || organizationType != null || foundedDate != null ||
               missionStatement != null || description != null || numberOfEmployees != null ||
               annualBudget != null || budgetRange != null || serviceAreas != null || causeAreas != null;
    }

    /**
     * Checks if contact information has been updated.
     */
    public boolean hasContactInfoUpdates() {
        return primaryContactName != null || primaryContactTitle != null ||
               primaryContactEmail != null || primaryContactPhone != null ||
               mailingAddress != null || physicalAddress != null ||
               timeZone != null || operatingHours != null;
    }

    /**
     * Checks if volunteer management preferences have been updated.
     */
    public boolean hasVolunteerManagementUpdates() {
        return acceptingVolunteers != null || requiresBackgroundCheck != null ||
               requiresOrientation != null || requiresTraining != null ||
               minimumAge != null || minimumCommitmentHours != null ||
               volunteerApplicationProcess != null || requiredSkills != null ||
               preferredSkills != null || volunteerCoordinatorName != null ||
               volunteerCoordinatorEmail != null;
    }

    /**
     * Checks if organization settings have been updated.
     */
    public boolean hasSettingsUpdates() {
        return allowPublicApplications != null || allowVolunteerReviews != null ||
               showImpactMetrics != null || showFinancialInfo != null ||
               sendNotificationEmails != null || recruitmentStrategy != null ||
               communicationPreferences != null;
    }

    /**
     * Checks if the organization requires any form of volunteer verification.
     */
    public boolean requiresVolunteerVerification() {
        return (requiresBackgroundCheck != null && requiresBackgroundCheck) ||
               (requiresOrientation != null && requiresOrientation) ||
               (requiresTraining != null && requiresTraining);
    }

    /**
     * Gets the organization size category based on number of employees.
     */
    public String getOrganizationSizeCategory() {
        if (numberOfEmployees == null) return null;
        if (numberOfEmployees <= 10) return "SMALL";
        if (numberOfEmployees <= 50) return "MEDIUM";
        if (numberOfEmployees <= 250) return "LARGE";
        return "ENTERPRISE";
    }

    /**
     * Checks if all required contact information is provided.
     */
    public boolean hasCompleteContactInfo() {
        return primaryContactName != null && primaryContactEmail != null &&
               (mailingAddress != null || physicalAddress != null);
    }

    /**
     * Gets a summary of cause areas.
     */
    public String getCauseAreasSummary() {
        if (causeAreas == null || causeAreas.isEmpty()) {
            return "No cause areas specified";
        }
        return String.join(", ", causeAreas);
    }

    /**
     * Validates that all email fields are properly formatted.
     */
    public boolean areEmailsValid() {
        return isValidEmail(primaryContactEmail) && isValidEmail(volunteerCoordinatorEmail);
    }

    /**
     * Helper method to validate email format.
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true; // null/empty emails are valid (optional fields)
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    @Override
    public boolean hasAnyUpdates() {
        return super.hasAnyUpdates() || hasOrganizationSpecificUpdates() ||
               hasContactInfoUpdates() || hasVolunteerManagementUpdates() || hasSettingsUpdates();
    }

    @Override
    public String toString() {
        return "UpdateOrganizationProfileRequest{" +
                "organizationName='" + organizationName + '\'' +
                ", organizationType='" + organizationType + '\'' +
                ", numberOfEmployees=" + numberOfEmployees +
                ", acceptingVolunteers=" + acceptingVolunteers +
                ", causeAreas=" + (causeAreas != null ? causeAreas.size() + " areas" : "null") +
                ", requiresBackgroundCheck=" + requiresBackgroundCheck +
                ", baseUpdates=" + super.hasAnyUpdates() +
                '}';
    }
}