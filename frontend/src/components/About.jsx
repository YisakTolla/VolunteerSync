import React from 'react';
import { Heart, Users, Globe, Award, Target, Zap, Shield, ArrowRight } from 'lucide-react';
import './About.css';

const About = () => {
  const stats = [
    { number: '10,000+', label: 'Active Volunteers', icon: Users },
    { number: '2,500+', label: 'Partner Organizations', icon: Heart },
    { number: '150+', label: 'Cities Worldwide', icon: Globe },
    { number: '500K+', label: 'Volunteer Hours', icon: Award }
  ];

  const values = [
    {
      icon: Heart,
      title: 'Compassion First',
      description: 'We believe in the power of empathy and kindness to create meaningful change in communities worldwide.'
    },
    {
      icon: Users,
      title: 'Community Driven',
      description: 'Our platform thrives on the collective effort of volunteers, organizations, and supporters working together.'
    },
    {
      icon: Target,
      title: 'Impact Focused',
      description: 'Every feature we build and partnership we form is designed to maximize positive social impact.'
    },
    {
      icon: Shield,
      title: 'Trust & Safety',
      description: 'We maintain the highest standards of safety and verification to protect our community members.'
    },
    {
      icon: Zap,
      title: 'Innovation',
      description: 'We continuously evolve our technology to make volunteering more accessible and effective.'
    },
    {
      icon: Globe,
      title: 'Global Reach',
      description: 'We connect people across borders to address local and global challenges collaboratively.'
    }
  ];

  const team = [
    {
      name: 'Yisak Tolla',
      role: 'Founder',
      image: '/api/placeholder/150/150',
      bio: 'George Mason University Computer Science Student. Aspiring Software Engineer.'
    },
    {
      name: 'Mohamed Shaik',
      role: 'Founder',
      image: '/api/placeholder/150/150',
      bio: 'George Mason University Computer Science Masters Student. Software Engineer and ML/AI Engineer.'
    }
  ];

  const milestones = [
    {
      year: '2025',
      title: 'The Beginning',
      description: 'Founded with a vision to make volunteering more accessible and impactful for everyone.'
    },
  ];

  return (
    <div className="about-page">
      {/* Hero Section */}
      <section className="about-hero-section">
        <div className="about-hero-container">
          <div className="about-hero-content">
            <h1 className="about-hero-title">
              Building Bridges Between 
              <span className="about-gradient-text1"> Passionate Volunteers</span> and 
              <span className="about-gradient-text2"> Meaningful Opportunities</span>
            </h1>
            <p className="about-hero-description">
              VolunteerSync is more than a platform—we're a movement dedicated to creating 
              lasting positive change in communities worldwide through the power of connection, 
              technology, and shared purpose.
            </p>
            <div className="about-hero-cta">
              <button className="about-btn-primary">
                Join Our Mission <ArrowRight />
              </button>
              <button className="about-btn-secondary">
                Learn How It Works
              </button>
            </div>
          </div>
          <div className="about-hero-visual">
            <div className="about-volunteer-network">
              <div className="about-network-node about-primary">🤝</div>
              <div className="about-network-node">👥</div>
              <div className="about-network-node">🏢</div>
              <div className="about-network-node">🌍</div>
              <div className="about-network-node">💚</div>
              <div className="about-network-node">⭐</div>
              <div className="about-connecting-lines"></div>
            </div>
          </div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="about-stats-section">
        <div className="about-container">
          <h2 className="about-section-title">Our Impact in Numbers</h2>
          <div className="about-stats-grid">
            {stats.map((stat, index) => (
              <div key={index} className="about-stat-card">
                <div className="about-stat-icon">
                  <stat.icon />
                </div>
                <div className="about-stat-number">{stat.number}</div>
                <div className="about-stat-label">{stat.label}</div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Mission Section */}
      <section className="about-mission-section">
        <div className="about-container">
          <div className="about-mission-content">
            <div className="about-mission-text">
              <h2 className="about-section-title">Our Mission</h2>
              <p className="about-mission-description">
                We exist to democratize volunteering by removing barriers, creating meaningful 
                connections, and amplifying the impact of good intentions. Our technology-driven 
                approach ensures that every person who wants to make a difference can find their 
                perfect opportunity to contribute to causes they care about.
              </p>
              <div className="about-mission-highlights">
                <div className="about-highlight-item">
                  <div className="about-highlight-icon">🎯</div>
                  <div>
                    <h4>Precision Matching</h4>
                    <p>AI-powered algorithms connect volunteers with opportunities that match their skills, interests, and availability.</p>
                  </div>
                </div>
                <div className="about-highlight-item">
                  <div className="about-highlight-icon">📊</div>
                  <div>
                    <h4>Measurable Impact</h4>
                    <p>Track and measure the real-world impact of volunteer efforts with comprehensive analytics and reporting.</p>
                  </div>
                </div>
                <div className="about-highlight-item">
                  <div className="about-highlight-icon">🌐</div>
                  <div>
                    <h4>Global Community</h4>
                    <p>Connect with like-minded individuals and organizations across the world to tackle both local and global challenges.</p>
                  </div>
                </div>
              </div>
            </div>
            <div className="about-mission-visual">
              <div className="about-mission-illustration">
                <div className="about-illustration-circle">
                  <Heart />
                </div>
                <div className="about-impact-ripples">
                  <div className="about-ripple"></div>
                  <div className="about-ripple"></div>
                  <div className="about-ripple"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Values Section */}
      <section className="about-values-section">
        <div className="about-container">
          <h2 className="about-section-title">Our Core Values</h2>
          <p className="about-section-subtitle">
            These principles guide every decision we make and every feature we build
          </p>
          <div className="about-values-grid">
            {values.map((value, index) => (
              <div key={index} className="about-value-card">
                <div className="about-value-icon">
                  <value.icon />
                </div>
                <h3 className="about-value-title">{value.title}</h3>
                <p className="about-value-description">{value.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Team Section */}
      <section className="about-team-section">
        <div className="about-container">
          <h2 className="about-section-title">Meet Our Team</h2>
          <p className="about-section-subtitle">
            Passionate individuals dedicated to making volunteering accessible to everyone
          </p>
          <div className="about-team-grid">
            {team.map((member, index) => (
              <div key={index} className="about-team-card">
                <div className="about-team-avatar">
                  <div className="about-avatar-placeholder">
                    {member.name.split(' ').map(n => n[0]).join('')}
                  </div>
                </div>
                <h3 className="about-team-name">{member.name}</h3>
                <p className="about-team-role">{member.role}</p>
                <p className="about-team-bio">{member.bio}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Timeline Section */}
      <section className="about-timeline-section">
        <div className="about-container">
          <h2 className="about-section-title">Our Journey</h2>
          <p className="about-section-subtitle">
            From a simple idea to a global movement transforming how people volunteer
          </p>
          <div className="about-timeline">
            {milestones.map((milestone, index) => (
              <div key={index} className="about-timeline-item">
                <div className="about-timeline-marker"></div>
                <div className="about-timeline-content">
                  <div className="about-timeline-year">{milestone.year}</div>
                  <h3 className="about-timeline-title">{milestone.title}</h3>
                  <p className="about-timeline-description">{milestone.description}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="about-cta-section">
        <div className="about-container">
          <div className="about-cta-content">
            <h2 className="about-cta-title">Ready to Make a Difference?</h2>
            <p className="about-cta-description">
              Join thousands of volunteers and organizations already making an impact through VolunteerSync. 
              Whether you're looking to volunteer or need volunteers, we're here to help you succeed.
            </p>
            <div className="about-cta-buttons">
              <button className="about-btn-primary about-large">
                Start Volunteering Today
              </button>
              <button className="about-btn-secondary about-large">
                Register Your Organization
              </button>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default About;