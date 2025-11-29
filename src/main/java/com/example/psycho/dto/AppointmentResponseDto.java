package com.example.psycho.dto;

import com.example.psycho.model.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponseDto(
        Long id,
        Long clientId,
        String clientName,
        Long psychologistId,
        String psychologistName,
        LocalDateTime time,
        AppointmentStatus status
) {
}
