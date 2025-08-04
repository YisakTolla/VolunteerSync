package com.volunteersync.backend.repository;

import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // =====================================================
    // BASIC USER QUERIES
    // =====================================================

    /**
     * Find user by email (for login)
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if email already exists (for registration)
     */
    boolean existsByEmail(String email);

    /**
     * Find users by type
     */
    List<User> findByUserType(UserType userType);

    /**
     * Find active users only
     */
    List<User> findByIsActiveTrue();

    /**
     * Find verified users only
     */
    List<User> findByEmailVerifiedTrue();

    /**
     * Find users by type and active status
     */
    List<User> findByUserTypeAndIsActiveTrue(UserType userType);

    // =====================================================
    // SEARCH AND FILTERING
    // =====================================================

    /**
     * Find users by email pattern (for admin search)
     */
    List<User> findByEmailContainingIgnoreCase(String emailPattern);

    /**
     * Find recently registered users
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= :since ORDER BY u.createdAt DESC")
    List<User> findRecentlyRegistered(@Param("since") LocalDateTime since);

    /**
     * Find users who need email verification
     */
    List<User> findByEmailVerifiedFalseAndIsActiveTrue();

    // =====================================================
    // STATISTICS QUERIES
    // =====================================================

    /**
     * Count users by type
     */
    long countByUserType(UserType userType);

    /**
     * Count active users
     */
    long countByIsActiveTrue();

    /**
     * Count verified users
     */
    long countByEmailVerifiedTrue();

    /**
     * Get registration statistics by date range
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    long countRegistrationsBetween(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Get user growth statistics
     */
    @Query("SELECT DATE(u.createdAt) as date, COUNT(u) as count FROM User u " +
            "WHERE u.createdAt >= :since GROUP BY DATE(u.createdAt) ORDER BY date")
    List<Object[]> getUserGrowthStats(@Param("since") LocalDateTime since);
}