package com.jel.spys.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RefreshTokenCleanupService {
    
    @Autowired
    private RefreshTokenService refreshTokenService;
    
    // Run cleanup every hour
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    public void cleanupExpiredTokens() {
        log.info("Starting cleanup of expired refresh tokens");
        try {
            refreshTokenService.cleanupExpiredTokens();
            log.info("Completed cleanup of expired refresh tokens");
        } catch (Exception e) {
            log.error("Error during refresh token cleanup", e);
        }
    }
}
