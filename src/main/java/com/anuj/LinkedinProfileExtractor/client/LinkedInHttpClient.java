package com.anuj.LinkedinProfileExtractor.client;

import com.anuj.LinkedinProfileExtractor.config.LinkedInProperties;
import com.anuj.LinkedinProfileExtractor.exception.ProfileFetchException;
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

        try {

            return restClient
                    .get()
                    .uri(uriBuilder ->
                            uriBuilder
                                    .path(properties.getProfileEndpoint())
                                    .queryParam(
                                            "profileUrl",
                                            profileUrl
                                    )
                                    .build()
                    )
                    .header(
                            "Authorization",
                            "Bearer " + properties.getApiToken()
                    )
                    .retrieve()
                    .body(String.class);

        } catch (Exception e) {

            throw new ProfileFetchException(
                    "Unable to retrieve LinkedIn profile",
                    e
            );
        }
    }
}