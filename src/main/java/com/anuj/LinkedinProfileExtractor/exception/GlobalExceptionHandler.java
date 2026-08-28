package com.anuj.LinkedinProfileExtractor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidLinkedInUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUrl(InvalidLinkedInUrlException exception) {

        ErrorResponse response = ErrorResponse.builder()
                        .success(false)
                        .message(exception.getMessage())
                        .errorCode("INVALID_LINKEDIN_URL")
                        .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(ProfileFetchException.class)
    public ResponseEntity<ErrorResponse> handleFetchError(ProfileFetchException exception) {

        ErrorResponse response =
                ErrorResponse.builder()
                        .success(false)
                        .message(exception.getMessage())
                        .errorCode("PROFILE_FETCH_FAILED")
                        .build();

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(response);
    }

    @ExceptionHandler(ProfileParseException.class)
    public ResponseEntity<ErrorResponse> handleParseError(ProfileParseException exception) {

        ErrorResponse response =
                ErrorResponse.builder()
                        .success(false)
                        .message(exception.getMessage())
                        .errorCode("PROFILE_PARSE_FAILED")
                        .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}