import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getCurrentUser, isLoggedIn, logout } from '../services/authService';
import './Navbar.css';

const Navbar = () => {
  const [isScrolled, setIsScrolled] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [userMenuOpen, setUserMenuOpen] = useState(false);
  const [user, setUser] = useState(null);
  const [loggedIn, setLoggedIn] = useState(false);
  const navigate = useNavigate();

  // Check login status and get user data
  useEffect(() => {
    const checkAuthStatus = () => {
      const loginStatus = isLoggedIn();
      setLoggedIn(loginStatus);
      
      if (loginStatus) {
        const userData = getCurrentUser();
        setUser(userData);
      } else {
        setUser(null);
      }
    };

    // Check on mount
    checkAuthStatus();

    // Check on storage changes (when user logs in/out in another tab)
    const handleStorageChange = () => {
      checkAuthStatus();
    };

    window.addEventListener('storage', handleStorageChange);
    
    // Also check periodically in case of manual localStorage changes
    const interval = setInterval(checkAuthStatus, 1000);

    return () => {
      window.removeEventListener('storage', handleStorageChange);
      clearInterval(interval);
    };
  }, []);

  const handleJoinNow = () => {
    navigate('/login', { state: { mode: 'signup' } });
  };

  const handleSignIn = () => {
    navigate('/login', { state: { mode: 'login' } });
  };

  const handleLogoClick = () => {
    navigate('/');
  };

  const handleHowItWorksClick = () => {
    navigate('/how-it-works');
    setMobileMenuOpen(false);
  };

  const handleOrganizationsClick = () => {
    navigate('/find-organizations');
    setMobileMenuOpen(false);
  };

  const handleFindEventsClick = () => {
    // TODO: Navigate to events page when it's created
    navigate('/find-events');
    setMobileMenuOpen(false);
  };

  const handleAboutClick = () => {
    // TODO: Navigate to about page when it's created
    navigate('/about');
    setMobileMenuOpen(false);
  };

  const handleDashboard = () => {
    navigate('/dashboard');
    setUserMenuOpen(false);
    setMobileMenuOpen(false);
  };

   const handleSettingsClick = () => {
    navigate('/settings');
    setUserMenuOpen(false);
    setMobileMenuOpen(false);
  };

  const handleProfile = () => {
    navigate('/profile');
    setUserMenuOpen(false);
    setMobileMenuOpen(false);
  };

  const handleLogout = () => {
    logout();
    setUser(null);
    setLoggedIn(false);
    setUserMenuOpen(false);
    setMobileMenuOpen(false);
    navigate('/');
  };

  useEffect(() => {
    const handleScroll = () => {
      setIsScrolled(window.scrollY > 10);
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const scrollToSection = (sectionId) => {
    const element = document.getElementById(sectionId);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' });
    }
    setMobileMenuOpen(false);
  };

  // Close user menu when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (userMenuOpen && !event.target.closest('.user-menu-container')) {
        setUserMenuOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [userMenuOpen]);

  const getUserDisplayName = () => {
    if (!user) return 'User';
    
    // For organizations, show organization name
    if (user.userType === 'ORGANIZATION' && user.organizationName) {
      return user.organizationName;
    }
    
    // For volunteers, show first + last name
    if (user.userType === 'VOLUNTEER' && user.firstName && user.lastName) {
      return `${user.firstName} ${user.lastName}`;
    }
    
    // Fallback to email
    return user.email || 'User';
  };

  const getUserInitials = () => {
    if (!user) return 'U';
    
    // For organizations, use organization name initials
    if (user.userType === 'ORGANIZATION' && user.organizationName) {
      const words = user.organizationName.split(' ').filter(word => word.length > 0);
      if (words.length >= 2) {
        return `${words[0][0]}${words[1][0]}`.toUpperCase();
      } else if (words.length === 1) {
        return words[0].substring(0, 2).toUpperCase();
      }
    }
    
    // For volunteers, use first + last name initials
    if (user.userType === 'VOLUNTEER' && user.firstName && user.lastName) {
      return `${user.firstName[0]}${user.lastName[0]}`.toUpperCase();
    }
    
    // Fallback to email initial
    if (user.email) {
      return user.email[0].toUpperCase();
    }
    
    return 'U';
  };

  const getUserShortName = () => {
    if (!user) return 'User';
    
    // For organizations, show shortened org name (first word or abbreviated)
    if (user.userType === 'ORGANIZATION' && user.organizationName) {
      const words = user.organizationName.split(' ');
      if (words[0].length > 12) {
        return words[0].substring(0, 12) + '...';
      }
      return words[0];
    }
    
    // For volunteers, show first name
    if (user.userType === 'VOLUNTEER' && user.firstName) {
      return user.firstName;
    }
    
    return user.email?.split('@')[0] || 'User';
  };

  const getUserTypeDisplay = () => {
    if (!user) return '';
    
    switch (user.userType) {
      case 'ORGANIZATION':
        return '🏢 Organization';
      case 'VOLUNTEER':
        return '🙋‍♀️ Volunteer';
      default:
        return user.userType;
    }
  };

  return (
    <nav className={`navbar ${isScrolled ? 'navbar-scrolled' : ''}`}>
      <div className="navbar-container">
        {/* Logo */}
        <div className="navbar-logo" onClick={handleLogoClick}>
          <span className="navbar-logo-icon">🤝</span>
          <span className="navbar-logo-text">VolunteerSync</span>
        </div>

        {/* Desktop Navigation */}
        <div className="navbar-menu">
          <button 
            className="navbar-link" 
            onClick={handleHowItWorksClick}
          >
            How It Works
          </button>
          <button 
            className="navbar-link" 
            onClick={handleOrganizationsClick}
          >
            Organizations
          </button>
          <button 
            className="navbar-link" 
            onClick={handleFindEventsClick}
          >
            Find Events
          </button>
          <button 
            className="navbar-link" 
            onClick={handleAboutClick}
          >
            About
          </button>
          
          {loggedIn ? (
            // Logged in user menu
            <div className="user-menu-container">
              <button 
                className="user-profile-btn"
                onClick={() => setUserMenuOpen(!userMenuOpen)}
                aria-label="User menu"
              >
                <div className="user-avatar">
                  {user?.profilePicture ? (
                    <img 
                      src={user.profilePicture} 
                      alt="Profile" 
                      className="user-avatar-img"
                    />
                  ) : (
                    <div className="user-avatar-placeholder">
                      {getUserInitials()}
                    </div>
                  )}
                </div>
                <span className="user-name">{getUserShortName()}</span>
                <svg 
                  className={`dropdown-icon ${userMenuOpen ? 'rotated' : ''}`} 
                  width="16" 
                  height="16" 
                  viewBox="0 0 24 24" 
                  fill="none" 
                  stroke="currentColor" 
                  strokeWidth="2"
                >
                  <polyline points="6,9 12,15 18,9"></polyline>
                </svg>
              </button>

              {/* User Dropdown Menu */}
              {userMenuOpen && (
                <div className="user-dropdown">
                  <div className="user-dropdown-header">
                    <div className="user-dropdown-avatar">
                      {user?.profilePicture ? (
                        <img 
                          src={user.profilePicture} 
                          alt="Profile" 
                          className="user-avatar-img"
                        />
                      ) : (
                        <div className="user-avatar-placeholder">
                          {getUserInitials()}
                        </div>
                      )}
                    </div>
                    <div className="user-dropdown-info">
                      <div className="user-dropdown-name">{getUserDisplayName()}</div>
                      <div className="user-dropdown-email">{user?.email}</div>
                      <div className="user-dropdown-type">
                        {getUserTypeDisplay()}
                      </div>
                    </div>
                  </div>
                  
                  <div className="user-dropdown-divider"></div>
                  
                  <button className="user-dropdown-item" onClick={handleDashboard}>
                    <span className="dropdown-item-icon">📊</span>
                    Dashboard
                  </button>
                  <button className="user-dropdown-item" onClick={handleProfile}>
                    <span className="dropdown-item-icon">👤</span>
                    Profile
                  </button>
                  <button className="user-dropdown-item" onClick={handleSettingsClick}>
                    <span className="dropdown-item-icon">⚙️</span>
                    Settings
                  </button>
                  
                  <div className="user-dropdown-divider"></div>
                  
                  <button className="user-dropdown-item logout" onClick={handleLogout}>
                    <span className="dropdown-item-icon">🚪</span>
                    Sign Out
                  </button>
                </div>
              )}
            </div>
          ) : (
            // Not logged in - show auth buttons
            <>
              <button className="navbar-link" onClick={handleSignIn}>
                Sign In
              </button>
              <button className="navbar-cta" onClick={handleJoinNow}>
                Join Now
              </button>
            </>
          )}
        </div>

        {/* Mobile Menu Toggle */}
        <button
          className="navbar-mobile-toggle"
          onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          aria-label="Toggle mobile menu"
        >
          <span className={`hamburger-line ${mobileMenuOpen ? 'active' : ''}`}></span>
          <span className={`hamburger-line ${mobileMenuOpen ? 'active' : ''}`}></span>
          <span className={`hamburger-line ${mobileMenuOpen ? 'active' : ''}`}></span>
        </button>
      </div>

      {/* Mobile Menu */}
      <div className={`navbar-mobile-menu ${mobileMenuOpen ? 'active' : ''}`}>
        {loggedIn && (
          <>
            {/* Mobile User Info */}
            <div className="mobile-user-info">
              <div className="mobile-user-avatar">
                {user?.profilePicture ? (
                  <img 
                    src={user.profilePicture} 
                    alt="Profile" 
                    className="user-avatar-img"
                  />
                ) : (
                  <div className="user-avatar-placeholder">
                    {getUserInitials()}
                  </div>
                )}
              </div>
              <div className="mobile-user-details">
                <div className="mobile-user-name">{getUserDisplayName()}</div>
                <div className="mobile-user-type">
                  {getUserTypeDisplay()}
                </div>
              </div>
            </div>
            
            <div className="mobile-menu-divider"></div>
            
            <button className="navbar-mobile-link" onClick={handleDashboard}>
              📊 Dashboard
            </button>
            <button className="navbar-mobile-link" onClick={handleProfile}>
              👤 Profile
            </button>
            
            <div className="mobile-menu-divider"></div>
          </>
        )}
        
        <button 
          className="navbar-mobile-link" 
          onClick={handleHowItWorksClick}
        >
          How It Works
        </button>
        <button 
          className="navbar-mobile-link" 
          onClick={handleOrganizationsClick}
        >
          Find Organizations
        </button>
        <button 
          className="navbar-mobile-link" 
          onClick={handleFindEventsClick}
        >
          Find Events
        </button>
        <button 
          className="navbar-mobile-link" 
          onClick={handleAboutClick}
        >
          About
        </button>

        {loggedIn ? (
          <button className="navbar-mobile-cta logout" onClick={handleLogout}>
            🚪 Sign Out
          </button>
        ) : (
          <>
            <button className="navbar-mobile-cta" onClick={handleJoinNow}>
              Join Now
            </button>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;