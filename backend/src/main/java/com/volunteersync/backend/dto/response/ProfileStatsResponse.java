package com.volunteersync.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for profile statistics responses.
 * Contains comprehensive analytics and metrics about a user's profile
 * including engagement, activity, achievements, and performance indicators.
 * 
 * This response DTO provides detailed insights for both volunteer and
 * organization profiles with appropriate metrics for each user type.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileStatsResponse {

    // =====================================================
    // PROFILE BASIC INFORMATION
    // =====================================================

    private Long profileId;

    private String userType; // "VOLUNTEER" or "ORGANIZATION"

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastUpdated;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime statsGeneratedAt;

    // =====================================================
    // PROFILE ENGAGEMENT METRICS
    // =====================================================

    private Integer profileViews; // Total profile views

    private Integer profileViewsThisMonth; // Profile views in current month

    private Integer profileViewsThisWeek; // Profile views in current week

    private Integer uniqueViewers; // Number of unique users who viewed profile

    private Double averageViewsPerDay; // Average daily profile views

    private Integer searchAppearances; // Number of times profile appeared in search results

    private Integer searchClicks; // Number of times profile was clicked from search

    private Double searchClickRate; // Click-through rate from search results

    // =====================================================
    // PROFILE COMPLETION & QUALITY
    // =====================================================

    private Double profileCompletionPercentage; // Overall profile completion (0-100)

    private Integer profileQualityScore; // Quality score based on completeness and engagement (0-100)

    private List<String> missingFields; // Fields that are not completed

    private List<String> suggestedImprovements; // Suggestions to improve profile

    private Boolean hasProfileImage; // Whether profile has an image

    private Boolean hasCompleteBio; // Whether bio section is complete

    private Integer bioWordCount; // Number of words in bio

    // =====================================================
    // CONNECTIONS & NETWORKING
    // =====================================================

    private Integer connectionsCount; // Total connections/followers

    private Integer connectionsThisMonth; // New connections this month

    private Integer pendingConnectionRequests; // Pending incoming connection requests

    private Integer sentConnectionRequests; // Pending outgoing connection requests

    private Double connectionGrowthRate; // Monthly connection growth rate

    private Integer mutualConnections; // Connections in common with viewer (if applicable)

    // =====================================================
    // VOLUNTEER-SPECIFIC STATISTICS
    // =====================================================

    private Integer totalVolunteerHours; // Total volunteer hours logged

    private Integer volunteerHoursThisYear; // Volunteer hours in current year

    private Integer volunteerHoursThisMonth; // Volunteer hours in current month

    private Integer eventsParticipated; // Total events participated in

    private Integer eventsCompletedThisYear; // Events completed this year

    private Integer organizationsWorkedWith; // Number of organizations worked with

    private Double averageEventRating; // Average rating received from organizations

    private Integer totalReviews; // Total number of reviews received

    private Integer badgesEarned; // Total badges earned

    private Integer skillsCount; // Number of skills listed

    private Integer interestsCount; // Number of interests listed

    private Integer endorsements; // Total skill endorsements received

    // =====================================================
    // ORGANIZATION-SPECIFIC STATISTICS
    // =====================================================

    private Integer totalEventsHosted; // Total events hosted by organization

    private Integer eventsHostedThisYear; // Events hosted this year

    private Integer activeVolunteers; // Current active volunteers

    private Integer totalVolunteersManaged; // Total volunteers managed over time

    private Integer volunteerApplicationsReceived; // Applications received

    private Integer volunteerApplicationsApproved; // Applications approved

    private Double volunteerRetentionRate; // Volunteer retention rate

    private Double averageVolunteerRating; // Average rating from volunteers

    private Integer impactMetricsCount; // Number of impact metrics defined

    private Integer peopleServed; // Total people served by organization

    private Double totalImpactValue; // Quantified total impact value

    // =====================================================
    // ACTIVITY & ENGAGEMENT TRENDS
    // =====================================================

    private Map<String, Integer> weeklyActivity; // Activity by day of week

    private Map<String, Integer> monthlyActivity; // Activity by month

    private Map<String, Integer> skillUsageFrequency; // How often skills are used

    private Map<String, Integer> interestEngagementFrequency; // Engagement by interest

    private List<ActivityTrend> activityTrends; // Historical activity trends

    // =====================================================
    // PERFORMANCE BENCHMARKS
    // =====================================================

    private Map<String, Double> benchmarkComparisons; // How profile compares to others

    private String profileRank; // Rank among similar profiles (e.g., "Top 10%")

    private Integer percentileRank; // Percentile rank (0-100)

    private List<String> strengthAreas; // Areas where profile excels

    private List<String> improvementAreas; // Areas for improvement

    // =====================================================
    // RECENT ACHIEVEMENTS & MILESTONES
    // =====================================================

    private List<RecentAchievement> recentAchievements; // Recent badges, milestones

    private List<String> upcomingMilestones; // Milestones close to being achieved

    private Integer achievementsThisMonth; // Achievements earned this month

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastAchievementDate; // Date of most recent achievement

    // =====================================================
    // GEOGRAPHICAL & DEMOGRAPHIC INSIGHTS
    // =====================================================

    private Map<String, Integer> viewerLocationBreakdown; // Where viewers are from

    private Map<String, Integer> connectionLocationBreakdown; // Where connections are from

    private String mostActiveLocation; // Location with most activity

    private Integer localConnections; // Connections in same city/region

    // =====================================================
    // COMMUNICATION & MESSAGING STATS
    // =====================================================

    private Integer messagesReceived; // Total messages received

    private Integer messagesReceivedThisMonth; // Messages received this month

    private Integer messagesSent; // Total messages sent

    private Double messageResponseRate; // Response rate to messages

    private Double averageResponseTime; // Average time to respond (in hours)

    private Integer eventInvitationsReceived; // Event invitations received

    private Integer eventInvitationsAccepted; // Event invitations accepted

    // =====================================================
    // PRIVACY & VISIBILITY METRICS
    // =====================================================

    private String profileVisibility; // Current visibility setting

    private Boolean allowsDirectMessaging; // Whether profile allows messaging

    private Boolean appearsInSearch; // Whether profile appears in search

    private Integer privacyScore; // Privacy score (0-100, higher = more private)

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public ProfileStatsResponse() {
        this.statsGeneratedAt = LocalDateTime.now();
    }

    public ProfileStatsResponse(Long profileId, String userType) {
        this.profileId = profileId;
        this.userType = userType;
        this.statsGeneratedAt = LocalDateTime.now();
    }

    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LocalDateTime getStatsGeneratedAt() {
        return statsGeneratedAt;
    }

    public void setStatsGeneratedAt(LocalDateTime statsGeneratedAt) {
        this.statsGeneratedAt = statsGeneratedAt;
    }

    public Integer getProfileViews() {
        return profileViews;
    }

    public void setProfileViews(Integer profileViews) {
        this.profileViews = profileViews;
    }

    public Integer getProfileViewsThisMonth() {
        return profileViewsThisMonth;
    }

    public void setProfileViewsThisMonth(Integer profileViewsThisMonth) {
        this.profileViewsThisMonth = profileViewsThisMonth;
    }

    public Integer getProfileViewsThisWeek() {
        return profileViewsThisWeek;
    }

    public void setProfileViewsThisWeek(Integer profileViewsThisWeek) {
        this.profileViewsThisWeek = profileViewsThisWeek;
    }

    public Integer getUniqueViewers() {
        return uniqueViewers;
    }

    public void setUniqueViewers(Integer uniqueViewers) {
        this.uniqueViewers = uniqueViewers;
    }

    public Double getAverageViewsPerDay() {
        return averageViewsPerDay;
    }

    public void setAverageViewsPerDay(Double averageViewsPerDay) {
        this.averageViewsPerDay = averageViewsPerDay;
    }

    public Integer getSearchAppearances() {
        return searchAppearances;
    }

    public void setSearchAppearances(Integer searchAppearances) {
        this.searchAppearances = searchAppearances;
    }

    public Integer getSearchClicks() {
        return searchClicks;
    }

    public void setSearchClicks(Integer searchClicks) {
        this.searchClicks = searchClicks;
    }

    public Double getSearchClickRate() {
        return searchClickRate;
    }

    public void setSearchClickRate(Double searchClickRate) {
        this.searchClickRate = searchClickRate;
    }

    public Double getProfileCompletionPercentage() {
        return profileCompletionPercentage;
    }

    public void setProfileCompletionPercentage(Double profileCompletionPercentage) {
        this.profileCompletionPercentage = profileCompletionPercentage;
    }

    public Integer getProfileQualityScore() {
        return profileQualityScore;
    }

    public void setProfileQualityScore(Integer profileQualityScore) {
        this.profileQualityScore = profileQualityScore;
    }

    public List<String> getMissingFields() {
        return missingFields;
    }

    public void setMissingFields(List<String> missingFields) {
        this.missingFields = missingFields;
    }

    public List<String> getSuggestedImprovements() {
        return suggestedImprovements;
    }

    public void setSuggestedImprovements(List<String> suggestedImprovements) {
        this.suggestedImprovements = suggestedImprovements;
    }

    public Boolean getHasProfileImage() {
        return hasProfileImage;
    }

    public void setHasProfileImage(Boolean hasProfileImage) {
        this.hasProfileImage = hasProfileImage;
    }

    public Boolean getHasCompleteBio() {
        return hasCompleteBio;
    }

    public void setHasCompleteBio(Boolean hasCompleteBio) {
        this.hasCompleteBio = hasCompleteBio;
    }

    public Integer getBioWordCount() {
        return bioWordCount;
    }

    public void setBioWordCount(Integer bioWordCount) {
        this.bioWordCount = bioWordCount;
    }

    public Integer getConnectionsCount() {
        return connectionsCount;
    }

    public void setConnectionsCount(Integer connectionsCount) {
        this.connectionsCount = connectionsCount;
    }

    public Integer getConnectionsThisMonth() {
        return connectionsThisMonth;
    }

    public void setConnectionsThisMonth(Integer connectionsThisMonth) {
        this.connectionsThisMonth = connectionsThisMonth;
    }

    public Integer getPendingConnectionRequests() {
        return pendingConnectionRequests;
    }

    public void setPendingConnectionRequests(Integer pendingConnectionRequests) {
        this.pendingConnectionRequests = pendingConnectionRequests;
    }

    public Integer getSentConnectionRequests() {
        return sentConnectionRequests;
    }

    public void setSentConnectionRequests(Integer sentConnectionRequests) {
        this.sentConnectionRequests = sentConnectionRequests;
    }

    public Double getConnectionGrowthRate() {
        return connectionGrowthRate;
    }

    public void setConnectionGrowthRate(Double connectionGrowthRate) {
        this.connectionGrowthRate = connectionGrowthRate;
    }

    public Integer getMutualConnections() {
        return mutualConnections;
    }

    public void setMutualConnections(Integer mutualConnections) {
        this.mutualConnections = mutualConnections;
    }

    public Integer getTotalVolunteerHours() {
        return totalVolunteerHours;
    }

    public void setTotalVolunteerHours(Integer totalVolunteerHours) {
        this.totalVolunteerHours = totalVolunteerHours;
    }

    public Integer getVolunteerHoursThisYear() {
        return volunteerHoursThisYear;
    }

    public void setVolunteerHoursThisYear(Integer volunteerHoursThisYear) {
        this.volunteerHoursThisYear = volunteerHoursThisYear;
    }

    public Integer getVolunteerHoursThisMonth() {
        return volunteerHoursThisMonth;
    }

    public void setVolunteerHoursThisMonth(Integer volunteerHoursThisMonth) {
        this.volunteerHoursThisMonth = volunteerHoursThisMonth;
    }

    public Integer getEventsParticipated() {
        return eventsParticipated;
    }

    public void setEventsParticipated(Integer eventsParticipated) {
        this.eventsParticipated = eventsParticipated;
    }

    public Integer getEventsCompletedThisYear() {
        return eventsCompletedThisYear;
    }

    public void setEventsCompletedThisYear(Integer eventsCompletedThisYear) {
        this.eventsCompletedThisYear = eventsCompletedThisYear;
    }

    public Integer getOrganizationsWorkedWith() {
        return organizationsWorkedWith;
    }

    public void setOrganizationsWorkedWith(Integer organizationsWorkedWith) {
        this.organizationsWorkedWith = organizationsWorkedWith;
    }

    public Double getAverageEventRating() {
        return averageEventRating;
    }

    public void setAverageEventRating(Double averageEventRating) {
        this.averageEventRating = averageEventRating;
    }

    public Integer getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }

    public Integer getBadgesEarned() {
        return badgesEarned;
    }

    public void setBadgesEarned(Integer badgesEarned) {
        this.badgesEarned = badgesEarned;
    }

    public Integer getSkillsCount() {
        return skillsCount;
    }

    public void setSkillsCount(Integer skillsCount) {
        this.skillsCount = skillsCount;
    }

    public Integer getInterestsCount() {
        return interestsCount;
    }

    public void setInterestsCount(Integer interestsCount) {
        this.interestsCount = interestsCount;
    }

    public Integer getEndorsements() {
        return endorsements;
    }

    public void setEndorsements(Integer endorsements) {
        this.endorsements = endorsements;
    }

    public Integer getTotalEventsHosted() {
        return totalEventsHosted;
    }

    public void setTotalEventsHosted(Integer totalEventsHosted) {
        this.totalEventsHosted = totalEventsHosted;
    }

    public Integer getEventsHostedThisYear() {
        return eventsHostedThisYear;
    }

    public void setEventsHostedThisYear(Integer eventsHostedThisYear) {
        this.eventsHostedThisYear = eventsHostedThisYear;
    }

    public Integer getActiveVolunteers() {
        return activeVolunteers;
    }

    public void setActiveVolunteers(Integer activeVolunteers) {
        this.activeVolunteers = activeVolunteers;
    }

    public Integer getTotalVolunteersManaged() {
        return totalVolunteersManaged;
    }

    public void setTotalVolunteersManaged(Integer totalVolunteersManaged) {
        this.totalVolunteersManaged = totalVolunteersManaged;
    }

    public Integer getVolunteerApplicationsReceived() {
        return volunteerApplicationsReceived;
    }

    public void setVolunteerApplicationsReceived(Integer volunteerApplicationsReceived) {
        this.volunteerApplicationsReceived = volunteerApplicationsReceived;
    }

    public Integer getVolunteerApplicationsApproved() {
        return volunteerApplicationsApproved;
    }

    public void setVolunteerApplicationsApproved(Integer volunteerApplicationsApproved) {
        this.volunteerApplicationsApproved = volunteerApplicationsApproved;
    }

    public Double getVolunteerRetentionRate() {
        return volunteerRetentionRate;
    }

    public void setVolunteerRetentionRate(Double volunteerRetentionRate) {
        this.volunteerRetentionRate = volunteerRetentionRate;
    }

    public Double getAverageVolunteerRating() {
        return averageVolunteerRating;
    }

    public void setAverageVolunteerRating(Double averageVolunteerRating) {
        this.averageVolunteerRating = averageVolunteerRating;
    }

    public Integer getImpactMetricsCount() {
        return impactMetricsCount;
    }

    public void setImpactMetricsCount(Integer impactMetricsCount) {
        this.impactMetricsCount = impactMetricsCount;
    }

    public Integer getPeopleServed() {
        return peopleServed;
    }

    public void setPeopleServed(Integer peopleServed) {
        this.peopleServed = peopleServed;
    }

    public Double getTotalImpactValue() {
        return totalImpactValue;
    }

    public void setTotalImpactValue(Double totalImpactValue) {
        this.totalImpactValue = totalImpactValue;
    }

    public Map<String, Integer> getWeeklyActivity() {
        return weeklyActivity;
    }

    public void setWeeklyActivity(Map<String, Integer> weeklyActivity) {
        this.weeklyActivity = weeklyActivity;
    }

    public Map<String, Integer> getMonthlyActivity() {
        return monthlyActivity;
    }

    public void setMonthlyActivity(Map<String, Integer> monthlyActivity) {
        this.monthlyActivity = monthlyActivity;
    }

    public Map<String, Integer> getSkillUsageFrequency() {
        return skillUsageFrequency;
    }

    public void setSkillUsageFrequency(Map<String, Integer> skillUsageFrequency) {
        this.skillUsageFrequency = skillUsageFrequency;
    }

    public Map<String, Integer> getInterestEngagementFrequency() {
        return interestEngagementFrequency;
    }

    public void setInterestEngagementFrequency(Map<String, Integer> interestEngagementFrequency) {
        this.interestEngagementFrequency = interestEngagementFrequency;
    }

    public List<ActivityTrend> getActivityTrends() {
        return activityTrends;
    }

    public void setActivityTrends(List<ActivityTrend> activityTrends) {
        this.activityTrends = activityTrends;
    }

    public Map<String, Double> getBenchmarkComparisons() {
        return benchmarkComparisons;
    }

    public void setBenchmarkComparisons(Map<String, Double> benchmarkComparisons) {
        this.benchmarkComparisons = benchmarkComparisons;
    }

    public String getProfileRank() {
        return profileRank;
    }

    public void setProfileRank(String profileRank) {
        this.profileRank = profileRank;
    }

    public Integer getPercentileRank() {
        return percentileRank;
    }

    public void setPercentileRank(Integer percentileRank) {
        this.percentileRank = percentileRank;
    }

    public List<String> getStrengthAreas() {
        return strengthAreas;
    }

    public void setStrengthAreas(List<String> strengthAreas) {
        this.strengthAreas = strengthAreas;
    }

    public List<String> getImprovementAreas() {
        return improvementAreas;
    }

    public void setImprovementAreas(List<String> improvementAreas) {
        this.improvementAreas = improvementAreas;
    }

    public List<RecentAchievement> getRecentAchievements() {
        return recentAchievements;
    }

    public void setRecentAchievements(List<RecentAchievement> recentAchievements) {
        this.recentAchievements = recentAchievements;
    }

    public List<String> getUpcomingMilestones() {
        return upcomingMilestones;
    }

    public void setUpcomingMilestones(List<String> upcomingMilestones) {
        this.upcomingMilestones = upcomingMilestones;
    }

    public Integer getAchievementsThisMonth() {
        return achievementsThisMonth;
    }

    public void setAchievementsThisMonth(Integer achievementsThisMonth) {
        this.achievementsThisMonth = achievementsThisMonth;
    }

    public LocalDate getLastAchievementDate() {
        return lastAchievementDate;
    }

    public void setLastAchievementDate(LocalDate lastAchievementDate) {
        this.lastAchievementDate = lastAchievementDate;
    }

    public Map<String, Integer> getViewerLocationBreakdown() {
        return viewerLocationBreakdown;
    }

    public void setViewerLocationBreakdown(Map<String, Integer> viewerLocationBreakdown) {
        this.viewerLocationBreakdown = viewerLocationBreakdown;
    }

    public Map<String, Integer> getConnectionLocationBreakdown() {
        return connectionLocationBreakdown;
    }

    public void setConnectionLocationBreakdown(Map<String, Integer> connectionLocationBreakdown) {
        this.connectionLocationBreakdown = connectionLocationBreakdown;
    }

    public String getMostActiveLocation() {
        return mostActiveLocation;
    }

    public void setMostActiveLocation(String mostActiveLocation) {
        this.mostActiveLocation = mostActiveLocation;
    }

    public Integer getLocalConnections() {
        return localConnections;
    }

    public void setLocalConnections(Integer localConnections) {
        this.localConnections = localConnections;
    }

    public Integer getMessagesReceived() {
        return messagesReceived;
    }

    public void setMessagesReceived(Integer messagesReceived) {
        this.messagesReceived = messagesReceived;
    }

    public Integer getMessagesReceivedThisMonth() {
        return messagesReceivedThisMonth;
    }

    public void setMessagesReceivedThisMonth(Integer messagesReceivedThisMonth) {
        this.messagesReceivedThisMonth = messagesReceivedThisMonth;
    }

    public Integer getMessagesSent() {
        return messagesSent;
    }

    public void setMessagesSent(Integer messagesSent) {
        this.messagesSent = messagesSent;
    }

    public Double getMessageResponseRate() {
        return messageResponseRate;
    }

    public void setMessageResponseRate(Double messageResponseRate) {
        this.messageResponseRate = messageResponseRate;
    }

    public Double getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(Double averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }

    public Integer getEventInvitationsReceived() {
        return eventInvitationsReceived;
    }

    public void setEventInvitationsReceived(Integer eventInvitationsReceived) {
        this.eventInvitationsReceived = eventInvitationsReceived;
    }

    public Integer getEventInvitationsAccepted() {
        return eventInvitationsAccepted;
    }

    public void setEventInvitationsAccepted(Integer eventInvitationsAccepted) {
        this.eventInvitationsAccepted = eventInvitationsAccepted;
    }

    public String getProfileVisibility() {
        return profileVisibility;
    }

    public void setProfileVisibility(String profileVisibility) {
        this.profileVisibility = profileVisibility;
    }

    public Boolean getAllowsDirectMessaging() {
        return allowsDirectMessaging;
    }

    public void setAllowsDirectMessaging(Boolean allowsDirectMessaging) {
        this.allowsDirectMessaging = allowsDirectMessaging;
    }

    public Boolean getAppearsInSearch() {
        return appearsInSearch;
    }

    public void setAppearsInSearch(Boolean appearsInSearch) {
        this.appearsInSearch = appearsInSearch;
    }

    public Integer getPrivacyScore() {
        return privacyScore;
    }

    public void setPrivacyScore(Integer privacyScore) {
        this.privacyScore = privacyScore;
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    /**
     * Checks if this is a volunteer profile.
     */
    public boolean isVolunteerProfile() {
        return "VOLUNTEER".equals(userType);
    }

    /**
     * Checks if this is an organization profile.
     */
    public boolean isOrganizationProfile() {
        return "ORGANIZATION".equals(userType);
    }

    /**
     * Gets the engagement score based on various metrics.
     */
    public Double getEngagementScore() {
        if (profileViews == null || profileViews == 0) return 0.0;
        
        double score = 0.0;
        
        // Base score from profile views (max 30 points)
        score += Math.min(profileViews * 0.1, 30.0);
        
        // Connections score (max 25 points)
        if (connectionsCount != null) {
            score += Math.min(connectionsCount * 0.5, 25.0);
        }
        
        // Activity score varies by user type
        if (isVolunteerProfile()) {
            // Volunteer hours score (max 25 points)
            if (totalVolunteerHours != null) {
                score += Math.min(totalVolunteerHours * 0.2, 25.0);
            }
            // Events score (max 20 points)
            if (eventsParticipated != null) {
                score += Math.min(eventsParticipated * 2.0, 20.0);
            }
        } else if (isOrganizationProfile()) {
            // Events hosted score (max 25 points)
            if (totalEventsHosted != null) {
                score += Math.min(totalEventsHosted * 2.5, 25.0);
            }
            // Volunteers managed score (max 20 points)
            if (activeVolunteers != null) {
                score += Math.min(activeVolunteers * 1.0, 20.0);
            }
        }
        
        return Math.min(score, 100.0); // Cap at 100
    }

    /**
     * Calculates activity level based on recent engagement.
     */
    public String getActivityLevel() {
        Integer monthlyViews = profileViewsThisMonth != null ? profileViewsThisMonth : 0;
        Integer monthlyConnections = connectionsThisMonth != null ? connectionsThisMonth : 0;
        
        int activityScore = monthlyViews + (monthlyConnections * 5);
        
        if (isVolunteerProfile() && volunteerHoursThisMonth != null) {
            activityScore += volunteerHoursThisMonth * 2;
        } else if (isOrganizationProfile() && eventsHostedThisYear != null) {
            activityScore += (eventsHostedThisYear / 12) * 10; // Approximate monthly events
        }
        
        if (activityScore >= 50) return "Very High";
        if (activityScore >= 25) return "High";
        if (activityScore >= 10) return "Medium";
        if (activityScore >= 3) return "Low";
        return "Very Low";
    }

    /**
     * Gets the profile strength indicator.
     */
    public String getProfileStrength() {
        if (profileCompletionPercentage == null) return "Unknown";
        
        if (profileCompletionPercentage >= 90) return "Excellent";
        if (profileCompletionPercentage >= 75) return "Strong";
        if (profileCompletionPercentage >= 50) return "Good";
        if (profileCompletionPercentage >= 25) return "Fair";
        return "Needs Improvement";
    }

    /**
     * Gets the response time category.
     */
    public String getResponseTimeCategory() {
        if (averageResponseTime == null) return "Unknown";
        
        if (averageResponseTime <= 2) return "Very Fast";
        if (averageResponseTime <= 12) return "Fast";
        if (averageResponseTime <= 24) return "Normal";
        if (averageResponseTime <= 72) return "Slow";
        return "Very Slow";
    }

    /**
     * Checks if the profile is considered active.
     */
    public boolean isActiveProfile() {
        return profileViewsThisMonth != null && profileViewsThisMonth > 0 ||
               connectionsThisMonth != null && connectionsThisMonth > 0 ||
               (isVolunteerProfile() && volunteerHoursThisMonth != null && volunteerHoursThisMonth > 0) ||
               (isOrganizationProfile() && messagesReceivedThisMonth != null && messagesReceivedThisMonth > 0);
    }

    /**
     * Gets a summary of key stats.
     */
    public String getStatsSummary() {
        StringBuilder summary = new StringBuilder();
        
        if (isVolunteerProfile()) {
            summary.append("Volunteer: ");
            if (totalVolunteerHours != null) summary.append(totalVolunteerHours).append(" hours, ");
            if (eventsParticipated != null) summary.append(eventsParticipated).append(" events");
        } else {
            summary.append("Organization: ");
            if (totalEventsHosted != null) summary.append(totalEventsHosted).append(" events hosted, ");
            if (activeVolunteers != null) summary.append(activeVolunteers).append(" active volunteers");
        }
        
        if (profileViews != null) {
            summary.append(", ").append(profileViews).append(" profile views");
        }
        
        return summary.toString();
    }

    @Override
    public String toString() {
        return "ProfileStatsResponse{" +
                "profileId=" + profileId +
                ", userType='" + userType + '\'' +
                ", profileViews=" + profileViews +
                ", connectionsCount=" + connectionsCount +
                ", profileCompletionPercentage=" + profileCompletionPercentage +
                ", engagementScore=" + getEngagementScore() +
                ", activityLevel='" + getActivityLevel() + '\'' +
                '}';
    }

    // =====================================================
    // INNER CLASSES FOR COMPLEX DATA TYPES
    // =====================================================

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ActivityTrend {
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate date;
        private String metric;
        private Integer value;
        private Double changeFromPrevious;

        public ActivityTrend() {}

        public ActivityTrend(LocalDate date, String metric, Integer value) {
            this.date = date;
            this.metric = metric;
            this.value = value;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public String getMetric() {
            return metric;
        }

        public void setMetric(String metric) {
            this.metric = metric;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public Double getChangeFromPrevious() {
            return changeFromPrevious;
        }

        public void setChangeFromPrevious(Double changeFromPrevious) {
            this.changeFromPrevious = changeFromPrevious;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RecentAchievement {
        private String type; // "BADGE", "MILESTONE", "RECOGNITION"
        private String title;
        private String description;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime achievedAt;
        private String iconUrl;
        private Boolean isVisible;

        public RecentAchievement() {}

        public RecentAchievement(String type, String title, LocalDateTime achievedAt) {
            this.type = type;
            this.title = title;
            this.achievedAt = achievedAt;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public LocalDateTime getAchievedAt() {
            return achievedAt;
        }

        public void setAchievedAt(LocalDateTime achievedAt) {
            this.achievedAt = achievedAt;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public Boolean getIsVisible() {
            return isVisible;
        }

        public void setIsVisible(Boolean isVisible) {
            this.isVisible = isVisible;
        }
    }
}