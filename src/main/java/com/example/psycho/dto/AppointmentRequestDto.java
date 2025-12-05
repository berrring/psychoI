package com.example.psycho.dto;

import java.time.LocalDateTime;

public record AppointmentRequestDto(
        Long clientId,
        LocalDateTime time
) {             
}
