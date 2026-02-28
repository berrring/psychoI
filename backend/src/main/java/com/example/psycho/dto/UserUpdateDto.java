package com.example.psycho.dto;

public record UserUpdateDto(
        String name,
        String phone,
        String specialization,
        String licenseNumber,
        Integer yearsOfExperience,
        String about,
        Long clinicId,
        Boolean active
) {
}

