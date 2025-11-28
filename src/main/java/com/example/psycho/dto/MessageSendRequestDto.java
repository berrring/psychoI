package com.example.psycho.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MessageSendRequestDto(
        @NotNull Long senderId,
        @NotNull Long appointmentId,
        @NotBlank String text
){
}
