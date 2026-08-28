package com.anuj.LinkedinProfileExtractor.service;

import com.anuj.LinkedinProfileExtractor.client.LinkedInClient;
import com.anuj.LinkedinProfileExtractor.dto.ProfileData;
import com.anuj.LinkedinProfileExtractor.dto.ProfileRequest;
import com.anuj.LinkedinProfileExtractor.dto.ProfileResponse;
import com.anuj.LinkedinProfileExtractor.parser.ProfileParser;
import com.anuj.LinkedinProfileExtractor.util.LinkedInUrlUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final LinkedInUrlUtil linkedInUrlUtil;
    private final LinkedInClient linkedInClient;
    private final ProfileParser profileParser;

    public ProfileResponse getProfile(ProfileRequest request) {

        String username = linkedInUrlUtil.extractUsername(request.profileUrl());
        String normalizedUrl = linkedInUrlUtil.normalizeUrl(request.profileUrl());

        String rawResponse = linkedInClient.fetchProfile(normalizedUrl);

        ProfileData profileData = profileParser.parse(rawResponse, normalizedUrl);

        return new ProfileResponse(
                true,
                "Profile URL validated successfully",
                username,
                profileData
        );
    }
}