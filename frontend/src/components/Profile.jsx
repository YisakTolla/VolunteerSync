import React, { useState } from 'react';
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
  Link
} from 'lucide-react';
import './Profile.css';

const Profile = ({ userType = 'volunteer' }) => { // 'volunteer' or 'organization'
  const [isEditing, setIsEditing] = useState(false);
  const [activeTab, setActiveTab] = useState('overview');

  // Mock volunteer data
  const volunteerData = {
    id: 1,
    type: 'volunteer',
    name: 'Yisak Tolla',
    email: 'ytolla@gmu.edu',
    phone: '+1 (555) 123-4567',
    bio: 'Passionate computer science student at George Mason University with a strong interest in volunteer work and community service. I love connecting with like-minded individuals and making a positive impact through technology and direct action.',
    location: 'Fairfax, Virginia',
    website: 'https://yisaktolla.dev',
    joinDate: 'January 2024',
    profileImage: '/api/placeholder/150/150',
    coverImage: '/api/placeholder/800/200',
    stats: {
      hoursVolunteered: 156,
      eventsAttended: 23,
      connections: 87,
      organizations: 5
    },
    interests: [
      'Education', 'Technology', 'Environment', 'Youth Development', 
      'Community Service', 'Animal Welfare', 'Healthcare', 'Arts & Culture'
    ],
    skills: [
      'JavaScript', 'React', 'Node.js', 'Python', 'Project Management',
      'Public Speaking', 'Event Planning', 'Team Leadership'
    ],
    badges: [
      { id: 1, name: 'Early Adopter', icon: '🚀', description: 'Joined in the first month' },
      { id: 2, name: 'Community Builder', icon: '🤝', description: 'Connected 50+ volunteers' },
      { id: 3, name: 'Dedicated Volunteer', icon: '⭐', description: 'Completed 20+ events' },
      { id: 4, name: 'Mentor', icon: '🎓', description: 'Helped train new volunteers' }
    ],
    recentActivity: [
      { id: 1, type: 'event', title: 'Food Bank Volunteer Drive', date: '2 days ago', organization: 'Local Food Bank' },
      { id: 2, type: 'connection', title: 'Connected with Sarah Chen', date: '1 week ago' },
      { id: 3, type: 'organization', title: 'Joined Environmental Action Group', date: '2 weeks ago' },
      { id: 4, type: 'event', title: 'Community Cleanup Day', date: '3 weeks ago', organization: 'City Parks Department' }
    ],
    organizations: [
      { id: 1, name: 'Local Food Bank', role: 'Regular Volunteer', since: 'Jan 2024', logo: '🍽️' },
      { id: 2, name: 'Environmental Action Group', role: 'Team Leader', since: 'Feb 2024', logo: '🌱' },
      { id: 3, name: 'Youth Mentorship Program', role: 'Mentor', since: 'Mar 2024', logo: '👥' },
      { id: 4, name: 'Animal Rescue Center', role: 'Volunteer', since: 'Apr 2024', logo: '🐾' }
    ],
    connections: [
      { id: 1, name: 'Sarah Chen', role: 'Community Organizer', mutualConnections: 12, avatar: 'SC' },
      { id: 2, name: 'Marcus Rodriguez', role: 'Volunteer Coordinator', mutualConnections: 8, avatar: 'MR' },
      { id: 3, name: 'Dr. Amira Okafor', role: 'Program Director', mutualConnections: 15, avatar: 'AO' },
      { id: 4, name: 'James Kim', role: 'Event Organizer', mutualConnections: 6, avatar: 'JK' }
    ]
  };

  // Mock organization data
  const organizationData = {
    id: 1,
    type: 'organization',
    name: 'Environmental Action Group',
    organizationType: 'Non-Profit',
    email: 'contact@enviroaction.org',
    phone: '+1 (555) 987-6543',
    bio: 'We are a passionate environmental organization dedicated to protecting our planet through community action, education, and sustainable practices. Join us in making a difference for future generations.',
    location: 'Washington, D.C.',
    website: 'https://enviroaction.org',
    founded: 'March 2018',
    ein: '12-3456789',
    profileImage: '/api/placeholder/150/150',
    coverImage: '/api/placeholder/800/200',
    stats: {
      volunteers: 342,
      eventsHosted: 89,
      hoursImpacted: 2450,
      fundingGoal: 75000,
      fundingRaised: 52000
    },
    causes: [
      'Climate Change', 'Wildlife Conservation', 'Clean Water', 'Renewable Energy',
      'Sustainable Agriculture', 'Ocean Protection', 'Forest Conservation', 'Green Technology'
    ],
    services: [
      'Environmental Education', 'Community Cleanups', 'Tree Planting', 'Policy Advocacy',
      'Research & Data Collection', 'Youth Programs', 'Corporate Partnerships'
    ],
    achievements: [
      { id: 1, name: 'Verified Organization', icon: '✅', description: 'Background checked and verified' },
      { id: 2, name: 'Top Rated', icon: '⭐', description: '4.9/5 volunteer satisfaction rating' },
      { id: 3, name: 'Impact Leader', icon: '🏆', description: 'Top 10% for community impact' },
      { id: 4, name: 'Transparency Award', icon: '🔍', description: 'Excellent financial transparency' }
    ],
    recentActivity: [
      { id: 1, type: 'event', title: 'Hosted River Cleanup Event', date: '3 days ago', volunteers: 45 },
      { id: 2, type: 'volunteer', title: 'Welcome new volunteer: Alex Johnson', date: '1 week ago' },
      { id: 3, type: 'achievement', title: 'Reached 300+ volunteer milestone', date: '2 weeks ago' },
      { id: 4, type: 'event', title: 'Earth Day Community Festival', date: '1 month ago', volunteers: 120 }
    ],
    volunteers: [
      { id: 1, name: 'Sarah Chen', role: 'Team Leader', hoursContributed: 156, avatar: 'SC' },
      { id: 2, name: 'Marcus Rodriguez', role: 'Event Coordinator', hoursContributed: 142, avatar: 'MR' },
      { id: 3, name: 'Dr. Amira Okafor', role: 'Research Lead', hoursContributed: 98, avatar: 'AO' },
      { id: 4, name: 'James Kim', role: 'Outreach Specialist', hoursContributed: 87, avatar: 'JK' }
    ],
    partnerships: [
      { id: 1, name: 'City Parks Department', type: 'Government Partner', since: 'Jan 2022', logo: '🏛️' },
      { id: 2, name: 'Green Tech Solutions', type: 'Corporate Sponsor', since: 'Mar 2023', logo: '💼' },
      { id: 3, name: 'University Research Lab', type: 'Academic Partner', since: 'Sep 2023', logo: '🎓' },
      { id: 4, name: 'Local Community Center', type: 'Community Partner', since: 'Nov 2023', logo: '🏢' }
    ]
  };

  const userData = userType === 'volunteer' ? volunteerData : organizationData;

  const volunteerTabs = [
    { id: 'overview', label: 'Overview', icon: Users },
    { id: 'activity', label: 'Activity', icon: Clock },
    { id: 'organizations', label: 'Organizations', icon: Heart },
    { id: 'connections', label: 'Connections', icon: UserPlus }
  ];

  const organizationTabs = [
    { id: 'overview', label: 'Overview', icon: Building },
    { id: 'activity', label: 'Activity', icon: Clock },
    { id: 'volunteers', label: 'Volunteers', icon: Users },
    { id: 'partnerships', label: 'Partnerships', icon: Heart }
  ];

  const tabs = userType === 'volunteer' ? volunteerTabs : organizationTabs;

  const renderVolunteerOverview = () => (
    <div className="profile-overview">
      <div className="profile-overview-grid">
        {/* About Section */}
        <div className="profile-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">About</h3>
            <button className="profile-edit-btn" onClick={() => setIsEditing(!isEditing)}>
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
              <div className="profile-detail">
                <Globe className="profile-detail-icon" />
                <a href={userData.website} target="_blank" rel="noopener noreferrer">
                  {userData.website}
                </a>
              </div>
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
                <div className="profile-stat-number">{userData.stats.hoursVolunteered}</div>
                <div className="profile-stat-label">Hours Volunteered</div>
              </div>
              <div className="profile-stat">
                <div className="profile-stat-number">{userData.stats.eventsAttended}</div>
                <div className="profile-stat-label">Events Attended</div>
              </div>
              <div className="profile-stat">
                <div className="profile-stat-number">{userData.stats.connections}</div>
                <div className="profile-stat-label">Connections</div>
              </div>
              <div className="profile-stat">
                <div className="profile-stat-number">{userData.stats.organizations}</div>
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
              {userData.interests.map((interest, index) => (
                <span key={index} className="profile-tag interest">
                  {interest}
                </span>
              ))}
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
              {userData.skills.map((skill, index) => (
                <span key={index} className="profile-tag skill">
                  {skill}
                </span>
              ))}
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
              {userData.badges.map((badge) => (
                <div key={badge.id} className="profile-badge">
                  <div className="profile-badge-icon">{badge.icon}</div>
                  <div className="profile-badge-content">
                    <div className="profile-badge-name">{badge.name}</div>
                    <div className="profile-badge-description">{badge.description}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );

  const renderOrganizationOverview = () => (
    <div className="profile-overview">
      <div className="profile-overview-grid">
        {/* About Section */}
        <div className="profile-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">About</h3>
            <button className="profile-edit-btn" onClick={() => setIsEditing(!isEditing)}>
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
                <span>Founded {userData.founded}</span>
              </div>
              <div className="profile-detail">
                <FileText className="profile-detail-icon" />
                <span>EIN: {userData.ein}</span>
              </div>
              <div className="profile-detail">
                <Globe className="profile-detail-icon" />
                <a href={userData.website} target="_blank" rel="noopener noreferrer">
                  {userData.website}
                </a>
              </div>
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
                <div className="profile-stat-number">{userData.stats.volunteers}</div>
                <div className="profile-stat-label">Active Volunteers</div>
              </div>
              <div className="profile-stat">
                <div className="profile-stat-number">{userData.stats.eventsHosted}</div>
                <div className="profile-stat-label">Events Hosted</div>
              </div>
              <div className="profile-stat">
                <div className="profile-stat-number">{userData.stats.hoursImpacted}</div>
                <div className="profile-stat-label">Volunteer Hours</div>
              </div>
              <div className="profile-stat">
                <div className="profile-stat-number">{Math.round((userData.stats.fundingRaised / userData.stats.fundingGoal) * 100)}%</div>
                <div className="profile-stat-label">Funding Goal</div>
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
                  <span className="profile-funding-number">${userData.stats.fundingRaised.toLocaleString()}</span>
                  <span className="profile-funding-label">Raised</span>
                </div>
                <div className="profile-funding-goal">
                  <span className="profile-funding-number">${userData.stats.fundingGoal.toLocaleString()}</span>
                  <span className="profile-funding-label">Goal</span>
                </div>
              </div>
              <div className="profile-funding-bar">
                <div 
                  className="profile-funding-progress" 
                  style={{ width: `${(userData.stats.fundingRaised / userData.stats.fundingGoal) * 100}%` }}
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
              {userData.causes.map((cause, index) => (
                <span key={index} className="profile-tag interest">
                  {cause}
                </span>
              ))}
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
              {userData.services.map((service, index) => (
                <span key={index} className="profile-tag skill">
                  {service}
                </span>
              ))}
            </div>
          </div>
        </div>

        {/* Achievements Section */}
        <div className="profile-card profile-badges-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">Achievements & Recognition</h3>
          </div>
          <div className="profile-card-content">
            <div className="profile-badges">
              {userData.achievements.map((achievement) => (
                <div key={achievement.id} className="profile-badge">
                  <div className="profile-badge-icon">{achievement.icon}</div>
                  <div className="profile-badge-content">
                    <div className="profile-badge-name">{achievement.name}</div>
                    <div className="profile-badge-description">{achievement.description}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );

  const renderActivity = () => (
    <div className="profile-activity">
      <div className="profile-card">
        <div className="profile-card-header">
          <h3 className="profile-card-title">Recent Activity</h3>
        </div>
        <div className="profile-card-content">
          <div className="profile-activity-list">
            {userData.recentActivity.map((activity) => (
              <div key={activity.id} className="profile-activity-item">
                <div className="profile-activity-icon">
                  {activity.type === 'event' && <Calendar />}
                  {activity.type === 'connection' && <Users />}
                  {activity.type === 'organization' && <Heart />}
                  {activity.type === 'volunteer' && <UserPlus />}
                  {activity.type === 'achievement' && <Award />}
                </div>
                <div className="profile-activity-content">
                  <div className="profile-activity-title">{activity.title}</div>
                  {activity.organization && (
                    <div className="profile-activity-organization">at {activity.organization}</div>
                  )}
                  {activity.volunteers && (
                    <div className="profile-activity-organization">{activity.volunteers} volunteers participated</div>
                  )}
                  <div className="profile-activity-date">{activity.date}</div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );

  const renderVolunteersOrConnections = () => {
    const data = userType === 'volunteer' ? userData.connections : userData.volunteers;
    const title = userType === 'volunteer' ? 'My Connections' : 'Our Volunteers';
    const addIcon = userType === 'volunteer' ? UserPlus : Plus;

    return (
      <div className="profile-connections">
        <div className="profile-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">{title}</h3>
            <button className="profile-add-btn">
              {React.createElement(addIcon)}
            </button>
          </div>
          <div className="profile-card-content">
            <div className="profile-connections-grid">
              {data.map((item) => (
                <div key={item.id} className="profile-connection-card">
                  <div className="profile-connection-avatar">
                    {item.avatar}
                  </div>
                  <div className="profile-connection-content">
                    <div className="profile-connection-name">{item.name}</div>
                    <div className="profile-connection-role">{item.role}</div>
                    <div className="profile-connection-mutual">
                      {userType === 'volunteer' 
                        ? `${item.mutualConnections} mutual connections`
                        : `${item.hoursContributed} hours contributed`
                      }
                    </div>
                  </div>
                  <div className="profile-connection-actions">
                    <button className="profile-connection-action">
                      <MessageCircle />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    );
  };

  const renderOrganizationsOrPartnerships = () => {
    const data = userType === 'volunteer' ? userData.organizations : userData.partnerships;
    const title = userType === 'volunteer' ? 'My Organizations' : 'Our Partnerships';

    return (
      <div className="profile-organizations">
        <div className="profile-card">
          <div className="profile-card-header">
            <h3 className="profile-card-title">{title}</h3>
            <button className="profile-add-btn">
              <Plus />
            </button>
          </div>
          <div className="profile-card-content">
            <div className="profile-organizations-grid">
              {data.map((item) => (
                <div key={item.id} className="profile-organization-card">
                  <div className="profile-organization-logo">{item.logo}</div>
                  <div className="profile-organization-content">
                    <div className="profile-organization-name">{item.name}</div>
                    <div className="profile-organization-role">
                      {userType === 'volunteer' ? item.role : item.type}
                    </div>
                    <div className="profile-organization-since">Since {item.since}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    );
  };

  return (
    <div className="profile-page">
      <div className="profile-container">
        {/* Header Section */}
        <div className="profile-header">
          <div className="profile-cover">
            <img src={userData.coverImage} alt="Cover" className="profile-cover-image" />
            <button className="profile-cover-edit">
              <Camera />
            </button>
          </div>
          
          <div className="profile-header-content">
            <div className="profile-header-info">
              <div className="profile-avatar-container">
                <img src={userData.profileImage} alt={userData.name} className="profile-avatar" />
                <button className="profile-avatar-edit">
                  <Camera />
                </button>
              </div>
              
              <div className="profile-header-details">
                <h1 className="profile-name">{userData.name}</h1>
                {userType === 'organization' && (
                  <div className="profile-organization-type">{userData.organizationType}</div>
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
              <button className="profile-action-btn primary">
                <Edit />
                Edit Profile
              </button>
              <button className="profile-action-btn secondary">
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
                className={`profile-nav-tab ${activeTab === tab.id ? 'active' : ''}`}
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
          {activeTab === 'overview' && (
            userType === 'volunteer' ? renderVolunteerOverview() : renderOrganizationOverview()
          )}
          {activeTab === 'activity' && renderActivity()}
          {(activeTab === 'organizations' || activeTab === 'partnerships') && renderOrganizationsOrPartnerships()}
          {(activeTab === 'connections' || activeTab === 'volunteers') && renderVolunteersOrConnections()}
        </div>
      </div>
    </div>
  );
};

export default Profile;