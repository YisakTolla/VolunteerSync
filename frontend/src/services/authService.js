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

// 🔧 FIXED: Proper token interceptor with refresh handling
let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  
  failedQueue = [];
};

// Add token to requests if available
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token && token !== 'undefined' && token !== 'null') {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 🔧 FIXED: Proper response interceptor with automatic token refresh
api.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;

    // Handle 401 unauthorized errors
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        // If we're already refreshing, queue this request
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        }).then(token => {
          originalRequest.headers.Authorization = `Bearer ${token}`;
          return api(originalRequest);
        }).catch(err => {
          return Promise.reject(err);
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        // Attempt to refresh the token
        const refreshResult = await performTokenRefresh();
        
        if (refreshResult.success) {
          const newToken = refreshResult.token;
          localStorage.setItem('authToken', newToken);
          
          // Update the original request with new token
          originalRequest.headers.Authorization = `Bearer ${newToken}`;
          
          // Process all queued requests
          processQueue(null, newToken);
          
          // Retry the original request
          return api(originalRequest);
        } else {
          // Refresh failed, clear auth and redirect
          processQueue(error, null);
          logoutWithCleanup();
          window.location.href = '/login';
          return Promise.reject(error);
        }
      } catch (refreshError) {
        processQueue(refreshError, null);
        logoutWithCleanup();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    // For other errors, just reject
    return Promise.reject(error);
  }
);

// ==========================================
// TOKEN MANAGEMENT - 🔧 FIXED
// ==========================================

/**
 * 🔧 FIXED: Separate function for token refresh that doesn't use the interceptor
 */
async function performTokenRefresh() {
  try {
    const currentToken = localStorage.getItem('authToken');
    
    if (!currentToken || currentToken === 'undefined' || currentToken === 'null') {
      throw new Error('No token available for refresh');
    }

    // Create a separate axios instance for refresh to avoid interceptor loops
    const refreshApi = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${currentToken}`
      },
    });

    console.log('🔄 Attempting token refresh...');
    const response = await refreshApi.post('/auth/refresh');

    if (response.data && response.data.token) {
      console.log('✅ Token refresh successful');
      
      // Update user data if provided
      if (response.data.user) {
        localStorage.setItem('user', JSON.stringify(response.data.user));
      }

      return { 
        success: true, 
        token: response.data.token,
        data: response.data 
      };
    }

    throw new Error('No token in refresh response');
  } catch (error) {
    console.error('❌ Token refresh failed:', error.response?.data || error.message);
    return {
      success: false,
      message: error.response?.data?.error || error.message || 'Token refresh failed'
    };
  }
}

export function getAuthToken() {
  return localStorage.getItem('authToken');
}

export function isTokenExpired(token = null) {
  const authToken = token || localStorage.getItem('authToken');
  if (!authToken || authToken === 'undefined' || authToken === 'null') return true;

  try {
    const payload = JSON.parse(atob(authToken.split('.')[1]));
    return payload.exp * 1000 < Date.now();
  } catch (error) {
    console.error('Error checking token expiry:', error);
    return true;
  }
}

export function getTokenTimeRemaining(token = null) {
  const authToken = token || localStorage.getItem('authToken');
  if (!authToken || authToken === 'undefined' || authToken === 'null') return 0;

  try {
    const payload = JSON.parse(atob(authToken.split('.')[1]));
    const timeRemaining = payload.exp * 1000 - Date.now();
    return Math.max(0, timeRemaining);
  } catch (error) {
    return 0;
  }
}

export function shouldRefreshToken(token = null) {
  const timeRemaining = getTokenTimeRemaining(token);
  // Refresh if less than 5 minutes remaining
  return timeRemaining > 0 && timeRemaining < 5 * 60 * 1000;
}

// 🔧 FIXED: Public refresh function
export async function refreshToken() {
  return await performTokenRefresh();
}

// ==========================================
// REGISTRATION FUNCTIONS
// ==========================================

export async function registerUser(userData) {
  try {
    // Build request payload based on user type
    let requestPayload = {
      email: userData.email.trim().toLowerCase(),
      password: userData.password,
      confirmPassword: userData.confirmPassword,
      userType: userData.userType
    };

    // Add type-specific fields
    if (userData.userType === 'VOLUNTEER') {
      requestPayload.firstName = userData.firstName.trim();
      requestPayload.lastName = userData.lastName.trim();
    } else if (userData.userType === 'ORGANIZATION') {
      requestPayload.organizationName = userData.organizationName.trim();
    }

    console.log('🔄 Registering user...', { email: requestPayload.email, userType: requestPayload.userType });

    const response = await api.post('/auth/register', requestPayload);

    console.log('✅ Registration successful:', response.data);

    // Handle successful registration
    if (response.data && response.data.token) {
      const token = response.data.token;
      const user = response.data;

      localStorage.setItem('authToken', token);
      localStorage.setItem('user', JSON.stringify(user));

      console.log('💾 Saved auth data to localStorage');

      return { success: true, data: response.data };
    }

    return { success: false, message: 'Registration successful but no token received' };
  } catch (error) {
    console.error('❌ Registration error:', error.response?.data);
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || 'Registration failed'
    };
  }
}

export async function registerWithGoogle(googleIdToken, userType) {
  try {
    console.log('🔄 Google registration...', { userType });

    const response = await api.post('/auth/google', {
      googleToken: googleIdToken,
      userType: userType
    });

    console.log('✅ Google registration successful:', response.data);

    if (response.data && response.data.token) {
      const token = response.data.token;
      const user = response.data;

      localStorage.setItem('authToken', token);
      localStorage.setItem('user', JSON.stringify(user));

      return { success: true, data: response.data };
    }

    return { success: false, message: 'Google registration successful but no token received' };
  } catch (error) {
    console.error('❌ Google registration error:', error.response?.data);
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || 'Google registration failed'
    };
  }
}

// ==========================================
// LOGIN FUNCTIONS - 🔧 FIXED
// ==========================================

export async function loginUser(email, password) {
  try {
    const normalizedEmail = email.trim().toLowerCase();

    console.log('🔄 Logging in user...', { email: normalizedEmail });

    const response = await api.post('/auth/login', {
      email: normalizedEmail,
      password: password
    });

    console.log('✅ Login successful:', response.data);

    if (response.data && response.data.token) {
      const token = response.data.token;
      const user = response.data;

      localStorage.setItem('authToken', token);
      localStorage.setItem('user', JSON.stringify(user));

      console.log('💾 Saved auth data to localStorage');

      return { success: true, data: response.data };
    }

    return { success: false, message: 'Login successful but no token received' };
  } catch (error) {
    console.error('❌ Login error:', error.response?.data);
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || 'Login failed'
    };
  }
}

export async function loginWithGoogle(googleIdToken, userType) {
  try {
    console.log('🔄 Google login...', { userType });

    const response = await api.post('/auth/google', {
      googleToken: googleIdToken,
      userType: userType
    });

    console.log('✅ Google login successful:', response.data);

    if (response.data && response.data.token) {
      const token = response.data.token;
      const user = response.data;

      localStorage.setItem('authToken', token);
      localStorage.setItem('user', JSON.stringify(user));

      return { success: true, data: response.data };
    }

    return { success: false, message: 'Google login successful but no token received' };
  } catch (error) {
    console.error('❌ Google login error:', error.response?.data);
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
    return user && user !== 'undefined' && user !== 'null' ? JSON.parse(user) : null;
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
  const token = localStorage.getItem('authToken');
  const user = getCurrentUser();
  
  const hasValidToken = token && token !== 'undefined' && token !== 'null' && !isTokenExpired(token);
  const hasUser = user !== null;
  
  console.log('🔍 Auth status:', { hasValidToken, hasUser, userId: user?.id });
  
  return hasValidToken && hasUser;
}

export function logout() {
  console.log('🚪 Logging out...');
  localStorage.removeItem('authToken');
  localStorage.removeItem('user');
  localStorage.removeItem('googleUser');
  console.log('🧹 Cleared localStorage');
}

export function logoutWithCleanup() {
  logout();
}

// ==========================================
// PROFILE DATA FETCHING - 🔧 FIXED
// ==========================================

export async function getUserProfile() {
  try {
    console.log('🔄 Fetching user profile...');

    const user = getCurrentUser();
    if (!user) {
      throw new Error('No user logged in');
    }

    // Ensure we have a valid token before making the request
    const token = getAuthToken();
    if (!token || isTokenExpired(token)) {
      console.log('Token invalid, attempting refresh...');
      const refreshResult = await refreshToken();
      if (!refreshResult.success) {
        throw new Error('Token refresh failed');
      }
    }

    let response;
    const userType = user.userType;

    // Fetch from appropriate endpoint based on user type
    if (userType === 'VOLUNTEER') {
      response = await api.get('/volunteer-profiles/me');
    } else if (userType === 'ORGANIZATION') {
      response = await api.get('/organization-profiles/me');
    } else {
      response = await api.get('/users/me');
    }

    console.log('✅ Profile fetch successful:', response.data);

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
    console.error('❌ Get profile error:', error.response?.data);

    // If unauthorized, the interceptor will handle it
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || 'Failed to get profile'
    };
  }
}

// ==========================================
// PROFILE COMPLETION FUNCTIONS
// ==========================================

export function isProfileComplete(user = null) {
  const currentUser = user || getCurrentUser();

  if (!currentUser) {
    return false;
  }

  // Check if profileComplete flag is explicitly set by backend
  if (currentUser.profileComplete !== undefined) {
    return currentUser.profileComplete;
  }

  // Manual checks based on user type
  const hasBasicInfo = currentUser.email;

  if (currentUser.userType === 'VOLUNTEER') {
    const required = {
      firstName: !!currentUser.firstName,
      lastName: !!currentUser.lastName,
      email: !!currentUser.email
    };
    return Object.values(required).every(Boolean);
  }

  if (currentUser.userType === 'ORGANIZATION') {
    const required = {
      organizationName: !!currentUser.organizationName,
      email: !!currentUser.email
    };
    return Object.values(required).every(Boolean);
  }

  return hasBasicInfo;
}

export function needsProfileSetup(user = null) {
  const currentUser = user || getCurrentUser();

  if (!currentUser || !isLoggedIn()) {
    return false;
  }

  return !isProfileComplete(currentUser);
}

export function shouldRedirectToProfileSetup(user = null) {
  const currentUser = user || getCurrentUser();

  if (!isLoggedIn()) {
    return false;
  }

  return needsProfileSetup(currentUser);
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

export function debugAuthState() {
  console.log('🔍 === Auth Debug Info ===');
  console.log('Token:', localStorage.getItem('authToken')?.substring(0, 20) + '...');
  console.log('User:', getCurrentUser());
  console.log('Is Logged In:', isLoggedIn());
  console.log('Profile Complete:', isProfileComplete(getCurrentUser()));
  console.log('Needs Setup:', needsProfileSetup(getCurrentUser()));
  console.log('Token Expired:', isTokenExpired());
  console.log('========================');
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
// ADDITIONAL UTILITY FUNCTIONS
// ==========================================

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
// ENSURE VALID TOKEN UTILITY
// ==========================================

export async function ensureValidToken() {
  const token = localStorage.getItem('authToken');

  if (!token || token === 'undefined' || token === 'null') {
    throw new Error('No token found');
  }

  if (isTokenExpired(token)) {
    console.log('🔄 Token expired, refreshing...');
    const refreshResult = await refreshToken();
    if (!refreshResult.success) {
      logoutWithCleanup();
      window.location.href = '/login';
      throw new Error('Token expired and refresh failed');
    }
    return refreshResult.token;
  }

  // Check if token expires soon and refresh proactively
  if (shouldRefreshToken(token)) {
    console.log('🔄 Token expiring soon, refreshing proactively...');
    try {
      const refreshResult = await refreshToken();
      if (refreshResult.success) {
        return refreshResult.token;
      }
    } catch (error) {
      console.warn('Proactive token refresh failed:', error);
      // Continue with current token if proactive refresh fails
    }
  }

  return token;
}

// ==========================================
// EXPORTS
// ==========================================

export default api;