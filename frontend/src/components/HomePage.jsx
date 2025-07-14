import React from 'react';

const HomePage = () => {
  return (
    <div style={{ 
      fontFamily: 'Arial, sans-serif',
      lineHeight: '1.6',
      color: '#333'
    }}>
      {/* Hero Section */}
      <section style={{
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        color: 'white',
        padding: '80px 20px',
        textAlign: 'center'
      }}>
        <div style={{ maxWidth: '800px', margin: '0 auto' }}>
          <h1 style={{ 
            fontSize: '3.5rem', 
            marginBottom: '20px',
            fontWeight: 'bold'
          }}>
            🤝 VolunteerSync
          </h1>
          <p style={{ 
            fontSize: '1.3rem', 
            marginBottom: '30px',
            opacity: '0.9'
          }}>
            Connecting passionate volunteers with meaningful opportunities in their community
          </p>
          <div style={{ marginBottom: '40px' }}>
            <button style={{
              backgroundColor: '#ff6b6b',
              color: 'white',
              border: 'none',
              padding: '15px 30px',
              fontSize: '1.1rem',
              borderRadius: '50px',
              cursor: 'pointer',
              margin: '10px',
              boxShadow: '0 4px 15px rgba(0,0,0,0.2)',
              transition: 'transform 0.2s'
            }}
            onMouseOver={(e) => e.target.style.transform = 'translateY(-2px)'}
            onMouseOut={(e) => e.target.style.transform = 'translateY(0)'}
            >
              🙋‍♀️ Find Volunteer Opportunities
            </button>
            <button style={{
              backgroundColor: 'rgba(255,255,255,0.2)',
              color: 'white',
              border: '2px solid white',
              padding: '15px 30px',
              fontSize: '1.1rem',
              borderRadius: '50px',
              cursor: 'pointer',
              margin: '10px',
              transition: 'all 0.2s'
            }}
            onMouseOver={(e) => {
              e.target.style.backgroundColor = 'white';
              e.target.style.color = '#667eea';
            }}
            onMouseOut={(e) => {
              e.target.style.backgroundColor = 'rgba(255,255,255,0.2)';
              e.target.style.color = 'white';
            }}
            >
              🏢 Register Your Organization
            </button>
          </div>
        </div>
      </section>

      {/* Statistics Section */}
      <section style={{ 
        padding: '60px 20px',
        backgroundColor: '#f8f9fa',
        textAlign: 'center'
      }}>
        <div style={{ maxWidth: '1000px', margin: '0 auto' }}>
          <h2 style={{ 
            fontSize: '2.5rem', 
            marginBottom: '50px',
            color: '#2c3e50'
          }}>
            Making an Impact Together
          </h2>
          <div style={{ 
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
            gap: '30px'
          }}>
            <div style={{
              padding: '30px',
              backgroundColor: 'white',
              borderRadius: '15px',
              boxShadow: '0 5px 15px rgba(0,0,0,0.1)'
            }}>
              <div style={{ fontSize: '3rem', marginBottom: '10px' }}>👥</div>
              <h3 style={{ fontSize: '2rem', color: '#667eea', marginBottom: '5px' }}>2,500+</h3>
              <p style={{ color: '#666' }}>Active Volunteers</p>
            </div>
            <div style={{
              padding: '30px',
              backgroundColor: 'white',
              borderRadius: '15px',
              boxShadow: '0 5px 15px rgba(0,0,0,0.1)'
            }}>
              <div style={{ fontSize: '3rem', marginBottom: '10px' }}>🏢</div>
              <h3 style={{ fontSize: '2rem', color: '#667eea', marginBottom: '5px' }}>150+</h3>
              <p style={{ color: '#666' }}>Partner Organizations</p>
            </div>
            <div style={{
              padding: '30px',
              backgroundColor: 'white',
              borderRadius: '15px',
              boxShadow: '0 5px 15px rgba(0,0,0,0.1)'
            }}>
              <div style={{ fontSize: '3rem', marginBottom: '10px' }}>🎯</div>
              <h3 style={{ fontSize: '2rem', color: '#667eea', marginBottom: '5px' }}>500+</h3>
              <p style={{ color: '#666' }}>Opportunities Posted</p>
            </div>
            <div style={{
              padding: '30px',
              backgroundColor: 'white',
              borderRadius: '15px',
              boxShadow: '0 5px 15px rgba(0,0,0,0.1)'
            }}>
              <div style={{ fontSize: '3rem', marginBottom: '10px' }}>⏰</div>
              <h3 style={{ fontSize: '2rem', color: '#667eea', marginBottom: '5px' }}>10,000+</h3>
              <p style={{ color: '#666' }}>Hours Contributed</p>
            </div>
          </div>
        </div>
      </section>

      {/* How It Works Section */}
      <section style={{ 
        padding: '80px 20px',
        backgroundColor: 'white'
      }}>
        <div style={{ maxWidth: '1000px', margin: '0 auto', textAlign: 'center' }}>
          <h2 style={{ 
            fontSize: '2.5rem', 
            marginBottom: '20px',
            color: '#2c3e50'
          }}>
            How VolunteerSync Works
          </h2>
          <p style={{ 
            fontSize: '1.2rem', 
            color: '#666', 
            marginBottom: '50px',
            maxWidth: '600px',
            margin: '0 auto 50px'
          }}>
            Our smart matching system connects you with opportunities that align with your interests, skills, and availability.
          </p>
          
          <div style={{ 
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
            gap: '40px',
            marginTop: '40px'
          }}>
            <div style={{ textAlign: 'center' }}>
              <div style={{
                width: '80px',
                height: '80px',
                backgroundColor: '#667eea',
                borderRadius: '50%',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                margin: '0 auto 20px',
                fontSize: '2rem'
              }}>
                📝
              </div>
              <h3 style={{ color: '#2c3e50', marginBottom: '15px' }}>1. Create Your Profile</h3>
              <p style={{ color: '#666' }}>Tell us about your interests, skills, and availability preferences</p>
            </div>
            
            <div style={{ textAlign: 'center' }}>
              <div style={{
                width: '80px',
                height: '80px',
                backgroundColor: '#ff6b6b',
                borderRadius: '50%',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                margin: '0 auto 20px',
                fontSize: '2rem'
              }}>
                🔍
              </div>
              <h3 style={{ color: '#2c3e50', marginBottom: '15px' }}>2. Get Matched</h3>
              <p style={{ color: '#666' }}>Our algorithm finds opportunities that perfectly match your profile</p>
            </div>
            
            <div style={{ textAlign: 'center' }}>
              <div style={{
                width: '80px',
                height: '80px',
                backgroundColor: '#4ecdc4',
                borderRadius: '50%',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                margin: '0 auto 20px',
                fontSize: '2rem'
              }}>
              💝
              </div>
              <h3 style={{ color: '#2c3e50', marginBottom: '15px' }}>3. Make an Impact</h3>
              <p style={{ color: '#666' }}>Start volunteering and track your contributions to the community</p>
            </div>
          </div>
        </div>
      </section>

      {/* Categories Section */}
      <section style={{ 
        padding: '80px 20px',
        backgroundColor: '#f8f9fa'
      }}>
        <div style={{ maxWidth: '1000px', margin: '0 auto', textAlign: 'center' }}>
          <h2 style={{ 
            fontSize: '2.5rem', 
            marginBottom: '50px',
            color: '#2c3e50'
          }}>
            Popular Volunteer Categories
          </h2>
          
          <div style={{ 
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
            gap: '25px'
          }}>
            {[
              { emoji: '🌱', title: 'Environment', desc: 'Help protect our planet' },
              { emoji: '📚', title: 'Education', desc: 'Support learning initiatives' },
              { emoji: '🏥', title: 'Healthcare', desc: 'Assist medical programs' },
              { emoji: '🐕', title: 'Animal Welfare', desc: 'Care for animals in need' },
              { emoji: '🍽️', title: 'Food Security', desc: 'Fight hunger in communities' },
              { emoji: '👵', title: 'Senior Care', desc: 'Support elderly community members' }
            ].map((category, index) => (
              <div key={index} style={{
                padding: '25px',
                backgroundColor: 'white',
                borderRadius: '12px',
                boxShadow: '0 3px 10px rgba(0,0,0,0.1)',
                cursor: 'pointer',
                transition: 'transform 0.2s, box-shadow 0.2s'
              }}
              onMouseOver={(e) => {
                e.currentTarget.style.transform = 'translateY(-5px)';
                e.currentTarget.style.boxShadow = '0 8px 25px rgba(0,0,0,0.15)';
              }}
              onMouseOut={(e) => {
                e.currentTarget.style.transform = 'translateY(0)';
                e.currentTarget.style.boxShadow = '0 3px 10px rgba(0,0,0,0.1)';
              }}
              >
                <div style={{ fontSize: '2.5rem', marginBottom: '15px' }}>{category.emoji}</div>
                <h3 style={{ color: '#2c3e50', marginBottom: '10px', fontSize: '1.2rem' }}>{category.title}</h3>
                <p style={{ color: '#666', fontSize: '0.9rem' }}>{category.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Call to Action Section */}
      <section style={{
        background: 'linear-gradient(135deg, #ff6b6b 0%, #feca57 100%)',
        color: 'white',
        padding: '80px 20px',
        textAlign: 'center'
      }}>
        <div style={{ maxWidth: '600px', margin: '0 auto' }}>
          <h2 style={{ 
            fontSize: '2.5rem', 
            marginBottom: '20px',
            fontWeight: 'bold'
          }}>
            Ready to Make a Difference?
          </h2>
          <p style={{ 
            fontSize: '1.2rem', 
            marginBottom: '30px',
            opacity: '0.9'
          }}>
            Join thousands of volunteers already making an impact in their communities.
          </p>
          <button style={{
            backgroundColor: 'white',
            color: '#ff6b6b',
            border: 'none',
            padding: '18px 40px',
            fontSize: '1.2rem',
            borderRadius: '50px',
            cursor: 'pointer',
            fontWeight: 'bold',
            boxShadow: '0 4px 15px rgba(0,0,0,0.2)',
            transition: 'transform 0.2s'
          }}
          onMouseOver={(e) => e.target.style.transform = 'translateY(-2px)'}
          onMouseOut={(e) => e.target.style.transform = 'translateY(0)'}
          >
            🚀 Get Started Today
          </button>
        </div>
      </section>

      {/* Footer */}
      <footer style={{
        backgroundColor: '#2c3e50',
        color: 'white',
        padding: '40px 20px',
        textAlign: 'center'
      }}>
        <div style={{ maxWidth: '1000px', margin: '0 auto' }}>
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
            gap: '30px',
            marginBottom: '30px'
          }}>
            <div>
              <h3 style={{ marginBottom: '15px' }}>🤝 VolunteerSync</h3>
              <p style={{ color: '#bdc3c7', fontSize: '0.9rem' }}>
                Connecting volunteers with meaningful opportunities since 2024.
              </p>
            </div>
            <div>
              <h4 style={{ marginBottom: '15px' }}>For Volunteers</h4>
              <ul style={{ listStyle: 'none', padding: 0 }}>
                <li style={{ marginBottom: '8px' }}><a href="#" style={{ color: '#bdc3c7', textDecoration: 'none' }}>Find Opportunities</a></li>
                <li style={{ marginBottom: '8px' }}><a href="#" style={{ color: '#bdc3c7', textDecoration: 'none' }}>Create Profile</a></li>
                <li style={{ marginBottom: '8px' }}><a href="#" style={{ color: '#bdc3c7', textDecoration: 'none' }}>Track Hours</a></li>
              </ul>
            </div>
            <div>
              <h4 style={{ marginBottom: '15px' }}>For Organizations</h4>
              <ul style={{ listStyle: 'none', padding: 0 }}>
                <li style={{ marginBottom: '8px' }}><a href="#" style={{ color: '#bdc3c7', textDecoration: 'none' }}>Post Opportunities</a></li>
                <li style={{ marginBottom: '8px' }}><a href="#" style={{ color: '#bdc3c7', textDecoration: 'none' }}>Manage Volunteers</a></li>
                <li style={{ marginBottom: '8px' }}><a href="#" style={{ color: '#bdc3c7', textDecoration: 'none' }}>Analytics</a></li>
              </ul>
            </div>
            <div>
              <h4 style={{ marginBottom: '15px' }}>Support</h4>
              <ul style={{ listStyle: 'none', padding: 0 }}>
                <li style={{ marginBottom: '8px' }}><a href="#" style={{ color: '#bdc3c7', textDecoration: 'none' }}>Help Center</a></li>
                <li style={{ marginBottom: '8px' }}><a href="#" style={{ color: '#bdc3c7', textDecoration: 'none' }}>Contact Us</a></li>
                <li style={{ marginBottom: '8px' }}><a href="#" style={{ color: '#bdc3c7', textDecoration: 'none' }}>Privacy Policy</a></li>
              </ul>
            </div>
          </div>
          <div style={{ 
            borderTop: '1px solid #34495e', 
            paddingTop: '20px',
            color: '#bdc3c7',
            fontSize: '0.9rem'
          }}>
            <p>&copy; 2024 VolunteerSync. Made with ❤️ for communities everywhere.</p>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default HomePage;