import api from './authService';

// ==========================================
// VIEW ORGANIZATION SERVICE
// API calls for viewing and following organizations
// ==========================================

const ViewOrganizationService = {
  // ==========================================
  // ORGANIZATION FOLLOWING API CALLS
  // ==========================================

  /**
   * Follow an organization
   */
  async followOrganization(organizationId) {
    try {
      console.log('Following organization:', organizationId);
      
      const response = await api.post(`/volunteer-profiles/me/follow/${organizationId}`);
      
      console.log('Successfully followed organization:', response.data);
      return {
        success: true,
        data: response.data,
        message: response.data.message || 'Successfully followed organization'
      };
    } catch (error) {
      console.error('Error following organization:', error.response?.data);
      return {
        success: false,
        message: error.response?.data?.error || error.response?.data?.message || 'Failed to follow organization'
      };
    }
  },

  /**
   * Unfollow an organization - UPDATED to use proper DELETE endpoint
   */
  async unfollowOrganization(organizationId) {
    try {
      console.log('Unfollowing organization:', organizationId);
      
      // Use DELETE method as implemented in the backend controller
      const response = await api.delete(`/volunteer-profiles/me/follow/${organizationId}`);
      
      console.log('Successfully unfollowed organization:', response.data);
      
      // Return formatted response matching backend response structure
      return {
        success: response.data.success || true,
        data: response.data,
        message: response.data.message || 'Successfully unfollowed organization',
        organizationId: response.data.organizationId || organizationId,
        isFollowing: response.data.isFollowing || false,
        remainingFollowedCount: response.data.remainingFollowedCount
      };
    } catch (error) {
      console.error('Error unfollowing organization:', error.response?.data);
      
      // Handle specific error cases from backend
      if (error.response?.status === 400) {
        // User wasn't following the organization
        return {
          success: false,
          message: error.response.data.message || 'You are not currently following this organization',
          organizationId: organizationId,
          isFollowing: false,
          errorType: 'NOT_FOLLOWING'
        };
      }
      
      if (error.response?.status === 401) {
        // Authentication required
        return {
          success: false,
          message: 'Authentication required. Please log in again.',
          organizationId: organizationId,
          errorType: 'AUTH_REQUIRED'
        };
      }
      
      // Generic error handling
      return {
        success: false,
        message: error.response?.data?.message || error.response?.data?.error || 'Failed to unfollow organization',
        organizationId: organizationId,
        errorType: 'UNKNOWN'
      };
    }
  },

  /**
   * Toggle follow status for an organization
   */
  async toggleFollowOrganization(organizationId) {
    try {
      console.log('Toggling follow for organization:', organizationId);
      
      const response = await api.put(`/volunteer-profiles/me/follow/${organizationId}`);
      
      console.log('Successfully toggled follow:', response.data);
      return {
        success: response.data.success || true,
        data: response.data,
        isFollowing: response.data.isFollowing,
        message: response.data.message || 'Successfully updated follow status',
        organizationId: response.data.organizationId || organizationId
      };
    } catch (error) {
      console.error('Error toggling follow:', error.response?.data);
      return {
        success: false,
        message: error.response?.data?.error || error.response?.data?.message || 'Failed to toggle follow status',
        organizationId: organizationId
      };
    }
  },

  /**
   * Check follow status for an organization
   */
  async checkFollowStatus(organizationId) {
    try {
      console.log('Checking follow status for organization:', organizationId);
      
      const response = await api.get(`/volunteer-profiles/me/follow/${organizationId}/status`);
      
      console.log('Follow status check result:', response.data);
      return {
        success: response.data.success || true,
        isFollowing: response.data.isFollowing,
        organizationId: response.data.organizationId || organizationId
      };
    } catch (error) {
      console.error('Error checking follow status:', error.response?.data);
      return {
        success: false,
        isFollowing: false,
        message: error.response?.data?.error || error.response?.data?.message || 'Failed to check follow status',
        organizationId: organizationId
      };
    }
  },

  /**
   * Get organizations followed by current user (IDs only)
   */
  async getFollowedOrganizationIds() {
    try {
      console.log('Getting followed organization IDs...');
      
      const response = await api.get('/volunteer-profiles/me/followed-organizations');
      
      console.log('Successfully got followed organization IDs:', response.data);
      return {
        success: true,
        data: response.data || []
      };
    } catch (error) {
      console.error('Error getting followed organization IDs:', error.response?.data);
      return {
        success: false,
        data: [],
        message: error.response?.data?.error || error.response?.data?.message || 'Failed to get followed organization IDs'
      };
    }
  },

  /**
   * Get full details of organizations followed by current user
   */
  async getFollowedOrganizations() {
    try {
      console.log('Getting followed organizations details...');
      
      const response = await api.get('/volunteer-profiles/me/followed-organizations-details');
      
      console.log('Successfully got followed organizations details:', response.data);
      return {
        success: true,
        data: response.data || []
      };
    } catch (error) {
      console.error('Error getting followed organizations details:', error.response?.data);
      return {
        success: false,
        data: [],
        message: error.response?.data?.error || error.response?.data?.message || 'Failed to get followed organizations details'
      };
    }
  },

  /**
   * Unfollow multiple organizations at once - ENHANCED with individual DELETE calls
   */
  async unfollowMultipleOrganizations(organizationIds) {
    try {
      console.log('Unfollowing multiple organizations:', organizationIds);
      
      if (!Array.isArray(organizationIds) || organizationIds.length === 0) {
        return {
          success: false,
          message: 'No organization IDs provided',
          unfollowedCount: 0,
          failedCount: 0
        };
      }

      const results = [];
      let successCount = 0;
      let failedCount = 0;

      // Process each organization individually using the DELETE endpoint
      for (const orgId of organizationIds) {
        try {
          const result = await this.unfollowOrganization(orgId);
          results.push({ organizationId: orgId, ...result });
          
          if (result.success) {
            successCount++;
          } else {
            failedCount++;
          }
        } catch (error) {
          results.push({
            organizationId: orgId,
            success: false,
            message: 'Failed to unfollow organization',
            error: error.message
          });
          failedCount++;
        }
      }

      console.log('Multiple unfollow results:', { successCount, failedCount, results });
      
      return {
        success: successCount > 0,
        unfollowedCount: successCount,
        failedCount: failedCount,
        totalProcessed: organizationIds.length,
        results: results,
        message: `Successfully unfollowed ${successCount} organization(s). ${failedCount} failed.`
      };
      
    } catch (error) {
      console.error('Error unfollowing multiple organizations:', error);
      return {
        success: false,
        unfollowedCount: 0,
        failedCount: organizationIds?.length || 0,
        message: 'Failed to process multiple unfollow request'
      };
    }
  },

  // ==========================================
  // ORGANIZATION STATISTICS API CALLS
  // ==========================================

  /**
   * Get followers for an organization (public endpoint)
   */
  async getOrganizationFollowers(organizationId, limit = 10) {
    try {
      console.log('Getting followers for organization:', organizationId);
      
      const response = await api.get(`/volunteer-profiles/organization/${organizationId}/followers?limit=${limit}`);
      
      console.log('Successfully got organization followers:', response.data);
      return {
        success: true,
        data: response.data
      };
    } catch (error) {
      console.error('Error getting organization followers:', error.response?.data);
      return {
        success: false,
        message: error.response?.data?.error || error.response?.data?.message || 'Failed to get organization followers'
      };
    }
  },

  /**
   * Get follower count for an organization (public endpoint)
   */
  async getOrganizationFollowerCount(organizationId) {
    try {
      console.log('Getting follower count for organization:', organizationId);
      
      const response = await api.get(`/volunteer-profiles/organization/${organizationId}/follower-count`);
      
      console.log('Successfully got organization follower count:', response.data);
      return {
        success: response.data.success || true,
        followerCount: response.data.followerCount || 0,
        organizationId: response.data.organizationId || organizationId
      };
    } catch (error) {
      console.error('Error getting organization follower count:', error.response?.data);
      return {
        success: false,
        followerCount: 0,
        organizationId: organizationId,
        message: error.response?.data?.error || error.response?.data?.message || 'Failed to get organization follower count'
      };
    }
  },

  // ==========================================
  // RECOMMENDATIONS
  // ==========================================

  /**
   * Get recommended organizations for a volunteer based on their interests
   */
  async getRecommendedOrganizations(limit = 10) {
    try {
      console.log('Getting recommended organizations...');
      
      const response = await api.get(`/volunteer-profiles/me/recommended-organizations?limit=${limit}`);
      
      console.log('Successfully got recommended organizations:', response.data);
      return {
        success: true,
        data: response.data || []
      };
    } catch (error) {
      console.error('Error getting recommended organizations:', error.response?.data);
      return {
        success: false,
        data: [],
        message: error.response?.data?.error || error.response?.data?.message || 'Failed to get recommended organizations'
      };
    }
  }
};

export default ViewOrganizationService;