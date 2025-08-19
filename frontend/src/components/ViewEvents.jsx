import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  ArrowLeft,
  MapPin,
  Calendar,
  Clock,
  Users,
  Mail,
  Phone,
  Globe,
  Star,
  Heart,
  Share2,
  CheckCircle,
  AlertCircle,
  Target,
  Building,
  UserCheck,
  Award,
  ExternalLink,
} from "lucide-react";
import "./ViewEvents.css";
import findEventsService from "../services/findEventsService";

const ViewEvent = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  console.log("ViewEvent component mounted with ID:", id);

  useEffect(() => {
    if (id) {
      loadEvent();
    } else {
      setError("No event ID provided");
      setLoading(false);
    }
  }, [id]);

  const loadEvent = async () => {
    try {
      console.log("Loading event with ID:", id);
      setLoading(true);
      setError(null);

      const eventData = await findEventsService.findEventById(id);
      console.log("Event data received:", eventData);

      setEvent(eventData);
    } catch (err) {
      console.error("Failed to load event:", err);
      setError(`Failed to load event: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleBack = () => {
    navigate("/find-events");
  };

  const handleApplyToEvent = () => {
    // TODO: Implement apply to event functionality with application service
    console.log("Apply to event:", event.id);
    alert("Application functionality will be implemented when we create the application system!");
  };

  const handleShareEvent = () => {
    // TODO: Implement share event functionality
    navigator.clipboard.writeText(window.location.href);
    alert("Event link copied to clipboard!");
  };

  const getEventTypeClass = (eventType) => {
    if (!eventType) return "";

    const typeMap = {
      COMMUNITY_SERVICE: "community-service",
      FUNDRAISING: "fundraising",
      EDUCATION: "education",
      ENVIRONMENT: "environment",
      HEALTHCARE: "healthcare",
      ANIMAL_WELFARE: "animal-welfare",
      DISASTER_RELIEF: "disaster-relief",
      ARTS_CULTURE: "arts-culture",
      SPORTS_RECREATION: "sports-recreation",
      SENIOR_SERVICES: "senior-services",
      YOUTH_DEVELOPMENT: "youth-development",
    };

    return typeMap[eventType] || "";
  };

  const formatEventDate = (startDate, endDate) => {
    if (!startDate) return "Date TBA";

    const start = new Date(startDate);
    const end = endDate ? new Date(endDate) : null;

    const formatOptions = {
      weekday: "long",
      year: "numeric",
      month: "long",
      day: "numeric",
    };

    const timeOptions = {
      hour: "numeric",
      minute: "2-digit",
      hour12: true,
    };

    const dateStr = start.toLocaleDateString("en-US", formatOptions);
    const startTimeStr = start.toLocaleTimeString("en-US", timeOptions);

    if (end && end.getTime() !== start.getTime()) {
      const endTimeStr = end.toLocaleTimeString("en-US", timeOptions);
      return `${dateStr} from ${startTimeStr} to ${endTimeStr}`;
    }

    return `${dateStr} at ${startTimeStr}`;
  };

  const formatDuration = (startDate, endDate, estimatedHours) => {
    if (estimatedHours) {
      return `${estimatedHours} hour${estimatedHours !== 1 ? "s" : ""}`;
    }

    if (startDate && endDate) {
      const start = new Date(startDate);
      const end = new Date(endDate);
      const diffHours = Math.round((end - start) / (1000 * 60 * 60));
      return `${diffHours} hour${diffHours !== 1 ? "s" : ""}`;
    }

    return "Duration TBA";
  };

  const getAvailableSpots = (maxVolunteers, currentVolunteers) => {
    if (!maxVolunteers) return "Unlimited spots available";
    const remaining = maxVolunteers - (currentVolunteers || 0);
    return remaining > 0
      ? `${remaining} spot${remaining !== 1 ? "s" : ""} available`
      : "Event is full";
  };

  const getRequirementsArray = (requirements) => {
    if (!requirements) return [];
    if (typeof requirements === "string") {
      return requirements.split(",").map((req) => req.trim());
    }
    if (Array.isArray(requirements)) {
      return requirements;
    }
    return [];
  };

  const isEventFull = (maxVolunteers, currentVolunteers) => {
    if (!maxVolunteers) return false;
    return currentVolunteers >= maxVolunteers;
  };

  const isEventPast = (startDate) => {
    if (!startDate) return false;
    return new Date(startDate) < new Date();
  };

  // Show loading state
  if (loading) {
    return (
      <div className="view-event-page">
        <div className="view-event-loading">
          <div className="view-event-loading-spinner"></div>
          <p>Loading event details...</p>
        </div>
      </div>
    );
  }

  // Show error state
  if (error) {
    return (
      <div className="view-event-page">
        <div className="view-event-error">
          <p>{error}</p>
          <button onClick={loadEvent} className="view-event-retry-btn">
            Try Again
          </button>
          <button onClick={handleBack} className="view-event-back-btn">
            Back to Events
          </button>
        </div>
      </div>
    );
  }

  // Show not found state
  if (!event) {
    return (
      <div className="view-event-page">
        <div className="view-event-error">
          <p>Event not found.</p>
          <button onClick={handleBack} className="view-event-back-btn">
            Back to Events
          </button>
        </div>
      </div>
    );
  }

  console.log("Rendering event:", event);

  // Main render
  try {
    return (
      <div className="view-event-page">
        <div className="view-event-container">
          {/* Back Button */}
          <button onClick={handleBack} className="view-event-back-button">
            <ArrowLeft />
            Back to Events
          </button>

          {/* Event Header */}
          <div className="view-event-header">
            {event.imageUrl && (
              <div className="view-event-cover">
                <img src={event.imageUrl} alt={`${event.title} cover`} />
              </div>
            )}

            <div className="view-event-header-content">
              <div className="view-event-header-main">
                <div className="view-event-profile-section">
                  <div className="view-event-profile-image">
                    {event.imageUrl ? (
                      <img src={event.imageUrl} alt={event.title || "Event"} />
                    ) : (
                      <div className="view-event-profile-placeholder">
                        <Calendar />
                      </div>
                    )}
                    {event.status === "FEATURED" && (
                      <div className="view-event-featured-badge">
                        <Star />
                      </div>
                    )}
                  </div>

                  <div className="view-event-header-info">
                    <div className="view-event-categories">
                      {event.eventType && (
                        <span
                          className={`view-event-category ${getEventTypeClass(
                            event.eventType
                          )}`}
                        >
                          {event.eventType.replace(/_/g, " ")}
                        </span>
                      )}
                      {event.isVirtual && (
                        <span className="view-event-category virtual">
                          Virtual Event
                        </span>
                      )}
                      {event.skillLevelRequired && (
                        <span className="view-event-category skill">
                          {event.skillLevelRequired.replace(/_/g, " ")}
                        </span>
                      )}
                    </div>

                    <h1 className="view-event-title">
                      {event.title || "Event Title Not Available"}
                    </h1>

                    <div className="view-event-meta">
                      <div className="view-event-meta-item">
                        <Calendar />
                        <span>{formatEventDate(event.startDate, event.endDate)}</span>
                      </div>

                      <div className="view-event-meta-item">
                        <Clock />
                        <span>{formatDuration(event.startDate, event.endDate, event.estimatedHours)}</span>
                      </div>

                      <div className="view-event-meta-item">
                        <MapPin />
                        <span>
                          {event.isVirtual
                            ? "Virtual Event"
                            : event.location || "Location TBA"}
                        </span>
                      </div>

                      <div className="view-event-meta-item">
                        <Building />
                        <span>{event.organizationName || "Organization"}</span>
                      </div>

                      <div className="view-event-meta-item">
                        <Users />
                        <span>
                          {getAvailableSpots(event.maxVolunteers, event.currentVolunteers)}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>

                <div className="view-event-actions">
                  {!isEventPast(event.startDate) && !isEventFull(event.maxVolunteers, event.currentVolunteers) ? (
                    <button 
                      className="view-event-btn primary"
                      onClick={handleApplyToEvent}
                    >
                      <Heart />
                      Apply to Volunteer
                    </button>
                  ) : isEventFull(event.maxVolunteers, event.currentVolunteers) ? (
                    <button className="view-event-btn disabled" disabled>
                      <Users />
                      Event Full
                    </button>
                  ) : (
                    <button className="view-event-btn disabled" disabled>
                      <Clock />
                      Event Ended
                    </button>
                  )}
                  <button 
                    className="view-event-btn secondary"
                    onClick={handleShareEvent}
                  >
                    <Share2 />
                    Share Event
                  </button>
                </div>
              </div>
            </div>
          </div>

          {/* Event Content */}
          <div className="view-event-content">
            <div className="view-event-main">
              {/* About Section */}
              <div className="view-event-section">
                <h2 className="view-event-section-title">About This Event</h2>
                <div className="view-event-section-content">
                  <p className="view-event-description">
                    {event.description || "No description available."}
                  </p>
                </div>
              </div>

              {/* Requirements Section */}
              {getRequirementsArray(event.requirements).length > 0 && (
                <div className="view-event-section">
                  <h2 className="view-event-section-title">Requirements</h2>
                  <div className="view-event-section-content">
                    <div className="view-event-tags">
                      {getRequirementsArray(event.requirements).map(
                        (requirement, index) => (
                          <span key={index} className="view-event-tag">
                            <CheckCircle />
                            {requirement}
                          </span>
                        )
                      )}
                    </div>
                  </div>
                </div>
              )}

              {/* What You'll Do Section */}
              {event.estimatedHours && (
                <div className="view-event-section">
                  <h2 className="view-event-section-title">What You'll Do</h2>
                  <div className="view-event-section-content">
                    <div className="view-event-activities">
                      <div className="view-event-activity-item">
                        <Target />
                        <div>
                          <span className="activity-label">Time Commitment</span>
                          <span className="activity-value">
                            {formatDuration(event.startDate, event.endDate, event.estimatedHours)}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              )}
            </div>

            {/* Sidebar */}
            <div className="view-event-sidebar">
              {/* Event Details */}
              <div className="view-event-sidebar-card">
                <h3 className="view-event-sidebar-title">Event Details</h3>
                <div className="view-event-details">
                  <div className="view-event-detail-item">
                    <Calendar />
                    <div>
                      <span className="detail-label">Date & Time</span>
                      <span className="detail-value">
                        {formatEventDate(event.startDate, event.endDate)}
                      </span>
                    </div>
                  </div>

                  {!event.isVirtual && event.address && (
                    <div className="view-event-detail-item">
                      <MapPin />
                      <div>
                        <span className="detail-label">Location</span>
                        <span className="detail-value">
                          {event.address}
                          {event.city && `, ${event.city}`}
                          {event.state && `, ${event.state}`}
                          {event.zipCode && ` ${event.zipCode}`}
                        </span>
                      </div>
                    </div>
                  )}

                  {event.isVirtual && event.virtualMeetingLink && (
                    <div className="view-event-detail-item">
                      <Globe />
                      <div>
                        <span className="detail-label">Virtual Meeting</span>
                        <a 
                          href={event.virtualMeetingLink}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="detail-link"
                        >
                          Join Online Meeting
                        </a>
                      </div>
                    </div>
                  )}

                  <div className="view-event-detail-item">
                    <Users />
                    <div>
                      <span className="detail-label">Volunteers Needed</span>
                      <span className="detail-value">
                        {event.currentVolunteers || 0}
                        {event.maxVolunteers ? ` of ${event.maxVolunteers}` : "+"}
                      </span>
                    </div>
                  </div>
                </div>
              </div>

              {/* Contact Information */}
              <div className="view-event-sidebar-card">
                <h3 className="view-event-sidebar-title">Contact Information</h3>
                <div className="view-event-contact">
                  {event.contactEmail && (
                    <div className="view-event-contact-item">
                      <Mail />
                      <a href={`mailto:${event.contactEmail}`}>
                        Send Email
                      </a>
                    </div>
                  )}

                  {event.contactPhone && (
                    <div className="view-event-contact-item">
                      <Phone />
                      <a href={`tel:${event.contactPhone}`}>
                        {event.contactPhone}
                      </a>
                    </div>
                  )}

                  {event.organizationName && (
                    <div className="view-event-contact-item">
                      <Building />
                      <span>Organized by {event.organizationName}</span>
                    </div>
                  )}
                </div>
              </div>

              {/* Event Stats */}
              <div className="view-event-sidebar-card">
                <h3 className="view-event-sidebar-title">Event Stats</h3>
                <div className="view-event-stats">
                  <div className="view-event-stat">
                    <UserCheck />
                    <div>
                      <span className="stat-number">
                        {event.currentVolunteers || 0}
                      </span>
                      <span className="stat-label">Volunteers Registered</span>
                    </div>
                  </div>

                  <div className="view-event-stat">
                    <Clock />
                    <div>
                      <span className="stat-number">
                        {event.estimatedHours || "TBA"}
                      </span>
                      <span className="stat-label">Estimated Hours</span>
                    </div>
                  </div>

                  {event.maxVolunteers && (
                    <div className="view-event-stat">
                      <Target />
                      <div>
                        <span className="stat-number">
                          {Math.round(
                            ((event.currentVolunteers || 0) / event.maxVolunteers) * 100
                          )}%
                        </span>
                        <span className="stat-label">Capacity Filled</span>
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
    console.error("Error rendering ViewEvent:", renderError);
    return (
      <div className="view-event-page">
        <div className="view-event-error">
          <p>Error rendering event details: {renderError.message}</p>
          <button onClick={handleBack} className="view-event-back-btn">
            Back to Events
          </button>
        </div>
      </div>
    );
  }
};

export default ViewEvent;