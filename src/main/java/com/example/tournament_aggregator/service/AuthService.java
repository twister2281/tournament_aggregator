package com.example.tournament_aggregator.service;

import com.example.tournament_aggregator.domain.dto.auth.RegistrationRequest;
import com.example.tournament_aggregator.domain.entity.User;

public interface AuthService {

    User register(RegistrationRequest request);
}

