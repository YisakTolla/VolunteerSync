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

    // Handle authentication errors globally
    if (error.response?.status === 401 || error.response?.status === 403) {
      console.warn('Authentication error - clearing local storage');
      localStorage.removeItem('authToken');
      localStorage.removeItem('user');
      // Don't auto-redirect here to avoid infinite loops
    }

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
      email: userData.email.trim().toLowerCase(), // 🔧 FIX: Always normalize email to lowercase
      password: userData.password,
      confirmPassword: userData.confirmPassword,
      userType: userData.userType // "VOLUNTEER" or "ORGANIZATION"
    };

    // Add type-specific fields
    if (userData.userType === 'VOLUNTEER') {
      requestPayload.firstName = userData.firstName.trim();
      requestPayload.lastName = userData.lastName.trim();
    } else if (userData.userType === 'ORGANIZATION') {
      requestPayload.organizationName = userData.organizationName.trim();
    }

    console.log('=== REGISTRATION DEBUG ===');
    console.log('Original email:', userData.email);
    console.log('Normalized email:', requestPayload.email);
    console.log('Sending registration data:', requestPayload);

    const response = await api.post('/auth/register', requestPayload);

    console.log('Registration response:', response.data);
    console.log('Full response structure:', response);

    // Handle successful registration
    if (response.data && response.data.token) {
      const token = response.data.token;

      // 🔧 FIX: Extract user data correctly from JwtResponse structure
      const user = response.data; // The user data is directly in response.data, not nested

      localStorage.setItem('authToken', token);
      localStorage.setItem('user', JSON.stringify(user));

      console.log('Registration successful! Saved to localStorage.');
      console.log('Token:', token);
      console.log('User data being saved:', user);
      console.log('User ID:', user.id);

      return { success: true, data: response.data };
    }

    return { success: false, message: 'Registration successful but no token received' };
  } catch (error) {
    console.error('=== REGISTRATION ERROR ===');
    console.error('Registration error:', error.response?.data);
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || 'Registration failed'
    };
  }
}

export async function registerWithGoogle(googleIdToken, userType) {
  try {
    console.log('Sending Google registration with token:', googleIdToken.substring(0, 20) + '...');
    console.log('User type:', userType);

    const response = await api.post('/auth/google', {
      googleToken: googleIdToken,
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
      message: error.response?.data?.error || error.response?.data?.message || 'Google registration failed'
    };
  }
}

// ==========================================
// LOGIN FUNCTIONS - 🔧 FIXED WITH EMAIL NORMALIZATION
// ==========================================

export async function loginUser(email, password) {
  try {
    const normalizedEmail = email.trim().toLowerCase(); // 🔧 FIX: Always normalize email to lowercase

    console.log('=== LOGIN DEBUG ===');
    console.log('Original email:', email);
    console.log('Normalized email:', normalizedEmail);
    console.log('Attempting login...');

    const response = await api.post('/auth/login', {
      email: normalizedEmail, // 🔧 FIX: Use normalized email
      password: password
    });

    console.log('Login response:', response.data);
    console.log('Full response structure:', response);

    if (response.data && response.data.token) {
      const token = response.data.token;

      // 🔧 FIX: Extract user data correctly from JwtResponse structure
      const user = response.data; // The user data is directly in response.data, not nested

      localStorage.setItem('authToken', token);
      localStorage.setItem('user', JSON.stringify(user));

      console.log('Login successful! Saved to localStorage.');
      console.log('Token:', token);
      console.log('User data being saved:', user);
      console.log('User ID:', user.id);

      return { success: true, data: response.data };
    }

    return { success: false, message: 'Login successful but no token received' };
  } catch (error) {
    console.error('=== LOGIN ERROR ===');
    console.error('Login error:', error.response?.data);
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || 'Login failed'
    };
  }
}

export async function loginWithGoogle(googleIdToken, userType) {
  try {
    console.log('Sending Google login with token:', googleIdToken.substring(0, 20) + '...');
    console.log('User type:', userType);

    const response = await api.post('/auth/google', {
      googleToken: googleIdToken,
      userType: userType // "VOLUNTEER" or "ORGANIZATION"
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
      message: error.response?.data?.error || error.response?.data?.message || 'Google login failed'
    };
  }
}

// ==========================================
// USER MANAGEMENT FUNCTIONS
// ==========================================

export function getCurrentUser() {
  try {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  } catch (error) {
    console.error('Error parsing user data from localStorage:', error);
    return null;
  }
}

export function updateCurrentUser(userData) {
  try {
    const currentUser = getCurrentUser();
    if (currentUser) {
      const updatedUser = { ...currentUser, ...userData };
      localStorage.setItem('user', JSON.stringify(updatedUser));
      return updatedUser;
    }
    return null;
  } catch (error) {
    console.error('Error updating user data in localStorage:', error);
    return null;
  }
}

export function isLoggedIn() {
  console.log('=== AUTH STATUS CHECK ===');
  const token = localStorage.getItem('authToken');
  const user = getCurrentUser();
  console.log('Token exists:', !!token);
  console.log('User exists:', !!user);
  console.log('User ID:', user?.id);
  console.log('========================');

  // Check that both token exists and is not the string "undefined"
  return token && token !== 'undefined' && token !== 'null' && user !== null;
}

export function logout() {
  console.log('=== LOGOUT ===');
  localStorage.removeItem('authToken');
  localStorage.removeItem('user');
  localStorage.removeItem('googleUser');
  console.log('Cleared localStorage');
}

// ==========================================
// PROFILE DATA FETCHING
// ==========================================

export async function getUserProfile() {
  try {
    console.log('=== FETCHING USER PROFILE ===');

    const user = getCurrentUser();
    if (!user) {
      throw new Error('No user logged in');
    }

    let response;
    const userType = user.userType;

    // Fetch from appropriate endpoint based on user type
    if (userType === 'VOLUNTEER') {
      response = await api.get('/volunteer-profiles/me');
    } else if (userType === 'ORGANIZATION') {
      response = await api.get('/organization-profiles/me');
    } else {
      // Fallback to user endpoint
      response = await api.get('/users/me');
    }

    console.log('Profile fetch response:', response.data);

    // Update local storage with fresh user data
    if (response.data) {
      const updatedUser = updateCurrentUser(response.data);
      return {
        success: true,
        data: response.data,
        user: updatedUser
      };
    }

    return { success: true, data: response.data };
  } catch (error) {
    console.error('Get profile error:', error.response?.data);

    // If unauthorized, clear local storage
    if (error.response?.status === 401 || error.response?.status === 403) {
      console.warn('Authentication failed - clearing storage');
      logout();
    }

    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || 'Failed to get profile'
    };
  }
}

export async function refreshUserData() {
  try {
    console.log('=== REFRESHING USER DATA ===');

    const result = await getUserProfile();
    if (result.success) {
      console.log('User data refreshed successfully');
      return result;
    } else {
      console.warn('Failed to refresh user data:', result.message);
      return result;
    }
  } catch (error) {
    console.error('Error refreshing user data:', error);
    return {
      success: false,
      message: 'Failed to refresh user data'
    };
  }
}

// ==========================================
// PROFILE COMPLETION FUNCTIONS
// ==========================================

export function isProfileComplete(user) {
  if (!user) return false;

  // Check if profileComplete flag is explicitly set
  if (user.profileComplete !== undefined) {
    return user.profileComplete;
  }

  // Fallback to manual checks based on user type
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

export function shouldRedirectToProfileSetup() {
  const user = getCurrentUser();
  return needsProfileSetup(user);
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
      message: error.response?.data?.error || error.response?.data?.message || 'Failed to complete profile'
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

      // Update user data if provided
      if (response.data.user) {
        localStorage.setItem('user', JSON.stringify(response.data.user));
      }

      return { success: true, data: response.data };
    }

    return { success: false, message: 'Token refresh failed' };
  } catch (error) {
    console.error('Token refresh error:', error.response?.data);
    logout(); // Clear invalid tokens
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || 'Token refresh failed'
    };
  }
}

export function getAuthToken() {
  return localStorage.getItem('authToken');
}

export function isTokenExpired() {
  const token = getAuthToken();
  if (!token) return true;

  try {
    // Simple JWT expiry check (this is a basic implementation)
    const payload = JSON.parse(atob(token.split('.')[1]));
    const currentTime = Date.now() / 1000;
    return payload.exp < currentTime;
  } catch (error) {
    console.error('Error checking token expiry:', error);
    return true; // Assume expired if we can't parse
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
  console.log('Profile Complete:', isProfileComplete(getCurrentUser()));
  console.log('Needs Setup:', needsProfileSetup(getCurrentUser()));
  console.log('Token Expired:', isTokenExpired());
  console.log('=====================');
}

// ==========================================
// UTILITY FUNCTIONS
// ==========================================

export function getUserDisplayName(user = null) {
  const currentUser = user || getCurrentUser();
  if (!currentUser) return 'User';

  if (currentUser.userType === 'ORGANIZATION' && currentUser.organizationName) {
    return currentUser.organizationName;
  }

  if (currentUser.userType === 'VOLUNTEER' && currentUser.firstName && currentUser.lastName) {
    return `${currentUser.firstName} ${currentUser.lastName}`;
  }

  return currentUser.email || 'User';
}

export function getUserInitials(user = null) {
  const currentUser = user || getCurrentUser();
  if (!currentUser) return 'U';

  if (currentUser.userType === 'ORGANIZATION' && currentUser.organizationName) {
    const words = currentUser.organizationName.split(' ').filter(word => word.length > 0);
    if (words.length >= 2) {
      return `${words[0][0]}${words[1][0]}`.toUpperCase();
    } else if (words.length === 1) {
      return words[0].substring(0, 2).toUpperCase();
    }
  }

  if (currentUser.userType === 'VOLUNTEER' && currentUser.firstName && currentUser.lastName) {
    return `${currentUser.firstName[0]}${currentUser.lastName[0]}`.toUpperCase();
  }

  if (currentUser.email) {
    return currentUser.email[0].toUpperCase();
  }

  return 'U';
}

export function getUserTypeDisplay(user = null) {
  const currentUser = user || getCurrentUser();
  if (!currentUser) return '';

  switch (currentUser.userType) {
    case 'ORGANIZATION':
      return '🏢 Organization';
    case 'VOLUNTEER':
      return '🙋‍♀️ Volunteer';
    default:
      return currentUser.userType;
  }
}


// Add these updated functions to your existing authService.js

// ==========================================
// IMPROVED PROFILE COMPLETION FUNCTIONS
// ==========================================

export function isProfileComplete(user = null) {
  const currentUser = user || getCurrentUser();

  console.log('=== CHECKING PROFILE COMPLETION ===');
  console.log('User data:', currentUser);

  if (!currentUser) {
    console.log('No user found');
    return false;
  }

  // Check if profileComplete flag is explicitly set by backend
  if (currentUser.profileComplete !== undefined) {
    console.log('Backend profileComplete flag:', currentUser.profileComplete);
    return currentUser.profileComplete;
  }

  // Manual checks based on user type
  const hasBasicInfo = currentUser.email;

  if (currentUser.userType === 'VOLUNTEER') {
    const required = {
      firstName: !!currentUser.firstName,
      lastName: !!currentUser.lastName,
      email: !!currentUser.email,
      bio: !!currentUser.bio,
      location: !!currentUser.location
    };

    const isComplete = Object.values(required).every(Boolean);

    console.log('Volunteer profile completion check:', {
      required,
      isComplete
    });

    return isComplete;
  }

  if (currentUser.userType === 'ORGANIZATION') {
    const required = {
      organizationName: !!currentUser.organizationName,
      email: !!currentUser.email,
      bio: !!currentUser.bio,
      location: !!currentUser.location
    };

    const isComplete = Object.values(required).every(Boolean);

    console.log('Organization profile completion check:', {
      required,
      isComplete
    });

    return isComplete;
  }

  console.log('Fallback to basic info check:', hasBasicInfo);
  return hasBasicInfo;
}

export function needsProfileSetup(user = null) {
  const currentUser = user || getCurrentUser();

  if (!currentUser || !isLoggedIn()) {
    console.log('No user or not logged in, no profile setup needed');
    return false;
  }

  const needs = !isProfileComplete(currentUser);
  console.log('User needs profile setup:', needs);
  return needs;
}

export function shouldRedirectToProfileSetup(user = null) {
  const currentUser = user || getCurrentUser();

  if (!isLoggedIn()) {
    console.log('Not logged in, should redirect to login');
    return false;
  }

  const shouldRedirect = needsProfileSetup(currentUser);
  console.log('Should redirect to profile setup:', shouldRedirect);
  return shouldRedirect;
}

// ==========================================
// PROFILE COMPLETION HELPERS
// ==========================================

export function getProfileCompletionStatus(user = null) {
  const currentUser = user || getCurrentUser();

  if (!currentUser) {
    return {
      isComplete: false,
      percentage: 0,
      missing: ['User not found']
    };
  }

  const missing = [];
  const required = [];

  // Common requirements
  if (!currentUser.email) {
    missing.push('Email');
  }
  required.push('Email');

  if (!currentUser.bio) {
    missing.push('Bio');
  }
  required.push('Bio');

  if (!currentUser.location) {
    missing.push('Location');
  }
  required.push('Location');

  // User type specific requirements
  if (currentUser.userType === 'VOLUNTEER') {
    if (!currentUser.firstName) {
      missing.push('First Name');
    }
    required.push('First Name');

    if (!currentUser.lastName) {
      missing.push('Last Name');
    }
    required.push('Last Name');

    // Optional but counted towards completion
    if (!currentUser.interests) {
      missing.push('Interests');
    }
    required.push('Interests');
  }

  if (currentUser.userType === 'ORGANIZATION') {
    if (!currentUser.organizationName) {
      missing.push('Organization Name');
    }
    required.push('Organization Name');

    // Optional but counted towards completion
    if (!currentUser.categories) {
      missing.push('Focus Areas');
    }
    required.push('Focus Areas');
  }

  const completed = required.length - missing.length;
  const percentage = Math.round((completed / required.length) * 100);
  const isComplete = missing.length === 0;

  return {
    isComplete,
    percentage,
    missing,
    completed,
    total: required.length
  };
}

// ==========================================
// REGISTRATION SUCCESS HANDLER
// ==========================================

export function handleRegistrationSuccess(userData) {
  console.log('=== HANDLING REGISTRATION SUCCESS ===');
  console.log('User data:', userData);

  // Mark user as needing profile setup since they just registered
  const userWithFlags = {
    ...userData,
    profileComplete: false, // Explicitly mark as incomplete
    isNewUser: true // Flag for new user
  };

  localStorage.setItem('user', JSON.stringify(userWithFlags));
  console.log('Updated user data in localStorage:', userWithFlags);

  return userWithFlags;
}

// ==========================================
// EXPORTS
// ==========================================

export default api;