package com.example.psycho.service;

import com.example.psycho.model.UserEntity;

public interface IdentityService {
    UserEntity requireByEmail(String email);
}

