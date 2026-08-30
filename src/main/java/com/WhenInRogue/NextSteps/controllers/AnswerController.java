package com.WhenInRogue.NextSteps.controllers;

import com.WhenInRogue.NextSteps.dtos.AnswerDTO;
import com.WhenInRogue.NextSteps.dtos.Response;
import com.WhenInRogue.NextSteps.services.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping("/api/test-results/{testResultId}/answer")
    public ResponseEntity<Response> saveAnswer(@PathVariable Long testResultId,
                                               @RequestBody @Valid AnswerDTO answerDTO) {
        return ResponseEntity.ok(answerService.saveAnswer(testResultId, answerDTO));
    }

    @GetMapping("/api/test-results/{testResultId}/answers")
    public ResponseEntity<Response> getAnswersByTestResult(@PathVariable Long testResultId) {
        return ResponseEntity.ok(answerService.getAnswersByTestResult(testResultId));
    }
}
