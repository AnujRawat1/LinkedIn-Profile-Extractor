package com.anuj.LinkedinProfileExtractor.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class LinkedInHttpClient implements LinkedInClient {

    private final RestClient restClient;

    @Override
    public String fetchProfile(String profileUrl) {

        return restClient
                .get()
                .uri(profileUrl)
                .retrieve()
                .body(String.class);
    }
}