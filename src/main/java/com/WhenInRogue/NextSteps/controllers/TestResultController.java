package com.WhenInRogue.NextSteps.controllers;

import com.WhenInRogue.NextSteps.dtos.Response;
import com.WhenInRogue.NextSteps.services.TestResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test-results")
@RequiredArgsConstructor
public class TestResultController {

    private final TestResultService testResultService;

    @GetMapping("/current")
    public ResponseEntity<Response> getCurrentUserTestResults() {
        return ResponseEntity.ok(testResultService.getCurrentUserTestResults());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Response> getTestResultsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(testResultService.getTestResultsByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getTestResultById(@PathVariable Long id) {
        return ResponseEntity.ok(testResultService.getTestResultById(id));
    }
}
