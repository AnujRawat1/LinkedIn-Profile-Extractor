package com.anuj.LinkedinProfileExtractor.client;

import com.anuj.LinkedinProfileExtractor.config.LinkedInProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class LinkedInHttpClient {

    private final RestClient voyagerRestClient;
    private final LinkedInProperties properties;

    /**
     * Fetch full profile data using LinkedIn's internal Voyager API
     * This endpoint uses cookie-based authentication (li_at cookie)
     *
     * Endpoint: GET /voyager/api/identity/dash/profiles
     * Params: q=memberIdentity, memberIdentity={publicIdentifier}, decorationId={decorationId}
     *
     * @param liAtCookie The li_at cookie value from a logged-in LinkedIn session
     * @param jsessionidCookie The JSESSIONID cookie value for CSRF protection
     * @param publicIdentifier The LinkedIn public identifier (vanity name from URL)
     * @return Raw JSON response from Voyager API
     */
    public String fetchVoyagerProfile(String liAtCookie, String jsessionidCookie, String publicIdentifier) {
        log.debug("Fetching Voyager profile for public identifier: {}", publicIdentifier);

        try {
            var response = voyagerRestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/voyager/api/identity/dash/profiles")
                            .queryParam("q", "memberIdentity")
                            .queryParam("memberIdentity", publicIdentifier)
                            .queryParam("decorationId", properties.getProfileDecorationId())
                            .build())
                    .header(HttpHeaders.COOKIE, "li_at=" + liAtCookie + "; JSESSIONID=" + jsessionidCookie)
                    .header("csrf-token", jsessionidCookie != null ? jsessionidCookie.replace("\"", "") : "")
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .header("x-restli-protocol-version", "2.0.0")
                    .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .retrieve()
                    .toEntity(String.class);

            log.info("LinkedIn Voyager API response fetched successfully");
            log.debug("HTTP Status: {}", response.getStatusCode());

            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to fetch Voyager profile: {}", e.getMessage(), e);
            throw new com.anuj.LinkedinProfileExtractor.exception.ProfileFetchException(
                    "Failed to fetch profile from LinkedIn Voyager API: " + e.getMessage(),
                    e
            );
        }
    }
}