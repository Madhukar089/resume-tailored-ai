package com.madhukar.resumetailoredai.controller;


import com.madhukar.resumetailoredai.dto.JobDescriptionRequest;
import com.madhukar.resumetailoredai.dto.ResumeAnalysisResponse;
import com.madhukar.resumetailoredai.service.GeminiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "http://localhost:5173")
public class ResumeAnalyzerController {

    private final GeminiService geminiService;

    public ResumeAnalyzerController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/analyze")
    public ResumeAnalysisResponse analyze(@RequestBody JobDescriptionRequest request) {

        return geminiService.analyzeJobDescription(request);
    }
}
