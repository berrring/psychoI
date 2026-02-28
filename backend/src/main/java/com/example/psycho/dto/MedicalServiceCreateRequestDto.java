package com.example.psycho.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MedicalServiceCreateRequestDto(
        @NotBlank String code,
        @NotBlank String name,
        String description,
        @NotNull @Min(5) Integer durationMinutes,
        @NotNull @DecimalMin("0.0") BigDecimal basePrice
) {
}