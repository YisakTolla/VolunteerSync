import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getCurrentUser, logout, getUserProfile, isLoggedIn } from '../services/authService';
import './Dashboard.css';

const Dashboard = () => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    if (!isLoggedIn()) {
      navigate('/login');
      return;
    }
    loadUserData();
  }, [navigate]);

  const loadUserData = async () => {
    try {
      // Get user from localStorage first for immediate display
      const localUser = getCurrentUser();
      if (localUser) {
        setUser(localUser);
      }

      // Then fetch latest data from server
      const result = await getUserProfile();
      if (result.success) {
        // Handle different response structures
        const userData = result.data.data || result.data.user || result.data;
        setUser(userData);
        setError('');
      } else {
        setError('Failed to load user profile. Please try refreshing.');
        console.error('Failed to load user profile:', result.message);
      }
    } catch (err) {
      setError('Failed to load user data. Please try refreshing.');
      console.error('Error loading user data:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const handleEditProfile = () => {
    // TODO: Navigate to profile edit page
    console.log('Edit profile clicked');
  };

  const handleViewProfile = () => {
    // TODO: Navigate to full profile view
    console.log('View profile clicked');
  };

  // Helper functions for user display
  const getUserDisplayName = () => {
    if (!user) return 'User';
    
    // For organizations, show organization name
    if (user.userType === 'ORGANIZATION' && user.organizationName) {
      return user.organizationName;
    }
    
    // For volunteers, show first + last name
    if (user.userType === 'VOLUNTEER' && user.firstName && user.lastName) {
      return `${user.firstName} ${user.lastName}`;
    }
    
    // Fallback to email
    return user.email || 'User';
  };

  const getUserWelcomeName = () => {
    if (!user) return 'User';
    
    // For organizations, show organization name
    if (user.userType === 'ORGANIZATION' && user.organizationName) {
      return user.organizationName;
    }
    
    // For volunteers, show first name only
    if (user.userType === 'VOLUNTEER' && user.firstName) {
      return user.firstName;
    }
    
    // Fallback to email prefix
    return user.email?.split('@')[0] || 'User';
  };

  const getUserInitials = () => {
    if (!user) return 'U';
    
    // For organizations, use organization name initials
    if (user.userType === 'ORGANIZATION' && user.organizationName) {
      const words = user.organizationName.split(' ').filter(word => word.length > 0);
      if (words.length >= 2) {
        return `${words[0][0]}${words[1][0]}`.toUpperCase();
      } else if (words.length === 1) {
        return words[0].substring(0, 2).toUpperCase();
      }
    }
    
    // For volunteers, use first + last name initials
    if (user.userType === 'VOLUNTEER' && user.firstName && user.lastName) {
      return `${user.firstName[0]}${user.lastName[0]}`.toUpperCase();
    }
    
    // Fallback to email initial
    if (user.email) {
      return user.email[0].toUpperCase();
    }
    
    return 'U';
  };

  const getUserTypeDisplay = () => {
    if (!user) return '';
    
    switch (user.userType) {
      case 'ORGANIZATION':
        return '🏢 Organization';
      case 'VOLUNTEER':
        return '🙋‍♀️ Volunteer';
      default:
        return user.userType;
    }
  };

  if (loading) {
    return (
      <div className="dashboard-loading">
        <div className="loading-spinner"></div>
        <p>Loading your dashboard...</p>
      </div>
    );
  }

  if (error && !user) {
    return (
      <div className="dashboard-error">
        <div className="error-icon">⚠️</div>
        <h3>Oops! Something went wrong</h3>
        <p>{error}</p>
        <div className="error-actions">
          <button onClick={loadUserData} className="btn-primary">
            Try Again
          </button>
          <button onClick={handleLogout} className="btn-secondary">
            Sign Out
          </button>
        </div>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="dashboard-error">
        <div className="error-icon">👤</div>
        <h3>User data not found</h3>
        <p>Please sign in again to continue.</p>
        <button onClick={handleLogout} className="btn-primary">
          Go to Login
        </button>
      </div>
    );
  }

  return (
    <div className="dashboard-wrapper">
      <div className="dashboard">
        {/* Dashboard Header */}
        <div className="dashboard-header">
          <div className="header-content">
            <div className="user-welcome">
              <div className="user-avatar-large">
                {user.profilePicture ? (
                  <img src={user.profilePicture} alt="Profile" />
                ) : (
                  <div className="avatar-placeholder-large">
                    {getUserInitials()}
                  </div>
                )}
              </div>
              <div className="user-info">
                <h1>Welcome back, {getUserWelcomeName()}! 👋</h1>
                <p className="user-type">
                  {getUserTypeDisplay()} Account
                </p>
                <p className="user-email">{user.email}</p>
              </div>
            </div>
            
            <div className="header-actions">
              <button onClick={handleLogout} className="btn-danger">
                Sign Out
              </button>
            </div>
          </div>
          
          {error && (
            <div className="header-error">
              <span>⚠️ {error}</span>
              <button onClick={loadUserData} className="refresh-btn">
                Refresh
              </button>
            </div>
          )}
        </div>

        {/* Dashboard Content */}
        <div className="dashboard-content">
          {user.userType === 'VOLUNTEER' ? (
            <VolunteerDashboard user={user} />
          ) : (
            <OrganizationDashboard user={user} />
          )}
        </div>
      </div>
    </div>
  );
};

// Volunteer-specific dashboard content
const VolunteerDashboard = ({ user }) => {
  const [stats, setStats] = useState({
    hoursCompleted: user.volunteer_hours_completed || 0,
    eventsAttended: 0,
    upcomingEvents: 0,
    connections: 0
  });

  return (
    <div className="volunteer-dashboard">
      {/* Quick Stats */}
      <div className="stats-overview">
        <div className="stat-card">
          <div className="stat-icon">⏰</div>
          <div className="stat-info">
            <h3>{stats.hoursCompleted}</h3>
            <p>Hours Volunteered</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">🎯</div>
          <div className="stat-info">
            <h3>{stats.eventsAttended}</h3>
            <p>Events Attended</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">📅</div>
          <div className="stat-info">
            <h3>{stats.upcomingEvents}</h3>
            <p>Upcoming Events</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">🤝</div>
          <div className="stat-info">
            <h3>{stats.connections}</h3>
            <p>Connections</p>
          </div>
        </div>
      </div>

      {/* Main Dashboard Grid */}
      <div className="dashboard-grid">
        {/* Find Opportunities */}
        <div className="dashboard-card featured">
          <div className="card-header">
            <h3>🔍 Find Opportunities</h3>
            <p>Discover volunteer opportunities that match your interests</p>
          </div>
          <div className="card-content">
            <div className="opportunity-categories">
              <div className="category">🌱 Environment</div>
              <div className="category">📚 Education</div>
              <div className="category">🏥 Healthcare</div>
              <div className="category">🍽️ Food & Hunger</div>
            </div>
          </div>
          <div className="card-actions">
            <button className="btn-primary">Browse All Events</button>
            <button className="btn-secondary">Set Preferences</button>
          </div>
        </div>

        {/* My Applications */}
        <div className="dashboard-card">
          <div className="card-header">
            <h3>📋 My Applications</h3>
            <p>Track your volunteer applications</p>
          </div>
          <div className="card-content">
            <div className="application-list">
              <div className="empty-state">
                <div className="empty-icon">📋</div>
                <p>No recent applications</p>
                <small>Apply to events to see them here</small>
              </div>
            </div>
          </div>
          <div className="card-actions">
            <button className="btn-primary">View All Applications</button>
          </div>
        </div>

        {/* Upcoming Events */}
        <div className="dashboard-card">
          <div className="card-header">
            <h3>📅 Upcoming Events</h3>
            <p>Your confirmed volunteer activities</p>
          </div>
          <div className="card-content">
            <div className="event-list">
              <div className="empty-state">
                <div className="empty-icon">📅</div>
                <p>No upcoming events yet</p>
                <small>Apply to events to see them here</small>
              </div>
            </div>
          </div>
          <div className="card-actions">
            <button className="btn-primary">Find Events</button>
          </div>
        </div>

        {/* Impact Summary */}
        <div className="dashboard-card">
          <div className="card-header">
            <h3>⭐ My Impact</h3>
            <p>See the difference you're making</p>
          </div>
          <div className="card-content">
            <div className="impact-stats">
              <div className="impact-item">
                <span className="impact-number">{stats.hoursCompleted}</span>
                <span className="impact-label">Total Hours</span>
              </div>
              <div className="impact-item">
                <span className="impact-number">{user.rating || 0}</span>
                <span className="impact-label">Rating</span>
              </div>
            </div>
            <div className="achievements">
              <h4>Achievements</h4>
              <div className="achievement-badges">
                <div className="badge">🏆 First Event</div>
                {stats.hoursCompleted >= 10 && <div className="badge">⭐ 10 Hours</div>}
                {stats.hoursCompleted >= 50 && <div className="badge">🎯 50 Hours</div>}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

// Organization-specific dashboard content
const OrganizationDashboard = ({ user }) => {
  const [stats, setStats] = useState({
    activeEvents: 0,
    totalVolunteers: 0,
    pendingApplications: 0,
    eventsThisMonth: 0
  });

  // Get organization display name for messaging
  const getOrgDisplayName = () => {
    return user.organizationName || user.email?.split('@')[0] || 'Organization';
  };

  return (
    <div className="organization-dashboard">
      {/* Quick Stats */}
      <div className="stats-overview">
        <div className="stat-card">
          <div className="stat-icon">📅</div>
          <div className="stat-info">
            <h3>{stats.activeEvents}</h3>
            <p>Active Events</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">👥</div>
          <div className="stat-info">
            <h3>{stats.totalVolunteers}</h3>
            <p>Total Volunteers</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">📋</div>
          <div className="stat-info">
            <h3>{stats.pendingApplications}</h3>
            <p>Pending Applications</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">📊</div>
          <div className="stat-info">
            <h3>{stats.eventsThisMonth}</h3>
            <p>Events This Month</p>
          </div>
        </div>
      </div>

      {/* Main Dashboard Grid */}
      <div className="dashboard-grid">
        {/* Create Event */}
        <div className="dashboard-card">
          <div className="card-header">
            <h3>➕ Create New Event</h3>
            <p>Post volunteer opportunities for {getOrgDisplayName()}</p>
          </div>
          <div className="card-content">
            <div className="create-event-preview">
              <div className="event-types">
                <div className="event-type">🌱 Environmental</div>
                <div className="event-type">📚 Educational</div>
                <div className="event-type">🍽️ Community Service</div>
                <div className="event-type">🎯 Fundraising</div>
              </div>
            </div>
          </div>
          <div className="card-actions">
            <button className="btn-primary">Create Event</button>
            <button className="btn-secondary">View Templates</button>
          </div>
        </div>

        {/* Manage Applications */}
        <div className="dashboard-card">
          <div className="card-header">
            <h3>📋 Manage Applications</h3>
            <p>Review and respond to volunteer applications</p>
          </div>
          <div className="card-content">
            <div className="application-summary">
              <div className="empty-state">
                <div className="empty-icon">📋</div>
                <p>No pending applications</p>
                <small>Applications will appear here when volunteers apply to your events</small>
              </div>
            </div>
          </div>
          <div className="card-actions">
            <button className="btn-primary">View All Applications</button>
          </div>
        </div>

        {/* Event Analytics */}
        <div className="dashboard-card">
          <div className="card-header">
            <h3>📊 Event Analytics</h3>
            <p>Track your events' performance and impact</p>
          </div>
          <div className="card-content">
            <div className="analytics-preview">
              <div className="metric">
                <span className="metric-value">0</span>
                <span className="metric-label">Total Event Views</span>
              </div>
              <div className="metric">
                <span className="metric-value">0</span>
                <span className="metric-label">Applications Received</span>
              </div>
              <div className="metric">
                <span className="metric-value">0</span>
                <span className="metric-label">Events Created</span>
              </div>
            </div>
          </div>
          <div className="card-actions">
            <button className="btn-primary">View Detailed Analytics</button>
          </div>
        </div>

        {/* Organization Events */}
        <div className="dashboard-card">
          <div className="card-header">
            <h3>📅 {getOrgDisplayName()} Events</h3>
            <p>Manage your posted volunteer opportunities</p>
          </div>
          <div className="card-content">
            <div className="events-list">
              <div className="empty-state">
                <div className="empty-icon">📅</div>
                <p>No events created yet</p>
                <small>Create your first event to start attracting volunteers</small>
              </div>
            </div>
          </div>
          <div className="card-actions">
            <button className="btn-primary">Create First Event</button>
          </div>
        </div>

        {/* Organization Profile */}
        <div className="dashboard-card">
          <div className="card-header">
            <h3>🏢 Organization Profile</h3>
            <p>Manage your organization's public profile</p>
          </div>
          <div className="card-content">
            <div className="org-profile-preview">
              <div className="org-info">
                <h4>{user.organizationName || 'Organization Name'}</h4>
                <p className="org-description">
                  {user.description || 'Add a description to tell volunteers about your organization and mission.'}
                </p>
              </div>
              <div className="profile-stats">
                <div className="profile-stat">
                  <span className="stat-number">0</span>
                  <span className="stat-label">Profile Views</span>
                </div>
                <div className="profile-stat">
                  <span className="stat-number">0</span>
                  <span className="stat-label">Followers</span>
                </div>
              </div>
            </div>
          </div>
          <div className="card-actions">
            <button className="btn-primary">Edit Profile</button>
            <button className="btn-secondary">View Public Profile</button>
          </div>
        </div>

        {/* Volunteer Management */}
        <div className="dashboard-card">
          <div className="card-header">
            <h3>👥 Volunteer Management</h3>
            <p>Manage relationships with your volunteers</p>
          </div>
          <div className="card-content">
            <div className="volunteer-summary">
              <div className="volunteer-metrics">
                <div className="volunteer-metric">
                  <span className="metric-value">{stats.totalVolunteers}</span>
                  <span className="metric-label">Active Volunteers</span>
                </div>
                <div className="volunteer-metric">
                  <span className="metric-value">0</span>
                  <span className="metric-label">Regular Volunteers</span>
                </div>
              </div>
              {stats.totalVolunteers === 0 && (
                <div className="empty-state">
                  <div className="empty-icon">👥</div>
                  <p>No volunteers yet</p>
                  <small>Create events to start building your volunteer community</small>
                </div>
              )}
            </div>
          </div>
          <div className="card-actions">
            <button className="btn-primary">View All Volunteers</button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;