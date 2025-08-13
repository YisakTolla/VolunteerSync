// ========================================
// SETTINGS SERVICE
// Handle all settings-related API calls
// ========================================

import axios from 'axios';
import { 
  ensureValidToken, 
  getCurrentUser, 
  logoutWithCleanup,
  updateLocalUser 
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
    console.log('✅ Settings API Response:', response.status, response.config.url);
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    
    console.error('❌ Settings API Error:', {
      status: error.response?.status,
      url: error.config?.url,
      data: error.response?.data,
      message: error.message
    });

    // Handle authentication errors
    if (error.response?.status === 401 && !originalRequest._retry) {
      console.log('🔄 401 error in settings service, attempting token refresh...');
      
      try {
        originalRequest._retry = true;
        const token = await ensureValidToken();
        
        if (token) {
          originalRequest.headers.Authorization = `Bearer ${token}`;
          return settingsApi(originalRequest);
        }
      } catch (refreshError) {
        console.error('❌ Token refresh failed in settings service:', refreshError);
        logoutWithCleanup();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);

// ==========================================
// PROFILE SETTINGS FUNCTIONS
// ==========================================

/**
 * Fetch current user settings/profile data
 * @returns {Object} - Success/error response with settings data
 */
export async function fetchUserSettings() {
  try {
    console.log('=== FETCHING USER SETTINGS ===');

    const user = getCurrentUser();
    if (!user) {
      throw new Error('User not logged in');
    }

    const userType = user.userType;
    let response;

    if (userType === 'VOLUNTEER') {
      response = await settingsApi.get('/volunteer-profiles/me');
    } else if (userType === 'ORGANIZATION') {
      response = await settingsApi.get('/organization-profiles/me');
    } else {
      throw new Error('Invalid user type');
    }

    console.log('Settings fetch response:', response.data);

    return {
      success: true,
      data: response.data
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
 * @param {Object} profileData - Updated profile data
 * @returns {Object} - Success/error response
 */
export async function updateProfileSettings(profileData) {
  try {
    console.log('=== UPDATING PROFILE SETTINGS ===');
    console.log('Profile data:', profileData);

    const user = getCurrentUser();
    if (!user) {
      throw new Error('User not logged in');
    }

    const userType = user.userType;
    let response;

    const formattedData = formatProfileData(profileData, userType);
    console.log('Formatted data:', formattedData);

    if (userType === 'VOLUNTEER') {
      response = await settingsApi.put('/volunteer-profiles/me', formattedData);
    } else if (userType === 'ORGANIZATION') {
      response = await settingsApi.put('/organization-profiles/me', formattedData);
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
    console.error('=== PROFILE SETTINGS UPDATE ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to update profile'
    };
  }
}

// ==========================================
// ACCOUNT SECURITY FUNCTIONS
// ==========================================

/**
 * Change user password
 * @param {Object} passwordData - Password change data
 * @returns {Object} - Success/error response
 */
export async function changePassword(passwordData) {
  try {
    console.log('=== CHANGING PASSWORD ===');

    const { currentPassword, newPassword, confirmPassword } = passwordData;

    // Validate passwords match
    if (newPassword !== confirmPassword) {
      return {
        success: false,
        message: 'New passwords do not match'
      };
    }

    // Validate password strength
    if (newPassword.length < 8) {
      return {
        success: false,
        message: 'Password must be at least 8 characters long'
      };
    }

    const response = await settingsApi.put('/auth/change-password', {
      currentPassword,
      newPassword
    });

    console.log('Password change response:', response.data);

    return {
      success: true,
      message: 'Password updated successfully'
    };

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
 * @returns {Object} - Success/error response with setup data
 */
export async function enableTwoFactor() {
  try {
    console.log('=== ENABLING TWO-FACTOR AUTH ===');

    const response = await settingsApi.post('/auth/2fa/enable');

    console.log('2FA enable response:', response.data);

    return {
      success: true,
      data: response.data,
      message: 'Two-factor authentication setup initiated'
    };

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
 * @param {string} password - User password for verification
 * @returns {Object} - Success/error response
 */
export async function disableTwoFactor(password) {
  try {
    console.log('=== DISABLING TWO-FACTOR AUTH ===');

    const response = await settingsApi.post('/auth/2fa/disable', { password });

    console.log('2FA disable response:', response.data);

    return {
      success: true,
      message: 'Two-factor authentication disabled'
    };

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
// NOTIFICATION SETTINGS FUNCTIONS
// ==========================================

/**
 * Fetch notification preferences
 * @returns {Object} - Success/error response with notification settings
 */
export async function fetchNotificationSettings() {
  try {
    console.log('=== FETCHING NOTIFICATION SETTINGS ===');

    const response = await settingsApi.get('/users/notification-settings');

    console.log('Notification settings response:', response.data);

    return {
      success: true,
      data: response.data
    };

  } catch (error) {
    console.error('=== NOTIFICATION SETTINGS FETCH ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to fetch notification settings'
    };
  }
}

/**
 * Update notification preferences
 * @param {Object} notificationSettings - Updated notification settings
 * @returns {Object} - Success/error response
 */
export async function updateNotificationSettings(notificationSettings) {
  try {
    console.log('=== UPDATING NOTIFICATION SETTINGS ===');
    console.log('Notification settings:', notificationSettings);

    const response = await settingsApi.put('/users/notification-settings', notificationSettings);

    console.log('Notification update response:', response.data);

    return {
      success: true,
      data: response.data,
      message: 'Notification settings updated successfully'
    };

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
// PRIVACY SETTINGS FUNCTIONS
// ==========================================

/**
 * Fetch privacy settings
 * @returns {Object} - Success/error response with privacy settings
 */
export async function fetchPrivacySettings() {
  try {
    console.log('=== FETCHING PRIVACY SETTINGS ===');

    const response = await settingsApi.get('/users/privacy-settings');

    console.log('Privacy settings response:', response.data);

    return {
      success: true,
      data: response.data
    };

  } catch (error) {
    console.error('=== PRIVACY SETTINGS FETCH ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to fetch privacy settings'
    };
  }
}

/**
 * Update privacy settings
 * @param {Object} privacySettings - Updated privacy settings
 * @returns {Object} - Success/error response
 */
export async function updatePrivacySettings(privacySettings) {
  try {
    console.log('=== UPDATING PRIVACY SETTINGS ===');
    console.log('Privacy settings:', privacySettings);

    const response = await settingsApi.put('/users/privacy-settings', privacySettings);

    console.log('Privacy update response:', response.data);

    return {
      success: true,
      data: response.data,
      message: 'Privacy settings updated successfully'
    };

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
// DATA MANAGEMENT FUNCTIONS
// ==========================================

/**
 * Request data export
 * @returns {Object} - Success/error response
 */
export async function requestDataExport() {
  try {
    console.log('=== REQUESTING DATA EXPORT ===');

    const response = await settingsApi.post('/users/export-data');

    console.log('Data export response:', response.data);

    return {
      success: true,
      data: response.data,
      message: 'Data export requested successfully. You will receive an email when ready.'
    };

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
 * @param {string} password - User password for verification
 * @param {string} reason - Reason for deletion (optional)
 * @returns {Object} - Success/error response
 */
export async function deleteAccount(password, reason = '') {
  try {
    console.log('=== DELETING ACCOUNT ===');

    const response = await settingsApi.delete('/users/account', {
      data: {
        password,
        reason
      }
    });

    console.log('Account deletion response:', response.data);

    // Clear local storage
    logoutWithCleanup();

    return {
      success: true,
      message: 'Account deleted successfully'
    };

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
// SESSION MANAGEMENT FUNCTIONS
// ==========================================

/**
 * Fetch active sessions
 * @returns {Object} - Success/error response with session data
 */
export async function fetchActiveSessions() {
  try {
    console.log('=== FETCHING ACTIVE SESSIONS ===');

    const response = await settingsApi.get('/auth/sessions');

    console.log('Sessions response:', response.data);

    return {
      success: true,
      data: response.data
    };

  } catch (error) {
    console.error('=== SESSIONS FETCH ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to fetch sessions'
    };
  }
}

/**
 * Terminate a specific session
 * @param {string} sessionId - Session ID to terminate
 * @returns {Object} - Success/error response
 */
export async function terminateSession(sessionId) {
  try {
    console.log('=== TERMINATING SESSION ===');
    console.log('Session ID:', sessionId);

    const response = await settingsApi.delete(`/auth/sessions/${sessionId}`);

    console.log('Session termination response:', response.data);

    return {
      success: true,
      message: 'Session terminated successfully'
    };

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
 * @returns {Object} - Success/error response
 */
export async function terminateAllOtherSessions() {
  try {
    console.log('=== TERMINATING ALL OTHER SESSIONS ===');

    const response = await settingsApi.delete('/auth/sessions/others');

    console.log('All sessions termination response:', response.data);

    return {
      success: true,
      message: 'All other sessions terminated successfully'
    };

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
 * @param {Object} profileData - Raw profile data
 * @param {string} userType - User type (VOLUNTEER/ORGANIZATION)
 * @returns {Object} - Formatted profile data
 */
function formatProfileData(profileData, userType) {
  console.log('🔄 Formatting profile data for settings:', userType);
  console.log('Raw profile data:', profileData);

  const baseData = {
    bio: profileData.bio || '',
    location: profileData.location || '',
    phoneNumber: profileData.phone || profileData.phoneNumber || '',
    website: profileData.website || '',
  };

  if (userType === 'VOLUNTEER') {
    const formattedData = {
      ...baseData,
      firstName: profileData.firstName || '',
      lastName: profileData.lastName || '',
      displayName: profileData.displayName || `${profileData.firstName || ''} ${profileData.lastName || ''}`.trim(),
    };
    console.log('📝 Formatted volunteer data:', formattedData);
    return formattedData;
  } else if (userType === 'ORGANIZATION') {
    const formattedData = {
      ...baseData,
      organizationName: profileData.displayName || profileData.organizationName || '',
      organizationType: profileData.organizationType || '',
    };
    console.log('🏢 Formatted organization data:', formattedData);
    return formattedData;
  }

  console.log('📝 Formatted base data:', baseData);
  return baseData;
}

/**
 * Get default notification settings
 * @returns {Object} - Default notification settings
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
 * @returns {Object} - Default privacy settings
 */
export function getDefaultPrivacySettings() {
  return {
    profileVisibility: 'public',
    showEmail: false,
    showPhone: false,
    showLocation: true,
    allowMessaging: true,
    showActivity: true,
    searchable: true
  };
}

// ==========================================
// EXPORTS
// ==========================================

export default {
  // Profile settings
  fetchUserSettings,
  updateProfileSettings,
  
  // Account security
  changePassword,
  enableTwoFactor,
  disableTwoFactor,
  
  // Notifications
  fetchNotificationSettings,
  updateNotificationSettings,
  
  // Privacy
  fetchPrivacySettings,
  updatePrivacySettings,
  
  // Data management
  requestDataExport,
  deleteAccount,
  
  // Session management
  fetchActiveSessions,
  terminateSession,
  terminateAllOtherSessions,
  
  // Utilities
  getDefaultNotificationSettings,
  getDefaultPrivacySettings
};