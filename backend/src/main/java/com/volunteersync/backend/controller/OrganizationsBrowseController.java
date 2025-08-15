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

    /**
     * Search organizations by name
     * GET /api/organizations/search/name?name=searchTerm
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<OrganizationProfileDTO>> searchOrganizationsByName(
            @RequestParam String name) {
        try {
            List<OrganizationProfileDTO> organizations = organizationProfileService.searchOrganizationsByName(name);
            return ResponseEntity.ok(organizations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search organizations by category
     * GET /api/organizations/search/category?category=Education
     */
    @GetMapping("/search/category")
    public ResponseEntity<List<OrganizationProfileDTO>> searchOrganizationsByCategory(
            @RequestParam String category) {
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
    public ResponseEntity<List<OrganizationProfileDTO>> searchOrganizationsByType(
            @RequestParam String type) {
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
     * Get newest organizations
     * GET /api/organizations/sorted/newest
     */
    @GetMapping("/sorted/newest")
    public ResponseEntity<List<OrganizationProfileDTO>> getNewestOrganizations() {
        try {
            List<OrganizationProfileDTO> organizations = organizationProfileService.getVerifiedOrganizations();
            // Sort by creation date (newest first)
            organizations.sort((org1, org2) -> {
                if (org1.getCreatedAt() == null) return 1;
                if (org2.getCreatedAt() == null) return -1;
                return org2.getCreatedAt().compareTo(org1.getCreatedAt());
            });
            return ResponseEntity.ok(organizations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
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

    /**
     * Get organization statistics
     * GET /api/organizations/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getOrganizationStats() {
        try {
            // Create basic stats from available data
            List<OrganizationProfileDTO> verified = organizationProfileService.getVerifiedOrganizations();
            List<OrganizationProfileDTO> nonProfit = organizationProfileService.getNonProfitOrganizations();
            List<OrganizationProfileDTO> international = organizationProfileService.getInternationalOrganizations();

            Map<String, Object> stats = new HashMap<>();
            stats.put("total", verified.size() + nonProfit.size()); // Rough estimate
            stats.put("verified", verified.size());
            stats.put("nonProfit", nonProfit.size());
            stats.put("international", international.size());
            stats.put("lastUpdated", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
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
                "Research & Advocacy", "Public Safety"
            );
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
                "Social Enterprise", "Foundation", "Cooperative"
            );
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
                "France", "Netherlands", "Sweden", "Denmark", "Ireland", "Switzerland"
            );
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