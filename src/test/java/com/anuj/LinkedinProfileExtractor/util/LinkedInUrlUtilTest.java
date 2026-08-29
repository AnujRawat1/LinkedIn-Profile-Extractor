package com.anuj.LinkedinProfileExtractor.util;

import com.anuj.LinkedinProfileExtractor.exception.InvalidLinkedInUrlException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkedInUrlUtilTest {

    private final LinkedInUrlUtil urlUtil = new LinkedInUrlUtil();

    @Test
    void shouldExtractUsernameFromValidUrl() {
        String username = urlUtil.extractUsername("https://www.linkedin.com/in/john-doe/");
        assertEquals("john-doe", username);
    }

    @Test
    void shouldExtractUsernameFromUrlWithoutTrailingSlash() {
        String username = urlUtil.extractUsername("https://www.linkedin.com/in/john-doe");
        assertEquals("john-doe", username);
    }

    @Test
    void shouldNormalizeUrl() {
        String normalized = urlUtil.normalizeUrl("https://www.linkedin.com/in/john-doe");
        assertEquals("https://www.linkedin.com/in/john-doe/", normalized);
    }

    @Test
    void shouldNormalizeUrlWithTrailingSlash() {
        String normalized = urlUtil.normalizeUrl("https://www.linkedin.com/in/john-doe/");
        assertEquals("https://www.linkedin.com/in/john-doe/", normalized);
    }

    @Test
    void shouldThrowExceptionForEmptyUrl() {
        assertThrows(
                InvalidLinkedInUrlException.class,
                () -> urlUtil.extractUsername("")
        );
    }

    @Test
    void shouldThrowExceptionForNullUrl() {
        assertThrows(
                InvalidLinkedInUrlException.class,
                () -> urlUtil.extractUsername(null)
        );
    }

    @Test
    void shouldThrowExceptionForHttpUrl() {
        assertThrows(
                InvalidLinkedInUrlException.class,
                () -> urlUtil.extractUsername("http://www.linkedin.com/in/john-doe/")
        );
    }

    @Test
    void shouldThrowExceptionForInvalidHost() {
        assertThrows(
                InvalidLinkedInUrlException.class,
                () -> urlUtil.extractUsername("https://example.com/in/john-doe/")
        );
    }

    @Test
    void shouldThrowExceptionForMissingInPath() {
        assertThrows(
                InvalidLinkedInUrlException.class,
                () -> urlUtil.extractUsername("https://www.linkedin.com/profile/john-doe/")
        );
    }

    @Test
    void shouldThrowExceptionForExtraPathSegments() {
        assertThrows(
                InvalidLinkedInUrlException.class,
                () -> urlUtil.extractUsername("https://www.linkedin.com/in/john-doe/details/")
        );
    }

    @Test
    void shouldThrowExceptionForInvalidUrlFormat() {
        assertThrows(
                InvalidLinkedInUrlException.class,
                () -> urlUtil.extractUsername("not-a-url")
        );
    }

    @Test
    void shouldHandleUrlWithQueryParameters() {
        // Query parameters are valid - should extract username successfully
        String username = urlUtil.extractUsername("https://www.linkedin.com/in/john-doe/?param=value");
        assertEquals("john-doe", username);
    }

    @Test
    void shouldExtractUsernameWithHyphens() {
        String username = urlUtil.extractUsername("https://www.linkedin.com/in/john-doe-smith-123/");
        assertEquals("john-doe-smith-123", username);
    }

    @Test
    void shouldExtractUsernameWithNumbers() {
        String username = urlUtil.extractUsername("https://www.linkedin.com/in/john123/");
        assertEquals("john123", username);
    }
}
