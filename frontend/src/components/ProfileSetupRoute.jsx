import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { isLoggedIn, getCurrentUser } from '../services/authService';
import './ProfileSetupRoute.css';

// Helper function to check if user has completed profile setup
const isProfileComplete = () => {
  try {
    const user = getCurrentUser();
    
    console.log('=== PROFILE COMPLETION CHECK ===');
    console.log('User data:', user);
    
    if (!user || !user.id) {
      console.log('No user or user ID found');
      return false;
    }
    
    // Check if profileComplete flag is explicitly set by backend
    if (user.profileComplete !== undefined) {
      console.log('Using profileComplete flag:', user.profileComplete);
      return user.profileComplete;
    }
    
    // Manual checks based on user type
    const hasBasicInfo = user.email;
    console.log('Has basic info (email):', hasBasicInfo);
    
    if (user.userType === 'VOLUNTEER') {
      const isComplete = hasBasicInfo && user.firstName && user.lastName && user.bio && user.location;
      console.log('Volunteer profile complete check:', {
        hasBasicInfo,
        firstName: !!user.firstName,
        lastName: !!user.lastName,
        bio: !!user.bio,
        location: !!user.location,
        result: isComplete
      });
      return isComplete;
    }
    
    if (user.userType === 'ORGANIZATION') {
      const isComplete = hasBasicInfo && user.organizationName && user.bio && user.location;
      console.log('Organization profile complete check:', {
        hasBasicInfo,
        organizationName: !!user.organizationName,
        bio: !!user.bio,
        location: !!user.location,
        result: isComplete
      });
      return isComplete;
    }
    
    // Default: just check basic info
    console.log('Defaulting to basic info check:', hasBasicInfo);
    return hasBasicInfo;
  } catch (error) {
    console.error('Error checking profile completion:', error);
    return false;
  }
};

// Check if user needs profile setup
const needsProfileSetup = () => {
  if (!isLoggedIn()) {
    console.log('User not logged in, no profile setup needed');
    return false;
  }
  
  const needs = !isProfileComplete();
  console.log('User needs profile setup:', needs);
  return needs;
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
      console.log('=== PROFILE SETUP ROUTE CHECK ===');
      
      try {
        if (!isLoggedIn()) {
          // Not logged in - send to login
          console.log('User not logged in, redirecting to login');
          setRedirectTo('/login');
        } else if (isProfileComplete()) {
          // Profile already complete - send to dashboard
          console.log('Profile complete, redirecting to dashboard');
          setRedirectTo('/dashboard');
        } else {
          // Profile incomplete - allow access to profile setup
          console.log('Profile incomplete, allowing access to setup');
          setRedirectTo(null);
        }
      } catch (error) {
        console.error('Error in ProfileSetupRoute check:', error);
        // On error, redirect to login to be safe
        setRedirectTo('/login');
      }
      
      setLoading(false);
    };

    // Add a small delay to ensure localStorage is ready
    const timeoutId = setTimeout(checkAccess, 100);
    
    return () => clearTimeout(timeoutId);
  }, []);

  if (loading) {
    return (
      <div className="route-loading-container">
        <div className="route-loading-content">
          <div className="route-loading-spinner"></div>
          <p className="route-loading-text">
            Setting up your profile...
          </p>
        </div>
      </div>
    );
  }

  if (redirectTo) {
    console.log('ProfileSetupRoute redirecting to:', redirectTo);
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
      console.log('=== PROTECTED ROUTE CHECK ===');
      
      try {
        if (!isLoggedIn()) {
          // Not logged in - send to login
          console.log('User not logged in, redirecting to login');
          setRedirectTo('/login');
        } else if (needsProfileSetup()) {
          // Profile incomplete - send to profile setup
          console.log('Profile incomplete, redirecting to setup');
          setRedirectTo('/profile-setup');
        } else {
          // All good - allow access
          console.log('All checks passed, allowing access');
          setRedirectTo(null);
        }
      } catch (error) {
        console.error('Error in ProtectedRoute check:', error);
        // On error, redirect to login to be safe
        setRedirectTo('/login');
      }
      
      setLoading(false);
    };

    // Add a small delay to ensure localStorage is ready
    const timeoutId = setTimeout(checkAccess, 100);
    
    return () => clearTimeout(timeoutId);
  }, []);

  if (loading) {
    return (
      <div className="route-loading-container">
        <div className="route-loading-content">
          <div className="route-loading-spinner"></div>
          <p className="route-loading-text">
            Loading...
          </p>
        </div>
      </div>
    );
  }

  if (redirectTo) {
    console.log('ProtectedRoute redirecting to:', redirectTo);
    return <Navigate to={redirectTo} replace />;
  }

  return children;
};

// Export utility functions for use in other components
export { isProfileComplete, needsProfileSetup };