package com.anuj.LinkedinProfileExtractor.client;

import com.anuj.LinkedinProfileExtractor.config.LinkedInProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class LinkedInHttpClient {

    private final RestClient restClient;
    private final LinkedInProperties properties;

    public String fetchProfile(String accessToken) {

        return restClient
                .get()
                .uri(properties.getBaseUrl() + properties.getProfileEndpoint())
                .header(
                        "Authorization",
                        "Bearer " + accessToken
                )
                .retrieve()
                .body(String.class);
    }
}