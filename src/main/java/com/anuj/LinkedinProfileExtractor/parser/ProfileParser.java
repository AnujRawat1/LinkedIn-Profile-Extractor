package com.anuj.LinkedinProfileExtractor.parser;

import com.anuj.LinkedinProfileExtractor.dto.ProfileData;

public interface ProfileParser {

    ProfileData parse(String rawResponse, String profileUrl);

}