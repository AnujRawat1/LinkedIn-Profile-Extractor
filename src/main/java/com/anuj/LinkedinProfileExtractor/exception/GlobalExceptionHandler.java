package com.anuj.LinkedinProfileExtractor.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidLinkedInUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUrl(InvalidLinkedInUrlException exception) {
        log.error("Invalid LinkedIn URL: {}", exception.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("INVALID_LINKEDIN_URL")
                .message(exception.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(LinkedInAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthError(LinkedInAuthenticationException exception) {
        log.error("LinkedIn authentication error: {}", exception.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("LINKEDIN_AUTHENTICATION_FAILED")
                .message("Authentication failed. Please check your credentials.")
                .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(LinkedInProfileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProfileNotFound(LinkedInProfileNotFoundException exception) {
        log.error("LinkedIn profile not found: {}", exception.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("LINKEDIN_PROFILE_NOT_FOUND")
                .message(exception.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(LinkedInApiException.class)
    public ResponseEntity<ErrorResponse> handleLinkedInApiError(LinkedInApiException exception) {
        log.error("LinkedIn API error (status {}): {}", exception.getStatusCode(), exception.getMessage());

        HttpStatus status;
        String error;

        switch (exception.getStatusCode()) {
            case 401:
                status = HttpStatus.UNAUTHORIZED;
                error = "UNAUTHORIZED";
                break;
            case 403:
                status = HttpStatus.FORBIDDEN;
                error = "FORBIDDEN";
                break;
            case 404:
                status = HttpStatus.NOT_FOUND;
                error = "NOT_FOUND";
                break;
            case 429:
                status = HttpStatus.TOO_MANY_REQUESTS;
                error = "RATE_LIMIT_EXCEEDED";
                break;
            default:
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                error = "LINKEDIN_API_ERROR";
        }

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(exception.getMessage())
                .build();

        return ResponseEntity
                .status(status)
                .body(response);
    }

    @ExceptionHandler(ProfileFetchException.class)
    public ResponseEntity<ErrorResponse> handleFetchError(ProfileFetchException exception) {
        log.error("Profile fetch error: {}", exception.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_GATEWAY.value())
                .error("PROFILE_FETCH_FAILED")
                .message(exception.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(response);
    }

    @ExceptionHandler(org.springframework.web.client.HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientError(org.springframework.web.client.HttpClientErrorException exception) {
        log.error("HTTP client error: {} - {}", exception.getStatusCode(), exception.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(exception.getStatusCode().value())
                .error("HTTP_CLIENT_ERROR")
                .message("LinkedIn API error: " + exception.getStatusCode() + " - " + exception.getResponseBodyAsString())
                .build();

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(response);
    }

    @ExceptionHandler(ProfileParseException.class)
    public ResponseEntity<ErrorResponse> handleParseError(ProfileParseException exception) {
        log.error("Profile parse error: {}", exception.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("PROFILE_PARSE_FAILED")
                .message(exception.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException exception) {
        log.error("Illegal state: {}", exception.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("INTERNAL_ERROR")
                .message(exception.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(Exception exception) {
        log.error("Unexpected error: {}", exception.getMessage(), exception);

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred")
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}