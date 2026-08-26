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

    //Take Test
    Response takeTest(Long id);

    Response submitTest(Long id);
}
