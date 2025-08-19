import axios from 'axios';
import { ensureValidToken, getCurrentUser, logoutWithCleanup } from './authService';

// ==========================================
// API BASE CONFIGURATION
// ==========================================

const API_BASE_URL = 'http://localhost:8080/api';

const profileApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
profileApi.interceptors.request.use(async (config) => {
  try {
    const token = await ensureValidToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  } catch (error) {
    console.error('Token validation failed in profile service:', error);
    logoutWithCleanup();
    window.location.href = '/login';
    return Promise.reject(error);
  }
});

// Response interceptor
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

const formatProfileData = (profileData, userType) => {
  console.log('🔄 Formatting profile data for type:', userType);
  console.log('Raw profile data:', profileData);

  // Helper function to safely convert arrays to strings
  const arrayToString = (value) => {
    if (Array.isArray(value)) {
      return value.length > 0 ? value.join(',') : '';
    }
    return value || '';
  };

  // Helper function to ensure string values
  const ensureString = (value) => {
    if (value === null || value === undefined) {
      return '';
    }
    if (Array.isArray(value)) {
      return value.join(',');
    }
    return String(value);
  };

  const baseData = {
    bio: ensureString(profileData.bio),
    location: ensureString(profileData.location),
    phoneNumber: ensureString(profileData.phoneNumber || profileData.phone),
    skills: ensureString(profileData.skills),
    interests: ensureString(profileData.interests),
    profileComplete: profileData.profileComplete || false
  };

  if (userType === 'VOLUNTEER') {
    const formattedData = {
      ...baseData,
      firstName: ensureString(profileData.firstName),
      lastName: ensureString(profileData.lastName),
      availability: ensureString(profileData.availability || 'flexible'),
      profileImageUrl: profileData.profileImageUrl || null,
    };

    console.log('🙋 Formatted volunteer data:', formattedData);
    return formattedData;

  } else if (userType === 'ORGANIZATION') {
    const formattedData = {
      ...baseData,
      organizationName: ensureString(profileData.organizationName),
      // ✅ NEW: Handle organization type tags (comma-separated string)
      organizationType: ensureString(profileData.organizationType),
      // ✅ NEW: Handle organization size
      organizationSize: ensureString(profileData.organizationSize),
      website: ensureString(profileData.website),
      missionStatement: ensureString(profileData.missionStatement || profileData.bio),
      // ✅ UPDATED: Categories can be derived from organizationType for backward compatibility
      categories: ensureString(profileData.categories || profileData.organizationType),
      // ✅ FIXED: Convert services array to string
      services: arrayToString(profileData.services),
      profileImageUrl: profileData.profileImageUrl || null,
      coverImageUrl: profileData.coverImageUrl || null,
      // Additional fields that might be sent
      description: ensureString(profileData.description || profileData.bio),
      address: ensureString(profileData.address || profileData.location),
      city: ensureString(profileData.city),
      state: ensureString(profileData.state),
      zipCode: ensureString(profileData.zipCode),
      country: ensureString(profileData.country),
      primaryCategory: ensureString(profileData.primaryCategory),
      languagesSupported: ensureString(profileData.languagesSupported),
      taxExemptStatus: ensureString(profileData.taxExemptStatus),
      // Handle numeric fields properly
      employeeCount: profileData.employeeCount ? Number(profileData.employeeCount) : null,
      foundedYear: profileData.foundedYear ? Number(profileData.foundedYear) : null,
    };

    console.log('🏢 Formatted organization data:', formattedData);
    return formattedData;
  }

  console.log('📄 Formatted base data:', baseData);
  return baseData;
};

/**
 * Check if profile data is complete based on user type
 * @param {Object} profileData - Profile data to check
 * @param {string} userType - User type (VOLUNTEER/ORGANIZATION)
 * @returns {boolean} - Whether profile is complete
 */
const checkProfileCompleteness = (profileData, userType) => {
  if (!profileData) return false;

  // If profileComplete flag is explicitly set, use that
  if (profileData.profileComplete !== undefined) {
    return profileData.profileComplete;
  }

  // Check required fields based on user type
  if (userType === 'VOLUNTEER') {
    return !!(
      profileData.firstName &&
      profileData.lastName &&
      profileData.bio &&
      profileData.location &&
      profileData.interests
    );
  } else if (userType === 'ORGANIZATION') {
    return !!(
      profileData.organizationName &&
      profileData.bio &&
      profileData.location &&
      // ✅ UPDATED: Check for organizationType instead of just categories
      (profileData.organizationType || profileData.categories)
    );
  }

  return false;
};

// ==========================================
// MAIN PROFILE FUNCTIONS
// ==========================================

const createOrUpdateProfile = async (profileData) => {
  try {
    console.log('=== CREATING OR UPDATING PROFILE ===');
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
      endpoint = '/volunteer-profiles/me';
      console.log('🙋‍♀️ Creating/updating volunteer profile...');
      response = await profileApi.put(endpoint, formattedData);
    } else if (userType === 'ORGANIZATION') {
      endpoint = '/organization-profiles/me';
      console.log('🏢 Creating/updating organization profile...');
      
      // ✅ NEW: Log organization-specific data for debugging
      console.log('🏷️ Organization Types:', formattedData.organizationType);
      console.log('📏 Organization Size:', formattedData.organizationSize);
      console.log('📂 Categories (backward compatibility):', formattedData.categories);
      
      response = await profileApi.put(endpoint, formattedData);
    } else {
      throw new Error(`Invalid user type: ${userType}`);
    }

    console.log('✅ Profile upsert response:', response.data);

    // Update local user data with profile completion status
    const updatedUser = updateLocalUser({
      ...formattedData,
      profileComplete: profileData.profileComplete || true
    });

    return {
      success: true,
      data: response.data,
      user: updatedUser,
      message: 'Profile saved successfully'
    };

  } catch (error) {
    console.error('=== PROFILE UPSERT ERROR - DETAILED ===');
    console.error('Error type:', error.constructor.name);
    console.error('Error message:', error.message);

    if (error.response) {
      console.error('Response status:', error.response.status);
      console.error('Response statusText:', error.response.statusText);
      console.error('Response data:', error.response.data);

      if (error.response.data && typeof error.response.data === 'object') {
        try {
          const errorData = error.response.data;
          const finalErrorMessage = errorData.error || errorData.message || 'Unknown error occurred';

          return {
            success: false,
            message: finalErrorMessage,
            details: errorData
          };
        } catch (parseError) {
          console.error('Error parsing response data:', parseError);
        }
      }
    }

    return {
      success: false,
      message: error.response?.data?.error ||
        error.response?.data?.message ||
        error.message ||
        'Failed to save profile'
    };
  }
};

const createProfile = async (profileData) => {
  console.log('=== CREATING PROFILE ===');
  console.log('📝 Profile data:', profileData);

  // Mark as profile complete when successfully created through setup
  const profileDataWithCompletion = {
    ...profileData,
    profileComplete: profileData.profileComplete !== false // Default to true unless explicitly false
  };

  return createOrUpdateProfile(profileDataWithCompletion);
};

const updateProfile = async (profileData) => {
  console.log('=== UPDATING PROFILE ===');
  console.log('📝 Profile data:', profileData);

  return createOrUpdateProfile(profileData);
};

const checkProfileExists = async () => {
  try {
    console.log('=== CHECKING PROFILE EXISTS ===');

    const user = getCurrentUser();
    if (!user) {
      throw new Error('User not logged in');
    }

    const userType = user.userType;
    let response;

    if (userType === 'VOLUNTEER') {
      response = await profileApi.get('/volunteer-profiles/me');
    } else if (userType === 'ORGANIZATION') {
      response = await profileApi.get('/organization-profiles/me');
    } else {
      throw new Error('Invalid user type');
    }

    // Check if profile is considered complete
    const profileData = response.data;
    const isComplete = checkProfileCompleteness(profileData, userType);

    return {
      success: true,
      exists: true,
      data: profileData,
      isComplete: isComplete
    };

  } catch (error) {
    if (error.response?.status === 404 ||
      error.response?.data?.message?.includes('not found')) {
      return {
        success: true,
        exists: false,
        isComplete: false
      };
    }

    console.error('=== PROFILE EXISTENCE CHECK ERROR ===');
    console.error('Error:', error.response?.data || error.message);

    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to check profile'
    };
  }
};

const fetchMyProfile = async () => {
  try {
    console.log('=== FETCHING PROFILE ===');

    const user = getCurrentUser();
    if (!user) {
      throw new Error('User not logged in');
    }

    const userType = user.userType;
    let response;

    if (userType === 'VOLUNTEER') {
      response = await profileApi.get('/volunteer-profiles/me');
    } else if (userType === 'ORGANIZATION') {
      response = await profileApi.get('/organization-profiles/me');
    } else {
      throw new Error('Invalid user type');
    }

    console.log('Profile fetch response:', response.data);

    // ✅ NEW: Log organization-specific data when fetching
    if (userType === 'ORGANIZATION') {
      console.log('🏷️ Fetched Organization Types:', response.data.organizationType);
      console.log('📏 Fetched Organization Size:', response.data.organizationSize);
    }

    // Check if profile is complete and update local storage
    const isComplete = checkProfileCompleteness(response.data, userType);
    const updatedProfileData = {
      ...response.data,
      profileComplete: isComplete
    };

    const updatedUser = updateLocalUser(updatedProfileData);

    return {
      success: true,
      data: updatedProfileData,
      user: updatedUser,
      isComplete: isComplete
    };

  } catch (error) {
    console.error('=== PROFILE FETCH ERROR ===');
    console.error('Error:', error.response?.data || error.message);

    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to fetch profile'
    };
  }
};

const fetchPublicProfile = async (userId, userType) => {
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
};

const uploadProfileImage = async (imageFile, imageType = 'profile') => {
  try {
    console.log('=== UPLOADING PROFILE IMAGE ===');
    console.log('Image file:', imageFile);
    console.log('Image type:', imageType);

    if (!imageFile) {
      throw new Error('No image file provided');
    }

    const formData = new FormData();
    formData.append('image', imageFile);
    formData.append('type', imageType);

    const user = getCurrentUser();
    if (!user) {
      throw new Error('User not logged in');
    }

    const response = await profileApi.post('/upload/profile-image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    console.log('Image upload response:', response.data);

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
};

const fetchProfileStats = async () => {
  try {
    console.log('=== FETCHING PROFILE STATS ===');

    const user = getCurrentUser();
    if (!user) {
      throw new Error('User not logged in');
    }

    const userType = user.userType;
    let response;

    if (userType === 'VOLUNTEER') {
      response = await profileApi.get('/volunteer-profiles/me/stats');
    } else if (userType === 'ORGANIZATION') {
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
};

const deleteProfile = async () => {
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
};

// ==========================================
// PROFILE COMPLETION UTILITIES
// ==========================================

/**
 * Check if user profile is complete
 * @param {Object} user - User object (optional, will get current user if not provided)
 * @returns {boolean} - Whether profile is complete
 */
const isProfileComplete = (user = null) => {
  const currentUser = user || getCurrentUser();
  if (!currentUser) return false;

  // Check explicit profileComplete flag first
  if (currentUser.profileComplete === true) {
    return true;
  }

  if (currentUser.profileComplete === false) {
    return false;
  }

  // Check required fields based on user type
  if (currentUser.userType === 'VOLUNTEER') {
    return !!(
      currentUser.firstName &&
      currentUser.lastName &&
      currentUser.bio &&
      currentUser.location
    );
  } else if (currentUser.userType === 'ORGANIZATION') {
    return !!(
      currentUser.organizationName &&
      currentUser.bio &&
      currentUser.location &&
      // ✅ UPDATED: Check for organizationType or categories
      (currentUser.organizationType || currentUser.categories)
    );
  }

  return false;
};

/**
 * Mark profile as complete in local storage
 * @param {boolean} isComplete - Whether profile is complete
 */
const setProfileComplete = (isComplete = true) => {
  const user = getCurrentUser();
  if (user) {
    updateLocalUser({ profileComplete: isComplete });
  }
};

// ==========================================
// EXPORTS
// ==========================================

export {
  createOrUpdateProfile,
  createProfile,
  updateProfile,
  checkProfileExists,
  fetchMyProfile,
  fetchPublicProfile,
  uploadProfileImage,
  fetchProfileStats,
  deleteProfile,
  getCurrentUser,
  updateLocalUser,
  formatProfileData,
  isProfileComplete,
  setProfileComplete,
  checkProfileCompleteness
};

export default {
  createOrUpdateProfile,
  createProfile,
  updateProfile,
  checkProfileExists,
  fetchMyProfile,
  fetchPublicProfile,
  uploadProfileImage,
  fetchProfileStats,
  deleteProfile,
  getCurrentUser,
  updateLocalUser,
  isProfileComplete,
  setProfileComplete
};