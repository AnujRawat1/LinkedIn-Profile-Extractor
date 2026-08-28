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
    private final RestClient voyagerRestClient;
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
     * Fetch full profile data using LinkedIn's internal Voyager API
     * This endpoint uses cookie-based authentication (li_at cookie)
     *
     * Endpoint: GET /voyager/api/identity/dash/profiles
     * Params: q=memberIdentity, memberIdentity={publicIdentifier}, decorationId={decorationId}
     *
     * @param liAtCookie The li_at cookie value from a logged-in LinkedIn session
     * @param publicIdentifier The LinkedIn public identifier (vanity name from URL)
     * @return Raw JSON response from Voyager API
     */
    public String fetchVoyagerProfile(String liAtCookie, String publicIdentifier) {
        log.debug("Fetching Voyager profile for public identifier: {}", publicIdentifier);

        return voyagerRestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/voyager/api/identity/dash/profiles")
                        .queryParam("q", "memberIdentity")
                        .queryParam("memberIdentity", publicIdentifier)
                        .queryParam("decorationId", properties.getProfileDecorationId())
                        .build())
                .header(HttpHeaders.COOKIE, "li_at=" + liAtCookie)
                .header("x-li-lang", "en_US")
                .header("x-restli-protocol-version", "2.0.0")
                .header(HttpHeaders.ACCEPT, "application/vnd.linkedin.normalized+json+2.1")
                .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
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