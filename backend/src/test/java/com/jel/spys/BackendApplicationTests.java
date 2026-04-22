package com.jel.spys;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Main application context test.
 * This test verifies that the Spring Boot application context loads
 * successfully
 * with Testcontainers configuration.
 */
@SpringBootTest
class BackendApplicationTests extends BaseIntegrationTest {

	/**
	 * Test that the Spring Boot application context loads successfully
	 * with all beans configured properly including the Testcontainer database.
	 */
	@Test
	void contextLoads() {
		// This test will pass if the application context loads successfully
		// The Testcontainer PostgreSQL database will be automatically started
		// and configured via the BaseIntegrationTest class
	}

}
