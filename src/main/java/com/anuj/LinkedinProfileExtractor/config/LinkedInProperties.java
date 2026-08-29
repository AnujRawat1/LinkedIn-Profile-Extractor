package com.anuj.LinkedinProfileExtractor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "linkedin")
public class LinkedInProperties {

    // Voyager API configuration
    private String voyagerBaseUrl = "https://www.linkedin.com";
    private String profileDecorationId = "com.linkedin.voyager.dash.deco.identity.profile.FullProfileWithEntities-93";

}