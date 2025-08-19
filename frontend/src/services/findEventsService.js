// frontend/src/services/findEventService.js

const API_BASE_URL = 'http://localhost:8080/api';

class FindEventService {
  
  /**
   * Find all events (using multiple fallback strategies)
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
   * Search events by title
   */
  async findEventsByTitle(title) {
    try {
      const params = new URLSearchParams({ q: title });
      const response = await fetch(`${API_BASE_URL}/events/search/title?${params}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error searching events by title:', error);
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
   * Search events with multiple filters
   */
  async searchEvents(filters = {}) {
    try {
      const searchRequest = {
        searchTerm: filters.title || '',
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
   * NEW METHOD - Find events from newly created organizations
   * Helps users discover events from organizations that were recently established
   */
  async findEventsFromNewOrganizations(days = 30, limit = 50) {
    try {
      console.log(`🔍 Searching for events from organizations created within the last ${days} days`);
      
      // Strategy 1: Try dedicated endpoint for events from new organizations
      try {
        const response = await fetch(`${API_BASE_URL}/events/from-new-organizations?days=${days}&limit=${limit}`);
        if (response.ok) {
          const data = await response.json();
          console.log(`✅ Found ${data.length} events from new organizations`);
          return Array.isArray(data) ? data : [];
        } else {
          console.log(`❌ New organizations events endpoint failed: ${response.status}`);
        }
      } catch (endpointError) {
        console.log(`❌ New organizations events endpoint error:`, endpointError.message);
      }
      
      // Strategy 2: Get recently created organizations and find their events
      try {
        // Import the organization service to get new organizations
        // Note: In a real implementation, you might want to inject this dependency
        const organizationResponse = await fetch(`${API_BASE_URL}/organization-profiles/recently-created?days=${days}`);
        if (organizationResponse.ok) {
          const newOrganizations = await organizationResponse.json();
          console.log(`📋 Found ${newOrganizations.length} recently created organizations`);
          
          // Get events for each new organization
          const eventPromises = newOrganizations.map(async (org) => {
            try {
              const orgEvents = await this.findEventsByOrganization(org.id || org.organizationId);
              return orgEvents.map(event => ({
                ...event,
                organizationInfo: {
                  id: org.id || org.organizationId,
                  name: org.organizationName || org.name,
                  isNew: true,
                  createdDate: org.createdDate || org.registrationDate
                }
              }));
            } catch (error) {
              console.log(`❌ Failed to get events for organization ${org.id}:`, error.message);
              return [];
            }
          });
          
          const eventsArrays = await Promise.all(eventPromises);
          const allEvents = eventsArrays.flat();
          
          // Sort by event start date and limit results
          const sortedEvents = allEvents
            .sort((a, b) => new Date(a.startDate || a.eventDate) - new Date(b.startDate || b.eventDate))
            .slice(0, limit);
          
          console.log(`✅ Found ${sortedEvents.length} events from ${newOrganizations.length} new organizations`);
          return sortedEvents;
        }
      } catch (orgError) {
        console.log(`❌ Failed to get new organizations:`, orgError.message);
      }
      
      // Strategy 3: Use general search with organization creation date filter
      try {
        const searchRequest = {
          searchTerm: '',
          eventType: '',
          location: '',
          skillLevel: '',
          organizationCreatedAfter: new Date(Date.now() - (days * 24 * 60 * 60 * 1000)).toISOString()
        };
        
        const response = await fetch(`${API_BASE_URL}/events/search?page=0&size=${limit}&sortBy=startDate&sortDirection=asc`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(searchRequest)
        });
        
        if (response.ok) {
          const searchData = await response.json();
          const events = searchData.content || searchData || [];
          console.log(`✅ Found events using search with organization filter`);
          return events;
        }
      } catch (searchError) {
        console.log(`❌ Search with organization filter failed:`, searchError.message);
      }
      
      // Final fallback: Get upcoming events and mark them as potentially from new organizations
      console.log(`🔄 Using upcoming events as final fallback`);
      const upcomingEvents = await this.findUpcomingEvents();
      return upcomingEvents.slice(0, limit);
      
    } catch (error) {
      console.error('Error fetching events from new organizations:', error);
      throw error;
    }
  }

  /**
   * NEW METHOD - Find recent events from newly created organizations
   * More targeted search for events created recently by new organizations
   */
  async findRecentEventsFromNewOrganizations({
    organizationAgeDays = 30,
    eventAgeDays = 14,
    limit = 30,
    eventType = '',
    location = '',
    sortBy = 'startDate',
    sortDirection = 'asc'
  } = {}) {
    try {
      console.log(`🔍 Searching for events created within ${eventAgeDays} days by organizations created within ${organizationAgeDays} days`);
      
      // Calculate date thresholds
      const orgCreatedAfter = new Date(Date.now() - (organizationAgeDays * 24 * 60 * 60 * 1000));
      const eventCreatedAfter = new Date(Date.now() - (eventAgeDays * 24 * 60 * 60 * 1000));
      
      // Strategy 1: Try dedicated endpoint with advanced filters
      try {
        const params = new URLSearchParams({
          orgAgeDays: organizationAgeDays.toString(),
          eventAgeDays: eventAgeDays.toString(),
          limit: limit.toString(),
          sortBy,
          sortDirection
        });
        
        if (eventType) params.append('eventType', eventType);
        if (location) params.append('location', location);
        
        const response = await fetch(`${API_BASE_URL}/events/recent-from-new-organizations?${params}`);
        if (response.ok) {
          const data = await response.json();
          console.log(`✅ Found ${data.length} recent events from new organizations`);
          return Array.isArray(data) ? data : [];
        }
      } catch (endpointError) {
        console.log(`❌ Recent events from new organizations endpoint failed:`, endpointError.message);
      }
      
      // Strategy 2: Combine organization and event searches
      try {
        // First get new organizations
        const newOrgsResponse = await fetch(`${API_BASE_URL}/organization-profiles/recently-created?days=${organizationAgeDays}`);
        if (newOrgsResponse.ok) {
          const newOrganizations = await newOrgsResponse.json();
          console.log(`📋 Found ${newOrganizations.length} new organizations`);
          
          // Then get recent events from those organizations
          const recentEventsPromises = newOrganizations.map(async (org) => {
            try {
              // Get all events from this organization
              const orgEvents = await this.findEventsByOrganization(org.id || org.organizationId);
              
              // Filter for events created recently
              const recentEvents = orgEvents.filter(event => {
                const eventCreated = new Date(event.createdDate || event.publishedDate);
                return eventCreated >= eventCreatedAfter;
              });
              
              // Add organization metadata
              return recentEvents.map(event => ({
                ...event,
                organizationInfo: {
                  id: org.id || org.organizationId,
                  name: org.organizationName || org.name,
                  isNew: true,
                  createdDate: org.createdDate || org.registrationDate,
                  daysSinceCreated: Math.floor((Date.now() - new Date(org.createdDate || org.registrationDate)) / (1000 * 60 * 60 * 24))
                }
              }));
            } catch (error) {
              console.log(`❌ Failed to get recent events for organization ${org.id}:`, error.message);
              return [];
            }
          });
          
          const eventsArrays = await Promise.all(recentEventsPromises);
          let allEvents = eventsArrays.flat();
          
          // Apply additional filters
          if (eventType) {
            allEvents = allEvents.filter(event => 
              event.eventType?.toLowerCase() === eventType.toLowerCase() ||
              event.type?.toLowerCase() === eventType.toLowerCase()
            );
          }
          
          if (location) {
            allEvents = allEvents.filter(event => 
              event.location?.toLowerCase().includes(location.toLowerCase()) ||
              event.city?.toLowerCase().includes(location.toLowerCase()) ||
              event.state?.toLowerCase().includes(location.toLowerCase())
            );
          }
          
          // Sort and limit results
          const sortedEvents = allEvents
            .sort((a, b) => {
              const aDate = new Date(a[sortBy] || a.startDate || a.eventDate);
              const bDate = new Date(b[sortBy] || b.startDate || b.eventDate);
              return sortDirection === 'desc' ? bDate - aDate : aDate - bDate;
            })
            .slice(0, limit);
          
          console.log(`✅ Found ${sortedEvents.length} recent events from new organizations`);
          return sortedEvents;
        }
      } catch (combinedError) {
        console.log(`❌ Combined search strategy failed:`, combinedError.message);
      }
      
      // Final fallback: Get events from new organizations without recency filter
      console.log(`🔄 Falling back to general new organization events`);
      return await this.findEventsFromNewOrganizations(organizationAgeDays, limit);
      
    } catch (error) {
      console.error('Error fetching recent events from new organizations:', error);
      throw error;
    }
  }

  /**
   * NEW METHOD - Refresh events from new organizations
   * Forces a fresh fetch to ensure users see the latest events from newly created organizations
   */
  async refreshEventsFromNewOrganizations(organizationMaxAge = 7, limit = 50) {
    try {
      console.log(`🔄 Refreshing events from organizations created within ${organizationMaxAge} days`);
      
      // Add cache-busting parameter
      const cacheBuster = new Date().getTime();
      
      // Try multiple endpoints with cache busting
      const refreshEndpoints = [
        `events/from-new-organizations?days=${organizationMaxAge}&limit=${limit}&_t=${cacheBuster}`,
        `events/recent-from-new-organizations?orgAgeDays=${organizationMaxAge}&limit=${limit}&_t=${cacheBuster}`,
        `events/by-new-organizations?maxAge=${organizationMaxAge}&limit=${limit}&_t=${cacheBuster}`
      ];
      
      for (const endpoint of refreshEndpoints) {
        try {
          console.log(`🔄 Trying fresh data from: ${API_BASE_URL}/${endpoint}`);
          const response = await fetch(`${API_BASE_URL}/${endpoint}`, {
            method: 'GET',
            headers: {
              'Content-Type': 'application/json',
              'Accept': 'application/json',
              'Cache-Control': 'no-cache',
              'Pragma': 'no-cache'
            }
          });
          
          if (response.ok) {
            const data = await response.json();
            console.log(`✅ Successfully refreshed events from ${endpoint}:`, data.length);
            return Array.isArray(data) ? data : [];
          }
        } catch (endpointError) {
          console.log(`❌ Refresh failed for ${endpoint}:`, endpointError.message);
          continue;
        }
      }
      
      // Fallback: Use the standard method with forced refresh
      console.log(`🔄 Using standard method with forced refresh`);
      return await this.findEventsFromNewOrganizations(organizationMaxAge, limit);
      
    } catch (error) {
      console.error('Error refreshing events from new organizations:', error);
      throw error;
    }
  }

  /**
   * NEW METHOD - Find events from a specific newly created organization
   * Helps users find events after they've just created an organization
   */
  async findEventsFromNewlyCreatedOrganization(organizationId, timeWindowHours = 24) {
    try {
      console.log(`🔍 Searching for events from newly created organization ${organizationId} within ${timeWindowHours} hours`);
      
      // Strategy 1: Get all events from the organization
      try {
        const orgEvents = await this.findEventsByOrganization(organizationId);
        
        // Filter for events created within the time window
        const timeWindowMs = timeWindowHours * 60 * 60 * 1000;
        const cutoffTime = new Date(Date.now() - timeWindowMs);
        
        const recentEvents = orgEvents.filter(event => {
          const eventCreated = new Date(event.createdDate || event.publishedDate || event.startDate);
          return eventCreated >= cutoffTime;
        });
        
        console.log(`✅ Found ${recentEvents.length} recent events from organization ${organizationId}`);
        return recentEvents;
        
      } catch (orgError) {
        console.log(`❌ Failed to get events for organization ${organizationId}:`, orgError.message);
      }
      
      // Strategy 2: Search by organization ID in general search
      try {
        const searchRequest = {
          searchTerm: '',
          eventType: '',
          location: '',
          skillLevel: '',
          organizationId: organizationId
        };
        
        const response = await fetch(`${API_BASE_URL}/events/search?page=0&size=50&sortBy=createdDate&sortDirection=desc`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(searchRequest)
        });
        
        if (response.ok) {
          const searchData = await response.json();
          const events = searchData.content || searchData || [];
          console.log(`✅ Found events using organization search`);
          return events;
        }
      } catch (searchError) {
        console.log(`❌ Organization search failed:`, searchError.message);
      }
      
      // Strategy 3: Refresh and try again
      console.log(`🔄 Refreshing data and searching again...`);
      const refreshedEvents = await this.refreshEventsFromNewOrganizations(1, 100);
      const orgEvents = refreshedEvents.filter(event => 
        event.organizationId === organizationId ||
        event.organizationInfo?.id === organizationId
      );
      
      console.log(`✅ Found ${orgEvents.length} events after refresh`);
      return orgEvents;
      
    } catch (error) {
      console.error('Error finding events from newly created organization:', error);
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
}

// Export singleton instance
const findEventService = new FindEventService();
export default findEventService;