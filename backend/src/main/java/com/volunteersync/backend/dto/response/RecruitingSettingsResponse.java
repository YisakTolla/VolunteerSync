package com.volunteersync.backend.dto.response;

import java.util.List;

public class RecruitingSettingsResponse {
    private Boolean autoAcceptApplications;
    private List<String> preferredSkills;
    private String recruitingMessage;
    private Integer maxVolunteersPerEvent;
    
    // Constructors
    public RecruitingSettingsResponse() {}
    
    public RecruitingSettingsResponse(Boolean autoAcceptApplications, List<String> preferredSkills, 
                                     String recruitingMessage, Integer maxVolunteersPerEvent) {
        this.autoAcceptApplications = autoAcceptApplications;
        this.preferredSkills = preferredSkills;
        this.recruitingMessage = recruitingMessage;
        this.maxVolunteersPerEvent = maxVolunteersPerEvent;
    }
    
    // Getters and Setters
    public Boolean getAutoAcceptApplications() {
        return autoAcceptApplications;
    }
    
    public void setAutoAcceptApplications(Boolean autoAcceptApplications) {
        this.autoAcceptApplications = autoAcceptApplications;
    }
    
    public List<String> getPreferredSkills() {
        return preferredSkills;
    }
    
    public void setPreferredSkills(List<String> preferredSkills) {
        this.preferredSkills = preferredSkills;
    }
    
    public String getRecruitingMessage() {
        return recruitingMessage;
    }
    
    public void setRecruitingMessage(String recruitingMessage) {
        this.recruitingMessage = recruitingMessage;
    }
    
    public Integer getMaxVolunteersPerEvent() {
        return maxVolunteersPerEvent;
    }
    
    public void setMaxVolunteersPerEvent(Integer maxVolunteersPerEvent) {
        this.maxVolunteersPerEvent = maxVolunteersPerEvent;
    }
    
    // toString, equals, hashCode
    @Override
    public String toString() {
        return "RecruitingSettingsResponse{" +
                "autoAcceptApplications=" + autoAcceptApplications +
                ", preferredSkills=" + preferredSkills +
                ", recruitingMessage='" + recruitingMessage + '\'' +
                ", maxVolunteersPerEvent=" + maxVolunteersPerEvent +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        RecruitingSettingsResponse that = (RecruitingSettingsResponse) o;
        
        if (autoAcceptApplications != null ? !autoAcceptApplications.equals(that.autoAcceptApplications) : that.autoAcceptApplications != null) return false;
        if (preferredSkills != null ? !preferredSkills.equals(that.preferredSkills) : that.preferredSkills != null) return false;
        if (recruitingMessage != null ? !recruitingMessage.equals(that.recruitingMessage) : that.recruitingMessage != null) return false;
        return maxVolunteersPerEvent != null ? maxVolunteersPerEvent.equals(that.maxVolunteersPerEvent) : that.maxVolunteersPerEvent == null;
    }
    
    @Override
    public int hashCode() {
        int result = autoAcceptApplications != null ? autoAcceptApplications.hashCode() : 0;
        result = 31 * result + (preferredSkills != null ? preferredSkills.hashCode() : 0);
        result = 31 * result + (recruitingMessage != null ? recruitingMessage.hashCode() : 0);
        result = 31 * result + (maxVolunteersPerEvent != null ? maxVolunteersPerEvent.hashCode() : 0);
        return result;
    }
}