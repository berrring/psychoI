package com.example.psycho.service.impl;

import com.example.psycho.model.UserEntity;
import com.example.psycho.repository.UserRepository;
import com.example.psycho.service.IdentityService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class IdentityServiceImpl implements IdentityService {
    private final UserRepository userRepository;

    public IdentityServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity requireByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found by email: " + email));
    }
}

