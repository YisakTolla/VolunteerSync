// frontend/src/services/findOrganizationService.js

const API_BASE_URL = 'http://localhost:8080/api';

class FindOrganizationService {
  
  /**
   * Find all organizations
   */
  async findAllOrganizations() {
    try {
      console.log('🔍 Fetching all organizations');
      const response = await fetch(`${API_BASE_URL}/organizations`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      const data = await response.json();
      console.log(`✅ Found ${data.length} organizations`);
      return data;
    } catch (error) {
      console.error('Error fetching all organizations:', error);
      throw error;
    }
  }

  /**
   * Find organizations with pagination
   */
  async findAllOrganizationsWithPagination(page = 0, size = 10, sortBy = 'organizationName', sortDirection = 'asc') {
    try {
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        sortBy,
        sortDirection
      });
      
      const response = await fetch(`${API_BASE_URL}/organizations/paginated?${params}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching paginated organizations:', error);
      throw error;
    }
  }

  /**
   * Find organization by ID
   */
  async findOrganizationById(id) {
    try {
      console.log(`🔍 Fetching organization with ID: ${id}`);
      const response = await fetch(`${API_BASE_URL}/organizations/${id}`);
      if (!response.ok) {
        if (response.status === 404) {
          console.log(`❌ Organization ${id} not found`);
          return null;
        }
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      const data = await response.json();
      console.log(`✅ Found organization: ${data.organizationName}`);
      return data;
    } catch (error) {
      console.error(`Error fetching organization ${id}:`, error);
      throw error;
    }
  }

  /**
   * Enhanced search organizations by name with fallback strategies
   */
  async findOrganizationsByName(name) {
    try {
      if (!name || name.trim() === '') {
        console.log('❌ Empty search name provided');
        return [];
      }

      console.log(`🔍 Searching organizations by name: "${name}"`);
      
      // Use the parameter 'name' to match backend expectation
      const params = new URLSearchParams({ name: name.trim() });
      const response = await fetch(`${API_BASE_URL}/organizations/search/name?${params}`);
      
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      
      const result = await response.json();
      const data = Array.isArray(result) ? result : [];
      console.log(`✅ Found ${data.length} organizations matching "${name}"`);
      return data;
      
    } catch (error) {
      console.error('Error searching organizations by name:', error);
      
      // Fallback: try general search
      try {
        console.log('🔄 Trying fallback search...');
        const fallbackData = await this.searchOrganizations({ name });
        return Array.isArray(fallbackData) ? fallbackData : [];
      } catch (fallbackError) {
        console.error('Fallback search also failed:', fallbackError);
        return [];
      }
    }
  }

  /**
   * Find organizations by category
   */
  async findOrganizationsByCategory(category) {
    try {
      const params = new URLSearchParams({ category });
      const response = await fetch(`${API_BASE_URL}/organizations/search/category?${params}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error finding organizations by category:', error);
      throw error;
    }
  }

  /**
   * Find organizations by type
   */
  async findOrganizationsByType(organizationType) {
    try {
      const params = new URLSearchParams({ type: organizationType });
      const response = await fetch(`${API_BASE_URL}/organizations/search/type?${params}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error finding organizations by type:', error);
      throw error;
    }
  }

  /**
   * Find organizations by location
   */
  async findOrganizationsByLocation(city, state) {
    try {
      const params = new URLSearchParams();
      if (city) params.append('city', city);
      if (state) params.append('state', state);
      
      const response = await fetch(`${API_BASE_URL}/organizations/search/location?${params}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error finding organizations by location:', error);
      throw error;
    }
  }

  /**
   * Find verified organizations only
   */
  async findVerifiedOrganizations() {
    try {
      const response = await fetch(`${API_BASE_URL}/organizations/verified`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching verified organizations:', error);
      throw error;
    }
  }

  /**
   * Find organizations by employee count range
   */
  async findOrganizationsByEmployeeCount(minEmployees, maxEmployees) {
    try {
      const params = new URLSearchParams();
      if (minEmployees !== null && minEmployees !== undefined) {
        params.append('minEmployees', minEmployees.toString());
      }
      if (maxEmployees !== null && maxEmployees !== undefined) {
        params.append('maxEmployees', maxEmployees.toString());
      }
      
      const response = await fetch(`${API_BASE_URL}/organizations/search/employee-count?${params}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error finding organizations by employee count:', error);
      throw error;
    }
  }

  /**
   * Search organizations with multiple filters
   */
  async searchOrganizations(filters = {}) {
    try {
      const params = new URLSearchParams();
      
      // Add all provided filters to params
      Object.entries(filters).forEach(([key, value]) => {
        if (value !== null && value !== undefined && value !== '') {
          params.append(key, value.toString());
        }
      });
      
      const response = await fetch(`${API_BASE_URL}/organizations/search?${params}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error searching organizations:', error);
      throw error;
    }
  }

  /**
   * Get organizations sorted by name
   */
  async findOrganizationsSortedByName() {
    try {
      const response = await fetch(`${API_BASE_URL}/organizations/sorted/name`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching organizations sorted by name:', error);
      throw error;
    }
  }

  /**
   * Enhanced get newest organizations with fallback strategies
   */
  async findNewestOrganizations(days = 30, limit = 20) {
    try {
      console.log(`🔍 Searching for organizations created within the last ${days} days`);
      
      // Try the enhanced newest endpoint first
      const response = await fetch(`${API_BASE_URL}/organizations/sorted/newest?limit=${limit}`);
      if (response.ok) {
        const data = await response.json();
        console.log(`✅ Found ${data.length} newest organizations`);
        return Array.isArray(data) ? data : [];
      }
      
      // Fallback to recently-created endpoint
      const recentResponse = await fetch(`${API_BASE_URL}/organizations/recently-created?days=${days}&limit=${limit}`);
      if (recentResponse.ok) {
        const recentData = await recentResponse.json();
        console.log(`✅ Found ${recentData.length} recently created organizations`);
        return Array.isArray(recentData) ? recentData : [];
      }
      
      // Final fallback to verified organizations
      const verifiedResponse = await fetch(`${API_BASE_URL}/organizations/verified`);
      if (verifiedResponse.ok) {
        const verifiedData = await verifiedResponse.json();
        const limitedData = Array.isArray(verifiedData) ? verifiedData.slice(0, limit) : [];
        console.log(`✅ Using verified organizations fallback: ${limitedData.length}`);
        return limitedData;
      }
      
      return [];
      
    } catch (error) {
      console.error('Error fetching newest organizations:', error);
      return [];
    }
  }

  /**
   * Get most active organizations (by events hosted)
   */
  async findMostActiveOrganizations() {
    try {
      const response = await fetch(`${API_BASE_URL}/organizations/sorted/most-active`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching most active organizations:', error);
      throw error;
    }
  }

  /**
   * Get highest impact organizations (by volunteers served)
   */
  async findHighestImpactOrganizations() {
    try {
      const response = await fetch(`${API_BASE_URL}/organizations/sorted/highest-impact`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching highest impact organizations:', error);
      throw error;
    }
  }

  /**
   * Find non-profit organizations
   */
  async findNonProfitOrganizations() {
    try {
      const response = await fetch(`${API_BASE_URL}/organizations/non-profit`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching non-profit organizations:', error);
      throw error;
    }
  }

  /**
   * Find highly verified organizations
   */
  async findHighlyVerifiedOrganizations() {
    try {
      const response = await fetch(`${API_BASE_URL}/organizations/highly-verified`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching highly verified organizations:', error);
      throw error;
    }
  }

  /**
   * Find international organizations
   */
  async findInternationalOrganizations() {
    try {
      const response = await fetch(`${API_BASE_URL}/organizations/international`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching international organizations:', error);
      throw error;
    }
  }

  /**
   * Enhanced get organization statistics
   */
  async getOrganizationStats() {
    try {
      const response = await fetch(`${API_BASE_URL}/organizations/stats`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching organization stats:', error);
      throw error;
    }
  }

  /**
   * Check if organization name exists
   */
  async checkOrganizationNameExists(name) {
    try {
      const params = new URLSearchParams({ name });
      const response = await fetch(`${API_BASE_URL}/organizations/exists?${params}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      const result = await response.json();
      return result.exists;
    } catch (error) {
      console.error('Error checking organization name:', error);
      throw error;
    }
  }

  /**
   * Advanced search with multiple criteria
   */
  async advancedSearch({
    name,
    category,
    type,
    city,
    state,
    country,
    verified,
    verificationLevel,
    minEmployees,
    maxEmployees,
    minFoundedYear,
    maxFoundedYear,
    language,
    sortBy = 'organizationName',
    sortDirection = 'asc',
    page = 0,
    size = 20
  } = {}) {
    try {
      const params = new URLSearchParams();
      
      // Add search filters
      if (name) params.append('name', name);
      if (category) params.append('category', category);
      if (type) params.append('type', type);
      if (city) params.append('city', city);
      if (state) params.append('state', state);
      if (country) params.append('country', country);
      if (verified !== null && verified !== undefined) params.append('verified', verified);
      if (verificationLevel) params.append('verificationLevel', verificationLevel);
      if (minEmployees !== null && minEmployees !== undefined) params.append('minEmployees', minEmployees);
      if (maxEmployees !== null && maxEmployees !== undefined) params.append('maxEmployees', maxEmployees);
      if (minFoundedYear !== null && minFoundedYear !== undefined) params.append('minFoundedYear', minFoundedYear);
      if (maxFoundedYear !== null && maxFoundedYear !== undefined) params.append('maxFoundedYear', maxFoundedYear);
      if (language) params.append('language', language);
      
      // Add pagination and sorting
      params.append('sortBy', sortBy);
      params.append('sortDirection', sortDirection);
      params.append('page', page.toString());
      params.append('size', size.toString());
      
      const response = await fetch(`${API_BASE_URL}/organizations/advanced-search?${params}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error performing advanced search:', error);
      throw error;
    }
  }

  /**
   * Get organization categories (for filter dropdowns)
   */
  async getOrganizationCategories() {
    try {
      const response = await fetch(`${API_BASE_URL}/organizations/categories`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching organization categories:', error);
      throw error;
    }
  }

  /**
   * Get organization types (for filter dropdowns)
   */
  async getOrganizationTypes() {
    try {
      const response = await fetch(`${API_BASE_URL}/organizations/types`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching organization types:', error);
      throw error;
    }
  }

  /**
   * Get organization locations (for filter dropdowns)
   */
  async getOrganizationLocations() {
    try {
      const response = await fetch(`${API_BASE_URL}/organizations/locations`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching organization locations:', error);
      throw error;
    }
  }

  // ==========================================
  // ENHANCED METHODS FOR NEWLY CREATED ORGANIZATIONS
  // ==========================================

  /**
   * Find recently created organizations with advanced filtering
   */
  async findRecentlyCreatedOrganizations({
    days = 7,
    limit = 50,
    category = '',
    verified = null,
    sortBy = 'createdDate',
    sortDirection = 'desc'
  } = {}) {
    try {
      console.log(`🔍 Searching for organizations created in the last ${days} days with filters`);
      
      // Use the correct browse controller endpoint
      const params = new URLSearchParams({
        days: days.toString(),
        limit: limit.toString()
      });
      
      if (category) params.append('category', category);
      if (verified !== null) params.append('verified', verified.toString());
      
      const response = await fetch(`${API_BASE_URL}/organizations/recently-created?${params}`);
      
      if (response.ok) {
        const data = await response.json();
        console.log(`✅ Found ${data.length} recently created organizations`);
        return Array.isArray(data) ? data : [];
      } else {
        console.log(`❌ Recently-created endpoint failed: ${response.status}`);
      }
      
      // Fallback to general organizations endpoint
      const fallbackResponse = await fetch(`${API_BASE_URL}/organizations`);
      if (fallbackResponse.ok) {
        const fallbackData = await fallbackResponse.json();
        console.log(`✅ Using fallback organizations data`);
        return Array.isArray(fallbackData) ? fallbackData.slice(0, limit) : [];
      }
      
      return [];
      
    } catch (error) {
      console.error('Error fetching recently created organizations:', error);
      throw error;
    }
  }

  /**
   * Get recently updated organizations
   */
  async findRecentlyUpdatedOrganizations(days = 7, limit = 50) {
    try {
      console.log(`🔍 Searching for organizations updated in the last ${days} days`);
      
      const params = new URLSearchParams({
        days: days.toString(),
        limit: limit.toString()
      });
      
      const response = await fetch(`${API_BASE_URL}/organizations/recently-updated?${params}`);
      
      if (response.ok) {
        const data = await response.json();
        console.log(`✅ Found ${data.length} recently updated organizations`);
        return Array.isArray(data) ? data : [];
      }
      
      return [];
      
    } catch (error) {
      console.error('Error fetching recently updated organizations:', error);
      throw error;
    }
  }

  /**
   * Refresh organizations data with cache busting
   */
  async refreshNewOrganizations(maxAge = 1) {
    try {
      console.log(`🔄 Refreshing organizations data (max age: ${maxAge} day(s))`);
      
      // Convert days to minutes for the endpoint
      const maxAgeMinutes = maxAge * 24 * 60;
      const cacheBuster = new Date().getTime();
      
      // Try the refresh endpoint with cache busting
      const refreshResponse = await fetch(`${API_BASE_URL}/organizations/refresh?maxAgeMinutes=${maxAgeMinutes}&_t=${cacheBuster}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          'Cache-Control': 'no-cache',
          'Pragma': 'no-cache'
        }
      });
      
      if (refreshResponse.ok) {
        const data = await refreshResponse.json();
        console.log(`✅ Successfully refreshed data: ${data.length} organizations`);
        return Array.isArray(data) ? data : [];
      }
      
      // Fallback to recently created
      return await this.findRecentlyCreatedOrganizations({ days: maxAge });
      
    } catch (error) {
      console.error('Error refreshing organizations:', error);
      throw error;
    }
  }

  /**
   * Find specific organization using the enhanced find endpoint
   */
  async findSpecificOrganization(organizationName, searchRecent = true) {
    try {
      console.log(`🔍 Searching for specific organization: "${organizationName}"`);
      
      if (!organizationName || organizationName.trim() === '') {
        console.log('❌ Empty organization name provided');
        return null;
      }
      
      // Use the specific find endpoint with recent flag
      const params = new URLSearchParams({
        name: organizationName.trim(),
        recent: searchRecent.toString()
      });
      
      const findResponse = await fetch(`${API_BASE_URL}/organizations/find?${params}`);
      
      if (findResponse.ok) {
        const organization = await findResponse.json();
        console.log(`✅ Found organization using find endpoint:`, organization);
        return organization;
      } else if (findResponse.status === 404) {
        console.log(`❌ Organization "${organizationName}" not found via find endpoint`);
        return null;
      } else {
        console.log(`❌ Find endpoint failed with status: ${findResponse.status}`);
        return null;
      }
      
    } catch (error) {
      console.error('Error finding specific organization:', error);
      return null;
    }
  }

  /**
   * Advanced method to find newly created organization with multiple strategies
   */
  async findNewlyCreatedOrganization(organizationName, timeWindowMinutes = 60) {
    try {
      console.log(`🔍 Searching for newly created organization: "${organizationName}" within ${timeWindowMinutes} minutes`);
      
      if (!organizationName || organizationName.trim() === '') {
        console.log('❌ Empty organization name provided');
        return null;
      }
      
      // Strategy 1: Use the specific find endpoint with recent flag
      try {
        const organization = await this.findSpecificOrganization(organizationName, true);
        if (organization) {
          console.log(`✅ Found organization using find endpoint:`, organization);
          return organization;
        }
      } catch (error) {
        console.log(`❌ Find endpoint failed:`, error.message);
      }
      
      // Strategy 2: Search in recently created organizations
      try {
        const recentOrgs = await this.findRecentlyCreatedOrganizations({
          days: 1,
          limit: 100
        });
        
        const exactMatch = recentOrgs.find(org => 
          org.organizationName?.toLowerCase() === organizationName.toLowerCase()
        );
        
        if (exactMatch) {
          console.log(`✅ Found exact match in recent organizations:`, exactMatch);
          return exactMatch;
        }
      } catch (error) {
        console.log(`❌ Recent organizations search failed:`, error.message);
      }
      
      // Strategy 3: Use enhanced name search
      try {
        const nameResults = await this.findOrganizationsByName(organizationName);
        if (nameResults.length > 0) {
          const exactMatch = nameResults.find(org => 
            org.organizationName?.toLowerCase() === organizationName.toLowerCase()
          );
          
          if (exactMatch) {
            console.log(`✅ Found organization by name search:`, exactMatch);
            return exactMatch;
          }
          
          // Return the first result if no exact match
          console.log(`✅ Found similar organization by name search:`, nameResults[0]);
          return nameResults[0];
        }
      } catch (error) {
        console.log(`❌ Name search failed:`, error.message);
      }
      
      // Strategy 4: Refresh and try find again
      console.log(`🔄 Refreshing data and searching again...`);
      try {
        await this.refreshNewOrganizations(1);
        
        const retryOrganization = await this.findSpecificOrganization(organizationName, true);
        if (retryOrganization) {
          console.log(`✅ Found organization after refresh:`, retryOrganization);
          return retryOrganization;
        }
      } catch (error) {
        console.log(`❌ Refresh and retry failed:`, error.message);
      }
      
      console.log(`❌ Could not find newly created organization: "${organizationName}"`);
      return null;
      
    } catch (error) {
      console.error('Error finding newly created organization:', error);
      throw error;
    }
  }

  /**
   * MAIN METHOD - Find organization immediately after creation with intelligent retry logic
   * This is the primary method to use after creating an organization
   */
  async findJustCreatedOrganization(organizationName, maxRetries = 8, delayMs = 1500) {
    console.log(`🚀 Starting intelligent search for just-created organization: "${organizationName}"`);
    console.log(`📋 Search strategy: ${maxRetries} attempts with ${delayMs}ms base delay`);
    
    if (!organizationName || organizationName.trim() === '') {
      console.log('❌ Invalid organization name provided');
      return null;
    }
    
    for (let attempt = 1; attempt <= maxRetries; attempt++) {
      console.log(`📍 Attempt ${attempt}/${maxRetries}: Searching for "${organizationName}"`);
      
      try {
        // Use the comprehensive search method
        const organization = await this.findNewlyCreatedOrganization(organizationName, 60);
        
        if (organization) {
          console.log(`🎉 SUCCESS! Found organization on attempt ${attempt}:`);
          console.log(`   Name: ${organization.organizationName}`);
          console.log(`   ID: ${organization.id}`);
          console.log(`   Created: ${organization.createdAt}`);
          return organization;
        }
        
        // Progressive delay with exponential backoff
        if (attempt < maxRetries) {
          const waitTime = Math.min(delayMs * Math.pow(1.3, attempt - 1), 8000); // Cap at 8 seconds
          console.log(`⏳ No results found. Waiting ${Math.round(waitTime)}ms before attempt ${attempt + 1}...`);
          await new Promise(resolve => setTimeout(resolve, waitTime));
        }
        
      } catch (error) {
        console.log(`❌ Attempt ${attempt} failed with error:`, error.message);
        
        // On error, wait a bit longer before retry
        if (attempt < maxRetries) {
          const errorWaitTime = delayMs * attempt;
          console.log(`⏳ Waiting ${errorWaitTime}ms before retry due to error...`);
          await new Promise(resolve => setTimeout(resolve, errorWaitTime));
        }
      }
    }
    
    console.log(`💔 SEARCH FAILED: Could not find organization "${organizationName}" after ${maxRetries} attempts`);
    console.log(`💡 SUGGESTIONS:`);
    console.log(`   • The organization was likely created successfully`);
    console.log(`   • It may take a few minutes to appear in search results`);
    console.log(`   • Try refreshing the page or searching manually`);
    console.log(`   • Check the organization list page to verify creation`);
    
    return null;
  }

  /**
   * Quick retry search for immediate use (simpler alternative)
   */
  async quickSearchRetry(organizationName, maxAttempts = 5, delaySeconds = 2) {
    console.log(`🔄 Quick retry search for: "${organizationName}"`);
    
    for (let i = 0; i < maxAttempts; i++) {
      try {
        console.log(`🔍 Quick attempt ${i + 1}/${maxAttempts}: Searching for "${organizationName}"`);
        
        const results = await this.findOrganizationsByName(organizationName);
        if (results.length > 0) {
          const exactMatch = results.find(org => 
            org.organizationName?.toLowerCase() === organizationName.toLowerCase()
          );
          
          if (exactMatch) {
            console.log(`✅ Quick search success on attempt ${i + 1}:`, exactMatch);
            return exactMatch;
          } else {
            console.log(`✅ Found similar match on attempt ${i + 1}:`, results[0]);
            return results[0];
          }
        }
        
        // Wait before next attempt
        if (i < maxAttempts - 1) {
          console.log(`⏳ Waiting ${delaySeconds} seconds before next attempt...`);
          await new Promise(resolve => setTimeout(resolve, delaySeconds * 1000));
        }
        
      } catch (error) {
        console.log(`❌ Quick attempt ${i + 1} failed:`, error.message);
      }
    }
    
    console.log(`❌ Quick search failed after ${maxAttempts} attempts`);
    return null;
  }

  /**
   * Utility method to validate search results
   */
  validateSearchResult(result, expectedName) {
    if (!result || !result.organizationName) {
      return false;
    }
    
    const resultName = result.organizationName.toLowerCase().trim();
    const searchName = expectedName.toLowerCase().trim();
    
    // Exact match
    if (resultName === searchName) {
      return true;
    }
    
    // Close match (handles minor differences)
    const similarity = this.calculateStringSimilarity(resultName, searchName);
    return similarity > 0.8; // 80% similarity threshold
  }

  /**
   * Calculate string similarity (simple Levenshtein-based)
   */
  calculateStringSimilarity(str1, str2) {
    const maxLength = Math.max(str1.length, str2.length);
    if (maxLength === 0) return 1;
    
    const distance = this.levenshteinDistance(str1, str2);
    return (maxLength - distance) / maxLength;
  }

  /**
   * Calculate Levenshtein distance between two strings
   */
  levenshteinDistance(str1, str2) {
    const matrix = [];
    
    for (let i = 0; i <= str2.length; i++) {
      matrix[i] = [i];
    }
    
    for (let j = 0; j <= str1.length; j++) {
      matrix[0][j] = j;
    }
    
    for (let i = 1; i <= str2.length; i++) {
      for (let j = 1; j <= str1.length; j++) {
        if (str2.charAt(i - 1) === str1.charAt(j - 1)) {
          matrix[i][j] = matrix[i - 1][j - 1];
        } else {
          matrix[i][j] = Math.min(
            matrix[i - 1][j - 1] + 1,
            matrix[i][j - 1] + 1,
            matrix[i - 1][j] + 1
          );
        }
      }
    }
    
    return matrix[str2.length][str1.length];
  }

  /**
   * Debug method to test all search strategies
   */
  async debugSearchStrategies(organizationName) {
    console.log(`🔬 DEBUG: Testing all search strategies for "${organizationName}"`);
    
    const strategies = [
      {
        name: 'Find Specific Organization',
        method: () => this.findSpecificOrganization(organizationName, true)
      },
      {
        name: 'Recently Created Organizations',
        method: () => this.findRecentlyCreatedOrganizations({ days: 1, limit: 100 })
      },
      {
        name: 'Search by Name',
        method: () => this.findOrganizationsByName(organizationName)
      },
      {
        name: 'Refresh and Retry',
        method: async () => {
          await this.refreshNewOrganizations(1);
          return this.findSpecificOrganization(organizationName, true);
        }
      }
    ];
    
    for (const strategy of strategies) {
      try {
        console.log(`🧪 Testing: ${strategy.name}`);
        const result = await strategy.method();
        console.log(`   Result: ${result ? 'SUCCESS' : 'NO RESULTS'}`);
        if (result) {
          if (Array.isArray(result)) {
            console.log(`   Found ${result.length} results`);
          } else {
            console.log(`   Found: ${result.organizationName}`);
          }
        }
      } catch (error) {
        console.log(`   ERROR: ${error.message}`);
      }
    }
  }
}

// Export singleton instance
const findOrganizationService = new FindOrganizationService();
export default findOrganizationService;