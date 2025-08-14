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
  AlertTriangle,
  Loader,
  CheckCircle,
  XCircle
} from 'lucide-react';
import { getCurrentUser, getUserDisplayName, getUserInitials, updateCurrentUser } from '../services/authService';
import {
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
} from '../services/settingsService';
import { uploadProfileImage } from '../services/profileService';
import './Settings.css';

const SettingsPage = () => {
  const [activeSection, setActiveSection] = useState('profile');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [user, setUser] = useState(null);
  
  // Profile data state
  const [profileData, setProfileData] = useState({
    firstName: '',
    lastName: '',
    displayName: '',
    email: '',
    phone: '',
    location: '',
    website: '',
    bio: ''
  });

  // Notification settings state
  const [notificationSettings, setNotificationSettings] = useState(getDefaultNotificationSettings());

  // Privacy settings state
  const [privacySettings, setPrivacySettings] = useState(getDefaultPrivacySettings());

  // Password change state
  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  });

  // Active sessions state
  const [activeSessions, setActiveSessions] = useState([]);

  const [hasUnsavedChanges, setHasUnsavedChanges] = useState(false);

  const sections = [
    { id: 'profile', label: 'Profile Information', icon: User },
    { id: 'account', label: 'Account & Security', icon: Shield },
    { id: 'notifications', label: 'Notifications', icon: Bell },
    { id: 'privacy', label: 'Privacy Settings', icon: Eye },
    { id: 'data', label: 'Data & Privacy', icon: Download }
  ];

  // Load initial data
  useEffect(() => {
    loadSettingsData();
  }, []);

  const loadSettingsData = async () => {
    try {
      setLoading(true);
      setError('');

      const currentUser = getCurrentUser();
      if (!currentUser) {
        setError('Please log in to access settings');
        return;
      }

      setUser(currentUser);

      // Load profile data
      const profileResult = await fetchUserSettings();
      if (profileResult.success) {
        const data = profileResult.data;
        setProfileData({
          firstName: data.firstName || '',
          lastName: data.lastName || '',
          displayName: data.displayName || data.organizationName || getUserDisplayName(currentUser),
          email: data.email || currentUser.email || '',
          phone: data.phoneNumber || data.phone || '',
          location: data.location || '',
          website: data.website || '',
          bio: data.bio || data.description || ''
        });
      }

      // Load notification settings
      const notificationResult = await fetchNotificationSettings();
      if (notificationResult.success) {
        setNotificationSettings({ ...getDefaultNotificationSettings(), ...notificationResult.data });
      }

      // Load privacy settings
      const privacyResult = await fetchPrivacySettings();
      if (privacyResult.success) {
        setPrivacySettings({ ...getDefaultPrivacySettings(), ...privacyResult.data });
      }

      // Load active sessions
      const sessionsResult = await fetchActiveSessions();
      if (sessionsResult.success) {
        setActiveSessions(sessionsResult.data || []);
      }

    } catch (err) {
      console.error('Error loading settings:', err);
      setError('Failed to load settings. Please try refreshing the page.');
    } finally {
      setLoading(false);
    }
  };

  const handleProfileChange = (field, value) => {
    setProfileData(prev => ({ ...prev, [field]: value }));
    setHasUnsavedChanges(true);
    setError('');
  };

  const handleNotificationChange = (setting) => {
    setNotificationSettings(prev => ({ ...prev, [setting]: !prev[setting] }));
    setHasUnsavedChanges(true);
  };

  const handlePrivacyChange = (setting, value) => {
    setPrivacySettings(prev => ({ ...prev, [setting]: value }));
    setHasUnsavedChanges(true);
  };

  const handleSaveChanges = async () => {
    try {
      setSaving(true);
      setError('');
      setSuccess('');

      const promises = [];

      // Save profile data if changed
      if (hasUnsavedChanges) {
        promises.push(updateProfileSettings(profileData));
        promises.push(updateNotificationSettings(notificationSettings));
        promises.push(updatePrivacySettings(privacySettings));
      }

      const results = await Promise.all(promises);
      const failures = results.filter(result => !result.success);

      if (failures.length > 0) {
        setError(failures[0].message || 'Some settings could not be saved');
      } else {
        setSuccess('Settings saved successfully!');
        setHasUnsavedChanges(false);
        
        // Update local user data if profile was updated
        if (results[0] && results[0].success) {
          updateCurrentUser(profileData);
        }
        
        setTimeout(() => setSuccess(''), 3000);
      }

    } catch (err) {
      console.error('Error saving settings:', err);
      setError('Failed to save settings. Please try again.');
    } finally {
      setSaving(false);
    }
  };

  const handlePasswordChange = async () => {
    try {
      if (!passwordData.currentPassword || !passwordData.newPassword || !passwordData.confirmPassword) {
        setError('Please fill in all password fields');
        return;
      }

      if (passwordData.newPassword !== passwordData.confirmPassword) {
        setError('New passwords do not match');
        return;
      }

      if (passwordData.newPassword.length < 8) {
        setError('Password must be at least 8 characters long');
        return;
      }

      setSaving(true);
      setError('');
      setSuccess('');

      const result = await changePassword(passwordData);

      if (result.success) {
        setSuccess('Password changed successfully!');
        setPasswordData({
          currentPassword: '',
          newPassword: '',
          confirmPassword: ''
        });
        setTimeout(() => setSuccess(''), 3000);
      } else {
        setError(result.message || 'Failed to change password');
      }

    } catch (err) {
      console.error('Error changing password:', err);
      setError('Failed to change password. Please try again.');
    } finally {
      setSaving(false);
    }
  };

  const handleImageUpload = async (file) => {
    try {
      setSaving(true);
      setError('');

      // Validate file
      if (!file.type.startsWith('image/')) {
        setError('Please select a valid image file');
        return;
      }

      if (file.size > 5 * 1024 * 1024) {
        setError('Image size must be less than 5MB');
        return;
      }

      const result = await uploadProfileImage(file, 'profile');

      if (result.success) {
        setSuccess('Profile picture updated successfully!');
        // Update local user data
        updateCurrentUser({ profileImageUrl: result.imageUrl });
        // Reload user data to show new image
        const updatedUser = getCurrentUser();
        setUser(updatedUser);
        setTimeout(() => setSuccess(''), 3000);
      } else {
        setError(result.message || 'Failed to upload image');
      }

    } catch (err) {
      console.error('Error uploading image:', err);
      setError('Failed to upload image. Please try again.');
    } finally {
      setSaving(false);
    }
  };

  const handleDataExport = async () => {
    try {
      setSaving(true);
      setError('');

      const result = await requestDataExport();

      if (result.success) {
        setSuccess('Data export requested. You will receive an email when ready.');
        setTimeout(() => setSuccess(''), 5000);
      } else {
        setError(result.message || 'Failed to request data export');
      }

    } catch (err) {
      console.error('Error requesting data export:', err);
      setError('Failed to request data export. Please try again.');
    } finally {
      setSaving(false);
    }
  };

  const handleAccountDeletion = async () => {
    const password = prompt('Please enter your password to confirm account deletion:');
    if (!password) return;

    const confirmed = window.confirm('Are you sure you want to delete your account? This action cannot be undone.');
    if (!confirmed) return;

    try {
      setSaving(true);
      setError('');

      const result = await deleteAccount(password);

      if (result.success) {
        alert('Account deleted successfully. You will be redirected to the homepage.');
        window.location.href = '/';
      } else {
        setError(result.message || 'Failed to delete account');
      }

    } catch (err) {
      console.error('Error deleting account:', err);
      setError('Failed to delete account. Please try again.');
    } finally {
      setSaving(false);
    }
  };

  const handleSessionTermination = async (sessionId) => {
    try {
      const result = await terminateSession(sessionId);

      if (result.success) {
        setSuccess('Session terminated successfully');
        // Reload sessions
        const sessionsResult = await fetchActiveSessions();
        if (sessionsResult.success) {
          setActiveSessions(sessionsResult.data || []);
        }
        setTimeout(() => setSuccess(''), 3000);
      } else {
        setError(result.message || 'Failed to terminate session');
      }

    } catch (err) {
      console.error('Error terminating session:', err);
      setError('Failed to terminate session. Please try again.');
    }
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
        <div className="form-group">
          <label className="form-label">Profile Picture</label>
          <div className="profile-picture-container">
            <div className="profile-avatar">
              {user?.profileImageUrl ? (
                <img src={user.profileImageUrl} alt="Profile" style={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: '50%' }} />
              ) : (
                <span>{getUserInitials(user)}</span>
              )}
            </div>
            <div className="profile-picture-actions">
              <input
                type="file"
                accept="image/*"
                onChange={(e) => {
                  const file = e.target.files[0];
                  if (file) handleImageUpload(file);
                }}
                style={{ display: 'none' }}
                id="profile-picture-upload"
              />
              <label htmlFor="profile-picture-upload" className="btn-secondary">
                <Camera className="w-4 h-4" />
                Change Photo
              </label>
            </div>
          </div>
        </div>

        {/* Name Fields */}
        {user?.userType === 'VOLUNTEER' && (
          <>
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
          </>
        )}

        <div className="form-group">
          <label className="form-label">
            {user?.userType === 'ORGANIZATION' ? 'Organization Name' : 'Display Name'}
          </label>
          <input
            type="text"
            className="form-input"
            value={profileData.displayName}
            onChange={(e) => handleProfileChange('displayName', e.target.value)}
          />
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
            placeholder="City, State"
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
            maxLength={500}
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

        <button 
          className="btn-secondary"
          onClick={handlePasswordChange}
          disabled={saving || !passwordData.currentPassword || !passwordData.newPassword || !passwordData.confirmPassword}
        >
          <Lock className="w-4 h-4" />
          {saving ? 'Updating...' : 'Update Password'}
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

        {/* Active Sessions */}
        <div className="security-section">
          <h3 className="subsection-title">Active Sessions</h3>
          <div className="sessions-list">
            {activeSessions.length > 0 ? (
              activeSessions.map((session) => (
                <div key={session.id} className="session-item">
                  <div className="session-info">
                    <h4>{session.deviceName || 'Unknown Device'}</h4>
                    <p>{session.location || 'Unknown Location'} • {session.browser || 'Unknown Browser'}</p>
                    <span className={`session-status ${session.current ? 'active' : ''}`}>
                      {session.current ? 'Current Session' : `Last active: ${session.lastActive}`}
                    </span>
                  </div>
                  {!session.current && (
                    <button 
                      className="btn-secondary"
                      onClick={() => handleSessionTermination(session.id)}
                    >
                      Terminate
                    </button>
                  )}
                </div>
              ))
            ) : (
              <p className="no-data">No active sessions found.</p>
            )}
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
            <h3 className="subsection-title">Email Preferences</h3>
            
            <div className="toggle-item">
              <div className="toggle-info">
                <h4>Weekly Digest</h4>
                <p>Receive a weekly summary of your activity</p>
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
                <p>Receive promotional content and feature updates</p>
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
                <p>Display your email address on your profile</p>
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
                <p>Display your phone number on your profile</p>
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
                <p>Display your location on your profile</p>
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
            <h3 className="subsection-title">Communication</h3>
            
            <div className="toggle-item">
              <div className="toggle-info">
                <h4>Allow Direct Messages</h4>
                <p>Let others send you direct messages</p>
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
            <button 
              className="btn-secondary"
              onClick={handleDataExport}
              disabled={saving}
            >
              {saving ? 'Requesting...' : 'Request Download'}
            </button>
          </div>

          <div className="action-card danger">
            <div className="action-header">
              <AlertTriangle className="w-6 h-6 text-red-600" />
              <div>
                <h3>Delete Account</h3>
                <p>Permanently delete your account and all associated data. This action cannot be undone.</p>
              </div>
            </div>
            <button 
              className="btn-danger"
              onClick={handleAccountDeletion}
              disabled={saving}
            >
              {saving ? 'Deleting...' : 'Delete Account'}
            </button>
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

  // Loading state
  if (loading) {
    return (
      <div className="settings-page">
        <div className="settings-container">
          <div className="settings-section">
            <div className="section-header">
              <Loader className="w-6 h-6" />
              <p>Loading settings...</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="settings-page">
      <div className="settings-container">
        {/* Error/Success Messages */}
        {error && (
          <div className="settings-section" style={{ 
            marginBottom: 'var(--spacing-4)', 
            background: '#fef2f2', 
            borderColor: '#dc3545',
            padding: 'var(--spacing-4)'
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--spacing-2)' }}>
              <XCircle className="w-5 h-5" style={{ color: '#dc3545' }} />
              <span style={{ color: '#dc3545' }}>{error}</span>
              <button 
                onClick={() => setError('')} 
                style={{ 
                  marginLeft: 'auto', 
                  background: 'none', 
                  border: 'none', 
                  color: '#dc3545', 
                  cursor: 'pointer',
                  fontSize: '1.2rem'
                }}
              >
                ×
              </button>
            </div>
          </div>
        )}

        {success && (
          <div className="settings-section" style={{ 
            marginBottom: 'var(--spacing-4)', 
            background: '#f0f9ff', 
            borderColor: 'var(--accent-green)',
            padding: 'var(--spacing-4)'
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--spacing-2)' }}>
              <CheckCircle className="w-5 h-5" style={{ color: 'var(--accent-green)' }} />
              <span style={{ color: 'var(--accent-green)' }}>{success}</span>
              <button 
                onClick={() => setSuccess('')} 
                style={{ 
                  marginLeft: 'auto', 
                  background: 'none', 
                  border: 'none', 
                  color: 'var(--accent-green)', 
                  cursor: 'pointer',
                  fontSize: '1.2rem'
                }}
              >
                ×
              </button>
            </div>
          </div>
        )}

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
                    onClick={() => {
                      setHasUnsavedChanges(false);
                      loadSettingsData(); // Reload original data
                    }}
                    disabled={saving}
                  >
                    Discard
                  </button>
                  <button 
                    className="btn-primary"
                    onClick={handleSaveChanges}
                    disabled={saving}
                  >
                    {saving ? (
                      <Loader className="w-4 h-4" />
                    ) : (
                      <Save className="w-4 h-4" />
                    )}
                    {saving ? 'Saving...' : 'Save Changes'}
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