package com.WhenInRogue.NextSteps.controllers;

import com.WhenInRogue.NextSteps.dtos.QuestionDTO;
import com.WhenInRogue.NextSteps.dtos.Response;
import com.WhenInRogue.NextSteps.services.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/api/tests/{testId}/addquestion")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> createQuestion(@PathVariable Long testId,
                                                   @RequestBody @Valid QuestionDTO questionDTO) {
        return ResponseEntity.ok(questionService.createQuestion(testId, questionDTO));
    }

    @GetMapping("/api/tests/{testId}/questions")
    public ResponseEntity<Response> getQuestionsByTest(@PathVariable Long testId) {
        return ResponseEntity.ok(questionService.getQuestionsByTest(testId));
    }

    @GetMapping("/api/questions/{id}")
    public ResponseEntity<Response> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    @PutMapping("/api/questions/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> updateQuestion(@PathVariable Long id,
                                                   @RequestBody @Valid QuestionDTO questionDTO) {
        return ResponseEntity.ok(questionService.updateQuestion(id, questionDTO));
    }

    @DeleteMapping("/api/questions/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> deleteQuestion(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.deleteQuestion(id));
    }
}
