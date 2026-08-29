package com.anuj.LinkedinProfileExtractor.exception;

public class LinkedInApiException extends RuntimeException {

    private final int statusCode;

    public LinkedInApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public LinkedInApiException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
