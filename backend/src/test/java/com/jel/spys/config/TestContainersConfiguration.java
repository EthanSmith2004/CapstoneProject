package com.jel.spys.config;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import jakarta.annotation.PostConstruct;
import java.security.Security;

/**
 * Test configuration for Testcontainers.
 * This configuration provides a PostgreSQL container for integration tests
 * and registers the Bouncy Castle security provider for cryptographic
 * operations.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfiguration {

    /**
     * Registers the Bouncy Castle security provider during test initialization.
     * This is needed for web push notifications and other cryptographic operations.
     */
    @PostConstruct
    public void registerBouncyCastleProvider() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * Creates a PostgreSQL container for testing.
     * The @ServiceConnection annotation automatically configures Spring Boot
     * to use this container for database connections in tests.
     */
    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:17-alpine"))
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true); // Reuse container across test runs for better performance
    }
}
