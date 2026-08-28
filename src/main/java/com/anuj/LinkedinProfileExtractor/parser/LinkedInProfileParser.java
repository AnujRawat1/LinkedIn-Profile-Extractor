package com.anuj.LinkedinProfileExtractor.parser;

import com.anuj.LinkedinProfileExtractor.dto.Certification;
import com.anuj.LinkedinProfileExtractor.dto.Education;
import com.anuj.LinkedinProfileExtractor.dto.Experience;
import com.anuj.LinkedinProfileExtractor.dto.Language;
import com.anuj.LinkedinProfileExtractor.dto.ProfileData;
import com.anuj.LinkedinProfileExtractor.dto.ProfileImages;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LinkedInProfileParser implements ProfileParser {

    private final ObjectMapper objectMapper;

    @Override
    public ProfileData parse(
            String rawResponse,
            String profileUrl
    ) {

        try {
            JsonNode root = objectMapper.readTree(rawResponse);

            return ProfileData.builder()
                    .name(getText(root, "name"))
                    .headline(getText(root, "headline"))
                    .location(getText(root, "location"))
                    .about(getText(root, "about"))
                    .profileUrl(profileUrl)
                    .profileImages(parseImages(root))
                    .experience(parseExperience(root))
                    .education(parseEducation(root))
                    .skills(parseSkills(root))
                    .certifications(parseCertifications(root))
                    .languages(parseLanguages(root))
                    .build();

        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Unable to parse profile response",
                    e
            );
        }
    }

    private String getText(JsonNode root, String field) {

        JsonNode node = root.get(field);

        if (node == null || node.isNull()) {
            return null;
        }

        return node.asText();
    }

    private ProfileImages parseImages(JsonNode root) {

        JsonNode images = root.get("profileImages");

        if (images == null || images.isNull()) {
            return null;
        }

        return ProfileImages.builder()
                .profileImage(getText(images, "profileImage"))
                .backgroundImage(getText(images, "backgroundImage"))
                .build();
    }

    private List<Experience> parseExperience(JsonNode root) {

        List<Experience> experiences = new ArrayList<>();

        JsonNode experienceNode = root.get("experience");

        if (experienceNode == null || !experienceNode.isArray()) {
            return experiences;
        }

        for (JsonNode node : experienceNode) {

            experiences.add(
                    Experience.builder()
                            .title(getText(node, "title"))
                            .company(getText(node, "company"))
                            .location(getText(node, "location"))
                            .startDate(getText(node, "startDate"))
                            .endDate(getText(node, "endDate"))
                            .current(getBoolean(node, "current"))
                            .description(getText(node, "description"))
                            .build()
            );
        }

        return experiences;
    }

    private List<Education> parseEducation(JsonNode root) {

        List<Education> educationList = new ArrayList<>();

        JsonNode educationNode = root.get("education");

        if (educationNode == null || !educationNode.isArray()) {
            return educationList;
        }

        for (JsonNode node : educationNode) {

            educationList.add(
                    Education.builder()
                            .institution(getText(node, "institution"))
                            .degree(getText(node, "degree"))
                            .fieldOfStudy(getText(node, "fieldOfStudy"))
                            .startDate(getText(node, "startDate"))
                            .endDate(getText(node, "endDate"))
                            .description(getText(node, "description"))
                            .build()
            );
        }

        return educationList;
    }

    private List<String> parseSkills(JsonNode root) {

        List<String> skills = new ArrayList<>();

        JsonNode skillsNode = root.get("skills");

        if (skillsNode == null || !skillsNode.isArray()) {
            return skills;
        }

        for (JsonNode node : skillsNode) {
            if (!node.isNull()) {
                skills.add(node.asText());
            }
        }

        return skills;
    }

    private List<Certification> parseCertifications(JsonNode root) {

        List<Certification> certifications = new ArrayList<>();

        JsonNode certificationNode =
                root.get("certifications");

        if (certificationNode == null ||
                !certificationNode.isArray()) {

            return certifications;
        }

        for (JsonNode node : certificationNode) {

            certifications.add(
                    Certification.builder()
                            .name(getText(node, "name"))
                            .issuer(getText(node, "issuer"))
                            .issueDate(getText(node, "issueDate"))
                            .expirationDate(
                                    getText(node, "expirationDate")
                            )
                            .credentialId(
                                    getText(node, "credentialId")
                            )
                            .credentialUrl(
                                    getText(node, "credentialUrl")
                            )
                            .build()
            );
        }

        return certifications;
    }

    private List<Language> parseLanguages(JsonNode root) {

        List<Language> languages = new ArrayList<>();

        JsonNode languageNode = root.get("languages");

        if (languageNode == null || !languageNode.isArray()) {
            return languages;
        }

        for (JsonNode node : languageNode) {

            languages.add(
                    Language.builder()
                            .name(getText(node, "name"))
                            .proficiency(
                                    getText(node, "proficiency")
                            )
                            .build()
            );
        }

        return languages;
    }

    private boolean getBoolean(
            JsonNode root,
            String field
    ) {

        JsonNode node = root.get(field);

        return node != null && node.asBoolean(false);
    }
}