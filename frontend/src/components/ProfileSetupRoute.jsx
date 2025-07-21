import React from 'react';
import { Navigate } from 'react-router-dom';
import { isLoggedIn, shouldRedirectToProfileSetup } from '../services/authService';

const ProfileSetupRoute = ({ children }) => {
  if (!isLoggedIn()) {
    return <Navigate to="/login" replace />;
  }

  if (!shouldRedirectToProfileSetup()) {
    // Profile is already complete, redirect to dashboard
    return <Navigate to="/dashboard" replace />;
  }

  return children;
};

const ProtectedRoute = ({ children }) => {
  if (!isLoggedIn()) {
    return <Navigate to="/login" replace />;
  }

  if (shouldRedirectToProfileSetup()) {
    // Profile is incomplete, redirect to setup
    return <Navigate to="/profile-setup" replace />;
  }

  return children;
};

export { ProfileSetupRoute, ProtectedRoute };