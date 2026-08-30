package com.WhenInRogue.NextSteps.services.impl;

import com.WhenInRogue.NextSteps.dtos.QuestionDTO;
import com.WhenInRogue.NextSteps.dtos.Response;
import com.WhenInRogue.NextSteps.enums.UserRole;
import com.WhenInRogue.NextSteps.exceptions.ForbiddenException;
import com.WhenInRogue.NextSteps.exceptions.NameValueRequiredException;
import com.WhenInRogue.NextSteps.exceptions.NotFoundException;
import com.WhenInRogue.NextSteps.models.Category;
import com.WhenInRogue.NextSteps.models.Question;
import com.WhenInRogue.NextSteps.models.Test;
import com.WhenInRogue.NextSteps.models.User;
import com.WhenInRogue.NextSteps.repositories.CategoryRepository;
import com.WhenInRogue.NextSteps.repositories.QuestionRepository;
import com.WhenInRogue.NextSteps.repositories.TestRepository;
import com.WhenInRogue.NextSteps.services.QuestionService;
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
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Response createQuestion(Long testId, QuestionDTO questionDTO) {
        requireAdmin();
        Test test = findTest(testId);
        Question question = buildQuestion(test, questionDTO);
        questionRepository.save(question);

        return Response.builder()
                .status(200)
                .message("Question Created Successfully")
                .question(modelMapper.map(question, QuestionDTO.class))
                .build();
    }

    @Override
    public Response getQuestionsByTest(Long testId) {
        Test test = findTest(testId);
        List<Question> questions = questionRepository.findByTestOrderByQuestionNumberAsc(test);

        List<QuestionDTO> questionDTOs = modelMapper.map(questions, new TypeToken<List<QuestionDTO>>() {
        }.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .questions(questionDTOs)
                .build();
    }

    @Override
    public Response getQuestionById(Long id) {
        Question question = findQuestion(id);

        return Response.builder()
                .status(200)
                .message("success")
                .question(modelMapper.map(question, QuestionDTO.class))
                .build();
    }

    @Override
    @Transactional
    public Response updateQuestion(Long id, QuestionDTO questionDTO) {
        requireAdmin();

        Question existingQuestion = findQuestion(id);

        if (questionDTO.getQuestionNumber() != null) {
            if (questionRepository.existsByTestAndQuestionNumber(existingQuestion.getTest(), questionDTO.getQuestionNumber())
                    && !existingQuestion.getQuestionNumber().equals(questionDTO.getQuestionNumber())) {
                throw new IllegalArgumentException("Question number already exists on this test");
            }
            existingQuestion.setQuestionNumber(questionDTO.getQuestionNumber());
        }
        if (questionDTO.getQuestionText() != null) {
            existingQuestion.setQuestionText(questionDTO.getQuestionText());
        }

        Long incomingCategoryId = incomingCategoryId(questionDTO);
        if (incomingCategoryId != null
                && !incomingCategoryId.equals(existingQuestion.getCategory().getCategoryId())) {
            throw new IllegalArgumentException("The category of a question cannot be changed");
        }

        questionRepository.save(existingQuestion);

        return Response.builder()
                .status(200)
                .message("Question Updated Successfully")
                .question(modelMapper.map(existingQuestion, QuestionDTO.class))
                .build();
    }

    @Override
    @Transactional
    public Response deleteQuestion(Long id) {
        requireAdmin();

        Question question = findQuestion(id);
        questionRepository.delete(question);

        return Response.builder()
                .status(200)
                .message("Question Deleted Successfully")
                .build();
    }

    private Question buildQuestion(Test test, QuestionDTO questionDTO) {
        if (questionDTO.getQuestionNumber() == null) {
            throw new NameValueRequiredException("Question number is required");
        }
        if (questionDTO.getQuestionText() == null || questionDTO.getQuestionText().isBlank()) {
            throw new NameValueRequiredException("Question text is required");
        }

        if (questionRepository.existsByTestAndQuestionNumber(test, questionDTO.getQuestionNumber())) {
            throw new IllegalArgumentException("Question number already exists on this test");
        }

        return Question.builder()
                .test(test)
                .category(findCategory(resolveCategoryId(questionDTO)))
                .questionNumber(questionDTO.getQuestionNumber())
                .questionText(questionDTO.getQuestionText())
                .build();
    }

    private Long incomingCategoryId(QuestionDTO questionDTO) {
        if (questionDTO.getCategoryId() != null) {
            return questionDTO.getCategoryId();
        }
        if (questionDTO.getCategory() != null && questionDTO.getCategory().getCategoryId() != null) {
            return questionDTO.getCategory().getCategoryId();
        }
        return null;
    }

    private Long resolveCategoryId(QuestionDTO questionDTO) {
        if (questionDTO.getCategoryId() != null) {
            return questionDTO.getCategoryId();
        }
        if (questionDTO.getCategory() != null && questionDTO.getCategory().getCategoryId() != null) {
            return questionDTO.getCategory().getCategoryId();
        }
        throw new NameValueRequiredException("An existing category id is required to create a question");
    }

    private Test findTest(Long testId) {
        return testRepository.findById(testId)
                .orElseThrow(() -> new NotFoundException("Test Not Found"));
    }

    private Question findQuestion(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Question Not Found"));
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category Not Found. A question must be linked to an existing category."));
    }

    private void requireAdmin() {
        User currentUser = userService.getCurrentLoggedInUser();
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Only an admin can manage questions");
        }
    }
}
