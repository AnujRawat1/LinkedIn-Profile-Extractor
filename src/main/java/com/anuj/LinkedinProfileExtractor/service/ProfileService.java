package com.anuj.LinkedinProfileExtractor.service;

import com.anuj.LinkedinProfileExtractor.dto.ProfileRequest;
import com.anuj.LinkedinProfileExtractor.dto.ProfileResponse;
import com.anuj.LinkedinProfileExtractor.util.LinkedInUrlUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final LinkedInUrlUtil linkedInUrlUtil;

    public ProfileResponse getProfile(ProfileRequest request) {

        String username = linkedInUrlUtil.extractUsername(request.profileUrl());

        String normalizedUrl = linkedInUrlUtil.normalizeUrl(request.profileUrl());

        return new ProfileResponse(
                true,
                "Profile URL validated successfully",
                normalizedUrl
        );
    }
}