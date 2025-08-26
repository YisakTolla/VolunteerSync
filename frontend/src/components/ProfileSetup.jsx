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
} from "../services/profileSetUpService";
import "./ProfileSetup.css";

const ProfileSetup = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [user, setUser] = useState(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [selectedInterests, setSelectedInterests] = useState([]);
  const [selectedSkills, setSelectedSkills] = useState([]);
  const [selectedCauses, setSelectedCauses] = useState([]);
  const [selectedServices, setSelectedServices] = useState([]);
  const [selectedLanguages, setSelectedLanguages] = useState([]);
  const [selectedOrgSize, setSelectedOrgSize] = useState("");
  const [primaryCategory, setPrimaryCategory] = useState("");
  const [hasExistingProfile, setHasExistingProfile] = useState(false);
  const [formData, setFormData] = useState({
    // Volunteer fields
    firstName: "",
    lastName: "",
    bio: "",
    location: "",
    interests: "",
    skills: "",
    phoneNumber: "",
    availability: "flexible",

    // Organization fields - Basic Info
    organizationName: "",
    description: "",
    missionStatement: "",
    website: "",

    // Organization fields - Contact & Location
    address: "",
    city: "",
    state: "",
    country: "",
    zipCode: "",

    // Organization fields - Classification
    organizationType: "",
    organizationSize: "",
    primaryCategory: "",
    categories: "",
    causes: "",
    services: "",

    // Organization fields - Details
    ein: "",
    employeeCount: "",
    foundedYear: "",
    fundingGoal: "",
    fundingRaised: "",
    languagesSupported: "",
    taxExemptStatus: "",

    // System fields
    profileComplete: false,
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
    { value: "DISASTER_RELIEF", label: "Disaster Relief", emoji: "🚨" },
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
    { value: "WRITING", label: "Writing", emoji: "✏️" },
    { value: "PHOTOGRAPHY", label: "Photography", emoji: "📸" },
    { value: "LANGUAGES", label: "Languages", emoji: "🌍" },
    { value: "HEALTHCARE", label: "Healthcare", emoji: "⚕️" },
    { value: "LEGAL", label: "Legal", emoji: "⚖️" },
  ];

  // Organization Categories (single selection for primary)
  const categoryOptions = [
    { value: "Education", label: "Education", cssClass: "education" },
    { value: "Environment", label: "Environment", cssClass: "environment" },
    { value: "Healthcare", label: "Healthcare", cssClass: "healthcare" },
    {
      value: "Animal Welfare",
      label: "Animal Welfare",
      cssClass: "animal-welfare",
    },
    {
      value: "Community Service",
      label: "Community Service",
      cssClass: "community-service",
    },
    {
      value: "Human Services",
      label: "Human Services",
      cssClass: "human-services",
    },
    {
      value: "Arts & Culture",
      label: "Arts & Culture",
      cssClass: "arts-culture",
    },
    {
      value: "Youth Development",
      label: "Youth Development",
      cssClass: "youth-development",
    },
    {
      value: "Senior Services",
      label: "Senior Services",
      cssClass: "senior-services",
    },
    {
      value: "Hunger & Homelessness",
      label: "Hunger & Homelessness",
      cssClass: "hunger-homelessness",
    },
    {
      value: "Disaster Relief",
      label: "Disaster Relief",
      cssClass: "disaster-relief",
    },
    {
      value: "International",
      label: "International",
      cssClass: "international",
    },
    {
      value: "Sports & Recreation",
      label: "Sports & Recreation",
      cssClass: "sports-recreation",
    },
    {
      value: "Mental Health",
      label: "Mental Health",
      cssClass: "mental-health",
    },
    { value: "Veterans", label: "Veterans", cssClass: "veterans" },
    {
      value: "Women's Issues",
      label: "Women's Issues",
      cssClass: "womens-issues",
    },
    {
      value: "Children & Families",
      label: "Children & Families",
      cssClass: "children-families",
    },
    {
      value: "Disability Services",
      label: "Disability Services",
      cssClass: "disability-services",
    },
    { value: "Religious", label: "Religious", cssClass: "religious" },
    { value: "Political", label: "Political", cssClass: "political" },
    { value: "LGBTQ+", label: "LGBTQ+", cssClass: "lgbtq" },
    { value: "Technology", label: "Technology", cssClass: "technology" },
    {
      value: "Research & Advocacy",
      label: "Research & Advocacy",
      cssClass: "research-advocacy",
    },
    {
      value: "Public Safety",
      label: "Public Safety",
      cssClass: "public-safety",
    },
  ];

  // Organization Causes (multiple selection - reuse categories)
  const causeOptions = categoryOptions;

  // Organization Services (multiple selection)
  const serviceOptions = [
    { value: "Tutoring", label: "Tutoring", emoji: "📖" },
    { value: "Cleanup Events", label: "Cleanup Events", emoji: "🧹" },
    { value: "Food Distribution", label: "Food Distribution", emoji: "🍽️" },
    { value: "Mentoring", label: "Mentoring", emoji: "👥" },
    { value: "Fundraising", label: "Fundraising", emoji: "💰" },
    { value: "Community Outreach", label: "Community Outreach", emoji: "🤝" },
    { value: "Health Screenings", label: "Health Screenings", emoji: "🏥" },
    { value: "Emergency Response", label: "Emergency Response", emoji: "🚨" },
    {
      value: "Educational Workshops",
      label: "Educational Workshops",
      emoji: "🎓",
    },
    { value: "Social Services", label: "Social Services", emoji: "👫" },
    {
      value: "Environmental Education",
      label: "Environmental Education",
      emoji: "🌱",
    },
    { value: "Senior Care", label: "Senior Care", emoji: "👴" },
    { value: "Youth Programs", label: "Youth Programs", emoji: "👦" },
    { value: "Animal Care", label: "Animal Care", emoji: "🐾" },
    { value: "Arts Programs", label: "Arts Programs", emoji: "🎨" },
    { value: "Technology Training", label: "Technology Training", emoji: "💻" },
  ];

  // Language Options (multiple selection)
  const languageOptions = [
    { value: "English", label: "English", emoji: "🇺🇸" },
    { value: "Spanish", label: "Spanish", emoji: "🇪🇸" },
    { value: "French", label: "French", emoji: "🇫🇷" },
    { value: "German", label: "German", emoji: "🇩🇪" },
    { value: "Italian", label: "Italian", emoji: "🇮🇹" },
    { value: "Portuguese", label: "Portuguese", emoji: "🇵🇹" },
    { value: "Chinese", label: "Chinese", emoji: "🇨🇳" },
    { value: "Japanese", label: "Japanese", emoji: "🇯🇵" },
    { value: "Korean", label: "Korean", emoji: "🇰🇷" },
    { value: "Arabic", label: "Arabic", emoji: "🇸🇦" },
    { value: "Hindi", label: "Hindi", emoji: "🇮🇳" },
    { value: "Russian", label: "Russian", emoji: "🇷🇺" },
  ];

  const organizationTypeOptions = [
    { value: "Non-Profit", label: "Non-Profit" },
    { value: "Charity", label: "Charity" },
    { value: "Foundation", label: "Foundation" },
    { value: "Community Group", label: "Community Group" },
    { value: "Religious Organization", label: "Religious Organization" },
    { value: "Educational Institution", label: "Educational Institution" },
    { value: "Government Agency", label: "Government Agency" },
    { value: "Social Enterprise", label: "Social Enterprise" },
    { value: "Cooperative", label: "Cooperative" },
    { value: "NGO", label: "NGO" },
  ];

  const organizationSizeOptions = [
    { value: "Small (1-50)", label: "Small (1-50)", cssClass: "size-small" },
    {
      value: "Medium (51-200)",
      label: "Medium (51-200)",
      cssClass: "size-medium",
    },
    {
      value: "Large (201-1000)",
      label: "Large (201-1000)",
      cssClass: "size-large",
    },
    {
      value: "Enterprise (1000+)",
      label: "Enterprise (1000+)",
      cssClass: "size-enterprise",
    },
  ];

  const taxExemptOptions = [
    { value: "501(c)(3)", label: "501(c)(3) - Charitable Organization" },
    { value: "501(c)(4)", label: "501(c)(4) - Social Welfare" },
    { value: "501(c)(6)", label: "501(c)(6) - Business League" },
    { value: "501(c)(7)", label: "501(c)(7) - Social Club" },
    { value: "Not Applicable", label: "Not Applicable" },
    { value: "Pending", label: "Pending Application" },
  ];

  const countryOptions = [
    "United States",
    "Canada",
    "United Kingdom",
    "Australia",
    "Germany",
    "France",
    "Netherlands",
    "Sweden",
    "Denmark",
    "Ireland",
    "Switzerland",
  ];

  const parseCommaSeparatedArray = (value) => {
    if (!value) return [];

    if (Array.isArray(value)) {
      return value
        .filter((item) => item && item.trim() !== "")
        .map((item) => item.trim());
    }

    if (typeof value === "string") {
      return value
        .split(",")
        .map((item) => item.trim())
        .filter((item) => item !== "");
    }

    return [];
  };

  /**
   * Enhanced helper to safely convert arrays to comma-separated strings
   * Ensures no empty values and proper formatting
   */
  const arrayToCommaSeparated = (value) => {
    if (!value) return "";

    if (Array.isArray(value)) {
      const cleanArray = value.filter((item) => item && item.trim() !== "");
      return cleanArray.length > 0
        ? cleanArray.map((item) => item.trim()).join(",")
        : "";
    }

    if (typeof value === "string") {
      return value
        .split(",")
        .map((item) => item.trim())
        .filter((item) => item !== "")
        .join(",");
    }

    return "";
  };

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

        console.log("=== PROFILE DATA PARSING ===");
        console.log("Raw existing data:", existingData);

        setFormData((prev) => ({
          ...prev,
          // Volunteer fields
          firstName: existingData.firstName || currentUser.firstName || "",
          lastName: existingData.lastName || currentUser.lastName || "",
          bio: existingData.bio || "",
          location: existingData.location || "",
          skills: existingData.skills || "",
          phoneNumber: existingData.phoneNumber || "",
          availability: existingData.availability || "flexible",

          // Organization fields - Basic
          organizationName:
            existingData.organizationName || currentUser.organizationName || "",
          description: existingData.description || existingData.bio || "",
          missionStatement: existingData.missionStatement || "",
          website: existingData.website || "",

          // Organization fields - Location
          address: existingData.address || "",
          city: existingData.city || "",
          state: existingData.state || "",
          country: existingData.country || "",
          zipCode: existingData.zipCode || "",

          // Organization fields - Classification
          organizationType: existingData.organizationType || "",
          organizationSize: existingData.organizationSize || "",
          primaryCategory: existingData.primaryCategory || "",
          categories: existingData.categories || "",
          causes: existingData.causes || "",
          services: existingData.services || "",

          // Organization fields - Details
          ein: existingData.ein || "",
          employeeCount: existingData.employeeCount || "",
          foundedYear: existingData.foundedYear || "",
          fundingGoal: existingData.fundingGoal || "",
          fundingRaised: existingData.fundingRaised || "",
          languagesSupported: existingData.languagesSupported || "",
          taxExemptStatus: existingData.taxExemptStatus || "",
        }));

        // ✅ PARSE EXISTING SELECTIONS USING THE HELPER FUNCTIONS
        if (existingData.interests) {
          const interests = parseCommaSeparatedArray(existingData.interests);
          console.log("Parsed interests:", interests);
          setSelectedInterests(interests);
        }

        if (existingData.skills) {
          const skills = parseCommaSeparatedArray(existingData.skills);
          console.log("Parsed skills:", skills);
          setSelectedSkills(skills);
        }

        if (existingData.primaryCategory) {
          setPrimaryCategory(existingData.primaryCategory);
        }

        if (existingData.causes) {
          const causes = parseCommaSeparatedArray(existingData.causes);
          console.log("Parsed causes:", causes);
          setSelectedCauses(causes);
        }

        if (existingData.services) {
          const services = parseCommaSeparatedArray(existingData.services);
          console.log("Parsed services:", services);
          setSelectedServices(services);
        }

        if (existingData.languagesSupported) {
          const languages = parseCommaSeparatedArray(
            existingData.languagesSupported
          );
          console.log("Parsed languages:", languages);
          setSelectedLanguages(languages);
        }

        if (existingData.organizationSize) {
          setSelectedOrgSize(existingData.organizationSize);
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

  const handlePrimaryCategorySelect = (categoryValue) => {
    setPrimaryCategory((prev) => (prev === categoryValue ? "" : categoryValue));
    if (error) setError("");
  };

  const handleCauseToggle = (causeValue) => {
    setSelectedCauses((prev) =>
      prev.includes(causeValue)
        ? prev.filter((c) => c !== causeValue)
        : [...prev, causeValue]
    );
    if (error) setError("");
  };

  const handleServiceToggle = (serviceValue) => {
    setSelectedServices((prev) =>
      prev.includes(serviceValue)
        ? prev.filter((s) => s !== serviceValue)
        : [...prev, serviceValue]
    );
    if (error) setError("");
  };

  const handleLanguageToggle = (languageValue) => {
    setSelectedLanguages((prev) =>
      prev.includes(languageValue)
        ? prev.filter((l) => l !== languageValue)
        : [...prev, languageValue]
    );
    if (error) setError("");
  };

  const handleOrgSizeSelect = (sizeValue) => {
    setSelectedOrgSize((prev) => (prev === sizeValue ? "" : sizeValue));
    if (error) setError("");
  };

  const validateForm = () => {
    if (!formData.bio && !formData.description) {
      setError("Please provide a description about yourself/organization");
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
      if (!primaryCategory) {
        setError("Please select a primary category");
        return false;
      }
      if (!formData.city.trim()) {
        setError("Please provide your city");
        return false;
      }
      if (!formData.country.trim()) {
        setError("Please select your country");
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

      // ✅ PREPARE SUBMISSION DATA USING HELPER FUNCTIONS
      const submissionData = {
        ...formData,
        interests: arrayToCommaSeparated(selectedInterests),
        skills: arrayToCommaSeparated(selectedSkills),
        primaryCategory: primaryCategory,
        categories: primaryCategory, // Set categories to primaryCategory for backward compatibility
        causes: arrayToCommaSeparated(selectedCauses),
        services: arrayToCommaSeparated(selectedServices),
        languagesSupported: arrayToCommaSeparated(selectedLanguages),
        organizationSize: selectedOrgSize,
        profileComplete: true,
      };

      console.log("=== ENHANCED ORGANIZATION PROFILE SUBMISSION ===");
      console.log("Organization Name:", submissionData.organizationName);
      console.log("Primary Category:", submissionData.primaryCategory);
      console.log("Causes:", submissionData.causes);
      console.log("Services:", submissionData.services);
      console.log("Languages:", submissionData.languagesSupported);
      console.log(
        "Full submission data:",
        JSON.stringify(submissionData, null, 2)
      );

      const result = hasExistingProfile
        ? await updateProfile(submissionData)
        : await createProfile(submissionData);

      if (result.success) {
        setSuccess(
          `Profile ${hasExistingProfile ? "updated" : "created"} successfully!`
        );

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
      navigate("/dashboard");
    } catch (error) {
      console.error("Skip profile error:", error);
      navigate("/dashboard");
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
            Help us personalize your experience by completing your profile.
          </p>
        </div>

        {error && <div className="profile-setup-error">{error}</div>}
        {success && <div className="profile-setup-success">{success}</div>}

        <form onSubmit={handleSubmit} className="profile-setup-form">
          {/* VOLUNTEER FIELDS */}
          {user.userType === "VOLUNTEER" && (
            <>
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
                  placeholder="Share your passion for volunteering, what motivates you, and what you hope to achieve..."
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
                  placeholder="City, Country"
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

          {/* ORGANIZATION FIELDS */}
          {user.userType === "ORGANIZATION" && (
            <>
              {/* Basic Information */}
              <div className="profile-setup-section">
                <h3 className="profile-setup-section-title">
                  Basic Information
                </h3>

                <div className="profile-setup-field">
                  <label className="profile-setup-label">
                    Organization Name *
                  </label>
                  <input
                    type="text"
                    name="organizationName"
                    value={formData.organizationName}
                    onChange={handleInputChange}
                    className="profile-setup-input"
                    required
                  />
                </div>

                <div className="profile-setup-field">
                  <label className="profile-setup-label">Description *</label>
                  <textarea
                    name="description"
                    value={formData.description}
                    onChange={handleInputChange}
                    className="profile-setup-textarea"
                    rows={4}
                    placeholder="Describe your organization's mission, values, and the impact you're making in the community..."
                    required
                  />
                </div>

                <div className="profile-setup-field">
                  <label className="profile-setup-label">
                    Mission Statement
                  </label>
                  <textarea
                    name="missionStatement"
                    value={formData.missionStatement}
                    onChange={handleInputChange}
                    className="profile-setup-textarea"
                    rows={3}
                    placeholder="Your organization's formal mission statement..."
                  />
                </div>

                <div className="profile-setup-row">
                  <div className="profile-setup-field">
                    <label className="profile-setup-label">Website</label>
                    <input
                      type="url"
                      name="website"
                      value={formData.website}
                      onChange={handleInputChange}
                      className="profile-setup-input"
                      placeholder="https://yourorganization.org"
                    />
                  </div>
                  <div className="profile-setup-field">
                    <label className="profile-setup-label">Phone Number</label>
                    <input
                      type="tel"
                      name="phoneNumber"
                      value={formData.phoneNumber}
                      onChange={handleInputChange}
                      className="profile-setup-input"
                      placeholder="(555) 123-4567"
                    />
                  </div>
                </div>
              </div>

              {/* Location Information */}
              <div className="profile-setup-section">
                <h3 className="profile-setup-section-title">Location</h3>

                <div className="profile-setup-field">
                  <label className="profile-setup-label">Street Address</label>
                  <input
                    type="text"
                    name="address"
                    value={formData.address}
                    onChange={handleInputChange}
                    className="profile-setup-input"
                    placeholder="123 Main Street"
                  />
                </div>

                <div className="profile-setup-row">
                  <div className="profile-setup-field">
                    <label className="profile-setup-label">City *</label>
                    <input
                      type="text"
                      name="city"
                      value={formData.city}
                      onChange={handleInputChange}
                      className="profile-setup-input"
                      required
                    />
                  </div>
                  <div className="profile-setup-field">
                    <label className="profile-setup-label">
                      State/Province
                    </label>
                    <input
                      type="text"
                      name="state"
                      value={formData.state}
                      onChange={handleInputChange}
                      className="profile-setup-input"
                    />
                  </div>
                </div>

                <div className="profile-setup-row">
                  <div className="profile-setup-field">
                    <label className="profile-setup-label">Country *</label>
                    <select
                      name="country"
                      value={formData.country}
                      onChange={handleInputChange}
                      className="profile-setup-select"
                      required
                    >
                      <option value="">Select Country</option>
                      {countryOptions.map((country) => (
                        <option key={country} value={country}>
                          {country}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="profile-setup-field">
                    <label className="profile-setup-label">
                      ZIP/Postal Code
                    </label>
                    <input
                      type="text"
                      name="zipCode"
                      value={formData.zipCode}
                      onChange={handleInputChange}
                      className="profile-setup-input"
                    />
                  </div>
                </div>
              </div>

              {/* Organization Classification */}
              <div className="profile-setup-section">
                <h3 className="profile-setup-section-title">Classification</h3>

                <div className="profile-setup-field">
                  <label className="profile-setup-label">
                    Primary Category *
                  </label>
                  <p className="category-description">
                    Choose the main area your organization focuses on:
                  </p>
                  <div className="category-container">
                    {categoryOptions.map((category) => (
                      <button
                        key={category.value}
                        type="button"
                        onClick={() =>
                          handlePrimaryCategorySelect(category.value)
                        }
                        className={`category-tag ${category.cssClass} ${
                          primaryCategory === category.value ? "selected" : ""
                        }`}
                      >
                        {category.label}
                      </button>
                    ))}
                  </div>
                  {primaryCategory && (
                    <p className="category-count">
                      Selected: {primaryCategory}
                    </p>
                  )}
                </div>

                <div className="profile-setup-field">
                  <label className="profile-setup-label">
                    Additional Causes
                  </label>
                  <p className="causes-description">
                    Select other areas your organization works in:
                  </p>
                  <div className="causes-container">
                    {causeOptions.map((cause) => (
                      <button
                        key={cause.value}
                        type="button"
                        onClick={() => handleCauseToggle(cause.value)}
                        className={`cause-tag ${cause.cssClass} ${
                          selectedCauses.includes(cause.value) ? "selected" : ""
                        }`}
                      >
                        {cause.label}
                      </button>
                    ))}
                  </div>
                  {selectedCauses.length > 0 && (
                    <p className="causes-count">
                      Selected: {selectedCauses.length}
                    </p>
                  )}
                </div>

                <div className="profile-setup-field">
                  <label className="profile-setup-label">
                    Services Offered
                  </label>
                  <p className="services-description">
                    What services does your organization provide?
                  </p>
                  <div className="services-container">
                    {serviceOptions.map((service) => (
                      <button
                        key={service.value}
                        type="button"
                        onClick={() => handleServiceToggle(service.value)}
                        className={`service-tag ${
                          selectedServices.includes(service.value)
                            ? "selected"
                            : ""
                        }`}
                      >
                        <span>{service.emoji}</span>{" "}
                        <span>{service.label}</span>
                      </button>
                    ))}
                  </div>
                  {selectedServices.length > 0 && (
                    <p className="services-count">
                      Selected: {selectedServices.length}
                    </p>
                  )}
                </div>
              </div>

              {/* Organization Details */}
              <div className="profile-setup-section">
                <h3 className="profile-setup-section-title">
                  Organization Details
                </h3>

                <div className="profile-setup-row">
                  <div className="profile-setup-field">
                    <label className="profile-setup-label">
                      Organization Type
                    </label>
                    <select
                      name="organizationType"
                      value={formData.organizationType}
                      onChange={handleInputChange}
                      className="profile-setup-select"
                    >
                      <option value="">Select Type</option>
                      {organizationTypeOptions.map((type) => (
                        <option key={type.value} value={type.value}>
                          {type.label}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="profile-setup-field">
                    <label className="profile-setup-label">
                      Tax-Exempt Status
                    </label>
                    <select
                      name="taxExemptStatus"
                      value={formData.taxExemptStatus}
                      onChange={handleInputChange}
                      className="profile-setup-select"
                    >
                      <option value="">Select Status</option>
                      {taxExemptOptions.map((status) => (
                        <option key={status.value} value={status.value}>
                          {status.label}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>

                <div className="profile-setup-field">
                  <label className="profile-setup-label">
                    Organization Size
                  </label>
                  <div className="org-size-container">
                    {organizationSizeOptions.map((size) => (
                      <button
                        key={size.value}
                        type="button"
                        onClick={() => handleOrgSizeSelect(size.value)}
                        className={`org-size-tag ${size.cssClass} ${
                          selectedOrgSize === size.value ? "selected" : ""
                        }`}
                      >
                        {size.label}
                      </button>
                    ))}
                  </div>
                  {selectedOrgSize && (
                    <p className="org-size-count">
                      Selected: {selectedOrgSize}
                    </p>
                  )}
                </div>

                <div className="profile-setup-row">
                  <div className="profile-setup-field">
                    <label className="profile-setup-label">
                      Employee Count
                    </label>
                    <input
                      type="number"
                      name="employeeCount"
                      value={formData.employeeCount}
                      onChange={handleInputChange}
                      className="profile-setup-input"
                      min="0"
                    />
                  </div>
                  <div className="profile-setup-field">
                    <label className="profile-setup-label">Founded Year</label>
                    <input
                      type="number"
                      name="foundedYear"
                      value={formData.foundedYear}
                      onChange={handleInputChange}
                      className="profile-setup-input"
                      min="1800"
                      max={new Date().getFullYear()}
                    />
                  </div>
                </div>

                <div className="profile-setup-field">
                  <label className="profile-setup-label">
                    EIN (Tax ID Number)
                  </label>
                  <input
                    type="text"
                    name="ein"
                    value={formData.ein}
                    onChange={handleInputChange}
                    className="profile-setup-input"
                    placeholder="XX-XXXXXXX"
                  />
                </div>

                <div className="profile-setup-field">
                  <label className="profile-setup-label">
                    Languages Supported
                  </label>
                  <p className="languages-description">
                    What languages can your organization provide services in?
                  </p>
                  <div className="languages-container">
                    {languageOptions.map((language) => (
                      <button
                        key={language.value}
                        type="button"
                        onClick={() => handleLanguageToggle(language.value)}
                        className={`language-tag ${
                          selectedLanguages.includes(language.value)
                            ? "selected"
                            : ""
                        }`}
                      >
                        <span>{language.emoji}</span>{" "}
                        <span>{language.label}</span>
                      </button>
                    ))}
                  </div>
                  {selectedLanguages.length > 0 && (
                    <p className="languages-count">
                      Selected: {selectedLanguages.length}
                    </p>
                  )}
                </div>

                <div className="profile-setup-row">
                  <div className="profile-setup-field">
                    <label className="profile-setup-label">
                      Funding Goal (Optional)
                    </label>
                    <input
                      type="number"
                      name="fundingGoal"
                      value={formData.fundingGoal}
                      onChange={handleInputChange}
                      className="profile-setup-input"
                      min="0"
                      placeholder="0"
                    />
                  </div>
                  <div className="profile-setup-field">
                    <label className="profile-setup-label">
                      Funding Raised (Optional)
                    </label>
                    <input
                      type="number"
                      name="fundingRaised"
                      value={formData.fundingRaised}
                      onChange={handleInputChange}
                      className="profile-setup-input"
                      min="0"
                      placeholder="0"
                    />
                  </div>
                </div>
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
