package com.anuj.LinkedinProfileExtractor.exception;

public class LinkedInAuthenticationException extends RuntimeException {

    public LinkedInAuthenticationException(String message) {
        super(message);
    }

    public LinkedInAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
