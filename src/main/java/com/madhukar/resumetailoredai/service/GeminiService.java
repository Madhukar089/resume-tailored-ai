package com.madhukar.resumetailoredai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Schema;
import com.google.genai.types.Type;
import com.madhukar.resumetailoredai.dto.JobDescriptionRequest;
import com.madhukar.resumetailoredai.dto.ResumeAnalysisResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);
    private final Client client;
    private final ObjectMapper objectMapper;
    
    public GeminiService() {
        this.client = new Client();
        this.objectMapper = new ObjectMapper();
    }

    public ResumeAnalysisResponse analyzeJobDescription(JobDescriptionRequest request) {
        log.debug("Entering");

        String prompt = """
                You are an expert technical recruiter and resume optimization assistant.

                Analyze the following Job Description and identify what a candidate should
                highlight in their resume when applying for this role.

                JOB DESCRIPTION:
                %s

                Your analysis must:

                1. Extract the important technical and professional keywords.
                2. Identify the most important must-have skills explicitly mentioned in the JD.
                3. Identify nice-to-have skills explicitly mentioned in the JD.
                4. Generate exactly 3 resume bullet suggestions based ONLY on the responsibilities,
                   technologies and requirements mentioned in the JD.
                5. Extract important ATS keywords.

                IMPORTANT RULES:
                - Do not invent technologies, responsibilities or candidate experience.
                - Do not claim that the candidate has experience with any technology.
                - Resume bullets must be written as suggestions that the candidate can use
                  IF they have actually performed that work.
                - Prefer strong action-oriented resume language.
                - Keep resume bullets concise and professional.
                - Avoid generic statements.
                - Do not include explanations outside the requested JSON structure.

                Return only the requested JSON structure.
                """.formatted(request.jobDescription());

        Schema responseSchema =
                Schema.builder()
                        .type(Type.Known.OBJECT)
                        .properties(
                                ImmutableMap.of(

                                        "keywords",
                                        Schema.builder()
                                                .type(Type.Known.ARRAY)
                                                .items(
                                                        Schema.builder()
                                                                .type(Type.Known.STRING)
                                                                .build()
                                                )
                                                .build(),

                                        "mustHaveSkills",
                                        Schema.builder()
                                                .type(Type.Known.ARRAY)
                                                .items(
                                                        Schema.builder()
                                                                .type(Type.Known.STRING)
                                                                .build()
                                                )
                                                .build(),

                                        "niceToHaveSkills",
                                        Schema.builder()
                                                .type(Type.Known.ARRAY)
                                                .items(
                                                        Schema.builder()
                                                                .type(Type.Known.STRING)
                                                                .build()
                                                )
                                                .build(),

                                        "resumeBullets",
                                        Schema.builder()
                                                .type(Type.Known.ARRAY)
                                                .items(
                                                        Schema.builder()
                                                                .type(Type.Known.STRING)
                                                                .build()
                                                )
                                                .build(),

                                        "atsKeywords",
                                        Schema.builder()
                                                .type(Type.Known.ARRAY)
                                                .items(
                                                        Schema.builder()
                                                                .type(Type.Known.STRING)
                                                                .build()
                                                )
                                                .build()
                                )
                        )
                        .required(List.of(
                                "keywords",
                                "mustHaveSkills",
                                "niceToHaveSkills",
                                "resumeBullets",
                                "atsKeywords"
                        ))
                        .build();

        GenerateContentConfig config =
                GenerateContentConfig.builder()
                        .responseMimeType("application/json")
                        .responseSchema(responseSchema)
                        .candidateCount(1)
                        .build();

        log.debug("Processing the Call");

        try {
            GenerateContentResponse response =
                    client.models.generateContent(
                            "gemini-3.6-flash",
                            prompt,
                            config
                    );

            log.debug("Leaving");
            return objectMapper.readValue(
                    response.text(),
                    ResumeAnalysisResponse.class
            );
        } catch (Exception e) {
            log.error("Failed to parse Gemini response", e);
            throw new RuntimeException("Failed to parse Gemini response", e);
        }
    }
}