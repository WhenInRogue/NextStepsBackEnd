package com.WhenInRogue.NextSteps.services.impl;

import com.WhenInRogue.NextSteps.dtos.QuestionDTO;
import com.WhenInRogue.NextSteps.dtos.Response;
import com.WhenInRogue.NextSteps.dtos.TestDTO;
import com.WhenInRogue.NextSteps.dtos.TestResultDTO;
import com.WhenInRogue.NextSteps.exceptions.ForbiddenException;
import com.WhenInRogue.NextSteps.exceptions.NotFoundException;
import com.WhenInRogue.NextSteps.models.Answer;
import com.WhenInRogue.NextSteps.models.Category;
import com.WhenInRogue.NextSteps.models.CategoryScore;
import com.WhenInRogue.NextSteps.models.Question;
import com.WhenInRogue.NextSteps.models.Test;
import com.WhenInRogue.NextSteps.models.TestResult;
import com.WhenInRogue.NextSteps.models.User;
import com.WhenInRogue.NextSteps.repositories.AnswerRepository;
import com.WhenInRogue.NextSteps.repositories.QuestionRepository;
import com.WhenInRogue.NextSteps.repositories.TestRepository;
import com.WhenInRogue.NextSteps.repositories.TestResultRepository;
import com.WhenInRogue.NextSteps.services.TestService;
import com.WhenInRogue.NextSteps.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestServiceImpl implements TestService {

    private static final int MAX_POINTS_PER_QUESTION = 4;

    private final ModelMapper modelMapper;
    private final TestRepository testRepository;
    private final TestResultRepository testResultRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserService userService;

    @Override
    public Response createTest(TestDTO testDTO) {

        Test testToSave = modelMapper.map(testDTO, Test.class);

        testRepository.save(testToSave);

        return Response.builder()
                .status(200)
                .message("Test Created Successfully")
                .build();
    }

    @Override
    public Response getAllTests() {

        List<Test> tests = testRepository.findAll(Sort.by(Sort.Direction.DESC, "testId"));

        List<TestDTO> TestDTOList = modelMapper.map(tests, new TypeToken<List<TestDTO>>() {
        }.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .tests(TestDTOList)
                .build();
    }

    @Override
    public Response getTestById(Long id) {

        Test test = testRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Test Not Found"));

        TestDTO testDTO = modelMapper.map(test, TestDTO.class);

        return Response.builder()
                .status(200)
                .message("success")
                .test(testDTO)
                .build();
    }

    @Override
    public Response updateTest(Long id, TestDTO testDTO) {

        Test existingTest = testRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Test Not Found"));

        //need to create check that name is unique
        if (testDTO.getName() != null) existingTest.setName(testDTO.getName());
        if (testDTO.getDescription() != null) existingTest.setDescription(testDTO.getDescription());

        testRepository.save(existingTest);

        return Response.builder()
                .status(200)
                .message("Test Updated Successfully")
                .build();
    }

    @Override
    @Transactional
    public Response takeTest(Long testId) {
        User currentUser = userService.getCurrentLoggedInUser();
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new NotFoundException("Test Not Found"));

        List<Question> questions = questionRepository.findByTestOrderByQuestionNumberAsc(test);
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("This test has no questions yet");
        }

        TestResult testResult = testResultRepository
                .findFirstByUserAndTestAndCompleteFalseOrderByTestResultIdDesc(currentUser, test)
                .orElseGet(() -> testResultRepository.save(TestResult.builder()
                        .user(currentUser)
                        .test(test)
                        .complete(false)
                        .build()));

        TestResultDTO testResultDTO = modelMapper.map(testResult, TestResultDTO.class);
        testResultDTO.setAnswers(null);
        testResultDTO.setCategoryScores(null);

        List<QuestionDTO> questionDTOs = modelMapper.map(questions, new TypeToken<List<QuestionDTO>>() {
        }.getType());

        return Response.builder()
                .status(200)
                .message("Test started successfully")
                .testResult(testResultDTO)
                .questions(questionDTOs)
                .build();
    }

    @Override
    @Transactional
    public Response submitTest(Long testResultId) {
        TestResult testResult = testResultRepository.findById(testResultId)
                .orElseThrow(() -> new NotFoundException("Test Result Not Found"));

        User currentUser = userService.getCurrentLoggedInUser();
        requireOwner(currentUser, testResult);

        if (testResult.isComplete()) {
            throw new IllegalArgumentException("This test has already been submitted");
        }

        List<Question> questions = questionRepository.findByTestOrderByQuestionNumberAsc(testResult.getTest());
        List<Answer> answers = answerRepository.findByTestResultWithQuestionAndCategory(testResult);

        Set<Long> answeredQuestionIds = answers.stream()
                .map(answer -> answer.getQuestion().getQuestionId())
                .collect(Collectors.toSet());

        List<Integer> missingNumbers = questions.stream()
                .filter(question -> !answeredQuestionIds.contains(question.getQuestionId()))
                .map(Question::getQuestionNumber)
                .toList();

        if (!missingNumbers.isEmpty()) {
            throw new IllegalArgumentException("All questions must be answered before submitting. Missing: " + missingNumbers);
        }

        Map<Long, Category> categoriesById = new HashMap<>();
        Map<Long, Integer> totalsByCategory = new HashMap<>();
        Map<Long, Integer> questionCountsByCategory = new HashMap<>();

        for (Answer answer : answers) {
            Category category = answer.getQuestion().getCategory();
            Long categoryId = category.getCategoryId();
            categoriesById.put(categoryId, category);
            totalsByCategory.merge(categoryId, answer.getResponseValue(), Integer::sum);
            questionCountsByCategory.merge(categoryId, 1, Integer::sum);
        }

        testResult.getCategoryScores().clear();
        for (Long categoryId : categoriesById.keySet()) {
            int questionCount = questionCountsByCategory.get(categoryId);
            CategoryScore categoryScore = CategoryScore.builder()
                    .testResult(testResult)
                    .category(categoriesById.get(categoryId))
                    .totalRawPoints(totalsByCategory.get(categoryId))
                    .maxPoints(questionCount * MAX_POINTS_PER_QUESTION)
                    .build();
            testResult.getCategoryScores().add(categoryScore);
        }

        testResult.setComplete(true);
        testResult.setCompletedAt(LocalDateTime.now());
        testResultRepository.save(testResult);

        TestResultDTO testResultDTO = modelMapper.map(testResult, TestResultDTO.class);

        return Response.builder()
                .status(200)
                .message("Test submitted successfully")
                .testResult(testResultDTO)
                .categoryScores(testResultDTO.getCategoryScores())
                .build();
    }

    @Override
    public Response deleteTest(Long id) {
        testRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Test Not Found"));

        testRepository.deleteById(id);

        return Response.builder()
                .status(200)
                .message("Test Deleted Successfully")
                .build();
    }

    private void requireOwner(User currentUser, TestResult testResult) {
        if (currentUser.getId().equals(testResult.getUser().getId())) {
            return;
        }
        throw new ForbiddenException("You can only submit your own test");
    }
}
