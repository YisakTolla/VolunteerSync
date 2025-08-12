package com.volunteersync.backend.config;

import com.volunteersync.backend.entity.User;
import com.volunteersync.backend.repository.UserRepository;
import com.volunteersync.backend.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

/**
 * JWT Authentication Filter - validates JWT tokens and sets authentication context
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, 
            HttpServletResponse response, 
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // Extract JWT token from Authorization header
            String authHeader = request.getHeader("Authorization");
            String token = null;
            String email = null;

            log.debug("Processing request: {} {}", request.getMethod(), request.getRequestURI());

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                log.debug("Found JWT token: {}...", token.substring(0, Math.min(20, token.length())));
                
                try {
                    email = jwtService.getEmailFromToken(token);
                    log.debug("Extracted email from token: {}", email);
                } catch (Exception e) {
                    log.warn("Failed to extract email from token: {}", e.getMessage());
                }
            } else {
                log.debug("No Authorization header found or invalid format");
            }

            // If token exists and no authentication is set yet
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.debug("Validating token for email: {}", email);

                // Find user by email
                Optional<User> userOpt = userRepository.findByEmail(email.toLowerCase().trim());
                
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    log.debug("Found user: {} (ID: {}, Type: {}, Active: {})", 
                            user.getEmail(), user.getId(), user.getUserType(), user.getIsActive());

                    // Validate token
                    if (jwtService.validateToken(token, user)) {
                        log.debug("Token is valid for user: {}", email);

                        // 🔧 FIXED: Create authority string from user type
                        String authority = "ROLE_" + user.getUserType().toString();

                        // Create authentication token with user as principal
                        UsernamePasswordAuthenticationToken authToken = 
                            new UsernamePasswordAuthenticationToken(
                                user,  // Set User entity as principal
                                null, 
                                Collections.singletonList(new SimpleGrantedAuthority(authority))
                            );

                        // Set authentication details
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        // Set authentication in security context
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        
                        log.debug("Authentication set for user: {} with authority: {}", email, authority);
                    } else {
                        log.warn("Token validation failed for user: {}", email);
                    }
                } else {
                    log.warn("User not found for email: {}", email);
                }
            }

        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage(), e);
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        // Skip JWT validation for public endpoints
        return path.startsWith("/api/auth/") ||
               path.startsWith("/api/events/public/") ||
               path.startsWith("/api/organizations/public/") ||
               path.startsWith("/api/search/") ||
               path.startsWith("/h2-console/") ||
               path.startsWith("/actuator/") ||
               path.equals("/") ||
               request.getMethod().equals("OPTIONS");
    }
}