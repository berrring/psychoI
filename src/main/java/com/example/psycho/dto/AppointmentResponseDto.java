package com.example.psycho.dto;

import com.example.psycho.model.AppointmentStatus;
import com.fasterxml.jackson.annotation.JsonFormat; // Нужен для @JsonFormat

import java.time.LocalDateTime;

public record AppointmentResponseDto(
        Long id,
        Long clientId,
        String clientName,
        Long psychologistId,
        String psychologistName,

        // Принудительно задаем формат времени "год-месяц-деньTчас:минуты:секунды" для корректного парсинга на iOS
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime time,

        AppointmentStatus status
) {
}
