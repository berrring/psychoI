package com.example.psycho.mapper;

import com.example.psycho.dto.AuditEventResponseDto;
import com.example.psycho.model.AuditEventEntity;
import org.springframework.stereotype.Component;

@Component
public class AuditMapper {
    public AuditEventResponseDto toDto(AuditEventEntity event) {
        return new AuditEventResponseDto(
                event.getId(),
                event.getEntityName(),
                event.getEntityId(),
                event.getAction(),
                event.getActorId(),
                event.getActorEmail(),
                event.getDetails(),
                event.getCreatedAt()
        );
    }
}

