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
      message: error.response?.data?.message || 'Registration failed' 
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
      message: error.response?.data?.message || 'Google registration failed' 
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
    
    console.error('Login response missing token:', response.data);
    return { success: false, message: 'Login successful but no token received' };
  } catch (error) {
    console.error('=== LOGIN ERROR ===');
    console.error('Login error:', error.response?.data);
    console.error('Status:', error.response?.status);
    return { 
      success: false, 
      message: error.response?.data?.message || 'Login failed' 
    };
  }
}

export async function loginWithGoogle(googleIdToken) {
  try {
    console.log('Sending Google login with token:', googleIdToken.substring(0, 20) + '...');

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

// ==========================================
// UTILITY FUNCTIONS - 🔧 IMPROVED ERROR HANDLING
// ==========================================

export function getCurrentUser() {
  try {
    const user = localStorage.getItem('user');
    
    // Check for null, undefined, or the string "undefined"
    if (!user || user === 'undefined' || user === 'null') {
      return null;
    }
    
    return JSON.parse(user);
  } catch (error) {
    console.error('Error parsing user from localStorage:', error);
    // Clear corrupted data
    localStorage.removeItem('user');
    return null;
  }
}

export function isLoggedIn() {
  const token = localStorage.getItem('authToken');
  const user = getCurrentUser();
  
  console.log('=== AUTH STATUS CHECK ===');
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
// PROFILE COMPLETION FUNCTIONS
// ==========================================

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
      message: error.response?.data?.message || 'Failed to complete profile' 
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