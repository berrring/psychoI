package com.example.psycho.service;


import com.example.psycho.dto.UserResponseDto;
import com.example.psycho.entity.UserEntity;
import com.example.psycho.enums.UserRole;
import com.example.psycho.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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


}
