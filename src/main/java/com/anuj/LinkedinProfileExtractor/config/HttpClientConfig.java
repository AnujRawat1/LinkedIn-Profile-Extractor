package com.anuj.LinkedinProfileExtractor.config;

import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class HttpClientConfig {

    private final LinkedInProperties properties;

    @Bean
    public RestClient voyagerRestClient() {
        HttpClient httpClient = HttpClientBuilder.create()
                .build();

        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return RestClient.builder()
                .baseUrl(properties.getVoyagerBaseUrl())
                .requestFactory(requestFactory)
                .build();
    }
}