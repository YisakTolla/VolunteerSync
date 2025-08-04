package com.volunteersync.backend.repository;

import com.volunteersync.backend.entity.Application;
import com.volunteersync.backend.entity.Event;
import com.volunteersync.backend.entity.VolunteerProfile;
import com.volunteersync.backend.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // =====================================================
    // VOLUNTEER APPLICATION QUERIES
    // =====================================================

    /**
     * Find all applications by volunteer
     */
    List<Application> findByVolunteer(VolunteerProfile volunteer);

    /**
     * Find volunteer applications ordered by date
     */
    List<Application> findByVolunteerOrderByAppliedAtDesc(VolunteerProfile volunteer);

    /**
     * Find volunteer applications by status
     */
    List<Application> findByVolunteerAndStatus(VolunteerProfile volunteer, ApplicationStatus status);

    /**
     * Check if volunteer already applied to event
     */
    Optional<Application> findByVolunteerAndEvent(VolunteerProfile volunteer, Event event);

    boolean existsByVolunteerAndEvent(VolunteerProfile volunteer, Event event);

    // =====================================================
    // EVENT APPLICATION QUERIES
    // =====================================================

    /**
     * Find all applications for an event
     */
    List<Application> findByEvent(Event event);

    /**
     * Find event applications ordered by date
     */
    List<Application> findByEventOrderByAppliedAtAsc(Event event);

    /**
     * Find applications by event and status
     */
    List<Application> findByEventAndStatus(Event event, ApplicationStatus status);

    /**
     * Get accepted applications for an event
     */
    @Query("SELECT a FROM Application a WHERE a.event.id = :eventId AND a.status = 'ACCEPTED'")
    List<Application> findAcceptedApplicationsByEvent(@Param("eventId") Long eventId);

    /**
     * Get pending applications for an event
     */
    @Query("SELECT a FROM Application a WHERE a.event.id = :eventId AND a.status = 'PENDING' ORDER BY a.appliedAt ASC")
    List<Application> findPendingApplicationsByEvent(@Param("eventId") Long eventId);

    // =====================================================
    // ORGANIZATION MANAGEMENT QUERIES
    // =====================================================

    /**
     * Get all pending applications for an organization
     */
    @Query("SELECT a FROM Application a WHERE a.event.organization.id = :orgId AND a.status = 'PENDING' " +
            "ORDER BY a.appliedAt ASC")
    List<Application> findPendingApplicationsByOrganization(@Param("orgId") Long organizationId);

    /**
     * Get all applications for an organization's events
     */
    @Query("SELECT a FROM Application a WHERE a.event.organization.id = :orgId ORDER BY a.appliedAt DESC")
    List<Application> findApplicationsByOrganization(@Param("orgId") Long organizationId);

    /**
     * Get applications by organization and status
     */
    @Query("SELECT a FROM Application a WHERE a.event.organization.id = :orgId AND a.status = :status " +
            "ORDER BY a.appliedAt ASC")
    List<Application> findApplicationsByOrganizationAndStatus(@Param("orgId") Long organizationId,
            @Param("status") ApplicationStatus status);

    // =====================================================
    // STATUS-BASED QUERIES
    // =====================================================

    /**
     * Find applications by status
     */
    List<Application> findByStatus(ApplicationStatus status);

    /**
     * Find applications by status ordered by date
     */
    List<Application> findByStatusOrderByAppliedAtDesc(ApplicationStatus status);

    /**
     * Find recent applications needing review
     */
    @Query("SELECT a FROM Application a WHERE a.status = 'PENDING' AND a.appliedAt >= :since " +
            "ORDER BY a.appliedAt ASC")
    List<Application> findRecentPendingApplications(@Param("since") LocalDateTime since);

    // =====================================================
    // VOLUNTEER HOURS AND STATISTICS
    // =====================================================

    /**
     * Get total hours completed by volunteer
     */
    @Query("SELECT SUM(a.hoursCompleted) FROM Application a WHERE a.volunteer.id = :volunteerId AND a.status = 'ATTENDED'")
    Integer getTotalHoursForVolunteer(@Param("volunteerId") Long volunteerId);

    /**
     * Count completed applications for volunteer
     */
    @Query("SELECT COUNT(a) FROM Application a WHERE a.volunteer.id = :volunteerId AND a.status = 'ATTENDED'")
    Long countCompletedApplicationsForVolunteer(@Param("volunteerId") Long volunteerId);

    /**
     * Get volunteer attendance rate
     */
    @Query("SELECT " +
            "SUM(CASE WHEN a.status = 'ATTENDED' THEN 1 ELSE 0 END) as attended, " +
            "SUM(CASE WHEN a.status = 'NO_SHOW' THEN 1 ELSE 0 END) as noShow, " +
            "COUNT(a) as total " +
            "FROM Application a WHERE a.volunteer.id = :volunteerId AND a.status IN ('ATTENDED', 'NO_SHOW')")
    Object[] getVolunteerAttendanceStats(@Param("volunteerId") Long volunteerId);

    // =====================================================
    // EVENT STATISTICS
    // =====================================================

    /**
     * Count applications by event and status
     */
    long countByEventAndStatus(Event event, ApplicationStatus status);

    /**
     * Get event application statistics
     */
    @Query("SELECT a.status, COUNT(a) FROM Application a WHERE a.event.id = :eventId GROUP BY a.status")
    List<Object[]> getEventApplicationStats(@Param("eventId") Long eventId);

    // =====================================================
    // PLATFORM STATISTICS
    // =====================================================

    /**
     * Count applications by status
     */
    long countByStatus(ApplicationStatus status);

    /**
     * Get platform-wide application statistics
     */
    @Query("SELECT COUNT(a), AVG(a.hoursCompleted) FROM Application a WHERE a.status = 'ATTENDED'")
    Object[] getPlatformApplicationStats();

    /**
     * Get monthly application statistics
     */
    @Query("SELECT YEAR(a.appliedAt), MONTH(a.appliedAt), COUNT(a) FROM Application a " +
            "WHERE a.appliedAt >= :since GROUP BY YEAR(a.appliedAt), MONTH(a.appliedAt) " +
            "ORDER BY YEAR(a.appliedAt), MONTH(a.appliedAt)")
    List<Object[]> getMonthlyApplicationStats(@Param("since") LocalDateTime since);

    /**
     * Find applications in date range
     */
    @Query("SELECT a FROM Application a WHERE a.appliedAt BETWEEN :startDate AND :endDate ORDER BY a.appliedAt DESC")
    List<Application> findApplicationsBetweenDates(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}