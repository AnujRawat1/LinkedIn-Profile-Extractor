package com.anuj.LinkedinProfileExtractor.parser;

import com.anuj.LinkedinProfileExtractor.dto.Certification;
import com.anuj.LinkedinProfileExtractor.dto.Education;
import com.anuj.LinkedinProfileExtractor.dto.Experience;
import com.anuj.LinkedinProfileExtractor.dto.Language;
import com.anuj.LinkedinProfileExtractor.dto.ProfileData;
import com.anuj.LinkedinProfileExtractor.exception.ProfileParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class LinkedInProfileParser implements ProfileParser {

    private final ObjectMapper objectMapper;

    @Override
    public ProfileData parse(
            String rawResponse,
            String profileUrl
    ) {

        try {
            JsonNode root = objectMapper.readTree(rawResponse);

            // Python implementation expects "elements" array at root level
            JsonNode elements = root.get("elements");
            if (elements == null || !elements.isArray() || elements.isEmpty()) {
                log.warn("No elements array found in Voyager response");
                return ProfileData.builder().build();
            }

            JsonNode profileNode = elements.get(0);

            // Extract base fields
            String firstName = getText(profileNode, "firstName");
            String lastName = getText(profileNode, "lastName");
            String headline = getText(profileNode, "headline");
            String about = getText(profileNode, "summary");
            String location = extractLocation(profileNode);
            String profilePictureUrl = extractProfilePicture(profileNode);

            // Extract Skills
            List<String> skills = extractSkills(profileNode);

            // Extract Experience
            List<Experience> experience = extractExperience(profileNode);

            // Extract Education
            List<Education> education = extractEducation(profileNode);

            // Extract Certifications
            List<Certification> certifications = extractCertifications(profileNode);

            return ProfileData.builder()
                    .firstName(firstName != null ? firstName : "")
                    .lastName(lastName != null ? lastName : "")
                    .headline(headline != null ? headline : "")
                    .location(location)
                    .about(about)
                    .profilePictureUrl(profilePictureUrl)
                    .skills(skills)
                    .experience(experience)
                    .education(education)
                    .certifications(certifications)
                    .languages(new ArrayList<>()) // Not extracted in Python version
                    .build();

        } catch (Exception e) {
            log.error("Error parsing Voyager profile response", e);
            throw new ProfileParseException("Unable to parse profile response", e);
        }
    }

    private String getText(JsonNode node, String field) {
        JsonNode fieldNode = node.get(field);
        if (fieldNode == null || fieldNode.isNull()) {
            return null;
        }
        return fieldNode.asText();
    }

    private String extractLocation(JsonNode profileNode) {
        JsonNode geoLocation = profileNode.get("geoLocation");
        if (geoLocation != null && !geoLocation.isNull()) {
            JsonNode geo = geoLocation.get("geo");
            if (geo != null && !geo.isNull()) {
                JsonNode defaultLocalizedName = geo.get("defaultLocalizedName");
                if (defaultLocalizedName != null && !defaultLocalizedName.isNull()) {
                    return defaultLocalizedName.asText();
                }
            }
        }
        return null;
    }

    private String extractProfilePicture(JsonNode profileNode) {
        try {
            JsonNode profilePicture = profileNode.get("profilePicture");
            if (profilePicture == null || profilePicture.isNull()) {
                return null;
            }

            JsonNode displayImageReference = profilePicture.get("displayImageReference");
            if (displayImageReference == null || displayImageReference.isNull()) {
                return null;
            }

            JsonNode vectorImage = displayImageReference.get("vectorImage");
            if (vectorImage == null || vectorImage.isNull()) {
                return null;
            }

            JsonNode rootUrl = vectorImage.get("rootUrl");
            JsonNode artifacts = vectorImage.get("artifacts");

            if (rootUrl != null && !rootUrl.isNull() && artifacts != null && artifacts.isArray() && artifacts.size() > 0) {
                // Usually the last artifact in the array is the highest resolution
                JsonNode lastArtifact = artifacts.get(artifacts.size() - 1);
                JsonNode fileIdentifyingUrlPathSegment = lastArtifact.get("fileIdentifyingUrlPathSegment");
                if (fileIdentifyingUrlPathSegment != null && !fileIdentifyingUrlPathSegment.isNull()) {
                    return rootUrl.asText() + fileIdentifyingUrlPathSegment.asText();
                }
            }
        } catch (Exception e) {
            log.debug("Failed to extract profile picture: {}", e.getMessage());
        }
        return null;
    }

    private List<String> extractSkills(JsonNode profileNode) {
        List<String> skills = new ArrayList<>();
        JsonNode profileSkills = profileNode.get("profileSkills");
        if (profileSkills != null && !profileSkills.isNull()) {
            JsonNode skillsElements = profileSkills.get("elements");
            if (skillsElements != null && skillsElements.isArray()) {
                for (JsonNode skill : skillsElements) {
                    String name = getText(skill, "name");
                    if (name != null && !name.isEmpty()) {
                        skills.add(name);
                    }
                }
            }
        }
        return skills;
    }

    private List<Experience> extractExperience(JsonNode profileNode) {
        List<Experience> experienceList = new ArrayList<>();
        JsonNode profilePositionGroups = profileNode.get("profilePositionGroups");
        if (profilePositionGroups != null && !profilePositionGroups.isNull()) {
            JsonNode expGroups = profilePositionGroups.get("elements");
            if (expGroups != null && expGroups.isArray()) {
                for (JsonNode group : expGroups) {
                    JsonNode profilePositionInPositionGroup = group.get("profilePositionInPositionGroup");
                    if (profilePositionInPositionGroup != null && !profilePositionInPositionGroup.isNull()) {
                        JsonNode positions = profilePositionInPositionGroup.get("elements");
                        if (positions != null && positions.isArray()) {
                            for (JsonNode pos : positions) {
                                experienceList.add(
                                        Experience.builder()
                                                .company(getText(pos, "companyName"))
                                                .title(getText(pos, "title"))
                                                .location(getText(pos, "locationName"))
                                                .build()
                                );
                            }
                        }
                    }
                }
            }
        }
        return experienceList;
    }

    private List<Education> extractEducation(JsonNode profileNode) {
        List<Education> educationList = new ArrayList<>();
        JsonNode profileEducations = profileNode.get("profileEducations");
        if (profileEducations != null && !profileEducations.isNull()) {
            JsonNode eduElements = profileEducations.get("elements");
            if (eduElements != null && eduElements.isArray()) {
                for (JsonNode edu : eduElements) {
                    educationList.add(
                            Education.builder()
                                    .school(getText(edu, "schoolName"))
                                    .degree(getText(edu, "degreeName"))
                                    .fieldOfStudy(getText(edu, "fieldOfStudy"))
                                    .build()
                    );
                }
            }
        }
        return educationList;
    }

    private List<Certification> extractCertifications(JsonNode profileNode) {
        List<Certification> certificationList = new ArrayList<>();
        JsonNode profileCertifications = profileNode.get("profileCertifications");
        if (profileCertifications != null && !profileCertifications.isNull()) {
            JsonNode certElements = profileCertifications.get("elements");
            if (certElements != null && certElements.isArray()) {
                for (JsonNode cert : certElements) {
                    certificationList.add(
                            Certification.builder()
                                    .name(getText(cert, "name"))
                                    .authority(getText(cert, "authority"))
                                    .licenseNumber(getText(cert, "licenseNumber"))
                                    .build()
                    );
                }
            }
        }
        return certificationList;
    }
}