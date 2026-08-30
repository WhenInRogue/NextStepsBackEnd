package com.WhenInRogue.NextSteps.repositories;

import com.WhenInRogue.NextSteps.models.Answer;
import com.WhenInRogue.NextSteps.models.Question;
import com.WhenInRogue.NextSteps.models.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByTestResult(TestResult testResult);

    Optional<Answer> findByTestResultAndQuestion(TestResult testResult, Question question);

    @Query("SELECT a FROM Answer a JOIN FETCH a.question q JOIN FETCH q.category WHERE a.testResult = :testResult")
    List<Answer> findByTestResultWithQuestionAndCategory(@Param("testResult") TestResult testResult);
}
