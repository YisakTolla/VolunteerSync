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
    private VolunteerProfileRepository volunteerRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private BadgeRepository badgeRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private final Random random = new Random();
    
    // Expanded sample data arrays for more variety
    private final String[] firstNames = {
        "John", "Jane", "Michael", "Sarah", "David", "Emily", "Robert", "Jessica", "William", "Ashley",
        "James", "Amanda", "Christopher", "Stephanie", "Daniel", "Michelle", "Matthew", "Jennifer", "Anthony", "Lisa",
        "Mark", "Karen", "Paul", "Nancy", "Steven", "Betty", "Kenneth", "Helen", "Joshua", "Sandra",
        "Kevin", "Donna", "Brian", "Carol", "George", "Ruth", "Edward", "Sharon", "Ronald", "Laura",
        "Timothy", "Maria", "Jason", "Patricia", "Jeffrey", "Linda", "Ryan", "Barbara", "Jacob", "Elizabeth",
        "Andrew", "Mary", "Alexander", "Susan", "Nicholas", "Angela", "Douglas", "Brenda", "Benjamin", "Emma",
        "Charles", "Olivia", "Tyler", "Deborah", "Justin", "Rachel", "Samuel", "Catherine", "Gregory", "Carolyn",
        "Frank", "Janet", "Raymond", "Virginia", "Jack", "Maria", "Dennis", "Heather", "Jerry", "Diane"
    };
    
    private final String[] lastNames = {
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez",
        "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin",
        "Lee", "Perez", "Thompson", "White", "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson",
        "Walker", "Young", "Allen", "King", "Wright", "Scott", "Torres", "Nguyen", "Hill", "Flores",
        "Green", "Adams", "Nelson", "Baker", "Hall", "Rivera", "Campbell", "Mitchell", "Carter", "Roberts",
        "Phillips", "Evans", "Turner", "Diaz", "Parker", "Cruz", "Edwards", "Collins", "Reyes", "Stewart",
        "Morris", "Morales", "Murphy", "Cook", "Rogers", "Gutierrez", "Ortiz", "Morgan", "Cooper", "Peterson",
        "Bailey", "Reed", "Kelly", "Howard", "Ramos", "Kim", "Cox", "Ward", "Richardson", "Watson"
    };
    
    // SIGNIFICANTLY EXPANDED organization names (145 total)
    private final String[] orgNames = {
        // Environment & Conservation (25)
        "Green Earth Initiative", "Environmental Warriors", "Clean Water Project", "Coastal Conservation Society", 
        "Urban Garden Project", "Climate Action Network", "Environmental Education Center", "River Restoration Foundation",
        "Wildlife Conservation Alliance", "Sustainable Future Coalition", "Ocean Cleanup Network", "Forest Protection Society",
        "Green Energy Advocates", "Eco-Friendly Community", "Nature Preservation Trust", "Earth Guardians Initiative",
        "Renewable Resources Group", "Carbon Neutral Alliance", "Biodiversity Conservation Fund", "Climate Change Response",
        "Green Living Foundation", "Environmental Health Network", "Pollution Prevention Society", "Ecological Restoration",
        "Clean Air Initiative",
        
        // Education & Youth (25)
        "Youth Education Foundation", "Future Leaders Academy", "Literacy Champions", "Children's Art Center",
        "Digital Divide Initiative", "STEM Education Foundation", "Reading Buddies Program", "School Supply Drive",
        "Coding Bootcamp for Kids", "Youth Mentorship Alliance", "Educational Excellence Fund", "Learning Support Network",
        "Academic Achievement Society", "Student Success Coalition", "Knowledge Sharing Initiative", "Tutoring Heroes",
        "Educational Technology Alliance", "Youth Leadership Institute", "College Prep Foundation", "Early Learning Center",
        "After School Programs United", "Educational Equity Fund", "Scholarship Opportunity Network", "Skills Development Hub",
        "Youth Innovation Lab",
        
        // Health & Wellness (25)
        "Mental Health Support", "Healthcare Heroes", "Community Health Initiative", "Senior Care Alliance",
        "Medical Assistance Network", "Wellness Advocacy Group", "Public Health Foundation", "Health Education Alliance",
        "Medical Outreach Program", "Therapy Support Network", "Rehabilitation Services", "Preventive Care Initiative",
        "Health Screening Network", "Medical Equipment Fund", "Patient Support Alliance", "Health Awareness Campaign",
        "Community Wellness Center", "Mental Health Advocates", "Disability Support Network", "Health Equity Initiative",
        "Medical Research Foundation", "Pain Management Support", "Health Technology Innovation", "Nutrition Education Hub",
        "Fitness for All Foundation",
        
        // Community Service (25)
        "Community Kitchen Network", "Food Bank United", "Homeless Outreach", "Neighborhood Watch Alliance",
        "Meals on Wheels Plus", "Community Development Fund", "Local History Preservation", "Emergency Response Volunteers",
        "Community Safety Patrol", "Disaster Relief Foundation", "Volunteer Coordination Center", "Community Unity Project",
        "Neighborhood Improvement Society", "Social Services Alliance", "Community Building Initiative", "Public Service Fund",
        "Civic Engagement Network", "Community Resource Center", "Social Impact Coalition", "Community Empowerment Hub",
        "Local Leadership Initiative", "Community Pride Project", "Social Welfare Network", "Community Action Alliance",
        "Public Good Foundation",
        
        // Arts, Culture & Sports (25)
        "Cultural Bridge", "Music Therapy Group", "Sports for All", "Community Arts Collective",
        "Cultural Heritage Society", "Youth Soccer League", "Arts Education Foundation", "Sports Equipment Drive",
        "Cultural Festival Committee", "Athletic Development Program", "Creative Arts Alliance", "Recreation for All",
        "Music Education Network", "Theater Arts Foundation", "Dance Academy Volunteers", "Visual Arts Society",
        "Sports Medicine Foundation", "Cultural Diversity Initiative", "Performing Arts Center", "Athletic Scholarship Fund",
        "Community Recreation Center", "Arts Therapy Program", "Sports Safety Foundation", "Cultural Exchange Program",
        "Creative Writing Workshop",
        
        // Technology & Innovation (20)
        "Tech for Good", "Senior Technology Training", "Bike Share Cooperative", "Technology Donation Drive",
        "Digital Literacy Program", "Innovation Hub Network", "Coding for Community", "Tech Skills Development",
        "Computer Refurbishment Project", "Internet Access Initiative", "Software Development Volunteers", "Cybersecurity Education",
        "AI Ethics Foundation", "Digital Inclusion Network", "Tech Mentorship Program", "Robotics Education Alliance",
        "Open Source Community", "Technology Accessibility Fund", "Data Science for Good", "Digital Privacy Advocates"
    };
    
    // SIGNIFICANTLY EXPANDED event titles (145 total)
    private final String[] eventTitles = {
        // Environment & Conservation (30)
        "Community Garden Cleanup", "Beach Cleanup Initiative", "River Restoration Project", "Tree Planting Initiative",
        "Environmental Awareness Fair", "Recycling Education Program", "Wildlife Conservation Talk", "Solar Panel Installation",
        "Composting Workshop", "Native Plant Restoration", "Water Quality Testing", "Climate Action Rally",
        "Green Energy Fair", "Sustainable Living Workshop", "Ocean Cleanup Drive", "Forest Trail Maintenance",
        "Renewable Energy Seminar", "Carbon Footprint Reduction", "Eco-Friendly Gardening", "Pollution Monitoring",
        "Green Building Tour", "Environmental Film Screening", "Zero Waste Challenge", "Nature Photography Contest",
        "Butterfly Garden Creation", "Rain Garden Installation", "Environmental Science Fair", "Green Transportation Day",
        "Habitat Restoration Project", "Earth Day Festival",
        
        // Education & Youth (30)
        "Youth Mentorship Program", "Reading to Children", "Technology Workshop", "Career Development Workshop",
        "Coding Bootcamp for Kids", "Youth Leadership Summit", "After School Tutoring", "College Prep Session",
        "STEM Science Fair", "Creative Writing Workshop", "Math Olympiad Training", "Language Learning Circle",
        "Study Skills Seminar", "Scholarship Information Fair", "Educational Game Development", "Public Speaking Training",
        "Digital Literacy Class", "Homework Help Session", "Art Education Program", "Music Lesson Volunteer",
        "Science Experiment Lab", "Reading Comprehension Workshop", "Computer Skills Training", "Academic Mentoring",
        "Educational Field Trip", "Learning Disability Support", "Gifted Student Program", "Parent Education Night",
        "Teacher Appreciation Event", "School Supply Collection",
        
        // Health & Wellness (25)
        "Health Screening Event", "Mental Health Awareness Walk", "Senior Exercise Classes", "Healthcare Fair",
        "Blood Drive Campaign", "Wellness Education Seminar", "Nutrition Workshop", "Fitness Challenge Event",
        "First Aid Training", "Mental Health Support Group", "Health Insurance Information", "Disease Prevention Talk",
        "Medication Management Class", "Stress Reduction Workshop", "Healthy Cooking Class", "Vision Screening",
        "Dental Health Education", "Heart Health Awareness", "Diabetes Education Program", "Cancer Support Group",
        "Physical Therapy Demo", "Senior Health Fair", "Youth Health Education", "Addiction Recovery Support",
        "Community Health Assessment",
        
        // Community Service (30)
        "Food Drive Collection", "Senior Visit Day", "Homeless Shelter Meal Prep", "Community Safety Patrol",
        "Emergency Response Training", "Neighborhood Beautification", "Volunteer Appreciation Dinner", "Disaster Preparedness Training",
        "Community Mural Painting", "Local History Documentation", "Public Space Cleanup", "Winter Clothing Drive",
        "Holiday Gift Wrapping", "Community Garden Setup", "Senior Technology Help", "Voter Registration Drive",
        "Community Meeting Facilitation", "Neighborhood Watch Training", "Public Park Maintenance", "Community Survey",
        "Local Business Support", "Community Picnic Organization", "Furniture Donation Drive", "Transportation Assistance",
        "Community Resource Fair", "Public Art Installation", "Community Center Renovation", "Social Services Information",
        "Community Leadership Training", "Civic Engagement Workshop",
        
        // Arts, Culture & Sports (30)
        "Art Therapy Session", "Music Concert for Charity", "Cultural Festival", "Youth Basketball Tournament",
        "Cultural Dance Workshop", "Community Theater Production", "Art Supply Collection Drive", "Sports Equipment Drive",
        "Music Lesson Volunteers", "Creative Arts Workshop", "Cultural Heritage Celebration", "Athletic Training Camp",
        "Art Gallery Exhibition", "Dance Performance", "Music Therapy Session", "Sports Mentorship Program",
        "Cultural Exchange Event", "Art Competition Judging", "Sports Equipment Repair", "Creative Writing Contest",
        "Photography Workshop", "Drama Club Support", "Sports Safety Training", "Cultural Education Program",
        "Arts and Crafts Fair", "Athletic Scholarship Fundraiser", "Community Band Performance", "Art Installation Project",
        "Sports Tournament Organization", "Cultural Diversity Celebration"
    };
    
    private final String[] categories = {
        "Environment", "Education", "Health", "Community Service", "Animal Welfare", "Youth Development", 
        "Senior Care", "Arts & Culture", "Technology", "Sports & Recreation", "Mental Health", "Disaster Relief",
        "Hunger & Homelessness", "International", "Veterans", "Women's Issues", "Children & Families",
        "Disability Services", "Religious", "LGBTQ+", "Research & Advocacy", "Public Safety"
    };
    
    private final String[] orgTypes = {
        "Non-Profit", "Charity", "Foundation", "Community Group", "Religious Organization", 
        "Educational Institution", "Government Agency", "Social Enterprise", "Cooperative", "NGO"
    };
    
    private final String[] cities = {
        "Portland", "Seattle", "San Francisco", "Los Angeles", "Denver", "Austin", "Chicago", "New York", "Boston", "Miami",
        "Phoenix", "Philadelphia", "San Antonio", "Dallas", "San Jose", "Jacksonville", "Indianapolis", "Columbus", "Charlotte", "Detroit",
        "Memphis", "Baltimore", "El Paso", "Nashville", "Oklahoma City", "Louisville", "Milwaukee", "Las Vegas", "Albuquerque", "Tucson",
        "Fresno", "Sacramento", "Long Beach", "Kansas City", "Mesa", "Virginia Beach", "Atlanta", "Colorado Springs", "Omaha", "Raleigh",
        "Minneapolis", "Tulsa", "Cleveland", "Wichita", "New Orleans", "Tampa", "Honolulu", "Anaheim", "Santa Ana", "St. Louis"
    };
    
    private final String[] states = {
        "OR", "WA", "CA", "CA", "CO", "TX", "IL", "NY", "MA", "FL",
        "AZ", "PA", "TX", "TX", "CA", "FL", "IN", "OH", "NC", "MI",
        "TN", "MD", "TX", "TN", "OK", "KY", "WI", "NV", "NM", "AZ",
        "CA", "CA", "CA", "MO", "AZ", "VA", "GA", "CO", "NE", "NC",
        "MN", "OK", "OH", "KS", "LA", "FL", "HI", "CA", "CA", "MO"
    };
    
    private final String[] skills = {
        "Leadership", "Communication", "Teaching", "Event Planning", "Photography", "Graphic Design", 
        "Marketing", "Project Management", "First Aid", "Language Translation", "Public Speaking", 
        "Social Media", "Web Development", "Data Analysis", "Fundraising", "Customer Service",
        "Writing", "Research", "Team Building", "Problem Solving", "Counseling", "Tutoring",
        "Coaching", "Administration", "Financial Management", "Grant Writing", "Program Development"
    };
    
    private final String[] interests = {
        "Environment", "Education", "Healthcare", "Technology", "Arts", "Sports", "Community Development", 
        "Animal Welfare", "Senior Care", "Youth Programs", "Mental Health", "Social Justice", 
        "Cultural Events", "Disaster Relief", "Poverty Alleviation", "Science", "Music", "Reading",
        "Outdoor Activities", "Cooking", "Gardening", "Travel", "Photography", "Writing"
    };
    
    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println("🚀 Loading expanded mock data...");
            loadMockData();
            System.out.println("✅ Mock data loaded successfully!");
            System.out.println("📊 Created: 180 users, 145 organizations, 35 volunteer profiles, 145 events, and 200 applications");
        } else {
            System.out.println("📋 Data already exists, skipping mock data load");
        }
    }
    
    private void loadMockData() {
        // Create users first (180 total: 35 volunteers + 145 organizations)
        List<User> users = createMockUsers();
        
        // Create organizations and volunteer profiles
        List<OrganizationProfile> organizations = createMockOrganizations(users);
        List<VolunteerProfile> volunteers = createMockVolunteers(users);
        
        // Create events for organizations
        List<Event> events = createMockEvents(organizations);
        
        // Create applications
        createMockApplications(volunteers, events);
        
        // Create badges
        createMockBadges(users);
    }
    
    private List<User> createMockUsers() {
        List<User> users = new ArrayList<>();
        
        // Create 180 users (35 volunteers, 145 organizations)
        for (int i = 0; i < 180; i++) {
            User user = new User();
            String firstName = firstNames[i % firstNames.length];
            String lastName = lastNames[i % lastNames.length];
            
            // Make email unique by adding index
            user.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + i + "@example.com");
            user.setPassword(passwordEncoder.encode("password123"));
            
            // First 35 are volunteers, rest are organizations
            user.setUserType(i < 35 ? UserType.VOLUNTEER : UserType.ORGANIZATION);
            user.setIsActive(true);
            user.setEmailVerified(random.nextBoolean());
            user.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(365)));
            user.setUpdatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            
            users.add(userRepository.save(user));
        }
        
        return users;
    }
    
    private List<OrganizationProfile> createMockOrganizations(List<User> users) {
        List<OrganizationProfile> organizations = new ArrayList<>();
        
        // Create 145 organizations for the organization users (users 35-179)
        for (int i = 35; i < 180; i++) {
            OrganizationProfile org = new OrganizationProfile();
            
            // Link to user
            org.setUser(users.get(i));
            
            // Basic info
            int orgIndex = i - 35; // 0-144
            org.setOrganizationName(orgNames[orgIndex]);
            org.setDescription("A dedicated organization focused on " + categories[random.nextInt(categories.length)].toLowerCase() + " and community impact. We strive to make meaningful change through volunteer engagement and community partnerships.");
            org.setMissionStatement("To make a positive difference in our community through dedicated service, collaboration, and sustainable impact initiatives.");
            org.setWebsite("https://" + orgNames[orgIndex].toLowerCase().replace(" ", "").replace("'", "") + ".org");
            org.setPhoneNumber("+1-555-" + String.format("%04d", random.nextInt(10000)));
            org.setAddress((100 + random.nextInt(9900)) + " " + getRandomStreetName());
            
            String city = cities[random.nextInt(cities.length)];
            String state = states[random.nextInt(states.length)];
            org.setCity(city);
            org.setState(state);
            org.setZipCode(String.format("%05d", 10000 + random.nextInt(90000)));
            org.setCountry("USA");
            
            // Categories and type
            org.setCategories(getRandomCategories());
            org.setPrimaryCategory(categories[random.nextInt(categories.length)]);
            org.setOrganizationType(orgTypes[random.nextInt(orgTypes.length)]);
            
            // Enhanced fields with more realistic ranges
            org.setEmployeeCount(1 + random.nextInt(999)); // 1-1000 employees
            org.setFoundedYear(1970 + random.nextInt(55)); // 1970-2024
            org.setFundingGoal(25000 + random.nextInt(2000000)); // $25K-$2M
            org.setFundingRaised(random.nextInt(org.getFundingGoal()));
            org.setIsVerified(random.nextDouble() < 0.7); // 70% chance of being verified
            org.setTotalEventsHosted(random.nextInt(150));
            org.setTotalVolunteersServed(random.nextInt(2000));
            org.setLanguagesSupported(getRandomLanguages());
            org.setTaxExemptStatus(getTaxExemptStatus());
            org.setVerificationLevel(getVerificationLevel());
            
            // Additional fields
            org.setServices(getRandomServices());
            org.setCauses(getRandomCauses());
            org.setCoverImageUrl("https://images.unsplash.com/photo-" + (1500000000 + random.nextInt(200000000)) + "?w=1200&h=400&fit=crop");
            org.setProfileImageUrl("https://images.unsplash.com/photo-" + (1500000000 + random.nextInt(200000000)) + "?w=400&h=400&fit=crop");
            
            // Timestamps
            org.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(365)));
            org.setUpdatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            
            organizations.add(organizationRepository.save(org));
        }
        
        return organizations;
    }
    
    private List<VolunteerProfile> createMockVolunteers(List<User> users) {
        List<VolunteerProfile> volunteers = new ArrayList<>();
        
        // Create volunteer profiles for the first 35 users (volunteer users)
        for (int i = 0; i < 35; i++) {
            VolunteerProfile volunteer = new VolunteerProfile();
            
            // Link to user
            volunteer.setUser(users.get(i));
            
            // Basic info
            volunteer.setFirstName(firstNames[i % firstNames.length]);
            volunteer.setLastName(lastNames[i % lastNames.length]);
            volunteer.setBio(getRandomVolunteerBio());
            volunteer.setLocation(cities[random.nextInt(cities.length)] + ", " + states[random.nextInt(states.length)]);
            volunteer.setPhoneNumber("+1-555-" + String.format("%04d", random.nextInt(10000)));
            volunteer.setProfileImageUrl("https://images.unsplash.com/photo-" + (1500000000 + random.nextInt(200000000)) + "?w=400&h=400&fit=crop");
            
            // Volunteer-specific fields with realistic ranges
            volunteer.setTotalVolunteerHours(random.nextInt(1000));
            volunteer.setEventsParticipated(random.nextInt(75));
            volunteer.setIsAvailable(random.nextDouble() < 0.8); // 80% available
            volunteer.setSkills(getRandomSkills());
            volunteer.setInterests(getRandomInterests());
            volunteer.setAvailabilityPreference(getAvailabilityPreference());
            
            // Timestamps
            volunteer.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(365)));
            volunteer.setUpdatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            
            volunteers.add(volunteerRepository.save(volunteer));
        }
        
        return volunteers;
    }
    
    private List<Event> createMockEvents(List<OrganizationProfile> organizations) {
        List<Event> events = new ArrayList<>();
        
        // Create 145 events
        for (int i = 0; i < 145; i++) {
            Event event = new Event();
            
            // Basic info
            event.setTitle(eventTitles[i]);
            event.setDescription(getRandomEventDescription(eventTitles[i]));
            
            // Location details
            String city = cities[random.nextInt(cities.length)];
            String state = states[random.nextInt(states.length)];
            event.setAddress((100 + random.nextInt(9900)) + " " + getRandomStreetName());
            event.setCity(city);
            event.setState(state);
            event.setZipCode(String.format("%05d", 10000 + random.nextInt(90000)));
            event.setLocation(city + ", " + state);
            
            // Contact info
            event.setContactEmail("contact@" + organizations.get(i % organizations.size()).getOrganizationName().toLowerCase().replace(" ", "").replace("'", "") + ".org");
            event.setContactPhone("+1-555-" + String.format("%04d", random.nextInt(10000)));
            
            // Dates and timing - mix of past, current, and future events
            LocalDateTime startDate;
            if (random.nextDouble() < 0.2) {
                // 20% past events
                startDate = LocalDateTime.now().minusDays(random.nextInt(180));
            } else if (random.nextDouble() < 0.1) {
                // 10% current/today events
                startDate = LocalDateTime.now().plusHours(random.nextInt(12));
            } else {
                // 70% future events
                startDate = LocalDateTime.now().plusDays(random.nextInt(120));
            }
            
            event.setStartDate(startDate);
            event.setEndDate(startDate.plusHours(1 + random.nextInt(10))); // 1-10 hour events
            
            // Volunteer details with more realistic ranges
            int maxVolunteers = 3 + random.nextInt(197); // 3-200 max volunteers
            event.setMaxVolunteers(maxVolunteers);
            event.setCurrentVolunteers(random.nextInt(maxVolunteers + 1));
            event.setEstimatedHours(1 + random.nextInt(12)); // 1-12 hours
            
            // Event characteristics (using your actual enum values)
            event.setEventType(EventType.values()[random.nextInt(EventType.values().length)]);
            event.setSkillLevelRequired(SkillLevel.values()[random.nextInt(SkillLevel.values().length)]);
            event.setDurationCategory(EventDuration.values()[random.nextInt(EventDuration.values().length)]);
            event.setStatus(getRealisticEventStatus(startDate));
            
            // Boolean flags with realistic distributions
            event.setIsVirtual(random.nextDouble() < 0.3); // 30% virtual
            event.setHasFlexibleTiming(random.nextDouble() < 0.4); // 40% flexible
            event.setIsRecurring(random.nextDouble() < 0.25); // 25% recurring
            event.setIsWeekdaysOnly(random.nextDouble() < 0.3); // 30% weekdays only
            event.setIsWeekendsOnly(!event.getIsWeekdaysOnly() && random.nextDouble() < 0.4); // 40% weekends only if not weekdays
            
            // Additional details
            event.setRequirements(generateRequirements());
            
            // Virtual event details
            if (event.getIsVirtual()) {
                event.setVirtualMeetingLink("https://meet.google.com/" + generateMeetingId());
            }
            
            // Recurring event details
            if (event.getIsRecurring()) {
                String[] patterns = {"Weekly", "Monthly", "Bi-weekly", "Quarterly"};
                event.setRecurrencePattern(patterns[random.nextInt(patterns.length)]);
            }
            
            // Image URL
            event.setImageUrl("https://images.unsplash.com/photo-" + (1500000000 + random.nextInt(200000000)) + "?w=800&h=400&fit=crop");
            
            // Timestamps
            event.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(90)));
            event.setUpdatedAt(LocalDateTime.now().minusDays(random.nextInt(14)));
            
            // Assign to organization (cycle through organizations)
            event.setOrganization(organizations.get(i % organizations.size()));
            
            events.add(eventRepository.save(event));
        }
        
        return events;
    }
    
    private void createMockApplications(List<VolunteerProfile> volunteers, List<Event> events) {
        // Create 200 random applications (increased from 75)
        for (int i = 0; i < 200; i++) {
            Application application = new Application();
            
            // Random volunteer and event
            VolunteerProfile volunteer = volunteers.get(random.nextInt(volunteers.size()));
            Event event = events.get(random.nextInt(events.size()));
            
            application.setVolunteer(volunteer);
            application.setEvent(event);
            application.setStatus(getRealisticApplicationStatus());
            application.setMessage(getRandomApplicationMessage());
            
            if (application.getStatus() != ApplicationStatus.PENDING) {
                application.setOrganizationNotes(getRandomOrganizationNotes());
                application.setRespondedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            }
            
            if (application.getStatus() == ApplicationStatus.ATTENDED) {
                application.setHoursCompleted(1 + random.nextInt(12));
                application.setCompletedAt(LocalDateTime.now().minusDays(random.nextInt(10)));
            }
            
            application.setAppliedAt(LocalDateTime.now().minusDays(random.nextInt(60)));
            
            applicationRepository.save(application);
        }
    }
    
    private void createMockBadges(List<User> users) {
        // Create badges for 60% of users
        for (User user : users) {
            if (random.nextDouble() < 0.6) {
                Badge badge = new Badge();
                badge.setUser(user);
                badge.setBadgeType(BadgeType.values()[random.nextInt(BadgeType.values().length)]);
                badge.setProgressValue(random.nextInt(badge.getBadgeType().getRequiredCount() + 20));
                badge.setIsFeatured(random.nextDouble() < 0.3); // 30% featured
                badge.setNotes(getRandomBadgeNotes());
                badge.setEarnedAt(LocalDateTime.now().minusDays(random.nextInt(365)));
                
                badgeRepository.save(badge);
            }
        }
    }
    
    // Helper methods for more realistic data generation
    
    private String getRandomCategories() {
        List<String> selectedCategories = new ArrayList<>();
        int numCategories = 1 + random.nextInt(4); // 1-4 categories
        
        for (int i = 0; i < numCategories; i++) {
            String category = categories[random.nextInt(categories.length)];
            if (!selectedCategories.contains(category)) {
                selectedCategories.add(category);
            }
        }
        
        return String.join(",", selectedCategories);
    }
    
    private String getRandomLanguages() {
        String[] languages = {"English", "Spanish", "French", "German", "Italian", "Portuguese", "Chinese", "Japanese", "Korean", "Arabic"};
        List<String> selected = new ArrayList<>();
        selected.add("English"); // Always include English
        
        int numLanguages = random.nextInt(3); // 0-2 additional languages
        for (int i = 0; i < numLanguages; i++) {
            String language = languages[1 + random.nextInt(languages.length - 1)]; // Skip English
            if (!selected.contains(language)) {
                selected.add(language);
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
    
    private String getRandomServices() {
        String[] services = {
            "Tutoring", "Cleanup Events", "Food Distribution", "Mentoring", "Fundraising", "Community Outreach",
            "Health Screenings", "Emergency Response", "Educational Workshops", "Social Services", "Environmental Education",
            "Senior Care", "Youth Programs", "Technology Training", "Arts & Crafts", "Sports Coaching", "Language Classes",
            "Job Training", "Financial Literacy", "Housing Assistance", "Transportation Services", "Childcare Support",
            "Mental Health Counseling", "Addiction Recovery", "Disability Services", "Immigration Support"
        };
        List<String> selected = new ArrayList<>();
        int numServices = 1 + random.nextInt(4);
        
        for (int i = 0; i < numServices; i++) {
            String service = services[random.nextInt(services.length)];
            if (!selected.contains(service)) {
                selected.add(service);
            }
        }
        
        return String.join(",", selected);
    }
    
    private String getRandomCauses() {
        String[] causes = {
            "Education", "Environment", "Health", "Poverty", "Animal Welfare", "Community Development",
            "Social Justice", "Youth Development", "Senior Care", "Disaster Relief", "Mental Health",
            "Veterans Support", "Women's Rights", "LGBTQ+ Rights", "Racial Equality", "Immigration",
            "Human Rights", "Hunger Relief", "Homelessness", "Public Safety", "Arts & Culture", "Technology"
        };
        List<String> selected = new ArrayList<>();
        int numCauses = 1 + random.nextInt(4);
        
        for (int i = 0; i < numCauses; i++) {
            String cause = causes[random.nextInt(causes.length)];
            if (!selected.contains(cause)) {
                selected.add(cause);
            }
        }
        
        return String.join(",", selected);
    }
    
    private String getRandomSkills() {
        List<String> selected = new ArrayList<>();
        int numSkills = 1 + random.nextInt(5);
        
        for (int i = 0; i < numSkills; i++) {
            String skill = skills[random.nextInt(skills.length)];
            if (!selected.contains(skill)) {
                selected.add(skill);
            }
        }
        
        return String.join(",", selected);
    }
    
    private String getRandomInterests() {
        List<String> selected = new ArrayList<>();
        int numInterests = 1 + random.nextInt(5);
        
        for (int i = 0; i < numInterests; i++) {
            String interest = interests[random.nextInt(interests.length)];
            if (!selected.contains(interest)) {
                selected.add(interest);
            }
        }
        
        return String.join(",", selected);
    }
    
    private String getAvailabilityPreference() {
        String[] preferences = {"weekends", "weekdays", "evenings", "flexible", "mornings", "afternoons"};
        return preferences[random.nextInt(preferences.length)];
    }
    
    private String getRandomVolunteerBio() {
        String[] bios = {
            "Passionate volunteer dedicated to making a positive impact in the community through meaningful service and collaboration.",
            "Experienced volunteer with a strong commitment to environmental conservation and community outreach programs.",
            "Enthusiastic about helping others and contributing to causes that promote education and youth development.",
            "Community-minded individual with expertise in event planning and a passion for social justice initiatives.",
            "Dedicated volunteer with experience in healthcare support and senior care programs.",
            "Technology professional committed to bridging the digital divide and supporting educational initiatives.",
            "Creative individual passionate about arts education and cultural preservation in the community.",
            "Experienced mentor focused on youth development and leadership training programs.",
            "Health advocate with a background in nutrition and wellness education for underserved communities.",
            "Environmental scientist dedicated to conservation efforts and sustainability education.",
            "Former educator committed to literacy programs and academic support for students of all ages.",
            "Social worker passionate about mental health awareness and community support systems."
        };
        return bios[random.nextInt(bios.length)];
    }
    
    private String getRandomEventDescription(String title) {
        String[] templates = {
            "Join us for " + title.toLowerCase() + ". This is a fantastic opportunity to make a meaningful difference in our community while connecting with like-minded individuals who share your passion for positive change.",
            "We're excited to invite you to participate in " + title.toLowerCase() + ". Whether you're new to volunteering or an experienced community member, we welcome your participation and unique contributions.",
            "Come be part of " + title.toLowerCase() + " and help us create lasting positive impact in our community. This event offers a great way to meet new people while supporting an important cause.",
            "Volunteers needed for " + title.toLowerCase() + "! This is an excellent opportunity to gain hands-on experience while making a real difference in the lives of others.",
            "Join our team for " + title.toLowerCase() + " and experience the joy of community service. All skill levels welcome - we'll provide training and support.",
            "Make a difference at " + title.toLowerCase() + "! This volunteer opportunity allows you to contribute directly to our mission while developing new skills and connections."
        };
        return templates[random.nextInt(templates.length)];
    }
    
    private EventStatus getRealisticEventStatus(LocalDateTime startDate) {
        LocalDateTime now = LocalDateTime.now();
        
        if (startDate.isBefore(now.minusDays(1))) {
            // Past events
            return random.nextDouble() < 0.9 ? EventStatus.COMPLETED : EventStatus.CANCELLED;
        } else if (startDate.isAfter(now.plusDays(7))) {
            // Future events
            double rand = random.nextDouble();
            if (rand < 0.7) return EventStatus.ACTIVE;
            if (rand < 0.85) return EventStatus.DRAFT;
            return EventStatus.FULL;
        } else {
            // Near-term events
            return EventStatus.ACTIVE;
        }
    }
    
    private ApplicationStatus getRealisticApplicationStatus() {
        double rand = random.nextDouble();
        if (rand < 0.3) return ApplicationStatus.PENDING;
        if (rand < 0.6) return ApplicationStatus.ACCEPTED;
        if (rand < 0.75) return ApplicationStatus.ATTENDED;
        if (rand < 0.85) return ApplicationStatus.REJECTED;
        if (rand < 0.95) return ApplicationStatus.NO_SHOW;
        return ApplicationStatus.WITHDRAWN;
    }
    
    private String getRandomApplicationMessage() {
        String[] messages = {
            "I'm excited to volunteer for this event and contribute to the community!",
            "This cause is very important to me and I'd love to help make a difference.",
            "I have relevant experience and skills that would be valuable for this event.",
            "Looking forward to meeting other volunteers and learning more about your organization.",
            "I'm passionate about this cause and eager to contribute my time and energy.",
            "I believe in the mission of your organization and want to support your work.",
            "This event aligns perfectly with my interests and availability.",
            "I'm committed to making a positive impact and would be honored to volunteer.",
            "I have experience in this area and am excited to put my skills to good use.",
            "This sounds like a meaningful way to give back to the community.",
            "I'm available for the full duration and ready to help in any way needed.",
            "I'm interested in learning more about your organization while volunteering."
        };
        return messages[random.nextInt(messages.length)];
    }
    
    private String getRandomOrganizationNotes() {
        String[] notes = {
            "Thank you for your interest in volunteering with us. We're excited to have you join our team!",
            "We appreciate your application and look forward to working with you on this important initiative.",
            "Your skills and enthusiasm make you a great fit for this volunteer opportunity.",
            "We're grateful for volunteers like you who help make our mission possible.",
            "Thank you for your dedication to community service and positive change.",
            "We look forward to your participation and the unique perspective you'll bring to our team.",
            "Your commitment to this cause is evident and we're honored to have your support.",
            "We believe you'll find this volunteer experience both rewarding and impactful.",
            "Thank you for choosing to volunteer with us - together we can make a difference!",
            "Your application stood out to us and we're excited to welcome you to our volunteer family."
        };
        return notes[random.nextInt(notes.length)];
    }
    
    private String getRandomBadgeNotes() {
        String[] notes = {
            "Earned through dedicated community service and outstanding volunteer work.",
            "Recognized for exceptional commitment to environmental conservation efforts.",
            "Awarded for leadership in youth mentorship and educational programs.",
            "Achieved through consistent participation in community outreach initiatives.",
            "Earned for significant contributions to healthcare and social service programs.",
            "Recognized for innovation in technology education and digital literacy programs.",
            "Awarded for outstanding performance in disaster relief and emergency response.",
            "Earned through exemplary service in arts and cultural preservation programs.",
            "Recognized for dedication to senior care and intergenerational programs.",
            "Awarded for excellence in fundraising and resource development activities."
        };
        return notes[random.nextInt(notes.length)];
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
    
    private String getRandomStreetName() {
        String[] streetNames = {
            "Main St", "Oak Ave", "Pine Rd", "Elm Dr", "Maple Ln", "Cedar Blvd", "Park Ave", "First St", "Second St", "Broadway",
            "Washington St", "Lincoln Ave", "Jefferson Rd", "Madison Dr", "Monroe Ln", "Jackson Blvd", "Adams Ave", "Franklin St",
            "State St", "Church St", "School St", "High St", "Mill St", "Water St", "Spring St", "Market St", "Union St", "Hill St"
        };
        return streetNames[random.nextInt(streetNames.length)];
    }
    
    private String generateRequirements() {
        String[] requirements = {
            "No experience necessary",
            "Must be 18 or older",
            "Physical activity required",
            "Background check required",
            "Own transportation preferred",
            "Comfortable working with children",
            "Lifting up to 25 lbs required",
            "Outdoor work in various weather",
            "Must be comfortable with public speaking",
            "Basic computer skills helpful",
            "Flexible schedule preferred",
            "Team player attitude essential",
            "Professional appearance required",
            "Bilingual skills a plus",
            "Previous volunteer experience preferred",
            "Must be able to stand for extended periods",
            "Valid driver's license required",
            "First aid certification preferred",
            "Comfortable with technology",
            "Strong communication skills needed",
            "Ability to work independently",
            "Weekend availability required",
            "Evening hours available",
            "Must be reliable and punctual"
        };
        
        List<String> selected = new ArrayList<>();
        int numReqs = 1 + random.nextInt(4); // 1-4 requirements
        
        for (int i = 0; i < numReqs; i++) {
            String req = requirements[random.nextInt(requirements.length)];
            if (!selected.contains(req)) {
                selected.add(req);
            }
        }
        
        return String.join("; ", selected);
    }
}