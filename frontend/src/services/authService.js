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

// Add the missing getToken function
export function getToken() {
  return localStorage.getItem('authToken');
}

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

// ==========================================
// USER PROFILE FUNCTIONS
// ==========================================

export const getUserProfile = async () => {
  try {
    const token = getToken();
    if (!token) {
      console.log('No token found in localStorage');
      return { success: false, message: 'No token found' };
    }

    console.log('Making request to:', `${API_BASE_URL}/auth/me`);
    console.log('With token:', token ? `Token exists (length: ${token.length})` : 'No token');

    // Use axios instead of fetch to be consistent with the rest of the service
    const response = await api.get('/auth/me');

    console.log('Response status:', response.status);
    console.log('Response data:', response.data);
    
    // Add debugging
    console.log('=== API RESPONSE DEBUG ===');
    console.log('Full response:', response.data);
    console.log('Success:', response.data.success);
    console.log('Data:', response.data.data);
    if (response.data.data) {
      console.log('Organization name:', response.data.data.organizationName);
      console.log('User type:', response.data.data.userType);
      console.log('Email:', response.data.data.email);
      console.log('First name:', response.data.data.firstName);
      console.log('Last name:', response.data.data.lastName);
    }
    console.log('========================');

    if (response.data.success && response.data.data) {
      // Extract the actual user data from the ApiResponse wrapper
      const userData = response.data.data;
      
      // Update localStorage with the latest user data
      localStorage.setItem('user', JSON.stringify(userData));
      
      return {
        success: true,
        data: userData
      };
    } else {
      return {
        success: false,
        message: response.data.message || 'Failed to get user profile'
      };
    }
  } catch (error) {
    console.error('Error getting user profile:', error);
    if (error.response) {
      console.error('Error response:', error.response.data);
      console.error('Error status:', error.response.status);
    }
    return {
      success: false,
      message: error.response?.data?.message || error.message || 'Network error'
    };
  }
};

export default api;