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

    console.log('Sending registration data:', requestPayload); // Debug log

    const response = await api.post('/auth/register', requestPayload);

    // Save token and user data
    if (response.data.token) {
      localStorage.setItem('authToken', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data.user || response.data));
      return { success: true, data: response.data };
    }
  } catch (error) {
    console.error('Registration error:', error.response?.data); // Debug log
    return { 
      success: false, 
      message: error.response?.data?.message || 'Registration failed' 
    };
  }
}

export async function registerWithGoogle(googleToken, userType) {
  try {
    const response = await api.post('/auth/google', {
      googleToken: googleToken,
      userType: userType // "VOLUNTEER" or "ORGANIZATION"
    });

    if (response.data.token) {
      localStorage.setItem('authToken', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data.user || response.data));
      return { success: true, data: response.data };
    }
  } catch (error) {
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

    if (response.data.token) {
      localStorage.setItem('authToken', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data.user || response.data));
      return { success: true, data: response.data };
    }
  } catch (error) {
    return { 
      success: false, 
      message: error.response?.data?.message || 'Login failed' 
    };
  }
}

export async function loginWithGoogle(googleToken) {
  try {
    const response = await api.post('/auth/google', {
      googleToken: googleToken,
      userType: "VOLUNTEER" // Default, will use existing user type if user exists
    });

    if (response.data.token) {
      localStorage.setItem('authToken', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data.user || response.data));
      return { success: true, data: response.data };
    }
  } catch (error) {
    return { 
      success: false, 
      message: error.response?.data?.message || 'Google login failed' 
    };
  }
}

// ==========================================
// UTILITY FUNCTIONS
// ==========================================

export function getCurrentUser() {
  const user = localStorage.getItem('user');
  return user ? JSON.parse(user) : null;
}

export function isLoggedIn() {
  return localStorage.getItem('authToken') !== null;
}

export function logout() {
  localStorage.removeItem('authToken');
  localStorage.removeItem('user');
  // Don't redirect here, let the component handle it
}

export async function getUserProfile() {
  try {
    const response = await api.get('/auth/me');
    return { success: true, data: response.data };
  } catch (error) {
    return { 
      success: false, 
      message: error.response?.data?.message || 'Failed to get profile' 
    };
  }
}

export default api;