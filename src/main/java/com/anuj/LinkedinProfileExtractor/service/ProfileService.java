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

        // TODO: This needs OAuth access token
        // For now, we'll use a placeholder to test the flow
        // In production, this would come from the OAuth flow or token storage
        String accessToken = "PLACEHOLDER_TOKEN";

        String rawResponse = linkedInHttpClient.fetchProfileData(accessToken, username);

        ProfileData profileData = profileParser.parse(rawResponse, normalizedUrl);

        return new ProfileResponse(
                true,
                "Profile extracted successfully",
                username,
                profileData
        );
    }
}