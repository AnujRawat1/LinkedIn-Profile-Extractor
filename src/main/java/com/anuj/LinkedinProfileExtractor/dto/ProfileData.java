package com.anuj.LinkedinProfileExtractor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "LinkedIn profile data")
public class ProfileData {

    @Schema(description = "First name")
    private String firstName;

    @Schema(description = "Last name")
    private String lastName;

    @Schema(description = "Professional headline")
    private String headline;

    @Schema(description = "Location")
    private String location;

    @Schema(description = "About section")
    private String about;

    @Schema(description = "Profile picture URL")
    private String profilePictureUrl;

    @Schema(description = "Skills")
    private List<String> skills;

    @Schema(description = "Work experience")
    private List<Experience> experience;

    @Schema(description = "Education history")
    private List<Education> education;

    @Schema(description = "Certifications")
    private List<Certification> certifications;

    @Schema(description = "Languages")
    private List<Language> languages;

}