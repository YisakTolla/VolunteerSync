// frontend/src/services/findEventsService.js

const API_BASE_URL = 'http://localhost:8080/api';

class FindEventsService {
  
  /**
   * Find all events
   */
  async findAllEvents() {
    try {
      const response = await fetch(`${API_BASE_URL}/events`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching all events:', error);
      throw error;
    }
  }

  /**
   * Find events with pagination
   */
  async findAllEventsWithPagination(page = 0, size = 10, sortBy = 'startDate', sortDirection = 'asc') {
    try {
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        sortBy,
        sortDirection
      });
      
      const response = await fetch(`${API_BASE_URL}/events/paginated?${params}`);
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
   * Find event by ID
   */
  async findEventById(id) {
    try {
      const response = await fetch(`${API_BASE_URL}/events/${id}`);
      if (!response.ok) {
        if (response.status === 404) {
          return null;
        }
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error(`Error fetching event ${id}:`, error);
      throw error;
    }
  }

  /**
   * Search events by title
   */
  async findEventsByTitle(title) {
    try {
      const params = new URLSearchParams({ title });
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
   * Find past events
   */
  async findPastEvents() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/past`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching past events:', error);
      throw error;
    }
  }

  /**
   * Find ongoing events
   */
  async findOngoingEvents() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/ongoing`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching ongoing events:', error);
      throw error;
    }
  }

  /**
   * Find events by status
   */
  async findEventsByStatus(status) {
    try {
      const params = new URLSearchParams({ status });
      const response = await fetch(`${API_BASE_URL}/events/search/status?${params}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error finding events by status:', error);
      throw error;
    }
  }

  /**
   * Find active events
   */
  async findActiveEvents() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/active`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching active events:', error);
      throw error;
    }
  }

  /**
   * Find events by type
   */
  async findEventsByType(eventType) {
    try {
      const params = new URLSearchParams({ type: eventType });
      const response = await fetch(`${API_BASE_URL}/events/search/type?${params}`);
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
   * Find events by skill level
   */
  async findEventsBySkillLevel(skillLevel) {
    try {
      const params = new URLSearchParams({ skillLevel });
      const response = await fetch(`${API_BASE_URL}/events/search/skill-level?${params}`);
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
   * Find in-person events
   */
  async findInPersonEvents() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/in-person`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching in-person events:', error);
      throw error;
    }
  }

  /**
   * Find events by location
   */
  async findEventsByLocation(city, state) {
    try {
      const params = new URLSearchParams();
      if (city) params.append('city', city);
      if (state) params.append('state', state);
      
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
   * Find events by time of day
   */
  async findEventsByTimeOfDay(timeOfDay) {
    try {
      const params = new URLSearchParams({ timeOfDay });
      const response = await fetch(`${API_BASE_URL}/events/search/time-of-day?${params}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error finding events by time of day:', error);
      throw error;
    }
  }

  /**
   * Find weekend events
   */
  async findWeekendEvents() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/weekend`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching weekend events:', error);
      throw error;
    }
  }

  /**
   * Find weekday events
   */
  async findWeekdayEvents() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/weekday`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching weekday events:', error);
      throw error;
    }
  }

  /**
   * Find recurring events
   */
  async findRecurringEvents() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/recurring`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching recurring events:', error);
      throw error;
    }
  }

  /**
   * Find events with flexible timing
   */
  async findFlexibleTimingEvents() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/flexible-timing`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching flexible timing events:', error);
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
      
      const response = await fetch(`${API_BASE_URL}/events/search/date-range?${params}`);
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
   * Find today's events
   */
  async findTodaysEvents() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/today`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching today\'s events:', error);
      throw error;
    }
  }

  /**
   * Find this week's events
   */
  async findThisWeeksEvents() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/this-week`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching this week\'s events:', error);
      throw error;
    }
  }

  /**
   * Find this month's events
   */
  async findThisMonthsEvents() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/this-month`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching this month\'s events:', error);
      throw error;
    }
  }

  /**
   * Find available events (not full)
   */
  async findAvailableEvents() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/available`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching available events:', error);
      throw error;
    }
  }

  /**
   * Find urgent events (need volunteers, happening soon)
   */
  async findUrgentEvents() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/urgent`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching urgent events:', error);
      throw error;
    }
  }

  /**
   * Find beginner-friendly events
   */
  async findBeginnerFriendlyEvents() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/beginner-friendly`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching beginner-friendly events:', error);
      throw error;
    }
  }

  /**
   * Find specialized skill events
   */
  async findSpecializedSkillEvents() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/specialized-skills`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching specialized skill events:', error);
      throw error;
    }
  }

  /**
   * Find featured events (from verified organizations)
   */
  async findFeaturedEvents() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/featured`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching featured events:', error);
      throw error;
    }
  }

  /**
   * Search events with multiple filters
   */
  async searchEvents(filters = {}) {
    try {
      const params = new URLSearchParams();
      
      // Add all provided filters to params
      Object.entries(filters).forEach(([key, value]) => {
        if (value !== null && value !== undefined && value !== '') {
          params.append(key, value.toString());
        }
      });
      
      const response = await fetch(`${API_BASE_URL}/events/search?${params}`);
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
   * Advanced search with multiple criteria
   */
  async advancedSearch({
    title,
    eventType,
    status,
    skillLevel,
    duration,
    isVirtual,
    city,
    state,
    timeOfDay,
    isRecurring,
    hasFlexibleTiming,
    startDate,
    endDate,
    organizationId,
    minVolunteers,
    maxVolunteers,
    minHours,
    maxHours,
    sortBy = 'startDate',
    sortDirection = 'asc',
    page = 0,
    size = 20
  } = {}) {
    try {
      const params = new URLSearchParams();
      
      // Add search filters
      if (title) params.append('title', title);
      if (eventType) params.append('eventType', eventType);
      if (status) params.append('status', status);
      if (skillLevel) params.append('skillLevel', skillLevel);
      if (duration) params.append('duration', duration);
      if (isVirtual !== null && isVirtual !== undefined) params.append('isVirtual', isVirtual);
      if (city) params.append('city', city);
      if (state) params.append('state', state);
      if (timeOfDay) params.append('timeOfDay', timeOfDay);
      if (isRecurring !== null && isRecurring !== undefined) params.append('isRecurring', isRecurring);
      if (hasFlexibleTiming !== null && hasFlexibleTiming !== undefined) params.append('hasFlexibleTiming', hasFlexibleTiming);
      if (startDate) params.append('startDate', startDate);
      if (endDate) params.append('endDate', endDate);
      if (organizationId) params.append('organizationId', organizationId);
      if (minVolunteers !== null && minVolunteers !== undefined) params.append('minVolunteers', minVolunteers);
      if (maxVolunteers !== null && maxVolunteers !== undefined) params.append('maxVolunteers', maxVolunteers);
      if (minHours !== null && minHours !== undefined) params.append('minHours', minHours);
      if (maxHours !== null && maxHours !== undefined) params.append('maxHours', maxHours);
      
      // Add pagination and sorting
      params.append('sortBy', sortBy);
      params.append('sortDirection', sortDirection);
      params.append('page', page.toString());
      params.append('size', size.toString());
      
      const response = await fetch(`${API_BASE_URL}/events/advanced-search?${params}`);
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
   * Get events sorted by date (nearest first)
   */
  async findEventsSortedByDate() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/sorted/date`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching events sorted by date:', error);
      throw error;
    }
  }

  /**
   * Get events sorted by title
   */
  async findEventsSortedByTitle() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/sorted/title`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching events sorted by title:', error);
      throw error;
    }
  }

  /**
   * Get newest events
   */
  async findNewestEvents() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/sorted/newest`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching newest events:', error);
      throw error;
    }
  }

  /**
   * Get events sorted by capacity (largest first)
   */
  async findEventsByCapacity() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/sorted/capacity`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching events sorted by capacity:', error);
      throw error;
    }
  }

  /**
   * Get events sorted by available spots
   */
  async findEventsByAvailableSpots() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/sorted/available-spots`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching events sorted by available spots:', error);
      throw error;
    }
  }

  /**
   * Find similar events to a given event
   */
  async findSimilarEvents(eventId) {
    try {
      const response = await fetch(`${API_BASE_URL}/events/${eventId}/similar`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error finding similar events:', error);
      throw error;
    }
  }

  /**
   * Get recommended events for user skill level
   */
  async findRecommendedEvents(userSkillLevel) {
    try {
      const params = new URLSearchParams({ skillLevel: userSkillLevel });
      const response = await fetch(`${API_BASE_URL}/events/recommended?${params}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching recommended events:', error);
      throw error;
    }
  }

  /**
   * Get event statistics
   */
  async getEventStats() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/stats`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching event stats:', error);
      throw error;
    }
  }

  /**
   * Get event types (for filter dropdowns)
   */
  async getEventTypes() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/types`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching event types:', error);
      throw error;
    }
  }

  /**
   * Get skill levels (for filter dropdowns)
   */
  async getSkillLevels() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/skill-levels`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching skill levels:', error);
      throw error;
    }
  }

  /**
   * Get event durations (for filter dropdowns)
   */
  async getEventDurations() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/durations`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching event durations:', error);
      throw error;
    }
  }

  /**
   * Get event statuses (for filter dropdowns)
   */
  async getEventStatuses() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/statuses`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching event statuses:', error);
      throw error;
    }
  }

  /**
   * Get event locations (for filter dropdowns)
   */
  async getEventLocations() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/locations`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching event locations:', error);
      throw error;
    }
  }

  /**
   * Check if user can register for event
   */
  async canRegisterForEvent(eventId, userId) {
    try {
      const params = new URLSearchParams({ userId: userId.toString() });
      const response = await fetch(`${API_BASE_URL}/events/${eventId}/can-register?${params}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      const result = await response.json();
      return result.canRegister;
    } catch (error) {
      console.error('Error checking registration eligibility:', error);
      throw error;
    }
  }

  /**
   * Get events happening near a location
   */
  async findEventsNearLocation(latitude, longitude, radiusKm = 25) {
    try {
      const params = new URLSearchParams({
        lat: latitude.toString(),
        lng: longitude.toString(),
        radius: radiusKm.toString()
      });
      const response = await fetch(`${API_BASE_URL}/events/near?${params}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error finding events near location:', error);
      throw error;
    }
  }

  /**
   * Get trending events (popular, high registration)
   */
  async findTrendingEvents() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/trending`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching trending events:', error);
      throw error;
    }
  }

  /**
   * Find events by duration category
   */
  async findEventsByDuration(duration) {
    try {
      const params = new URLSearchParams({ duration });
      const response = await fetch(`${API_BASE_URL}/events/search/duration?${params}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error finding events by duration:', error);
      throw error;
    }
  }

  /**
   * Find short events (1-2 hours)
   */
  async findShortEvents() {
    return this.findEventsByDuration('SHORT');
  }

  /**
   * Find long-term commitment events
   */
  async findLongTermEvents() {
    try {
      const response = await fetch(`${API_BASE_URL}/events/long-term`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching long-term events:', error);
      throw error;
    }
  }

  /**
   * Find events by volunteer capacity range
   */
  async findEventsByVolunteerCapacity(minCapacity, maxCapacity) {
    try {
      const params = new URLSearchParams();
      if (minCapacity !== null && minCapacity !== undefined) {
        params.append('minCapacity', minCapacity.toString());
      }
      if (maxCapacity !== null && maxCapacity !== undefined) {
        params.append('maxCapacity', maxCapacity.toString());
      }
      
      const response = await fetch(`${API_BASE_URL}/events/search/capacity?${params}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error finding events by capacity:', error);
      throw error;
    }
  }

  /**
   * Find events requiring specific time commitment
   */
  async findEventsByHours(minHours, maxHours) {
    try {
      const params = new URLSearchParams();
      if (minHours !== null && minHours !== undefined) {
        params.append('minHours', minHours.toString());
      }
      if (maxHours !== null && maxHours !== undefined) {
        params.append('maxHours', maxHours.toString());
      }
      
      const response = await fetch(`${API_BASE_URL}/events/search/hours?${params}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error finding events by hours:', error);
      throw error;
    }
  }
}

// Export singleton instance
const findEventsService = new FindEventsService();
export default findEventsService;