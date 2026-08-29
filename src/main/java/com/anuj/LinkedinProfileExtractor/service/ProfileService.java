package com.anuj.LinkedinProfileExtractor.service;

import com.anuj.LinkedinProfileExtractor.client.LinkedInHttpClient;
import com.anuj.LinkedinProfileExtractor.dto.ProfileData;
import com.anuj.LinkedinProfileExtractor.dto.ProfileExtractionRequest;
import com.anuj.LinkedinProfileExtractor.dto.ProfileResponse;
import com.anuj.LinkedinProfileExtractor.exception.LinkedInAuthenticationException;
import com.anuj.LinkedinProfileExtractor.parser.ProfileParser;
import com.anuj.LinkedinProfileExtractor.util.LinkedInUrlUtil;
import io.github.cdimascio.dotenv.Dotenv;
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

    private final Dotenv dotenv = Dotenv.configure()
            .filename(".env")
            .ignoreIfMissing()
            .load();

    public ProfileResponse getProfile(ProfileExtractionRequest request) {

        String normalizedUrl = linkedInUrlUtil.normalizeUrl(request.getProfileUrl());
        String publicIdentifier = linkedInUrlUtil.extractUsername(request.getProfileUrl());

        log.info("Starting profile extraction for LinkedIn URL: {}", normalizedUrl);

        String liAtCookie = dotenv.get("LI_AT");
        String jsessionIdCookie = dotenv.get("JSESSIONID");

        log.info("LI_AT value: {}", liAtCookie != null ? (liAtCookie.isEmpty() ? "EMPTY" : "SET") : "NULL");
        log.info("JSESSIONID value: {}", jsessionIdCookie != null ? (jsessionIdCookie.isEmpty() ? "EMPTY" : "SET") : "NULL");

        if (liAtCookie == null || liAtCookie.isEmpty()) {
            throw new LinkedInAuthenticationException(
                    "LI_AT environment variable is required for LinkedIn authentication"
            );
        }

        if (jsessionIdCookie == null || jsessionIdCookie.isEmpty()) {
            throw new LinkedInAuthenticationException(
                    "JSESSIONID environment variable is required for LinkedIn session"
            );
        }

        try {
            // Fetch profile data using LinkedIn Voyager API
            String jsonResponse = linkedInHttpClient.fetchVoyagerProfile(liAtCookie, jsessionIdCookie, publicIdentifier);

            log.info("LinkedIn Voyager API response fetched successfully");

            // DIAGNOSTIC: Skip parsing to see response details
            if (jsonResponse == null || jsonResponse.isEmpty()) {
                log.error("DIAGNOSTIC - Response is null or empty, cannot parse");
                throw new com.anuj.LinkedinProfileExtractor.exception.ProfileFetchException(
                        "DIAGNOSTIC - Response is null or empty, cannot parse"
                );
            }

            // Parse JSON response
            ProfileData profileData = profileParser.parse(jsonResponse, normalizedUrl);

            log.info("Profile parsing completed successfully");

            return new ProfileResponse(
                    true,
                    "Profile extracted successfully",
                    linkedInUrlUtil.extractUsername(normalizedUrl),
                    profileData
            );
        } catch (Exception e) {
            log.error("Error during profile extraction: {}", e.getMessage(), e);
            throw new com.anuj.LinkedinProfileExtractor.exception.ProfileFetchException(
                    "Failed to extract profile: " + e.getMessage(),
                    e
            );
        }
    }
}