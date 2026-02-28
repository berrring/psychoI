package com.example.psycho.dto;

import java.math.BigDecimal;

public record MedicalServiceResponseDto(
        Long id,
        String code,
        String name,
        String description,
        Integer durationMinutes,
        BigDecimal basePrice,
        Long departmentId,
        String departmentName
) {
}

