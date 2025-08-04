package com.volunteersync.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "organization_profiles")
public class OrganizationProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "organization_name", nullable = false)
    private String organizationName;
    
    @Column(length = 2000)
    private String description;
    
    @Column(name = "mission_statement", length = 1000)
    private String missionStatement;
    
    private String website;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    // =====================================================
    // NEW FIELDS FOR ENHANCED FILTERING
    // =====================================================
    
    /**
     * Organization Category/Focus Areas - NEW FIELD for Category filtering
     * Multiple categories can be stored as comma-separated values
     */
    @Column(name = "categories", length = 500)
    private String categories; // "Education,Healthcare,Environment"
    
    /**
     * Primary Category - NEW FIELD for main category classification
     */
    @Column(name = "primary_category")
    private String primaryCategory; // "Education", "Environment", "Healthcare", etc.
    
    /**
     * Organization Type - NEW FIELD for type classification
     */
    @Column(name = "organization_type")
    private String organizationType; // "Non-Profit", "Religious", "Educational", "Government"
    
    /**
     * Organization Size Category - ENHANCED FIELD for size filtering
     */
    @Column(name = "organization_size")
    private String organizationSize; // "Small (1-50)", "Medium (51-200)", "Large (201-1000)", "Enterprise (1000+)"
    
    /**
     * Number of Employees - NEW FIELD for precise size calculation
     */
    @Column(name = "employee_count")
    private Integer employeeCount;
    
    /**
     * Country - ENHANCED FIELD for international filtering
     */
    @Column(name = "country")
    private String country = "United States";
    
    /**
     * Language Support - NEW FIELD for international organizations
     */
    @Column(name = "languages_supported")
    private String languagesSupported; // "English,Spanish,French"
    
    /**
     * Founded Year - NEW FIELD for organization age filtering
     */
    @Column(name = "founded_year")
    private Integer foundedYear;
    
    /**
     * Tax Exempt Status - NEW FIELD for legal classification
     */
    @Column(name = "tax_exempt_status")
    private String taxExemptStatus; // "501(c)(3)", "501(c)(4)", etc.
    
    /**
     * Verification Status - ENHANCED FIELD
     */
    @Column(name = "verification_level")
    private String verificationLevel; // "Unverified", "Basic", "Verified", "Premium"
    
    // =====================================================
    // EXISTING FIELDS (KEEP THESE)
    // =====================================================
    
    private String address;
    private String city;
    private String state;
    
    @Column(name = "zip_code")
    private String zipCode;
    
    @Column(name = "profile_image_url")
    private String profileImageUrl;
    
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    
    @Column(name = "total_events_hosted")
    private Integer totalEventsHosted = 0;
    
    @Column(name = "total_volunteers_served")
    private Integer totalVolunteersServed = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // =====================================================
    // CONSTRUCTORS
    // =====================================================
    
    public OrganizationProfile() {}
    
    public OrganizationProfile(User user, String organizationName) {
        this.user = user;
        this.organizationName = organizationName;
        this.updateOrganizationSize();
    }

    // =====================================================
    // NEW HELPER METHODS FOR FILTERING
    // =====================================================
    
    /**
     * Get list of categories as individual strings
     */
    public List<String> getCategoryList() {
        List<String> categoryList = new ArrayList<>();
        if (categories != null && !categories.trim().isEmpty()) {
            String[] categoryArray = categories.split(",");
            for (String category : categoryArray) {
                categoryList.add(category.trim());
            }
        }
        return categoryList;
    }
    
    /**
     * Add a category to the organization
     */
    public void addCategory(String category) {
        if (category == null || category.trim().isEmpty()) return;
        
        List<String> currentCategories = getCategoryList();
        if (!currentCategories.contains(category.trim())) {
            currentCategories.add(category.trim());
            this.categories = String.join(",", currentCategories);
        }
    }
    
    /**
     * Remove a category from the organization
     */
    public void removeCategory(String category) {
        if (category == null || category.trim().isEmpty()) return;
        
        List<String> currentCategories = getCategoryList();
        currentCategories.remove(category.trim());
        this.categories = String.join(",", currentCategories);
    }
    
    /**
     * Check if organization serves a specific category
     */
    public boolean hasCategory(String category) {
        return getCategoryList().contains(category);
    }
    
    /**
     * Automatically determine organization size based on employee count
     */
    private void updateOrganizationSize() {
        if (employeeCount != null) {
            if (employeeCount <= 50) {
                this.organizationSize = "Small (1-50)";
            } else if (employeeCount <= 200) {
                this.organizationSize = "Medium (51-200)";
            } else if (employeeCount <= 1000) {
                this.organizationSize = "Large (201-1000)";
            } else {
                this.organizationSize = "Enterprise (1000+)";
            }
        }
    }
    
    /**
     * Get supported languages as list
     */
    public List<String> getLanguageList() {
        List<String> languageList = new ArrayList<>();
        if (languagesSupported != null && !languagesSupported.trim().isEmpty()) {
            String[] languageArray = languagesSupported.split(",");
            for (String language : languageArray) {
                languageList.add(language.trim());
            }
        }
        return languageList;
    }
    
    /**
     * Check if organization was updated within specified days
     */
    public boolean wasUpdatedWithinDays(int days) {
        if (updatedAt == null) return false;
        LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
        return updatedAt.isAfter(cutoff);
    }
    
    /**
     * Get organization age in years
     */
    public Integer getOrganizationAge() {
        if (foundedYear != null) {
            return LocalDateTime.now().getYear() - foundedYear;
        }
        return null;
    }
    
    /**
     * Check if organization is international (operates outside home country)
     */
    public boolean isInternational() {
        return languagesSupported != null && 
               languagesSupported.contains(",") && 
               !languagesSupported.equals("English");
    }

    // =====================================================
    // GETTERS AND SETTERS FOR NEW FIELDS
    // =====================================================
    
    public String getCategories() { 
        return categories; 
    }
    
    public void setCategories(String categories) { 
        this.categories = categories; 
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
    }
    
    public String getOrganizationSize() { 
        return organizationSize; 
    }
    
    public void setOrganizationSize(String organizationSize) { 
        this.organizationSize = organizationSize; 
    }
    
    public Integer getEmployeeCount() { 
        return employeeCount; 
    }
    
    public void setEmployeeCount(Integer employeeCount) { 
        this.employeeCount = employeeCount;
        this.updateOrganizationSize();
    }
    
    public String getCountry() { 
        return country; 
    }
    
    public void setCountry(String country) { 
        this.country = country; 
    }
    
    public String getLanguagesSupported() { 
        return languagesSupported; 
    }
    
    public void setLanguagesSupported(String languagesSupported) { 
        this.languagesSupported = languagesSupported; 
    }
    
    public Integer getFoundedYear() { 
        return foundedYear; 
    }
    
    public void setFoundedYear(Integer foundedYear) { 
        this.foundedYear = foundedYear; 
    }
    
    public String getTaxExemptStatus() { 
        return taxExemptStatus; 
    }
    
    public void setTaxExemptStatus(String taxExemptStatus) { 
        this.taxExemptStatus = taxExemptStatus; 
    }
    
    public String getVerificationLevel() { 
        return verificationLevel; 
    }
    
    public void setVerificationLevel(String verificationLevel) { 
        this.verificationLevel = verificationLevel; 
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

    public User getUser() { 
        return user; 
    }
    
    public void setUser(User user) { 
        this.user = user; 
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
    
    public void setTotalVolunteersServed(Integer totalVolunteersServed) { 
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
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================
    
    /**
     * Get full address as formatted string
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (address != null) sb.append(address);
        if (city != null) sb.append(sb.length() > 0 ? ", " : "").append(city);
        if (state != null) sb.append(sb.length() > 0 ? ", " : "").append(state);
        if (zipCode != null) sb.append(sb.length() > 0 ? " " : "").append(zipCode);
        if (country != null && !country.equals("United States")) {
            sb.append(sb.length() > 0 ? ", " : "").append(country);
        }
        return sb.toString();
    }
    
    /**
     * Get location for filtering (City, State, Country)
     */
    public String getLocationString() {
        StringBuilder location = new StringBuilder();
        if (city != null) location.append(city);
        if (state != null) location.append(location.length() > 0 ? ", " : "").append(state);
        if (country != null) location.append(location.length() > 0 ? ", " : "").append(country);
        return location.toString();
    }
    
    /**
     * Check if organization is non-profit
     */
    public boolean isNonProfit() {
        return organizationType != null && 
               (organizationType.toLowerCase().contains("non-profit") ||
                organizationType.toLowerCase().contains("nonprofit") ||
                (taxExemptStatus != null && taxExemptStatus.startsWith("501(c)")));
    }
    
    /**
     * Check if organization is highly verified
     */
    public boolean isHighlyVerified() {
        return isVerified != null && isVerified && 
               ("Verified".equals(verificationLevel) || "Premium".equals(verificationLevel));
    }
    
    /**
     * Get display name for size category
     */
    public String getSizeDisplayName() {
        if (organizationSize == null) return "Unknown";
        return organizationSize;
    }

    // =====================================================
    // JPA LIFECYCLE METHODS
    // =====================================================
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.updateOrganizationSize();
    }
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.updateOrganizationSize();
        
        // Set default values
        if (country == null) {
            country = "United States";
        }
        if (verificationLevel == null) {
            verificationLevel = "Unverified";
        }
    }

    @Override
    public String toString() {
        return "OrganizationProfile{" +
                "id=" + id +
                ", organizationName='" + organizationName + '\'' +
                ", primaryCategory='" + primaryCategory + '\'' +
                ", organizationType='" + organizationType + '\'' +
                ", organizationSize='" + organizationSize + '\'' +
                ", country='" + country + '\'' +
                ", isVerified=" + isVerified +
                ", verificationLevel='" + verificationLevel + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganizationProfile that = (OrganizationProfile) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}