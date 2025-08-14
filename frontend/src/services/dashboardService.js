import axios from 'axios';
import { ensureValidToken, getCurrentUser, logoutWithCleanup } from './authService';

// ==========================================
// API BASE CONFIGURATION
// ==========================================

const API_BASE_URL = 'http://localhost:8080/api';

const dashboardApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
dashboardApi.interceptors.request.use(async (config) => {
  try {
    const token = await ensureValidToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  } catch (error) {
    console.error('Token validation failed in dashboard service:', error);
    logoutWithCleanup();
    window.location.href = '/login';
    return Promise.reject(error);
  }
});

// Response interceptor
dashboardApi.interceptors.response.use(
  (response) => {
    console.log('✅ Dashboard API Response:', response.status, response.config.url);
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    
    console.error('❌ Dashboard API Error:', {
      status: error.response?.status,
      url: error.config?.url,
      data: error.response?.data,
      message: error.message
    });

    if (error.response?.status === 401 && !originalRequest._retry) {
      console.log('🔄 401 error in dashboard service, attempting token refresh...');
      
      try {
        originalRequest._retry = true;
        const token = await ensureValidToken();
        
        if (token) {
          originalRequest.headers.Authorization = `Bearer ${token}`;
          return dashboardApi(originalRequest);
        }
      } catch (refreshError) {
        console.error('❌ Token refresh failed in dashboard service:', refreshError);
        logoutWithCleanup();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);

// ==========================================
// MAIN DASHBOARD FUNCTIONS
// ==========================================

const getDashboardData = async () => {
  try {
    console.log('=== FETCHING DASHBOARD DATA ===');

    const user = getCurrentUser();
    if (!user) {
      throw new Error('User not logged in');
    }

    console.log('👤 Current user type:', user.userType);

    let dashboardData = {
      user: user,
      profile: null,
      stats: null,
      events: [],
      applications: [],
      error: null
    };

    if (user.userType === 'VOLUNTEER') {
      dashboardData = await getVolunteerDashboardData(user);
    } else if (user.userType === 'ORGANIZATION') {
      dashboardData = await getOrganizationDashboardData(user);
    } else {
      throw new Error(`Invalid user type: ${user.userType}`);
    }

    console.log('✅ Dashboard data loaded:', dashboardData);

    return {
      success: true,
      data: dashboardData
    };

  } catch (error) {
    console.error('=== DASHBOARD DATA ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to load dashboard data',
      data: null
    };
  }
};

const getVolunteerDashboardData = async (user) => {
  console.log('🙋‍♀️ Fetching volunteer dashboard data...');
  
  const dashboardData = {
    user: user,
    userType: 'VOLUNTEER',
    profile: null,
    stats: {
      hoursCompleted: 0,
      eventsAttended: 0,
      upcomingEvents: 0,
      connections: 0,
      rating: 0
    },
    events: [],
    applications: [],
    badges: [],
    recentActivity: []
  };

  try {
    console.log('📝 Fetching volunteer profile...');
    const profileResponse = await dashboardApi.get('/volunteer-profiles/me');
    dashboardData.profile = profileResponse.data;
    
    if (profileResponse.data) {
      dashboardData.stats.hoursCompleted = profileResponse.data.totalVolunteerHours || 0;
      dashboardData.stats.eventsAttended = profileResponse.data.eventsParticipated || 0;
      dashboardData.stats.rating = profileResponse.data.rating || 0;
    }

    console.log('✅ Volunteer profile loaded');
  } catch (error) {
    console.error('❌ Error fetching volunteer profile:', error.response?.data || error.message);
    dashboardData.error = 'Failed to load profile data';
  }

  try {
    console.log('📊 Fetching volunteer statistics...');
    const statsResponse = await dashboardApi.get('/volunteer-profiles/me/stats');
    if (statsResponse.data) {
      dashboardData.stats = { ...dashboardData.stats, ...statsResponse.data };
    }
    console.log('✅ Volunteer statistics loaded');
  } catch (error) {
    console.error('❌ Error fetching volunteer statistics:', error.response?.data || error.message);
  }

  try {
    console.log('📋 Fetching volunteer applications...');
    const applicationsResponse = await dashboardApi.get('/applications/volunteer/me');
    dashboardData.applications = applicationsResponse.data || [];
    
    const upcomingEvents = dashboardData.applications.filter(app => 
      app.status === 'APPROVED' && new Date(app.event?.eventDate) > new Date()
    );
    dashboardData.stats.upcomingEvents = upcomingEvents.length;
    
    console.log('✅ Volunteer applications loaded');
  } catch (error) {
    console.error('❌ Error fetching volunteer applications:', error.response?.data || error.message);
  }

  try {
    console.log('📅 Fetching volunteer events...');
    const eventsResponse = await dashboardApi.get('/events/volunteer/me');
    dashboardData.events = eventsResponse.data || [];
    console.log('✅ Volunteer events loaded');
  } catch (error) {
    console.error('❌ Error fetching volunteer events:', error.response?.data || error.message);
  }

  try {
    console.log('🏆 Fetching volunteer badges...');
    const badgesResponse = await dashboardApi.get('/volunteer-profiles/me/badges');
    dashboardData.badges = badgesResponse.data || [];
    console.log('✅ Volunteer badges loaded');
  } catch (error) {
    console.error('❌ Error fetching volunteer badges:', error.response?.data || error.message);
  }

  try {
    console.log('📈 Fetching volunteer activity...');
    const activityResponse = await dashboardApi.get('/volunteer-profiles/me/activity');
    dashboardData.recentActivity = activityResponse.data || [];
    console.log('✅ Volunteer activity loaded');
  } catch (error) {
    console.error('❌ Error fetching volunteer activity:', error.response?.data || error.message);
  }

  return dashboardData;
};

const getOrganizationDashboardData = async (user) => {
  console.log('🏢 Fetching organization dashboard data...');
  
  const dashboardData = {
    user: user,
    userType: 'ORGANIZATION',
    profile: null,
    stats: {
      activeEvents: 0,
      totalVolunteers: 0,
      pendingApplications: 0,
      eventsThisMonth: 0,
      totalEvents: 0,
      profileViews: 0
    },
    events: [],
    applications: [],
    volunteers: [],
    analytics: null
  };

  try {
    console.log('📝 Fetching organization profile...');
    const profileResponse = await dashboardApi.get('/organization-profiles/me');
    dashboardData.profile = profileResponse.data;
    console.log('✅ Organization profile loaded');
  } catch (error) {
    console.error('❌ Error fetching organization profile:', error.response?.data || error.message);
    dashboardData.error = 'Failed to load profile data';
  }

  try {
    console.log('📅 Fetching organization events...');
    const eventsResponse = await dashboardApi.get('/events/organization/me');
    dashboardData.events = eventsResponse.data || [];
    
    const currentDate = new Date();
    const currentMonth = currentDate.getMonth();
    const currentYear = currentDate.getFullYear();
    
    dashboardData.stats.totalEvents = dashboardData.events.length;
    dashboardData.stats.activeEvents = dashboardData.events.filter(event => 
      event.status === 'ACTIVE' && new Date(event.eventDate) > currentDate
    ).length;
    
    dashboardData.stats.eventsThisMonth = dashboardData.events.filter(event => {
      const eventDate = new Date(event.eventDate);
      return eventDate.getMonth() === currentMonth && eventDate.getFullYear() === currentYear;
    }).length;
    
    console.log('✅ Organization events loaded');
  } catch (error) {
    console.error('❌ Error fetching organization events:', error.response?.data || error.message);
  }

  try {
    console.log('📋 Fetching organization applications...');
    const applicationsResponse = await dashboardApi.get('/applications/organization/me');
    dashboardData.applications = applicationsResponse.data || [];
    
    dashboardData.stats.pendingApplications = dashboardData.applications.filter(app => 
      app.status === 'PENDING'
    ).length;
    
    console.log('✅ Organization applications loaded');
  } catch (error) {
    console.error('❌ Error fetching organization applications:', error.response?.data || error.message);
  }

  try {
    console.log('👥 Fetching organization volunteers...');
    const volunteersResponse = await dashboardApi.get('/volunteer-management/volunteers');
    dashboardData.volunteers = volunteersResponse.data || [];
    dashboardData.stats.totalVolunteers = dashboardData.volunteers.length;
    console.log('✅ Organization volunteers loaded');
  } catch (error) {
    console.error('❌ Error fetching organization volunteers:', error.response?.data || error.message);
  }

  try {
    console.log('📊 Fetching organization statistics...');
    const statsResponse = await dashboardApi.get('/organization-profiles/me/stats');
    if (statsResponse.data) {
      dashboardData.stats = { ...dashboardData.stats, ...statsResponse.data };
    }
    console.log('✅ Organization statistics loaded');
  } catch (error) {
    console.error('❌ Error fetching organization statistics:', error.response?.data || error.message);
  }

  return dashboardData;
};

const getQuickStats = async () => {
  try {
    console.log('=== FETCHING QUICK STATS ===');

    const user = getCurrentUser();
    if (!user) {
      throw new Error('User not logged in');
    }

    let statsEndpoint;
    if (user.userType === 'VOLUNTEER') {
      statsEndpoint = '/volunteer-profiles/me/stats';
    } else if (user.userType === 'ORGANIZATION') {
      statsEndpoint = '/organization-profiles/me/stats';
    } else {
      throw new Error('Invalid user type');
    }

    const response = await dashboardApi.get(statsEndpoint);

    return {
      success: true,
      data: response.data
    };

  } catch (error) {
    console.error('=== QUICK STATS ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to load statistics',
      data: null
    };
  }
};

const refreshDashboardData = async () => {
  try {
    console.log('=== REFRESHING DASHBOARD DATA ===');
    return await getDashboardData();
  } catch (error) {
    console.error('=== REFRESH DASHBOARD ERROR ===');
    console.error('Error:', error);
    
    return {
      success: false,
      message: 'Failed to refresh dashboard data',
      data: null
    };
  }
};

// ==========================================
// HELPER FUNCTIONS
// ==========================================

const isProfileComplete = (user) => {
  if (!user) return false;
  
  if (user.userType === 'VOLUNTEER') {
    return !!(user.firstName && user.lastName && user.bio);
  } else if (user.userType === 'ORGANIZATION') {
    return !!(user.organizationName && user.description);
  }
  
  return false;
};

const getUserWelcomeName = (user) => {
  if (!user) return 'User';
  
  if (user.userType === 'ORGANIZATION' && user.organizationName) {
    return user.organizationName;
  }
  
  if (user.userType === 'VOLUNTEER') {
    if (user.firstName && user.lastName) {
      return `${user.firstName} ${user.lastName}`;
    } else if (user.firstName) {
      return user.firstName;
    }
  }
  
  // Fallback to email prefix
  if (user.email) {
    return user.email.split('@')[0];
  }
  
  return 'User';
};

const getProfileCompletionStatus = (user) => {
  if (!user) return { isComplete: false, missingFields: [] };
  
  const missingFields = [];
  
  if (user.userType === 'VOLUNTEER') {
    if (!user.firstName) missingFields.push('First Name');
    if (!user.lastName) missingFields.push('Last Name');
    if (!user.bio) missingFields.push('Bio');
    if (!user.skills || user.skills.length === 0) missingFields.push('Skills');
  } else if (user.userType === 'ORGANIZATION') {
    if (!user.organizationName) missingFields.push('Organization Name');
    if (!user.description) missingFields.push('Description');
    if (!user.address) missingFields.push('Address');
  }
  
  return {
    isComplete: missingFields.length === 0,
    missingFields: missingFields,
    completionPercentage: user.userType === 'VOLUNTEER' 
      ? Math.round(((4 - missingFields.length) / 4) * 100)
      : Math.round(((3 - missingFields.length) / 3) * 100)
  };
};

// ==========================================
// PROFILE IMAGE UPLOAD
// ==========================================

const uploadProfileImage = async (file, imageType = 'profile') => {
  try {
    console.log('=== UPLOADING PROFILE IMAGE ===');
    console.log('File:', file.name, 'Type:', imageType);

    const formData = new FormData();
    formData.append('image', file);
    formData.append('imageType', imageType);

    const user = getCurrentUser();
    let uploadEndpoint;
    
    if (user.userType === 'VOLUNTEER') {
      uploadEndpoint = '/volunteer-profiles/me/upload-image';
    } else if (user.userType === 'ORGANIZATION') {
      uploadEndpoint = '/organization-profiles/me/upload-image';
    } else {
      throw new Error('Invalid user type for image upload');
    }

    const response = await dashboardApi.post(uploadEndpoint, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    console.log('✅ Image uploaded successfully');

    return {
      success: true,
      data: response.data,
      imageUrl: response.data.imageUrl
    };

  } catch (error) {
    console.error('=== IMAGE UPLOAD ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to upload image',
      data: null
    };
  }
};

// ==========================================
// ACTIVITY TRACKING
// ==========================================

const logActivity = async (activityData) => {
  try {
    console.log('=== LOGGING ACTIVITY ===');
    
    const user = getCurrentUser();
    if (!user) {
      throw new Error('User not logged in');
    }

    let activityEndpoint;
    if (user.userType === 'VOLUNTEER') {
      activityEndpoint = '/volunteer-profiles/me/activity';
    } else if (user.userType === 'ORGANIZATION') {
      activityEndpoint = '/organization-profiles/me/activity';
    } else {
      throw new Error('Invalid user type');
    }

    const response = await dashboardApi.post(activityEndpoint, {
      ...activityData,
      timestamp: new Date().toISOString()
    });

    console.log('✅ Activity logged successfully');

    return {
      success: true,
      data: response.data
    };

  } catch (error) {
    console.error('=== ACTIVITY LOG ERROR ===');
    console.error('Error:', error.response?.data || error.message);
    
    return {
      success: false,
      message: error.response?.data?.error || error.response?.data?.message || error.message || 'Failed to log activity',
      data: null
    };
  }
};

// ==========================================
// EXPORTS
// ==========================================

export {
  getDashboardData,
  getQuickStats,
  refreshDashboardData,
  isProfileComplete,
  getUserWelcomeName,
  getProfileCompletionStatus,
  uploadProfileImage,
  logActivity,
  getVolunteerDashboardData,
  getOrganizationDashboardData
};

export default {
  getDashboardData,
  getQuickStats,
  refreshDashboardData,
  isProfileComplete,
  getUserWelcomeName,
  getProfileCompletionStatus,
  uploadProfileImage,
  logActivity
};