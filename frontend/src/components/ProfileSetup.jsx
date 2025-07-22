import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { isLoggedIn } from '../services/authService';
import {
  User,
  Building,
  MapPin,
  Phone,
  Camera,
  ArrowRight,
  CheckCircle
} from 'lucide-react';
import './ProfileSetup.css';

const ProfileSetup = () => {
  const navigate = useNavigate();
  const [currentStep, setCurrentStep] = useState(1);
  const [formData, setFormData] = useState({
    profilePicture: null,
    phone: '',
    location: '',
    bio: '',
    interests: [],
    skills: [],
    availability: 'flexible'
  });
  const [loading, setLoading] = useState(false);

  // Redirect if not logged in
  useEffect(() => {
    if (!isLoggedIn()) {
      navigate('/login');
    }
  }, [navigate]);

  const totalSteps = 3;

  const availableInterests = [
    'Environment', 'Education', 'Healthcare', 'Community Service',
    'Animal Welfare', 'Senior Care', 'Youth Development', 'Arts & Culture',
    'Technology', 'Disaster Relief', 'Food Security', 'Homelessness'
  ];

  const availableSkills = [
    'Communication', 'Leadership', 'Teaching', 'Event Planning',
    'Marketing', 'Photography', 'Writing', 'Translation',
    'Technology Support', 'First Aid', 'Fundraising', 'Administrative'
  ];

  const handleInputChange = (field, value) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleArrayToggle = (field, value) => {
    setFormData(prev => ({
      ...prev,
      [field]: prev[field].includes(value)
        ? prev[field].filter(item => item !== value)
        : [...prev[field], value]
    }));
  };

  const handleNext = () => {
    if (currentStep < totalSteps) {
      setCurrentStep(currentStep + 1);
    }
  };

  const handlePrevious = () => {
    if (currentStep > 1) {
      setCurrentStep(currentStep - 1);
    }
  };

  const handleSkip = () => {
    navigate('/dashboard');
  };

  const handleComplete = async () => {
    setLoading(true);
    try {
      // TODO: Send profile data to backend
      console.log('Profile setup data:', formData);
      
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      navigate('/dashboard');
    } catch (error) {
      console.error('Profile setup failed:', error);
    } finally {
      setLoading(false);
    }
  };

  const renderStep1 = () => (
    <div className="setup-step">
      <div className="step-header">
        <h2 className="step-title">Welcome! Let's set up your profile</h2>
        <p className="step-description">
          Add a photo and basic information to help others connect with you
        </p>
      </div>

      <div className="step-content">
        {/* Profile Picture */}
        <div className="form-group">
          <label className="form-label">Profile Picture</label>
          <div className="profile-picture-upload">
            <div className="profile-picture-preview">
              {formData.profilePicture ? (
                <img src={formData.profilePicture} alt="Profile" />
              ) : (
                <div className="profile-picture-placeholder">
                  <Camera />
                  <span>Add Photo</span>
                </div>
              )}
            </div>
            <input
              type="file"
              accept="image/*"
              onChange={(e) => {
                const file = e.target.files[0];
                if (file) {
                  const reader = new FileReader();
                  reader.onload = (e) => {
                    handleInputChange('profilePicture', e.target.result);
                  };
                  reader.readAsDataURL(file);
                }
              }}
              className="file-input"
            />
          </div>
        </div>

        {/* Phone */}
        <div className="form-group">
          <label className="form-label">
            <Phone className="input-icon" />
            Phone Number
          </label>
          <input
            type="tel"
            className="form-input"
            value={formData.phone}
            onChange={(e) => handleInputChange('phone', e.target.value)}
            placeholder="Enter your phone number"
          />
        </div>

        {/* Location */}
        <div className="form-group">
          <label className="form-label">
            <MapPin className="input-icon" />
            Location
          </label>
          <input
            type="text"
            className="form-input"
            value={formData.location}
            onChange={(e) => handleInputChange('location', e.target.value)}
            placeholder="City, State"
          />
        </div>
      </div>
    </div>
  );

  const renderStep2 = () => (
    <div className="setup-step">
      <div className="step-header">
        <h2 className="step-title">Tell us about yourself</h2>
        <p className="step-description">
          Share your interests and skills to help us match you with the right opportunities
        </p>
      </div>

      <div className="step-content">
        {/* Bio */}
        <div className="form-group">
          <label className="form-label">Bio</label>
          <textarea
            className="form-textarea"
            value={formData.bio}
            onChange={(e) => handleInputChange('bio', e.target.value)}
            placeholder="Tell us a bit about yourself and what motivates you to volunteer..."
            rows={4}
          />
        </div>

        {/* Interests */}
        <div className="form-group">
          <label className="form-label">Interests</label>
          <p className="form-help">Select causes you're passionate about</p>
          <div className="tag-grid">
            {availableInterests.map(interest => (
              <button
                key={interest}
                type="button"
                className={`tag-btn ${formData.interests.includes(interest) ? 'active' : ''}`}
                onClick={() => handleArrayToggle('interests', interest)}
              >
                {interest}
              </button>
            ))}
          </div>
        </div>

        {/* Skills */}
        <div className="form-group">
          <label className="form-label">Skills</label>
          <p className="form-help">What skills can you bring to volunteer work?</p>
          <div className="tag-grid">
            {availableSkills.map(skill => (
              <button
                key={skill}
                type="button"
                className={`tag-btn ${formData.skills.includes(skill) ? 'active' : ''}`}
                onClick={() => handleArrayToggle('skills', skill)}
              >
                {skill}
              </button>
            ))}
          </div>
        </div>
      </div>
    </div>
  );

  const renderStep3 = () => (
    <div className="setup-step">
      <div className="step-header">
        <h2 className="step-title">Availability & Preferences</h2>
        <p className="step-description">
          Help organizations understand when and how you'd like to volunteer
        </p>
      </div>

      <div className="step-content">
        {/* Availability */}
        <div className="form-group">
          <label className="form-label">Availability</label>
          <div className="radio-group">
            <label className="radio-option">
              <input
                type="radio"
                name="availability"
                value="weekends"
                checked={formData.availability === 'weekends'}
                onChange={(e) => handleInputChange('availability', e.target.value)}
              />
              <span className="radio-label">Weekends only</span>
            </label>
            <label className="radio-option">
              <input
                type="radio"
                name="availability"
                value="weekdays"
                checked={formData.availability === 'weekdays'}
                onChange={(e) => handleInputChange('availability', e.target.value)}
              />
              <span className="radio-label">Weekdays only</span>
            </label>
            <label className="radio-option">
              <input
                type="radio"
                name="availability"
                value="flexible"
                checked={formData.availability === 'flexible'}
                onChange={(e) => handleInputChange('availability', e.target.value)}
              />
              <span className="radio-label">Flexible schedule</span>
            </label>
          </div>
        </div>

        {/* Summary */}
        <div className="setup-summary">
          <h3 className="summary-title">Profile Summary</h3>
          <div className="summary-grid">
            <div className="summary-item">
              <strong>Location:</strong>
              <span>{formData.location || 'Not specified'}</span>
            </div>
            <div className="summary-item">
              <strong>Interests:</strong>
              <span>{formData.interests.length} selected</span>
            </div>
            <div className="summary-item">
              <strong>Skills:</strong>
              <span>{formData.skills.length} selected</span>
            </div>
            <div className="summary-item">
              <strong>Availability:</strong>
              <span>{formData.availability}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );

  return (
    <div className="profile-setup-page">
      <div className="setup-container">
        {/* Progress Bar */}
        <div className="setup-progress">
          <div className="progress-steps">
            {Array.from({ length: totalSteps }, (_, i) => i + 1).map(step => (
              <div
                key={step}
                className={`progress-step ${step <= currentStep ? 'active' : ''} ${step < currentStep ? 'completed' : ''}`}
              >
                {step < currentStep ? <CheckCircle /> : <span>{step}</span>}
              </div>
            ))}
          </div>
          <div className="progress-bar">
            <div
              className="progress-fill"
              style={{ width: `${(currentStep / totalSteps) * 100}%` }}
            />
          </div>
        </div>

        {/* Step Content */}
        <div className="setup-content">
          {currentStep === 1 && renderStep1()}
          {currentStep === 2 && renderStep2()}
          {currentStep === 3 && renderStep3()}
        </div>

        {/* Navigation */}
        <div className="setup-navigation">
          <div className="nav-left">
            {currentStep > 1 && (
              <button
                type="button"
                onClick={handlePrevious}
                className="btn-secondary"
              >
                Previous
              </button>
            )}
            <button
              type="button"
              onClick={handleSkip}
              className="btn-text"
            >
              Skip for now
            </button>
          </div>

          <div className="nav-right">
            {currentStep < totalSteps ? (
              <button
                type="button"
                onClick={handleNext}
                className="btn-primary"
              >
                Next
                <ArrowRight />
              </button>
            ) : (
              <button
                type="button"
                onClick={handleComplete}
                disabled={loading}
                className="btn-primary"
              >
                {loading ? 'Completing...' : 'Complete Setup'}
                {!loading && <CheckCircle />}
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfileSetup;