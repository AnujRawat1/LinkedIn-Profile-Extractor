package com.anuj.LinkedinProfileExtractor.service;

import com.anuj.LinkedinProfileExtractor.client.LinkedInSeleniumClient;
import com.anuj.LinkedinProfileExtractor.dto.ProfileData;
import com.anuj.LinkedinProfileExtractor.dto.ProfileExtractionRequest;
import com.anuj.LinkedinProfileExtractor.dto.ProfileResponse;
import com.anuj.LinkedinProfileExtractor.exception.LinkedInAuthenticationException;
import com.anuj.LinkedinProfileExtractor.parser.ProfileParser;
import com.anuj.LinkedinProfileExtractor.util.LinkedInUrlUtil;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final LinkedInUrlUtil linkedInUrlUtil;
    private final LinkedInSeleniumClient linkedInSeleniumClient;
    private final ProfileParser profileParser;

    private final Dotenv dotenv = Dotenv.configure()
            .filename(".env")
            .ignoreIfMissing()
            .load();

    public ProfileResponse getProfile(ProfileExtractionRequest request) {

        String normalizedUrl = linkedInUrlUtil.normalizeUrl(request.getProfileUrl());

        log.info("Starting profile extraction for LinkedIn URL: {}", normalizedUrl);

        String liAtCookie = dotenv.get("LINKEDIN_LI_AT_COOKIE");
        String jsessionIdCookie = dotenv.get("LINKEDIN_JSESSIONID");

        log.info("LINKEDIN_LI_AT_COOKIE value: {}", liAtCookie != null ? (liAtCookie.isEmpty() ? "EMPTY" : "SET") : "NULL");
        log.info("LINKEDIN_JSESSIONID value: {}", jsessionIdCookie != null ? (jsessionIdCookie.isEmpty() ? "EMPTY" : "SET") : "NULL");

        if (liAtCookie == null || liAtCookie.isEmpty()) {
            throw new LinkedInAuthenticationException(
                    "LINKEDIN_LI_AT_COOKIE environment variable is required for LinkedIn authentication"
            );
        }

        if (jsessionIdCookie == null || jsessionIdCookie.isEmpty()) {
            throw new LinkedInAuthenticationException(
                    "LINKEDIN_JSESSIONID environment variable is required for LinkedIn session"
            );
        }

        try {
            // Initialize Selenium WebDriver
            linkedInSeleniumClient.initializeDriver();

            // Set LinkedIn session cookies
            linkedInSeleniumClient.setLinkedInCookies(liAtCookie, jsessionIdCookie);

            // Fetch profile page HTML
            String htmlContent = linkedInSeleniumClient.fetchProfilePage(normalizedUrl);

            log.info("LinkedIn profile page fetched successfully");

            // Parse HTML content
            ProfileData profileData = profileParser.parse(htmlContent, normalizedUrl);

            log.info("Profile parsing completed successfully");

            return new ProfileResponse(
                    true,
                    "Profile extracted successfully",
                    linkedInUrlUtil.extractUsername(normalizedUrl),
                    profileData
            );
        } catch (Exception e) {
            log.error("Error during profile extraction: {}", e.getMessage(), e);
            throw new com.anuj.LinkedinProfileExtractor.exception.ProfileFetchException(
                    "Failed to extract profile: " + e.getMessage(),
                    e
            );
        } finally {
            // Always close the driver
            try {
                Thread.sleep(1000); // Give time for any pending operations
                linkedInSeleniumClient.closeDriver();
            } catch (Exception e) {
                log.warn("Error during driver cleanup: {}", e.getMessage());
            }
        }
    }
}