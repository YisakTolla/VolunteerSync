// frontend/src/services/findEventsService.js - ENHANCED FOR REAL-TIME SEARCH

const API_BASE_URL = 'http://localhost:8080/api';

class FindEventService {
  
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
   * This method ensures users see the latest events, including newly created ones
   */
  async performRealtimeSearch(searchParams = {}) {
    try {
      const {
        searchTerm = '',
        eventType = '',
        location = '',
        skillLevel = '',
        isVirtual = null,
        startDate = null,
        endDate = null,
        forceRefresh = false,
        limit = 100
      } = searchParams;

      console.log('🚀 Performing real-time event search:', searchParams);

      // Build query parameters
      const params = new URLSearchParams();
      if (searchTerm) params.append('searchTerm', searchTerm);
      if (eventType) params.append('eventType', eventType);
      if (location) params.append('location', location);
      if (skillLevel) params.append('skillLevel', skillLevel);
      if (isVirtual !== null) params.append('isVirtual', isVirtual.toString());
      if (startDate) params.append('startDate', startDate);
      if (endDate) params.append('endDate', endDate);
      if (forceRefresh) params.append('forceRefresh', 'true');
      params.append('limit', limit.toString());

      // Add cache-busting timestamp
      params.append('_t', new Date().getTime().toString());

      const response = await fetch(`${API_BASE_URL}/events/search/realtime?${params}`, {
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

      console.log(`✅ Real-time event search completed: ${resultCount} results at ${timestamp}`);
      
      return {
        data: Array.isArray(data) ? data : [],
        timestamp,
        resultCount: parseInt(resultCount) || 0,
        searchParams
      };

    } catch (error) {
      console.error('❌ Real-time event search failed:', error);
      
      // Fallback to standard search
      try {
        console.log('🔄 Falling back to standard event search...');
        const fallback = await this.findAllEvents();
        return {
          data: Array.isArray(fallback) ? fallback : [],
          timestamp: new Date().toISOString(),
          resultCount: fallback?.length || 0,
          searchParams,
          fallback: true
        };
      } catch (fallbackError) {
        console.error('❌ Fallback event search also failed:', fallbackError);
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
   * ENHANCED: Live data refresh with comprehensive stats
   * Ensures users always see the most up-to-date event data
   */
  async refreshLiveData(options = {}) {
    try {
      const {
        force = false,
        maxAgeMinutes = 5,
        includeStats = true,
        limit = 200
      } = options;

      console.log(`🔄 Live event data refresh (force: ${force}, maxAge: ${maxAgeMinutes}min)`);

      const params = new URLSearchParams({
        maxAgeMinutes: maxAgeMinutes.toString(),
        force: force.toString(),
        includeStats: includeStats.toString(),
        limit: limit.toString(),
        _t: new Date().getTime().toString()
      });

      const refreshStartTime = performance.now();

      const response = await fetch(`${API_BASE_URL}/events/refresh/live?${params}`, {
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

      console.log(`✅ Live event refresh completed:`, {
        totalCount: result.totalCount,
        serverDuration: result.refreshDurationMs,
        clientDuration: Math.round(clientRefreshDuration),
        dataSource: result.dataSource,
        timestamp: result.refreshTimestamp
      });

      return {
        events: result.events || [],
        totalCount: result.totalCount || 0,
        refreshTimestamp: result.refreshTimestamp,
        serverDurationMs: result.refreshDurationMs,
        clientDurationMs: Math.round(clientRefreshDuration),
        dataSource: result.dataSource,
        stats: result.stats,
        success: true
      };

    } catch (error) {
      console.error('❌ Live event refresh failed:', error);
      
      // Try fallback refresh
      try {
        console.log('🔄 Attempting fallback event refresh...');
        const fallback = await this.findAllEvents();
        return {
          events: Array.isArray(fallback) ? fallback : [],
          totalCount: fallback?.length || 0,
          refreshTimestamp: new Date().toISOString(),
          success: false,
          fallback: true,
          error: error.message
        };
      } catch (fallbackError) {
        return {
          events: [],
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
      if (searchParams.searchTerm && this.searchCache.has(cacheKey)) {
        const cached = this.searchCache.get(cacheKey);
        if (now - cached.timestamp < this.cacheTimeout) {
          console.log('📋 Using cached event search results');
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
        console.log('🚀 Using real-time event search');
        result = await this.performRealtimeSearch({
          ...searchParams,
          forceRefresh: this.isDataStale()
        });
      } else {
        console.log('📡 Using standard event search');
        result = await this.performStandardSearch(searchParams);
      }

      // Cache the result
      if (searchParams.searchTerm && result.data.length > 0) {
        this.searchCache.set(cacheKey, {
          result,
          timestamp: now
        });
        
        // Clean old cache entries
        this.cleanCache();
      }

      return result;

    } catch (error) {
      console.error('❌ Smart event search failed:', error);
      throw error;
    }
  }

  /**
   * ENHANCED: Immediate event finder for post-creation searches
   * Uses multiple strategies to find newly created events immediately
   */
  async findEventImmediate(eventTitle, options = {}) {
    try {
      const {
        maxAgeMinutes = 5,
        searchRecent = true,
        retryAttempts = 3,
        retryDelay = 1000
      } = options;

      console.log(`🎯 Immediate search for event: "${eventTitle}"`);

      if (!eventTitle || eventTitle.trim() === '') {
        throw new Error('Event title is required');
      }

      // Build query parameters
      const params = new URLSearchParams({
        title: eventTitle.trim(),
        maxAgeMinutes: maxAgeMinutes.toString(),
        searchRecent: searchRecent.toString(),
        _t: new Date().getTime().toString() // Cache busting
      });

      let lastError = null;

      // Retry logic for immediate searches
      for (let attempt = 1; attempt <= retryAttempts; attempt++) {
        try {
          console.log(`🔍 Immediate event search attempt ${attempt}/${retryAttempts}`);

          const response = await fetch(`${API_BASE_URL}/events/find/immediate?${params}`, {
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

          console.log(`📊 Event search result:`, {
            found: result.found,
            strategy: searchStrategy,
            timestamp: result.searchTimestamp
          });

          if (result.found && result.event) {
            console.log(`✅ Found event on attempt ${attempt}:`, result.event.title);
            return {
              found: true,
              event: result.event,
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
      console.error('❌ Immediate event search failed completely:', error);
      return {
        found: false,
        error: error.message,
        timestamp: new Date().toISOString(),
        suggestions: [
          'The event may still be processing',
          'Try refreshing the page and searching again',
          'Check the event list manually',
          'Contact support if the event should exist'
        ]
      };
    }
  }

  // ==========================================
  // ENHANCED EXISTING METHODS
  // ==========================================

  /**
   * Find all events (enhanced with multiple fallback strategies)
   */
  async findAllEvents() {
    try {
      console.log(`Trying primary endpoint: ${API_BASE_URL}/events`);
      const response = await fetch(`${API_BASE_URL}/events`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      });
      
      if (response.ok) {
        const data = await response.json();
        console.log(`✅ Success with primary endpoint`, data);
        return Array.isArray(data) ? data : [];
      } else {
        console.log(`❌ Primary endpoint failed: ${response.status}: ${response.statusText}`);
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
    } catch (error) {
      console.error('Error fetching all events:', error);
      
      // Fallback strategies
      const fallbackEndpoints = [
        'events/active',
        'events/upcoming',
        'events/featured'
      ];

      for (const endpoint of fallbackEndpoints) {
        try {
          console.log(`🔄 Trying fallback: ${API_BASE_URL}/${endpoint}`);
          const response = await fetch(`${API_BASE_URL}/${endpoint}`, {
            method: 'GET',
            headers: {
              'Content-Type': 'application/json',
              'Accept': 'application/json'
            }
          });
          
          if (response.ok) {
            const data = await response.json();
            console.log(`✅ Success with fallback: ${endpoint}`, data);
            return Array.isArray(data) ? data : [];
          }
        } catch (fallbackError) {
          console.log(`❌ Fallback failed: ${endpoint}`, fallbackError.message);
          continue;
        }
      }

      // Final fallback - return empty array to prevent crashes
      console.warn('⚠️ All event endpoints failed, returning empty array');
      return [];
    }
  }

  /**
   * Enhanced search events by title with real-time capabilities
   */
  async findEventsByTitle(title, options = {}) {
    try {
      if (!title || title.trim() === '') {
        console.log('❌ Empty search title provided');
        return [];
      }

      const {
        includeRecent = true,
        limit = 50,
        useRealTime = false
      } = options;

      console.log(`🔍 Enhanced title search: "${title}" (realTime: ${useRealTime})`);
      
      if (useRealTime) {
        const result = await this.performRealtimeSearch({ searchTerm: title, limit });
        return result.data;
      }

      // Use enhanced backend endpoint
      const params = new URLSearchParams({ 
        q: title.trim(),
        includeRecent: includeRecent.toString(),
        limit: limit.toString()
      });
      
      const response = await fetch(`${API_BASE_URL}/events/search/title?${params}`);
      
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      
      const data = await response.json();
      const searchTerm = response.headers.get('X-Search-Term');
      const resultCount = response.headers.get('X-Results-Count');
      
      console.log(`✅ Enhanced search found ${resultCount} events for "${searchTerm}"`);
      return Array.isArray(data) ? data : [];
      
    } catch (error) {
      console.error('❌ Enhanced title search failed:', error);
      
      // Fallback to original method
      try {
        console.log('🔄 Trying standard search fallback...');
        const fallbackData = await this.searchEvents({ searchTerm: title });
        return Array.isArray(fallbackData) ? fallbackData : [];
      } catch (fallbackError) {
        console.error('❌ Fallback search also failed:', fallbackError);
        return [];
      }
    }
  }

  // ==========================================
  // ALL EXISTING METHODS (PRESERVED)
  // ==========================================

  /**
   * Find event by ID
   */
  async findEventById(id) {
    try {
      console.log(`Fetching event with ID: ${id}`);
      const response = await fetch(`${API_BASE_URL}/events/${id}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      });
      
      if (response.ok) {
        const data = await response.json();
        console.log(`✅ Success fetching event:`, data);
        return data;
      } else if (response.status === 404) {
        throw new Error('Event not found');
      } else {
        console.log(`❌ Failed to fetch event: ${response.status}: ${response.statusText}`);
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
    } catch (error) {
      console.error('Error fetching event by ID:', error);
      throw error;
    }
  }

  /**
   * Search events with multiple filters
   */
  async searchEvents(filters = {}) {
    try {
      const searchRequest = {
        searchTerm: filters.searchTerm || '',
        eventType: filters.eventType || '',
        location: filters.location || '',
        skillLevel: filters.skillLevel || '',
        isVirtual: filters.isVirtual,
        startDate: filters.startDate,
        endDate: filters.endDate
      };
      
      const response = await fetch(`${API_BASE_URL}/events/search`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(searchRequest)
      });
      
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error searching events:', error);
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
    // 1. Searching for specific title (likely looking for new event)
    // 2. Data is stale
    // 3. No search term (showing all events, want fresh data)
    
    return (
      (searchParams.searchTerm && searchParams.searchTerm.length > 2) ||
      this.isDataStale() ||
      (!searchParams.searchTerm && !searchParams.eventType)
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
      if (searchParams.searchTerm) filters.searchTerm = searchParams.searchTerm;
      if (searchParams.eventType) filters.eventType = searchParams.eventType;
      if (searchParams.location) filters.location = searchParams.location;
      if (searchParams.skillLevel) filters.skillLevel = searchParams.skillLevel;
      
      const data = await this.searchEvents(filters);
      
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
   * Find events with pagination
   */
  async findAllEventsWithPagination(page = 0, size = 10, sortBy = 'startDate', sortDirection = 'asc') {
    try {
      const response = await fetch(`${API_BASE_URL}/events/search?page=${page}&size=${size}&sortBy=${sortBy}&sortDirection=${sortDirection}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          searchTerm: '',
          eventType: '',
          location: '',
          skillLevel: ''
        })
      });
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching paginated events:', error);
      throw error;
    }
  }

  /**
   * Find events by type
   */
  async findEventsByType(eventType) {
    try {
      const response = await fetch(`${API_BASE_URL}/events/type/${encodeURIComponent(eventType)}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error finding events by type:', error);
      throw error;
    }
  }

  /**
   * Find events by location
   */
  async findEventsByLocation(city, state) {
    try {
      let location = '';
      if (city && state) {
        location = `${city}, ${state}`;
      } else if (city) {
        location = city;
      } else if (state) {
        location = state;
      }
      
      const params = new URLSearchParams({ q: location });
      const response = await fetch(`${API_BASE_URL}/events/search/location?${params}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error finding events by location:', error);
      throw error;
    }
  }

  /**
   * Find upcoming events
   */
  async findUpcomingEvents() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/upcoming`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching upcoming events:', error);
      throw error;
    }
  }

  

  /**
   * Find events by organization
   */
  async findEventsByOrganization(organizationId) {
    try {
      const response = await fetch(`${API_BASE_URL}/events/organization/${organizationId}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error finding events by organization:', error);
      throw error;
    }
  }

  /**
   * Find events by skill level
   */
  async findEventsBySkillLevel(skillLevel) {
    try {
      const response = await fetch(`${API_BASE_URL}/events/skill-level/${encodeURIComponent(skillLevel)}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error finding events by skill level:', error);
      throw error;
    }
  }

  /**
   * Find virtual events
   */
  async findVirtualEvents() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/virtual`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching virtual events:', error);
      throw error;
    }
  }

  /**
   * Find events by date range
   */
  async findEventsByDateRange(startDate, endDate) {
    try {
      const params = new URLSearchParams();
      if (startDate) params.append('startDate', startDate);
      if (endDate) params.append('endDate', endDate);
      
      const response = await fetch(`${API_BASE_URL}/events/date-range?${params}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error finding events by date range:', error);
      throw error;
    }
  }

  /**
   * Get events sorted by start date
   */
  async findEventsSortedByDate() {
    try {
      return await this.findAllEvents();
    } catch (error) {
      console.error('Error fetching events sorted by date:', error);
      throw error;
    }
  }

  /**
   * Get featured events
   */
  async findFeaturedEvents() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/featured`);
      if (!response.ok) {
        return await this.findUpcomingEvents();
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching featured events:', error);
      throw error;
    }
  }

  /**
   * Get event statistics
   */
  async getEventStats() {
    try {
      const [upcoming, featured, virtual] = await Promise.all([
        this.findUpcomingEvents(),
        this.findFeaturedEvents(),
        this.findVirtualEvents()
      ]);

      return {
        total: upcoming.length + featured.length,
        upcoming: upcoming.length,
        featured: featured.length,
        virtual: virtual.length,
        lastUpdated: new Date().toISOString()
      };
    } catch (error) {
      console.error('Error fetching event stats:', error);
      throw error;
    }
  }

  /**
   * Advanced search with multiple criteria
   */
  async advancedSearch({
    title,
    eventType,
    location,
    city,
    state,
    skillLevel,
    isVirtual,
    startDate,
    endDate,
    organizationId,
    sortBy = 'startDate',
    sortDirection = 'asc',
    page = 0,
    size = 20
  } = {}) {
    try {
      const searchRequest = {
        searchTerm: title || '',
        eventType: eventType || '',
        location: location || (city && state ? `${city}, ${state}` : city || state || ''),
        skillLevel: skillLevel || '',
        isVirtual: isVirtual,
        startDate: startDate,
        endDate: endDate,
        organizationId: organizationId
      };
      
      const response = await fetch(`${API_BASE_URL}/events/search?page=${page}&size=${size}&sortBy=${sortBy}&sortDirection=${sortDirection}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(searchRequest)
      });
      
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
   * Apply to event (placeholder)
   */
  async applyToEvent(eventId, applicationData) {
    try {
      const response = await fetch(`${API_BASE_URL}/applications`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          eventId: eventId,
          message: applicationData.message || ''
        })
      });
      
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error applying to event:', error);
      throw error;
    }
  }

  /**
   * Get event types
   */
  async getEventTypes() {
    try {
      return [
        'Community Service', 'Fundraising', 'Education', 'Environment', 
        'Healthcare', 'Animal Welfare', 'Disaster Relief', 'Arts & Culture',
        'Sports & Recreation', 'Senior Services', 'Youth Development',
        'Mental Health', 'Technology', 'Religious', 'International'
      ];
    } catch (error) {
      console.error('Error fetching event types:', error);
      throw error;
    }
  }

  /**
   * Get skill levels
   */
  async getSkillLevels() {
    try {
      return [
        'No Experience Required', 'Beginner', 'Intermediate', 
        'Advanced', 'Expert', 'Professional'
      ];
    } catch (error) {
      console.error('Error fetching skill levels:', error);
      throw error;
    }
  }
}

// Export singleton instance
const findEventService = new FindEventService();
export default findEventService;