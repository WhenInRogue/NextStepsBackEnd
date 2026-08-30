package com.WhenInRogue.NextSteps.services;

import com.WhenInRogue.NextSteps.dtos.Response;
import com.WhenInRogue.NextSteps.dtos.TestDTO;

public interface TestService {

    //Create
    Response createTest(TestDTO testDTO);

    //Read
    Response getAllTests();

    Response getTestById(Long id);

    //Update
    Response updateTest(Long id, TestDTO testDTO);

    //Delete
    Response deleteTest(Long id);

    //Take / submit
    Response takeTest(Long testId);

    Response submitTest(Long testResultId);
}
