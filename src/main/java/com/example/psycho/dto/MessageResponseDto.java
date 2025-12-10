package com.example.psycho.dto;

import com.fasterxml.jackson.annotation.JsonFormat; // Нужен для @JsonFormat
import java.time.LocalDateTime;

public record MessageResponseDto(
        Long id,
        Long senderId,
        String senderName,
        Long appointmentId,
        String text,

        // Принудительно задаем формат времени для iOS
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime time
){
}
