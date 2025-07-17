package com.volunteersync.backend.entity;

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
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
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
    
    // Getters
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
    
    // Setters
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
    
    // Helper methods
    public boolean isVolunteer() {
        return UserType.VOLUNTEER.equals(userType);
    }
    
    public boolean isOrganization() {
        return UserType.ORGANIZATION.equals(userType);
    }
    
    // Get display name based on user type
    public String getDisplayName() {
        if (isOrganization() && organizationName != null) {
            return organizationName;
        } else if (isVolunteer() && firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return email; // fallback to email if names are not available
    }
    
    // Get full name for volunteers (returns null for organizations)
    public String getFullName() {
        if (isVolunteer() && firstName != null && lastName != null) {
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
    
    @Override
    public String toString() {
        if (isOrganization()) {
            return "User{" +
                    "id=" + id +
                    ", organizationName='" + organizationName + '\'' +
                    ", email='" + email + '\'' +
                    ", userType=" + userType +
                    ", createdAt=" + createdAt +
                    '}';
        } else {
            return "User{" +
                    "id=" + id +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", email='" + email + '\'' +
                    ", userType=" + userType +
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