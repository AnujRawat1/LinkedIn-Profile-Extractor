package com.anuj.LinkedinProfileExtractor.exception;

public class ProfileFetchException extends RuntimeException {

    public ProfileFetchException(String message) {
        super(message);
    }

    public ProfileFetchException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}