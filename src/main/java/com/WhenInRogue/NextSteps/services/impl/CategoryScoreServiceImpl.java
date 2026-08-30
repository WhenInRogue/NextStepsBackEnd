package com.WhenInRogue.NextSteps.services.impl;

import com.WhenInRogue.NextSteps.dtos.CategoryScoreDTO;
import com.WhenInRogue.NextSteps.dtos.Response;
import com.WhenInRogue.NextSteps.enums.UserRole;
import com.WhenInRogue.NextSteps.exceptions.ForbiddenException;
import com.WhenInRogue.NextSteps.exceptions.NotFoundException;
import com.WhenInRogue.NextSteps.models.CategoryScore;
import com.WhenInRogue.NextSteps.models.TestResult;
import com.WhenInRogue.NextSteps.models.User;
import com.WhenInRogue.NextSteps.repositories.CategoryScoreRepository;
import com.WhenInRogue.NextSteps.repositories.TestResultRepository;
import com.WhenInRogue.NextSteps.repositories.UserRepository;
import com.WhenInRogue.NextSteps.services.CategoryScoreService;
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
public class CategoryScoreServiceImpl implements CategoryScoreService {

    private final CategoryScoreRepository categoryScoreRepository;
    private final TestResultRepository testResultRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public Response getScoresByTestResult(Long testResultId) {
        TestResult testResult = testResultRepository.findById(testResultId)
                .orElseThrow(() -> new NotFoundException("Test Result Not Found"));

        requireCanViewUser(userService.getCurrentLoggedInUser(), testResult.getUser().getId());

        List<CategoryScore> scores = categoryScoreRepository
                .findByTestResultOrderByCategory_CategoryTypeAscCategory_CategoryNameAsc(testResult);

        return Response.builder()
                .status(200)
                .message("success")
                .categoryScores(mapScores(scores))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Response getScoresByUser(Long userId) {
        requireCanViewUser(userService.getCurrentLoggedInUser(), userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User Not Found"));

        List<CategoryScore> scores = categoryScoreRepository.findByTestResult_User(user);

        return Response.builder()
                .status(200)
                .message("success")
                .categoryScores(mapScores(scores))
                .build();
    }

    private List<CategoryScoreDTO> mapScores(List<CategoryScore> scores) {
        return modelMapper.map(scores, new TypeToken<List<CategoryScoreDTO>>() {
        }.getType());
    }

    private void requireCanViewUser(User currentUser, Long userId) {
        if (currentUser.getRole() == UserRole.ADMIN
                || currentUser.getRole() == UserRole.DREAM_TEAM_LEADER) {
            return;
        }
        if (currentUser.getId().equals(userId)) {
            return;
        }
        throw new ForbiddenException("You can only view your own category scores");
    }
}
