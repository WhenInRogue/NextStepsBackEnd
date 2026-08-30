package com.WhenInRogue.NextSteps.services;

import com.WhenInRogue.NextSteps.dtos.Response;

public interface CategoryScoreService {

    Response getScoresByTestResult(Long testResultId);

    Response getScoresByUser(Long userId);
}
