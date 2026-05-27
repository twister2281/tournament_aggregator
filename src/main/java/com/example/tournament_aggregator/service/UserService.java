package com.example.tournament_aggregator.service;

import com.example.tournament_aggregator.domain.dto.user.UserRequest;
import com.example.tournament_aggregator.domain.dto.user.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest request);

    UserResponse updateUser(Long id, UserRequest request);

    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();

    void deleteUser(Long id);
}

