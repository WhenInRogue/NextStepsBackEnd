package com.WhenInRogue.NextSteps.services.impl;

import com.WhenInRogue.NextSteps.dtos.AnswerDTO;
import com.WhenInRogue.NextSteps.dtos.Response;
import com.WhenInRogue.NextSteps.enums.UserRole;
import com.WhenInRogue.NextSteps.exceptions.ForbiddenException;
import com.WhenInRogue.NextSteps.exceptions.NameValueRequiredException;
import com.WhenInRogue.NextSteps.exceptions.NotFoundException;
import com.WhenInRogue.NextSteps.models.Answer;
import com.WhenInRogue.NextSteps.models.Question;
import com.WhenInRogue.NextSteps.models.TestResult;
import com.WhenInRogue.NextSteps.models.User;
import com.WhenInRogue.NextSteps.repositories.AnswerRepository;
import com.WhenInRogue.NextSteps.repositories.QuestionRepository;
import com.WhenInRogue.NextSteps.repositories.TestResultRepository;
import com.WhenInRogue.NextSteps.services.AnswerService;
import com.WhenInRogue.NextSteps.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnswerServiceImpl implements AnswerService {

    private static final int MIN_RESPONSE = 0;
    private static final int MAX_RESPONSE = 4;

    private final AnswerRepository answerRepository;
    private final TestResultRepository testResultRepository;
    private final QuestionRepository questionRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Response saveAnswer(Long testResultId, AnswerDTO answerDTO) {
        TestResult testResult = findTestResult(testResultId);
        User currentUser = userService.getCurrentLoggedInUser();
        requireOwner(currentUser, testResult);

        if (testResult.isComplete()) {
            throw new IllegalArgumentException("This test has already been submitted");
        }

        Long questionId = resolveQuestionId(answerDTO);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Question Not Found"));

        if (!question.getTest().getTestId().equals(testResult.getTest().getTestId())) {
            throw new IllegalArgumentException("Question does not belong to this test");
        }

        Integer responseValue = answerDTO.getResponseValue();
        if (responseValue == null) {
            throw new NameValueRequiredException("Response value is required");
        }
        if (responseValue < MIN_RESPONSE || responseValue > MAX_RESPONSE) {
            throw new IllegalArgumentException("Response value must be between 0 and 4");
        }

        Answer answer = answerRepository.findByTestResultAndQuestion(testResult, question)
                .orElse(Answer.builder()
                        .testResult(testResult)
                        .question(question)
                        .build());
        answer.setResponseValue(responseValue);
        answerRepository.save(answer);

        return Response.builder()
                .status(200)
                .message("Answer Saved Successfully")
                .answer(modelMapper.map(answer, AnswerDTO.class))
                .build();
    }

    @Override
    public Response getAnswersByTestResult(Long testResultId) {
        TestResult testResult = findTestResult(testResultId);
        User currentUser = userService.getCurrentLoggedInUser();
        requireCanViewTestResult(currentUser, testResult);

        List<Answer> answers = answerRepository.findByTestResult(testResult);
        List<AnswerDTO> answerDTOs = modelMapper.map(answers, new TypeToken<List<AnswerDTO>>() {
        }.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .answers(answerDTOs)
                .build();
    }

    private TestResult findTestResult(Long testResultId) {
        return testResultRepository.findById(testResultId)
                .orElseThrow(() -> new NotFoundException("Test Result Not Found"));
    }

    private Long resolveQuestionId(AnswerDTO answerDTO) {
        if (answerDTO.getQuestionId() != null) {
            return answerDTO.getQuestionId();
        }
        if (answerDTO.getQuestion() != null && answerDTO.getQuestion().getQuestionId() != null) {
            return answerDTO.getQuestion().getQuestionId();
        }
        throw new NameValueRequiredException("Question id is required");
    }

    private void requireOwner(User currentUser, TestResult testResult) {
        if (currentUser.getId().equals(testResult.getUser().getId())) {
            return;
        }
        throw new ForbiddenException("You can only submit answers for your own test");
    }

    private void requireCanViewTestResult(User currentUser, TestResult testResult) {
        if (currentUser.getRole() == UserRole.ADMIN
                || currentUser.getRole() == UserRole.DREAM_TEAM_LEADER) {
            return;
        }
        if (currentUser.getId().equals(testResult.getUser().getId())) {
            return;
        }
        throw new ForbiddenException("You can only view your own test answers");
    }
}
