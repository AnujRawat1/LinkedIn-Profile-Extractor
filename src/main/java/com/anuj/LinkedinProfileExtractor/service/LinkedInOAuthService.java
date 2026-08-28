package com.anuj.LinkedinProfileExtractor.service;

import com.anuj.LinkedinProfileExtractor.client.LinkedInHttpClient;
import com.anuj.LinkedinProfileExtractor.config.LinkedInProperties;
import com.anuj.LinkedinProfileExtractor.model.LinkedInTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class LinkedInOAuthService {

    private final RestClient restClient;
    private final LinkedInProperties properties;
    private final LinkedInHttpClient linkedInHttpClient;

    public String fetchProfile(String accessToken) {
        return linkedInHttpClient.fetchUserInfo(accessToken);
    }

    public LinkedInTokenResponse exchangeCodeForAccessToken(
            String code) {

        MultiValueMap<String, String> formData =
                new LinkedMultiValueMap<>();

        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add(
                "client_id",
                properties.getClientId()
        );
        formData.add(
                "client_secret",
                properties.getClientSecret()
        );
        formData.add(
                "redirect_uri",
                properties.getRedirectUri()
        );

        return restClient
                .post()
                .uri(properties.getTokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(LinkedInTokenResponse.class);
    }
}