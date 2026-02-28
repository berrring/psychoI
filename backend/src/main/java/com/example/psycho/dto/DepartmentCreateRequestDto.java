package com.example.psycho.dto;

import jakarta.validation.constraints.NotBlank;

public record DepartmentCreateRequestDto(
        @NotBlank String name,
        String description
) {
}