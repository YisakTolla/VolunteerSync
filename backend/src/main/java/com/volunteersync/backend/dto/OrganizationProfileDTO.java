package com.volunteersync.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.volunteersync.backend.dto.VolunteerProfileDTO.ActivityEntry;

import java.util.ArrayList;

public class OrganizationProfileDTO {

    // =====================================================
    // EXISTING FIELDS
    // =====================================================

    private Long id;
    private Long userId;
    private String organizationName;
    private String description;
    private String missionStatement;
    private String website;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String fullAddress;
    private String profileImageUrl;
    private Boolean isVerified;
    private Integer totalEventsHosted;
    private Integer totalVolunteersServed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // =====================================================
    // NEW FIELDS FOR ENHANCED FILTERING
    // =====================================================

    private String categories;
    private List<String> categoryList;
    private String primaryCategory;
    private String organizationType;
    private String organizationSize;
    private Integer employeeCount;
    private String country;
    private String languagesSupported;
    private List<String> languageList;
    private Integer foundedYear;
    private String taxExemptStatus;
    private String verificationLevel;

    // =====================================================
    // COMPUTED FIELDS FOR FRONTEND
    // =====================================================

    private String locationString;
    private String sizeDisplayName;
    private Integer organizationAge;
    private Boolean isNonProfit;
    private Boolean isHighlyVerified;
    private Boolean isInternational;
    private Boolean wasRecentlyUpdated;

    private String coverImageUrl;
    private List<String> services;
    private List<String> causes;
    private Integer fundingGoal;
    private Integer fundingRaised;
    private String ein;
    private String founded; // Frontend-compatible founded field

    // Add these fields for frontend compatibility:
    private List<Achievement> achievements;
    private List<Partnership> partnerships;
    private List<VolunteerSummary> volunteers;
    private List<ActivityEntry> recentActivity;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public OrganizationProfileDTO() {
        this.categoryList = new ArrayList<>();
        this.languageList = new ArrayList<>();
    }

    public OrganizationProfileDTO(Long id, String organizationName, String city, String state,
            Boolean isVerified, Integer totalEventsHosted) {
        this();
        this.id = id;
        this.organizationName = organizationName;
        this.city = city;
        this.state = state;
        this.isVerified = isVerified;
        this.totalEventsHosted = totalEventsHosted;
        updateComputedFields();
    }

    /**
     * Enhanced constructor with filtering fields
     */
    public OrganizationProfileDTO(Long id, String organizationName, String primaryCategory,
            String organizationType, String organizationSize, String country,
            String city, String state, Boolean isVerified, String verificationLevel,
            Integer totalEventsHosted, Integer totalVolunteersServed) {
        this();
        this.id = id;
        this.organizationName = organizationName;
        this.primaryCategory = primaryCategory;
        this.organizationType = organizationType;
        this.organizationSize = organizationSize;
        this.country = country;
        this.city = city;
        this.state = state;
        this.isVerified = isVerified;
        this.verificationLevel = verificationLevel;
        this.totalEventsHosted = totalEventsHosted;
        this.totalVolunteersServed = totalVolunteersServed;
        updateComputedFields();
    }

    // =====================================================
    // COMPUTED FIELD UPDATES
    // =====================================================

    private void updateComputedFields() {
        updateFullAddress();
        updateLocationString();
        updateCategoryList();
        updateLanguageList();
        updateComputedProperties();
    }

    private void updateFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (address != null && !address.trim().isEmpty())
            sb.append(address);
        if (city != null && !city.trim().isEmpty())
            sb.append(sb.length() > 0 ? ", " : "").append(city);
        if (state != null && !state.trim().isEmpty())
            sb.append(sb.length() > 0 ? ", " : "").append(state);
        if (zipCode != null && !zipCode.trim().isEmpty())
            sb.append(sb.length() > 0 ? " " : "").append(zipCode);
        if (country != null && !country.equals("United States"))
            sb.append(sb.length() > 0 ? ", " : "").append(country);
        this.fullAddress = sb.toString();
    }

    private void updateLocationString() {
        StringBuilder location = new StringBuilder();
        if (city != null)
            location.append(city);
        if (state != null)
            location.append(location.length() > 0 ? ", " : "").append(state);
        if (country != null)
            location.append(location.length() > 0 ? ", " : "").append(country);
        this.locationString = location.toString();
    }

    private void updateCategoryList() {
        this.categoryList = new ArrayList<>();
        if (categories != null && !categories.trim().isEmpty()) {
            String[] categoryArray = categories.split(",");
            for (String category : categoryArray) {
                String trimmedCategory = category.trim();
                if (!trimmedCategory.isEmpty()) {
                    this.categoryList.add(trimmedCategory);
                }
            }
        }
    }

    private void updateLanguageList() {
        this.languageList = new ArrayList<>();
        if (languagesSupported != null && !languagesSupported.trim().isEmpty()) {
            String[] languageArray = languagesSupported.split(",");
            for (String language : languageArray) {
                String trimmedLanguage = language.trim();
                if (!trimmedLanguage.isEmpty()) {
                    this.languageList.add(trimmedLanguage);
                }
            }
        }
    }

    private void updateComputedProperties() {
        // Calculate organization age
        if (foundedYear != null) {
            this.organizationAge = LocalDateTime.now().getYear() - foundedYear;
        }

        // Check if non-profit
        this.isNonProfit = organizationType != null &&
                (organizationType.toLowerCase().contains("non-profit") ||
                        organizationType.toLowerCase().contains("nonprofit") ||
                        (taxExemptStatus != null && taxExemptStatus.startsWith("501(c)")));

        // Check verification level
        this.isHighlyVerified = isVerified != null && isVerified &&
                ("Verified".equals(verificationLevel) || "Premium".equals(verificationLevel));

        // Check if international
        this.isInternational = languagesSupported != null &&
                languagesSupported.contains(",") &&
                !languagesSupported.equals("English");

        // Set size display name
        this.sizeDisplayName = organizationSize != null ? organizationSize : "Unknown";

        // Check if recently updated (within 7 days)
        this.wasRecentlyUpdated = updatedAt != null &&
                updatedAt.isAfter(LocalDateTime.now().minusDays(7));
    }

    // =====================================================
    // EXISTING GETTERS AND SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMissionStatement() {
        return missionStatement;
    }

    public void setMissionStatement(String missionStatement) {
        this.missionStatement = missionStatement;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        updateComputedFields();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
        updateComputedFields();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        updateComputedFields();
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
        updateComputedFields();
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
        updateComputedFields();
    }

    public Integer getTotalEventsHosted() {
        return totalEventsHosted;
    }

    public void setTotalEventsHosted(Integer totalEventsHosted) {
        this.totalEventsHosted = totalEventsHosted;
    }

    public Integer getTotalVolunteersServed() {
        return totalVolunteersServed;
    }

    public void setnumberOfVolunteer(Integer totalVolunteersServed) {
        this.totalVolunteersServed = totalVolunteersServed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        updateComputedFields();
    }

    // =====================================================
    // NEW GETTERS AND SETTERS
    // =====================================================

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
        updateCategoryList();
    }

    public List<String> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<String> categoryList) {
        this.categoryList = categoryList;
        if (categoryList != null) {
            this.categories = String.join(",", categoryList);
        }
    }

    public String getPrimaryCategory() {
        return primaryCategory;
    }

    public void setPrimaryCategory(String primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    public String getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
        updateComputedFields();
    }

    public String getOrganizationSize() {
        return organizationSize;
    }

    public void setOrganizationSize(String organizationSize) {
        this.organizationSize = organizationSize;
        updateComputedFields();
    }

    public Integer getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(Integer employeeCount) {
        this.employeeCount = employeeCount;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
        updateComputedFields();
    }

    public String getLanguagesSupported() {
        return languagesSupported;
    }

    public void setLanguagesSupported(String languagesSupported) {
        this.languagesSupported = languagesSupported;
        updateComputedFields();
    }

    public List<String> getLanguageList() {
        return languageList;
    }

    public void setLanguageList(List<String> languageList) {
        this.languageList = languageList;
        if (languageList != null) {
            this.languagesSupported = String.join(",", languageList);
        }
    }

    public Integer getFoundedYear() {
        return foundedYear;
    }

    public void setFoundedYear(Integer foundedYear) {
        this.foundedYear = foundedYear;
        updateComputedFields();
    }

    public String getTaxExemptStatus() {
        return taxExemptStatus;
    }

    public void setTaxExemptStatus(String taxExemptStatus) {
        this.taxExemptStatus = taxExemptStatus;
        updateComputedFields();
    }

    public String getVerificationLevel() {
        return verificationLevel;
    }

    public void setVerificationLevel(String verificationLevel) {
        this.verificationLevel = verificationLevel;
        updateComputedFields();
    }

    // =====================================================
    // COMPUTED FIELD GETTERS
    // =====================================================

    public String getLocationString() {
        return locationString;
    }

    public String getSizeDisplayName() {
        return sizeDisplayName;
    }

    public Integer getOrganizationAge() {
        return organizationAge;
    }

    public Boolean getIsNonProfit() {
        return isNonProfit;
    }

    public Boolean getIsHighlyVerified() {
        return isHighlyVerified;
    }

    public Boolean getIsInternational() {
        return isInternational;
    }

    public Boolean getWasRecentlyUpdated() {
        return wasRecentlyUpdated;
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    /**
     * Check if organization has a specific category
     */
    public boolean hasCategory(String category) {
        return categoryList != null && categoryList.contains(category);
    }

    /**
     * Check if organization supports a specific language
     */
    public boolean supportsLanguage(String language) {
        return languageList != null && languageList.contains(language);
    }

    /**
     * Get primary location (city, state or country)
     */
    public String getPrimaryLocation() {
        if (city != null && state != null) {
            return city + ", " + state;
        } else if (city != null) {
            return city;
        } else if (state != null) {
            return state;
        } else if (country != null) {
            return country;
        }
        return "Location not specified";
    }

    /**
     * Check if organization was updated within specified days
     */
    public boolean wasUpdatedWithinDays(int days) {
        if (updatedAt == null)
            return false;
        LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
        return updatedAt.isAfter(cutoff);
    }

    /**
     * Get verification status display text
     */
    public String getVerificationDisplayText() {
        if (isHighlyVerified != null && isHighlyVerified) {
            return "Highly Verified";
        } else if (isVerified != null && isVerified) {
            return "Verified";
        } else {
            return "Unverified";
        }
    }

    /**
     * Get activity level based on events hosted
     */
    public String getActivityLevel() {
        if (totalEventsHosted == null || totalEventsHosted == 0) {
            return "New";
        } else if (totalEventsHosted <= 5) {
            return "Beginner";
        } else if (totalEventsHosted <= 20) {
            return "Active";
        } else {
            return "Very Active";
        }
    }

    @Override
    public String toString() {
        return "OrganizationProfileDTO{" +
                "id=" + id +
                ", organizationName='" + organizationName + '\'' +
                ", primaryCategory='" + primaryCategory + '\'' +
                ", organizationType='" + organizationType + '\'' +
                ", organizationSize='" + organizationSize + '\'' +
                ", country='" + country + '\'' +
                ", isVerified=" + isVerified +
                ", verificationLevel='" + verificationLevel + '\'' +
                ", totalEventsHosted=" + totalEventsHosted +
                ", totalVolunteersServed=" + totalVolunteersServed +
                '}';
    }

    // Add supporting classes:
    public static class Achievement {
        private Long id;
        private String name;
        private String icon;
        private String description;

        // Constructors, getters, setters
        public Achievement() {
        }

        public Achievement(Long id, String name, String icon, String description) {
            this.id = id;
            this.name = name;
            this.icon = icon;
            this.description = description;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class Partnership {
        private Long id;
        private String name;
        private String type;
        private String since;
        private String logo;

        // Constructors, getters, setters
        public Partnership() {
        }

        public Partnership(Long id, String name, String type, String since, String logo) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.since = since;
            this.logo = logo;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSince() {
            return since;
        }

        public void setSince(String since) {
            this.since = since;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }
    }

    public static class VolunteerSummary {
        private Long id;
        private String name;
        private String role;
        private Integer hoursContributed;
        private String avatar;

        // Constructors, getters, setters
        public VolunteerSummary() {
        }

        public VolunteerSummary(Long id, String name, String role, Integer hoursContributed, String avatar) {
            this.id = id;
            this.name = name;
            this.role = role;
            this.hoursContributed = hoursContributed;
            this.avatar = avatar;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public Integer getHoursContributed() {
            return hoursContributed;
        }

        public void setHoursContributed(Integer hoursContributed) {
            this.hoursContributed = hoursContributed;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }

    public static class ActivityEntry {
        private Long id;
        private String type;
        private String title;
        private String date;
        private String organization;
        private Integer volunteers;

        // Constructors, getters, setters
        public ActivityEntry() {
        }

        public ActivityEntry(Long id, String type, String title, String date, Integer volunteers) {
            this.id = id;
            this.type = type;
            this.title = title;
            this.date = date;
            this.volunteers = volunteers;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getOrganization() {
            return organization;
        }

        public void setOrganization(String organization) {
            this.organization = organization;
        }

        public Integer getVolunteers() {
            return volunteers;
        }

        public void setVolunteers(Integer volunteers) {
            this.volunteers = volunteers;
        }
    }

    // Add getters/setters for all new DTO fields:
    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public List<String> getCauses() {
        return causes;
    }

    public void setCauses(List<String> causes) {
        this.causes = causes;
    }

    public Integer getFundingGoal() {
        return fundingGoal;
    }

    public void setFundingGoal(Integer fundingGoal) {
        this.fundingGoal = fundingGoal;
    }

    public Integer getFundingRaised() {
        return fundingRaised;
    }

    public void setFundingRaised(Integer fundingRaised) {
        this.fundingRaised = fundingRaised;
    }

    public String getEin() {
        return ein;
    }

    public void setEin(String ein) {
        this.ein = ein;
    }

    public String getFounded() {
        return founded;
    }

    public void setFounded(String founded) {
        this.founded = founded;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    public List<Partnership> getPartnerships() {
        return partnerships;
    }

    public void setPartnerships(List<Partnership> partnerships) {
        this.partnerships = partnerships;
    }

    public List<VolunteerSummary> getVolunteers() {
        return volunteers;
    }

    public void setVolunteers(List<VolunteerSummary> volunteers) {
        this.volunteers = volunteers;
    }

    public List<ActivityEntry> getRecentActivity() {
        return recentActivity;
    }

    public void setRecentActivity(List<ActivityEntry> recentActivity) {
        this.recentActivity = recentActivity;
    }

}