package com.WhenInRogue.NextSteps.repositories;

import com.WhenInRogue.NextSteps.models.Test;
import com.WhenInRogue.NextSteps.models.TestResult;
import com.WhenInRogue.NextSteps.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TestResultRepository extends JpaRepository<TestResult, Long> {

    List<TestResult> findByUser(User user);

    List<TestResult> findByTest(Test test);

    List<TestResult> findByUserAndCompleteTrue(User user);

    Optional<TestResult> findFirstByUserAndTestOrderByTestResultIdDesc(User user, Test test);
}
