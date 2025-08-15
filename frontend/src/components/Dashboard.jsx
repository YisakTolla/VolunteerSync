import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getCurrentUser, logout, isLoggedIn } from '../services/authService';
import {
  getDashboardData,
  refreshDashboardData,
  isProfileComplete,
  getUserWelcomeName
} from '../services/dashboardService';
import './Dashboard.css';

const Dashboard = () => {
  const [user, setUser] = useState(null);
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [refreshing, setRefreshing] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (!isLoggedIn()) {
      navigate('/login');
      return;
    }
    loadDashboardData();
  }, [navigate]);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      setError('');

      const localUser = getCurrentUser();
      if (localUser) {
        setUser(localUser);
      }

      const result = await getDashboardData();
      if (result.success) {
        setDashboardData(result.data);
        setUser(result.data.user);
        setError('');
      } else {
        setError(result.message || 'Failed to load dashboard data');
      }
    } catch (err) {
      setError('Failed to load dashboard. Please try refreshing.');
      console.error('❌ Error loading dashboard data:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleRefresh = async () => {
    try {
      setRefreshing(true);
      setError('');

      const result = await refreshDashboardData();
      if (result.success) {
        setDashboardData(result.data);
        setUser(result.data.user);
        setError('');
      } else {
        setError(result.message || 'Failed to refresh dashboard data');
      }
    } catch (err) {
      setError('Failed to refresh dashboard');
      console.error('❌ Error refreshing dashboard:', err);
    } finally {
      setRefreshing(false);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const handleEditProfile = () => {
    if (user?.userType === 'VOLUNTEER') {
      navigate('/profile');
    } else if (user?.userType === 'ORGANIZATION') {
      navigate('/profile');
    } else {
      navigate('/profile');
    }
  };

  const handleViewProfile = () => {
    if (user?.userType === 'VOLUNTEER') {
      navigate('/profile');
    } else if (user?.userType === 'ORGANIZATION') {
      navigate('/profile');
    } else {
      navigate('/profile');
    }
  };

  const getUserInitials = () => {
    if (!user) return 'U';

    if (user.userType === 'ORGANIZATION' && user.organizationName) {
      const words = user.organizationName.split(' ').filter(Boolean);
      if (words.length >= 2) {
        return `${words[0][0]}${words[1][0]}`.toUpperCase();
      } else {
        return words[0].substring(0, 2).toUpperCase();
      }
    }

    if (user.userType === 'VOLUNTEER' && user.firstName && user.lastName) {
      return `${user.firstName[0]}${user.lastName[0]}`.toUpperCase();
    }

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

  const getStatsOverview = () => {
    if (!dashboardData?.stats) return [];

    if (user?.userType === 'VOLUNTEER') {
      return [
        {
          icon: '⏰',
          title: 'Hours Completed',
          value: dashboardData.stats.hoursCompleted || 0,
          color: 'green'
        },
        {
          icon: '📅',
          title: 'Events Attended',
          value: dashboardData.stats.eventsAttended || 0,
          color: 'blue'
        },
        {
          icon: '🎯',
          title: 'Upcoming Events',
          value: dashboardData.stats.upcomingEvents || 0,
          color: 'purple'
        },
        {
          icon: '⭐',
          title: 'Rating',
          value: dashboardData.stats.rating ? `${dashboardData.stats.rating}/5` : 'N/A',
          color: 'yellow'
        }
      ];
    } else {
      return [
        {
          icon: '📊',
          title: 'Active Events',
          value: dashboardData.stats.activeEvents || 0,
          color: 'green'
        },
        {
          icon: '👥',
          title: 'Total Volunteers',
          value: dashboardData.stats.totalVolunteers || 0,
          color: 'blue'
        },
        {
          icon: '📋',
          title: 'Pending Applications',
          value: dashboardData.stats.pendingApplications || 0,
          color: 'orange'
        },
        {
          icon: '📈',
          title: 'Events This Month',
          value: dashboardData.stats.eventsThisMonth || 0,
          color: 'purple'
        }
      ];
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
          <button onClick={loadDashboardData} className="btn-primary" disabled={refreshing}>
            {refreshing ? 'Retrying...' : 'Try Again'}
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

  if (!isProfileComplete(user)) {
    return (
      <div className="dashboard-setup">
        <div className="setup-content">
          <div className="setup-icon">📝</div>
          <h2>Complete Your Profile</h2>
          <p>
            Welcome! To get started, please complete your {user.userType.toLowerCase()} profile.
          </p>
          <div className="setup-actions">
            <button onClick={() => navigate('/profile/setup')} className="btn-primary">
              Complete Profile Setup
            </button>
            <button onClick={handleLogout} className="btn-secondary">
              Sign Out
            </button>
          </div>
        </div>
      </div>
    );
  }

  const statsOverview = getStatsOverview();

  return (
    <div className="dashboard-wrapper">
      <div className="dashboard">
        <div className="dashboard-header">
          <div className="header-content">
            <div className="user-welcome">
              <div className="user-avatar-large">
                {user.profileImageUrl || user.profilePicture ? (
                  <img src={user.profileImageUrl || user.profilePicture} alt="Profile" />
                ) : (
                  <div className="avatar-placeholder-large">
                    {getUserInitials()}
                  </div>
                )}
              </div>
              <div className="user-info">
                <h1>Welcome back, {getUserWelcomeName(user)}! 👋</h1>
                <p className="user-type">{getUserTypeDisplay()} Account</p>
                <p className="user-email">{user.email}</p>
              </div>
            </div>

            <div className="header-actions">
              <button onClick={handleEditProfile} className="btn-secondary">
                Edit Profile
              </button>
              <button onClick={handleViewProfile} className="btn-secondary">
                View Profile
              </button>
              <button onClick={handleLogout} className="btn-danger">
                Sign Out
              </button>
            </div>
          </div>

          {error && (
            <div className="header-error">
              <span>⚠️ {error}</span>
              <button onClick={handleRefresh} className="refresh-btn" disabled={refreshing}>
                {refreshing ? 'Refreshing...' : 'Refresh'}
              </button>
            </div>
          )}
        </div>

        <div className="dashboard-content">
          {/* Stats Overview */}
          <div className="stats-overview">
            {statsOverview.map((stat, index) => (
              <div key={index} className="stat-card">
                <div className="stat-icon">{stat.icon}</div>
                <div className="stat-info">
                  <h3>{stat.value}</h3>
                  <p>{stat.title}</p>
                </div>
              </div>
            ))}
          </div>

          {/* Dashboard Content Based on User Type */}
          {user.userType === 'VOLUNTEER' ? (
            <VolunteerDashboard
              dashboardData={dashboardData}
              onRefresh={handleRefresh}
              refreshing={refreshing}
              onNavigate={navigate}
            />
          ) : (
            <OrganizationDashboard
              dashboardData={dashboardData}
              onRefresh={handleRefresh}
              refreshing={refreshing}
              onNavigate={navigate}
            />
          )}
        </div>
      </div>
    </div>
  );
};

const VolunteerDashboard = ({ dashboardData, onRefresh, refreshing, onNavigate }) => (
  <div className="dashboard-grid">
    {/* Upcoming Events Card */}
    <div className="dashboard-card">
      <div className="card-header">
        <h3>📅 Upcoming Events</h3>
        <p>Your scheduled volunteer activities</p>
      </div>
      <div className="card-content">
        {dashboardData?.events?.length > 0 ? (
          <div className="events-list">
            {dashboardData.events.slice(0, 3).map((event, idx) => (
              <div key={idx} className="event-item">
                <div className="event-info">
                  <h4>{event.title || event.name}</h4>
                  <p>{event.date || event.eventDate}</p>
                  <p>{event.location}</p>
                </div>
                <div className={`event-status ${event.status?.toLowerCase()}`}>
                  {event.status || 'Active'}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p>No upcoming events. <button onClick={() => onNavigate('/find-events')} className="btn-primary">Find Events</button></p>
        )}
      </div>
      <div className="card-actions">
        <button onClick={() => onNavigate('/volunteer/events')} className="btn-secondary">
          View All Events
        </button>
      </div>
    </div>

    {/* Recent Activity Card */}
    <div className="dashboard-card">
      <div className="card-header">
        <h3>📈 Recent Activity</h3>
        <p>Your volunteer history</p>
      </div>
      <div className="card-content">
        {dashboardData?.recentActivity?.length > 0 ? (
          <div className="activity-list">
            {dashboardData.recentActivity.slice(0, 3).map((activity, idx) => (
              <div key={idx} className="activity-item">
                <p>{activity.description}</p>
                <span>{activity.date}</span>
              </div>
            ))}
          </div>
        ) : (
          <p>No recent activity to display.</p>
        )}
      </div>
    </div>

    {/* Applications Card */}
    <div className="dashboard-card">
      <div className="card-header">
        <h3>📝 My Applications</h3>
        <p>Track your volunteer applications</p>
      </div>
      <div className="card-content">
        {dashboardData?.applications?.length > 0 ? (
          <div className="applications-list">
            {dashboardData.applications.slice(0, 3).map((app, idx) => (
              <div key={idx} className="application-item">
                <div className="application-info">
                  <h5>{app.eventTitle || app.event?.title}</h5>
                  <p>{app.submittedDate || app.createdAt}</p>
                </div>
                <div className={`application-status ${app.status?.toLowerCase()}`}>
                  {app.status}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p>No applications yet. <button onClick={() => onNavigate('/find-events')} className="btn-primary">Apply to Events</button></p>
        )}
      </div>
      <div className="card-actions">
        <button onClick={() => onNavigate('/volunteer/applications')} className="btn-secondary">
          View All Applications
        </button>
      </div>
    </div>

    {/* Quick Actions Card */}
    <div className="dashboard-card featured">
      <div className="card-header">
        <h3>🚀 Quick Actions</h3>
        <p>Get started with volunteer activities</p>
      </div>
      <div className="card-content">
        <div className="quick-actions-grid">
          <button onClick={() => onNavigate('/find-events')} className="btn-primary">
            Find Events
          </button>
          <button onClick={() => onNavigate('/find-organizations')} className="btn-secondary">
            Find Organizations
          </button>
          <button onClick={() => onNavigate('/profile')} className="btn-secondary">
            Update Profile
          </button>
        </div>
      </div>
    </div>
  </div>
);

const OrganizationDashboard = ({ dashboardData, onRefresh, refreshing, onNavigate }) => (
  <div className="dashboard-grid">
    {/* Posted Events Card */}
    <div className="dashboard-card">
      <div className="card-header">
        <h3>📊 Posted Events</h3>
        <p>Your organization's volunteer opportunities</p>
      </div>
      <div className="card-content">
        {dashboardData?.events?.length > 0 ? (
          <div className="events-list">
            {dashboardData.events.slice(0, 3).map((event, idx) => (
              <div key={idx} className="event-item">
                <div className="event-info">
                  <h4>{event.title}</h4>
                  <p>{event.eventDate}</p>
                  <p>{event.location}</p>
                </div>
                <div className={`event-status ${event.status?.toLowerCase()}`}>
                  {event.status}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p>No events posted yet. <button onClick={() => onNavigate('/organization/events/create')} className="btn-primary">Create Event</button></p>
        )}
      </div>
      <div className="card-actions">
        <button onClick={() => onNavigate('/organization/events')} className="btn-secondary">
          Manage Events
        </button>
        <button onClick={() => onNavigate('/organization/events/create')} className="btn-primary">
          Create Event
        </button>
      </div>
    </div>

    {/* Applications Card */}
    <div className="dashboard-card">
      <div className="card-header">
        <h3>📋 Recent Applications</h3>
        <p>Volunteer applications to review</p>
      </div>
      <div className="card-content">
        {dashboardData?.applications?.length > 0 ? (
          <div className="applications-list">
            {dashboardData.applications.slice(0, 3).map((app, idx) => (
              <div key={idx} className="application-item">
                <div className="application-info">
                  <h5>{app.volunteerName}</h5>
                  <p>{app.eventTitle}</p>
                  <span>{app.submittedDate}</span>
                </div>
                <div className={`application-status ${app.status?.toLowerCase()}`}>
                  {app.status}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p>No recent applications.</p>
        )}
      </div>
      <div className="card-actions">
        <button onClick={() => onNavigate('/organization/applications')} className="btn-secondary">
          Review Applications
        </button>
      </div>
    </div>

    {/* Volunteers Card */}
    <div className="dashboard-card">
      <div className="card-header">
        <h3>👥 Your Volunteers</h3>
        <p>Active volunteer community</p>
      </div>
      <div className="card-content">
        {dashboardData?.volunteers?.length > 0 ? (
          <div className="volunteers-list">
            {dashboardData.volunteers.slice(0, 3).map((volunteer, idx) => (
              <div key={idx} className="volunteer-item">
                <div className="volunteer-info">
                  <h5>{volunteer.firstName} {volunteer.lastName}</h5>
                  <p>{volunteer.email}</p>
                  <span>{volunteer.totalHours || 0} hours</span>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p>No volunteers yet. Share your events to attract volunteers!</p>
        )}
      </div>
      <div className="card-actions">
        <button onClick={() => onNavigate('/organization/volunteers')} className="btn-secondary">
          Manage Volunteers
        </button>
      </div>
    </div>

    {/* Quick Actions Card */}
    <div className="dashboard-card featured">
      <div className="card-header">
        <h3>🚀 Quick Actions</h3>
        <p>Manage your organization efficiently</p>
      </div>
      <div className="card-content">
        <div className="quick-actions-grid">
          <button onClick={() => onNavigate('/organization/events/create')} className="btn-primary">
            Create Event
          </button>
          <button onClick={() => onNavigate('/organization/profile')} className="btn-secondary">
            Update Profile
          </button>
          <button onClick={() => onNavigate('/organization/analytics')} className="btn-secondary">
            View Analytics
          </button>
        </div>
      </div>
    </div>
  </div>
);

export default Dashboard;