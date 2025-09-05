import React, { useState, useEffect, useCallback, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import {
  Search,
  MapPin,
  Filter,
  Calendar,
  Clock,
  Users,
  ExternalLink,
  Star,
  ChevronLeft,
  ChevronRight,
  RefreshCw,
} from "lucide-react";
import "./Events.css";
import findEventsService from "../services/findEventsService";

const Events = () => {
  const navigate = useNavigate();

  const [searchTerm, setSearchTerm] = useState("");
  const [locationSearchTerm, setLocationSearchTerm] = useState("");
  const [selectedEventTypes, setSelectedEventTypes] = useState([]);
  const [selectedLocations, setSelectedLocations] = useState([]);
  const [selectedDates, setSelectedDates] = useState([]);
  const [selectedTimes, setSelectedTimes] = useState([]);
  const [selectedDurations, setSelectedDurations] = useState([]);
  const [selectedSkillLevels, setSelectedSkillLevels] = useState([]);
  const [showMoreEventTypes, setShowMoreEventTypes] = useState(false);
  const [showMoreLocations, setShowMoreLocations] = useState(false);

  // Backend data state
  const [events, setEvents] = useState([]);
  const [filteredEvents, setFilteredEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchLoading, setSearchLoading] = useState(false);
  const [error, setError] = useState(null);
  const [lastRefresh, setLastRefresh] = useState(new Date());

  // Pagination state
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(20);
  const [totalPages, setTotalPages] = useState(1);
  const [paginatedEvents, setPaginatedEvents] = useState([]);

  // Search debounce
  const [searchDebounceTimer, setSearchDebounceTimer] = useState(null);

  // ADD THIS FUNCTION FOR HANDLING CARD CLICKS
  const handleEventCardClick = (eventId) => {
    navigate(`/find-events/${eventId}`);
  };

  // Enum-to-Display-Name Translation Function
  const getEventTypeDisplayName = (enumValue) => {
    const eventTypeMap = {
      COMMUNITY_CLEANUP: "Community Cleanup",
      FOOD_SERVICE: "Food Service",
      TUTORING_EDUCATION: "Tutoring & Education",
      ANIMAL_CARE: "Animal Care",
      ENVIRONMENTAL_CONSERVATION: "Environmental Conservation",
      SENIOR_SUPPORT: "Senior Support",
      YOUTH_MENTORING: "Youth Mentoring",
      HEALTHCARE_SUPPORT: "Healthcare Support",
      DISASTER_RELIEF: "Disaster Relief",
      ARTS_CULTURE: "Arts & Culture",
      SPORTS_RECREATION: "Sports & Recreation",
      FUNDRAISING: "Fundraising",
      ADMINISTRATIVE_SUPPORT: "Administrative Support",
      CONSTRUCTION_BUILDING: "Construction & Building",
      TECHNOLOGY_SUPPORT: "Technology Support",
      TECHNOLOGY_DIGITAL: "Technology & Digital",
      EVENT_PLANNING: "Event Planning",
      ADVOCACY_AWARENESS: "Advocacy & Awareness",
      RESEARCH_DATA: "Research & Data",
      TRANSPORTATION: "Transportation",
      GARDENING: "Gardening",
      CRISIS_SUPPORT: "Crisis Support",
      FESTIVAL_FAIR: "Festival & Fair",
      WORKSHOP_TRAINING: "Workshop & Training",
      BLOOD_DRIVE: "Blood Drive",
      COMMUNITY_BUILDING: "Community Building",
      OTHER: "Other"
    };

    return eventTypeMap[enumValue] || enumValue;
  };

  // Event types/categories (display names for UI)
  const eventTypes = [
    "Community Cleanup",
    "Food Service",
    "Tutoring & Education",
    "Animal Care",
    "Environmental Conservation",
    "Senior Support",
    "Youth Mentoring",
    "Healthcare Support",
    "Disaster Relief",
    "Arts & Culture",
    "Sports & Recreation",
    "Fundraising",
    "Administrative Support",
    "Construction & Building",
    "Technology Support",
    "Event Planning",
    "Advocacy & Awareness",
    "Research & Data",
    "Transportation",
    "Gardening",
    "Crisis Support",
    "Festival & Fair",
    "Workshop & Training",
    "Blood Drive",
    "Other",
  ];

  const locations = [
    "Virtual/Remote",
    "New York, NY",
    "Los Angeles, CA",
    "Chicago, IL",
    "Toronto, ON",
    "Vancouver, BC",
    "Montreal, QC",
    "London, UK",
    "Manchester, UK",
    "Edinburgh, UK",
    "Sydney, NSW",
    "Melbourne, VIC",
    "Brisbane, QLD",
    "Berlin, Germany",
    "Munich, Germany",
    "Hamburg, Germany",
    "Paris, France",
    "Lyon, France",
    "Marseille, France",
    "Amsterdam, Netherlands",
    "Rotterdam, Netherlands",
    "The Hague, Netherlands",
    "Stockholm, Sweden",
    "Gothenburg, Sweden",
    "Malmö, Sweden",
    "Copenhagen, Denmark",
    "Aarhus, Denmark",
    "Odense, Denmark",
    "Dublin, Ireland",
    "Cork, Ireland",
    "Galway, Ireland",
    "Zurich, Switzerland",
    "Geneva, Switzerland",
    "Basel, Switzerland",
    "Other"
  ];
  
  const dateOptions = [
    "Today",
    "Tomorrow",
    "This Week",
    "Next Week",
    "This Weekend",
    "Next Weekend",
    "This Month",
    "Next Month",
    "Next 3 Months",
    "Custom Date Range",
  ];

  const timeOptions = [
    "Morning (6AM-12PM)",
    "Afternoon (12PM-6PM)",
    "Evening (6PM-10PM)",
    "Weekdays Only",
    "Weekends Only",
    "Flexible Timing",
  ];

  const durationOptions = [
    "1-2 Hours",
    "3-4 Hours",
    "5-8 Hours (Full Day)",
    "Multi-Day Event",
    "Weekly Commitment",
    "Monthly Commitment",
    "Ongoing/Long-term",
  ];

  const skillLevels = [
    "No Experience Required",
    "Beginner Friendly",
    "Some Experience Preferred",
    "Experienced Volunteers",
    "Specialized Skills Required",
    "Training Provided",
  ];

  // Helper function to get event type CSS class (works with display names)
  const getEventTypeClass = (eventType) => {
    if (!eventType) return "";

    const typeMap = {
      "Community Cleanup": "community-cleanup",
      "Food Service": "food-service",
      "Tutoring & Education": "tutoring-education",
      "Animal Care": "animal-care",
      "Environmental Conservation": "environmental-conservation",
      "Senior Support": "senior-support",
      "Youth Mentoring": "youth-mentoring",
      "Healthcare Support": "healthcare-support",
      "Disaster Relief": "disaster-relief",
      "Arts & Culture": "arts-culture",
      "Sports & Recreation": "sports-recreation",
      Fundraising: "fundraising",
      "Administrative Support": "administrative-support",
      "Construction & Building": "construction-building",
      "Technology Support": "technology-digital",
      "Technology & Digital": "technology-digital",
      "Event Planning": "event-planning",
      "Advocacy & Awareness": "advocacy-awareness",
      "Research & Data": "research-data",
      Transportation: "transportation",
      Gardening: "gardening",
      "Crisis Support": "crisis-support",
      "Festival & Fair": "festival-fair",
      "Workshop & Training": "workshop-training",
      "Blood Drive": "blood-drive",
      "Community Building": "community-building",
      Other: "other",
    };

    return typeMap[eventType] || "";
  };

  const getDateFilterClass = (dateOption) => {
    const dateClassMap = {
      Today: "date-today",
      Tomorrow: "date-tomorrow",
      "This Week": "date-this-week",
      "This Weekend": "date-this-weekend",
      "Next Week": "date-next-week",
      "Next Weekend": "date-next-weekend",
      "This Month": "date-this-month",
      "Next Month": "date-next-month",
      "Next 3 Months": "date-next-3-months",
      "Custom Date Range": "date-custom-date-range",
    };

    return dateClassMap[dateOption] || "";
  };

  const applyClientSideFilters = useCallback((events, params) => {
    let filtered = [...events];

    // Apply event type filters (multiple types)
    if (params.eventTypes && params.eventTypes.length > 0) {
      filtered = filtered.filter((event) => {
        const eventTypeDisplayName = getEventTypeDisplayName(event.eventType);
        return params.eventTypes.some(type =>
          eventTypeDisplayName?.toLowerCase().includes(type.toLowerCase()) ||
          event.eventType?.toLowerCase().includes(type.toLowerCase()) ||
          event.title?.toLowerCase().includes(type.toLowerCase())
        );
      });
    }

    // Apply location filters
    if (params.locations && params.locations.length > 0) {
      filtered = filtered.filter((event) =>
        params.locations.some(location => {
          if (location === "Virtual/Remote") {
            return event.isVirtual;
          }
          return event.location?.includes(location) ||
                 event.city?.includes(location) ||
                 event.state?.includes(location);
        })
      );
    }

    // Apply date filters
    if (params.dates && params.dates.length > 0) {
      filtered = filtered.filter((event) => {
        if (!event.startDate) return false;
        
        const eventDate = new Date(event.startDate);
        const now = new Date();

        return params.dates.some((option) => {
          switch (option) {
            case "Today":
              return eventDate.toDateString() === now.toDateString();
            case "Tomorrow":
              const tomorrow = new Date(now);
              tomorrow.setDate(tomorrow.getDate() + 1);
              return eventDate.toDateString() === tomorrow.toDateString();
            case "This Week":
              const weekEnd = new Date(now);
              weekEnd.setDate(weekEnd.getDate() + 7);
              return eventDate >= now && eventDate <= weekEnd;
            case "Next Week":
              const nextWeekStart = new Date(now);
              nextWeekStart.setDate(nextWeekStart.getDate() + 7);
              const nextWeekEnd = new Date(now);
              nextWeekEnd.setDate(nextWeekEnd.getDate() + 14);
              return eventDate >= nextWeekStart && eventDate <= nextWeekEnd;
            case "This Weekend":
              const dayOfWeek = eventDate.getDay();
              const daysUntilWeekend = 6 - now.getDay(); // Days until Saturday
              const weekendStart = new Date(now);
              weekendStart.setDate(now.getDate() + daysUntilWeekend);
              const weekendEnd = new Date(weekendStart);
              weekendEnd.setDate(weekendStart.getDate() + 1); // Sunday
              return (dayOfWeek === 0 || dayOfWeek === 6) && 
                     eventDate >= weekendStart && eventDate <= weekendEnd;
            case "Next Weekend":
              const nextWeekendStart = new Date(now);
              nextWeekendStart.setDate(now.getDate() + 7 + (6 - now.getDay()));
              const nextWeekendEnd = new Date(nextWeekendStart);
              nextWeekendEnd.setDate(nextWeekendStart.getDate() + 1);
              const nextDayOfWeek = eventDate.getDay();
              return (nextDayOfWeek === 0 || nextDayOfWeek === 6) && 
                     eventDate >= nextWeekendStart && eventDate <= nextWeekendEnd;
            case "This Month":
              return (
                eventDate.getMonth() === now.getMonth() &&
                eventDate.getFullYear() === now.getFullYear()
              );
            case "Next Month":
              const nextMonth = new Date(now);
              nextMonth.setMonth(nextMonth.getMonth() + 1);
              return (
                eventDate.getMonth() === nextMonth.getMonth() &&
                eventDate.getFullYear() === nextMonth.getFullYear()
              );
            case "Next 3 Months":
              const threeMonthsLater = new Date(now);
              threeMonthsLater.setMonth(now.getMonth() + 3);
              return eventDate >= now && eventDate <= threeMonthsLater;
            default:
              return true;
          }
        });
      });
    }

    // Apply time filters
    if (params.times && params.times.length > 0) {
      filtered = filtered.filter((event) => {
        if (!event.startDate) return false;
        
        const eventTime = new Date(event.startDate).getHours();
        const eventDay = new Date(event.startDate).getDay();

        return params.times.some((timeOption) => {
          switch (timeOption) {
            case "Morning (6AM-12PM)":
              return eventTime >= 6 && eventTime < 12;
            case "Afternoon (12PM-6PM)":
              return eventTime >= 12 && eventTime < 18;
            case "Evening (6PM-10PM)":
              return eventTime >= 18 && eventTime < 22;
            case "Weekdays Only":
              return eventDay >= 1 && eventDay <= 5;
            case "Weekends Only":
              return eventDay === 0 || eventDay === 6;
            case "Flexible Timing":
              return event.hasFlexibleTiming || event.isFlexible;
            default:
              return true;
          }
        });
      });
    }

    // Apply duration filters
    if (params.durations && params.durations.length > 0) {
      filtered = filtered.filter((event) => {
        const duration = event.estimatedHours || event.duration || 0;

        return params.durations.some((durationOption) => {
          switch (durationOption) {
            case "1-2 Hours":
              return duration >= 1 && duration <= 2;
            case "3-4 Hours":
              return duration >= 3 && duration <= 4;
            case "5-8 Hours (Full Day)":
              return duration >= 5 && duration <= 8;
            case "Multi-Day Event":
              return duration > 8 || event.durationCategory === "MULTI_DAY";
            case "Weekly Commitment":
              return event.durationCategory === "WEEKLY_COMMITMENT";
            case "Monthly Commitment":
              return event.durationCategory === "MONTHLY_COMMITMENT";
            case "Ongoing/Long-term":
              return event.durationCategory === "ONGOING_LONG_TERM";
            default:
              return true;
          }
        });
      });
    }

    // Apply skill level filters
    if (params.skillLevels && params.skillLevels.length > 0) {
      filtered = filtered.filter((event) =>
        params.skillLevels.some((skill) => {
          const skillLower = skill.toLowerCase();
          const eventSkill = (event.skillLevelRequired || event.skillLevel || '').toLowerCase();
          const requirements = (event.requirements || '').toLowerCase();
          
          return eventSkill.includes(skillLower) || 
                 requirements.includes(skillLower) ||
                 (skillLower.includes('no experience') && (eventSkill.includes('beginner') || eventSkill.includes('no experience')));
        })
      );
    }

    // Apply additional location term filter
    if (params.locationTerm) {
      const locationLower = params.locationTerm.toLowerCase();
      filtered = filtered.filter((event) =>
        event.location?.toLowerCase().includes(locationLower) ||
        event.city?.toLowerCase().includes(locationLower) ||
        event.state?.toLowerCase().includes(locationLower) ||
        event.zipCode?.includes(params.locationTerm) ||
        event.address?.toLowerCase().includes(locationLower)
      );
    }

    return filtered;
  }, []);

  // FIXED: performSearch with empty dependency array
  const performSearch = useCallback(
    async (searchParams = {}, delay = 0) => {
      // Clear any existing timer
      if (searchDebounceTimer) {
        clearTimeout(searchDebounceTimer);
        setSearchDebounceTimer(null);
      }

      const executeSearch = async () => {
        try {
          setSearchLoading(true);
          setError(null);

          const {
            searchTerm: term = searchTerm,
            locationTerm = locationSearchTerm,
            eventTypes = selectedEventTypes,
            locations = selectedLocations,
            dates = selectedDates,
            times = selectedTimes,
            durations = selectedDurations,
            skillLevels = selectedSkillLevels,
            forceRefresh = false,
          } = searchParams;

          console.log("Performing real-time search with params:", {
            term,
            locationTerm,
            eventTypes: eventTypes.length,
            locations: locations.length,
            hasFilters: dates.length > 0 || times.length > 0 || durations.length > 0 || skillLevels.length > 0,
            forceRefresh
          });

          let searchResult;

          // Check if we have search terms or need real-time data
          if (term || locationTerm || eventTypes.length > 0 || forceRefresh) {
            const realTimeSearchParams = {
              searchTerm: term,
              location: locationTerm,
              eventType: eventTypes.length > 0 ? eventTypes[0] : '',
              forceRefresh: forceRefresh,
              limit: 200 // Get more results for better filtering
            };

            console.log("Using real-time search with params:", realTimeSearchParams);
            searchResult = await findEventsService.performRealtimeSearch(realTimeSearchParams);
          } else {
            // No search terms, get all events with real-time data
            console.log("Getting all events with real-time refresh");
            const allEventsData = await findEventsService.findAllEvents();
            searchResult = {
              data: allEventsData || [],
              timestamp: new Date().toISOString(),
              resultCount: allEventsData.length,
              source: "all_events"
            };
          }

          let filteredResults = searchResult.data || [];

          filteredResults = applyClientSideFilters(filteredResults, {
            eventTypes,
            locations,
            dates,
            times,
            durations,
            skillLevels,
            locationTerm
          });

          console.log(`Real-time search completed: ${searchResult.resultCount} -> ${filteredResults.length} events`);

          setEvents(filteredResults);
          setFilteredEvents(filteredResults);
          setCurrentPage(1);

          // Update last refresh time
          if (searchResult.timestamp) {
            setLastRefresh(new Date(searchResult.timestamp));
          }

        } catch (err) {
          console.error("Real-time search failed:", err);
          setError("Search failed. Please try again.");

          // Fallback: try to get all events
          try {
            console.log("Attempting fallback to all events...");
            const fallbackEvents = await findEventsService.findAllEvents();
            const fallbackFiltered = applyClientSideFilters(fallbackEvents, searchParams);
            
            setEvents(fallbackFiltered);
            setFilteredEvents(fallbackFiltered);
            setCurrentPage(1);
            console.log("Fallback successful");
          } catch (fallbackErr) {
            console.error("Fallback also failed:", fallbackErr);
            setEvents([]);
            setFilteredEvents([]);
          }
        } finally {
          setSearchLoading(false);
        }
      };

      if (delay > 0) {
        const timer = setTimeout(() => {
          executeSearch();
          setSearchDebounceTimer(null);
        }, delay);
        setSearchDebounceTimer(timer);
      } else {
        await executeSearch();
      }
    },
    [] // FIXED: Empty dependency array
  );

  const handleRefresh = async () => {
    console.log("Manual real-time refresh triggered");
    setLastRefresh(new Date());
    
    try {
      setSearchLoading(true);
      setError(null);

      // Use the live data refresh method from the service
      const refreshResult = await findEventsService.refreshLiveData({
        force: true,
        maxAgeMinutes: 0, // Force fresh data
        includeStats: true,
        limit: 200
      });

      console.log("Live refresh completed:", refreshResult);

      if (refreshResult.success) {
        let allEvents = refreshResult.events || [];
        
        // Apply current filters to the refreshed data
        const filteredResults = applyClientSideFilters(allEvents, {
          eventTypes: selectedEventTypes,
          locations: selectedLocations,
          dates: selectedDates,
          times: selectedTimes,
          durations: selectedDurations,
          skillLevels: selectedSkillLevels,
          locationTerm: locationSearchTerm
        });

        setEvents(filteredResults);
        setFilteredEvents(filteredResults);
        setCurrentPage(1);
        
        if (refreshResult.refreshTimestamp) {
          setLastRefresh(new Date(refreshResult.refreshTimestamp));
        }
      } else {
        throw new Error(refreshResult.error || "Refresh failed");
      }
    } catch (err) {
      console.error("Manual refresh failed:", err);
      setError("Refresh failed. Please try again.");
      
      // Fallback to regular search
      await performSearch({
        searchTerm,
        locationTerm: locationSearchTerm,
        eventTypes: selectedEventTypes,
        locations: selectedLocations,
        dates: selectedDates,
        times: selectedTimes,
        durations: selectedDurations,
        skillLevels: selectedSkillLevels,
        forceRefresh: true
      });
    } finally {
      setSearchLoading(false);
    }
  };

  // Load events on component mount
  useEffect(() => {
    loadEvents();
  }, []);

  // FIXED: Remove performSearch from dependency array
  useEffect(() => {
    const searchParams = {
      searchTerm,
      locationTerm: locationSearchTerm,
      eventTypes: selectedEventTypes,
      locations: selectedLocations,
      dates: selectedDates,
      times: selectedTimes,
      durations: selectedDurations,
      skillLevels: selectedSkillLevels,
    };

    // Use debounced search for text inputs, immediate for filters
    if (searchTerm || locationSearchTerm) {
      performSearch(searchParams, 500); // 500ms delay for text
    } else {
      performSearch(searchParams, 0); // Immediate for filters
    }
  }, [
    searchTerm,
    locationSearchTerm,
    selectedEventTypes,
    selectedLocations,
    selectedDates,
    selectedTimes,
    selectedDurations,
    selectedSkillLevels
    // FIXED: Removed performSearch from dependencies
  ]);

  // Update pagination when filtered results change
  useEffect(() => {
    updatePagination();
  }, [filteredEvents, currentPage]);

  const loadEvents = async () => {
    try {
      setLoading(true);
      setError(null);
      console.log("Initial load with real-time events");
      
      // Use smart search for initial load to get fresh data
      const searchResult = await findEventsService.smartSearch({});
      const eventsData = searchResult.data || [];
      
      setEvents(eventsData);
      setFilteredEvents(eventsData);
      if (searchResult.timestamp) {
        setLastRefresh(new Date(searchResult.timestamp));
      }
    } catch (err) {
      console.error("Failed to load events:", err);
      setError("Failed to load events. Please try again later.");
    } finally {
      setLoading(false);
    }
  };

  const updatePagination = () => {
    const total = Math.ceil(filteredEvents.length / itemsPerPage);
    setTotalPages(total);

    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const paginated = filteredEvents.slice(startIndex, endIndex);
    setPaginatedEvents(paginated);
  };

  const handlePageChange = (page) => {
    if (page >= 1 && page <= totalPages) {
      setCurrentPage(page);
      // Scroll to top of results
      document
        .querySelector(".events-main")
        ?.scrollIntoView({ behavior: "smooth" });
    }
  };

  const getPageNumbers = () => {
    const delta = 2;
    const range = [];
    const rangeWithDots = [];

    for (
      let i = Math.max(2, currentPage - delta);
      i <= Math.min(totalPages - 1, currentPage + delta);
      i++
    ) {
      range.push(i);
    }

    if (currentPage - delta > 2) {
      rangeWithDots.push(1, "...");
    } else {
      rangeWithDots.push(1);
    }

    rangeWithDots.push(...range);

    if (currentPage + delta < totalPages - 1) {
      rangeWithDots.push("...", totalPages);
    } else if (totalPages > 1) {
      rangeWithDots.push(totalPages);
    }

    return rangeWithDots;
  };

  const handleEventTypeToggle = (type) => {
    const newEventTypes = selectedEventTypes.includes(type)
      ? selectedEventTypes.filter((t) => t !== type)
      : [...selectedEventTypes, type];
    
    setSelectedEventTypes(newEventTypes);
  };

  const handleLocationToggle = (location) => {
    const newLocations = selectedLocations.includes(location)
      ? selectedLocations.filter((l) => l !== location)
      : [...selectedLocations, location];
      
    setSelectedLocations(newLocations);
  };

  const handleDateToggle = (date) => {
    const newDates = selectedDates.includes(date)
      ? selectedDates.filter((d) => d !== date)
      : [...selectedDates, date];
      
    setSelectedDates(newDates);
  };

  const handleTimeToggle = (time) => {
    const newTimes = selectedTimes.includes(time)
      ? selectedTimes.filter((t) => t !== time)
      : [...selectedTimes, time];
      
    setSelectedTimes(newTimes);
  };

  const handleDurationToggle = (duration) => {
    const newDurations = selectedDurations.includes(duration)
      ? selectedDurations.filter((d) => d !== duration)
      : [...selectedDurations, duration];
      
    setSelectedDurations(newDurations);
  };

  const handleSkillLevelToggle = (skill) => {
    const newSkillLevels = selectedSkillLevels.includes(skill)
      ? selectedSkillLevels.filter((s) => s !== skill)
      : [...selectedSkillLevels, skill];
      
    setSelectedSkillLevels(newSkillLevels);
  };

  const clearAllFilters = () => {
    setSelectedEventTypes([]);
    setSelectedLocations([]);
    setSelectedDates([]);
    setSelectedTimes([]);
    setSelectedDurations([]);
    setSelectedSkillLevels([]);
    setSearchTerm("");
    setLocationSearchTerm("");
  };

  const hasActiveFilters = useMemo(
    () =>
      selectedEventTypes.length > 0 ||
      selectedLocations.length > 0 ||
      selectedDates.length > 0 ||
      selectedTimes.length > 0 ||
      selectedDurations.length > 0 ||
      selectedSkillLevels.length > 0 ||
      searchTerm ||
      locationSearchTerm,
    [
      selectedEventTypes,
      selectedLocations,
      selectedDates,
      selectedTimes,
      selectedDurations,
      selectedSkillLevels,
      searchTerm,
      locationSearchTerm,
    ]
  );

  // Enhanced helper functions for tags
  const getDateBadge = (dateStr) => {
    if (!dateStr) return { text: "TBD", class: "default" };
    
    const eventDate = new Date(dateStr);
    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);

    const eventDateOnly = new Date(
      eventDate.getFullYear(),
      eventDate.getMonth(),
      eventDate.getDate()
    );

    if (eventDateOnly.getTime() === today.getTime()) {
      return { text: "Today", class: "today" };
    } else if (eventDateOnly.getTime() === tomorrow.getTime()) {
      return { text: "Tomorrow", class: "tomorrow" };
    }

    const diffDays = Math.ceil((eventDateOnly - today) / (1000 * 60 * 60 * 24));

    if (diffDays <= 7) {
      const isWeekend = eventDate.getDay() === 0 || eventDate.getDay() === 6;
      if (isWeekend) {
        return { text: "This Weekend", class: "this-weekend" };
      } else {
        return { text: "This Week", class: "this-week" };
      }
    } else if (diffDays <= 14) {
      const isWeekend = eventDate.getDay() === 0 || eventDate.getDay() === 6;
      if (isWeekend) {
        return { text: "Next Weekend", class: "next-weekend" };
      } else {
        return { text: "Next Week", class: "next-week" };
      }
    } else if (diffDays <= 30) {
      return { text: "This Month", class: "this-month" };
    }

    return {
      text: eventDate.toLocaleDateString("en-US", {
        month: "short",
        day: "numeric",
      }),
      class: "default",
    };
  };

  const getDurationText = (event) => {
    const duration = event.estimatedHours || event.duration || 0;

    if (duration >= 1 && duration <= 2) {
      return "1-2 Hours";
    } else if (duration >= 3 && duration <= 4) {
      return "3-4 Hours";
    } else if (duration >= 5 && duration <= 8) {
      return "Full Day";
    } else if (duration > 8) {
      return "Multi-Day";
    } else {
      return "Flexible";
    }
  };

  const getSkillLevelText = (event) => {
    const skillLevel = event.skillLevelRequired || event.skillLevel;

    if (!skillLevel) {
      return "All Levels";
    }

    const skillLower = skillLevel.toLowerCase();

    if (
      skillLower.includes("no experience") ||
      skillLower.includes("beginner")
    ) {
      return "Beginner";
    } else if (
      skillLower.includes("some experience") ||
      skillLower.includes("intermediate")
    ) {
      return "Intermediate";
    } else if (
      skillLower.includes("experienced") ||
      skillLower.includes("advanced")
    ) {
      return "Advanced";
    } else if (
      skillLower.includes("specialized") ||
      skillLower.includes("expert")
    ) {
      return "Expert";
    } else {
      return "All Levels";
    }
  };

  const formatEventTime = (dateStr) => {
    if (!dateStr) return "TBD";
    
    const date = new Date(dateStr);
    return date.toLocaleTimeString("en-US", {
      hour: "numeric",
      minute: "2-digit",
      hour12: true,
    });
  };

  return (
    <div className="events-page">
      <div className="events-container">
        {/* Left Sidebar - Filters */}
        <div className="events-sidebar">
          <div className="events-filter-card">
            <h2 className="events-filter-title">
              <Filter />
              Filter Events
            </h2>

            {/* Event Type Filter */}
            <div className="events-filter-section">
              <h3 className="events-filter-section-title">Event Type</h3>
              <div className="events-filter-options">
                {eventTypes
                  .slice(0, showMoreEventTypes ? eventTypes.length : 8)
                  .map((type) => (
                    <div
                      key={type}
                      className={`events-filter-option ${
                        selectedEventTypes.includes(type)
                          ? `active event-type-${getEventTypeClass(type)}`
                          : ""
                      }`}
                      onClick={() => handleEventTypeToggle(type)}
                    >
                      {type}
                    </div>
                  ))}
              </div>
              {eventTypes.length > 8 && (
                <button
                  onClick={() => setShowMoreEventTypes(!showMoreEventTypes)}
                  className="events-show-more-btn"
                >
                  {showMoreEventTypes ? "Show Less" : "Show More"}
                </button>
              )}
            </div>

            {/* Date Filter */}
            <div className="events-filter-section">
              <h3 className="events-filter-section-title">Date</h3>
              <div className="events-filter-options">
                {dateOptions.map((date) => (
                  <div
                    key={date}
                    className={`events-filter-option date ${
                      selectedDates.includes(date)
                        ? `active ${getDateFilterClass(date)}`
                        : ""
                    }`}
                    onClick={() => handleDateToggle(date)}
                  >
                    <Calendar className="events-filter-option-icon" />
                    {date}
                  </div>
                ))}
              </div>
            </div>

            {/* Time Filter */}
            <div className="events-filter-section">
              <h3 className="events-filter-section-title">Time of Day</h3>
              <div className="events-filter-options">
                {timeOptions.map((time) => (
                  <div
                    key={time}
                    className={`events-filter-option time ${
                      selectedTimes.includes(time) ? "active" : ""
                    }`}
                    onClick={() => handleTimeToggle(time)}
                  >
                    <Clock className="events-filter-option-icon" />
                    {time}
                  </div>
                ))}
              </div>
            </div>

            {/* Duration Filter */}
            <div className="events-filter-section">
              <h3 className="events-filter-section-title">Duration</h3>
              <div className="events-filter-options">
                {durationOptions.map((duration) => (
                  <div
                    key={duration}
                    className={`events-filter-option duration ${
                      selectedDurations.includes(duration) ? "active" : ""
                    }`}
                    onClick={() => handleDurationToggle(duration)}
                  >
                    {duration}
                  </div>
                ))}
              </div>
            </div>

            {/* Location Filter */}
            <div className="events-filter-section">
              <h3 className="events-filter-section-title">Location</h3>
              <div className="events-filter-options">
                {locations
                  .slice(0, showMoreLocations ? locations.length : 6)
                  .map((location) => (
                    <div
                      key={location}
                      className={`events-filter-option location ${
                        selectedLocations.includes(location) ? "active" : ""
                      }`}
                      onClick={() => handleLocationToggle(location)}
                    >
                      <MapPin className="events-filter-option-icon" />
                      {location}
                    </div>
                  ))}
              </div>
              {locations.length > 6 && (
                <button
                  onClick={() => setShowMoreLocations(!showMoreLocations)}
                  className="events-show-more-btn"
                >
                  {showMoreLocations ? "Show Less" : "Show More"}
                </button>
              )}
            </div>

            {/* Skill Level Filter */}
            <div className="events-filter-section">
              <h3 className="events-filter-section-title">Skill Level</h3>
              <div className="events-filter-options">
                {skillLevels.map((skill) => (
                  <div
                    key={skill}
                    className={`events-filter-option skill ${
                      selectedSkillLevels.includes(skill) ? "active" : ""
                    }`}
                    onClick={() => handleSkillLevelToggle(skill)}
                  >
                    {skill}
                  </div>
                ))}
              </div>
            </div>

            {/* Clear Filters Button */}
            {hasActiveFilters && (
              <button
                onClick={clearAllFilters}
                className="events-clear-filters-btn"
              >
                Clear All Filters
              </button>
            )}
          </div>
        </div>

        {/* Main Content */}
        <div className="events-main">
          {/* Header */}
          <div className="events-header">
            <div>
              <h1 className="events-title">Volunteer Events</h1>
              <p className="events-subtitle">
                {loading
                  ? "Loading..."
                  : `${filteredEvents.length} events available`}{" "}
                •
                {!loading && totalPages > 0 && (
                  <>
                    Showing {(currentPage - 1) * itemsPerPage + 1}-
                    {Math.min(currentPage * itemsPerPage, filteredEvents.length)}{" "}
                    of {filteredEvents.length} • {" "}
                  </>
                )}
                Last Updated:{" "}
                <span className="events-highlight">
                  {lastRefresh.toLocaleTimeString()}
                </span>
              </p>
            </div>
            {/* Manual refresh button */}
            <button
              onClick={handleRefresh}
              disabled={loading || searchLoading}
              className="events-refresh-btn"
              title="Refresh events from database"
            >
              <RefreshCw
                className={`events-refresh-icon ${
                  loading || searchLoading ? "spinning" : ""
                }`}
              />
              Refresh
            </button>
          </div>

          {/* Search Bar */}
          <div className="events-search-section">
            <div className="events-search-input-container">
              <Search className="events-search-icon" />
              <input
                type="text"
                placeholder="Search events, organizations, or causes..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="events-search-input"
              />
              {searchLoading && <div className="search-loading-spinner"></div>}
            </div>

            {/* Location Search */}
            <div className="events-search-input-container">
              <MapPin className="events-search-icon" />
              <input
                type="text"
                placeholder="Search by city, state, zip code..."
                value={locationSearchTerm}
                onChange={(e) => setLocationSearchTerm(e.target.value)}
                className="events-search-input"
              />
            </div>
          </div>

          {/* Active Filters */}
          {hasActiveFilters && (
            <div className="events-active-filters">
              <div className="events-active-filters-content">
                <Filter className="events-active-filters-icon" />
                <span className="events-active-filters-label">
                  Active Filters:
                </span>

                {selectedEventTypes.map((type) => (
                  <span
                    key={type}
                    className={`events-filter-chip event-type ${getEventTypeClass(
                      type
                    )}`}
                  >
                    {type}
                    <button
                      onClick={() => handleEventTypeToggle(type)}
                      className="events-filter-chip-remove"
                    >
                      ×
                    </button>
                  </span>
                ))}

                {selectedLocations.map((location) => (
                  <span key={location} className="events-filter-chip location">
                    {location}
                    <button
                      onClick={() => handleLocationToggle(location)}
                      className="events-filter-chip-remove"
                    >
                      ×
                    </button>
                  </span>
                ))}

                {selectedDates.map((date) => (
                  <span key={date} className="events-filter-chip date">
                    {date}
                    <button
                      onClick={() => handleDateToggle(date)}
                      className="events-filter-chip-remove"
                    >
                      ×
                    </button>
                  </span>
                ))}

                {selectedTimes.map((time) => (
                  <span key={time} className="events-filter-chip time">
                    {time}
                    <button
                      onClick={() => handleTimeToggle(time)}
                      className="events-filter-chip-remove"
                    >
                      ×
                    </button>
                  </span>
                ))}

                {selectedDurations.map((duration) => (
                  <span key={duration} className="events-filter-chip duration">
                    {duration}
                    <button
                      onClick={() => handleDurationToggle(duration)}
                      className="events-filter-chip-remove"
                    >
                      ×
                    </button>
                  </span>
                ))}

                {selectedSkillLevels.map((skill) => (
                  <span key={skill} className="events-filter-chip skill">
                    {skill}
                    <button
                      onClick={() => handleSkillLevelToggle(skill)}
                      className="events-filter-chip-remove"
                    >
                      ×
                    </button>
                  </span>
                ))}
              </div>
            </div>
          )}

          {/* Loading State */}
          {(loading || searchLoading) && (
            <div className="events-loading">
              <div className="events-loading-spinner"></div>
              <p>{loading ? "Loading events..." : "Searching real-time database..."}</p>
            </div>
          )}

          {/* Error State */}
          {error && (
            <div className="events-error">
              <p>{error}</p>
              <button onClick={loadEvents} className="events-retry-btn">
                Try Again
              </button>
            </div>
          )}

          {/* Events List */}
          {!loading && !error && paginatedEvents.length > 0 && (
            <>
              <div className="events-list">
                {paginatedEvents.map((event) => {
                  const dateBadge = getDateBadge(event.startDate);
                  const displayEventType = getEventTypeDisplayName(
                    event.eventType
                  );

                  return (
                    <div
                      key={event.id}
                      className={`event-card ${
                        event.isVirtual ? "virtual" : ""
                      } ${event.featured ? "featured" : ""} clickable`}
                      onClick={() => handleEventCardClick(event.id)}
                      style={{ cursor: "pointer" }}
                    >
                      {/* Event Card Header with Primary Tags Only */}
                      <div className="event-card-header">
                        {/* Date Tag */}
                        <div
                          className={`event-card-date-badge ${dateBadge.class}`}
                        >
                          {dateBadge.text}
                        </div>

                        {/* Event Type Tag - Uses translated display name */}
                        {event.eventType && (
                          <div
                            className={`event-card-type-tag ${getEventTypeClass(
                              displayEventType
                            )}`}
                          >
                            {displayEventType}
                          </div>
                        )}
                      </div>

                      <h3 className="event-card-title">{event.title}</h3>

                      <div className="event-card-organization">
                        {event.organizationName || "Local Organization"}
                      </div>

                      <div className="event-card-footer">
                        <div className="event-card-location">
                          <MapPin className="event-card-location-icon" />
                          <span>
                            {event.isVirtual
                              ? "Virtual"
                              : event.location || event.city || "Location TBD"}
                          </span>

                          {/* Time Info */}
                          {event.startDate && (
                            <>
                              <span className="event-card-separator">•</span>
                              <span className="event-card-time">
                                {formatEventTime(event.startDate)}
                              </span>
                            </>
                          )}

                          {/* Duration Info */}
                          <span className="event-card-separator">•</span>
                          <span className="event-card-duration-info">
                            {getDurationText(event)}
                          </span>

                          {/* Skill Level Info */}
                          <span className="event-card-separator">•</span>
                          <span className="event-card-skill-info">
                            {getSkillLevelText(event)}
                          </span>
                        </div>

                        <div className="event-card-volunteers">
                          <Users className="event-card-volunteers-icon" />
                          <span className="event-card-volunteers-count">
                            {event.currentVolunteers || 0} volunteers
                          </span>
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>

              {/* Pagination */}
              {totalPages > 1 && (
                <div className="events-pagination">
                  <button
                    onClick={() => handlePageChange(currentPage - 1)}
                    disabled={currentPage === 1}
                    className="events-pagination-btn events-pagination-prev"
                  >
                    <ChevronLeft className="events-pagination-icon" />
                    Previous
                  </button>

                  <div className="events-pagination-numbers">
                    {getPageNumbers().map((pageNum, index) => (
                      <button
                        key={index}
                        onClick={() =>
                          typeof pageNum === "number"
                            ? handlePageChange(pageNum)
                            : null
                        }
                        className={`events-pagination-number ${
                          pageNum === currentPage ? "active" : ""
                        } ${typeof pageNum !== "number" ? "dots" : ""}`}
                        disabled={typeof pageNum !== "number"}
                      >
                        {pageNum}
                      </button>
                    ))}
                  </div>

                  <button
                    onClick={() => handlePageChange(currentPage + 1)}
                    disabled={currentPage === totalPages}
                    className="events-pagination-btn events-pagination-next"
                  >
                    Next
                    <ChevronRight className="events-pagination-icon" />
                  </button>
                </div>
              )}
            </>
          )}

          {/* Empty State */}
          {!loading && !error && filteredEvents.length === 0 && (
            <div className="events-empty-state">
              <div className="events-empty-state-icon">
                <Calendar />
              </div>
              {events.length === 0 ? (
                <>
                  <h3 className="events-empty-state-title">
                    Events Coming Soon!
                  </h3>
                  <p className="events-empty-state-description">
                    We're working hard to bring you amazing volunteer
                    opportunities. Check back soon to discover events that match
                    your interests and schedule.
                  </p>
                </>
              ) : (
                <>
                  <h3 className="events-empty-state-title">
                    No Events Match Your Filters
                  </h3>
                  <p className="events-empty-state-description">
                    Try adjusting your search criteria or clearing some filters
                    to see more events.
                  </p>
                  <button
                    onClick={clearAllFilters}
                    className="events-clear-filters-btn"
                  >
                    Clear All Filters
                  </button>
                </>
              )}
              <div className="events-empty-state-cta">
                <p className="events-empty-state-cta-text">
                  <strong>Are you an organization?</strong> Create and post
                  volunteer events to connect with passionate volunteers in your
                  community.
                </p>
                <button className="events-empty-state-cta-button">
                  Post an Event
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Events;