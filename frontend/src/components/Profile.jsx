import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  Edit,
  MapPin,
  Calendar,
  Users,
  Heart,
  Award,
  Clock,
  Mail,
  Phone,
  Globe,
  Star,
  Plus,
  Settings,
  Camera,
  UserPlus,
  MessageCircle,
  Building,
  Target,
  DollarSign,
  UserCheck,
  TrendingUp,
  FileText,
  Link,
  Loader,
  AlertCircle,
} from "lucide-react";
import {
  getProfileData,
  updateProfileData,
  uploadProfileImage,
  addInterest,
  addSkill,
} from "../services/profilePageService";
import { getCurrentUser, isLoggedIn } from "../services/authService";
import "./Profile.css";

const Profile = () => {
  const [userData, setUserData] = useState(null);
  const [userType, setUserType] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [isEditing, setIsEditing] = useState(false);
  const [activeTab, setActiveTab] = useState("overview");
  const [uploading, setUploading] = useState(false);
  const navigate = useNavigate();
  const { userId } = useParams();

  useEffect(() => {
    if (!isLoggedIn()) {
      navigate("/login");
      return;
    }
    loadProfileData();
  }, [navigate, userId]);

  const loadProfileData = async () => {
    try {
      setLoading(true);
      setError("");

      console.log("📊 Loading profile data...");
      const result = await getProfileData(userId);

      if (result.success) {
        console.log('=== PROFILE DEBUG INFO ===');
        console.log('Result userType:', result.userType);
        console.log('Result data:', result.data);
        console.log('Data type field:', result.data?.type);
        
        setUserData(result.data);
        setUserType(result.userType);
        setError("");
        console.log("✅ Profile data loaded successfully");
        console.log('Component userType set to:', result.userType);
        console.log('Component userData set to:', result.data);
      } else {
        setError(result.message || "Failed to load profile data");
        console.error("❌ Failed to load profile data:", result.message);
      }
    } catch (err) {
      setError("Failed to load profile. Please try refreshing.");
      console.error("❌ Error loading profile data:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleSettingsClick = () => {
    navigate("/settings");
  };

  const handleImageUpload = async (file, imageType) => {
    try {
      setUploading(true);
      const result = await uploadProfileImage(file, imageType);

      if (result.success) {
        // Reload profile data to get updated image URL
        await loadProfileData();
        console.log("✅ Image uploaded successfully");
      } else {
        setError(result.message || "Failed to upload image");
      }
    } catch (err) {
      setError("Failed to upload image");
      console.error("❌ Error uploading image:", err);
    } finally {
      setUploading(false);
    }
  };

  const handleFileInputChange = (event, imageType) => {
    const file = event.target.files[0];
    if (file) {
      handleImageUpload(file, imageType);
    }
  };

  // Show loading state
  if (loading) {
    return (
      <div className="profile-loading">
        <div className="loading-content">
          <Loader className="loading-spinner" />
          <p>Loading profile...</p>
        </div>
      </div>
    );
  }

  // Show error state
  if (error && !userData) {
    return (
      <div className="profile-error">
        <div className="error-content">
          <AlertCircle className="error-icon" />
          <h3>Unable to load profile</h3>
          <p>{error}</p>
          <div className="error-actions">
            <button onClick={loadProfileData} className="btn-primary">
              Try Again
            </button>
            <button
              onClick={() => navigate("/dashboard")}
              className="btn-secondary"
            >
              Go to Dashboard
            </button>
          </div>
        </div>
      </div>
    );
  }

  if (!userData) {
    return (
      <div className="profile-error">
        <div className="error-content">
          <AlertCircle className="error-icon" />
          <h3>Profile not found</h3>
          <p>The requested profile could not be found.</p>
          <button
            onClick={() => navigate("/dashboard")}
            className="btn-primary"
          >
            Go to Dashboard
          </button>
        </div>
      </div>
    );
  }

  const volunteerTabs = [
    { id: "overview", label: "Overview", icon: Users },
    { id: "organizations", label: "Organizations", icon: Heart },
  ];

  const organizationTabs = [
    { id: "overview", label: "Overview", icon: Building },
    { id: "activity", label: "Activity", icon: Clock },
    { id: "volunteers", label: "Volunteers", icon: Users },
  ];

  const tabs = userType === "volunteer" ? volunteerTabs : organizationTabs;

  console.log('=== TABS DEBUG ===');
  console.log('userType for tabs:', userType);
  console.log('Selected tabs:', tabs);
  console.log('Tab structure:', tabs.map(t => ({ id: t.id, label: t.label })));

  const renderVolunteerOverview = () => {
    console.log('🙋 EXECUTING renderVolunteerOverview()');
    console.log('userData in volunteer render:', userData);
    return (
      <div className="profile-overview">
        <div className="profile-overview-grid">
        {/* About Section */}
        <div className="profile-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">About</h3>
            <button
              className="profile-edit-btn"
              onClick={() => setIsEditing(!isEditing)}
            >
              <Edit />
            </button>
          </div>
          <div className="profile-card-content">
            <p className="profile-bio">{userData.bio}</p>
            <div className="profile-details">
              <div className="profile-detail">
                <MapPin className="profile-detail-icon" />
                <span>{userData.location}</span>
              </div>
              <div className="profile-detail">
                <Calendar className="profile-detail-icon" />
                <span>Joined {userData.joinDate}</span>
              </div>
              {userData.website && (
                <div className="profile-detail">
                  <Globe className="profile-detail-icon" />
                  <a
                    href={userData.website}
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    {userData.website}
                  </a>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Stats Section */}
        <div className="profile-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">Impact Stats</h3>
          </div>
          <div className="profile-card-content">
            <div className="profile-stats-grid">
              <div className="profile-stat">
                <div className="profile-stat-number">
                  {userData.stats.hoursVolunteered}
                </div>
                <div className="profile-stat-label">Hours Volunteered</div>
              </div>
              <div className="profile-stat">
                <div className="profile-stat-number">
                  {userData.stats.eventsAttended}
                </div>
                <div className="profile-stat-label">Events Attended</div>
              </div>
              <div className="profile-stat">
                <div className="profile-stat-number">
                  {userData.stats.organizations}
                </div>
                <div className="profile-stat-label">Organizations</div>
              </div>
            </div>
          </div>
        </div>

        {/* Interests Section */}
        <div className="profile-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">Interests</h3>
            <button className="profile-add-btn">
              <Plus />
            </button>
          </div>
          <div className="profile-card-content">
            <div className="profile-tags">
              {userData.interests && userData.interests.length > 0 ? (
                userData.interests.map((interest, index) => (
                  <span key={index} className="profile-tag interest">
                    {interest}
                  </span>
                ))
              ) : (
                <p className="no-data">
                  No interests added yet. Click the + button to add some!
                </p>
              )}
            </div>
          </div>
        </div>

        {/* Skills Section */}
        <div className="profile-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">Skills</h3>
            <button className="profile-add-btn">
              <Plus />
            </button>
          </div>
          <div className="profile-card-content">
            <div className="profile-tags">
              {userData.skills && userData.skills.length > 0 ? (
                userData.skills.map((skill, index) => (
                  <span key={index} className="profile-tag skill">
                    {skill}
                  </span>
                ))
              ) : (
                <p className="no-data">
                  No skills added yet. Click the + button to add some!
                </p>
              )}
            </div>
          </div>
        </div>

        {/* Badges Section */}
        <div className="profile-card profile-badges-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">Badges & Achievements</h3>
          </div>
          <div className="profile-card-content">
            <div className="profile-badges">
              {userData.badges && userData.badges.length > 0 ? (
                userData.badges.map((badge) => (
                  <div key={badge.id} className="profile-badge">
                    <div className="profile-badge-icon">{badge.icon}</div>
                    <div className="profile-badge-content">
                      <div className="profile-badge-name">{badge.name}</div>
                      <div className="profile-badge-description">
                        {badge.description}
                      </div>
                    </div>
                  </div>
                ))
              ) : (
                <p className="no-data">
                  No badges earned yet. Start volunteering to earn achievements!
                </p>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
  };

  const renderOrganizationOverview = () => {
    console.log('🏢 EXECUTING renderOrganizationOverview()');
    console.log('userData in organization render:', userData);
    console.log('userData.causes:', userData?.causes);
    console.log('userData.services:', userData?.services);
    console.log('userData.categories:', userData?.categories);
    return (
      <div className="profile-overview">
        <div className="profile-overview-grid">
        {/* About Section */}
        <div className="profile-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">About</h3>
            <button
              className="profile-edit-btn"
              onClick={() => setIsEditing(!isEditing)}
            >
              <Edit />
            </button>
          </div>
          <div className="profile-card-content">
            <p className="profile-bio">{userData.bio}</p>
            <div className="profile-details">
              <div className="profile-detail">
                <Building className="profile-detail-icon" />
                <span>{userData.organizationType}</span>
              </div>
              <div className="profile-detail">
                <MapPin className="profile-detail-icon" />
                <span>{userData.location}</span>
              </div>
              <div className="profile-detail">
                <Calendar className="profile-detail-icon" />
                <span>Founded {userData.foundedYear || userData.founded}</span>
              </div>
              <div className="profile-detail">
                <Users className="profile-detail-icon" />
                <span>{userData.employeeCount || 'Not specified'} employees</span>
              </div>
              {userData.isVerified && (
                <div className="profile-detail">
                  <UserCheck className="profile-detail-icon" />
                  <span>Verified Organization</span>
                </div>
              )}
              {userData.website && (
                <div className="profile-detail">
                  <Globe className="profile-detail-icon" />
                  <a
                    href={userData.website}
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    {userData.website}
                  </a>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Organization Stats */}
        <div className="profile-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">Organization Impact</h3>
          </div>
          <div className="profile-card-content">
            <div className="profile-stats-grid">
              <div className="profile-stat">
                <div className="profile-stat-number">
                  {userData.totalVolunteersServed || userData.stats?.volunteers || 0}
                </div>
                <div className="profile-stat-label">Volunteers Served</div>
              </div>
              <div className="profile-stat">
                <div className="profile-stat-number">
                  {userData.totalEventsHosted || userData.stats?.eventsHosted || 0}
                </div>
                <div className="profile-stat-label">Events Hosted</div>
              </div>
              <div className="profile-stat">
                <div className="profile-stat-number">
                  {userData.stats?.hoursImpacted || 0}
                </div>
                <div className="profile-stat-label">Volunteer Hours</div>
              </div>
              <div className="profile-stat">
                <div className="profile-stat-number">
                  {userData.foundedYear ? new Date().getFullYear() - userData.foundedYear : 'N/A'}
                </div>
                <div className="profile-stat-label">Years Active</div>
              </div>
            </div>
          </div>
        </div>

        {/* Funding Progress */}
        <div className="profile-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">Funding Progress</h3>
          </div>
          <div className="profile-card-content">
            <div className="profile-funding">
              <div className="profile-funding-amounts">
                <div className="profile-funding-raised">
                  <span className="profile-funding-number">
                    ${(userData.fundingRaised || userData.stats?.fundingRaised || 0).toLocaleString()}
                  </span>
                  <span className="profile-funding-label">Raised</span>
                </div>
                <div className="profile-funding-goal">
                  <span className="profile-funding-number">
                    ${(userData.fundingGoal || userData.stats?.fundingGoal || 0).toLocaleString()}
                  </span>
                  <span className="profile-funding-label">Goal</span>
                </div>
              </div>
              <div className="profile-funding-bar">
                <div
                  className="profile-funding-progress"
                  style={{
                    width: `${
                      userData.fundingGoal ? 
                        Math.min(((userData.fundingRaised || 0) / userData.fundingGoal) * 100, 100) : 
                        0
                    }%`,
                  }}
                ></div>
              </div>
            </div>
          </div>
        </div>

        {/* Causes Section */}
        <div className="profile-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">Causes We Support</h3>
            <button className="profile-add-btn">
              <Plus />
            </button>
          </div>
          <div className="profile-card-content">
            <div className="profile-tags">
              {userData.causes && userData.causes.length > 0 ? (
                (Array.isArray(userData.causes) ? userData.causes : userData.causes.split(',')).map((cause, index) => (
                  <span key={index} className="profile-tag interest">
                    {typeof cause === 'string' ? cause.trim() : cause}
                  </span>
                ))
              ) : (
                <p className="no-data">No causes added yet. Click the + button to add some!</p>
              )}
            </div>
          </div>
        </div>

        {/* Services Section */}
        <div className="profile-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">Services & Programs</h3>
            <button className="profile-add-btn">
              <Plus />
            </button>
          </div>
          <div className="profile-card-content">
            <div className="profile-tags">
              {userData.services && userData.services.length > 0 ? (
                (Array.isArray(userData.services) ? userData.services : userData.services.split(',')).map((service, index) => (
                  <span key={index} className="profile-tag skill">
                    {typeof service === 'string' ? service.trim() : service}
                  </span>
                ))
              ) : (
                <p className="no-data">No services added yet. Click the + button to add some!</p>
              )}
            </div>
          </div>
        </div>

        {/* Focus Areas Section */}
        <div className="profile-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">Focus Areas</h3>
            <button className="profile-add-btn">
              <Plus />
            </button>
          </div>
          <div className="profile-card-content">
            <div className="profile-tags">
              {userData.categories && userData.categories.length > 0 ? (
                (Array.isArray(userData.categories) ? userData.categories : userData.categories.split(',')).map((category, index) => (
                  <span key={index} className="profile-tag interest">
                    {typeof category === 'string' ? category.trim() : category}
                  </span>
                ))
              ) : (
                <p className="no-data">No focus areas added yet. Click the + button to add some!</p>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
    );
  };

  const renderActivity = () => (
    <div className="profile-activity">
      <div className="profile-card">
        <div className="profile-card-header">
          <h3 className="profile-card-title">Recent Activity</h3>
        </div>
        <div className="profile-card-content">
          <div className="profile-activity-list">
            {userData.recentActivity && userData.recentActivity.length > 0 ? (
              userData.recentActivity.map((activity) => (
                <div key={activity.id} className="profile-activity-item">
                  <div className="profile-activity-icon">
                    {activity.type === "event" && <Calendar />}
                    {activity.type === "volunteer" && <UserPlus />}
                    {activity.type === "achievement" && <Award />}
                  </div>
                  <div className="profile-activity-content">
                    <div className="profile-activity-title">
                      {activity.title}
                    </div>
                    {activity.volunteers && (
                      <div className="profile-activity-organization">
                        {activity.volunteers} volunteers participated
                      </div>
                    )}
                    <div className="profile-activity-date">{activity.date}</div>
                  </div>
                </div>
              ))
            ) : (
              <p className="no-data">No recent activity to show.</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );

  const renderVolunteersOrOrganizations = () => {
    const data =
      userType === "volunteer" ? userData.organizations : userData.volunteers;
    const title =
      userType === "volunteer" ? "My Organizations" : "Our Volunteers";

    return (
      <div className="profile-connections">
        <div className="profile-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">{title}</h3>
            <button className="profile-add-btn">
              <Plus />
            </button>
          </div>
          <div className="profile-card-content">
            <div className="profile-connections-grid">
              {data && data.length > 0 ? (
                data.map((item) => (
                  <div key={item.id} className="profile-connection-card">
                    <div className="profile-connection-avatar">
                      {item.avatar || item.logo}
                    </div>
                    <div className="profile-connection-content">
                      <div className="profile-connection-name">{item.name}</div>
                      <div className="profile-connection-role">{item.role}</div>
                      <div className="profile-connection-mutual">
                        {userType === "volunteer"
                          ? `Since ${item.since}`
                          : `${item.hoursContributed || 0} hours contributed`}
                      </div>
                    </div>
                    <div className="profile-connection-actions">
                      <button className="profile-connection-action">
                        <MessageCircle />
                      </button>
                    </div>
                  </div>
                ))
              ) : (
                <p className="no-data">
                  {userType === "volunteer"
                    ? "No organizations yet. Start volunteering to connect with organizations!"
                    : "No volunteers yet. Create events to attract volunteers!"}
                </p>
              )}
            </div>
          </div>
        </div>
      </div>
    );
  };

  return (
    <div className="profile-page">
      <div className="profile-container">
        {/* Error display */}
        {error && (
          <div className="profile-error-banner">
            <AlertCircle className="error-icon" />
            <span>{error}</span>
            <button onClick={() => setError("")} className="error-dismiss">
              ×
            </button>
          </div>
        )}

        {/* Header Section */}
        <div className="profile-header">
          <div className="profile-cover">
            <img
              src={userData.coverImage}
              alt="Cover"
              className="profile-cover-image"
            />
            <button className="profile-cover-edit">
              <input
                type="file"
                accept="image/*"
                onChange={(e) => handleFileInputChange(e, "cover")}
                style={{ display: "none" }}
                id="cover-upload"
              />
              <label htmlFor="cover-upload">
                <Camera />
              </label>
            </button>
          </div>

          <div className="profile-header-content">
            <div className="profile-header-info">
              <div className="profile-avatar-container">
                <img
                  src={userData.profileImage}
                  alt={userData.name}
                  className="profile-avatar"
                />
                <button className="profile-avatar-edit">
                  <input
                    type="file"
                    accept="image/*"
                    onChange={(e) => handleFileInputChange(e, "profile")}
                    style={{ display: "none" }}
                    id="avatar-upload"
                  />
                  <label htmlFor="avatar-upload">
                    <Camera />
                  </label>
                </button>
              </div>

              <div className="profile-header-details">
                <h1 className="profile-name">{userData.name}</h1>
                {userType === "organization" && (
                  <div className="profile-organization-type">
                    {userData.organizationType}
                  </div>
                )}
                <div className="profile-contact">
                  <div className="profile-contact-item">
                    <Mail className="profile-contact-icon" />
                    <span>{userData.email}</span>
                  </div>
                  <div className="profile-contact-item">
                    <Phone className="profile-contact-icon" />
                    <span>{userData.phone}</span>
                  </div>
                </div>
              </div>
            </div>

            <div className="profile-header-actions">
              {uploading && (
                <div className="upload-status">
                  <Loader className="upload-spinner" />
                  Uploading...
                </div>
              )}
              <button
                className="profile-action-btn secondary"
                onClick={handleSettingsClick}
              >
                <Settings />
                Settings
              </button>
            </div>
          </div>
        </div>

        {/* Navigation Tabs */}
        <div className="profile-nav">
          <div className="profile-nav-tabs">
            {tabs.map((tab) => (
              <button
                key={tab.id}
                className={`profile-nav-tab ${
                  activeTab === tab.id ? "active" : ""
                }`}
                onClick={() => setActiveTab(tab.id)}
              >
                <tab.icon className="profile-nav-tab-icon" />
                {tab.label}
              </button>
            ))}
          </div>
        </div>

        {/* Content */}
        <div className="profile-content">
          {(() => {
            console.log('=== RENDER DECISION DEBUG ===');
            console.log('activeTab:', activeTab);
            console.log('userType:', userType);
            console.log('userData?.type:', userData?.type);
            console.log('userData?.organizationType:', userData?.organizationType);
            console.log('userData?.name:', userData?.name);
            console.log('About to render with userType:', userType);
            
            if (activeTab === "overview") {
              // Force organization rendering if we detect organization data
              if (userType === "organization" || userData?.type === "organization" || userData?.organizationType) {
                console.log('🏢 Rendering organization overview (FORCED)');
                return renderOrganizationOverview();
              } else if (userType === "volunteer" || userData?.type === "volunteer") {
                console.log('🙋 Rendering volunteer overview');
                return renderVolunteerOverview();
              } else {
                console.log('⚠️ Unknown user type, defaulting to volunteer');
                return renderVolunteerOverview();
              }
            }
            if (activeTab === "activity") return renderActivity();
            if (activeTab === "organizations" && (userType === "volunteer" || userData?.type === "volunteer")) return renderVolunteersOrOrganizations();
            if (activeTab === "volunteers" && (userType === "organization" || userData?.type === "organization")) return renderVolunteersOrOrganizations();
            
            return null;
          })()}
        </div>
      </div>
    </div>
  );
};

export default Profile;