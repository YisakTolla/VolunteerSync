// backend/src/main/java/com/volunteersync/backend/service/auth/JwtService.java
package com.volunteersync.backend.service;

import com.volunteersync.backend.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.SignatureException;

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
     * @param claimsResolver Claims resolver function
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
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Check if JWT token is expired
     * @param token JWT token
     * @return True if expired
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Validate JWT token
     * @param token JWT token
     * @return True if valid
     */
    public Boolean validateToken(String token) {
        try {
            // Check if token is blacklisted
            if (tokenBlacklist.contains(token)) {
                log.warn("Token is blacklisted: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
                return false;
            }

            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            
            return !isTokenExpired(token);
            
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        
        return false;
    }

    /**
     * Validate JWT token for specific user
     * @param token JWT token
     * @param user User entity
     * @return True if valid for user
     */
    public Boolean validateToken(String token, User user) {
        final String email = getEmailFromToken(token);
        return (email.equals(user.getEmail()) && validateToken(token));
    }

    /**
     * Validate password reset token
     * @param token Password reset token
     * @return True if valid
     */
    public Boolean validatePasswordResetToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            String tokenType = claims.get("type", String.class);
            
            return "password_reset".equals(tokenType) && validateToken(token);
            
        } catch (Exception e) {
            log.error("Invalid password reset token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validate refresh token
     * @param token Refresh token
     * @return True if valid
     */
    public Boolean validateRefreshToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            String tokenType = claims.get("type", String.class);
            
            return "refresh".equals(tokenType) && validateToken(token);
            
        } catch (Exception e) {
            log.error("Invalid refresh token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Add token to blacklist (for logout)
     * @param token JWT token to invalidate
     */
    public void invalidateToken(String token) {
        tokenBlacklist.add(token);
        log.info("Token added to blacklist");
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
     * Get remaining time until token expiration
     * @param token JWT token
     * @return Time in milliseconds
     */
    public Long getTokenRemainingTime(String token) {
        Date expiration = getExpirationDateFromToken(token);
        Date now = new Date();
        
        if (expiration.before(now)) {
            return 0L;
        }
        
        return expiration.getTime() - now.getTime();
    }

    /**
     * Check if token needs refresh (expires within next hour)
     * @param token JWT token
     * @return True if needs refresh
     */
    public Boolean needsRefresh(String token) {
        Long remainingTime = getTokenRemainingTime(token);
        // Refresh if token expires within next hour (3600000ms)
        return remainingTime < 3600000L;
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
     * Clean up expired tokens from blacklist
     * This method should be called periodically to prevent memory leaks
     */
    public void cleanupBlacklist() {
        log.info("Cleaning up token blacklist");
        
        // Remove expired tokens from blacklist
        tokenBlacklist.removeIf(token -> {
            try {
                return isTokenExpired(token);
            } catch (Exception e) {
                // If we can't parse the token, it's probably corrupted, so remove it
                return true;
            }
        });
        
        log.info("Token blacklist cleanup completed. Current size: {}", tokenBlacklist.size());
    }
}