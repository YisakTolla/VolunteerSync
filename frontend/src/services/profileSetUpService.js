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

  // ✅ FIXED: Define helper functions within the formatProfileData function
  const arrayToString = (value) => {
    if (!value) return '';

    if (Array.isArray(value)) {
      // Filter out empty values and trim whitespace
      const cleanArray = value
        .filter(item => item && typeof item === 'string' && item.trim() !== '')
        .map(item => item.trim());
      return cleanArray.length > 0 ? cleanArray.join(',') : '';
    }

    if (typeof value === 'string') {
      // Already a string, clean it up by splitting and rejoining
      const cleanArray = value
        .split(',')
        .map(item => item.trim())
        .filter(item => item !== '');
      return cleanArray.join(',');
    }

    return String(value || '');
  };

  const ensureString = (value) => {
    if (value === null || value === undefined) {
      return '';
    }
    if (Array.isArray(value)) {
      return arrayToString(value);
    }
    return String(value);
  };

  const ensureNumber = (value) => {
    if (value === null || value === undefined || value === '') {
      return null;
    }
    const num = Number(value);
    return isNaN(num) ? null : num;
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
      // ✅ BASIC INFORMATION
      organizationName: ensureString(profileData.organizationName),
      description: ensureString(profileData.description || profileData.bio),
      missionStatement: ensureString(profileData.missionStatement),
      website: ensureString(profileData.website),

      // ✅ CONTACT & LOCATION (matching database schema)
      address: ensureString(profileData.address),
      city: ensureString(profileData.city),
      state: ensureString(profileData.state),
      country: ensureString(profileData.country),
      zipCode: ensureString(profileData.zipCode),

      // ✅ CLASSIFICATION & CATEGORIES
      organizationType: ensureString(profileData.organizationType),
      organizationSize: ensureString(profileData.organizationSize),
      primaryCategory: ensureString(profileData.primaryCategory),
      categories: ensureString(profileData.categories || profileData.primaryCategory), // Backward compatibility

      // ✅ FIXED: Proper array to string conversion using arrayToString function
      causes: arrayToString(profileData.causes),
      services: arrayToString(profileData.services),

      // ✅ ORGANIZATION DETAILS
      ein: ensureString(profileData.ein),
      employeeCount: ensureNumber(profileData.employeeCount),
      foundedYear: ensureNumber(profileData.foundedYear),
      fundingGoal: ensureNumber(profileData.fundingGoal),
      fundingRaised: ensureNumber(profileData.fundingRaised),

      // ✅ FIXED: Language array handling using arrayToString function
      languagesSupported: arrayToString(profileData.languagesSupported),
      taxExemptStatus: ensureString(profileData.taxExemptStatus),

      // ✅ VERIFICATION & STATUS
      verificationLevel: profileData.verificationLevel || 'Unverified',
      isVerified: profileData.isVerified || false,

      // ✅ IMAGES
      profileImageUrl: profileData.profileImageUrl || null,
      coverImageUrl: profileData.coverImageUrl || null,

      // ✅ STATS (these will be auto-generated by backend, but we include them for completeness)
      totalEventsHosted: ensureNumber(profileData.totalEventsHosted) || 0,
      totalVolunteersServed: ensureNumber(profileData.totalVolunteersServed) || 0,
    };

    console.log('🏢 Formatted organization data:');
    console.log('- Organization Name:', formattedData.organizationName);
    console.log('- Primary Category:', formattedData.primaryCategory);
    console.log('- Causes (processed):', formattedData.causes);
    console.log('- Services (processed):', formattedData.services);
    console.log('- Languages (processed):', formattedData.languagesSupported);
    console.log('- Full formatted data:', formattedData);

    return formattedData;
  }

  console.log('🔄 Formatted base data:', baseData);
  return baseData;
};

const parseProfileDataFromBackend = (backendData, userType) => {
  if (!backendData) return null;

  console.log('📥 Parsing backend data for frontend:', backendData);

  const parsedData = { ...backendData };

  if (userType === 'ORGANIZATION') {
    // Convert comma-separated strings back to arrays for frontend components
    if (parsedData.causes && typeof parsedData.causes === 'string') {
      parsedData.causesArray = stringToArray(parsedData.causes);
    }

    if (parsedData.services && typeof parsedData.services === 'string') {
      parsedData.servicesArray = stringToArray(parsedData.services);
    }

    if (parsedData.languagesSupported && typeof parsedData.languagesSupported === 'string') {
      parsedData.languagesArray = stringToArray(parsedData.languagesSupported);
    }

    if (parsedData.categories && typeof parsedData.categories === 'string') {
      parsedData.categoriesArray = stringToArray(parsedData.categories);
    }

    console.log('🔄 Parsed organization arrays:');
    console.log('- Causes array:', parsedData.causesArray);
    console.log('- Services array:', parsedData.servicesArray);
    console.log('- Languages array:', parsedData.languagesArray);
    console.log('- Categories array:', parsedData.categoriesArray);
  }

  if (userType === 'VOLUNTEER') {
    // Convert comma-separated strings back to arrays for volunteers
    if (parsedData.interests && typeof parsedData.interests === 'string') {
      parsedData.interestsArray = stringToArray(parsedData.interests);
    }

    if (parsedData.skills && typeof parsedData.skills === 'string') {
      parsedData.skillsArray = stringToArray(parsedData.skills);
    }

    console.log('🔄 Parsed volunteer arrays:');
    console.log('- Interests array:', parsedData.interestsArray);
    console.log('- Skills array:', parsedData.skillsArray);
  }

  return parsedData;
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
      (profileData.description || profileData.bio) &&
      profileData.city &&
      profileData.country &&
      (profileData.primaryCategory || profileData.categories)
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
      console.log('=== ORGANIZATION PROFILE DEBUG ===');
      console.log('🏢 Organization Name:', formattedData.organizationName);
      console.log('📝 Description:', formattedData.description);
      console.log('🎯 Primary Category:', formattedData.primaryCategory);
      console.log('📂 Categories:', formattedData.categories);
      console.log('💡 Causes:', formattedData.causes);
      console.log('🛠️ Services:', formattedData.services);
      console.log('🗣️ Languages:', formattedData.languagesSupported);
      console.log('📍 Location:', `${formattedData.city}, ${formattedData.state}, ${formattedData.country}`);
      console.log('🏗️ Organization Type:', formattedData.organizationType);
      console.log('📏 Organization Size:', formattedData.organizationSize);
      console.log('🆔 EIN:', formattedData.ein);
      console.log('👥 Employee Count:', formattedData.employeeCount);
      console.log('📅 Founded Year:', formattedData.foundedYear);
      console.log('💰 Funding Goal:', formattedData.fundingGoal);
      console.log('💵 Funding Raised:', formattedData.fundingRaised);
      console.log('📋 Tax Exempt Status:', formattedData.taxExemptStatus);

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
    console.log('=== ENHANCED FETCHING PROFILE ===');

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

    console.log('Raw profile fetch response:', response.data);

    // ✅ ENHANCED: Parse the backend data for frontend use
    const parsedData = parseProfileDataFromBackend(response.data, userType);

    // ✅ NEW: Log organization-specific data when fetching
    if (userType === 'ORGANIZATION') {
      console.log('=== ENHANCED FETCHED ORGANIZATION DATA ===');
      console.log('🏢 Organization Name:', parsedData.organizationName);
      console.log('🎯 Primary Category:', parsedData.primaryCategory);
      console.log('📂 Categories (string):', parsedData.categories);
      console.log('📂 Categories (array):', parsedData.categoriesArray);
      console.log('💡 Causes (string):', parsedData.causes);
      console.log('💡 Causes (array):', parsedData.causesArray);
      console.log('🛠️ Services (string):', parsedData.services);
      console.log('🛠️ Services (array):', parsedData.servicesArray);
      console.log('🗣️ Languages (string):', parsedData.languagesSupported);
      console.log('🗣️ Languages (array):', parsedData.languagesArray);
      console.log('📏 Organization Size:', parsedData.organizationSize);
    }

    // Check if profile is complete and update local storage
    const isComplete = checkProfileCompleteness(parsedData, userType);
    const updatedProfileData = {
      ...parsedData,
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
    console.error('=== ENHANCED PROFILE FETCH ERROR ===');
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
      (currentUser.description || currentUser.bio) &&
      currentUser.city &&
      currentUser.country &&
      (currentUser.primaryCategory || currentUser.categories)
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
// ENHANCED ORGANIZATION UTILITIES
// ==========================================

/**
 * Get organization categories list for form dropdowns
 * @returns {Array} - Array of category options
 */
const getOrganizationCategories = () => {
  return [
    { value: "Education", label: "Education" },
    { value: "Environment", label: "Environment" },
    { value: "Healthcare", label: "Healthcare" },
    { value: "Animal Welfare", label: "Animal Welfare" },
    { value: "Community Service", label: "Community Service" },
    { value: "Human Services", label: "Human Services" },
    { value: "Arts & Culture", label: "Arts & Culture" },
    { value: "Youth Development", label: "Youth Development" },
    { value: "Senior Services", label: "Senior Services" },
    { value: "Hunger & Homelessness", label: "Hunger & Homelessness" },
    { value: "Disaster Relief", label: "Disaster Relief" },
    { value: "International", label: "International" },
    { value: "Sports & Recreation", label: "Sports & Recreation" },
    { value: "Mental Health", label: "Mental Health" },
    { value: "Veterans", label: "Veterans" },
    { value: "Women's Issues", label: "Women's Issues" },
    { value: "Children & Families", label: "Children & Families" },
    { value: "Disability Services", label: "Disability Services" },
    { value: "Religious", label: "Religious" },
    { value: "Political", label: "Political" },
    { value: "LGBTQ+", label: "LGBTQ+" },
    { value: "Technology", label: "Technology" },
    { value: "Research & Advocacy", label: "Research & Advocacy" },
    { value: "Public Safety", label: "Public Safety" },
  ];
};

/**
 * Get organization sizes list for form dropdowns
 * @returns {Array} - Array of size options
 */
const getOrganizationSizes = () => {
  return [
    { value: "Small (1-50)", label: "Small (1-50)" },
    { value: "Medium (51-200)", label: "Medium (51-200)" },
    { value: "Large (201-1000)", label: "Large (201-1000)" },
    { value: "Enterprise (1000+)", label: "Enterprise (1000+)" },
  ];
};

/**
 * Get supported languages list for form dropdowns
 * @returns {Array} - Array of language options
 */
const getSupportedLanguages = () => {
  return [
    { value: "English", label: "English" },
    { value: "Spanish", label: "Spanish" },
    { value: "French", label: "French" },
    { value: "German", label: "German" },
    { value: "Italian", label: "Italian" },
    { value: "Portuguese", label: "Portuguese" },
    { value: "Chinese", label: "Chinese" },
    { value: "Japanese", label: "Japanese" },
    { value: "Korean", label: "Korean" },
    { value: "Arabic", label: "Arabic" },
    { value: "Hindi", label: "Hindi" },
    { value: "Russian", label: "Russian" },
  ];
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
  checkProfileCompleteness,
  getOrganizationCategories,
  getOrganizationSizes,
  getSupportedLanguages
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
  setProfileComplete,
  getOrganizationCategories,
  getOrganizationSizes,
  getSupportedLanguages
};