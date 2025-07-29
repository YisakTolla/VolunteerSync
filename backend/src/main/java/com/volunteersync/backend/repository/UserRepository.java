package com.volunteersync.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.volunteersync.backend.entity.user.User;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by email (used for login and registration checks)
    // Returns Optional to handle cases where user might not exist
    Optional<User> findByEmail(String email);
    
    // Check if user exists by email
    boolean existsByEmail(String email);
    
    // Find user by Google ID (for OAuth users)
    Optional<User> findByGoogleId(String googleId);
    
    // Check if user exists by Google ID
    boolean existsByGoogleId(String googleId);
    
    // Additional useful methods
    
    /**
     * Find users by user type
     */
    @Query("SELECT u FROM User u WHERE u.userType = :userType AND u.isActive = true")
    List<User> findByUserTypeAndIsActiveTrue(@Param("userType") com.volunteersync.backend.entity.user.UserType userType);
    
    /**
     * Find all active users
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findAllActiveUsers();
    
    /**
     * Find users by organization name (for organizations)
     */
    @Query("SELECT u FROM User u WHERE u.organizationName LIKE %:organizationName% AND u.userType = 'ORGANIZATION'")
    List<User> findByOrganizationNameContaining(@Param("organizationName") String organizationName);
    
    /**
     * Find users by first and last name (for volunteers)
     */
    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:firstName% AND u.lastName LIKE %:lastName% AND u.userType = 'VOLUNTEER'")
    List<User> findByFirstNameContainingAndLastNameContaining(
        @Param("firstName") String firstName, 
        @Param("lastName") String lastName);
    
    /**
     * Find users by location
     */
    @Query("SELECT u FROM User u WHERE u.location LIKE %:location% AND u.isActive = true")
    List<User> findByLocationContaining(@Param("location") String location);
    
    /**
     * Count users by type
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = :userType AND u.isActive = true")
    long countByUserTypeAndIsActiveTrue(@Param("userType") com.volunteersync.backend.entity.user.UserType userType);
    
    /**
     * Find recently registered users
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= :date AND u.isActive = true ORDER BY u.createdAt DESC")
    List<User> findRecentlyRegisteredUsers(@Param("date") java.time.LocalDateTime date);
    
    /**
     * Find users with verification pending (if you have email verification)
     */
    @Query("SELECT u FROM User u WHERE u.emailVerified = false AND u.isActive = true")
    List<User> findUnverifiedUsers();
}