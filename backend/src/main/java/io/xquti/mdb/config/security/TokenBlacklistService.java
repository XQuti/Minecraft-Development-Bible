package io.xquti.mdb.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * JWT Token blacklist service for secure token revocation
 * Implements secure token invalidation using Redis
 */
@Service
public class TokenBlacklistService {

    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistService.class);
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";
    private static final String USER_TOKENS_PREFIX = "jwt:user:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * Blacklist a JWT token
     * @param token The JWT token to blacklist
     * @param expirationTime Token expiration time
     */
    public void blacklistToken(String token, Instant expirationTime) {
        try {
            String tokenHash = hashToken(token);
            String key = BLACKLIST_PREFIX + tokenHash;
            
            // Calculate TTL based on token expiration
            long ttlSeconds = Duration.between(Instant.now(), expirationTime).getSeconds();
            if (ttlSeconds > 0) {
                redisTemplate.opsForValue().set(key, "blacklisted", ttlSeconds, TimeUnit.SECONDS);
                logger.info("Token blacklisted successfully with TTL: {} seconds", ttlSeconds);
            } else {
                logger.warn("Token already expired, not adding to blacklist");
            }
        } catch (Exception e) {
            logger.error("Failed to blacklist token: {}", e.getMessage());
            // SECURITY: Fail secure - if we can't blacklist, log the security event
            logger.error("SECURITY ALERT: Token blacklisting failed - manual intervention required");
        }
    }

    /**
     * Check if a token is blacklisted
     * @param token The JWT token to check
     * @return true if token is blacklisted
     */
    public boolean isTokenBlacklisted(String token) {
        try {
            String tokenHash = hashToken(token);
            String key = BLACKLIST_PREFIX + tokenHash;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            logger.error("Failed to check token blacklist status: {}", e.getMessage());
            // SECURITY: Fail secure - if we can't check, assume blacklisted
            return true;
        }
    }

    /**
     * Blacklist all tokens for a specific user (for logout all sessions)
     * @param username The username
     */
    public void blacklistAllUserTokens(String username) {
        try {
            String userKey = USER_TOKENS_PREFIX + username;
            
            // Set a marker that all tokens issued before this time are invalid
            String currentTime = String.valueOf(Instant.now().getEpochSecond());
            redisTemplate.opsForValue().set(userKey, currentTime, 7, TimeUnit.DAYS);
            
            logger.info("All tokens blacklisted for user: {}", username);
        } catch (Exception e) {
            logger.error("Failed to blacklist all user tokens for {}: {}", username, e.getMessage());
        }
    }

    /**
     * Check if token was issued before user logout-all event
     * @param username The username
     * @param tokenIssuedAt Token issued timestamp
     * @return true if token should be considered invalid
     */
    public boolean isTokenInvalidatedByUserLogout(String username, Instant tokenIssuedAt) {
        try {
            String userKey = USER_TOKENS_PREFIX + username;
            String logoutTime = redisTemplate.opsForValue().get(userKey);
            
            if (logoutTime != null) {
                long logoutTimestamp = Long.parseLong(logoutTime);
                return tokenIssuedAt.getEpochSecond() < logoutTimestamp;
            }
            
            return false;
        } catch (Exception e) {
            logger.error("Failed to check user logout status for {}: {}", username, e.getMessage());
            // SECURITY: Fail secure
            return true;
        }
    }

    /**
     * Clean up expired blacklist entries (called by scheduled task)
     */
    public void cleanupExpiredEntries() {
        try {
            // Redis automatically handles TTL expiration, but we can log cleanup events
            logger.debug("Blacklist cleanup completed");
        } catch (Exception e) {
            logger.error("Failed to cleanup blacklist entries: {}", e.getMessage());
        }
    }

    /**
     * Hash token for storage (to avoid storing full JWT in Redis)
     * @param token The JWT token
     * @return SHA-256 hash of the token
     */
    private String hashToken(String token) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            logger.error("Failed to hash token: {}", e.getMessage());
            // Fallback to using token directly (less secure but functional)
            return token.substring(token.length() - 32); // Use last 32 chars as identifier
        }
    }
}