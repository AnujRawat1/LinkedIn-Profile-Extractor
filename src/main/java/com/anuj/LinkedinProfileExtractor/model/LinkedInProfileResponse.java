package com.anuj.LinkedinProfileExtractor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkedInProfileResponse {

    private String sub;

    private String name;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String familyName;

    private String picture;

    private String email;

    @JsonProperty("email_verified")
    private Boolean emailVerified;
}