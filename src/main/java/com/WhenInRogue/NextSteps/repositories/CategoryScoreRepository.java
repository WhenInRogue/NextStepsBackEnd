package com.WhenInRogue.NextSteps.repositories;

import com.WhenInRogue.NextSteps.models.Category;
import com.WhenInRogue.NextSteps.models.CategoryScore;
import com.WhenInRogue.NextSteps.models.TestResult;
import com.WhenInRogue.NextSteps.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryScoreRepository extends JpaRepository<CategoryScore, Long> {

    List<CategoryScore> findByTestResult(TestResult testResult);

    Optional<CategoryScore> findByTestResultAndCategory(TestResult testResult, Category category);

    List<CategoryScore> findByTestResult_User(User user);

    List<CategoryScore> findByTestResult_User_Id(Long userId);
}
