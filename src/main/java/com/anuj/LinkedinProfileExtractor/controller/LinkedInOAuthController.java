package com.anuj.LinkedinProfileExtractor.controller;

import com.anuj.LinkedinProfileExtractor.config.LinkedInProperties;
import com.anuj.LinkedinProfileExtractor.model.LinkedInTokenResponse;
import com.anuj.LinkedinProfileExtractor.service.LinkedInOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth/linkedin")
@RequiredArgsConstructor
public class LinkedInOAuthController {

    private final LinkedInProperties properties;
    private final LinkedInOAuthService linkedInOAuthService;

    @GetMapping
    public RedirectView authenticate() {

        String state = UUID.randomUUID().toString();

        String authorizationUrl =
                properties.getAuthorizationUrl()
                        + "?response_type=code"
                        + "&client_id=" + encode(properties.getClientId())
                        + "&redirect_uri=" + encode(properties.getRedirectUri())
                        + "&state=" + encode(state)
                        + "&scope=" + encode("openid profile email");

        return new RedirectView(authorizationUrl);
    }

    @GetMapping("/callback")
    public String callback(
            @RequestParam String code,
            @RequestParam String state) {

        LinkedInTokenResponse tokenResponse = linkedInOAuthService.exchangeCodeForAccessToken(code);

        return linkedInOAuthService.fetchProfile(
                tokenResponse.getAccessToken()
        );
    }

    private String encode(String value) {
        return URLEncoder.encode(
                value,
                StandardCharsets.UTF_8
        );
    }
}