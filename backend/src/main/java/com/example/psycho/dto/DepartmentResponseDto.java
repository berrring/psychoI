package com.example.psycho.dto;

public record DepartmentResponseDto(
        Long id,
        String name,
        String description,
        Long clinicId,
        String clinicName
) {
}

