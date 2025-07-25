import React, { useState, useEffect } from 'react';
import { Search, MapPin, Filter, Users } from 'lucide-react';
import './Organizations.css';

const Organizations = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategories, setSelectedCategories] = useState([]);
  const [selectedLocations, setSelectedLocations] = useState([]);
  const [selectedDatePosted, setSelectedDatePosted] = useState([]);
  const [selectedSizes, setSelectedSizes] = useState([]);
  const [showMoreCategories, setShowMoreCategories] = useState(false);
  const [showMoreLocations, setShowMoreLocations] = useState(false);

  // Organization categories
  const categories = [
    'Education', 'Environment', 'Healthcare', 'Animal Welfare', 'Community Service',
    'Human Services', 'Arts & Culture', 'Youth Development', 'Senior Services',
    'Hunger & Homelessness', 'Disaster Relief', 'International', 'Sports & Recreation',
    'Mental Health', 'Veterans', 'Women\'s Issues', 'Children & Families',
    'Disability Services', 'Religious', 'Political', 'LGBTQ+', 'Technology',
    'Research & Advocacy', 'Public Safety'
  ];

  const locations = [
    'United States', 'Canada', 'United Kingdom', 'Australia', 'Germany',
    'France', 'Netherlands', 'Sweden', 'Denmark', 'Ireland', 'Switzerland'
  ];

  const datePostedOptions = [
    'Last 24 hours', 'Last 3 days', 'Last 7 days', 'Last 14 days', 'Last 30 days'
  ];

  const organizationSizes = [
    'Small (1-50)', 'Medium (51-200)', 'Large (201-1000)', 'Enterprise (1000+)'
  ];

  const handleCategoryToggle = (category) => {
    setSelectedCategories(prev => 
      prev.includes(category) 
        ? prev.filter(c => c !== category)
        : [...prev, category]
    );
  };

  const handleLocationToggle = (location) => {
    setSelectedLocations(prev => 
      prev.includes(location) 
        ? prev.filter(l => l !== location)
        : [...prev, location]
    );
  };

  const handleDatePostedToggle = (option) => {
    setSelectedDatePosted(prev => 
      prev.includes(option) 
        ? prev.filter(d => d !== option)
        : [...prev, option]
    );
  };

  const handleSizeToggle = (size) => {
    setSelectedSizes(prev => 
      prev.includes(size) 
        ? prev.filter(s => s !== size)
        : [...prev, size]
    );
  };

  return (
    <div className="organizations-page">
      <div className="organizations-container">
        {/* Left Sidebar - Filters */}
        <div className="organizations-sidebar">
          <div className="filter-card">
            <h2 className="filter-title">
              <Filter className="w-5 h-5" />
              Filter Options
            </h2>
            
            {/* Category Filter */}
            <div className="filter-section">
              <h3 className="filter-section-title">Category</h3>
              <div className="filter-options">
                {categories.slice(0, showMoreCategories ? categories.length : 8).map((category) => (
                  <div
                    key={category}
                    className={`filter-option ${selectedCategories.includes(category) ? 'active' : ''}`}
                    onClick={() => handleCategoryToggle(category)}
                  >
                    {category}
                  </div>
                ))}
              </div>
              {categories.length > 8 && (
                <button
                  onClick={() => setShowMoreCategories(!showMoreCategories)}
                  className="show-more-btn"
                >
                  {showMoreCategories ? 'Show Less' : 'Show More'}
                </button>
              )}
            </div>

            {/* Location Filter */}
            <div className="filter-section">
              <h3 className="filter-section-title">Location</h3>
              <div className="filter-options">
                {locations.slice(0, showMoreLocations ? locations.length : 6).map((location) => (
                  <div
                    key={location}
                    className={`filter-option location ${selectedLocations.includes(location) ? 'active' : ''}`}
                    onClick={() => handleLocationToggle(location)}
                  >
                    {location}
                  </div>
                ))}
              </div>
              {locations.length > 6 && (
                <button
                  onClick={() => setShowMoreLocations(!showMoreLocations)}
                  className="show-more-btn"
                >
                  {showMoreLocations ? 'Show Less' : 'Show More'}
                </button>
              )}
            </div>

            {/* Date Posted Filter */}
            <div className="filter-section">
              <h3 className="filter-section-title">Date Updated</h3>
              <div className="filter-options">
                {datePostedOptions.map((option) => (
                  <div
                    key={option}
                    className={`filter-option date ${selectedDatePosted.includes(option) ? 'active' : ''}`}
                    onClick={() => handleDatePostedToggle(option)}
                  >
                    {option}
                  </div>
                ))}
              </div>
            </div>

            {/* Organization Size Filter */}
            <div className="filter-section">
              <h3 className="filter-section-title">Organization Size</h3>
              <div className="filter-options">
                {organizationSizes.map((size) => (
                  <div
                    key={size}
                    className={`filter-option size ${selectedSizes.includes(size) ? 'active' : ''}`}
                    onClick={() => handleSizeToggle(size)}
                  >
                    {size}
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>

        {/* Main Content */}
        <div className="organizations-main">
          {/* Header */}
          <div className="organizations-header">
            <div>
              <h1 className="organizations-title">All Organizations</h1>
              <p className="organizations-subtitle">
                Last Updated: <span className="highlight">Today</span>
              </p>
            </div>
          </div>

          {/* Search Bar */}
          <div className="search-section">
            <div className="search-input-container">
              <Search className="search-icon" />
              <input
                type="text"
                placeholder="Search organizations, causes, or locations..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="search-input"
              />
            </div>

            {/* Location Search */}
            <div className="search-input-container location-search">
              <MapPin className="search-icon" />
              <input
                type="text"
                placeholder="Search by city, state, country, or zip code..."
                className="search-input"
              />
            </div>
          </div>

          {/* Active Filters */}
          {(selectedCategories.length > 0 || selectedLocations.length > 0 || selectedDatePosted.length > 0 || selectedSizes.length > 0) && (
            <div className="active-filters">
              <div className="active-filters-content">
                <Filter className="active-filters-icon" />
                <span className="active-filters-label">Active Filters:</span>
                
                {selectedCategories.map((category) => (
                  <span
                    key={category}
                    className="filter-chip category"
                  >
                    {category}
                    <button
                      onClick={() => handleCategoryToggle(category)}
                      className="filter-chip-remove"
                    >
                      ×
                    </button>
                  </span>
                ))}

                {selectedLocations.map((location) => (
                  <span
                    key={location}
                    className="filter-chip location"
                  >
                    {location}
                    <button
                      onClick={() => handleLocationToggle(location)}
                      className="filter-chip-remove"
                    >
                      ×
                    </button>
                  </span>
                ))}

                {selectedDatePosted.map((date) => (
                  <span
                    key={date}
                    className="filter-chip date"
                  >
                    {date}
                    <button
                      onClick={() => handleDatePostedToggle(date)}
                      className="filter-chip-remove"
                    >
                      ×
                    </button>
                  </span>
                ))}

                {selectedSizes.map((size) => (
                  <span
                    key={size}
                    className="filter-chip size"
                  >
                    {size}
                    <button
                      onClick={() => handleSizeToggle(size)}
                      className="filter-chip-remove"
                    >
                      ×
                    </button>
                  </span>
                ))}
              </div>
            </div>
          )}

          {/* Empty State */}
          <div className="empty-state">
            <div className="empty-state-icon">
              <Users />
            </div>
            <h3 className="empty-state-title">
              Organizations Coming Soon!
            </h3>
            <p className="empty-state-description">
              We're working hard to partner with amazing organizations in your area. 
              Check back soon to discover volunteer opportunities that match your interests.
            </p>
            <div className="empty-state-cta">
              <p className="empty-state-cta-text">
                <strong>Are you an organization?</strong> Join our platform to connect with passionate volunteers 
                and make a greater impact in your community.
              </p>
              <button className="empty-state-cta-button">
                Register Your Organization
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Organizations;