import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { isLoggedIn, getCurrentUser, ensureValidToken, isTokenExpired } from '../services/authService';
import { createProfile, updateProfile, fetchMyProfile } from '../services/profileService';
import './ProfileSetup.css';

const ProfileSetup = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [user, setUser] = useState(null);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [selectedInterests, setSelectedInterests] = useState([]);
  const [hasExistingProfile, setHasExistingProfile] = useState(false);
  const [formData, setFormData] = useState({
    bio: '',
    location: '',
    interests: '',
    skills: '',
    phoneNumber: '',
    organizationName: '',
    organizationType: '',
    website: '',
    missionStatement: '',
    categories: '',
    services: '',
    firstName: '',
    lastName: '',
    availability: 'flexible'
  });

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
    if (!isLoggedIn()) {
      navigate('/login');
      return;
    }

    const token = localStorage.getItem('authToken');
    if (!token) {
      navigate('/login');
      return;
    }

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const timeRemainingMs = payload.exp * 1000 - Date.now();

      if (isTokenExpired(token)) {
        ensureValidToken().then(initializeComponent).catch(() => navigate('/login'));
      } else if (timeRemainingMs < 5 * 60 * 1000) {
        ensureValidToken().then(initializeComponent).catch(initializeComponent);
      } else {
        initializeComponent();
      }
    } catch {
      localStorage.removeItem('authToken');
      navigate('/login');
    }
  }, [navigate]);

  const initializeComponent = async () => {
    const currentUser = getCurrentUser();
    if (!currentUser) {
      navigate('/login');
      return;
    }
    setUser(currentUser);

    try {
      const profileResult = await fetchMyProfile();
      if (profileResult.success && profileResult.data) {
        setHasExistingProfile(true);
        const existingData = profileResult.data;
        setFormData(prev => ({
          ...prev,
          firstName: existingData.firstName || '',
          lastName: existingData.lastName || '',
          organizationName: existingData.organizationName || '',
          bio: existingData.bio || '',
          location: existingData.location || '',
          skills: existingData.skills || '',
          phoneNumber: existingData.phoneNumber || '',
          organizationType: existingData.organizationType || '',
          website: existingData.website || '',
          services: existingData.services || '',
          categories: existingData.categories || '',
          availability: existingData.availability || 'flexible'
        }));
        if (existingData.interests) {
          setSelectedInterests(existingData.interests.split(',').map(i => i.trim()));
        }
      } else {
        setHasExistingProfile(false);
        setFormData(prev => ({
          ...prev,
          firstName: currentUser.firstName || '',
          lastName: currentUser.lastName || '',
          organizationName: currentUser.organizationName || '',
        }));
      }
    } catch {
      setHasExistingProfile(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (error) setError('');
  };

  const handleInterestToggle = (interestValue) => {
    setSelectedInterests(prev =>
      prev.includes(interestValue)
        ? prev.filter(i => i !== interestValue)
        : [...prev, interestValue]
    );
    if (error) setError('');
  };

  const validateForm = () => {
    if (!formData.bio.trim()) return setError('Please tell us about yourself'), false;
    if (!formData.location.trim()) return setError('Please provide your location'), false;
    if (user.userType === 'VOLUNTEER' && selectedInterests.length === 0)
      return setError('Please select at least one interest or cause'), false;
    if (user.userType === 'ORGANIZATION' && !formData.categories.trim())
      return setError('Please specify your organization focus areas'), false;
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      await ensureValidToken();
      if (!validateForm()) {
        setLoading(false);
        return;
      }

      const submissionData = { ...formData, interests: selectedInterests.join(',') };
      const result = hasExistingProfile
        ? await updateProfile(submissionData)
        : await createProfile(submissionData);

      if (result.success) {
        setSuccess(`Profile ${hasExistingProfile ? 'updated' : 'created'} successfully!`);
        setTimeout(() => navigate('/dashboard'), 2000);
      } else {
        setError(result.message || 'Profile save failed.');
      }
    } catch {
      setError('Something went wrong. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleSkip = async () => {
    await ensureValidToken();
    const skipData = {
      bio: '',
      location: '',
      interests: '',
      skills: '',
      phoneNumber: '',
      ...(user?.userType === 'VOLUNTEER' ? { firstName: user.firstName || '', lastName: user.lastName || '', availability: 'flexible' } : {}),
      ...(user?.userType === 'ORGANIZATION' ? { organizationName: user.organizationName || '', organizationType: '', website: '', missionStatement: '', categories: '', services: '' } : {})
    };

    if (hasExistingProfile) await updateProfile(skipData);
    else await createProfile(skipData);
    navigate('/dashboard');
  };

  if (!user) {
    return (
      <div className="profile-setup-loading">
        <div className="profile-setup-loading-content">
          <div className="profile-setup-loading-spinner"></div>
          <span className="profile-setup-loading-text">Loading...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="profile-setup-container">
      <div className="profile-setup-card">
        <div className="profile-setup-header">
          <h1 className="profile-setup-title">
            {hasExistingProfile ? 'Update Your Profile' : 'Complete Your Profile'}
          </h1>
          <p className="profile-setup-subtitle">
            Welcome, {user.firstName || user.organizationName || user.email}!
          </p>
        </div>

        {error && <div className="profile-setup-error">{error}</div>}
        {success && <div className="profile-setup-success">{success}</div>}

        <form onSubmit={handleSubmit} className="profile-setup-form">
          <div className="profile-setup-field">
            <label className="profile-setup-label">Tell us about yourself *</label>
            <textarea
              name="bio"
              value={formData.bio}
              onChange={handleInputChange}
              className="profile-setup-textarea"
              rows={4}
              required
            />
          </div>

          <div className="profile-setup-field">
            <label className="profile-setup-label">Location *</label>
            <input
              type="text"
              name="location"
              value={formData.location}
              onChange={handleInputChange}
              className="profile-setup-input"
              required
            />
          </div>

          {user.userType === 'VOLUNTEER' && (
            <>
              <div className="profile-setup-field">
                <label className="profile-setup-label">Interests & Causes *</label>
                <p className="interests-description">
                  Select the causes you're passionate about:
                </p>
                <div className="interests-container">
                  {interestOptions.map(opt => (
                    <button
                      key={opt.value}
                      type="button"
                      onClick={() => handleInterestToggle(opt.value)}
                      className={`interest-tag ${selectedInterests.includes(opt.value) ? 'selected' : ''}`}
                    >
                      <span>{opt.emoji}</span> <span>{opt.label}</span>
                    </button>
                  ))}
                </div>
                {selectedInterests.length > 0 && (
                  <p className="interests-count">Selected: {selectedInterests.length}</p>
                )}
              </div>
            </>
          )}

          <div className="profile-setup-buttons">
            <button type="submit" disabled={loading} className="profile-setup-submit-btn">
              {loading && <div className="loading-spinner"></div>}
              {hasExistingProfile ? 'Update Profile' : 'Complete Profile'}
            </button>
            <button type="button" onClick={handleSkip} disabled={loading} className="profile-setup-skip-btn">
              Skip for now
            </button>
          </div>
        </form>

        <p className="profile-setup-footer">
          You can always update this information later in your profile settings.
        </p>
      </div>
    </div>
  );
};

export default ProfileSetup;
