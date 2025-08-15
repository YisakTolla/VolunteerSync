import React, { useState, useEffect } from 'react';
import { Search, MapPin, Filter, Calendar, Clock, Users, ExternalLink, Star, ChevronLeft, ChevronRight } from 'lucide-react';
import './Events.css';
import findEventsService from '../services/findEventsService';

const Events = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [locationSearchTerm, setLocationSearchTerm] = useState('');
  const [selectedEventTypes, setSelectedEventTypes] = useState([]);
  const [selectedLocations, setSelectedLocations] = useState([]);
  const [selectedDates, setSelectedDates] = useState([]);
  const [selectedTimes, setSelectedTimes] = useState([]);
  const [selectedDurations, setSelectedDurations] = useState([]);
  const [selectedSkillLevels, setSelectedSkillLevels] = useState([]);
  const [showMoreEventTypes, setShowMoreEventTypes] = useState(false);
  const [showMoreLocations, setShowMoreLocations] = useState(false);

  // Backend data state
  const [events, setEvents] = useState([]);
  const [filteredEvents, setFilteredEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Pagination state
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(20);
  const [totalPages, setTotalPages] = useState(1);
  const [paginatedEvents, setPaginatedEvents] = useState([]);

  // Event types/categories
  const eventTypes = [
    'Community Cleanup', 'Food Service', 'Tutoring & Education', 'Animal Care', 
    'Environmental Conservation', 'Senior Support', 'Youth Mentoring', 'Healthcare Support',
    'Disaster Relief', 'Arts & Culture', 'Sports & Recreation', 'Fundraising',
    'Administrative Support', 'Construction & Building', 'Technology Support', 'Event Planning',
    'Advocacy & Awareness', 'Research & Data', 'Transportation', 'Gardening',
    'Crisis Support', 'Festival & Fair', 'Workshop & Training', 'Blood Drive'
  ];

  const locations = [
    'Virtual/Remote', 'New York, NY', 'Los Angeles, CA', 'Chicago, IL', 'Houston, TX',
    'Phoenix, AZ', 'Philadelphia, PA', 'San Antonio, TX', 'San Diego, CA', 'Dallas, TX',
    'San Jose, CA', 'Austin, TX', 'Jacksonville, FL', 'Fort Worth, TX', 'Columbus, OH',
    'Charlotte, NC', 'San Francisco, CA', 'Indianapolis, IN', 'Seattle, WA', 'Denver, CO'
  ];

  const dateOptions = [
    'Today', 'Tomorrow', 'This Week', 'Next Week', 'This Weekend', 'Next Weekend',
    'This Month', 'Next Month', 'Next 3 Months', 'Custom Date Range'
  ];

  const timeOptions = [
    'Morning (6AM-12PM)', 'Afternoon (12PM-6PM)', 'Evening (6PM-10PM)', 
    'Weekdays Only', 'Weekends Only', 'Flexible Timing'
  ];

  const durationOptions = [
    '1-2 Hours', '3-4 Hours', '5-8 Hours (Full Day)', 'Multi-Day Event',
    'Weekly Commitment', 'Monthly Commitment', 'Ongoing/Long-term'
  ];

  const skillLevels = [
    'No Experience Required', 'Beginner Friendly', 'Some Experience Preferred',
    'Experienced Volunteers', 'Specialized Skills Required', 'Training Provided'
  ];

  // Load events on component mount
  useEffect(() => {
    loadEvents();
  }, []);

  // Apply filters when filter criteria change
  useEffect(() => {
    applyFilters();
  }, [events, searchTerm, locationSearchTerm, selectedEventTypes, selectedLocations, selectedDates, selectedTimes, selectedDurations, selectedSkillLevels]);

  // Update pagination when filtered results change
  useEffect(() => {
    updatePagination();
  }, [filteredEvents, currentPage]);

  const loadEvents = async () => {
    try {
      setLoading(true);
      setError(null);
      const eventsData = await findEventsService.findAllEvents();
      setEvents(eventsData);
    } catch (err) {
      console.error('Failed to load events:', err);
      setError('Failed to load events. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  const applyFilters = () => {
    let filtered = [...events];

    // Search term filter
    if (searchTerm) {
      filtered = filtered.filter(event => 
        event.title?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        event.description?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        event.eventType?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        event.organizationName?.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    // Location search filter
    if (locationSearchTerm) {
      filtered = filtered.filter(event => 
        event.location?.toLowerCase().includes(locationSearchTerm.toLowerCase()) ||
        event.city?.toLowerCase().includes(locationSearchTerm.toLowerCase()) ||
        event.state?.toLowerCase().includes(locationSearchTerm.toLowerCase()) ||
        event.zipCode?.includes(locationSearchTerm)
      );
    }

    // Event type filter
    if (selectedEventTypes.length > 0) {
      filtered = filtered.filter(event => 
        selectedEventTypes.some(type => 
          event.eventType?.includes(type) ||
          event.title?.includes(type)
        )
      );
    }

    // Location filter
    if (selectedLocations.length > 0) {
      filtered = filtered.filter(event => 
        selectedLocations.some(location => 
          event.location?.includes(location) ||
          event.city?.includes(location) ||
          (location === 'Virtual/Remote' && event.isVirtual)
        )
      );
    }

    // Date filter
    if (selectedDates.length > 0) {
      filtered = filtered.filter(event => {
        const eventDate = new Date(event.startDate);
        const now = new Date();
        
        return selectedDates.some(option => {
          switch(option) {
            case 'Today':
              return eventDate.toDateString() === now.toDateString();
            case 'Tomorrow':
              const tomorrow = new Date(now);
              tomorrow.setDate(tomorrow.getDate() + 1);
              return eventDate.toDateString() === tomorrow.toDateString();
            case 'This Week':
              const weekEnd = new Date(now);
              weekEnd.setDate(weekEnd.getDate() + 7);
              return eventDate >= now && eventDate <= weekEnd;
            case 'Next Week':
              const nextWeekStart = new Date(now);
              nextWeekStart.setDate(nextWeekStart.getDate() + 7);
              const nextWeekEnd = new Date(now);
              nextWeekEnd.setDate(nextWeekEnd.getDate() + 14);
              return eventDate >= nextWeekStart && eventDate <= nextWeekEnd;
            case 'This Month':
              return eventDate.getMonth() === now.getMonth() && eventDate.getFullYear() === now.getFullYear();
            case 'Next Month':
              const nextMonth = new Date(now);
              nextMonth.setMonth(nextMonth.getMonth() + 1);
              return eventDate.getMonth() === nextMonth.getMonth() && eventDate.getFullYear() === nextMonth.getFullYear();
            default:
              return true;
          }
        });
      });
    }

    // Time filter
    if (selectedTimes.length > 0) {
      filtered = filtered.filter(event => {
        const eventTime = new Date(event.startDate).getHours();
        
        return selectedTimes.some(timeOption => {
          switch(timeOption) {
            case 'Morning (6AM-12PM)':
              return eventTime >= 6 && eventTime < 12;
            case 'Afternoon (12PM-6PM)':
              return eventTime >= 12 && eventTime < 18;
            case 'Evening (6PM-10PM)':
              return eventTime >= 18 && eventTime < 22;
            case 'Weekdays Only':
              const dayOfWeek = new Date(event.startDate).getDay();
              return dayOfWeek >= 1 && dayOfWeek <= 5;
            case 'Weekends Only':
              const weekendDay = new Date(event.startDate).getDay();
              return weekendDay === 0 || weekendDay === 6;
            default:
              return true;
          }
        });
      });
    }

    // Duration filter
    if (selectedDurations.length > 0) {
      filtered = filtered.filter(event => {
        const duration = event.duration || 0;
        
        return selectedDurations.some(durationOption => {
          switch(durationOption) {
            case '1-2 Hours':
              return duration >= 1 && duration <= 2;
            case '3-4 Hours':
              return duration >= 3 && duration <= 4;
            case '5-8 Hours (Full Day)':
              return duration >= 5 && duration <= 8;
            case 'Multi-Day Event':
              return duration > 8;
            default:
              return true;
          }
        });
      });
    }

    // Skill level filter
    if (selectedSkillLevels.length > 0) {
      filtered = filtered.filter(event => 
        selectedSkillLevels.some(skill => 
          event.skillLevel?.includes(skill) ||
          event.requirements?.includes(skill)
        )
      );
    }

    setFilteredEvents(filtered);
    setCurrentPage(1); // Reset to first page when filters change
  };

  const updatePagination = () => {
    const total = Math.ceil(filteredEvents.length / itemsPerPage);
    setTotalPages(total);

    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const paginated = filteredEvents.slice(startIndex, endIndex);
    setPaginatedEvents(paginated);
  };

  const handlePageChange = (page) => {
    if (page >= 1 && page <= totalPages) {
      setCurrentPage(page);
      // Scroll to top of results
      document.querySelector('.events-main')?.scrollIntoView({ behavior: 'smooth' });
    }
  };

  const getPageNumbers = () => {
    const delta = 2;
    const range = [];
    const rangeWithDots = [];

    for (let i = Math.max(2, currentPage - delta); i <= Math.min(totalPages - 1, currentPage + delta); i++) {
      range.push(i);
    }

    if (currentPage - delta > 2) {
      rangeWithDots.push(1, '...');
    } else {
      rangeWithDots.push(1);
    }

    rangeWithDots.push(...range);

    if (currentPage + delta < totalPages - 1) {
      rangeWithDots.push('...', totalPages);
    } else if (totalPages > 1) {
      rangeWithDots.push(totalPages);
    }

    return rangeWithDots;
  };

  const handleEventTypeToggle = (type) => {
    setSelectedEventTypes(prev => 
      prev.includes(type) 
        ? prev.filter(t => t !== type)
        : [...prev, type]
    );
  };

  const handleLocationToggle = (location) => {
    setSelectedLocations(prev => 
      prev.includes(location) 
        ? prev.filter(l => l !== location)
        : [...prev, location]
    );
  };

  const handleDateToggle = (date) => {
    setSelectedDates(prev => 
      prev.includes(date) 
        ? prev.filter(d => d !== date)
        : [...prev, date]
    );
  };

  const handleTimeToggle = (time) => {
    setSelectedTimes(prev => 
      prev.includes(time) 
        ? prev.filter(t => t !== time)
        : [...prev, time]
    );
  };

  const handleDurationToggle = (duration) => {
    setSelectedDurations(prev => 
      prev.includes(duration) 
        ? prev.filter(d => d !== duration)
        : [...prev, duration]
    );
  };

  const handleSkillLevelToggle = (skill) => {
    setSelectedSkillLevels(prev => 
      prev.includes(skill) 
        ? prev.filter(s => s !== skill)
        : [...prev, skill]
    );
  };

  const clearAllFilters = () => {
    setSelectedEventTypes([]);
    setSelectedLocations([]);
    setSelectedDates([]);
    setSelectedTimes([]);
    setSelectedDurations([]);
    setSelectedSkillLevels([]);
    setSearchTerm('');
    setLocationSearchTerm('');
  };

  const hasActiveFilters = selectedEventTypes.length > 0 || selectedLocations.length > 0 || 
                         selectedDates.length > 0 || selectedTimes.length > 0 || 
                         selectedDurations.length > 0 || selectedSkillLevels.length > 0 ||
                         searchTerm || locationSearchTerm;

  const formatEventDate = (dateStr) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-US', { 
      weekday: 'short', 
      month: 'short', 
      day: 'numeric', 
      year: 'numeric' 
    });
  };

  const formatEventTime = (dateStr) => {
    const date = new Date(dateStr);
    return date.toLocaleTimeString('en-US', { 
      hour: 'numeric', 
      minute: '2-digit',
      hour12: true 
    });
  };

  const getEventStatus = (event) => {
    const now = new Date();
    const startDate = new Date(event.startDate);
    const endDate = new Date(event.endDate);
    
    if (endDate < now) return 'Completed';
    if (startDate > now) return 'Upcoming';
    return 'In Progress';
  };

  const getSpotsRemaining = (event) => {
    const maxVolunteers = event.maxVolunteers || 0;
    const currentVolunteers = event.currentVolunteers || 0;
    return Math.max(0, maxVolunteers - currentVolunteers);
  };

  return (
    <div className="events-page">
      <div className="events-container">
        {/* Left Sidebar - Filters */}
        <div className="events-sidebar">
          <div className="events-filter-card">
            <h2 className="events-filter-title">
              <Filter />
              Filter Events
            </h2>
            
            {/* Event Type Filter */}
            <div className="events-filter-section">
              <h3 className="events-filter-section-title">Event Type</h3>
              <div className="events-filter-options">
                {eventTypes.slice(0, showMoreEventTypes ? eventTypes.length : 8).map((type) => (
                  <div
                    key={type}
                    className={`events-filter-option ${selectedEventTypes.includes(type) ? 'active' : ''}`}
                    onClick={() => handleEventTypeToggle(type)}
                  >
                    {type}
                  </div>
                ))}
              </div>
              {eventTypes.length > 8 && (
                <button
                  onClick={() => setShowMoreEventTypes(!showMoreEventTypes)}
                  className="events-show-more-btn"
                >
                  {showMoreEventTypes ? 'Show Less' : 'Show More'}
                </button>
              )}
            </div>

            {/* Date Filter */}
            <div className="events-filter-section">
              <h3 className="events-filter-section-title">Date</h3>
              <div className="events-filter-options">
                {dateOptions.map((date) => (
                  <div
                    key={date}
                    className={`events-filter-option date ${selectedDates.includes(date) ? 'active' : ''}`}
                    onClick={() => handleDateToggle(date)}
                  >
                    <Calendar className="events-filter-option-icon" />
                    {date}
                  </div>
                ))}
              </div>
            </div>

            {/* Time Filter */}
            <div className="events-filter-section">
              <h3 className="events-filter-section-title">Time of Day</h3>
              <div className="events-filter-options">
                {timeOptions.map((time) => (
                  <div
                    key={time}
                    className={`events-filter-option time ${selectedTimes.includes(time) ? 'active' : ''}`}
                    onClick={() => handleTimeToggle(time)}
                  >
                    <Clock className="events-filter-option-icon" />
                    {time}
                  </div>
                ))}
              </div>
            </div>

            {/* Duration Filter */}
            <div className="events-filter-section">
              <h3 className="events-filter-section-title">Duration</h3>
              <div className="events-filter-options">
                {durationOptions.map((duration) => (
                  <div
                    key={duration}
                    className={`events-filter-option duration ${selectedDurations.includes(duration) ? 'active' : ''}`}
                    onClick={() => handleDurationToggle(duration)}
                  >
                    {duration}
                  </div>
                ))}
              </div>
            </div>

            {/* Location Filter */}
            <div className="events-filter-section">
              <h3 className="events-filter-section-title">Location</h3>
              <div className="events-filter-options">
                {locations.slice(0, showMoreLocations ? locations.length : 6).map((location) => (
                  <div
                    key={location}
                    className={`events-filter-option location ${selectedLocations.includes(location) ? 'active' : ''}`}
                    onClick={() => handleLocationToggle(location)}
                  >
                    <MapPin className="events-filter-option-icon" />
                    {location}
                  </div>
                ))}
              </div>
              {locations.length > 6 && (
                <button
                  onClick={() => setShowMoreLocations(!showMoreLocations)}
                  className="events-show-more-btn"
                >
                  {showMoreLocations ? 'Show Less' : 'Show More'}
                </button>
              )}
            </div>

            {/* Skill Level Filter */}
            <div className="events-filter-section">
              <h3 className="events-filter-section-title">Skill Level</h3>
              <div className="events-filter-options">
                {skillLevels.map((skill) => (
                  <div
                    key={skill}
                    className={`events-filter-option skill ${selectedSkillLevels.includes(skill) ? 'active' : ''}`}
                    onClick={() => handleSkillLevelToggle(skill)}
                  >
                    {skill}
                  </div>
                ))}
              </div>
            </div>

            {/* Clear Filters Button */}
            {hasActiveFilters && (
              <button
                onClick={clearAllFilters}
                className="events-clear-filters-btn"
              >
                Clear All Filters
              </button>
            )}
          </div>
        </div>

        {/* Main Content */}
        <div className="events-main">
          {/* Header */}
          <div className="events-header">
            <h1 className="events-title">Volunteer Events</h1>
            <p className="events-subtitle">
              {loading ? 'Loading...' : `${filteredEvents.length} events available`} • 
              {!loading && totalPages > 0 && (
                <>Showing {((currentPage - 1) * itemsPerPage) + 1}-{Math.min(currentPage * itemsPerPage, filteredEvents.length)} of {filteredEvents.length} • </>
              )}
              Last Updated: <span className="events-highlight">Today</span>
            </p>
          </div>

          {/* Search Bar */}
          <div className="events-search-section">
            <div className="events-search-input-container">
              <Search className="events-search-icon" />
              <input
                type="text"
                placeholder="Search events, organizations, or causes..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="events-search-input"
              />
            </div>

            {/* Location Search */}
            <div className="events-search-input-container">
              <MapPin className="events-search-icon" />
              <input
                type="text"
                placeholder="Search by city, state, zip code..."
                value={locationSearchTerm}
                onChange={(e) => setLocationSearchTerm(e.target.value)}
                className="events-search-input"
              />
            </div>
          </div>

          {/* Active Filters */}
          {hasActiveFilters && (
            <div className="events-active-filters">
              <div className="events-active-filters-content">
                <Filter className="events-active-filters-icon" />
                <span className="events-active-filters-label">Active Filters:</span>
                
                {selectedEventTypes.map((type) => (
                  <span key={type} className="events-filter-chip">
                    {type}
                    <button
                      onClick={() => handleEventTypeToggle(type)}
                      className="events-filter-chip-remove"
                    >
                      ×
                    </button>
                  </span>
                ))}

                {selectedLocations.map((location) => (
                  <span key={location} className="events-filter-chip location">
                    {location}
                    <button
                      onClick={() => handleLocationToggle(location)}
                      className="events-filter-chip-remove"
                    >
                      ×
                    </button>
                  </span>
                ))}

                {selectedDates.map((date) => (
                  <span key={date} className="events-filter-chip date">
                    {date}
                    <button
                      onClick={() => handleDateToggle(date)}
                      className="events-filter-chip-remove"
                    >
                      ×
                    </button>
                  </span>
                ))}

                {selectedTimes.map((time) => (
                  <span key={time} className="events-filter-chip time">
                    {time}
                    <button
                      onClick={() => handleTimeToggle(time)}
                      className="events-filter-chip-remove"
                    >
                      ×
                    </button>
                  </span>
                ))}

                {selectedDurations.map((duration) => (
                  <span key={duration} className="events-filter-chip duration">
                    {duration}
                    <button
                      onClick={() => handleDurationToggle(duration)}
                      className="events-filter-chip-remove"
                    >
                      ×
                    </button>
                  </span>
                ))}

                {selectedSkillLevels.map((skill) => (
                  <span key={skill} className="events-filter-chip skill">
                    {skill}
                    <button
                      onClick={() => handleSkillLevelToggle(skill)}
                      className="events-filter-chip-remove"
                    >
                      ×
                    </button>
                  </span>
                ))}
              </div>
            </div>
          )}

          {/* Loading State */}
          {loading && (
            <div className="events-loading">
              <div className="events-loading-spinner"></div>
              <p>Loading events...</p>
            </div>
          )}

          {/* Error State */}
          {error && (
            <div className="events-error">
              <p>{error}</p>
              <button onClick={loadEvents} className="events-retry-btn">
                Try Again
              </button>
            </div>
          )}

          {/* Events List */}
          {!loading && !error && paginatedEvents.length > 0 && (
            <>
              <div className="events-list">
                {paginatedEvents.map((event) => (
                  <div key={event.id} className="event-card">
                    <div className="event-card-header">
                      <div className="event-card-image">
                        {event.imageUrl ? (
                          <img src={event.imageUrl} alt={event.title} />
                        ) : (
                          <div className="event-card-placeholder">
                            <Calendar />
                          </div>
                        )}
                        <div className={`event-card-status ${getEventStatus(event).toLowerCase().replace(' ', '-')}`}>
                          {getEventStatus(event)}
                        </div>
                      </div>
                      <div className="event-card-content">
                        <div className="event-card-meta">
                          <span className="event-card-type">{event.eventType || 'Volunteer Event'}</span>
                          {event.isVirtual && <span className="event-card-virtual">Virtual</span>}
                        </div>
                        <h3 className="event-card-title">{event.title}</h3>
                        <p className="event-card-description">
                          {event.description?.substring(0, 150)}
                          {event.description?.length > 150 ? '...' : ''}
                        </p>
                        <div className="event-card-details">
                          <div className="event-card-detail">
                            <Calendar className="event-card-detail-icon" />
                            <span>{formatEventDate(event.startDate)}</span>
                          </div>
                          <div className="event-card-detail">
                            <Clock className="event-card-detail-icon" />
                            <span>{formatEventTime(event.startDate)}</span>
                          </div>
                          <div className="event-card-detail">
                            <MapPin className="event-card-detail-icon" />
                            <span>{event.isVirtual ? 'Virtual Event' : event.location || 'Location TBD'}</span>
                          </div>
                          <div className="event-card-detail">
                            <Users className="event-card-detail-icon" />
                            <span>{getSpotsRemaining(event)} spots remaining</span>
                          </div>
                        </div>
                        {event.organizationName && (
                          <div className="event-card-organization">
                            <strong>Organized by:</strong> {event.organizationName}
                          </div>
                        )}
                        <div className="event-card-stats">
                          <div className="event-card-stat">
                            <span className="event-card-stat-number">{event.currentVolunteers || 0}</span>
                            <span className="event-card-stat-label">Volunteers</span>
                          </div>
                          <div className="event-card-stat">
                            <span className="event-card-stat-number">{event.duration || 'TBD'}</span>
                            <span className="event-card-stat-label">Duration (hrs)</span>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div className="event-card-footer">
                      <button className="event-card-btn primary">
                        View Details
                        <ExternalLink className="event-card-btn-icon" />
                      </button>
                      <button className="event-card-btn secondary">
                        Sign Up
                      </button>
                    </div>
                  </div>
                ))}
              </div>

              {/* Pagination */}
              {totalPages > 1 && (
                <div className="events-pagination">
                  <button
                    onClick={() => handlePageChange(currentPage - 1)}
                    disabled={currentPage === 1}
                    className="events-pagination-btn events-pagination-prev"
                  >
                    <ChevronLeft className="events-pagination-icon" />
                    Previous
                  </button>

                  <div className="events-pagination-numbers">
                    {getPageNumbers().map((pageNum, index) => (
                      <button
                        key={index}
                        onClick={() => typeof pageNum === 'number' ? handlePageChange(pageNum) : null}
                        className={`events-pagination-number ${
                          pageNum === currentPage ? 'active' : ''
                        } ${typeof pageNum !== 'number' ? 'dots' : ''}`}
                        disabled={typeof pageNum !== 'number'}
                      >
                        {pageNum}
                      </button>
                    ))}
                  </div>

                  <button
                    onClick={() => handlePageChange(currentPage + 1)}
                    disabled={currentPage === totalPages}
                    className="events-pagination-btn events-pagination-next"
                  >
                    Next
                    <ChevronRight className="events-pagination-icon" />
                  </button>
                </div>
              )}
            </>
          )}

          {/* Empty State */}
          {!loading && !error && filteredEvents.length === 0 && (
            <div className="events-empty-state">
              <div className="events-empty-state-icon">
                <Calendar />
              </div>
              {events.length === 0 ? (
                <>
                  <h3 className="events-empty-state-title">
                    Events Coming Soon!
                  </h3>
                  <p className="events-empty-state-description">
                    We're working hard to bring you amazing volunteer opportunities. 
                    Check back soon to discover events that match your interests and schedule.
                  </p>
                </>
              ) : (
                <>
                  <h3 className="events-empty-state-title">
                    No Events Match Your Filters
                  </h3>
                  <p className="events-empty-state-description">
                    Try adjusting your search criteria or clearing some filters to see more events.
                  </p>
                  <button onClick={clearAllFilters} className="events-clear-filters-btn">
                    Clear All Filters
                  </button>
                </>
              )}
              <div className="events-empty-state-cta">
                <p className="events-empty-state-cta-text">
                  <strong>Are you an organization?</strong> Create and post volunteer events 
                  to connect with passionate volunteers in your community.
                </p>
                <button className="events-empty-state-cta-button">
                  Post an Event
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Events;