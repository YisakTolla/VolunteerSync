package com.volunteersync.backend.dto.response;

import com.volunteersync.backend.dto.profile.ProfileDTO;
import com.volunteersync.backend.dto.profile.VolunteerProfileDTO;
import com.volunteersync.backend.dto.profile.OrganizationProfileDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for profile search responses.
 * Contains paginated search results with metadata about the search operation,
 * filtering information, and suggested refinements.
 * 
 * This response DTO provides comprehensive search functionality including
 * faceted search, result ranking, and search analytics.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileSearchResponse {

    // =====================================================
    // SEARCH RESULTS
    // =====================================================

    private List<ProfileDTO> profiles; // Main search results

    private List<VolunteerProfileDTO> volunteers; // Volunteer-specific results

    private List<OrganizationProfileDTO> organizations; // Organization-specific results

    // =====================================================
    // PAGINATION INFORMATION
    // =====================================================

    private Integer currentPage; // Current page number (0-based)

    private Integer pageSize; // Number of results per page

    private Long totalResults; // Total number of matching profiles

    private Integer totalPages; // Total number of pages available

    private Boolean hasNextPage; // Whether there are more results

    private Boolean hasPreviousPage; // Whether there are previous results

    private Integer nextPage; // Next page number (if available)

    private Integer previousPage; // Previous page number (if available)

    // =====================================================
    // SEARCH METADATA
    // =====================================================

    private String searchQuery; // Original search query

    private Map<String, String> appliedFilters; // Filters that were applied

    private List<String> searchTerms; // Parsed search terms

    private String sortBy; // Field used for sorting

    private String sortOrder; // ASC or DESC

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime searchTimestamp; // When the search was performed

    private Long searchDurationMs; // Time taken to execute search (in milliseconds)

    // =====================================================
    // FACETED SEARCH & FILTERS
    // =====================================================

    private Map<String, List<FacetOption>> facets; // Available filter options with counts

    private List<String> availableSkills; // Skills found in results for filtering

    private List<String> availableInterests; // Interests found in results for filtering

    private List<String> availableLocations; // Locations found in results for filtering

    private List<String> availableCauseAreas; // Cause areas found in results for filtering

    private Map<String, Long> skillCounts; // Count of profiles by skill

    private Map<String, Long> locationCounts; // Count of profiles by location

    private Map<String, Long> experienceLevelCounts; // Count of profiles by experience level

    // =====================================================
    // SEARCH SUGGESTIONS & RECOMMENDATIONS
    // =====================================================

    private List<String> searchSuggestions; // Suggested search terms

    private List<String> relatedSearches; // Related search queries

    private List<String> didYouMean; // Spelling correction suggestions

    private List<ProfileDTO> recommendedProfiles; // Profiles recommended based on search

    private List<String> popularSearches; // Popular search terms

    // =====================================================
    // RESULT QUALITY & RANKING
    // =====================================================

    private Double averageRelevanceScore; // Average relevance score of results

    private String rankingAlgorithm; // Algorithm used for ranking results

    private Boolean hasHighQualityResults; // Whether results meet quality threshold

    private Integer verifiedProfilesCount; // Number of verified profiles in results

    private Integer featuredProfilesCount; // Number of featured profiles in results

    // =====================================================
    // SEARCH ANALYTICS & INSIGHTS
    // =====================================================

    private Map<String, Object> searchInsights; // Additional analytics data

    private String searchId; // Unique identifier for this search

    private Boolean searchCached; // Whether results were served from cache

    private List<String> appliedBoosts; // Search boosts that were applied

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public ProfileSearchResponse() {
        // Default constructor for JSON deserialization
    }

    public ProfileSearchResponse(List<ProfileDTO> profiles, Integer currentPage, Integer pageSize, 
                               Long totalResults, String searchQuery) {
        this.profiles = profiles;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalResults = totalResults;
        this.searchQuery = searchQuery;
        this.searchTimestamp = LocalDateTime.now();
        calculatePagination();
    }

    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================

    public List<ProfileDTO> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<ProfileDTO> profiles) {
        this.profiles = profiles;
    }

    public List<VolunteerProfileDTO> getVolunteers() {
        return volunteers;
    }

    public void setVolunteers(List<VolunteerProfileDTO> volunteers) {
        this.volunteers = volunteers;
    }

    public List<OrganizationProfileDTO> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<OrganizationProfileDTO> organizations) {
        this.organizations = organizations;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
        calculatePagination();
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        calculatePagination();
    }

    public Long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Long totalResults) {
        this.totalResults = totalResults;
        calculatePagination();
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Boolean getHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(Boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public Boolean getHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(Boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    public Integer getNextPage() {
        return nextPage;
    }

    public void setNextPage(Integer nextPage) {
        this.nextPage = nextPage;
    }

    public Integer getPreviousPage() {
        return previousPage;
    }

    public void setPreviousPage(Integer previousPage) {
        this.previousPage = previousPage;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public Map<String, String> getAppliedFilters() {
        return appliedFilters;
    }

    public void setAppliedFilters(Map<String, String> appliedFilters) {
        this.appliedFilters = appliedFilters;
    }

    public List<String> getSearchTerms() {
        return searchTerms;
    }

    public void setSearchTerms(List<String> searchTerms) {
        this.searchTerms = searchTerms;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public LocalDateTime getSearchTimestamp() {
        return searchTimestamp;
    }

    public void setSearchTimestamp(LocalDateTime searchTimestamp) {
        this.searchTimestamp = searchTimestamp;
    }

    public Long getSearchDurationMs() {
        return searchDurationMs;
    }

    public void setSearchDurationMs(Long searchDurationMs) {
        this.searchDurationMs = searchDurationMs;
    }

    public Map<String, List<FacetOption>> getFacets() {
        return facets;
    }

    public void setFacets(Map<String, List<FacetOption>> facets) {
        this.facets = facets;
    }

    public List<String> getAvailableSkills() {
        return availableSkills;
    }

    public void setAvailableSkills(List<String> availableSkills) {
        this.availableSkills = availableSkills;
    }

    public List<String> getAvailableInterests() {
        return availableInterests;
    }

    public void setAvailableInterests(List<String> availableInterests) {
        this.availableInterests = availableInterests;
    }

    public List<String> getAvailableLocations() {
        return availableLocations;
    }

    public void setAvailableLocations(List<String> availableLocations) {
        this.availableLocations = availableLocations;
    }

    public List<String> getAvailableCauseAreas() {
        return availableCauseAreas;
    }

    public void setAvailableCauseAreas(List<String> availableCauseAreas) {
        this.availableCauseAreas = availableCauseAreas;
    }

    public Map<String, Long> getSkillCounts() {
        return skillCounts;
    }

    public void setSkillCounts(Map<String, Long> skillCounts) {
        this.skillCounts = skillCounts;
    }

    public Map<String, Long> getLocationCounts() {
        return locationCounts;
    }

    public void setLocationCounts(Map<String, Long> locationCounts) {
        this.locationCounts = locationCounts;
    }

    public Map<String, Long> getExperienceLevelCounts() {
        return experienceLevelCounts;
    }

    public void setExperienceLevelCounts(Map<String, Long> experienceLevelCounts) {
        this.experienceLevelCounts = experienceLevelCounts;
    }

    public List<String> getSearchSuggestions() {
        return searchSuggestions;
    }

    public void setSearchSuggestions(List<String> searchSuggestions) {
        this.searchSuggestions = searchSuggestions;
    }

    public List<String> getRelatedSearches() {
        return relatedSearches;
    }

    public void setRelatedSearches(List<String> relatedSearches) {
        this.relatedSearches = relatedSearches;
    }

    public List<String> getDidYouMean() {
        return didYouMean;
    }

    public void setDidYouMean(List<String> didYouMean) {
        this.didYouMean = didYouMean;
    }

    public List<ProfileDTO> getRecommendedProfiles() {
        return recommendedProfiles;
    }

    public void setRecommendedProfiles(List<ProfileDTO> recommendedProfiles) {
        this.recommendedProfiles = recommendedProfiles;
    }

    public List<String> getPopularSearches() {
        return popularSearches;
    }

    public void setPopularSearches(List<String> popularSearches) {
        this.popularSearches = popularSearches;
    }

    public Double getAverageRelevanceScore() {
        return averageRelevanceScore;
    }

    public void setAverageRelevanceScore(Double averageRelevanceScore) {
        this.averageRelevanceScore = averageRelevanceScore;
    }

    public String getRankingAlgorithm() {
        return rankingAlgorithm;
    }

    public void setRankingAlgorithm(String rankingAlgorithm) {
        this.rankingAlgorithm = rankingAlgorithm;
    }

    public Boolean getHasHighQualityResults() {
        return hasHighQualityResults;
    }

    public void setHasHighQualityResults(Boolean hasHighQualityResults) {
        this.hasHighQualityResults = hasHighQualityResults;
    }

    public Integer getVerifiedProfilesCount() {
        return verifiedProfilesCount;
    }

    public void setVerifiedProfilesCount(Integer verifiedProfilesCount) {
        this.verifiedProfilesCount = verifiedProfilesCount;
    }

    public Integer getFeaturedProfilesCount() {
        return featuredProfilesCount;
    }

    public void setFeaturedProfilesCount(Integer featuredProfilesCount) {
        this.featuredProfilesCount = featuredProfilesCount;
    }

    public Map<String, Object> getSearchInsights() {
        return searchInsights;
    }

    public void setSearchInsights(Map<String, Object> searchInsights) {
        this.searchInsights = searchInsights;
    }

    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public Boolean getSearchCached() {
        return searchCached;
    }

    public void setSearchCached(Boolean searchCached) {
        this.searchCached = searchCached;
    }

    public List<String> getAppliedBoosts() {
        return appliedBoosts;
    }

    public void setAppliedBoosts(List<String> appliedBoosts) {
        this.appliedBoosts = appliedBoosts;
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    /**
     * Calculates pagination information based on current values.
     */
    private void calculatePagination() {
        if (totalResults != null && pageSize != null && pageSize > 0) {
            totalPages = (int) Math.ceil((double) totalResults / pageSize);
        }
        
        if (currentPage != null) {
            hasPreviousPage = currentPage > 0;
            previousPage = hasPreviousPage ? currentPage - 1 : null;
            
            if (totalPages != null) {
                hasNextPage = currentPage < totalPages - 1;
                nextPage = hasNextPage ? currentPage + 1 : null;
            }
        }
    }

    /**
     * Gets the total count of profiles in results.
     */
    public int getResultCount() {
        int count = 0;
        if (profiles != null) count += profiles.size();
        if (volunteers != null) count += volunteers.size();
        if (organizations != null) count += organizations.size();
        return count;
    }

    /**
     * Checks if the search returned any results.
     */
    public boolean hasResults() {
        return getResultCount() > 0;
    }

    /**
     * Gets the range of results being displayed (e.g., "1-20 of 150").
     */
    public String getResultRange() {
        if (totalResults == null || totalResults == 0) {
            return "0 results";
        }
        
        int start = (currentPage != null && pageSize != null) ? (currentPage * pageSize) + 1 : 1;
        int end = Math.min(start + getResultCount() - 1, totalResults.intValue());
        
        return String.format("%d-%d of %d", start, end, totalResults);
    }

    /**
     * Checks if search performance was good (under 500ms).
     */
    public boolean isSearchPerformanceGood() {
        return searchDurationMs != null && searchDurationMs < 500;
    }

    /**
     * Gets a summary of applied filters.
     */
    public String getFilterSummary() {
        if (appliedFilters == null || appliedFilters.isEmpty()) {
            return "No filters applied";
        }
        
        return String.format("%d filter(s) applied", appliedFilters.size());
    }

    /**
     * Checks if there are suggestions available.
     */
    public boolean hasSuggestions() {
        return (searchSuggestions != null && !searchSuggestions.isEmpty()) ||
               (didYouMean != null && !didYouMean.isEmpty()) ||
               (relatedSearches != null && !relatedSearches.isEmpty());
    }

    @Override
    public String toString() {
        return "ProfileSearchResponse{" +
                "searchQuery='" + searchQuery + '\'' +
                ", totalResults=" + totalResults +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", resultCount=" + getResultCount() +
                ", searchDurationMs=" + searchDurationMs +
                ", hasHighQualityResults=" + hasHighQualityResults +
                '}';
    }

    // =====================================================
    // INNER CLASS FOR FACET OPTIONS
    // =====================================================

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FacetOption {
        private String value;
        private Long count;
        private Boolean selected;

        public FacetOption() {}

        public FacetOption(String value, Long count) {
            this.value = value;
            this.count = count;
            this.selected = false;
        }

        public FacetOption(String value, Long count, Boolean selected) {
            this.value = value;
            this.count = count;
            this.selected = selected;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }

        public Boolean getSelected() {
            return selected;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }

        @Override
        public String toString() {
            return "FacetOption{" +
                    "value='" + value + '\'' +
                    ", count=" + count +
                    ", selected=" + selected +
                    '}';
        }
    }
}