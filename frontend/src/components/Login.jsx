import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { loginUser, registerUser, loginWithGoogle, registerWithGoogle, isLoggedIn } from '../services/authService';
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
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [googleLoaded, setGoogleLoaded] = useState(false);

  const GOOGLE_CLIENT_ID = '511877812187-6jg8ojddjq5qp6ci4nqgk6jn4vuea87a.apps.googleusercontent.com';

  // Redirect if already logged in
  useEffect(() => {
    if (isLoggedIn()) {
      navigate('/dashboard');
    }
  }, [navigate]);

  // Load Google OAuth script and initialize
  useEffect(() => {
    const loadGoogleScript = () => {
      // Check if script is already loaded
      if (window.google?.accounts?.id) {
        initializeGoogle();
        return;
      }

      // Check if script tag exists
      const existingScript = document.querySelector('script[src*="accounts.google.com"]');
      if (existingScript) {
        existingScript.addEventListener('load', initializeGoogle);
        return;
      }

      // Create and load the script
      const script = document.createElement('script');
      script.src = 'https://accounts.google.com/gsi/client';
      script.async = true;
      script.defer = true;
      script.onload = initializeGoogle;
      script.onerror = () => {
        console.error('Failed to load Google OAuth script');
        setGoogleLoaded(false);
      };
      document.head.appendChild(script);
    };

    const initializeGoogle = () => {
      if (window.google?.accounts?.id) {
        try {
          window.google.accounts.id.initialize({
            client_id: GOOGLE_CLIENT_ID,
            callback: handleGoogleResponse,
            auto_select: false,
            cancel_on_tap_outside: true
          });
          setGoogleLoaded(true);
          console.log('Google OAuth initialized successfully');
        } catch (error) {
          console.error('Failed to initialize Google OAuth:', error);
          setGoogleLoaded(false);
        }
      } else {
        console.error('Google accounts API not available');
        setGoogleLoaded(false);
      }
    };

    loadGoogleScript();
  }, []);

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
        // Registration
        if (formData.password !== formData.confirmPassword) {
          setError('Passwords do not match');
          setLoading(false);
          return;
        }

        let registrationData;
        
        if (formData.userType === 'organization') {
          registrationData = {
            organizationName: formData.organizationName,
            email: formData.email,
            password: formData.password,
            confirmPassword: formData.confirmPassword,
            userType: formData.userType.toUpperCase()
          };
        } else {
          registrationData = {
            firstName: formData.firstName,
            lastName: formData.lastName,
            email: formData.email,
            password: formData.password,
            confirmPassword: formData.confirmPassword,
            userType: formData.userType.toUpperCase()
          };
        }

        const result = await registerUser(registrationData);
        
        if (result.success) {
          setSuccess('Account created successfully! Redirecting...');
          setTimeout(() => {
            navigate('/dashboard');
          }, 1500);
        } else {
          setError(result.message);
        }
      } else {
        // Login
        const result = await loginUser(formData.email, formData.password);
        
        if (result.success) {
          navigate('/dashboard');
        } else {
          setError(result.message);
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
    console.log('Google login clicked, googleLoaded:', googleLoaded);
    
    if (!googleLoaded) {
      setError('Google OAuth is still loading. Please wait a moment and try again.');
      return;
    }

    if (!window.google?.accounts?.id) {
      setError('Google OAuth is not available. Please refresh the page and try again.');
      return;
    }

    setLoading(true);
    setError('');
    
    try {
      // Show Google sign-in prompt
      window.google.accounts.id.prompt((notification) => {
        console.log('Google prompt notification:', notification);
        
        if (notification.isNotDisplayed()) {
          setError('Google sign-in popup was blocked. Please allow popups and try again.');
          setLoading(false);
        } else if (notification.isSkippedMoment()) {
          setError('Google sign-in was cancelled. Please try again.');
          setLoading(false);
        }
      });
    } catch (error) {
      console.error('Google OAuth error:', error);
      setError('Google sign-in failed. Please try again.');
      setLoading(false);
    }
  };

  const handleGoogleResponse = async (response) => {
    console.log('Google response received:', response);
    
    if (!response.credential) {
      setError('Google authentication failed. Please try again.');
      setLoading(false);
      return;
    }

    setLoading(true);
    setError('');

    try {
      let result;
      
      if (isSignUp) {
        result = await registerWithGoogle(
          response.credential, 
          formData.userType.toUpperCase()
        );
      } else {
        result = await loginWithGoogle(response.credential);
      }
      
      if (result.success) {
        setSuccess(`${isSignUp ? 'Account created' : 'Signed in'} successfully! Redirecting...`);
        setTimeout(() => {
          navigate('/dashboard');
        }, 1500);
      } else {
        setError(result.message || 'Google authentication failed. Please try again.');
      }
    } catch (error) {
      console.error('Google auth error:', error);
      setError('Google authentication failed. Please try again.');
    } finally {
      setLoading(false);
    }
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
            <div className="login-logo" onClick={onBackToHome} style={{ cursor: 'pointer' }}>
              <span className="login-logo-icon">🤝</span>
              <span className="login-logo-text">VolunteerSync</span>
            </div>

            {/* Header */}
            <div className="login-header">
              <h1 className="login-title">
                {isSignUp ? 'Join VolunteerSync' : 'Welcome back'}
              </h1>
              <p className="login-subtitle">
                {isSignUp 
                  ? 'Start making a difference in your community today'
                  : 'Sign in to continue your volunteer journey'
                }
              </p>
            </div>

            {/* Error/Success Messages */}
            {error && (
              <div className="error-message" style={{
                backgroundColor: '#ffebee',
                color: '#c62828',
                padding: '12px',
                borderRadius: '8px',
                marginBottom: '16px',
                fontSize: '14px'
              }}>
                {error}
              </div>
            )}

            {success && (
              <div className="success-message" style={{
                backgroundColor: '#e8f5e8',
                color: '#2e7d32',
                padding: '12px',
                borderRadius: '8px',
                marginBottom: '16px',
                fontSize: '14px'
              }}>
                {success}
              </div>
            )}

            {/* User Type Selection (Sign Up only) */}
            {isSignUp && (
              <div className="user-type-selector">
                <p className="user-type-label">I want to join as:</p>
                <div className="user-type-buttons">
                  <button
                    type="button"
                    className={`user-type-btn ${formData.userType === 'volunteer' ? 'active' : ''}`}
                    onClick={() => handleUserTypeChange('volunteer')}
                  >
                    <span className="user-type-icon">👥</span>
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
              disabled={loading || !googleLoaded}
              style={{
                opacity: googleLoaded ? 1 : 0.6,
                cursor: googleLoaded && !loading ? 'pointer' : 'not-allowed'
              }}
            >
              <svg className="google-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
              </svg>
              {loading ? 'Connecting...' : googleLoaded ? 'Continue with Google' : 'Loading Google...'}
            </button>

            {/* Divider */}
            <div className="login-divider">
              <span className="login-divider-text">or</span>
            </div>

            {/* Form */}
            <form className="login-form" onSubmit={handleSubmit}>
              {/* Name fields for Sign Up */}
              {isSignUp && (
                <>
                  {formData.userType === 'organization' ? (
                    // Organization Name field
                    <div className="form-group">
                      <label className="form-label" htmlFor="organizationName">Organization Name</label>
                      <input
                        type="text"
                        id="organizationName"
                        name="organizationName"
                        className="form-input"
                        value={formData.organizationName}
                        onChange={handleInputChange}
                        required={isSignUp}
                        placeholder="Enter your organization name"
                        disabled={loading}
                      />
                    </div>
                  ) : (
                    // First Name and Last Name fields for volunteers
                    <div className="form-row">
                      <div className="form-group">
                        <label className="form-label" htmlFor="firstName">First Name</label>
                        <input
                          type="text"
                          id="firstName"
                          name="firstName"
                          className="form-input"
                          value={formData.firstName}
                          onChange={handleInputChange}
                          required={isSignUp}
                          placeholder="Enter your first name"
                          disabled={loading}
                        />
                      </div>
                      <div className="form-group">
                        <label className="form-label" htmlFor="lastName">Last Name</label>
                        <input
                          type="text"
                          id="lastName"
                          name="lastName"
                          className="form-input"
                          value={formData.lastName}
                          onChange={handleInputChange}
                          required={isSignUp}
                          placeholder="Enter your last name"
                          disabled={loading}
                        />
                      </div>
                    </div>
                  )}
                </>
              )}

              {/* Email */}
              <div className="form-group">
                <label className="form-label" htmlFor="email">Email Address</label>
                <input
                  type="email"
                  id="email"
                  name="email"
                  className="form-input"
                  value={formData.email}
                  onChange={handleInputChange}
                  required
                  placeholder="Enter your email"
                  disabled={loading}
                />
              </div>

              {/* Password */}
              <div className="form-group">
                <label className="form-label" htmlFor="password">Password</label>
                <div className="password-input-container">
                  <input
                    type={showPassword ? 'text' : 'password'}
                    id="password"
                    name="password"
                    className="form-input"
                    value={formData.password}
                    onChange={handleInputChange}
                    required
                    placeholder="Enter your password"
                    disabled={loading}
                    minLength="6"
                  />
                  <button
                    type="button"
                    className="password-toggle"
                    onClick={() => setShowPassword(!showPassword)}
                  >
                    {showPassword ? '👁️' : '👁️‍🗨️'}
                  </button>
                </div>
              </div>

              {/* Confirm Password for Sign Up */}
              {isSignUp && (
                <div className="form-group">
                  <label className="form-label" htmlFor="confirmPassword">Confirm Password</label>
                  <input
                    type="password"
                    id="confirmPassword"
                    name="confirmPassword"
                    className="form-input"
                    value={formData.confirmPassword}
                    onChange={handleInputChange}
                    required={isSignUp}
                    placeholder="Confirm your password"
                    disabled={loading}
                  />
                </div>
              )}

              {/* Forgot Password Link (Login only) */}
              {!isSignUp && (
                <div className="forgot-password">
                  <a href="#" className="forgot-password-link">Forgot your password?</a>
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
            </form>

            {/* Terms (Sign Up only) */}
            {isSignUp && (
              <p className="login-terms">
                By creating an account, you agree to our{' '}
                <a href="#" className="login-link">Terms of Service</a> and{' '}
                <a href="#" className="login-link">Privacy Policy</a>
              </p>
            )}

            {/* Toggle Mode */}
            <div className="login-toggle">
              <p>
                {isSignUp ? 'Already have an account?' : "Don't have an account?"}{' '}
                <button
                  type="button"
                  className="toggle-mode-btn"
                  onClick={toggleMode}
                  disabled={loading}
                >
                  {isSignUp ? 'Sign In' : 'Sign Up'}
                </button>
              </p>
            </div>
          </div>
        </div>

        {/* Right side - Visual */}
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
                  <div className="demo-avatar purple">AS</div>
                  <div className="demo-info">
                    <div className="demo-name">Alex Smith</div>
                    <div className="demo-role">Education Mentor</div>
                    <div className="demo-hours">32 hours this month</div>
                  </div>
                </div>
                
                <div className="volunteer-card-demo">
                  <div className="demo-avatar orange">LB</div>
                  <div className="demo-info">
                    <div className="demo-name">Lisa Brown</div>
                    <div className="demo-role">Community Organizer</div>
                    <div className="demo-hours">56 hours this month</div>
                  </div>
                </div>

                <div className="volunteer-card-demo">
                  <div className="demo-avatar blue">DC</div>
                  <div className="demo-info">
                    <div className="demo-name">David Chen</div>
                    <div className="demo-role">Healthcare Assistant</div>
                    <div className="demo-hours">28 hours this month</div>
                  </div>
                </div>

                <div className="volunteer-card-demo">
                  <div className="demo-avatar pink">SR</div>
                  <div className="demo-info">
                    <div className="demo-name">Sarah Rodriguez</div>
                    <div className="demo-role">Food Bank Coordinator</div>
                    <div className="demo-hours">42 hours this month</div>
                  </div>
                </div>

                <div className="volunteer-card-demo">
                  <div className="demo-avatar black">JW</div>
                  <div className="demo-info">
                    <div className="demo-name">James Wilson</div>
                    <div className="demo-role">Youth Sports Coach</div>
                    <div className="demo-hours">36 hours this month</div>
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
              <h2>Join a community of changemakers</h2>
              <p>Connect with passionate volunteers and meaningful opportunities in your area.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;