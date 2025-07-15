package com.volunteersync.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class GoogleAuthRequest {
    
    @NotBlank(message = "Google token is required")
    private String googleToken;
    
    @NotBlank(message = "User type is required")
    private String userType; // "VOLUNTEER" or "ORGANIZATION"
    
    // Default constructor
    public GoogleAuthRequest() {}
    
    // Constructor with all fields
    public GoogleAuthRequest(String googleToken, String userType) {
        this.googleToken = googleToken;
        this.userType = userType;
    }
    
    // Getters
    public String getGoogleToken() {
        return googleToken;
    }
    
    public String getUserType() {
        return userType;
    }
    
    // Setters
    public void setGoogleToken(String googleToken) {
        this.googleToken = googleToken;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    @Override
    public String toString() {
        return "GoogleAuthRequest{" +
                "userType='" + userType + '\'' +
                '}';
    }
}