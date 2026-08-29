package com.anuj.LinkedinProfileExtractor.controller;

import com.anuj.LinkedinProfileExtractor.dto.ProfileExtractionRequest;
import com.anuj.LinkedinProfileExtractor.dto.ProfileResponse;
import com.anuj.LinkedinProfileExtractor.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
@Tag(name = "Profile Extraction", description = "LinkedIn profile extraction API")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    @Operation(
            summary = "Extract LinkedIn profile data",
            description = "Extracts comprehensive profile information from a LinkedIn profile URL using the Voyager API"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile extracted successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid LinkedIn URL provided"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed - missing or invalid li_at cookie"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "LinkedIn profile not found"
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Rate limit exceeded"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error during profile extraction"
            )
    })
    public ResponseEntity<ProfileResponse> getProfile(@Valid @RequestBody ProfileExtractionRequest request) {

        ProfileResponse response = profileService.getProfile(request);
        return ResponseEntity.ok(response);

    }
}