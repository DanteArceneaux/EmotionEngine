package org.TouchAPI.controllers;

import org.TouchAPI.exceptions.RateLimitException;
import org.TouchAPI.models.SentimentRequest;
import org.TouchAPI.models.SentimentResult;
import org.TouchAPI.services.SentimentAnalyzer;
import org.TouchAPI.services.rateLimiting.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@Validated
public class SentimentController {

    @Autowired
    private SentimentAnalyzer sentimentAnalyzer;

    @PostMapping("api/sentiment")
    public ResponseEntity<SentimentResult> analyzeSentiment(@Valid @RequestBody SentimentRequest sentimentRequest,
                                                            HttpServletRequest request) {
        try {
            SentimentResult sentimentResult = sentimentAnalyzer.analyzeSentiment(sentimentRequest.getText(), request);
            return ResponseEntity.ok(sentimentResult);
        } catch (RateLimitException |
                 RateLimiter.RateLimitExceededException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }
}
