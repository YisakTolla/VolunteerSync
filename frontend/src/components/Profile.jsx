import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Loader, AlertCircle } from "lucide-react";
import { getProfileData } from "../services/profilePageService";
import { getCurrentUser, isLoggedIn } from "../services/authService";
import VolunteerProfile from "./VolunteerProfile";
import OrganizationProfile from "./OrganizationProfile";
import "./Profile.css";

/**
 * Main Profile Router Component
 * Determines user type and renders appropriate profile component
 */
const Profile = () => {
  const [userData, setUserData] = useState(null);
  const [userType, setUserType] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const { userId } = useParams();

  useEffect(() => {
    if (!isLoggedIn()) {
      navigate("/login");
      return;
    }
    loadProfileData();
  }, [navigate, userId]);

  const loadProfileData = async () => {
    try {
      setLoading(true);
      setError("");

      console.log("📊 Loading profile data...");
      const result = await getProfileData(userId);

      if (result.success) {
        console.log("=== PROFILE ROUTER DEBUG ===");
        console.log("Result userType:", result.userType);
        console.log("Result data type:", result.data?.type);
        console.log("Organization name:", result.data?.organizationName);

        setUserData(result.data);
        setUserType(result.userType);
        setError("");
        
        console.log("✅ Profile data loaded, routing to:", result.userType);
      } else {
        setError(result.message || "Failed to load profile data");
        console.error("❌ Failed to load profile data:", result.message);
      }
    } catch (err) {
      setError("Failed to load profile. Please try refreshing.");
      console.error("❌ Error loading profile data:", err);
    } finally {
      setLoading(false);
    }
  };

  // Show loading state
  if (loading) {
    return (
      <div className="profile-loading">
        <div className="loading-content">
          <Loader className="loading-spinner" />
          <p>Loading profile...</p>
        </div>
      </div>
    );
  }

  // Show error state
  if (error && !userData) {
    return (
      <div className="profile-error">
        <div className="error-content">
          <AlertCircle className="error-icon" />
          <h3>Unable to load profile</h3>
          <p>{error}</p>
          <div className="error-actions">
            <button onClick={loadProfileData} className="btn-primary">
              Try Again
            </button>
            <button
              onClick={() => navigate("/dashboard")}
              className="btn-secondary"
            >
              Go to Dashboard
            </button>
          </div>
        </div>
      </div>
    );
  }

  // Show not found state
  if (!userData) {
    return (
      <div className="profile-error">
        <div className="error-content">
          <AlertCircle className="error-icon" />
          <h3>Profile not found</h3>
          <p>The requested profile could not be found.</p>
          <button
            onClick={() => navigate("/dashboard")}
            className="btn-primary"
          >
            Go to Dashboard
          </button>
        </div>
      </div>
    );
  }

  // Route to appropriate profile component based on user type
  const renderProfileComponent = () => {
    // Determine user type from multiple sources
    const isOrganization = 
      userType === "organization" ||
      userData?.type === "organization" ||
      userData?.organizationType ||
      userData?.organizationName;

    console.log("=== ROUTING DECISION ===");
    console.log("userType:", userType);
    console.log("userData.type:", userData?.type);
    console.log("organizationType:", userData?.organizationType);
    console.log("organizationName:", userData?.organizationName);
    console.log("Final decision - isOrganization:", isOrganization);

    if (isOrganization) {
      console.log("🏢 Routing to OrganizationProfile");
      return (
        <OrganizationProfile
          userData={userData}
          userType={userType}
          onDataUpdate={setUserData}
          onError={setError}
        />
      );
    } else {
      console.log("🙋 Routing to VolunteerProfile");
      return (
        <VolunteerProfile
          userData={userData}
          userType={userType}
          onDataUpdate={setUserData}
          onError={setError}
        />
      );
    }
  };

  return (
    <div className="profile-page">
      <div className="profile-container">
        {/* Global Error Display */}
        {error && (
          <div className="profile-error-banner">
            <AlertCircle className="error-icon" />
            <span>{error}</span>
            <button onClick={() => setError("")} className="error-dismiss">
              ×
            </button>
          </div>
        )}

        {/* Render appropriate profile component */}
        {renderProfileComponent()}
      </div>
    </div>
  );
};

export default Profile;