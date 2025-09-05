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
    setRefreshing(true);
    try {
      const result = await refreshDashboardData();
      if (result.success) {
        setDashboardData(result.data);
        setError('');
      } else {
        setError(result.message || 'Failed to refresh dashboard data');
      }
    } catch (err) {
      setError('Failed to refresh dashboard data');
    } finally {
      setRefreshing(false);
    }
  };

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const handleViewProfile = () => {
    navigate('/profile');
  };

  const getUserInitials = () => {
    if (user.firstName && user.lastName) {
      return `${user.firstName.charAt(0)}${user.lastName.charAt(0)}`;
    } else if (user.organizationName) {
      return user.organizationName.charAt(0);
    } else if (user.email) {
      return user.email.charAt(0);
    }
    return '?';
  };

  const getUserTypeDisplay = () => {
    if (user.userType === 'VOLUNTEER') return 'Volunteer';
    if (user.userType === 'ORGANIZATION') return 'Organization';
    return 'User';
  };

  const getStatsOverview = () => {
    if (!dashboardData || !dashboardData.stats) return [];
    
    if (user.userType === 'VOLUNTEER') {
      return [
        {
          title: 'Active Applications',
          value: dashboardData.stats.activeApplications || 0,
          icon: '📋'
        },
        {
          title: 'Upcoming Events',
          value: dashboardData.stats.upcomingEvents || 0,
          icon: '📅'
        },
        {
          title: 'Hours Volunteered',
          value: dashboardData.stats.hoursVolunteered || 0,
          icon: '⏰'
        }
      ];
    } else {
      return [
        {
          title: 'Active Events',
          value: dashboardData.stats.activeEvents || 0,
          icon: '📊'
        },
        {
          title: 'Total Volunteers',
          value: dashboardData.stats.totalVolunteers || 0,
          icon: '👥'
        },
        {
          title: 'Pending Applications',
          value: dashboardData.stats.pendingApplications || 0,
          icon: '📋'
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
    <div className="dashboard-simple">
      {/* Simple Header */}
      <div className="dashboard-header-simple">
        <div className="header-content-simple">
          <div className="user-section">
            <div className="user-avatar-simple">
              {user.profileImageUrl || user.profilePicture ? (
                <img src={user.profileImageUrl || user.profilePicture} alt="Profile" />
              ) : (
                <div className="avatar-placeholder-simple">
                  {getUserInitials()}
                </div>
              )}
            </div>
            <div>
              <h1>Welcome back, {getUserWelcomeName(user)}!</h1>
              <p className="user-role">{getUserTypeDisplay()}</p>
            </div>
          </div>
          
          <div className="header-actions-simple">
            <button onClick={handleViewProfile} className="btn-outline">
              Profile
            </button>
            <button onClick={handleLogout} className="btn-outline">
              Sign Out
            </button>
          </div>
        </div>
      </div>

      {/* Simple Stats */}
      <div className="dashboard-content-simple">
        <div className="stats-simple">
          {statsOverview.map((stat, index) => (
            <div key={index} className="stat-item">
              <span className="stat-icon">{stat.icon}</span>
              <div>
                <h3>{stat.value}</h3>
                <p>{stat.title}</p>
              </div>
            </div>
          ))}
        </div>

        {/* Content based on user type */}
        {user.userType === 'VOLUNTEER' ? (
          <SimpleVolunteerContent 
            dashboardData={dashboardData} 
            navigate={navigate}
          />
        ) : (
          <SimpleOrganizationContent 
            dashboardData={dashboardData} 
            navigate={navigate}
          />
        )}
      </div>
    </div>
  );
};

const SimpleVolunteerContent = ({ dashboardData, navigate }) => (
  <>
    {/* Find Events Section */}
    <div className="content-section">
      <div className="section-header">
        <h2>Find & Apply to Events</h2>
        <p>Discover volunteer opportunities and manage your applications</p>
      </div>
      
      <div className="main-actions">
        <div className="main-action-card primary" onClick={() => navigate('/find-events')}>
          <div className="action-icon">🔍</div>
          <div className="action-content">
            <h3>Find Events</h3>
            <p>Browse available volunteer opportunities</p>
          </div>
        </div>
        
        <div className="main-action-card" onClick={() => navigate('/volunteer/applications')}>
          <div className="action-icon">📄</div>
          <div className="action-content">
            <h3>My Applications</h3>
            <p>View and manage your event applications</p>
          </div>
        </div>
      </div>
    </div>

    {/* Manage Profile Section */}
    <div className="content-section">
      <div className="section-header">
        <h2>Manage Your Profile</h2>
        <p>Keep your volunteer profile updated and connect with organizations</p>
      </div>
      
      <div className="main-actions">
        <div className="main-action-card" onClick={() => navigate('/profile')}>
          <div className="action-icon">👤</div>
          <div className="action-content">
            <h3>Update Profile</h3>
            <p>Edit your skills, availability, and preferences</p>
          </div>
        </div>
        
        <div className="main-action-card" onClick={() => navigate('/find-organizations')}>
          <div className="action-icon">🏢</div>
          <div className="action-content">
            <h3>Find Organizations</h3>
            <p>Discover organizations you'd like to volunteer with</p>
          </div>
        </div>
      </div>
    </div>
  </>
);

const SimpleOrganizationContent = ({ dashboardData, navigate }) => (
  <>
    {/* Create/Manage Events Section */}
    <div className="content-section">
      <div className="section-header">
        <h2>Create & Manage Events</h2>
        <p>Post volunteer opportunities and manage your organization's events</p>
      </div>
      
      <div className="main-actions">
        <div className="main-action-card primary" onClick={() => navigate('/create-events')}>
          <div className="action-icon">➕</div>
          <div className="action-content">
            <h3>Create New Event</h3>
            <p>Post a new volunteer opportunity for your organization</p>
            <div className="action-meta">
              <span className="meta-item">📊 {dashboardData?.stats?.activeEvents || 0} active events</span>
            </div>
          </div>
        </div>
        
        <div className="main-action-card" onClick={() => navigate('/organization/events')}>
          <div className="action-icon">📋</div>
          <div className="action-content">
            <h3>Manage Events</h3>
            <p>Edit, update, and monitor your existing events</p>
            <div className="action-meta">
              <span className="meta-item">📈 {dashboardData?.stats?.eventsThisMonth || 0} events this month</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    {/* Review Applications / Manage Volunteers Section */}
    <div className="content-section">
      <div className="section-header">
        <h2>Review Applications & Manage Volunteers</h2>
        <p>Review volunteer applications and manage your volunteer community</p>
      </div>
      
      <div className="main-actions">
        <div className="main-action-card primary" onClick={() => navigate('/organization/applications')}>
          <div className="action-icon">👥</div>
          <div className="action-content">
            <h3>Review Applications</h3>
            <p>Review and approve volunteer applications for your events</p>
            <div className="action-meta">
              <span className="meta-item">📋 {dashboardData?.stats?.pendingApplications || 0} pending applications</span>
            </div>
          </div>
        </div>
        
        <div className="main-action-card" onClick={() => navigate('/organization/volunteers')}>
          <div className="action-icon">🤝</div>
          <div className="action-content">
            <h3>Manage Volunteers</h3>
            <p>View and communicate with your volunteer community</p>
            <div className="action-meta">
              <span className="meta-item">👥 {dashboardData?.stats?.totalVolunteers || 0} total volunteers</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </>
);

export default Dashboard;