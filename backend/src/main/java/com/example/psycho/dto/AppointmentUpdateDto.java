package com.example.psycho.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record AppointmentUpdateDto(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime time,
        Integer durationMinutes,
        String diagnosis,
        String treatmentPlan,
        String notes,
        String complaint,
        Long doctorId,
        Long departmentId,
        Long medicalServiceId
) {
}

