package com.example.psycho.dto;

import com.example.psycho.enums.UserRole;

public record AuthResponseDto(
        String token,
        Long userId,
        UserRole role,
        String name
) {
}
