import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getCurrentUser } from '../services/authService';
import { createEvent } from '../services/createEventService';
import './CreateEvent.css';

const CreateEvent = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Form state
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    eventType: '',
    skillLevelRequired: 'NO_EXPERIENCE_REQUIRED',
    durationCategory: 'SHORT',
    startDate: '',
    startTime: '',
    endDate: '',
    endTime: '',
    location: '',
    address: '',
    city: '',
    state: '',
    zipCode: '',
    maxVolunteers: '',
    estimatedHours: '',
    requirements: '',
    contactEmail: '',
    contactPhone: '',
    imageUrl: '',
    isVirtual: false,
    virtualMeetingLink: '',
    timeOfDay: '',
    isRecurring: false,
    recurrencePattern: '',
    hasFlexibleTiming: false,
    isWeekdaysOnly: false,
    isWeekendsOnly: false
  });

  useEffect(() => {
    const currentUser = getCurrentUser();
    if (!currentUser) {
      navigate('/login');
      return;
    }
    
    if (currentUser.userType !== 'ORGANIZATION') {
      navigate('/dashboard');
      return;
    }
    
    setUser(currentUser);
    setFormData(prev => ({
      ...prev,
      contactEmail: currentUser.email || ''
    }));
  }, [navigate]);

  // Event types with icons and colors - matches the backend enum
  const eventTypes = [
    { value: 'COMMUNITY_CLEANUP', label: 'Community Cleanup', icon: '🧹', category: 'environment' },
    { value: 'FOOD_SERVICE', label: 'Food Service', icon: '🍽️', category: 'animal-welfare' },
    { value: 'TUTORING_EDUCATION', label: 'Tutoring & Education', icon: '📚', category: 'education' },
    { value: 'ANIMAL_CARE', label: 'Animal Care', icon: '🐾', category: 'animal-welfare' },
    { value: 'ENVIRONMENTAL_CONSERVATION', label: 'Environmental Conservation', icon: '🌱', category: 'environment' },
    { value: 'SENIOR_SUPPORT', label: 'Senior Support', icon: '👴', category: 'human-services' },
    { value: 'YOUTH_MENTORING', label: 'Youth Mentoring', icon: '👥', category: 'youth-development' },
    { value: 'HEALTHCARE_SUPPORT', label: 'Healthcare Support', icon: '🏥', category: 'healthcare' },
    { value: 'DISASTER_RELIEF', label: 'Disaster Relief', icon: '🚑', category: 'disaster-relief' },
    { value: 'ARTS_CULTURE', label: 'Arts & Culture', icon: '🎨', category: 'arts-culture' },
    { value: 'SPORTS_RECREATION', label: 'Sports & Recreation', icon: '⚽', category: 'sports-recreation' },
    { value: 'FUNDRAISING', label: 'Fundraising', icon: '💰', category: 'community-service' },
    { value: 'ADMINISTRATIVE_SUPPORT', label: 'Administrative Support', icon: '📁', category: 'community-service' },
    { value: 'CONSTRUCTION_BUILDING', label: 'Construction & Building', icon: '🔨', category: 'community-service' },
    { value: 'TECHNOLOGY_SUPPORT', label: 'Technology Support', icon: '💻', category: 'education' },
    { value: 'EVENT_PLANNING', label: 'Event Planning', icon: '📅', category: 'arts-culture' },
    { value: 'ADVOCACY_AWARENESS', label: 'Advocacy & Awareness', icon: '📢', category: 'community-service' },
    { value: 'RESEARCH_DATA', label: 'Research & Data', icon: '📊', category: 'education' },
    { value: 'TRANSPORTATION', label: 'Transportation', icon: '🚗', category: 'human-services' },
    { value: 'GARDENING', label: 'Gardening', icon: '🌻', category: 'environment' },
    { value: 'CRISIS_SUPPORT', label: 'Crisis Support', icon: '🆘', category: 'healthcare' },
    { value: 'FESTIVAL_FAIR', label: 'Festival & Fair', icon: '🎪', category: 'arts-culture' },
    { value: 'WORKSHOP_TRAINING', label: 'Workshop & Training', icon: '🎓', category: 'education' },
    { value: 'BLOOD_DRIVE', label: 'Blood Drive', icon: '🩸', category: 'healthcare' },
    { value: 'OTHER', label: 'Other', icon: '📋', category: 'community-service' }
  ];

  // Skill levels - matches backend enum
  const skillLevels = [
    { value: 'NO_EXPERIENCE_REQUIRED', label: 'No Experience Required' },
    { value: 'BEGINNER_FRIENDLY', label: 'Beginner Friendly' },
    { value: 'SOME_EXPERIENCE_PREFERRED', label: 'Some Experience Preferred' },
    { value: 'EXPERIENCED_VOLUNTEERS', label: 'Experienced Volunteers' },
    { value: 'SPECIALIZED_SKILLS_REQUIRED', label: 'Specialized Skills Required' },
    { value: 'TRAINING_PROVIDED', label: 'Training Provided' }
  ];

  // Duration categories - matches backend enum
  const durations = [
    { value: 'SHORT', label: '1-2 Hours' },
    { value: 'MEDIUM', label: '3-4 Hours' },
    { value: 'FULL_DAY', label: '5-8 Hours (Full Day)' },
    { value: 'MULTI_DAY', label: 'Multi-Day Event' },
    { value: 'WEEKLY_COMMITMENT', label: 'Weekly Commitment' },
    { value: 'MONTHLY_COMMITMENT', label: 'Monthly Commitment' },
    { value: 'ONGOING_LONG_TERM', label: 'Ongoing/Long-term' }
  ];

  // Time of day options
  const timeOfDayOptions = [
    { value: 'MORNING', label: 'Morning (6AM - 12PM)' },
    { value: 'AFTERNOON', label: 'Afternoon (12PM - 6PM)' },
    { value: 'EVENING', label: 'Evening (6PM - 12AM)' }
  ];

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      // Enhanced validation
      if (!formData.title.trim()) {
        throw new Error('Event title is required');
      }
      if (!formData.description.trim()) {
        throw new Error('Event description is required');
      }
      if (!formData.eventType) {
        throw new Error('Event type is required');
      }
      if (!formData.startDate || !formData.startTime) {
        throw new Error('Start date and time are required');
      }
      if (!formData.isVirtual && !formData.location.trim()) {
        throw new Error('Location is required for in-person events');
      }
      if (formData.isVirtual && !formData.virtualMeetingLink.trim()) {
        throw new Error('Virtual meeting link is required for virtual events');
      }

      // Validate date format and ensure end date is after start date
      const startDateTime = new Date(`${formData.startDate}T${formData.startTime}:00`);
      const endDateTime = formData.endDate && formData.endTime 
        ? new Date(`${formData.endDate}T${formData.endTime}:00`)
        : new Date(startDateTime.getTime() + (2 * 60 * 60 * 1000)); // Default to 2 hours later

      if (isNaN(startDateTime.getTime())) {
        throw new Error('Invalid start date/time');
      }

      if (endDateTime <= startDateTime) {
        throw new Error('End date/time must be after start date/time');
      }

      // Format dates for backend (ensure proper ISO format with timezone)
      const formattedStartDate = startDateTime.toISOString();
      const formattedEndDate = endDateTime.toISOString();

      console.log('📅 Date formatting:', {
        original: { startDate: formData.startDate, startTime: formData.startTime },
        parsed: { startDateTime, endDateTime },
        formatted: { formattedStartDate, formattedEndDate }
      });

      // Prepare event data for backend
      const eventData = {
        title: formData.title.trim(),
        description: formData.description.trim(),
        eventType: formData.eventType,
        skillLevelRequired: formData.skillLevelRequired,
        durationCategory: formData.durationCategory,
        startDate: formattedStartDate,
        endDate: formattedEndDate,
        location: formData.location.trim(),
        address: formData.address.trim(),
        city: formData.city.trim(),
        state: formData.state.trim(),
        zipCode: formData.zipCode.trim(),
        maxVolunteers: formData.maxVolunteers ? parseInt(formData.maxVolunteers) : null,
        estimatedHours: formData.estimatedHours ? parseInt(formData.estimatedHours) : null,
        requirements: formData.requirements.trim(),
        contactEmail: formData.contactEmail.trim() || user.email,
        contactPhone: formData.contactPhone.trim(),
        isVirtual: formData.isVirtual,
        virtualMeetingLink: formData.virtualMeetingLink.trim(),
        timeOfDay: formData.timeOfDay,
        isRecurring: formData.isRecurring,
        recurrencePattern: formData.recurrencePattern,
        hasFlexibleTiming: formData.hasFlexibleTiming,
        isWeekdaysOnly: formData.isWeekdaysOnly,
        isWeekendsOnly: formData.isWeekendsOnly
      };

      console.log('📝 Submitting event data:', eventData);

      const result = await createEvent(eventData);
      
      if (result.success) {
        setSuccess('Event created successfully!');
        setTimeout(() => {
          navigate('/organization/events');
        }, 2000);
      } else {
        setError(result.message || 'Failed to create event');
      }
    } catch (err) {
      setError(err.message || 'An error occurred while creating the event');
    } finally {
      setLoading(false);
    }
  };

  if (!user) {
    return (
      <div className="create-event-loading">
        <div className="loading-spinner"></div>
        <p>Loading...</p>
      </div>
    );
  }

  return (
    <div className="create-event-page">
      <div className="create-event-container">
        <div className="create-event-header">
          <button 
            onClick={() => navigate('/organization/events')} 
            className="create-event-back-btn"
          >
            ← Back to Events
          </button>
          <h1>Create New Event</h1>
          <p>Post a volunteer opportunity for your organization</p>
        </div>

        <form onSubmit={handleSubmit} className="create-event-form">
          {error && (
            <div className="create-event-error">
              <span>⚠️ {error}</span>
            </div>
          )}

          {success && (
            <div className="create-event-success">
              <span>✅ {success}</span>
            </div>
          )}

          {/* Basic Information */}
          <div className="form-section">
            <h2>Basic Information</h2>
            
            <div className="form-row">
              <div className="form-field full-width">
                <label htmlFor="title">Event Title *</label>
                <input
                  type="text"
                  id="title"
                  name="title"
                  value={formData.title}
                  onChange={handleChange}
                  placeholder="Enter event title"
                  required
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-field full-width">
                <label htmlFor="description">Description *</label>
                <textarea
                  id="description"
                  name="description"
                  value={formData.description}
                  onChange={handleChange}
                  placeholder="Describe your event, what volunteers will do, and why it matters"
                  rows="6"
                  required
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-field">
                <label htmlFor="eventType">Event Type *</label>
                <div className="event-type-grid">
                  {eventTypes.map(type => (
                    <div
                      key={type.value}
                      className={`event-type-option ${type.category} ${
                        formData.eventType === type.value ? 'selected' : ''
                      }`}
                      onClick={() => setFormData(prev => ({ ...prev, eventType: type.value }))}
                    >
                      <span className="event-type-icon">{type.icon}</span>
                      <span className="event-type-label">{type.label}</span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>

          {/* Date & Time */}
          <div className="form-section">
            <h2>Date & Time</h2>
            
            <div className="form-row">
              <div className="form-field">
                <label htmlFor="startDate">Start Date *</label>
                <input
                  type="date"
                  id="startDate"
                  name="startDate"
                  value={formData.startDate}
                  onChange={handleChange}
                  required
                />
              </div>
              <div className="form-field">
                <label htmlFor="startTime">Start Time *</label>
                <input
                  type="time"
                  id="startTime"
                  name="startTime"
                  value={formData.startTime}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-field">
                <label htmlFor="endDate">End Date</label>
                <input
                  type="date"
                  id="endDate"
                  name="endDate"
                  value={formData.endDate}
                  onChange={handleChange}
                />
              </div>
              <div className="form-field">
                <label htmlFor="endTime">End Time</label>
                <input
                  type="time"
                  id="endTime"
                  name="endTime"
                  value={formData.endTime}
                  onChange={handleChange}
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-field">
                <label htmlFor="durationCategory">Duration Category</label>
                <select
                  id="durationCategory"
                  name="durationCategory"
                  value={formData.durationCategory}
                  onChange={handleChange}
                >
                  {durations.map(duration => (
                    <option key={duration.value} value={duration.value}>
                      {duration.label}
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-field">
                <label htmlFor="timeOfDay">Time of Day</label>
                <select
                  id="timeOfDay"
                  name="timeOfDay"
                  value={formData.timeOfDay}
                  onChange={handleChange}
                >
                  <option value="">Select time of day</option>
                  {timeOfDayOptions.map(time => (
                    <option key={time.value} value={time.value}>
                      {time.label}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="form-row">
              <div className="form-field checkbox-group">
                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    name="hasFlexibleTiming"
                    checked={formData.hasFlexibleTiming}
                    onChange={handleChange}
                  />
                  Has flexible timing
                </label>
                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    name="isRecurring"
                    checked={formData.isRecurring}
                    onChange={handleChange}
                  />
                  Recurring event
                </label>
              </div>
            </div>

            {formData.isRecurring && (
              <div className="form-row">
                <div className="form-field">
                  <label htmlFor="recurrencePattern">Recurrence Pattern</label>
                  <select
                    id="recurrencePattern"
                    name="recurrencePattern"
                    value={formData.recurrencePattern}
                    onChange={handleChange}
                  >
                    <option value="">Select pattern</option>
                    <option value="WEEKLY">Weekly</option>
                    <option value="BIWEEKLY">Bi-weekly</option>
                    <option value="MONTHLY">Monthly</option>
                    <option value="QUARTERLY">Quarterly</option>
                  </select>
                </div>
              </div>
            )}

            <div className="form-row">
              <div className="form-field checkbox-group">
                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    name="isWeekdaysOnly"
                    checked={formData.isWeekdaysOnly}
                    onChange={handleChange}
                  />
                  Weekdays only
                </label>
                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    name="isWeekendsOnly"
                    checked={formData.isWeekendsOnly}
                    onChange={handleChange}
                  />
                  Weekends only
                </label>
              </div>
            </div>
          </div>

          {/* Location */}
          <div className="form-section">
            <h2>Location</h2>
            
            <div className="form-row">
              <div className="form-field full-width">
                <label className="checkbox-label virtual-toggle">
                  <input
                    type="checkbox"
                    name="isVirtual"
                    checked={formData.isVirtual}
                    onChange={handleChange}
                  />
                  <span className="virtual-icon">💻</span>
                  This is a virtual/remote event
                </label>
              </div>
            </div>

            {formData.isVirtual ? (
              <div className="form-row">
                <div className="form-field full-width">
                  <label htmlFor="virtualMeetingLink">Virtual Meeting Link *</label>
                  <input
                    type="url"
                    id="virtualMeetingLink"
                    name="virtualMeetingLink"
                    value={formData.virtualMeetingLink}
                    onChange={handleChange}
                    placeholder="https://zoom.us/j/123456789 or https://meet.google.com/abc-def-ghi"
                  />
                </div>
              </div>
            ) : (
              <>
                <div className="form-row">
                  <div className="form-field full-width">
                    <label htmlFor="location">Venue/Location Name *</label>
                    <input
                      type="text"
                      id="location"
                      name="location"
                      value={formData.location}
                      onChange={handleChange}
                      placeholder="Community Center, Park, School, etc."
                    />
                  </div>
                </div>

                <div className="form-row">
                  <div className="form-field full-width">
                    <label htmlFor="address">Street Address</label>
                    <input
                      type="text"
                      id="address"
                      name="address"
                      value={formData.address}
                      onChange={handleChange}
                      placeholder="123 Main Street"
                    />
                  </div>
                </div>

                <div className="form-row">
                  <div className="form-field">
                    <label htmlFor="city">City</label>
                    <input
                      type="text"
                      id="city"
                      name="city"
                      value={formData.city}
                      onChange={handleChange}
                      placeholder="City"
                    />
                  </div>
                  <div className="form-field">
                    <label htmlFor="state">State</label>
                    <input
                      type="text"
                      id="state"
                      name="state"
                      value={formData.state}
                      onChange={handleChange}
                      placeholder="State"
                    />
                  </div>
                  <div className="form-field">
                    <label htmlFor="zipCode">ZIP Code</label>
                    <input
                      type="text"
                      id="zipCode"
                      name="zipCode"
                      value={formData.zipCode}
                      onChange={handleChange}
                      placeholder="12345"
                    />
                  </div>
                </div>
              </>
            )}
          </div>

          {/* Requirements & Details */}
          <div className="form-section">
            <h2>Requirements & Details</h2>
            
            <div className="form-row">
              <div className="form-field">
                <label htmlFor="skillLevelRequired">Skill Level Required</label>
                <select
                  id="skillLevelRequired"
                  name="skillLevelRequired"
                  value={formData.skillLevelRequired}
                  onChange={handleChange}
                >
                  {skillLevels.map(skill => (
                    <option key={skill.value} value={skill.value}>
                      {skill.label}
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-field">
                <label htmlFor="estimatedHours">Estimated Hours</label>
                <input
                  type="number"
                  id="estimatedHours"
                  name="estimatedHours"
                  value={formData.estimatedHours}
                  onChange={handleChange}
                  min="1"
                  max="168"
                  placeholder="4"
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-field">
                <label htmlFor="maxVolunteers">Maximum Volunteers</label>
                <input
                  type="number"
                  id="maxVolunteers"
                  name="maxVolunteers"
                  value={formData.maxVolunteers}
                  onChange={handleChange}
                  min="1"
                  placeholder="10"
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-field full-width">
                <label htmlFor="requirements">Requirements & Instructions</label>
                <textarea
                  id="requirements"
                  name="requirements"
                  value={formData.requirements}
                  onChange={handleChange}
                  placeholder="Any special requirements, what to bring, dress code, age restrictions, etc."
                  rows="4"
                />
              </div>
            </div>
          </div>

          {/* Contact Information */}
          <div className="form-section">
            <h2>Contact Information</h2>
            
            <div className="form-row">
              <div className="form-field">
                <label htmlFor="contactEmail">Contact Email</label>
                <input
                  type="email"
                  id="contactEmail"
                  name="contactEmail"
                  value={formData.contactEmail}
                  onChange={handleChange}
                  placeholder="volunteer@organization.org"
                />
              </div>
              <div className="form-field">
                <label htmlFor="contactPhone">Contact Phone</label>
                <input
                  type="tel"
                  id="contactPhone"
                  name="contactPhone"
                  value={formData.contactPhone}
                  onChange={handleChange}
                  placeholder="(555) 123-4567"
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-field full-width">
                <label htmlFor="imageUrl">Event Image URL (optional)</label>
                <input
                  type="url"
                  id="imageUrl"
                  name="imageUrl"
                  value={formData.imageUrl}
                  onChange={handleChange}
                  placeholder="https://example.com/event-image.jpg"
                />
              </div>
            </div>
          </div>

          {/* Submit Buttons */}
          <div className="form-actions">
            <button 
              type="button" 
              onClick={() => navigate('/organization/events')}
              className="btn-cancel"
              disabled={loading}
            >
              Cancel
            </button>
            <button 
              type="submit" 
              className="btn-create-event"
              disabled={loading}
            >
              {loading ? 'Creating Event...' : 'Create Event'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateEvent;