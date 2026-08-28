package com.anuj.LinkedinProfileExtractor.dto;

public record ProfileResponse(
        boolean success,
        String message,
        String profileUrl
) {
}
