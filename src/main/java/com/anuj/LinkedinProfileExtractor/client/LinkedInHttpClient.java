package com.anuj.LinkedinProfileExtractor.client;

import com.anuj.LinkedinProfileExtractor.config.LinkedInProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class LinkedInHttpClient implements LinkedInClient {

    private final RestClient restClient;
    private final LinkedInProperties properties;

    @Override
    public String fetchProfile(String profileUrl) {

        return restClient
                .get()
                .uri(profileUrl)
                .retrieve()
                .body(String.class);
    }
}