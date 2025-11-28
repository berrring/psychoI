package com.example.psycho.service;

import com.example.psycho.dto.AuthResponseDto;
import com.example.psycho.dto.LoginRequestDto;
import com.example.psycho.dto.RegisterRequestDto;
import com.example.psycho.entity.UserEntity;
import com.example.psycho.repository.UserRepository;
import com.example.psycho.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager; // Добавили менеджер

    public AuthService(UserRepository userRepository,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponseDto register(RegisterRequestDto registerRequestDto) {
        if (userRepository.existsByEmail(registerRequestDto.email())){
            throw new RuntimeException("Email is already used");
        }

        var user = new UserEntity();
        // Убедись, что в Entity у тебя есть соответствующие сеттеры или конструктор
        user.setEmail(registerRequestDto.email());
        user.setName(registerRequestDto.name());
        user.setPassword(passwordEncoder.encode(registerRequestDto.password()));
        user.setRole(registerRequestDto.role());

        user = userRepository.save(user);

        var token = jwtService.generateToken(user);

        // Возвращаем DTO
        return new AuthResponseDto(
                token,
                user.getId(), // Убедись, что у user есть getId()
                user.getRole(),
                user.getName()
        );
    }

    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        // Эта строчка сама проверит пароль. Если неверно - выкинет исключение.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.email(),
                        loginRequestDto.password()
                )
        );

        // Если дошли сюда, значит пароль верный
        var user = userRepository.findByEmail(loginRequestDto.email())
                .orElseThrow();

        var token = jwtService.generateToken(user);

        return new AuthResponseDto(
                token,
                user.getId(),
                user.getRole(),
                user.getName()
        );
    }
}