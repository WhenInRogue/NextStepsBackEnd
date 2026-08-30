package com.WhenInRogue.NextSteps.services;

import com.WhenInRogue.NextSteps.dtos.QuestionDTO;
import com.WhenInRogue.NextSteps.dtos.Response;

public interface QuestionService {

    Response createQuestion(Long testId, QuestionDTO questionDTO);

    Response getQuestionsByTest(Long testId);

    Response getQuestionById(Long id);

    Response updateQuestion(Long id, QuestionDTO questionDTO);

    Response deleteQuestion(Long id);
}
