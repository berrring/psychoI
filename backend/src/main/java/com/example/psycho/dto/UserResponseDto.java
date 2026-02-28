package com.example.psycho.dto;

import com.example.psycho.model.UserRole;

public record UserResponseDto(
        Long id,
        String name,
        String email,
        UserRole role,
        String phone,
        String specialization,
        String licenseNumber,
        Integer yearsOfExperience,
        String about,
        Long clinicId,
        String clinicName,
        boolean active
) {
}

