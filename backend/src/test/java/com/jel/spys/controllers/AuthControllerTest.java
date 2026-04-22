package com.jel.spys.controllers;

import com.jel.spys.BaseIntegrationTest;
import com.jel.spys.model.AuthResponse;
import com.jel.spys.model.LoginRequest;
import com.jel.spys.model.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the AuthController.
 * Tests authentication, registration, and logout functionality with
 * Testcontainers PostgreSQL database.
 */
@DisplayName("AuthController Integration Tests")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthControllerTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/auth";
    }

    @Test
    @DisplayName("Should authenticate admin superuser successfully")
    void shouldAuthenticateAdminSuperuser() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@demo.com");
        loginRequest.setPassword("admin123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        // When
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                baseUrl + "/login",
                request,
                AuthResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        AuthResponse authResponse = response.getBody();
        assertThat(authResponse.getAccessToken()).isNotNull().isNotEmpty();
        assertThat(authResponse.getRefreshToken()).isNotNull().isNotEmpty();
        assertThat(authResponse.getTokenType()).isEqualTo("Bearer");
        assertThat(authResponse.getExpiresIn()).isPositive();
        assertThat(authResponse.getEmail()).isEqualTo("admin@demo.com");
        assertThat(authResponse.getRoles()).isNotNull();
        assertThat(authResponse.getRoles()).contains("ROLE_ADMIN");

        // Verify the response contains expected admin roles
        System.out.println("Admin user roles: " + authResponse.getRoles());
        System.out.println("Admin login successful with access token: " +
                authResponse.getAccessToken().substring(0, Math.min(20, authResponse.getAccessToken().length()))
                + "...");
    }

    @Test
    @DisplayName("Should fail authentication with invalid credentials")
    void shouldFailAuthenticationWithInvalidCredentials() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@demo.com");
        loginRequest.setPassword("wrongpassword");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/login",
                request,
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        System.out.println("Invalid login correctly rejected: " + response.getStatusCode());
    }

    @Test
    @DisplayName("Should register a new user successfully")
    void shouldRegisterNewUserSuccessfully() {
        // Given
        String uniqueEmail = "newuser" + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail(uniqueEmail);
        registerRequest.setPassword("password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> request = new HttpEntity<>(registerRequest, headers);

        // When
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                baseUrl + "/register",
                request,
                AuthResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        AuthResponse authResponse = response.getBody();
        assertThat(authResponse.getAccessToken()).isNotNull().isNotEmpty();
        assertThat(authResponse.getRefreshToken()).isNotNull().isNotEmpty();
        assertThat(authResponse.getTokenType()).isEqualTo("Bearer");
        assertThat(authResponse.getExpiresIn()).isPositive();
        assertThat(authResponse.getFirstName()).isEqualTo("John");
        assertThat(authResponse.getLastName()).isEqualTo("Doe");
        assertThat(authResponse.getEmail()).isEqualTo(uniqueEmail);
        assertThat(authResponse.getRoles()).isNotNull();
        assertThat(authResponse.getRoles()).contains("ROLE_USER");

        System.out.println("New user registered successfully: " + uniqueEmail);
        System.out.println("New user roles: " + authResponse.getRoles());
    }

    @Test
    @DisplayName("Should login with newly registered user")
    void shouldLoginWithNewlyRegisteredUser() {
        // Given - First register a new user
        String uniqueEmail = "testuser" + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
        String password = "password123";

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Jane");
        registerRequest.setLastName("Smith");
        registerRequest.setEmail(uniqueEmail);
        registerRequest.setPassword(password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> registerEntity = new HttpEntity<>(registerRequest, headers);

        // Register the user
        ResponseEntity<AuthResponse> registerResponse = restTemplate.postForEntity(
                baseUrl + "/register",
                registerEntity,
                AuthResponse.class);

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println("User registration completed for: " + uniqueEmail);

        // When - Now login with the registered user
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(uniqueEmail);
        loginRequest.setPassword(password);

        HttpEntity<LoginRequest> loginEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity(
                baseUrl + "/login",
                loginEntity,
                AuthResponse.class);

        // Then
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();

        AuthResponse authResponse = loginResponse.getBody();
        assertThat(authResponse.getAccessToken()).isNotNull().isNotEmpty();
        assertThat(authResponse.getRefreshToken()).isNotNull().isNotEmpty();
        assertThat(authResponse.getTokenType()).isEqualTo("Bearer");
        assertThat(authResponse.getExpiresIn()).isPositive();
        assertThat(authResponse.getFirstName()).isEqualTo("Jane");
        assertThat(authResponse.getLastName()).isEqualTo("Smith");
        assertThat(authResponse.getEmail()).isEqualTo(uniqueEmail);
        assertThat(authResponse.getRoles()).isNotNull();
        assertThat(authResponse.getRoles()).contains("ROLE_USER");

        System.out.println("Login successful for newly registered user: " + uniqueEmail);
        System.out.println("User access token: " +
                authResponse.getAccessToken().substring(0, Math.min(20, authResponse.getAccessToken().length()))
                + "...");
    }

    @Test
    @DisplayName("Should fail registration with duplicate email")
    void shouldFailRegistrationWithDuplicateEmail() {
        // Given - First register a user
        String email = "duplicate" + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
        RegisterRequest firstRegisterRequest = new RegisterRequest();
        firstRegisterRequest.setFirstName("First");
        firstRegisterRequest.setLastName("User");
        firstRegisterRequest.setEmail(email);
        firstRegisterRequest.setPassword("password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> firstRequest = new HttpEntity<>(firstRegisterRequest, headers);

        ResponseEntity<AuthResponse> firstResponse = restTemplate.postForEntity(
                baseUrl + "/register",
                firstRequest,
                AuthResponse.class);

        assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // When - Try to register with the same email
        RegisterRequest duplicateRegisterRequest = new RegisterRequest();
        duplicateRegisterRequest.setFirstName("Second");
        duplicateRegisterRequest.setLastName("User");
        duplicateRegisterRequest.setEmail(email); // Same email
        duplicateRegisterRequest.setPassword("differentpassword");

        HttpEntity<RegisterRequest> duplicateRequest = new HttpEntity<>(duplicateRegisterRequest, headers);

        ResponseEntity<String> duplicateResponse = restTemplate.postForEntity(
                baseUrl + "/register",
                duplicateRequest,
                String.class);

        // Then
        assertThat(duplicateResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        System.out.println("Duplicate email registration correctly rejected: " + duplicateResponse.getStatusCode());
    }

    @Test
    @DisplayName("Should fail registration with invalid email format")
    void shouldFailRegistrationWithInvalidEmailFormat() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("invalid-email-format"); // Invalid email
        registerRequest.setPassword("password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> request = new HttpEntity<>(registerRequest, headers);

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/register",
                request,
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        System.out.println("Invalid email format correctly rejected: " + response.getStatusCode());
    }

    @Test
    @DisplayName("Should fail registration with short password")
    void shouldFailRegistrationWithShortPassword() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("test" + UUID.randomUUID().toString().substring(0, 8) + "@test.com");
        registerRequest.setPassword("123"); // Too short

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> request = new HttpEntity<>(registerRequest, headers);

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/register",
                request,
                String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        System.out.println("Short password correctly rejected: " + response.getStatusCode());
    }

    @Test
    @DisplayName("Should complete full authentication flow")
    void shouldCompleteFullAuthenticationFlow() {
        // Given
        String uniqueEmail = "flowtest" + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
        String password = "password123";

        // Step 1: Register new user
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Flow");
        registerRequest.setLastName("Test");
        registerRequest.setEmail(uniqueEmail);
        registerRequest.setPassword(password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // When & Then - Registration
        ResponseEntity<AuthResponse> registerResponse = restTemplate.postForEntity(
                baseUrl + "/register",
                new HttpEntity<>(registerRequest, headers),
                AuthResponse.class);

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        AuthResponse registerAuthResponse = registerResponse.getBody();
        assertThat(registerAuthResponse).isNotNull();
        assertThat(registerAuthResponse.getAccessToken()).isNotNull();

        System.out.println("✅ Registration successful for: " + uniqueEmail);

        // Step 2: Login with registered credentials
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(uniqueEmail);
        loginRequest.setPassword(password);

        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity(
                baseUrl + "/login",
                new HttpEntity<>(loginRequest, headers),
                AuthResponse.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        AuthResponse loginAuthResponse = loginResponse.getBody();
        assertThat(loginAuthResponse).isNotNull();
        assertThat(loginAuthResponse.getAccessToken()).isNotNull();
        assertThat(loginAuthResponse.getRefreshToken()).isNotNull();

        System.out.println("✅ Login successful for: " + uniqueEmail);

        // Verify both responses have consistent user data
        assertThat(loginAuthResponse.getEmail()).isEqualTo(registerAuthResponse.getEmail());
        assertThat(loginAuthResponse.getFirstName()).isEqualTo(registerAuthResponse.getFirstName());
        assertThat(loginAuthResponse.getLastName()).isEqualTo(registerAuthResponse.getLastName());
        assertThat(loginAuthResponse.getRoles()).isEqualTo(registerAuthResponse.getRoles());

        System.out.println("✅ Full authentication flow completed successfully!");
        System.out.println("   User: " + loginAuthResponse.getFirstName() + " " + loginAuthResponse.getLastName());
        System.out.println("   Email: " + loginAuthResponse.getEmail());
        System.out.println("   Roles: " + loginAuthResponse.getRoles());
    }
}
