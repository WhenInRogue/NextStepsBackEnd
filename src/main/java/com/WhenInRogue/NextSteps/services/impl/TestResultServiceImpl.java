package com.WhenInRogue.NextSteps.services.impl;

import com.WhenInRogue.NextSteps.dtos.Response;
import com.WhenInRogue.NextSteps.dtos.TestResultDTO;
import com.WhenInRogue.NextSteps.enums.UserRole;
import com.WhenInRogue.NextSteps.exceptions.ForbiddenException;
import com.WhenInRogue.NextSteps.exceptions.NotFoundException;
import com.WhenInRogue.NextSteps.models.TestResult;
import com.WhenInRogue.NextSteps.models.User;
import com.WhenInRogue.NextSteps.repositories.TestResultRepository;
import com.WhenInRogue.NextSteps.repositories.UserRepository;
import com.WhenInRogue.NextSteps.services.TestResultService;
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
public class TestResultServiceImpl implements TestResultService {

    private final TestResultRepository testResultRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public Response getTestResultById(Long testResultId) {
        TestResult testResult = testResultRepository.findById(testResultId)
                .orElseThrow(() -> new NotFoundException("Test Result Not Found"));

        requireCanViewUser(userService.getCurrentLoggedInUser(), testResult.getUser().getId());

        return Response.builder()
                .status(200)
                .message("success")
                .testResult(modelMapper.map(testResult, TestResultDTO.class))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Response getTestResultsByUser(Long userId) {
        requireCanViewUser(userService.getCurrentLoggedInUser(), userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User Not Found"));

        return Response.builder()
                .status(200)
                .message("success")
                .testResults(mapResultsWithoutAnswers(testResultRepository.findByUserOrderByTestResultIdDesc(user)))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Response getCurrentUserTestResults() {
        User currentUser = userService.getCurrentLoggedInUser();
        return Response.builder()
                .status(200)
                .message("success")
                .testResults(mapResultsWithoutAnswers(
                        testResultRepository.findByUserOrderByTestResultIdDesc(currentUser)))
                .build();
    }

    private List<TestResultDTO> mapResultsWithoutAnswers(List<TestResult> testResults) {
        List<TestResultDTO> resultDTOs = modelMapper.map(testResults, new TypeToken<List<TestResultDTO>>() {
        }.getType());
        resultDTOs.forEach(dto -> dto.setAnswers(null));
        return resultDTOs;
    }

    private void requireCanViewUser(User currentUser, Long userId) {
        if (currentUser.getRole() == UserRole.ADMIN
                || currentUser.getRole() == UserRole.DREAM_TEAM_LEADER) {
            return;
        }
        if (currentUser.getId().equals(userId)) {
            return;
        }
        throw new ForbiddenException("You can only view your own test results");
    }
}
