package com.madhukar.resumetailoredai.dto;

import java.util.List;

public record ResumeAnalysisResponse(
        List<String> keywords,
        List<String> mustHaveSkills,
        List<String> niceToHaveSkills,
        List<String> resumeBullets,
        List<String> atsKeywords
) {
}