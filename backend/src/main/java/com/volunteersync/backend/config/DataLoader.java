package com.volunteersync.backend.config;

import com.volunteersync.backend.entity.*;
import com.volunteersync.backend.enums.*;
import com.volunteersync.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationProfileRepository organizationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Random random = new Random();

    // Organization names (150 total to ensure variety)
    private final String[] orgNames = {
            // Education Organizations (30)
            "Future Leaders Academy", "Literacy Champions Network", "STEM Education Foundation",
            "Digital Learning Initiative", "Academic Excellence Society", "Children's Education Fund",
            "Youth Scholarship Foundation", "Learning Support Network", "Educational Technology Alliance",
            "Coding Bootcamp for Kids", "Reading Buddies Program", "School Supply Drive",
            "College Prep Foundation", "Early Learning Center", "After School Programs United",
            "Educational Equity Fund", "Tutoring Heroes Network", "Student Success Coalition",
            "Knowledge Sharing Initiative", "Youth Leadership Institute", "Skills Development Hub",
            "Youth Innovation Lab", "Educational Research Institute", "Adult Learning Center",
            "Multilingual Education Society", "Special Needs Learning Support", "Career Development Center",
            "Academic Mentorship Network", "Educational Technology Hub", "Community Learning Center",

            // Environment Organizations (30)
            "Green Earth Initiative", "Environmental Warriors", "Clean Water Project", 
            "Coastal Conservation Society", "Urban Garden Project", "Climate Action Network",
            "Environmental Education Center", "River Restoration Foundation", "Wildlife Conservation Alliance",
            "Sustainable Future Coalition", "Ocean Cleanup Network", "Forest Protection Society",
            "Green Energy Advocates", "Eco-Friendly Community", "Nature Preservation Trust",
            "Earth Guardians Initiative", "Renewable Resources Group", "Carbon Neutral Alliance",
            "Biodiversity Conservation Fund", "Climate Change Response", "Green Living Foundation",
            "Environmental Health Network", "Pollution Prevention Society", "Ecological Restoration",
            "Clean Air Initiative", "Sustainable Agriculture Network", "Green Transportation Alliance",
            "Environmental Justice Coalition", "Conservation Education Center", "Zero Waste Initiative",

            // Healthcare Organizations (30)
            "Community Health Initiative", "Healthcare Heroes Network", "Medical Assistance Foundation",
            "Public Health Alliance", "Health Education Center", "Medical Outreach Program",
            "Preventive Care Initiative", "Health Screening Network", "Patient Support Alliance",
            "Health Awareness Campaign", "Community Wellness Center", "Health Equity Initiative",
            "Medical Research Foundation", "Health Technology Innovation", "Nutrition Education Hub",
            "Healthcare Access Fund", "Community Medical Center", "Health Advocacy Network",
            "Medical Equipment Foundation", "Primary Care Initiative", "Health Literacy Program",
            "Chronic Disease Support", "Women's Health Alliance", "Men's Health Foundation",
            "Pediatric Care Network", "Mental Health Support Center", "Senior Health Services",
            "Disability Health Services", "Emergency Medical Response", "Health Volunteer Corps",

            // Animal Welfare Organizations (15)
            "Animal Rescue Alliance", "Pet Adoption Network", "Wildlife Protection Society",
            "Animal Shelter Support", "Stray Animal Care Foundation", "Animal Rights Advocacy",
            "Pet Therapy Program", "Animal Rehabilitation Center", "Wildlife Sanctuary Support",
            "Animal Cruelty Prevention", "Pet Food Bank", "Animal Emergency Response",
            "Farm Animal Welfare", "Marine Animal Protection", "Endangered Species Foundation",

            // Community Service Organizations (15)
            "Community Kitchen Network", "Neighborhood Watch Alliance", "Community Development Fund",
            "Local History Preservation", "Emergency Response Volunteers", "Community Safety Patrol",
            "Volunteer Coordination Center", "Community Unity Project", "Neighborhood Improvement Society",
            "Community Building Initiative", "Public Service Fund", "Civic Engagement Network",
            "Community Resource Center", "Social Impact Coalition", "Community Empowerment Hub",

            // Other Categories (30 total)
            "Social Services Alliance", "Human Services Coalition", "Family Support Network",
            "Crisis Intervention Center", "Cultural Bridge Foundation", "Community Arts Collective",
            "Youth Mentorship Alliance", "Young Leaders Foundation", "Senior Services Alliance",
            "Mental Health Support Network", "Technology for Good Foundation", "Sports & Recreation Center",
            "Disaster Relief Coalition", "Veterans Support Network", "Women's Empowerment Initiative",
            "Children & Families First", "Disability Rights Alliance", "Religious Community Center",
            "Political Action Committee", "LGBTQ+ Support Center", "Research & Advocacy Institute",
            "Public Safety Foundation", "International Aid Society", "Hunger Relief Network",
            "Homelessness Prevention Center", "Immigrant Support Services", "Elder Care Alliance",
            "Youth Development Center", "Community Action Network", "Social Justice Initiative"
    };

    // Event titles covering ALL 25 event types (300+ events)
    private final String[] eventTitles = {
            // Community Cleanup Events
            "Beach Cleanup Drive", "Park Restoration Day", "River Cleanup Initiative", "Street Beautification Project",
            "Neighborhood Cleanup Campaign", "Community Garden Cleanup", "Highway Cleanup Drive", 
            "School Grounds Cleanup", "Historic Site Cleanup", "Downtown Cleanup Day",

            // Food Service Events
            "Community Kitchen Service", "Food Bank Sorting", "Meal Preparation for Homeless", "Food Drive Collection",
            "Senior Meal Delivery", "School Lunch Program", "Holiday Meal Service", "Soup Kitchen Volunteer",
            "Emergency Food Distribution", "Community Pantry Stocking",

            // Tutoring & Education Events
            "After School Tutoring", "Reading to Children", "Math Help Session", "ESL Classes",
            "Computer Literacy Training", "Study Skills Workshop", "Homework Help Program", "College Prep Session",
            "Adult Education Classes", "Special Needs Tutoring",

            // Animal Care Events
            "Animal Shelter Volunteering", "Pet Adoption Event", "Dog Walking Program", "Cat Socialization",
            "Wildlife Rehabilitation", "Pet Food Drive", "Animal Rescue Transport", "Pet Therapy Sessions",
            "Veterinary Clinic Support", "Animal Education Program",

            // Environmental Conservation Events
            "Tree Planting Drive", "Nature Trail Maintenance", "Wildlife Habitat Restoration", "Recycling Education",
            "Water Quality Testing", "Solar Panel Installation", "Composting Workshop", "Green Energy Fair",
            "Conservation Photography", "Environmental Film Screening",

            // Senior Support Events
            "Senior Center Activities", "Elder Care Visits", "Technology Training for Seniors", "Senior Meal Delivery",
            "Companion Services", "Senior Transportation", "Health Screening for Elderly", "Senior Exercise Classes",
            "Intergenerational Programs", "Senior Holiday Celebration",

            // Youth Mentoring Events
            "Youth Leadership Program", "Teen Mentorship Sessions", "Career Exploration Workshop", "Life Skills Training",
            "Youth Sports Coaching", "Creative Arts Mentoring", "STEM Mentorship", "Youth Entrepreneurship",
            "College Guidance Sessions", "Youth Community Service",

            // Healthcare Support Events
            "Health Screening Clinic", "Blood Drive Campaign", "First Aid Training", "Health Education Seminar",
            "Vaccination Clinic Support", "Medical Equipment Drive", "Health Fair Volunteering", "Wellness Workshop",
            "Chronic Disease Support", "Health Insurance Education",

            // Disaster Relief Events
            "Emergency Response Training", "Disaster Preparedness Workshop", "Relief Supply Packing", "Evacuation Support",
            "Emergency Shelter Setup", "Disaster Recovery Cleanup", "Emergency Communications", "Relief Fund Drive",
            "Community Emergency Planning", "Post-Disaster Counseling",

            // Arts & Culture Events
            "Community Art Project", "Cultural Festival Planning", "Art Therapy Sessions", "Music Education Program",
            "Theater Production Support", "Cultural Heritage Preservation", "Community Mural Painting", "Art Gallery Support",
            "Creative Writing Workshop", "Dance Instruction Volunteer",

            // Sports & Recreation Events
            "Youth Soccer Coaching", "Community Basketball League", "Senior Fitness Classes", "Swimming Lessons",
            "Marathon Event Support", "Sports Equipment Drive", "Disability Sports Program", "Summer Camp Activities",
            "Outdoor Adventure Club", "Community Sports Tournament",

            // Fundraising Events
            "Charity Auction Setup", "Fundraising Gala Planning", "Crowdfunding Campaign", "Donation Drive Coordination",
            "Benefit Concert Organization", "Silent Auction Support", "Corporate Sponsorship Outreach", "Grant Writing Workshop",
            "Fundraising Strategy Session", "Donor Appreciation Event",

            // Administrative Support Events
            "Data Entry Volunteer", "Office Administration Help", "Reception Desk Coverage", "Document Filing",
            "Mail Processing Support", "Database Management", "Volunteer Coordination", "Event Registration",
            "Phone Call Support", "Record Keeping Assistance",

            // Construction & Building Events
            "Habitat for Humanity Build", "Playground Construction", "Community Center Renovation", "Wheelchair Ramp Building",
            "Garden Shed Assembly", "Fence Installation", "Roof Repair Project", "Accessibility Improvements",
            "Community Infrastructure", "Building Maintenance",

            // Technology Support Events
            "Computer Repair Workshop", "Digital Literacy Training", "Website Development", "Tech Support for Seniors",
            "Equipment Setup", "Software Training", "IT Support Volunteer", "Digital Divide Bridge",
            "Online Safety Education", "Tech Equipment Drive",

            // Event Planning Events
            "Community Festival Planning", "Wedding Planning for Low-Income", "Birthday Party Organization", 
            "Holiday Celebration Planning", "Awards Ceremony Setup", "Conference Organization", 
            "Workshop Coordination", "Volunteer Recognition Event", "Graduation Ceremony Support", "Networking Event Setup",

            // Advocacy & Awareness Events
            "Community Forum Facilitation", "Awareness Campaign", "Policy Research", "Community Surveying",
            "Public Speaking Training", "Grassroots Organizing", "Petition Drive", "Educational Workshop",
            "Community Meeting Support", "Advocacy Training",

            // Research & Data Events
            "Survey Data Collection", "Research Study Support", "Data Analysis Volunteer", "Community Needs Assessment",
            "Academic Research Assistance", "Grant Research", "Statistical Analysis", "Report Writing",
            "Field Research Support", "Database Development",

            // Transportation Events
            "Senior Transportation Service", "Medical Appointment Rides", "Grocery Shopping Assistance", 
            "Community Event Shuttle", "Wheelchair Accessible Transport", "Emergency Transportation",
            "Student Transportation", "Job Interview Rides", "Volunteer Driver Program", "Public Transit Assistance",

            // Gardening Events
            "Community Garden Maintenance", "School Garden Project", "Urban Farming Initiative", "Composting Program",
            "Herb Garden Workshop", "Vegetable Harvest Event", "Garden Design Project", "Plant Distribution",
            "Greenhouse Volunteer", "Landscaping Project",

            // Crisis Support Events
            "Crisis Hotline Training", "Emergency Response Support", "Disaster Relief Coordination", "Mental Health First Aid",
            "Homeless Shelter Volunteer", "Domestic Violence Support", "Suicide Prevention Training", "Crisis Intervention",
            "Emergency Shelter Setup", "Trauma Support Training",

            // Festival & Fair Events
            "County Fair Volunteer", "Music Festival Support", "Cultural Festival Booth", "Arts & Crafts Fair",
            "Food Festival Helper", "Holiday Market Setup", "Farmers Market Support", "Community Carnival",
            "Street Festival Coordination", "Craft Fair Organization",

            // Workshop & Training Events
            "Skills Training Workshop", "Professional Development", "Leadership Training", "Communication Workshop",
            "Financial Literacy Training", "Job Skills Development", "Personal Development Session", "Team Building Workshop",
            "Conflict Resolution Training", "Public Speaking Workshop",

            // Blood Drive Events
            "Red Cross Blood Drive", "Hospital Blood Drive", "Community Blood Donation", "Emergency Blood Drive",
            "Blood Donor Registration", "Blood Drive Coordination", "Mobile Blood Unit Support", "Donor Recruitment",
            "Blood Drive Setup", "Post-Donation Care",

            // Other Events
            "Special Project Support", "Community Initiative", "Volunteer Orientation", "Skills Assessment",
            "Community Outreach", "Public Service Project", "Social Impact Initiative", "Civic Engagement",
            "Community Development", "Volunteer Recognition"
    };

    // UPDATED: Complete filter coverage arrays - ALL 25 EVENT TYPES
    private final EventType[] allEventTypes = {
            EventType.COMMUNITY_CLEANUP, EventType.FOOD_SERVICE, EventType.TUTORING_EDUCATION, 
            EventType.ANIMAL_CARE, EventType.ENVIRONMENTAL_CONSERVATION, EventType.SENIOR_SUPPORT, 
            EventType.YOUTH_MENTORING, EventType.HEALTHCARE_SUPPORT, EventType.DISASTER_RELIEF,
            EventType.ARTS_CULTURE, EventType.SPORTS_RECREATION, EventType.FUNDRAISING, 
            EventType.ADMINISTRATIVE_SUPPORT, EventType.CONSTRUCTION_BUILDING, EventType.TECHNOLOGY_SUPPORT,
            EventType.EVENT_PLANNING, EventType.ADVOCACY_AWARENESS, EventType.RESEARCH_DATA,
            EventType.TRANSPORTATION, EventType.GARDENING, EventType.CRISIS_SUPPORT,
            EventType.FESTIVAL_FAIR, EventType.WORKSHOP_TRAINING, EventType.BLOOD_DRIVE, EventType.OTHER
    };

    private final String[] allSkillLevels = {
            "NO_EXPERIENCE_REQUIRED", "BEGINNER_FRIENDLY", "SOME_EXPERIENCE_PREFERRED",
            "EXPERIENCED_VOLUNTEERS", "SPECIALIZED_SKILLS_REQUIRED", "TRAINING_PROVIDED"
    };

    // CORRECTED TO MATCH YOUR ACTUAL EventDuration ENUM
    private final EventDuration[] allDurations = {
            EventDuration.SHORT, EventDuration.MEDIUM, EventDuration.FULL_DAY,
            EventDuration.MULTI_DAY, EventDuration.WEEKLY_COMMITMENT, 
            EventDuration.MONTHLY_COMMITMENT, EventDuration.ONGOING_LONG_TERM
    };

    private final String[] allCategories = {
            "Education", "Environment", "Healthcare", "Animal Welfare", "Community Service",
            "Human Services", "Arts & Culture", "Youth Development", "Senior Services",
            "Hunger & Homelessness", "Disaster Relief", "International", "Sports & Recreation",
            "Mental Health", "Veterans", "Women's Issues", "Children & Families",
            "Disability Services", "Religious", "Political", "LGBTQ+", "Technology",
            "Research & Advocacy", "Public Safety"
    };

    private final String[] allCountries = {
            "United States", "Canada", "United Kingdom", "Australia", "Germany",
            "France", "Netherlands", "Sweden", "Denmark", "Ireland", "Switzerland"
    };

    private final String[] allCities = {
            // US Cities
            "New York, NY", "Los Angeles, CA", "Chicago, IL",
            // Canadian Cities
            "Toronto, ON", "Vancouver, BC", "Montreal, QC",
            // UK Cities
            "London, UK", "Manchester, UK", "Edinburgh, UK",
            // Australian Cities
            "Sydney, NSW", "Melbourne, VIC", "Brisbane, QLD",
            // German Cities
            "Berlin, Germany", "Munich, Germany", "Hamburg, Germany",
            // French Cities
            "Paris, France", "Lyon, France", "Marseille, France",
            // Netherlands Cities
            "Amsterdam, Netherlands", "Rotterdam, Netherlands", "The Hague, Netherlands",
            // Swedish Cities
            "Stockholm, Sweden", "Gothenburg, Sweden", "Malmö, Sweden",
            // Danish Cities
            "Copenhagen, Denmark", "Aarhus, Denmark", "Odense, Denmark",
            // Irish Cities
            "Dublin, Ireland", "Cork, Ireland", "Galway, Ireland",
            // Swiss Cities
            "Zurich, Switzerland", "Geneva, Switzerland", "Basel, Switzerland"
    };

    private final String[] organizationSizes = {
            "Small (1-50)", "Medium (51-200)", "Large (201-1000)", "Enterprise (1000+)"
    };

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println("🚀 Loading comprehensive mock data...");
            loadMockData();
            System.out.println("✅ Mock data loaded successfully!");
            System.out.println("📊 Created: 200 organizations and 500 events covering ALL 25 event types");
        } else {
            System.out.println("📋 Data already exists, skipping mock data load");
        }
    }

    private void loadMockData() {
        // Create organization users and profiles
        List<User> organizationUsers = createOrganizationUsers();
        List<OrganizationProfile> organizations = createComprehensiveOrganizations(organizationUsers);
        
        // Create comprehensive events covering all filters
        createComprehensiveEvents(organizations);
    }

    private List<User> createOrganizationUsers() {
        List<User> users = new ArrayList<>();
        
        // Create 200 organization users
        for (int i = 0; i < 200; i++) {
            User user = new User();
            user.setEmail("org" + i + "@volunteersync.example.com");
            user.setPassword(passwordEncoder.encode("password123"));
            user.setUserType(UserType.ORGANIZATION);
            user.setIsActive(true);
            user.setEmailVerified(random.nextBoolean());
            user.setCreatedAt(generateDistributedCreatedAt());
            user.setUpdatedAt(generateUpdatedAt(user.getCreatedAt()));
            
            users.add(userRepository.save(user));
        }
        
        return users;
    }

    private List<OrganizationProfile> createComprehensiveOrganizations(List<User> users) {
        List<OrganizationProfile> organizations = new ArrayList<>();
        
        for (int i = 0; i < users.size(); i++) {
            OrganizationProfile org = new OrganizationProfile();
            org.setUser(users.get(i));
            
            // Ensure even distribution across categories
            String primaryCategory = allCategories[i % allCategories.length];
            org.setPrimaryCategory(primaryCategory);
            org.setCategories(generateMultipleCategories(primaryCategory));
            
            // Organization details
            org.setOrganizationName(orgNames[i % orgNames.length] + " " + (i / orgNames.length > 0 ? (i / orgNames.length + 1) : ""));
            org.setDescription("A dedicated organization focused on " + primaryCategory.toLowerCase() + 
                             " and community impact. We strive to make meaningful change through volunteer engagement.");
            org.setMissionStatement("To create positive change in our community through " + primaryCategory.toLowerCase() + 
                                  " initiatives and volunteer collaboration.");
            
            // Location - ensure coverage of all countries and cities
            setComprehensiveLocation(org, i);
            
            // Organization characteristics
            org.setOrganizationType(getRandomOrgType());
            org.setOrganizationSize(organizationSizes[i % organizationSizes.length]);
            org.setEmployeeCount(generateEmployeeCountForSize(org.getOrganizationSize()));
            org.setFoundedYear(1970 + random.nextInt(55));
            org.setIsVerified(random.nextDouble() < 0.7);
            
            // Contact info
            org.setWebsite("https://" + org.getOrganizationName().toLowerCase().replaceAll("[^a-z0-9]", "") + ".org");
            org.setPhoneNumber(generatePhoneNumber());
            org.setAddress(generateAddress());
            
            // Financial info
            org.setFundingGoal(25000 + random.nextInt(2000000));
            org.setFundingRaised(random.nextInt(org.getFundingGoal()));
            
            // Stats
            org.setTotalEventsHosted(random.nextInt(200));
            org.setTotalVolunteersServed(random.nextInt(5000));
            
            // Additional fields
            org.setLanguagesSupported(generateLanguages());
            org.setTaxExemptStatus(getTaxExemptStatus());
            org.setVerificationLevel(getVerificationLevel());
            org.setServices(generateServices());
            org.setCauses(generateCauses());
            
            // Images
            org.setProfileImageUrl("https://images.unsplash.com/photo-" + (1500000000 + random.nextInt(200000000)) + "?w=400&h=400");
            org.setCoverImageUrl("https://images.unsplash.com/photo-" + (1500000000 + random.nextInt(200000000)) + "?w=1200&h=400");
            
            // Timestamps to cover all date filter options
            org.setCreatedAt(generateDistributedCreatedAt());
            org.setUpdatedAt(generateUpdatedAt(org.getCreatedAt()));
            
            organizations.add(organizationRepository.save(org));
        }
        
        return organizations;
    }

    private void createComprehensiveEvents(List<OrganizationProfile> organizations) {
        // Create 750 events to ensure comprehensive coverage (30 events per event type)
        for (int i = 0; i < 750; i++) {
            Event event = new Event();
            
            // Ensure coverage of all 25 event types
            EventType eventType = allEventTypes[i % allEventTypes.length];
            event.setEventType(eventType);
            
            // Match title to event type
            event.setTitle(getEventTitleForType(eventType, i));
            event.setDescription(generateEventDescription(event.getTitle()));
            
            // Assign to organization
            OrganizationProfile org = organizations.get(i % organizations.size());
            event.setOrganization(org);
            
            // Location - ensure coverage including Virtual/Remote
            if (i % 10 == 0) {
                // 10% virtual events
                event.setIsVirtual(true);
                event.setLocation("Virtual/Remote");
                event.setVirtualMeetingLink("https://meet.google.com/" + generateMeetingId());
            } else {
                event.setIsVirtual(false);
                String city = allCities[i % allCities.length];
                event.setLocation(city);
                String[] cityParts = city.split(", ");
                event.setCity(cityParts[0]);
                if (cityParts.length > 1) {
                    event.setState(cityParts[1]);
                }
                event.setAddress(generateAddress());
                event.setZipCode(generateZipCode());
            }
            
            // Date and time - ensure coverage of all date filters
            LocalDateTime startDate = generateComprehensiveDateTime(i);
            event.setStartDate(startDate);
            
            // Duration - ensure coverage of all duration options
            SkillLevel skillLevel = SkillLevel.valueOf(allSkillLevels[i % allSkillLevels.length]);
            event.setSkillLevelRequired(skillLevel);
            
            // Duration category - ensure coverage
            EventDuration duration = allDurations[i % allDurations.length];
            event.setDurationCategory(duration);
            event.setEstimatedHours(generateHoursForDuration(duration));
            
            // End date based on duration
            event.setEndDate(calculateEndDate(startDate, duration));
            
            // Time-based flags for time filter coverage
            setTimeBasedFlags(event, startDate, i);
            
            // Volunteer details
            int maxVolunteers = 5 + random.nextInt(195); // 5-200
            event.setMaxVolunteers(maxVolunteers);
            event.setCurrentVolunteers(random.nextInt(maxVolunteers + 1));
            
            // Event status based on date
            event.setStatus(calculateEventStatus(startDate));
            
            // Other flags
            event.setIsRecurring(random.nextDouble() < 0.25);
            event.setHasFlexibleTiming(random.nextDouble() < 0.4);
            
            // Contact info
            event.setContactEmail("events@" + org.getOrganizationName().toLowerCase().replaceAll("[^a-z0-9]", "") + ".org");
            event.setContactPhone(generatePhoneNumber());
            
            // Requirements
            event.setRequirements(generateRequirements());
            
            // Image
            event.setImageUrl("https://images.unsplash.com/photo-" + (1500000000 + random.nextInt(200000000)) + "?w=800&h=400");
            
            // Timestamps
            event.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(90)));
            event.setUpdatedAt(LocalDateTime.now().minusDays(random.nextInt(14)));
            
            eventRepository.save(event);
        }
    }

    // Helper methods for comprehensive coverage

    private void setComprehensiveLocation(OrganizationProfile org, int index) {
        // Ensure even distribution across all countries
        String country = allCountries[index % allCountries.length];
        org.setCountry(country);
        
        // Set city based on country
        if (country.equals("United States")) {
            String[] usCities = {"New York, NY", "Los Angeles, CA", "Chicago, IL"};
            String city = usCities[index % usCities.length];
            String[] parts = city.split(", ");
            org.setCity(parts[0]);
            org.setState(parts[1]);
        } else {
            // International cities
            String city = getCityForCountry(country, index);
            String[] parts = city.split(", ");
            org.setCity(parts[0]);
            org.setState(parts.length > 1 ? parts[1] : getStateForCountry(country));
        }
        
        org.setZipCode(generateZipCode());
    }

    private LocalDateTime generateComprehensiveDateTime(int index) {
        LocalDateTime now = LocalDateTime.now();
        
        // Ensure coverage of all date filter options
        int dateFilter = index % 10;
        switch (dateFilter) {
            case 0: // Today
                return now.plusHours(random.nextInt(12));
            case 1: // Tomorrow
                return now.plusDays(1).plusHours(random.nextInt(12));
            case 2: // This Week (next 7 days)
                return now.plusDays(2 + random.nextInt(5)).plusHours(random.nextInt(12));
            case 3: // Next Week
                return now.plusDays(7 + random.nextInt(7)).plusHours(random.nextInt(12));
            case 4: // This Weekend
                int daysUntilWeekend = 6 - now.getDayOfWeek().getValue(); // Saturday
                if (daysUntilWeekend <= 0) daysUntilWeekend += 7;
                return now.plusDays(daysUntilWeekend).plusHours(random.nextInt(12));
            case 5: // Next Weekend
                int daysUntilNextWeekend = 13 - now.getDayOfWeek().getValue(); // Next Saturday
                return now.plusDays(daysUntilNextWeekend).plusHours(random.nextInt(12));
            case 6: // This Month
                return now.plusDays(8 + random.nextInt(22)).plusHours(random.nextInt(12));
            case 7: // Next Month
                return now.plusMonths(1).plusDays(random.nextInt(28)).plusHours(random.nextInt(12));
            case 8: // Next 3 Months
                return now.plusMonths(2).plusDays(random.nextInt(30)).plusHours(random.nextInt(12));
            case 9: // Future
                return now.plusMonths(3 + random.nextInt(6)).plusHours(random.nextInt(12));
            default:
                return now.plusDays(random.nextInt(365)).plusHours(random.nextInt(12));
        }
    }

    private void setTimeBasedFlags(Event event, LocalDateTime startDate, int index) {
        int hour = startDate.getHour();
        
        // Time of day coverage
        if (hour >= 6 && hour < 12) {
            // Morning event
        } else if (hour >= 12 && hour < 18) {
            // Afternoon event
        } else {
            // Evening event
        }
        
        // Weekday/Weekend flags
        int dayOfWeek = startDate.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
        if (index % 5 == 0) {
            event.setIsWeekdaysOnly(true);
            event.setIsWeekendsOnly(false);
        } else if (index % 5 == 1) {
            event.setIsWeekdaysOnly(false);
            event.setIsWeekendsOnly(true);
        } else {
            event.setIsWeekdaysOnly(false);
            event.setIsWeekendsOnly(false);
        }
    }

    private LocalDateTime generateDistributedCreatedAt() {
        LocalDateTime now = LocalDateTime.now();
        double rand = random.nextDouble();
        
        if (rand < 0.05) {
            // Last 24 hours
            return now.minusHours(random.nextInt(24));
        } else if (rand < 0.15) {
            // Last 3 days
            return now.minusDays(random.nextInt(3));
        } else if (rand < 0.25) {
            // Last 7 days
            return now.minusDays(random.nextInt(7));
        } else if (rand < 0.35) {
            // Last 14 days
            return now.minusDays(random.nextInt(14));
        } else if (rand < 0.50) {
            // Last 30 days
            return now.minusDays(random.nextInt(30));
        } else {
            // Older than 30 days
            return now.minusDays(30 + random.nextInt(335));
        }
    }

    private LocalDateTime generateUpdatedAt(LocalDateTime createdAt) {
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(createdAt, LocalDateTime.now());
        if (daysBetween <= 0) return createdAt;
        return createdAt.plusDays(random.nextInt((int) daysBetween + 1));
    }

    // UPDATED: Complete getEventTitleForType method covering ALL 25 event types
    private String getEventTitleForType(EventType eventType, int index) {
        switch (eventType) {
            case COMMUNITY_CLEANUP:
                String[] cleanupTitles = {"Beach Cleanup Drive", "Park Restoration Day", "River Cleanup Initiative", 
                                        "Street Beautification Project", "Neighborhood Cleanup Campaign", "Community Garden Cleanup",
                                        "Highway Cleanup Drive", "School Grounds Cleanup", "Historic Site Cleanup", "Downtown Cleanup Day"};
                return cleanupTitles[index % cleanupTitles.length];
                
            case FOOD_SERVICE:
                String[] foodTitles = {"Community Kitchen Service", "Food Bank Sorting", "Meal Preparation for Homeless",
                                     "Food Drive Collection", "Senior Meal Delivery", "School Lunch Program", 
                                     "Holiday Meal Service", "Soup Kitchen Volunteer", "Emergency Food Distribution", "Community Pantry Stocking"};
                return foodTitles[index % foodTitles.length];
                
            case TUTORING_EDUCATION:
                String[] tutorTitles = {"After School Tutoring", "Reading to Children", "Math Help Session",
                                      "ESL Classes", "Computer Literacy Training", "Study Skills Workshop", 
                                      "Homework Help Program", "College Prep Session", "Adult Education Classes", "Special Needs Tutoring"};
                return tutorTitles[index % tutorTitles.length];
                
            case ANIMAL_CARE:
                String[] animalTitles = {"Animal Shelter Volunteering", "Pet Adoption Event", "Dog Walking Program",
                                       "Cat Socialization", "Wildlife Rehabilitation", "Pet Food Drive", 
                                       "Animal Rescue Transport", "Pet Therapy Sessions", "Veterinary Clinic Support", "Animal Education Program"};
                return animalTitles[index % animalTitles.length];
                
            case ENVIRONMENTAL_CONSERVATION:
                String[] envTitles = {"Tree Planting Drive", "Nature Trail Maintenance", "Wildlife Habitat Restoration",
                                    "Recycling Education", "Water Quality Testing", "Solar Panel Installation", 
                                    "Composting Workshop", "Green Energy Fair", "Conservation Photography", "Environmental Film Screening"};
                return envTitles[index % envTitles.length];
                
            case SENIOR_SUPPORT:
                String[] seniorTitles = {"Senior Center Activities", "Elder Care Visits", "Technology Training for Seniors",
                                       "Senior Meal Delivery", "Companion Services", "Senior Transportation", 
                                       "Health Screening for Elderly", "Senior Exercise Classes", "Intergenerational Programs", "Senior Holiday Celebration"};
                return seniorTitles[index % seniorTitles.length];
                
            case YOUTH_MENTORING:
                String[] youthTitles = {"Youth Leadership Program", "Teen Mentorship Sessions", "Career Exploration Workshop",
                                      "Life Skills Training", "Youth Sports Coaching", "Creative Arts Mentoring", 
                                      "STEM Mentorship", "Youth Entrepreneurship", "College Guidance Sessions", "Youth Community Service"};
                return youthTitles[index % youthTitles.length];
                
            case HEALTHCARE_SUPPORT:
                String[] healthTitles = {"Health Screening Clinic", "Blood Drive Campaign", "First Aid Training",
                                       "Health Education Seminar", "Vaccination Clinic Support", "Medical Equipment Drive", 
                                       "Health Fair Volunteering", "Wellness Workshop", "Chronic Disease Support", "Health Insurance Education"};
                return healthTitles[index % healthTitles.length];
                
            case DISASTER_RELIEF:
                String[] disasterTitles = {"Emergency Response Training", "Disaster Preparedness Workshop", "Relief Supply Packing",
                                         "Evacuation Support", "Emergency Shelter Setup", "Disaster Recovery Cleanup", 
                                         "Emergency Communications", "Relief Fund Drive", "Community Emergency Planning", "Post-Disaster Counseling"};
                return disasterTitles[index % disasterTitles.length];
                
            case ARTS_CULTURE:
                String[] artsTitles = {"Community Art Project", "Cultural Festival Planning", "Art Therapy Sessions",
                                     "Music Education Program", "Theater Production Support", "Cultural Heritage Preservation", 
                                     "Community Mural Painting", "Art Gallery Support", "Creative Writing Workshop", "Dance Instruction Volunteer"};
                return artsTitles[index % artsTitles.length];
                
            case SPORTS_RECREATION:
                String[] sportsTitles = {"Youth Soccer Coaching", "Community Basketball League", "Senior Fitness Classes",
                                       "Swimming Lessons", "Marathon Event Support", "Sports Equipment Drive", 
                                       "Disability Sports Program", "Summer Camp Activities", "Outdoor Adventure Club", "Community Sports Tournament"};
                return sportsTitles[index % sportsTitles.length];
                
            case FUNDRAISING:
                String[] fundraisingTitles = {"Charity Auction Setup", "Fundraising Gala Planning", "Crowdfunding Campaign",
                                            "Donation Drive Coordination", "Benefit Concert Organization", "Silent Auction Support", 
                                            "Corporate Sponsorship Outreach", "Grant Writing Workshop", "Fundraising Strategy Session", "Donor Appreciation Event"};
                return fundraisingTitles[index % fundraisingTitles.length];
                
            case ADMINISTRATIVE_SUPPORT:
                String[] adminTitles = {"Data Entry Volunteer", "Office Administration Help", "Reception Desk Coverage",
                                      "Document Filing", "Mail Processing Support", "Database Management", 
                                      "Volunteer Coordination", "Event Registration", "Phone Call Support", "Record Keeping Assistance"};
                return adminTitles[index % adminTitles.length];
                
            case CONSTRUCTION_BUILDING:
                String[] constructionTitles = {"Habitat for Humanity Build", "Playground Construction", "Community Center Renovation",
                                             "Wheelchair Ramp Building", "Garden Shed Assembly", "Fence Installation", 
                                             "Roof Repair Project", "Accessibility Improvements", "Community Infrastructure", "Building Maintenance"};
                return constructionTitles[index % constructionTitles.length];
                
            case TECHNOLOGY_SUPPORT:
                String[] techTitles = {"Computer Repair Workshop", "Digital Literacy Training", "Website Development",
                                     "Tech Support for Seniors", "Equipment Setup", "Software Training", 
                                     "IT Support Volunteer", "Digital Divide Bridge", "Online Safety Education", "Tech Equipment Drive"};
                return techTitles[index % techTitles.length];
                
            case EVENT_PLANNING:
                String[] eventTitles = {"Community Festival Planning", "Wedding Planning for Low-Income", "Birthday Party Organization",
                                      "Holiday Celebration Planning", "Awards Ceremony Setup", "Conference Organization", 
                                      "Workshop Coordination", "Volunteer Recognition Event", "Graduation Ceremony Support", "Networking Event Setup"};
                return eventTitles[index % eventTitles.length];
                
            case ADVOCACY_AWARENESS:
                String[] advocacyTitles = {"Community Forum Facilitation", "Awareness Campaign", "Policy Research",
                                         "Community Surveying", "Public Speaking Training", "Grassroots Organizing", 
                                         "Petition Drive", "Educational Workshop", "Community Meeting Support", "Advocacy Training"};
                return advocacyTitles[index % advocacyTitles.length];
                
            case RESEARCH_DATA:
                String[] researchTitles = {"Survey Data Collection", "Research Study Support", "Data Analysis Volunteer",
                                         "Community Needs Assessment", "Academic Research Assistance", "Grant Research", 
                                         "Statistical Analysis", "Report Writing", "Field Research Support", "Database Development"};
                return researchTitles[index % researchTitles.length];
                
            case TRANSPORTATION:
                String[] transportTitles = {"Senior Transportation Service", "Medical Appointment Rides", "Grocery Shopping Assistance",
                                          "Community Event Shuttle", "Wheelchair Accessible Transport", "Emergency Transportation", 
                                          "Student Transportation", "Job Interview Rides", "Volunteer Driver Program", "Public Transit Assistance"};
                return transportTitles[index % transportTitles.length];
                
            case GARDENING:
                String[] gardenTitles = {"Community Garden Maintenance", "School Garden Project", "Urban Farming Initiative",
                                       "Composting Program", "Herb Garden Workshop", "Vegetable Harvest Event", 
                                       "Garden Design Project", "Plant Distribution", "Greenhouse Volunteer", "Landscaping Project"};
                return gardenTitles[index % gardenTitles.length];
                
            case CRISIS_SUPPORT:
                String[] crisisTitles = {"Crisis Hotline Training", "Emergency Response Support", "Disaster Relief Coordination",
                                       "Mental Health First Aid", "Homeless Shelter Volunteer", "Domestic Violence Support", 
                                       "Suicide Prevention Training", "Crisis Intervention", "Emergency Shelter Setup", "Trauma Support Training"};
                return crisisTitles[index % crisisTitles.length];
                
            case FESTIVAL_FAIR:
                String[] festivalTitles = {"County Fair Volunteer", "Music Festival Support", "Cultural Festival Booth",
                                         "Arts & Crafts Fair", "Food Festival Helper", "Holiday Market Setup", 
                                         "Farmers Market Support", "Community Carnival", "Street Festival Coordination", "Craft Fair Organization"};
                return festivalTitles[index % festivalTitles.length];
                
            case WORKSHOP_TRAINING:
                String[] workshopTitles = {"Skills Training Workshop", "Professional Development", "Leadership Training",
                                         "Communication Workshop", "Financial Literacy Training", "Job Skills Development", 
                                         "Personal Development Session", "Team Building Workshop", "Conflict Resolution Training", "Public Speaking Workshop"};
                return workshopTitles[index % workshopTitles.length];
                
            case BLOOD_DRIVE:
                String[] bloodTitles = {"Red Cross Blood Drive", "Hospital Blood Drive", "Community Blood Donation",
                                      "Emergency Blood Drive", "Blood Donor Registration", "Blood Drive Coordination", 
                                      "Mobile Blood Unit Support", "Donor Recruitment", "Blood Drive Setup", "Post-Donation Care"};
                return bloodTitles[index % bloodTitles.length];
                
            case OTHER:
                String[] otherTitles = {"Special Project Support", "Community Initiative", "Volunteer Orientation",
                                      "Skills Assessment", "Community Outreach", "Public Service Project", 
                                      "Social Impact Initiative", "Civic Engagement", "Community Development", "Volunteer Recognition"};
                return otherTitles[index % otherTitles.length];
                
            default:
                return "Community Volunteer Event";
        }
    }

    // CORRECTED TO USE YOUR ACTUAL EventDuration ENUM VALUES
    private int generateHoursForDuration(EventDuration duration) {
        switch (duration) {
            case SHORT: return duration.getMinHours() + random.nextInt(duration.getMaxHours() - duration.getMinHours() + 1);
            case MEDIUM: return duration.getMinHours() + random.nextInt(duration.getMaxHours() - duration.getMinHours() + 1);
            case FULL_DAY: return duration.getMinHours() + random.nextInt(duration.getMaxHours() - duration.getMinHours() + 1);
            case MULTI_DAY: return 8 + random.nextInt(16); // 8-24 hours for multi-day
            case WEEKLY_COMMITMENT: return 4 + random.nextInt(6); // 4-10 hours weekly
            case MONTHLY_COMMITMENT: return 2 + random.nextInt(4); // 2-6 hours monthly
            case ONGOING_LONG_TERM: return 3 + random.nextInt(5); // 3-8 hours ongoing
            default: return 2 + random.nextInt(4);
        }
    }

    private LocalDateTime calculateEndDate(LocalDateTime startDate, EventDuration duration) {
        switch (duration) {
            case SHORT: return startDate.plusHours(1 + random.nextInt(2));
            case MEDIUM: return startDate.plusHours(3 + random.nextInt(2));
            case FULL_DAY: return startDate.plusHours(5 + random.nextInt(4));
            case MULTI_DAY: return startDate.plusDays(1 + random.nextInt(6));
            case WEEKLY_COMMITMENT: return startDate.plusWeeks(1);
            case MONTHLY_COMMITMENT: return startDate.plusMonths(1);
            case ONGOING_LONG_TERM: return startDate.plusMonths(3 + random.nextInt(9));
            default: return startDate.plusHours(2 + random.nextInt(4));
        }
    }

    private EventStatus calculateEventStatus(LocalDateTime startDate) {
        LocalDateTime now = LocalDateTime.now();
        
        if (startDate.isBefore(now.minusDays(1))) {
            return random.nextDouble() < 0.9 ? EventStatus.COMPLETED : EventStatus.CANCELLED;
        } else if (startDate.isAfter(now.plusDays(7))) {
            double rand = random.nextDouble();
            if (rand < 0.7) return EventStatus.ACTIVE;
            if (rand < 0.85) return EventStatus.DRAFT;
            return EventStatus.FULL;
        } else {
            return EventStatus.ACTIVE;
        }
    }

    private String getCityForCountry(String country, int index) {
        switch (country) {
            case "Canada":
                String[] canadaCities = {"Toronto, ON", "Vancouver, BC", "Montreal, QC"};
                return canadaCities[index % canadaCities.length];
            case "United Kingdom":
                String[] ukCities = {"London, UK", "Manchester, UK", "Edinburgh, UK"};
                return ukCities[index % ukCities.length];
            case "Australia":
                String[] ausCities = {"Sydney, NSW", "Melbourne, VIC", "Brisbane, QLD"};
                return ausCities[index % ausCities.length];
            case "Germany":
                String[] germanCities = {"Berlin, Germany", "Munich, Germany", "Hamburg, Germany"};
                return germanCities[index % germanCities.length];
            case "France":
                String[] frenchCities = {"Paris, France", "Lyon, France", "Marseille, France"};
                return frenchCities[index % frenchCities.length];
            case "Netherlands":
                String[] dutchCities = {"Amsterdam, Netherlands", "Rotterdam, Netherlands", "The Hague, Netherlands"};
                return dutchCities[index % dutchCities.length];
            case "Sweden":
                String[] swedishCities = {"Stockholm, Sweden", "Gothenberg, Sweden", "Malmö, Sweden"};
                return swedishCities[index % swedishCities.length];
            case "Denmark":
                String[] danishCities = {"Copenhagen, Denmark", "Aarhus, Denmark", "Odense, Denmark"};
                return danishCities[index % danishCities.length];
            case "Ireland":
                String[] irishCities = {"Dublin, Ireland", "Cork, Ireland", "Galway, Ireland"};
                return irishCities[index % irishCities.length];
            case "Switzerland":
                String[] swissCities = {"Zurich, Switzerland", "Geneva, Switzerland", "Basel, Switzerland"};
                return swissCities[index % swissCities.length];
            default:
                return "International City";
        }
    }

    private String getStateForCountry(String country) {
        switch (country) {
            case "Canada": return "Province";
            case "United Kingdom": return "England";
            case "Australia": return "State";
            case "Germany": return "State";
            case "France": return "Region";
            case "Netherlands": return "Province";
            case "Sweden": return "County";
            case "Denmark": return "Region";
            case "Ireland": return "County";
            case "Switzerland": return "Canton";
            default: return "Region";
        }
    }

    private int generateEmployeeCountForSize(String size) {
        switch (size) {
            case "Small (1-50)": return 1 + random.nextInt(50);
            case "Medium (51-200)": return 51 + random.nextInt(150);
            case "Large (201-1000)": return 201 + random.nextInt(800);
            case "Enterprise (1000+)": return 1001 + random.nextInt(4000);
            default: return 1 + random.nextInt(100);
        }
    }

    private String generateMultipleCategories(String primaryCategory) {
        List<String> categories = new ArrayList<>();
        categories.add(primaryCategory);
        
        // Add 1-3 additional categories
        int additionalCount = random.nextInt(3);
        for (int i = 0; i < additionalCount; i++) {
            String category = allCategories[random.nextInt(allCategories.length)];
            if (!categories.contains(category)) {
                categories.add(category);
            }
        }
        
        return String.join(",", categories);
    }

    private String getRandomOrgType() {
        String[] types = {"Non-Profit", "Charity", "Foundation", "Community Group", "Religious Organization",
                         "Educational Institution", "Government Agency", "Social Enterprise", "Cooperative", "NGO"};
        return types[random.nextInt(types.length)];
    }

    private String generatePhoneNumber() {
        return "+1-555-" + String.format("%04d", random.nextInt(10000));
    }

    private String generateAddress() {
        String[] streets = {"Main St", "Oak Ave", "Pine Rd", "Elm Dr", "Maple Ln", "Cedar Blvd", "Park Ave"};
        return (100 + random.nextInt(9900)) + " " + streets[random.nextInt(streets.length)];
    }

    private String generateZipCode() {
        return String.format("%05d", 10000 + random.nextInt(90000));
    }

    private String generateLanguages() {
        String[] languages = {"English", "Spanish", "French", "German", "Italian", "Portuguese", "Chinese", "Japanese"};
        List<String> selected = new ArrayList<>();
        selected.add("English");
        
        int count = random.nextInt(3);
        for (int i = 0; i < count; i++) {
            String lang = languages[1 + random.nextInt(languages.length - 1)];
            if (!selected.contains(lang)) {
                selected.add(lang);
            }
        }
        
        return String.join(",", selected);
    }

    private String getTaxExemptStatus() {
        String[] statuses = {"501(c)(3)", "501(c)(4)", "501(c)(6)", "501(c)(7)", "Not Applicable"};
        return statuses[random.nextInt(statuses.length)];
    }

    private String getVerificationLevel() {
        String[] levels = {"Unverified", "Basic", "Verified", "Premium"};
        double rand = random.nextDouble();
        if (rand < 0.1) return "Premium";
        if (rand < 0.4) return "Verified";
        if (rand < 0.7) return "Basic";
        return "Unverified";
    }

    private String generateServices() {
        String[] services = {"Tutoring", "Cleanup Events", "Food Distribution", "Mentoring", "Fundraising",
                           "Community Outreach", "Health Screenings", "Emergency Response", "Educational Workshops",
                           "Social Services", "Environmental Education", "Senior Care", "Youth Programs"};
        List<String> selected = new ArrayList<>();
        int count = 1 + random.nextInt(4);
        
        for (int i = 0; i < count; i++) {
            String service = services[random.nextInt(services.length)];
            if (!selected.contains(service)) {
                selected.add(service);
            }
        }
        
        return String.join(",", selected);
    }

    private String generateCauses() {
        List<String> selected = new ArrayList<>();
        int count = 1 + random.nextInt(4);
        
        for (int i = 0; i < count; i++) {
            String cause = allCategories[random.nextInt(allCategories.length)];
            if (!selected.contains(cause)) {
                selected.add(cause);
            }
        }
        
        return String.join(",", selected);
    }

    private String generateEventDescription(String title) {
        String[] templates = {
            "Join us for " + title.toLowerCase() + ". This is a fantastic opportunity to make a meaningful difference in our community.",
            "We're excited to invite you to participate in " + title.toLowerCase() + ". All skill levels welcome!",
            "Come be part of " + title.toLowerCase() + " and help us create lasting positive impact in our community.",
            "Volunteers needed for " + title.toLowerCase() + "! This is an excellent opportunity to gain hands-on experience.",
            "Join our team for " + title.toLowerCase() + " and experience the joy of community service.",
            "Make a difference at " + title.toLowerCase() + "! This volunteer opportunity allows you to contribute directly to our mission."
        };
        return templates[random.nextInt(templates.length)];
    }

    private String generateMeetingId() {
        String chars = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                id.append(chars.charAt(random.nextInt(chars.length())));
            }
            if (i < 2) id.append("-");
        }
        return id.toString();
    }

    private String generateRequirements() {
        String[] requirements = {
            "No experience necessary", "Must be 18 or older", "Physical activity required",
            "Background check required", "Own transportation preferred", "Comfortable working with children",
            "Lifting up to 25 lbs required", "Outdoor work in various weather", "Must be comfortable with public speaking",
            "Basic computer skills helpful", "Flexible schedule preferred", "Team player attitude essential"
        };
        
        List<String> selected = new ArrayList<>();
        int count = 1 + random.nextInt(4);
        
        for (int i = 0; i < count; i++) {
            String req = requirements[random.nextInt(requirements.length)];
            if (!selected.contains(req)) {
                selected.add(req);
            }
        }
        
        return String.join("; ", selected);
    }
}