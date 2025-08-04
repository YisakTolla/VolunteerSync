package com.volunteersync.backend.repository;

import com.volunteersync.backend.entity.Event;
import com.volunteersync.backend.entity.OrganizationProfile;
import com.volunteersync.backend.enums.EventStatus;
import com.volunteersync.backend.enums.EventType;
import com.volunteersync.backend.enums.SkillLevel;
import com.volunteersync.backend.enums.EventDuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // =====================================================
    // ORGANIZATION EVENT QUERIES
    // =====================================================

    /**
     * Find all events by organization
     */
    List<Event> findByOrganization(OrganizationProfile organization);

    /**
     * Find events by organization ordered by date
     */
    List<Event> findByOrganizationOrderByStartDateDesc(OrganizationProfile organization);

    /**
     * Find events by organization and status
     */
    List<Event> findByOrganizationAndStatus(OrganizationProfile organization, EventStatus status);

    // =====================================================
    // STATUS-BASED QUERIES
    // =====================================================

    /**
     * Find events by status
     */
    List<Event> findByStatus(EventStatus status);

    /**
     * Find events by status ordered by date
     */
    List<Event> findByStatusOrderByStartDateAsc(EventStatus status);

    /**
     * Find upcoming active events for browsing
     */
    @Query("SELECT e FROM Event e WHERE e.status = 'ACTIVE' AND e.startDate > :now ORDER BY e.startDate ASC")
    List<Event> findUpcomingActiveEvents(@Param("now") LocalDateTime now);

    /**
     * Find past events (completed or cancelled)
     */
    @Query("SELECT e FROM Event e WHERE e.startDate < :now AND (e.status = 'COMPLETED' OR e.status = 'CANCELLED') ORDER BY e.startDate DESC")
    List<Event> findPastEvents(@Param("now") LocalDateTime now);

    // =====================================================
    // ENHANCED EVENT TYPE FILTERING
    // =====================================================

    /**
     * Find events by event type
     */
    List<Event> findByEventTypeAndStatus(EventType eventType, EventStatus status);
    
    /**
     * Find events by event type with pagination
     */
    Page<Event> findByEventTypeAndStatus(EventType eventType, EventStatus status, Pageable pageable);

    /**
     * Find events by multiple event types
     */
    @Query("SELECT e FROM Event e WHERE e.eventType IN :eventTypes AND e.status = :status ORDER BY e.startDate ASC")
    List<Event> findByEventTypesAndStatus(@Param("eventTypes") List<EventType> eventTypes, @Param("status") EventStatus status);

    /**
     * Get event type statistics
     */
    @Query("SELECT e.eventType, COUNT(e) FROM Event e WHERE e.eventType IS NOT NULL GROUP BY e.eventType ORDER BY COUNT(e) DESC")
    List<Object[]> getEventTypeStatistics();

    // =====================================================
    // ENHANCED SKILL LEVEL FILTERING
    // =====================================================

    /**
     * Find events by skill level requirement
     */
    List<Event> findBySkillLevelRequiredAndStatus(SkillLevel skillLevel, EventStatus status);

    /**
     * Find events accessible for specific skill level or lower
     */
    @Query("SELECT e FROM Event e WHERE e.status = :status AND " +
           "(e.skillLevelRequired IS NULL OR e.skillLevelRequired <= :skillLevel) " +
           "ORDER BY e.startDate ASC")
    List<Event> findEventsAccessibleForSkillLevel(@Param("skillLevel") SkillLevel skillLevel, @Param("status") EventStatus status);

    /**
     * Find events requiring no experience
     */
    @Query("SELECT e FROM Event e WHERE e.status = :status AND " +
           "(e.skillLevelRequired IS NULL OR e.skillLevelRequired = 'NO_EXPERIENCE_REQUIRED') " +
           "ORDER BY e.startDate ASC")
    List<Event> findBeginnerFriendlyEvents(@Param("status") EventStatus status);

    /**
     * Get skill level statistics
     */
    @Query("SELECT e.skillLevelRequired, COUNT(e) FROM Event e WHERE e.skillLevelRequired IS NOT NULL GROUP BY e.skillLevelRequired ORDER BY COUNT(e) DESC")
    List<Object[]> getSkillLevelStatistics();

    // =====================================================
    // ENHANCED DURATION FILTERING
    // =====================================================

    /**
     * Find events by duration category
     */
    List<Event> findByDurationCategoryAndStatus(EventDuration durationCategory, EventStatus status);

    /**
     * Find short events (1-2 hours)
     */
    @Query("SELECT e FROM Event e WHERE e.status = :status AND " +
           "(e.durationCategory = 'SHORT' OR " +
           "(e.startDate IS NOT NULL AND e.endDate IS NOT NULL AND " +
           "FUNCTION('TIMESTAMPDIFF', HOUR, e.startDate, e.endDate) <= 2)) " +
           "ORDER BY e.startDate ASC")
    List<Event> findShortEvents(@Param("status") EventStatus status);

    /**
     * Find long-term commitment events
     */
    @Query("SELECT e FROM Event e WHERE e.status = :status AND " +
           "(e.durationCategory IN ('WEEKLY', 'MONTHLY', 'ONGOING') OR e.isRecurring = true) " +
           "ORDER BY e.startDate ASC")
    List<Event> findLongTermEvents(@Param("status") EventStatus status);

    /**
     * Get duration statistics
     */
    @Query("SELECT e.durationCategory, COUNT(e) FROM Event e WHERE e.durationCategory IS NOT NULL GROUP BY e.durationCategory ORDER BY COUNT(e) DESC")
    List<Object[]> getDurationStatistics();

    // =====================================================
    // ENHANCED LOCATION AND VIRTUAL FILTERING
    // =====================================================

    /**
     * Find virtual events
     */
    List<Event> findByIsVirtualAndStatus(Boolean isVirtual, EventStatus status);

    /**
     * Find in-person events
     */
    @Query("SELECT e FROM Event e WHERE e.status = :status AND (e.isVirtual = false OR e.isVirtual IS NULL) ORDER BY e.startDate ASC")
    List<Event> findInPersonEvents(@Param("status") EventStatus status);

    /**
     * Find events by location with virtual option
     */
    @Query("SELECT e FROM Event e WHERE e.status = :status AND " +
           "(e.isVirtual = true OR " +
           "LOWER(e.city) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "LOWER(e.state) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
           "ORDER BY e.startDate ASC")
    List<Event> findEventsByLocationIncludingVirtual(@Param("location") String location, @Param("status") EventStatus status);

    // =====================================================
    // ENHANCED TIME-BASED FILTERING
    // =====================================================

    /**
     * Find events by time of day
     */
    List<Event> findByTimeOfDayAndStatus(String timeOfDay, EventStatus status);

    /**
     * Find morning events
     */
    @Query("SELECT e FROM Event e WHERE e.status = :status AND e.timeOfDay = 'MORNING' ORDER BY e.startDate ASC")
    List<Event> findMorningEvents(@Param("status") EventStatus status);

    /**
     * Find afternoon events
     */
    @Query("SELECT e FROM Event e WHERE e.status = :status AND e.timeOfDay = 'AFTERNOON' ORDER BY e.startDate ASC")
    List<Event> findAfternoonEvents(@Param("status") EventStatus status);

    /**
     * Find evening events
     */
    @Query("SELECT e FROM Event e WHERE e.status = :status AND e.timeOfDay = 'EVENING' ORDER BY e.startDate ASC")
    List<Event> findEveningEvents(@Param("status") EventStatus status);

    /**
     * Find weekday events
     */
    List<Event> findByIsWeekdaysOnlyAndStatus(Boolean isWeekdaysOnly, EventStatus status);

    /**
     * Find weekend events
     */
    List<Event> findByIsWeekendsOnlyAndStatus(Boolean isWeekendsOnly, EventStatus status);

    /**
     * Find flexible timing events
     */
    List<Event> findByHasFlexibleTimingAndStatus(Boolean hasFlexibleTiming, EventStatus status);

    /**
     * Find recurring events
     */
    List<Event> findByIsRecurringAndStatus(Boolean isRecurring, EventStatus status);

    /**
     * Find events by recurrence pattern
     */
    List<Event> findByRecurrencePatternAndStatus(String recurrencePattern, EventStatus status);

    // =====================================================
    // COMPREHENSIVE FILTERING QUERY
    // =====================================================

    /**
     * Advanced filter query supporting all filter criteria
     */
    @Query("SELECT e FROM Event e WHERE " +
           "e.status = :status AND " +
           "(:eventType IS NULL OR e.eventType = :eventType) AND " +
           "(:skillLevel IS NULL OR e.skillLevelRequired <= :skillLevel OR e.skillLevelRequired IS NULL) AND " +
           "(:durationCategory IS NULL OR e.durationCategory = :durationCategory) AND " +
           "(:isVirtual IS NULL OR e.isVirtual = :isVirtual) AND " +
           "(:timeOfDay IS NULL OR e.timeOfDay = :timeOfDay) AND " +
           "(:isWeekend IS NULL OR " +
           "  (:isWeekend = true AND e.isWeekendsOnly = true) OR " +
           "  (:isWeekend = false AND e.isWeekdaysOnly = true)" +
           ") AND " +
           "(:hasFlexibleTiming IS NULL OR e.hasFlexibleTiming = :hasFlexibleTiming) AND " +
           "(:isRecurring IS NULL OR e.isRecurring = :isRecurring) AND " +
           "(:location IS NULL OR " +
           "  e.isVirtual = true OR " +
           "  LOWER(e.city) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "  LOWER(e.state) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "  LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%'))" +
           ") AND " +
           "(:startDate IS NULL OR e.startDate >= :startDate) AND " +
           "(:endDate IS NULL OR e.startDate <= :endDate) " +
           "ORDER BY " +
           "CASE WHEN :sortBy = 'date' THEN e.startDate END ASC, " +
           "CASE WHEN :sortBy = 'capacity' THEN e.maxVolunteers END DESC, " +
           "CASE WHEN :sortBy = 'spots' THEN (e.maxVolunteers - e.currentVolunteers) END DESC, " +
           "CASE WHEN :sortBy = 'created' THEN e.createdAt END DESC, " +
           "e.startDate ASC")
    Page<Event> findWithAdvancedFilters(
            @Param("status") EventStatus status,
            @Param("eventType") EventType eventType,
            @Param("skillLevel") SkillLevel skillLevel,
            @Param("durationCategory") EventDuration durationCategory,
            @Param("isVirtual") Boolean isVirtual,
            @Param("timeOfDay") String timeOfDay,
            @Param("isWeekend") Boolean isWeekend,
            @Param("hasFlexibleTiming") Boolean hasFlexibleTiming,
            @Param("isRecurring") Boolean isRecurring,
            @Param("location") String location,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("sortBy") String sortBy,
            Pageable pageable);

    // =====================================================
    // SEARCH AND FILTERING
    // =====================================================

    /**
     * Search events by title
     */
    List<Event> findByTitleContainingIgnoreCaseAndStatus(String title, EventStatus status);

    /**
     * Search events by title (any status)
     */
    List<Event> findByTitleContainingIgnoreCase(String title);

    /**
     * Find events by location
     */
    List<Event> findByCityContainingIgnoreCaseAndStatus(String city, EventStatus status);

    List<Event> findByStateContainingIgnoreCaseAndStatus(String state, EventStatus status);

    /**
     * Complex location search for active events
     */
    @Query("SELECT e FROM Event e WHERE e.status = 'ACTIVE' AND " +
            "(LOWER(e.city) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
            "LOWER(e.state) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
            "LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
            "ORDER BY e.startDate ASC")
    List<Event> findActiveEventsByLocation(@Param("location") String location);

    /**
     * Search events by description/requirements
     */
    @Query("SELECT e FROM Event e WHERE e.status = 'ACTIVE' AND " +
            "(LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.requirements) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Event> findActiveEventsByKeyword(@Param("keyword") String keyword);

    /**
     * Search events with filters and text search
     */
    @Query("SELECT e FROM Event e WHERE " +
           "e.status = :status AND " +
           "(:searchTerm IS NULL OR " +
           "  LOWER(e.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "  LOWER(e.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "  LOWER(e.requirements) LIKE LOWER(CONCAT('%', :searchTerm, '%'))" +
           ") AND " +
           "(:eventType IS NULL OR e.eventType = :eventType) AND " +
           "(:location IS NULL OR " +
           "  e.isVirtual = true OR " +
           "  LOWER(e.city) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "  LOWER(e.state) LIKE LOWER(CONCAT('%', :location, '%'))" +
           ") AND " +
           "(:skillLevel IS NULL OR e.skillLevelRequired <= :skillLevel OR e.skillLevelRequired IS NULL)")
    Page<Event> searchWithFilters(
            @Param("searchTerm") String searchTerm,
            @Param("status") EventStatus status,
            @Param("eventType") EventType eventType,
            @Param("location") String location,
            @Param("skillLevel") SkillLevel skillLevel,
            Pageable pageable);

    // =====================================================
    // DATE RANGE QUERIES
    // =====================================================

    /**
     * Find events in date range
     */
    @Query("SELECT e FROM Event e WHERE e.startDate BETWEEN :startDate AND :endDate ORDER BY e.startDate ASC")
    List<Event> findEventsBetweenDates(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find events starting today
     */
    @Query("SELECT e FROM Event e WHERE DATE(e.startDate) = DATE(:today) ORDER BY e.startDate ASC")
    List<Event> findEventsToday(@Param("today") LocalDateTime today);

    /**
     * Find events this week
     */
    @Query("SELECT e FROM Event e WHERE e.startDate BETWEEN :weekStart AND :weekEnd ORDER BY e.startDate ASC")
    List<Event> findEventsThisWeek(@Param("weekStart") LocalDateTime weekStart,
            @Param("weekEnd") LocalDateTime weekEnd);

    /**
     * Find events tomorrow
     */
    @Query("SELECT e FROM Event e WHERE e.status = :status AND " +
           "DATE(e.startDate) = DATE(:tomorrow) ORDER BY e.startDate ASC")
    List<Event> findEventsTomorrow(@Param("tomorrow") LocalDateTime tomorrow, @Param("status") EventStatus status);

    /**
     * Find events this weekend
     */
    @Query("SELECT e FROM Event e WHERE e.status = :status AND " +
           "e.startDate BETWEEN :weekendStart AND :weekendEnd AND " +
           "(e.isWeekendsOnly = true OR DAYOFWEEK(e.startDate) IN (1, 7)) " +
           "ORDER BY e.startDate ASC")
    List<Event> findEventsThisWeekend(@Param("weekendStart") LocalDateTime weekendStart,
            @Param("weekendEnd") LocalDateTime weekendEnd, @Param("status") EventStatus status);

    /**
     * Find events next week
     */
    @Query("SELECT e FROM Event e WHERE e.status = :status AND " +
           "e.startDate BETWEEN :nextWeekStart AND :nextWeekEnd ORDER BY e.startDate ASC")
    List<Event> findEventsNextWeek(@Param("nextWeekStart") LocalDateTime nextWeekStart,
            @Param("nextWeekEnd") LocalDateTime nextWeekEnd, @Param("status") EventStatus status);

    /**
     * Find events this month
     */
    @Query("SELECT e FROM Event e WHERE e.status = :status AND " +
           "YEAR(e.startDate) = :year AND MONTH(e.startDate) = :month ORDER BY e.startDate ASC")
    List<Event> findEventsThisMonth(@Param("year") int year, @Param("month") int month, @Param("status") EventStatus status);

    // =====================================================
    // AVAILABILITY AND CAPACITY
    // =====================================================

    /**
     * Find events with available spots
     */
    @Query("SELECT e FROM Event e WHERE e.status = 'ACTIVE' AND " +
            "(e.maxVolunteers IS NULL OR e.currentVolunteers < e.maxVolunteers) " +
            "ORDER BY e.startDate ASC")
    List<Event> findEventsWithAvailableSpots();

    /**
     * Find events by volunteer capacity range
     */
    @Query("SELECT e FROM Event e WHERE e.status = 'ACTIVE' AND " +
            "e.maxVolunteers BETWEEN :minCapacity AND :maxCapacity " +
            "ORDER BY e.startDate ASC")
    List<Event> findEventsByCapacityRange(@Param("minCapacity") Integer minCapacity,
            @Param("maxCapacity") Integer maxCapacity);

    /**
     * Find small events (less than 10 volunteers)
     */
    @Query("SELECT e FROM Event e WHERE e.status = 'ACTIVE' AND e.maxVolunteers <= 10")
    List<Event> findSmallEvents();

    /**
     * Find large events (more than 50 volunteers)
     */
    @Query("SELECT e FROM Event e WHERE e.status = 'ACTIVE' AND e.maxVolunteers > 50")
    List<Event> findLargeEvents();

    /**
     * Find events almost full (90% capacity)
     */
    @Query("SELECT e FROM Event e WHERE e.status = 'ACTIVE' AND " +
           "e.maxVolunteers IS NOT NULL AND " +
           "e.currentVolunteers >= (e.maxVolunteers * 0.9) AND " +
           "e.currentVolunteers < e.maxVolunteers " +
           "ORDER BY e.startDate ASC")
    List<Event> findEventsAlmostFull();

    /**
     * Find events with specific spots remaining
     */
    @Query("SELECT e FROM Event e WHERE e.status = 'ACTIVE' AND " +
           "e.maxVolunteers IS NOT NULL AND " +
           "(e.maxVolunteers - e.currentVolunteers) <= :spotsRemaining AND " +
           "e.currentVolunteers < e.maxVolunteers " +
           "ORDER BY e.startDate ASC")
    List<Event> findEventsBySpotsRemaining(@Param("spotsRemaining") Integer spotsRemaining);

    // =====================================================
    // URGENT AND PRIORITY EVENTS
    // =====================================================

    /**
     * Find urgent events (starting within 24 hours)
     */
    @Query("SELECT e FROM Event e WHERE e.status = 'ACTIVE' AND " +
           "e.startDate BETWEEN :now AND :twentyFourHours ORDER BY e.startDate ASC")
    List<Event> findUrgentEvents(@Param("now") LocalDateTime now, @Param("twentyFourHours") LocalDateTime twentyFourHours);

    /**
     * Find events starting soon (within 72 hours)
     */
    @Query("SELECT e FROM Event e WHERE e.status = 'ACTIVE' AND " +
           "e.startDate BETWEEN :now AND :seventyTwoHours ORDER BY e.startDate ASC")
    List<Event> findEventsStartingSoon(@Param("now") LocalDateTime now, @Param("seventyTwoHours") LocalDateTime seventyTwoHours);

    /**
     * Find events needing volunteers (low registration)
     */
    @Query("SELECT e FROM Event e WHERE e.status = 'ACTIVE' AND " +
           "e.maxVolunteers IS NOT NULL AND " +
           "e.currentVolunteers < (e.maxVolunteers * 0.5) AND " +
           "e.startDate > :now " +
           "ORDER BY e.startDate ASC")
    List<Event> findEventsNeedingVolunteers(@Param("now") LocalDateTime now);

    // =====================================================
    // STATISTICS QUERIES
    // =====================================================

    /**
     * Count events by status
     */
    long countByStatus(EventStatus status);

    /**
     * Count upcoming events
     */
    @Query("SELECT COUNT(e) FROM Event e WHERE e.startDate > :now AND e.status = 'ACTIVE'")
    long countUpcomingEvents(@Param("now") LocalDateTime now);

    /**
     * Count events by organization
     */
    long countByOrganization(OrganizationProfile organization);

    /**
     * Count events by event type
     */
    long countByEventTypeAndStatus(EventType eventType, EventStatus status);

    /**
     * Count virtual vs in-person events
     */
    @Query("SELECT " +
           "SUM(CASE WHEN e.isVirtual = true THEN 1 ELSE 0 END) as virtual, " +
           "SUM(CASE WHEN e.isVirtual = false OR e.isVirtual IS NULL THEN 1 ELSE 0 END) as inPerson " +
           "FROM Event e WHERE e.status = :status")
    Object[] getVirtualVsInPersonStats(@Param("status") EventStatus status);

    /**
     * Get event statistics by date range
     */
    @Query("SELECT COUNT(e), AVG(e.maxVolunteers), SUM(e.currentVolunteers) FROM Event e " +
            "WHERE e.startDate BETWEEN :startDate AND :endDate")
    Object[] getEventStatsBetweenDates(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Get monthly event creation stats
     */
    @Query("SELECT YEAR(e.createdAt), MONTH(e.createdAt), COUNT(e) FROM Event e " +
            "WHERE e.createdAt >= :since GROUP BY YEAR(e.createdAt), MONTH(e.createdAt) " +
            "ORDER BY YEAR(e.createdAt), MONTH(e.createdAt)")
    List<Object[]> getMonthlyEventCreationStats(@Param("since") LocalDateTime since);

    /**
     * Get comprehensive event statistics
     */
    @Query("SELECT " +
           "COUNT(*) as total, " +
           "SUM(CASE WHEN e.status = 'ACTIVE' THEN 1 ELSE 0 END) as active, " +
           "SUM(CASE WHEN e.status = 'DRAFT' THEN 1 ELSE 0 END) as draft, " +
           "SUM(CASE WHEN e.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed, " +
           "SUM(CASE WHEN e.isVirtual = true THEN 1 ELSE 0 END) as virtual, " +
           "SUM(CASE WHEN e.isRecurring = true THEN 1 ELSE 0 END) as recurring, " +
           "AVG(e.maxVolunteers) as avgCapacity, " +
           "SUM(e.currentVolunteers) as totalVolunteers " +
           "FROM Event e")
    Object[] getComprehensiveEventStatistics();

    /**
     * Get time of day distribution
     */
    @Query("SELECT e.timeOfDay, COUNT(e) FROM Event e WHERE e.timeOfDay IS NOT NULL " +
           "GROUP BY e.timeOfDay ORDER BY COUNT(e) DESC")
    List<Object[]> getTimeOfDayStatistics();

    /**
     * Get skill level distribution
     */
    @Query("SELECT e.skillLevelRequired, COUNT(e) FROM Event e WHERE e.skillLevelRequired IS NOT NULL " +
           "GROUP BY e.skillLevelRequired ORDER BY COUNT(e) DESC")
    List<Object[]> getSkillLevelDistribution();

    /**
     * Get events by capacity utilization
     */
    @Query("SELECT " +
           "SUM(CASE WHEN e.maxVolunteers IS NULL THEN 1 ELSE 0 END) as unlimited, " +
           "SUM(CASE WHEN e.currentVolunteers >= e.maxVolunteers THEN 1 ELSE 0 END) as full, " +
           "SUM(CASE WHEN e.currentVolunteers >= (e.maxVolunteers * 0.8) AND e.currentVolunteers < e.maxVolunteers THEN 1 ELSE 0 END) as almostFull, " +
           "SUM(CASE WHEN e.currentVolunteers < (e.maxVolunteers * 0.5) THEN 1 ELSE 0 END) as needsVolunteers " +
           "FROM Event e WHERE e.status = :status AND e.maxVolunteers IS NOT NULL")
    Object[] getCapacityUtilizationStats(@Param("status") EventStatus status);

    // =====================================================
    // ADMINISTRATIVE QUERIES
    // =====================================================

    /**
     * Find events needing review (missing required fields)
     */
    @Query("SELECT e FROM Event e WHERE " +
           "e.eventType IS NULL OR " +
           "e.skillLevelRequired IS NULL OR " +
           "e.durationCategory IS NULL OR " +
           "e.timeOfDay IS NULL OR " +
           "(e.isVirtual = false AND (e.city IS NULL OR e.state IS NULL)) OR " +
           "(e.isVirtual = true AND e.virtualMeetingLink IS NULL)")
    List<Event> findEventsNeedingReview();

    /**
     * Find events with inconsistent data
     */
    @Query("SELECT e FROM Event e WHERE " +
           "(e.endDate IS NOT NULL AND e.endDate <= e.startDate) OR " +
           "(e.currentVolunteers > e.maxVolunteers) OR " +
           "(e.isVirtual = true AND e.virtualMeetingLink IS NULL) OR " +
           "(e.isVirtual = false AND (e.city IS NULL AND e.location IS NULL))")
    List<Event> findEventsWithInconsistentData();

    /**
     * Find abandoned events (draft for too long)
     */
    @Query("SELECT e FROM Event e WHERE e.status = 'DRAFT' AND e.createdAt < :cutoffDate")
    List<Event> findAbandonedEvents(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Find overdue events (should have ended but status not updated)
     */
    @Query("SELECT e FROM Event e WHERE e.status IN ('ACTIVE', 'DRAFT') AND " +
           "((e.endDate IS NOT NULL AND e.endDate < :now) OR " +
           "(e.endDate IS NULL AND e.startDate < :dayAgo))")
    List<Event> findOverdueEvents(@Param("now") LocalDateTime now, @Param("dayAgo") LocalDateTime dayAgo);
}