package com.WhenInRogue.NextSteps.services;

import com.WhenInRogue.NextSteps.dtos.LoginRequest;
import com.WhenInRogue.NextSteps.dtos.RegisterRequest;
import com.WhenInRogue.NextSteps.dtos.Response;
import com.WhenInRogue.NextSteps.dtos.UserDTO;
import com.WhenInRogue.NextSteps.models.User;

public interface UserService {
    Response registerUser(RegisterRequest registerRequest);

    Response loginUser(LoginRequest loginRequest);

    Response getAllUsers();

    User getCurrentLoggedInUser();

    Response getUserById(Long id);

    Response updateUser(Long id, UserDTO userDTO);

    Response deleteUser(Long id);
}
