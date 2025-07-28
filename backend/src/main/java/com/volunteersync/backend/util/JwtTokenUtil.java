package com.volunteersync.backend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationInMs;

    // Generate secret key from the configured secret
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Generate token with just username
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    // Generate token with custom claims
    public String generateToken(String username, Map<String, Object> claims) {
        return createToken(claims, username);
    }

    // Generate token with UserDetails
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userDetails.getAuthorities());
        return createToken(claims, userDetails.getUsername());
    }

    // Create token with claims and subject
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    // Extract username from token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // Extract expiration date from token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // Extract issued date from token
    public Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    // Extract specific claim from token
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from token
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    // Check if token is expired
    public Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    // Validate token against user details
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    // Validate token (basic validation)
    public Boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Check if token can be refreshed
    public Boolean canTokenBeRefreshed(String token) {
        return !isTokenExpired(token);
    }

    // Refresh token
    public String refreshToken(String token) {
        try {
            final Claims claims = getAllClaimsFromToken(token);
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

            return Jwts.builder()
                    .claims(claims)
                    .issuedAt(now)
                    .expiration(expiryDate)
                    .signWith(getSigningKey(), Jwts.SIG.HS256)
                    .compact();
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot refresh token", e);
        }
    }

    // Get remaining time until expiration in milliseconds
    public Long getRemainingTimeUntilExpiration(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            return 0L;
        }
    }

    // Extract custom claim
    public Object getCustomClaim(String token, String claimName) {
        try {
            final Claims claims = getAllClaimsFromToken(token);
            return claims.get(claimName);
        } catch (Exception e) {
            return null;
        }
    }

    // Check if token is valid format (doesn't check expiration)
    public Boolean isValidTokenFormat(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return false;
            }

            // JWT should have 3 parts separated by dots
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }

            // Try to parse without verification (just format check)
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }

    // Get token expiration time in seconds (for client-side handling)
    public int getExpirationTimeInSeconds() {
        return jwtExpirationInMs / 1000;
    }

    // Generate token with custom expiration
    public String generateTokenWithCustomExpiration(String username, int expirationInMs) {
        Map<String, Object> claims = new HashMap<>();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationInMs);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Extract JWT token from HTTP request
     */
    public String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Extract email from JWT token
     */
    public String extractEmail(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
}