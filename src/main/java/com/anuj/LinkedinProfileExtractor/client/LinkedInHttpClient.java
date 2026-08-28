package com.anuj.LinkedinProfileExtractor.client;

import com.anuj.LinkedinProfileExtractor.config.LinkedInProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class LinkedInHttpClient {

    private final RestClient restClient;
    private final LinkedInProperties properties;

    /**
     * Fetch basic profile information from LinkedIn OIDC userinfo endpoint
     * This requires a valid OAuth access token
     */
    public String fetchUserInfo(String accessToken) {
        log.debug("Fetching user info from LinkedIn");

        return restClient
                .get()
                .uri(properties.getBaseUrl() + properties.getProfileEndpoint())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .body(String.class);
    }

    /**
     * Fetch profile data for a specific LinkedIn profile
     * This will be implemented after reverse-engineering LinkedIn's profile endpoints
     */
    public String fetchProfileData(String accessToken, String profileIdentifier) {
        log.debug("Fetching profile data for identifier: {}", profileIdentifier);

        // TODO: Implement after reverse-engineering LinkedIn profile endpoints
        // This will need to discover the actual endpoint LinkedIn uses for profile data
        throw new UnsupportedOperationException(
                "Profile data endpoint not yet reverse-engineered"
        );
    }
}