package com.anuj.LinkedinProfileExtractor.parser;

import com.anuj.LinkedinProfileExtractor.dto.Certification;
import com.anuj.LinkedinProfileExtractor.dto.Education;
import com.anuj.LinkedinProfileExtractor.dto.Experience;
import com.anuj.LinkedinProfileExtractor.dto.Language;
import com.anuj.LinkedinProfileExtractor.dto.ProfileData;
import com.anuj.LinkedinProfileExtractor.exception.ProfileParseException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
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

            // Voyager API uses REST.li pointer format with "included" array
            JsonNode included = root.get("included");
            JsonNode data = root.get("data");

            if (data == null && included != null && !included.isEmpty()) {
                // Profile data is in the first element of included array
                data = included.get(0);
            }

            if (data == null) {
                log.warn("No profile data found in Voyager response");
                return ProfileData.builder().build();
            }

            return ProfileData.builder()
                    .name(extractLocalizedText(data, "firstName") + " " + extractLocalizedText(data, "lastName"))
                    .headline(extractLocalizedText(data, "headline"))
                    .location(extractLocalizedText(data, "address"))
                    .about(extractLocalizedText(data, "summary"))
                    .profileImage(extractProfilePicture(data))
                    .experience(parseVoyagerExperience(included))
                    .education(parseVoyagerEducation(included))
                    .skills(parseVoyagerSkills(included))
                    .certifications(parseVoyagerCertifications(included))
                    .languages(parseVoyagerLanguages(included))
                    .build();

        } catch (Exception e) {
            log.error("Error parsing Voyager profile response", e);
            throw new ProfileParseException("Unable to parse profile response", e);
        }
    }

    /**
     * Extract localized text from Voyager API response
     * Voyager uses localized format: { "localized": { "en_US": "text" }, "preferredLocale": {...} }
     */
    private String extractLocalizedText(JsonNode node, String field) {
        JsonNode fieldNode = node.get(field);
        if (fieldNode == null || fieldNode.isNull()) {
            return null;
        }

        // Check for localized format
        JsonNode localized = fieldNode.get("localized");
        if (localized != null && !localized.isEmpty()) {
            // Try common locales
            String[] commonLocales = {"en_US", "en", "default"};
            for (String locale : commonLocales) {
                JsonNode localeNode = localized.get(locale);
                if (localeNode != null && !localeNode.isNull()) {
                    return localeNode.asText();
                }
            }
            // Fallback to first available key
            if (localized.size() > 0) {
                return localized.get(0).asText();
            }
        }

        // Fallback to direct text
        return fieldNode.asText();
    }

    /**
     * Extract profile picture URL from Voyager response
     */
    private String extractProfilePicture(JsonNode data) {
        JsonNode profilePicture = data.get("profilePicture");
        if (profilePicture == null || profilePicture.isNull()) {
            return null;
        }

        JsonNode displayImage = profilePicture.get("displayImage");
        if (displayImage != null && !displayImage.isNull()) {
            return displayImage.asText();
        }

        return null;
    }

    /**
     * Parse experience from Voyager API included array
     */
    private List<Experience> parseVoyagerExperience(JsonNode included) {
        List<Experience> experiences = new ArrayList<>();

        if (included == null || !included.isArray()) {
            return experiences;
        }

        for (JsonNode item : included) {
            String type = getText(item, "$type");
            if (type != null && type.contains("Position")) {
                experiences.add(
                        Experience.builder()
                                .title(extractLocalizedText(item, "title"))
                                .company(extractCompanyName(item))
                                .companyUrl(extractCompanyUrl(item))
                                .location(extractLocalizedText(item, "location"))
                                .startDate(extractDate(item, "startDate"))
                                .endDate(extractDate(item, "endDate"))
                                .current(isCurrentPosition(item))
                                .description(extractLocalizedText(item, "description"))
                                .build()
                );
            }
        }

        return experiences;
    }

    private String extractCompanyName(JsonNode positionNode) {
        JsonNode companyName = positionNode.get("companyName");
        if (companyName != null && !companyName.isNull()) {
            return extractLocalizedText(companyName, "name");
        }
        return null;
    }

    private String extractCompanyUrl(JsonNode positionNode) {
        JsonNode companyUrn = positionNode.get("company");
        if (companyUrn != null && !companyUrn.isNull()) {
            // Convert URN to URL format
            String urn = companyUrn.asText();
            if (urn.startsWith("urn:li:company:")) {
                String companyId = urn.substring("urn:li:company:".length());
                return "https://www.linkedin.com/company/" + companyId;
            }
        }
        return null;
    }

    private boolean isCurrentPosition(JsonNode positionNode) {
        JsonNode endDate = positionNode.get("endDate");
        return endDate == null || endDate.isNull();
    }

    /**
     * Parse education from Voyager API included array
     */
    private List<Education> parseVoyagerEducation(JsonNode included) {
        List<Education> educationList = new ArrayList<>();

        if (included == null || !included.isArray()) {
            return educationList;
        }

        for (JsonNode item : included) {
            String type = getText(item, "$type");
            if (type != null && type.contains("Education")) {
                educationList.add(
                        Education.builder()
                                .school(extractSchoolName(item))
                                .degree(extractLocalizedText(item, "degreeName"))
                                .fieldOfStudy(extractLocalizedText(item, "fieldOfStudy"))
                                .startDate(extractDate(item, "startDate"))
                                .endDate(extractDate(item, "endDate"))
                                .build()
                );
            }
        }

        return educationList;
    }

    private String extractSchoolName(JsonNode educationNode) {
        JsonNode schoolName = educationNode.get("schoolName");
        if (schoolName != null && !schoolName.isNull()) {
            return extractLocalizedText(schoolName, "name");
        }
        return null;
    }

    /**
     * Parse skills from Voyager API included array
     */
    private List<String> parseVoyagerSkills(JsonNode included) {
        List<String> skills = new ArrayList<>();

        if (included == null || !included.isArray()) {
            return skills;
        }

        for (JsonNode item : included) {
            String type = getText(item, "$type");
            if (type != null && type.contains("Skill")) {
                String skillName = extractLocalizedText(item, "name");
                if (skillName != null && !skillName.isEmpty()) {
                    skills.add(skillName);
                }
            }
        }

        return skills;
    }

    /**
     * Parse certifications from Voyager API included array
     */
    private List<Certification> parseVoyagerCertifications(JsonNode included) {
        List<Certification> certifications = new ArrayList<>();

        if (included == null || !included.isArray()) {
            return certifications;
        }

        for (JsonNode item : included) {
            String type = getText(item, "$type");
            if (type != null && type.contains("Certification")) {
                certifications.add(
                        Certification.builder()
                                .name(extractLocalizedText(item, "name"))
                                .issuer(extractLocalizedText(item, "authority"))
                                .issueDate(extractDate(item, "issueDate"))
                                .credentialUrl(extractCredentialUrl(item))
                                .build()
                );
            }
        }

        return certifications;
    }

    private String extractCredentialUrl(JsonNode certificationNode) {
        JsonNode url = certificationNode.get("url");
        if (url != null && !url.isNull()) {
            return url.asText();
        }
        return null;
    }

    /**
     * Parse languages from Voyager API included array
     */
    private List<Language> parseVoyagerLanguages(JsonNode included) {
        List<Language> languages = new ArrayList<>();

        if (included == null || !included.isArray()) {
            return languages;
        }

        for (JsonNode item : included) {
            String type = getText(item, "$type");
            if (type != null && type.contains("Language")) {
                languages.add(
                        Language.builder()
                                .name(extractLocalizedText(item, "name"))
                                .proficiency(extractLocalizedText(item, "proficiency"))
                                .build()
                );
            }
        }

        return languages;
    }

    /**
     * Extract date from Voyager date format
     * Voyager uses: { "year": 2020, "month": 1 }
     */
    private String extractDate(JsonNode node, String field) {
        JsonNode dateNode = node.get(field);
        if (dateNode == null || dateNode.isNull()) {
            return null;
        }

        JsonNode year = dateNode.get("year");
        JsonNode month = dateNode.get("month");

        if (year != null && !year.isNull()) {
            if (month != null && !month.isNull()) {
                return year.asText() + "-" + String.format("%02d", month.asInt());
            }
            return year.asText();
        }

        return null;
    }

    private String getText(JsonNode root, String field) {
        JsonNode node = root.get(field);
        if (node == null || node.isNull()) {
            return null;
        }
        return node.asText();
    }
}