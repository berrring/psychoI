package com.example.psycho.dto;

public record UserUpdateDto(
        String name,
        String phone,
        String specialization,
        Integer yearsOfExperience,
        String about,
        Long clinicId,
        Boolean active
) {
}

