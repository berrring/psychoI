package com.example.psycho.dto;

public record ClinicResponseDto(
        Long id,
        String name,
        String city,
        String address,
        String phone,
        String email,
        String description,
        boolean active
) {
}

