package com.jel.spys;

import com.jel.spys.config.TestContainersConfiguration;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for integration tests that require a database.
 * This class sets up Testcontainers with PostgreSQL and provides
 * common configuration for integration tests.
 * 
 * Features:
 * - PostgreSQL Testcontainer automatically started and configured
 * - Test profile activated
 * - Transactional rollback after each test
 * - Full Spring Boot context loaded
 * 
 * Usage: Extend this class in your integration test classes.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIntegrationTest {

    // This class serves as a base for integration tests
    // Extend this class in your test classes that need database access

}
