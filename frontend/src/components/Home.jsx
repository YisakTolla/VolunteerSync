import React from 'react';
import { useNavigate } from 'react-router-dom';
import './Home.css';

const Home = () => {
  const navigate = useNavigate();

  const handleVolunteerSignup = () => {
    navigate('/login', { state: { userType: 'volunteer', mode: 'signup' } });
  };

  const handleOrganizationSignup = () => {
    navigate('/login', { state: { userType: 'organization', mode: 'signup' } });
  };

  const handleJoinVolunteer = () => {
    navigate('/login', { state: { userType: 'volunteer', mode: 'signup' } });
  };

  const features = [
    {
      icon: '📅',
      title: 'Create & Join Events',
      description: 'Easily create volunteer events or discover opportunities in your community. From one-time events to ongoing commitments.',
    },
    {
      icon: '🏢',
      title: 'Connect with Organizations',
      description: 'Browse verified nonprofits and community organizations. Build lasting relationships with causes you care about.',
    },
    {
      icon: '👥',
      title: 'Meet Like-minded People',
      description: 'Connect with other volunteers who share your passions. Build friendships while making a difference together.',
    },
    {
      icon: '📍',
      title: 'Location-Based Discovery',
      description: 'Find volunteer opportunities near you with smart location filtering. Support your local community effectively.',
    },
    {
      icon: '🎯',
      title: 'Skill-Based Matching',
      description: 'Get matched with opportunities that utilize your unique skills and interests for maximum impact.',
    },
    {
      icon: '📊',
      title: 'Track Your Impact',
      description: 'See your volunteer hours, impact metrics, and achievements. Share your journey and inspire others.',
    },
  ];

  const stats = [
    { number: '15,000+', label: 'Active Volunteers' },
    { number: '500+', label: 'Partner Organizations' },
    { number: '2,000+', label: 'Active Events' },
    { number: '50+', label: 'Cities Worldwide' },
  ];

  const howItWorks = [
    {
      step: '01',
      title: 'Create Your Profile',
      description: 'Tell us about your interests, skills, availability, and location to get personalized recommendations.',
      icon: '👤'
    },
    {
      step: '02',
      title: 'Discover Opportunities',
      description: 'Browse events and organizations near you, or get matched with opportunities that fit your profile.',
      icon: '🔍'
    },
    {
      step: '03',
      title: 'Start Volunteering',
      description: 'Join events, connect with organizations, and start making a positive impact in your community.',
      icon: '❤️'
    }
  ];

  return (
    <div className="home">
      {/* Hero Section */}
      <section className="hero">
        <div className="hero-container">
          <div className="hero-content">
            <h1 className="hero-title">
              Connect. Volunteer.
              <br />
              <span className="hero-title-gradient">Make an Impact.</span>
            </h1>
            
            <p className="hero-description">
              Join thousands of volunteers making a difference in their communities. 
              Find meaningful opportunities, connect with like-minded people, and create lasting change.
            </p>

            <div className="hero-buttons">
              <button className="btn btn-primary btn-lg" onClick={handleJoinVolunteer}>
                Join as Volunteer
              </button>
              <button className="btn btn-secondary btn-lg" onClick={handleOrganizationSignup}>
                Register Organization
              </button>
            </div>

            <div className="hero-stats">
              {stats.map((stat, index) => (
                <div key={index} className="stat-item">
                  <div className="stat-number">{stat.number}</div>
                  <div className="stat-label">{stat.label}</div>
                </div>
              ))}
            </div>
          </div>

          <div className="hero-visual">
            <div className="community-preview">
              <div className="preview-header">
                <h3>Upcoming Events Near You</h3>
              </div>
              
              <div className="event-cards">
                <div className="event-card">
                  <div className="event-badge">Tomorrow</div>
                  <h4>Community Garden Cleanup</h4>
                  <p className="event-org">Green Earth Society</p>
                  <div className="event-details">
                    <span className="event-location">📍 Central Park</span>
                    <span className="event-volunteers">👥 8 volunteers</span>
                  </div>
                </div>
                
                <div className="event-card">
                  <div className="event-badge">This Weekend</div>
                  <h4>Food Bank Sorting</h4>
                  <p className="event-org">Metro Food Bank</p>
                  <div className="event-details">
                    <span className="event-location">📍 Downtown</span>
                    <span className="event-volunteers">👥 15 volunteers</span>
                  </div>
                </div>
                
                <div className="event-card">
                  <div className="event-badge">Next Week</div>
                  <h4>Reading to Children</h4>
                  <p className="event-org">City Library</p>
                  <div className="event-details">
                    <span className="event-location">📍 Main Library</span>
                    <span className="event-volunteers">👥 5 volunteers</span>
                  </div>
                </div>
              </div>
            </div>

            <div className="floating-elements">
              <div className="floating-element volunteer-card">
                <div className="volunteer-avatar">MJ</div>
                <div className="volunteer-info">
                  <span className="volunteer-name">Maria J.</span>
                  <span className="volunteer-stat">32 hours this month</span>
                </div>
              </div>
              
              <div className="floating-element org-card">
                <div className="org-logo">🌱</div>
                <div className="org-info">
                  <span className="org-name">EcoClean</span>
                  <span className="org-type">Environmental</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="features">
        <div className="container">
          <div className="section-header">
            <h2 className="section-title">
              Everything you need to volunteer effectively
            </h2>
            <p className="section-description">
              From finding the perfect opportunity to tracking your impact, 
              VolunteerSync makes it easy to connect with causes you care about.
            </p>
          </div>

          <div className="features-grid">
            {features.map((feature, index) => (
              <div key={index} className="feature-card">
                <div className="feature-icon">{feature.icon}</div>
                <h3 className="feature-title">{feature.title}</h3>
                <p className="feature-description">{feature.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* How It Works Section */}
      <section className="how-it-works">
        <div className="container">
          <div className="section-header">
            <h2 className="section-title">How VolunteerSync Works</h2>
            <p className="section-description">
              Getting started is simple. Create your profile, discover opportunities, and start making a difference.
            </p>
          </div>

          <div className="how-it-works-grid">
            {howItWorks.map((step, index) => (
              <div key={index} className="step-card">
                <div className="step-circle">
                  <div className="step-icon">{step.icon}</div>
                  <div className="step-number">{step.step}</div>
                </div>
                <h3 className="step-title">{step.title}</h3>
                <p className="step-description">{step.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Organizations Section */}
      <section className="organizations">
        <div className="container">
          <div className="section-header">
            <h2 className="section-title">Trusted by Amazing Organizations</h2>
            <p className="section-description">
              Partner with verified nonprofits and community organizations making real impact.
            </p>
          </div>

          <div className="org-categories">
            <div className="org-category">
              <div className="org-category-icon">🌱</div>
              <h3 className="org-category-title">Environmental</h3>
              <p className="org-category-count">120+ organizations</p>
            </div>
            
            <div className="org-category">
              <div className="org-category-icon">🎓</div>
              <h3 className="org-category-title">Education</h3>
              <p className="org-category-count">95+ organizations</p>
            </div>
            
            <div className="org-category">
              <div className="org-category-icon">🏥</div>
              <h3 className="org-category-title">Healthcare</h3>
              <p className="org-category-count">80+ organizations</p>
            </div>
            
            <div className="org-category">
              <div className="org-category-icon">🏠</div>
              <h3 className="org-category-title">Housing</h3>
              <p className="org-category-count">65+ organizations</p>
            </div>
            
            <div className="org-category">
              <div className="org-category-icon">🍽️</div>
              <h3 className="org-category-title">Food Security</h3>
              <p className="org-category-count">75+ organizations</p>
            </div>
            
            <div className="org-category">
              <div className="org-category-icon">🎨</div>
              <h3 className="org-category-title">Arts & Culture</h3>
              <p className="org-category-count">40+ organizations</p>
            </div>
          </div>
        </div>
      </section>

      {/* Community Impact Section */}
      <section className="impact">
        <div className="container">
          <div className="impact-content">
            <div className="impact-text">
              <h2 className="section-title">Real Stories, Real Impact</h2>
              <p className="section-description">
                See how volunteers and organizations are creating positive change in communities worldwide.
              </p>
              
              <div className="impact-testimonial">
                <div className="testimonial-content">
                  <p>"VolunteerSync helped me find my passion for environmental work. I've made incredible friends and contributed over 200 hours to local conservation efforts."</p>
                  <div className="testimonial-author">
                    <div className="author-avatar">SH</div>
                    <div className="author-info">
                      <div className="author-name">Sarah H.</div>
                      <div className="author-role">Environmental Volunteer</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <div className="impact-stats">
              <div className="impact-stat">
                <div className="impact-number">250K+</div>
                <div className="impact-label">Volunteer Hours</div>
              </div>
              <div className="impact-stat">
                <div className="impact-number">5,000+</div>
                <div className="impact-label">Events Completed</div>
              </div>
              <div className="impact-stat">
                <div className="impact-number">150+</div>
                <div className="impact-label">Communities Served</div>
              </div>
              <div className="impact-stat">
                <div className="impact-number">98%</div>
                <div className="impact-label">Satisfaction Rate</div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="cta">
        <div className="container">
          <div className="cta-content">
            <h2 className="cta-title">Ready to make a difference?</h2>
            <p className="cta-description">
              Join our community of passionate volunteers and start creating positive change in your area today.
            </p>
            <div className="cta-buttons">
              <button className="btn btn-primary btn-xl" onClick={handleVolunteerSignup}>
                Start Volunteering
              </button>
              <button className="btn btn-secondary btn-xl" onClick={handleOrganizationSignup}>
                List Your Organization
              </button>
            </div>
            <p className="cta-note">Free to join • No commitment required • Find opportunities near you</p>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Home;