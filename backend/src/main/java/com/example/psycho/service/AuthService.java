package com.example.psycho.service;

import com.example.psycho.dto.AuthResponseDto;
import com.example.psycho.dto.LoginRequestDto;
import com.example.psycho.dto.RegisterRequestDto;

public interface AuthService {
    AuthResponseDto register(RegisterRequestDto request);

    AuthResponseDto login(LoginRequestDto request);
}

