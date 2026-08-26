package com.WhenInRogue.NextSteps.controllers;

import com.WhenInRogue.NextSteps.dtos.Response;
import com.WhenInRogue.NextSteps.dtos.TestDTO;
import com.WhenInRogue.NextSteps.services.TestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> createTest(@RequestBody @Valid TestDTO testDTO){
        return ResponseEntity.ok(testService.createTest(testDTO));
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllTests(){
        return ResponseEntity.ok(testService.getAllTests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getTestById(@PathVariable Long id){
        return ResponseEntity.ok(testService.getTestById(id));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> updateTest(@PathVariable Long id, @RequestBody @Valid TestDTO testDTO){
        return ResponseEntity.ok(testService.updateTest(id, testDTO));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> deleteTest(@PathVariable Long id){
        return ResponseEntity.ok(testService.deleteTest(id));
    }
}
