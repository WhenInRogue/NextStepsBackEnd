package com.WhenInRogue.NextSteps.services.impl;

import com.WhenInRogue.NextSteps.dtos.LoginRequest;
import com.WhenInRogue.NextSteps.dtos.RegisterRequest;
import com.WhenInRogue.NextSteps.dtos.Response;
import com.WhenInRogue.NextSteps.dtos.UserDTO;
import com.WhenInRogue.NextSteps.models.User;
import com.WhenInRogue.NextSteps.repositories.UserRepository;
import com.WhenInRogue.NextSteps.security.JwtUtils;
import com.WhenInRogue.NextSteps.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JwtUtils jwtUtils;

    @Override
    public Response registerUser(RegisterRequest registerRequest) {
        return null;
    }

    @Override
    public Response loginUser(LoginRequest loginRequest) {
        return null;
    }

    @Override
    public Response getAllUsers() {
        return null;
    }

    @Override
    public User getCurrentLoggedInUser() {
        return null;
    }

    @Override
    public Response getUserById(Long id) {
        return null;
    }

    @Override
    public Response updateUser(Long id, UserDTO userDTO) {
        return null;
    }

    @Override
    public Response deleteUser(Long id) {
        return null;
    }
}
