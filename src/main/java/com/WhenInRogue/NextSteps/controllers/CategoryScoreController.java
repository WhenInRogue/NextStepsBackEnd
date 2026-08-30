package com.WhenInRogue.NextSteps.controllers;

import com.WhenInRogue.NextSteps.dtos.Response;
import com.WhenInRogue.NextSteps.services.CategoryScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryScoreController {

    private final CategoryScoreService categoryScoreService;

    @GetMapping("/api/test-results/{testResultId}/scores")
    public ResponseEntity<Response> getScoresByTestResult(@PathVariable Long testResultId) {
        return ResponseEntity.ok(categoryScoreService.getScoresByTestResult(testResultId));
    }

    @GetMapping("/api/users/{userId}/category-scores")
    public ResponseEntity<Response> getScoresByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(categoryScoreService.getScoresByUser(userId));
    }
}
