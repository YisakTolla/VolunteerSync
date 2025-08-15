// ========================================
// PROFILE PAGE SERVICE
// Wrapper service for Profile component
// ========================================

import { 
  fetchMyProfile, 
  fetchPublicProfile, 
  updateProfile, 
  uploadProfileImage as uploadImage,
  getCurrentUser 
} from './profileService';

// ==========================================
// PROFILE DATA FUNCTIONS
// ==========================================

/**
 * Get profile data for the Profile page
 * @param {string|number} userId - User ID (optional, if not provided uses current user)
 * @returns {Object} - Success/error response with profile data and user type
 */
export async function getProfileData(userId = null) {
  try {
    console.log('=== GETTING PROFILE DATA ===');
    console.log('User ID:', userId);

    const currentUser = getCurrentUser();
    
    if (!currentUser) {
      return {
        success: false,
        message: 'User not logged in'
      };
    }

    let result;
    
    // If no userId provided or userId matches current user, fetch own profile
    if (!userId || userId === currentUser.id || userId === String(currentUser.id)) {
      console.log('Fetching own profile');
      result = await fetchMyProfile();
    } else {
      // Fetch public profile of another user
      console.log('Fetching public profile for user:', userId);
      // For public profiles, we need to determine the user type
      // For now, we'll try volunteer first, then organization
      result = await fetchPublicProfile(userId, 'VOLUNTEER');
      
      if (!result.success) {
        // Try as organization if volunteer fetch failed
        result = await fetchPublicProfile(userId, 'ORGANIZATION');
      }
    }

    if (result.success) {
      // Determine user type from the data
      const userType = result.data.userType || 
                      (result.data.organizationName ? 'organization' : 'volunteer');
      
      // Format the data to ensure all required fields exist
      const formattedData = formatProfileDataForDisplay(result.data, userType);
      
      return {
        success: true,
        data: formattedData,
        userType: userType === 'ORGANIZATION' ? 'organization' : 'volunteer'
      };
    }

    return result;

  } catch (error) {
    console.error('Error getting profile data:', error);
    return {
      success: false,
      message: 'Failed to load profile data'
    };
  }
}

/**
 * Update profile data
 * @param {Object} updatedData - Updated profile information
 * @returns {Object} - Success/error response
 */
export async function updateProfileData(updatedData) {
  try {
    console.log('=== UPDATING PROFILE DATA ===');
    return await updateProfile(updatedData);
  } catch (error) {
    console.error('Error updating profile data:', error);
    return {
      success: false,
      message: 'Failed to update profile data'
    };
  }
}

/**
 * Upload profile image (wrapper function)
 * @param {File} file - Image file to upload
 * @param {string} type - Image type ('profile' or 'cover')
 * @returns {Object} - Success/error response
 */
export async function uploadProfileImage(file, type = 'profile') {
  try {
    console.log('=== UPLOADING PROFILE IMAGE ===');
    return await uploadImage(file, type);
  } catch (error) {
    console.error('Error uploading profile image:', error);
    return {
      success: false,
      message: 'Failed to upload image'
    };
  }
}

/**
 * Add interest to user profile
 * @param {string} interest - Interest to add
 * @returns {Object} - Success/error response
 */
export async function addInterest(interest) {
  try {
    console.log('=== ADDING INTEREST ===');
    console.log('Interest:', interest);

    const currentUser = getCurrentUser();
    if (!currentUser) {
      return {
        success: false,
        message: 'User not logged in'
      };
    }

    // Get current interests
    const currentInterests = currentUser.interests || '';
    const interestsArray = currentInterests.split(',').map(i => i.trim()).filter(i => i);
    
    // Add new interest if not already present
    if (!interestsArray.includes(interest)) {
      interestsArray.push(interest);
    }

    // Update profile with new interests
    const updatedData = {
      interests: interestsArray.join(', ')
    };

    return await updateProfile(updatedData);

  } catch (error) {
    console.error('Error adding interest:', error);
    return {
      success: false,
      message: 'Failed to add interest'
    };
  }
}

/**
 * Add skill to user profile
 * @param {string} skill - Skill to add
 * @returns {Object} - Success/error response
 */
export async function addSkill(skill) {
  try {
    console.log('=== ADDING SKILL ===');
    console.log('Skill:', skill);

    const currentUser = getCurrentUser();
    if (!currentUser) {
      return {
        success: false,
        message: 'User not logged in'
      };
    }

    // Get current skills
    const currentSkills = currentUser.skills || '';
    const skillsArray = currentSkills.split(',').map(s => s.trim()).filter(s => s);
    
    // Add new skill if not already present
    if (!skillsArray.includes(skill)) {
      skillsArray.push(skill);
    }

    // Update profile with new skills
    const updatedData = {
      skills: skillsArray.join(', ')
    };

    return await updateProfile(updatedData);

  } catch (error) {
    console.error('Error adding skill:', error);
    return {
      success: false,
      message: 'Failed to add skill'
    };
  }
}

// ==========================================
// DATA FORMATTING FUNCTIONS
// ==========================================

/**
 * Format profile data for display in Profile component
 * @param {Object} rawData - Raw profile data from API
 * @param {string} userType - User type ('volunteer' or 'organization')
 * @returns {Object} - Formatted profile data
 */
function formatProfileDataForDisplay(rawData, userType) {
  console.log('=== FORMATTING PROFILE DATA FOR DISPLAY ===');
  console.log('Raw data:', rawData);
  console.log('User type:', userType);

  // Base data that all profiles have
  const baseData = {
    id: rawData.id,
    name: rawData.name || 
          `${rawData.firstName || ''} ${rawData.lastName || ''}`.trim() || 
          rawData.organizationName || 'Unknown User',
    email: rawData.email || '',
    phone: rawData.phone || rawData.phoneNumber || '',
    bio: rawData.bio || rawData.description || '',
    location: rawData.location || rawData.address ||
              `${rawData.city || ''}, ${rawData.state || ''}`.replace(/, $/, '') || '',
    website: rawData.website || '',
    profileImage: rawData.profileImageUrl || rawData.profileImage || '/api/placeholder/150/150',
    coverImage: rawData.coverImageUrl || rawData.coverImage || '/api/placeholder/800/200',
    joinDate: formatJoinDate(rawData.createdAt || rawData.joinDate),
  };

  if (userType === 'volunteer') {
    return {
      ...baseData,
      type: 'volunteer',
      interests: parseArrayField(rawData.interests),
      skills: parseArrayField(rawData.skills),
      stats: {
        hoursVolunteered: rawData.totalHours || 0,
        eventsAttended: rawData.eventsAttended || 0,
        organizations: rawData.organizationsCount || 0,
      },
      badges: rawData.badges || [],
      organizations: rawData.organizations || [],
    };
  } else {
    // Organization profile data
    const organizationData = {
      ...baseData,
      type: 'organization',
      organizationType: rawData.organizationType || 'Non-Profit',
      founded: formatFoundedDate(rawData.foundedYear || rawData.founded),
      foundedYear: rawData.foundedYear || null,
      employeeCount: rawData.employeeCount || null,
      isVerified: rawData.verificationLevel === 'Verified' || false,
      ein: rawData.ein || rawData.taxId || 'Not provided',
      causes: parseArrayField(rawData.categories || rawData.causes),
      services: parseArrayField(rawData.services),
      categories: parseArrayField(rawData.categories),
      totalVolunteersServed: rawData.totalVolunteersServed || 0,
      totalEventsHosted: rawData.totalEventsHosted || 0,
      fundingRaised: rawData.fundingRaised || 0,
      fundingGoal: rawData.fundingGoal || 50000,
      stats: {
        volunteers: rawData.volunteersCount || rawData.totalVolunteersServed || 0,
        eventsHosted: rawData.eventsHosted || rawData.totalEventsHosted || 0,
        hoursImpacted: rawData.totalVolunteerHours || 0,
        fundingGoal: rawData.fundingGoal || 50000,
        fundingRaised: rawData.fundingRaised || 0,
      },
      achievements: rawData.achievements || [],
      recentActivity: rawData.recentActivity || [],
      volunteers: rawData.volunteers || [],
    };

    console.log('🏢 Formatted organization data:', organizationData);
    return organizationData;
  }
}

/**
 * Parse comma-separated string into array
 * @param {string|array} field - Field to parse
 * @returns {array} - Parsed array
 */
function parseArrayField(field) {
  if (Array.isArray(field)) {
    return field;
  }
  if (typeof field === 'string' && field.trim()) {
    return field.split(',').map(item => item.trim()).filter(item => item);
  }
  return [];
}

/**
 * Format join date for display
 * @param {string} dateString - Date string to format
 * @returns {string} - Formatted date
 */
function formatJoinDate(dateString) {
  if (!dateString) return 'Recently';
  
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      year: 'numeric', 
      month: 'long' 
    });
  } catch (error) {
    return 'Recently';
  }
}

/**
 * Format founded date for display
 * @param {string|number} founded - Founded year or date string
 * @returns {string} - Formatted founded date
 */
function formatFoundedDate(founded) {
  if (!founded) return 'Unknown';
  
  if (typeof founded === 'number') {
    return founded.toString();
  }
  
  if (typeof founded === 'string') {
    // Try to extract year from date string
    const year = founded.match(/\d{4}/);
    return year ? year[0] : founded;
  }
  
  return 'Unknown';
}

// ==========================================
// EXPORTS
// ==========================================

export default {
  getProfileData,
  updateProfileData,
  uploadProfileImage,
  addInterest,
  addSkill
};