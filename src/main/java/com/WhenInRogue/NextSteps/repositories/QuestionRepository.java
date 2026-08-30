package com.WhenInRogue.NextSteps.repositories;

import com.WhenInRogue.NextSteps.models.Category;
import com.WhenInRogue.NextSteps.models.Question;
import com.WhenInRogue.NextSteps.models.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByTestOrderByQuestionNumberAsc(Test test);

    List<Question> findByCategory(Category category);

    long countByTestAndCategory(Test test, Category category);

    boolean existsByTestAndQuestionNumber(Test test, Integer questionNumber);
}
