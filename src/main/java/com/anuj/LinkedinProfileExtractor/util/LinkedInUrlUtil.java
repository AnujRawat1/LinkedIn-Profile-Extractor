package com.anuj.LinkedinProfileExtractor.util;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class LinkedInUrlUtil {

    private static final String LINKEDIN_HOST = "www.linkedin.com";

    public String extractUsername(String profileUrl) {

        URI uri = parseUrl(profileUrl);

        validateHost(uri);

        String path = uri.getPath();

        if (path == null || !path.matches("^/in/[^/]+/?$")) {
            throw new IllegalArgumentException("URL must be a valid LinkedIn profile URL");
        }

        String[] parts = path.split("/");

        return parts[2];
    }

    public String normalizeUrl(String profileUrl) {
        String username = extractUsername(profileUrl);
        return "https://www.linkedin.com/in/" + username + "/";
    }

    private URI parseUrl(String profileUrl) {

        try {
            URI uri = new URI(profileUrl);

            if (!"https".equalsIgnoreCase(uri.getScheme())) {
                throw new IllegalArgumentException("LinkedIn URL must use HTTPS");
            }

            return uri;

        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL format");
        }
    }

    private void validateHost(URI uri) {

        String host = uri.getHost();

        if (host == null || !host.equalsIgnoreCase(LINKEDIN_HOST)) {
            throw new IllegalArgumentException("URL must belong to linkedin.com");
        }
    }
}