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
      navigate('/volunteer/profile/edit');
    } else if (user?.userType === 'ORGANIZATION') {
      navigate('/organization/profile/edit');
    } else {
      navigate('/profile/setup');
    }
  };

  const handleViewProfile = () => {
    if (user?.userType === 'VOLUNTEER') {
      navigate('/volunteer/profile');
    } else if (user?.userType === 'ORGANIZATION') {
      navigate('/organization/profile');
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
          {user.userType === 'VOLUNTEER' ? (
            <VolunteerDashboard
              dashboardData={dashboardData}
              onRefresh={handleRefresh}
              refreshing={refreshing}
            />
          ) : (
            <OrganizationDashboard
              dashboardData={dashboardData}
              onRefresh={handleRefresh}
              refreshing={refreshing}
            />
          )}
        </div>
      </div>
    </div>
  );
};

const VolunteerDashboard = ({ dashboardData }) => (
  <div className="volunteer-dashboard">
    <h2>Your Upcoming Events</h2>
    {dashboardData?.upcomingEvents?.length > 0 ? (
      <div className="event-list">
        {dashboardData.upcomingEvents.map((event, idx) => (
          <div key={idx} className="event-card">
            <h3>{event.title}</h3>
            <p>{event.date}</p>
          </div>
        ))}
      </div>
    ) : (
      <p>No upcoming events.</p>
    )}

    <h2>Recent Activity</h2>
    {dashboardData?.recentActivity?.length > 0 ? (
      <div className="activity-list">
        {dashboardData.recentActivity.map((activity, idx) => (
          <div key={idx} className="activity-card">
            <p>{activity.description}</p>
            <span>{activity.date}</span>
          </div>
        ))}
      </div>
    ) : (
      <p>No recent activity.</p>
    )}
  </div>
);

const OrganizationDashboard = ({ dashboardData }) => (
  <div className="organization-dashboard">
    <h2>Posted Opportunities</h2>
    {dashboardData?.opportunities?.length > 0 ? (
      <div className="opportunity-list">
        {dashboardData.opportunities.map((opp, idx) => (
          <div key={idx} className="opportunity-card">
            <h3>{opp.title}</h3>
            <p>{opp.date}</p>
          </div>
        ))}
      </div>
    ) : (
      <p>No opportunities posted yet.</p>
    )}

    <h2>Recent Applications</h2>
    {dashboardData?.recentApplications?.length > 0 ? (
      <div className="application-list">
        {dashboardData.recentApplications.map((app, idx) => (
          <div key={idx} className="application-card">
            <p>{app.volunteerName}</p>
            <span>{app.date}</span>
          </div>
        ))}
      </div>
    ) : (
      <p>No recent applications.</p>
    )}
  </div>
);

export default Dashboard;
