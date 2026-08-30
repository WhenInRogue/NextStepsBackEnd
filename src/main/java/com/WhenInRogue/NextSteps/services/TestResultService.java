package com.WhenInRogue.NextSteps.services;

import com.WhenInRogue.NextSteps.dtos.Response;

public interface TestResultService {

    Response getTestResultById(Long testResultId);

    Response getTestResultsByUser(Long userId);

    Response getCurrentUserTestResults();
}
