package com.example.psycho.dto;

import com.example.psycho.model.AuditAction;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record AuditEventResponseDto(
        Long id,
        String entityName,
        Long entityId,
        AuditAction action,
        Long actorId,
        String actorEmail,
        String details,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt
) {
}

