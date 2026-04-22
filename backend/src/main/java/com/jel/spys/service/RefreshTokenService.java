package com.jel.spys.service;

import com.jel.spys.entity.RefreshTokenEntity;
import com.jel.spys.entity.UserEntity;
import com.jel.spys.entity.UserEventType;
import com.jel.spys.exception.InvalidTokenException;
import com.jel.spys.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class RefreshTokenService {
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserEventService userEventService;

    @Value("${app.refresh-token.expiration-ms:604800000}")
    private int refreshTokenDurationMs;
    
    @Value("${app.refresh-token.max-per-user:5}")
    private int maxRefreshTokensPerUser;

    private final SecureRandom secureRandom = new SecureRandom();
    @Autowired
    private ClockService clockService;

    public RefreshTokenEntity createRefreshToken(UserEntity user) {
        cleanupExpiredTokens();
        
        long activeTokenCount = refreshTokenRepository.countByUserAndRevokedFalse(user);
        if (activeTokenCount >= maxRefreshTokensPerUser) {
            // Revoke the oldest token to make room
            refreshTokenRepository.findByUserAndRevokedFalse(user)
                    .stream()
                    .findFirst()
                    .ifPresent(RefreshTokenEntity::revoke);
        }
        
        String tokenValue = generateSecureToken();
        
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .token(tokenValue)
                .user(user)
                .expiresAt(clockService.now().plusMillis(refreshTokenDurationMs))
                .build();
        
        return refreshTokenRepository.save(refreshToken);
    }
    
    public Optional<RefreshTokenEntity> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
    
    public void verifyExpiration(RefreshTokenEntity token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new InvalidTokenException("Refresh token has expired. Please make a new login request");
        }
        
        if (token.getRevoked()) {
            throw new InvalidTokenException("Refresh token has been revoked. Please make a new login request");
        }

    }

    public RefreshTokenEntity updateToken(RefreshTokenEntity token) {
        // This wil run the preUpdate hook to update the dates
        userEventService.logEvent(token.getUser(), UserEventType.TOKEN_REFRESH);
        return refreshTokenRepository.save(token);
    }
    
    public void revokeToken(String tokenValue) {
        refreshTokenRepository.findByToken(tokenValue)
                .ifPresent(token -> {
                    token.revoke();
                    userEventService.logEvent(token.getUser(), UserEventType.LOGOUT_EXPLICIT);
                    refreshTokenRepository.save(token);
                });
    }
    
    public void revokeAllUserTokens(UserEntity user) {
        userEventService.logEvent(user, UserEventType.LOGOUT_EXPLICIT);
        refreshTokenRepository.revokeAllUserTokens(user, clockService.now());
    }
    
    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredAndRevokedTokens(clockService.now());
    }
    
    private String generateSecureToken() {
        byte[] tokenBytes = new byte[32]; // 256-bit token
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
}
