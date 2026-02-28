package com.example.psycho.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentRequestDto(
        @NotNull Long patientId,
        @NotNull Long doctorId,
        @NotNull Long clinicId,
        Long departmentId,
        Long medicalServiceId,
        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime time,
        Integer durationMinutes,
        String complaint,
        String notes
) {
}

