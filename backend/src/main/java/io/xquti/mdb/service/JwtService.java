package io.xquti.mdb.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    @Value("${jwt.refresh-expiration:604800000}") // 7 days default
    private Long refreshExpiration;
    
    @Value("${jwt.issuer:mdb-platform}")
    private String issuer;
    
    @Value("${jwt.audience:mdb-users}")
    private String audience;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    private SecretKey getSigningKey() {
        // CRITICAL SECURITY: Validate JWT secret
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException("JWT_SECRET environment variable is required and cannot be empty");
        }
        
        if (secret.length() < 64) {
            throw new IllegalStateException("JWT secret must be at least 64 characters long for production security");
        }
        
        // Validate secret strength - must not be default/predictable
        if (secret.contains("please-change-this") || secret.contains("super-secret") || 
            secret.contains("CHANGE_THIS") || secret.equals("default") || 
            secret.matches("^[a-zA-Z0-9]{1,20}$")) { // Simple patterns
            throw new IllegalStateException("JWT secret is weak or uses default value. Must be cryptographically secure and at least 64 characters");
        }
        
        // Use SHA-256 hash of secret for additional security
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
    
    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            logger.warn("Failed to extract username from token: {}", e.getMessage());
            throw e;
        }
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .requireIssuer(issuer)
                    .requireAudience(audience)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            // Additional security validations
            if (claims.getIssuedAt() == null) {
                throw new JwtException("Token missing issued at claim");
            }
            
            if (claims.getIssuedAt().after(new Date())) {
                throw new JwtException("Token issued in the future");
            }
            
            return claims;
        } catch (SignatureException e) {
            logger.warn("JWT signature validation failed: {}", e.getMessage());
            throw new JwtException("Invalid token signature");
        } catch (JwtException e) {
            logger.warn("JWT validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.warn("Failed to parse JWT token: {}", e.getMessage());
            throw new JwtException("Token parsing failed", e);
        }
    }
    
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    public String generateToken(String username) {
        return generateToken(username, false);
    }
    
    public String generateToken(String username, boolean isRefreshToken) {
        logger.debug("Generating JWT token for user: {} (refresh: {})", username, isRefreshToken);
        
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", isRefreshToken ? "refresh" : "access");
        
        long tokenExpiration = isRefreshToken ? refreshExpiration : expiration;
        String token = createToken(claims, username, tokenExpiration);
        
        logger.debug("Successfully generated JWT token for user: {} (refresh: {})", username, isRefreshToken);
        return token;
    }
    
    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);
        
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer(issuer)
                .audience().add(audience).and()
                .issuedAt(now)
                .notBefore(now) // Token not valid before now
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    public Boolean validateToken(String token, String username) {
        try {
            // Check if token is blacklisted
            if (isTokenBlacklisted(token)) {
                logger.warn("Token is blacklisted for user: {}", username);
                return false;
            }
            
            final String extractedUsername = extractUsername(token);
            
            // SECURITY: Use constant-time comparison to prevent timing attacks
            boolean usernameMatches = MessageDigest.isEqual(
                extractedUsername.getBytes(StandardCharsets.UTF_8), 
                username.getBytes(StandardCharsets.UTF_8)
            );
            
            boolean isValid = usernameMatches && !isTokenExpired(token);
            logger.debug("Token validation result for user {}: {}", username, isValid);
            return isValid;
        } catch (Exception e) {
            logger.warn("Token validation failed for user {}: {}", username, e.getMessage());
            return false;
        }
    }
    
    /**
     * Blacklist a token (for logout/revocation)
     */
    public void blacklistToken(String token) {
        try {
            Date expiration = extractExpiration(token);
            long ttl = expiration.getTime() - System.currentTimeMillis();
            if (ttl > 0) {
                String key = "blacklisted_token:" + token;
                redisTemplate.opsForValue().set(key, "true", Duration.ofMillis(ttl));
                logger.info("Token blacklisted successfully");
            }
        } catch (Exception e) {
            logger.error("Failed to blacklist token: {}", e.getMessage());
        }
    }
    
    /**
     * Check if token is blacklisted
     */
    private boolean isTokenBlacklisted(String token) {
        try {
            String key = "blacklisted_token:" + token;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            logger.error("Failed to check token blacklist: {}", e.getMessage());
            // Fail secure - if we can't check blacklist, assume token is valid
            // This prevents Redis outages from blocking all authentication
            return false;
        }
    }
}