// frontend/src/services/settingsService.js
// COMPLETE FIXED VERSION - All sections implemented

import axios from 'axios';
import { 
  ensureValidToken, 
  getCurrentUser, 
  logoutWithCleanup,
  updateCurrentUser 
} from './authService';

// ==========================================
// API BASE CONFIGURATION
// ==========================================

const API_BASE_URL = 'http://localhost:8080/api';

// Create axios instance for settings API
const settingsApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
settingsApi.interceptors.request.use(async (config) => {
  try {
    const token = await ensureValidToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  } catch (error) {
    console.error('Token validation failed in settings service:', error);
    logoutWithCleanup();
    window.location.href = '/login';
    return Promise.reject(error);
  }
});

// Response interceptor for error handling
settingsApi.interceptors.response.use(
  (response) => {
    console.log('Settings API Response:', response.status, response.config.url);
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    
    console.error('Settings API Error:', {
      status: error.response?.status,
      url: error.config?.url,
      data: error.response?.data,
      message: error.message
    });

    // Handle authentication errors
    if (error.response?.status === 401 && !originalRequest._retry) {
      console.log('401 error in settings service, attempting token refresh...');
      
      try {
        originalRequest._retry = true;
        const token = await ensureValidToken();
        
        if (token) {
          originalRequest.headers.Authorization = `Bearer ${token}`;
          return settingsApi(originalRequest);
        }
      } catch (refreshError) {
        console.error('Token refresh failed in settings service:', refreshError);
        logoutWithCleanup();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);

// ==========================================
// PROFILE INFORMATION SECTION
// ==========================================

/**
 * Fetch current user settings/profile data
 */
export async function fetchUserSettings() {
  try {
    console.log('=== FETCHING USER SETTINGS ===');

    const user = getCurrentUser();
    if (!user) {
      throw new Error('User not logged in');
    }

    const userType = user.userType;
    console.log('User type:', userType);

    // First get base user data
    let baseUserData = {};
    try {
      const userResponse = await settingsApi.get('/users/me');
      baseUserData = userResponse.data;
      console.log('Base user data:', baseUserData);
    } catch (userError) {
      console.warn('Could not fetch base user data:', userError);
      baseUserData = user;
    }

    // Then get profile-specific data
    let profileData = {};
    try {
      if (userType === 'VOLUNTEER') {
        const response = await settingsApi.get('/volunteer-profiles/me');
        profileData = response.data;
        console.log('Volunteer profile data:', profileData);
      } else if (userType === 'ORGANIZATION') {
        const response = await settingsApi.get('/organization-profiles/me');
        profileData = response.data;
        console.log('Organization profile data:', profileData);
      }
    } catch (profileError) {
      console.warn('Could not fetch profile data:', profileError);
    }

    // Combine the data into a unified structure
    const combinedData = {
      id: baseUserData.id || user.id,
      email: baseUserData.email || user.email,
      userType: baseUserData.userType || user.userType,
      isActive: baseUserData.isActive !== undefined ? baseUserData.isActive : true,
      createdAt: baseUserData.createdAt || user.createdAt,
      
      // Common profile fields
      bio: profileData.bio || profileData.description || '',
      location: profileData.location || profileData.city || '',
      phoneNumber: profileData.phoneNumber || '',
      phone: profileData.phoneNumber || '',
      website: profileData.website || '',
      profileImageUrl: profileData.profileImageUrl || '',
      
      // Volunteer-specific fields
      ...(userType === 'VOLUNTEER' && {
        firstName: profileData.firstName || user.firstName || '',
        lastName: profileData.lastName || user.lastName || '',
        displayName: profileData.displayName || user.displayName || 
                    `${profileData.firstName || user.firstName || ''} ${profileData.lastName || user.lastName || ''}`.trim(),
        skills: profileData.skills || [],
        interests: profileData.interests || [],
        availability: profileData.availability || {}
      }),
      
      // Organization-specific fields
      ...(userType === 'ORGANIZATION' && {
        organizationName: profileData.organizationName || user.organizationName || '',
        displayName: profileData.organizationName || user.organizationName || user.displayName || '',
        organizationType: profileData.organizationType || '',
        missionStatement: profileData.missionStatement || '',
        description: profileData.description || profileData.bio || '',
        categories: profileData.categories || [],
        foundedYear: profileData.foundedYear || null,
        employeeCount: profileData.employeeCount || ''
      })
    };

    console.log('Combined settings data:', combinedData);

    return {
      success: true,
      data: combinedData
    };

  } catch (error) {
    console.error('=== SETTINGS FETCH ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to fetch settings'
    };
  }
}

/**
 * Update profile information
 */
export async function updateProfileSettings(profileData) {
  try {
    console.log('=== UPDATING PROFILE SETTINGS ===');
    console.log('Raw profile data:', profileData);

    const user = getCurrentUser();
    if (!user) {
      throw new Error('User not logged in');
    }

    const userType = user.userType;
    const formattedData = formatProfileDataForUpdate(profileData, userType);
    console.log('Formatted data for API:', formattedData);

    let response;
    if (userType === 'VOLUNTEER') {
      response = await settingsApi.put('/volunteer-profiles/me', formattedData);
    } else if (userType === 'ORGANIZATION') {
      response = await settingsApi.put('/organization-profiles/me', formattedData);
    } else {
      throw new Error('Invalid user type');
    }

    console.log('Profile update response:', response.data);

    // Update local user data
    try {
      updateCurrentUser({
        ...user,
        firstName: formattedData.firstName || user.firstName,
        lastName: formattedData.lastName || user.lastName,
        displayName: formattedData.displayName || formattedData.organizationName || user.displayName,
        organizationName: formattedData.organizationName || user.organizationName
      });
    } catch (updateError) {
      console.warn('Could not update local user data:', updateError);
    }

    return {
      success: true,
      data: response.data,
      message: 'Profile updated successfully'
    };

  } catch (error) {
    console.error('=== PROFILE SETTINGS UPDATE ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to update profile'
    };
  }
}

// ==========================================
// ACCOUNT SECURITY SECTION
// ==========================================

/**
 * Change user password
 */
export async function changePassword(passwordData) {
  try {
    console.log('=== CHANGING PASSWORD ===');

    const { currentPassword, newPassword, confirmPassword } = passwordData;

    // Validate passwords
    if (newPassword !== confirmPassword) {
      return {
        success: false,
        message: 'New passwords do not match'
      };
    }

    if (newPassword.length < 8) {
      return {
        success: false,
        message: 'Password must be at least 8 characters long'
      };
    }

    try {
      const response = await settingsApi.put('/auth/change-password', {
        currentPassword,
        newPassword
      });

      console.log('Password change response:', response.data);

      return {
        success: true,
        message: 'Password updated successfully'
      };
    } catch (apiError) {
      if (apiError.response?.status === 404) {
        console.warn('Password change API not implemented yet');
        return {
          success: false,
          message: 'Password change feature is not available yet. Please contact support.'
        };
      } else {
        throw apiError;
      }
    }

  } catch (error) {
    console.error('=== PASSWORD CHANGE ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to change password'
    };
  }
}

/**
 * Enable two-factor authentication
 */
export async function enableTwoFactor() {
  try {
    console.log('=== ENABLING TWO-FACTOR AUTHENTICATION ===');

    try {
      const response = await settingsApi.post('/auth/2fa/enable');
      console.log('2FA enable response:', response.data);

      return {
        success: true,
        data: response.data,
        message: 'Two-factor authentication enabled successfully'
      };
    } catch (apiError) {
      if (apiError.response?.status === 404) {
        console.warn('2FA API not implemented yet');
        return {
          success: false,
          message: 'Two-factor authentication is not available yet.'
        };
      } else {
        throw apiError;
      }
    }

  } catch (error) {
    console.error('=== 2FA ENABLE ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to enable two-factor authentication'
    };
  }
}

/**
 * Disable two-factor authentication
 */
export async function disableTwoFactor() {
  try {
    console.log('=== DISABLING TWO-FACTOR AUTHENTICATION ===');

    try {
      const response = await settingsApi.post('/auth/2fa/disable');
      console.log('2FA disable response:', response.data);

      return {
        success: true,
        message: 'Two-factor authentication disabled successfully'
      };
    } catch (apiError) {
      if (apiError.response?.status === 404) {
        console.warn('2FA API not implemented yet');
        return {
          success: false,
          message: 'Two-factor authentication is not available yet.'
        };
      } else {
        throw apiError;
      }
    }

  } catch (error) {
    console.error('=== 2FA DISABLE ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to disable two-factor authentication'
    };
  }
}

// ==========================================
// NOTIFICATION SETTINGS SECTION
// ==========================================

/**
 * Fetch notification preferences
 */
export async function fetchNotificationSettings() {
  try {
    console.log('=== FETCHING NOTIFICATION SETTINGS ===');

    try {
      const response = await settingsApi.get('/users/notification-settings');
      console.log('Notification settings from API:', response.data);
      
      return {
        success: true,
        data: response.data
      };
    } catch (apiError) {
      if (apiError.response?.status === 404) {
        console.warn('Notification settings API not available, using defaults');
        
        // Try to get from localStorage as fallback
        try {
          const saved = localStorage.getItem('userNotificationSettings');
          if (saved) {
            return {
              success: true,
              data: { ...getDefaultNotificationSettings(), ...JSON.parse(saved) }
            };
          }
        } catch (storageError) {
          console.warn('Could not read notification settings from localStorage');
        }
        
        return {
          success: true,
          data: getDefaultNotificationSettings()
        };
      } else {
        throw apiError;
      }
    }

  } catch (error) {
    console.error('=== NOTIFICATION SETTINGS FETCH ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: true,
      data: getDefaultNotificationSettings()
    };
  }
}

/**
 * Update notification preferences
 */
export async function updateNotificationSettings(notificationSettings) {
  try {
    console.log('=== UPDATING NOTIFICATION SETTINGS ===');
    console.log('Notification settings:', notificationSettings);

    try {
      const response = await settingsApi.put('/users/notification-settings', notificationSettings);
      console.log('Notification update response:', response.data);

      return {
        success: true,
        data: response.data,
        message: 'Notification settings updated successfully'
      };
    } catch (apiError) {
      if (apiError.response?.status === 404) {
        console.warn('Notification settings API not implemented, storing locally');
        
        try {
          localStorage.setItem('userNotificationSettings', JSON.stringify(notificationSettings));
          return {
            success: true,
            data: notificationSettings,
            message: 'Notification settings saved locally'
          };
        } catch (storageError) {
          throw new Error('Failed to save notification settings');
        }
      } else {
        throw apiError;
      }
    }

  } catch (error) {
    console.error('=== NOTIFICATION SETTINGS UPDATE ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to update notification settings'
    };
  }
}

// ==========================================
// PRIVACY SETTINGS SECTION
// ==========================================

/**
 * Fetch privacy settings
 */
export async function fetchPrivacySettings() {
  try {
    console.log('=== FETCHING PRIVACY SETTINGS ===');

    try {
      const response = await settingsApi.get('/users/privacy-settings');
      console.log('Privacy settings from API:', response.data);
      
      return {
        success: true,
        data: response.data
      };
    } catch (apiError) {
      if (apiError.response?.status === 404) {
        console.warn('Privacy settings API not available, using defaults');
        
        // Try to get from localStorage as fallback
        try {
          const saved = localStorage.getItem('userPrivacySettings');
          if (saved) {
            return {
              success: true,
              data: { ...getDefaultPrivacySettings(), ...JSON.parse(saved) }
            };
          }
        } catch (storageError) {
          console.warn('Could not read privacy settings from localStorage');
        }
        
        return {
          success: true,
          data: getDefaultPrivacySettings()
        };
      } else {
        throw apiError;
      }
    }

  } catch (error) {
    console.error('=== PRIVACY SETTINGS FETCH ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: true,
      data: getDefaultPrivacySettings()
    };
  }
}

/**
 * Update privacy settings
 */
export async function updatePrivacySettings(privacySettings) {
  try {
    console.log('=== UPDATING PRIVACY SETTINGS ===');
    console.log('Privacy settings:', privacySettings);

    const validatedSettings = validatePrivacySettings(privacySettings);

    try {
      const response = await settingsApi.put('/users/privacy-settings', validatedSettings);
      console.log('Privacy update response:', response.data);

      return {
        success: true,
        data: response.data,
        message: 'Privacy settings updated successfully'
      };
    } catch (apiError) {
      if (apiError.response?.status === 404) {
        console.warn('Privacy settings API not implemented, storing locally');
        
        try {
          localStorage.setItem('userPrivacySettings', JSON.stringify(validatedSettings));
          return {
            success: true,
            data: validatedSettings,
            message: 'Privacy settings saved locally'
          };
        } catch (storageError) {
          throw new Error('Failed to save privacy settings');
        }
      } else {
        throw apiError;
      }
    }

  } catch (error) {
    console.error('=== PRIVACY SETTINGS UPDATE ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to update privacy settings'
    };
  }
}

// ==========================================
// DATA MANAGEMENT SECTION
// ==========================================

/**
 * Request data export
 */
export async function requestDataExport() {
  try {
    console.log('=== REQUESTING DATA EXPORT ===');

    try {
      const response = await settingsApi.post('/users/export-data');
      console.log('Data export response:', response.data);

      return {
        success: true,
        data: response.data,
        message: 'Data export requested successfully. You will receive an email when ready.'
      };
    } catch (apiError) {
      if (apiError.response?.status === 404) {
        console.warn('Data export API not implemented yet');
        
        return {
          success: false,
          message: 'Data export feature is not available yet. Please contact support for manual data export.'
        };
      } else {
        throw apiError;
      }
    }

  } catch (error) {
    console.error('=== DATA EXPORT ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to request data export'
    };
  }
}

/**
 * Delete user account
 */
export async function deleteAccount(password, reason = '') {
  try {
    console.log('=== DELETING ACCOUNT ===');

    if (!password || password.trim() === '') {
      return {
        success: false,
        message: 'Password is required to delete account'
      };
    }

    try {
      const response = await settingsApi.delete('/users/account', {
        data: {
          password,
          reason: reason || 'User requested deletion'
        }
      });

      console.log('Account deletion response:', response.data);

      // Clear local storage and logout
      logoutWithCleanup();

      return {
        success: true,
        message: 'Account deleted successfully'
      };
    } catch (apiError) {
      if (apiError.response?.status === 404) {
        console.warn('Account deletion API not implemented yet');
        
        return {
          success: false,
          message: 'Account deletion feature is not available yet. Please contact support to delete your account.'
        };
      } else {
        throw apiError;
      }
    }

  } catch (error) {
    console.error('=== ACCOUNT DELETION ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to delete account'
    };
  }
}

// ==========================================
// SESSION MANAGEMENT SECTION
// ==========================================

/**
 * Fetch active sessions
 */
export async function fetchActiveSessions() {
  try {
    console.log('=== FETCHING ACTIVE SESSIONS ===');

    try {
      const response = await settingsApi.get('/users/sessions');
      console.log('Active sessions response:', response.data);

      return {
        success: true,
        data: response.data || []
      };
    } catch (apiError) {
      if (apiError.response?.status === 404) {
        console.warn('Session management API not implemented yet');
        
        // Return mock current session
        return {
          success: true,
          data: [{
            id: 'current',
            deviceName: 'Current Device',
            location: 'Unknown Location',
            browser: navigator.userAgent.split(' ')[0] || 'Unknown Browser',
            current: true,
            lastActive: 'Now'
          }]
        };
      } else {
        throw apiError;
      }
    }

  } catch (error) {
    console.error('=== ACTIVE SESSIONS FETCH ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: true,
      data: []
    };
  }
}

/**
 * Terminate a specific session
 */
export async function terminateSession(sessionId) {
  try {
    console.log('=== TERMINATING SESSION ===', sessionId);

    try {
      const response = await settingsApi.delete(`/users/sessions/${sessionId}`);
      console.log('Session termination response:', response.data);

      return {
        success: true,
        message: 'Session terminated successfully'
      };
    } catch (apiError) {
      if (apiError.response?.status === 404) {
        console.warn('Session management API not implemented yet');
        
        return {
          success: false,
          message: 'Session management is not available yet.'
        };
      } else {
        throw apiError;
      }
    }

  } catch (error) {
    console.error('=== SESSION TERMINATION ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to terminate session'
    };
  }
}

/**
 * Terminate all other sessions
 */
export async function terminateAllOtherSessions() {
  try {
    console.log('=== TERMINATING ALL OTHER SESSIONS ===');

    try {
      const response = await settingsApi.delete('/users/sessions/others');
      console.log('All sessions termination response:', response.data);

      return {
        success: true,
        message: 'All other sessions terminated successfully'
      };
    } catch (apiError) {
      if (apiError.response?.status === 404) {
        console.warn('Session management API not implemented yet');
        
        return {
          success: false,
          message: 'Session management is not available yet.'
        };
      } else {
        throw apiError;
      }
    }

  } catch (error) {
    console.error('=== ALL SESSIONS TERMINATION ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to terminate sessions'
    };
  }
}

// ==========================================
// UTILITY FUNCTIONS
// ==========================================

/**
 * Format profile data for API submission
 */
function formatProfileDataForUpdate(profileData, userType) {
  console.log('Formatting profile data for update:', userType);
  console.log('Raw profile data:', profileData);

  const baseData = {
    bio: (profileData.bio || '').trim(),
    location: (profileData.location || '').trim(),
    phoneNumber: (profileData.phone || profileData.phoneNumber || '').trim(),
    website: (profileData.website || '').trim()
  };

  // Remove empty strings
  Object.keys(baseData).forEach(key => {
    if (baseData[key] === '') {
      delete baseData[key];
    }
  });

  if (userType === 'VOLUNTEER') {
    const volunteerData = {
      ...baseData,
      firstName: (profileData.firstName || '').trim(),
      lastName: (profileData.lastName || '').trim(),
      displayName: (profileData.displayName || '').trim()
    };

    if (!volunteerData.displayName && (volunteerData.firstName || volunteerData.lastName)) {
      volunteerData.displayName = `${volunteerData.firstName || ''} ${volunteerData.lastName || ''}`.trim();
    }

    if (profileData.skills && Array.isArray(profileData.skills)) {
      volunteerData.skills = profileData.skills;
    }
    if (profileData.interests && Array.isArray(profileData.interests)) {
      volunteerData.interests = profileData.interests;
    }
    if (profileData.availability && typeof profileData.availability === 'object') {
      volunteerData.availability = profileData.availability;
    }

    console.log('Formatted volunteer data:', volunteerData);
    return volunteerData;
    
  } else if (userType === 'ORGANIZATION') {
    const organizationData = {
      ...baseData,
      organizationName: (profileData.displayName || profileData.organizationName || '').trim(),
      organizationType: (profileData.organizationType || '').trim(),
      description: (profileData.bio || profileData.description || '').trim(),
      missionStatement: (profileData.missionStatement || '').trim()
    };

    if (profileData.categories && Array.isArray(profileData.categories)) {
      organizationData.categories = profileData.categories;
    }
    if (profileData.foundedYear) {
      organizationData.foundedYear = parseInt(profileData.foundedYear) || null;
    }
    if (profileData.employeeCount) {
      organizationData.employeeCount = profileData.employeeCount;
    }

    console.log('Formatted organization data:', organizationData);
    return organizationData;
  }

  console.log('Formatted base data:', baseData);
  return baseData;
}

/**
 * Get default notification settings
 */
export function getDefaultNotificationSettings() {
  return {
    emailNotifications: true,
    pushNotifications: true,
    eventReminders: true,
    organizationUpdates: true,
    connectionRequests: true,
    weeklyDigest: false,
    marketingEmails: false
  };
}

/**
 * Get default privacy settings
 */
export function getDefaultPrivacySettings() {
  const user = getCurrentUser();
  
  return {
    profileVisibility: 'public',
    showEmail: false,
    showPhone: false,
    showLocation: true,
    allowMessaging: true,
    showActivity: true,
    searchable: true,
    allowEventNotifications: true,
    showVolunteerHistory: user?.userType === 'VOLUNTEER' ? true : false,
    showOrganizationEvents: user?.userType === 'ORGANIZATION' ? true : false
  };
}

/**
 * Validate privacy settings
 */
function validatePrivacySettings(settings) {
  const validSettings = {};
  
  const validKeys = {
    profileVisibility: ['public', 'private', 'connections'],
    showEmail: 'boolean',
    showPhone: 'boolean',
    showLocation: 'boolean',
    allowMessaging: 'boolean',
    showActivity: 'boolean',
    searchable: 'boolean',
    allowEventNotifications: 'boolean',
    showVolunteerHistory: 'boolean',
    showOrganizationEvents: 'boolean'
  };

  Object.keys(validKeys).forEach(key => {
    if (settings.hasOwnProperty(key)) {
      const expectedType = validKeys[key];
      
      if (Array.isArray(expectedType)) {
        if (expectedType.includes(settings[key])) {
          validSettings[key] = settings[key];
        }
      } else if (expectedType === 'boolean') {
        validSettings[key] = Boolean(settings[key]);
      } else {
        validSettings[key] = settings[key];
      }
    }
  });

  return validSettings;
}

// ==========================================
// DEFAULT EXPORT
// ==========================================

export default {
  fetchUserSettings,
  updateProfileSettings,
  changePassword,
  enableTwoFactor,
  disableTwoFactor,
  fetchNotificationSettings,
  updateNotificationSettings,
  fetchPrivacySettings,
  updatePrivacySettings,
  requestDataExport,
  deleteAccount,
  fetchActiveSessions,
  terminateSession,
  terminateAllOtherSessions,
  getDefaultNotificationSettings,
  getDefaultPrivacySettings
};