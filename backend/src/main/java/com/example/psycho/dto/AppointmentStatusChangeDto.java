package com.example.psycho.dto;

import com.example.psycho.model.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public record AppointmentStatusChangeDto(
        @NotNull AppointmentStatus status,
        String reason
) {
}

