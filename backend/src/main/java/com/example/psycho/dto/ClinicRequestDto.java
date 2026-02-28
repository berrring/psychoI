package com.example.psycho.dto;

import jakarta.validation.constraints.NotBlank;

public record ClinicRequestDto(
        @NotBlank String name,
        @NotBlank String city,
        @NotBlank String address,
        String phone,
        String email,
        String description,
        Boolean active
) {
}

