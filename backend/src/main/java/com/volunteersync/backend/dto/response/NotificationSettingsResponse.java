package com.volunteersync.backend.dto.response;

public class NotificationSettingsResponse {
    private Boolean emailNotifications;
    private Boolean pushNotifications;
    private Boolean smsNotifications;
    private Boolean eventReminders;
    private Boolean applicationUpdates;
    
    // Constructors
    public NotificationSettingsResponse() {}
    
    public NotificationSettingsResponse(Boolean emailNotifications, Boolean pushNotifications, 
                                       Boolean smsNotifications, Boolean eventReminders, 
                                       Boolean applicationUpdates) {
        this.emailNotifications = emailNotifications;
        this.pushNotifications = pushNotifications;
        this.smsNotifications = smsNotifications;
        this.eventReminders = eventReminders;
        this.applicationUpdates = applicationUpdates;
    }
    
    // Getters and Setters
    public Boolean getEmailNotifications() {
        return emailNotifications;
    }
    
    public void setEmailNotifications(Boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }
    
    public Boolean getPushNotifications() {
        return pushNotifications;
    }
    
    public void setPushNotifications(Boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
    }
    
    public Boolean getSmsNotifications() {
        return smsNotifications;
    }
    
    public void setSmsNotifications(Boolean smsNotifications) {
        this.smsNotifications = smsNotifications;
    }
    
    public Boolean getEventReminders() {
        return eventReminders;
    }
    
    public void setEventReminders(Boolean eventReminders) {
        this.eventReminders = eventReminders;
    }
    
    public Boolean getApplicationUpdates() {
        return applicationUpdates;
    }
    
    public void setApplicationUpdates(Boolean applicationUpdates) {
        this.applicationUpdates = applicationUpdates;
    }
    
    // toString, equals, hashCode
    @Override
    public String toString() {
        return "NotificationSettingsResponse{" +
                "emailNotifications=" + emailNotifications +
                ", pushNotifications=" + pushNotifications +
                ", smsNotifications=" + smsNotifications +
                ", eventReminders=" + eventReminders +
                ", applicationUpdates=" + applicationUpdates +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        NotificationSettingsResponse that = (NotificationSettingsResponse) o;
        
        if (emailNotifications != null ? !emailNotifications.equals(that.emailNotifications) : that.emailNotifications != null) return false;
        if (pushNotifications != null ? !pushNotifications.equals(that.pushNotifications) : that.pushNotifications != null) return false;
        if (smsNotifications != null ? !smsNotifications.equals(that.smsNotifications) : that.smsNotifications != null) return false;
        if (eventReminders != null ? !eventReminders.equals(that.eventReminders) : that.eventReminders != null) return false;
        return applicationUpdates != null ? applicationUpdates.equals(that.applicationUpdates) : that.applicationUpdates == null;
    }
    
    @Override
    public int hashCode() {
        int result = emailNotifications != null ? emailNotifications.hashCode() : 0;
        result = 31 * result + (pushNotifications != null ? pushNotifications.hashCode() : 0);
        result = 31 * result + (smsNotifications != null ? smsNotifications.hashCode() : 0);
        result = 31 * result + (eventReminders != null ? eventReminders.hashCode() : 0);
        result = 31 * result + (applicationUpdates != null ? applicationUpdates.hashCode() : 0);
        return result;
    }
}