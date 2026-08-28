package com.anuj.LinkedinProfileExtractor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Certification {

    private String name;
    private String issuer;
    private String issueDate;
    private String credentialUrl;

}