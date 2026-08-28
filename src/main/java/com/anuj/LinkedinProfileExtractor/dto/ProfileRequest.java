package com.anuj.LinkedinProfileExtractor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ProfileRequest(

        @NotBlank(message = "LinkedIn profile URL is required")
        @Pattern(
                regexp = "^https://(www\\.)?linkedin\\.com/in/[^/?#]+/?$",
                message = "Please provide a valid LinkedIn profile URL"
        )
        String profileUrl

) {
}