// frontend/src/services/findOrganizationService.js

const API_BASE_URL = 'http://localhost:8080/api';

class FindOrganizationService {
  
  /**
   * Find all organizations (using multiple fallback strategies)
   * Now using the correct /api/organizations endpoint
   */
  async findAllOrganizations() {
    try {
      console.log(`Trying primary endpoint: ${API_BASE_URL}/organizations`);
      const response = await fetch(`${API_BASE_URL}/organizations`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      });
      
      if (response.ok) {
        const data = await response.json();
        console.log(`✅ Success with primary endpoint`, data);
        return Array.isArray(data) ? data : [];
      } else {
        console.log(`❌ Primary endpoint failed: ${response.status}: ${response.statusText}`);
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
    } catch (error) {
      console.error('Error fetching all organizations:', error);
      
      // Fallback strategies
      const fallbackEndpoints = [
        'organizations/verified',
        'organizations/non-profit',
        'organizations/most-active',
        'organization-profiles/verified'
      ];

      for (const endpoint of fallbackEndpoints) {
        try {
          console.log(`🔄 Trying fallback: ${API_BASE_URL}/${endpoint}`);
          const response = await fetch(`${API_BASE_URL}/${endpoint}`, {
            method: 'GET',
            headers: {
              'Content-Type': 'application/json',
              'Accept': 'application/json'
            }
          });
          
          if (response.ok) {
            const data = await response.json();
            console.log(`✅ Success with fallback: ${endpoint}`, data);
            return Array.isArray(data) ? data : [];
          }
        } catch (fallbackError) {
          console.log(`❌ Fallback failed: ${endpoint}`, fallbackError.message);
          continue;
        }
      }

      // Final fallback - return empty array to prevent crashes
      console.warn('⚠️ All organization endpoints failed, returning empty array');
      return [];
    }
  }

  /**
   * Find organization by ID - ADDED METHOD
   */
  async findOrganizationById(id) {
    try {
      console.log(`Fetching organization with ID: ${id}`);
      const response = await fetch(`${API_BASE_URL}/organizations/${id}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      });
      
      if (response.ok) {
        const data = await response.json();
        console.log(`✅ Success fetching organization:`, data);
        return data;
      } else if (response.status === 404) {
        throw new Error('Organization not found');
      } else {
        console.log(`❌ Failed to fetch organization: ${response.status}: ${response.statusText}`);
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
    } catch (error) {
      console.error('Error fetching organization by ID:', error);
      
      // Fallback: try the organization-profiles endpoint
      try {
        console.log(`🔄 Trying fallback endpoint: ${API_BASE_URL}/organization-profiles/${id}`);
        const response = await fetch(`${API_BASE_URL}/organization-profiles/${id}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          }
        });
        
        if (response.ok) {
          const data = await response.json();
          console.log(`✅ Success with fallback endpoint:`, data);
          return data;
        } else if (response.status === 404) {
          throw new Error('Organization not found');
        }
      } catch (fallbackError) {
        console.error('Fallback also failed:', fallbackError);
      }
      
      throw error;
    }
  }

  /**
   * Find organizations with pagination (using advanced search with empty criteria)
   */
  async findAllOrganizationsWithPagination(page = 0, size = 10, sortBy = 'organizationName', sortDirection = 'asc') {
    try {
      // Use POST endpoint for advanced search with empty criteria to get all organizations
      const response = await fetch(`${API_BASE_URL}/organization-profiles/search?page=${page}&size=${size}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          searchTerm: '',
          category: '',
          country: '',
          organizationSize: '',
          isVerified: null
        })
      });
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching paginated organizations:', error);
      throw error;
    }
  }

  /**
   * Search organizations by name
   */
  async findOrganizationsByName(name) {
    try {
      const params = new URLSearchParams({ q: name });
      const response = await fetch(`${API_BASE_URL}/organization-profiles/search/name?${params}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error searching organizations by name:', error);
      throw error;
    }
  }

  /**
   * Find organizations by category
   */
  async findOrganizationsByCategory(category) {
    try {
      const response = await fetch(`${API_BASE_URL}/organization-profiles/category/${encodeURIComponent(category)}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error finding organizations by category:', error);
      throw error;
    }
  }

  /**
   * Find organizations by type
   */
  async findOrganizationsByType(organizationType) {
    try {
      const response = await fetch(`${API_BASE_URL}/organization-profiles/type/${encodeURIComponent(organizationType)}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error finding organizations by type:', error);
      throw error;
    }
  }

  /**
   * Find organizations by location
   */
  async findOrganizationsByLocation(city, state) {
    try {
      let location = '';
      if (city && state) {
        location = `${city}, ${state}`;
      } else if (city) {
        location = city;
      } else if (state) {
        location = state;
      }
      
      const params = new URLSearchParams({ q: location });
      const response = await fetch(`${API_BASE_URL}/organization-profiles/search/location?${params}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error finding organizations by location:', error);
      throw error;
    }
  }

  /**
   * Find verified organizations only
   */
  async findVerifiedOrganizations() {
    try {
      const response = await fetch(`${API_BASE_URL}/organization-profiles/verified`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching verified organizations:', error);
      throw error;
    }
  }

  /**
   * Find organizations by employee count range (using size categories)
   */
  async findOrganizationsByEmployeeCount(minEmployees, maxEmployees) {
    try {
      let size = '';
      if (maxEmployees <= 50) {
        size = 'Small (1-50)';
      } else if (maxEmployees <= 200) {
        size = 'Medium (51-200)';
      } else if (maxEmployees <= 1000) {
        size = 'Large (201-1000)';
      } else {
        size = 'Enterprise (1000+)';
      }
      
      const response = await fetch(`${API_BASE_URL}/organization-profiles/size/${encodeURIComponent(size)}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error finding organizations by employee count:', error);
      throw error;
    }
  }

  /**
   * Search organizations with multiple filters
   */
  async searchOrganizations(filters = {}) {
    try {
      // Convert frontend filters to backend format
      const searchRequest = {
        searchTerm: filters.name || '',
        category: filters.category || '',
        country: filters.country || '',
        organizationSize: filters.organizationSize || '',
        isVerified: filters.verified
      };
      
      const response = await fetch(`${API_BASE_URL}/organization-profiles/search`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(searchRequest)
      });
      
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error searching organizations:', error);
      throw error;
    }
  }

  /**
   * Get organizations sorted by name (use search with empty criteria)
   */
  async findOrganizationsSortedByName() {
    try {
      return await this.findAllOrganizations();
    } catch (error) {
      console.error('Error fetching organizations sorted by name:', error);
      throw error;
    }
  }

  /**
   * Get newest organizations (use recently updated)
   */
  async findNewestOrganizations() {
    try {
      const response = await fetch(`${API_BASE_URL}/organization-profiles/recently-updated?days=30`);
      if (!response.ok) {
        // Fallback to verified organizations
        return await this.findVerifiedOrganizations();
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching newest organizations:', error);
      throw error;
    }
  }

  /**
   * Get most active organizations
   */
  async findMostActiveOrganizations() {
    try {
      const response = await fetch(`${API_BASE_URL}/organization-profiles/most-active?limit=20`);
      if (!response.ok) {
        // Fallback to verified organizations
        return await this.findVerifiedOrganizations();
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching most active organizations:', error);
      throw error;
    }
  }

  /**
   * Get highest impact organizations
   */
  async findHighestImpactOrganizations() {
    try {
      const response = await fetch(`${API_BASE_URL}/organization-profiles/top-impact?limit=20`);
      if (!response.ok) {
        // Fallback to verified organizations
        return await this.findVerifiedOrganizations();
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching highest impact organizations:', error);
      throw error;
    }
  }

  /**
   * Find non-profit organizations
   */
  async findNonProfitOrganizations() {
    try {
      const response = await fetch(`${API_BASE_URL}/organization-profiles/non-profit`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching non-profit organizations:', error);
      throw error;
    }
  }

  /**
   * Find highly verified organizations
   */
  async findHighlyVerifiedOrganizations() {
    try {
      const response = await fetch(`${API_BASE_URL}/organization-profiles/highly-verified`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching highly verified organizations:', error);
      throw error;
    }
  }

  /**
   * Find international organizations
   */
  async findInternationalOrganizations() {
    try {
      const response = await fetch(`${API_BASE_URL}/organization-profiles/international`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error fetching international organizations:', error);
      throw error;
    }
  }

  /**
   * Get organization statistics (placeholder - no direct endpoint available)
   */
  async getOrganizationStats() {
    try {
      // Since there's no stats endpoint, we'll create basic stats from available data
      const [verified, nonProfit, international] = await Promise.all([
        this.findVerifiedOrganizations(),
        this.findNonProfitOrganizations(),
        this.findInternationalOrganizations()
      ]);

      return {
        total: verified.length + nonProfit.length, // Rough estimate
        verified: verified.length,
        nonProfit: nonProfit.length,
        international: international.length,
        lastUpdated: new Date().toISOString()
      };
    } catch (error) {
      console.error('Error fetching organization stats:', error);
      throw error;
    }
  }

  /**
   * Check if organization name exists (placeholder)
   */
  async checkOrganizationNameExists(name) {
    try {
      const organizations = await this.findOrganizationsByName(name);
      return organizations.length > 0;
    } catch (error) {
      console.error('Error checking organization name:', error);
      return false;
    }
  }

  /**
   * Advanced search with multiple criteria
   */
  async advancedSearch({
    name,
    category,
    type,
    city,
    state,
    country,
    verified,
    verificationLevel,
    minEmployees,
    maxEmployees,
    minFoundedYear,
    maxFoundedYear,
    language,
    sortBy = 'organizationName',
    sortDirection = 'asc',
    page = 0,
    size = 20
  } = {}) {
    try {
      // Convert to backend search format
      const searchRequest = {
        searchTerm: name || '',
        category: category || '',
        country: country || '',
        organizationSize: this.convertEmployeeCountToSize(minEmployees, maxEmployees),
        isVerified: verified
      };
      
      const response = await fetch(`${API_BASE_URL}/organization-profiles/search?page=${page}&size=${size}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(searchRequest)
      });
      
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      return await response.json();
    } catch (error) {
      console.error('Error performing advanced search:', error);
      throw error;
    }
  }

  /**
   * Helper method to convert employee count range to organization size
   */
  convertEmployeeCountToSize(minEmployees, maxEmployees) {
    if (!minEmployees && !maxEmployees) return '';
    
    if (maxEmployees <= 50) return 'Small (1-50)';
    if (maxEmployees <= 200) return 'Medium (51-200)';
    if (maxEmployees <= 1000) return 'Large (201-1000)';
    return 'Enterprise (1000+)';
  }

  /**
   * Get organization categories (placeholder - would need dedicated endpoint)
   */
  async getOrganizationCategories() {
    try {
      // Return common categories since we don't have a dedicated endpoint
      return [
        'Education', 'Environment', 'Healthcare', 'Animal Welfare', 'Community Service',
        'Human Services', 'Arts & Culture', 'Youth Development', 'Senior Services',
        'Hunger & Homelessness', 'Disaster Relief', 'International', 'Sports & Recreation',
        'Mental Health', 'Veterans', 'Women\'s Issues', 'Children & Families',
        'Disability Services', 'Religious', 'Political', 'LGBTQ+', 'Technology',
        'Research & Advocacy', 'Public Safety'
      ];
    } catch (error) {
      console.error('Error fetching organization categories:', error);
      throw error;
    }
  }

  /**
   * Get organization types (placeholder)
   */
  async getOrganizationTypes() {
    try {
      // Return common organization types
      return [
        'Non-Profit', 'Charity', 'NGO', 'Community Organization', 
        'Religious Organization', 'Educational Institution', 'Government Agency',
        'Social Enterprise', 'Foundation', 'Cooperative'
      ];
    } catch (error) {
      console.error('Error fetching organization types:', error);
      throw error;
    }
  }

  /**
   * Get organization locations (placeholder)
   */
  async getOrganizationLocations() {
    try {
      // Return common locations
      return [
        'United States', 'Canada', 'United Kingdom', 'Australia', 'Germany',
        'France', 'Netherlands', 'Sweden', 'Denmark', 'Ireland', 'Switzerland'
      ];
    } catch (error) {
      console.error('Error fetching organization locations:', error);
      throw error;
    }
  }
}

// Export singleton instance
const findOrganizationService = new FindOrganizationService();
export default findOrganizationService;