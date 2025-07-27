package com.volunteersync.backend.dto.profile;

import com.volunteersync.backend.entity.enums.ProfileVisibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for OrganizationProfile entities.
 * Extends the base ProfileDTO with organization-specific fields including
 * verification status, organization details, impact metrics, and volunteer management.
 * 
 * This DTO is used for API responses and includes both basic profile
 * information and organization-specific data like verification and impact metrics.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationProfileDTO extends ProfileDTO {

    // =====================================================
    // ORGANIZATION BASIC INFORMATION
    // =====================================================

    @Size(max = 100, message = "Organization name cannot exceed 100 characters")
    private String organizationName;

    private String organizationType; // "NONPROFIT", "CHARITY", "NGO", "GOVERNMENT", "CORPORATE", "RELIGIOUS"

    private String taxId; // Tax identification number

    private String registrationNumber; // Official registration number

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate foundedDate;

    @Size(max = 1000, message = "Mission statement cannot exceed 1000 characters")
    private String missionStatement;

    @Size(max = 1000, message = "Organization description cannot exceed 1000 characters")
    private String description;

    // =====================================================
    // ORGANIZATION VERIFICATION & LEGAL
    // =====================================================

    private String verificationStatus; // "PENDING", "VERIFIED", "REJECTED", "NOT_SUBMITTED"

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime verificationSubmittedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime verificationCompletedAt;

    private String verificationNotes;

    private List<String> verificationDocuments; // URLs to uploaded documents

    private Boolean isNonprofit;

    private Boolean isTaxExempt;

    private String taxExemptionNumber;

    // =====================================================
    // ORGANIZATION SIZE & STRUCTURE
    // =====================================================

    @Min(value = 1, message = "Number of employees must be at least 1")
    private Integer numberOfEmployees;

    @Min(value = 0, message = "Number of volunteers cannot be negative")
    private Integer numberOfVolunteers;

    @Min(value = 0, message = "Annual budget cannot be negative")
    private Double annualBudget;

    private String budgetRange; // "UNDER_10K", "10K_50K", "50K_100K", "100K_500K", "500K_1M", "OVER_1M"

    private List<String> serviceAreas; // Geographic areas served

    private List<String> causeAreas; // e.g., ["ENVIRONMENT", "EDUCATION", "HEALTH", "POVERTY"]

    // =====================================================
    // CONTACT & ADMINISTRATIVE INFORMATION
    // =====================================================

    private String primaryContactName;

    private String primaryContactTitle;

    private String primaryContactEmail;

    private String primaryContactPhone;

    private String mailingAddress;

    private String physicalAddress;

    private String timeZone;

    private List<String> operatingHours; // e.g., ["MON:9-17", "TUE:9-17"]

    // =====================================================
    // VOLUNTEER MANAGEMENT & REQUIREMENTS
    // =====================================================

    private Boolean acceptingVolunteers;

    private Boolean requiresBackgroundCheck;

    private Boolean requiresOrientation;

    private Boolean requiresTraining;

    private Integer minimumAge;

    private Integer minimumCommitmentHours;

    private String volunteerApplicationProcess; // "OPEN", "APPROVAL_REQUIRED", "INVITATION_ONLY"

    private List<String> requiredSkills;

    private List<String> preferredSkills;

    private String volunteerCoordinatorName;

    private String volunteerCoordinatorEmail;

    // =====================================================
    // IMPACT METRICS & STATISTICS
    // =====================================================

    @Min(value = 0, message = "Total events hosted cannot be negative")
    private Integer totalEventsHosted;

    @Min(value = 0, message = "Total volunteer hours cannot be negative")
    private Integer totalVolunteerHours;

    @Min(value = 0, message = "People served cannot be negative")
    private Integer peopleServed;

    private String impactStatement;

    private List<String> impactMetrics; // Custom impact measurements

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastEventDate;

    private Double averageVolunteerRating; // Average rating from volunteers

    @Min(value = 0, message = "Total reviews cannot be negative")
    private Integer totalReviews;

    // =====================================================
    // PARTNERSHIPS & AFFILIATIONS
    // =====================================================

    private List<String> partnerOrganizations;

    private List<String> affiliations;

    private List<String> accreditations;

    private List<String> awards;

    private String parentOrganization;

    private List<String> subsidiaries;

    // =====================================================
    // PREFERENCES & SETTINGS
    // =====================================================

    private Boolean allowPublicApplications;

    private Boolean allowVolunteerReviews;

    private Boolean showImpactMetrics;

    private Boolean showFinancialInfo;

    private Boolean sendNotificationEmails;

    private String recruitmentStrategy; // "PASSIVE", "ACTIVE", "AGGRESSIVE"

    private List<String> communicationPreferences; // "EMAIL", "SMS", "PHONE", "MAIL"

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public OrganizationProfileDTO() {
        super();
    }

    public OrganizationProfileDTO(Long id, Long userId, String userType, String bio, String location,
                                 ProfileVisibility visibility, Boolean isActive, Boolean isVerified,
                                 Boolean isCompleted, LocalDateTime createdAt, LocalDateTime updatedAt,
                                 String organizationName, String organizationType, String verificationStatus,
                                 Integer numberOfVolunteers, String missionStatement) {
        super(id, userId, userType, bio, location, visibility, isActive, isVerified, 
              isCompleted, createdAt, updatedAt);
        this.organizationName = organizationName;
        this.organizationType = organizationType;
        this.verificationStatus = verificationStatus;
        this.numberOfVolunteers = numberOfVolunteers;
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

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
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

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public LocalDateTime getVerificationSubmittedAt() {
        return verificationSubmittedAt;
    }

    public void setVerificationSubmittedAt(LocalDateTime verificationSubmittedAt) {
        this.verificationSubmittedAt = verificationSubmittedAt;
    }

    public LocalDateTime getVerificationCompletedAt() {
        return verificationCompletedAt;
    }

    public void setVerificationCompletedAt(LocalDateTime verificationCompletedAt) {
        this.verificationCompletedAt = verificationCompletedAt;
    }

    public String getVerificationNotes() {
        return verificationNotes;
    }

    public void setVerificationNotes(String verificationNotes) {
        this.verificationNotes = verificationNotes;
    }

    public List<String> getVerificationDocuments() {
        return verificationDocuments;
    }

    public void setVerificationDocuments(List<String> verificationDocuments) {
        this.verificationDocuments = verificationDocuments;
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

    public Integer getNumberOfEmployees() {
        return numberOfEmployees;
    }

    public void setNumberOfEmployees(Integer numberOfEmployees) {
        this.numberOfEmployees = numberOfEmployees;
    }

    public Integer getNumberOfVolunteers() {
        return numberOfVolunteers;
    }

    public void setNumberOfVolunteers(Integer numberOfVolunteers) {
        this.numberOfVolunteers = numberOfVolunteers;
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

    public Integer getTotalEventsHosted() {
        return totalEventsHosted;
    }

    public void setTotalEventsHosted(Integer totalEventsHosted) {
        this.totalEventsHosted = totalEventsHosted;
    }

    public Integer getTotalVolunteerHours() {
        return totalVolunteerHours;
    }

    public void setTotalVolunteerHours(Integer totalVolunteerHours) {
        this.totalVolunteerHours = totalVolunteerHours;
    }

    public Integer getPeopleServed() {
        return peopleServed;
    }

    public void setPeopleServed(Integer peopleServed) {
        this.peopleServed = peopleServed;
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

    public LocalDateTime getLastEventDate() {
        return lastEventDate;
    }

    public void setLastEventDate(LocalDateTime lastEventDate) {
        this.lastEventDate = lastEventDate;
    }

    public Double getAverageVolunteerRating() {
        return averageVolunteerRating;
    }

    public void setAverageVolunteerRating(Double averageVolunteerRating) {
        this.averageVolunteerRating = averageVolunteerRating;
    }

    public Integer getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
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

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    /**
     * Checks if the organization is verified.
     */
    public boolean isVerifiedOrganization() {
        return "VERIFIED".equals(verificationStatus);
    }

    /**
     * Checks if the organization is currently accepting volunteers.
     */
    public boolean isCurrentlyAcceptingVolunteers() {
        return acceptingVolunteers != null && acceptingVolunteers;
    }

    /**
     * Gets the organization's size category based on number of employees.
     */
    public String getOrganizationSize() {
        if (numberOfEmployees == null) return "UNKNOWN";
        if (numberOfEmployees <= 10) return "SMALL";
        if (numberOfEmployees <= 50) return "MEDIUM";
        if (numberOfEmployees <= 250) return "LARGE";
        return "ENTERPRISE";
    }

    /**
     * Checks if background check is required for volunteers.
     */
    public boolean requiresVolunteerBackgroundCheck() {
        return requiresBackgroundCheck != null && requiresBackgroundCheck;
    }

    /**
     * Gets the number of cause areas the organization serves.
     */
    public int getCauseAreaCount() {
        return causeAreas != null ? causeAreas.size() : 0;
    }

    /**
     * Gets the organization's years of operation.
     */
    public Integer getYearsOfOperation() {
        if (foundedDate != null) {
            return LocalDate.now().getYear() - foundedDate.getYear();
        }
        return null;
    }

    @Override
    public String toString() {
        return "OrganizationProfileDTO{" +
                "id=" + getId() +
                ", userId=" + getUserId() +
                ", organizationName='" + organizationName + '\'' +
                ", organizationType='" + organizationType + '\'' +
                ", verificationStatus='" + verificationStatus + '\'' +
                ", numberOfVolunteers=" + numberOfVolunteers +
                ", totalEventsHosted=" + totalEventsHosted +
                ", acceptingVolunteers=" + acceptingVolunteers +
                '}';
    }
}