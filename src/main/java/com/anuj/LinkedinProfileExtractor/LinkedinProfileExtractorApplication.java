package com.anuj.LinkedinProfileExtractor;

import com.anuj.LinkedinProfileExtractor.config.LinkedInProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(LinkedInProperties.class)
public class LinkedinProfileExtractorApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinkedinProfileExtractorApplication.class, args);
	}

}
