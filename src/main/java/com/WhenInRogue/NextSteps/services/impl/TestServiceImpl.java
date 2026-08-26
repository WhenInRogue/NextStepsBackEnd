package com.WhenInRogue.NextSteps.services.impl;

import com.WhenInRogue.NextSteps.dtos.Response;
import com.WhenInRogue.NextSteps.dtos.TestDTO;
import com.WhenInRogue.NextSteps.exceptions.NotFoundException;
import com.WhenInRogue.NextSteps.models.Test;
import com.WhenInRogue.NextSteps.repositories.TestRepository;
import com.WhenInRogue.NextSteps.services.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestServiceImpl implements TestService {

    private final ModelMapper modelMapper;
    private final TestRepository testRepository;

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
    public Response takeTest(Long id) {
        return null;
    }

    @Override
    public Response submitTest(Long id) {
        return null;
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
}
