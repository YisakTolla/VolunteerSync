import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Edit,
  MapPin,
  Calendar,
  Users,
  Award,
  Clock,
  Mail,
  Phone,
  Globe,
  Plus,
  Settings,
  Camera,
  UserPlus,
  MessageCircle,
  Building,
  UserCheck,
  Loader,
} from "lucide-react";
import {
  updateProfileData,
  uploadProfileImage,
} from "../services/profilePageService";

/**
 * Organization Profile Component
 * Displays and manages organization-specific profile information
 */
const OrganizationProfile = ({ userData, userType, onDataUpdate, onError }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [activeTab, setActiveTab] = useState("overview");
  const [uploading, setUploading] = useState(false);
  
  const navigate = useNavigate();

  const handleImageUpload = async (file, imageType) => {
    try {
      setUploading(true);
      const result = await uploadProfileImage(file, imageType);

      if (result.success) {
        // Update the userData with new image URL
        onDataUpdate(prev => ({
          ...prev,
          [imageType === 'profile' ? 'profileImageUrl' : 'coverImageUrl']: result.imageUrl
        }));
        console.log("✅ Image uploaded successfully");
      } else {
        onError(result.message || "Failed to upload image");
      }
    } catch (err) {
      onError("Failed to upload image");
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

  const getDisplayName = () => {
    return (
      userData.displayName || userData.organizationName || "Organization"
    );
  };

  const getContactEmail = () => {
    return (
      userData.email ||
      userData.contactEmail ||
      userData.organizationEmail ||
      ""
    );
  };

  const getContactPhone = () => {
    return (
      userData.phone || 
      userData.phoneNumber || 
      userData.contactPhone || 
      ""
    );
  };

  const getProfileImage = () => {
    return (
      userData.profileImageUrl ||
      userData.profileImage ||
      "/api/placeholder/150/150"
    );
  };

  const getCoverImage = () => {
    return (
      userData.coverImageUrl ||
      userData.coverImage ||
      "/api/placeholder/800/200"
    );
  };

  const organizationTabs = [
    { id: "overview", label: "Overview", icon: Building },
    { id: "volunteers", label: "Volunteers", icon: Users },
  ];

  const renderOverview = () => (
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
            <p className="profile-bio">
              {userData.bio || userData.missionStatement || userData.description}
            </p>
            <div className="profile-details">
              {(userData.organizationType || userData.primaryCategory) && (
                <div className="profile-detail">
                  <Building className="profile-detail-icon" />
                  <span>
                    {Array.isArray(userData.organizationType)
                      ? userData.organizationType.join(", ")
                      : userData.organizationType || userData.primaryCategory}
                  </span>
                </div>
              )}

              <div className="profile-detail">
                <MapPin className="profile-detail-icon" />
                <span>
                  {userData.city && userData.state && userData.country
                    ? `${userData.city}, ${userData.state}, ${userData.country}`
                    : userData.location ||
                      userData.address ||
                      "Location not specified"}
                </span>
              </div>

              <div className="profile-detail">
                <Calendar className="profile-detail-icon" />
                <span>
                  Founded{" "}
                  {userData.foundedYear ||
                    userData.founded ||
                    "Year not specified"}
                </span>
              </div>

              <div className="profile-detail">
                <Users className="profile-detail-icon" />
                <span>
                  {userData.organizationSize ||
                    (userData.employeeCount
                      ? `${userData.employeeCount} employees`
                      : "Size not specified")}
                </span>
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
                    href={
                      userData.website.startsWith("http")
                        ? userData.website
                        : `https://${userData.website}`
                    }
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

        {/* Organization Impact Stats */}
        <div className="profile-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">Organization Impact</h3>
          </div>
          <div className="profile-card-content">
            <div className="profile-stats-grid">
              <div className="profile-stat">
                <div className="profile-stat-number">
                  {userData.totalVolunteersServed ||
                    userData.stats?.volunteers ||
                    userData.volunteersCount ||
                    0}
                </div>
                <div className="profile-stat-label">Volunteers Served</div>
              </div>
              <div className="profile-stat">
                <div className="profile-stat-number">
                  {userData.stats?.hoursImpacted || 0}
                </div>
                <div className="profile-stat-label">Volunteer Hours</div>
              </div>
              <div className="profile-stat">
                <div className="profile-stat-number">
                  {userData.foundedYear
                    ? new Date().getFullYear() - userData.foundedYear
                    : "N/A"}
                </div>
                <div className="profile-stat-label">Years Active</div>
              </div>
            </div>
          </div>
        </div>

        {/* Funding Progress - Conditional */}
        {(userData.fundingGoal !== null && userData.fundingGoal !== undefined) ||
        (userData.stats?.fundingGoal !== null && userData.stats?.fundingGoal !== undefined) ? (
          <div className="profile-card">
            <div className="profile-card-header">
              <h3 className="profile-card-title">Funding Progress</h3>
            </div>
            <div className="profile-card-content">
              <div className="profile-funding">
                <div className="profile-funding-amounts">
                  <div className="profile-funding-raised">
                    <span className="profile-funding-number">
                      $
                      {(
                        userData.fundingRaised ||
                        userData.stats?.fundingRaised ||
                        0
                      ).toLocaleString()}
                    </span>
                    <span className="profile-funding-label">Raised</span>
                  </div>
                  <div className="profile-funding-goal">
                    <span className="profile-funding-number">
                      $
                      {(
                        userData.fundingGoal ||
                        userData.stats?.fundingGoal ||
                        0
                      ).toLocaleString()}
                    </span>
                    <span className="profile-funding-label">Goal</span>
                  </div>
                </div>
                <div className="profile-funding-bar">
                  <div
                    className="profile-funding-progress"
                    style={{
                      width: `${
                        userData.fundingGoal || userData.stats?.fundingGoal
                          ? Math.min(
                              ((userData.fundingRaised ||
                                userData.stats?.fundingRaised ||
                                0) /
                                (userData.fundingGoal ||
                                  userData.stats?.fundingGoal)) *
                                100,
                              100
                            )
                          : 0
                      }%`,
                    }}
                  ></div>
                </div>
              </div>
            </div>
          </div>
        ) : null}

        {/* Causes We Support */}
        <div className="profile-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">Causes We Support</h3>
            <button className="profile-add-btn">
              <Plus />
            </button>
          </div>
          <div className="profile-card-content">
            <div className="profile-tags">
              {(() => {
                const causes =
                  userData.causes ||
                  userData.categories ||
                  userData.interests ||
                  [];
                const causesArray = Array.isArray(causes)
                  ? causes
                  : typeof causes === "string"
                  ? causes.split(",")
                  : [];

                return causesArray.length > 0 ? (
                  causesArray.map((cause, index) => (
                    <span key={index} className="profile-tag interest">
                      {typeof cause === "string" ? cause.trim() : cause}
                    </span>
                  ))
                ) : (
                  <p className="no-data">
                    No causes added yet. Click the + button to add some!
                  </p>
                );
              })()}
            </div>
          </div>
        </div>

        {/* Services & Programs */}
        <div className="profile-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">Services & Programs</h3>
            <button className="profile-add-btn">
              <Plus />
            </button>
          </div>
          <div className="profile-card-content">
            <div className="profile-tags">
              {(() => {
                const services = userData.services || [];
                const servicesArray = Array.isArray(services)
                  ? services
                  : typeof services === "string"
                  ? services.split(",")
                  : [];

                return servicesArray.length > 0 ? (
                  servicesArray.map((service, index) => (
                    <span key={index} className="profile-tag skill">
                      {typeof service === "string" ? service.trim() : service}
                    </span>
                  ))
                ) : (
                  <p className="no-data">
                    No services added yet. Click the + button to add some!
                  </p>
                );
              })()}
            </div>
          </div>
        </div>

        {/* Focus Areas */}
        <div className="profile-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">Focus Areas</h3>
            <button className="profile-add-btn">
              <Plus />
            </button>
          </div>
          <div className="profile-card-content">
            <div className="profile-tags">
              {(() => {
                const focusAreas =
                  userData.categories || userData.organizationType || [];
                const focusAreasArray = Array.isArray(focusAreas)
                  ? focusAreas
                  : typeof focusAreas === "string"
                  ? focusAreas.split(",")
                  : [];

                return focusAreasArray.length > 0 ? (
                  focusAreasArray.map((area, index) => (
                    <span key={index} className="profile-tag interest">
                      {typeof area === "string" ? area.trim() : area}
                    </span>
                  ))
                ) : (
                  <p className="no-data">
                    No focus areas added yet. Click the + button to add some!
                  </p>
                );
              })()}
            </div>
          </div>
        </div>
      </div>
    </div>
  );


  const renderVolunteers = () => {
    const volunteers = userData.volunteers || [];

    return (
      <div className="profile-connections">
        <div className="profile-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">Our Volunteers</h3>
            <button className="profile-add-btn">
              <Plus />
            </button>
          </div>
          <div className="profile-card-content">
            <div className="profile-connections-grid">
              {volunteers && volunteers.length > 0 ? (
                volunteers.map((volunteer) => (
                  <div key={volunteer.id} className="profile-connection-card">
                    <div className="profile-connection-avatar">
                      {volunteer.profileImageUrl ? (
                        <img 
                          src={volunteer.profileImageUrl} 
                          alt={volunteer.name}
                          className="volunteer-avatar"
                        />
                      ) : (
                        <div className="volunteer-avatar-placeholder">
                          <Users />
                        </div>
                      )}
                    </div>
                    <div className="profile-connection-content">
                      <div className="profile-connection-name">
                        {volunteer.name || `${volunteer.firstName || ''} ${volunteer.lastName || ''}`.trim()}
                      </div>
                      <div className="profile-connection-role">
                        {volunteer.role || 'Volunteer'}
                      </div>
                      <div className="profile-connection-mutual">
                        {`${volunteer.hoursContributed || 0} hours contributed`}
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
                <div className="no-data-state">
                  <Users size={48} className="no-data-icon" />
                  <p className="no-data">
                    No volunteers yet. Create events to attract volunteers!
                  </p>
                  <button 
                    className="btn-primary"
                    onClick={() => navigate('/create-event')}
                  >
                    Create Event
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    );
  };

  return (
    <>
      {/* Profile Header */}
      <div className="profile-header">
        <div className="profile-cover">
          <img
            src={getCoverImage()}
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
                src={getProfileImage()}
                alt={getDisplayName()}
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
              <h1 className="profile-name">{getDisplayName()}</h1>
              {userData.organizationType && (
                <div className="profile-organization-type">
                  {Array.isArray(userData.organizationType)
                    ? userData.organizationType.join(", ")
                    : userData.organizationType}
                </div>
              )}

              <div className="profile-contact">
                {getContactEmail() && (
                  <div className="profile-contact-item">
                    <Mail className="profile-contact-icon" />
                    <span>{getContactEmail()}</span>
                  </div>
                )}

                {getContactPhone() && (
                  <div className="profile-contact-item">
                    <Phone className="profile-contact-icon" />
                    <span>{getContactPhone()}</span>
                  </div>
                )}
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
              onClick={() => navigate("/settings")}
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
          {organizationTabs.map((tab) => (
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
        {activeTab === "overview" && renderOverview()}
        {activeTab === "volunteers" && renderVolunteers()}
      </div>
    </>
  );
};

export default OrganizationProfile;