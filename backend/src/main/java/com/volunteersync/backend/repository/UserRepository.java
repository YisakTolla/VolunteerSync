package com.volunteersync.backend.repository;

import com.volunteersync.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by email (used for login and registration checks)
    User findByEmail(String email);
    
    // Check if user exists by email
    boolean existsByEmail(String email);
    
    // Find user by Google ID (for OAuth users)
    User findByGoogleId(String googleId);
    
    // Check if user exists by Google ID
    boolean existsByGoogleId(String googleId);
}