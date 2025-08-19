package com.volunteersync.backend.controller;

import com.volunteersync.backend.dto.OrganizationProfileDTO;
import com.volunteersync.backend.service.OrganizationProfileService;
import com.volunteersync.backend.service.OrganizationProfileService.OrganizationSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Organizations Browse Controller - Public endpoints for browsing organizations
 * Provides PUBLIC access to organization data for the Organizations page
 * No authentication required - suitable for discovery and browsing
 */
@RestController
@RequestMapping("/api/organizations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrganizationsBrowseController {

    @Autowired
    private OrganizationProfileService organizationProfileService;

    // ==========================================
    // CORE ORGANIZATION ENDPOINTS
    // ==========================================

    /**
     * Get all public organizations (verified only for quality)
     * GET /api/organizations
     */
    @GetMapping
    public ResponseEntity<List<OrganizationProfileDTO>> getAllOrganizations() {
        try {
            // Return verified organizations for public browsing
            List<OrganizationProfileDTO> organizations = organizationProfileService.getVerifiedOrganizations();
            return ResponseEntity.ok(organizations);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to non-profit organizations if verified fails
            try {
                List<OrganizationProfileDTO> fallback = organizationProfileService.getNonProfitOrganizations();
                return ResponseEntity.ok(fallback);
            } catch (Exception fallbackError) {
                return ResponseEntity.ok(List.of()); // Return empty list to prevent crashes
            }
        }
    }

    /**
     * Get organizations with pagination
     * GET /api/organizations/paginated?page=0&size=10&sortBy=organizationName&sortDirection=asc
     */
    @GetMapping("/paginated")
    public ResponseEntity<Page<OrganizationProfileDTO>> getAllOrganizationsWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "organizationName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        try {
            // Use empty search request to get all organizations with pagination
            Pageable pageable = PageRequest.of(page, size,
                    "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC,
                    sortBy);

            // Create empty search request
            OrganizationSearchRequest searchRequest = new OrganizationSearchRequest();
            searchRequest.setSearchTerm("");
            searchRequest.setCategory("");
            searchRequest.setCountry("");
            searchRequest.setOrganizationSize("");
            searchRequest.setIsVerified(null);

            Page<OrganizationProfileDTO> organizations = organizationProfileService.advancedSearch(searchRequest, pageable);
            return ResponseEntity.ok(organizations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get organization by ID
     * GET /api/organizations/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrganizationProfileDTO> getOrganizationById(@PathVariable Long id) {
        try {
            OrganizationProfileDTO organization = organizationProfileService.getProfileById(id);
            return ResponseEntity.ok(organization);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    // ==========================================
    // SEARCH ENDPOINTS
    // ==========================================

    /**
     * Enhanced search by name with fuzzy matching
     * GET /api/organizations/search/name?name=searchTerm
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<OrganizationProfileDTO>> searchOrganizationsByName(@RequestParam String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.ok(List.of());
            }

            // Try the enhanced search method first
            OrganizationProfileDTO exactMatch = organizationProfileService.findOrganizationByName(name.trim());
            if (exactMatch != null) {
                System.out.println("Found exact match for: " + name);
                return ResponseEntity.ok(List.of(exactMatch));
            }

            // Fallback to the existing search method
            List<OrganizationProfileDTO> organizations = organizationProfileService.searchOrganizationsByName(name.trim());
            System.out.println("Found " + organizations.size() + " organizations matching: " + name);
            return ResponseEntity.ok(organizations);

        } catch (Exception e) {
            System.err.println("Error in searchOrganizationsByName: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Search organizations by category
     * GET /api/organizations/search/category?category=Education
     */
    @GetMapping("/search/category")
    public ResponseEntity<List<OrganizationProfileDTO>> searchOrganizationsByCategory(@RequestParam String category) {
        try {
            List<OrganizationProfileDTO> organizations = organizationProfileService.getOrganizationsByCategory(category);
            return ResponseEntity.ok(organizations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search organizations by type
     * GET /api/organizations/search/type?type=Non-Profit
     */
    @GetMapping("/search/type")
    public ResponseEntity<List<OrganizationProfileDTO>> searchOrganizationsByType(@RequestParam String type) {
        try {
            List<OrganizationProfileDTO> organizations = organizationProfileService.getOrganizationsByType(type);
            return ResponseEntity.ok(organizations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search organizations by location
     * GET /api/organizations/search/location?city=Seattle&state=WA
     */
    @GetMapping("/search/location")
    public ResponseEntity<List<OrganizationProfileDTO>> searchOrganizationsByLocation(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state) {
        try {
            String location = "";
            if (city != null && state != null) {
                location = city + ", " + state;
            } else if (city != null) {
                location = city;
            } else if (state != null) {
                location = state;
            }

            List<OrganizationProfileDTO> organizations = organizationProfileService.searchOrganizationsByLocation(location);
            return ResponseEntity.ok(organizations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search organizations by employee count range
     * GET /api/organizations/search/employee-count?minEmployees=50&maxEmployees=200
     */
    @GetMapping("/search/employee-count")
    public ResponseEntity<List<OrganizationProfileDTO>> searchOrganizationsByEmployeeCount(
            @RequestParam(required = false) Integer minEmployees,
            @RequestParam(required = false) Integer maxEmployees) {
        try {
            String size = convertEmployeeCountToSize(minEmployees, maxEmployees);
            if (size.isEmpty()) {
                // Return all organizations if no size specified
                return getAllOrganizations();
            }

            List<OrganizationProfileDTO> organizations = organizationProfileService.getOrganizationsBySize(size);
            return ResponseEntity.ok(organizations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Advanced search with multiple filters
     * GET /api/organizations/search?name=example&category=Education&verified=true
     */
    @GetMapping("/search")
    public ResponseEntity<List<OrganizationProfileDTO>> searchOrganizations(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Boolean verified,
            @RequestParam(required = false) String verificationLevel,
            @RequestParam(required = false) Integer minEmployees,
            @RequestParam(required = false) Integer maxEmployees,
            @RequestParam(required = false) Integer minFoundedYear,
            @RequestParam(required = false) Integer maxFoundedYear,
            @RequestParam(required = false) String language) {
        try {
            // Create search request
            OrganizationSearchRequest searchRequest = new OrganizationSearchRequest();
            searchRequest.setSearchTerm(name != null ? name : "");
            searchRequest.setCategory(category != null ? category : "");
            searchRequest.setCountry(country != null ? country : "");
            searchRequest.setOrganizationSize(convertEmployeeCountToSize(minEmployees, maxEmployees));
            searchRequest.setIsVerified(verified);

            // Use first page with large size for simple search
            Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.ASC, "organizationName"));
            Page<OrganizationProfileDTO> organizations = organizationProfileService.advancedSearch(searchRequest, pageable);

            return ResponseEntity.ok(organizations.getContent());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==========================================
    // SORTED ENDPOINTS
    // ==========================================

    /**
     * Get organizations sorted by name
     * GET /api/organizations/sorted/name
     */
    @GetMapping("/sorted/name")
    public ResponseEntity<List<OrganizationProfileDTO>> getOrganizationsSortedByName() {
        try {
            List<OrganizationProfileDTO> organizations = organizationProfileService.getVerifiedOrganizations();
            // Sort by name (assuming the service doesn't already sort them)
            organizations.sort((org1, org2) -> {
                String name1 = org1.getOrganizationName() != null ? org1.getOrganizationName() : "";
                String name2 = org2.getOrganizationName() != null ? org2.getOrganizationName() : "";
                return name1.compareToIgnoreCase(name2);
            });
            return ResponseEntity.ok(organizations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get newest organizations (ENHANCED - includes recently created organizations)
     * GET /api/organizations/sorted/newest?limit=50
     */
    @GetMapping("/sorted/newest")
    public ResponseEntity<List<OrganizationProfileDTO>> getNewestOrganizations(
            @RequestParam(defaultValue = "50") int limit) {
        try {
            // Try to get recently created organizations first
            List<OrganizationProfileDTO> organizations = organizationProfileService.getRecentlyCreatedOrganizations(30, limit);

            if (organizations.isEmpty()) {
                // Fallback to the existing method
                organizations = organizationProfileService.getVerifiedOrganizations();
                // Sort by creation date (newest first)
                organizations.sort((org1, org2) -> {
                    if (org1.getCreatedAt() == null) return 1;
                    if (org2.getCreatedAt() == null) return -1;
                    return org2.getCreatedAt().compareTo(org1.getCreatedAt());
                });
                organizations = organizations.stream().limit(limit).collect(Collectors.toList());
            }

            System.out.println("Returning " + organizations.size() + " newest organizations");
            return ResponseEntity.ok(organizations);

        } catch (Exception e) {
            System.err.println("Error in getNewestOrganizations: " + e.getMessage());
            e.printStackTrace();

            // Final fallback
            try {
                List<OrganizationProfileDTO> fallback = organizationProfileService.getVerifiedOrganizations();
                return ResponseEntity.ok(fallback.stream().limit(limit).collect(Collectors.toList()));
            } catch (Exception fallbackError) {
                return ResponseEntity.ok(List.of());
            }
        }
    }

    /**
     * Get most active organizations (by events hosted)
     * GET /api/organizations/sorted/most-active
     */
    @GetMapping("/sorted/most-active")
    public ResponseEntity<List<OrganizationProfileDTO>> getMostActiveOrganizations() {
        try {
            List<OrganizationProfileDTO> organizations = organizationProfileService.getMostActiveOrganizations(20);
            return ResponseEntity.ok(organizations);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to verified organizations
            try {
                List<OrganizationProfileDTO> fallback = organizationProfileService.getVerifiedOrganizations();
                fallback.sort((org1, org2) -> {
                    Integer events1 = org1.getTotalEventsHosted() != null ? org1.getTotalEventsHosted() : 0;
                    Integer events2 = org2.getTotalEventsHosted() != null ? org2.getTotalEventsHosted() : 0;
                    return events2.compareTo(events1);
                });
                return ResponseEntity.ok(fallback);
            } catch (Exception fallbackError) {
                return ResponseEntity.internalServerError().build();
            }
        }
    }

    /**
     * Get highest impact organizations (by volunteers served)
     * GET /api/organizations/sorted/highest-impact
     */
    @GetMapping("/sorted/highest-impact")
    public ResponseEntity<List<OrganizationProfileDTO>> getHighestImpactOrganizations() {
        try {
            List<OrganizationProfileDTO> organizations = organizationProfileService.getOrganizationsByVolunteerImpact(20);
            return ResponseEntity.ok(organizations);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to verified organizations sorted by volunteers served
            try {
                List<OrganizationProfileDTO> fallback = organizationProfileService.getVerifiedOrganizations();
                fallback.sort((org1, org2) -> {
                    Integer vol1 = org1.getTotalVolunteersServed() != null ? org1.getTotalVolunteersServed() : 0;
                    Integer vol2 = org2.getTotalVolunteersServed() != null ? org2.getTotalVolunteersServed() : 0;
                    return vol2.compareTo(vol1);
                });
                return ResponseEntity.ok(fallback);
            } catch (Exception fallbackError) {
                return ResponseEntity.internalServerError().build();
            }
        }
    }

    // ==========================================
    // FILTERED ENDPOINTS
    // ==========================================

    /**
     * Get verified organizations
     * GET /api/organizations/verified
     */
    @GetMapping("/verified")
    public ResponseEntity<List<OrganizationProfileDTO>> getVerifiedOrganizations() {
        try {
            List<OrganizationProfileDTO> organizations = organizationProfileService.getVerifiedOrganizations();
            return ResponseEntity.ok(organizations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get non-profit organizations
     * GET /api/organizations/non-profit
     */
    @GetMapping("/non-profit")
    public ResponseEntity<List<OrganizationProfileDTO>> getNonProfitOrganizations() {
        try {
            List<OrganizationProfileDTO> organizations = organizationProfileService.getNonProfitOrganizations();
            return ResponseEntity.ok(organizations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get highly verified organizations
     * GET /api/organizations/highly-verified
     */
    @GetMapping("/highly-verified")
    public ResponseEntity<List<OrganizationProfileDTO>> getHighlyVerifiedOrganizations() {
        try {
            List<OrganizationProfileDTO> organizations = organizationProfileService.getHighlyVerifiedOrganizations();
            return ResponseEntity.ok(organizations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get international organizations
     * GET /api/organizations/international
     */
    @GetMapping("/international")
    public ResponseEntity<List<OrganizationProfileDTO>> getInternationalOrganizations() {
        try {
            List<OrganizationProfileDTO> organizations = organizationProfileService.getInternationalOrganizations();
            return ResponseEntity.ok(organizations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==========================================
    // RECENTLY CREATED ORGANIZATIONS ENDPOINTS (NEW)
    // ==========================================

    /**
     * Get recently created organizations
     * GET /api/organizations/recently-created?days=7&limit=50
     */
    @GetMapping("/recently-created")
    public ResponseEntity<List<OrganizationProfileDTO>> getRecentlyCreatedOrganizations(
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean verified) {
        try {
            List<OrganizationProfileDTO> organizations = organizationProfileService.getRecentlyCreatedOrganizations(days, limit);

            // Apply additional filters if provided
            if (category != null && !category.trim().isEmpty()) {
                organizations = organizations.stream()
                        .filter(org -> org.getPrimaryCategory() != null &&
                                org.getPrimaryCategory().toLowerCase().contains(category.toLowerCase()))
                        .collect(Collectors.toList());
            }

            if (verified != null) {
                organizations = organizations.stream()
                        .filter(org -> verified.equals(org.getIsVerified()))
                        .collect(Collectors.toList());
            }

            System.out.println("Returning " + organizations.size() + " recently created organizations");
            return ResponseEntity.ok(organizations);

        } catch (Exception e) {
            System.err.println("Error in getRecentlyCreatedOrganizations: " + e.getMessage());
            e.printStackTrace();

            // Fallback to verified organizations
            try {
                List<OrganizationProfileDTO> fallback = organizationProfileService.getVerifiedOrganizations();
                return ResponseEntity.ok(fallback.stream().limit(limit).collect(Collectors.toList()));
            } catch (Exception fallbackError) {
                return ResponseEntity.ok(List.of());
            }
        }
    }

    /**
     * Get recently updated organizations
     * GET /api/organizations/recently-updated?days=7&limit=50
     */
    @GetMapping("/recently-updated")
    public ResponseEntity<List<OrganizationProfileDTO>> getRecentlyUpdatedOrganizations(
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "50") int limit) {
        try {
            List<OrganizationProfileDTO> organizations = organizationProfileService.getRecentlyUpdatedOrganizations(days, limit);
            System.out.println("Returning " + organizations.size() + " recently updated organizations");
            return ResponseEntity.ok(organizations);

        } catch (Exception e) {
            System.err.println("Error in getRecentlyUpdatedOrganizations: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Find specific organization for immediate post-creation search
     * GET /api/organizations/find?name=exactName&recent=true
     */
    @GetMapping("/find")
    public ResponseEntity<OrganizationProfileDTO> findSpecificOrganization(
            @RequestParam String name,
            @RequestParam(defaultValue = "false") boolean recent) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            OrganizationProfileDTO organization = null;

            if (recent) {
                // Search in recently created organizations first
                List<OrganizationProfileDTO> recentOrgs = organizationProfileService.getRecentlyCreatedOrganizations(1, 100);
                organization = recentOrgs.stream()
                        .filter(org -> org.getOrganizationName() != null &&
                                org.getOrganizationName().equalsIgnoreCase(name.trim()))
                        .findFirst()
                        .orElse(null);
            }

            if (organization == null) {
                // Try the general search
                organization = organizationProfileService.findOrganizationByName(name.trim());
            }

            if (organization != null) {
                System.out.println("Found organization: " + name);
                return ResponseEntity.ok(organization);
            } else {
                System.out.println("Organization not found: " + name);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            System.err.println("Error in findSpecificOrganization: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Refresh organization data - get the latest organizations
     * GET /api/organizations/refresh?maxAgeMinutes=5
     */
    @GetMapping("/refresh")
    public ResponseEntity<List<OrganizationProfileDTO>> refreshOrganizationData(
            @RequestParam(defaultValue = "5") int maxAgeMinutes) {
        try {
            List<OrganizationProfileDTO> organizations = organizationProfileService.refreshOrganizationData(maxAgeMinutes);
            System.out.println("Refreshed organization data: " + organizations.size() + " organizations");
            return ResponseEntity.ok(organizations);

        } catch (Exception e) {
            System.err.println("Error in refreshOrganizationData: " + e.getMessage());
            e.printStackTrace();

            // Fallback to verified organizations
            try {
                List<OrganizationProfileDTO> fallback = organizationProfileService.getVerifiedOrganizations();
                return ResponseEntity.ok(fallback);
            } catch (Exception fallbackError) {
                return ResponseEntity.ok(List.of());
            }
        }
    }

    // ==========================================
    // UTILITY ENDPOINTS
    // ==========================================

    /**
     * Get enhanced organization statistics (includes recently created stats)
     * GET /api/organizations/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getOrganizationStats() {
        try {
            // Get basic counts
            List<OrganizationProfileDTO> allOrgs = organizationProfileService.getVerifiedOrganizations();
            List<OrganizationProfileDTO> recentOrgs = organizationProfileService.getRecentlyCreatedOrganizations(30, 1000);
            List<OrganizationProfileDTO> newOrgs = organizationProfileService.getRecentlyCreatedOrganizations(7, 1000);
            List<OrganizationProfileDTO> nonProfit = organizationProfileService.getNonProfitOrganizations();
            List<OrganizationProfileDTO> international = organizationProfileService.getInternationalOrganizations();

            Map<String, Object> stats = new HashMap<>();
            stats.put("total", allOrgs.size());
            stats.put("verified", allOrgs.size()); // Since we're only showing verified
            stats.put("nonProfit", nonProfit.size());
            stats.put("international", international.size());
            stats.put("recentlyCreated", recentOrgs.size());
            stats.put("newThisWeek", newOrgs.size());
            stats.put("lastUpdated", java.time.LocalDateTime.now().toString());

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            System.err.println("Error in getOrganizationStats: " + e.getMessage());
            e.printStackTrace();

            // Return basic stats
            Map<String, Object> fallbackStats = new HashMap<>();
            fallbackStats.put("total", 0);
            fallbackStats.put("verified", 0);
            fallbackStats.put("nonProfit", 0);
            fallbackStats.put("international", 0);
            fallbackStats.put("recentlyCreated", 0);
            fallbackStats.put("newThisWeek", 0);
            fallbackStats.put("lastUpdated", java.time.LocalDateTime.now().toString());

            return ResponseEntity.ok(fallbackStats);
        }
    }

    /**
     * Check if organization name exists
     * GET /api/organizations/exists?name=ExampleOrg
     */
    @GetMapping("/exists")
    public ResponseEntity<Map<String, Object>> checkOrganizationNameExists(@RequestParam String name) {
        try {
            List<OrganizationProfileDTO> organizations = organizationProfileService.searchOrganizationsByName(name);
            boolean exists = organizations.stream()
                    .anyMatch(org -> name.equalsIgnoreCase(org.getOrganizationName()));

            Map<String, Object> response = new HashMap<>();
            response.put("exists", exists);
            response.put("name", name);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get organization categories (for filter dropdowns)
     * GET /api/organizations/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getOrganizationCategories() {
        try {
            // Return common categories - could be enhanced to pull from database
            List<String> categories = List.of(
                    "Education", "Environment", "Healthcare", "Animal Welfare", "Community Service",
                    "Human Services", "Arts & Culture", "Youth Development", "Senior Services",
                    "Hunger & Homelessness", "Disaster Relief", "International", "Sports & Recreation",
                    "Mental Health", "Veterans", "Women's Issues", "Children & Families",
                    "Disability Services", "Religious", "Political", "LGBTQ+", "Technology",
                    "Research & Advocacy", "Public Safety");
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get organization types (for filter dropdowns)
     * GET /api/organizations/types
     */
    @GetMapping("/types")
    public ResponseEntity<List<String>> getOrganizationTypes() {
        try {
            List<String> types = List.of(
                    "Non-Profit", "Charity", "NGO", "Community Organization",
                    "Religious Organization", "Educational Institution", "Government Agency",
                    "Social Enterprise", "Foundation", "Cooperative");
            return ResponseEntity.ok(types);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get organization locations (for filter dropdowns)
     * GET /api/organizations/locations
     */
    @GetMapping("/locations")
    public ResponseEntity<List<String>> getOrganizationLocations() {
        try {
            List<String> locations = List.of(
                    "United States", "Canada", "United Kingdom", "Australia", "Germany",
                    "France", "Netherlands", "Sweden", "Denmark", "Ireland", "Switzerland");
            return ResponseEntity.ok(locations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    /**
     * Convert employee count range to organization size category
     */
    private String convertEmployeeCountToSize(Integer minEmployees, Integer maxEmployees) {
        if (minEmployees == null && maxEmployees == null) return "";

        if (maxEmployees != null && maxEmployees <= 50) return "Small (1-50)";
        if (maxEmployees != null && maxEmployees <= 200) return "Medium (51-200)";
        if (maxEmployees != null && maxEmployees <= 1000) return "Large (201-1000)";
        if (minEmployees != null && minEmployees > 1000) return "Enterprise (1000+)";

        return "";
    }
}