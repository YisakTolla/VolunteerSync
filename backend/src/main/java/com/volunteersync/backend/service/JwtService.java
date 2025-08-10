package com.volunteersync.backend.service;

import com.volunteersync.backend.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;

/**
 * JWT service for token generation, validation, and management
 */
@Slf4j
@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration}")
    private long jwtRefreshExpirationMs;

    // Token blacklist for logout functionality
    private final Set<String> tokenBlacklist = ConcurrentHashMap.newKeySet();

    /**
     * Generate JWT token for user
     * @param user User entity
     * @return JWT token
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("userType", user.getUserType().toString()); 
        return createToken(claims, user.getEmail(), jwtExpirationMs);
    }

    /**
     * Generate refresh token for user
     * @param user User entity
     * @return Refresh token
     */
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("type", "refresh");
        
        return createToken(claims, user.getEmail(), jwtRefreshExpirationMs);
    }

    /**
     * Generate password reset token
     * @param user User entity
     * @return Password reset token
     */
    public String generatePasswordResetToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("type", "password_reset");
        
        // Password reset tokens expire in 1 hour
        return createToken(claims, user.getEmail(), 3600000L); // 1 hour in milliseconds
    }

    /**
     * Create JWT token with claims and expiration
     * @param claims Token claims
     * @param subject Token subject
     * @param expiration Expiration time in milliseconds
     * @return JWT token
     */
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extract email from JWT token
     * @param token JWT token
     * @return Email
     */
    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extract user ID from JWT token
     * @param token JWT token
     * @return User ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return Long.valueOf(claims.get("id").toString());
    }

    /**
     * Extract user type from JWT token
     * @param token JWT token
     * @return User type
     */
    public String getUserTypeFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("userType").toString();
    }

    /**
     * Extract expiration date from JWT token
     * @param token JWT token
     * @return Expiration date
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Extract specific claim from JWT token
     * @param token JWT token
     * @param claimsResolver Function to extract claim
     * @return Claim value
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from JWT token
     * @param token JWT token
     * @return Claims
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            throw new RuntimeException("JWT token is expired");
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
            throw new RuntimeException("JWT token is unsupported");
        } catch (MalformedJwtException e) {
            log.error("JWT token is malformed: {}", e.getMessage());
            throw new RuntimeException("JWT token is malformed");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            throw new RuntimeException("JWT claims string is empty");
        } catch (Exception e) {
            log.error("JWT token validation error: {}", e.getMessage());
            throw new RuntimeException("JWT token validation failed");
        }
    }

    /**
     * Check if JWT token is expired
     * @param token JWT token
     * @return True if expired
     */
    public Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true; // Consider invalid tokens as expired
        }
    }

    /**
     * Validate JWT token against user details
     * @param token JWT token
     * @param user User entity
     * @return True if valid
     */
    public Boolean validateToken(String token, User user) {
        try {
            // Check if token is blacklisted
            if (tokenBlacklist.contains(token)) {
                log.warn("Token is blacklisted: {}", token.substring(0, 20) + "...");
                return false;
            }

            final String email = getEmailFromToken(token);
            final Long userId = getUserIdFromToken(token);
            
            return (email.equals(user.getEmail()) && 
                    userId.equals(user.getId()) && 
                    !isTokenExpired(token) &&
                    user.getIsActive());
                    
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validate JWT token without user context
     * @param token JWT token
     * @return True if valid
     */
    public Boolean validateToken(String token) {
        try {
            // Check if token is blacklisted
            if (tokenBlacklist.contains(token)) {
                return false;
            }

            // Parse token to validate structure and signature
            getAllClaimsFromToken(token);
            
            // Check if not expired
            return !isTokenExpired(token);
            
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Blacklist token (for logout functionality)
     * @param token JWT token to blacklist
     */
    public void blacklistToken(String token) {
        tokenBlacklist.add(token);
        log.info("Token blacklisted: {}", token.substring(0, 20) + "...");
    }

    /**
     * Check if token is blacklisted
     * @param token JWT token
     * @return True if blacklisted
     */
    public Boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }

    /**
     * Get signing key for JWT
     * @return Signing key
     */
    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extract token from Authorization header
     * @param authHeader Authorization header value
     * @return JWT token or null
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * Get remaining time until token expires
     * @param token JWT token
     * @return Remaining time in milliseconds
     */
    public Long getTokenRemainingTime(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.getTime() - new Date().getTime();
        } catch (Exception e) {
            return 0L;
        }
    }
}