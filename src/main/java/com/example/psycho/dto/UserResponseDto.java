package com.example.psycho.dto;

import com.example.psycho.enums.UserRole;

public record UserResponseDto(
        Long id,
        String name,
        String email,
        UserRole role
){

}
