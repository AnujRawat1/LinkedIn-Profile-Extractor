package com.anuj.LinkedinProfileExtractor.parser;

import com.anuj.LinkedinProfileExtractor.dto.ProfileData;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class LinkedInProfileParserTest {

    private final ProfileParser parser = new LinkedInProfileParser(new ObjectMapper());

    @Test
    void shouldParseBasicProfileInformation()
            throws IOException {

        String json = """
                {
                  "name": "John Doe",
                  "headline": "Senior Software Engineer",
                  "location": "Bengaluru, India",
                  "about": "Backend engineer"
                }
                """;

        ProfileData result =
                parser.parse(
                        json,
                        "https://www.linkedin.com/in/john-doe/"
                );

        assertEquals(
                "John Doe",
                result.getName()
        );

        assertEquals(
                "Senior Software Engineer",
                result.getHeadline()
        );

        assertEquals(
                "Bengaluru, India",
                result.getLocation()
        );

        assertEquals(
                "Backend engineer",
                result.getAbout()
        );
    }

    @Test
    void shouldParseCompleteProfile() throws IOException {

        String json = """
            {
              "name": "John Doe",
              "headline": "Senior Software Engineer",
              "location": "Bengaluru, India",
              "about": "Backend engineer",
              "profileImages": {
                "profileImage": "https://example.com/profile.jpg",
                "backgroundImage": "https://example.com/background.jpg"
              },
              "experience": [
                {
                  "title": "Senior Software Engineer",
                  "company": "ABC Technologies",
                  "location": "Bengaluru",
                  "startDate": "2022-01",
                  "endDate": null,
                  "current": true,
                  "description": "Building backend services."
                }
              ],
              "education": [
                {
                  "institution": "ABC University",
                  "degree": "B.Tech",
                  "fieldOfStudy": "Computer Science",
                  "startDate": "2016",
                  "endDate": "2020"
                }
              ],
              "skills": [
                "Java",
                "Spring Boot"
              ],
              "certifications": [
                {
                  "name": "AWS Certified Developer",
                  "issuer": "AWS"
                }
              ],
              "languages": [
                {
                  "name": "English",
                  "proficiency": "Professional Working"
                }
              ]
            }
            """;

        ProfileData result = parser.parse(
                json,
                "https://www.linkedin.com/in/john-doe/"
        );

        assertEquals("John Doe", result.getName());
        assertEquals("Senior Software Engineer", result.getHeadline());

        assertNotNull(result.getProfileImages());
        assertEquals(
                "https://example.com/profile.jpg",
                result.getProfileImages().getProfileImage()
        );

        assertEquals(1, result.getExperience().size());
        assertEquals(
                "ABC Technologies",
                result.getExperience().get(0).getCompany()
        );

        assertEquals(1, result.getEducation().size());

        assertEquals(2, result.getSkills().size());
        assertTrue(result.getSkills().contains("Java"));

        assertEquals(1, result.getCertifications().size());

        assertEquals(1, result.getLanguages().size());
        assertEquals(
                "English",
                result.getLanguages().get(0).getName()
        );
    }
}