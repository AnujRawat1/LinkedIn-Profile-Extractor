package com.anuj.LinkedinProfileExtractor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for LinkedIn profile extraction")
public class ProfileExtractionRequest {

    @NotBlank(message = "LinkedIn profile URL is required")
    @Schema(
            description = "LinkedIn profile URL",
            example = "https://www.linkedin.com/in/john-doe/",
            required = true
    )
    private String profileUrl;

}