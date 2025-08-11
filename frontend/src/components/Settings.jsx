import React, { useState, useEffect } from 'react';
import { 
  Settings, 
  User, 
  Shield, 
  Bell, 
  Globe, 
  Eye, 
  Trash2, 
  Save,
  Camera,
  Mail,
  Phone,
  MapPin,
  Link,
  Lock,
  Download,
  AlertTriangle
} from 'lucide-react';
import './Settings.css';

const SettingsPage = () => {
  const [activeSection, setActiveSection] = useState('profile');
  const [profileData, setProfileData] = useState({
    firstName: 'Yisak',
    lastName: 'Tolla',
    displayName: 'Yisak Tolla',
    email: 'ytolla@gmu.edu',
    phone: '+1 (555) 123-4567',
    location: 'Fairfax, Virginia',
    website: 'https://yisaktolla.dev',
    bio: 'Passionate computer science student at George Mason University with a strong interest in volunteer work and community service. I love connecting with like-minded individuals and making a positive impact through technology and direct action.'
  });

  const [notificationSettings, setNotificationSettings] = useState({
    emailNotifications: true,
    pushNotifications: true,
    eventReminders: true,
    organizationUpdates: true,
    connectionRequests: true,
    weeklyDigest: false,
    marketingEmails: false
  });

  const [privacySettings, setPrivacySettings] = useState({
    profileVisibility: 'public',
    showEmail: false,
    showPhone: false,
    showLocation: true,
    allowMessaging: true,
    showActivity: true,
    searchable: true
  });

  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  });

  const [hasUnsavedChanges, setHasUnsavedChanges] = useState(false);

  const sections = [
    { id: 'profile', label: 'Profile Information', icon: User },
    { id: 'account', label: 'Account & Security', icon: Shield },
    { id: 'notifications', label: 'Notifications', icon: Bell },
    { id: 'privacy', label: 'Privacy Settings', icon: Eye },
    { id: 'data', label: 'Data & Privacy', icon: Download }
  ];

  const handleProfileChange = (field, value) => {
    setProfileData(prev => ({ ...prev, [field]: value }));
    setHasUnsavedChanges(true);
  };

  const handleNotificationChange = (setting) => {
    setNotificationSettings(prev => ({ ...prev, [setting]: !prev[setting] }));
    setHasUnsavedChanges(true);
  };

  const handlePrivacyChange = (setting, value) => {
    setPrivacySettings(prev => ({ ...prev, [setting]: value }));
    setHasUnsavedChanges(true);
  };

  const handleSaveChanges = () => {
    // Here you would make API calls to save the changes
    console.log('Saving changes...');
    setHasUnsavedChanges(false);
  };

  const renderProfileSection = () => (
    <div className="settings-section">
      <div className="section-header">
        <h2 className="section-title">Profile Information</h2>
        <p className="section-description">
          Update your personal information and profile details
        </p>
      </div>

      <div className="settings-form">
        {/* Profile Picture */}
        <div className="form-group profile-picture-group">
          <label className="form-label">Profile Picture</label>
          <div className="profile-picture-container">
            <div className="profile-picture-current">
              <div className="profile-avatar">
                <span>YT</span>
              </div>
            </div>
            <div className="profile-picture-actions">
              <button className="btn-secondary">
                <Camera className="w-4 h-4" />
                Change Photo
              </button>
              <button className="btn-text">Remove</button>
            </div>
          </div>
        </div>

        {/* Name Fields */}
        <div className="form-row">
          <div className="form-group">
            <label className="form-label">First Name</label>
            <input
              type="text"
              className="form-input"
              value={profileData.firstName}
              onChange={(e) => handleProfileChange('firstName', e.target.value)}
            />
          </div>
          <div className="form-group">
            <label className="form-label">Last Name</label>
            <input
              type="text"
              className="form-input"
              value={profileData.lastName}
              onChange={(e) => handleProfileChange('lastName', e.target.value)}
            />
          </div>
        </div>

        {/* Display Name */}
        <div className="form-group">
          <label className="form-label">Display Name</label>
          <input
            type="text"
            className="form-input"
            value={profileData.displayName}
            onChange={(e) => handleProfileChange('displayName', e.target.value)}
          />
          <p className="form-help">This is how your name will appear to other users</p>
        </div>

        {/* Contact Information */}
        <div className="form-group">
          <label className="form-label">
            <Mail className="w-4 h-4" />
            Email Address
          </label>
          <input
            type="email"
            className="form-input"
            value={profileData.email}
            onChange={(e) => handleProfileChange('email', e.target.value)}
          />
        </div>

        <div className="form-group">
          <label className="form-label">
            <Phone className="w-4 h-4" />
            Phone Number
          </label>
          <input
            type="tel"
            className="form-input"
            value={profileData.phone}
            onChange={(e) => handleProfileChange('phone', e.target.value)}
          />
        </div>

        <div className="form-group">
          <label className="form-label">
            <MapPin className="w-4 h-4" />
            Location
          </label>
          <input
            type="text"
            className="form-input"
            value={profileData.location}
            onChange={(e) => handleProfileChange('location', e.target.value)}
          />
        </div>

        <div className="form-group">
          <label className="form-label">
            <Link className="w-4 h-4" />
            Website
          </label>
          <input
            type="url"
            className="form-input"
            value={profileData.website}
            onChange={(e) => handleProfileChange('website', e.target.value)}
          />
        </div>

        {/* Bio */}
        <div className="form-group">
          <label className="form-label">About Me</label>
          <textarea
            className="form-textarea"
            rows="4"
            value={profileData.bio}
            onChange={(e) => handleProfileChange('bio', e.target.value)}
            placeholder="Tell others about yourself, your interests, and what motivates you to volunteer..."
          />
          <p className="form-help">{profileData.bio.length}/500 characters</p>
        </div>
      </div>
    </div>
  );

  const renderAccountSection = () => (
    <div className="settings-section">
      <div className="section-header">
        <h2 className="section-title">Account & Security</h2>
        <p className="section-description">
          Manage your login credentials and account security
        </p>
      </div>

      <div className="settings-form">
        {/* Change Password */}
        <div className="form-group">
          <label className="form-label">Current Password</label>
          <input
            type="password"
            className="form-input"
            value={passwordData.currentPassword}
            onChange={(e) => setPasswordData(prev => ({ ...prev, currentPassword: e.target.value }))}
          />
        </div>

        <div className="form-group">
          <label className="form-label">New Password</label>
          <input
            type="password"
            className="form-input"
            value={passwordData.newPassword}
            onChange={(e) => setPasswordData(prev => ({ ...prev, newPassword: e.target.value }))}
          />
        </div>

        <div className="form-group">
          <label className="form-label">Confirm New Password</label>
          <input
            type="password"
            className="form-input"
            value={passwordData.confirmPassword}
            onChange={(e) => setPasswordData(prev => ({ ...prev, confirmPassword: e.target.value }))}
          />
        </div>

        <button className="btn-secondary">
          <Lock className="w-4 h-4" />
          Update Password
        </button>

        {/* Two-Factor Authentication */}
        <div className="security-section">
          <h3 className="subsection-title">Two-Factor Authentication</h3>
          <div className="security-item">
            <div className="security-item-info">
              <h4>Authenticator App</h4>
              <p>Use an authenticator app for enhanced security</p>
            </div>
            <button className="btn-secondary">Enable</button>
          </div>
        </div>
      </div>
    </div>
  );

  const renderNotificationsSection = () => (
    <div className="settings-section">
      <div className="section-header">
        <h2 className="section-title">Notification Preferences</h2>
        <p className="section-description">
          Choose how and when you want to receive notifications
        </p>
      </div>

      <div className="settings-form">
        <div className="notification-groups">
          <div className="notification-group">
            <h3 className="subsection-title">General Notifications</h3>
            
            <div className="toggle-item">
              <div className="toggle-info">
                <h4>Email Notifications</h4>
                <p>Receive notifications via email</p>
              </div>
              <label className="toggle-switch">
                <input
                  type="checkbox"
                  checked={notificationSettings.emailNotifications}
                  onChange={() => handleNotificationChange('emailNotifications')}
                />
                <span className="toggle-slider"></span>
              </label>
            </div>

            <div className="toggle-item">
              <div className="toggle-info">
                <h4>Push Notifications</h4>
                <p>Receive push notifications in your browser</p>
              </div>
              <label className="toggle-switch">
                <input
                  type="checkbox"
                  checked={notificationSettings.pushNotifications}
                  onChange={() => handleNotificationChange('pushNotifications')}
                />
                <span className="toggle-slider"></span>
              </label>
            </div>
          </div>

          <div className="notification-group">
            <h3 className="subsection-title">Activity Notifications</h3>
            
            <div className="toggle-item">
              <div className="toggle-info">
                <h4>Event Reminders</h4>
                <p>Get reminded about upcoming volunteer events</p>
              </div>
              <label className="toggle-switch">
                <input
                  type="checkbox"
                  checked={notificationSettings.eventReminders}
                  onChange={() => handleNotificationChange('eventReminders')}
                />
                <span className="toggle-slider"></span>
              </label>
            </div>

            <div className="toggle-item">
              <div className="toggle-info">
                <h4>Organization Updates</h4>
                <p>Receive updates from organizations you follow</p>
              </div>
              <label className="toggle-switch">
                <input
                  type="checkbox"
                  checked={notificationSettings.organizationUpdates}
                  onChange={() => handleNotificationChange('organizationUpdates')}
                />
                <span className="toggle-slider"></span>
              </label>
            </div>

            <div className="toggle-item">
              <div className="toggle-info">
                <h4>Connection Requests</h4>
                <p>Get notified when someone wants to connect</p>
              </div>
              <label className="toggle-switch">
                <input
                  type="checkbox"
                  checked={notificationSettings.connectionRequests}
                  onChange={() => handleNotificationChange('connectionRequests')}
                />
                <span className="toggle-slider"></span>
              </label>
            </div>
          </div>

          <div className="notification-group">
            <h3 className="subsection-title">Digest & Marketing</h3>
            
            <div className="toggle-item">
              <div className="toggle-info">
                <h4>Weekly Digest</h4>
                <p>Weekly summary of platform activity</p>
              </div>
              <label className="toggle-switch">
                <input
                  type="checkbox"
                  checked={notificationSettings.weeklyDigest}
                  onChange={() => handleNotificationChange('weeklyDigest')}
                />
                <span className="toggle-slider"></span>
              </label>
            </div>

            <div className="toggle-item">
              <div className="toggle-info">
                <h4>Marketing Emails</h4>
                <p>Receive updates about new features and opportunities</p>
              </div>
              <label className="toggle-switch">
                <input
                  type="checkbox"
                  checked={notificationSettings.marketingEmails}
                  onChange={() => handleNotificationChange('marketingEmails')}
                />
                <span className="toggle-slider"></span>
              </label>
            </div>
          </div>
        </div>
      </div>
    </div>
  );

  const renderPrivacySection = () => (
    <div className="settings-section">
      <div className="section-header">
        <h2 className="section-title">Privacy Settings</h2>
        <p className="section-description">
          Control who can see your information and how you appear to others
        </p>
      </div>

      <div className="settings-form">
        <div className="privacy-groups">
          <div className="privacy-group">
            <h3 className="subsection-title">Profile Visibility</h3>
            
            <div className="radio-group">
              <label className="radio-item">
                <input
                  type="radio"
                  name="profileVisibility"
                  value="public"
                  checked={privacySettings.profileVisibility === 'public'}
                  onChange={(e) => handlePrivacyChange('profileVisibility', e.target.value)}
                />
                <div className="radio-info">
                  <h4>Public</h4>
                  <p>Anyone can view your profile</p>
                </div>
              </label>

              <label className="radio-item">
                <input
                  type="radio"
                  name="profileVisibility"
                  value="connections"
                  checked={privacySettings.profileVisibility === 'connections'}
                  onChange={(e) => handlePrivacyChange('profileVisibility', e.target.value)}
                />
                <div className="radio-info">
                  <h4>Connections Only</h4>
                  <p>Only your connections can view your full profile</p>
                </div>
              </label>

              <label className="radio-item">
                <input
                  type="radio"
                  name="profileVisibility"
                  value="private"
                  checked={privacySettings.profileVisibility === 'private'}
                  onChange={(e) => handlePrivacyChange('profileVisibility', e.target.value)}
                />
                <div className="radio-info">
                  <h4>Private</h4>
                  <p>Only you can view your profile</p>
                </div>
              </label>
            </div>
          </div>

          <div className="privacy-group">
            <h3 className="subsection-title">Contact Information</h3>
            
            <div className="toggle-item">
              <div className="toggle-info">
                <h4>Show Email Address</h4>
                <p>Allow others to see your email address</p>
              </div>
              <label className="toggle-switch">
                <input
                  type="checkbox"
                  checked={privacySettings.showEmail}
                  onChange={() => handlePrivacyChange('showEmail', !privacySettings.showEmail)}
                />
                <span className="toggle-slider"></span>
              </label>
            </div>

            <div className="toggle-item">
              <div className="toggle-info">
                <h4>Show Phone Number</h4>
                <p>Allow others to see your phone number</p>
              </div>
              <label className="toggle-switch">
                <input
                  type="checkbox"
                  checked={privacySettings.showPhone}
                  onChange={() => handlePrivacyChange('showPhone', !privacySettings.showPhone)}
                />
                <span className="toggle-slider"></span>
              </label>
            </div>

            <div className="toggle-item">
              <div className="toggle-info">
                <h4>Show Location</h4>
                <p>Allow others to see your location</p>
              </div>
              <label className="toggle-switch">
                <input
                  type="checkbox"
                  checked={privacySettings.showLocation}
                  onChange={() => handlePrivacyChange('showLocation', !privacySettings.showLocation)}
                />
                <span className="toggle-slider"></span>
              </label>
            </div>
          </div>

          <div className="privacy-group">
            <h3 className="subsection-title">Platform Interactions</h3>
            
            <div className="toggle-item">
              <div className="toggle-info">
                <h4>Allow Direct Messages</h4>
                <p>Let other users send you direct messages</p>
              </div>
              <label className="toggle-switch">
                <input
                  type="checkbox"
                  checked={privacySettings.allowMessaging}
                  onChange={() => handlePrivacyChange('allowMessaging', !privacySettings.allowMessaging)}
                />
                <span className="toggle-slider"></span>
              </label>
            </div>

            <div className="toggle-item">
              <div className="toggle-info">
                <h4>Show Activity Status</h4>
                <p>Let others see when you're active on the platform</p>
              </div>
              <label className="toggle-switch">
                <input
                  type="checkbox"
                  checked={privacySettings.showActivity}
                  onChange={() => handlePrivacyChange('showActivity', !privacySettings.showActivity)}
                />
                <span className="toggle-slider"></span>
              </label>
            </div>

            <div className="toggle-item">
              <div className="toggle-info">
                <h4>Appear in Search Results</h4>
                <p>Allow your profile to appear in search results</p>
              </div>
              <label className="toggle-switch">
                <input
                  type="checkbox"
                  checked={privacySettings.searchable}
                  onChange={() => handlePrivacyChange('searchable', !privacySettings.searchable)}
                />
                <span className="toggle-slider"></span>
              </label>
            </div>
          </div>
        </div>
      </div>
    </div>
  );

  const renderDataSection = () => (
    <div className="settings-section">
      <div className="section-header">
        <h2 className="section-title">Data & Privacy</h2>
        <p className="section-description">
          Manage your data, download your information, or delete your account
        </p>
      </div>

      <div className="settings-form">
        <div className="data-actions">
          <div className="action-card">
            <div className="action-header">
              <Download className="w-6 h-6 text-blue-600" />
              <div>
                <h3>Download Your Data</h3>
                <p>Get a copy of all your data including profile, activities, and connections</p>
              </div>
            </div>
            <button className="btn-secondary">Request Download</button>
          </div>

          <div className="action-card danger">
            <div className="action-header">
              <AlertTriangle className="w-6 h-6 text-red-600" />
              <div>
                <h3>Delete Account</h3>
                <p>Permanently delete your account and all associated data. This action cannot be undone.</p>
              </div>
            </div>
            <button className="btn-danger">Delete Account</button>
          </div>
        </div>
      </div>
    </div>
  );

  const renderSection = () => {
    switch (activeSection) {
      case 'profile':
        return renderProfileSection();
      case 'account':
        return renderAccountSection();
      case 'notifications':
        return renderNotificationsSection();
      case 'privacy':
        return renderPrivacySection();
      case 'data':
        return renderDataSection();
      default:
        return renderProfileSection();
    }
  };

  return (
    <div className="settings-page">
      <div className="settings-container">
        {/* Left Sidebar - Navigation */}
        <div className="settings-sidebar">
          <div className="sidebar-header">
            <h1 className="sidebar-title">
              <Settings className="w-6 h-6" />
              Settings
            </h1>
          </div>
          
          <nav className="settings-nav">
            {sections.map((section) => {
              const IconComponent = section.icon;
              return (
                <button
                  key={section.id}
                  className={`nav-item ${activeSection === section.id ? 'active' : ''}`}
                  onClick={() => setActiveSection(section.id)}
                >
                  <IconComponent className="w-5 h-5" />
                  {section.label}
                </button>
              );
            })}
          </nav>
        </div>

        {/* Main Content */}
        <div className="settings-main">
          {renderSection()}

          {/* Save Button - Fixed at bottom */}
          {hasUnsavedChanges && (
            <div className="save-bar">
              <div className="save-bar-content">
                <span className="save-bar-text">You have unsaved changes</span>
                <div className="save-bar-actions">
                  <button 
                    className="btn-text"
                    onClick={() => setHasUnsavedChanges(false)}
                  >
                    Discard
                  </button>
                  <button 
                    className="btn-primary"
                    onClick={handleSaveChanges}
                  >
                    <Save className="w-4 h-4" />
                    Save Changes
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default SettingsPage;