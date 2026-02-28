package com.example.psycho.dto;

import com.example.psycho.model.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MessageSendRequestDto(
        @NotNull Long senderId,
        @NotNull Long appointmentId,
        MessageType type,
        @NotBlank String text,
        String metadata
) {
}

