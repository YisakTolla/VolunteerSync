import axios from 'axios';

// ==========================================
// API BASE CONFIGURATION
// ==========================================

const API_BASE_URL = 'http://localhost:8080/api';

// Create axios instance with base configuration
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests if available
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Add response interceptor for debugging
api.interceptors.response.use(
  (response) => {
    console.log('API Response:', response);
    return response;
  },
  (error) => {
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

// ==========================================
// REGISTRATION FUNCTIONS
// ==========================================

export async function registerUser(userData) {
  try {
    // Build request payload based on user type
    let requestPayload = {
      email: userData.email,
      password: userData.password,
      confirmPassword: userData.confirmPassword,
      userType: userData.userType // "VOLUNTEER" or "ORGANIZATION"
    };

    // Add type-specific fields
    if (userData.userType === 'VOLUNTEER') {
      requestPayload.firstName = userData.firstName;
      requestPayload.lastName = userData.lastName;
    } else if (userData.userType === 'ORGANIZATION') {
      requestPayload.organizationName = userData.organizationName;
    }

    console.log('Sending registration data:', requestPayload);

    const response = await api.post('/auth/register', requestPayload);

    // Handle successful registration
    if (response.data && (response.data.token || response.data.data?.token)) {
      const token = response.data.token || response.data.data.token;
      const user = response.data.user || response.data.data?.user || response.data.data;

      localStorage.setItem('authToken', token);
      localStorage.setItem('user', JSON.stringify(user));
      return { success: true, data: response.data };
    }

    return { success: false, message: 'Registration successful but no token received' };
  } catch (error) {
    console.error('Registration error:', error.response?.data);
    return {
      success: false,
      message: error.response?.data?.message || 'Registration failed'
    };
  }
}

export async function registerWithGoogle(googleIdToken, userType) {
  try {
    console.log('Sending Google registration with token:', googleIdToken.substring(0, 20) + '...');
    console.log('User type:', userType);

    const response = await api.post('/auth/google', {
      googleToken: googleIdToken, // This should match your backend expectation
      userType: userType // "VOLUNTEER" or "ORGANIZATION"
    });

    console.log('Google registration response:', response.data);

    if (response.data && (response.data.token || response.data.data?.token)) {
      const token = response.data.token || response.data.data.token;
      const user = response.data.user || response.data.data?.user || response.data.data;

      localStorage.setItem('authToken', token);
      localStorage.setItem('user', JSON.stringify(user));
      return { success: true, data: response.data };
    }

    return { success: false, message: 'Google registration successful but no token received' };
  } catch (error) {
    console.error('Google registration error:', error.response?.data);
    return {
      success: false,
      message: error.response?.data?.message || 'Google registration failed'
    };
  }
}

// ==========================================
// LOGIN FUNCTIONS
// ==========================================

export async function loginUser(email, password) {
  try {
    const response = await api.post('/auth/login', {
      email: email,
      password: password
    });

    if (response.data && (response.data.token || response.data.data?.token)) {
      const token = response.data.token || response.data.data.token;
      const user = response.data.user || response.data.data?.user || response.data.data;

      localStorage.setItem('authToken', token);
      localStorage.setItem('user', JSON.stringify(user));
      return { success: true, data: response.data };
    }

    return { success: false, message: 'Login successful but no token received' };
  } catch (error) {
    console.error('Login error:', error.response?.data);
    return {
      success: false,
      message: error.response?.data?.message || 'Login failed'
    };
  }
}

export async function loginWithGoogle(googleIdToken) {
  try {
    console.log('Sending Google login with token:', googleIdToken.substring(0, 20) + '...');

    // For login, we don't specify user type - backend will use existing user's type
    const response = await api.post('/auth/google', {
      googleToken: googleIdToken,
      userType: "VOLUNTEER" // Default, backend will override with existing user type
    });

    console.log('Google login response:', response.data);

    if (response.data && (response.data.token || response.data.data?.token)) {
      const token = response.data.token || response.data.data.token;
      const user = response.data.user || response.data.data?.user || response.data.data;

      localStorage.setItem('authToken', token);
      localStorage.setItem('user', JSON.stringify(user));
      return { success: true, data: response.data };
    }

    return { success: false, message: 'Google login successful but no token received' };
  } catch (error) {
    console.error('Google login error:', error.response?.data);
    return {
      success: false,
      message: error.response?.data?.message || 'Google login failed'
    };
  }
}

// Add these functions to your existing authService.js

export function isProfileComplete(user) {
  if (!user) return false;

  // Check required fields based on user type
  if (user.userType === 'VOLUNTEER') {
    return !!(
      user.firstName &&
      user.lastName &&
      user.email &&
      user.bio && // Profile completion indicator
      user.location // Basic profile info
    );
  } else if (user.userType === 'ORGANIZATION') {
    return !!(
      user.organizationName &&
      user.email &&
      user.bio && // Profile completion indicator
      user.location // Basic profile info
    );
  }

  return false;
}

export function needsProfileSetup(user) {
  return user && !isProfileComplete(user);
}

// Complete user profile after signup
export async function completeProfile(profileData) {
  try {
    const response = await api.put('/auth/complete-profile', profileData);

    if (response.data && response.data.user) {
      // Update localStorage with complete user data
      localStorage.setItem('user', JSON.stringify(response.data.user));
      return { success: true, data: response.data };
    }

    return { success: false, message: 'Profile update failed' };
  } catch (error) {
    console.error('Profile completion error:', error.response?.data);
    return {
      success: false,
      message: error.response?.data?.message || 'Failed to complete profile'
    };
  }
}

// Check if user needs onboarding on login/registration
export function shouldRedirectToProfileSetup() {
  const user = getCurrentUser();
  return needsProfileSetup(user);
}

// ==========================================
// UTILITY FUNCTIONS
// ==========================================

export function getCurrentUser() {
  const user = localStorage.getItem('user');
  return user ? JSON.parse(user) : null;
}

export function isLoggedIn() {
  const token = localStorage.getItem('authToken');
  const user = localStorage.getItem('user');
  return token !== null && user !== null;
}

export function logout() {
  localStorage.removeItem('authToken');
  localStorage.removeItem('user');
  // Clear any other auth-related data
  localStorage.removeItem('googleUser');
}

export async function getUserProfile() {
  try {
    const response = await api.get('/auth/me');

    // Update local storage with fresh user data
    if (response.data && response.data.data) {
      localStorage.setItem('user', JSON.stringify(response.data.data));
    }

    return { success: true, data: response.data };
  } catch (error) {
    console.error('Get profile error:', error.response?.data);

    // If unauthorized, clear local storage
    if (error.response?.status === 401 || error.response?.status === 403) {
      logout();
    }

    return {
      success: false,
      message: error.response?.data?.message || 'Failed to get profile'
    };
  }
}

// ==========================================
// TOKEN MANAGEMENT
// ==========================================

export async function refreshToken() {
  try {
    const response = await api.post('/auth/refresh');

    if (response.data && response.data.token) {
      localStorage.setItem('authToken', response.data.token);
      return { success: true };
    }

    return { success: false, message: 'Token refresh failed' };
  } catch (error) {
    console.error('Token refresh error:', error.response?.data);
    logout(); // Clear invalid tokens
    return {
      success: false,
      message: error.response?.data?.message || 'Token refresh failed'
    };
  }
}



// ==========================================
// HELPER FUNCTIONS FOR DEBUGGING
// ==========================================

export function debugAuthState() {
  console.log('=== Auth Debug Info ===');
  console.log('Token:', localStorage.getItem('authToken')?.substring(0, 20) + '...');
  console.log('User:', getCurrentUser());
  console.log('Is Logged In:', isLoggedIn());
  console.log('=====================');
}

export default api;