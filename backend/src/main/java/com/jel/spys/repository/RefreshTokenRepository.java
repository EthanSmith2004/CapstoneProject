package com.jel.spys.repository;

import com.jel.spys.entity.RefreshTokenEntity;
import com.jel.spys.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    
    Optional<RefreshTokenEntity> findByToken(String token);
    
    List<RefreshTokenEntity> findByUserAndRevokedFalse(UserEntity user);
    
    @Modifying
    @Query("UPDATE RefreshTokenEntity rt SET rt.revoked = true, rt.revokedAt = :revokedAt WHERE rt.user = :user AND rt.revoked = false")
    void revokeAllUserTokens(@Param("user") UserEntity user, @Param("revokedAt") Instant revokedAt);
    
    @Modifying
    @Query("DELETE FROM RefreshTokenEntity rt WHERE rt.expiresAt < :now OR rt.revoked = true")
    void deleteExpiredAndRevokedTokens(@Param("now") Instant now);
    
    long countByUserAndRevokedFalse(UserEntity user);
}
