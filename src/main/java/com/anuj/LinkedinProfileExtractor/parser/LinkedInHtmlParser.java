package com.anuj.LinkedinProfileExtractor.parser;

import com.anuj.LinkedinProfileExtractor.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class LinkedInHtmlParser implements ProfileParser {

    @Override
    public ProfileData parse(String htmlContent, String profileUrl) {
        log.info("Parsing LinkedIn profile HTML from URL: {}", profileUrl);

        try {
            Document doc = Jsoup.parse(htmlContent);

            String name = extractName(doc);
            String headline = extractHeadline(doc);
            String location = extractLocation(doc);
            String about = extractAbout(doc);
            String profileImage = extractProfileImage(doc);

            List<Experience> experiences = extractExperiences(doc);
            List<Education> education = extractEducation(doc);
            List<String> skills = extractSkills(doc);
            List<Certification> certifications = extractCertifications(doc);
            List<Language> languages = extractLanguages(doc);

            log.info("Successfully parsed profile for: {}", name);

            return ProfileData.builder()
                    .firstName(name != null && name.contains(" ") ? name.split(" ")[0] : name)
                    .lastName(name != null && name.contains(" ") ? name.substring(name.indexOf(" ") + 1) : "")
                    .headline(headline)
                    .location(location)
                    .about(about)
                    .profilePictureUrl(profileImage)
                    .skills(skills)
                    .experience(experiences)
                    .education(education)
                    .certifications(certifications)
                    .languages(languages)
                    .build();
        } catch (Exception e) {
            log.error("Failed to parse LinkedIn profile HTML: {}", e.getMessage(), e);
            throw new com.anuj.LinkedinProfileExtractor.exception.ProfileParseException(
                    "Failed to parse profile HTML: " + e.getMessage(),
                    e
            );
        }
    }

    private String extractName(Document doc) {
        try {
            // Try multiple selectors for name
            Element nameElement = doc.selectFirst("h1.text-heading-xlarge");
            if (nameElement == null) {
                nameElement = doc.selectFirst("h1");
            }
            if (nameElement == null) {
                nameElement = doc.selectFirst("span[aria-hidden='true']");
            }
            return nameElement != null ? nameElement.text().trim() : "Unknown";
        } catch (Exception e) {
            log.warn("Could not extract name: {}", e.getMessage());
            return "Unknown";
        }
    }

    private String extractHeadline(Document doc) {
        try {
            // Try multiple selectors for headline
            Element headlineElement = doc.selectFirst("div.text-body-medium");
            if (headlineElement == null) {
                headlineElement = doc.selectFirst("div.pv-text-details__left-panel");
            }
            if (headlineElement == null) {
                headlineElement = doc.selectFirst("p.text-body-medium");
            }
            return headlineElement != null ? headlineElement.text().trim() : "";
        } catch (Exception e) {
            log.warn("Could not extract headline: {}", e.getMessage());
            return "";
        }
    }

    private String extractLocation(Document doc) {
        try {
            // Try multiple selectors for location
            Element locationElement = doc.selectFirst("div.text-body-small.inline-show-more-text--is-collapsed");
            if (locationElement == null) {
                locationElement = doc.selectFirst("span.text-body-small");
            }
            if (locationElement == null) {
                locationElement = doc.selectFirst("div.pv-text-details__right-panel");
            }
            return locationElement != null ? locationElement.text().trim() : "";
        } catch (Exception e) {
            log.warn("Could not extract location: {}", e.getMessage());
            return "";
        }
    }

    private String extractAbout(Document doc) {
        try {
            // Try multiple selectors for about
            Element aboutElement = doc.selectFirst("div.display-flex.ph5.pv3");
            if (aboutElement == null) {
                aboutElement = doc.selectFirst("div.pv-about__summary-text");
            }
            if (aboutElement == null) {
                aboutElement = doc.selectFirst("div.pv-about-section");
            }
            if (aboutElement == null) {
                aboutElement = doc.selectFirst("section[data-section='about']");
            }
            return aboutElement != null ? aboutElement.text().trim() : "";
        } catch (Exception e) {
            log.warn("Could not extract about: {}", e.getMessage());
            return "";
        }
    }

    private String extractProfileImage(Document doc) {
        try {
            // Try multiple selectors for profile image
            Element imgElement = doc.selectFirst("img.pv-top-card-profile-picture__image");
            if (imgElement == null) {
                imgElement = doc.selectFirst("img.profile-photo-edit__preview");
            }
            if (imgElement == null) {
                imgElement = doc.selectFirst("img[alt*='profile']");
            }
            return imgElement != null ? imgElement.attr("src") : "";
        } catch (Exception e) {
            log.warn("Could not extract profile image: {}", e.getMessage());
            return "";
        }
    }

    private List<Experience> extractExperiences(Document doc) {
        List<Experience> experiences = new ArrayList<>();
        try {
            // Try multiple selectors for experience section
            Elements experienceElements = doc.select("div.pvs-list__item--line-separated");
            if (experienceElements.isEmpty()) {
                experienceElements = doc.select("li[data-occludable='true']");
            }
            if (experienceElements.isEmpty()) {
                experienceElements = doc.select("div.pvs-entity__pvs-entity--with-path");
            }
            
            for (Element element : experienceElements) {
                try {
                    String title = element.select("span[aria-hidden='true']").text();
                    if (title.isEmpty()) {
                        title = element.select("div.t-16").text();
                    }
                    String company = element.select("span.t-14").text();
                    String duration = element.select("span.t-black--light").text();
                    
                    if (!title.isEmpty() && !title.equals("Experience")) {
                        experiences.add(Experience.builder()
                                .title(title)
                                .company(company)
                                .location(null)
                                .build());
                    }
                } catch (Exception e) {
                    log.warn("Could not extract individual experience: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("Could not extract experiences: {}", e.getMessage());
        }
        return experiences;
    }

    private List<Education> extractEducation(Document doc) {
        List<Education> education = new ArrayList<>();
        try {
            // Try multiple selectors for education section
            Elements educationElements = doc.select("div.pvs-list__item--line-separated");
            if (educationElements.isEmpty()) {
                educationElements = doc.select("li[data-occludable='true']");
            }
            if (educationElements.isEmpty()) {
                educationElements = doc.select("div.pvs-entity__pvs-entity--with-path");
            }
            
            for (Element element : educationElements) {
                try {
                    String school = element.select("span[aria-hidden='true']").text();
                    if (school.isEmpty()) {
                        school = element.select("div.t-16").text();
                    }
                    String degree = element.select("span.t-14").text();
                    String field = element.select("span.t-black--light").text();
                    
                    if (!school.isEmpty() && (school.toLowerCase().contains("university") || school.toLowerCase().contains("college") || school.toLowerCase().contains("institute") || school.toLowerCase().contains("school"))) {
                        education.add(Education.builder()
                                .school(school)
                                .degree(degree)
                                .fieldOfStudy(field)
                                .build());
                    }
                } catch (Exception e) {
                    log.warn("Could not extract individual education: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("Could not extract education: {}", e.getMessage());
        }
        return education;
    }

    private List<String> extractSkills(Document doc) {
        List<String> skills = new ArrayList<>();
        try {
            // Try multiple selectors for skills
            Elements skillElements = doc.select("span.pv-skill-category-entity__name");
            if (skillElements.isEmpty()) {
                skillElements = doc.select("span[aria-hidden='true']");
            }
            if (skillElements.isEmpty()) {
                skillElements = doc.select("div.pvs-entity__pvs-entity--with-path");
            }
            
            for (Element element : skillElements) {
                String skill = element.text().trim();
                if (!skill.isEmpty() && skill.length() < 50) {
                    skills.add(skill);
                }
            }
        } catch (Exception e) {
            log.warn("Could not extract skills: {}", e.getMessage());
        }
        return skills;
    }

    private List<Certification> extractCertifications(Document doc) {
        List<Certification> certifications = new ArrayList<>();
        try {
            // Try multiple selectors for certifications
            Elements certElements = doc.select("div.pvs-list__item--line-separated");
            if (certElements.isEmpty()) {
                certElements = doc.select("li[data-occludable='true']");
            }
            
            for (Element element : certElements) {
                try {
                    String name = element.select("span[aria-hidden='true']").text();
                    String issuer = element.select("span.t-14").text();
                    
                    if (!name.isEmpty() && (name.toLowerCase().contains("certif") || name.toLowerCase().contains("certificate"))) {
                        certifications.add(Certification.builder()
                                .name(name)
                                .authority(issuer)
                                .licenseNumber(null)
                                .build());
                    }
                } catch (Exception e) {
                    log.warn("Could not extract individual certification: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("Could not extract certifications: {}", e.getMessage());
        }
        return certifications;
    }

    private List<Language> extractLanguages(Document doc) {
        List<Language> languages = new ArrayList<>();
        try {
            // Try multiple selectors for languages
            Elements languageElements = doc.select("span.pv-text-details__right-panel-item-text");
            if (languageElements.isEmpty()) {
                languageElements = doc.select("span[aria-hidden='true']");
            }
            
            for (Element element : languageElements) {
                String language = element.text().trim();
                if (!language.isEmpty() && language.length() < 30) {
                    languages.add(new Language(language, "Fluent"));
                }
            }
        } catch (Exception e) {
            log.warn("Could not extract languages: {}", e.getMessage());
        }
        return languages;
    }
}
