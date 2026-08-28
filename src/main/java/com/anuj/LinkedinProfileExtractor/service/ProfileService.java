package com.anuj.LinkedinProfileExtractor.service;

import com.anuj.LinkedinProfileExtractor.dto.ProfileRequest;
import com.anuj.LinkedinProfileExtractor.dto.ProfileResponse;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    public ProfileResponse getProfile(ProfileRequest request) {

        return new ProfileResponse(
                true,
                "Profile URL received successfully",
                request.profileUrl()
        );
    }
}