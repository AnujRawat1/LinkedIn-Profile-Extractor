package com.anuj.LinkedinProfileExtractor.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class HttpClientConfig {

    private final LinkedInProperties properties;

    @Bean
    public RestClient restClient() {

        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }
}