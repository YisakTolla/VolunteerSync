import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { isLoggedIn } from '../services/authService';

// Helper function to check if user has completed profile setup
const isProfileComplete = () => {
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    
    // Basic profile completion checks
    if (!user.id) return false;
    
    // Check if user has completed essential profile information
    // You can customize these requirements based on your needs
    const hasBasicInfo = user.email;
    
    // For volunteers, check if they have first/last name
    if (user.userType === 'VOLUNTEER') {
      return hasBasicInfo && user.firstName && user.lastName;
    }
    
    // For organizations, check if they have organization name
    if (user.userType === 'ORGANIZATION') {
      return hasBasicInfo && user.organizationName;
    }
    
    return hasBasicInfo;
  } catch (error) {
    console.error('Error checking profile completion:', error);
    return false;
  }
};

// Check if user needs profile setup
const needsProfileSetup = () => {
  if (!isLoggedIn()) return false;
  return !isProfileComplete();
};

/**
 * ProfileSetupRoute - Only allows access if user is logged in but profile is incomplete
 * Redirects completed profiles to dashboard
 * Redirects non-logged-in users to login
 */
export const ProfileSetupRoute = ({ children }) => {
  const [loading, setLoading] = useState(true);
  const [redirectTo, setRedirectTo] = useState(null);

  useEffect(() => {
    const checkAccess = () => {
      if (!isLoggedIn()) {
        // Not logged in - send to login
        setRedirectTo('/login');
      } else if (isProfileComplete()) {
        // Profile already complete - send to dashboard
        setRedirectTo('/dashboard');
      } else {
        // Profile incomplete - allow access to profile setup
        setRedirectTo(null);
      }
      setLoading(false);
    };

    checkAccess();
  }, []);

  if (loading) {
    return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        backgroundColor: '#f9fafb'
      }}>
        <div style={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          gap: '1rem'
        }}>
          <div style={{
            width: '40px',
            height: '40px',
            border: '4px solid #e5e7eb',
            borderTop: '4px solid #10b981',
            borderRadius: '50%',
            animation: 'spin 1s linear infinite'
          }}></div>
          <p style={{ color: '#6b7280', fontSize: '0.875rem' }}>
            Setting up your profile...
          </p>
        </div>
      </div>
    );
  }

  if (redirectTo) {
    return <Navigate to={redirectTo} replace />;
  }

  return children;
};

/**
 * ProtectedRoute - Only allows access if user is logged in AND profile is complete
 * Redirects incomplete profiles to profile setup
 * Redirects non-logged-in users to login
 */
export const ProtectedRoute = ({ children }) => {
  const [loading, setLoading] = useState(true);
  const [redirectTo, setRedirectTo] = useState(null);

  useEffect(() => {
    const checkAccess = () => {
      if (!isLoggedIn()) {
        // Not logged in - send to login
        setRedirectTo('/login');
      } else if (needsProfileSetup()) {
        // Profile incomplete - send to profile setup
        setRedirectTo('/profile-setup');
      } else {
        // All good - allow access
        setRedirectTo(null);
      }
      setLoading(false);
    };

    checkAccess();
  }, []);

  if (loading) {
    return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        backgroundColor: '#f9fafb'
      }}>
        <div style={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          gap: '1rem'
        }}>
          <div style={{
            width: '40px',
            height: '40px',
            border: '4px solid #e5e7eb',
            borderTop: '4px solid #10b981',
            borderRadius: '50%',
            animation: 'spin 1s linear infinite'
          }}></div>
          <p style={{ color: '#6b7280', fontSize: '0.875rem' }}>
            Loading...
          </p>
        </div>
      </div>
    );
  }

  if (redirectTo) {
    return <Navigate to={redirectTo} replace />;
  }

  return children;
};

// Export utility functions for use in other components
export { isProfileComplete, needsProfileSetup };

// Add CSS for spinner animation (you can move this to a CSS file if preferred)
const style = document.createElement('style');
style.textContent = `
  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
`;
document.head.appendChild(style);