package com.anuj.LinkedinProfileExtractor.controller;

import com.anuj.LinkedinProfileExtractor.dto.ProfileExtractionRequest;
import com.anuj.LinkedinProfileExtractor.dto.ProfileResponse;
import com.anuj.LinkedinProfileExtractor.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<ProfileResponse> getProfile(@Valid @RequestBody ProfileExtractionRequest request) {

        ProfileResponse response = profileService.getProfile(request);
        return ResponseEntity.ok(response);

    }
}