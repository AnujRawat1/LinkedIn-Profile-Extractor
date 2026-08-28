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
public class Experience {

    private String title;
    private String company;
    private String companyUrl;
    private String location;
    private String startDate;
    private String endDate;
    private boolean current;
    private String description;

}