// frontend/src/services/findEventsService.js - FINAL FIXED VERSION

const API_BASE_URL = 'http://localhost:8080/api';

// Import the existing createEventService for consistency
import { getAllEvents, getEventById, getUpcomingEvents } from './createEventService';

class FindEventService {
  
  constructor() {
    this.lastRefreshTime = new Date();
    this.refreshInterval = 30000; // 30 seconds
    this.searchCache = new Map();
    this.cacheTimeout = 10000; // 10 seconds cache timeout
  }

  // ==========================================
  // FINAL FIXED METHODS - Handle All Error Cases
  // ==========================================

  /**
   * FINAL: Get all events using verified backend endpoint
   */
  async findAllEvents() {
    try {
      console.log('🔍 Fetching all events from backend...');
      
      // Use the working createEventService method first
      const result = await getAllEvents();
      
      if (result.success) {
        console.log(`✅ Successfully fetched ${result.data.length} events via service`);
        this.lastRefreshTime = new Date();
        return Array.isArray(result.data) ? result.data : [];
      } else {
        throw new Error(result.message || 'Service failed');
      }
      
    } catch (error) {
      console.error('❌ Service failed, trying direct API call:', error);
      
      // Direct API fallback using verified endpoint
      try {
        console.log('🔄 Trying direct API call: GET /api/events');
        
        const response = await fetch(`${API_BASE_URL}/events`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'Cache-Control': 'no-cache'
          }
        });
        
        if (response.ok) {
          const data = await response.json();
          console.log(`✅ Direct API success: ${data.length} events`);
          this.lastRefreshTime = new Date();
          return Array.isArray(data) ? data : [];
        } else {
          console.error(`❌ Direct API failed: ${response.status} ${response.statusText}`);
          throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
      } catch (directError) {
        console.error('❌ Direct API also failed:', directError);
        // Return empty array to prevent crashes, but log the issue
        console.warn('⚠️ All methods failed, returning empty array');
        return [];
      }
    }
  }

  /**
   * FINAL: Real-time search with comprehensive error handling
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

      console.log('🚀 Performing search with backend endpoints:', searchParams);

      let events = [];

      // If we have search criteria, use the POST /api/events/search endpoint
      if (searchTerm || eventType || location || skillLevel || isVirtual !== null || startDate || endDate) {
        console.log('🔍 Using POST /api/events/search for filtered search');
        
        // FIXED: Create properly formatted and cleaned search request
        const searchRequest = {
          searchTerm: searchTerm || '',
          eventType: eventType || '',
          location: location || '',
          skillLevel: skillLevel || ''
        };

        // Only add optional fields if they have meaningful values
        if (isVirtual !== null && isVirtual !== undefined) {
          searchRequest.isVirtual = isVirtual;
        }
        if (startDate && startDate.trim() !== '') {
          searchRequest.startDate = startDate;
        }
        if (endDate && endDate.trim() !== '') {
          searchRequest.endDate = endDate;
        }

        console.log('📤 Sending cleaned search request:', JSON.stringify(searchRequest, null, 2));

        try {
          const response = await fetch(`${API_BASE_URL}/events/search`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'Accept': 'application/json',
              'Cache-Control': 'no-cache'
            },
            body: JSON.stringify(searchRequest)
          });

          if (response.ok) {
            const responseData = await response.json();
            events = Array.isArray(responseData) ? responseData : [];
            console.log(`✅ Search API returned ${events.length} events`);
          } else {
            const errorText = await response.text().catch(() => 'Unknown error');
            console.error(`❌ Search API failed: ${response.status} ${response.statusText}`, errorText);
            throw new Error(`Search API failed: ${response.status}`);
          }
        } catch (searchError) {
          console.error('❌ Search API request failed:', searchError);
          
          // ENHANCED: Better fallback strategy
          console.log('🔄 Falling back to all events with client-side filtering...');
          const allEvents = await this.findAllEvents();
          events = this.applyClientSideFilters(allEvents, searchParams);
          console.log(`🔄 Client-side filtering completed: ${events.length} events`);
        }
      } else {
        // No search criteria, get all events
        console.log('🔍 No search criteria, getting all events');
        events = await this.findAllEvents();
      }

      // Ensure we have an array and apply limit
      if (!Array.isArray(events)) {
        console.warn('⚠️ Search returned non-array, converting...');
        events = [];
      }

      if (limit && events.length > limit) {
        events = events.slice(0, limit);
        console.log(`✂️ Limited results to ${limit} events`);
      }

      console.log(`✅ Search completed: ${events.length} results`);
      
      return {
        data: events,
        timestamp: new Date().toISOString(),
        resultCount: events.length,
        searchParams,
        source: searchTerm || eventType || location ? 'search_api' : 'all_events'
      };

    } catch (error) {
      console.error('❌ Real-time search failed:', error);
      
      // FINAL FALLBACK: Just return all events
      try {
        console.log('🔄 Final fallback: getting all events...');
        const allEvents = await this.findAllEvents();
        
        console.log(`🔄 Final fallback completed: ${allEvents.length} events`);
        
        return {
          data: allEvents,
          timestamp: new Date().toISOString(),
          resultCount: allEvents.length,
          searchParams,
          source: 'final_fallback',
          fallback: true,
          error: error.message
        };
      } catch (fallbackError) {
        console.error('❌ Final fallback also failed:', fallbackError);
        return {
          data: [],
          timestamp: new Date().toISOString(),
          resultCount: 0,
          searchParams,
          error: error.message,
          fallbackError: fallbackError.message
        };
      }
    }
  }

  /**
   * ENHANCED: Client-side filtering helper
   */
  applyClientSideFilters(events, searchParams) {
    try {
      let filtered = [...events];

      // Search term filter
      if (searchParams.searchTerm) {
        const term = searchParams.searchTerm.toLowerCase();
        filtered = filtered.filter(event => 
          event.title?.toLowerCase().includes(term) ||
          event.description?.toLowerCase().includes(term) ||
          event.organizationName?.toLowerCase().includes(term)
        );
      }

      // Event type filter
      if (searchParams.eventType) {
        const typePattern = searchParams.eventType.toLowerCase();
        filtered = filtered.filter(event => 
          event.eventType?.toLowerCase().includes(typePattern) ||
          event.eventType?.toLowerCase().replace(/_/g, ' ').includes(typePattern) ||
          event.eventType?.toLowerCase().replace(/_/g, '-').includes(typePattern)
        );
      }

      // Location filter
      if (searchParams.location) {
        const locationLower = searchParams.location.toLowerCase();
        filtered = filtered.filter(event =>
          event.location?.toLowerCase().includes(locationLower) ||
          event.city?.toLowerCase().includes(locationLower) ||
          event.state?.toLowerCase().includes(locationLower)
        );
      }

      // Virtual filter
      if (searchParams.isVirtual !== null && searchParams.isVirtual !== undefined) {
        filtered = filtered.filter(event => event.isVirtual === searchParams.isVirtual);
      }

      return filtered;
    } catch (error) {
      console.error('❌ Client-side filtering failed:', error);
      return events; // Return original events if filtering fails
    }
  }

  /**
   * FINAL: Live data refresh with error handling
   */
  async refreshLiveData(options = {}) {
    try {
      const { force = false } = options;

      console.log(`🔄 Live event data refresh (force: ${force})`);

      const refreshStartTime = performance.now();

      // Get fresh data from backend
      const events = await this.findAllEvents();

      const refreshEndTime = performance.now();
      const clientRefreshDuration = refreshEndTime - refreshStartTime;

      // Update last refresh time
      this.lastRefreshTime = new Date();

      console.log(`✅ Live event refresh completed:`, {
        totalCount: events.length,
        clientDuration: Math.round(clientRefreshDuration),
        timestamp: this.lastRefreshTime.toISOString()
      });

      return {
        events: events,
        totalCount: events.length,
        refreshTimestamp: this.lastRefreshTime.toISOString(),
        clientDurationMs: Math.round(clientRefreshDuration),
        success: true
      };

    } catch (error) {
      console.error('❌ Live event refresh failed:', error);
      return {
        events: [],
        totalCount: 0,
        refreshTimestamp: new Date().toISOString(),
        success: false,
        error: error.message
      };
    }
  }

  /**
   * FINAL: Smart search with caching
   */
  async smartSearch(searchParams = {}) {
    try {
      const cacheKey = JSON.stringify(searchParams);
      const now = Date.now();
      
      // Check cache first
      if (searchParams.searchTerm && this.searchCache.has(cacheKey)) {
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

      // Perform the search
      console.log('🔍 Performing smart search');
      const result = await this.performRealtimeSearch(searchParams);

      // Cache successful results
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
      console.error('❌ Smart search failed:', error);
      return {
        data: [],
        timestamp: new Date().toISOString(),
        resultCount: 0,
        error: error.message
      };
    }
  }

  // ==========================================
  // OTHER WORKING METHODS (PRESERVED)
  // ==========================================

  async findEventById(id) {
    try {
      console.log(`🔍 Fetching event with ID: ${id}`);
      
      const result = await getEventById(id);
      
      if (result.success) {
        console.log(`✅ Successfully fetched event: ${result.data.title}`);
        return result.data;
      } else {
        throw new Error(result.message || 'Service failed');
      }
      
    } catch (error) {
      console.error('❌ Service failed, trying direct API:', error);
      
      try {
        const response = await fetch(`${API_BASE_URL}/events/${id}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          }
        });
        
        if (response.ok) {
          const event = await response.json();
          console.log(`✅ Direct API success: ${event.title}`);
          return event;
        } else if (response.status === 404) {
          throw new Error('Event not found');
        } else {
          throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
      } catch (directError) {
        console.error('❌ Direct API also failed:', directError);
        throw directError;
      }
    }
  }

  async findEventsByTitle(title, options = {}) {
    try {
      if (!title || title.trim() === '') {
        console.log('❌ Empty search title provided');
        return [];
      }

      console.log(`🔍 Searching events by title: "${title}"`);
      const result = await this.performRealtimeSearch({ searchTerm: title });
      return result.data;
      
    } catch (error) {
      console.error('❌ Title search failed:', error);
      return [];
    }
  }

  async findUpcomingEvents() {
    try {
      console.log('🔍 Fetching upcoming events...');
      
      const result = await getUpcomingEvents();
      
      if (result.success) {
        console.log(`✅ Successfully fetched ${result.data.length} upcoming events`);
        return Array.isArray(result.data) ? result.data : [];
      } else {
        throw new Error(result.message || 'Service failed');
      }
      
    } catch (error) {
      console.error('❌ Service failed, trying direct API:', error);
      
      try {
        const response = await fetch(`${API_BASE_URL}/events/upcoming`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          }
        });
        
        if (response.ok) {
          const events = await response.json();
          console.log(`✅ Direct API success: ${events.length} upcoming events`);
          return Array.isArray(events) ? events : [];
        } else {
          throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
      } catch (directError) {
        console.error('❌ Direct API also failed:', directError);
        
        // Final fallback: filter all events
        try {
          console.log('🔄 Final fallback: filtering all events...');
          const allEvents = await this.findAllEvents();
          const now = new Date();
          const upcomingEvents = allEvents.filter(event => {
            if (!event.startDate) return false;
            const eventDate = new Date(event.startDate);
            return eventDate >= now;
          });
          
          console.log(`📋 Fallback successful: Found ${upcomingEvents.length} upcoming events`);
          return upcomingEvents;
          
        } catch (fallbackError) {
          console.error('❌ Final fallback also failed:', fallbackError);
          return [];
        }
      }
    }
  }

  // ==========================================
  // UTILITY METHODS
  // ==========================================

  searchEvents(filters = {}) {
    return this.performRealtimeSearch(filters);
  }

  isDataStale() {
    const now = new Date();
    const timeSinceRefresh = now - this.lastRefreshTime;
    return timeSinceRefresh > this.refreshInterval;
  }

  cleanCache() {
    const now = Date.now();
    for (const [key, value] of this.searchCache.entries()) {
      if (now - value.timestamp > this.cacheTimeout * 2) {
        this.searchCache.delete(key);
      }
    }
  }

  async getEventStats() {
    try {
      const events = await this.findAllEvents();
      const now = new Date();
      
      const upcoming = events.filter(event => {
        if (!event.startDate) return false;
        return new Date(event.startDate) >= now;
      });
      
      const virtual = events.filter(event => event.isVirtual);
      
      return {
        total: events.length,
        upcoming: upcoming.length,
        virtual: virtual.length,
        lastUpdated: new Date().toISOString()
      };
    } catch (error) {
      console.error('❌ Error fetching event stats:', error);
      return {
        total: 0,
        upcoming: 0,
        virtual: 0,
        lastUpdated: new Date().toISOString()
      };
    }
  }
}

// Export singleton instance
const findEventService = new FindEventService();
export default findEventService;