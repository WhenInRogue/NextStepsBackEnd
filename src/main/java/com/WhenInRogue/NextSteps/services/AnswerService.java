package com.WhenInRogue.NextSteps.services;

import com.WhenInRogue.NextSteps.dtos.AnswerDTO;
import com.WhenInRogue.NextSteps.dtos.Response;

public interface AnswerService {

    Response saveAnswer(Long testResultId, AnswerDTO answerDTO);

    Response getAnswersByTestResult(Long testResultId);
}
