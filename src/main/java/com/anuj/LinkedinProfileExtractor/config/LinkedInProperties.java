package com.anuj.LinkedinProfileExtractor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "linkedin")
public class LinkedInProperties {

    private String baseUrl;
    private String apiToken;

}