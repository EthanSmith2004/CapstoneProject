package com.jel.spys.exception;

import com.jel.spys.model.AdminFinanceLoadResponse;
import com.jel.spys.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
                        ResourceNotFoundException ex, HttpServletRequest request) {
                log.error("Resource not found: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("Resource Not Found")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(UserNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleUserNotFoundException(
                        UserNotFoundException ex, HttpServletRequest request) {
                log.error("User not found: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("User Not Found")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(UserAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(
                        UserAlreadyExistsException ex, HttpServletRequest request) {
                log.error("User already exists: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.CONFLICT.value())
                                .error("User Already Exists")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }

        @ExceptionHandler(UserProfileNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleUserProfileNotFoundException(
                        UserProfileNotFoundException ex, HttpServletRequest request) {
                log.error("User profile not found: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("User Profile Not Found")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(UserProfileAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleUserProfileAlreadyExistsException(
                        UserProfileAlreadyExistsException ex, HttpServletRequest request) {
                log.error("User profile already exists: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.CONFLICT.value())
                                .error("User Profile Already Exists")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }

        @ExceptionHandler(InvalidTokenException.class)
        public ResponseEntity<ErrorResponse> handleInvalidTokenException(
                        InvalidTokenException ex, HttpServletRequest request) {
                log.error("Invalid token: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("Invalid Token")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(AuthenticationFailedException.class)
        public ResponseEntity<ErrorResponse> handleAuthenticationFailedException(
                        AuthenticationFailedException ex, HttpServletRequest request) {
                log.error("Authentication failed: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("Authentication Failed")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleBadCredentialsException(
                        BadCredentialsException ex, HttpServletRequest request) {
                log.error("Bad credentials: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("Authentication Failed")
                                .message("Invalid username or password")
                                .path(request.getRequestURI())
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ErrorResponse> handleAuthenticationException(
                        AuthenticationException ex, HttpServletRequest request) {
                log.error("Authentication error: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("Authentication Error")
                                .message("Authentication failed")
                                .path(request.getRequestURI())
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(
                        AccessDeniedException ex, HttpServletRequest request) {
                log.error("Access denied: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.FORBIDDEN.value())
                                .error("Access Denied")
                                .message("You don't have permission to access this resource")
                                .path(request.getRequestURI())
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationExceptions(
                        MethodArgumentNotValidException ex, HttpServletRequest request) {
                log.error("Validation failed: {}", ex.getMessage());

                List<ErrorResponse.ValidationError> validationErrors = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(fieldError -> ErrorResponse.ValidationError.builder()
                                                .field(fieldError.getField())
                                                .message(fieldError.getDefaultMessage())
                                                .build())
                                .collect(Collectors.toList());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Validation Failed")
                                .message("Request validation failed")
                                .path(request.getRequestURI())
                                .validationErrors(validationErrors)
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ErrorResponse> handleConstraintViolationException(
                        ConstraintViolationException ex, HttpServletRequest request) {
                log.error("Constraint violation: {}", ex.getMessage());

                List<ErrorResponse.ValidationError> validationErrors = ex.getConstraintViolations()
                                .stream()
                                .map(violation -> ErrorResponse.ValidationError.builder()
                                                .field(violation.getPropertyPath().toString())
                                                .message(violation.getMessage())
                                                .build())
                                .collect(Collectors.toList());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Validation Failed")
                                .message("Request validation failed")
                                .path(request.getRequestURI())
                                .validationErrors(validationErrors)
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
                        IllegalArgumentException ex, HttpServletRequest request) {
                log.error("Illegal argument: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Bad Request")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(RequestStateValidationException.class)
        public ResponseEntity<ErrorResponse> handleRequestStateValidationException(
                        RuntimeException ex, HttpServletRequest request) {
                log.error("Request State Validation exception: {}", ex.getMessage(), ex);

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("BadRequest")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ErrorResponse> handleRuntimeException(
                        RuntimeException ex, HttpServletRequest request) {
                log.error("Runtime exception: {}", ex.getMessage(), ex);

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .error("Internal Server Error")
                                .message("An unexpected error occurred")
                                .path(request.getRequestURI())
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(BulkLoadException.class)
        public ResponseEntity<AdminFinanceLoadResponse> handleBulkLoadException(BulkLoadException ex, HttpServletRequest request) {
                log.error("Runtime exception: {}", ex.getMessage(), ex);

                return new ResponseEntity<>(ex.getResponse(), HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(
                        Exception ex, HttpServletRequest request) {
                log.error("Unexpected exception: {}", ex.getMessage(), ex);

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .error("Internal Server Error")
                                .message("An unexpected error occurred")
                                .path(request.getRequestURI())
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
}
