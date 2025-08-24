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
      console.log('🔄 Following organization:', organizationId);
      
      const response = await api.post(`/volunteer-profiles/me/follow/${organizationId}`);
      
      console.log('✅ Successfully followed organization:', response.data);
      return {
        success: true,
        data: response.data,
        message: response.data.message || 'Successfully followed organization'
      };
    } catch (error) {
      console.error('❌ Error following organization:', error.response?.data);
      return {
        success: false,
        message: error.response?.data?.error || error.response?.data?.message || 'Failed to follow organization'
      };
    }
  },

  /**
   * Unfollow an organization
   */
  async unfollowOrganization(organizationId) {
    try {
      console.log('🔄 Unfollowing organization:', organizationId);
      
      const response = await api.delete(`/volunteer-profiles/me/follow/${organizationId}`);
      
      console.log('✅ Successfully unfollowed organization:', response.data);
      return {
        success: true,
        data: response.data,
        message: response.data.message || 'Successfully unfollowed organization'
      };
    } catch (error) {
      console.error('❌ Error unfollowing organization:', error.response?.data);
      return {
        success: false,
        message: error.response?.data?.error || error.response?.data?.message || 'Failed to unfollow organization'
      };
    }
  },

  /**
   * Toggle follow status for an organization
   */
  async toggleFollowOrganization(organizationId) {
    try {
      console.log('🔄 Toggling follow status for organization:', organizationId);
      
      const response = await api.put(`/volunteer-profiles/me/follow/${organizationId}`);
      
      console.log('✅ Successfully toggled follow status:', response.data);
      return {
        success: true,
        data: response.data,
        isFollowing: response.data.isFollowing,
        message: response.data.message
      };
    } catch (error) {
      console.error('❌ Error toggling follow status:', error.response?.data);
      return {
        success: false,
        message: error.response?.data?.error || error.response?.data?.message || 'Failed to update follow status'
      };
    }
  },

  /**
   * Check if currently following an organization
   */
  async checkFollowStatus(organizationId) {
    try {
      console.log('🔄 Checking follow status for organization:', organizationId);
      
      const response = await api.get(`/volunteer-profiles/me/follow/${organizationId}/status`);
      
      console.log('✅ Follow status check successful:', response.data);
      return {
        success: true,
        isFollowing: response.data.isFollowing,
        organizationId: response.data.organizationId
      };
    } catch (error) {
      console.error('❌ Error checking follow status:', error.response?.data);
      
      // If not authenticated, return false instead of error
      if (error.response?.status === 401 || error.response?.status === 403) {
        return {
          success: true,
          isFollowing: false,
          organizationId: organizationId
        };
      }
      
      return {
        success: false,
        isFollowing: false,
        message: error.response?.data?.error || error.response?.data?.message || 'Failed to check follow status'
      };
    }
  },

  /**
   * Get all organizations followed by current user
   */
  async getFollowedOrganizations() {
    try {
      console.log('🔄 Getting followed organizations...');
      
      const response = await api.get('/volunteer-profiles/me/followed-organizations');
      
      console.log('✅ Successfully got followed organizations:', response.data);
      return {
        success: true,
        data: response.data || []
      };
    } catch (error) {
      console.error('❌ Error getting followed organizations:', error.response?.data);
      return {
        success: false,
        data: [],
        message: error.response?.data?.error || error.response?.data?.message || 'Failed to get followed organizations'
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
      console.log('🔄 Getting followers for organization:', organizationId);
      
      const response = await api.get(`/volunteer-profiles/organization/${organizationId}/followers?limit=${limit}`);
      
      console.log('✅ Successfully got organization followers:', response.data);
      return {
        success: true,
        data: response.data
      };
    } catch (error) {
      console.error('❌ Error getting organization followers:', error.response?.data);
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
      console.log('🔄 Getting follower count for organization:', organizationId);
      
      const response = await api.get(`/volunteer-profiles/organization/${organizationId}/follower-count`);
      
      console.log('✅ Successfully got follower count:', response.data);
      return {
        success: true,
        data: response.data,
        followerCount: response.data.followerCount
      };
    } catch (error) {
      console.error('❌ Error getting follower count:', error.response?.data);
      return {
        success: false,
        followerCount: 0,
        message: error.response?.data?.error || error.response?.data?.message || 'Failed to get follower count'
      };
    }
  },

  // ==========================================
  // ORGANIZATION DATA API CALLS
  // ==========================================

  /**
   * Get organization details with follow status
   */
  async getOrganizationWithFollowStatus(organizationId) {
    try {
      console.log('🔄 Getting organization with follow status:', organizationId);
      
      // Get basic organization data (assuming you have this endpoint)
      // const orgResponse = await api.get(`/organizations/${organizationId}`);
      
      // Get follow status
      const followStatusResponse = await this.checkFollowStatus(organizationId);
      
      // Get follower count
      const followerCountResponse = await this.getOrganizationFollowerCount(organizationId);
      
      return {
        success: true,
        // organization: orgResponse.data,
        isFollowing: followStatusResponse.isFollowing,
        followerCount: followerCountResponse.followerCount
      };
    } catch (error) {
      console.error('❌ Error getting organization with follow status:', error);
      return {
        success: false,
        isFollowing: false,
        followerCount: 0,
        message: 'Failed to get organization details'
      };
    }
  }
};

export default ViewOrganizationService;