package com.anuj.LinkedinProfileExtractor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Response containing extracted LinkedIn profile data")
public class ProfileResponse {

    @Schema(description = "Indicates if the extraction was successful")
    private boolean success;

    @Schema(description = "Status message")
    private String message;

    @Schema(description = "LinkedIn username (vanity name)")
    private String username;

    @Schema(description = "Extracted profile data")
    private ProfileData data;

}

