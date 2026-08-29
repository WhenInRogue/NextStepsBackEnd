package com.WhenInRogue.NextSteps.repositories;

import com.WhenInRogue.NextSteps.models.Answer;
import com.WhenInRogue.NextSteps.models.Question;
import com.WhenInRogue.NextSteps.models.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByTestResult(TestResult testResult);

    Optional<Answer> findByTestResultAndQuestion(TestResult testResult, Question question);
}
