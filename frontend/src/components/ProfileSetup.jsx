import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { isLoggedIn, getCurrentUser, ensureValidToken, isTokenExpired } from '../services/authService';
import { createProfile } from '../services/profileService';

const ProfileSetup = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [user, setUser] = useState(null);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [selectedInterests, setSelectedInterests] = useState([]);
  const [formData, setFormData] = useState({
    bio: '',
    location: '',
    interests: '',
    skills: '',
    phoneNumber: '',
    // Organization-specific fields
    organizationName: '',
    organizationType: '',
    website: '',
    missionStatement: '',
    categories: '',
    services: '',
    // Volunteer-specific fields
    firstName: '',
    lastName: '',
    availability: 'flexible'
  });

  // Interest/Cause options based on your enum
  const interestOptions = [
    { value: 'COMMUNITY_CLEANUP', label: 'Community Cleanup', emoji: '🧹' },
    { value: 'FOOD_SERVICE', label: 'Food Service', emoji: '🍽️' },
    { value: 'TUTORING_EDUCATION', label: 'Tutoring & Education', emoji: '📚' },
    { value: 'ANIMAL_CARE', label: 'Animal Care', emoji: '🐾' },
    { value: 'ENVIRONMENTAL_CONSERVATION', label: 'Environmental Conservation', emoji: '🌱' },
    { value: 'SENIOR_SUPPORT', label: 'Senior Support', emoji: '👴' },
    { value: 'YOUTH_MENTORING', label: 'Youth Mentoring', emoji: '👥' },
    { value: 'HEALTHCARE_SUPPORT', label: 'Healthcare Support', emoji: '🏥' },
    { value: 'ARTS_CULTURE', label: 'Arts & Culture', emoji: '🎨' },
    { value: 'TECHNOLOGY_DIGITAL', label: 'Technology & Digital', emoji: '💻' },
    { value: 'DISASTER_RELIEF', label: 'Disaster Relief', emoji: '🚑' },
    { value: 'COMMUNITY_BUILDING', label: 'Community Building', emoji: '🏘️' },
    { value: 'OTHER', label: 'Other', emoji: '📋' }
  ];

  useEffect(() => {
    // Check authentication first
    if (!isLoggedIn()) {
      navigate('/login');
      return;
    }

    // Check if token is expired
    if (isTokenExpired()) {
      console.log('Token expired, redirecting to login');
      localStorage.removeItem('authToken');
      localStorage.removeItem('user');
      navigate('/login');
      return;
    }

    // Get current user data and pre-populate form
    const currentUser = getCurrentUser();
    setUser(currentUser);

    // Pre-populate form with existing user data
    if (currentUser) {
      setFormData(prev => ({
        ...prev,
        firstName: currentUser.firstName || '',
        lastName: currentUser.lastName || '',
        organizationName: currentUser.organizationName || '',
        // Keep other fields as empty for user to fill
      }));
    }
  }, [navigate]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Clear error when user starts typing
    if (error) setError('');
  };

  const handleInterestToggle = (interestValue) => {
    setSelectedInterests(prev => {
      if (prev.includes(interestValue)) {
        // Remove interest
        return prev.filter(interest => interest !== interestValue);
      } else {
        // Add interest
        return [...prev, interestValue];
      }
    });
    
    // Clear error when user selects interests
    if (error) setError('');
  };

  const validateForm = () => {
    if (!user) return false;

    // Basic validation
    if (!formData.bio.trim()) {
      setError('Please tell us about yourself');
      return false;
    }

    if (!formData.location.trim()) {
      setError('Please provide your location');
      return false;
    }

    // User type specific validation
    if (user.userType === 'VOLUNTEER') {
      if (selectedInterests.length === 0) {
        setError('Please select at least one interest or cause');
        return false;
      }
    } else if (user.userType === 'ORGANIZATION') {
      if (!formData.categories.trim()) {
        setError('Please specify your organization focus areas');
        return false;
      }
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      // Ensure token is valid before making request
      await ensureValidToken();

      // Validate form
      if (!validateForm()) {
        setLoading(false);
        return;
      }

      // Prepare form data with interests formatted as comma-separated string
      const submissionData = {
        ...formData,
        interests: selectedInterests.join(',') // Convert array to comma-separated string
      };

      console.log('=== DETAILED SUBMISSION DEBUG ===');
      console.log('Selected interests array:', selectedInterests);
      console.log('Interests as string:', selectedInterests.join(','));
      console.log('Current user:', user);
      console.log('User type:', user?.userType);
      console.log('Form data before submission:', formData);
      console.log('Final submission data:', submissionData);
      console.log('Auth token exists:', !!localStorage.getItem('authToken'));
      console.log('Auth token preview:', localStorage.getItem('authToken')?.substring(0, 20) + '...');

      // Create profile using the service
      const result = await createProfile(submissionData);

      console.log('=== PROFILE CREATION RESULT ===');
      console.log('Result:', result);

      if (result.success) {
        setSuccess('Profile created successfully! Redirecting...');
        
        // Wait a moment to show success message, then redirect
        setTimeout(() => {
          navigate('/dashboard');
        }, 2000);
      } else {
        console.error('Profile creation failed:', result);
        setError(result.message || 'Failed to create profile. Please try again.');
      }

    } catch (error) {
      console.error('=== PROFILE SETUP CATCH ERROR ===');
      console.error('Error object:', error);
      console.error('Error message:', error.message);
      
      if (error.message.includes('Token expired') || error.message.includes('not authenticated')) {
        setError('Your session has expired. Please log in again.');
        setTimeout(() => {
          navigate('/login');
        }, 2000);
      } else {
        setError('Something went wrong. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleSkip = async () => {
    try {
      // Ensure token is valid before making request
      await ensureValidToken();

      // Send minimal/null data to backend to mark profile as "skipped"
      const skipData = {
        bio: '', // Empty string instead of null
        location: '',
        interests: '',
        skills: '',
        phoneNumber: '',
        // Add user type specific empty fields
        ...(user?.userType === 'VOLUNTEER' ? {
          firstName: user?.firstName || '',
          lastName: user?.lastName || '',
          availability: 'flexible'
        } : {}),
        ...(user?.userType === 'ORGANIZATION' ? {
          organizationName: user?.organizationName || '',
          organizationType: '',
          website: '',
          missionStatement: '',
          categories: '',
          services: ''
        } : {})
      };

      console.log('Skipping profile setup with minimal data:', skipData);

      // Create profile with empty/minimal data
      const result = await createProfile(skipData);
      
      if (result.success) {
        navigate('/dashboard');
      } else {
        console.error('Skip profile failed:', result);
        // Just navigate anyway if skip fails
        navigate('/dashboard');
      }
    } catch (error) {
      console.error('Skip profile error:', error);
      
      if (error.message.includes('Token expired') || error.message.includes('not authenticated')) {
        navigate('/login');
      } else {
        // Just navigate to dashboard if skip fails for other reasons
        navigate('/dashboard');
      }
    }
  };

  if (!user) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        minHeight: '50vh' 
      }}>
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '1rem'
        }}>
          <div style={{
            width: '32px',
            height: '32px',
            border: '3px solid #e5e7eb',
            borderTop: '3px solid #10b981',
            borderRadius: '50%',
            animation: 'spin 1s linear infinite'
          }}></div>
          <span style={{ color: '#6b7280' }}>Loading...</span>
        </div>
      </div>
    );
  }

  return (
    <div style={{
      minHeight: '80vh',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      padding: '2rem',
      background: 'linear-gradient(135deg, #f0f9ff 0%, #ffffff 100%)'
    }}>
      <div style={{
        background: 'white',
        borderRadius: '16px',
        boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)',
        padding: '3rem',
        width: '100%',
        maxWidth: '700px'
      }}>
        {/* Header */}
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <h1 style={{
            fontSize: '2.25rem',
            fontWeight: '700',
            color: '#1f2937',
            marginBottom: '0.5rem'
          }}>
            Complete Your Profile
          </h1>
          <p style={{
            fontSize: '1.125rem',
            color: '#6b7280',
            marginBottom: '0.5rem'
          }}>
            Welcome, {user.firstName || user.organizationName || user.email}!
          </p>
          <p style={{
            fontSize: '1rem',
            color: '#6b7280'
          }}>
            Help us personalize your experience by completing your profile.
          </p>
        </div>

        {/* Error/Success Messages */}
        {error && (
          <div style={{
            background: '#fef2f2',
            border: '1px solid #fecaca',
            color: '#dc2626',
            padding: '0.75rem 1rem',
            borderRadius: '8px',
            marginBottom: '1.5rem',
            fontSize: '0.875rem'
          }}>
            {error}
          </div>
        )}

        {success && (
          <div style={{
            background: '#f0fdf4',
            border: '1px solid #bbf7d0',
            color: '#166534',
            padding: '0.75rem 1rem',
            borderRadius: '8px',
            marginBottom: '1.5rem',
            fontSize: '0.875rem'
          }}>
            {success}
          </div>
        )}

        {/* Form */}
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          {/* Bio */}
          <div>
            <label style={{
              display: 'block',
              fontSize: '0.875rem',
              fontWeight: '600',
              color: '#374151',
              marginBottom: '0.5rem'
            }}>
              Tell us about yourself *
            </label>
            <textarea
              name="bio"
              value={formData.bio}
              onChange={handleInputChange}
              placeholder={user.userType === 'VOLUNTEER' 
                ? "Share your interests, passions, and what motivates you to volunteer..."
                : "Describe your organization's mission and the impact you're making..."
              }
              rows={4}
              required
              style={{
                width: '100%',
                padding: '0.75rem',
                border: '1px solid #d1d5db',
                borderRadius: '8px',
                fontSize: '1rem',
                resize: 'vertical',
                fontFamily: 'inherit'
              }}
            />
          </div>

          {/* Location */}
          <div>
            <label style={{
              display: 'block',
              fontSize: '0.875rem',
              fontWeight: '600',
              color: '#374151',
              marginBottom: '0.5rem'
            }}>
              Location *
            </label>
            <input
              type="text"
              name="location"
              value={formData.location}
              onChange={handleInputChange}
              placeholder="City, Country (e.g., San Francisco, USA)"
              required
              style={{
                width: '100%',
                padding: '0.75rem',
                border: '1px solid #d1d5db',
                borderRadius: '8px',
                fontSize: '1rem'
              }}
            />
          </div>

          {/* Conditional fields based on user type */}
          {user.userType === 'VOLUNTEER' ? (
            <>
              {/* Interests & Causes with Tags */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '0.875rem',
                  fontWeight: '600',
                  color: '#374151',
                  marginBottom: '0.5rem'
                }}>
                  Interests & Causes *
                </label>
                <p style={{
                  fontSize: '0.75rem',
                  color: '#6b7280',
                  marginBottom: '0.75rem'
                }}>
                  Select the causes you're passionate about (choose one or more):
                </p>
                <div style={{
                  display: 'flex',
                  flexWrap: 'wrap',
                  gap: '0.5rem',
                  padding: '0.75rem',
                  border: '1px solid #d1d5db',
                  borderRadius: '8px',
                  backgroundColor: '#f9fafb',
                  minHeight: '3rem'
                }}>
                  {interestOptions.map((interest) => {
                    const isSelected = selectedInterests.includes(interest.value);
                    return (
                      <button
                        key={interest.value}
                        type="button"
                        onClick={() => handleInterestToggle(interest.value)}
                        className={`interest-tag ${isSelected ? 'selected' : ''}`}
                        style={{
                          display: 'inline-flex',
                          alignItems: 'center',
                          gap: '0.375rem',
                          padding: '0.5rem 0.75rem',
                          border: isSelected 
                            ? '2px solid #10b981' 
                            : '1px solid #d1d5db',
                          borderRadius: '20px',
                          fontSize: '0.875rem',
                          fontWeight: '500',
                          backgroundColor: isSelected 
                            ? '#10b981' 
                            : 'white',
                          color: isSelected 
                            ? 'white' 
                            : '#374151',
                          cursor: 'pointer',
                          transition: 'all 0.2s ease',
                          outline: 'none'
                        }}
                      >
                        <span>{interest.emoji}</span>
                        <span>{interest.label}</span>
                      </button>
                    );
                  })}
                </div>
                {selectedInterests.length > 0 && (
                  <p style={{
                    fontSize: '0.75rem',
                    color: '#10b981',
                    marginTop: '0.5rem'
                  }}>
                    Selected: {selectedInterests.length} cause{selectedInterests.length !== 1 ? 's' : ''}
                  </p>
                )}
              </div>

              {/* Skills */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '0.875rem',
                  fontWeight: '600',
                  color: '#374151',
                  marginBottom: '0.5rem'
                }}>
                  Skills & Abilities
                </label>
                <input
                  type="text"
                  name="skills"
                  value={formData.skills}
                  onChange={handleInputChange}
                  placeholder="e.g., Communication, Teaching, Event Planning, Photography"
                  style={{
                    width: '100%',
                    padding: '0.75rem',
                    border: '1px solid #d1d5db',
                    borderRadius: '8px',
                    fontSize: '1rem'
                  }}
                />
              </div>

              {/* Availability */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '0.875rem',
                  fontWeight: '600',
                  color: '#374151',
                  marginBottom: '0.5rem'
                }}>
                  Availability Preference
                </label>
                <select
                  name="availability"
                  value={formData.availability}
                  onChange={handleInputChange}
                  style={{
                    width: '100%',
                    padding: '0.75rem',
                    border: '1px solid #d1d5db',
                    borderRadius: '8px',
                    fontSize: '1rem',
                    backgroundColor: 'white'
                  }}
                >
                  <option value="flexible">Flexible</option>
                  <option value="weekdays">Weekdays Only</option>
                  <option value="weekends">Weekends Only</option>
                  <option value="evenings">Evenings</option>
                  <option value="mornings">Mornings</option>
                </select>
              </div>
            </>
          ) : (
            /* Organization-specific fields */
            <>
              {/* Organization Focus Areas */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '0.875rem',
                  fontWeight: '600',
                  color: '#374151',
                  marginBottom: '0.5rem'
                }}>
                  Organization Focus Areas *
                </label>
                <input
                  type="text"
                  name="categories"
                  value={formData.categories}
                  onChange={handleInputChange}
                  placeholder="e.g., Community Development, Education, Healthcare, Environment"
                  required
                  style={{
                    width: '100%',
                    padding: '0.75rem',
                    border: '1px solid #d1d5db',
                    borderRadius: '8px',
                    fontSize: '1rem'
                  }}
                />
              </div>

              {/* Organization Type */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '0.875rem',
                  fontWeight: '600',
                  color: '#374151',
                  marginBottom: '0.5rem'
                }}>
                  Organization Type
                </label>
                <select
                  name="organizationType"
                  value={formData.organizationType}
                  onChange={handleInputChange}
                  style={{
                    width: '100%',
                    padding: '0.75rem',
                    border: '1px solid #d1d5db',
                    borderRadius: '8px',
                    fontSize: '1rem',
                    backgroundColor: 'white'
                  }}
                >
                  <option value="">Select type...</option>
                  <option value="nonprofit">Non-Profit</option>
                  <option value="charity">Charity</option>
                  <option value="government">Government</option>
                  <option value="religious">Religious Organization</option>
                  <option value="educational">Educational Institution</option>
                  <option value="community">Community Group</option>
                  <option value="other">Other</option>
                </select>
              </div>

              {/* Website */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '0.875rem',
                  fontWeight: '600',
                  color: '#374151',
                  marginBottom: '0.5rem'
                }}>
                  Website
                </label>
                <input
                  type="url"
                  name="website"
                  value={formData.website}
                  onChange={handleInputChange}
                  placeholder="https://yourorganization.org"
                  style={{
                    width: '100%',
                    padding: '0.75rem',
                    border: '1px solid #d1d5db',
                    borderRadius: '8px',
                    fontSize: '1rem'
                  }}
                />
              </div>

              {/* Services */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '0.875rem',
                  fontWeight: '600',
                  color: '#374151',
                  marginBottom: '0.5rem'
                }}>
                  Services Provided
                </label>
                <input
                  type="text"
                  name="services"
                  value={formData.services}
                  onChange={handleInputChange}
                  placeholder="e.g., Food distribution, Education programs, Healthcare services"
                  style={{
                    width: '100%',
                    padding: '0.75rem',
                    border: '1px solid #d1d5db',
                    borderRadius: '8px',
                    fontSize: '1rem'
                  }}
                />
              </div>
            </>
          )}

          {/* Phone Number */}
          <div>
            <label style={{
              display: 'block',
              fontSize: '0.875rem',
              fontWeight: '600',
              color: '#374151',
              marginBottom: '0.5rem'
            }}>
              Phone Number (Optional)
            </label>
            <input
              type="tel"
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleInputChange}
              placeholder="(555) 123-4567"
              style={{
                width: '100%',
                padding: '0.75rem',
                border: '1px solid #d1d5db',
                borderRadius: '8px',
                fontSize: '1rem'
              }}
            />
          </div>

          {/* Buttons */}
          <div style={{
            display: 'flex',
            gap: '1rem',
            marginTop: '1.5rem',
            flexDirection: 'column'
          }}>
            <button
              type="submit"
              disabled={loading}
              style={{
                width: '100%',
                backgroundColor: loading ? '#9ca3af' : '#10b981',
                color: 'white',
                padding: '1rem 1.5rem',
                borderRadius: '8px',
                border: 'none',
                fontSize: '1rem',
                fontWeight: '600',
                cursor: loading ? 'not-allowed' : 'pointer',
                transition: 'background-color 0.2s',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '0.5rem'
              }}
            >
              {loading && (
                <div style={{
                  width: '16px',
                  height: '16px',
                  border: '2px solid transparent',
                  borderTop: '2px solid white',
                  borderRadius: '50%',
                  animation: 'spin 1s linear infinite'
                }}></div>
              )}
              {loading ? 'Creating Profile...' : 'Complete Profile'}
            </button>

            <button
              type="button"
              onClick={handleSkip}
              disabled={loading}
              style={{
                width: '100%',
                backgroundColor: 'transparent',
                color: '#6b7280',
                padding: '1rem 1.5rem',
                borderRadius: '8px',
                border: '1px solid #d1d5db',
                fontSize: '1rem',
                fontWeight: '500',
                cursor: loading ? 'not-allowed' : 'pointer',
                transition: 'all 0.2s'
              }}
              onMouseOver={(e) => {
                if (!loading) {
                  e.target.style.backgroundColor = '#f9fafb';
                  e.target.style.borderColor = '#9ca3af';
                }
              }}
              onMouseOut={(e) => {
                if (!loading) {
                  e.target.style.backgroundColor = 'transparent';
                  e.target.style.borderColor = '#d1d5db';
                }
              }}
            >
              Skip for now
            </button>
          </div>
        </form>

        {/* Footer note */}
        <p style={{
          textAlign: 'center',
          fontSize: '0.875rem',
          color: '#9ca3af',
          marginTop: '1.5rem',
          lineHeight: '1.4'
        }}>
          You can always update this information later in your profile settings.
          Fields marked with * are required.
        </p>
      </div>

      {/* Add spinner animation and hover styles */}
      <style>{`
        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }
        
        .interest-tag:not(.selected):hover {
          background-color: #f3f4f6 !important;
          border-color: #9ca3af !important;
        }
        
        .interest-tag.selected {
          background-color: #10b981 !important;
          color: white !important;
          border-color: #10b981 !important;
        }
        
        .interest-tag.selected:hover {
          background-color: #059669 !important;
          border-color: #059669 !important;
        }
      `}</style>
    </div>
  );
};

export default ProfileSetup;