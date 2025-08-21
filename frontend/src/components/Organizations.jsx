import React, { useState, useEffect, useCallback, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import {
  Search,
  MapPin,
  Filter,
  Users,
  ExternalLink,
  Star,
  Calendar,
  Award,
  ChevronLeft,
  ChevronRight,
  RefreshCw,
} from "lucide-react";
import "./Organizations.css";
import { getCurrentUser, isLoggedIn } from "../services/authService";
import findOrganizationService from "../services/findOrganizationService";

const Organizations = () => {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState("");
  const [locationSearchTerm, setLocationSearchTerm] = useState("");
  const [selectedCategories, setSelectedCategories] = useState([]);
  const [selectedLocations, setSelectedLocations] = useState([]);
  const [selectedDatePosted, setSelectedDatePosted] = useState([]);
  const [selectedSizes, setSelectedSizes] = useState([]);
  const [showMoreCategories, setShowMoreCategories] = useState(false);
  const [showMoreLocations, setShowMoreLocations] = useState(false);

  // Backend data state
  const [organizations, setOrganizations] = useState([]);
  const [filteredOrganizations, setFilteredOrganizations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchLoading, setSearchLoading] = useState(false);
  const [error, setError] = useState(null);
  const [lastRefresh, setLastRefresh] = useState(new Date());

  // Pagination state
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(20);
  const [totalPages, setTotalPages] = useState(1);
  const [paginatedOrganizations, setPaginatedOrganizations] = useState([]);

  // Search debounce
  const [searchDebounceTimer, setSearchDebounceTimer] = useState(null);

  // Organization categories
  const categories = [
    "Education",
    "Environment",
    "Healthcare",
    "Animal Welfare",
    "Community Service",
    "Human Services",
    "Arts & Culture",
    "Youth Development",
    "Senior Services",
    "Hunger & Homelessness",
    "Disaster Relief",
    "International",
    "Sports & Recreation",
    "Mental Health",
    "Veterans",
    "Women's Issues",
    "Children & Families",
    "Disability Services",
    "Religious",
    "Political",
    "LGBTQ+",
    "Technology",
    "Research & Advocacy",
    "Public Safety",
  ];

  const locations = [
    "United States",
    "Canada",
    "United Kingdom",
    "Australia",
    "Germany",
    "France",
    "Netherlands",
    "Sweden",
    "Denmark",
    "Ireland",
    "Switzerland",
  ];

  const datePostedOptions = [
    "Last 24 hours",
    "Last 3 days",
    "Last 7 days",
    "Last 14 days",
    "Last 30 days",
  ];

  const organizationSizes = [
    "Small (1-50)",
    "Medium (51-200)",
    "Large (201-1000)",
    "Enterprise (1000+)",
  ];

  // Helper function to navigate to organization details
  const handleOrganizationClick = (organizationId) => {
    navigate(`/find-organizations/${organizationId}`);
  };

  // Helper function to get category CSS class
  const getCategoryClass = (category) => {
    if (!category) return "";
    const categoryLower = category
      .toLowerCase()
      .replace(/[^\w\s]/g, "")
      .replace(/\s+/g, "-");

    const categoryMap = {
      education: "education",
      environment: "environment",
      healthcare: "healthcare",
      "animal-welfare": "animal-welfare",
      "community-service": "community-service",
      "human-services": "human-services",
      "arts-culture": "arts-culture",
      "arts--culture": "arts-culture",
      "youth-development": "youth-development",
      "senior-services": "senior-services",
      "hunger--homelessness": "hunger-homelessness",
      "hunger-homelessness": "hunger-homelessness",
      "disaster-relief": "disaster-relief",
      international: "international",
      "sports--recreation": "sports-recreation",
      "sports-recreation": "sports-recreation",
      "mental-health": "mental-health",
      veterans: "veterans",
      "womens-issues": "womens-issues",
      "children--families": "children-families",
      "children-families": "children-families",
      "disability-services": "disability-services",
      religious: "religious",
      political: "political",
      lgbtq: "lgbtq",
      technology: "technology",
      "research--advocacy": "research-advocacy",
      "research-advocacy": "research-advocacy",
      "public-safety": "public-safety",
    };

    return categoryMap[categoryLower] || "";
  };

  // Helper function to get organization type CSS class
  const getOrganizationTypeClass = (primaryCategory, categories) => {
    const allCategories = [
      primaryCategory,
      ...(categories ? categories.split(",").map((cat) => cat.trim()) : []),
    ];

    if (allCategories.some((cat) => cat?.toLowerCase().includes("education")))
      return "education";
    if (
      allCategories.some(
        (cat) =>
          cat?.toLowerCase().includes("healthcare") ||
          cat?.toLowerCase().includes("health")
      )
    )
      return "healthcare";
    if (allCategories.some((cat) => cat?.toLowerCase().includes("environment")))
      return "environment";
    if (allCategories.some((cat) => cat?.toLowerCase().includes("animal")))
      return "animal-welfare";
    if (allCategories.some((cat) => cat?.toLowerCase().includes("community")))
      return "community-service";
    if (
      allCategories.some((cat) => cat?.toLowerCase().includes("human services"))
    )
      return "human-services";
    if (
      allCategories.some(
        (cat) =>
          cat?.toLowerCase().includes("arts") ||
          cat?.toLowerCase().includes("culture")
      )
    )
      return "arts-culture";
    if (allCategories.some((cat) => cat?.toLowerCase().includes("youth")))
      return "youth-development";
    if (allCategories.some((cat) => cat?.toLowerCase().includes("senior")))
      return "senior-services";

    return "default";
  };

  // ✅ ENHANCED: Unified real-time search and filter function
  const performUnifiedRealtimeSearch = useCallback(
    async (searchParams = {}, delay = 0) => {
      if (searchDebounceTimer) {
        clearTimeout(searchDebounceTimer);
      }

      const executeSearch = async () => {
        try {
          setSearchLoading(true);
          setError(null);

          const {
            searchTerm: term = searchTerm,
            locationTerm = locationSearchTerm,
            categories = selectedCategories,
            locations = selectedLocations,
            sizes = selectedSizes,
            datePosted = selectedDatePosted,
            forceRefresh = false,
          } = searchParams;

          console.log("🚀 Unified real-time search with params:", {
            term,
            locationTerm,
            categories,
            locations,
            sizes,
            datePosted,
            forceRefresh
          });

          // ✅ ENHANCED: Build comprehensive search parameters for backend
          const enhancedSearchParams = {
            name: term,
            // Send first category to backend for primary filtering
            category: categories.length > 0 ? categories[0] : undefined,
            // Send location info to backend
            city: locationTerm,
            state: locationTerm,
            country: locationTerm,
            // Force refresh for real-time data
            forceRefresh: forceRefresh || isDataOld(),
            limit: 200,
            // ✅ NEW: Include filter hints for backend optimization
            filterHints: {
              hasMultipleCategories: categories.length > 1,
              hasLocationFilters: locations.length > 0,
              hasSizeFilters: sizes.length > 0,
              hasDateFilters: datePosted.length > 0,
              locationSearchTerm: locationTerm
            }
          };

          let searchResult;

          // Determine search strategy based on complexity
          const hasComplexFilters = 
            categories.length > 0 ||
            locations.length > 0 ||
            sizes.length > 0 ||
            datePosted.length > 0 ||
            locationTerm;

          if (!term && !hasComplexFilters) {
            // Load all with real-time refresh
            console.log("📋 Loading all organizations with real-time refresh");
            searchResult = await findOrganizationService.refreshLiveData({
              force: forceRefresh,
              maxAgeMinutes: 2, // Shorter cache for better real-time performance
              includeStats: true,
              limit: 200,
            });
            
            searchResult = {
              data: searchResult.organizations,
              timestamp: searchResult.refreshTimestamp,
              resultCount: searchResult.totalCount,
              source: "live_refresh_all"
            };
          } else {
            // Use enhanced real-time search for all filtered queries
            console.log("🎯 Using enhanced real-time search for filtering");
            searchResult = await findOrganizationService.performRealtimeSearch(enhancedSearchParams);
          }

          let searchResults = searchResult.data || [];

          // ✅ ENHANCED: Apply remaining filters with real-time data
          if (categories.length > 1 && searchResults.length > 0) {
            console.log("🔍 Applying additional category filters");
            searchResults = searchResults.filter((org) => {
              const orgCategories = [
                org.primaryCategory,
                ...(org.categories?.split(",").map((cat) => cat.trim()) || [])
              ].filter(Boolean);
              
              return categories.some(category =>
                orgCategories.some(orgCat => 
                  orgCat.toLowerCase().includes(category.toLowerCase()) ||
                  category.toLowerCase().includes(orgCat.toLowerCase())
                )
              );
            });
          }

          if (locations.length > 0 && searchResults.length > 0) {
            console.log("🌍 Applying location filters");
            searchResults = searchResults.filter((org) =>
              locations.some(location =>
                org.country?.toLowerCase().includes(location.toLowerCase()) ||
                org.state?.toLowerCase().includes(location.toLowerCase()) ||
                org.city?.toLowerCase().includes(location.toLowerCase())
              )
            );
          }

          if (sizes.length > 0 && searchResults.length > 0) {
            console.log("👥 Applying size filters");
            searchResults = searchResults.filter((org) => {
              const employeeCount = org.employeeCount || 0;
              return sizes.some((size) => {
                switch (size) {
                  case "Small (1-50)":
                    return employeeCount >= 1 && employeeCount <= 50;
                  case "Medium (51-200)":
                    return employeeCount >= 51 && employeeCount <= 200;
                  case "Large (201-1000)":
                    return employeeCount >= 201 && employeeCount <= 1000;
                  case "Enterprise (1000+)":
                    return employeeCount > 1000;
                  default:
                    return true;
                }
              });
            });
          }

          if (datePosted.length > 0 && searchResults.length > 0) {
            console.log("📅 Applying date filters");
            const now = new Date();
            searchResults = searchResults.filter((org) => {
              const updatedDate = new Date(org.updatedAt || org.createdAt);
              
              return datePosted.some((option) => {
                const diffHours = (now - updatedDate) / (1000 * 60 * 60);
                const diffDays = diffHours / 24;
                
                switch (option) {
                  case "Last 24 hours":
                    return diffHours <= 24;
                  case "Last 3 days":
                    return diffDays <= 3;
                  case "Last 7 days":
                    return diffDays <= 7;
                  case "Last 14 days":
                    return diffDays <= 14;
                  case "Last 30 days":
                    return diffDays <= 30;
                  default:
                    return true;
                }
              });
            });
          }

          // ✅ ENHANCED: Additional location term filtering if specified
          if (locationTerm && searchResults.length > 0) {
            console.log("🗺️ Applying location search term filter");
            searchResults = searchResults.filter((org) =>
              org.city?.toLowerCase().includes(locationTerm.toLowerCase()) ||
              org.state?.toLowerCase().includes(locationTerm.toLowerCase()) ||
              org.country?.toLowerCase().includes(locationTerm.toLowerCase()) ||
              org.zipCode?.includes(locationTerm) ||
              org.address?.toLowerCase().includes(locationTerm.toLowerCase())
            );
          }

          console.log(`✅ Enhanced unified search completed: ${searchResults.length} organizations found`);
          console.log(`📊 Search details:`, {
            source: searchResult.source,
            originalCount: searchResult.resultCount,
            filteredCount: searchResults.length,
            appliedFilters: {
              categories: categories.length,
              locations: locations.length,
              sizes: sizes.length,
              datePosted: datePosted.length,
              hasLocationTerm: !!locationTerm
            }
          });

          setOrganizations(searchResults);
          setFilteredOrganizations(searchResults);
          setCurrentPage(1);

          // Update last refresh time
          if (searchResult.timestamp) {
            setLastRefresh(new Date(searchResult.timestamp));
          }

        } catch (err) {
          console.error("❌ Enhanced unified search failed:", err);
          setError("Search failed. Please try again.");

          // Enhanced fallback strategy
          try {
            console.log("🔄 Attempting enhanced fallback...");
            const fallbackResult = await findOrganizationService.refreshLiveData({
              force: false,
              maxAgeMinutes: 10,
              limit: 100,
            });

            if (fallbackResult.success && fallbackResult.organizations.length > 0) {
              // Apply client-side filters to fallback data
              let fallbackFiltered = fallbackResult.organizations;
              
              if (searchParams.searchTerm || searchParams.categories?.length > 0) {
                fallbackFiltered = applyClientSideFilters(fallbackFiltered, searchParams);
              }
              
              setOrganizations(fallbackFiltered);
              setFilteredOrganizations(fallbackFiltered);
              setCurrentPage(1);
              console.log("✅ Enhanced fallback successful");
            }
          } catch (fallbackErr) {
            console.error("❌ Enhanced fallback also failed:", fallbackErr);
          }
        } finally {
          setSearchLoading(false);
        }
      };

      if (delay > 0) {
        const timer = setTimeout(executeSearch, delay);
        setSearchDebounceTimer(timer);
      } else {
        await executeSearch();
      }
    },
    [searchTerm, locationSearchTerm, selectedCategories, selectedLocations, selectedSizes, selectedDatePosted, searchDebounceTimer]
  );

  // ✅ ENHANCED: Client-side fallback filters with better matching
  const applyClientSideFilters = (orgs, params) => {
    let filtered = [...orgs];

    if (params.searchTerm) {
      filtered = filtered.filter(org =>
        org.organizationName?.toLowerCase().includes(params.searchTerm.toLowerCase()) ||
        org.description?.toLowerCase().includes(params.searchTerm.toLowerCase()) ||
        org.primaryCategory?.toLowerCase().includes(params.searchTerm.toLowerCase()) ||
        org.categories?.toLowerCase().includes(params.searchTerm.toLowerCase())
      );
    }

    if (params.categories?.length > 0) {
      filtered = filtered.filter(org => {
        const orgCategories = [
          org.primaryCategory,
          ...(org.categories?.split(",").map(cat => cat.trim()) || [])
        ].filter(Boolean);
        
        return params.categories.some(category =>
          orgCategories.some(orgCat => 
            orgCat.toLowerCase().includes(category.toLowerCase()) ||
            category.toLowerCase().includes(orgCat.toLowerCase())
          )
        );
      });
    }

    return filtered;
  };

  // ✅ ENHANCED: Manual refresh function
  const handleRefresh = async () => {
    console.log("🔄 Manual refresh triggered");
    setLastRefresh(new Date());
    
    await performUnifiedRealtimeSearch({
      searchTerm,
      locationTerm: locationSearchTerm,
      categories: selectedCategories,
      locations: selectedLocations,
      sizes: selectedSizes,
      datePosted: selectedDatePosted,
      forceRefresh: true
    });
  };

  // ✅ ENHANCED: Check if data is old
  const isDataOld = () => {
    const now = new Date();
    const timeSinceRefresh = now - lastRefresh;
    return timeSinceRefresh > 60000; // 1 minute
  };

  // Load organizations on component mount
  useEffect(() => {
    loadOrganizations();
  }, []);

  // ✅ ENHANCED: React to filter changes with unified search
  useEffect(() => {
    const searchParams = {
      searchTerm,
      locationTerm: locationSearchTerm,
      categories: selectedCategories,
      locations: selectedLocations,
      sizes: selectedSizes,
      datePosted: selectedDatePosted,
    };

    // Use immediate search for filter changes (no delay)
    // Use debounced search for text inputs only
    if (searchTerm || locationSearchTerm) {
      performUnifiedRealtimeSearch(searchParams, 500); // 500ms delay for text
    } else {
      performUnifiedRealtimeSearch(searchParams, 0); // Immediate for filters
    }
  }, [
    searchTerm,
    locationSearchTerm,
    selectedCategories,
    selectedLocations,
    selectedDatePosted,
    selectedSizes,
    performUnifiedRealtimeSearch
  ]);

  // Update pagination when filtered results change
  useEffect(() => {
    updatePagination();
  }, [filteredOrganizations, currentPage]);

  const loadOrganizations = async () => {
    try {
      setLoading(true);
      setError(null);
      console.log("📋 Initial load of organizations");
      
      // Use real-time refresh for initial load
      const refreshResult = await findOrganizationService.refreshLiveData({
        force: false,
        maxAgeMinutes: 5,
        includeStats: true,
        limit: 200,
      });
      
      if (refreshResult.success) {
        setOrganizations(refreshResult.organizations);
        setFilteredOrganizations(refreshResult.organizations);
        setLastRefresh(new Date(refreshResult.refreshTimestamp));
      } else {
        throw new Error("Failed to load organizations");
      }
    } catch (err) {
      console.error("Failed to load organizations:", err);
      setError("Failed to load organizations. Please try again later.");
    } finally {
      setLoading(false);
    }
  };

  const updatePagination = () => {
    const total = Math.ceil(filteredOrganizations.length / itemsPerPage);
    setTotalPages(total);

    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const paginated = filteredOrganizations.slice(startIndex, endIndex);
    setPaginatedOrganizations(paginated);
  };

  const handlePageChange = (page) => {
    if (page >= 1 && page <= totalPages) {
      setCurrentPage(page);
      document
        .querySelector(".organizations-main")
        ?.scrollIntoView({ behavior: "smooth" });
    }
  };

  const getPageNumbers = () => {
    const delta = 2;
    const range = [];
    const rangeWithDots = [];

    for (
      let i = Math.max(2, currentPage - delta);
      i <= Math.min(totalPages - 1, currentPage + delta);
      i++
    ) {
      range.push(i);
    }

    if (currentPage - delta > 2) {
      rangeWithDots.push(1, "...");
    } else {
      rangeWithDots.push(1);
    }

    rangeWithDots.push(...range);

    if (currentPage + delta < totalPages - 1) {
      rangeWithDots.push("...", totalPages);
    } else if (totalPages > 1) {
      rangeWithDots.push(totalPages);
    }

    return rangeWithDots;
  };

  // ✅ ENHANCED: Filter toggle functions trigger immediate real-time search
  const handleCategoryToggle = (category) => {
    const newCategories = selectedCategories.includes(category)
      ? selectedCategories.filter((c) => c !== category)
      : [...selectedCategories, category];
    
    setSelectedCategories(newCategories);
  };

  const handleLocationToggle = (location) => {
    const newLocations = selectedLocations.includes(location)
      ? selectedLocations.filter((l) => l !== location)
      : [...selectedLocations, location];
      
    setSelectedLocations(newLocations);
  };

  const handleDatePostedToggle = (option) => {
    const newDatePosted = selectedDatePosted.includes(option)
      ? selectedDatePosted.filter((d) => d !== option)
      : [...selectedDatePosted, option];
      
    setSelectedDatePosted(newDatePosted);
  };

  const handleSizeToggle = (size) => {
    const newSizes = selectedSizes.includes(size)
      ? selectedSizes.filter((s) => s !== size)
      : [...selectedSizes, size];
      
    setSelectedSizes(newSizes);
  };

  const clearAllFilters = () => {
    setSelectedCategories([]);
    setSelectedLocations([]);
    setSelectedDatePosted([]);
    setSelectedSizes([]);
    setSearchTerm("");
    setLocationSearchTerm("");
  };

  const hasActiveFilters = useMemo(
    () =>
      selectedCategories.length > 0 ||
      selectedLocations.length > 0 ||
      selectedDatePosted.length > 0 ||
      selectedSizes.length > 0 ||
      searchTerm ||
      locationSearchTerm,
    [
      selectedCategories,
      selectedLocations,
      selectedDatePosted,
      selectedSizes,
      searchTerm,
      locationSearchTerm,
    ]
  );

  const formatFoundedYear = (year) => {
    if (!year) return "Founded: N/A";
    return `Founded: ${year}`;
  };

  const getOrganizationSize = (employeeCount) => {
    if (!employeeCount) return "Size: Unknown";
    if (employeeCount <= 50) return "Small (1-50)";
    if (employeeCount <= 200) return "Medium (51-200)";
    if (employeeCount <= 1000) return "Large (201-1000)";
    return "Enterprise (1000+)";
  };

  const getCategoriesArray = (categories, primaryCategory) => {
    const categoryArray = [];

    if (primaryCategory) {
      categoryArray.push(primaryCategory);
    }

    if (categories) {
      const additionalCategories = categories
        .split(",")
        .map((cat) => cat.trim());
      additionalCategories.forEach((cat) => {
        if (cat && !categoryArray.includes(cat)) {
          categoryArray.push(cat);
        }
      });
    }

    return categoryArray.slice(0, 3);
  };

  return (
    <div className="organizations-page">
      <div className="organizations-container">
        {/* Left Sidebar - Filters */}
        <div className="organizations-sidebar">
          <div className="organizations-filter-card">
            <h2 className="organizations-filter-title">
              <Filter />
              Filter Options
            </h2>

            {/* Category Filter */}
            <div className="organizations-filter-section">
              <h3 className="organizations-filter-section-title">Category</h3>
              <div className="organizations-filter-options">
                {categories
                  .slice(0, showMoreCategories ? categories.length : 8)
                  .map((category) => (
                    <div
                      key={category}
                      className={`organizations-filter-option ${
                        selectedCategories.includes(category)
                          ? `active category-${getCategoryClass(category)}`
                          : ""
                      }`}
                      onClick={() => handleCategoryToggle(category)}
                    >
                      {category}
                    </div>
                  ))}
              </div>
              {categories.length > 8 && (
                <button
                  onClick={() => setShowMoreCategories(!showMoreCategories)}
                  className="organizations-show-more-btn"
                >
                  {showMoreCategories ? "Show Less" : "Show More"}
                </button>
              )}
            </div>

            {/* Location Filter */}
            <div className="organizations-filter-section">
              <h3 className="organizations-filter-section-title">Location</h3>
              <div className="organizations-filter-options">
                {locations
                  .slice(0, showMoreLocations ? locations.length : 6)
                  .map((location) => (
                    <div
                      key={location}
                      className={`organizations-filter-option location ${
                        selectedLocations.includes(location) ? "active" : ""
                      }`}
                      onClick={() => handleLocationToggle(location)}
                    >
                      {location}
                    </div>
                  ))}
              </div>
              {locations.length > 6 && (
                <button
                  onClick={() => setShowMoreLocations(!showMoreLocations)}
                  className="organizations-show-more-btn"
                >
                  {showMoreLocations ? "Show Less" : "Show More"}
                </button>
              )}
            </div>

            {/* Date Posted Filter */}
            <div className="organizations-filter-section">
              <h3 className="organizations-filter-section-title">
                Date Updated
              </h3>
              <div className="organizations-filter-options">
                {datePostedOptions.map((option) => (
                  <div
                    key={option}
                    className={`organizations-filter-option date ${
                      selectedDatePosted.includes(option) ? "active" : ""
                    }`}
                    onClick={() => handleDatePostedToggle(option)}
                  >
                    {option}
                  </div>
                ))}
              </div>
            </div>

            {/* Organization Size Filter */}
            <div className="organizations-filter-section">
              <h3 className="organizations-filter-section-title">
                Organization Size
              </h3>
              <div className="organizations-filter-options">
                {organizationSizes.map((size) => (
                  <div
                    key={size}
                    className={`organizations-filter-option size ${
                      selectedSizes.includes(size) ? "active" : ""
                    }`}
                    onClick={() => handleSizeToggle(size)}
                  >
                    {size}
                  </div>
                ))}
              </div>
            </div>

            {/* Clear Filters Button */}
            {hasActiveFilters && (
              <button
                onClick={clearAllFilters}
                className="organizations-clear-filters-btn"
              >
                Clear All Filters
              </button>
            )}
          </div>
        </div>

        {/* Main Content */}
        <div className="organizations-main">
          {/* Header */}
          <div className="organizations-header">
            <div>
              <h1 className="organizations-title">All Organizations</h1>
              <p className="organizations-subtitle">
                {loading
                  ? "Loading..."
                  : `${filteredOrganizations.length} organizations available`}{" "}
                •
                {!loading && totalPages > 0 && (
                  <>
                    Showing {(currentPage - 1) * itemsPerPage + 1}-
                    {Math.min(
                      currentPage * itemsPerPage,
                      filteredOrganizations.length
                    )}{" "}
                    of {filteredOrganizations.length} •{" "}
                  </>
                )}
                Last Updated:{" "}
                <span className="organizations-highlight">
                  {lastRefresh.toLocaleTimeString()}
                </span>
              </p>
            </div>
            {/* Manual refresh button */}
            <button
              onClick={handleRefresh}
              disabled={loading || searchLoading}
              className="organizations-refresh-btn"
              title="Refresh organizations"
            >
              <RefreshCw
                className={`organizations-refresh-icon ${
                  loading || searchLoading ? "spinning" : ""
                }`}
              />
              Refresh
            </button>
          </div>

          {/* Search Bar */}
          <div className="organizations-search-section">
            <div className="organizations-search-input-container">
              <Search className="organizations-search-icon" />
              <input
                type="text"
                placeholder="Search organizations, causes, or locations..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="organizations-search-input"
              />
              {searchLoading && <div className="search-loading-spinner"></div>}
            </div>

            {/* Location Search */}
            <div className="organizations-search-input-container organizations-location-search">
              <MapPin className="organizations-search-icon" />
              <input
                type="text"
                placeholder="Search by city, state, country, or zip code..."
                value={locationSearchTerm}
                onChange={(e) => setLocationSearchTerm(e.target.value)}
                className="organizations-search-input"
              />
            </div>
          </div>

          {/* Active Filters */}
          {hasActiveFilters && (
            <div className="organizations-active-filters">
              <div className="organizations-active-filters-content">
                <Filter className="organizations-active-filters-icon" />
                <span className="organizations-active-filters-label">
                  Active Filters:
                </span>

                {selectedCategories.map((category) => (
                  <span
                    key={category}
                    className={`organizations-filter-chip category ${getCategoryClass(
                      category
                    )}`}
                  >
                    {category}
                    <button
                      onClick={() => handleCategoryToggle(category)}
                      className="organizations-filter-chip-remove"
                    >
                      ×
                    </button>
                  </span>
                ))}

                {selectedLocations.map((location) => (
                  <span
                    key={location}
                    className="organizations-filter-chip location"
                  >
                    {location}
                    <button
                      onClick={() => handleLocationToggle(location)}
                      className="organizations-filter-chip-remove"
                    >
                      ×
                    </button>
                  </span>
                ))}

                {selectedDatePosted.map((date) => (
                  <span key={date} className="organizations-filter-chip date">
                    {date}
                    <button
                      onClick={() => handleDatePostedToggle(date)}
                      className="organizations-filter-chip-remove"
                    >
                      ×
                    </button>
                  </span>
                ))}

                {selectedSizes.map((size) => (
                  <span key={size} className="organizations-filter-chip size">
                    {size}
                    <button
                      onClick={() => handleSizeToggle(size)}
                      className="organizations-filter-chip-remove"
                    >
                      ×
                    </button>
                  </span>
                ))}
              </div>
            </div>
          )}

          {/* Loading State */}
          {(loading || searchLoading) && (
            <div className="organizations-loading">
              <div className="organizations-loading-spinner"></div>
              <p>{loading ? "Loading organizations..." : "Searching..."}</p>
            </div>
          )}

          {/* Error State */}
          {error && (
            <div className="organizations-error">
              <p>{error}</p>
              <button
                onClick={loadOrganizations}
                className="organizations-retry-btn"
              >
                Try Again
              </button>
            </div>
          )}

          {/* Organizations List */}
          {!loading && !error && paginatedOrganizations.length > 0 && (
            <>
              <div className="organizations-list">
                {paginatedOrganizations.map((org) => (
                  <div
                    key={org.id}
                    className="organization-card"
                    onClick={() => handleOrganizationClick(org.id)}
                    role="button"
                    tabIndex={0}
                    onKeyDown={(e) => {
                      if (e.key === "Enter" || e.key === " ") {
                        e.preventDefault();
                        handleOrganizationClick(org.id);
                      }
                    }}
                  >
                    <div className="organization-card-header">
                      <div className="organization-card-image">
                        {org.profileImageUrl ? (
                          <img
                            src={org.profileImageUrl}
                            alt={org.organizationName}
                          />
                        ) : (
                          <div className="organization-card-placeholder">
                            <Users />
                          </div>
                        )}
                        {org.isVerified && (
                          <div className="organization-card-verified">
                            <Star className="organization-card-verified-icon" />
                          </div>
                        )}
                      </div>
                      <div className="organization-card-content">
                        <div className="organization-card-meta">
                          <span
                            className={`organization-card-type ${getOrganizationTypeClass(
                              org.primaryCategory,
                              org.categories
                            )}`}
                          >
                            {org.organizationType ||
                              org.primaryCategory ||
                              "Organization"}
                          </span>
                          {getCategoriesArray(
                            org.categories,
                            org.primaryCategory
                          ).map((category, index) => (
                            <span
                              key={index}
                              className={`organization-card-category ${getCategoryClass(
                                category
                              )}`}
                            >
                              {category}
                            </span>
                          ))}
                        </div>
                        <h3 className="organization-card-title">
                          {org.organizationName}
                        </h3>
                        <p className="organization-card-description">
                          {org.description?.substring(0, 120)}
                          {org.description?.length > 120 ? "..." : ""}
                        </p>
                        <div className="organization-card-details">
                          <div className="organization-card-detail">
                            <MapPin className="organization-card-detail-icon" />
                            <span>
                              {org.city && org.state
                                ? `${org.city}, ${org.state}`
                                : "Location not specified"}
                            </span>
                          </div>
                          <div className="organization-card-detail">
                            <Users className="organization-card-detail-icon" />
                            <span>
                              {getOrganizationSize(org.employeeCount)}
                            </span>
                          </div>
                          <div className="organization-card-detail">
                            <Calendar className="organization-card-detail-icon" />
                            <span>{formatFoundedYear(org.foundedYear)}</span>
                          </div>
                          <div className="organization-card-detail">
                            <Award className="organization-card-detail-icon" />
                            <span>
                              {org.totalEventsHosted || 0} events hosted
                            </span>
                          </div>
                          <div className="organization-card-detail">
                            <Users className="organization-card-detail-icon" />
                            <span>
                              {org.totalVolunteersServed || 0} volunteers served
                            </span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>

              {/* Pagination */}
              {totalPages > 1 && (
                <div className="organizations-pagination">
                  <button
                    onClick={() => handlePageChange(currentPage - 1)}
                    disabled={currentPage === 1}
                    className="organizations-pagination-btn organizations-pagination-prev"
                  >
                    <ChevronLeft className="organizations-pagination-icon" />
                    Previous
                  </button>

                  <div className="organizations-pagination-numbers">
                    {getPageNumbers().map((pageNum, index) => (
                      <button
                        key={index}
                        onClick={() =>
                          typeof pageNum === "number"
                            ? handlePageChange(pageNum)
                            : null
                        }
                        className={`organizations-pagination-number ${
                          pageNum === currentPage ? "active" : ""
                        } ${typeof pageNum !== "number" ? "dots" : ""}`}
                        disabled={typeof pageNum !== "number"}
                      >
                        {pageNum}
                      </button>
                    ))}
                  </div>

                  <button
                    onClick={() => handlePageChange(currentPage + 1)}
                    disabled={currentPage === totalPages}
                    className="organizations-pagination-btn organizations-pagination-next"
                  >
                    Next
                    <ChevronRight className="organizations-pagination-icon" />
                  </button>
                </div>
              )}
            </>
          )}

          {/* Empty State */}
          {!loading && !error && filteredOrganizations.length === 0 && (
            <div className="organizations-empty-state">
              <div className="organizations-empty-state-icon">
                <Users />
              </div>
              {organizations.length === 0 ? (
                <>
                  <h3 className="organizations-empty-state-title">
                    Organizations Coming Soon!
                  </h3>
                  <p className="organizations-empty-state-description">
                    We're working hard to partner with amazing organizations in
                    your area. Check back soon to discover volunteer
                    opportunities that match your interests.
                  </p>
                </>
              ) : (
                <>
                  <h3 className="organizations-empty-state-title">
                    No Organizations Match Your Filters
                  </h3>
                  <p className="organizations-empty-state-description">
                    Try adjusting your search criteria or clearing some filters
                    to see more organizations.
                  </p>
                  <button
                    onClick={clearAllFilters}
                    className="organizations-clear-filters-btn"
                  >
                    Clear All Filters
                  </button>
                </>
              )}
              <div className="organizations-empty-state-cta">
                <p className="organizations-empty-state-cta-text">
                  <strong>Are you an organization?</strong> Join our platform to
                  connect with passionate volunteers and make a greater impact
                  in your community.
                </p>
                <button className="organizations-empty-state-cta-button">
                  Register Your Organization
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Organizations;