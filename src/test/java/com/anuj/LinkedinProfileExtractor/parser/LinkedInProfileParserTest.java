package com.anuj.LinkedinProfileExtractor.parser;

import com.anuj.LinkedinProfileExtractor.dto.ProfileData;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class LinkedInProfileParserTest {

    private final ProfileParser parser = new LinkedInProfileParser(new ObjectMapper());

    @Test
    void shouldParseVoyagerProfileWithBasicInfo() throws IOException {

        String json = """
                {
                  "included": [
                    {
                      "firstName": {
                        "localized": {
                          "en_US": "John"
                        }
                      },
                      "lastName": {
                        "localized": {
                          "en_US": "Doe"
                        }
                      },
                      "headline": {
                        "localized": {
                          "en_US": "Senior Software Engineer"
                        }
                      },
                      "address": {
                        "localized": {
                          "en_US": "Bengaluru, India"
                        }
                      },
                      "summary": {
                        "localized": {
                          "en_US": "Backend engineer"
                        }
                      },
                      "profilePicture": {
                        "displayImage": "https://example.com/profile.jpg"
                      }
                    }
                  ]
                }
                """;

        ProfileData result = parser.parse(
                json,
                "https://www.linkedin.com/in/john-doe/"
        );

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("Senior Software Engineer", result.getHeadline());
        assertEquals("Bengaluru, India", result.getLocation());
        assertEquals("Backend engineer", result.getAbout());
        assertEquals("https://example.com/profile.jpg", result.getProfileImage());
    }

    @Test
    void shouldParseVoyagerProfileWithExperience() throws IOException {

        String json = """
                {
                  "included": [
                    {
                      "firstName": {"localized": {"en_US": "John"}},
                      "lastName": {"localized": {"en_US": "Doe"}},
                      "$type": "com.linkedin.voyager.dash.identity.profile.Profile"
                    },
                    {
                      "$type": "com.linkedin.voyager.dash.identity.profile.Position",
                      "title": {"localized": {"en_US": "Senior Software Engineer"}},
                      "companyName": {"name": {"localized": {"en_US": "ABC Technologies"}}},
                      "location": {"localized": {"en_US": "Bengaluru"}},
                      "startDate": {"year": 2022, "month": 1},
                      "description": {"localized": {"en_US": "Building backend services."}}
                    }
                  ]
                }
                """;

        ProfileData result = parser.parse(
                json,
                "https://www.linkedin.com/in/john-doe/"
        );

        assertNotNull(result);
        assertNotNull(result.getExperience());
        assertEquals(1, result.getExperience().size());
        assertEquals("Senior Software Engineer", result.getExperience().get(0).getTitle());
        assertEquals("ABC Technologies", result.getExperience().get(0).getCompany());
        assertEquals("2022-01", result.getExperience().get(0).getStartDate());
        assertTrue(result.getExperience().get(0).isCurrent());
    }

    @Test
    void shouldParseVoyagerProfileWithEducation() throws IOException {

        String json = """
                {
                  "included": [
                    {
                      "firstName": {"localized": {"en_US": "John"}},
                      "lastName": {"localized": {"en_US": "Doe"}}
                    },
                    {
                      "$type": "com.linkedin.voyager.dash.identity.profile.Education",
                      "schoolName": {"name": {"localized": {"en_US": "ABC University"}}},
                      "degreeName": {"localized": {"en_US": "B.Tech"}},
                      "fieldOfStudy": {"localized": {"en_US": "Computer Science"}},
                      "startDate": {"year": 2016},
                      "endDate": {"year": 2020}
                    }
                  ]
                }
                """;

        ProfileData result = parser.parse(
                json,
                "https://www.linkedin.com/in/john-doe/"
        );

        assertNotNull(result);
        assertNotNull(result.getEducation());
        assertEquals(1, result.getEducation().size());
        assertEquals("ABC University", result.getEducation().get(0).getSchool());
        assertEquals("B.Tech", result.getEducation().get(0).getDegree());
        assertEquals("Computer Science", result.getEducation().get(0).getFieldOfStudy());
        assertEquals("2016", result.getEducation().get(0).getStartDate());
        assertEquals("2020", result.getEducation().get(0).getEndDate());
    }

    @Test
    void shouldHandleEmptyVoyagerResponse() throws IOException {

        String json = """
                {
                  "included": []
                }
                """;

        ProfileData result = parser.parse(
                json,
                "https://www.linkedin.com/in/john-doe/"
        );

        assertNotNull(result);
        assertNull(result.getName());
        assertNull(result.getHeadline());
    }

    @Test
    void shouldHandleMissingSectionsInVoyagerResponse() throws IOException {

        String json = """
                {
                  "included": [
                    {
                      "firstName": {"localized": {"en_US": "John"}},
                      "lastName": {"localized": {"en_US": "Doe"}}
                    }
                  ]
                }
                """;

        ProfileData result = parser.parse(
                json,
                "https://www.linkedin.com/in/john-doe/"
        );

        assertNotNull(result);
        assertNotNull(result.getExperience());
        assertTrue(result.getExperience().isEmpty());
        assertNotNull(result.getEducation());
        assertTrue(result.getEducation().isEmpty());
        assertNotNull(result.getSkills());
        assertTrue(result.getSkills().isEmpty());
    }
}