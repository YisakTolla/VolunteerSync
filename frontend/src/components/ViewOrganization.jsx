import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { 
  ArrowLeft, 
  MapPin, 
  Globe, 
  Phone, 
  Users, 
  Calendar, 
  Award, 
  Star, 
  ExternalLink, 
  Mail,
  Building,
  Target,
  Heart,
  CheckCircle,
  DollarSign,
  Languages
} from 'lucide-react';
import './ViewOrganization.css';
import findOrganizationService from '../services/findOrganizationService';

const ViewOrganization = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [organization, setOrganization] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  console.log('ViewOrganization component mounted with ID:', id);

  useEffect(() => {
    if (id) {
      loadOrganization();
    } else {
      setError('No organization ID provided');
      setLoading(false);
    }
  }, [id]);

  const loadOrganization = async () => {
    try {
      console.log('Loading organization with ID:', id);
      setLoading(true);
      setError(null);
      
      const orgData = await findOrganizationService.findOrganizationById(id);
      console.log('Organization data received:', orgData);
      
      setOrganization(orgData);
    } catch (err) {
      console.error('Failed to load organization:', err);
      setError(`Failed to load organization: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleBack = () => {
    navigate('/find-organizations');
  };

  const handleVisitWebsite = () => {
    if (organization?.website) {
      window.open(organization.website, '_blank', 'noopener,noreferrer');
    }
  };

  const getCategoryClass = (category) => {
    if (!category) return '';
    
    const categoryLower = category.toLowerCase().replace(/[^\w\s]/g, '').replace(/\s+/g, '-');
    
    const categoryMap = {
      'education': 'education',
      'environment': 'environment', 
      'healthcare': 'healthcare',
      'animal-welfare': 'animal-welfare',
      'community-service': 'community-service',
      'human-services': 'human-services',
      'arts-culture': 'arts-culture',
      'arts--culture': 'arts-culture',
      'youth-development': 'youth-development',
      'senior-services': 'senior-services',
      'hunger--homelessness': 'hunger-homelessness',
      'hunger-homelessness': 'hunger-homelessness',
      'disaster-relief': 'disaster-relief',
      'international': 'international',
      'sports--recreation': 'sports-recreation',
      'sports-recreation': 'sports-recreation',
      'mental-health': 'mental-health',
      'veterans': 'veterans',
      'womens-issues': 'womens-issues',
      'children--families': 'children-families',
      'children-families': 'children-families',
      'disability-services': 'disability-services',
      'religious': 'religious',
      'political': 'political',
      'lgbtq': 'lgbtq',
      'technology': 'technology',
      'research--advocacy': 'research-advocacy',
      'research-advocacy': 'research-advocacy',
      'public-safety': 'public-safety'
    };
    
    return categoryMap[categoryLower] || '';
  };

  const formatFoundedYear = (year) => {
    if (!year) return 'N/A';
    return year.toString();
  };

  const getOrganizationSize = (employeeCount) => {
    if (!employeeCount) return 'Unknown';
    if (employeeCount <= 50) return 'Small (1-50 employees)';
    if (employeeCount <= 200) return 'Medium (51-200 employees)';
    if (employeeCount <= 1000) return 'Large (201-1000 employees)';
    return 'Enterprise (1000+ employees)';
  };

  const getCategoriesArray = (categories, primaryCategory) => {
    const categoryArray = [];
    
    if (primaryCategory) {
      categoryArray.push(primaryCategory);
    }
    
    if (categories) {
      const additionalCategories = categories.split(',').map(cat => cat.trim());
      additionalCategories.forEach(cat => {
        if (cat && !categoryArray.includes(cat)) {
          categoryArray.push(cat);
        }
      });
    }
    
    return categoryArray;
  };

  const getLanguagesArray = (languagesSupported) => {
    if (!languagesSupported) return ['English'];
    return languagesSupported.split(',').map(lang => lang.trim());
  };

  const getServicesArray = (services) => {
    if (!services) return [];
    return services.split(',').map(service => service.trim());
  };

  const getCausesArray = (causes) => {
    if (!causes) return [];
    return causes.split(',').map(cause => cause.trim());
  };

  // Show loading state
  if (loading) {
    return (
      <div className="view-organization-page">
        <div className="view-organization-loading">
          <div className="view-organization-loading-spinner"></div>
          <p>Loading organization details...</p>
        </div>
      </div>
    );
  }

  // Show error state
  if (error) {
    return (
      <div className="view-organization-page">
        <div className="view-organization-error">
          <p>{error}</p>
          <button onClick={loadOrganization} className="view-organization-retry-btn">
            Try Again
          </button>
          <button onClick={handleBack} className="view-organization-back-btn">
            Back to Organizations
          </button>
        </div>
      </div>
    );
  }

  // Show not found state
  if (!organization) {
    return (
      <div className="view-organization-page">
        <div className="view-organization-error">
          <p>Organization not found.</p>
          <button onClick={handleBack} className="view-organization-back-btn">
            Back to Organizations
          </button>
        </div>
      </div>
    );
  }

  console.log('Rendering organization:', organization);

  // Main render
  try {
    return (
      <div className="view-organization-page">
        <div className="view-organization-container">
          {/* Back Button */}
          <button onClick={handleBack} className="view-organization-back-button">
            <ArrowLeft />
            Back to Organizations
          </button>

          {/* Organization Header */}
          <div className="view-organization-header">
            {organization.coverImageUrl && (
              <div className="view-organization-cover">
                <img src={organization.coverImageUrl} alt={`${organization.organizationName} cover`} />
              </div>
            )}
            
            <div className="view-organization-header-content">
              <div className="view-organization-header-main">
                <div className="view-organization-profile-section">
                  <div className="view-organization-profile-image">
                    {organization.profileImageUrl ? (
                      <img src={organization.profileImageUrl} alt={organization.organizationName || 'Organization'} />
                    ) : (
                      <div className="view-organization-profile-placeholder">
                        <Users />
                      </div>
                    )}
                    {organization.isVerified && (
                      <div className="view-organization-verified-badge">
                        <Star />
                      </div>
                    )}
                  </div>
                  
                  <div className="view-organization-header-info">
                    <div className="view-organization-categories">
                      {getCategoriesArray(organization.categories, organization.primaryCategory).map((category, index) => (
                        <span key={index} className={`view-organization-category ${getCategoryClass(category)}`}>
                          {category}
                        </span>
                      ))}
                    </div>
                    
                    <h1 className="view-organization-title">{organization.organizationName || 'Organization Name Not Available'}</h1>
                    
                    <div className="view-organization-meta">
                      <div className="view-organization-meta-item">
                        <MapPin />
                        <span>
                          {organization.city && organization.state 
                            ? `${organization.city}, ${organization.state}, ${organization.country || 'United States'}`
                            : organization.country || 'Location not specified'
                          }
                        </span>
                      </div>
                      
                      <div className="view-organization-meta-item">
                        <Building />
                        <span>{organization.organizationType || 'Organization'}</span>
                      </div>
                      
                      <div className="view-organization-meta-item">
                        <Users />
                        <span>{getOrganizationSize(organization.employeeCount)}</span>
                      </div>
                      
                      {organization.foundedYear && (
                        <div className="view-organization-meta-item">
                          <Calendar />
                          <span>Founded {formatFoundedYear(organization.foundedYear)}</span>
                        </div>
                      )}
                    </div>
                  </div>
                </div>

                <div className="view-organization-actions">
                  <button className="view-organization-btn primary">
                    <Heart />
                    Follow Organization
                  </button>
                  {organization.website && (
                    <button 
                      className="view-organization-btn secondary"
                      onClick={handleVisitWebsite}
                    >
                      <ExternalLink />
                      Visit Website
                    </button>
                  )}
                </div>
              </div>
            </div>
          </div>

          {/* Organization Content */}
          <div className="view-organization-content">
            <div className="view-organization-main">
              {/* About Section */}
              <div className="view-organization-section">
                <h2 className="view-organization-section-title">About This Organization</h2>
                <div className="view-organization-section-content">
                  <p className="view-organization-description">
                    {organization.description || 'No description available.'}
                  </p>
                  
                  {organization.missionStatement && (
                    <div className="view-organization-mission">
                      <h3>Mission Statement</h3>
                      <p>{organization.missionStatement}</p>
                    </div>
                  )}
                </div>
              </div>

              {/* Services Section */}
              {getServicesArray(organization.services).length > 0 && (
                <div className="view-organization-section">
                  <h2 className="view-organization-section-title">Services Offered</h2>
                  <div className="view-organization-section-content">
                    <div className="view-organization-tags">
                      {getServicesArray(organization.services).map((service, index) => (
                        <span key={index} className="view-organization-tag">
                          <CheckCircle />
                          {service}
                        </span>
                      ))}
                    </div>
                  </div>
                </div>
              )}

              {/* Causes Section */}
              {getCausesArray(organization.causes).length > 0 && (
                <div className="view-organization-section">
                  <h2 className="view-organization-section-title">Causes We Support</h2>
                  <div className="view-organization-section-content">
                    <div className="view-organization-tags">
                      {getCausesArray(organization.causes).map((cause, index) => (
                        <span key={index} className="view-organization-tag">
                          <Target />
                          {cause}
                        </span>
                      ))}
                    </div>
                  </div>
                </div>
              )}

              {/* Funding Section */}
              {(organization.fundingGoal || organization.fundingRaised) && (
                <div className="view-organization-section">
                  <h2 className="view-organization-section-title">Funding Information</h2>
                  <div className="view-organization-section-content">
                    <div className="view-organization-funding">
                      {organization.fundingRaised && (
                        <div className="view-organization-funding-item">
                          <DollarSign />
                          <div>
                            <span className="funding-label">Funds Raised</span>
                            <span className="funding-amount">${organization.fundingRaised.toLocaleString()}</span>
                          </div>
                        </div>
                      )}
                      
                      {organization.fundingGoal && (
                        <div className="view-organization-funding-item">
                          <Target />
                          <div>
                            <span className="funding-label">Funding Goal</span>
                            <span className="funding-amount">${organization.fundingGoal.toLocaleString()}</span>
                          </div>
                        </div>
                      )}
                    </div>
                    
                    {organization.fundingGoal && organization.fundingRaised && (
                      <div className="view-organization-progress">
                        <div className="progress-bar">
                          <div 
                            className="progress-fill" 
                            style={{ width: `${Math.min((organization.fundingRaised / organization.fundingGoal) * 100, 100)}%` }}
                          ></div>
                        </div>
                        <span className="progress-text">
                          {Math.round((organization.fundingRaised / organization.fundingGoal) * 100)}% of goal reached
                        </span>
                      </div>
                    )}
                  </div>
                </div>
              )}
            </div>

            {/* Sidebar */}
            <div className="view-organization-sidebar">
              {/* Contact Information */}
              <div className="view-organization-sidebar-card">
                <h3 className="view-organization-sidebar-title">Contact Information</h3>
                <div className="view-organization-contact">
                  {organization.website && (
                    <div className="view-organization-contact-item">
                      <Globe />
                      <a href={organization.website} target="_blank" rel="noopener noreferrer">
                        Visit Website
                      </a>
                    </div>
                  )}
                  
                  {organization.phoneNumber && (
                    <div className="view-organization-contact-item">
                      <Phone />
                      <a href={`tel:${organization.phoneNumber}`}>
                        {organization.phoneNumber}
                      </a>
                    </div>
                  )}
                  
                  {organization.user?.email && (
                    <div className="view-organization-contact-item">
                      <Mail />
                      <a href={`mailto:${organization.user.email}`}>
                        Contact via Email
                      </a>
                    </div>
                  )}
                  
                  {organization.address && (
                    <div className="view-organization-contact-item">
                      <MapPin />
                      <span>
                        {organization.address}
                        {organization.city && `, ${organization.city}`}
                        {organization.state && `, ${organization.state}`}
                        {organization.zipCode && ` ${organization.zipCode}`}
                      </span>
                    </div>
                  )}
                </div>
              </div>

              {/* Organization Stats */}
              <div className="view-organization-sidebar-card">
                <h3 className="view-organization-sidebar-title">Organization Stats</h3>
                <div className="view-organization-stats">
                  <div className="view-organization-stat">
                    <Award />
                    <div>
                      <span className="stat-number">{organization.totalEventsHosted || 0}</span>
                      <span className="stat-label">Events Hosted</span>
                    </div>
                  </div>
                  
                  <div className="view-organization-stat">
                    <Users />
                    <div>
                      <span className="stat-number">{organization.totalVolunteersServed || 0}</span>
                      <span className="stat-label">Volunteers Served</span>
                    </div>
                  </div>
                  
                  {organization.foundedYear && (
                    <div className="view-organization-stat">
                      <Calendar />
                      <div>
                        <span className="stat-number">
                          {new Date().getFullYear() - organization.foundedYear}
                        </span>
                        <span className="stat-label">Years Active</span>
                      </div>
                    </div>
                  )}
                </div>
              </div>

              {/* Additional Information */}
              <div className="view-organization-sidebar-card">
                <h3 className="view-organization-sidebar-title">Additional Information</h3>
                <div className="view-organization-additional">
                  {getLanguagesArray(organization.languagesSupported).length > 0 && (
                    <div className="view-organization-additional-item">
                      <Languages />
                      <div>
                        <span className="additional-label">Languages Supported</span>
                        <span className="additional-value">
                          {getLanguagesArray(organization.languagesSupported).join(', ')}
                        </span>
                      </div>
                    </div>
                  )}
                  
                  {organization.taxExemptStatus && (
                    <div className="view-organization-additional-item">
                      <CheckCircle />
                      <div>
                        <span className="additional-label">Tax Status</span>
                        <span className="additional-value">{organization.taxExemptStatus}</span>
                      </div>
                    </div>
                  )}
                  
                  {organization.verificationLevel && (
                    <div className="view-organization-additional-item">
                      <Star />
                      <div>
                        <span className="additional-label">Verification</span>
                        <span className="additional-value">{organization.verificationLevel}</span>
                      </div>
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  } catch (renderError) {
    console.error('Error rendering ViewOrganization:', renderError);
    return (
      <div className="view-organization-page">
        <div className="view-organization-error">
          <p>Error rendering organization details: {renderError.message}</p>
          <button onClick={handleBack} className="view-organization-back-btn">
            Back to Organizations
          </button>
        </div>
      </div>
    );
  }
};

export default ViewOrganization;