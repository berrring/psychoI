package com.example.psycho.service;

import com.example.psycho.dto.ChangePasswordRequestDto;
import com.example.psycho.dto.UserResponseDto;
import com.example.psycho.dto.UserUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponseDto getUserById(Long id);

    Page<UserResponseDto> getDoctors(String query, Pageable pageable);

    Page<UserResponseDto> getPatients(Pageable pageable);

    UserResponseDto updateUser(Long id, UserUpdateDto updateDto);

    void changePassword(Long userId, ChangePasswordRequestDto request);
}

