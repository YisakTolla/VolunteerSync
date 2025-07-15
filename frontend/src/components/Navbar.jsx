import React, { useState, useEffect } from 'react';
import './Navbar.css';

const Navbar = ({ onNavigateToLogin, onNavigateToHome }) => {
  const [isScrolled, setIsScrolled] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleJoinNow = () => {
    onNavigateToLogin({ mode: 'signup' });
  };

  const handleLogoClick = () => {
    if (onNavigateToHome) {
      onNavigateToHome();
    } else {
      // Fallback for when on home page - scroll to top
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
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
            onClick={() => scrollToSection('features')}
          >
            How It Works
          </button>
          <button 
            className="navbar-link" 
            onClick={() => scrollToSection('organizations')}
          >
            Organizations
          </button>
          <a href="#" className="navbar-link">Find Events</a>
          <a href="#" className="navbar-link">About</a>
          
          <button className="navbar-cta" onClick={handleJoinNow}>
            Join Now
          </button>
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
        <button 
          className="navbar-mobile-link" 
          onClick={() => scrollToSection('features')}
        >
          How It Works
        </button>
        <button 
          className="navbar-mobile-link" 
          onClick={() => scrollToSection('organizations')}
        >
          Organizations
        </button>
        <a href="#" className="navbar-mobile-link">Find Events</a>
        <a href="#" className="navbar-mobile-link">About</a>
        <button className="navbar-mobile-cta" onClick={handleJoinNow}>
          Join Now
        </button>
      </div>
    </nav>
  );
};

export default Navbar;