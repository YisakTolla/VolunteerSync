import axios from 'axios';
import { ensureValidToken, getCurrentUser, logoutWithCleanup } from './authService';

// ==========================================
// API BASE CONFIGURATION
// ==========================================

const API_BASE_URL = 'http://localhost:8080/api';

// 🔧 FIXED: Create axios instance with enhanced error handling
const profileApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 🔧 FIXED: Enhanced request interceptor that ensures valid token
profileApi.interceptors.request.use(async (config) => {
  try {
    // Ensure we have a valid token before making the request
    const token = await ensureValidToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  } catch (error) {
    console.error('Token validation failed in profile service:', error);
    // If token validation fails, redirect to login
    logoutWithCleanup();
    window.location.href = '/login';
    return Promise.reject(error);
  }
});

// 🔧 FIXED: Enhanced response interceptor
profileApi.interceptors.response.use(
  (response) => {
    console.log('✅ Profile API Response:', response.status, response.config.url);
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    
    console.error('❌ Profile API Error:', {
      status: error.response?.status,
      url: error.config?.url,
      data: error.response?.data,
      message: error.message
    });

    // Handle authentication errors
    if (error.response?.status === 401 && !originalRequest._retry) {
      console.log('🔄 401 error in profile service, attempting token refresh...');
      
      try {
        originalRequest._retry = true;
        const token = await ensureValidToken();
        
        if (token) {
          originalRequest.headers.Authorization = `Bearer ${token}`;
          return profileApi(originalRequest);
        }
      } catch (refreshError) {
        console.error('❌ Token refresh failed in profile service:', refreshError);
        logoutWithCleanup();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);

// ==========================================
// UTILITY FUNCTIONS
// ==========================================

/**
 * Update user data in localStorage
 */
const updateLocalUser = (updatedData) => {
  try {
    const currentUser = getCurrentUser();
    if (currentUser) {
      const updatedUser = { ...currentUser, ...updatedData };
      localStorage.setItem('user', JSON.stringify(updatedUser));
      console.log('💾 Updated local user data:', updatedUser);
      return updatedUser;
    }
    return null;
  } catch (error) {
    console.error('Error updating local user data:', error);
    return null;
  }
};

/**
 * Determine user type and format profile data accordingly
 */
const formatProfileData = (profileData, userType) => {
  console.log('🔄 Formatting profile data for type:', userType);
  console.log('Raw profile data:', profileData);

  const baseData = {
    bio: profileData.bio || '',
    location: profileData.location || '',
    phoneNumber: profileData.phoneNumber || profileData.phone || '',
  };

  if (userType === 'VOLUNTEER') {
    const formattedData = {
      ...baseData,
      firstName: profileData.firstName || '',
      lastName: profileData.lastName || '',
      skills: profileData.skills || '',
      interests: profileData.interests || '',
      availability: profileData.availability || 'flexible',
      profileImageUrl: profileData.profileImageUrl || null,
    };
    console.log('📝 Formatted volunteer data:', formattedData);
    return formattedData;
  } else if (userType === 'ORGANIZATION') {
    const formattedData = {
      ...baseData,
      organizationName: profileData.organizationName || '',
      organizationType: profileData.organizationType || '',
      website: profileData.website || '',
      missionStatement: profileData.missionStatement || profileData.bio || '',
      categories: profileData.categories || profileData.interests || '',
      services: profileData.services || '',
      profileImageUrl: profileData.profileImageUrl || null,
      coverImageUrl: profileData.coverImageUrl || null,
    };
    console.log('🏢 Formatted organization data:', formattedData);
    return formattedData;
  }

  console.log('📝 Formatted base data:', baseData);
  return baseData;
};

// ==========================================
// PROFILE CREATION FUNCTIONS
// ==========================================

/**
 * 🔧 FIXED: Create initial profile during profile setup
 * @param {Object} profileData - Profile information from setup form
 * @returns {Object} - Success/error response with profile data
 */
export async function createProfile(profileData) {
  try {
    console.log('=== CREATING PROFILE ===');
    console.log('📝 Profile data received:', profileData);

    const user = getCurrentUser();
    if (!user) {
      throw new Error('User not logged in');
    }

    console.log('👤 Current user:', user);
    console.log('🎯 User type:', user.userType);

    const userType = user.userType;
    const formattedData = formatProfileData(profileData, userType);

    console.log('📋 Formatted profile data:', formattedData);

    let response;
    let endpoint;

    if (userType === 'VOLUNTEER') {
      endpoint = '/volunteer-profiles';
      console.log('🙋‍♀️ Creating volunteer profile...');
      response = await profileApi.post(endpoint, formattedData);
    } else if (userType === 'ORGANIZATION') {
      endpoint = '/organization-profiles';
      console.log('🏢 Creating organization profile...');
      response = await profileApi.post(endpoint, formattedData);
    } else {
      throw new Error(`Invalid user type: ${userType}`);
    }

    console.log('✅ Profile creation response:', response.data);

    // Update local user data with profile completion
    const updatedUser = updateLocalUser({
      ...formattedData,
      profileComplete: true
    });

    return {
      success: true,
      data: response.data,
      user: updatedUser,
      message: 'Profile created successfully'
    };

  } catch (error) {
    console.error('=== PROFILE CREATION ERROR - DETAILED ===');
    console.error('Error type:', error.constructor.name);
    console.error('Error message:', error.message);
    
    if (error.response) {
      console.error('Response status:', error.response.status);
      console.error('Response statusText:', error.response.statusText);
      console.error('Response headers:', error.response.headers);
      console.error('Response data type:', typeof error.response.data);
      console.error('Response data:', error.response.data);
      
      if (error.response.data && typeof error.response.data === 'object') {
        console.error('Response data constructor:', error.response.data.constructor.name);
        console.error('Response data stringified:', JSON.stringify(error.response.data));
        
        // Try to parse error data if it's an object
        try {
          const errorData = error.response.data;
          console.error('Error data parsed:', errorData);
          
          // Get all properties of the error data
          const dataProperties = Object.keys(errorData);
          console.error('Data properties:', dataProperties);
          
          // Log specific error properties
          if (errorData.error) {
            console.error('data.error:', errorData.error);
          }
          if (errorData.message) {
            console.error('data.message:', errorData.message);
          }
          if (errorData.timestamp) {
            console.error('data.timestamp:', errorData.timestamp);
          }
          
          // Extract the final error message
          const finalErrorMessage = errorData.error || errorData.message || 'Unknown error';
          console.error('Final extracted error message:', finalErrorMessage);
          console.error('Error details object:', errorData);
          
        } catch (parseError) {
          console.error('Error parsing response data:', parseError);
        }
      }
    } else if (error.request) {
      console.error('Request error - no response received');
      console.error('Request:', error.request);
    } else {
      console.error('Setup error:', error.message);
    }
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to create profile',
      details: error.response?.data,
      status: error.response?.status,
      statusText: error.response?.statusText || ''
    };
  }
}

// ==========================================
// PROFILE UPDATE FUNCTIONS
// ==========================================

/**
 * Update existing profile (from Profile page or Settings)
 * @param {Object} updatedData - Updated profile information
 * @returns {Object} - Success/error response with updated profile data
 */
export async function updateProfile(updatedData) {
  try {
    console.log('=== UPDATING PROFILE ===');
    console.log('Updated data:', updatedData);

    const user = getCurrentUser();
    if (!user) {
      throw new Error('User not logged in');
    }

    const userType = user.userType;
    const formattedData = formatProfileData(updatedData, userType);

    console.log('Formatted update data:', formattedData);

    let response;

    if (userType === 'VOLUNTEER') {
      // Update volunteer profile
      response = await profileApi.put('/volunteer-profiles/me', formattedData);
    } else if (userType === 'ORGANIZATION') {
      // Update organization profile
      response = await profileApi.put('/organization-profiles/me', formattedData);
    } else {
      throw new Error('Invalid user type');
    }

    console.log('Profile update response:', response.data);

    // Update local user data
    const updatedUser = updateLocalUser(formattedData);

    return {
      success: true,
      data: response.data,
      user: updatedUser,
      message: 'Profile updated successfully'
    };

  } catch (error) {
    console.error('=== PROFILE UPDATE ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to update profile'
    };
  }
}

// ==========================================
// PROFILE FETCHING FUNCTIONS
// ==========================================

/**
 * Fetch current user's complete profile
 * @returns {Object} - Success/error response with profile data
 */
export async function fetchMyProfile() {
  try {
    console.log('=== FETCHING PROFILE ===');

    const user = getCurrentUser();
    if (!user) {
      throw new Error('User not logged in');
    }

    const userType = user.userType;
    let response;

    if (userType === 'VOLUNTEER') {
      // Fetch volunteer profile
      response = await profileApi.get('/volunteer-profiles/me');
    } else if (userType === 'ORGANIZATION') {
      // Fetch organization profile
      response = await profileApi.get('/organization-profiles/me');
    } else {
      throw new Error('Invalid user type');
    }

    console.log('Profile fetch response:', response.data);

    // Update local user data with fetched profile
    const updatedUser = updateLocalUser(response.data);

    return {
      success: true,
      data: response.data,
      user: updatedUser
    };

  } catch (error) {
    console.error('=== PROFILE FETCH ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to fetch profile'
    };
  }
}

/**
 * Fetch public profile by user ID
 * @param {number} userId - User ID to fetch profile for
 * @param {string} userType - Type of user (VOLUNTEER/ORGANIZATION)
 * @returns {Object} - Success/error response with profile data
 */
export async function fetchPublicProfile(userId, userType) {
  try {
    console.log('=== FETCHING PUBLIC PROFILE ===');
    console.log('User ID:', userId, 'Type:', userType);

    let response;

    if (userType === 'VOLUNTEER') {
      response = await profileApi.get(`/volunteer-profiles/${userId}`);
    } else if (userType === 'ORGANIZATION') {
      response = await profileApi.get(`/organization-profiles/${userId}`);
    } else {
      throw new Error('Invalid user type');
    }

    console.log('Public profile fetch response:', response.data);

    return {
      success: true,
      data: response.data
    };

  } catch (error) {
    console.error('=== PUBLIC PROFILE FETCH ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to fetch profile'
    };
  }
}

// ==========================================
// IMAGE UPLOAD FUNCTIONS
// ==========================================

/**
 * Upload profile image
 * @param {File} imageFile - Image file to upload
 * @param {string} imageType - Type of image ('profile' or 'cover')
 * @returns {Object} - Success/error response with image URL
 */
export async function uploadProfileImage(imageFile, imageType = 'profile') {
  try {
    console.log('=== UPLOADING PROFILE IMAGE ===');
    console.log('Image file:', imageFile);
    console.log('Image type:', imageType);

    if (!imageFile) {
      throw new Error('No image file provided');
    }

    // Create FormData for file upload
    const formData = new FormData();
    formData.append('image', imageFile);
    formData.append('type', imageType);

    const user = getCurrentUser();
    if (!user) {
      throw new Error('User not logged in');
    }

    // Upload image
    const response = await profileApi.post('/upload/profile-image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    console.log('Image upload response:', response.data);

    // Update local user data with new image URL
    const imageUrlField = imageType === 'cover' ? 'coverImageUrl' : 'profileImageUrl';
    const updatedUser = updateLocalUser({
      [imageUrlField]: response.data.imageUrl
    });

    return {
      success: true,
      imageUrl: response.data.imageUrl,
      user: updatedUser,
      message: 'Image uploaded successfully'
    };

  } catch (error) {
    console.error('=== IMAGE UPLOAD ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to upload image'
    };
  }
}

// ==========================================
// PROFILE STATISTICS FUNCTIONS
// ==========================================

/**
 * Fetch profile statistics (volunteer hours, events, etc.)
 * @returns {Object} - Success/error response with statistics
 */
export async function fetchProfileStats() {
  try {
    console.log('=== FETCHING PROFILE STATS ===');

    const user = getCurrentUser();
    if (!user) {
      throw new Error('User not logged in');
    }

    const userType = user.userType;
    let response;

    if (userType === 'VOLUNTEER') {
      // Fetch volunteer statistics
      response = await profileApi.get('/volunteer-profiles/me/stats');
    } else if (userType === 'ORGANIZATION') {
      // Fetch organization statistics
      response = await profileApi.get('/organization-profiles/me/stats');
    } else {
      throw new Error('Invalid user type');
    }

    console.log('Profile stats response:', response.data);

    return {
      success: true,
      data: response.data
    };

  } catch (error) {
    console.error('=== PROFILE STATS ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to fetch statistics'
    };
  }
}

// ==========================================
// PROFILE DELETION FUNCTIONS
// ==========================================

/**
 * Delete user profile (soft delete)
 * @returns {Object} - Success/error response
 */
export async function deleteProfile() {
  try {
    console.log('=== DELETING PROFILE ===');

    const user = getCurrentUser();
    if (!user) {
      throw new Error('User not logged in');
    }

    const userType = user.userType;

    if (userType === 'VOLUNTEER') {
      await profileApi.delete('/volunteer-profiles/me');
    } else if (userType === 'ORGANIZATION') {
      await profileApi.delete('/organization-profiles/me');
    } else {
      throw new Error('Invalid user type');
    }

    // Clear local storage
    logoutWithCleanup();

    return {
      success: true,
      message: 'Profile deleted successfully'
    };

  } catch (error) {
    console.error('=== PROFILE DELETION ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to delete profile'
    };
  }
}

// ==========================================
// UTILITY EXPORTS
// ==========================================

export {
  getCurrentUser,
  updateLocalUser,
  formatProfileData
};

// Default export
export default {
  createProfile,
  updateProfile,
  fetchMyProfile,
  fetchPublicProfile,
  uploadProfileImage,
  fetchProfileStats,
  deleteProfile,
  getCurrentUser,
  updateLocalUser
};