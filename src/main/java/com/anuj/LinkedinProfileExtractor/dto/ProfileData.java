package com.anuj.LinkedinProfileExtractor.dto;

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
public class ProfileData {

    private String name;
    private String headline;
    private String location;
    private String about;
    private String profileUrl;

    private ProfileImages profileImages;

    private List<Experience> experience;
    private List<Education> education;
    private List<String> skills;
    private List<Certification> certifications;
    private List<Language> languages;

}