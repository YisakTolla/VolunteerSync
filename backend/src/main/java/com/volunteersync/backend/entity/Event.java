package com.volunteersync.backend.entity;

import com.volunteersync.backend.enums.EventStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private OrganizationProfile organization;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 2000)
    private String description;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    private String location;
    private String address;
    private String city;
    private String state;
    
    @Column(name = "zip_code")
    private String zipCode;
    
    @Column(name = "max_volunteers")
    private Integer maxVolunteers;
    
    @Column(name = "current_volunteers")
    private Integer currentVolunteers = 0;
    
    @Column(name = "estimated_hours")
    private Integer estimatedHours;
    
    @Enumerated(EnumType.STRING)
    private EventStatus status = EventStatus.DRAFT;
    
    @Column(name = "requirements", length = 1000)
    private String requirements;
    
    @Column(name = "contact_email")
    private String contactEmail;
    
    @Column(name = "contact_phone")
    private String contactPhone;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Constructors
    public Event() {}
    
    public Event(OrganizationProfile organization, String title, LocalDateTime startDate) {
        this.organization = organization;
        this.title = title;
        this.startDate = startDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public OrganizationProfile getOrganization() { return organization; }
    public void setOrganization(OrganizationProfile organization) { this.organization = organization; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

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

    public Integer getMaxVolunteers() { return maxVolunteers; }
    public void setMaxVolunteers(Integer maxVolunteers) { this.maxVolunteers = maxVolunteers; }

    public Integer getCurrentVolunteers() { return currentVolunteers; }
    public void setCurrentVolunteers(Integer currentVolunteers) { this.currentVolunteers = currentVolunteers; }

    public Integer getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(Integer estimatedHours) { this.estimatedHours = estimatedHours; }

    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public boolean isFull() {
        return maxVolunteers != null && currentVolunteers != null && 
               currentVolunteers >= maxVolunteers;
    }

    public int getSpotsRemaining() {
        if (maxVolunteers == null) return Integer.MAX_VALUE;
        return Math.max(0, maxVolunteers - (currentVolunteers != null ? currentVolunteers : 0));
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}