package com.volunteersync.backend.dto.response;

import java.util.Map;

public class ImpactMetricsResponse {
    private Map<String, Object> metrics;
    private String period;
    private Long totalBeneficiaries;
    private Long totalHours;
    private Long totalEvents;
    
    // Constructors
    public ImpactMetricsResponse() {}
    
    public ImpactMetricsResponse(Map<String, Object> metrics, String period, 
                                Long totalBeneficiaries, Long totalHours, Long totalEvents) {
        this.metrics = metrics;
        this.period = period;
        this.totalBeneficiaries = totalBeneficiaries;
        this.totalHours = totalHours;
        this.totalEvents = totalEvents;
    }
    
    // Getters and Setters
    public Map<String, Object> getMetrics() {
        return metrics;
    }
    
    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }
    
    public String getPeriod() {
        return period;
    }
    
    public void setPeriod(String period) {
        this.period = period;
    }
    
    public Long getTotalBeneficiaries() {
        return totalBeneficiaries;
    }
    
    public void setTotalBeneficiaries(Long totalBeneficiaries) {
        this.totalBeneficiaries = totalBeneficiaries;
    }
    
    public Long getTotalHours() {
        return totalHours;
    }
    
    public void setTotalHours(Long totalHours) {
        this.totalHours = totalHours;
    }
    
    public Long getTotalEvents() {
        return totalEvents;
    }
    
    public void setTotalEvents(Long totalEvents) {
        this.totalEvents = totalEvents;
    }
    
    // toString, equals, hashCode
    @Override
    public String toString() {
        return "ImpactMetricsResponse{" +
                "metrics=" + metrics +
                ", period='" + period + '\'' +
                ", totalBeneficiaries=" + totalBeneficiaries +
                ", totalHours=" + totalHours +
                ", totalEvents=" + totalEvents +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ImpactMetricsResponse that = (ImpactMetricsResponse) o;
        
        if (metrics != null ? !metrics.equals(that.metrics) : that.metrics != null) return false;
        if (period != null ? !period.equals(that.period) : that.period != null) return false;
        if (totalBeneficiaries != null ? !totalBeneficiaries.equals(that.totalBeneficiaries) : that.totalBeneficiaries != null) return false;
        if (totalHours != null ? !totalHours.equals(that.totalHours) : that.totalHours != null) return false;
        return totalEvents != null ? totalEvents.equals(that.totalEvents) : that.totalEvents == null;
    }
    
    @Override
    public int hashCode() {
        int result = metrics != null ? metrics.hashCode() : 0;
        result = 31 * result + (period != null ? period.hashCode() : 0);
        result = 31 * result + (totalBeneficiaries != null ? totalBeneficiaries.hashCode() : 0);
        result = 31 * result + (totalHours != null ? totalHours.hashCode() : 0);
        result = 31 * result + (totalEvents != null ? totalEvents.hashCode() : 0);
        return result;
    }
}