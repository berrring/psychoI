package com.example.psycho.service.impl;

import com.example.psycho.dto.AuthResponseDto;
import com.example.psycho.dto.LoginRequestDto;
import com.example.psycho.dto.RegisterRequestDto;
import com.example.psycho.exception.ResourceConflictException;
import com.example.psycho.model.UserEntity;
import com.example.psycho.model.AuditAction;
import com.example.psycho.model.UserRole;
import com.example.psycho.repository.UserRepository;
import com.example.psycho.security.JwtService;
import com.example.psycho.service.AuditService;
import com.example.psycho.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuditService auditService;

    public AuthServiceImpl(UserRepository userRepository,
                           JwtService jwtService,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           AuditService auditService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.auditService = auditService;
    }

    @Override
    public AuthResponseDto register(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResourceConflictException("Email is already used");
        }

        UserEntity user = new UserEntity();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.PATIENT);

        user = userRepository.save(user);

        String token = jwtService.generateToken(
                Map.of("role", user.getRole().name(), "userId", user.getId()),
                user
        );

        auditService.log("users", user.getId(), AuditAction.AUTH, user, "Patient self-registration");

        return new AuthResponseDto(token, user.getId(), user.getRole(), user.getName(), user.getEmail());
    }

    @Override
    public AuthResponseDto login(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtService.generateToken(
                Map.of("role", user.getRole().name(), "userId", user.getId()),
                user
        );

        auditService.log("users", user.getId(), AuditAction.AUTH, user, "User login");

        return new AuthResponseDto(token, user.getId(), user.getRole(), user.getName(), user.getEmail());
    }
}

