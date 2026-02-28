package com.example.psycho.dto;

import com.example.psycho.model.AppointmentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record AppointmentResponseDto(
        Long id,
        Long patientId,
        String patientName,
        Long doctorId,
        String doctorName,
        Long clinicId,
        String clinicName,
        Long departmentId,
        String departmentName,
        Long medicalServiceId,
        String medicalServiceName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime time,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime endTime,
        AppointmentStatus status,
        String complaint,
        String diagnosis,
        String treatmentPlan,
        String notes,
        String cancellationReason
) {
}

