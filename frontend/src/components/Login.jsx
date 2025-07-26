import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { 
  loginUser, 
  registerUser, 
  loginWithGoogle, 
  registerWithGoogle, 
  isLoggedIn 
} from '../services/authService';
import {
  User,
  Building,
  Mail,
  Lock,
  Eye,
  EyeOff,
  ArrowLeft
} from 'lucide-react';
import './Login.css';

const Login = ({ onBackToHome }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const navigationState = location.state;

  const [isSignUp, setIsSignUp] = useState(navigationState?.mode === 'signup' || false);
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    confirmPassword: '',
    firstName: '',
    lastName: '',
    organizationName: '',
    userType: navigationState?.userType || 'volunteer'
  });
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Redirect if already logged in
  useEffect(() => {
    if (isLoggedIn()) {
      navigate('/dashboard');
    }
  }, [navigate]);

  // Update form when navigation state changes
  useEffect(() => {
    if (navigationState) {
      setIsSignUp(navigationState.mode === 'signup');
      setFormData(prev => ({
        ...prev,
        userType: navigationState.userType || 'volunteer'
      }));
    }
  }, [navigationState]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // Clear error when user starts typing
    if (error) setError('');
  };

  const handleUserTypeChange = (userType) => {
    setFormData(prev => ({
      ...prev,
      userType,
      // Clear name fields when switching user types
      firstName: '',
      lastName: '',
      organizationName: ''
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      if (isSignUp) {
        // Registration validation
        if (formData.password !== formData.confirmPassword) {
          setError('Passwords do not match');
          setLoading(false);
          return;
        }

        if (formData.password.length < 6) {
          setError('Password must be at least 6 characters long');
          setLoading(false);
          return;
        }

        // Prepare registration data based on user type
        const baseData = {
          email: formData.email.trim(), // 🔧 Will be normalized in authService
          password: formData.password,
          confirmPassword: formData.confirmPassword,
          userType: formData.userType.toUpperCase()
        };

        let registrationData;

        if (formData.userType === 'organization') {
          if (!formData.organizationName.trim()) {
            setError('Organization name is required');
            setLoading(false);
            return;
          }
          
          registrationData = {
            ...baseData,
            organizationName: formData.organizationName.trim()
          };
        } else {
          // volunteer
          if (!formData.firstName.trim() || !formData.lastName.trim()) {
            setError('First name and last name are required');
            setLoading(false);
            return;
          }
          
          registrationData = {
            ...baseData,
            firstName: formData.firstName.trim(),
            lastName: formData.lastName.trim()
          };
        }

        console.log('Attempting registration...'); // Debug log

        const result = await registerUser(registrationData);
        
        if (result.success) {
          setSuccess('Account created successfully! Redirecting...');
          setTimeout(() => {
            // Send new users to profile setup
            navigate('/profile-setup');
          }, 1500);
        } else {
          setError(result.message || 'Registration failed');
        }
      } else {
        // 🔧 LOGIN LOGIC - Now with proper email handling
        if (!formData.email.trim() || !formData.password) {
          setError('Email and password are required');
          setLoading(false);
          return;
        }

        console.log('Attempting login...'); // Debug log

        const result = await loginUser(formData.email.trim(), formData.password);
        
        if (result.success) {
          setSuccess('Login successful! Redirecting...');
          setTimeout(() => {
            navigate('/dashboard'); // Existing users go to dashboard
          }, 1000);
        } else {
          setError(result.message || 'Login failed');
        }
      }
    } catch (error) {
      setError('Something went wrong. Please try again.');
      console.error('Auth error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleLogin = async () => {
    console.log('Google login clicked');
    setError('Google OAuth not implemented yet');
  };

  const toggleMode = () => {
    setIsSignUp(!isSignUp);
    setFormData({
      email: '',
      password: '',
      confirmPassword: '',
      firstName: '',
      lastName: '',
      organizationName: '',
      userType: 'volunteer'
    });
    setError('');
    setSuccess('');
  };

  return (
    <div className="login-page">
      <div className="login-container">
        {/* Left side - Form */}
        <div className="login-form-section">
          <div className="login-form-container">
            {/* Logo with back navigation */}
            <div className="login-logo" onClick={() => navigate('/')} style={{ cursor: 'pointer' }}>
              <span className="login-logo-icon">🤝</span>
              <span className="login-logo-text">VolunteerSync</span>
            </div>

            {/* Header */}
            <div className="login-header">
              <h1 className="login-title">
                {isSignUp ? 'Join VolunteerSync' : 'Welcome Back'}
              </h1>
              <p className="login-subtitle">
                {isSignUp 
                  ? 'Start making a difference in your community today'
                  : 'Sign in to continue your volunteer journey'
                }
              </p>
            </div>

            {/* User Type Selection (only for sign up) */}
            {isSignUp && (
              <div className="user-type-selector">
                <p className="user-type-label">I want to join as:</p>
                <div className="user-type-buttons">
                  <button
                    type="button"
                    className={`user-type-btn ${formData.userType === 'volunteer' ? 'active' : ''}`}
                    onClick={() => handleUserTypeChange('volunteer')}
                  >
                    <span className="user-type-icon">👤</span>
                    <span className="user-type-text">Volunteer</span>
                  </button>
                  <button
                    type="button"
                    className={`user-type-btn ${formData.userType === 'organization' ? 'active' : ''}`}
                    onClick={() => handleUserTypeChange('organization')}
                  >
                    <span className="user-type-icon">🏢</span>
                    <span className="user-type-text">Organization</span>
                  </button>
                </div>
              </div>
            )}

            {/* Google Sign In Button */}
            <button
              type="button"
              className="google-btn"
              onClick={handleGoogleLogin}
              disabled={loading}
            >
              <svg className="google-icon" width="18" height="18" viewBox="0 0 18 18">
                <path fill="#4285F4" d="M16.51 8H8.98v3h4.3c-.18 1-.74 1.48-1.6 2.04v2.01h2.6a7.8 7.8 0 0 0 2.38-5.88c0-.57-.05-.66-.15-1.18z"/>
                <path fill="#34A853" d="M8.98 17c2.16 0 3.97-.72 5.3-1.94l-2.6-2.04a4.8 4.8 0 0 1-2.7.75 4.8 4.8 0 0 1-4.52-3.26H1.83v2.07A8 8 0 0 0 8.98 17z"/>
                <path fill="#FBBC05" d="M4.46 10.51a4.8 4.8 0 0 1-.25-1.51c0-.52.09-1.03.25-1.51V5.42H1.83a8 8 0 0 0 0 7.16l2.63-2.07z"/>
                <path fill="#EA4335" d="M8.98 4.75c1.23 0 2.33.42 3.2 1.24l2.4-2.4A8 8 0 0 0 1.83 5.42L4.46 7.5A4.8 4.8 0 0 1 8.98 4.75z"/>
              </svg>
              {loading ? 'Connecting...' : `${isSignUp ? 'Sign up' : 'Sign in'} with Google`}
            </button>

            <div className="login-divider">
              <span className="login-divider-text">or</span>
            </div>

            {/* Form */}
            <form onSubmit={handleSubmit} className="login-form">
              {/* Name fields for volunteers and organization name for organizations (only signup) */}
              {isSignUp && (
                <>
                  {formData.userType === 'volunteer' ? (
                    <div className="form-row">
                      <div className="form-group">
                        <label htmlFor="firstName" className="form-label">First Name</label>
                        <input
                          type="text"
                          id="firstName"
                          name="firstName"
                          className="form-input"
                          value={formData.firstName}
                          onChange={handleInputChange}
                          placeholder="Enter your first name"
                          required
                        />
                      </div>
                      <div className="form-group">
                        <label htmlFor="lastName" className="form-label">Last Name</label>
                        <input
                          type="text"
                          id="lastName"
                          name="lastName"
                          className="form-input"
                          value={formData.lastName}
                          onChange={handleInputChange}
                          placeholder="Enter your last name"
                          required
                        />
                      </div>
                    </div>
                  ) : (
                    <div className="form-group">
                      <label htmlFor="organizationName" className="form-label">Organization Name</label>
                      <input
                        type="text"
                        id="organizationName"
                        name="organizationName"
                        className="form-input"
                        value={formData.organizationName}
                        onChange={handleInputChange}
                        placeholder="Enter your organization name"
                        required
                      />
                    </div>
                  )}
                </>
              )}

              {/* Email */}
              <div className="form-group">
                <label htmlFor="email" className="form-label">Email Address</label>
                <input
                  type="email"
                  id="email"
                  name="email"
                  className="form-input"
                  value={formData.email}
                  onChange={handleInputChange}
                  placeholder="Enter your email"
                  required
                />
              </div>

              {/* Password */}
              <div className="form-group">
                <label htmlFor="password" className="form-label">Password</label>
                <div className="password-input-container">
                  <input
                    type={showPassword ? "text" : "password"}
                    id="password"
                    name="password"
                    className="form-input"
                    value={formData.password}
                    onChange={handleInputChange}
                    placeholder="Enter your password"
                    required
                  />
                  <button
                    type="button"
                    className="password-toggle"
                    onClick={() => setShowPassword(!showPassword)}
                  >
                    {showPassword ? <EyeOff /> : <Eye />}
                  </button>
                </div>
              </div>

              {/* Confirm Password (only for signup) */}
              {isSignUp && (
                <div className="form-group">
                  <label htmlFor="confirmPassword" className="form-label">Confirm Password</label>
                  <div className="password-input-container">
                    <input
                      type={showConfirmPassword ? "text" : "password"}
                      id="confirmPassword"
                      name="confirmPassword"
                      className="form-input"
                      value={formData.confirmPassword}
                      onChange={handleInputChange}
                      placeholder="Confirm your password"
                      required
                    />
                    <button
                      type="button"
                      className="password-toggle"
                      onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                    >
                      {showConfirmPassword ? <EyeOff /> : <Eye />}
                    </button>
                  </div>
                </div>
              )}

              {/* Forgot Password Link (only for login) */}
              {!isSignUp && (
                <div className="forgot-password">
                  <a href="/forgot-password" className="forgot-password-link">
                    Forgot your password?
                  </a>
                </div>
              )}

              {/* Error Message */}
              {error && (
                <div className="error-message" style={{ 
                  padding: 'var(--spacing-3)', 
                  backgroundColor: '#fef2f2', 
                  color: '#dc2626', 
                  borderRadius: 'var(--radius-lg)', 
                  fontSize: 'var(--font-size-sm)',
                  marginBottom: 'var(--spacing-4)'
                }}>
                  {error}
                </div>
              )}
              
              {/* Success Message */}
              {success && (
                <div className="success-message" style={{ 
                  padding: 'var(--spacing-3)', 
                  backgroundColor: '#f0fdf4', 
                  color: '#059669', 
                  borderRadius: 'var(--radius-lg)', 
                  fontSize: 'var(--font-size-sm)',
                  marginBottom: 'var(--spacing-4)'
                }}>
                  {success}
                </div>
              )}

              {/* Submit Button */}
              <button
                type="submit"
                className="login-submit-btn"
                disabled={loading}
              >
                {loading ? (
                  <div className="loading-spinner"></div>
                ) : (
                  isSignUp ? 'Create Account' : 'Sign In'
                )}
              </button>

              {/* Terms (only for signup) */}
              {isSignUp && (
                <div className="login-terms">
                  By creating an account, you agree to our{' '}
                  <a href="/terms" className="login-link">Terms of Service</a>
                  {' '}and{' '}
                  <a href="/privacy" className="login-link">Privacy Policy</a>
                </div>
              )}
            </form>

            {/* Toggle Mode */}
            <div className="login-toggle">
              <p>
                {isSignUp ? 'Already have an account?' : "Don't have an account?"}
                {' '}
                <button type="button" onClick={toggleMode} className="toggle-mode-btn">
                  {isSignUp ? 'Sign In' : 'Sign Up'}
                </button>
              </p>
            </div>
          </div>
        </div>

        {/* Right side - Visual Section */}
        <div className="login-visual-section">
          <div className="login-visual-content">
            <div className="visual-graphic">
              <div className="volunteer-cards">
                <div className="volunteer-card-demo">
                  <div className="demo-avatar green">MJ</div>
                  <div className="demo-info">
                    <div className="demo-name">Maria Johnson</div>
                    <div className="demo-role">Environmental Volunteer</div>
                    <div className="demo-hours">48 hours this month</div>
                  </div>
                </div>
                
                <div className="volunteer-card-demo">
                  <div className="demo-avatar blue">AS</div>
                  <div className="demo-info">
                    <div className="demo-name">Alex Smith</div>
                    <div className="demo-role">Education Mentor</div>
                    <div className="demo-hours">32 hours this month</div>
                  </div>
                </div>
                
                <div className="volunteer-card-demo">
                  <div className="demo-avatar purple">LB</div>
                  <div className="demo-info">
                    <div className="demo-name">Lisa Brown</div>
                    <div className="demo-role">Community Organizer</div>
                    <div className="demo-hours">56 hours this month</div>
                  </div>
                </div>
              </div>

              <div className="floating-stats">
                <div className="stat-bubble">
                  <div className="stat-number">2,500+</div>
                  <div className="stat-label">Active Volunteers</div>
                </div>
                <div className="stat-bubble">
                  <div className="stat-number">500+</div>
                  <div className="stat-label">Organizations</div>
                </div>
              </div>
            </div>

            <div className="visual-text">
              <h2>Making a Difference Together</h2>
              <p>Join thousands of volunteers and organizations creating positive change in communities worldwide.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;