import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  isLoggedIn,
  getCurrentUser,
  ensureValidToken,
  isTokenExpired,
  updateCurrentUser,
} from "../services/authService";
import {
  createProfile,
  updateProfile,
  fetchMyProfile,
} from "../services/profileService";
import "./ProfileSetup.css";

const ProfileSetup = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [user, setUser] = useState(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [selectedInterests, setSelectedInterests] = useState([]);
  const [hasExistingProfile, setHasExistingProfile] = useState(false);
  const [formData, setFormData] = useState({
    bio: "",
    location: "",
    interests: "",
    skills: "",
    phoneNumber: "",
    organizationName: "",
    organizationType: "",
    website: "",
    missionStatement: "",
    categories: "",
    services: "",
    firstName: "",
    lastName: "",
    availability: "flexible",
  });

  const interestOptions = [
    { value: "COMMUNITY_CLEANUP", label: "Community Cleanup", emoji: "🧹" },
    { value: "FOOD_SERVICE", label: "Food Service", emoji: "🍽️" },
    { value: "TUTORING_EDUCATION", label: "Tutoring & Education", emoji: "📚" },
    { value: "ANIMAL_CARE", label: "Animal Care", emoji: "🐾" },
    {
      value: "ENVIRONMENTAL_CONSERVATION",
      label: "Environmental Conservation",
      emoji: "🌱",
    },
    { value: "SENIOR_SUPPORT", label: "Senior Support", emoji: "👴" },
    { value: "YOUTH_MENTORING", label: "Youth Mentoring", emoji: "👥" },
    { value: "HEALTHCARE_SUPPORT", label: "Healthcare Support", emoji: "🏥" },
    { value: "ARTS_CULTURE", label: "Arts & Culture", emoji: "🎨" },
    { value: "TECHNOLOGY_DIGITAL", label: "Technology & Digital", emoji: "💻" },
    { value: "DISASTER_RELIEF", label: "Disaster Relief", emoji: "🚑" },
    { value: "COMMUNITY_BUILDING", label: "Community Building", emoji: "🏘️" },
    { value: "OTHER", label: "Other", emoji: "📋" },
  ];

  const skillOptions = [
    { value: "COMMUNICATION", label: "Communication", emoji: "💬" },
    { value: "LEADERSHIP", label: "Leadership", emoji: "👑" },
    { value: "ORGANIZATION", label: "Organization", emoji: "📊" },
    { value: "TEACHING", label: "Teaching", emoji: "👩‍🏫" },
    { value: "TECHNICAL", label: "Technical/IT", emoji: "🔧" },
    { value: "CREATIVE", label: "Creative/Design", emoji: "🎨" },
    { value: "FUNDRAISING", label: "Fundraising", emoji: "💰" },
    { value: "EVENT_PLANNING", label: "Event Planning", emoji: "🎉" },
    { value: "SOCIAL_MEDIA", label: "Social Media", emoji: "📱" },
    { value: "WRITING", label: "Writing", emoji: "✍️" },
    { value: "PHOTOGRAPHY", label: "Photography", emoji: "📸" },
    { value: "LANGUAGES", label: "Languages", emoji: "🌍" },
    { value: "HEALTHCARE", label: "Healthcare", emoji: "⚕️" },
    { value: "LEGAL", label: "Legal", emoji: "⚖️" },
  ];

  const [selectedSkills, setSelectedSkills] = useState([]);

  useEffect(() => {
    if (!isLoggedIn()) {
      navigate("/login");
      return;
    }

    const token = localStorage.getItem("authToken");
    if (!token) {
      navigate("/login");
      return;
    }

    try {
      const payload = JSON.parse(atob(token.split(".")[1]));
      const timeRemainingMs = payload.exp * 1000 - Date.now();

      if (isTokenExpired(token)) {
        ensureValidToken()
          .then(initializeComponent)
          .catch(() => navigate("/login"));
      } else if (timeRemainingMs < 5 * 60 * 1000) {
        ensureValidToken().then(initializeComponent).catch(initializeComponent);
      } else {
        initializeComponent();
      }
    } catch {
      localStorage.removeItem("authToken");
      navigate("/login");
    }
  }, [navigate]);

  const initializeComponent = async () => {
    const currentUser = getCurrentUser();
    if (!currentUser) {
      navigate("/login");
      return;
    }
    setUser(currentUser);

    try {
      const profileResult = await fetchMyProfile();
      if (profileResult.success && profileResult.data) {
        setHasExistingProfile(true);
        const existingData = profileResult.data;

        setFormData((prev) => ({
          ...prev,
          firstName: existingData.firstName || currentUser.firstName || "",
          lastName: existingData.lastName || currentUser.lastName || "",
          organizationName:
            existingData.organizationName || currentUser.organizationName || "",
          bio: existingData.bio || "",
          location: existingData.location || "",
          skills: existingData.skills || "",
          phoneNumber: existingData.phoneNumber || "",
          organizationType: existingData.organizationType || "",
          website: existingData.website || "",
          // ✅ FIXED: Ensure services is always a string
          services: Array.isArray(existingData.services)
            ? existingData.services.join(",")
            : existingData.services || "",
          categories: existingData.categories || "",
          availability: existingData.availability || "flexible",
        }));

        // Parse interests and skills
        if (existingData.interests) {
          const interests =
            typeof existingData.interests === "string"
              ? existingData.interests.split(",").map((i) => i.trim())
              : existingData.interests;
          setSelectedInterests(interests);
        }

        if (existingData.skills) {
          const skills =
            typeof existingData.skills === "string"
              ? existingData.skills.split(",").map((s) => s.trim())
              : existingData.skills;
          setSelectedSkills(skills);
        }
      } else {
        setHasExistingProfile(false);
        setFormData((prev) => ({
          ...prev,
          firstName: currentUser.firstName || "",
          lastName: currentUser.lastName || "",
          organizationName: currentUser.organizationName || "",
        }));
      }
    } catch (error) {
      console.error("Error loading existing profile:", error);
      setHasExistingProfile(false);
    }
  };

  // Function to check if profile is complete
  const isProfileComplete = (user) => {
    if (!user) return false;

    // Check if profileComplete flag is set
    if (user.profileComplete === true) {
      return true;
    }

    // For volunteer users
    if (user.userType === "VOLUNTEER") {
      return !!(user.firstName && user.lastName && user.bio && user.location);
    }

    // For organization users
    if (user.userType === "ORGANIZATION") {
      return !!(user.organizationName && user.bio && user.location);
    }

    return false;
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (error) setError("");
  };

  const handleInterestToggle = (interestValue) => {
    setSelectedInterests((prev) =>
      prev.includes(interestValue)
        ? prev.filter((i) => i !== interestValue)
        : [...prev, interestValue]
    );
    if (error) setError("");
  };

  const handleSkillToggle = (skillValue) => {
    setSelectedSkills((prev) =>
      prev.includes(skillValue)
        ? prev.filter((s) => s !== skillValue)
        : [...prev, skillValue]
    );
    if (error) setError("");
  };

  const validateForm = () => {
    if (!formData.bio.trim()) {
      setError("Please tell us about yourself");
      return false;
    }

    if (!formData.location.trim()) {
      setError("Please provide your location");
      return false;
    }

    if (user.userType === "VOLUNTEER") {
      if (!formData.firstName.trim()) {
        setError("Please provide your first name");
        return false;
      }
      if (!formData.lastName.trim()) {
        setError("Please provide your last name");
        return false;
      }
      if (selectedInterests.length === 0) {
        setError("Please select at least one interest or cause");
        return false;
      }
    }

    if (user.userType === "ORGANIZATION") {
      if (!formData.organizationName.trim()) {
        setError("Please provide your organization name");
        return false;
      }
      if (!formData.categories.trim()) {
        setError("Please specify your organization focus areas");
        return false;
      }
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setSuccess("");

    try {
      await ensureValidToken();

      if (!validateForm()) {
        setLoading(false);
        return;
      }

      // Prepare submission data
      const submissionData = {
        ...formData,
        interests: selectedInterests.join(","),
        skills: selectedSkills.join(","),
        profileComplete: true, // Mark profile as complete
      };

      console.log("=== FRONTEND DEBUG: SUBMISSION DATA ===");
      console.log("User type:", user?.userType);
      console.log("Form data keys:", Object.keys(formData));
      console.log("Selected interests:", selectedInterests);
      console.log("Selected skills:", selectedSkills);
      console.log("Full submission data:");
      console.log(JSON.stringify(submissionData, null, 2));

      // Check for organization-specific fields
      if (user?.userType === "ORGANIZATION") {
        console.log("=== ORGANIZATION SPECIFIC DEBUG ===");
        console.log("Organization Name:", submissionData.organizationName);
        console.log("Bio:", submissionData.bio);
        console.log("Location:", submissionData.location);
        console.log("Categories:", submissionData.categories);
        console.log("Services:", submissionData.services);
        console.log("Website:", submissionData.website);
        console.log("Organization Type:", submissionData.organizationType);
        console.log("Mission Statement:", submissionData.missionStatement);

        // Check for required fields
        const requiredFields = [
          "organizationName",
          "bio",
          "location",
          "categories",
        ];
        const missingFields = requiredFields.filter(
          (field) =>
            !submissionData[field] || submissionData[field].trim() === ""
        );

        if (missingFields.length > 0) {
          console.error("Missing required fields:", missingFields);
          setError(`Missing required fields: ${missingFields.join(", ")}`);
          setLoading(false);
          return;
        }
      }

      console.log("Submitting profile data:", submissionData);

      const result = hasExistingProfile
        ? await updateProfile(submissionData)
        : await createProfile(submissionData);

      console.log("=== PROFILE SUBMISSION RESULT ===");
      console.log("Success:", result.success);
      console.log("Message:", result.message);
      console.log("Data:", result.data);

      if (result.success) {
        setSuccess(
          `Profile ${hasExistingProfile ? "updated" : "created"} successfully!`
        );

        // Update local user data to mark profile as complete
        updateCurrentUser({
          ...submissionData,
          profileComplete: true,
        });

        setTimeout(() => navigate("/dashboard"), 2000);
      } else {
        console.error("Profile submission failed:", result);
        setError(result.message || "Profile save failed. Please try again.");
      }
    } catch (error) {
      console.error("Profile submission error:", error);
      setError("Something went wrong. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleSkip = async () => {
    try {
      await ensureValidToken();

      // Create minimal profile data
      const skipData = {
        bio: "Profile setup skipped - will complete later",
        location: "Location not specified",
        interests:
          user?.userType === "VOLUNTEER" ? selectedInterests.join(",") : "",
        skills: selectedSkills.join(","),
        phoneNumber: formData.phoneNumber || "",
        profileComplete: false, // Mark as incomplete so they can finish later
        ...(user?.userType === "VOLUNTEER"
          ? {
              firstName: formData.firstName || user.firstName || "",
              lastName: formData.lastName || user.lastName || "",
              availability: "flexible",
            }
          : {}),
        ...(user?.userType === "ORGANIZATION"
          ? {
              organizationName:
                formData.organizationName || user.organizationName || "",
              organizationType: formData.organizationType || "",
              website: formData.website || "",
              missionStatement: formData.missionStatement || "",
              categories: formData.categories || "",
              services: formData.services || "",
            }
          : {}),
      };

      if (hasExistingProfile) {
        await updateProfile(skipData);
      } else {
        await createProfile(skipData);
      }

      // Update local user but don't mark as complete
      updateCurrentUser({
        ...skipData,
        profileComplete: false,
      });

      navigate("/dashboard");
    } catch (error) {
      console.error("Skip profile error:", error);
      navigate("/dashboard"); // Navigate anyway
    }
  };

  if (!user) {
    return (
      <div className="profile-setup-loading">
        <div className="profile-setup-loading-content">
          <div className="profile-setup-loading-spinner"></div>
          <span className="profile-setup-loading-text">Loading...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="profile-setup-container">
      <div className="profile-setup-card">
        <div className="profile-setup-header">
          <h1 className="profile-setup-title">
            {hasExistingProfile
              ? "Update Your Profile"
              : "Complete Your Profile"}
          </h1>
          <p className="profile-setup-subtitle">
            Welcome, {user.firstName || user.organizationName || user.email}!
          </p>
          <p className="profile-setup-description">
            Help us personalize your volunteer experience by completing your
            profile.
          </p>
        </div>

        {error && <div className="profile-setup-error">{error}</div>}
        {success && <div className="profile-setup-success">{success}</div>}

        <form onSubmit={handleSubmit} className="profile-setup-form">
          {/* Name fields for volunteers */}
          {user.userType === "VOLUNTEER" && (
            <div className="profile-setup-row">
              <div className="profile-setup-field">
                <label className="profile-setup-label">First Name *</label>
                <input
                  type="text"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleInputChange}
                  className="profile-setup-input"
                  required
                />
              </div>
              <div className="profile-setup-field">
                <label className="profile-setup-label">Last Name *</label>
                <input
                  type="text"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleInputChange}
                  className="profile-setup-input"
                  required
                />
              </div>
            </div>
          )}

          {/* Organization name for organizations */}
          {user.userType === "ORGANIZATION" && (
            <div className="profile-setup-field">
              <label className="profile-setup-label">Organization Name *</label>
              <input
                type="text"
                name="organizationName"
                value={formData.organizationName}
                onChange={handleInputChange}
                className="profile-setup-input"
                required
              />
            </div>
          )}

          <div className="profile-setup-field">
            <label className="profile-setup-label">
              Tell us about yourself *
            </label>
            <textarea
              name="bio"
              value={formData.bio}
              onChange={handleInputChange}
              className="profile-setup-textarea"
              rows={4}
              placeholder={
                user.userType === "VOLUNTEER"
                  ? "Share your passion for volunteering, what motivates you, and what you hope to achieve..."
                  : "Describe your organization's mission, values, and the impact you're making in the community..."
              }
              required
            />
          </div>

          <div className="profile-setup-field">
            <label className="profile-setup-label">Location *</label>
            <input
              type="text"
              name="location"
              value={formData.location}
              onChange={handleInputChange}
              className="profile-setup-input"
              placeholder="City, State"
              required
            />
          </div>

          <div className="profile-setup-field">
            <label className="profile-setup-label">
              Phone Number (Optional)
            </label>
            <input
              type="tel"
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleInputChange}
              className="profile-setup-input"
              placeholder="(555) 123-4567"
            />
          </div>

          {user.userType === "VOLUNTEER" && (
            <>
              <div className="profile-setup-field">
                <label className="profile-setup-label">
                  Interests & Causes *
                </label>
                <p className="interests-description">
                  Select the causes you're passionate about:
                </p>
                <div className="interests-container">
                  {interestOptions.map((opt) => (
                    <button
                      key={opt.value}
                      type="button"
                      onClick={() => handleInterestToggle(opt.value)}
                      className={`interest-tag ${
                        selectedInterests.includes(opt.value) ? "selected" : ""
                      }`}
                    >
                      <span>{opt.emoji}</span> <span>{opt.label}</span>
                    </button>
                  ))}
                </div>
                {selectedInterests.length > 0 && (
                  <p className="interests-count">
                    Selected: {selectedInterests.length}
                  </p>
                )}
              </div>

              <div className="profile-setup-field">
                <label className="profile-setup-label">
                  Skills & Abilities
                </label>
                <p className="skills-description">
                  What skills can you bring to volunteer opportunities?
                </p>
                <div className="skills-container">
                  {skillOptions.map((skill) => (
                    <button
                      key={skill.value}
                      type="button"
                      onClick={() => handleSkillToggle(skill.value)}
                      className={`skill-tag ${
                        selectedSkills.includes(skill.value) ? "selected" : ""
                      }`}
                    >
                      <span>{skill.emoji}</span> <span>{skill.label}</span>
                    </button>
                  ))}
                </div>
                {selectedSkills.length > 0 && (
                  <p className="skills-count">
                    Selected: {selectedSkills.length}
                  </p>
                )}
              </div>

              <div className="profile-setup-field">
                <label className="profile-setup-label">Availability</label>
                <select
                  name="availability"
                  value={formData.availability}
                  onChange={handleInputChange}
                  className="profile-setup-select"
                >
                  <option value="flexible">Flexible</option>
                  <option value="weekdays">Weekdays only</option>
                  <option value="weekends">Weekends only</option>
                  <option value="evenings">Evenings only</option>
                  <option value="specific">
                    Specific times (will coordinate)
                  </option>
                </select>
              </div>
            </>
          )}

          {user.userType === "ORGANIZATION" && (
            <>
              <div className="profile-setup-field">
                <label className="profile-setup-label">Organization Type</label>
                <select
                  name="organizationType"
                  value={formData.organizationType}
                  onChange={handleInputChange}
                  className="profile-setup-select"
                >
                  <option value="">Select organization type</option>
                  <option value="nonprofit">Non-profit</option>
                  <option value="charity">Charity</option>
                  <option value="community">Community Group</option>
                  <option value="religious">Religious Organization</option>
                  <option value="educational">Educational Institution</option>
                  <option value="healthcare">Healthcare Organization</option>
                  <option value="environmental">
                    Environmental Organization
                  </option>
                  <option value="other">Other</option>
                </select>
              </div>

              <div className="profile-setup-field">
                <label className="profile-setup-label">Focus Areas *</label>
                <input
                  type="text"
                  name="categories"
                  value={formData.categories}
                  onChange={handleInputChange}
                  className="profile-setup-input"
                  placeholder="e.g., Education, Environment, Community Service"
                  required
                />
              </div>

              <div className="profile-setup-field">
                <label className="profile-setup-label">
                  Website (Optional)
                </label>
                <input
                  type="url"
                  name="website"
                  value={formData.website}
                  onChange={handleInputChange}
                  className="profile-setup-input"
                  placeholder="https://yourorganization.org"
                />
              </div>
            </>
          )}

          <div className="profile-setup-buttons">
            <button
              type="submit"
              disabled={loading}
              className="profile-setup-submit-btn"
            >
              {loading && <div className="loading-spinner"></div>}
              {hasExistingProfile ? "Update Profile" : "Complete Profile"}
            </button>
            <button
              type="button"
              onClick={handleSkip}
              disabled={loading}
              className="profile-setup-skip-btn"
            >
              Skip for now
            </button>
          </div>
        </form>

        <p className="profile-setup-footer">
          You can always update this information later in your profile settings.
        </p>
      </div>
    </div>
  );
};

export default ProfileSetup;
