import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
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
  Plus,
  Settings,
  Camera,
  Building,
  Loader,
  ExternalLink,
  HeartOff,
  X,
  Check,
  Trash2,
} from "lucide-react";
import {
  updateProfileData,
  uploadProfileImage,
  addInterest,
  addSkill,
} from "../services/profilePageService";
import ViewOrganizationService from "../services/viewOrganizationService";

/**
 * Volunteer Profile Component
 * Displays and manages volunteer-specific profile information
 */
const VolunteerProfile = ({ userData, userType, onDataUpdate, onError }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [activeTab, setActiveTab] = useState("overview");
  const [uploading, setUploading] = useState(false);
  
  // Followed organizations state
  const [followedOrgsDetails, setFollowedOrgsDetails] = useState([]);
  const [loadingFollowedOrgs, setLoadingFollowedOrgs] = useState(false);
  
  // Popup states
  const [showInterestsPopup, setShowInterestsPopup] = useState(false);
  const [showSkillsPopup, setShowSkillsPopup] = useState(false);
  const [newSkill, setNewSkill] = useState("");
  const [savingChanges, setSavingChanges] = useState(false);
  
  const navigate = useNavigate();

  // Available interests list - matching profile setup page
  const availableInterests = [
    "Community Cleanup",
    "Food Service", 
    "Tutoring & Education",
    "Animal Care",
    "Environmental Conservation",
    "Senior Support",
    "Youth Mentoring",
    "Healthcare Support",
    "Arts & Culture",
    "Technology & Digital",
    "Disaster Relief",
    "Community Building",
    "Other"
  ];

  useEffect(() => {
    // Load followed organizations details when component mounts
    if (userData?.followedOrganizations?.length > 0) {
      loadFollowedOrganizationsDetails(userData.followedOrganizations);
    }
  }, [userData?.followedOrganizations]);

  const loadFollowedOrganizationsDetails = async (orgIds) => {
    if (!orgIds || orgIds.length === 0) {
      setFollowedOrgsDetails([]);
      return;
    }

    try {
      setLoadingFollowedOrgs(true);
      console.log("Loading details for followed organizations:", orgIds);
      
      const result = await ViewOrganizationService.getFollowedOrganizations();
      
      if (result.success && result.data) {
        setFollowedOrgsDetails(result.data);
        console.log("Followed organizations details loaded:", result.data);
      } else {
        console.error("Failed to load followed organizations details:", result.message);
        setFollowedOrgsDetails([]);
      }
    } catch (error) {
      console.error("Error loading followed organizations details:", error);
      setFollowedOrgsDetails([]);
    } finally {
      setLoadingFollowedOrgs(false);
    }
  };

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
        console.log("Image uploaded successfully");
      } else {
        onError(result.message || "Failed to upload image");
      }
    } catch (err) {
      onError("Failed to upload image");
      console.error("Error uploading image:", err);
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

  const handleUnfollowOrganization = async (organizationId) => {
    try {
      const result = await ViewOrganizationService.unfollowOrganization(organizationId);
      
      if (result.success) {
        // Remove organization from local state
        setFollowedOrgsDetails(prev => 
          prev.filter(org => org.id !== organizationId)
        );
        
        // Update userData
        onDataUpdate(prev => ({
          ...prev,
          followedOrganizations: prev.followedOrganizations.filter(id => id !== organizationId),
          followedOrganizationsCount: Math.max(0, (prev.followedOrganizationsCount || 0) - 1),
          stats: {
            ...prev.stats,
            organizations: Math.max(0, (prev.stats?.organizations || 0) - 1)
          }
        }));
        
        console.log("Successfully unfollowed organization");
      } else {
        console.error("Failed to unfollow organization:", result.message);
        onError(result.message || "Failed to unfollow organization");
      }
    } catch (error) {
      console.error("Error unfollowing organization:", error);
      onError("Failed to unfollow organization");
    }
  };

  // Interest management functions
  const handleInterestToggle = (interest) => {
    const currentInterests = userData.interests || [];
    const isSelected = currentInterests.includes(interest);
    
    let updatedInterests;
    if (isSelected) {
      updatedInterests = currentInterests.filter(i => i !== interest);
    } else {
      updatedInterests = [...currentInterests, interest];
    }
    
    onDataUpdate(prev => ({
      ...prev,
      interests: updatedInterests,
      stats: {
        ...prev.stats,
        causes: updatedInterests.length
      }
    }));
  };

  const handleRemoveInterest = (interestToRemove) => {
    const currentInterests = userData.interests || [];
    const updatedInterests = currentInterests.filter(interest => interest !== interestToRemove);
    
    onDataUpdate(prev => ({
      ...prev,
      interests: updatedInterests,
      stats: {
        ...prev.stats,
        causes: updatedInterests.length
      }
    }));
  };

  const saveInterests = async () => {
    try {
      setSavingChanges(true);
      console.log('Saving interests:', userData.interests);
      
      // Convert array to comma-separated string for backend
      const interestsString = Array.isArray(userData.interests) 
        ? userData.interests.join(',') 
        : userData.interests || '';
      
      const result = await updateProfileData({
        interests: interestsString
      });
      
      if (result.success) {
        console.log("Interests updated successfully");
        // Update the user data in localStorage if needed
        if (result.data) {
          onDataUpdate(prev => ({
            ...prev,
            ...result.data,
            interests: userData.interests // Keep as array in frontend
          }));
        }
        setShowInterestsPopup(false);
      } else {
        onError(result.message || "Failed to update interests");
      }
    } catch (error) {
      console.error("Error saving interests:", error);
      onError("Failed to save interests");
    } finally {
      setSavingChanges(false);
    }
  };

  // Skills management functions
  const handleAddSkill = () => {
    if (!newSkill.trim()) return;
    
    const currentSkills = userData.skills || [];
    if (currentSkills.includes(newSkill.trim())) {
      onError("Skill already exists");
      return;
    }
    
    const updatedSkills = [...currentSkills, newSkill.trim()];
    onDataUpdate(prev => ({
      ...prev,
      skills: updatedSkills,
      stats: {
        ...prev.stats,
        skills: updatedSkills.length
      }
    }));
    
    setNewSkill("");
  };

  const handleRemoveSkill = (skillToRemove) => {
    const currentSkills = userData.skills || [];
    const updatedSkills = currentSkills.filter(skill => skill !== skillToRemove);
    
    onDataUpdate(prev => ({
      ...prev,
      skills: updatedSkills,
      stats: {
        ...prev.stats,
        skills: updatedSkills.length
      }
    }));
  };

  const saveSkills = async () => {
    try {
      setSavingChanges(true);
      console.log('Saving skills:', userData.skills);
      
      // Convert array to comma-separated string for backend
      const skillsString = Array.isArray(userData.skills) 
        ? userData.skills.join(',') 
        : userData.skills || '';
      
      const result = await updateProfileData({
        skills: skillsString
      });
      
      if (result.success) {
        console.log("Skills updated successfully");
        // Update the user data in localStorage if needed
        if (result.data) {
          onDataUpdate(prev => ({
            ...prev,
            ...result.data,
            skills: userData.skills // Keep as array in frontend
          }));
        }
        setShowSkillsPopup(false);
      } else {
        onError(result.message || "Failed to update skills");
      }
    } catch (error) {
      console.error("Error saving skills:", error);
      onError("Failed to save skills");
    } finally {
      setSavingChanges(false);
    }
  };

  const getDisplayName = () => {
    return (
      userData.displayName ||
      userData.name ||
      `${userData.firstName || ""} ${userData.lastName || ""}`.trim() ||
      "User"
    );
  };

  const getContactEmail = () => {
    return (
      userData.email ||
      userData.contactEmail ||
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

  const volunteerTabs = [
    { id: "overview", label: "Overview", icon: Users },
    { id: "organizations", label: "Organizations", icon: Heart },
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

        {/* Impact Stats */}
        <div className="profile-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">Impact Stats</h3>
          </div>
          <div className="profile-card-content">
            <div className="profile-stats-grid">
              <div className="profile-stat">
                <div className="profile-stat-number">
                  {userData.stats?.hours || userData.totalHours || 0}
                </div>
                <div className="profile-stat-label">Hours Volunteered</div>
              </div>
              <div className="profile-stat">
                <div className="profile-stat-number">
                  {userData.stats?.volunteered || userData.eventsAttended || 0}
                </div>
                <div className="profile-stat-label">Events Attended</div>
              </div>
              <div className="profile-stat">
                <div className="profile-stat-number">
                  {userData.stats?.causes || userData.interests?.length || 0}
                </div>
                <div className="profile-stat-label">Causes Supported</div>
              </div>
              <div className="profile-stat">
                <div className="profile-stat-number">
                  {userData.followedOrganizationsCount || userData.stats?.organizations || 0}
                </div>
                <div className="profile-stat-label">Organizations Following</div>
              </div>
            </div>
          </div>
        </div>

        {/* Interests */}
        <div className="profile-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">Interests</h3>
            <button 
              className="profile-add-btn"
              onClick={() => setShowInterestsPopup(true)}
            >
              <Plus />
            </button>
          </div>
          <div className="profile-card-content">
            <div className="profile-tags">
              {userData.interests && userData.interests.length > 0 ? (
                userData.interests.map((interest, index) => (
                  <span key={`interest-${index}-${interest}`} className="profile-tag interest">
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

        {/* Skills */}
        <div className="profile-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">Skills</h3>
            <button 
              className="profile-add-btn"
              onClick={() => setShowSkillsPopup(true)}
            >
              <Plus />
            </button>
          </div>
          <div className="profile-card-content">
            <div className="profile-tags">
              {userData.skills && userData.skills.length > 0 ? (
                userData.skills.map((skill, index) => (
                  <span key={`skill-${index}-${skill}`} className="profile-tag skill">
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

        {/* Badges & Achievements */}
        <div className="profile-card profile-badges-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">Badges & Achievements</h3>
          </div>
          <div className="profile-card-content">
            <div className="profile-badges">
              {userData.badges && userData.badges.length > 0 ? (
                userData.badges.map((badge) => (
                  <div key={`badge-${badge.id || badge.name}`} className="profile-badge">
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

  const renderOrganizations = () => (
    <div className="profile-connections">
      <div className="profile-card">
        <div className="profile-card-header">
          <h3 className="profile-card-title">Following Organizations</h3>
          <div className="profile-card-subtitle">
            {userData.followedOrganizationsCount || 0} organization{(userData.followedOrganizationsCount || 0) !== 1 ? 's' : ''}
          </div>
        </div>
        <div className="profile-card-content">
          {loadingFollowedOrgs ? (
            <div className="loading-state">
              <Loader className="loading-spinner" />
              <p>Loading organizations...</p>
            </div>
          ) : followedOrgsDetails && followedOrgsDetails.length > 0 ? (
            <div className="profile-connections-grid">
              {followedOrgsDetails.map((org) => (
                <div key={`org-${org.id || org.organizationName}`} className="profile-connection-card">
                  <div className="profile-connection-avatar">
                    {org.profileImageUrl ? (
                      <img 
                        src={org.profileImageUrl} 
                        alt={org.organizationName}
                        className="org-avatar"
                      />
                    ) : (
                      <div className="org-avatar-placeholder">
                        <Building />
                      </div>
                    )}
                  </div>
                  <div className="profile-connection-content">
                    <div className="profile-connection-name">
                      {org.organizationName || org.name || 'Unnamed Organization'}
                    </div>
                    <div className="profile-connection-role">
                      {(() => {
                        // Display categories with better fallback handling
                        const categories = org.categories || org.primaryCategory || org.organizationType;
                        
                        if (Array.isArray(categories) && categories.length > 0) {
                          return categories.filter(cat => cat && cat.trim()).join(', ');
                        }
                        
                        if (typeof categories === 'string' && categories.trim()) {
                          if (categories.includes(',')) {
                            return categories.split(',')
                              .map(cat => cat.trim())
                              .filter(cat => cat)
                              .join(', ');
                          }
                          return categories.trim();
                        }
                        
                        // Enhanced fallback - try other potential category fields
                        const fallbackCategory = org.cause || org.sector || org.type || org.focus;
                        if (fallbackCategory) {
                          return fallbackCategory;
                        }
                        
                        return 'Non-Profit Organization';
                      })()}
                    </div>
                    <div className="profile-connection-mutual">
                      {(() => {
                        if (org.city && org.state) {
                          return `${org.city}, ${org.state}`;
                        }
                        if (org.location) {
                          return org.location;
                        }
                        if (org.address) {
                          return org.address;
                        }
                        return 'Location not specified';
                      })()}
                    </div>
                  </div>
                  <div className="profile-connection-actions">
                    <button 
                      className="profile-connection-action"
                      onClick={() => navigate(`/find-organizations/${org.id}`)}
                      title="View Organization"
                    >
                      <ExternalLink size={16} />
                    </button>
                    <button 
                      className="profile-connection-action unfollow"
                      onClick={() => handleUnfollowOrganization(org.id)}
                      title="Unfollow Organization"
                    >
                      <HeartOff size={16} />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="no-data-state">
              <Heart size={48} className="no-data-icon" />
              <p className="no-data">
                No organizations followed yet. Start exploring organizations to connect with causes you care about!
              </p>
              <button 
                className="btn-primary"
                onClick={() => navigate('/find-organizations')}
              >
                Find Organizations
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );

  const renderInterestsPopup = () => {
    if (!showInterestsPopup) return null;

    return (
      <div className="popup-overlay" onClick={() => setShowInterestsPopup(false)}>
        <div className="popup-content" onClick={(e) => e.stopPropagation()}>
          <div className="popup-header">
            <h3>Manage Interests</h3>
            <button 
              className="popup-close"
              onClick={() => setShowInterestsPopup(false)}
            >
              <X />
            </button>
          </div>
          
          <div className="popup-body">
            <div className="add-skill-section">
              <label>Add New Interest:</label>
              <div className="interests-selection">
                <p style={{ fontSize: 'var(--font-size-sm)', color: 'var(--text-secondary)', marginBottom: 'var(--spacing-3)' }}>
                  Select from available interests (already added interests are disabled):
                </p>
                <div className="interests-grid">
                  {availableInterests.map((interest) => {
                    const isAlreadyAdded = userData.interests?.includes(interest) || false;
                    return (
                      <button
                        key={interest}
                        className={`interest-option ${isAlreadyAdded ? 'disabled' : ''}`}
                        onClick={() => {
                          if (!isAlreadyAdded) {
                            handleInterestToggle(interest);
                          }
                        }}
                        disabled={isAlreadyAdded}
                        title={isAlreadyAdded ? 'Already added - remove from current interests below' : 'Click to add this interest'}
                      >
                        <span className="interest-text">{interest}</span>
                        {isAlreadyAdded ? <Check size={16} /> : <Plus size={16} />}
                      </button>
                    );
                  })}
                </div>
              </div>
            </div>
            
            <div className="current-skills-section">
              <h4>Current Interests:</h4>
              {userData.interests && userData.interests.length > 0 ? (
                <div className="skills-list">
                  {userData.interests.map((interest, index) => (
                    <div key={`interest-${index}-${interest}`} className="skill-item">
                      <span>{interest}</span>
                      <button
                        className="remove-skill-btn"
                        onClick={() => handleRemoveInterest(interest)}
                        title="Remove interest"
                      >
                        <Trash2 size={14} />
                      </button>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="no-skills">No interests added yet.</p>
              )}
            </div>
          </div>
          
          <div className="popup-footer">
            <button 
              className="btn-secondary"
              onClick={() => setShowInterestsPopup(false)}
              disabled={savingChanges}
            >
              Cancel
            </button>
            <button 
              className="btn-primary"
              onClick={saveInterests}
              disabled={savingChanges}
            >
              {savingChanges ? <Loader className="btn-loader" /> : null}
              Save Changes
            </button>
          </div>
        </div>
      </div>
    );
  };

  const renderSkillsPopup = () => {
    if (!showSkillsPopup) return null;

    return (
      <div className="popup-overlay" onClick={() => setShowSkillsPopup(false)}>
        <div className="popup-content" onClick={(e) => e.stopPropagation()}>
          <div className="popup-header">
            <h3>Manage Skills</h3>
            <button 
              className="popup-close"
              onClick={() => setShowSkillsPopup(false)}
            >
              <X />
            </button>
          </div>
          
          <div className="popup-body">
            <div className="add-skill-section">
              <label htmlFor="new-skill">Add New Skill:</label>
              <div className="add-skill-input">
                <input
                  id="new-skill"
                  type="text"
                  value={newSkill}
                  onChange={(e) => setNewSkill(e.target.value)}
                  placeholder="Enter a skill (e.g., Project Management)"
                  onKeyPress={(e) => {
                    if (e.key === 'Enter') {
                      handleAddSkill();
                    }
                  }}
                />
                <button 
                  className="add-skill-btn"
                  onClick={handleAddSkill}
                  disabled={!newSkill.trim()}
                >
                  <Plus size={16} />
                </button>
              </div>
            </div>
            
            <div className="current-skills-section">
              <h4>Current Skills:</h4>
              {userData.skills && userData.skills.length > 0 ? (
                <div className="skills-list">
                  {userData.skills.map((skill, index) => (
                    <div key={`skill-${index}-${skill}`} className="skill-item">
                      <span>{skill}</span>
                      <button
                        className="remove-skill-btn"
                        onClick={() => handleRemoveSkill(skill)}
                        title="Remove skill"
                      >
                        <Trash2 size={14} />
                      </button>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="no-skills">No skills added yet.</p>
              )}
            </div>
          </div>
          
          <div className="popup-footer">
            <button 
              className="btn-secondary"
              onClick={() => setShowSkillsPopup(false)}
              disabled={savingChanges}
            >
              Cancel
            </button>
            <button 
              className="btn-primary"
              onClick={saveSkills}
              disabled={savingChanges}
            >
              {savingChanges ? <Loader className="btn-loader" /> : null}
              Save Changes
            </button>
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
          {volunteerTabs.map((tab) => (
            <button
              key={`tab-${tab.id}`}
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
        {activeTab === "organizations" && renderOrganizations()}
      </div>

      {/* Popups */}
      {renderInterestsPopup()}
      {renderSkillsPopup()}
    </>
  );
};

export default VolunteerProfile;