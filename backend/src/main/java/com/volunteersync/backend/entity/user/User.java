package com.volunteersync.backend.entity.user;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // For volunteers - nullable for organizations
    @Column(nullable = true)
    private String firstName;
    
    @Column(nullable = true)
    private String lastName;
    
    // For organizations - nullable for volunteers
    @Column(nullable = true)
    private String organizationName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column
    private String password; // null for OAuth users
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType; // VOLUNTEER, ORGANIZATION
    
    @Column
    private String googleId; // for OAuth users
    
    @Column
    private String profilePicture;
    
    // 🔧 NEW FIELDS FOR PROFILE COMPLETION
    @Column(columnDefinition = "TEXT")
    private String bio; // User's biography/description
    
    @Column
    private String location; // User's location (city, state)
    
    @Column
    private String phone; // User's phone number
    
    @Column(columnDefinition = "TEXT")
    private String interests; // Comma-separated list of interests
    
    @Column(columnDefinition = "TEXT") 
    private String skills; // Comma-separated list of skills
    
    @Column
    private String availability; // User's availability (weekends, weekdays, flexible)
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // ========================================
    // CONSTRUCTORS
    // ========================================
    
    // Default constructor
    public User() {}
    
    // Constructor for volunteers
    public User(String firstName, String lastName, String email, String password, UserType userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }
    
    // Constructor for organizations
    public User(String organizationName, String email, String password, UserType userType) {
        this.organizationName = organizationName;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }
    
    // Constructor with all fields (useful for flexibility)
    public User(String firstName, String lastName, String organizationName, String email, 
                String password, UserType userType, String googleId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.organizationName = organizationName;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.googleId = googleId;
    }
    
    // ========================================
    // GETTERS - BASIC FIELDS
    // ========================================
    
    public Long getId() {
        return id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getOrganizationName() {
        return organizationName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public UserType getUserType() {
        return userType;
    }
    
    public String getGoogleId() {
        return googleId;
    }
    
    public String getProfilePicture() {
        return profilePicture;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // ========================================
    // GETTERS - NEW PROFILE FIELDS
    // ========================================
    
    public String getBio() {
        return bio;
    }
    
    public String getLocation() {
        return location;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public String getInterests() {
        return interests;
    }
    
    public String getSkills() {
        return skills;
    }
    
    public String getAvailability() {
        return availability;
    }
    
    // ========================================
    // SETTERS - BASIC FIELDS
    // ========================================
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setUserType(UserType userType) {
        this.userType = userType;
    }
    
    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }
    
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // ========================================
    // SETTERS - NEW PROFILE FIELDS
    // ========================================
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public void setInterests(String interests) {
        this.interests = interests;
    }
    
    public void setSkills(String skills) {
        this.skills = skills;
    }
    
    public void setAvailability(String availability) {
        this.availability = availability;
    }
    
    // ========================================
    // HELPER METHODS
    // ========================================
    
    // Helper methods to check user type
    public boolean isVolunteer() {
        return this.userType == UserType.VOLUNTEER;
    }
    
    public boolean isOrganization() {
        return this.userType == UserType.ORGANIZATION;
    }
    
    // Get display name based on user type
    public String getDisplayName() {
        if (isOrganization()) {
            return organizationName;
        } else if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return null;
    }
    
    // Get full name (for volunteers only)
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return null;
    }
    
    // Validation helper - check if user has required fields for their type
    public boolean hasValidFields() {
        if (isVolunteer()) {
            return firstName != null && !firstName.trim().isEmpty() && 
                   lastName != null && !lastName.trim().isEmpty();
        } else if (isOrganization()) {
            return organizationName != null && !organizationName.trim().isEmpty();
        }
        return false;
    }
    
    // Check if profile is complete (for profile setup flow)
    public boolean isProfileComplete() {
        // Basic fields must be valid
        if (!hasValidFields()) {
            return false;
        }
        
        // Check for additional profile information
        boolean hasLocation = location != null && !location.trim().isEmpty();
        boolean hasBio = bio != null && !bio.trim().isEmpty();
        
        return hasLocation && hasBio;
    }
    
    // ========================================
    // OBJECT METHODS
    // ========================================
    
    @Override
    public String toString() {
        if (isOrganization()) {
            return "User{" +
                    "id=" + id +
                    ", organizationName='" + organizationName + '\'' +
                    ", email='" + email + '\'' +
                    ", userType=" + userType +
                    ", location='" + location + '\'' +
                    ", createdAt=" + createdAt +
                    '}';
        } else {
            return "User{" +
                    "id=" + id +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", email='" + email + '\'' +
                    ", userType=" + userType +
                    ", location='" + location + '\'' +
                    ", createdAt=" + createdAt +
                    '}';
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        User user = (User) o;
        return email.equals(user.email);
    }
    
    @Override
    public int hashCode() {
        return email.hashCode();
    }
}