import React, { useState, useEffect } from 'react';
import { getCurrentUser } from '../services/authService';
import { updateProfile, fetchMyProfile, uploadProfileImage } from '../services/profileService';

const ProfileEditor = ({ 
  onProfileUpdate, 
  showSuccessMessage = true, 
  compact = false,
  fields = 'all' // 'all', 'basic', 'contact', 'custom'
}) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(false);
  const [imageUploading, setImageUploading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [formData, setFormData] = useState({
    bio: '',
    location: '',
    phoneNumber: '',
    interests: '',
    skills: '',
    availability: '',
    // Organization fields
    organizationName: '',
    organizationType: '',
    website: '',
    missionStatement: '',
    categories: '',
    services: '',
    // Contact fields
    firstName: '',
    lastName: '',
    profileImageUrl: ''
  });

  useEffect(() => {
    loadUserProfile();
  }, []);

  const loadUserProfile = async () => {
    try {
      const currentUser = getCurrentUser();
      setUser(currentUser);

      // Try to fetch latest profile data from backend
      const result = await fetchMyProfile();
      
      if (result.success) {
        // Use backend data
        populateForm(result.data);
      } else {
        // Fallback to localStorage data
        populateForm(currentUser);
      }
    } catch (error) {
      console.error('Error loading profile:', error);
      // Fallback to localStorage
      const currentUser = getCurrentUser();
      setUser(currentUser);
      populateForm(currentUser);
    }
  };

  const populateForm = (userData) => {
    if (!userData) return;

    setFormData({
      bio: userData.bio || '',
      location: userData.location || '',
      phoneNumber: userData.phoneNumber || userData.phone || '',
      interests: userData.interests || '',
      skills: userData.skills || '',
      availability: userData.availability || 'flexible',
      organizationName: userData.organizationName || '',
      organizationType: userData.organizationType || '',
      website: userData.website || '',
      missionStatement: userData.missionStatement || userData.bio || '',
      categories: userData.categories || userData.interests || '',
      services: userData.services || '',
      firstName: userData.firstName || '',
      lastName: userData.lastName || '',
      profileImageUrl: userData.profileImageUrl || ''
    });
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Clear error when user starts typing
    if (error) setError('');
  };

  const handleImageUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    // Validate file type
    if (!file.type.startsWith('image/')) {
      setError('Please select a valid image file');
      return;
    }

    // Validate file size (5MB limit)
    if (file.size > 5 * 1024 * 1024) {
      setError('Image size must be less than 5MB');
      return;
    }

    setImageUploading(true);
    setError('');

    try {
      const result = await uploadProfileImage(file, 'profile');
      
      if (result.success) {
        setFormData(prev => ({
          ...prev,
          profileImageUrl: result.imageUrl
        }));
        
        if (showSuccessMessage) {
          setSuccess('Profile image updated successfully!');
          setTimeout(() => setSuccess(''), 3000);
        }
        
        // Notify parent component
        if (onProfileUpdate) {
          onProfileUpdate({ profileImageUrl: result.imageUrl });
        }
      } else {
        setError(result.message || 'Failed to upload image');
      }
    } catch (error) {
      setError('Failed to upload image. Please try again.');
    } finally {
      setImageUploading(false);
    }
  };

  const validateForm = () => {
    if (!user) return false;

    // Basic validation based on field set
    if (fields === 'all' || fields === 'basic') {
      if (!formData.bio.trim()) {
        setError('Bio is required');
        return false;
      }
      
      if (!formData.location.trim()) {
        setError('Location is required');
        return false;
      }
    }

    // User type specific validation
    if (user.userType === 'VOLUNTEER' && (fields === 'all' || fields === 'basic')) {
      if (!formData.interests.trim()) {
        setError('Interests are required');
        return false;
      }
    }

    if (user.userType === 'ORGANIZATION' && (fields === 'all' || fields === 'basic')) {
      if (!formData.categories.trim()) {
        setError('Focus areas are required');
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
      // Validate form
      if (!validateForm()) {
        setLoading(false);
        return;
      }

      // Update profile using the service
      const result = await updateProfile(formData);

      if (result.success) {
        if (showSuccessMessage) {
          setSuccess('Profile updated successfully!');
          setTimeout(() => setSuccess(''), 3000);
        }
        
        // Notify parent component
        if (onProfileUpdate) {
          onProfileUpdate(result.data);
        }
      } else {
        setError(result.message || 'Failed to update profile. Please try again.');
      }

    } catch (error) {
      console.error('Profile update error:', error);
      setError('Something went wrong. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const renderField = (fieldName, component) => {
    if (fields === 'all') return component;
    
    const fieldGroups = {
      basic: ['bio', 'location', 'interests', 'skills', 'categories', 'services'],
      contact: ['firstName', 'lastName', 'phoneNumber', 'website'],
      custom: [] // Define custom fields as needed
    };
    
    if (Array.isArray(fields)) {
      return fields.includes(fieldName) ? component : null;
    }
    
    return fieldGroups[fields]?.includes(fieldName) ? component : null;
  };

  if (!user) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        padding: '2rem' 
      }}>
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '1rem'
        }}>
          <div style={{
            width: '24px',
            height: '24px',
            border: '2px solid #e5e7eb',
            borderTop: '2px solid #10b981',
            borderRadius: '50%',
            animation: 'spin 1s linear infinite'
          }}></div>
          <span style={{ color: '#6b7280' }}>Loading profile...</span>
        </div>
      </div>
    );
  }

  const containerStyle = {
    maxWidth: compact ? '500px' : '700px',
    margin: '0 auto',
    padding: compact ? '1.5rem' : '2rem',
    background: 'white',
    borderRadius: compact ? '12px' : '16px',
    boxShadow: compact 
      ? '0 4px 6px -1px rgba(0, 0, 0, 0.1)' 
      : '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)'
  };

  const formGroupStyle = {
    marginBottom: compact ? '1rem' : '1.5rem'
  };

  const labelStyle = {
    display: 'block',
    fontSize: '0.875rem',
    fontWeight: '600',
    color: '#374151',
    marginBottom: '0.5rem'
  };

  const inputStyle = {
    width: '100%',
    padding: compact ? '0.625rem' : '0.75rem',
    border: '1px solid #d1d5db',
    borderRadius: '8px',
    fontSize: '1rem',
    fontFamily: 'inherit'
  };

  return (
    <div style={containerStyle}>
      {/* Header */}
      {!compact && (
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <h2 style={{
            fontSize: '1.875rem',
            fontWeight: '700',
            color: '#1f2937',
            marginBottom: '0.5rem'
          }}>
            Edit Profile
          </h2>
          <p style={{
            fontSize: '1rem',
            color: '#6b7280'
          }}>
            Update your profile information
          </p>
        </div>
      )}

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

      <form onSubmit={handleSubmit}>
        {/* Profile Image */}
        {renderField('profileImage', (
          <div style={formGroupStyle}>
            <label style={labelStyle}>Profile Picture</label>
            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
              {formData.profileImageUrl && (
                <img
                  src={formData.profileImageUrl}
                  alt="Profile"
                  style={{
                    width: '64px',
                    height: '64px',
                    borderRadius: '50%',
                    objectFit: 'cover',
                    border: '2px solid #e5e7eb'
                  }}
                />
              )}
              <div>
                <input
                  type="file"
                  accept="image/*"
                  onChange={handleImageUpload}
                  disabled={imageUploading}
                  style={{ display: 'none' }}
                  id="profile-image-upload"
                />
                <label
                  htmlFor="profile-image-upload"
                  style={{
                    display: 'inline-block',
                    padding: '0.5rem 1rem',
                    backgroundColor: imageUploading ? '#9ca3af' : '#10b981',
                    color: 'white',
                    borderRadius: '6px',
                    cursor: imageUploading ? 'not-allowed' : 'pointer',
                    fontSize: '0.875rem',
                    fontWeight: '500'
                  }}
                >
                  {imageUploading ? 'Uploading...' : 'Change Photo'}
                </label>
                <p style={{ fontSize: '0.75rem', color: '#6b7280', marginTop: '0.25rem' }}>
                  JPG, PNG up to 5MB
                </p>
              </div>
            </div>
          </div>
        ))}

        {/* Bio */}
        {renderField('bio', (
          <div style={formGroupStyle}>
            <label style={labelStyle}>
              {user.userType === 'VOLUNTEER' ? 'About You' : 'About Your Organization'} *
            </label>
            <textarea
              name="bio"
              value={formData.bio}
              onChange={handleInputChange}
              placeholder={user.userType === 'VOLUNTEER' 
                ? "Tell us about your interests, passions, and what motivates you..."
                : "Describe your organization's mission and impact..."
              }
              rows={4}
              style={{
                ...inputStyle,
                resize: 'vertical'
              }}
            />
          </div>
        ))}

        {/* Location */}
        {renderField('location', (
          <div style={formGroupStyle}>
            <label style={labelStyle}>Location *</label>
            <input
              type="text"
              name="location"
              value={formData.location}
              onChange={handleInputChange}
              placeholder="City, State"
              style={inputStyle}
            />
          </div>
        ))}

        {/* Contact Information */}
        {user.userType === 'VOLUNTEER' && (
          <>
            {renderField('firstName', (
              <div style={formGroupStyle}>
                <label style={labelStyle}>First Name</label>
                <input
                  type="text"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleInputChange}
                  style={inputStyle}
                />
              </div>
            ))}

            {renderField('lastName', (
              <div style={formGroupStyle}>
                <label style={labelStyle}>Last Name</label>
                <input
                  type="text"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleInputChange}
                  style={inputStyle}
                />
              </div>
            ))}
          </>
        )}

        {renderField('phoneNumber', (
          <div style={formGroupStyle}>
            <label style={labelStyle}>Phone Number</label>
            <input
              type="tel"
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleInputChange}
              placeholder="(555) 123-4567"
              style={inputStyle}
            />
          </div>
        ))}

        {/* User type specific fields */}
        {user.userType === 'VOLUNTEER' ? (
          <>
            {renderField('interests', (
              <div style={formGroupStyle}>
                <label style={labelStyle}>Interests & Causes *</label>
                <input
                  type="text"
                  name="interests"
                  value={formData.interests}
                  onChange={handleInputChange}
                  placeholder="e.g., Environment, Education, Healthcare"
                  style={inputStyle}
                />
              </div>
            ))}

            {renderField('skills', (
              <div style={formGroupStyle}>
                <label style={labelStyle}>Skills & Abilities</label>
                <input
                  type="text"
                  name="skills"
                  value={formData.skills}
                  onChange={handleInputChange}
                  placeholder="e.g., Communication, Teaching, Event Planning"
                  style={inputStyle}
                />
              </div>
            ))}

            {renderField('availability', (
              <div style={formGroupStyle}>
                <label style={labelStyle}>Availability</label>
                <select
                  name="availability"
                  value={formData.availability}
                  onChange={handleInputChange}
                  style={inputStyle}
                >
                  <option value="flexible">Flexible</option>
                  <option value="weekdays">Weekdays Only</option>
                  <option value="weekends">Weekends Only</option>
                  <option value="evenings">Evenings</option>
                  <option value="mornings">Mornings</option>
                </select>
              </div>
            ))}
          </>
        ) : (
          /* Organization fields */
          <>
            {renderField('organizationName', (
              <div style={formGroupStyle}>
                <label style={labelStyle}>Organization Name</label>
                <input
                  type="text"
                  name="organizationName"
                  value={formData.organizationName}
                  onChange={handleInputChange}
                  style={inputStyle}
                />
              </div>
            ))}

            {renderField('categories', (
              <div style={formGroupStyle}>
                <label style={labelStyle}>Focus Areas *</label>
                <input
                  type="text"
                  name="categories"
                  value={formData.categories}
                  onChange={handleInputChange}
                  placeholder="e.g., Community Development, Education, Healthcare"
                  style={inputStyle}
                />
              </div>
            ))}

            {renderField('organizationType', (
              <div style={formGroupStyle}>
                <label style={labelStyle}>Organization Type</label>
                <select
                  name="organizationType"
                  value={formData.organizationType}
                  onChange={handleInputChange}
                  style={inputStyle}
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
            ))}

            {renderField('website', (
              <div style={formGroupStyle}>
                <label style={labelStyle}>Website</label>
                <input
                  type="url"
                  name="website"
                  value={formData.website}
                  onChange={handleInputChange}
                  placeholder="https://yourorganization.org"
                  style={inputStyle}
                />
              </div>
            ))}

            {renderField('services', (
              <div style={formGroupStyle}>
                <label style={labelStyle}>Services Provided</label>
                <input
                  type="text"
                  name="services"
                  value={formData.services}
                  onChange={handleInputChange}
                  placeholder="e.g., Food distribution, Education programs"
                  style={inputStyle}
                />
              </div>
            ))}
          </>
        )}

        {/* Submit Button */}
        <div style={{ marginTop: compact ? '1.5rem' : '2rem' }}>
          <button
            type="submit"
            disabled={loading}
            style={{
              width: '100%',
              backgroundColor: loading ? '#9ca3af' : '#10b981',
              color: 'white',
              padding: compact ? '0.75rem 1.5rem' : '1rem 1.5rem',
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
            {loading ? 'Updating...' : 'Update Profile'}
          </button>
        </div>
      </form>

      {/* Add spinner animation */}
      <style>{`
        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  );
};

export default ProfileEditor;