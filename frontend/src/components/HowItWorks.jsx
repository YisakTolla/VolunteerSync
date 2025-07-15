import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './HowItWorks.css';

const HowItWorks = () => {
  const [activeTab, setActiveTab] = useState('volunteers');
  const navigate = useNavigate();

  const handleGetStarted = (userType) => {
    navigate('/login', { state: { userType, mode: 'signup' } });
  };

  const volunteerSteps = [
    {
      step: '01',
      title: 'Create Your Profile',
      description: 'Sign up and build your volunteer profile with your skills, interests, availability, and location.',
      icon: '👤',
      features: [
        'Add your skills and expertise',
        'Set your availability preferences',
        'Upload a profile photo',
        'Specify your location and travel radius'
      ]
    },
    {
      step: '02',
      title: 'Discover & Search',
      description: 'Browse organizations and events in your area using our powerful search and filtering tools.',
      icon: '🔍',
      features: [
        'Search by location, cause, or keyword',
        'Filter by date, duration, and skill requirements',
        'View organization profiles and ratings',
        'Get personalized recommendations'
      ]
    },
    {
      step: '03',
      title: 'Connect & Apply',
      description: 'Join events, connect with organizations, and build relationships with fellow volunteers.',
      icon: '🤝',
      features: [
        'Apply to volunteer opportunities',
        'Send connection requests to other volunteers',
        'Join organization communities',
        'Message coordinators directly'
      ]
    },
    {
      step: '04',
      title: 'Track Your Impact',
      description: 'Monitor your volunteer hours, achievements, and the difference you\'re making.',
      icon: '📊',
      features: [
        'Log volunteer hours automatically',
        'Earn badges and achievements',
        'Generate impact reports',
        'Share your journey on social media'
      ]
    }
  ];

  const organizationSteps = [
    {
      step: '01',
      title: 'Register Your Organization',
      description: 'Create your organization profile and get verified to start recruiting volunteers.',
      icon: '🏢',
      features: [
        'Complete organization verification',
        'Add mission and impact statements',
        'Upload photos and documents',
        'Set up your organization dashboard'
      ]
    },
    {
      step: '02',
      title: 'Create Events & Opportunities',
      description: 'Post volunteer opportunities and events with detailed requirements and expectations.',
      icon: '📅',
      features: [
        'Create one-time events or ongoing opportunities',
        'Set skill requirements and prerequisites',
        'Define volunteer roles and responsibilities',
        'Schedule recurring volunteer activities'
      ]
    },
    {
      step: '03',
      title: 'Manage Applications',
      description: 'Review volunteer applications, communicate with candidates, and build your team.',
      icon: '✅',
      features: [
        'Review volunteer profiles and applications',
        'Conduct interviews and background checks',
        'Send acceptance or follow-up messages',
        'Create volunteer teams and groups'
      ]
    },
    {
      step: '04',
      title: 'Coordinate & Track',
      description: 'Manage your volunteers, track their contributions, and measure your organization\'s impact.',
      icon: '📈',
      features: [
        'Schedule and coordinate volunteer activities',
        'Track volunteer hours and contributions',
        'Generate impact and activity reports',
        'Recognize and reward top volunteers'
      ]
    }
  ];

  const features = [
    {
      icon: '🎯',
      title: 'Smart Matching',
      description: 'Our AI-powered algorithm connects volunteers with opportunities that match their skills, interests, and availability.',
      visual: '🤖'
    },
    {
      icon: '📱',
      title: 'Mobile Friendly',
      description: 'Access VolunteerSync on any device. Our responsive design works perfectly on desktop, tablet, and mobile.',
      visual: '📱'
    },
    {
      icon: '🛡️',
      title: 'Safe & Secure',
      description: 'All organizations are verified, and we provide safety guidelines and background check resources.',
      visual: '🔒'
    },
    {
      icon: '🌍',
      title: 'Global Reach',
      description: 'Connect with opportunities worldwide or focus on your local community. The choice is yours.',
      visual: '🗺️'
    }
  ];

  return (
    <div className="how-it-works-page">
      {/* Hero Section */}
      <section className="how-hero">
        <div className="container">
          <div className="how-hero-content">
            <h1 className="how-hero-title">
              How VolunteerSync 
              <span className="gradient-text"> Works</span>
            </h1>
            <p className="how-hero-description">
              Discover how easy it is to connect passionate volunteers with meaningful opportunities. 
              Whether you're looking to volunteer or manage volunteers, we've got you covered.
            </p>
            
            <div className="hero-demo-cards">
              <div className="demo-profile-card volunteer">
                <div className="demo-avatar">👤</div>
                <div className="demo-info">
                  <div className="demo-label">For Volunteers</div>
                  <div className="demo-text">Find opportunities, connect with organizations</div>
                </div>
              </div>
              
              <div className="demo-profile-card organization">
                <div className="demo-avatar">🏢</div>
                <div className="demo-info">
                  <div className="demo-label">For Organizations</div>
                  <div className="demo-text">Recruit volunteers, manage events</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Tab Navigation */}
      <section className="how-tabs-section">
        <div className="container">
          <div className="how-tabs">
            <button 
              className={`how-tab ${activeTab === 'volunteers' ? 'active' : ''}`}
              onClick={() => setActiveTab('volunteers')}
            >
              <span className="tab-icon">👥</span>
              <span className="tab-text">For Volunteers</span>
            </button>
            <button 
              className={`how-tab ${activeTab === 'organizations' ? 'active' : ''}`}
              onClick={() => setActiveTab('organizations')}
            >
              <span className="tab-icon">🏢</span>
              <span className="tab-text">For Organizations</span>
            </button>
          </div>
        </div>
      </section>

      {/* Steps Section */}
      <section className="how-steps-section">
        <div className="container">
          <div className="steps-grid">
            {(activeTab === 'volunteers' ? volunteerSteps : organizationSteps).map((step, index) => (
              <div key={index} className="step-card-detailed">
                <div className="step-header">
                  <div className="step-circle-large">
                    <div className="step-icon-large">{step.icon}</div>
                    <div className="step-number-badge">{step.step}</div>
                  </div>
                  <div className="step-info">
                    <h3 className="step-title-large">{step.title}</h3>
                    <p className="step-description-large">{step.description}</p>
                  </div>
                </div>
                
                <div className="step-features">
                  <ul className="feature-list">
                    {step.features.map((feature, featureIndex) => (
                      <li key={featureIndex} className="feature-item">
                        <span className="feature-check">✓</span>
                        <span className="feature-text">{feature}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              </div>
            ))}
          </div>
          
          <div className="steps-cta">
            <button 
              className="btn btn-primary btn-xl"
              onClick={() => handleGetStarted(activeTab === 'volunteers' ? 'volunteer' : 'organization')}
            >
              {activeTab === 'volunteers' ? 'Start Volunteering' : 'Register Organization'}
            </button>
          </div>
        </div>
      </section>

      {/* Features Overview */}
      <section className="how-features-section">
        <div className="container">
          <div className="section-header">
            <h2 className="section-title">Platform Features</h2>
            <p className="section-description">
              Powerful tools designed to make volunteering more accessible, efficient, and impactful.
            </p>
          </div>
          
          <div className="features-showcase">
            {features.map((feature, index) => (
              <div key={index} className="feature-showcase-card">
                <div className="feature-visual">
                  <div className="feature-icon-bg">
                    <span className="feature-visual-icon">{feature.visual}</span>
                  </div>
                </div>
                <div className="feature-content">
                  <div className="feature-icon-small">{feature.icon}</div>
                  <h3 className="feature-title-small">{feature.title}</h3>
                  <p className="feature-description-small">{feature.description}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Interactive Demo Section */}
      <section className="demo-section">
        <div className="container">
          <div className="demo-content">
            <div className="demo-text">
              <h2 className="section-title">See It In Action</h2>
              <p className="section-description">
                Experience how VolunteerSync makes it easy to find opportunities, 
                connect with organizations, and track your impact.
              </p>
              
              <div className="demo-features">
                <div className="demo-feature">
                  <div className="demo-feature-icon">🔍</div>
                  <div className="demo-feature-content">
                    <h4>Smart Search & Filters</h4>
                    <p>Find exactly what you're looking for with advanced filtering options.</p>
                  </div>
                </div>
                
                <div className="demo-feature">
                  <div className="demo-feature-icon">💬</div>
                  <div className="demo-feature-content">
                    <h4>Instant Messaging</h4>
                    <p>Communicate directly with organizations and fellow volunteers.</p>
                  </div>
                </div>
                
                <div className="demo-feature">
                  <div className="demo-feature-icon">📊</div>
                  <div className="demo-feature-content">
                    <h4>Impact Dashboard</h4>
                    <p>Track your volunteer hours and see the difference you're making.</p>
                  </div>
                </div>
              </div>
            </div>
            
            <div className="demo-visual">
              <div className="demo-interface">
                <div className="demo-screen">
                  <div className="demo-screen-header">
                    <div className="demo-screen-dots">
                      <span className="dot red"></span>
                      <span className="dot yellow"></span>
                      <span className="dot green"></span>
                    </div>
                    <div className="demo-screen-title">VolunteerSync Dashboard</div>
                  </div>
                  
                  <div className="demo-screen-content">
                    <div className="demo-search-bar">
                      <span className="search-icon">🔍</span>
                      <span className="search-text">Search opportunities near you...</span>
                    </div>
                    
                    <div className="demo-opportunities">
                      <div className="demo-opportunity active">
                        <div className="opp-icon">🌱</div>
                        <div className="opp-info">
                          <div className="opp-title">Beach Cleanup</div>
                          <div className="opp-org">Ocean Conservation</div>
                        </div>
                        <div className="opp-badge">Tomorrow</div>
                      </div>
                      
                      <div className="demo-opportunity">
                        <div className="opp-icon">📚</div>
                        <div className="opp-info">
                          <div className="opp-title">Reading Program</div>
                          <div className="opp-org">City Library</div>
                        </div>
                        <div className="opp-badge">Weekend</div>
                      </div>
                      
                      <div className="demo-opportunity">
                        <div className="opp-icon">🍽️</div>
                        <div className="opp-info">
                          <div className="opp-title">Food Drive</div>
                          <div className="opp-org">Food Bank</div>
                        </div>
                        <div className="opp-badge">Next Week</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Final CTA */}
      <section className="how-cta-section">
        <div className="container">
          <div className="how-cta-content">
            <h2 className="how-cta-title">Ready to Get Started?</h2>
            <p className="how-cta-description">
              Join thousands of volunteers and organizations making a difference in communities worldwide.
            </p>
            
            <div className="how-cta-buttons">
              <button 
                className="btn btn-primary btn-xl"
                onClick={() => handleGetStarted('volunteer')}
              >
                Join as Volunteer
              </button>
              <button 
                className="btn btn-secondary btn-xl"
                onClick={() => handleGetStarted('organization')}
              >
                Register Organization
              </button>
            </div>
            
            <p className="how-cta-note">
              Free to join • No hidden fees • Start making an impact today
            </p>
          </div>
        </div>
      </section>
    </div>
  );
};

export default HowItWorks;