package com.jel.spys.util;

import com.jel.spys.model.AuthResponse;
import com.jel.spys.model.LoginRequest;
import com.jel.spys.model.RegisterRequest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Utility class for authentication operations in test environments.
 * Provides convenient methods for login, registration, and token management
 * to reduce boilerplate code in integration tests.
 */
@Component
public class AuthTestUtils {

    private static final String AUTH_ENDPOINT = "/api/auth";
    
    // Default test credentials
    public static final String DEFAULT_ADMIN_EMAIL = "admin@demo.com";
    public static final String DEFAULT_ADMIN_PASSWORD = "admin123";
    
    /**
     * Authenticates with the default admin user.
     * 
     * @param restTemplate the TestRestTemplate instance
     * @param port the server port
     * @return AuthResponse containing tokens and user info
     * @throws AssertionError if login fails
     */
    public static AuthResponse loginAsAdmin(TestRestTemplate restTemplate, int port) {
        return login(restTemplate, port, DEFAULT_ADMIN_EMAIL, DEFAULT_ADMIN_PASSWORD);
    }
    
    /**
     * Authenticates with the provided credentials.
     * 
     * @param restTemplate the TestRestTemplate instance
     * @param port the server port
     * @param email the user's email
     * @param password the user's password
     * @return AuthResponse containing tokens and user info
     * @throws AssertionError if login fails
     */
    public static AuthResponse login(TestRestTemplate restTemplate, int port, String email, String password) {
        String url = "http://localhost:" + port + AUTH_ENDPOINT + "/login";
        
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);
        
        HttpHeaders headers = createJsonHeaders();
        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);
        
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(url, request, AuthResponse.class);
        
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new AssertionError("Login failed for email: " + email + ". Status: " + response.getStatusCode());
        }
        
        return response.getBody();
    }
    
    /**
     * Registers a new user with random email and returns the authentication response.
     * 
     * @param restTemplate the TestRestTemplate instance
     * @param port the server port
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param password the user's password
     * @return AuthResponse containing tokens and user info
     * @throws AssertionError if registration fails
     */
    public static AuthResponse registerUser(TestRestTemplate restTemplate, int port, 
                                          String firstName, String lastName, String password) {
        String uniqueEmail = generateUniqueEmail();
        return registerUser(restTemplate, port, firstName, lastName, uniqueEmail, password);
    }
    
    /**
     * Registers a new user with the specified details.
     * 
     * @param restTemplate the TestRestTemplate instance
     * @param port the server port
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param email the user's email
     * @param password the user's password
     * @return AuthResponse containing tokens and user info
     * @throws AssertionError if registration fails
     */
    public static AuthResponse registerUser(TestRestTemplate restTemplate, int port,
                                          String firstName, String lastName, String email, String password) {
        String url = "http://localhost:" + port + AUTH_ENDPOINT + "/register";
        
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName(firstName);
        registerRequest.setLastName(lastName);
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        
        HttpHeaders headers = createJsonHeaders();
        HttpEntity<RegisterRequest> request = new HttpEntity<>(registerRequest, headers);
        
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(url, request, AuthResponse.class);
        
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new AssertionError("Registration failed for email: " + email + ". Status: " + response.getStatusCode());
        }
        
        return response.getBody();
    }
    
    /**
     * Registers a new test user with default values and random email.
     * 
     * @param restTemplate the TestRestTemplate instance
     * @param port the server port
     * @return AuthResponse containing tokens and user info
     */
    public static AuthResponse registerTestUser(TestRestTemplate restTemplate, int port) {
        return registerUser(restTemplate, port, "Test", "User", "password123");
    }
    
    /**
     * Creates an HttpHeaders object with JSON content type.
     * 
     * @return HttpHeaders configured for JSON requests
     */
    public static HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
    
    /**
     * Creates an HttpHeaders object with Authorization bearer token.
     * 
     * @param token the JWT access token
     * @return HttpHeaders with Authorization header set
     */
    public static HttpHeaders createAuthHeaders(String token) {
        HttpHeaders headers = createJsonHeaders();
        headers.setBearerAuth(token);
        return headers;
    }
    
    /**
     * Creates an authenticated HttpEntity with the provided body and token.
     * 
     * @param body the request body
     * @param token the JWT access token
     * @param <T> the type of the request body
     * @return HttpEntity with authorization headers
     */
    public static <T> HttpEntity<T> createAuthenticatedRequest(T body, String token) {
        return new HttpEntity<>(body, createAuthHeaders(token));
    }
    
    /**
     * Generates a unique email address for testing.
     * 
     * @return a unique email address
     */
    public static String generateUniqueEmail() {
        return "testuser" + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
    }
    
    /**
     * Generates a unique email with a specific prefix.
     * 
     * @param prefix the prefix for the email
     * @return a unique email address with the specified prefix
     */
    public static String generateUniqueEmail(String prefix) {
        return prefix + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
    }
    
    /**
     * Helper class to hold authentication context for a user.
     */
    public static class AuthContext {
        private final AuthResponse authResponse;
        private final String email;
        
        public AuthContext(AuthResponse authResponse, String email) {
            this.authResponse = authResponse;
            this.email = email;
        }
        
        public String getAccessToken() {
            return authResponse.getAccessToken();
        }
        
        public String getRefreshToken() {
            return authResponse.getRefreshToken();
        }
        
        public String getEmail() {
            return email;
        }
        
        public AuthResponse getAuthResponse() {
            return authResponse;
        }
        
        /**
         * Creates authenticated headers for this user.
         * 
         * @return HttpHeaders with this user's access token
         */
        public HttpHeaders createHeaders() {
            return createAuthHeaders(getAccessToken());
        }
        
        /**
         * Creates an authenticated request for this user.
         * 
         * @param body the request body
         * @param <T> the type of the request body
         * @return HttpEntity with this user's authorization
         */
        public <T> HttpEntity<T> createRequest(T body) {
            return createAuthenticatedRequest(body, getAccessToken());
        }
    }
    
    /**
     * Creates an AuthContext for the admin user.
     * 
     * @param restTemplate the TestRestTemplate instance
     * @param port the server port
     * @return AuthContext for the admin user
     */
    public static AuthContext createAdminContext(TestRestTemplate restTemplate, int port) {
        AuthResponse auth = loginAsAdmin(restTemplate, port);
        return new AuthContext(auth, DEFAULT_ADMIN_EMAIL);
    }
    
    /**
     * Creates an AuthContext for a new test user.
     * 
     * @param restTemplate the TestRestTemplate instance
     * @param port the server port
     * @return AuthContext for a new test user
     */
    public static AuthContext createUserContext(TestRestTemplate restTemplate, int port) {
        AuthResponse auth = registerTestUser(restTemplate, port);
        return new AuthContext(auth, auth.getEmail());
    }
    
    /**
     * Creates an AuthContext for a user with specific details.
     * 
     * @param restTemplate the TestRestTemplate instance
     * @param port the server port
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param password the user's password
     * @return AuthContext for the created user
     */
    public static AuthContext createUserContext(TestRestTemplate restTemplate, int port,
                                              String firstName, String lastName, String password) {
        AuthResponse auth = registerUser(restTemplate, port, firstName, lastName, password);
        return new AuthContext(auth, auth.getEmail());
    }
}
