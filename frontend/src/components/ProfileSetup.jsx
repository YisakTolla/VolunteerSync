import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  getCurrentUser, 
  completeProfile, 
  isLoggedIn, 
  logout 
} from '../services/authService';
import {
  User,
  MapPin,
  Phone,
  Globe,
  FileText,
  Camera,
  Upload,
  ArrowRight,
  Check,
  AlertCircle,
  Building,
  Target
} from 'lucide-react';
import './ProfileSetup.css';

const ProfileSetup = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [profileImage, setProfileImage] = useState(null);
  const [profileImagePreview, setProfileImagePreview] = useState(null);

  const [profileData, setProfileData] = useState({
    phone: '',
    location: '',
    website: '',
    bio: '',
    interests: [],
    skills: [],
    causes: [],
    services: [],
    organizationType: 'Non-Profit',
    founded: ''
  });

  // Predefined options (same as RegisterUser)
  const volunteerInterests = [
    'Education', 'Technology', 'Environment', 'Youth Development',
    'Community Service', 'Animal Welfare', 'Healthcare', 'Arts & Culture',
    'Senior Support', 'Disaster Relief', 'Food Security', 'Housing'
  ];

  const volunteerSkills = [
    'Project Management', 'Public Speaking', 'Event Planning', 'Team Leadership',
    'Marketing', 'Social Media', 'Graphic Design', 'Writing', 'Translation',
    'Teaching', 'Counseling', 'First Aid', 'Computer Skills', 'Photography'
  ];

  const organizationCauses = [
    'Climate Change', 'Wildlife Conservation', 'Clean Water', 'Renewable Energy',
    'Education Access', 'Poverty Alleviation', 'Healthcare Access', 'Food Security',
    'Human Rights', 'Gender Equality', 'Mental Health', 'Youth Development'
  ];

  const organizationServices = [
    'Direct Service Delivery', 'Education & Training', 'Research & Advocacy',
    'Community Organizing', 'Emergency Response', 'Counseling & Support',
    'Resource Distribution', 'Infrastructure Development', 'Policy Development'
  ];

  const organizationTypes = [
    'Non-Profit', 'Educational Institution', 'Religious Organization',
    'Community Group', 'Government Agency', 'Healthcare Organization',
    'Environmental Group', 'Social Services', 'Arts & Culture', 'Other'
  ];

  useEffect(() => {
    if (!isLoggedIn()) {
      navigate('/login');
      return;
    }

    const currentUser = getCurrentUser();
    if (!currentUser) {
      navigate('/login');
      return;
    }

    setUser(currentUser);

    // Pre-fill with existing data if any
    setProfileData(prev => ({
      ...prev,
      phone: currentUser.phone || '',
      location: currentUser.location || '',
      website: currentUser.website || '',
      bio: currentUser.bio || '',
      organizationType: currentUser.organizationType || 'Non-Profit',
      founded: currentUser.founded || ''
    }));

  }, [navigate]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setProfileData(prev => ({
      ...prev,
      [name]: value
    }));
    if (error) setError('');
  };

  const handleTagToggle = (category, item) => {
    setProfileData(prev => ({
      ...prev,
      [category]: prev[category].includes(item)
        ? prev[category].filter(i => i !== item)
        : [...prev[category], item]
    }));
  };

  const handleImageUpload = (e) => {
    const file = e.target.files[0];
    if (file) {
      setProfileImage(file);
      const reader = new FileReader();
      reader.onloadend = () => {
        setProfileImagePreview(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const validateProfile = () => {
    if (!profileData.location.trim()) {
      setError('Location is required');
      return false;
    }

    if (!profileData.bio.trim()) {
      setError('Please add a brief bio');
      return false;
    }

    if (user?.userType === 'VOLUNTEER' && profileData.interests.length === 0) {
      setError('Please select at least one interest');
      return false;
    }

    if (user?.userType === 'ORGANIZATION' && profileData.causes.length === 0) {
      setError('Please select at least one cause your organization supports');
      return false;
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateProfile()) return;

    setLoading(true);
    setError('');

    try {
      const result = await completeProfile(profileData);
      
      if (result.success) {
        setSuccess('Profile completed successfully! Redirecting to dashboard...');
        setTimeout(() => {
          navigate('/dashboard');
        }, 2000);
      } else {
        setError(result.message || 'Failed to complete profile. Please try again.');
      }
    } catch (error) {
      console.error('Profile completion error:', error);
      setError('An unexpected error occurred. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleSkipForNow = () => {
    // Allow user to skip but mark profile as incomplete
    navigate('/dashboard');
  };

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  if (!user) {
    return <div>Loading...</div>;
  }

  return (
    <div className="profile-setup-page">
      <div className="profile-setup-container">
        {/* Header */}
        <div className="profile-setup-header">
          <div className="profile-setup-logo">
            <span className="profile-setup-logo-icon">🤝</span>
            <span className="profile-setup-logo-text">VolunteerSync</span>
          </div>
          
          <button 
            className="profile-setup-logout"
            onClick={handleLogout}
          >
            Logout
          </button>
        </div>

        {/* Welcome */}
        <div className="profile-setup-welcome">
          <h1>Welcome to VolunteerSync, {user.userType === 'VOLUNTEER' ? user.firstName : user.organizationName}!</h1>
          <p>Let's complete your profile to help you get the most out of our platform.</p>
        </div>

        {/* Form */}
        <div className="profile-setup-form-container">
          <form onSubmit={handleSubmit} className="profile-setup-form">
            
            {/* Profile Image */}
            <div className="profile-setup-section">
              <h2>Profile Photo</h2>
              <div className="profile-setup-image-upload">
                <div className="profile-setup-image-preview">
                  {profileImagePreview ? (
                    <img src={profileImagePreview} alt="Profile preview" />
                  ) : (
                    <div className="profile-setup-image-placeholder">
                      <Camera />
                      <span>Add Photo</span>
                    </div>
                  )}
                </div>
                <div className="profile-setup-image-actions">
                  <input
                    type="file"
                    id="profileImage"
                    accept="image/*"
                    onChange={handleImageUpload}
                    className="profile-setup-file-input"
                  />
                  <label htmlFor="profileImage" className="profile-setup-file-label">
                    <Upload />
                    Choose Photo
                  </label>
                </div>
              </div>
            </div>

            {/* Contact Information */}
            <div className="profile-setup-section">
              <h2>Contact Information</h2>
              
              <div className="profile-setup-form-row">
                <div className="profile-setup-form-group">
                  <label htmlFor="phone">
                    <Phone className="profile-setup-icon" />
                    Phone Number
                  </label>
                  <input
                    type="tel"
                    id="phone"
                    name="phone"
                    value={profileData.phone}
                    onChange={handleInputChange}
                    placeholder="Enter your phone number"
                  />
                </div>

                <div className="profile-setup-form-group">
                  <label htmlFor="location">
                    <MapPin className="profile-setup-icon" />
                    Location *
                  </label>
                  <input
                    type="text"
                    id="location"
                    name="location"
                    value={profileData.location}
                    onChange={handleInputChange}
                    placeholder="City, State"
                    required
                  />
                </div>
              </div>

              <div className="profile-setup-form-group">
                <label htmlFor="website">
                  <Globe className="profile-setup-icon" />
                  Website
                </label>
                <input
                  type="url"
                  id="website"
                  name="website"
                  value={profileData.website}
                  onChange={handleInputChange}
                  placeholder="https://your-website.com"
                />
              </div>

              {/* Organization specific fields */}
              {user.userType === 'ORGANIZATION' && (
                <div className="profile-setup-form-row">
                  <div className="profile-setup-form-group">
                    <label htmlFor="organizationType">
                      <Building className="profile-setup-icon" />
                      Organization Type
                    </label>
                    <select
                      id="organizationType"
                      name="organizationType"
                      value={profileData.organizationType}
                      onChange={handleInputChange}
                    >
                      {organizationTypes.map(type => (
                        <option key={type} value={type}>{type}</option>
                      ))}
                    </select>
                  </div>

                  <div className="profile-setup-form-group">
                    <label htmlFor="founded">
                      <Building className="profile-setup-icon" />
                      Founded
                    </label>
                    <input
                      type="text"
                      id="founded"
                      name="founded"
                      value={profileData.founded}
                      onChange={handleInputChange}
                      placeholder="e.g., March 2018"
                    />
                  </div>
                </div>
              )}
            </div>

            {/* Bio */}
            <div className="profile-setup-section">
              <h2>About {user.userType === 'VOLUNTEER' ? 'You' : 'Your Organization'}</h2>
              <div className="profile-setup-form-group">
                <label htmlFor="bio">
                  <FileText className="profile-setup-icon" />
                  Bio *
                </label>
                <textarea
                  id="bio"
                  name="bio"
                  value={profileData.bio}
                  onChange={handleInputChange}
                  placeholder={`Tell us about ${user.userType === 'VOLUNTEER' ? 'yourself and your interests' : 'your organization and mission'}...`}
                  rows={4}
                  required
                />
              </div>
            </div>

            {/* Interests/Causes */}
            <div className="profile-setup-section">
              <h2>
                <Target className="profile-setup-icon" />
                {user.userType === 'VOLUNTEER' ? 'Your Interests' : 'Causes You Support'} *
              </h2>
              <p className="profile-setup-section-description">
                {user.userType === 'VOLUNTEER' 
                  ? 'Select areas you\'re passionate about to help us match you with relevant opportunities.'
                  : 'Choose the causes your organization focuses on to attract the right volunteers.'
                }
              </p>
              <div className="profile-setup-tags">
                {(user.userType === 'VOLUNTEER' ? volunteerInterests : organizationCauses).map((item) => (
                  <button
                    key={item}
                    type="button"
                    className={`profile-setup-tag ${
                      profileData[user.userType === 'VOLUNTEER' ? 'interests' : 'causes'].includes(item) ? 'active' : ''
                    }`}
                    onClick={() => handleTagToggle(user.userType === 'VOLUNTEER' ? 'interests' : 'causes', item)}
                  >
                    {item}
                  </button>
                ))}
              </div>
            </div>

            {/* Skills/Services */}
            <div className="profile-setup-section">
              <h2>{user.userType === 'VOLUNTEER' ? 'Your Skills' : 'Services & Programs'}</h2>
              <p className="profile-setup-section-description">
                {user.userType === 'VOLUNTEER' 
                  ? 'What skills can you bring to volunteer opportunities?'
                  : 'What services and programs does your organization offer?'
                }
              </p>
              <div className="profile-setup-tags">
                {(user.userType === 'VOLUNTEER' ? volunteerSkills : organizationServices).map((item) => (
                  <button
                    key={item}
                    type="button"
                    className={`profile-setup-tag ${
                      profileData[user.userType === 'VOLUNTEER' ? 'skills' : 'services'].includes(item) ? 'active' : ''
                    }`}
                    onClick={() => handleTagToggle(user.userType === 'VOLUNTEER' ? 'skills' : 'services', item)}
                  >
                    {item}
                  </button>
                ))}
              </div>
            </div>

            {/* Messages */}
            {error && (
              <div className="profile-setup-message error">
                <AlertCircle />
                {error}
              </div>
            )}
            
            {success && (
              <div className="profile-setup-message success">
                <Check />
                {success}
              </div>
            )}

            {/* Actions */}
            <div className="profile-setup-actions">
              <button
                type="button"
                className="profile-setup-btn secondary"
                onClick={handleSkipForNow}
                disabled={loading}
              >
                Skip for Now
              </button>
              
              <button
                type="submit"
                className="profile-setup-btn primary"
                disabled={loading}
              >
                {loading ? 'Completing Profile...' : 'Complete Profile'}
                <ArrowRight />
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default ProfileSetup;