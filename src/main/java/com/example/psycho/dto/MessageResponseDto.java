package com.example.psycho.dto;

import java.time.LocalDateTime;

public record MessageResponseDto(
        Long id,
        Long senderId,
        String senderName,
        Long appointmentId,
        String text,
        LocalDateTime time

){
}
