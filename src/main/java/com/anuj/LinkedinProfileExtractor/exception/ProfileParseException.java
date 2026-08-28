package com.anuj.LinkedinProfileExtractor.exception;

public class ProfileParseException extends RuntimeException {

    public ProfileParseException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}