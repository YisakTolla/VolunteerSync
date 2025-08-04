package com.volunteersync.backend.repository;

import com.volunteersync.backend.entity.Event;
import com.volunteersync.backend.entity.OrganizationProfile;
import com.volunteersync.backend.enums.EventStatus;
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
}