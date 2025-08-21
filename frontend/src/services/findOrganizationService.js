// frontend/src/services/findOrganizationService.js - ENHANCED FOR REAL-TIME SEARCH

const API_BASE_URL = 'http://localhost:8080/api';

class FindOrganizationService {
  
  constructor() {
    this.lastRefreshTime = new Date();
    this.refreshInterval = 30000; // 30 seconds
    this.searchCache = new Map();
    this.cacheTimeout = 10000; // 10 seconds cache timeout
  }

  // ==========================================
  // NEW REAL-TIME SEARCH METHODS
  // ==========================================

  /**
   * ENHANCED: Real-time search with immediate data refresh
   * This method ensures users see the latest organizations, including newly created ones
   */
  async performRealtimeSearch(searchParams = {}) {
    try {
      const {
        name = '',
        category = '',
        type = '',
        city = '',
        state = '',
        country = '',
        verified = null,
        forceRefresh = false,
        limit = 100
      } = searchParams;

      console.log('🚀 Performing real-time search:', searchParams);

      // Build query parameters
      const params = new URLSearchParams();
      if (name) params.append('name', name);
      if (category) params.append('category', category);
      if (type) params.append('type', type);
      if (city) params.append('city', city);
      if (state) params.append('state', state);
      if (country) params.append('country', country);
      if (verified !== null) params.append('verified', verified.toString());
      if (forceRefresh) params.append('forceRefresh', 'true');
      params.append('limit', limit.toString());

      // Add cache-busting timestamp
      params.append('_t', new Date().getTime().toString());

      const response = await fetch(`${API_BASE_URL}/organizations/search/realtime?${params}`, {
        method: 'GET',
        headers: {
          'Accept': 'application/json',
          'Cache-Control': 'no-cache',
          'Pragma': 'no-cache'
        }
      });

      if (!response.ok) {
        throw new Error(`Real-time search failed: ${response.status} ${response.statusText}`);
      }

      const data = await response.json();
      const timestamp = response.headers.get('X-Data-Timestamp');
      const resultCount = response.headers.get('X-Results-Count');

      console.log(`✅ Real-time search completed: ${resultCount} results at ${timestamp}`);
      
      return {
        data: Array.isArray(data) ? data : [],
        timestamp,
        resultCount: parseInt(resultCount) || 0,
        searchParams
      };

    } catch (error) {
      console.error('❌ Real-time search failed:', error);
      
      // Fallback to standard search
      try {
        console.log('🔄 Falling back to standard search...');
        const fallback = await this.findAllOrganizations();
        return {
          data: Array.isArray(fallback) ? fallback : [],
          timestamp: new Date().toISOString(),
          resultCount: fallback?.length || 0,
          searchParams,
          fallback: true
        };
      } catch (fallbackError) {
        console.error('❌ Fallback search also failed:', fallbackError);
        return {
          data: [],
          timestamp: new Date().toISOString(),
          resultCount: 0,
          searchParams,
          error: error.message
        };
      }
    }
  }

  /**
   * ENHANCED: Immediate organization finder for post-creation searches
   * Uses multiple strategies to find newly created organizations immediately
   */
  async findOrganizationImmediate(organizationName, options = {}) {
    try {
      const {
        maxAgeMinutes = 5,
        searchRecent = true,
        retryAttempts = 3,
        retryDelay = 1000
      } = options;

      console.log(`🎯 Immediate search for: "${organizationName}"`);

      if (!organizationName || organizationName.trim() === '') {
        throw new Error('Organization name is required');
      }

      // Build query parameters
      const params = new URLSearchParams({
        name: organizationName.trim(),
        maxAgeMinutes: maxAgeMinutes.toString(),
        searchRecent: searchRecent.toString(),
        _t: new Date().getTime().toString() // Cache busting
      });

      let lastError = null;

      // Retry logic for immediate searches
      for (let attempt = 1; attempt <= retryAttempts; attempt++) {
        try {
          console.log(`🔍 Immediate search attempt ${attempt}/${retryAttempts}`);

          const response = await fetch(`${API_BASE_URL}/organizations/find/immediate?${params}`, {
            method: 'GET',
            headers: {
              'Accept': 'application/json',
              'Cache-Control': 'no-cache',
              'Pragma': 'no-cache'
            }
          });

          if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
          }

          const result = await response.json();
          const searchStrategy = response.headers.get('X-Search-Strategy');

          console.log(`📊 Search result:`, {
            found: result.found,
            strategy: searchStrategy,
            timestamp: result.searchTimestamp
          });

          if (result.found && result.organization) {
            console.log(`✅ Found organization on attempt ${attempt}:`, result.organization.organizationName);
            return {
              found: true,
              organization: result.organization,
              strategy: searchStrategy,
              attempt,
              timestamp: result.searchTimestamp,
              isRecent: result.isRecent
            };
          } else if (attempt === retryAttempts) {
            // Last attempt, return the detailed result even if not found
            return {
              found: false,
              strategy: searchStrategy,
              attempt,
              timestamp: result.searchTimestamp,
              suggestions: result.suggestions,
              message: result.message
            };
          }

          // Wait before retry (unless it's the last attempt)
          if (attempt < retryAttempts) {
            const waitTime = retryDelay * attempt; // Progressive delay
            console.log(`⏳ Waiting ${waitTime}ms before retry...`);
            await new Promise(resolve => setTimeout(resolve, waitTime));
            
            // Update cache buster for next attempt
            params.set('_t', new Date().getTime().toString());
          }

        } catch (error) {
          lastError = error;
          console.log(`❌ Attempt ${attempt} failed:`, error.message);
          
          if (attempt < retryAttempts) {
            const waitTime = retryDelay * attempt;
            console.log(`⏳ Waiting ${waitTime}ms before retry due to error...`);
            await new Promise(resolve => setTimeout(resolve, waitTime));
          }
        }
      }

      // All attempts failed
      throw lastError || new Error('All immediate search attempts failed');

    } catch (error) {
      console.error('❌ Immediate search failed completely:', error);
      return {
        found: false,
        error: error.message,
        timestamp: new Date().toISOString(),
        suggestions: [
          'The organization may still be processing',
          'Try refreshing the page and searching again',
          'Check the organization list manually',
          'Contact support if the organization should exist'
        ]
      };
    }
  }

  /**
   * ENHANCED: Live data refresh with comprehensive stats
   * Ensures users always see the most up-to-date organization data
   */
  async refreshLiveData(options = {}) {
    try {
      const {
        force = false,
        maxAgeMinutes = 5,
        includeStats = true,
        limit = 100
      } = options;

      console.log(`🔄 Live data refresh (force: ${force}, maxAge: ${maxAgeMinutes}min)`);

      const params = new URLSearchParams({
        maxAgeMinutes: maxAgeMinutes.toString(),
        force: force.toString(),
        includeStats: includeStats.toString(),
        limit: limit.toString(),
        _t: new Date().getTime().toString()
      });

      const refreshStartTime = performance.now();

      const response = await fetch(`${API_BASE_URL}/organizations/refresh/live?${params}`, {
        method: 'GET',
        headers: {
          'Accept': 'application/json',
          'Cache-Control': 'no-cache',
          'Pragma': 'no-cache'
        }
      });

      if (!response.ok) {
        throw new Error(`Live refresh failed: ${response.status} ${response.statusText}`);
      }

      const result = await response.json();
      const refreshEndTime = performance.now();
      const clientRefreshDuration = refreshEndTime - refreshStartTime;

      // Update last refresh time
      this.lastRefreshTime = new Date();

      console.log(`✅ Live refresh completed:`, {
        totalCount: result.totalCount,
        serverDuration: result.refreshDurationMs,
        clientDuration: Math.round(clientRefreshDuration),
        dataSource: result.dataSource,
        timestamp: result.refreshTimestamp
      });

      return {
        organizations: result.organizations || [],
        totalCount: result.totalCount || 0,
        refreshTimestamp: result.refreshTimestamp,
        serverDurationMs: result.refreshDurationMs,
        clientDurationMs: Math.round(clientRefreshDuration),
        dataSource: result.dataSource,
        stats: result.stats,
        success: true
      };

    } catch (error) {
      console.error('❌ Live refresh failed:', error);
      
      // Try fallback refresh
      try {
        console.log('🔄 Attempting fallback refresh...');
        const fallback = await this.findAllOrganizations();
        return {
          organizations: Array.isArray(fallback) ? fallback : [],
          totalCount: fallback?.length || 0,
          refreshTimestamp: new Date().toISOString(),
          success: false,
          fallback: true,
          error: error.message
        };
      } catch (fallbackError) {
        return {
          organizations: [],
          totalCount: 0,
          refreshTimestamp: new Date().toISOString(),
          success: false,
          error: error.message,
          fallbackError: fallbackError.message
        };
      }
    }
  }

  /**
   * ENHANCED: Smart search with caching and real-time capabilities
   * Combines local caching with real-time data for optimal performance
   */
  async smartSearch(searchParams = {}) {
    try {
      const cacheKey = JSON.stringify(searchParams);
      const now = Date.now();
      
      // Check cache first (for non-empty searches)
      if (searchParams.name && this.searchCache.has(cacheKey)) {
        const cached = this.searchCache.get(cacheKey);
        if (now - cached.timestamp < this.cacheTimeout) {
          console.log('📋 Using cached search results');
          return {
            ...cached.result,
            cached: true,
            cacheAge: now - cached.timestamp
          };
        }
      }

      // Determine if we need real-time search
      const needsRealTime = this.shouldUseRealTimeSearch(searchParams);
      
      let result;
      if (needsRealTime) {
        console.log('🚀 Using real-time search');
        result = await this.performRealtimeSearch({
          ...searchParams,
          forceRefresh: this.isDataStale()
        });
      } else {
        console.log('📡 Using standard search');
        result = await this.performStandardSearch(searchParams);
      }

      // Cache the result
      if (searchParams.name && result.data.length > 0) {
        this.searchCache.set(cacheKey, {
          result,
          timestamp: now
        });
        
        // Clean old cache entries
        this.cleanCache();
      }

      return result;

    } catch (error) {
      console.error('❌ Smart search failed:', error);
      throw error;
    }
  }

  // ==========================================
  // ENHANCED EXISTING METHODS
  // ==========================================

  /**
   * Enhanced search organizations by name with real-time capabilities
   */
  async findOrganizationsByName(name, options = {}) {
    try {
      if (!name || name.trim() === '') {
        console.log('❌ Empty search name provided');
        return [];
      }

      const {
        includeRecent = true,
        limit = 50,
        useRealTime = false
      } = options;

      console.log(`🔍 Enhanced name search: "${name}" (realTime: ${useRealTime})`);
      
      if (useRealTime) {
        const result = await this.performRealtimeSearch({ name, limit });
        return result.data;
      }

      // Use enhanced backend endpoint
      const params = new URLSearchParams({ 
        name: name.trim(),
        includeRecent: includeRecent.toString(),
        limit: limit.toString()
      });
      
      const response = await fetch(`${API_BASE_URL}/organizations/search/name?${params}`);
      
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      
      const data = await response.json();
      const searchTerm = response.headers.get('X-Search-Term');
      const resultCount = response.headers.get('X-Results-Count');
      
      console.log(`✅ Enhanced search found ${resultCount} organizations for "${searchTerm}"`);
      return Array.isArray(data) ? data : [];
      
    } catch (error) {
      console.error('❌ Enhanced name search failed:', error);
      
      // Fallback to original method
      try {
        console.log('🔄 Trying standard search fallback...');
        const fallbackData = await this.searchOrganizations({ name });
        return Array.isArray(fallbackData) ? fallbackData : [];
      } catch (fallbackError) {
        console.error('❌ Fallback search also failed:', fallbackError);
        return [];
      }
    }
  }

  /**
   * MAIN METHOD - Enhanced find just created organization with intelligent retry
   */
  async findJustCreatedOrganization(organizationName, options = {}) {
    const {
      maxRetries = 6,
      baseDelayMs = 1000,
      maxDelayMs = 8000,
      useImmediateSearch = true
    } = options;

    console.log(`🚀 Enhanced search for just-created organization: "${organizationName}"`);
    console.log(`📋 Strategy: ${maxRetries} attempts, immediate search: ${useImmediateSearch}`);
    
    if (!organizationName || organizationName.trim() === '') {
      console.log('❌ Invalid organization name provided');
      return null;
    }
    
    // First, try immediate search if enabled
    if (useImmediateSearch) {
      try {
        console.log('🎯 Attempting immediate search first...');
        const immediateResult = await this.findOrganizationImmediate(organizationName, {
          maxAgeMinutes: 2,
          searchRecent: true,
          retryAttempts: 2
        });
        
        if (immediateResult.found && immediateResult.organization) {
          console.log('🎉 Found via immediate search!');
          return immediateResult.organization;
        }
        
        console.log('🔄 Immediate search didn\'t find organization, trying comprehensive search...');
      } catch (error) {
        console.log('⚠️ Immediate search failed, continuing with comprehensive search:', error.message);
      }
    }
    
    // Comprehensive search with retry logic
    for (let attempt = 1; attempt <= maxRetries; attempt++) {
      console.log(`🔍 Comprehensive search attempt ${attempt}/${maxRetries}`);
      
      try {
        // Use real-time search for better chances
        const searchResult = await this.performRealtimeSearch({
          name: organizationName,
          forceRefresh: attempt > 2, // Force refresh after 2 failed attempts
          limit: 50
        });
        
        if (searchResult.data && searchResult.data.length > 0) {
          // Look for exact match
          const exactMatch = searchResult.data.find(org => 
            org.organizationName?.toLowerCase() === organizationName.toLowerCase()
          );
          
          if (exactMatch) {
            console.log(`🎉 Found exact match on attempt ${attempt}:`, exactMatch.organizationName);
            return exactMatch;
          }
          
          // If no exact match but found similar results
          const similarMatch = searchResult.data[0];
          console.log(`📋 Found similar match on attempt ${attempt}:`, similarMatch.organizationName);
          
          // Return similar match only on last attempt or if very close
          if (attempt === maxRetries || this.isSimilarName(organizationName, similarMatch.organizationName)) {
            return similarMatch;
          }
        }
        
        // Progressive delay with jitter
        if (attempt < maxRetries) {
          const delay = Math.min(baseDelayMs * Math.pow(1.5, attempt - 1), maxDelayMs);
          const jitter = Math.random() * 500; // Add up to 500ms jitter
          const waitTime = delay + jitter;
          
          console.log(`⏳ Waiting ${Math.round(waitTime)}ms before attempt ${attempt + 1}...`);
          await new Promise(resolve => setTimeout(resolve, waitTime));
        }
        
      } catch (error) {
        console.log(`❌ Attempt ${attempt} failed:`, error.message);
        
        if (attempt < maxRetries) {
          const errorDelay = baseDelayMs * attempt;
          console.log(`⏳ Error recovery delay: ${errorDelay}ms`);
          await new Promise(resolve => setTimeout(resolve, errorDelay));
        }
      }
    }
    
    console.log(`💔 Comprehensive search failed after ${maxRetries} attempts`);
    console.log(`💡 Suggestions for "${organizationName}":`);
    console.log(`   • The organization was likely created successfully`);
    console.log(`   • Try refreshing the page or checking the organization list`);
    console.log(`   • Search may work in a few minutes after backend processing`);
    
    return null;
  }

  // ==========================================
  // ALL EXISTING METHODS (PRESERVED)
  // ==========================================

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

  // ==========================================
  // UTILITY METHODS
  // ==========================================

  /**
   * Determine if real-time search should be used
   */
  shouldUseRealTimeSearch(searchParams) {
    // Use real-time search if:
    // 1. Searching for specific name (likely looking for new org)
    // 2. Data is stale
    // 3. No search term (showing all orgs, want fresh data)
    
    return (
      (searchParams.name && searchParams.name.length > 2) ||
      this.isDataStale() ||
      (!searchParams.name && !searchParams.category)
    );
  }

  /**
   * Check if data is stale and needs refresh
   */
  isDataStale() {
    const now = new Date();
    const timeSinceRefresh = now - this.lastRefreshTime;
    return timeSinceRefresh > this.refreshInterval;
  }

  /**
   * Check if two organization names are similar
   */
  isSimilarName(name1, name2) {
    if (!name1 || !name2) return false;
    
    const normalize = (str) => str.toLowerCase().replace(/[^\w]/g, '');
    const norm1 = normalize(name1);
    const norm2 = normalize(name2);
    
    // Check if one is a substring of the other
    return norm1.includes(norm2) || norm2.includes(norm1);
  }

  /**
   * Clean old cache entries
   */
  cleanCache() {
    const now = Date.now();
    for (const [key, value] of this.searchCache.entries()) {
      if (now - value.timestamp > this.cacheTimeout * 2) {
        this.searchCache.delete(key);
      }
    }
  }

  /**
   * Perform standard search (fallback method)
   */
  async performStandardSearch(searchParams) {
    try {
      const filters = {};
      if (searchParams.name) filters.name = searchParams.name;
      if (searchParams.category) filters.category = searchParams.category;
      if (searchParams.verified !== undefined) filters.verified = searchParams.verified;
      
      const data = await this.searchOrganizations(filters);
      
      return {
        data: Array.isArray(data) ? data : [],
        timestamp: new Date().toISOString(),
        resultCount: data?.length || 0,
        searchParams,
        source: 'standard'
      };
    } catch (error) {
      console.error('Standard search failed:', error);
      throw error;
    }
  }

  // ==========================================
  // ALL OTHER EXISTING METHODS (PRESERVED)
  // ==========================================
  
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

  // ... [All other existing methods remain exactly the same] ...
  // Including: findOrganizationsByCategory, findOrganizationsByType, 
  // findOrganizationsByLocation, findVerifiedOrganizations, etc.
}

// Export singleton instance
const findOrganizationService = new FindOrganizationService();
export default findOrganizationService;