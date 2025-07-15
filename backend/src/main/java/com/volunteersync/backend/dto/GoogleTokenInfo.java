package com.volunteersync.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GoogleTokenInfo {
    
    @JsonProperty("sub")
    private String sub; // Google user ID
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("email_verified")
    private boolean emailVerified;
    
    @JsonProperty("given_name")
    private String givenName;
    
    @JsonProperty("family_name")
    private String familyName;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("picture")
    private String picture;
    
    @JsonProperty("locale")
    private String locale;
    
    @JsonProperty("aud")
    private String audience; // Client ID
    
    @JsonProperty("iss")
    private String issuer;
    
    @JsonProperty("iat")
    private Long issuedAt;
    
    @JsonProperty("exp")
    private Long expiresAt;
    
    // Default constructor
    public GoogleTokenInfo() {}
    
    // Constructor with essential fields
    public GoogleTokenInfo(String sub, String email, String givenName, 
                          String familyName, String picture) {
        this.sub = sub;
        this.email = email;
        this.givenName = givenName;
        this.familyName = familyName;
        this.picture = picture;
    }
    
    // Getters
    public String getSub() {
        return sub;
    }
    
    public String getEmail() {
        return email;
    }
    
    public boolean isEmailVerified() {
        return emailVerified;
    }
    
    public String getGivenName() {
        return givenName;
    }
    
    public String getFamilyName() {
        return familyName;
    }
    
    public String getName() {
        return name;
    }
    
    public String getPicture() {
        return picture;
    }
    
    public String getLocale() {
        return locale;
    }
    
    public String getAudience() {
        return audience;
    }
    
    public String getIssuer() {
        return issuer;
    }
    
    public Long getIssuedAt() {
        return issuedAt;
    }
    
    public Long getExpiresAt() {
        return expiresAt;
    }
    
    // Setters
    public void setSub(String sub) {
        this.sub = sub;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
    
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }
    
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setPicture(String picture) {
        this.picture = picture;
    }
    
    public void setLocale(String locale) {
        this.locale = locale;
    }
    
    public void setAudience(String audience) {
        this.audience = audience;
    }
    
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
    
    public void setIssuedAt(Long issuedAt) {
        this.issuedAt = issuedAt;
    }
    
    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    // Helper method to get full name
    public String getFullName() {
        if (name != null && !name.isEmpty()) {
            return name;
        }
        return (givenName != null ? givenName : "") + " " + (familyName != null ? familyName : "");
    }
    
    // Helper method to check if token is expired
    public boolean isExpired() {
        if (expiresAt == null) return false;
        return System.currentTimeMillis() / 1000 > expiresAt;
    }
    
    @Override
    public String toString() {
        return "GoogleTokenInfo{" +
                "sub='" + sub + '\'' +
                ", email='" + email + '\'' +
                ", emailVerified=" + emailVerified +
                ", givenName='" + givenName + '\'' +
                ", familyName='" + familyName + '\'' +
                ", name='" + name + '\'' +
                ", picture='" + picture + '\'' +
                ", locale='" + locale + '\'' +
                '}';
    }
}