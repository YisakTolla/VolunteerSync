import React, { useState, useEffect } from 'react';
import { Search, MapPin, Filter, Calendar, Clock, Users } from 'lucide-react';
import './Events.css';

const Events = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedEventTypes, setSelectedEventTypes] = useState([]);
  const [selectedLocations, setSelectedLocations] = useState([]);
  const [selectedDates, setSelectedDates] = useState([]);
  const [selectedTimes, setSelectedTimes] = useState([]);
  const [selectedDurations, setSelectedDurations] = useState([]);
  const [selectedSkillLevels, setSelectedSkillLevels] = useState([]);
  const [showMoreEventTypes, setShowMoreEventTypes] = useState(false);
  const [showMoreLocations, setShowMoreLocations] = useState(false);

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
  };

  const hasActiveFilters = selectedEventTypes.length > 0 || selectedLocations.length > 0 || 
                         selectedDates.length > 0 || selectedTimes.length > 0 || 
                         selectedDurations.length > 0 || selectedSkillLevels.length > 0;

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
            <div>
              <h1 className="events-title">Volunteer Events</h1>
              <p className="events-subtitle">
                Last Updated: <span className="events-highlight">Today</span>
              </p>
            </div>
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
            <div className="events-search-input-container events-location-search">
              <MapPin className="events-search-icon" />
              <input
                type="text"
                placeholder="Search by city, state, or zip code..."
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
                  <span key={type} className="events-filter-chip type">
                    {type}
                    <button
                      onClick={() => handleEventTypeToggle(type)}
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

          {/* Empty State */}
          <div className="events-empty-state">
            <div className="events-empty-state-icon">
              <Calendar />
            </div>
            <h3 className="events-empty-state-title">
              Events Coming Soon!
            </h3>
            <p className="events-empty-state-description">
              We're working with organizations to bring you meaningful volunteer opportunities. 
              Check back soon to discover events that match your interests and schedule.
            </p>
            <div className="events-empty-state-cta">
              <p className="events-empty-state-cta-text">
                <strong>Want to host an event?</strong> Partner with us to create volunteer opportunities 
                that make a real difference in your community.
              </p>
              <button className="events-empty-state-cta-button">
                Create an Event
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Events;