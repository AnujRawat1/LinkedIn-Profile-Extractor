package com.anuj.LinkedinProfileExtractor.parser;

import com.anuj.LinkedinProfileExtractor.dto.ProfileData;
import org.springframework.stereotype.Component;

@Component
public class LinkedInProfileParser implements ProfileParser {

    @Override
    public ProfileData parse(String rawResponse, String profileUrl) {

        return ProfileData.builder()
                .profileUrl(profileUrl)
                .build();
    }
}