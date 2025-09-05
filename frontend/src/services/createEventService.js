// frontend/src/services/eventService.js

import axios from 'axios';
import { getCurrentUser, ensureValidToken, logoutWithCleanup } from './authService';

// ==========================================
// API BASE CONFIGURATION
// ==========================================

const API_BASE_URL = 'http://localhost:8080/api';

// Create axios instance for events API
const eventApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
eventApi.interceptors.request.use(async (config) => {
  try {
    const token = await ensureValidToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  } catch (error) {
    console.error('Token validation failed in event service:', error);
    logoutWithCleanup();
    window.location.href = '/login';
    return Promise.reject(error);
  }
});

// Response interceptor for error handling
eventApi.interceptors.response.use(
  (response) => {
    console.log('✅ Event API Response:', response.status, response.config.url);
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    
    console.error('❌ Event API Error:', {
      status: error.response?.status,
      url: error.config?.url,
      data: error.response?.data,
      message: error.message
    });

    // Handle authentication errors
    if (error.response?.status === 401 && !originalRequest._retry) {
      console.log('🔄 401 error in event service, attempting token refresh...');
      
      try {
        originalRequest._retry = true;
        const token = await ensureValidToken();
        
        if (token) {
          originalRequest.headers.Authorization = `Bearer ${token}`;
          return eventApi(originalRequest);
        }
      } catch (refreshError) {
        console.error('❌ Token refresh failed in event service:', refreshError);
        logoutWithCleanup();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);

/**
 * Event service for managing events
 */

// ==========================================
// EVENT CRUD OPERATIONS
// ==========================================

/**
 * Create a new event
 */
export const createEvent = async (eventData) => {
  try {
    console.log('Creating event:', eventData);
    
    // Ensure user is authenticated
    await ensureValidToken();
    const user = getCurrentUser();
    
    if (!user) {
      throw new Error('User not authenticated');
    }
    
    // Validate event type against backend enum
    const validEventTypes = [
      'COMMUNITY_CLEANUP', 'FOOD_SERVICE', 'TUTORING_EDUCATION', 'ANIMAL_CARE',
      'ENVIRONMENTAL_CONSERVATION', 'SENIOR_SUPPORT', 'YOUTH_MENTORING', 
      'HEALTHCARE_SUPPORT', 'DISASTER_RELIEF', 'ARTS_CULTURE', 'SPORTS_RECREATION',
      'FUNDRAISING', 'ADMINISTRATIVE_SUPPORT', 'CONSTRUCTION_BUILDING',
      'TECHNOLOGY_SUPPORT', 'EVENT_PLANNING', 'ADVOCACY_AWARENESS', 'RESEARCH_DATA',
      'TRANSPORTATION', 'GARDENING', 'CRISIS_SUPPORT', 'FESTIVAL_FAIR',
      'WORKSHOP_TRAINING', 'BLOOD_DRIVE', 'OTHER'
    ];

    if (eventData.eventType && !validEventTypes.includes(eventData.eventType)) {
      throw new Error(`Invalid event type: ${eventData.eventType}. Must be one of: ${validEventTypes.join(', ')}`);
    }

    // Validate skill level against backend enum
    const validSkillLevels = [
      'NO_EXPERIENCE_REQUIRED', 'BEGINNER_FRIENDLY', 'SOME_EXPERIENCE_PREFERRED',
      'EXPERIENCED_VOLUNTEERS', 'SPECIALIZED_SKILLS_REQUIRED', 'TRAINING_PROVIDED'
    ];

    if (eventData.skillLevelRequired && !validSkillLevels.includes(eventData.skillLevelRequired)) {
      throw new Error(`Invalid skill level: ${eventData.skillLevelRequired}. Must be one of: ${validSkillLevels.join(', ')}`);
    }

    // Validate duration category against backend enum
    const validDurations = [
      'SHORT', 'MEDIUM', 'FULL_DAY', 'MULTI_DAY', 'WEEKLY_COMMITMENT',
      'MONTHLY_COMMITMENT', 'ONGOING_LONG_TERM'
    ];

    if (eventData.durationCategory && !validDurations.includes(eventData.durationCategory)) {
      throw new Error(`Invalid duration category: ${eventData.durationCategory}. Must be one of: ${validDurations.join(', ')}`);
    }

    // Prepare the request payload
    const payload = {
      title: eventData.title,
      description: eventData.description,
      eventType: eventData.eventType,
      skillLevelRequired: eventData.skillLevelRequired || 'NO_EXPERIENCE_REQUIRED',
      durationCategory: eventData.durationCategory || 'SHORT',
      startDate: eventData.startDate,
      endDate: eventData.endDate,
      location: eventData.location || '',
      address: eventData.address || '',
      city: eventData.city || '',
      state: eventData.state || '',
      zipCode: eventData.zipCode || '',
      maxVolunteers: eventData.maxVolunteers || null,
      estimatedHours: eventData.estimatedHours || null,
      requirements: eventData.requirements || '',
      contactEmail: eventData.contactEmail || user.email,
      contactPhone: eventData.contactPhone || '',
      imageUrl: eventData.imageUrl || '',
      isVirtual: eventData.isVirtual || false,
      virtualMeetingLink: eventData.virtualMeetingLink || '',
      timeOfDay: eventData.timeOfDay || '',
      isRecurring: eventData.isRecurring || false,
      recurrencePattern: eventData.recurrencePattern || '',
      hasFlexibleTiming: eventData.hasFlexibleTiming || false,
      isWeekdaysOnly: eventData.isWeekdaysOnly || false,
      isWeekendsOnly: eventData.isWeekendsOnly || false
    };

    console.log('📤 Sending event payload to backend:', JSON.stringify(payload, null, 2));

    const response = await eventApi.post('/events', payload);
    
    console.log('Event created successfully:', response.data);
    
    return {
      success: true,
      data: response.data,
      message: 'Event created successfully'
    };
    
  } catch (error) {
    console.error('Create event error:', error);
    console.error('❌ Full error details:', {
      message: error.message,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      headers: error.response?.headers,
      config: error.config
    });
    
    const errorMessage = error.response?.data?.message || 
                        error.response?.data?.error || 
                        error.message || 
                        'Failed to create event';
    
    return {
      success: false,
      message: errorMessage,
      error: error.response?.data || error.message,
      details: error.response?.data // Add more details for debugging
    };
  }
};

/**
 * Get all events
 */
export const getAllEvents = async () => {
  try {
    console.log('Fetching all events');
    
    const response = await eventApi.get('/events');
    
    return {
      success: true,
      data: response.data,
      message: 'Events fetched successfully'
    };
    
  } catch (error) {
    console.error('Get events error:', error);
    
    return {
      success: false,
      message: error.response?.data?.message || 'Failed to fetch events',
      error: error.response?.data || error.message
    };
  }
};

/**
 * Get event by ID
 */
export const getEventById = async (eventId) => {
  try {
    console.log('Fetching event:', eventId);
    
    const response = await eventApi.get(`/events/${eventId}`);
    
    return {
      success: true,
      data: response.data,
      message: 'Event fetched successfully'
    };
    
  } catch (error) {
    console.error('Get event error:', error);
    
    return {
      success: false,
      message: error.response?.data?.message || 'Failed to fetch event',
      error: error.response?.data || error.message
    };
  }
};

/**
 * Get events by organization
 */
export const getEventsByOrganization = async (organizationId = null) => {
  try {
    await ensureValidToken();
    const user = getCurrentUser();
    
    if (!user) {
      throw new Error('User not authenticated');
    }

    // Use provided organizationId or current user's ID for organizations
    const orgId = organizationId || (user.userType === 'ORGANIZATION' ? user.userId : null);
    
    if (!orgId) {
      throw new Error('Organization ID required');
    }

    console.log('Fetching events for organization:', orgId);
    
    const response = await eventApi.get(`/events/organization/${orgId}`);
    
    return {
      success: true,
      data: response.data,
      message: 'Organization events fetched successfully'
    };
    
  } catch (error) {
    console.error('Get organization events error:', error);
    
    return {
      success: false,
      message: error.response?.data?.message || 'Failed to fetch organization events',
      error: error.response?.data || error.message
    };
  }
};

/**
 * Update event
 */
export const updateEvent = async (eventId, eventData) => {
  try {
    console.log('Updating event:', eventId, eventData);
    
    await ensureValidToken();
    const user = getCurrentUser();
    
    if (!user || user.userType !== 'ORGANIZATION') {
      throw new Error('Only organizations can update events');
    }

    const response = await eventApi.put(`/events/${eventId}`, eventData);
    
    return {
      success: true,
      data: response.data,
      message: 'Event updated successfully'
    };
    
  } catch (error) {
    console.error('Update event error:', error);
    
    return {
      success: false,
      message: error.response?.data?.message || 'Failed to update event',
      error: error.response?.data || error.message
    };
  }
};

/**
 * Delete event
 */
export const deleteEvent = async (eventId) => {
  try {
    console.log('Deleting event:', eventId);
    
    await ensureValidToken();
    const user = getCurrentUser();
    
    if (!user || user.userType !== 'ORGANIZATION') {
      throw new Error('Only organizations can delete events');
    }

    const response = await eventApi.delete(`/events/${eventId}`);
    
    return {
      success: true,
      message: 'Event deleted successfully'
    };
    
  } catch (error) {
    console.error('Delete event error:', error);
    
    return {
      success: false,
      message: error.response?.data?.message || 'Failed to delete event',
      error: error.response?.data || error.message
    };
  }
};

/**
 * Search events with filters
 */
export const searchEvents = async (filters = {}) => {
  try {
    console.log('Searching events with filters:', filters);
    
    const params = new URLSearchParams();
    
    if (filters.searchTerm) {
      params.append('searchTerm', filters.searchTerm);
    }
    if (filters.eventType) {
      params.append('eventType', filters.eventType);
    }
    if (filters.location) {
      params.append('location', filters.location);
    }
    if (filters.skillLevel) {
      params.append('skillLevel', filters.skillLevel);
    }
    if (filters.isVirtual !== undefined) {
      params.append('isVirtual', filters.isVirtual);
    }
    if (filters.startDate) {
      params.append('startDate', filters.startDate);
    }
    if (filters.endDate) {
      params.append('endDate', filters.endDate);
    }

    const response = await eventApi.get(`/events/search?${params.toString()}`);
    
    return {
      success: true,
      data: response.data,
      message: 'Events search completed successfully'
    };
    
  } catch (error) {
    console.error('Search events error:', error);
    
    return {
      success: false,
      message: error.response?.data?.message || 'Failed to search events',
      error: error.response?.data || error.message
    };
  }
};

/**
 * Get upcoming events
 */
export const getUpcomingEvents = async (limit = 10) => {
  try {
    console.log('Fetching upcoming events');
    
    const response = await eventApi.get(`/events/upcoming?limit=${limit}`);
    
    return {
      success: true,
      data: response.data,
      message: 'Upcoming events fetched successfully'
    };
    
  } catch (error) {
    console.error('Get upcoming events error:', error);
    
    return {
      success: false,
      message: error.response?.data?.message || 'Failed to fetch upcoming events',
      error: error.response?.data || error.message
    };
  }
};

/**
 * Get event statistics
 */
export const getEventStats = async (eventId) => {
  try {
    console.log('Fetching event stats:', eventId);
    
    await ensureValidToken();
    
    const response = await eventApi.get(`/events/${eventId}/stats`);
    
    return {
      success: true,
      data: response.data,
      message: 'Event stats fetched successfully'
    };
    
  } catch (error) {
    console.error('Get event stats error:', error);
    
    return {
      success: false,
      message: error.response?.data?.message || 'Failed to fetch event stats',
      error: error.response?.data || error.message
    };
  }
};

// ==========================================
// EVENT STATUS OPERATIONS
// ==========================================

/**
 * Update event status
 */
export const updateEventStatus = async (eventId, status) => {
  try {
    console.log('Updating event status:', eventId, status);
    
    await ensureValidToken();
    const user = getCurrentUser();
    
    if (!user || user.userType !== 'ORGANIZATION') {
      throw new Error('Only organizations can update event status');
    }

    const response = await eventApi.patch(`/events/${eventId}/status`, { status });
    
    return {
      success: true,
      data: response.data,
      message: 'Event status updated successfully'
    };
    
  } catch (error) {
    console.error('Update event status error:', error);
    
    return {
      success: false,
      message: error.response?.data?.message || 'Failed to update event status',
      error: error.response?.data || error.message
    };
  }
};

/**
 * Publish event (change status to ACTIVE)
 */
export const publishEvent = async (eventId) => {
  return updateEventStatus(eventId, 'ACTIVE');
};

/**
 * Cancel event
 */
export const cancelEvent = async (eventId) => {
  return updateEventStatus(eventId, 'CANCELLED');
};

/**
 * Complete event
 */
export const completeEvent = async (eventId) => {
  return updateEventStatus(eventId, 'COMPLETED');
};

// ==========================================
// UTILITY FUNCTIONS
// ==========================================

/**
 * Format event date for display
 */
export const formatEventDate = (dateString) => {
  if (!dateString) return '';
  
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  } catch (error) {
    console.error('Date formatting error:', error);
    return dateString;
  }
};

/**
 * Format event time for display
 */
export const formatEventTime = (dateString) => {
  if (!dateString) return '';
  
  try {
    const date = new Date(dateString);
    return date.toLocaleTimeString('en-US', {
      hour: 'numeric',
      minute: '2-digit',
      hour12: true
    });
  } catch (error) {
    console.error('Time formatting error:', error);
    return dateString;
  }
};

/**
 * Get event type icon
 */
export const getEventTypeIcon = (eventType) => {
  const iconMap = {
    'COMMUNITY_CLEANUP': '🧹',
    'FOOD_SERVICE': '🍽️',
    'TUTORING_EDUCATION': '📚',
    'ANIMAL_CARE': '🐾',
    'ENVIRONMENTAL_CONSERVATION': '🌱',
    'SENIOR_SUPPORT': '👴',
    'YOUTH_MENTORING': '👥',
    'HEALTHCARE_SUPPORT': '🏥',
    'DISASTER_RELIEF': '🚑',
    'ARTS_CULTURE': '🎨',
    'SPORTS_RECREATION': '⚽',
    'FUNDRAISING': '💰',
    'ADMINISTRATIVE_SUPPORT': '📁',
    'CONSTRUCTION_BUILDING': '🔨',
    'TECHNOLOGY_SUPPORT': '💻',
    'EVENT_PLANNING': '📅',
    'ADVOCACY_AWARENESS': '📢',
    'RESEARCH_DATA': '📊',
    'TRANSPORTATION': '🚗',
    'GARDENING': '🌻',
    'CRISIS_SUPPORT': '🆘',
    'FESTIVAL_FAIR': '🎪',
    'WORKSHOP_TRAINING': '🎓',
    'BLOOD_DRIVE': '🩸',
    'OTHER': '📋'
  };
  
  return iconMap[eventType] || '📋';
};

/**
 * Get event type display name
 */
export const getEventTypeDisplayName = (eventType) => {
  const nameMap = {
    'COMMUNITY_CLEANUP': 'Community Cleanup',
    'FOOD_SERVICE': 'Food Service',
    'TUTORING_EDUCATION': 'Tutoring & Education',
    'ANIMAL_CARE': 'Animal Care',
    'ENVIRONMENTAL_CONSERVATION': 'Environmental Conservation',
    'SENIOR_SUPPORT': 'Senior Support',
    'YOUTH_MENTORING': 'Youth Mentoring',
    'HEALTHCARE_SUPPORT': 'Healthcare Support',
    'DISASTER_RELIEF': 'Disaster Relief',
    'ARTS_CULTURE': 'Arts & Culture',
    'SPORTS_RECREATION': 'Sports & Recreation',
    'FUNDRAISING': 'Fundraising',
    'ADMINISTRATIVE_SUPPORT': 'Administrative Support',
    'CONSTRUCTION_BUILDING': 'Construction & Building',
    'TECHNOLOGY_SUPPORT': 'Technology Support',
    'EVENT_PLANNING': 'Event Planning',
    'ADVOCACY_AWARENESS': 'Advocacy & Awareness',
    'RESEARCH_DATA': 'Research & Data',
    'TRANSPORTATION': 'Transportation',
    'GARDENING': 'Gardening',
    'CRISIS_SUPPORT': 'Crisis Support',
    'FESTIVAL_FAIR': 'Festival & Fair',
    'WORKSHOP_TRAINING': 'Workshop & Training',
    'BLOOD_DRIVE': 'Blood Drive',
    'OTHER': 'Other'
  };
  
  return nameMap[eventType] || eventType;
};

export default {
  createEvent,
  getAllEvents,
  getEventById,
  getEventsByOrganization,
  updateEvent,
  deleteEvent,
  searchEvents,
  getUpcomingEvents,
  getEventStats,
  updateEventStatus,
  publishEvent,
  cancelEvent,
  completeEvent,
  formatEventDate,
  formatEventTime,
  getEventTypeIcon,
  getEventTypeDisplayName
};