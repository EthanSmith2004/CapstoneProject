package com.jel.spys.controllers;

import com.jel.spys.BaseIntegrationTest;
import com.jel.spys.model.AuthResponse;
import com.jel.spys.util.AuthTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Example test class showing how to use AuthTestUtils for cleaner authentication in tests.
 * This demonstrates various patterns for login and authentication testing.
 */
@DisplayName("Auth Utils Example Tests")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthUtilsExampleTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        // Any test setup if needed
    }

    @Test
    @DisplayName("Example: Simple admin login using utility")
    void shouldLoginAsAdminUsingUtility() {
        // Given & When - Login as admin using utility
        AuthResponse auth = AuthTestUtils.loginAsAdmin(restTemplate, port);

        // Then
        assertThat(auth).isNotNull();
        assertThat(auth.getAccessToken()).isNotNull().isNotEmpty();
        assertThat(auth.getEmail()).isEqualTo(AuthTestUtils.DEFAULT_ADMIN_EMAIL);
        assertThat(auth.getRoles()).contains("ROLE_ADMIN");

        System.out.println("Admin login successful with utility: " + auth.getEmail());
    }

    @Test
    @DisplayName("Example: Register and authenticate new user using utility")
    void shouldRegisterAndLoginNewUserUsingUtility() {
        // Given & When - Register new user
        AuthResponse auth = AuthTestUtils.registerUser(restTemplate, port, "Jane", "Doe", "password123");

        // Then
        assertThat(auth).isNotNull();
        assertThat(auth.getAccessToken()).isNotNull().isNotEmpty();
        assertThat(auth.getFirstName()).isEqualTo("Jane");
        assertThat(auth.getLastName()).isEqualTo("Doe");
        assertThat(auth.getRoles()).contains("ROLE_USER");

        System.out.println("User registered and authenticated: " + auth.getEmail());
    }

    @Test
    @DisplayName("Example: Using AuthContext for authenticated requests")
    void shouldUseAuthContextForAuthenticatedRequests() {
        // Given - Create authenticated contexts
        AuthTestUtils.AuthContext adminContext = AuthTestUtils.createAdminContext(restTemplate, port);
        AuthTestUtils.AuthContext userContext = AuthTestUtils.createUserContext(restTemplate, port);

        // Then - Verify contexts are created properly
        assertThat(adminContext.getAccessToken()).isNotNull();
        assertThat(adminContext.getEmail()).isEqualTo(AuthTestUtils.DEFAULT_ADMIN_EMAIL);
        
        assertThat(userContext.getAccessToken()).isNotNull();
        assertThat(userContext.getEmail()).isNotNull();

        // Example: Create authenticated headers
        HttpHeaders adminHeaders = adminContext.createHeaders();
        assertThat(adminHeaders.getFirst(HttpHeaders.AUTHORIZATION)).startsWith("Bearer ");

        // Example: Create authenticated request entity
        String requestBody = "{\"test\": \"data\"}";
        HttpEntity<String> authenticatedRequest = userContext.createRequest(requestBody);
        assertThat(authenticatedRequest.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)).startsWith("Bearer ");

        System.out.println("Admin context: " + adminContext.getEmail());
        System.out.println("User context: " + userContext.getEmail());
    }

    @Test
    @DisplayName("Example: Creating multiple users with different roles")
    void shouldCreateMultipleUsersWithUtility() {
        // Given & When - Create multiple users
        AuthTestUtils.AuthContext user1 = AuthTestUtils.createUserContext(restTemplate, port, "John", "Smith", "password123");
        AuthTestUtils.AuthContext user2 = AuthTestUtils.createUserContext(restTemplate, port, "Alice", "Johnson", "password456");
        AuthTestUtils.AuthContext admin = AuthTestUtils.createAdminContext(restTemplate, port);

        // Then - Verify all contexts
        assertThat(user1.getAuthResponse().getRoles()).contains("ROLE_USER");
        assertThat(user2.getAuthResponse().getRoles()).contains("ROLE_USER");
        assertThat(admin.getAuthResponse().getRoles()).contains("ROLE_ADMIN");

        // Verify unique emails
        assertThat(user1.getEmail()).isNotEqualTo(user2.getEmail());
        assertThat(user1.getEmail()).isNotEqualTo(admin.getEmail());

        System.out.println("Created users:");
        System.out.println("- User1: " + user1.getEmail());
        System.out.println("- User2: " + user2.getEmail());
        System.out.println("- Admin: " + admin.getEmail());
    }

    @Test
    @DisplayName("Example: Login with custom credentials")
    void shouldLoginWithCustomCredentials() {
        // Given - First register a user with specific credentials
        String email = AuthTestUtils.generateUniqueEmail("custom");
        String password = "customPassword123";
        
        // Register the user first
        AuthTestUtils.registerUser(restTemplate, port, "Custom", "User", email, password);

        // When - Login with the custom credentials
        AuthResponse loginResponse = AuthTestUtils.login(restTemplate, port, email, password);

        // Then
        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getEmail()).isEqualTo(email);
        assertThat(loginResponse.getFirstName()).isEqualTo("Custom");
        assertThat(loginResponse.getLastName()).isEqualTo("User");
        assertThat(loginResponse.getRoles()).contains("ROLE_USER");

        System.out.println("Custom user login successful: " + loginResponse.getEmail());
    }

    @Test
    @DisplayName("Example: Generate unique emails with prefix")
    void shouldGenerateUniqueEmails() {
        // When - Generate unique emails
        String email1 = AuthTestUtils.generateUniqueEmail("manager");
        String email2 = AuthTestUtils.generateUniqueEmail("employee");
        String email3 = AuthTestUtils.generateUniqueEmail();

        // Then
        assertThat(email1).startsWith("manager");
        assertThat(email1).endsWith("@test.com");
        
        assertThat(email2).startsWith("employee");
        assertThat(email2).endsWith("@test.com");
        
        assertThat(email3).startsWith("testuser");
        assertThat(email3).endsWith("@test.com");

        // Verify they are unique
        assertThat(email1).isNotEqualTo(email2);
        assertThat(email1).isNotEqualTo(email3);
        assertThat(email2).isNotEqualTo(email3);

        System.out.println("Generated unique emails:");
        System.out.println("- " + email1);
        System.out.println("- " + email2);
        System.out.println("- " + email3);
    }
}
