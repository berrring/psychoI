package com.example.psycho.service;


import com.example.psycho.dto.ChangePasswordRequestDto;
import com.example.psycho.dto.UserResponseDto;
import com.example.psycho.dto.UserUpdateDto;
import com.example.psycho.entity.UserEntity;
import com.example.psycho.model.UserRole;
import com.example.psycho.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }



    public UserResponseDto getUserId(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id: " + id));
        return toResponseUsers(userEntity);
    }



    public List<UserResponseDto> getAllPsychologists() {
        return userRepository.findByRole(UserRole.PSYCHOLOGIST)
                .stream()
                .map(this::toResponseUsers)
                .toList();
    }

    public List<UserResponseDto> getAllClients() {
        return userRepository.findByRole(UserRole.CLIENT)
                .stream()
                .map(this::toResponseUsers)
                .toList();
    }
    private UserResponseDto toResponseUsers(UserEntity userEntity){
        return new UserResponseDto(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getEmail(),
                userEntity.getRole()
        );
    }

    // Метод для обновления имени (Настройки)
    public UserResponseDto updateUserName(Long id, UserUpdateDto updateDto) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id: " + id));

        // Если имя прислали и оно не пустое — обновляем
        if (updateDto.name() != null && !updateDto.name().isBlank()) {
            user.setName(updateDto.name());
        }

        // Сохраняем в базу
        UserEntity savedUser = userRepository.save(user);

        // Возвращаем обновленные данные
        return toResponseUsers(savedUser);
    }

    public void changePassword(Long userId, ChangePasswordRequestDto request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // А. Проверяем, совпадает ли старый пароль с тем, что в базе
        // (matches: первый аргумент - "сырой" пароль, второй - зашифрованный из базы)
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new RuntimeException("Неверный текущий пароль!");
        }

        // Б. Если совпал — шифруем новый и сохраняем
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }


}
