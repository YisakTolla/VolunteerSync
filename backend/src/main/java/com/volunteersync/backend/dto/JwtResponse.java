package com.volunteersync.backend.dto;

import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.entity.UserType;

public class JwtResponse {
    
    private String token;
    private String type = "Bearer";
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private UserType userType;
    private String profilePicture;
    
    // Default constructor
    public JwtResponse() {}
    
    // Constructor with token and user
    public JwtResponse(String token, User user) {
        this.token = token;
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.userType = user.getUserType();
        this.profilePicture = user.getProfilePicture();
    }
    
    // Constructor with all fields
    public JwtResponse(String token, String type, Long id, String firstName, 
                      String lastName, String email, UserType userType, String profilePicture) {
        this.token = token;
        this.type = type;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userType = userType;
        this.profilePicture = profilePicture;
    }
    
    // Getters
    public String getToken() {
        return token;
    }
    
    public String getType() {
        return type;
    }
    
    public Long getId() {
        return id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public UserType getUserType() {
        return userType;
    }
    
    public String getProfilePicture() {
        return profilePicture;
    }
    
    // Setters
    public void setToken(String token) {
        this.token = token;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setUserType(UserType userType) {
        this.userType = userType;
    }
    
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    
    // Helper method to get full name
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public String toString() {
        return "JwtResponse{" +
                "token='" + token + '\'' +
                ", type='" + type + '\'' +
                ", id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", userType=" + userType +
                '}';
    }
}