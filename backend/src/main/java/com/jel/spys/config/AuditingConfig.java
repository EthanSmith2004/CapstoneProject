package com.jel.spys.config;

import com.jel.spys.service.ClockService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class AuditingConfig {

    @Bean
    public DateTimeProvider auditingDateTimeProvider(ClockService clock) {
        return () -> Optional.of(clock.now());
    }
}
