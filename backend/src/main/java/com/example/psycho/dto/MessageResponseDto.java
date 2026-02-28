package com.example.psycho.dto;

import com.example.psycho.model.MessageType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record MessageResponseDto(
        Long id,
        Long senderId,
        String senderName,
        Long appointmentId,
        MessageType type,
        String text,
        String metadata,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime time
) {
}

