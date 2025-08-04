package com.volunteersync.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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

    // Constructors
    public OrganizationProfile() {}
    
    public OrganizationProfile(User user, String organizationName) {
        this.user = user;
        this.organizationName = organizationName;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMissionStatement() { return missionStatement; }
    public void setMissionStatement(String missionStatement) { this.missionStatement = missionStatement; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (address != null) sb.append(address);
        if (city != null) sb.append(sb.length() > 0 ? ", " : "").append(city);
        if (state != null) sb.append(sb.length() > 0 ? ", " : "").append(state);
        if (zipCode != null) sb.append(sb.length() > 0 ? " " : "").append(zipCode);
        return sb.toString();
    }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public Integer getTotalEventsHosted() { return totalEventsHosted; }
    public void setTotalEventsHosted(Integer totalEventsHosted) { this.totalEventsHosted = totalEventsHosted; }

    public Integer getTotalVolunteersServed() { return totalVolunteersServed; }
    public void setTotalVolunteersServed(Integer totalVolunteersServed) { this.totalVolunteersServed = totalVolunteersServed; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}