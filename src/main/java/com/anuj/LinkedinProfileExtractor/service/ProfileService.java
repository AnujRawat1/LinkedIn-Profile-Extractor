package com.anuj.LinkedinProfileExtractor.service;

import com.anuj.LinkedinProfileExtractor.client.LinkedInHttpClient;
import com.anuj.LinkedinProfileExtractor.dto.ProfileData;
import com.anuj.LinkedinProfileExtractor.dto.ProfileExtractionRequest;
import com.anuj.LinkedinProfileExtractor.dto.ProfileResponse;
import com.anuj.LinkedinProfileExtractor.parser.ProfileParser;
import com.anuj.LinkedinProfileExtractor.util.LinkedInUrlUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final LinkedInUrlUtil linkedInUrlUtil;
    private final LinkedInHttpClient linkedInHttpClient;
    private final ProfileParser profileParser;

    public ProfileResponse getProfile(ProfileExtractionRequest request) {

        String username = linkedInUrlUtil.extractUsername(request.getProfileUrl());
        String normalizedUrl = linkedInUrlUtil.normalizeUrl(request.getProfileUrl());

        log.info("Starting profile extraction for LinkedIn username: {}", username);

        // TODO: This needs li_at cookie from a logged-in LinkedIn session
        // In production, this would come from session management or user-provided
        String liAtCookie = System.getenv("LINKEDIN_LI_AT_COOKIE");

        if (liAtCookie == null || liAtCookie.isEmpty()) {
            throw new IllegalStateException(
                    "LINKEDIN_LI_AT_COOKIE environment variable is required for Voyager API access"
            );
        }

        String rawResponse = linkedInHttpClient.fetchVoyagerProfile(liAtCookie, username);

        log.info("LinkedIn profile request completed for username: {}", username);

        ProfileData profileData = profileParser.parse(rawResponse, normalizedUrl);

        log.info("Profile parsing completed for username: {}", username);

        return new ProfileResponse(
                true,
                "Profile extracted successfully",
                username,
                profileData
        );
    }
}