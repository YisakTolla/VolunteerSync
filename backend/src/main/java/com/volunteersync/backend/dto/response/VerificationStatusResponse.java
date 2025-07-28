package com.volunteersync.backend.dto.response;

import java.time.LocalDateTime;

public class VerificationStatusResponse {
    private String status;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private String reviewNotes;
    
    // Constructors
    public VerificationStatusResponse() {}
    
    public VerificationStatusResponse(String status, LocalDateTime submittedAt, 
                                    LocalDateTime reviewedAt, String reviewNotes) {
        this.status = status;
        this.submittedAt = submittedAt;
        this.reviewedAt = reviewedAt;
        this.reviewNotes = reviewNotes;
    }
    
    // Getters and Setters
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
    
    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
    
    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }
    
    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
    
    public String getReviewNotes() {
        return reviewNotes;
    }
    
    public void setReviewNotes(String reviewNotes) {
        this.reviewNotes = reviewNotes;
    }
    
    // toString, equals, hashCode
    @Override
    public String toString() {
        return "VerificationStatusResponse{" +
                "status='" + status + '\'' +
                ", submittedAt=" + submittedAt +
                ", reviewedAt=" + reviewedAt +
                ", reviewNotes='" + reviewNotes + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        VerificationStatusResponse that = (VerificationStatusResponse) o;
        
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (submittedAt != null ? !submittedAt.equals(that.submittedAt) : that.submittedAt != null) return false;
        if (reviewedAt != null ? !reviewedAt.equals(that.reviewedAt) : that.reviewedAt != null) return false;
        return reviewNotes != null ? reviewNotes.equals(that.reviewNotes) : that.reviewNotes == null;
    }
    
    @Override
    public int hashCode() {
        int result = status != null ? status.hashCode() : 0;
        result = 31 * result + (submittedAt != null ? submittedAt.hashCode() : 0);
        result = 31 * result + (reviewedAt != null ? reviewedAt.hashCode() : 0);
        result = 31 * result + (reviewNotes != null ? reviewNotes.hashCode() : 0);
        return result;
    }
}